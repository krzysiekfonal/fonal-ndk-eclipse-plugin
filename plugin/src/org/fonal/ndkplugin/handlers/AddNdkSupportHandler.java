package org.fonal.ndkplugin.handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.internal.core.ToolChain;
import org.eclipse.cdt.managedbuilder.internal.dataprovider.ConfigurationDataProvider;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.fonal.ndkplugin.Activator;
import org.fonal.ndkplugin.Messages;
import org.fonal.ndkplugin.helpers.JDOMHelper;
import org.fonal.ndkplugin.helpers.JDOMHelper.JDOMHelperCollectionException;
import org.fonal.ndkplugin.helpers.MkHelper;
import org.fonal.ndkplugin.helpers.OsHelper;
import org.fonal.ndkplugin.preferences.NdkPreferences;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class AddNdkSupportHandler extends AbstractHandler {

	private static final String CDT_PROJECT_ID = "org.eclipse.cdt.make.core.make";
		
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveMenuSelection(event);
		
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IJavaProject) {
			new ConvertToNdkJob(((IJavaProject)firstElement).getProject(), Messages.CONVERT_TO_NDK_JOB_NAME).schedule();
		}
		
		return null;
	}
	
	private class ConvertToNdkJob extends Job {
		IProject androidProject;
		
		public ConvertToNdkJob(IProject project, String name) {
			super(name);		
			this.androidProject = project;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			IStatus result = Status.OK_STATUS;
			try {
				monitor.beginTask(Messages.CONVERT_TO_NDK_JOB_NAME, IProgressMonitor.UNKNOWN);
				
				//convert project to CC nature
				if (!androidProject.hasNature(CCProjectNature.CC_NATURE_ID)) {
					CCorePlugin.getDefault().convertProjectToCC(androidProject, monitor, CDT_PROJECT_ID);
				}
				
				//add builders configurations - UNSAFE FRAGMENT OF CODE
				//may be changed in in future CDT versions
				//values are grabbed after inspecting this process in debuger in CDT
				//by converting Java project to CCP with settings - Makeffile Project and other toolchain
				ICProjectDescriptionManager mngr = CoreModel.getDefault().getProjectDescriptionManager();
				ICProjectDescription des = mngr.createProjectDescription(androidProject, false, false);
				ManagedBuildInfo info = ManagedBuildManager.createBuildInfo(androidProject);
				ManagedProject mProj = new ManagedProject(des);
				info.setManagedProject(mProj);				

				Configuration cfg = new Configuration(mProj, (ToolChain)null, ManagedBuildManager.calculateChildId("0", null), "Default");
				IBuilder bld = cfg.getEditableBuilder();
				if (bld != null) {
					if(bld.isInternalBuilder()){
						IConfiguration prefCfg = ManagedBuildManager.getPreferenceConfiguration(false);
						IBuilder prefBuilder = prefCfg.getBuilder();
						cfg.changeBuilder(prefBuilder, ManagedBuildManager.calculateChildId(cfg.getId(), null), prefBuilder.getName());
						bld = cfg.getEditableBuilder();
						bld.setBuildPath(null);
					}
					bld.setManagedBuildOn(false);
				}
				cfg.setArtifactName(mProj.getDefaultArtifactName());
				CConfigurationData data = cfg.getConfigurationData();
				ICConfigurationDescription cfgDes = des.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);

				ConfigurationDataProvider.setDefaultLanguageSettingsProviders(androidProject, cfg, cfgDes);				
				mngr.setProjectDescription(androidProject, des);
				
				//create JNI dir if not exist
				IFolder jniFolder = androidProject.getFolder("jni");
				if (!jniFolder.exists()) {
					jniFolder.create(true, true, monitor);
				}
				
				//create default Android.mk file
				MkHelper.createMkFile(jniFolder);

				//change a few settings in project properties 
				setCProjectFile(androidProject.getFile(".cproject"));
				
				//reload project
				androidProject.close(monitor);
				androidProject.open(monitor);
								
			} catch (CoreException e) {
				e.printStackTrace();
				result =  new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage());
			}
			
			monitor.done();			
			return result;
		}
		
		private void setCProjectFile(IFile cproject) {
			SAXBuilder builder = new SAXBuilder();
			try {
				String filename = cproject.getLocation().toOSString();
				Document cprojectxml = builder.build(filename);
				Element root = cprojectxml.getRootElement();
				try {
					//modify .cproject - needed to change make command builder and
					//add to includes headers from NDK
					Element builderElement = JDOMHelper.createJDOMHelper(root).findElementsByName("builder").single();
//					builderElement.setAttribute("command", NdkPreferences.getNDKPath() + 
//							(OsHelper.isWindows() ? "\\" : "/" ) + 
//							"ndk-build");
					builderElement.setAttribute("command", NdkPreferences.getNDKPath() + 
							System.getProperty("file.separator") + 
							"ndk-build");
					
					//add unclude path to every languages
					String includePath = NdkPreferences.getNDKPath() + 
							System.getProperty("file.separator") +
							"platforms" +
							System.getProperty("file.separator") +
							NdkPreferences.getApiLevel() +
							System.getProperty("file.separator") +
							"arch-arm" +
							System.getProperty("file.separator") +
							"usr" + 
							System.getProperty("file.separator") +
							"include";
					List<Element> tools = JDOMHelper.createJDOMHelper(root).findElementsByName("tool").all();
					for (Element tool : tools) {
						if (!tool.getAttributeValue("name").equals("holder for library settings")) {
							//add option element if not exist
							Element option = tool.getChild("option");
							if (option == null) {
								option = new Element("option");
								option.setAttribute("id", "org.eclipse.cdt.build.core.settings.holder.incpaths");
								option.setAttribute("name", "Include Paths");
								option.setAttribute("valueType", "includePath");
								tool.addContent(option);
							}
							Element listOptionValue = new Element("listOptionValue");
							listOptionValue.setAttribute("builtIn", "false");
							listOptionValue.setAttribute("value", includePath);
							option.addContent(listOptionValue);
						}
					}
					
					//update .cproject
					new XMLOutputter(Format.getPrettyFormat()).output(cprojectxml, new FileWriter(filename));
				} catch (JDOMHelperCollectionException e) {
					e.printStackTrace();
				}
			} catch (JDOMException e) {				
				e.printStackTrace();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		
	}

}

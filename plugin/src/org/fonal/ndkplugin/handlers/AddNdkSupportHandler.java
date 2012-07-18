package org.fonal.ndkplugin.handlers;

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
import org.eclipse.cdt.managedbuilder.ui.wizards.CfgHolder;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.fonal.ndkplugin.Activator;
import org.fonal.ndkplugin.Messages;

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
			try {
				//convert project to CC nature
				CCorePlugin.getDefault().convertProjectToCC(androidProject, monitor, CDT_PROJECT_ID);
				
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
			} catch (CoreException e) {
				e.printStackTrace();
				return new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage());
			}
			
			return Status.OK_STATUS;
		}
		
	}

}

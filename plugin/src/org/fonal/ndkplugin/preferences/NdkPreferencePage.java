package org.fonal.ndkplugin.preferences;

import static org.fonal.ndkplugin.Consts.NDK_BUILD_TOOL;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.fonal.ndkplugin.Activator;
import org.fonal.ndkplugin.Messages;

public class NdkPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	DirectoryFieldEditor ndkPath;
	Label apiLevelLabel;
	Combo apiLevelCombo;
			
	@Override
	protected Control createContents(Composite parent) {
		final Composite content = new Composite(parent, SWT.NONE);		
		content.setLayout(new GridLayout(1, false));
		
		Composite ndkComposite = new Composite(content, SWT.NONE);
		GridData gd =new GridData(SWT.FILL, SWT.TOP, true, false);
		ndkComposite.setLayoutData(gd);
		
		ndkPath = new DirectoryFieldEditor(NdkPreferences.PREFERENCE_NDK_PATH, Messages.NDK_PATH, ndkComposite);
		ndkPath.setEmptyStringAllowed(false);
		ndkPath.setPropertyChangeListener(new IPropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				final File ndkDir = new File(ndkPath.getStringValue());
				
				setValid(ndkDir.list(new FilenameFilter() {					
					@Override
					public boolean accept(File dir, String filename) {
						return (dir.compareTo(ndkDir) == 0 &&
								filename.equals(NDK_BUILD_TOOL));
					}
				}).length > 0);
				
				if (!isValid()) {
					setErrorMessage(Messages.WRONG_NDK_PATH);
				} else {
					setErrorMessage(null);
					
					fillApiLevelsCombo(ndkDir.getAbsolutePath());					
				}
			}
		});
		ndkPath.setPage(this);
		ndkPath.setPreferenceStore(getPreferenceStore());
		ndkPath.load();
		
		Composite apiLevelComposite = new Composite(content, SWT.NONE);	
		apiLevelComposite.setLayout(new GridLayout(2, false));
		gd =new GridData(SWT.FILL, SWT.TOP, true, false);
		apiLevelComposite.setLayoutData(gd);
		
		apiLevelLabel = new Label(apiLevelComposite, SWT.NONE);
		apiLevelLabel.setText(Messages.API_LEVEL);
		
		apiLevelCombo = new Combo(apiLevelComposite, SWT.READ_ONLY);
		fillApiLevelsCombo(NdkPreferences.getNDKPath());
		selectApiLevelCombo(NdkPreferences.getApiLevel());
		
		return content;
	}
	
	private void fillApiLevelsCombo(String ndkPath) {
		apiLevelCombo.removeAll();
		for (String item : getApiLevelsForCombo(ndkPath)) {
			apiLevelCombo.add(item);
		}
		apiLevelCombo.select(0);
	}
	
	private void selectApiLevelCombo(String apiLevel) {
		if (apiLevel != null && !apiLevel.isEmpty()) {
			int index = 0;
			for (String item : apiLevelCombo.getItems()) {
				if (item.equals(apiLevel)) {
					apiLevelCombo.select(index);
					return;
				}
				index++;
			}
		}
	}

	private String[] getApiLevelsForCombo(String ndkPath) {
		//fill api levels of proper value
		File platformsDir = new File(ndkPath + System.getProperty("file.separator") + "platforms");
		String[] apiLevelDirs = platformsDir.list();

		if (apiLevelDirs != null) {
			return apiLevelDirs;
		} else {
			return new String[0];
		}
	}

	@Override
	public boolean performOk() {
		ndkPath.store();
		NdkPreferences.setApiLevel(apiLevelCombo.getItem(apiLevelCombo.getSelectionIndex()));
		
		return super.performOk();
	}
	
	@Override
	protected void performDefaults() {
		ndkPath.loadDefault();
		apiLevelCombo.select(0);
		
        super.performDefaults();
    }
	
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

}

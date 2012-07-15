package org.fonal.ndkplugin.preferences;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.fonal.ndkplugin.Messages;

import static org.fonal.ndkplugin.Consts.*;

public class NdkPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	@Override
	protected Control createContents(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		final DirectoryFieldEditor ndkPath = new DirectoryFieldEditor(NdkPreferences.PREFERENCE_NDK_PATH, Messages.NDK_PATH, content);
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
				}
			}
		});
		
		return content;
	}

	@Override
	public void init(IWorkbench workbench) {
	}

}

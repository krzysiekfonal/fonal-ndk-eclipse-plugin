package org.fonal.ndkplugin.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.fonal.ndkplugin.Activator;

public class NdkPreferencesInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore mPrefStore = Activator.getDefault().getPreferenceStore();
		
		mPrefStore.setDefault(NdkPreferences.PREFERENCE_NDK_PATH, "");
	}

}

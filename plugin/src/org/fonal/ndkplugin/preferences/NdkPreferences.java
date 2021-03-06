package org.fonal.ndkplugin.preferences;

import org.fonal.ndkplugin.Activator;

public class NdkPreferences {
	public static String PREFERENCE_NDK_PATH = "FONAL_NDK_PLUGIN_NDK_PATH";
	public static String PREFERENCE_NDK_API_LEVEL = "FONAL_NDK_PLUGIN_NDK_API_LEVEL";
	
	public static String getNDKPath() {
		return Activator.getDefault().getPreferenceStore().getString(PREFERENCE_NDK_PATH);
	}
	
	public static String getApiLevel() {
		return Activator.getDefault().getPreferenceStore().getString(PREFERENCE_NDK_API_LEVEL);
	}
	
	public static void setApiLevel(String apiLevel) {
		Activator.getDefault().getPreferenceStore().setValue(PREFERENCE_NDK_API_LEVEL, apiLevel);
	}
}

package org.fonal.ndkplugin;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BASE_NAME = "org.fonal.ndkplugin.messages"; //$NON-NLS-1$
	
	//***messages***
	public static String NDK_PATH;
	public static String WRONG_NDK_PATH;
	public static String CONVERT_TO_NDK_JOB_NAME;
	
	//***Init block***
	static {
		NLS.initializeMessages(BASE_NAME, Messages.class);
	}
	
	private Messages() {}
}

package org.fonal.ndkplugin.helpers;

import java.io.File;
import java.io.IOException;

public class JavahHelper {
	public static void createHeaderFromJava(String fullyQualifiedClassName, String projectDir) {		
		try {
			String command = String.format("%s -d %s -classpath %s %s", 
					"javah",
					projectDir + File.separator + "jni",
					projectDir + File.separator + "bin" + File.separator + "classes",
					fullyQualifiedClassName);
			System.out.println(command);
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}	
}

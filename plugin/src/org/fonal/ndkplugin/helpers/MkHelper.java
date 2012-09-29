package org.fonal.ndkplugin.helpers;

import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;

public class MkHelper {
	public static void createMkFile(IFolder folder) {
		IFile mkFile = folder.getFile("Android.mk");
		if (!mkFile.exists()) {			
			StringBuilder mkContent = new StringBuilder();
					
			//create content of default Android.mk file
			mkContent.append("LOCAL_PATH 		:= $(call my-dir)\n");
			mkContent.append("include $(CLEAR_VARS)\n\n");
			mkContent.append("#Below please type your proper name of library\n");
			mkContent.append("LOCAL_MODULE 	:= default\n");			
			mkContent.append("#LOCAL_SRC_FILES  := C/CPP FILES OF LIBRARY\n\n");
			mkContent.append("include $(BUILD_SHARED_LIBRARY)\n");
			
			try {
				mkFile.create(new ByteArrayInputStream(mkContent.toString().getBytes()), true, null);
			} catch (CoreException e) {				
				e.printStackTrace();
			}
		}
	}
}

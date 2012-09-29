package org.fonal.ndkplugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.fonal.ndkplugin.Messages;
import org.fonal.ndkplugin.helpers.JavahHelper;

public class GenerateHeaderHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {	
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveMenuSelection(event);
		
		Object firstElement = selection.getFirstElement();		
		if (firstElement instanceof ICompilationUnit) {
			JavahHelper.createHeaderFromJava(String.format("%s.%s", 
					((ICompilationUnit) firstElement).getParent().getElementName(),
					((ICompilationUnit) firstElement).getElementName().replace(".java", "")), 
					((ICompilationUnit) firstElement).getJavaProject().getResource().getLocation().toString());
			
			try {
				((ICompilationUnit) firstElement).getJavaProject().getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			} catch (CoreException e) {
				// ignored - should never happened
			}
		}		
				
		return null;
	}

}

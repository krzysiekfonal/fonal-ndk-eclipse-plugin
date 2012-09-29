package org.fonal.ndkplugin.helpers;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;

public class LayoutHelper {
	private FormData formData;

    private LayoutHelper(FormData formData) {
        this.formData = formData;
    }
    
	public static LayoutHelper createLayoutHelper(Control control) {	    	    
	    if (control.getLayoutData() != null && control.getLayoutData() instanceof FormData) {
	        return new LayoutHelper((FormData)control.getLayoutData());
	    } else {
	        FormData formData = new FormData();	        
	        control.setLayoutData(formData);
	        
	        return new LayoutHelper(formData);
	    }
	}
	
	public LayoutHelper height(int height) {
        formData.height = height;
        return this;
    }

    public LayoutHelper width(int width) {
        formData.width = width;
        return this;
    }
	
    public LayoutHelper top(Control control, int offset) {
    	formData.top = new FormAttachment(control, offset);         
        return this;
    }

    public LayoutHelper top(int percentage, int offset) {
    	formData.top = new FormAttachment(percentage, offset);         
        return this;
    }
    
    public LayoutHelper top(Control control, int offset, int alignment) {
    	formData.top = new FormAttachment(control, offset, alignment);         
        return this;
    }
    
    public LayoutHelper right(Control control, int offset) {
    	formData.right = new FormAttachment(control, -offset);         
        return this;
    }

    public LayoutHelper right(int percentage, int offset) {
        formData.right = new FormAttachment(percentage, -offset);        
        return this;
    }

    public LayoutHelper right(Control control, int offset, int alignment) {
        FormAttachment fa = new FormAttachment(control, -offset, alignment);
        formData.right = fa;
        return this;
    }
    
    public LayoutHelper bottom(Control control, int offset) {
    	formData.bottom = new FormAttachment(control, -offset);        
        return this;
    }

    public LayoutHelper bottom(int percentage, int offset) {
    	formData.bottom = new FormAttachment(percentage, -offset);        
        return this;
    }
    
    public LayoutHelper bottom(Control control, int offset, int alignment) {
    	formData.bottom = new FormAttachment(control, -offset, alignment);         
        return this;
    }
    
    public LayoutHelper left(Control control, int offset) {
    	formData.left = new FormAttachment(control, offset);        
        return this;
    }

    public LayoutHelper left(int percentage, int offset) {
    	formData.left = new FormAttachment(percentage, offset);        
        return this;
    }

    public LayoutHelper left(Control control, int offset, int alignment) {
        formData.left = new FormAttachment(control, offset, alignment);        
        return this;
    }    
}

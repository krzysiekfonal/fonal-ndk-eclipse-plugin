package org.fonal.ndkplugin.helpers;

import java.util.LinkedList;
import java.util.List;

import org.jdom2.Element;

public class JDOMHelper {
	
	private List<Element> elementsCollection = null;
	
	private JDOMHelper(Element rootElement) {
		elementsCollection = new LinkedList<Element>();
		elementsCollection.add(rootElement);
	}
	
	private JDOMHelper(List<Element> elementCollection) {
		this.elementsCollection = elementCollection;
	}
	
	public static JDOMHelper createJDOMHelper(Element rootElement) {
		return new JDOMHelper(rootElement);
	}
	
	/**
	 * This method is used to find in-deep all elements with given name
	 * 
	 * @param name of elements to find
	 * @return JDOMHelper containing elements match to given name
	 */
	public JDOMHelper findElementsByName(String name) {
		List<Element> result = new LinkedList<Element>();
		
		for (Element el : elementsCollection) {
			findElementsByName(name, el, result);
		}
		
		return new JDOMHelper(result);
	}
	
	private void findElementsByName(String name, Element element, List<Element> result) {
		for (Element el : element.getChildren()) {
			if (el.getName().equals(name)) {
				result.add(el);
			} else {
				findElementsByName(name, el, result);
			}
		}
	}
	
	/**
	 * This method takes first element from current collection regardless how many 
	 * elements there are. If collection is empty 
	 * 
	 * @return first element of current collection
	 * @throws EmptyCollectionException
	 */
	public Element first() throws EmptyCollectionException {
		if (elementsCollection.size() > 0) {
			return elementsCollection.iterator().next();
		} else {
			throw new EmptyCollectionException();
		}
	}
	
	/**
	 * This method takes single element from current collection. If there is more
	 * than one element then NonSingleElementCollection is thrown. This is main
	 * differ between single and first

	 * @return only element exist in current elements collection
	 * 
	 * @throws EmptyCollectionException
	 * @throws NonSingleElementCollectionException
	 */
	public Element single() throws EmptyCollectionException, NonSingleElementCollectionException {
		if (elementsCollection.size() == 0) {
			throw new EmptyCollectionException();
		} else if (elementsCollection.size() > 1) {
			throw new NonSingleElementCollectionException();
		} else {
			return elementsCollection.iterator().next();
		}
	}
	
	/**
	 * This method return all elements
	 * 
	 * @return all current elements collection
	 */
	public List<Element> all() {
		return elementsCollection;
	}
	
	
	@SuppressWarnings("serial")
	public static class JDOMHelperCollectionException extends Exception {		
		public JDOMHelperCollectionException(String message) {
			super(message);
		}
	}
	
	@SuppressWarnings("serial")
	public static class NonSingleElementCollectionException extends JDOMHelperCollectionException {
		public NonSingleElementCollectionException() {
			super("Current elements collection has more than single element");
		}
	}
	
	@SuppressWarnings("serial")
	public static class EmptyCollectionException extends JDOMHelperCollectionException {
		public EmptyCollectionException() {
			super("Current elements collection is empty");
		}
	}
}

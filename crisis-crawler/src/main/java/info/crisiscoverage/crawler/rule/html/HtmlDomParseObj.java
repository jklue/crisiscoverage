package info.crisiscoverage.crawler.rule.html;

import info.crisiscoverage.crawler.rule.ParseObj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlDomParseObj extends ParseObj<Element>{

	/**
	 * Default constructor.
	 */
	public HtmlDomParseObj() {
		super(null);
	}

	/**
	 * Single input constructor.
	 * @param input
	 */
	public HtmlDomParseObj(Element input) {
		super(input == null? null : Arrays.asList(input));
	}

	/**
	 * Collection input constructor.
	 * @param input
	 */
	public HtmlDomParseObj(Iterable<Element> input) {
		super(input);
	}	

	/**
	 * Convenience Method to get Elements from an Iterable.
	 * @param iterable Iterable<Element>
	 * @return Elements
	 */
	protected Elements elementsFromIterable(Iterable<Element> iterable){
		Elements elements = new Elements();
		if (iterable == null) return elements;
		Iterator<Element> it = iterable.iterator();
		while (it.hasNext()){
			Element e = it.next();
			if (!(e instanceof Document)) 
				elements.add(e);
		}
		return elements;
	}
	
	/**
	 * Convenience Method to get first Document an Iterable.
	 * @param iterable Iterable<Element>
	 * @return Elements
	 */
	protected Document firstDocFrom(Iterable<Element> iterable){
		if (iterable == null) return null;
		Iterator<Element> it = iterable.iterator();
		while (it.hasNext()){
			Element e = it.next();
			if (e instanceof Document) return (Document)e; 
		}
		return null;
	}
	
	@Override
	public String asString(boolean isResults) {
		if (isResults && isResultValid()){
			Document doc = firstDocFrom(result);
			if (doc != null) return doc.outerHtml();
			
			Elements elements = elementsFromIterable(result);
			if (elements.isEmpty()) return null;
			else return elements.outerHtml();
		}
		else if (!isResults && isInputValid()){
			Document doc = firstDocFrom(input);
			if (doc != null) return doc.outerHtml();
			
			Elements elements = elementsFromIterable(input);
			if (elements.isEmpty()) return null;
			else return elements.outerHtml();
		}
		else return null;
	}	

	@Override
	public boolean isValid(Element o) {
		return  o != null;
	}

	@Override
	public Collection<String> toIterableTypeString(boolean isResults) {
		Collection<String> c = new ArrayList<String>();
		
		Elements elements = new Elements();
		if (isResults && isResultValid()){
			Document doc = firstDocFrom(result);
			if (doc != null){
				c.add(doc.outerHtml());
				return c;
			} else elements = elementsFromIterable(result);
		} else if (!isResults && isInputValid()) {
			Document doc = firstDocFrom(input);
			if (doc != null){
				c.add(doc.outerHtml());
				return c;
			} else elements = elementsFromIterable(input);
		} 
	
	    for (Element e : elements)
	    	c.add(e.outerHtml());
	    
		return c;
	}

	@Override
	public ParseObj<Element> newParseObjWithInputFromOutputConcrete() {
		
//		System.out.println("in newParseObjWithInputFromOutput() ... "+sampleResults());
		
		switch(getWhichOneResults()){
		case special_string:
			HtmlDomParseObj po = new HtmlDomParseObj();
			po.setSpecialInput(specialResult);
			return po;
		case object:
			return new HtmlDomParseObj(result);
		default:
			return new HtmlDomParseObj();
		}
	}	
}

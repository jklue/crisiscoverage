package info.crisiscoverage.crawler;

import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Cleaner;
import org.jsoup.select.Elements;

public class JsoupUtils implements CrawlerConstants{

	/**
	 * Utilize Jsoup limited support for xml.
	 * @param content
	 * @return
	 */
	public static Document parseXmlDoc(String content){
		return Jsoup.parse(content, "", Parser.xmlParser());
	}
	
	/**
	 * Open a document from the web.
	 * @param url
	 * @param timeout
	 * @param userAgent
	 * @return Document
	 * @throws IOException
	 */
	public static Document getDocumentFromWeb(String url, int timeout, String userAgent) throws IOException{
		return Jsoup.connect(url).timeout(timeout).userAgent(userAgent).get();
	}
	
	/**
	 * Open a document from the web.
	 * @param url
	 * @param timeout
	 * @param userAgent
	 * @return Document
	 * @throws IOException
	 */
	public static Document getXmlDocumentFromWeb(String url, int timeout, String userAgent) throws IOException{
		return Jsoup.connect(url).parser(Parser.xmlParser()).timeout(timeout).userAgent(userAgent).ignoreContentType(true).get();
	}
	
	/**
	 * Get all anchors from document.
	 * @param doc Document
	 * @return Elements
	 */
	public static Elements getAnchors(Document doc){
		return doc.select("a");
	}
	
	/**
	 * Get href from anchor.
	 * @param link Element
	 * @return String 
	 */
	public static String getAnchors(Element link){
		return link.attr("href");
	}
	
	/**
	 * Turn an valid html block into a Document, forcing.
	 * @param elementStr String html block up to entire page
	 * @return Document
	 */
	public static Document elementToDocForce(String elementStr){
		Document doc;
		if (elementStr.contains("<html")) doc = Jsoup.parse(elementStr);
		else if (elementStr.startsWith("<")) doc = Jsoup.parseBodyFragment(elementStr);
		else doc = Jsoup.parseBodyFragment("<span>"+elementStr+"</span>");
		return doc;
	}
	
	/**
	 * Turn an valid html block into a Document.
	 * @param elementStr String html block up to entire page
	 * @param isBody boolean cue for parsing body fragment
	 * @return Document
	 */
	public static Document elementToDoc(String elementStr, boolean isBody){
		if (isBody) return Jsoup.parseBodyFragment(elementStr);
		else return Jsoup.parse(elementStr);
	}
	
	/**
	 * Wrap and Element as a Document, unless it already is one.
	 * @param element Element to test and wrap if needed
	 * @return Document or the original object if it already was a Document
	 */
	public static Document elementToDoc(Element element){
		if (element instanceof Document) return (Document)element;
		else return Jsoup.parseBodyFragment(element.html());
	}
	
	/**
	 * Clean html block into a Document, forcing.
	 * @param elementStr String to clean, up to entire page
	 * @param cleaner Cleaner to use
	 * @return Document cleaned
	 */
	public static Document cleanDocForce(String elementStr, Cleaner cleaner){
		return cleanDoc(elementToDocForce(elementStr),cleaner);
	}
	
	/**
	 * Clean Element using cleaner, first testing and wrapping into Document as needed.
	 * @param element Element to test / wrap + clean
	 * @param cleaner Cleaner to use
	 * @return Document cleaned
	 */
	public static Document cleanDoc(Element element, Cleaner cleaner){
			return cleaner.clean(elementToDoc(element));
	}
	
	/**
	 * Clean to Body String from a provided html block, forcing.
	 * @param elementStr String to clean, up to entire page 
	 * @param cleaner Cleaner to use
	 * @param escapeMode EscapeMode for output
	 * @return String body of cleaned Document
	 */
	public static String cleanDocBodyToStringForce(String elementStr, Cleaner cleaner,EscapeMode escapeMode){
		return cleanDocBodyToString(elementToDocForce(elementStr),cleaner,escapeMode);
	}
	
	/**
	 * Clean to Body String from a provided Element.
	 * @param element Element to clean
	 * @param cleaner Cleaner to use
	 * @param escapeMode EscapeMode for output
	 * @return String body of cleaned Document
	 */
	public static String cleanDocBodyToString(Element element, Cleaner cleaner, EscapeMode escapeMode){
		Document d = cleanDoc(element, cleaner);
		d.outputSettings().escapeMode(escapeMode);
		return d.body().html();
	}
	
	/**
	 * Convenience method.
	 * @param element
	 * @return elements
	 */
	public static Elements wrap(Element element){
		Elements es = new Elements(); 
		es.add(element);
		return es;	
	}
	
	/**
	 * Convenience method to convert iterator to Elements; it will only pull out Element objects within iterator.
	 * @param iterable
	 * @return Elements
	 */
	public static Elements elementsFromIterable(Iterable iterable){		
		Elements elements = new Elements();
		if (iterable == null) return elements;
		Iterator it = iterable.iterator();
		while (it.hasNext()){
			Object o = it.next();
			if (o instanceof Element)
				elements.add((Element)o);
		}
		return elements;
	}
	
	/**
	 * Generate Document from Elements, testing first for a Document within the elements;
	 * otherwise parsing elements into Document.
	 * @param elements
	 * @return Document
	 */
	public static Document docFromElements(Elements elements){
		for (Element e : elements)
			if (e instanceof Document) return (Document)e;
		
		return JsoupUtils.elementToDoc(elements.outerHtml(),true);
	}
}

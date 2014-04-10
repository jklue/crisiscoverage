package info.crisiscoverage.crawler.rule.html;

import info.crisiscoverage.crawler.IOUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Really important to return null if no match!
 * @author mjohns
 *
 */
public class CssSelectRule extends AbstractHtmlDomRule {

	final String cssQuery;
	
	public CssSelectRule(String cssQuery, boolean firstResult) {
		super(firstResult);
		this.cssQuery = cssQuery;
	}
	
	@Override
	protected String runRuleExtender(final Iterator<Element> inputs, final Set<Element> resultsHere, final Map<Element,Object> ioMap) {
		return run(inputs, resultsHere, ioMap, cssQuery, firstResult);
	}
	
	/**
	 * For static calls.
	 * @param inputs
	 * @param resultsHere
	 * @param ioMap
	 * @param cssQuery
	 * @param firstResult
	 * @return
	 */
	public static String run(final Iterator<Element> inputs, final Set<Element> resultsHere, final Map<Element,Object> ioMap, String cssQuery, boolean firstResult) {
		while (inputs.hasNext()){
			Element on = inputs.next();
			Elements elements = on.select(cssQuery);
			
			System.out.println("running cssSelect '"+cssQuery+"' on (sample): "+IOUtils.subSetOf(on.outerHtml(),defaultPrintMax,defaultStringSample)+", results size: "+elements.size());
			
			if (elements != null && !elements.isEmpty()){
				if (firstResult){
						resultsHere.add(elements.first());
				} else {
					for (Element e : elements)
						resultsHere.add(e);
				}
			}
			
			ioMap.put(on, elements);
		}

		return null;
	}
	
	/**
	 * Get cssQuery.
	 * @return
	 */
	public String getCssQuery() {
		return cssQuery;
	}
	

	@Override
	public String toStringExtender() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t").append("cssQuery: ").append(cssQuery).append("\n");
		return sb.toString();
	}	
}

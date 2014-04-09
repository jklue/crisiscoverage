package info.crisiscoverage.crawler.rule.html;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Element;

/**
 * This rule simply passes through the input element(s) to the results.
 * @author mjohns
 *
 */
public class PassThroughHtmlDomRule extends AbstractHtmlDomRule{

	public PassThroughHtmlDomRule(boolean firstResult) {
		super(firstResult);
	}

	@Override
	protected String runRuleExtender(Iterator<Element> inputs,
			Set<Element> resultsHere, Map<Element, Object> ioMap) {
		
		while(inputs.hasNext()){
			Element e = inputs.next();
			resultsHere.add(e);
			ioMap.put(e, e);
			if (firstResult) break;
		}
		return null;
	}

}

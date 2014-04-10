package info.crisiscoverage.crawler.rule.html;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Element;

import com.google.common.base.Strings;

public class AttrValueRule extends AbstractHtmlDomRule {

	final String key;
	
	public AttrValueRule(String key) {
		super(true);
		this.key = key;
	}

	@Override
	protected String runRuleExtender(final Iterator<Element> inputs, final Set<Element> resultsHere, final Map<Element,Object> ioMap){
		return run(inputs, resultsHere, ioMap, key);
	}
	
	/**
	 * For static calls.
	 * @param inputs
	 * @param resultsHere
	 * @param ioMap
	 * @param key
	 * @return
	 */
	public static String run(final Iterator<Element> inputs, final Set<Element> resultsHere, final Map<Element,Object> ioMap, String key){
		String specialResult = null;
		while (inputs.hasNext()){
			Element on = inputs.next();
			String v = on.attr(key);
			if (Strings.isNullOrEmpty(specialResult) && !Strings.isNullOrEmpty(v)) specialResult =  v;//first valid value.
			ioMap.put(on, v);
		}
		return specialResult;
	}

	@Override
	public String toStringExtender() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t").append("key: ").append(key).append("\n");
		return sb.toString();
	}	
}

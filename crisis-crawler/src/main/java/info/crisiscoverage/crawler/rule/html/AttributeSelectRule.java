package info.crisiscoverage.crawler.rule.html;

import info.crisiscoverage.crawler.CrawlerConstants;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author mjohns
 */
public class AttributeSelectRule extends AbstractHtmlDomRule implements CrawlerConstants{
	
	final AttrQueryType type;
	final String key;
	final Object val;
	
	public AttributeSelectRule(AttrQueryType type, String key, Object val, 
			boolean firstResult, List<AbstractHtmlDomRule> deeperRules) {
		super(firstResult);
		this.type = type;
		this.key = key;
		this.val = val;
		
		if (deeperRules != null){
			for (AbstractHtmlDomRule rule : deeperRules){
				getDeeperRules().add(rule);
			}
		}
	}
	
	@Override
	protected String runRuleExtender(final Iterator<Element> inputs, final Set<Element> resultsHere, final Map<Element,Object> ioMap) {
		return run(inputs, resultsHere, ioMap, type, key, val, firstResult);
	}
	
	/**
	 * For static calls.
	 * @param inputs
	 * @param resultsHere
	 * @param ioMap
	 * @param type
	 * @param key
	 * @param val
	 * @param firstResult
	 * @return
	 */
	public static String run(final Iterator<Element> inputs, final Set<Element> resultsHere, final Map<Element,Object> ioMap,
			AttrQueryType type, String key, Object val, boolean firstResult) {
		while (inputs.hasNext()){
			Element on = inputs.next();

			Elements elements = null;

			switch(type){
			case key:
				elements = on.getElementsByAttribute(key);
				break;
			case key_starting:
				elements = on.getElementsByAttributeStarting(key);
				break;
			case value:
				if (val != null && val instanceof String)
					elements = on.getElementsByAttributeValue(key,val.toString());
				break;
			case value_containing:
				if (val != null && val instanceof String)
					elements = on.getElementsByAttributeValueContaining(key,val.toString());
				break;
			case value_starting:
				if (val != null && val instanceof String)
					elements = on.getElementsByAttributeValueStarting(key,val.toString());
				break;
			case value_ending:
				if (val != null && val instanceof String)
					elements = on.getElementsByAttributeValueEnding(key,val.toString());
				break;
			case value_not:
				if (val != null && val instanceof String)
					elements = on.getElementsByAttributeValueNot(key,val.toString());
				break;
			case value_matching:
				if (val != null && val instanceof String)
					elements = on.getElementsByAttributeValueMatching(key,val.toString());
				else if (val != null && val instanceof Pattern)
					elements = on.getElementsByAttributeValueMatching(key,(Pattern)val);
				break;	
			}

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
	 * Get key.
	 * @return
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Get type.
	 * @return
	 */
	public AttrQueryType getType() {
		return type;
	}

	/**
	 * Get val.
	 * @return
	 */
	public Object getVal() {
		return val;
	}

	@Override
	public String toStringExtender() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t").append("type: ").append(type.name()).append("\n");
		sb.append("\t").append("key: ").append(key).append("\n");
		sb.append("\t").append("val: ").append(val).append(", val class? ")
			.append((val == null? "null":val.getClass().getName())).append("\n");
		return sb.toString();
	}	
}
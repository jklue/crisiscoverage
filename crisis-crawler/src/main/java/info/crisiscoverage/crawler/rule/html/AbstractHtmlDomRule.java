package info.crisiscoverage.crawler.rule.html;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Element;

import info.crisiscoverage.crawler.rule.ParseRule;

public abstract class AbstractHtmlDomRule extends ParseRule<Element>{
	
	final boolean firstResult;
	
	public AbstractHtmlDomRule(boolean firstResult) {
		super();
		this.firstResult = firstResult;
	}
	
	@Override
	final protected String runRuleConcrete(final Iterator<Element> inputs, final Set<Element> resultsHere, final Map<Element,Object> ioMap) {
		//anything in-between here.
		return runRuleExtender(inputs,resultsHere,ioMap);
	}
	
	/**
	 * #runRule for sub-classes.
	 * @param inputs
	 * @param results
	 * @param ioMap
	 * @result String use as specialString if valid.
	 */
	abstract protected String runRuleExtender(final Iterator<Element> inputs, final Set<Element> resultsHere,final Map<Element,Object> ioMap);
	
	/**
	 * Get firstResult.
	 * @return
	 */
	final public boolean isFirstResult() {
		return firstResult;
	}
	
	@Override
	final public String toStringConcrete() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t").append("firstResult? ").append(firstResult).append("\n");
		sb.append("\t").append("deeperRules? ").append((deeperRules == null? "no" : deeperRules.size())).append("\n");
		sb.append(toStringExtender());
		return sb.toString();
	}	
	
	/**
	 * Way for extending classes to add to the {{@link #toStringConcrete()} call.
	 * @return
	 */
	public String toStringExtender(){
		return "";
	}
	
	@Override
	public Class<Element> objectClass() {
		return Element.class;
	}	

}

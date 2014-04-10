package info.crisiscoverage.crawler.rule.string;

import info.crisiscoverage.crawler.rule.ParseRule;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractStringRule extends ParseRule<String>{
	
	final protected String searchFor;
	final protected boolean firstResult;
	
	/**
	 * Constructor.
	 * @param parseStage
	 * @param searchFor
	 * @param firstResult
	 */
	public AbstractStringRule(String searchFor, boolean firstResult){
		super();
		this.searchFor = searchFor;
		this.firstResult = firstResult;
	}
	
	@Override
	final protected String runRuleConcrete(final Iterator<String> inputs, final Set<String> resultsHere, final Map<String,Object> ioMap) {
		//Anything in-between here.
		return runRuleExtender(inputs,resultsHere,ioMap);
	}
	
	/**
	 * #runRule for sub-classes.
	 * @param inputs
	 * @param results
	 * @param ioMap
	 * @result String use as specialString if valid.
	 */
	abstract protected String runRuleExtender(final Iterator<String> inputs, final Set<String> resultsHere, final Map<String,Object> ioMap);
	
	/**
	 * Get searchFor.
	 * @return
	 */
	final public String getSearchFor() {
		return searchFor;
	}
	
	/**
	 * Get firstResult
	 * @return
	 */
	final public boolean isFirstResult() {
		return firstResult;
	}
	
	@Override
	final public String toStringConcrete(){
		StringBuilder sb = new StringBuilder();
		sb.append("\t").append("searchFor: ").append(searchFor).append("\n");
		sb.append("\t").append("firstResult? ").append(firstResult).append("\n");
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
	public Class<String> objectClass() {
		return String.class;
	}	
}

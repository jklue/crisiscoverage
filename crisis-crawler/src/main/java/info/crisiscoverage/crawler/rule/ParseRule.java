package info.crisiscoverage.crawler.rule;

import info.crisiscoverage.crawler.CrawlerConstants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;

public abstract class ParseRule<O> implements CrawlerConstants{
	
	//DeeperRules work on the SAME input and only stop on a specialResult being set; otherwise they finish.
	final protected List<ParseRule<O>> deeperRules = new LinkedList<ParseRule<O>>();
	
	public abstract Class<O> objectClass();
	 
	/**
	 * Run the rule.
	 * @param parseObj ParseObj<O>, expects input to be appropriately set. Internally, appropriate result value will be set if matches found.
	 */
	final public void runRule(final ParseObj<O> parseObj){
		if (parseObj == null || !parseObj.isAnyInputValid() || parseObj.isSpecialInputValid()) return;
		 
		 final Iterable<O> inputs = parseObj.getInput();
		 final Set<O> resultsHere = new HashSet<>();
		 final Map<O,Object> ioMap = parseObj.getIoMap();
		 String specialResult = runRuleConcrete(inputs.iterator(),resultsHere,ioMap);
		
		 //special_result handling.
		 if (!Strings.isNullOrEmpty(specialResult)){
			 resultsHere.clear();
			 parseObj.setSpecialResult(specialResult);
		 }  
		 //normal results with deeperRules handling.
		 else if (!resultsHere.isEmpty() && !deeperRules.isEmpty()){
			 	final Set<O> deeperResultsHere = new HashSet<>();
				specialResult = runDeeperRulesOnMatch(resultsHere,deeperResultsHere,ioMap);
				if (!Strings.isNullOrEmpty(specialResult)) parseObj.setSpecialResult(specialResult);
				else if (!deeperResultsHere.isEmpty()) parseObj.addResultsIfUnique(deeperResultsHere);
		 } 
		 //straight normal results handling.
		 else if (!resultsHere.isEmpty()){
			 parseObj.addResultsIfUnique(resultsHere);
		 }
		
		 System.out.println(parseObj.sampleResults());
	}
	
	/**
	 * Run the rule on the sub-class.
	 * @param inputs Iterator<O> input as iterator.
	 * @param results Set<O> populate valid results here.
	 * @param ioMap Map<O,Object> store output for given input, to include nulls.
	 * @return String specialString, optional.
	 */
	protected abstract String runRuleConcrete(final Iterator<O> inputs,final Set<O> resultsHere, final Map<O,Object> ioMap);
	
	/**
	 * Sub-Class can choose to run deeper rules on a elements, presumably interim results from the initial attribute query.
	 * @param resultsNormal Set<O> results already found in standard processing here.
	 * @param resultsDeeperHere Set<O> populate valid results here.
	 * @param ioMap Map<O,Object> store output for given input, to include nulls.
	 * @return String specialString, optional.
	 */
	final protected String runDeeperRulesOnMatch(final Set<O> resultsNormal, final Set<O> resultsDeeperHere, final Map<O,Object> ioMap){
	    
		//Stop on first success or fall through.
		for (ParseRule<O> rule : deeperRules){
			String specialResult = rule.runRuleConcrete(resultsNormal.iterator(),resultsDeeperHere,ioMap);

			//return on first specialResult, if present.
			if (!Strings.isNullOrEmpty(specialResult)){
				resultsDeeperHere.clear();
				return specialResult;
			}
		}
		
		return null;
	}
		
	@Override
	final public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n")
			.append("### RULE: "+this.getClass().getName()+" ###").append("\n");
		sb.append(toStringConcrete());
		sb.append("### END ###").append("\n").append("\n");
		
		return sb.toString();
	}
	
	/**
	 * Way for concrete classes to add contents to the {@link #toString()} call.
	 * @return
	 */
	public abstract String toStringConcrete();
	
	/**
	 * Get deeperRules and potentially add here.
	 * @return
	 */
	final public List<ParseRule<O>> getDeeperRules() {
		return deeperRules;
	}
}

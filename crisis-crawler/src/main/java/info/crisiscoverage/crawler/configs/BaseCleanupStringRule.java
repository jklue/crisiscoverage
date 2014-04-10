package info.crisiscoverage.crawler.configs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import info.crisiscoverage.crawler.rule.string.AbstractStringRule;
import info.crisiscoverage.crawler.rule.string.FindReplaceStringRule;
import info.crisiscoverage.crawler.rule.string.RegexReplaceStringRule;

/**
 * Some consolidated cleanup on strings for BBC Configs.
 * @author mjohns
 *
 */
public class BaseCleanupStringRule extends AbstractStringRule{

	protected Set<String> internalResults = new HashSet<>();
	protected Map<String,Object> io = new HashMap<>();
	
	public BaseCleanupStringRule() {
		super(null, false);
	}

	@Override
	final protected String runRuleExtender(Iterator<String> inputs,
			Set<String> resultsHere, Map<String, Object> ioMap) {
		
		//Run findReplace (<br>)
		FindReplaceStringRule.run(inputs,internalResults,io, "<br>"," ",false);
		
		//Run findReplace (")
		Iterator it = internalResults.iterator();
		internalResults = new HashSet<>();
		FindReplaceStringRule.run(it,internalResults,io, "\"","&quot;",false);
		
		//Run regex for multiple spaces
		it = internalResults.iterator();
		internalResults = new HashSet<>();
		RegexReplaceStringRule.run(it,internalResults,io, defaultAnyWhitespaceRegex," ",false);
		
		//Run regex for multiple spaces
		it = internalResults.iterator();
		internalResults = new HashSet<>();
		RegexReplaceStringRule.run(it,internalResults,io, defaultSpaceNormalizerRegex," ",false);
		
		runRulesInternal();
		resultsHere.addAll(internalResults);
		return null;
	}

	/**
	 * Run rules internal, ultimately, setting internalResults.
	 * Note: sub-classes should follow a simlar pattern as the base class.
	 */
	protected void runRulesInternal(){
		//sub-classes can make this do something.
	}
}

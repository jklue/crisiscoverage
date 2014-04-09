package info.crisiscoverage.crawler.configs.bbc;

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
public class BbcCleanupStringRule extends AbstractStringRule{

	public BbcCleanupStringRule() {
		super(null, false);
	}

	@Override
	protected String runRuleExtender(Iterator<String> inputs,
			Set<String> resultsHere, Map<String, Object> ioMap) {
		
		Set<String> results = new HashSet<String>();
		Map<String,Object> io = new HashMap<>();
		
		//Run findReplace (<br>)
		FindReplaceStringRule.run(inputs,results,io, "<br>"," ",false);
		
		//Run findReplace (")
		Iterator it = results.iterator();
		results = new HashSet<>();
		FindReplaceStringRule.run(it,results,io, "\"","&quot;",false);
		
		//Run regex for multiple spaces
		it = results.iterator();
		results = new HashSet<>();
		RegexReplaceStringRule.run(it,results,io, defaultAnyWhitespaceRegex," ",false);
		
		//Run regex for multiple spaces
		it = results.iterator();
		results = new HashSet<>();
		RegexReplaceStringRule.run(it,results,io, defaultSpaceNormalizerRegex," ",false);
		
		//Run string cleanup
		it = results.iterator();
		results = new HashSet<>();
		FindReplaceStringRule.run(it,results,io,
				" Share this page Delicious Digg Facebook reddit StumbleUpon Twitter Email Print","",false);
		
		resultsHere.addAll(results);
		return null;
	}

}

package info.crisiscoverage.crawler.configs.bbc;

import info.crisiscoverage.crawler.configs.BaseCleanupStringRule;
import info.crisiscoverage.crawler.rule.string.FindReplaceStringRule;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Some consolidated cleanup on strings for BBC Configs.
 * @author mjohns
 *
 */
public class BbcCleanupStringRule extends BaseCleanupStringRule{

	public BbcCleanupStringRule() {
		super();
	}

	@Override
	protected void runRulesInternal() {
		
		//Run string cleanup
		Iterator<String> it = internalResults.iterator();
		internalResults = new HashSet<>();
		FindReplaceStringRule.run(it,internalResults,io,
				" Share this page Delicious Digg Facebook reddit StumbleUpon Twitter Email Print","",false);
	}

}

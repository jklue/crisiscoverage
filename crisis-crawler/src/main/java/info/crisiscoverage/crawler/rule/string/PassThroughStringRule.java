package info.crisiscoverage.crawler.rule.string;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PassThroughStringRule extends AbstractStringRule{

	public PassThroughStringRule(String searchFor, boolean firstResult) {
		super(searchFor, firstResult);
	}

	@Override
	protected String runRuleExtender(Iterator<String> inputs,
			Set<String> resultsHere, Map<String, Object> ioMap) {
		
		while(inputs.hasNext()){
			String s = inputs.next();
			resultsHere.add(s);
			ioMap.put(s, s);
			if (firstResult) break;
		}
		return null;
	}
}

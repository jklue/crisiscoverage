package info.crisiscoverage.crawler.rule.string;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

public class RegexReplaceStringRule extends AbstractStringRule{
	
	final String replaceWith;

	public RegexReplaceStringRule(String regex, String replaceWith, boolean firstResult) {
		super(regex, firstResult);
		this.replaceWith = replaceWith;
	}
	
	@Override
	protected String runRuleExtender(Iterator<String> inputs,
			Set<String> resultsHere, Map<String, Object> ioMap) {
		return run(inputs, resultsHere, ioMap, searchFor, replaceWith, firstResult);
	}

	/**
	 * For static calls.
	 * @param inputs
	 * @param resultsHere
	 * @param ioMap
	 * @param regex
	 * @param replaceWith
	 * @param firstResult
	 * @return
	 */
	public static String run(Iterator<String> inputs, Set<String> resultsHere, Map<String, Object> ioMap,
			String regex, String replaceWith, boolean firstResult) {
		
		Pattern pattern = Pattern.compile(regex);
	
		while(inputs.hasNext()){
			String on = inputs.next();
			
			Matcher matcher = pattern.matcher(on);
			String result = null;
			if (matcher.find()) {
			    if (firstResult) result = matcher.replaceFirst(replaceWith);
			    else result = matcher.replaceAll(replaceWith);
			} else result = on;
			
			if (!Strings.isNullOrEmpty(result)) resultsHere.add(result);
			
			 //Add output to ioMap.
			 ioMap.put(on, result);
		}
			
		return null;
	}

}

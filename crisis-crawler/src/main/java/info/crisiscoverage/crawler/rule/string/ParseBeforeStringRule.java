package info.crisiscoverage.crawler.rule.string;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

public class ParseBeforeStringRule extends AbstractStringRule{
	
	final boolean includeSearchForInResult;
	final String appendStr;
	
	public ParseBeforeStringRule(String searchFor, boolean firstResult, boolean includeSearchForInResult, String appendStr){
		super(searchFor,firstResult);
		this.includeSearchForInResult = includeSearchForInResult;
		this.appendStr = appendStr;
	}

	@Override
	public String runRuleExtender(final Iterator<String> inputs, final Set<String> resultsHere, final Map<String,Object> ioMap) {
		return run(inputs, resultsHere, ioMap, searchFor, firstResult, includeSearchForInResult, appendStr);
	}
	
	/**
	 * For static calls.
	 * @param inputs
	 * @param resultsHere
	 * @param ioMap
	 * @param searchFor
	 * @param firstResult
	 * @param includeSearchForInResult
	 * @param appendStr
	 * @return
	 */
	public static String run(final Iterator<String> inputs, final Set<String> resultsHere, final Map<String,Object> ioMap,
			String searchFor, boolean firstResult, boolean includeSearchForInResult, String appendStr) {

		while(inputs.hasNext()){
			String on = inputs.next();
			String s;
			if (firstResult){
				s = StringUtils.substringBefore(on, searchFor);
			} else s = StringUtils.substringBeforeLast(on, searchFor);

			if (!Strings.isNullOrEmpty(s)){
				if (!s.equals(on)){
					if (includeSearchForInResult){
						s += searchFor+appendToResult(appendStr);
					} else {
						s += appendToResult(appendStr);
					}
				}
				resultsHere.add(s);
			}

			//Add output to ioMap.
			ioMap.put(on, s);
		}

		return null;//no specialString
	}
	
	/**
	 * Get a safe append value.
	 * @param appendStr
	 * @return
	 */
	public static String appendToResult(String appendStr) {
		return Strings.isNullOrEmpty(appendStr)? "" : appendStr;
	}
	
	/**
	 * Get includeSearchForInResult
	 * @return
	 */
	public boolean isIncludeSearchForInResult() {
		return includeSearchForInResult;
	}
	
	@Override
	public String toStringExtender(){
		StringBuilder sb = new StringBuilder();
		sb.append("\t").append("includeSearchForInFesult? ").append(includeSearchForInResult).append("\n");
		sb.append("\t").append("appendStr: ").append(appendStr).append("\n");
		return sb.toString();
	}	
}

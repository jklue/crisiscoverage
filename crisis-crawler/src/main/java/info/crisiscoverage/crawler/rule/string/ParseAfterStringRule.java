package info.crisiscoverage.crawler.rule.string;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

public class ParseAfterStringRule  extends AbstractStringRule{
	
	final boolean includeSearchForInResult;
	final String prependStr;
	
	public ParseAfterStringRule(String searchFor, boolean firstResult, boolean includeSearchForInResult,String prependStr){
		super(searchFor,firstResult);
		this.includeSearchForInResult = includeSearchForInResult;
		this.prependStr = prependStr;
	}

	@Override
	public String runRuleExtender(final Iterator<String> inputs, final Set<String> resultsHere, final Map<String,Object> ioMap) {
		return run(inputs, resultsHere, ioMap, searchFor, firstResult, includeSearchForInResult, prependStr);
	}
	
	public static String run(final Iterator<String> inputs, final Set<String> resultsHere, final Map<String,Object> ioMap,
			String searchFor, boolean firstResult, boolean includeSearchForInResult,String prependStr) {
		
		while(inputs.hasNext()){
			String on = inputs.next();
			String s;
			
			if (firstResult){
				s = StringUtils.substringAfter(on, searchFor);
			} else s = StringUtils.substringAfterLast(on, searchFor);

			if (!Strings.isNullOrEmpty(s)){
				if (!s.equals(on)){
					if (includeSearchForInResult){
						String tmp = prependToResult(prependStr)+searchFor+s;
						s = tmp;
					} else {
						String tmp = prependToResult(prependStr)+s;
						s = tmp;
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
	 * Get a safe prepend value.
	 * @return
	 */
	public static String prependToResult(String prependStr) {
		return Strings.isNullOrEmpty(prependStr)? "" : prependStr;
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
		sb.append("\t").append("prependStr: ").append(prependStr).append("\n");
		return sb.toString();
	}	
}

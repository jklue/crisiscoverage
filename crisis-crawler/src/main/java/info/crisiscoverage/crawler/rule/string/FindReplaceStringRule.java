package info.crisiscoverage.crawler.rule.string;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

public class FindReplaceStringRule extends AbstractStringRule{
	
	final String replaceWith;
	
	public FindReplaceStringRule(String searchFor, String replaceWith, boolean firstResult){
		super(searchFor,firstResult);
		this.replaceWith = replaceWith;
	}

	@Override
	public String runRuleExtender(final Iterator<String> inputs, final Set<String> resultsHere, final Map<String,Object> ioMap) {
		return run(inputs, resultsHere, ioMap, searchFor, replaceWith, firstResult);	
	}
	
	/**
	 * For static calls.
	 * @param inputs
	 * @param resultsHere
	 * @param ioMap
	 * @param searchFor
	 * @param replaceWith
	 * @param firstResult
	 * @return
	 */
	public static String run(final Iterator<String> inputs, final Set<String> resultsHere, final Map<String,Object> ioMap,
			String searchFor, String replaceWith, boolean firstResult) {
		while(inputs.hasNext()){
			String on = inputs.next();
			 String s;
			 if (firstResult){
				 s = StringUtils.replaceOnce(on, searchFor, replaceWith);
			 } else s = StringUtils.replace(on, searchFor, replaceWith);
			 
			 if (!Strings.isNullOrEmpty(s))
				 resultsHere.add(s);
			 
			 //Add output to ioMap.
			 ioMap.put(on, s);
		 }
		 return null;//no specialString
	}	

	/**
	 * Get replaceWith.
	 * @return
	 */
	public String getReplaceWith() {
		return replaceWith;
	}
	
	@Override
	public String toStringExtender(){
		StringBuilder sb = new StringBuilder();
		sb.append("\t").append("replaceWith: ").append(replaceWith).append("\n");
		return sb.toString();
	}	
}

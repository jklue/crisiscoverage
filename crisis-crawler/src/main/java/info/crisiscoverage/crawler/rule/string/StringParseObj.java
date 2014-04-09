package info.crisiscoverage.crawler.rule.string;

import info.crisiscoverage.crawler.rule.ParseObj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Strings;

public class StringParseObj extends ParseObj<String>{
	
	/**
	 * Default constructor, indicates no input provided.
	 * @param input
	 */
	public StringParseObj() {
		super(null);
	}
	
	/**
	 * Single input constructor.
	 * @param input
	 */
	public StringParseObj(String input) {
		super(Strings.isNullOrEmpty(input) ? null : Arrays.asList(input));
	}
	
	/**
	 * Collection input constructor.
	 * @param inputCollection
	 */
	public StringParseObj(Iterable<String> input) {
		super(input);
	}
	
	@Override
	public boolean isValid(String s) {
	return !Strings.isNullOrEmpty(s);
	}

	@Override
	public String asString(boolean isResult) {
		Iterable<String> strings = toIterableTypeString(isResult);
		boolean valid = isResult? isResultValid() : isInputValid();
		if (valid){
			List<String> list = new ArrayList<>();
			Iterator<String> it = strings.iterator();
			while(it.hasNext())
				list.add(it.next());
			
			if (list.size() == 1) return list.get(0);
			else return Arrays.toString(list.toArray());
			
		} else return null;
	}	

	@Override
	public Iterable<String> toIterableTypeString(
			boolean isResults) {
		List<String> c = new ArrayList<String>();
		if (isResults && isResultValid()){
			c.addAll(result);
		} else if (!isResults && isInputValid()){
			System.out.println("...inputs valid");
			c.addAll(iterableAsList(input));
		} 
		return c;
	}

	@Override
	protected ParseObj<String> newParseObjWithInputFromOutputConcrete() {
		switch(getWhichOneResults()){
		case special_string:
			StringParseObj po = new StringParseObj();
			po.setSpecialInput(specialResult);
			return po;
		case object:
			return new StringParseObj(result);
		default:
			return new StringParseObj();
		}
	}	
}

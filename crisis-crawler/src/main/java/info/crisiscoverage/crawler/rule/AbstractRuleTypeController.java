package info.crisiscoverage.crawler.rule;

import info.crisiscoverage.crawler.CrawlerConstants.ParseStage;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractRuleTypeController<O> {
	
	final protected List<ParseRule> origStringRules = new LinkedList<>();
	final protected List<ParseRule> origObjectRules = new LinkedList<>();
	final protected List<ParseRule> cleanObjectRules = new LinkedList<>();
	final protected List<ParseRule> cleanStringRules = new LinkedList<>();
	
	/**
	 * Get objectClass.
	 * @return
	 */
	public abstract Class<O> objectClass();
	
	/**
	 * Test that a rule is acceptable for a given stage.
	 * @param parseStage
	 * @param rule
	 * @return
	 */
	final public boolean isRuleAcceptableFor(ParseStage parseStage, ParseRule rule) {
		switch(parseStage){
		case orig_string:
			return String.class.isAssignableFrom(rule.objectClass());//ie must be like a StringParseObj
		case orig_object:
			return objectClass().isAssignableFrom(rule.objectClass());
		case clean_object:
			return objectClass().isAssignableFrom(rule.objectClass());
		case clean_string:
			return String.class.isAssignableFrom(rule.objectClass());//ie must be like a StringParseObj
			default:
				return false;
		}
	}

	/**
	 * Add a rule to the appropriate location.
	 * @param parseStage
	 * @param rule
	 */
	final public void addRuleTo(ParseStage parseStage, ParseRule rule) {
		if (!isRuleAcceptableFor(parseStage, rule)){
			System.out.println("... skipping unacceptable rule for parseStage: "+parseStage+rule.toString());
			return;
		}
		switch(parseStage){
		case orig_string:
			origStringRules.add(rule);
			return;
		case orig_object:
			origObjectRules.add(rule);
			return;
		case clean_object:
			cleanObjectRules.add(rule);
			return;
		case clean_string:
			cleanStringRules.add(rule);
			return;
			default:
				return;
		}
	}

	/**
	 * Get all applicable rules for the given ParserStage.
	 * @param parseStage
	 * @return List<ParseRule>
	 */
	final public List<ParseRule> getRulesFor(ParseStage parseStage) {
		switch(parseStage){
		case orig_string:
			return origStringRules;
		case orig_object:
			return origObjectRules;
		case clean_object:
			return cleanObjectRules;
		case clean_string:
			return cleanStringRules;
			default:
				return new LinkedList<ParseRule>();
		}
	}

	/**
	 * Run applicable rules for the given ParserStage, populating the inputObj. 
	 * @param parseStage
	 * @param inputObj ParseObj
	 * @return ParseResult
	 */
	final public void runRulesFor(ParseStage parseStage,final ParseObj inputObj) {
		
		if (inputObj == null || !inputObj.isAnyInputValid() || !parseStage.isValid()) return;
		List<ParseRule> rules = getRulesFor(parseStage);
		if (rules == null || rules.isEmpty()){
			return;
		}
		
		System.out.println("... runRulesFor() parseStage: "+parseStage.name()+", inputObj: "+inputObj.getClass().getName());
		
		ParseObj parseObj = inputObj;
		int count = 0;
		for (ParseRule rule : rules){
			count++;
			System.out.println("... running rule #"+count+" of "+rules.size()+" for parseStage: "+parseStage.name());
			
			rule.runRule(parseObj);
			
			switch(parseObj.getWhichOneResults()){
			case none_valid: continue;
			case special_string: 
			case object:
				default:
				return;
			}
		}
	}
}

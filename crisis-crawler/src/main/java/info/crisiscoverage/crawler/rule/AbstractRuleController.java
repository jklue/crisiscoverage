package info.crisiscoverage.crawler.rule;

import info.crisiscoverage.crawler.CrawlerConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractRuleController<O> implements CrawlerConstants{
	
	final protected AbstractRuleTypeController<O> metaRuler;
	final protected AbstractRuleTypeController<O> parseRuler;
	
	final protected Set<String> ignoreUrlsStartingWith = new HashSet<>();
	final protected Set<String> ignoreUrlsExact = new HashSet<>();
	
	/**
	 * Constructor for customizing parse and meta controllers.
	 * @param parseController
	 * @param metaController
	 */
	public AbstractRuleController(AbstractRuleTypeController<O> parseController, AbstractRuleTypeController<O> metaController){
		this.parseRuler = parseController;
		this.metaRuler = metaController;
	}

	/**
	 * Setup initParseObj used to feed stage transitions.
	 * NOTICE: Iterable may be O or String depending on previous stage!!!
	 * @param ruleType RuleType
	 * @param parseStage ParseStage
	 * @param iterable Iterable to use as input to stage (This may be String or O). 
	 * @param iterableAsString to leverage string conversion on {@link ParseObj}
	 * @return ParseObj will be 'initParseObj' var used in calls. 
	 */
	public abstract ParseObj setupParseObjForStage(RuleType ruleType,
			ParseStage parseStage, Iterable iterable, String iterableAsString);
	
	/**
	 * Test that a rule is acceptable for a given stage.
	 * @param ruleType
	 * @param parseStage
	 * @param rule
	 * @return
	 */
	final public boolean isRuleAcceptableFor(RuleType ruleType, ParseStage parseStage, ParseRule rule){
		switch(ruleType){
		case meta:
			return metaRuler.isRuleAcceptableFor(parseStage,rule);
		case parser:
			default:
			return parseRuler.isRuleAcceptableFor(parseStage,rule);
		}
	}
	
	/**
	 * Add a rule to the appropriate location.
	 * @param ruleType
	 * @param parseStage
	 * @param rule
	 */
	final public void addRuleTo(RuleType ruleType, ParseStage parseStage, ParseRule rule){
		switch(ruleType){
		case meta:
			metaRuler.addRuleTo(parseStage, rule);
			break;
		case parser:
			parseRuler.addRuleTo(parseStage, rule);
			break;
		}
	}
	
	/**
	 * Get all applicable rules for the given ParserStage.
	 * @param parseStage
	 * @return List<ParseRule>
	 */
	final public List<ParseRule> getRulesFor(final RuleType ruleType, final ParseStage parseStage){
		switch(ruleType){
		case meta:
			return metaRuler.getRulesFor(parseStage);
		case parser:
			default:
			return parseRuler.getRulesFor(parseStage);
		}
	}
	
	/**
	 * Run applicable rules for all ParseStages. 
	 * Note: the return will be appropriate to the return type for the stage.
	 * @param ruleType
	 * @param textData
	 * @return ParseResult
	 */
	final public ParseObj runRulesForAllParseStages(final RuleType ruleType, final String textData) {
		ParseObj initParseObj = setupParseObjForStage(ruleType, ParseStage.getFirstStage(), null, textData);
		return runRulesForParseStages(ruleType,ParseStage.getFirstStage(), ParseStage.getLastStage(), initParseObj);
	}

	/**
	 * Run applicable rules for the given ParseStages. 
	 * Note: the return will be appropriate to the return type for the stage.
	 * @param ruleType
	 * @param startStage
	 * @param endStage
	 * @param initParseObj
	 * @return ParseResult
	 */
	final public ParseObj runRulesForParseStages(
			final RuleType ruleType, final ParseStage firstStage, final ParseStage lastStage, final ParseObj initParseObj) {
		
		//Determine if you have a valid stage
		ParseStage parseStage = firstStage;
		if (parseStage.equals(ParseStage.init)) parseStage = parseStage.nextStage();
		while (getRulesFor(ruleType, parseStage).isEmpty()){
			if (parseStage.equals(lastStage)) parseStage = parseStage.no_next;//force to abandon.
			parseStage = parseStage.nextStage();
			if (!parseStage.isValid()) break;
		}
		if (!parseStage.isValid() || initParseObj == null || !initParseObj.isAnyInputValid()) return initParseObj;
		
		//Get the right input for the valid stage.
		ParseObj parseObj = initParseObj;
		switch(initParseObj.getWhichOneInputs()){
		case special_string:
			parseObj = setupParseObjForStage(ruleType, parseStage, null, initParseObj.getSpecialInput());
			break;
		case object:
			parseObj = setupParseObjForStage(ruleType, parseStage, initParseObj.getInput(), initParseObj.asStringInput());
			break;
			default:
		}
		
		while (true){
			//Stop conditions prior to runRule.
			if (
					parseObj == null || !parseObj.isAnyInputValid() || 
					parseObj.isAnyResultValid() || !parseStage.isValid()) break;
			
			//Run rule.
			runRulesForParseStage(ruleType, parseStage, parseObj);
			
			//Stop conditions after runRule.
			if (parseStage.equals(lastStage)) break;
			
			//Setup next stage with conditions.
			parseStage = parseStage.nextStage();
			System.out.println("... moving on to next stage: "+parseStage);
			while (getRulesFor(ruleType, parseStage).isEmpty()){
				if (parseStage.equals(lastStage)) parseStage = parseStage.no_next;//force to abandon.
				parseStage = parseStage.nextStage();
				if (!parseStage.isValid()) break;
			}
			if (!parseStage.isValid()) break;
			
			switch(parseObj.getWhichOneResults()){
			case special_string:
				parseObj = setupParseObjForStage(ruleType, parseStage, null, parseObj.getSpecialResult());
				break;
			case object:
				parseObj = setupParseObjForStage(ruleType, parseStage, parseObj.getResult(), parseObj.asStringResult());
				break;
				default:
			}
		}
		return parseObj;
	}
	
	/**
	 * Run applicable rules for the given ParserStage. 
	 * Note: the return will be appropriate to the return type for the stage.
	 * @param ruleType
	 * @param currentParseStage
	 * @param initParseObj
	 */
    final public void runRulesForParseStage(RuleType ruleType, ParseStage currentParseStage, ParseObj oarseObj){
    	switch(ruleType){
		case meta:
			metaRuler.runRulesFor(currentParseStage, oarseObj);
			break;
		case parser:
			default:
			parseRuler.runRulesFor(currentParseStage, oarseObj);
		}
    }

    /**
     * Get metaRuleController.
     * @return AbstractRuleTypeController<O>
     */
	final public AbstractRuleTypeController<O> getMetaRuleController() {
		return metaRuler;
	}

	/**
	 * Get parseRuleController.
	 * @return AbstractRuleTypeController<O>
	 */
	final public AbstractRuleTypeController<O> getParseRuleController() {
		return parseRuler;
	}

	/**
	 * Get ignoreUrlsStartingWith (ie add to it or use it)
	 * @return
	 */
	final public Set<String> getIgnoreUrlsStartingWith() {
		return ignoreUrlsStartingWith;
	}

	/**
	 * Get ignoreUrlsExact (ie add to it or use it) 
	 * @return
	 */
	final public Set<String> getIgnoreUrlsExact() {
		return ignoreUrlsExact;
	}
}

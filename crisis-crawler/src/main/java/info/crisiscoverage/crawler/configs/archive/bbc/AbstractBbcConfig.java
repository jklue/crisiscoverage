package info.crisiscoverage.crawler.configs.archive.bbc;

import info.crisiscoverage.crawler.configs.AbstractHtmlCrawlerConfig;
import info.crisiscoverage.crawler.rule.html.AbstractHtmlDomRule;
import info.crisiscoverage.crawler.rule.html.AttrValueRule;
import info.crisiscoverage.crawler.rule.html.AttributeSelectRule;
import info.crisiscoverage.crawler.rule.html.CssSelectRule;
import info.crisiscoverage.crawler.rule.html.PassThroughHtmlDomRule;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class AbstractBbcConfig extends AbstractHtmlCrawlerConfig {
	
	final static protected String template = "http://www.bbc.co.uk/search/news/?page="+pageToken+"&q="+queryToken;

	public static final ImmutableSet<String> ignoreUrlsStartingWith = ImmutableSet.of(
			"/",
			"http://m.bbc.co.uk",
			"http://www.bbc.co.uk/sport/",
			"http://www.bbc.co.uk/weather/",
			"http://www.bbc.com/capital/",
			"http://www.bbc.com/future/",
			"http://shop.bbc.com/",
			"http://www.bbc.co.uk/charityappeals/",
			"http://www.dec.org.uk",
			"http://c.moreover.com",
			"http://advertising",
			"http://www.bbc.co.uk/worldservice/learningenglish/language/wordsinthenews/",
			"http://www.bbc.co.uk/programmes/"
			);
	
	public static final ImmutableSet<String> ignoreUrlsExact = ImmutableSet.of(
			"http://www.bbc.co.uk/news/"
			);
	
	public static final ImmutableList<AbstractHtmlDomRule> deeperDates = ImmutableList.of(
			(AbstractHtmlDomRule)new AttrValueRule("content")
			);
	
    public AbstractBbcConfig(String collectionName, String tags) throws IOException{
    	super(collectionName, tags);
    	
    	/* Add parser rules */
//      orig_string 
//None
    	
//    	orig_object
    	ruleController.addRuleTo(
    			RuleType.parser, ParseStage.orig_object, new CssSelectRule("#story-body", true));
    	ruleController.addRuleTo(
    			RuleType.parser, ParseStage.orig_object, new CssSelectRule(".story-body", true));
    	ruleController.addRuleTo(
    			RuleType.parser, ParseStage.orig_object, new CssSelectRule("#main-content", true));
    	ruleController.addRuleTo(
    			RuleType.parser, ParseStage.orig_object, new PassThroughHtmlDomRule(false));// in case no results.

//    	clean_object
//None 
    	
//    	clean_string
    	ruleController.addRuleTo(
    			RuleType.parser, ParseStage.clean_string, new BbcCleanupStringRule());
    	
    	/* Add meta rules */
    	ruleController.addRuleTo(
    			RuleType.meta, ParseStage.orig_object, 
    			new AttributeSelectRule(AttrQueryType.value, "name","DCTERMS.created", true, deeperDates));
    }		
	
	@Override
	final public Set<String> ignoreUrlsStartingWith() {
		Set<String> set = new HashSet<String>();
		set.addAll(ignoreUrlsStartingWith);
		set.addAll(extenderIgnoreUrlsStartingWith());
		return set;
	}
	
	/**
	 * Allow Sub-Classes to add additional ignore urls.
	 * @return
	 */
	protected abstract Set<String> extenderIgnoreUrlsStartingWith();
	
	@Override
	final public Set<String> ignoreUrlsExact() {
		Set<String> set = new HashSet<String>();
		set.addAll(ignoreUrlsExact);
		set.addAll(extenderIgnoreUrlsExact());
		return set;
	}
	
	/**
	 * Allow Sub-Classes to add additional ignore urls.
	 * @return
	 */
	protected abstract Set<String> extenderIgnoreUrlsExact();
	
}

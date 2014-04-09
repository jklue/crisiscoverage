package info.crisiscoverage.crawler.rule;

import java.util.Map;

import info.crisiscoverage.crawler.CrawlerConstants.Column;

public interface MetaMapper<O> {
	
	/**
	 * handle populating fields in {@link Column} from the given O 
	 * @param collectionName String
	 * @param tags String
	 * @param docId String
	 * @param text String original text of source
	 * @param obj O Converted object of source, e.g. Document for html parsers
	 * @param url String
	 * @param cleanText String optional
	 * @param ruleController AbstractRuleController<O>
	 * @return Map<Column,String>
	 */
	Map<Column,String> populateColumnMap(
			String collectionName, String tags, String docId, String text, O obj, String url, String cleanText, AbstractRuleController<O> ruleController);

}

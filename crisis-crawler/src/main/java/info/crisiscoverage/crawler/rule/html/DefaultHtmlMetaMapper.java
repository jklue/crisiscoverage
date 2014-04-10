package info.crisiscoverage.crawler.rule.html;

import info.crisiscoverage.crawler.CrawlerConstants;
import info.crisiscoverage.crawler.LinkUtils;
import info.crisiscoverage.crawler.rule.AbstractRuleController;
import info.crisiscoverage.crawler.rule.MetaMapper;
import info.crisiscoverage.crawler.rule.ParseObj;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Strings;

public class DefaultHtmlMetaMapper implements MetaMapper<Element>, CrawlerConstants{
	
	final protected RuleType ruleType = RuleType.meta;
	
	@Override
	public Map<Column,String> populateColumnMap(
			final String collectionName, final String tags, final String docId, final String text, final Element element,
			final String url, final String cleanText, final AbstractRuleController<Element> ruleController){
		
		Map<Column,String> map = generateKeysOnlyMap();
		
		if (element == null){
			System.err.println("Document is null for url: '"+url+"', skipping MetaMapper.");
			return map;
		} else if (LinkUtils.isIgnoreUrl(url, ruleController.getIgnoreUrlsStartingWith(), ruleController.getIgnoreUrlsExact())){
			return map;
		}

		addQueryAndResultInfo(map,element,docId);
		
		map.put(Column.doc_id, Strings.isNullOrEmpty(docId)? "" : docId);
		
		String d = getDomain(url,docId);
		map.put(Column.domain, Strings.isNullOrEmpty(d)? "" : d);
		
		String t = getTitle(element,docId);
		map.put(Column.title, Strings.isNullOrEmpty(t)? "" : t);
		
		String p = getDatePublished(element, text, ruleController,docId);
		map.put(Column.date_published, Strings.isNullOrEmpty(p)? "" : p);
		
		map.put(Column.collection, Strings.isNullOrEmpty(collectionName)? "" : collectionName);
		map.put(Column.tags, Strings.isNullOrEmpty(tags)? "" : tags);
		map.put(Column.url, Strings.isNullOrEmpty(url)? "" : url);
		
		String summary = getSummary(element, docId);
		map.put(Column.summary, Strings.isNullOrEmpty(summary)? "" : summary);
		map.put(Column.clean_text, cleanText == null? "" : cleanText);
		
		return map;
	}
	
	/**
	 * Sub-classes can populate additional columns for query information.
	 * @param map
	 * @param element
	 * @param docId
	 */
	public void addQueryAndResultInfo(Map<Column,String> map,Element element,String docId){
		Column.removeQueryAndResultColumns(map);
	}
	
	/**
	 * Basic logic to isolate domain.
	 * @param url
	 * @param docId
	 * @return
	 */
	public String getDomain(final String url, String docId){
		if (Strings.isNullOrEmpty(url)) return "";
		
		String d = url;
		
		//handle protocol
		if (StringUtils.contains(d, "://"))
			d = StringUtils.substringAfter(d, "://");
		else if (StringUtils.contains(d, "//"))
			d = StringUtils.substringAfter(d, "//");
		
		//handle path
		if (StringUtils.contains(d, "/"))
			d = StringUtils.substringBefore(d, "/");
		
		if (d.contains("www."))
			d = StringUtils.substringAfter(d, "www.");
		return d;
	}
	
	/**
	 * Get the title tag info
	 * @param element Element (most likely a Document)
	 * @param docId
	 * @return
	 */
	public String getTitle(Element element, String docId){
		Elements titles = element.getElementsByTag("title");
		if (titles != null && !titles.isEmpty()) return titles.first().text();
		else return "";
	}
	
	/**
	 * Get the date published info, applying the available rules until first match.
	 * Note: These only work on AttrResult
	 * @param element
	 * @param text
	 * @param ruleController
	 * @param docId
	 * @return
	 */
	final public String getDatePublished(Element element, final String text, AbstractRuleController<Element> ruleController, String docId){
		ParseObj resultObj = ruleController.runRulesForAllParseStages(ruleType, text);
		
		if (resultObj.isAnyResultValid()){
			return resultObj.asStringWhichOneResults();
		} else return getFallbackDatePublished(element,docId);
	}
	
	/**
	 * Extenders can override this method for custom date parsing.
	 * @param element
	 * @param docId
	 * @return
	 */
	public String getFallbackDatePublished(Element element, String docId){
		return "";
	}
	
	/**
	 * Extenders can override this method for summary.
	 * @param element
	 * @param docId
	 * @return
	 */
	public String getSummary(Element element, String docId){
		return "";
	}
	
	/**
	 * Generate Keys Only Map with empty values.
	 * @return Map<String,String>
	 */
	public Map<Column,String> generateKeysOnlyMap(){
		Map<Column,String> map = new HashMap<Column,String>();
		
		for (Column col : Column.values()){
			map.put(col, "");
		}
		
		return map;
	}
	
	public void dumpMeta(Document doc){
		Elements meta = doc.select("meta");
		if (meta == null || meta.isEmpty()){
			System.out.println("::: NO META TAGS :::");
			return;
		} else {
			System.out.println("::: META TAGS :::");
			for (Element m : meta){
				System.out.println("... "+m.outerHtml());
			}
		}
	}
}

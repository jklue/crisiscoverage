package info.crisiscoverage.crawler.configs.google;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.google.common.base.Strings;

import info.crisiscoverage.crawler.configs.google.AbstractGoogleConfig.DateRestrict;
import info.crisiscoverage.crawler.rule.html.DefaultHtmlMetaMapper;

public class GoogleApiMetaMapper extends DefaultHtmlMetaMapper{
	
	protected Date dateToday = Calendar.getInstance().getTime();
	
	@Override
	public void addQueryAndResultInfo(Map<Column,String> map,Element element,String docId){
		
		if (map == null || element == null || Strings.isNullOrEmpty(docId) || !docId.contains("_")) return;
		
		map.put(Column.query_run_date, dateQueryFormat.format(dateToday));
		
		//query
		Element queryElement = AbstractGoogleConfig.queryElementFrom(element);
		String query = "";
		if (queryElement != null) query = queryStringFromQueryElement(queryElement);
		if (!Strings.isNullOrEmpty(query)) map.put(Column.query_distinct, query);
		
		//result_count
		String resultCount = AbstractGoogleConfig.resultCountFromEntry(element, docId);
		if (!Strings.isNullOrEmpty(resultCount)) map.put(Column.raw_result_count, resultCount);
		
		String dqStr = StringUtils.substringAfterLast(docId, "_");
		if (docId.contains("(")) 
			dqStr = StringUtils.substringBefore(dqStr,"(");
		
		DateRestrict dateRestrict = null;
		int periodsBack = 0;
		
		dateRestrict = DateRestrict.dateRestrictOf(dqStr);
		periodsBack = DateRestrict.periodsBackOf(dqStr);
		
		System.out.println("dateRestrict: "+dateRestrict+", periodsBack: "+periodsBack);
		
		if (dateRestrict != null && periodsBack > -1){
			int daysBack = dateRestrict.periodsAsDaysBack(periodsBack);
			
			map.put(Column.query_period, dateRestrict.name());
			map.put(Column.periods_back, Integer.toString(periodsBack));
			map.put(Column.days_back, Integer.toString(daysBack));
			
			Date dateStart = dateRestrict.dateFromDaysBack(dateToday, daysBack, true);//get period start
			if (dateStart != null){
				String ds = dateQueryFormat.format(dateStart);
				map.put(Column.date_query_start, ds);
				Date dateEnd =  dateRestrict.dateFromDaysBack(dateToday, daysBack, false);//get period end 
				String de = dateQueryFormat.format(dateEnd);
				if (dateEnd != null) map.put(Column.date_query_end,de);
				
				System.out.println("... dateStart: "+ds+", dateEnd: "+de);
			}
		}
	}
	
	/**
	 * Sub-classes may want to override for specifics.
	 * @param queryElement
	 * @return
	 */
	protected String queryStringFromQueryElement(Element queryElement){
		if (queryElement == null) return "";
		return queryElement.outerHtml();
	}
	
	@Override
	public String getFallbackDatePublished(Element element, String docId){
		return AbstractGoogleConfig.dateFromEntry(element, docId);
	}
	
	@Override
	public String getTitle(Element element, String docId){
		return AbstractGoogleConfig.titleFromEntry(element, docId);
	}
	
	@Override
	public String getSummary(Element element, String docId){
		return AbstractGoogleConfig.summaryFromEntry(element, docId);
	}	
	
}

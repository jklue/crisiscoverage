package info.crisiscoverage.crawler.configs.google;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.google.common.base.Strings;

import info.crisiscoverage.crawler.rule.html.DefaultHtmlMetaMapper;

public class GoogleApiMetaMapper extends DefaultHtmlMetaMapper{
	
	@Override
	public String getDateQueryName(Element element, String docId){
		if (Strings.isNullOrEmpty(docId)) return "";
		if (!docId.contains("_")) return "";

		String dateName = StringUtils.substringAfterLast(docId, "_");
		if (!docId.contains("(")) return "";
		dateName = StringUtils.substringBefore(dateName,"(");

		if (!Strings.isNullOrEmpty(dateName)){
			Calendar cal = Calendar.getInstance();
			dateName += "["+cal.get(Calendar.YEAR)+"-"+
					StringUtils.leftPad(""+(cal.get(Calendar.MONTH)+1),2,"0")+"-"+
					StringUtils.leftPad(""+cal.get(Calendar.DAY_OF_MONTH),2,"0")+"]";
			return dateName;
		} else return "";
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

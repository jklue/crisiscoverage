package info.crisiscoverage.crawler.configs.google;

import org.jsoup.nodes.Element;

import info.crisiscoverage.crawler.rule.html.DefaultHtmlMetaMapper;

public class GoogleApiMetaMapper extends DefaultHtmlMetaMapper{
	
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

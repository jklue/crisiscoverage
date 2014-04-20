package info.crisiscoverage.crawler.configs.google;

import info.crisiscoverage.crawler.CrawlerConstants.Column;
import info.crisiscoverage.crawler.CrawlerConstants.MetaMode;
import info.crisiscoverage.crawler.configs.google.AbstractGoogleConfig.DateRestrict;

import java.util.List;

import com.google.common.base.Strings;

public class MultiAdditionalResultObj extends AdditionalResultObj {
	
	protected final String siteName;
	protected final String siteType;
	
	public MultiAdditionalResultObj(
			String queryDistinctKey, DateRestrict dateRestrict, List<Column> rowHeaders,
			List<String> resultHeaders) throws Exception {
		super(queryDistinctKey, dateRestrict, rowHeaders, resultHeaders);
		
		siteName = getSiteNameFrom();
		siteType = getSiteTypeFrom();
	}
	
//	@Override
//	public boolean isForMetaMode(MetaMode metaMode){
//		return true;
//	}
	
	@Override
	protected String getCustomCellValueFor(String resultHeader, List<String> row){

//		System.out.println("... getting customCellValueFor resultHeader: "+ resultHeader+", domain: "+queryDistinctKey);
		if (resultHeader.equals(MultiGoogleConfig.siteNameCol))
			return siteName;
		else if (resultHeader.equals(MultiGoogleConfig.siteTypeCol))
			return siteType;
		else return "";
	}
	
	/**
	 * Get Site Name from Domain.
	 * @return Site Name, defaults to Google
	 */
	protected String getSiteNameFrom(){
		if (!Strings.isNullOrEmpty(queryDistinctKey)){
			String n = AbstractGoogleConfig.mediaNameMap.get(queryDistinctKey);
			if (!Strings.isNullOrEmpty(n)) return n;
		}
		return AbstractGoogleConfig.googleSiteName;
	}
	
	/**
	 * Get Site Type from Domain.
	 * @return Site Type, defaults to All
	 */
	protected String getSiteTypeFrom(){
		if (!Strings.isNullOrEmpty(queryDistinctKey)){
			String n = AbstractGoogleConfig.mediaTypeMap.get(queryDistinctKey);
			if (!Strings.isNullOrEmpty(n)) return n;
		}
		return AbstractGoogleConfig.defaultSiteTypeAll;
	}
}

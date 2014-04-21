package info.crisiscoverage.crawler.configs.google;

import info.crisiscoverage.crawler.CrawlerConstants.Column;
import info.crisiscoverage.crawler.CrawlerConstants.MetaMode;
import info.crisiscoverage.crawler.configs.google.AbstractGoogleConfig.DateRestrict;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

public class MultiAdditionalResultObj extends AdditionalResultObj {
	
	protected final String siteName;
	protected final String siteType;
	
	public MultiAdditionalResultObj(MetaMode metaMode,
			String queryDistinctKey, DateRestrict dateRestrict, List<Column> rowHeaders,
			List<String> resultHeaders) throws Exception {
		super(metaMode, queryDistinctKey, dateRestrict, rowHeaders, resultHeaders);
		
		siteName = getSiteNameFrom();
		siteType = getSiteTypeFrom();
	}
	
	@Override
	public boolean isForMetaMode(){
		return true;
	}
	
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
		if (isForMetaMode() && !Strings.isNullOrEmpty(queryDistinctKey)){
			if (isUsingPeriodCompareResults()) {
				String n = AbstractGoogleConfig.mediaNameMap.get(queryDistinctKey);
				if (!Strings.isNullOrEmpty(n)) return n;
			} else {
				String tmp = siteNameAndTypeFromDocId();
				for (String type : AbstractGoogleConfig.defaultSiteTypes){
					if (tmp.startsWith(type)){
						tmp = StringUtils.substringAfter(tmp,type);
						if (tmp.startsWith("-"))
							return StringUtils.substring(tmp,1);
					}
				}
			}
		}
		return AbstractGoogleConfig.googleSiteName;
	}
	
	/**
	 * Get Site Type from Domain.
	 * @return Site Type, defaults to All
	 */
	protected String getSiteTypeFrom(){
		if (isForMetaMode() && !Strings.isNullOrEmpty(queryDistinctKey)){
			if (isUsingPeriodCompareResults()) {
				String n = AbstractGoogleConfig.mediaTypeMap.get(queryDistinctKey);
				if (!Strings.isNullOrEmpty(n)) return n;
			} else {
				String tmp = siteNameAndTypeFromDocId();
				for (String type : AbstractGoogleConfig.defaultSiteTypes){
					if (tmp.startsWith(type)) return type;
				}
			}
		}
		return AbstractGoogleConfig.defaultSiteTypeAll;
	}
	
	/**
	 * Site Name and Type from DocId (ie queryDistinctKey in non-stats mode).
	 * @return
	 */
	protected String siteNameAndTypeFromDocId(){
		String r = "";
		if (isUsingPeriodCompareResults()) return r;
		
		if (queryDistinctKey.contains("_")) r = StringUtils.substringAfter(queryDistinctKey,"_");
		
		int d = StringUtils.indexOfAny(r, "0123456789");
		if (d > -1) r = StringUtils.substring(r, 0, d); 
		
		r = StringUtils.normalizeSpace(r);
		r = StringUtils.trim(r);
		
		if (r.endsWith("-")) r = StringUtils.substringBeforeLast(r, "-");
		return r;
	}
}

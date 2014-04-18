package info.crisiscoverage.crawler.configs.google;

import info.crisiscoverage.crawler.CrawlerConstants;
import info.crisiscoverage.crawler.configs.google.AbstractGoogleConfig.Param;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultiQueryObjFactory implements CrawlerConstants{
	
	public static enum MultiType {
		country, media, media_baseline;
	}
	
	public static final String defaultBaselineTags = "google-media-baseline";
	public static final String defaultMediaTags = "google-media";
	public static final String defaultCountryTags = "google-country";

	/**
	 * Consolidate construction of new MultiQueryObj
	 * @param multiType
	 * @param collectionName
	 * @param tags
	 * @param crisisYear
	 * @param crisisMonth
	 * @param crisisDay
	 * @param crisisQuery String NOT used in baseline.
	 * @param isPaidQuery
	 * @return MultiSiteQueryObj
	 * @throws IOException
	 */
	public static MultiQueryObj create(
			MultiType multiType, String collectionName, int crisisYear, int crisisMonth, int crisisDay, String crisisQuery, 
			boolean isPaidQuery) throws IOException{

		switch(multiType){
		case country:
			MultiQueryObj msq = new MultiQueryObj(
					new MultiCountryConfig(collectionName,defaultCountryTags,crisisQuery,crisisYear,crisisMonth,crisisDay),
					isPaidQuery
					);
			msq.setMediaGoTo(0);//BY DEFAULT, ONLY SITE USED IS GOOGLE !!!
			msq.setDateRestrictAndPeriods(null, 1, 0);//BY DEFAULT, GET ALL RESULTS !!! 
			
			List<String> countryCodes = new ArrayList<>();
			for (String countryCode : AbstractGoogleConfig.crLookup){
		    	countryCodes.add(countryCode);
			}
			msq.setSpecialParam(Param.cr, countryCodes);
			
			return msq;
		case media:
			return new MultiQueryObj(
					new MultiGoogleConfig(collectionName,defaultMediaTags,crisisQuery,crisisYear,crisisMonth,crisisDay),
					isPaidQuery
					);

		case media_baseline:
			return new MultiQueryObj(
					new MultiGoogleConfig(collectionName,defaultBaselineTags,defaultBaselineQueryVal,crisisYear,crisisMonth,crisisDay),
					isPaidQuery
					);
		default:
			return null;
		}
	}
}

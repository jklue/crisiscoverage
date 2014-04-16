package info.crisiscoverage.crawler.configs.google;

import info.crisiscoverage.crawler.CrawlerConstants;

public class HaiyanGoogleMediaConfig implements CrawlerConstants{
	
	/** TAKE NOTE ::: IS THIS PAID ??? */
	private static final boolean isPaidQuery = false;
	
	private static final String defaultQueryVal = "news OR article OR coverage OR Yolanda \"typhoon haiyan\"";
	private static final int crisisYear = 2013;
	private static final int crisisMonth = 11;
	private static final int crisisDay = 7;
	
	/**
	 * Entry-point to run config operations.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// GOOGLE-MEDIA-BASELINE CONFIG (NEWS OR COVERAGE OR ARTICLE)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

//		MultiSiteQueryObj msq = new MultiSiteQueryObj(
//				new MultiSiteGoogleConfig("haiyan","google-media-baseline",defaultBaselineQueryVal,crisisYear,crisisMonth,crisisDay),
//				isPaidQuery
//		);
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//GOOGLE-MEDIA CONFIG (ACTUAL QUERY) OVER 10 WEEKS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/* DONE: API Haiyan Query */
//		MultiSiteQueryObj msq = new MultiSiteQueryObj(
//				new MultiSiteGoogleConfig("haiyan","google-media",defaultQueryVal,crisisYear,crisisMonth,crisisDay),
//				isPaidQuery
//				);		
//		msq.setCrawl(true);
		
		//FIXME: REMOVE AFTER TESTING
		MultiSiteQueryObj msq = new MultiSiteQueryObj(
				new MultiSiteGoogleConfig("haiyan","google-media-test",defaultQueryVal,crisisYear,crisisMonth,crisisDay),
				isPaidQuery
		);		
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//GOOGLE-MEDIA-ALL CONFIG (ACTUAL QUERY) OVER ENTIRE CRISIS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/* DONE: API ALL counts PER SOURCE (PLUS TWITTER AND ALL) */
		
//		MultiSiteQueryObj msq = new MultiSiteQueryObj(
//				new MultiSiteGoogleConfig("haiyan","google-media-year",defaultQueryVal,crisisYear,crisisMonth,crisisDay),
//				isPaidQuery
//				);
//		msq.setDateRestrictAndPeriods(null,1);
//		msq.setMediaGoTo(0);
//		
//		//setup for Twitter, just for total (!!!NOTICE!!! REPLACE API RESULT WITH 'TOTAL' FROM GOOGLE BROWSER QUERY) {{
//		msq.setSiteOverrides(AbstractGoogleConfig.mediaLookup,AbstractGoogleConfig.mediaTypeMap);
//		msq.getSites().add("twitter.com");
//		msq.getSiteTypeMap().put("twitter.com", "Blogs-Social");
//		//}}
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//ACTIONS ON UNCOMMENTED CONFIG FROM ABOVE
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		
/** STEP-1: QUERY GOOGLE API */		
		AbstractGoogleConfig.dryRun = true;
//		msq.getConfig().runMultiSiteQuery(msq); 

/** STEP-2: BREAK OUT INDIVIDUAL RESULTS; (OPTIONALLY CRAWL) */		
//		msq.getConfig().extractFromApiDir(msq.isArchive(), msq.isCrawl());

/** STEP-3 (OPTION): CLEAN TEXT IF CRAWLING WAS TURNED ON IN #2 */		
//		msq.getConfig().cleanText(true);

/** STEP-4: GENERATE META CSV FILE VARIATIONS -- NOTE: FOR PRODUCTION, NOT NECESSARILY GENERATING THE *TEXT VARIANTS */		
//      msq.getConfig().metaToTable(MetaMode.entries_no_text, true, defaultValLimit, "dedup_all_no_text");
		msq.getConfig().metaToTable(MetaMode.query_stats_with_distinct, true, defaultValLimit, "stats_by_query_distinct");
//      msq.getConfig().metaToTable(MetaMode.entries_with_text, true, defaultValLimit, "all_with_text");
//		msq.getConfig().metaToTable(MetaMode.entries_with_text, true, 500, "all_500_chars_text");
//		msq.getConfig().metaToTable(MetaMode.entries_with_text, true, 8000, "all_8000_chars_text");
//		msq.getConfig().metaToTable(MetaMode.entries_with_text, true, 6000, "all_6000_chars_text");
	}

}

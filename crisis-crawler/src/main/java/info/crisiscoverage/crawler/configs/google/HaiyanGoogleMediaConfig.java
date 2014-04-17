package info.crisiscoverage.crawler.configs.google;

import java.nio.file.Path;
import java.util.List;

import info.crisiscoverage.crawler.CrawlerConstants;
import info.crisiscoverage.crawler.IOUtils;

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

		MultiSiteQueryObj msq = new MultiSiteQueryObj(
				new MultiSiteGoogleConfig("haiyan","google-media-baseline",defaultBaselineQueryVal,crisisYear,crisisMonth,crisisDay),
				isPaidQuery
		);

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//GOOGLE-MEDIA CONFIG (ACTUAL QUERY) OVER 6 MONTHS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//MultiSiteQueryObj msq = new MultiSiteQueryObj(
//new MultiSiteGoogleConfig("haiyan","google-media-month",defaultQueryVal,crisisYear,crisisMonth,crisisDay),
//isPaidQuery
//);

		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//GOOGLE-MEDIA-YEAR CONFIG (ACTUAL QUERY) OVER ENTIRE CRISIS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//MultiSiteQueryObj msq = new MultiSiteQueryObj(
//new MultiSiteGoogleConfig("haiyan","google-media-year",defaultQueryVal,crisisYear,crisisMonth,crisisDay),
//isPaidQuery
//);
//msq.setDateRestrictAndPeriods(DateRestrict.year,1);
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//GOOGLE-MEDIA-ALL CONFIG (ACTUAL QUERY) OVER ENTIRE CRISIS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		MultiSiteQueryObj msq = new MultiSiteQueryObj(
//				new MultiSiteGoogleConfig("haiyan","google-media-all",defaultQueryVal,crisisYear,crisisMonth,crisisDay),
//				isPaidQuery
//				);
//		msq.setDateRestrictAndPeriods(null,1);
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//ACTIONS ON UNCOMMENTED CONFIG FROM ABOVE
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/** MANAGE APPLICABLE SETTINGS AFFECTING RUNTIME */		
AbstractGoogleConfig.dryRun = true;//<-- !!! UTILIZE THIS SETTING TO PRESERVE API CALLS !!!
//msq.setOnlyRunPadPeriods(true);
//msq.setApiKeyStartFrom(1);
msq.setRunAdditional(true);
//msq.setCrawl(true);


//ONLY WANT TO TEST WITH GOOGLE ???
msq.setMediaGoTo(0);


/** STEP-1: QUERY GOOGLE API */		
//		msq.getConfig().runMultiSiteQuery(msq); 

/** STEP-2 VERIFY THAT LIVE FILES WERE PROPER XML (MANUAL SAVE ACTIONS OR INTERRUPTED DOWNLOADS CAN INTRODUCE ERROR) */
		List<Path> matchStartsWith = IOUtils.findEntriesMatching(
				msq.getConfig().getApiLiveFolder(), false, FilePart.content, StringMatch.starting_without, defaultXmlHeader, false);
		if (!matchStartsWith.isEmpty()){ 
			System.err.println("ERROR: startsWith test found #"+matchStartsWith.size()+" improper file(s) in api_live folder, please correct prior to proceeding\n!!! (rerun 'extractFromApiDir(...)') !!!");
			System.exit(1);
		}			
		List<Path> matchEndsWith = IOUtils.findEntriesMatching(
				msq.getConfig().getApiLiveFolder(), false, FilePart.content, StringMatch.ending_without, "</feed>", false);
		if (!matchEndsWith.isEmpty()){ 
			System.err.println("ERROR: endsWith test found #"+matchEndsWith.size()+" improper file(s) in api_live folder, please correct prior to proceeding\n!!! (rerun 'extractFromApiDir(...)') !!!");
			System.exit(1);
		}
		
/** STEP-3: BREAK OUT INDIVIDUAL RESULTS; (OPTIONALLY CRAWL) */		
//		msq.getConfig().extractFromApiDir(msq.isArchive(), msq.isCrawl());

/** STEP-4 (OPTION): CLEAN TEXT IF CRAWLING WAS TURNED ON IN #2 */		
//		msq.getConfig().cleanText(true);		
		
/** STEP-5: GENERATE META CSV FILE VARIATIONS -- NOTE: FOR PRODUCTION, NOT NECESSARILY GENERATING THE *TEXT VARIANTS */		
//      msq.getConfig().metaToTable(MetaMode.entries_no_text, true, defaultValLimit, "dedup_all_no_text",msq.isRunAdditional());
//		msq.getConfig().metaToTable(MetaMode.query_stats_with_distinct, true, defaultValLimit, "stats_by_query_distinct",msq.isRunAdditional());
		
		if (AbstractGoogleConfig.dryRun) System.err.println("\n!!! THIS WAS A DRY RUN, TURN OFF 'AbstractGoogleConfig.dryRun' FOR ACTUAL !!!\n");
	}
}

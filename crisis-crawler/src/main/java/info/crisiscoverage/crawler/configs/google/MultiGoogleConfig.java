package info.crisiscoverage.crawler.configs.google;

import info.crisiscoverage.crawler.IOUtils;
import info.crisiscoverage.crawler.CrawlerConstants.Column;
import info.crisiscoverage.crawler.configs.google.AbstractGoogleConfig.DateRestrict;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

public class MultiGoogleConfig  extends AbstractGoogleConfig{
	
	public static final String siteTypeCol = "site_type";
	public static final String siteNameCol = "site_name";
	
	protected final Date dateToday;
	protected final String queryVal; 
	protected final Date crisisDate;  
	
	/**
	 * @param collectionName
	 * @param tags
	 * @param queryVal
	 * @param crisisYear
	 * @param crisisMonth GIVE 1-based month NOT 0-based.
	 * @param crisisDay
	 * @throws IOException
	 */
	public MultiGoogleConfig(
			String collectionName, String tags, String queryVal, int crisisYear, int crisisMonth, int crisisDay)
			throws IOException {
		super(collectionName, tags);
		metaMapper = new MultiSiteMetaMapper();
		this.queryVal = queryVal;
		
  	    Calendar crisisCal = Calendar.getInstance();
  	    dateToday = crisisCal.getTime();
        crisisCal.clear();
        crisisCal.set(crisisYear, crisisMonth-1, crisisDay);
        crisisDate = crisisCal.getTime();
	}

	@Override
	protected Set<String> extenderIgnoreUrlsStartingWith() {
		return new HashSet<>();
	}

	@Override
	protected Set<String> extenderIgnoreUrlsExact() {
		return new HashSet<>();
	}
	
    /**
     * Run query over available sites (or other params if sub-classed).
     * @param msq MultiSiteQuery configures the query operation.
     * @throws Exception
     */
	public void runMultiQuery(MultiQueryObj msq) throws Exception {
		
        Map<Param,String> paramMap = msq.getParamMap();
    
        int apiKeyQueryCount = msq.getApiKeyStartFrom();
        int apiKeyNum = msq.getApiKeyStartFrom();
        String apiKey = apiKeys[apiKeyNum];
        
        List<String> sites;
        Map<String,String> siteTypeMap;
        
        if (msq.isUsingSitesOverride()){
        	sites = msq.getSites();
        	siteTypeMap = msq.getSiteTypeMap();
        } else {
        	sites = mediaLookup;
        	siteTypeMap = mediaTypeMap;
        }
        
        if (msq.getMediaStartFrom() < 0){
        	msq.setMediaStartFrom(0);
        	System.out.println("... setting missing mediaStartFrom to start, '0'");
        }
        
        if (msq.getMediaGoTo() < 0){
        	msq.setMediaGoTo(sites.size()-1);
        	System.out.println("... setting missing mediaGoTo to end, '"+msq.getMediaGoTo()+"'");
        }
        
        int mediaStartFrom = msq.getMediaStartFrom();
        int mediaGoTo = msq.getMediaGoTo();
        System.out.println("### will be running query: '"+queryVal+"' from source # "+msq.getMediaStartFrom()+" to # "+msq.getMediaGoTo()+" ###");
        
        boolean isDateRestricted = msq.getDateRestrict() != null;
        System.out.println("(NOTICE: THIS "+(isDateRestricted? "IS" : "IS NOT")+" A DATE RESTRICTED QUERY)");
        
        int specialStart = 0;
        int specialEnd = 0;
        
        if (msq.isUsingSpecialParam()){
        	System.out.println("--> THIS IS USING SPECIAL PARAM: "+msq.getSpecialParam().name()+", PARAM COUNT: "+msq.getSpecialParamVals().size()+", START-FROM: "+msq.getSpecialParamStartFrom()+", GO-TO: "+msq.getSpecialParamGoTo());
        	specialStart = msq.getSpecialParamStartFrom();
        	specialEnd = msq.getSpecialParamGoTo();
        }

        int totalQueryCount = 0;
        int actionCount = 0;
        for (int p= specialStart; p <= specialEnd; p++){

        	String specialVal = "";
        	if (msq.isUsingSpecialParam()){
        		specialVal = msq.getSpecialParamVals().get(p);
        		paramMap.put(msq.getSpecialParam(), specialVal);
        	}

        	for (int i=mediaStartFrom; i<= mediaGoTo; i++){
        		actionCount++;

        		if (!msq.isPaidQuery() && (apiKeyQueryCount + (msq.getQueriesPerPeriod() * msq.getNumberOfPeriods())) > msq.getApiBailout()){
        			if (apiKeyNum < apiKeys.length-1){
        				apiKeyNum++;
        				System.err.println("... fast approaching query limit for apiKey: "+apiKey+
        						", switching to next available key: "+apiKeys[apiKeyNum]);
        				apiKey = apiKeys[apiKeyNum];
        				totalQueryCount += apiKeyQueryCount;
        				apiKeyQueryCount = 0;
        			} else {
        				System.err.println("\n### Reached max available queries prior to completion, ending attempts! ###\n");
        				break;
        			}
        		}

        		paramMap.put(Param.key, apiKey);

        		String mediaDomain = sites.get(i);
        		paramMap.put(Param.site, mediaDomain);

        		String mediaType = siteTypeMap.get(mediaDomain);
        		System.out.println("\n### Action #"+actionCount+", for site #"+i+", domain: '"+mediaDomain+"', running next queries, queriesPerPeriod: "+msq.getQueriesPerPeriod()+", numberOfPeriods: "+msq.getNumberOfPeriods()+". ###");

        		String customDocIdPortion = "";
        		if (!Strings.isNullOrEmpty("mediaType")){
        			customDocIdPortion += mediaType;
        			if (!Strings.isNullOrEmpty(mediaDomain) || (msq.isUsingSpecialParam() && !Strings.isNullOrEmpty(specialVal)))
        				customDocIdPortion += "-";
        		}
        		if (!Strings.isNullOrEmpty(mediaDomain)){
        			customDocIdPortion += mediaDomain;
        			if (msq.isUsingSpecialParam() && !Strings.isNullOrEmpty(specialVal))
        				customDocIdPortion += "-";
        		}
        		if (msq.isUsingSpecialParam() && !Strings.isNullOrEmpty(specialVal)){
        			if (msq.getSpecialParam().equals(Param.cr)) customDocIdPortion += StringUtils.substringAfter(specialVal, "country");
        			else customDocIdPortion += specialVal;
        		}

        		//HANDLE THE PERIODS PRIOR TO THE CRISIS UNDER PROPER CONDITIONS
        		if (msq.getPadPeriods() > 0 && isDateRestricted && msq.getJumpForwardNPeriods() < 1){

        			int d = msq.getDateRestrict().periodsBack(dateToday, crisisDate, roundDown(msq.getDateRestrict()));
        			int periodsBack = d + msq.padPeriods;
        			System.out.println("\n... running padPeriod query --> starting from crisis period: "+d+", going back #"+msq.getPadPeriods()+", for a total back to period: "+periodsBack);

        			runLiveApiSearch(
        					paramMap, 1, defaultNum*msq.getQueriesPerPeriod(), msq.isArchive(), msq.getDateRestrict(), periodsBack, msq.getPadPeriods(), customDocIdPortion, msq.isForceEnglish(), 0);
        			apiKeyQueryCount += (msq.getQueriesPerPeriod() * (msq.getPadPeriods()));//increment by pad periods
        		} 

        		//HANDLE THE OTHER PERIODS AS LONG AS THIS ISN'T A RUN EXCLUSIVELY FOR PERIODS PRIOR
        		if (!msq.isOnlyRunPadPeriods()){
        			if (isDateRestricted){
        				runLiveApiSearch(
        						paramMap, 1, defaultNum*msq.getQueriesPerPeriod(), msq.isArchive(), msq.getDateRestrict(), crisisDate, msq.getNumberOfPeriods(), customDocIdPortion, msq.isForceEnglish(), msq.getJumpForwardNPeriods());
        				apiKeyQueryCount += (msq.getQueriesPerPeriod() * (msq.getNumberOfPeriods() - msq.getJumpForwardNPeriods()));//increment by periods, accounting for jumpForwardNPeriods

        			} else {
        				runLiveApiSearch(paramMap, 1, defaultNum, msq.isArchive(), customDocIdPortion, msq.isForceEnglish());
        				apiKeyQueryCount ++;//single increment
        			}
        		} else System.out.println("... only running pad periods as directed.");
        	}
        }
        
        totalQueryCount += apiKeyQueryCount;
        System.out.println("--> completed '"+totalQueryCount+"' total queries over '"+(apiKeyNum - msq.apiKeyStartFrom + 1)+
        		"' apiKeys");
	}

	/**
	 * Important test that all files in the api-live folder are valid. 
	 * @param exitOnError boolean whether to terminate execution on issue.
	 * @throws IOException
	 */
	public void verifyLiveApiFiles(boolean exitOnError) throws IOException{
		List<Path> matchStartsWith = IOUtils.findEntriesMatching(
				getApiLiveFolder(), false, FilePart.content, StringMatch.starting_without, defaultXmlHeader, false);
		if (!matchStartsWith.isEmpty()){ 
			System.err.println("ERROR: startsWith test found #"+matchStartsWith.size()+" improper file(s) in api_live folder, please correct prior to proceeding\n!!! (rerun 'extractFromApiDir(...)') !!!");
			if (exitOnError) System.exit(1);
		}			
		List<Path> matchEndsWith = IOUtils.findEntriesMatching(
				getApiLiveFolder(), false, FilePart.content, StringMatch.ending_without, "</feed>", false);
		if (!matchEndsWith.isEmpty()){ 
			System.err.println("ERROR: endsWith test found #"+matchEndsWith.size()+" improper file(s) in api_live folder, please correct prior to proceeding\n!!! (rerun 'extractFromApiDir(...)') !!!");
			if (exitOnError) System.exit(1);
		}
	}
	
	@Override
	protected AdditionalResultObj createAdditionalResultObj(
			String qdId, DateRestrict p, List<Column> headers, List<String> resultHeaders) throws Exception{
		 return new MultiAdditionalResultObj(qdId,p,headers,resultHeaders);
	}
	
	@Override
	protected void addCustomResultHeaders(MetaMode metaMode, List<String> resultHeaders){		
		resultHeaders.add(0,siteTypeCol);
		resultHeaders.add(0,siteNameCol);
		
		System.out.println("\n::: START (RESULT HEADERS AFTER CUSTOM) :::");
		for (String rh : resultHeaders)
			System.out.println("... '"+rh+"'");
		System.out.println("::: END (RESULT HEADERS AFTER CUSTOM) :::");
	}

	public String getQueryVal() {
		return queryVal;
	}

	public Date getCrisisDate() {
		return crisisDate;
	}
}
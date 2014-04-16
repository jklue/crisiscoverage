package info.crisiscoverage.crawler.configs.google;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiSiteGoogleConfig  extends AbstractGoogleConfig{

	protected final String queryVal; 
	protected final Date crisisDate;  
	
	/**
	 * 
	 * @param collectionName
	 * @param tags
	 * @param queryVal
	 * @param crisisYear
	 * @param crisisMonth GIVE 1-based month NOT 0-based.
	 * @param crisisDay
	 * @throws IOException
	 */
	public MultiSiteGoogleConfig(
			String collectionName, String tags, String queryVal, int crisisYear, int crisisMonth, int crisisDay)
			throws IOException {
		super(collectionName, tags);
		metaMapper = new MultiSiteMetaMapper();
		this.queryVal = queryVal;
		
  	    Calendar crisisCal = Calendar.getInstance();
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
     * Run query over available sites.
     * @param msq MultiSiteQuery configures the query operation.
     * @throws Exception
     */
	public void runMultiSiteQuery(MultiSiteQueryObj msq) throws Exception {
		
        Map<Param,String> paramMap = msq.getParamMap();
        
        boolean archive = msq.isArchive();
        boolean forceEnglish = msq.isForceEnglish();
       
        int numberOfPeriods = msq.getNumberOfPeriods();
        int queriesPerPeriod = msq.getQueriesPerPeriod();
          
        int apiKeyQueryCount = msq.getApiKeyStartFrom();
        int apiKeyNum = msq.apiKeyStartFrom;
        String apiKey = apiKeys[apiKeyNum];
        
        int apiBailOut = msq.getApiBailout();
        
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
        
        int totalQueryCount = 0;
        for (int i=mediaStartFrom; i<= mediaGoTo; i++){
        	
        	if (!msq.isPaidQuery() && (apiKeyQueryCount + (queriesPerPeriod * numberOfPeriods)) > apiBailOut){
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
        	System.out.println("\n### Site #"+i+", domain: '"+mediaDomain+"', running next queries, queriesPerPeriod: "+queriesPerPeriod+", numberOfPeriods: "+numberOfPeriods+". ###");
        	
        	String customDocIdPortion = mediaType+"-"+mediaDomain;
        	if (isDateRestricted){
        		runLiveApiSearch(
        				paramMap, 1, defaultNum*queriesPerPeriod, archive, msq.dateRestrict, crisisDate, numberOfPeriods, customDocIdPortion, forceEnglish, msq.getJumpForwardNPeriods());
        		apiKeyQueryCount += (queriesPerPeriod * (numberOfPeriods - msq.getJumpForwardNPeriods()));//increment by periods, accounting for jumpForwardNPeriods

        	} else {
        		runLiveApiSearch(paramMap, 1, defaultNum, archive, customDocIdPortion, forceEnglish);
        		apiKeyQueryCount ++;//single increment
        	}
        }
        
        totalQueryCount += apiKeyQueryCount;
        System.out.println("--> completed '"+totalQueryCount+"' total queries over '"+(apiKeyNum - msq.apiKeyStartFrom + 1)+
        		"' apiKeys");
	}

	public String getQueryVal() {
		return queryVal;
	}

	public Date getCrisisDate() {
		return crisisDate;
	}
}
package info.crisiscoverage.crawler.configs.google;

import info.crisiscoverage.crawler.configs.google.AbstractGoogleConfig.DateRestrict;
import info.crisiscoverage.crawler.configs.google.AbstractGoogleConfig.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiSiteQueryObj {
	
	boolean archive = false;
	boolean crawl = false;
	boolean forceEnglish = false;

	int numberOfPeriods = 10;
	int queriesPerPeriod = 1;
	int jumpForwardNPeriods = 0;

	int mediaStartFrom = 0;
	int mediaGoTo = -1;//THIS WILL GET SET
	
	int apiKeyStartFrom = 0;//THIS IS THE PAID API KEY!!!
	
	final boolean paidQuery;
	int apiBailout = AbstractGoogleConfig.defaultNonPaidBailout;//DEFAULT BAILOUT FOR NON-PAID
	
	Map<Param,String> paramMap = new HashMap<>();
	
	boolean usingSitesOverride;
	List<String> sites = new ArrayList<>();
	Map<String,String> siteTypeMap = new HashMap<>();
	
	DateRestrict dateRestrict = DateRestrict.weeks;

	MultiSiteGoogleConfig config;
	
	public MultiSiteQueryObj(MultiSiteGoogleConfig config, boolean paidQuery){
		this.config = config;
		this.paidQuery = paidQuery;
		if (paidQuery) apiBailout = AbstractGoogleConfig.defaultPaidBailout;
		
		this.generateParamMapForConfig(config);
	}
	
	/**
	 * May be overridden by sub-classes.
	 * @param config
	 * @return
	 */
	protected Map<Param,String> generateParamMapForConfig(MultiSiteGoogleConfig config){
		paramMap.clear();
	        paramMap.put(Param.cx,  config.cxAll);
	        paramMap.put(Param.alt, AbstractGoogleConfig.defaultAlt);	      
	        paramMap.put(Param.q, config.getQueryVal());
	        return paramMap;
	}
	
	public boolean isArchive(){
		return archive;
	}
	
	public boolean isCrawl(){
		return crawl;
	}
		
	public boolean isForceEnglish(){
		return forceEnglish;
	}
	
	protected int getApiBailout(){
		return apiBailout;
	}

	public int getNumberOfPeriods() {
		return numberOfPeriods;
	}

	public void setNumberOfPeriods(int numberOfPeriods) {
		this.numberOfPeriods = numberOfPeriods;
	}

	public int getQueriesPerPeriod() {
		return queriesPerPeriod;
	}

	public void setQueriesPerPeriod(int queriesPerPeriod) {
		this.queriesPerPeriod = queriesPerPeriod;
	}

	public int getMediaStartFrom() {
		return mediaStartFrom;
	}

	public void setMediaStartFrom(int mediaStartFrom) {
		this.mediaStartFrom = mediaStartFrom;
	}

	public int getMediaGoTo() {
		return mediaGoTo;
	}

	public void setMediaGoTo(int mediaGoTo) {
		this.mediaGoTo = mediaGoTo;
	}

	public int getApiKeyStartFrom() {
		return apiKeyStartFrom;
	}

	public void setApiKeyStartFrom(int apiKeyStartFrom) {
		this.apiKeyStartFrom = apiKeyStartFrom;
	}

	public Map<Param, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<Param, String> paramMap) {
		this.paramMap = paramMap;
	}
	
	public boolean isPaidQuery() {
		return paidQuery;
	}

	public void setArchive(boolean archive) {
		this.archive = archive;
	}

	public void setCrawl(boolean crawl) {
		this.crawl = crawl;
	}

	public void setForceEnglish(boolean forceEnglish) {
		this.forceEnglish = forceEnglish;
	}

	public void setApiBailout(int apiBailout) {
		this.apiBailout = apiBailout;
	}
	
	public DateRestrict getDateRestrict() {
		return dateRestrict;
	}

	public void setDateRestrictAndPeriods(DateRestrict dateRestrict, int numberOfPeriods) {
		this.dateRestrict = dateRestrict;
		this.numberOfPeriods = numberOfPeriods;
	}
	
	public List<String> getSites() {
		return sites;
	}

	public Map<String, String> getSiteTypeMap() {
		return siteTypeMap;
	}

	public void setSiteOverrides(List<String> sites,Map<String, String> siteTypeMap) {
		this.sites.addAll(sites);
		this.siteTypeMap.putAll(siteTypeMap);
		this.usingSitesOverride = true;
	}

	public boolean isUsingSitesOverride() {
		return usingSitesOverride;
	}

	public void setUsingSitesOverride(boolean usingSitesOverride) {
		this.usingSitesOverride = usingSitesOverride;
	}

	public int getJumpForwardNPeriods() {
		return jumpForwardNPeriods;
	}

	public void setJumpForwardNPeriods(int jumpForwardNPeriods) {
		this.jumpForwardNPeriods = jumpForwardNPeriods;
	}

	public MultiSiteGoogleConfig getConfig() {
		return config;
	}

	public void setConfig(MultiSiteGoogleConfig config) {
		this.config = config;
	}	
}

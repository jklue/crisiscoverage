package info.crisiscoverage.crawler.configs.google;

import info.crisiscoverage.crawler.configs.google.AbstractGoogleConfig.DateRestrict;
import info.crisiscoverage.crawler.configs.google.AbstractGoogleConfig.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiQueryObj {
	
	boolean archive = false;
	boolean crawl = false;
	boolean forceEnglish = false;
	boolean runAdditional = false;

	int padPeriods = 1;//Important to see the lead up!
	boolean onlyRunPadPeriods;
	int numberOfPeriods = 6;
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
	
	DateRestrict dateRestrict = DateRestrict.months;

	//Special Param to Loop over, e.g. country
	boolean usingSpecialParam = false;
	Param specialParam = null;
	List<String> specialParamVals = new ArrayList<>();
	int specialParamStartFrom = 0;
	int specialParamGoTo = -1;//THIS WILL GET SET
	
	MultiGoogleConfig config;
	
	public MultiQueryObj(MultiGoogleConfig config, boolean paidQuery){
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
	protected Map<Param,String> generateParamMapForConfig(MultiGoogleConfig config){
		paramMap.clear();
	        paramMap.put(Param.cx,  config.cxAll);
	        paramMap.put(Param.alt, AbstractGoogleConfig.defaultAlt);	      
	        paramMap.put(Param.q, config.getQueryVal());
	        paramMap.put(Param.filter, config.defaultFilter);
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

	public void setDateRestrictAndPeriods(DateRestrict dateRestrict, int numberOfPeriods, int padPeriods) {
		this.dateRestrict = dateRestrict;
		this.numberOfPeriods = numberOfPeriods;
		this.padPeriods = padPeriods;
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
	
	public void setSpecialParam(Param specialParam, List<String> specialParamVals) {
		this.specialParam = specialParam;
		this.specialParamVals.addAll(specialParamVals);
		this.specialParamGoTo = this.specialParamVals.size()-1;
		this.usingSpecialParam = true;
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

	public MultiGoogleConfig getConfig() {
		return config;
	}

	public void setConfig(MultiGoogleConfig config) {
		this.config = config;
	}

	public boolean isRunAdditional() {
		return runAdditional;
	}

	public void setRunAdditional(boolean runAdditional) {
		this.runAdditional = runAdditional;
	}

	public int getPadPeriods() {
		return padPeriods;
	}

	public void setPadPeriods(int padPeriods) {
		this.padPeriods = padPeriods;
	}

	public boolean isOnlyRunPadPeriods() {
		return onlyRunPadPeriods;
	}

	public void setOnlyRunPadPeriods(boolean onlyRunPadPeriods) {
		this.onlyRunPadPeriods = onlyRunPadPeriods;
	}

	public int getSpecialParamStartFrom() {
		return specialParamStartFrom;
	}

	public void setSpecialParamStartFrom(int specialParamStartFrom) {
		this.specialParamStartFrom = specialParamStartFrom;
	}

	public int getSpecialParamGoTo() {
		return specialParamGoTo;
	}

	public void setSpecialParamGoTo(int specialParamGoTo) {
		this.specialParamGoTo = specialParamGoTo;
	}

	public boolean isUsingSpecialParam() {
		return usingSpecialParam;
	}

	public Param getSpecialParam() {
		return specialParam;
	}

	public List<String> getSpecialParamVals() {
		return specialParamVals;
	}	
}

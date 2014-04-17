package info.crisiscoverage.crawler.configs;

import info.crisiscoverage.crawler.BasicCrawlController;
import info.crisiscoverage.crawler.CrawlerConstants;
import info.crisiscoverage.crawler.CurrentCrawlExt;
import info.crisiscoverage.crawler.IOUtils;
import info.crisiscoverage.crawler.LinkUtils;
import info.crisiscoverage.crawler.rule.AbstractRuleController;
import info.crisiscoverage.crawler.rule.MetaMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

/**
 * All Configs for crawling should extend this class for best practices.
 * @author mjohns
 * @param O the object class that is handled by an sub-classed config, e.g. Document for html parsing.
 *
 */
public abstract class AbstractCrawlerConfig<O> implements CrawlerConstants{
	
	final protected String collectionName;
	final protected String tags;
	final protected AbstractRuleController<O> ruleController;
	protected MetaMapper<O> metaMapper;
	final protected String textFolderExt;

	protected int numCrawlers = defaultNumCrawlers;
	protected int politenessDelay = defaultPolitenessDelay;
	protected int maxDepthOfCrawling = defaultMaxDepthOfCrawling;
	protected int maxPagesToFetch = defaultMaxPagesToFetch;
	protected boolean resumableCrawling = defaultResumableCrawling;
	
	final protected Set<String> urls = new HashSet<String>();
	final protected String outputFolder;
	final protected String urlFolder;
	final protected String docIdFolder;
	final protected String textFolder;
	final protected String cleanFolder;
	final protected String metaFolder;
	final protected String errorFolder;
	
	/**
	 * Constructor
	 * @param collectionName
	 * @param tags
	 * @throws IOException
	 */
	public AbstractCrawlerConfig(
			String collectionName, String tags, AbstractRuleController<O> ruleController, MetaMapper<O> metaMapper,
			String textFolderExt) throws IOException{
		this.collectionName = collectionName;
		this.tags = tags;
		this.ruleController = ruleController;
		this.metaMapper = metaMapper;
		this.textFolderExt = textFolderExt;
		
		if (ignoreUrlsStartingWith() != null) this.ruleController.getIgnoreUrlsStartingWith().addAll(ignoreUrlsStartingWith());
		if (ignoreUrlsExact() != null) this.ruleController.getIgnoreUrlsExact().addAll(ignoreUrlsExact());
		
		outputFolder = outputFolderName+File.separator+collectionName+File.separator+tags;
		Files.createDirectories(Paths.get(outputFolder));
		
		urlFolder = outputFolder+File.separator+urlFolderName;
		Files.createDirectories(Paths.get(urlFolder));
		
		docIdFolder = outputFolder+File.separator+doc_idFolderName;
		Files.createDirectories(Paths.get(docIdFolder));
		
		textFolder = outputFolder+File.separator+textFolderName;
		Files.createDirectories(Paths.get(textFolder));
		
		cleanFolder = outputFolder+File.separator+cleanFolderName;
		Files.createDirectories(Paths.get(cleanFolder));
		
		metaFolder = outputFolder+File.separator+metaFolderName;
		Files.createDirectories(Paths.get(metaFolder));
		
		errorFolder = outputFolder+File.separator+errorFolderName;
		Files.createDirectories(Paths.get(errorFolder));
	}
	
	/**
	 * Any conditions to be handled in-between actions here.
	 */
	public void resetForRun(){
		urls.clear();
	}
	
	/**
	 * Run Live Search.
	 * 
	 * @param template
	 * @param queryValue
	 * @param minPageOrOffset
	 * @param maxPageOrOffset
	 * @param offsetStepVal
	 * @param archive
	 * @param applyHttpPatternMatcher
	 * @param customDocIdPortion
	 * @throws Exception
	 */
	public void runLiveSearch(
			String template, String queryValue, int minPageOrOffset, int maxPageOrOffset, int offsetStepVal, 
			boolean archive,boolean applyHttpPatternMatcher, String customDocIdPortion) throws Exception{
		resetForRun();
		urls.addAll(LinkUtils.urlSetFromSearchResults(template, queryValue, minPageOrOffset, maxPageOrOffset, applyHttpPatternMatcher));
		LinkUtils.handleIgnoreUrls(urls,ignoreUrlsStartingWith(),ignoreUrlsExact());
		
		LinkUtils.writeUrls(urls,urlFolder,minPageOrOffset,maxPageOrOffset,archive);
	}
	
	/**
	 * Useful for managing manually supplied urls prior to calling crawl.
	 * @param archivePrevious
	 * @param applyHttpPatternMatcher
	 * @param filenameAppend
	 * @throws IOException 
	 */
	public void consolidateUrls(boolean archivePrevious, boolean applyHttpPatternMatcher, String filenameAppend) throws IOException{
		
		System.out.println("\n::: Consolidating URLs within URL Folder :::\n");
		
		resetForRun();
		List<Path> entries = IOUtils.entriesWithinDir(docIdFolder,false);
		urls.addAll(LinkUtils.urlSetFromEntries(entries,applyHttpPatternMatcher));
		LinkUtils.handleIgnoreUrls(urls,ignoreUrlsStartingWith(),ignoreUrlsExact());
		
		LinkUtils.writeUrls(urls,urlFolder,filenameAppend,archivePrevious);
	}
	
	/**
	 * Run Crawler via {@link BasicCrawlController}.
	 * @param archivePrevious
	 * @param applyHttpPatternMatcher
	 * @param resetToCrawlerDefaults true unless sub-class overrode settings.
	 * @throws Exception 
	 */
	public void runCrawler(boolean archivePrevious, boolean applyHttpPatternMatcher, boolean resetToCrawlerDefaults) throws Exception{
		resetForRun();
		List<Path> entries = IOUtils.entriesWithinDir(urlFolder,false);
		urls.addAll(LinkUtils.urlSetFromEntries(entries,applyHttpPatternMatcher));
		LinkUtils.handleIgnoreUrls(urls,ignoreUrlsStartingWith(),ignoreUrlsExact());
		
		if (resetToCrawlerDefaults){
			BasicCrawlController.numCrawlers = numCrawlers;
			BasicCrawlController.politenessDelay = politenessDelay;
			BasicCrawlController.maxDepthOfCrawling = maxDepthOfCrawling;
			BasicCrawlController.maxPagesToFetch = maxPagesToFetch;
			BasicCrawlController.resumableCrawling = resumableCrawling;
		}
		
		String[] params = new String[1+urls.size()];
		params[0] = outputFolder;
		
		int i=0;
		for (String url : urls){
			params[(1+i)] = url;
			i++;
		}
		
		//handle text archive
		if (archivePrevious){
			archiveFolder(textFolder);
		}

		CurrentCrawlExt.getInstance().setUpForNewCrawl(this);
		BasicCrawlController.main(params);
	}
	
	/**
	 * archive all within folder (not recursive).
	 * @param folder String
	 * @throws IOException
	 */
	public void archiveFolder(String folder) throws IOException{
		System.out.println("... archiving previous within folder: "+folder);
		
		Path archivePath = Paths.get(folder,archiveFolderName);
		Files.createDirectories(archivePath);
		
		Path textPath = Paths.get(folder);
		IOUtils.move(IOUtils.getFileEntries(textPath),archivePath);
	}
	
	/**
	 * Clean all Text in folder using common clean options and optionally applying available parse rules.
	 * @param applyParseRules boolean apply ParseRules if available.
	 * @throws IOException
	 */
	public abstract void cleanText(boolean applyParseRules) throws IOException;
	
	/**
	 * Build meta information about the collection by dumping all meta information into an aggregated table.
	 * @param metaMode MetaMode
	 * @param includeHeaders boolean
	 * @param cellSizeLimit int if < 1, no limit.
	 * @param filenameAppend optional String to append to filename
	 * @param runAdditional
	 * @return Path
	 * @throws Exception 
	 */
	public Path metaToTable(MetaMode metaMode, boolean includeHeaders, int cellSizeLimit, String filenameAppend, boolean runAdditional) throws Exception{
		resetForRun();
		
		List<Map<Column,String>> dedupList = new ArrayList<>();
		Path csvFile = Paths.get(metaFolder,IOUtils.filenameFromDate(collectionName+"-"+tags+"_"+filenameAppend, metaFolderExt));
		
		List<Path> entries = IOUtils.entriesWithinDir(docIdFolder,false);
		boolean firstRun = true;
		
		for (Path entry : entries){
			
			CsvOptions csvOptions = CsvOptions.append_data;
			if (firstRun){
				firstRun = false;
				if (includeHeaders)
					csvOptions = CsvOptions.create_headers_data;
				else csvOptions = CsvOptions.create_no_headers_data;
			}
			
			metaToTable(metaMode, csvFile, csvOptions, entry, includeHeaders, cellSizeLimit, filenameAppend,dedupList);
		}
		
		if (runAdditional)
			return additionalMetaToTable(csvFile, metaMode, includeHeaders, cellSizeLimit, filenameAppend);
		else return csvFile;
	}
	
	/**
	 * Sub-classes may want to override.
	 * @param csvFile
	 * @param metaMode
	 * @param includeHeaders
	 * @param cellSizeLimit
	 * @param filenameAppend
	 * @return
	 * @throws Exception
	 */
	protected Path additionalMetaToTable(Path csvFile, MetaMode metaMode, boolean includeHeaders, int cellSizeLimit, String filenameAppend) throws Exception{
		return csvFile;
	}
	
	/**
	 * Build meta information about the collection by dumping all meta information into an aggregated table.
	 * @param metaMode
	 * @param csvFile Path to write to
	 * @param csvOptions CsvOptions to apply
	 * @param entry Path 
	 * @param includeHeaders boolean
	 * @param cellSizeLimit int if < 1, no limit.
	 * @param filenameAppend optional String to append to filename
	 * @param dedupList List of previous rows for dedup.
	 * @throws Exception 
	 */
	public void metaToTable(MetaMode metaMode, Path csvFile, CsvOptions csvOptions, Path entry, boolean includeHeaders, int cellSizeLimit, String filenameAppend, List<Map<Column,String>> dedupList) throws Exception{

		String url = IOUtils.read(entry);
		String docId = getDocIdFromEntry(entry);
		String text = "";
		try{
			text = IOUtils.read(Paths.get(textFolder, docId + textFolderExt));
		} catch(Exception e){
			System.err.println("... text file not found for docId: '"+docId+"', text will be empty.");
		}

		String cleanText = "";
		if (metaMode.isCleanTextMode()){
			try{
				cleanText = IOUtils.read(Paths.get(cleanFolder, docId + cleanFolderExt));
			} catch(Exception e){
				System.err.println("... clean file not found for docId: '"+docId+"', clean text will be empty.");
			}
		}

		Map<Column,String> row = metaMapper.populateColumnMap(collectionName, tags, docId, text, generateMetaToTableObjectFromText(docId, text), url, cleanText, ruleController);
		
		if (metaMode.isQueryStatsMode()){
			Column.removeEntryLevelColumns(row);
		}
		
		if (!metaMode.isCleanTextMode()) {
			row.remove(Column.clean_text);
		}
		
		if (!metaMode.isWithQuery()){
			row.remove(Column.query_distinct);
		}

		if (!isDuplicate(metaMode,row,dedupList)){
			dedupList.add(row);
			IOUtils.writeCsv(csvFile, row, csvOptions, cellSizeLimit);
		}
	}
	
	/**
	 * Strip out the dateRestrict portion .... this is naive so be careful.
	 * @param queryDistinct
	 * @return
	 */
	protected String stripDateRestrictFromQueryDistinct(String queryDistinct){
		if (!Strings.isNullOrEmpty(queryDistinct)){
			if (queryDistinct.contains("-")) return StringUtils.substringBeforeLast(queryDistinct, "-");
		}
		return "";
	}
	
	/**
	 * Sub-classes may override.
	 * @param metaMode
	 * @param row
	 * @param dedupList
	 * @return
	 */
	protected boolean isDuplicate(MetaMode metaMode, Map<Column,String> row, List<Map<Column,String>> dedupList){
		
		if (row == null || dedupList == null) return false;
		
		//Summary columns to test -- all must be equal.
		Column qDistinct = Column.query_distinct;
		Column dateQ = Column.date_query_start;
		Column qPeriod = Column.query_period;
		Column pBack = Column.periods_back;
		
		//Full columns to test
		Column u = Column.url;
		
		for (Map<Column,String> d : dedupList){
			if (metaMode.isQueryStatsMode()){
				if (metaMode.equals(MetaMode.query_stats_only) && row.get(dateQ).equals(d.get(dateQ)) && row.get(qPeriod).equals(d.get(qPeriod)) && row.get(pBack).equals(d.get(pBack)))
					return true;
				else if (metaMode.equals(MetaMode.query_stats_with_distinct) && row.get(qDistinct).equals(d.get(qDistinct)))
						return true;
			} else if (row.get(u).equals(d.get(u))) return true; 
		}
		
		return false;
	}
	
	/**
	 * Special considerations for prepends.
	 * @param entry
	 * @return
	 */
	public String getDocIdFromEntry(Path entry){
		return IOUtils.filenameWithoutExt(entry.getFileName().toString());
	}
	
	/**
	 * Extenders will need to supply an appropriate <O> (e.g. Document for html parsers).
	 * This is the original text, not cleaned text.
	 * @param docId
	 * @param text
	 * @return O
	 * @throws Exception
	 */
	public abstract O generateMetaToTableObjectFromText(String docId, String text) throws Exception;
	
	
	/**
	 * Provided by implementors.
	 * @return
	 */
	public abstract Set<String> ignoreUrlsStartingWith();
	
	/**
	 * Provided by implementors.
	 * @return
	 */
	public abstract Set<String> ignoreUrlsExact();
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	//GETTERS
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getCollectionName() {
		return collectionName;
	}
		
	public String getTags() {
		return tags;
	}
	
	public String getTextFolderExt() {
		return textFolderExt;
	}
	
	public AbstractRuleController<O> getRuleController() {
		return ruleController;
	}
	
	public MetaMapper<O> getMetaMapper() {
		return metaMapper;
	}

	public Set<String> getUrls() {
		return urls;
	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public String getUrlFolder() {
		return urlFolder;
	}

	public String getDocIdFolder() {
		return docIdFolder;
	}

	public String getTextFolder() {
		return textFolder;
	}
	
	public String getCleanFolder() {
		return cleanFolder;
	}

	public String getMetaFolder() {
		return metaFolder;
	}

	public String getErrorFolder() {
		return errorFolder;
	}
	
	public int getNumCrawlers() {
		return numCrawlers;
	}

	public void setNumCrawlers(int numCrawlers) {
		this.numCrawlers = numCrawlers;
	}

	public int getPolitenessDelay() {
		return politenessDelay;
	}

	public void setPolitenessDelay(int politenessDelay) {
		this.politenessDelay = politenessDelay;
	}

	public int getMaxDepthOfCrawling() {
		return maxDepthOfCrawling;
	}

	public void setMaxDepthOfCrawling(int maxDepthOfCrawling) {
		this.maxDepthOfCrawling = maxDepthOfCrawling;
	}

	public int getMaxPagesToFetch() {
		return maxPagesToFetch;
	}

	public void setMaxPagesToFetch(int maxPagesToFetch) {
		this.maxPagesToFetch = maxPagesToFetch;
	}

	public boolean isResumableCrawling() {
		return resumableCrawling;
	}

	public void setResumableCrawling(boolean resumableCrawling) {
		this.resumableCrawling = resumableCrawling;
	}	
}

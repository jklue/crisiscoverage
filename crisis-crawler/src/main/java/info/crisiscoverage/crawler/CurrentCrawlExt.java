package info.crisiscoverage.crawler;

import info.crisiscoverage.crawler.configs.AbstractCrawlerConfig;

/**
 * Singleton class used to communicate into {@link BasicCrawler} which is constructed from within the blackbox crawler4j architecture.
 * 
 * @author mjohns
 *
 */
public class CurrentCrawlExt {

	private static CurrentCrawlExt _instance;
	public static CurrentCrawlExt getInstance(){
		if (_instance == null){
			_instance = new CurrentCrawlExt();
		}
		return _instance;
	}
	
	AbstractCrawlerConfig config;
	
	private CurrentCrawlExt(){}
	
	/**
	 * Setup for a new crawl with the provided object(s). 
	 * This will be called from {@link AbstractCrawlerConfig#runCrawler(boolean, boolean)}.
	 * Note: Method signature should be updated if additional information is needed.
	 * @param config
	 */
	public void setUpForNewCrawl(AbstractCrawlerConfig config){
		resetVarsAfterCrawl();
		this.config = config;
	}
	
	/**
	 * Reset all variables after current crawl is completed. This will be called from {@link BasicCrawler#onBeforeExit()}
	 */
	public void resetVarsAfterCrawl(){
		config = null;
	}

	public AbstractCrawlerConfig getConfig() {
		return config;
	}
}

package info.crisiscoverage.crawler.configs.bbc;

import info.crisiscoverage.crawler.IOUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class HaiyanBbcConfig extends AbstractBbcConfig {
	
	// These variables are changes potentially from run to run {{
	final static boolean archive = true;
	final static boolean applyHttpPatternMatcher = false;
	final static int minPage = 1;
	final static int maxPage = 2;
	final static String queryValue = "typhoon%20haiyan&text=on&start_day=01&start_month=11&start_year=2013&end_day=01&end_month=03&end_year=2014&sort=date&dir=fd&news=141&news_av=1";
	// }}
	
	/**
	 * Constructor.
	 * @throws IOException
	 */
    public HaiyanBbcConfig() throws IOException{
    	super("haiyan","bbc");
    }
	
    /**
     * Entry-point to run config operations.
     * @param args
     * @throws Exception
     */
	public static void main(String[] args) throws Exception {
        HaiyanBbcConfig config = new HaiyanBbcConfig();
//        config.runLiveSearch(template, queryValue, minPage, maxPage, archive, applyHttpPatternMatcher);
//        config.consolidateUrls(archive, applyHttpPatternMatcher, "manual-consolidation");
//        config.runCrawler(archive, applyHttpPatternMatcher,true);
//        config.cleanText(true);
//
//        IOUtils.maxFileLength(config.getCleanFolder(), false);//17,390
//        IOUtils.fileLength(Paths.get(config.getCleanFolder(),"143"+cleanFolderExt));
//
//        NOTICE: USE THIS ONE!
//        config.metaToTable(false, true, defaultValLimit, "all_no_text");
        
//        config.metaToTable(true, true, defaultValLimit, "all_with_text");
//        config.metaToTable(true, true, 500, "all_500_chars_text");
//        config.metaToTable(true, true, 8000, "all_8000_chars_text");
//        config.metaToTable(true, true, 6000, "all_6000_chars_text");
        
	}

	@Override
	protected Set<String> extenderIgnoreUrlsStartingWith() {
		return new HashSet<String>();
	}

	@Override
	protected Set<String> extenderIgnoreUrlsExact() {
		return new HashSet<String>();
	}	
}

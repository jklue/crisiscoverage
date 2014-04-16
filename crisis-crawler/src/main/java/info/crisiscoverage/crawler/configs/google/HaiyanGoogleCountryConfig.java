package info.crisiscoverage.crawler.configs.google;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HaiyanGoogleCountryConfig extends AbstractGoogleConfig{

public static final String queryVal = "news OR article OR coverage OR Yolanda \"typhoon haiyan\" --weather.com --wikipedia.org"; 

	public HaiyanGoogleCountryConfig()
			throws IOException {
		super("haiyan", "google-all-country");
		metaMapper = new GoogleCountryMetaMapper();
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
     * Entry-point to run config operations.
     * @param args
     * @throws Exception
     */
	public static void main(String[] args) throws Exception {
//		Properties extractProperties = new Properties();
        HaiyanGoogleCountryConfig config = new HaiyanGoogleCountryConfig();

        Map<Param,String> paramMap = new HashMap<>();
        paramMap.put(Param.key, config.apiKeys[0]);
        paramMap.put(Param.cx, config.cxAll);
        paramMap.put(Param.alt, defaultAlt);
//        paramMap.put(Param.num, defaultNum);
//        paramMap.put(Param.site, "cnn.com");
        paramMap.put(Param.dateRestrict, DateRestrict.years.valFor("1"));
        paramMap.put(Param.q, queryVal);
        
        boolean archive = false;
        boolean crawl = false;
        boolean forceEnglish = false;
        Calendar crisisCal = Calendar.getInstance();
        Date todayDate = crisisCal.getTime();
        crisisCal.clear();
        crisisCal.set(2013, 10, 07);
        Date crisisDate = crisisCal.getTime();
        
//        int countryStartFrom = 201;
//        int countryGoTo = 242;
//        for (int i=countryStartFrom; i< countryGoTo; i++){
//        	String countryName = crLookup.get(i);
//        	paramMap.put(Param.cr, countryName);
//        	System.out.println("\n#"+i+" COUNTRY NAME: "+countryName);
//        	config.runLiveApiSearch(paramMap, 1, defaultNum, archive, countryName, forceEnglish);
//        }
        
//        config.runLiveSearch(paramMap, 1, defaultNum, archive, DateRestrict.weeks, crisisDate,9);//9 for full. 
        
//        IOUtils.findReplaceAllFilenames(
//        		IOUtils.entriesWithinDir(config.apiLiveFolder, false), "-country", "-country",true);
        
//         config.extractFromApiDir(archive, crawl);
        
//      config.cleanText(true);
         
//        config.metaToTable(MetaMode.entries_no_text, true, defaultValLimit, "dedup_all_no_text");
        config.metaToTable(MetaMode.query_stats_with_distinct, true, defaultValLimit, "stats_by_query_url");
//        config.metaToTable(MetaMode.entries_with_text, true, defaultValLimit, "all_with_text");
//        config.metaToTable(MetaMode.entries_with_text, true, 500, "all_500_chars_text");
//        config.metaToTable(MetaMode.entries_with_text, true, 8000, "all_8000_chars_text");
//        config.metaToTable(MetaMode.entries_with_text, true, 6000, "all_6000_chars_text");
        
	}
}

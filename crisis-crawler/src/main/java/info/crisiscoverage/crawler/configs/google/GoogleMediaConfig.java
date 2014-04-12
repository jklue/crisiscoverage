package info.crisiscoverage.crawler.configs.google;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GoogleMediaConfig extends AbstractGoogleConfig {
	
	/* THIS IS FOR NEWS */
	public static final String queryVal = "news OR article OR coverage OR Yolanda \"typhoon haiyan\""; 

		public GoogleMediaConfig()
				throws IOException {
			super("haiyan", "google-media");
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
//			Properties extractProperties = new Properties();
	        GoogleMediaConfig config = new GoogleMediaConfig();

	        Map<Param,String> paramMap = new HashMap<>();
	        paramMap.put(Param.key, config.apiKey);
	        paramMap.put(Param.cx, config.cxMedia);//NOTE: cx is MEDIA HERE !!!
	        paramMap.put(Param.alt, defaultAlt);
//	        paramMap.put(Param.num, defaultNum);
//	        paramMap.put(Param.site, "cnn.com");
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
	        
	        config.runLiveApiSearch(paramMap, 1, defaultNum, archive, "", forceEnglish);
	        
	        
//	        config.runLiveSearch(paramMap, 1, defaultNum, archive, DateRestrict.weeks, crisisDate,9);//9 for full. 
	        
//	         config.extractFromApiDir(archive, crawl);
	        
//	      config.cleanText(true);
	         
//	        config.metaToTable(MetaMode.entries_no_text, true, defaultValLimit, "dedup_all_no_text");
//	        config.metaToTable(MetaMode.query_stats_only, true, defaultValLimit, "query_stats");
//	        config.metaToTable(MetaMode.entries_with_text, true, defaultValLimit, "all_with_text");
//	        config.metaToTable(MetaMode.entries_with_text, true, 500, "all_500_chars_text");
//	        config.metaToTable(MetaMode.entries_with_text, true, 8000, "all_8000_chars_text");
//	        config.metaToTable(MetaMode.entries_with_text, true, 6000, "all_6000_chars_text");
	        
		}
}
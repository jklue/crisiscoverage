package info.crisiscoverage.crawler.configs.google;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HaiyanGoogleBlogConfig extends AbstractGoogleConfig{

	/* THIS IS FOR BLOGS */
	public static final String blogQueryVal =  "typhoon haiyan blog --weather.com --wikipedia.org"; 


	public HaiyanGoogleBlogConfig()
			throws IOException {
		super("haiyan", "google-blog");
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
		//				Properties extractProperties = new Properties();
		HaiyanGoogleBlogConfig config = new HaiyanGoogleBlogConfig();

		Map<Param,String> paramMap = new HashMap<>();
		paramMap.put(Param.key, defaultKey);
		paramMap.put(Param.cx, defaultCx);
		paramMap.put(Param.alt, defaultAlt);
		//		        paramMap.put(Param.num, defaultNum);
		//		        paramMap.put(Param.site, "cnn.com");
		//		        paramMap.put(Param.dateRestrict, DateRestrict.years.valFor("1"));
//		paramMap.put(Param.lr, defaultLr);
//		paramMap.put(Param.cr, defaultCr);
		paramMap.put(Param.q, blogQueryVal);

		boolean archive = false;
		boolean crawl = false;
		Calendar crisisCal = Calendar.getInstance();
		crisisCal.clear();
		crisisCal.set(2013, 10, 07);
		Date crisisDate = crisisCal.getTime();

//				config.runLiveSearch(paramMap, 1, defaultNum, archive, DateRestrict.weeks, crisisDate,9);//8 for full. 

//				config.extractFromApiDir(archive, crawl);

//				config.cleanText(true);

//				config.metaToTable(MetaMode.entries_no_text, true, defaultValLimit, "dedup_all_no_text");
//				config.metaToTable(MetaMode.query_stats_only, true, defaultValLimit, "query_stats");
//				config.metaToTable(MetaMode.entries_with_text, true, defaultValLimit, "all_with_text");
//				config.metaToTable(MetaMode.entries_with_text, true, 500, "all_500_chars_text");
//				config.metaToTable(MetaMode.entries_with_text, true, 8000, "all_8000_chars_text");
//				config.metaToTable(MetaMode.entries_with_text, true, 6000, "all_6000_chars_text");

	}
}

package info.crisiscoverage.crawler.configs.google;

import info.crisiscoverage.crawler.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class HaiyanGoogleConfig extends AbstractGoogleConfig{
	
// THIS IS THE ONE WORKING for CNN
//https://www.googleapis.com/customsearch/v1?key=AIzaSyAmUPgD01HIXrwtDP5Xf0vMWmUpDglFXyQ&cx=007061251080714295857%3Anhvoqbzpcim&q=Typhoon+Haiyan&siteSearch=cnn.com&alt=atom&start=	
	
/* THIS IS FOR NEWS */
public static final String newsQueryVal = "typhoon haiyan news OR article OR coverage --blog --weather.com --wikipedia.org"; 

/* THIS IS FOR BLOGS */
//public static final String blogQueryVal =  "typhoon haiyan blog --weather.com --wikipedia.org"; 


	public HaiyanGoogleConfig()
			throws IOException {
		super("haiyan", "google");
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
        HaiyanGoogleConfig config = new HaiyanGoogleConfig();

        Map<Param,String> paramMap = new HashMap<>();
        paramMap.put(Param.key, defaultKey);
        paramMap.put(Param.cx, defaultCx);
        paramMap.put(Param.alt, defaultAlt);
//        paramMap.put(Param.num, defaultNum);
//        paramMap.put(Param.site, "cnn.com");
//        paramMap.put(Param.dateRestrict, DateRestrict.years.valFor("1"));
        paramMap.put(Param.q, newsQueryVal);
        
        boolean archive = true;
        boolean crawl = true;
        Calendar crisisCal = Calendar.getInstance();
        crisisCal.clear();
        crisisCal.set(2013, 10, 07);
        Date crisisDate = crisisCal.getTime();
        
//        config.runLiveSearch(paramMap, 1, 101, archive, DateRestrict.weeks, crisisDate,8);//8 for 
        
//        config.runLiveSearch(paramMap, 102, 202, archive);
        
//         config.extractFromApiDir(archive, crawl);

//        Path apiFile = Paths.get(apiLiveFolderName, "haiyan-cnn-10.xml"); 
//        config.extractFromApiFile(apiFile, false, false, extractProperties, 0, -1);
        
//      config.cleanText(true);
//      IOUtils.maxFileLength(config.getCleanFolder(), false);//17,390
//      IOUtils.fileLength(Paths.get(config.getCleanFolder(),"143"+cleanFolderExt));
         
//        config.metaToTable(false, true, defaultValLimit, "all_no_text");

//        config.metaToTable(true, true, defaultValLimit, "all_with_text");
//        config.metaToTable(true, true, 500, "all_500_chars_text");
//        config.metaToTable(true, true, 8000, "all_8000_chars_text");
//        config.metaToTable(true, true, 6000, "all_6000_chars_text");
        
	}
}

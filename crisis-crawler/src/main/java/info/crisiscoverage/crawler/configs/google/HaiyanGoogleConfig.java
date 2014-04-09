package info.crisiscoverage.crawler.configs.google;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class HaiyanGoogleConfig extends AbstractGoogleConfig{

public final static String queryVal = "key=&cx=&q=Typhoon+Haiyan&siteSearch=cnn.com&alt=atom";
	
// THIS IS THE ONE WORKING for CNN
//https://www.googleapis.com/customsearch/v1?key=AIzaSyAmUPgD01HIXrwtDP5Xf0vMWmUpDglFXyQ&cx=007061251080714295857%3Anhvoqbzpcim&q=Typhoon+Haiyan&siteSearch=cnn.com&alt=atom&start=	
	
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
		Properties extractProperties = new Properties();
        HaiyanGoogleConfig config = new HaiyanGoogleConfig();

        Map<Param,String> paramMap = new HashMap<>();
        paramMap.put(Param.key, defaultKey);
        paramMap.put(Param.cx, defaultCx);
        paramMap.put(Param.alt, defaultAlt);
//        paramMap.put(Param.num, defaultNum);
        paramMap.put(Param.site, "cnn.com");
        paramMap.put(Param.dateRestrict, DateRestrict.years.valFor("1"));
        paramMap.put(Param.q, "Typhoon Haiyan");
        
        boolean archive = true;
        boolean crawl = false;
        config.runLiveSearch(paramMap, 102, 202, archive);
        
         config.extractFromApiDir(archive, crawl);

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

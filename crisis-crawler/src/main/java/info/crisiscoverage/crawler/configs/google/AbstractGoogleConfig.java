package info.crisiscoverage.crawler.configs.google;

import info.crisiscoverage.crawler.IOUtils;
import info.crisiscoverage.crawler.JsoupUtils;
import info.crisiscoverage.crawler.LinkUtils;
import info.crisiscoverage.crawler.configs.AbstractApiXmlDomCrawlerConfig;
import info.crisiscoverage.crawler.configs.BaseCleanupStringRule;
import info.crisiscoverage.crawler.rule.html.AbstractHtmlDomRule;
import info.crisiscoverage.crawler.rule.html.DefaultHtmlRuleController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.UrlEscapers;

public abstract class AbstractGoogleConfig extends AbstractApiXmlDomCrawlerConfig{
	
	public static final String defaultSiteTypeAll = "All";
	public static final String defaultSiteTypeTraditional = "Traditional";
	public static final String defaultSiteTypeIndependent = "Independent";
	public static final String defaultSiteTypeBlogsSocial = "Blogs-Social";
	
	private int tmpCount = 0;

	public static boolean dryRun = false;

	public static final List<String> mediaLookup = new ArrayList<>();
	public static final Map<String, String> mediaTypeMap = new HashMap<>();
	static{
		try {
			List<String> siteLines = IOUtils.readLines(AbstractGoogleConfig.class.getResourceAsStream("media-sites.csv"));

			/* FOR ALL GOOGLE */
			mediaLookup.add("");
			mediaTypeMap.put("", defaultSiteTypeAll);

			boolean firstRow = true;
			for (String line : siteLines){
				if (firstRow){
					firstRow = false;
					continue;
				}

				if (!Strings.isNullOrEmpty(line)){
					List<String> row = new ArrayList<String>();
					StringTokenizer tokenizer = new StringTokenizer(line,"\t");

					while (tokenizer.hasMoreTokens()){
						row.add(tokenizer.nextToken());
					}

					String site = row.get(2);
					String type = row.get(3);
					mediaLookup.add(site);
					mediaTypeMap.put(site,type);
					//					System.out.println("... adding available media site: "+site+", of type: "+type);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final List<String> crLookup = new ArrayList<>();
	public static final Map<String, String> crNameMap = new HashMap<>();
	static{
		try {
			List<String> crLines = IOUtils.readLines(AbstractGoogleConfig.class.getResourceAsStream("cr_lookup.csv"));
			boolean firstRow = true;
			for (String line : crLines){
				if (firstRow){
					firstRow = false;
					continue;
				}
				if (!Strings.isNullOrEmpty(line) && line.contains("country")){
					String country = "country"+StringUtils.substringAfter(line, "country"); 
					crLookup.add(country);
					crNameMap.put(country, StringUtils.substringBefore(line,country).trim());
					//				System.out.println("... adding available cr: "+country);
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static enum Param{
		hl,safe,q,key,cx,alt,site,num,start,dateRestrict,lr,cr,exactTerms,orTerms,excludeTerms;

		public String appendToEscaped(String q, String paramVal){
			String r = q == null? "" : q;
			if (!q.isEmpty()) r+="&";
			String v = paramVal == null? "" : UrlEscapers.urlFragmentEscaper().escape(paramVal);

			switch(this){
			case hl: r += hlParam + v;
			break;
			case lr: r += lrParam + v;
			break;
			case cr: r += crParam + v;
			break;
			case safe: r += safeParam + v;
			break;
			case q: r += qParam + v;
			break;
			case key: r += keyParam + v;
			break;
			case cx: r += cxParam + v;
			break;
			case alt: r += altParam + v;
			break;
			case site: r += siteParam + v;
			break;
			case num: r += numParam + v;
			break;
			case start: r += startParam + v;
			break;
			case dateRestrict: r += dateRestrictParam + v;
			break;
			case exactTerms: r += exactTermsParam + v;
			break;
			case excludeTerms: r += excludeTermsParam + v;
			break;
			case orTerms: r += orTermsParam + v;
			break;
			}

			return r;
		}
	}

	public static enum DateRestrict{
		days,weeks,months,years;

		/**
		 * Get DateRestrict portion, e.g. 'w' from 'w1'
		 * @param dqStr
		 * @return
		 */
		public static DateRestrict dateRestrictOf(String dqStr){
			if (Strings.isNullOrEmpty(dqStr) || dqStr.length() < 1) return null;

			for (DateRestrict d : DateRestrict.values()){
				if (dqStr.toLowerCase().startsWith(d.periodStr())) return d;
			}
			return null;
		}

		/**
		 * Get Number portion, e.g. '1' from 'w1'
		 * @param dqStr
		 * @return
		 */
		public static int periodsBackOf(String dqStr){
			if (Strings.isNullOrEmpty(dqStr) || dqStr.length() < 2) return -1;

			try{
				return Integer.parseInt(
						StringUtils.substringAfter(dqStr,dateRestrictOf(dqStr).periodStr())
						);
			} catch(Exception e){
				System.err.println("... unable to parse DateRestrict numberBackOf() for '"+dqStr+"'");
			}

			return -1;
		}

		/**
		 * Construct a query value for a number, e.g. '1' becomes 'w1'
		 * @param period
		 * @return
		 */
		public String valFor(String period){
			return periodStr()+period;
		}

		public String periodStr(){
			switch(this){
			case days: return "d";
			case weeks: return "w";
			case months: return "m";
			case years:
			default:
				return "y";
			}
		}

		/**
		 * From now to then, get a number back.
		 * @param now
		 * @param then
		 * @param roundDown
		 * @return
		 */
		public int periodsBack(Date now, Date then, boolean roundDown){

			int daysBack = daysBetween(then,now);
			System.out.println("numberBack() result --> [days= "+daysBack+
					"], would be start date: "+dateFromDaysBack(now, daysBack,true).toString());

			switch(this){
			case days: return daysBack;
			default:
				return daysAsPeriods(daysBack, roundDown);
			}
		}

		/**
		 * This is a blunt instrument.
		 * @return
		 */
		public int period(){
			switch(this){
			case days: return 1;
			case weeks: return 7;
			case months: return 31;
			case years: return 365;
			}

			return -1;
		}

		/**
		 * Using a provided date, go back n days. 
		 * Either provided the start of the period or the end of the period.
		 * @param date Date
		 * @param daysBack int
		 * @param periodStart boolean
		 * @return Date
		 */
		public Date dateFromDaysBack(Date date, int daysBack, boolean periodStart){

			if (date == null || daysBack < 0) return null;
			if (daysBack == 0) return date;

			Date startDate = new Date( date.getTime() - (daysBack * millisInDays));
			if (periodStart) return startDate;

			return new Date(startDate.getTime() + period()*millisInDays);
		}

		/**
		 * Days between dates.
		 * @param from Date
		 * @param to Date
		 * @return int
		 */
		public int daysBetween(Date from, Date to){
			return (int)( (to.getTime() - from.getTime()) / (millisInDays)); 
		}

		/**
		 * Days as periods.
		 * @param days int
		 * @param roundDown boolean
		 * @return int periods
		 */
		public int daysAsPeriods(int days, boolean roundDown){
			int period = period();
			if (days < 1) return -1;
			else if (days > period){
				if (roundDown) return ((Double)Math.floor(days/period)).intValue();
				else return ((Double)Math.ceil(days/period)).intValue();
			}
			else return 1;
		}

		/**
		 * Convert from periodsBack to days back.
		 * @param periodsBack int
		 * @return int days.
		 */
		public int periodsAsDaysBack(int periodsBack){
			return period() * periodsBack;	
		}
	}

	public static final String template = "https://www.googleapis.com/customsearch/v1?"+queryToken+offsetToken;

	//  This was for the news but only get 30 days.
	//	final static protected String template = "https://news.google.com/?q="+queryToken+"&page="+pageToken+"&num=100&output=atom";//rss
	//	public static final String queryValue = "%22Typhoon%20Haiyan%22&as_drrb=q&as_qdr=y&num=100&output=atom";

	//	 THIS IS THE ONE WORKING for CNN
	//	https://www.googleapis.com/customsearch/v1?key=AIzaSyAmUPgD01HIXrwtDP5Xf0vMWmUpDglFXyQ&cx=007061251080714295857%3Anhvoqbzpcim&q=Typhoon+Haiyan&alt=atom&num=&siteSearch=cnn.com&start=&dateRestrict=

	//	SAMPLE QUERY FOR 14 weeks back (early DEC as of APR 09)
	/*
	 * 	GOOD --> https://www.googleapis.com/customsearch/v1?safe=high&alt=atom&cx=007061251080714295857:nhvoqbzpcim&hl=en&key=AIzaSyAmUPgD01HIXrwtDP5Xf0vMWmUpDglFXyQ&q=Typhoon%20Haiyan&num=10&start=1&dateRestrict=w14
	 *  NOTE: support adding lr=lang_en, cr=countryUS
	 */

	//FIXME: ADD Browser Query in MetaData results
	//https://www.google.com/search?q=typhoon+haiyan+news+or+article+or+coverage+--blog+--weather.com+--wikipedia.org&safe=high&tbs=cdr:1,cd_min:11/7/2013,cd_max:11/14/2013

	public static final int defaultNonPaidBailout = 100;
	public static final int defaultPaidBailout = 1000;

	public static final String defaultHl = "en";//Interface lang
	public static final String defaultLr = "lang_en";
	public static final String defaultCr = "countryUS";
	public static final String defaultSafe = "high";
	public static final String defaultAlt = "atom";
	public static final int defaultNum = 10;

	public static final String hlParam = "hl=";
	public static final String lrParam = "lr=";
	public static final String crParam = "cr=";
	public static final String safeParam = "safe=";
	public static final String qParam = "q=";
	public static final String keyParam = "key=";
	public static final String cxParam = "cx=";
	public static final String altParam = "alt=";
	public static final String siteParam = "siteSearch=";
	public static final String numParam = "num=";
	public static final String startParam = "start=";
	public static final String dateRestrictParam = "dateRestrict=";	
	public static final String exactTermsParam = "exactTerms=";
	public static final String excludeTermsParam = "excludeTerms=";
	public static final String orTermsParam = "orTerms=";

	public static final ImmutableSet<String> ignoreUrlsStartingWith = ImmutableSet.of(
			//Anything?
			);

	public static final ImmutableSet<String> ignoreUrlsExact = ImmutableSet.of(
			//Anything?
			);

	public static final ImmutableList<AbstractHtmlDomRule> deeperDates = ImmutableList.of(
			//			(AbstractHtmlDomRule)new AttrValueRule("content")
			);

	//THIS NEEDS TO BE LOADED FROM api-key.properties.
	final protected Properties keyProps = new Properties();

	// THIS MUST BE SUPPLIED VIA RESOURCE (see constructor)	
	protected String[] apiKeys;

	//	ALL GOOGLE
	protected String cxAll = "007061251080714295857:fevheb09whc";
	protected String cxMedia = "007061251080714295857:nhvoqbzpcim";

	public AbstractGoogleConfig(String collectionName, String tags) throws IOException {
		super(collectionName, tags, new DefaultHtmlRuleController(), new GoogleApiMetaMapper(), defaultHtmlExt, defaultXmlExt);

		keyProps.load(AbstractGoogleConfig.class.getResourceAsStream("api-key.properties"));
		if (keyProps.containsKey("apiKeys")){
			apiKeys = keyProps.getProperty("apiKeys").split(",");
			System.out.println("... initialized with apiKeys size: "+apiKeys.length);
			for (String apiKey : apiKeys){
				System.out.println("\t'"+apiKey+"'");
			}
		} else apiKeys = new String[]{""};

		//		clean_string
		ruleController.addRuleTo(
				RuleType.parser, ParseStage.clean_string, new BaseCleanupStringRule());
	}

	@Override
	public void runLiveSearch(
			final String template, final String queryValue, int minPageOrOffset, int maxPageOrOffset, int offsetStepVal,
			boolean archive, boolean applyHttpPatternMatcher, final String customDocIdPortion) throws Exception{
		resetForRun();

		int sv = offsetStepVal > 0? offsetStepVal : 1;
		int p = minPageOrOffset;

		int aStep = p+sv-1;
		if (aStep > (maxPageOrOffset)){
			sv = maxPageOrOffset-p;
			System.err.println("\n[adjusted offset step value for last query to '"+sv+"']");
			aStep = p+sv-1;
		}

		while (p < (maxPageOrOffset)){	
			String qv = Param.num.appendToEscaped(queryValue,Integer.toString(sv));
			String url = urlFromTemplate(template, qv, offsetVal(p));
			System.out.println("... building url for live run search --> "+url);

			String dateName = dateNameFromUrl(url);

			String filename = collectionName+"-"+tags+"_"+(customDocIdPortion == null? "" : customDocIdPortion+"-") + p +
					(aStep > 0 ? "-"+aStep : "")+"_"+dateName+apiFolderExt;
			System.out.println("... now writing filename ["+filename+"]");

			if (!dryRun){
				Document doc = LinkUtils.readUrlPolitely(url,true);
				IOUtils.write(Paths.get(apiLiveFolder, filename), doc.outerHtml());
			} else {
				System.err.println("--> THIS IS A DRYRUN ('dryRun=true', CHANGE SETTINGS TO GO LIVE <--");
			}

			p += sv;
			aStep = p+sv-1;

			if (p < maxPageOrOffset && aStep > maxPageOrOffset){
				sv = maxPageOrOffset-p;
				System.err.println("\n[adjusted offset step value for last query to '"+sv+"']");
				aStep = p+sv-1;
			}
		}
	}

	/**
	 * Generate url from template.
	 * @param template
	 * @param queryVal
	 * @param offsetVal
	 * @return
	 */
	protected String urlFromTemplate(String template, String queryVal, String offsetVal){
		String url = template.replace(queryToken, queryVal);
		url = url.replace(offsetToken, offsetVal);
		return url;
	}

	/**
	 * Generate offset
	 * @param offset
	 * @return
	 */
	protected String offsetVal(int offset){
		return "&"+startParam+offset;
	}

	/**
	 * Build Query and run live search, accounting for changing date ranges.
	 * @param params
	 * @param minPageOrOffset
	 * @param maxPageOrOffset
	 * @param archive
	 * @param dateRestrict DateRestrict
	 * @param dateStart Date
	 * @param numberOfDateIncrements int
	 * @param customDocIdPortion
	 * @param forceEnglish
	 * @throws Exception
	 */
	protected void runLiveApiSearch(
			final Map<Param,String> params, int minPageOrOffset, int maxPageOrOffset, boolean archive,
			DateRestrict dateRestrict, Date dateStart, int numberOfDateIncrements,String customDocIdPortion, boolean forceEnglish) throws Exception{
		runLiveApiSearch(params, minPageOrOffset, maxPageOrOffset, archive, dateRestrict, dateStart, numberOfDateIncrements, customDocIdPortion, forceEnglish, 0);
	}

	/**
	 * Build Query and run live search, accounting for changing date ranges.
	 * @param params
	 * @param minPageOrOffset
	 * @param maxPageOrOffset
	 * @param archive
	 * @param dateRestrict DateRestrict
	 * @param dateStart Date
	 * @param numberOfDateIncrements int
	 * @param customDocIdPortion
	 * @param forceEnglish
	 * @param jumpForwardNPeriods
	 * @throws Exception
	 */
	protected void runLiveApiSearch(
			final Map<Param,String> params, int minPageOrOffset, int maxPageOrOffset, boolean archive,
			DateRestrict dateRestrict, Date dateStart, int numberOfDateIncrements,String customDocIdPortion, boolean forceEnglish, int jumpForwardNPeriods) throws Exception{
		
		int periodsBack = dateRestrict.periodsBack(Calendar.getInstance().getTime(), dateStart, roundDown(dateRestrict));	
		runLiveApiSearch(
				params, minPageOrOffset, maxPageOrOffset, archive, dateRestrict, periodsBack, numberOfDateIncrements, customDocIdPortion, forceEnglish, jumpForwardNPeriods);
	}
	
	/**
	 * Build Query and run live search, directing date ranges.
	 * @param params
	 * @param minPageOrOffset
	 * @param maxPageOrOffset
	 * @param archive
	 * @param dateRestrict
	 * @param periodsBack
	 * @param numberOfDateIncrements
	 * @param customDocIdPortion
	 * @param forceEnglish
	 * @param jumpForwardNPeriods
	 * @throws Exception
	 */
	protected void runLiveApiSearch(
			final Map<Param,String> params, int minPageOrOffset, int maxPageOrOffset, boolean archive,
			DateRestrict dateRestrict, int periodsBack, int numberOfDateIncrements,String customDocIdPortion, boolean forceEnglish, int jumpForwardNPeriods) throws Exception{

		//handle archive
		if (archive) archiveCalled();

		//number of increments is 8 ... i, result calculated from 21 ... 14.  If jumpForward, then 15 ... 14. 
		System.out.println("... original periodsBack: "+periodsBack+", new periodsBack: "+(periodsBack-jumpForwardNPeriods));
		if (jumpForwardNPeriods > 0) periodsBack -= jumpForwardNPeriods;

		if (periodsBack > 0){
			//decrement d down to 1 over loop.
			for (int i= jumpForwardNPeriods > 0? jumpForwardNPeriods : 1; i<= numberOfDateIncrements; i++){
				if (periodsBack > 0){
					params.put(Param.dateRestrict, dateRestrict.valFor(Integer.toString(periodsBack)));
					System.out.println("#"+periodsBack+"... runLiveSearch() for next dateRestrict: "+params.get(Param.dateRestrict));
					runLiveApiSearch(params, minPageOrOffset, maxPageOrOffset, false,customDocIdPortion, forceEnglish);
					periodsBack--;//Next run will be closer to NOW!
				} else {
					System.err.println("... calculated number for dateRestrict invalid, skipping.");
					break;
				}
			}
		} else System.err.println("... calculated number for dateRestrict invalid, skipping.");
	}
	
	/**
	 * roundDown?
	 * @param dateRestrict
	 * @return
	 */
	public boolean roundDown(DateRestrict dateRestrict){
		boolean roundDown = dateRestrict.equals(DateRestrict.years) ? true : false;
		return roundDown;
	}
	
	

	/**
	 * Build Query and run live search. This doesn't account for changing date ranges.
	 * @param params
	 * @param minPageOrOffset
	 * @param maxPageOrOffset
	 * @param archive
	 * @param customDocIdPortion
	 * @param forceEnglish
	 * @throws Exception
	 */
	protected void runLiveApiSearch(
			Map<Param,String> params, int minPageOrOffset, int maxPageOrOffset, boolean archive, 
			String customDocIdPortion, boolean forceEnglish) throws Exception{
		String q = "";
		if (archive){
			archiveFolder(apiLiveFolder);
		}

		params.remove(Param.num);//this is handled inline.
		params.remove(Param.start);//this is handled inline.

		if (forceEnglish){
			if (!params.containsKey(Param.hl)) params.put(Param.hl, defaultHl); //THIS FORCES USER INTERFACE
			if (!params.containsKey(Param.lr)) params.put(Param.lr, defaultLr);	//THIS FORCES LANGUAGE		
		}

		if (!params.containsKey(Param.safe)) params.put(Param.safe, defaultSafe);

		for (Entry<Param,String> entry : params.entrySet()){
			Param k = entry.getKey();
			String v = entry.getValue();
			q = k.appendToEscaped(q, v);

			System.out.println("... now q --> "+q);
		}
		runLiveSearch(template, q, minPageOrOffset, maxPageOrOffset, defaultNum, archive, false,customDocIdPortion);
	}

	/**
	 * Archive
	 * @throws IOException
	 */
	protected void archiveCalled() throws IOException{
		archiveFolder(errorFolder);
		archiveFolder(docIdFolder);
		archiveFolder(textFolder);
		archiveFolder(apiEntryFolder);
		archiveFolder(abstractFolder);
	}

	/**
	 * Extract From Api dir, this will archive everything as ids will be disrupted.
	 * @param archive
	 * @param crawlUrls
	 * @throws Exception 
	 */
	public void extractFromApiDir(boolean archive, boolean crawlUrls) throws Exception{

		System.out.println("\n--- Extracting Api Entries for folder '"+apiLiveFolder+"', archive? "+archive+", crawlUrls? "+crawlUrls+" ---\n");
		if (AbstractGoogleConfig.dryRun) System.err.println("--> NOTICE ::: 'AbstractGoogleConfig.dryRun' is enabled <--");
		
		//handle archive
		if (archive) archiveCalled();
		extractFromApiEntries(IOUtils.entriesWithinDir(apiLiveFolder,false), false, crawlUrls);
	}

	/**
	 * Extract from api files, this may archive or may employ smart logic.
	 * @param entries
	 * @param archive
	 * @param crawlUrls
	 * @throws Exception
	 */
	public void extractFromApiEntries(List<Path> entries, boolean archive, boolean crawlUrls)
			throws Exception {		
		tmpCount = 0;

		//handle archive
		if (archive) archiveCalled();
		for (Path entry : entries){
			extractFromApiFile(entry, false, crawlUrls);
		}
	}

	@Override
	public void extractFromApiFile(Path apiFile, boolean archive, boolean crawlUrls) throws Exception {

		//handle archive
		if (archive) archiveCalled();
		extractFromApiFile(apiFile, false, crawlUrls,0,-1);
	}

	/**
	 * Granular control over what range of entries to support within a given apiFile.
	 * @param apiFile
	 * @param archive
	 * @param crawlUrls
	 * @param startFromIdx
	 * @param endAtIdx
	 * @throws Exception
	 */
	public void extractFromApiFile(
			Path apiFile, boolean archive, boolean crawlUrls, int startFromIdx, int endAtIdx) throws Exception {

		//handle archive
		if (archive) archiveCalled();
		int idx = 0;

		String content = IOUtils.read(apiFile);

		Document doc = JsoupUtils.parseXmlDoc(content);
		Elements tags = doc.getElementsByTag("entry");

		int dId = 0;
		for (Element tag : tags){
			tmpCount++;
			idx++;
			dId++;

			String docId = IOUtils.filenameWithoutExt(apiFile.getFileName().toString())+"("+StringUtils.leftPad(""+dId,2,"0")+")";

			if (idx < startFromIdx){
				System.out.println("... skipping docId: '"+docId+"' at idx: '"+idx+"' which is less than startFromIdx: '"+startFromIdx+"' as directed.");
				continue;//poor-man's skip function.
			}
			else if (endAtIdx >0 && idx > endAtIdx){
				System.out.println("... stopping at endAtIdx: '"+endAtIdx+"' as directed.");
				break;
			}

			String title = titleFromEntry(tag,docId);

			String url = tag.getElementsByTag("id").first().text();
			url = ("http"+StringUtils.substringAfter(url,"http")).trim();

			String date = dateFromEntry(tag,docId);

			String abstractText = summaryFromEntry(tag,docId);

			System.out.println("#"+tmpCount+" ... extracting docId: "+docId+", title: "+title+", date: "+date+", url: "+url);

			if (!AbstractGoogleConfig.dryRun){
				//write entry
				Path entryPath = Paths.get(apiEntryFolder, docId+apiFolderExt);
				IOUtils.write(entryPath, wrapEntryInFeedXml(doc,tag));

				//write urls as docId
				Path urlPath = Paths.get(docIdFolder, docId+urlFolderExt);
				IOUtils.write(urlPath, url);

				//write abstract
				Path abstractPath = Paths.get(abstractFolder, docId+textFolderExt);
				abstractText = JsoupUtils.cleanDocBodyToStringForce(abstractText, defaultCleaner, defaultEscapeMode);
				IOUtils.write(abstractPath, abstractText);

				//write text
				if (crawlUrls){

					Document textDoc = null;
					Exception _e = null;
					try{
						textDoc = LinkUtils.readUrlPolitely(url,false);
					} catch (Exception e){
						_e = e;
					}

					if (textDoc == null){
						try{
							String altUrl = tag.getElementsByTag("link").first().attr("href");
							System.err.println("after normal crawl fail, attempting with alt url: "+altUrl);
							textDoc = LinkUtils.readUrlPolitely(altUrl,false);
						} catch (Exception e){}
					}

					if (textDoc != null){
						String text = textDoc.outerHtml();
						Path textPath = Paths.get(textFolder, docId+textFolderExt);
						IOUtils.write(textPath, text);
					} else {
						Path errorPath = Paths.get(errorFolder, docId+textFolderExt);
						IOUtils.write(errorPath,  
								("exception crawling: "+url+" -->\n" + _e == null? "(no stack)" : _e.toString()));
						System.err.println("ERROR CRAWLING ID: "+docId+" -->");
						_e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Official way to get dateName from url.
	 * @param url
	 */
	public static String dateNameFromUrl(String url){
		String dateName = "no_date";
		if (url.contains(Param.dateRestrict.name()+"=")){
			String tmp = StringUtils.substringAfter(url,Param.dateRestrict.name()+"=");
			if (tmp.contains("&"))
				dateName = StringUtils.substringBefore(tmp,"&");
			else if (!tmp.isEmpty()) dateName = tmp;
		}
		return dateName;
	}

	/**
	 * Result Count element from entry (or feed).
	 * @param entry
	 * @return Element
	 */
	public static Element queryElementFrom(Element entry){
		if (entry == null) return null;
		Elements elements = entry.getElementsByTag("opensearch:query");
		if (elements != null && !elements.isEmpty()){
			return elements.first();
		}
		return null;
	}

	/**
	 * Result Count element from entry (or feed).
	 * @param entry
	 * @return Element
	 */
	public static Element resultCountElementFrom(Element entry){
		if (entry == null) return null;
		Elements elements = entry.getElementsByTag("opensearch:totalresults");
		if (elements != null && !elements.isEmpty()){
			return elements.first();
		}
		return null;
	}

	/**
	 * Get the result Count from entry (or feed).
	 * @param entry
	 * @param docId
	 * @return
	 */
	public static String resultCountFromEntry(Element entry, String docId){
		if (entry == null) return "";
		Element rc = resultCountElementFrom(entry);
		if (rc != null) return rc.text() == null? "" : rc.text();
		return "";
	}

	/**
	 * Get title from entry (or feed).
	 * @param entry
	 * @parm docId
	 * @return
	 */
	public static String titleFromEntry(Element entry, String docId){
		String title = "";
		if (entry == null) return title;
		title = StringEscapeUtils.unescapeHtml4(entry.getElementsByTag("title").first().text());
		title = JsoupUtils.cleanDocBodyToStringForce(title, defaultCleaner, defaultEscapeMode);
		return title;
	}

	/**
	 * Get date from entry.
	 * @param entry Element
	 * @param docId
	 * @return String
	 */
	public static String dateFromEntry(Element entry, String docId){
		String date = "";
		if (entry == null) return date;
		Element pagemap = entry.getElementsByTag("cse:pagemap").first();
		if (pagemap != null){
			Elements dates = pagemap.getElementsByAttributeValue("name", "pubdate");
			if (dates == null || dates.isEmpty()) {
				dates = pagemap.getElementsByAttributeValue("name", "datepublished");
			}
			if (dates != null && !dates.isEmpty()) date = dates.first().attr("value");
			else System.err.println("... date not findable by either 'pubdate' or 'datepublished' ");
		} else System.err.println("... pagemap not findable, much less 'pubdate' or 'datepublished' ");
		return date;
	}

	/**
	 * Get summary from entry.
	 * @param entry Element
	 * @param docId
	 * @return String
	 */
	public static String summaryFromEntry(Element entry, String docId){
		String summary = "";
		if (entry == null) return summary;
		else summary = entry.getElementsByTag("summary").first().text();

		return summary;
	}

	/**
	 * This will also grab particular elements from the doc and add them to the entry.
	 * wrap entry in feed.
	 * @param doc
	 * @param entry Element
	 * @return String
	 */
	protected String wrapEntryInFeedXml(Document doc, Element entry){

		Element rc = resultCountElementFrom(doc);
		if (rc != null) entry.appendChild(rc);

		Element q = queryElementFrom(doc);
		if (q != null) entry.appendChild(q);

		return wrapEntryInFeedXml(entry.outerHtml());
	}

	/**
	 * wrap entry in feed.
	 * @param entry String
	 * @return String
	 */
	protected String wrapEntryInFeedXml(String entry){
		StringBuilder sb = new StringBuilder();
		sb.append(defaultXmlHeader).append("\n"); 
		sb.append("<feed gd:kind=\"customsearch#search\" xmlns=\"http://www.w3.org/2005/Atom\" xmlns:cse=\"http://schemas.google.com/cseapi/2010\" "+
				"xmlns:gd=\"http://schemas.google.com/g/2005\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\">").append("\n");
		sb.append(entry).append("\n");
		sb.append("</feed>").append("\n");
		return sb.toString();
	}

	@Override
	public Element generateMetaToTableObjectFromText(String docId,String text) throws Exception {

		if (Strings.isNullOrEmpty(docId)) return null;

		Path entry = Paths.get(apiEntryFolder,docId+apiFolderExt);
		if (Files.isRegularFile(entry)){
			String content = IOUtils.read(entry);
			Document doc = JsoupUtils.parseXmlDoc(content);
			return doc.getElementsByTag("entry").first();
		}
		return null;
	}

	@Override
	final public Set<String> ignoreUrlsStartingWith() {
		Set<String> set = new HashSet<String>();
		set.addAll(ignoreUrlsStartingWith);
		set.addAll(extenderIgnoreUrlsStartingWith());
		return set;
	}

	/**
	 * Allow Sub-Classes to add additional ignore urls.
	 * @return
	 */
	protected abstract Set<String> extenderIgnoreUrlsStartingWith();

	@Override
	final public Set<String> ignoreUrlsExact() {
		Set<String> set = new HashSet<String>();
		set.addAll(ignoreUrlsExact);
		set.addAll(extenderIgnoreUrlsExact());
		return set;
	}

	/**
	 * Allow Sub-Classes to add additional ignore urls.
	 * @return
	 */
	protected abstract Set<String> extenderIgnoreUrlsExact();

	@Override
	protected Path additionalMetaToTable(
			Path csvFile, MetaMode metaMode, boolean includeHeaders, int cellSizeLimit, String filenameAppend) throws Exception{

		//ADD COMPARED_RESULTS
		if (metaMode.isQueryStatsMode()){
			System.out.println("\n### NOW ATTEMPTING TO APPEND COMPARED RESULTS ###\n");
			try {
				List<String> siteLines = IOUtils.readLines(csvFile);

				boolean firstRow = true;
				List<Column> headers = new ArrayList<>();
				List<Column> resultHeaders = new ArrayList<>();
				for (Column c : Column.values()){
					if (c.isQueryAndResultColumn()) resultHeaders.add(c);
				}

				/* key is the queryDistinct with dateRestrict stripped. */
				Map<String, ComparedResultObj> comparedMap = new TreeMap<>();

				int lineNum = -1;
				for (String line : siteLines){
					lineNum++;
					if (!Strings.isNullOrEmpty(line)){

						List<String> row = new ArrayList<String>();
					    
						String l;
						if(firstRow) l = StringUtils.replace(line,",","\t");
						else l = StringUtils.replace(line,"\",\"","\"\t\"");//get a valid separator
						StringTokenizer tokenizer = new StringTokenizer(l,"\t");
						
						while (tokenizer.hasMoreTokens()){
							row.add(StringUtils.replace(tokenizer.nextToken(),"\"",""));
						}						

						if (firstRow){
							firstRow = false;
							for (String cell : row){
								try{
									Column c = Column.valueOf(cell);
									System.out.println("... detected header column: '"+c.name()+"'");
									headers.add(c);
								} catch(Exception e){
									System.err.println("WARNING ::: cell '"+cell+"' in header row not recognized as a Column!");
									headers.add(null);
								}
							}
							continue;
						}

						int qdIdx = headers.indexOf(Column.query_distinct);
						if (qdIdx > -1){

							String qdId = stripDateRestrictFromQueryDistinct(row.get(qdIdx));

							ComparedResultObj cro = null;
							if(comparedMap.containsKey(qdId)){
								cro = comparedMap.get(qdId);
							} else {
								int pIdx = headers.indexOf(Column.query_period);
								if (pIdx > -1){
									String pStr = row.get(pIdx);
									DateRestrict p = null;
									try{
										p = DateRestrict.valueOf(pStr);
									} catch(Exception e){
										e.printStackTrace();
									}

									if (p != null){
										cro = new ComparedResultObj(qdId,p,headers,resultHeaders);
										comparedMap.put(qdId,cro);
									} else {
										System.err.println("... skipping row #"+lineNum+", no detected dateRestrict within query_period cell.");
									}
								} else {
									System.err.println("... skipping row #"+lineNum+", no query_period cell.");
								}
							}

							if (cro == null) continue;

							//HAVE CRO, POPULATE
							cro.addRowToPeriodValMap(row);

						} else {
							System.err.println("... skipping row #"+lineNum+", no query_distinct cell.");
							continue;
						}
					}
				}//end for lines

				//Write out the results
				Path csvFile2 = Paths.get(csvFile.getParent().toString(),"(Compared)"+csvFile.getFileName().toString());
				boolean firstRun = true;
				
				for (Map.Entry<String, ComparedResultObj> entry : comparedMap.entrySet()){
					List<Map<Column,String>> map = entry.getValue().populateCompareResult();
					
					for (Map<Column,String> row : map){
						CsvOptions csvOptions = CsvOptions.append_data;
						if (firstRun){
							firstRun = false;
							if (includeHeaders)
								csvOptions = CsvOptions.create_headers_data;
							else csvOptions = CsvOptions.create_no_headers_data;
						}
						IOUtils.writeCsv(csvFile2, row, csvOptions, cellSizeLimit);
					}
				}

				return csvFile2;
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return csvFile;
	}
}

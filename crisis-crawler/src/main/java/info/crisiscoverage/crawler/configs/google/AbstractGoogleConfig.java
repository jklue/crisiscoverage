package info.crisiscoverage.crawler.configs.google;

import info.crisiscoverage.crawler.IOUtils;
import info.crisiscoverage.crawler.JsoupUtils;
import info.crisiscoverage.crawler.LinkUtils;
import info.crisiscoverage.crawler.configs.AbstractApiXmlDomCrawlerConfig;
import info.crisiscoverage.crawler.rule.html.AbstractHtmlDomRule;
import info.crisiscoverage.crawler.rule.html.DefaultHtmlRuleController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Cleaner;
import org.jsoup.select.Elements;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.google.common.html.HtmlEscapers;
import com.google.common.net.UrlEscapers;

public abstract class AbstractGoogleConfig extends AbstractApiXmlDomCrawlerConfig{
	
	public static enum Param{
		q,key,cx,alt,site,num,start,dateRestrict;
		
		public String appendToEscaped(String q, String paramVal){
			String r = q == null? "" : q;
			if (!q.isEmpty()) r+="&";
			String v = paramVal == null? "" : UrlEscapers.urlFragmentEscaper().escape(paramVal);
			
			switch(this){
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
			}
			
			return r;
		}
	}
	
	public static enum DateRestrict{
		days,weeks,months,years;
		
		public String valFor(String number){
			switch(this){
			case days: return "d"+number;
			case weeks: return "w"+number;
			case months: return "m"+number;
			case years:
				default:
				return "y"+number;
			}
		}
	}
	
	public static final String template = "https://www.googleapis.com/customsearch/v1?"+queryToken+offsetToken;

//  This was for the news but only get 30 days.
//	final static protected String template = "https://news.google.com/?q="+queryToken+"&page="+pageToken+"&num=100&output=atom";//rss
//	public static final String queryValue = "%22Typhoon%20Haiyan%22&as_drrb=q&as_qdr=y&num=100&output=atom";
	
	// THIS IS THE ONE WORKING for CNN
	//https://www.googleapis.com/customsearch/v1?key=AIzaSyAmUPgD01HIXrwtDP5Xf0vMWmUpDglFXyQ&cx=007061251080714295857%3Anhvoqbzpcim&q=Typhoon+Haiyan&alt=atom&num=&siteSearch=cnn.com&start=&dateRestrict=
	
	public static final String defaultAlt = "atom";
	public static final int defaultNum = 10;
	public static final String defaultKey = "AIzaSyAmUPgD01HIXrwtDP5Xf0vMWmUpDglFXyQ";
	public static final String defaultCx = "007061251080714295857:nhvoqbzpcim";
	
	public static final String qParam = "q=";
	public static final String keyParam = "key=";
	public static final String cxParam = "cx=";
	public static final String altParam = "alt=";
	public static final String siteParam = "siteSearch=";
	public static final String numParam = "num=";
	public static final String startParam = "start=";
	public static final String dateRestrictParam = "dateRestrict=";	
	
	public static final ImmutableSet<String> ignoreUrlsStartingWith = ImmutableSet.of(
//Anything?
			);
	
	public static final ImmutableSet<String> ignoreUrlsExact = ImmutableSet.of(
//Anything?
			);
	
	public static final ImmutableList<AbstractHtmlDomRule> deeperDates = ImmutableList.of(
//			(AbstractHtmlDomRule)new AttrValueRule("content")
			);
	
	public AbstractGoogleConfig(String collectionName, String tags) throws IOException {
		super(collectionName, tags, new DefaultHtmlRuleController(), new GoogleApiMetaMapper(), defaultHtmlExt, defaultXmlExt);
		
    }
	
	@Override
	public void runLiveSearch(
			String template, String queryValue, int minPageOrOffset, int maxPageOrOffset, int offsetStepVal,
			boolean archive, boolean applyHttpPatternMatcher) throws Exception{
		resetForRun();
		
		int sv = offsetStepVal > 0? offsetStepVal : 0;
		int p = minPageOrOffset;
		
		if (p+sv > maxPageOrOffset){
			sv = maxPageOrOffset-p;
			System.err.println("\n[adjusted offset step value for last query to '"+sv+"']");
		}
		
		while (p < maxPageOrOffset){	
			String url = urlFromTemplate(template, Param.num.appendToEscaped(queryValue,Integer.toString(sv)), offsetVal(p));
			System.out.println("... building url for live run search --> "+url);
			
			Document doc = LinkUtils.readUrlPolitely(url,true);
			IOUtils.write(Paths.get(apiLiveFolder,
					collectionName+"-"+tags+"_"+p+"-"+(p+sv)+apiFolderExt), doc.outerHtml());
			
			p += sv;
			
			if (p < maxPageOrOffset && p+sv > maxPageOrOffset){
				sv = maxPageOrOffset-p;
				System.err.println("\n[adjusted offset step value for last query to '"+sv+"']");
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
	 * Build Query and run live search.
	 * @param params
	 * @param minPageOrOffset
	 * @param maxPageOrOffset
	 * @param archive
	 * @throws Exception
	 */
	protected void runLiveSearch(Map<Param,String> params, int minPageOrOffset, int maxPageOrOffset, boolean archive) throws Exception{
		String q = "";
		if (archive){
			archiveFolder(apiLiveFolder);
		}
		
		params.remove(Param.num);//this is handled inline.
		params.remove(Param.start);//this is handled inline.
		
		for (Entry<Param,String> entry : params.entrySet()){
			Param k = entry.getKey();
			String v = entry.getValue();
			 q = k.appendToEscaped(q, v);
			 
			 System.out.println("... now q --> "+q);
		}
		runLiveSearch(template, q, minPageOrOffset, maxPageOrOffset, defaultNum, archive, false);
	}
	
	/**
	 * Extract From Api dir, this will archive everything as ids will be disrupted.
	 * @param archive
	 * @param crawlUrls
	 * @throws Exception 
	 */
	public void extractFromApiDir(boolean archive, boolean crawlUrls) throws Exception{
		
		//handle text archive
		if (archive){
			archiveFolder(errorFolder);
			archiveFolder(docIdFolder);
			archiveFolder(textFolder);
			archiveFolder(apiEntryFolder);
			archiveFolder(abstractFolder);
		}
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
		
		//handle text archive
		if (archive){
			archiveFolder(errorFolder);
			archiveFolder(docIdFolder);
			archiveFolder(textFolder);
			archiveFolder(apiEntryFolder);
			archiveFolder(abstractFolder);
		}
		
		Properties props = new Properties();
		for (Path entry : entries){
			extractFromApiFile(entry, false, crawlUrls, props);
		}
	}

	@Override
	public void extractFromApiFile(Path apiFile, boolean archive, boolean crawlUrls, Properties props) throws Exception {
		
		//handle text archive
		if (archive){
			archiveFolder(errorFolder);
			archiveFolder(docIdFolder);
			archiveFolder(textFolder);
			archiveFolder(apiEntryFolder);
			archiveFolder(abstractFolder);
		}
		
		extractFromApiFile(apiFile, false, crawlUrls, props,0,-1);
	}
	
	/**
	 * Granular control over what range of entries to support within a given apiFile.
	 * @param apiFile
	 * @param archive
	 * @param crawlUrls
	 * @param props
	 * @param startFromIdx
	 * @param endAtIdx
	 * @throws Exception
	 */
	public void extractFromApiFile(
			Path apiFile, boolean archive, boolean crawlUrls, Properties props, int startFromIdx, int endAtIdx) throws Exception {
		
		//handle text archive
		if (archive){
			archiveFolder(errorFolder);
			archiveFolder(docIdFolder);
			archiveFolder(textFolder);
			archiveFolder(apiEntryFolder);
			archiveFolder(abstractFolder);
		}
		
		List<Path> entries = IOUtils.entriesWithinDir(apiLiveFolder, false);
		
		int idx = 0;
		for (Path entry : entries){
			String content = IOUtils.read(entry);
			
			Document doc = JsoupUtils.parseXmlDoc(content);
			Elements tags = doc.getElementsByTag("entry");
			
			int dId = 0;
			for (Element tag : tags){
				idx++;
				dId++;
				
				String docId = IOUtils.filenameWithoutExt(entry.getFileName().toString())+"("+dId+")";
				
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
				
				System.out.println("... extracting docId: "+docId+", title: "+title+", date: "+date+", url: "+url);
				
				//write entry
				Path entryPath = Paths.get(apiEntryFolder, docId+apiFolderExt);
				IOUtils.write(entryPath, wrapEntryInFeedXml(tag));
				
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
	 * Get title from entry.
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
		Elements dates = pagemap.getElementsByAttributeValue("name", "pubdate");
		if (dates.isEmpty()) {
			dates = pagemap.getElementsByAttributeValue("name", "datepublished");
		}
		if (!dates.isEmpty()) date = dates.first().attr("value");
		else System.err.println("... date not findable by either 'pubdate' or 'datepublished' ");
		
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
	 * wrap entry in feed.
	 * @param entry Element
	 * @return String
	 */
	protected String wrapEntryInFeedXml(Element entry){
		return wrapEntryInFeedXml(entry.outerHtml());
	}
	
	/**
	 * wrap entry in feed.
	 * @param entry String
	 * @return String
	 */
	protected String wrapEntryInFeedXml(String entry){
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n"); 
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
}

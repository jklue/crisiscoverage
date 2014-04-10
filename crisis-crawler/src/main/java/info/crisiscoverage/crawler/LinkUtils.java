package info.crisiscoverage.crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Strings;

/**
 * 
 * @author mjohns
 *
 */
public class LinkUtils implements CrawlerConstants{
	
	private static long lastUrlRetrievedTime = 0;
	
	/**
	 * Top Level: Get all urls from contents of entries.
	 * 
	 * @param entries
	 * @return Set<String>
	 * @param applyHttpPatternMatcher
	 * @throws IOException 
	 */
	public static Set<String> urlSetFromEntries(List<Path> entries, boolean applyHttpPatternMatcher) throws IOException {
		Set<String> set = new HashSet<String>();

		for (Path entry : entries) {
			readAllUrls(entry,set, applyHttpPatternMatcher);
		}

		System.out.println("# urls: " + set.size());
		return set;
	}
	
	/**
	 * Read all urls from the provided entry into a set.
	 * 
	 * @param entry
	 * @param set
	 * @param applyHttpPatternMatcher
	 * @throws IOException
	 */
	private static void readAllUrls(Path entry, Set<String> set, boolean applyHttpPatternMatcher) throws IOException {
		
		System.out.println("\n ::: URLS for entry '"+entry.toString()+"' :::\n");
		
		List<String> lines = IOUtils.readLines(entry);
		for (String line : lines){
			readAllUrls(line, set, applyHttpPatternMatcher);
		}
	}
	
	/**
	 * Read all urls from string into a set.
	 * @param s
	 * @param set
	 * @param applyHttpPatternMatcher
	 * @return int repeats skipped
	 */
	private static int readAllUrls(final String s, Set<String> set, boolean applyHttpPatternMatcher){
		int repeats = 0;
		if (Strings.isNullOrEmpty(s)) return repeats;
		
		if (applyHttpPatternMatcher){
			Matcher m = httpPattern.matcher(s);
			while (m.find()) {	
				repeats += handleAddingUrlToSet(m.group(),set);
			}
		} else {
			repeats += handleAddingUrlToSet(s,set);
		}
		
		return repeats;
	}
	
	/**
	 * Handle various conditions for adding a provided url to a set. This does not verify that this is indeed a url!
	 * @param s
	 * @param set
	 * @return
	 */
	private static int handleAddingUrlToSet(final String s, Set<String> set){
		
		String urlStr = s;
		if (Strings.isNullOrEmpty(s)) return 1;
		
		if(urlStr.endsWith("\t")) urlStr = StringUtils.substringBeforeLast(urlStr, "\t");
		if(urlStr.endsWith(",")) urlStr = StringUtils.substringBeforeLast(urlStr, ","); 
		if (urlStr.startsWith("(")) urlStr = StringUtils.substringAfter(urlStr, "(");
		if(urlStr.endsWith(")")) urlStr = StringUtils.substringBeforeLast(urlStr, ")");
		
		if (!set.contains(urlStr)){
			System.out.println("... found new url --> '"+urlStr+"'");
			set.add(urlStr);
			return 0;
		} else return 1;		
	}
	
	/**
	 * Top Level: Get urls matching urlPattern from found anchors into a set.  
	 * @param template
	 * @param queryValue
	 * @param pagesFrom
	 * @param pagesTo
	 * @param applyHttpPatternMatcher
	 * @return Set<String>
	 * @throws IOException 
	 */
	public static Set<String> urlSetFromSearchResults(
			String template, String queryValue, int pagesFrom,int pagesTo, boolean applyHttpPatternMatcher) throws Exception{
		
		System.out.println("\n <-- Template: '"+template+"', From: '"+pagesFrom+"', To: '"+pagesTo+"', QueryValue: '"+queryValue+"' -->\n");
		
		Set<String> set = new HashSet<String>();
		for (int i = pagesFrom; i<= pagesTo; i++){
			int size = set.size();
			int repeats = urlSetFromPageAnchors(template,queryValue, Integer.toString(i), set, applyHttpPatternMatcher);
			int diff = set.size()-size; 
			if (diff > 0) System.out.println("[Added '"+diff+"' unique urls total from this page, with '"+repeats+"' repeats skipped.]");
			else System.out.println("[Did not add any unique urls from this page, with '"+repeats+"' repeats skipped.]");
		}
		return set;
	}
	
	/**
	 * Get urls matching urlPattern from found anchors into a set.  
	 * @param template String template 
	 * @param queryValue
	 * @param pageValue
	 * @param set 
	 * @param applyHttpPatternMatcher
	 * @return int repeats skipped.
	 * @throws IOException
	 */
	private static int urlSetFromPageAnchors(
			String template, String queryValue, String pageValue, Set<String> set, boolean applyHttpPatternMatcher) throws Exception{
		String url = template;
		if (Strings.isNullOrEmpty(template)) return 0;
		if (!Strings.isNullOrEmpty(queryValue)) url = StringUtils.replace(url,queryToken, queryValue);
		if (!Strings.isNullOrEmpty(pageValue)) url = StringUtils.replace(url,pageToken, pageValue);
		
		return urlSetFromAnchors(url, set, applyHttpPatternMatcher);
	}

	/**
	 * Get urls matching urlPattern from found anchors into a set. 
	 * @param url
	 * @param set
	 * @param applyHttpPatternMatcher
	 * @return int repeats skipped
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	private static int urlSetFromAnchors(final String url, Set<String> set, boolean applyHttpPatternMatcher) throws Exception {
		int repeats = 0;
		if (Strings.isNullOrEmpty(url)) return repeats;
		
		System.out.println("\n ::: URLS from anchors within '"+url+"' :::\n");
		
		Document doc = readUrlPolitely(url,false);
		
		//(C) Read all urls from anchor hrefs.
		Elements links = JsoupUtils.getAnchors(doc);
		for (Element link : links){
			    String href = JsoupUtils.getAnchors(link);
				repeats += readAllUrls(href,set, applyHttpPatternMatcher); //Note: use 'abs:href' to handle relative urls.
		}
		return repeats;
	}
	
	/**
	 * The preferred way to read urls outside the crawler.
	 * @param url
	 * @param isXml
	 * @return Document
	 * @throws Exception
	 */
	public static Document readUrlPolitely(String url, boolean isXml) throws Exception{
		//(A) Check politeness delay since last get completed.
		long urlRetrievedTime = Calendar.getInstance().getTimeInMillis();
		long diff = urlRetrievedTime - lastUrlRetrievedTime;
		if (diff < BasicCrawlController.politenessDelay){
			Thread.sleep(BasicCrawlController.politenessDelay-diff);
		}

		//(B) Get Document and updated lastUrlRetrievedTime.
		Document doc;
		if (isXml) doc = JsoupUtils.getXmlDocumentFromWeb(url,defaultTimeout,defaultUserAgent);//2 minute timeout
		else doc = JsoupUtils.getDocumentFromWeb(url,defaultTimeout,defaultUserAgent);//2 minute timeout
		lastUrlRetrievedTime = Calendar.getInstance().getTimeInMillis();//Need to update politeness after read is completed.
		return doc;
	}
	
	/**
	 * Write urls to urlFolder, using minPag and maxPage as filenameAppend.
	 * @param urls Collection<String>
	 * @param urlFolder String path to dir
	 * @param minPage
	 * @param maxPage
	 * @param archivePrevious
	 * @throws IOException
	 */
	public static void writeUrls(Collection<String> urls, String urlFolder, int minPage, int maxPage, boolean archivePrevious) throws IOException{
		writeUrls(urls,urlFolder,"pages "+minPage+"-"+maxPage,archivePrevious);
	}
	
	/**
	 * Write urls to urlFolder, supplying optional filenameAppend value (other than date).
	 * @param urls Collection<String>
	 * @param urlFolder String path to dir
	 * @param filenamePrefix
	 * @param archivePrevious
	 * @throws IOException 
	 */
	public static void writeUrls(Collection<String> urls, String urlFolder, String filenameAppend, boolean archivePrevious) throws IOException{
		if (urls == null || Strings.isNullOrEmpty(urlFolder)) return;
		
		Path dir = Paths.get(urlFolder);
		Files.createDirectories(dir);
		
		//Handle archiving
		if (archivePrevious){
			System.out.println("... archiving previous within urlFolder");
			Path archive = Paths.get(dir.toString(),archiveFolderName);
			Files.createDirectories(archive);
			IOUtils.move(IOUtils.getFileEntries(dir),archive);
		}
		
		Path file = Paths.get(dir.toString(),IOUtils.filenameFromDate(filenameAppend, urlFolderExt));
		
		IOUtils.write(file,urls);
	    System.out.println("\n::: Wrote to file: "+file.toString()+", urls size: "+urls.size()+" :::\n");
	}
	
	/**
	 * Handle Ignore Urls
	 * @param urls Iterable<String>
	 * @param startsWithSet Set<String>
	 * @param exactSet Set<String>
	 */
	public static void handleIgnoreUrls(Iterable<String> urls, Set<String> startsWithSet, Set<String> exactSet){
		if (urls == null || (startsWithSet == null && exactSet == null)) return;
		
		System.out.println("\n--- Handling Ignore URL Matching Conditions ---\n");
		
		Iterator<String> it = urls.iterator();
		int keepers = 0;
		int removes = 0;
		while (it.hasNext()){
			if(isIgnoreUrl(it.next(), startsWithSet, exactSet)) {
				removes++;
				it.remove();
			} else keepers++;
		}
		
		if (removes > 0)
			System.out.println("[Removed '"+removes+"' urls based on ignore conditions, leaving '"+keepers+"' total available.]");
		else System.out.println("[No urls removed based on ignore conditions, with '"+keepers+"' total available.]");
	}
	
	/**
	 * is url to be ignored?
	 * @param url if null or empty it will also be flagged for ignore.
	 * @param startsWithSet
	 * @param exactSet
	 * @return boolean for true; otherwise false. 
	 */
	public static boolean isIgnoreUrl(String url, Set<String> startsWithSet, Set<String> exactSet){
		if (Strings.isNullOrEmpty(url)) return true;
		else if (startsWithSet == null && exactSet == null) return false;
				
		String urLower = url.toLowerCase();
		if (startsWithSet != null){
			for (String c : startsWithSet){
				if (urLower.startsWith(c.toLowerCase())){
					System.err.println("... ignore url: '"+url+"' matching 'startsWith' condition: '"+c+"'");
					return true;
				}
			}
		}

		if (exactSet != null){
			for (String c : exactSet){
				if (urLower.equals(c.toLowerCase())){
					System.err.println("... ignore url: '"+url+"' matching 'exact' condition: '"+c+"'");
					return true;
				}
			}
		}
				
		return false;
	}
}

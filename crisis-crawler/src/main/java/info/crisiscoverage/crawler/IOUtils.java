package info.crisiscoverage.crawler;

import info.crisiscoverage.crawler.CrawlerConstants.Column;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

/**
 * 
 * @author mjohns
 *
 */
public class IOUtils implements CrawlerConstants{
	
	/**
	 * Get All entries from a directory, including those in subdirectories.
	 * @param processFolder
	 * @param includeSubDirEntries
	 * @return List<Path> entries
	 * @throws IOException
	 */
	public static List<Path> entriesWithinDir(String processFolder, boolean includeSubDirEntries) throws IOException{
		List<Path> entries = new ArrayList<Path>();
		
		Path dir = Paths.get(processFolder);
		if (Files.isDirectory(dir) && Files.exists(dir)){
			entries.addAll(getFileEntries(dir));
			
			if (includeSubDirEntries){
				List<Path> subdirs = getDirectories(dir);
				for (Path subdir : subdirs){
					entries.addAll(getFileEntries(subdir));
				}
			}
		}
		
		System.out.println("# entries: "+entries.size());
		System.out.println(Arrays.toString(entries.toArray()));
		return entries;
	}

	/**
	 * Get directories within a directory.
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public static List<Path> getDirectories(final Path dir) throws IOException {
		final List<Path> dirlist = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (final Iterator<Path> it = stream.iterator(); it.hasNext();) {
				Path p = it.next();
				if (!Files.isDirectory(p)) continue;
				dirlist.add(p);
				dirlist.addAll(getDirectories(p));//recurse
			}
		}
		return dirlist;
	}

	/**
	 * Get File entries from within a directory.
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public static List<Path> getFileEntries(final Path dir) throws IOException {
		final List<Path> entries = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (final Iterator<Path> it = stream.iterator(); it.hasNext();) {
				Path p = it.next();
				if (Files.isDirectory(p)) continue;
				entries.add(p);
			}
		}
		return entries;
	}
	
	/**
	 * Read a file to String
	 * @param file
	 * @return String
	 * @throws IOException
	 */
	public static String read(Path file) throws IOException{
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
		String line = null;
		while ((line = reader.readLine()) != null) {
				sb.append(line);
		}
		return sb.toString();
	}
	
	/**
	 * Read a file to a list of lines
	 * @param file
	 * @return List<String>
	 * @throws IOException
	 */
	public static List<String> readLines(Path file) throws IOException{
		List<String> list = new ArrayList<String>();
		BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
		String line = null;
		while ((line = reader.readLine()) != null) {
				list.add(line);
		}
		return list;
	}
	
	/**
	 * Write collection to file.
	 * @param file
	 * @param iterable
	 * @throws IOException
	 */
	public static void write(Path file, Iterable<String> iterable) throws IOException{
			
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)){
			Iterator<String> it = iterable.iterator();
			while(it.hasNext()){
				writer.write(it.next());
				writer.newLine();
			}
		}
	}
	
	/**
	 * Write text to file.
	 * @param file
	 * @param collection
	 * @throws IOException
	 */
	public static void write(Path file, String content) throws IOException{
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)){
			
			StringTokenizer tokenizer = new StringTokenizer(content,"\n",false);
			while(tokenizer.hasMoreTokens()){
				writer.write(tokenizer.nextToken());
				writer.newLine();
			}
		}
	}
	
	/**
	 * Write Map<Column,String> to file.
	 * @param file Path
	 * @param columnMap Map<Column,String>
	 * @param csvOptions CsvOptions
	 * @param cellSizeLimit int if < 1 then no limit.
	 * @throws IOException
	 */
	public static void writeCsv(Path file, Map<Column,String> columnMap, CsvOptions csvOptions, int cellSizeLimit) throws IOException{
		
		switch(csvOptions){
		case create_no_headers:
			//Overwrite existing file
			try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,StandardOpenOption.CREATE)){
				writer.write("");
			}
			break;
		case create_headers:
			//Overwrite existing file with headers
			try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,StandardOpenOption.CREATE)){
				writer.write(columnMapToString(columnMap,csvDelim,true,cellSizeLimit));
				writer.newLine();
			}
			break;
		case create_no_headers_data:
			//Overwrite existing file with headers
			try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,StandardOpenOption.CREATE)){
				writer.write(columnMapToString(columnMap,csvDelim,false,cellSizeLimit));
				writer.newLine();
			}
			break;
		case append_data:
			//Append to existing file
			try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,StandardOpenOption.APPEND)){
				writer.write(columnMapToString(columnMap,csvDelim,false,cellSizeLimit));
				writer.newLine();
			}
			break;
		case create_headers_data://fall through to default.
	    default:
	    	//Overwrite existing file
	    	try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,StandardOpenOption.CREATE)){
	    		//write headers
				writer.write(columnMapToString(columnMap,csvDelim,true,cellSizeLimit));
				writer.newLine();
				
				//write data (1 row)
				writer.write(columnMapToString(columnMap,csvDelim,false,cellSizeLimit));
				writer.newLine();
			}
		}
	}
	
	/**
	 * These are written in the order of the {@link Column} enum
	 * @param columnMap
	 * @param delim
	 * @param headersMode
	 * @param cellSizeLimit
	 * @return
	 */
	public static String columnMapToString(Map<Column,String> columnMap, String delim, boolean headersMode, int cellSizeLimit){
		
		StringBuilder sb = new StringBuilder();
		Column[] cols = Column.values();
		boolean applyLimit = cellSizeLimit > 0;
		
		for (int i=0; i < cols.length; i++){
			Column col = cols[i];
			if (!columnMap.containsKey(col)) continue;
			
			if (headersMode){
				sb.append(col.name());
			} else {
				String v = columnMap.get(col);
				if (applyLimit) v =restrictValueSize(v,cellSizeLimit,true);
				sb.append("\""+escapeCsvDisrupters(v)+"\"");
			}
			if (i < columnMap.size()-1) sb.append(delim);
		}
		return sb.toString();
	}
	
	/**
	 * Restrict value to the first 'n' chars.
	 * @param value
	 * @param valueLimit
	 * @param includeTruncatedToken
	 * @return
	 */
	public static String restrictValueSize(String value, int valueLimit, boolean includeTruncatedToken){
		if (Strings.isNullOrEmpty(value)) return "";
		else if (value.length() <= valueLimit) return value;
		else return StringUtils.substring(value, 0, valueLimit)+truncatedToken;
	}
	
	/**
	 * quotes, and newline chars must be escaped.
	 * @param value
	 * @return String escaped.
	 */
	public static String escapeCsvDisrupters(String value){
		String v = StringUtils.replace(value, "\"", "&quot;");
		v = StringUtils.normalizeSpace(v);
//		v = StringUtils.replace(value, "\n", "<br>");
		
		return v;
	}
	
	/**
	 * Move entries to dir.
	 * @param entries
	 * @param toDir
	 * @throws IOException 
	 */
	public static void move(List<Path> entries, Path toDir) throws IOException{
		for (Path entry : entries) move(entry, toDir);
	}
	
	/**
	 * Move entry to dir.
	 * @param entry
	 * @param toDir
	 * @throws IOException
	 */
	public static void move(Path entry, Path toDir) throws IOException{
		Files.move(entry, Paths.get(toDir.toString(),entry.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Copy entries to dir.
	 * @param entries
	 * @param toDir
	 * @throws IOException 
	 */
	public static void copy(List<Path> entries, Path toDir) throws IOException{
		for (Path entry : entries) move(entry, toDir);
	}
	
	/**
	 * Copy entry to dir.
	 * @param entry
	 * @param toDir
	 * @throws IOException
	 */
	public static void copy(Path entry, Path toDir) throws IOException{
		Files.copy(entry, Paths.get(toDir.toString(),entry.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Strip extension from filename.
	 * @param filenameFull
	 * @return
	 */
	public static String filenameWithoutExt(String filenameFull){
		if (Strings.isNullOrEmpty(filenameFull)) return "";
		if (filenameFull.contains("."))
			return StringUtils.substringBeforeLast(filenameFull,".");
		else return filenameFull;
	}
	
	/**
	 * Filename using current date.
	 * @param filenameAppend optional String to append
	 * @param ext String e.g. ".html"
	 * @return String filename
	 */
	public static String filenameFromDate(String filenameAppend, String ext){
		String date = dateFilenameFormat.format(Calendar.getInstance().getTime());
		return date+(filenameAppend == null? "" : "_"+filenameAppend)+ext;
	}
	
	/**
	 * Convenience method to understand the max file length in a directory.
	 * @param dirPath
	 * @param includeSubDirs
	 * @return long or -1 if no files found.
	 * @throws IOException 
	 */
	public static long maxFileLength(String dirPath, boolean includeSubDirEntries) throws IOException{
		long max = -1;
		List<Path> entries =  entriesWithinDir(dirPath, includeSubDirEntries);
		for (Path entry : entries){
			long size = Files.size(entry);
			if (size > max) max = size;
		}
		
		System.out.println("max file length '"+max+"' for files in dirPath: '"+dirPath+"', includeSubDirEntries? "+includeSubDirEntries);
		return max;
	}
	
	/**
	 * Convenience method to understand the length of a file.
	 * @param file
	 * @return long 
	 * @throws IOException 
	 */
	public static long fileLength(Path file) throws IOException{
		long size = Files.size(file);
		System.out.println("file length '"+size+"' for file: '"+file.getFileName().toString()+"'");
		return size;
	}
	
	/**
	 * Utility for sampling string for logging, most likely.
	 * @param s
	 * @param maxChars
	 * @param sample
	 * @return
	 */
	public static String subSetOf(String s, int maxChars, StringSample stringSample){
		if (Strings.isNullOrEmpty(s) || ((s.length()-1) <= maxChars)) return s;
		
//		System.out.println("... stringSample: "+stringSample.name()+", maxChars: "+maxChars+", s.length():"+s.length());
		
		switch(stringSample){
		case begin_only:
			return StringUtils.substring(s, 0,(maxChars-1));
		case end_only:
			return StringUtils.substring(s,(s.length()-maxChars-1), (s.length()-1));
		default:
			String b = subSetOf(s, maxChars/2, StringSample.begin_only);
			String e = subSetOf(s, maxChars/2, StringSample.end_only);
			return b+truncatedToken+e;
		}		
	}
}

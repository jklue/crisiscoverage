package info.crisiscoverage.crawler.rule;

import static org.junit.Assert.*;
import info.crisiscoverage.crawler.CrawlerConstants;
import info.crisiscoverage.crawler.IOUtils;
import info.crisiscoverage.crawler.configs.archive.bbc.HaiyanBbcConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RunMetaTest implements CrawlerConstants{
	
	static HaiyanBbcConfig config;
	static Path csvPath;
	static Path docIdEntry;
	
	static MetaMode metaMode = MetaMode.entries_no_text;
	static boolean includeHeaders = true;
	static int cellSizeLimit = defaultValLimit;
	static String filenameAppend = "RunMetaTest";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		config = new HaiyanBbcConfig();
		csvPath = Paths.get(config.getMetaFolder(),IOUtils.filenameFromDate(filenameAppend, metaFolderExt));
		docIdEntry = Paths.get(config.getDocIdFolder(),"1"+docIdFolderExt);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {}

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void tesMetaToTable() throws Exception {
		List<Map<Column,String>> dedupList = new ArrayList<>();
		config.metaToTable(
				metaMode, csvPath,CsvOptions.create_headers_data,docIdEntry, includeHeaders,cellSizeLimit, filenameAppend,dedupList);
		
//		fail("Not yet implemented");
	}

}

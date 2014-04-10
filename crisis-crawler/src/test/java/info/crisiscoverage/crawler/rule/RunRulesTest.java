package info.crisiscoverage.crawler.rule;

import info.crisiscoverage.crawler.configs.AbstractHtmlCrawlerConfig;
import info.crisiscoverage.crawler.configs.bbc.HaiyanBbcConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RunRulesTest {
	
	static private Path htmlPath;
	static private AbstractHtmlCrawlerConfig config;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		config = new HaiyanBbcConfig();
		htmlPath = Paths.get(config.getTextFolder(),"12.html");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {}

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testParseRules() throws IOException {
		config.cleanText(htmlPath,true);
//		fail("Not yet implemented");
	}

}

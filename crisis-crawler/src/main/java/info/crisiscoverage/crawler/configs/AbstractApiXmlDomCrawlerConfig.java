package info.crisiscoverage.crawler.configs;

import info.crisiscoverage.crawler.rule.AbstractRuleController;
import info.crisiscoverage.crawler.rule.MetaMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.jsoup.nodes.Element;

public abstract class AbstractApiXmlDomCrawlerConfig extends AbstractHtmlCrawlerConfig{

	final protected String apiLiveFolder;
	final protected String apiEntryFolder;
	final protected String apiFolderExt;
	
	public AbstractApiXmlDomCrawlerConfig(String collectionName, String tags,
			AbstractRuleController<Element> ruleController, MetaMapper<Element> metaMapper,
			String textFolderExt, String apiFolderExt) throws IOException {
		super(collectionName, tags, ruleController, metaMapper, textFolderExt);
		
		this.apiFolderExt = apiFolderExt;
		
		apiLiveFolder = outputFolder+File.separator+apiLiveFolderName;
		Files.createDirectories(Paths.get(apiLiveFolder));
		
		apiEntryFolder = outputFolder+File.separator+apiEntryFolderName;
		Files.createDirectories(Paths.get(apiEntryFolder));
	}
	
	/**
	 * Extract information using api file.
	 * @param apiFile
	 * @param archive
	 * @param crawlUrls
	 * @param props
	 */
	public abstract void extractFromApiFile(Path apiFile, boolean archive, boolean crawlUrls, Properties props) throws Exception;

	public String getApiLiveFolder() {
		return apiLiveFolder;
	}

	public String getApiEntryFolder() {
		return apiEntryFolder;
	}

	public String getApiFolderExt() {
		return apiFolderExt;
	}
}

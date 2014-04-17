package info.crisiscoverage.crawler.configs;

import info.crisiscoverage.crawler.IOUtils;
import info.crisiscoverage.crawler.JsoupUtils;
import info.crisiscoverage.crawler.configs.google.AbstractGoogleConfig;
import info.crisiscoverage.crawler.rule.AbstractRuleController;
import info.crisiscoverage.crawler.rule.MetaMapper;
import info.crisiscoverage.crawler.rule.ParseObj;
import info.crisiscoverage.crawler.rule.html.DefaultHtmlMetaMapper;
import info.crisiscoverage.crawler.rule.html.DefaultHtmlRuleController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Whitelist;

public abstract class AbstractHtmlCrawlerConfig extends AbstractCrawlerConfig<Element>{
	
	final protected RuleType ruleType = RuleType.parser;
	final protected String abstractFolder;

	public AbstractHtmlCrawlerConfig(
			String collectionName, String tags, AbstractRuleController<Element> ruleController,
			MetaMapper<Element> metaMapper, String textFolderExt) throws IOException {
		super(collectionName, tags, ruleController, metaMapper, textFolderExt);
		
		abstractFolder = outputFolder+File.separator+abstractFolderName;
		Files.createDirectories(Paths.get(abstractFolder));
	}	
	
	public AbstractHtmlCrawlerConfig(String collectionName, String tags) throws IOException {
		super(collectionName, tags, new DefaultHtmlRuleController(), new DefaultHtmlMetaMapper(), defaultHtmlExt);
		
		abstractFolder = outputFolder+File.separator+abstractFolderName;
		Files.createDirectories(Paths.get(abstractFolder));
	}	

	@Override
	public void cleanText(boolean applyParseRules) throws IOException{
		cleanText(defaultWhitelist, defaultEscapeMode, applyParseRules);
	}
	
	/**
	 * Clean all html into a folder.
	 * @param whitelist Whitelist e.g. Whitelist.simpleText()
	 * @param escapeMode EscapeMode e.g. EscapeMode.xhtml
	 * @param applyParseRules boolean apply ParseRules is available.
	 * @throws IOException 
	 */
	public void cleanText(Whitelist whitelist, EscapeMode escapeMode, boolean applyParseRules) throws IOException{
		
		System.out.println("\n--- Cleaning HTML for folder '"+textFolder+"' into CLEAN Folder ---\n");
		if (AbstractGoogleConfig.dryRun) System.err.println("--> NOTICE ::: 'AbstractGoogleConfig.dryRun' is enabled <--");
		if (ruleController instanceof DefaultHtmlRuleController){
			((DefaultHtmlRuleController)ruleController).setWhitelist(whitelist);
			((DefaultHtmlRuleController)ruleController).setEscapeMode(escapeMode);
		}

		List<Path> entries = IOUtils.getFileEntries(Paths.get(textFolder));
		for (Path entry : entries){
			cleanText(entry, applyParseRules);
		}

	}
	
	/**
	 * Clean all html into a folder.
	 * @param entry
	 * @param applyParseRules boolean apply ParseRules is available.
	 * @throws IOException 
	 */
	public void cleanText(Path entry, boolean applyParseRules) throws IOException{
		
		String text = IOUtils.read(entry);
		String docId = IOUtils.filenameWithoutExt(entry.getFileName().toString());
		Path toFile = Paths.get(cleanFolder,docId+cleanFolderExt);

		if (applyParseRules){
			
			boolean success = false;
			ParseObj resultObj = null;
			try{
				resultObj = ruleController.runRulesForAllParseStages(ruleType, text);
				success = true;
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if (success && resultObj.isAnyResultValid()){
				System.out.println("Success in cleaning, now writing docId '"+docId+"'");

				if (!AbstractGoogleConfig.dryRun){
					resultObj.writeWhichOneResultsToFile(toFile);
				}
			} else {
				Path errFile = Paths.get(errorFolder,docId+textFolderExt);
				System.err.println("Error in cleaning, copying original for triage to '"+errFile.getFileName().toString()+"'");
				if (!AbstractGoogleConfig.dryRun){
					IOUtils.write(errFile, text);
				}
			}
		} else {
			if (!AbstractGoogleConfig.dryRun){
				IOUtils.write(toFile, text);
			}
		}
	}

	@Override
	public Element generateMetaToTableObjectFromText(String docId, String text) throws Exception{
		return JsoupUtils.elementToDoc(text, false);
	}

	public String getAbstractFolder() {
		return abstractFolder;
	}
}

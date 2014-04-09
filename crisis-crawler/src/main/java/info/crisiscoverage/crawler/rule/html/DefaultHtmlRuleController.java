package info.crisiscoverage.crawler.rule.html;

import info.crisiscoverage.crawler.JsoupUtils;
import info.crisiscoverage.crawler.rule.AbstractRuleController;
import info.crisiscoverage.crawler.rule.ParseObj;
import info.crisiscoverage.crawler.rule.string.StringParseObj;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.google.common.base.Strings;

public class DefaultHtmlRuleController extends AbstractRuleController<Element>{

	//These vars can be overridden through setters or directly for sub-classes {{
	protected Whitelist whitelist = defaultWhitelist;
	protected EscapeMode escapeMode = defaultEscapeMode;
	protected Cleaner cleaner = new Cleaner(whitelist);
	// }}
	
	public DefaultHtmlRuleController() {
		super(new DefaultHtmlMetaController(), new DefaultHtmlParseController());
	}

	@Override
	public ParseObj setupParseObjForStage(RuleType ruleType,
			ParseStage parseStage, Iterable iterable, String iterableAsString) {
		
		System.out.println("--> initializing for parseStage: "+parseStage.name());
		
		if (iterable == null && Strings.isNullOrEmpty(iterableAsString)){
			System.err.println("No valid input, returning with empty constructor.");
			if (parseStage.isStringStage()) return new StringParseObj();
			else return new HtmlDomParseObj();
		}
		
		Document doc = null;
		if (parseStage.isObjectStage()){
			if (iterable != null){
				Elements elements = JsoupUtils.elementsFromIterable(iterable);
				if (!elements.isEmpty()){
					System.out.println("... generating doc from #"+elements.size()+" elements.");
					doc = JsoupUtils.docFromElements(elements);
				} 
			} 

			if (doc == null){
				doc = JsoupUtils.elementToDocForce(iterableAsString);
			}
		}
		
		ParseObj po;
		switch(parseStage){
		case init: //fall through
		case orig_string: po = new StringParseObj(iterableAsString);
		break;
		case orig_object: po = new HtmlDomParseObj(doc);
		break;
		case clean_object: po = new HtmlDomParseObj(JsoupUtils.cleanDoc(doc,cleaner));
		break;
		case clean_string: po = new StringParseObj(JsoupUtils.cleanDocBodyToStringForce(iterableAsString, cleaner, escapeMode));
		break;
		case no_next:
		default:
			po = new HtmlDomParseObj(doc);
		}
		
		if (parseStage.equals(ParseStage.clean_object))
			System.out.println("... po.asStringInput() --> "+po.asStringInput());
		
		return po;
	}

	public Whitelist getWhitelist() {
		return whitelist;
	}

	public void setWhitelist(Whitelist whitelist) {
		this.whitelist = whitelist;
		cleaner = new Cleaner(whitelist);
	}

	public EscapeMode getEscapeMode() {
		return escapeMode;
	}

	public void setEscapeMode(EscapeMode escapeMode) {
		this.escapeMode = escapeMode;
	}

	public Cleaner getCleaner() {
		return cleaner;
	}

	public void setCleaner(Cleaner cleaner) {
		this.cleaner = cleaner;
	}	
}

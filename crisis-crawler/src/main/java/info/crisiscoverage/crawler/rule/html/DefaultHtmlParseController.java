package info.crisiscoverage.crawler.rule.html;

import info.crisiscoverage.crawler.rule.AbstractRuleTypeController;

import org.jsoup.nodes.Element;

public class DefaultHtmlParseController extends AbstractRuleTypeController<Element>{
	
	@Override
	public Class<Element> objectClass() {
		return Element.class;
	}
}

package info.crisiscoverage.crawler.configs.google;

import org.jsoup.nodes.Element;

import com.google.common.base.Strings;

public class GoogleCountryMetaMapper extends GoogleApiMetaMapper {
	
	@Override
	protected String queryStringFromQueryElement(Element queryElement){
		if (queryElement == null) return "";
		String q = queryElement.attr("cse:cr");
		if (!Strings.isNullOrEmpty(q)){
			q = AbstractGoogleConfig.crNameMap.get(q);
			if (!Strings.isNullOrEmpty(q))	return q;
		}
		return "";
	}

}

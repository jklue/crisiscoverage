package info.crisiscoverage.crawler.configs.google;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.google.common.base.Strings;

public class MultiCountryMetaMapper extends GoogleApiMetaMapper {
	
	@Override
	protected String queryStringFromQueryElement(Element queryElement){
		if (queryElement == null) return "";
		String cCode = queryElement.attr("cse:cr");
		if (!Strings.isNullOrEmpty(cCode)){
			String q = StringUtils.substringAfter(cCode, "country");
			
			String cName = AbstractGoogleConfig.crNameMap.get(cCode);
			if (!Strings.isNullOrEmpty(cName))
				q += " ("+cName+")";
			
			return q;
		}
		return "";
	}

}

package info.crisiscoverage.crawler.configs.google;

import java.io.IOException;

public class MultiCountryConfig extends MultiGoogleConfig{

	public MultiCountryConfig(String collectionName, String tags, String queryVal, int crisisYear, int crisisMonth, int crisisDay)
			throws IOException {
		super(collectionName, tags, queryVal, crisisYear, crisisMonth, crisisDay);
		metaMapper = new MultiCountryMetaMapper();
	}
}

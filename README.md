
# "From Turmoil to Resolution: Media Coverage Over the Life of a Crisis"

## Big Idea

### From our initial proposal:

>__This project will explore the extent and effects of media coverage on a crisis__, distinguishing between traditional and social media providers and further, to the extent data is available, between stakeholders and non-stakeholders. For anyone interested in the effectiveness of humanitarian or disaster engagement / response, our visual products will facilitate user understanding of which events garner media attention; in turn, a user can use our product to identify strategies how to achieve better results. A crisis for our purposes is a destructive event in human history inspiring extraordinary social and/or media attention. We hypothesize that media coverage is correlated to successful crisis resolution such that intensive coverage results in and from more stakeholder energy being exerted to efficiently manage a crisis. Also, we hypothesize that there are detectable indicators throughout the lifespan of a crisis, from onset to resolution, and we further hypothesize that by systematically analyzing and integrating patterns found in multiple crises, future indicators projected from an ongoing crisis may be predicted at a precision correlated to the quality and quantity of comparable analysis. Predictions will be constrained to represent anticipated conformance to or deviation relative the lifespan of other compared crises.

(visit our [public](http://www.crisiscoverage.info) page to track our progress and see the final product on 01 MAY 2014.)

## Work so far
We have made quite a bit of progress since the project effort kicked off. First, we put together a robust [proposal](assets/proposal_dalal_hatfield_johns.pdf). The breadth of hypothesis and potential data sources was necessary until we really understood which few directions we could really pursue. The potentials have boiled down to the more concrete efforts discussed below.

### Data Ingestion Effort  
We were unable to get historic Twitter data and had no clear choices among a number of search providers other than one of the biggies, namely Google, Bing, or Yahoo. A couple of potentials that didn't pan out due to incomplete results and/or lack of API are [BackTweets](http://backtweets.com), [DuckDuckGo](http://duckduckgo.com) and [Farooq (get url)](). We may still re-assess [Enginuity](http://theenginuity.com/) which offers social interest on pages returned in searches.  
1. Use [Google Custom Search API (v1)](https://developers.google.com/custom-search/docs/overview) for primary statistical data as it offers total results and can be crawled up to complete results under certain service tiers. We are currently using the free level service which affords 100 queries per day per user, with 10 results per query. However, we have access to increased search results as we deem necessary. The Google API allows a number of restrictions including language, date, and countries which will give us a few outlets for effective visualization of crises coverage.  
2. Use a [customized java API client](https://github.com/lukehatfield/crisiscoverage.info/tree/master/crisis-crawler/src/main/java/info/crisiscoverage/crawler) which we wrote in Java for backend processing. This console application wraps around [Crawler4j](http://code.google.com/p/crawler4j/) to crawl any pages identified. This effort is complete for initial goals.  
3. Clean crawled pages for potential text indexing using [Apache Solr](https://lucene.apache.org/solr/‎) or similar and/or [Semantria](http://semantria.com/) for sentiment analysis. We have explored both of these options and have run a small dataset from BBC through Semantria with positive results. We are also interested in word cloud options for results. The cleaning is done by the customized Java client mentioned above.  
4. [TALK about META DATA HERE]  


### Data Visualization

[Talk about Vis HERE]

## Sample Data
We have attached a compressed [sample](data/haiyan/haiyan-9-week-sample.7z) of our current starter queries to separate Google News from Google Blogs. The sample contains partial results of API queries, url crawling, text cleanup, and metadata extraction. The sample focuses on Typhoon Haiyan which struck the Philippines 13 NOV 2013. There are two collections in this sample held in the [google-news] and [google-blogs] folders. Each holds data from 9 weeks around the crisis. The [meta] folder within each provides aggregated metadata extraction with and with clean text as well as pure summary information. Additionally, the [clean] folder contains a stand-alone version of the cleanup results, ready for deeper indexing and analysis. There are other folders that are part of the processing pipeline but these folders are the output side of processing.

Below are the "starter" queries that drove this collection -- we will most certainly be refining these queries now that we have demonstrated viability:  
1. newsQueryVal = "typhoon haiyan news OR article OR coverage --blog --weather.com --wikipedia.org";  
2. blogQueryVal =  "typhoon haiyan blog --weather.com --wikipedia.org"

Each query was used populate the general Google Custom Search API (v1), reapeating over the weeks of interest, relative to the date of query which was 10 APR 2014. Here is the query pattern used in calling the API, where of variable %DATERESTRICT% ranged from 'w22' (week prior to crisis) down to 'w14' (~8 weeks after crisis); also, note that %CX% and %KEY% are Google API authorization params and are masked for privacy. :

[google-news] (numberBack() result --> [days= 154], would be start date: Thu Nov 07 12:08:28 EST 2013)  
```https://www.googleapis.com/customsearch/v1?hl=en&alt=atom&dateRestrict=__%DATERESTRICT%__&q=typhoon%20haiyan%20news%20OR%20article%20OR%20coverage%20--blog%20--weather.com%20--wikipedia.org&cx=__%CX%__&safe=high&key=__%KEY%__&num=10&start=1```

[google-blog] (numberBack() result --> [days= 154], would be start date: Thu Nov 07 12:08:28 EST 2013)  
```https://www.googleapis.com/customsearch/v1?hl=en&alt=atom&dateRestrict=__%DATERESTRICT%__&q=typhoon%20haiyan%20blog%20--weather.com%20--wikipedia.org&cx=__%CX%__&safe=high&key=__%KEY%__&num=10&start=1```  

_Note: In order to keep the sample light-weight, for each week of the 9 queried, only the first 10 results have been returned and processed. So, this is not complete in any stretch but the metadata does offer a blunt instrument. And in its entirety (along with other concepts and more robust samples) this should demonstrate that we have a solid handle on addressing data needs of the project._ 

## Next Steps
1. Use the Google API `cr` param to restrict to countries and build TBD choropleths from the results.  
2. Make use of unstructured indexing / sentiment analysis / word clouds.  
3. Consider crawling the entire corpus of select sites, such as [Top 15 Traditional News Sites](http://www.ebizmba.com/articles/news-websites) and TBD Social Media equivalents.  
4. Introduce crisis comparisons.  

# ["From Turmoil to Resolution: Media Coverage Over the Life of a Crisis"](http://crisiscoverage.info "Crisis Coverage")

## Overview and Motivation: Provide an overview of the  project goals and the motivation for it. Consider that this will be read by people who did not see your project proposal.
### From our initial proposal:
>__This project will explore the extent and effects of media coverage on a crisis__, distinguishing between traditional and social media providers and further, to the extent data is available, between stakeholders and non-stakeholders. For anyone interested in the effectiveness of humanitarian or disaster engagement / response, our visual products will facilitate user understanding of which events garner media attention; in turn, a user can use our product to identify strategies how to achieve better results. A crisis for our purposes is a destructive event in human history inspiring extraordinary social and/or media attention. We hypothesize that media coverage is correlated to successful crisis resolution such that intensive coverage results in and from more stakeholder energy being exerted to efficiently manage a crisis. Also, we hypothesize that there are detectable indicators throughout the lifespan of a crisis, from onset to resolution, and we further hypothesize that by systematically analyzing and integrating patterns found in multiple crises, future indicators projected from an ongoing crisis may be predicted at a precision correlated to the quality and quantity of comparable analysis. Predictions will be constrained to represent anticipated conformance to or deviation relative the lifespan of other compared crises.

We intentionally set broad outlines for the project until we really had a handle on what data would be reasonable. The ‘Questions’ section offers up what we think is more explorable and where we will attempt to scope our efforts. However, reasearch into the more ponderous questions originally presented would be of benefit on subsequent projects.

(visit our [public](http://www.crisiscoverage.info) page to track our progress and see the final product on 01 MAY 2014.)

## Related Work: Anything that inspired you, such as a paper, a web site, visualizations we discussed in class, etc.
There are quite a few, here are some on the processing side of things.
1. [Pickanews: Media Search](http://www.pickanews.com)
2. [IndexDen]( https://addons.heroku.com/indexden)
3. [ajax-solr](https://github.com/evolvingweb/ajax-solr)
4. [Semantria](http://semantria.com)
5. [Enginuity Search Engine](http://theenginuity.com/)
6. [BackTweets](http://backtweets.com)


## Questions: What questions are you trying to answer? How did these questions evolve over the course of the project? What new questions did you consider in the course of your analysis?
How much coverage is there of a crisis?
Where geographically is the coverage coming from?
What can be learned about coverage over time?
What can be learned about coverage by traditional and non-traditional media?
What sentiments can be extracted at various windows of time within a crisis?
What are the key story points that shape a crisis?

## Data: Source, scraping method, cleanup, etc.

We have made quite a bit of progress since the project effort kicked off. First, we put together a robust [proposal](assets/proposal_dalal_hatfield_johns.pdf). The breadth of hypothesis and potential data sources was necessary until we really understood which few directions we could really pursue. The potentials have boiled down to the more concrete efforts discussed below.

### Data Ingestion Effort  
We were unable to get historic Twitter data and had no clear choices among a number of search providers other than one of the biggies, namely Google, Bing, or Yahoo. A couple of potentials that didn't pan out due to incomplete results and/or lack of API are [BackTweets](http://backtweets.com), [DuckDuckGo](http://duckduckgo.com) and [Farooq (get url)](). We may still re-assess [Enginuity](http://theenginuity.com/) which offers social interest on pages returned in searches as well as [News-Is-Free](http://www.newsisfree.com/). However, we understand our core data source to be Google API search and are building our visualization plan around this understanding. Any additional sources will only be added after main objectives are obtained. More specifically, here is our data objectives: 
1. Use [Google Custom Search API (v1)](https://developers.google.com/custom-search/docs/overview) for primary statistical data as it offers total results and can be crawled up to complete results under certain service tiers. We are currently using the free level service which affords 100 queries per day per user, with 10 results per query. However, we have access to increased search results as we deem necessary. The Google API allows a number of restrictions including language, date, and countries which will give us a few outlets for effective visualization of crises coverage.  

2. Use a [customized java API client](https://github.com/lukehatfield/crisiscoverage.info/tree/master/crisis-crawler/src/main/java/info/crisiscoverage/crawler) which we wrote in Java for backend processing. This console application wraps around [Crawler4j](http://code.google.com/p/crawler4j/) to crawl any pages identified. Through abstraction and other good software engineering principles, we are able to rapidly implement new source configs to go target different query results from an API or other web-exposed data provider. A configuration is binned on the file structure by first its collection name and further by one or more succinct tag phrases suitable for using in a filename. An instance example is Typhoon Haiyan which is given a unique collection name of “haiyan” and currently has sample data for tag phrase “bbc”, “google-news”, and “google-blogs” which become sub-folders for storage and processing under the collection. A collection name and tag phrase are also the basis for locally unique ids along with any other distinctives that support separation such as result count and page. We have adopted locally unique identifiers for simplicity in implementation and choose to leverage the abstraction principles to generate a new configuration with new tag phases if query results would be in danger of stepping on previous previous. This effort is complete for initial goals.  

3. Clean crawled pages for potential text indexing using [Apache Solr](https://lucene.apache.org/solr/‎) or similar and/or [Semantria](http://semantria.com/) for sentiment analysis. We have explored both of these options and have run a small dataset from BBC through Semantria with positive results. We are also interested in word cloud options for results. The cleaning is done by the customized Java client mentioned above.  

4. Supply meaningful metadata results for use in front-end visualization products. We currently suply metadata results in a tabular format (as CSV) and operate on a single configuration at a time. As previously mentioned, the configurations are locally scoped to the collection and tag phrase level. Given the consistency in Columns supported in our tabular metadata output, it is quite easy to join results across multiple tables or use the file output separation as series data and handle it separately within the visualization products.  As of 10 APR 2014, we have two major varieties of metadata export -- ‘summary’ and ‘entries’, with the latter being further refined into ‘entries_no_text’ and ‘entries_with_text. Text is the cleaned text discussed briefly above. Here is the MetaMode enum for reference.  
```	
/**
  * What mode is meta in for output.
  * @author mjohns
  */
public static enum MetaMode{
query_stats_only, entries_no_text, entries_with_text;
…
}
```
Entry metadata consists of up to all of the available column fields, with the only exception being the presence or absence of the ‘clean_text’ column. Here is the Column enum for reference.
```
/**
  * Columns used in csv generation of available data /  metadata.
  * Most useful for {@link MetaMapper}
  * @author mjohns
  */
 public static enum Column{
query_run_date,query_period,periods_back,days_back,result_count,date_query_start,date_query_end,	doc_id,domain,title,date_published,collection,tags,url,summary,clean_text;
…
}
```
Summary metadata consists of the following fields: query_run_date,query_period,periods_back,days_back,result_count,date_query_start,date_query_end

### Sample Data
We have attached a compressed [sample](data/haiyan/haiyan-9-week-sample.7z) of our current starter queries to separate Google News from Google Blogs. The sample contains partial results of API queries, url crawling, text cleanup, and metadata extraction. The sample focuses on Typhoon Haiyan which struck the Philippines 13 NOV 2013. There are two collections in this sample held in the [google-news] and [google-blogs] folders. Each holds data from 9 weeks around the crisis. The [meta] folder within each provides aggregated metadata extraction with and with clean text as well as pure summary information. Additionally, the [clean] folder contains a stand-alone version of the cleanup results, ready for deeper indexing and analysis. There are other folders that are part of the processing pipeline but these folders are the output side of processing.

Below are the "starter" queries that drove this collection -- we will most certainly be refining these queries now that we have demonstrated viability:  
1. newsQueryVal = "typhoon haiyan news OR article OR coverage --blog --weather.com --wikipedia.org";  
2. blogQueryVal =  "typhoon haiyan blog --weather.com --wikipedia.org"

Each query was used populate the general Google Custom Search API (v1), reapeating over the weeks of interest, relative to the date of query which was 10 APR 2014. Here is the query pattern used in calling the API, where of variable %DATERESTRICT% ranged from 'w22' (week prior to crisis) down to 'w14' (~8 weeks after crisis); also, note that %CX% and %KEY% are Google API authorization params and are masked for privacy. :

[google-news] (numberBack() result --> [days= 154], would be start date: Thu Nov 07 12:08:28 EST 2013)  
https://www.googleapis.com/customsearch/v1?hl=en&alt=atom&dateRestrict=__%DATERESTRICT%__&q=typhoon%20haiyan%20news%20OR%20article%20OR%20coverage%20--blog%20--weather.com%20--wikipedia.org&cx=__%CX%__&safe=high&key=__%KEY%__&num=10&start=1

[google-blog] (numberBack() result --> [days= 154], would be start date: Thu Nov 07 12:08:28 EST 2013)  
https://www.googleapis.com/customsearch/v1?hl=en&alt=atom&dateRestrict=__%DATERESTRICT%__&q=typhoon%20haiyan%20blog%20--weather.com%20--wikipedia.org&cx=__%CX%__&safe=high&key=__%KEY%__&num=10&start=1

_Note: In order to keep the sample light-weight, for each week of the 9 queried, only the first 10 results have been returned and processed. So, this is not complete in any stretch but the metadata does offer a blunt instrument. And in its entirety (along with other concepts and more robust samples) this should demonstrate that we have a solid handle on addressing data needs of the project._ 

### Next Data Steps
1. Use the Google API `cr` param to restrict to countries and build TBD choropleths from the results.  We will be able to get a single number per period queried up to the entire crisis. The power of this approach is that each crisis only requires 1 query per period per country to use in our choropleth, so if we look at the top 25 countries in the world, only 25 queries are required to build our total aggregate choropleth. Further, if we want to show country responses over time, we can run 10 weekly queries per country and achieve desired results in only 250 total queries. This is a very obtainable result. One challenge will be the search terms across languages as was mentioned in the Design Studio discussion, so we will consider the effectiveness of non-english languages being included in in results.

2. Via the Google API, we are heavily considering crawling the corpus of select sites re, such as [Top 15 Traditional News Sites](http://www.ebizmba.com/articles/news-websites) and same number of Social Media equivalents. By doing a direct comparison of known quantities, a controlled sampling of the myriad possible results, we expact to work-around the otherwise opaque nature of sorting out social from traditional media via terms -- especially if we hope to remain in the free service tier and hope to avoid intensive crawling endeavors. By taking a domain focused approach similar to the previously mentioned country strategy, we will get the same frequency information in 1 query per domain per period, but we can also crawl the crisis related information on the domains which will be of importance to the next discussion point. 

3. Make use of unstructured indexing / sentiment analysis / word clouds.  This falls out of strategic crawling as discussed above as we certainly do not want to try and bite off too much.

4. Introduce crisis comparisons.  

## Exploratory Data Analysis: What visualizations did you use to initially look at your data? What insights did you gain? How did these insights inform your design?
We had a running catalog of visualizations from the course lectures and from d3 oriented sites. The initial mock-up which included the line chart, story points, and brush and link behavior for sentiment or word cloud or similar behavior came to the group nearly immediately as a really good fit for our goals. The data exploration itself was quite dicey. Our original project proposal had many sources that ultimately were not going to be predictably useful, either because the results were sparse or were too opaque to compare among datasets. Additionally, the crawling exploratory efforts were marked with challenges as well. A promising black-box solution [Crawl Anywhere](http://www.crawl-anywhere.com/download-crawl-anywhere/) turned out to have to many issues for our purposes and we ultimately abandoned it in favor of the solution laid out in the ‘Data’ section.


## Design Evolution
What are the different visualizations you considered? Justify the design decisions you made using the perceptual and design principles you learned in the course.

Initially we considered a worldmap that would show locations of Twitter data that related to a certain 'crisis.' When we realized Twitter data is unavailable past one week, we realized we would have to show less geographic data and more 2d chart data. A timeline was suggested to show crisis media according to its published date. The following is an image of our initial sketch.

![Initial Timeline Sketch](images/processbook/initialsketch.jpg "Initial Sketch")

We refined this initial sketch using [Balsamiq Mockups](http://balsamiq.com/products/mockups/ "Balsamiq Mockups"). This version is substantially the same, but gave an idea of the page layout on the project site.

![Refined Timeline Sketch](images/processbook/timeline.png "Refined Timeline Sketch")

All three of us were interested in leveraging the geo-data that would come with our search results. Not only were interested in leveraging D3's world map projection capabilities, but we thought it would add another dimension to our data analysis and give the user another avenue of exploration and discovery. The following was our initial geo-bubble sketch, although after our recent discoveries of Google API's country specific search results, we may be changing this to a country specific chloropleth version that shows media engagement in a crisis by country.

![Overview Sketch](images/processbook/overview.png "Overview Sketch")

Finally we wanted to add a granular level of engagement with the data, so we threw together a rough sketch showing interactivity with actual search results.

![Data Sketch](images/processbook/data.png "Data Sketch")

This is not our primary visualization and may take on a new form as we refine our search results and establish specific avenues for audience engagement. Our design studio partner group thought this page would be of particular interest to the audience as we have a broad topic and the implementation is unique. This course makes it clear we have many types of audience members, some of which may be more interested in 'how' we arrived at our conclusions just as much as 'what' the conclusions were.

## Implementation
Describe the intent and functionality of the interactive visualizations you implemented. Provide clear and well-referenced images showing the key design and interaction elements.

![Annotated Timeline](images/processbook/timeline-annotated.jpg "Annotated Timeline")

The image above shows our current working state of the timeline visualization. This is visible on our [website](http://crisiscoverage.info 'CrisisCoverage.info'). You can see the crisis summary (a), the number of traditional or non-traditional query results (b), key story points (c), the data source picker (d), and the legend (e). 

* (a) The crisis summary area is also a placeholder for the storypoints (c). When a storypoint is hovered, the info in the crisis summary area changes to display details of the hovered storypoint. We wanted this to be a 'narrative' visualization that tells a story and leaves little unexplained. 

* It should be added that upon mouseover of the data points (b) on the area chart, specific query result numbers are shown in a tooltip with the associated date.

* The data source picker (d) allows the audience to select different data sources and update the graph automatically. At the present time this feature uses two graphs and removes one when the other is called. As we finalize our data structure, multiple data sources will resemble each other more closely and hopefully allow for smooth transitions between data views.

* Finally, our legend (e) describes the colors chosen for either 'traditional' or 'non-traditional' data queries.

One word on the website layout itself: we were inspired by this d3 [example](http://remittances.herokuapp.com/?en "Worldwide remittance flows") and its horizontal navigation. This nav style has yet to be implemented on our site, but we do try to impress the equivalent nature of our three main views. 

We also tries to structures a high-level overview, a mid-level timeline view, and a granular level data view. We hope these three views will give a wider audience something interesting to discover while taking maximum advantage of our data-gathering efforts by employing the results in a variety of forms.

## Evaluation: What did you learn about the data by using your visualizations? How did you answer your questions? How well does your visualization work, and how could you further improve it?

[TBD]

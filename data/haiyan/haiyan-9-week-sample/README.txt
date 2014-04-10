This is a sample of our current (naive) starter query to separate Google News from Google Blogs. The sample contains partial results of API queries, url crawling, text cleanup, and metadata extraction. The sample focuses on Typhoon Haiyan which struck the Philippines 13 NOV 2013. There are two collections in this sample held in the [google-news] and [google-blogs] folders. Each holds data from 9 weeks around the crisis. The [meta] folder within each provides aggregated metadata extraction with and with clean text as well as pure summary information. Additionally, the [clean] folder contains a stand-alone version of the cleanup results, ready for deeper indexing and analysis. There are other folders that are part of the processing pipeline but these folders are the output side of processing.

Here are the queries that drove this collection (We will most certainly be refining these queries now that we have demonstrated viability):
(1)newsQueryVal = "typhoon haiyan news OR article OR coverage --blog --weather.com --wikipedia.org"; 
(2)blogQueryVal =  "typhoon haiyan blog --weather.com --wikipedia.org"

Each query was used populate the general Google Custom Search API (v1), reapeating over the weeks of interest, relative to the date of query which was 10 APR 2014. Here is the query pattern used in calling the API, where of variable %DATERESTRICT% ranged from 'w22' (week prior to crisis) down to 'w14' (~8 weeks after crisis); also, note that %CX% and %KEY% are Google API authorization params and are masked for privacy. :

[google-news] (numberBack() result --> [days= 154], would be start date: Thu Nov 07 12:08:28 EST 2013)
https://www.googleapis.com/customsearch/v1?hl=en&alt=atom&dateRestrict=%DATERESTRICT%&q=typhoon%20haiyan%20news%20OR%20article%20OR%20coverage%20--blog%20--weather.com%20--wikipedia.org&cx=%CX%&safe=high&key=%KEY%&num=10&start=1

[google-blog] (numberBack() result --> [days= 154], would be start date: Thu Nov 07 12:08:28 EST 2013)
https://www.googleapis.com/customsearch/v1?hl=en&alt=atom&dateRestrict=%DATERESTRICT%&q=typhoon%20haiyan%20blog%20--weather.com%20--wikipedia.org&cx=%CX%&safe=high&key=%KEY%&num=10&start=1

In order to keep the sample light-weight, for each week of the 9 queried, only the first 10 results have been returned and processed. So, this is not complete in any stretch but the metadata does offer a blunt instrument. And in its entirety (along with other concepts and more robust samples) this should demonstrate that we have a solid handle on addressing data needs of the project. 


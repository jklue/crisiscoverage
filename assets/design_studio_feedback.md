# Milestone Design Studio Feedback
### Feedback received from: 
* Carolina Nobre
* Rachel Boyce 

### Overall quality of the feedback: 
They both provided useful feedback. They highlighted several challenges we already aware due to limitations of the Google API, and raised a couple of of points we found very useful when it came to clarifying our narrative. Both Rachel and Carolina were enthusiastic and provided solid feedback. 

## Feedback

### General / Audience 
The feedback we got was overall positive. Rachel and Carolina did raise an interesting point:
>The perceived audience based off the visualization seems to primarily be journalists and news publications that might interested in SEO. This is based on the heavy focus on media coverage and the deep analysis of new providers. 

Our target audience is still in general people interested having a better understanding a given crisis, the content of media coverage, and the saturation of messages within analyzed coverage. The takeaway we got from the feedback is that we can perhaps better annotate our visualization so that the narrative we wish to convey is more evident. To achieve this, we will focus both fleshing out the visualizations at the detail level (described below) and providing a bit more contextual text to tell a story. 

### Visualization 

Rachel and Carolina raised the following points:

1.  The story points on the timeline were a bit confusing
2.  Why does the BBC graph have resolution than the Google graph
3.  The geographic map seems a bit redundant with the timeline in that it provides the same dimension of high level information

As we better develop our visualization the story points will be more clear. We will add some additional text explaining the purpose of the story points, which is to provide key events to establish a narrative for the crisis. The primary reason that the BBC graph has more resolution is because we have more through data from the BBC search API. We recently got our Google Search API working, so once the crawler runs, the overall quality of our google data will be better. 

We had a good discussion about the purpose of the world map. Michael based on his research of the Google API came up with an interesting use of the world map. The Google Search API gives us the country for each search result. One way we can tell our narrative better could be show the distribution of coverage of a crisis by country. 

### Data

Finally, Rachel and Carolina raised the following points about the data: 

1. What is the purpose of the Data tab?
2. Is there an English speaking bias in our search result?
3. How do we define social media?
4. What exactly are we measuring?

Our conversation with Rachel and Carolina was really helpful in formulating more defined vision of the Data tab. We’ll use the tab as an opportunity to show at a deeper level the distribution of coverage over select news providers and highlight the actual articles we found. This also answers question 4, as it’ll provide more clarity into the articles we’ve discovered using our crawler and provide more depth to what the coverage of a crisis looks like. The deep dive into specific news sources can also reveal narrative patterns localized at the level of the news provider which compare across news providers and crises. 

There is an English speak bias mainly due the use of English search terms. Given the scope of the project and limitations of the Google API, we have to note it in the data collection tab of our process book.

Social media initially was supposed to be sites like Twitter. However we ran into an insurmountable challenge acquiring historical Twitter data as the data was locked away under commercial licenses that were inaccessible to us. We’ll be redefining social media to be non-traditional media like blogs.

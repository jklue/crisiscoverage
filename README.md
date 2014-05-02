# ["From Turmoil to Resolution: Media Coverage Over the Life of a Crisis"](http://crisiscoverage.info "Crisis Coverage")

## Urls
* project [website](http://crisiscoverage.info)
* final [screencast video](http://crisiscoverage.info/screencast.php)
* [process-book](http://crisiscoverage.info/process-book.php)(and related)

## Code Overview
All of our data for the visualizations comes from the productiondata folder. Inside are four folders: one for each crisis. Inside a crisis folder is the crisis summary for displaying the overview (summary.csv), storypoints for hover effects (storypoints.csv), stats for query results by source name (google-media_stats.csv) from which we also gather results by media type information. Sample query result data lives in the google-media_results_subset.csv. This along with baseline stats (google-media_results_subset.csv) populate the bar chart on the details page.

Our various custom and borrowed javascript libraries (tooltips, crisis switching, jquery ui's, etc) live in the libs folder. The includes folder is just for php page rendering. And the rest of the pages are either the php page for the website, or the js page for the data wrangling and displaying.

## Third-Party Libs

1. Datatables (https://datatables.net/): This was used for both the data page and the article explorer on the details page. It provided a dynamic table that had built in filtering, sorting, and search features.
2. Crossfitler.js (http://square.github.io/crossfilter/): This was used to index and quick search the data for both populating the stacked bar chart and the articles in the article explorer
3. Delimited.IO (http://www.delimited.io/blog/2014/3/3/creating-multi-series-charts-in-d3-lines-bars-area-and-streamgraphs) Used their guide and data structure as a reference to build the stacked bar chart on the details page 
4. d3-tip (https://github.com/Caged/d3-tip) Used extensively throughout the site to give greater detail on mouseover.
5. jQuery UI. For site ease of use.
6. Colorbrewer (colorbrewer2.org) For colors in almost all graphs.
7. Base globe code from http://bl.ocks.org/mbostock/4183330
8. Legend code from https://github.com/jgoodall/d3-colorlegend
9. Tabs code (http://code-tricks.com/create-a-simple-html5-tabs-using-jquery/) For site architecture and skeleton.
10. Scrollpane (https://github.com/vitch/jScrollPane/blob/master/script/jquery.jscrollpane.min.js)
11. Underscore.js for easier javascript array and object wrangling.




# ["From Turmoil to Resolution: Media Coverage Over the Life of a Crisis"](http://crisiscoverage.info "Crisis Coverage")

## Urls
* project [website](http://crisiscoverage.info)
* final [screencast video](http://crisiscoverage.info/screencast.php)
* [process-book](http://crisiscoverage.info/process-book.php)(and related)

## Code Overview
All of our data for the visualizations comes from the productiondata folder. Inside are four folders: one for each crisis. Inside a crisis folder is the crisis summary for displaying the overview (summary.csv), storypoints for hover effects (storypoints.csv), stats for query results by source name (google-media_stats.csv) from which we also gather results by media type information. Sample query result data lives in the google-media_results_subset.csv. This along with baseline stats (google-media_results_subset.csv) populate the bar chart on the details page.

Our various custom and borrowed javascript libraries (tooltips, crisis switching, jquery ui's, etc) live in the libs folder. The includes folder is just for php page rendering. And the rest of the pages are either the php page for the website, or the js page for the data wrangling and displaying.

## Third-Party Libs
[Here]


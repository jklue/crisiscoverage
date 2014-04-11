# ["From Turmoil to Resolution: Media Coverage Over the Life of a Crisis"](http://crisiscoverage.info "Crisis Coverage")

## Design Evolution
What are the different visualizations you considered? Justify the design decisions you made using the perceptual and design principles you learned in the course.

Initially we considered a worldmap that would show locations of Twitter data that related to a certain 'crisis.' When we realized Twitter data is unavailable past one week, we realized we would have to show less geographic data and more 2d chart data. A timeline was suggested to show crisis media according to its published date. The following is an image of our initial sketch.

![Initial Timeline Sketch](images/processbook/initialsketch.tiff "Initial Sketch")

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



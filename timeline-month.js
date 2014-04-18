/* Setup */
	var allDates, bbDetail, bbOverview, detailFrame, dateList, blogDates, color, storyPoints, duplicateAllDates, duplicateTraditonalDates, duplicateBlogDates, padding, parseYear, sources, svg, xDetailScale, xOverviewScale ;

	var margin = {
	    top: 50,
	    right: 50,
	    bottom: 0,
	    left: 100
	};

	var width = 960 - margin.left - margin.right;

	var height = 400 - margin.bottom - margin.top;

	bbOverview = {
	    x: 0,
	    y: 10,
	    w: width,
	    h: 50
	};

	bbDetail = {
	    x: 0,
	    y: 25,
	    w: width,
	    h: 300
	};

	var padding = 30;

	// read in date format from csv, convert to js object to be read by d3
  var parseDate = d3.time.format("%Y-%m-%dT%H:%M:%S+00:00").parse;
  // read in date from date_query column
  var parseDateQuery = d3.time.format("%Y-%m-%d").parse;
  // remove hour from date so can aggregate articles by date w/out regard to hour
  var parseDateSimple = d3.time.format("%b %d %Y");
  // format date for tooltip
  var parseDateTips = d3.time.format("%b %d, %Y");
  // convert storypoints date to js object for graphing on x axis
  var parseStorypoint = d3.time.format("%Y-%m-%d").parse;

  // array for all dates
  allDates = []; // master list for date, traditional media count, and blog media count

  dateList = [];
  
  duplicateTraditonalDates = [];

  duplicateBlogDates = [];
  blogDates = []; // temporarily hold blog data before add to 'dates' var

  // build svg and bounding box
	svg = d3.select("#timelineVis").append("svg").attr({
      class: 'timeline',
	    width: width + margin.left + margin.right,
	    height: height + margin.top + margin.bottom
	}).append("g").attr({
	        transform: "translate(" + margin.left + "," + margin.top + ")"
	    });

  // add clipping path for brushing
  svg.append("defs").append("clipPath")
    .attr("id", "clip")
  .append("rect")
    .attr("width", width)
    .attr("height", height);

/* Set initial data sources */
  // Traditional source
    var mediaStats = 'productiondata/haiyan-meta/google-media_stats-no-m.csv';
    // var mediaStats = 'productiondata/haiyan-meta/google-media_stats.csv';
    // var traditionalSource = 'data/haiyan/2014-04-10 12.52.22_haiyan-google-news_query_stats.csv';

  // Blog source
    var blogSource = 'data/haiyan/2014-04-10 12.53.14_haiyan-google-blog_query_stats.csv';

  // start it off!
  getData();

/* Get traditional media data */
  function getData(){

    d3.csv(mediaStats, function(data) {
// console.log('mediaStats: ',data);
 
    /* Get news source titles */
      // iterate through .csv to find source names
      var sourceDuplicates = data.map(function(d){
        // get source name without month data
        var cleanSourceName = d.query_distinct.substring(0, d.query_distinct.length-3);
        return cleanSourceName;
      });
      // define new array to hold unique source names
      sources = [];
      // reduce source list to unique names
      sourceDuplicates.forEach(function(d,i){
        // if first in array, can't use indexOf, so just add it
        if(i == 0)
          sources.push(d);
        // if array does not already contain string, add it
        else if(sources.indexOf(d) == -1)
          sources.push(d)
      });
console.log('sources: ',sources);

    /* convert dates to js objects and record for x domain */
    data.forEach(function(d){
      // change each date and reassign
      d.date_query_end = parseDateQuery(d.date_query_end);
      // add date to master date list
      dateList.push(d.date_query_end);
    });

    /* wrangle data into nice form for graph */
      // define array to hold master data for graph
        var mediaStatsData= [];
      // use all source names to get dates and add to master list
      sources.forEach(function(d,i){
        // look for source data in original list and add appropriate dates and counts
          // initialize wrapper array to hold current info
          var currentSource = [];
          // add source name
          currentSource.name = d;
          // initialize values array
          currentSource.values = [];
          // add values by looking through original data and finding relevant dates and counts
          data.forEach(function(e){
            // get clean source name from original data
            var cleanSourceName = e.query_distinct.substring(0, e.query_distinct.length-3);
            // if source data equals entry in original data, wrangle it!
            if(d == cleanSourceName){
              // return object with pertinent data
              currentSource.values.push({ date: e.date_query_end, count: +e.raw_result_count, name: d });
            }
          });
          // add name to master array
          allDates.push(currentSource);
      });

      // define color
      color = d3.scale.category20();
      // color = d3.scale.ordinal().domain(sources).range(['#CC1452', '#14A6CC', 'red', 'blue','yellow','brown','green','amber','purple','tomato','sand','chocolate','coral','cyan','darkGray','cornsilk','DarkGreen','DarkCyan','Beige','Azure','AliceBlue','AntiqueWhite']);

      // skip blog data and merge data
      return storypoints();
    });
  }

/* Get blog media data */

  // function blogData() {

  //   d3.csv(blogSource, function(data) {

  //   // convert date to js object
  //     blogDates = data.map(function(d) {
  //       // convert each date to js date
  //       var date = parseDateQuery(d.date_query_start);
  //         return { date: date, count: +d.result_count, type: "blog" };
  //     });
  //       // go get data from story point document
  //       return mergeData();
  //     });
  // }

/* Merge traditional and blog data */
  // function mergeData() {

  // // add blog data to master array
  //   // wrapper array for blog data
  //   var blogData = [];
  //   // prepare blog date data for master array
  //   blogData.values = blogDates.map(function(d){ return d; });
  //   // set type
  //   blogData.type = 'blog';
  //   // send blog data to master array
  //   allDates.push(blogData);
  // // add traditional data to master array
  //   // wrapper array for traditional data
  //   var traditionalData = [];
  //   // prepare traditional date data for master array
  //   traditionalData.values = traditionalDates.map(function(d){ return d; });
  //   // set type
  //   traditionalData.type = 'traditional';
  //   // send traditional data to master array
  //   allDates.push(traditionalData);
  //   // go get data from story point document
  //   return storypoints();
  // }

/* Get storypoints */
  function storypoints() {

    /* Add storypoints */
    d3.csv("data/haiyan/storypoints.csv", function(data) {
      // make data accessible by d3 later on
      storyPoints = data;

      // convert storypoint dates to js objects
      storyPoints.forEach(function(d){
        // change date out with object
        d.date = parseStorypoint(d.date);
      });

    // create vis
      return detailVis();
    });

  }

/* Make detail */
	function detailVis() {

	// Reset
		var xDetailAxis, yDetailAxis, yDetailScale;

	// normal scale
	  xDetailScale = d3.time.scale().domain(d3.extent(dateList, function(d) { return d; })).range([0, bbDetail.w]);  // define the right domain
	// example that translates to the bottom left of our vis space:
	  var detailFrame = svg.append("g").attr({
	      class: 'detailFrame',
        transform: "translate(" + bbDetail.x + "," + bbDetail.y +")",
	  });
	// use that minimum and maximum possible y values for domain in log scale
    yDetailScale = d3.scale.linear().domain([0, d3.max(allDates, function(d) { return d3.max(d.values, function(v) { return v.count; }) })]).range([bbDetail.h, 0]);
  // x axis
    xDetailAxis = d3.svg.axis()
                  .scale(xDetailScale)
                  .orient('bottom')
                  .ticks(4)
                  .tickFormat(d3.time.format('%b'));
                  // .tickFormat(d3.time.format('%b %d'));

  // y axis for consolidated population line
    yDetailAxis = d3.svg.axis()
                  .scale(yDetailScale)
                  .orient('left')
                  .ticks(6);

    var area = d3.svg.area()
								     .x(function(d) { return xDetailScale(d.date); })
								     .y0(bbDetail.h)
								     .y1(function(d) { return yDetailScale(d.count); });

    var areaLine = d3.svg.line()
                     .x(function(d) { return xDetailScale(d.date); })
                     .y(function(d) { return yDetailScale(d.count); })
                     .interpolate('linear');

    // could not figure out how to set static y0 and y1 values using d3 line generator
    // var storypointLine = d3.svg.line();
  // add x axis to svg
    detailFrame.append('g')
            .attr({
              class: 'x axis',
              transform: 'translate(0,' + bbDetail.h +')'
            })
            .call(xDetailAxis)

  // add y axis to svg
    detailFrame.append('g')
            .attr({
              class: 'y axis',
            })
            .call(yDetailAxis)
          .append("text")
            .attr('x', -5)
            .attr("y", -45)
            .attr("dy", "30px")
            .style("text-anchor", "end")
            .text("Coverage")

  // bind data
    var dataTypes = svg.selectAll('.dataTypes')
                        .data(allDates)
                      .enter().append('g')
                        .attr('class','dataTypes');

  // add line
    dataTypes.append('path')
               .attr({
                  class: 'traditionalMediaPath',
                  d: function(e){ return areaLine(e.values); },
                  transform: 'translate(0,' + bbDetail.y + ')'                  
               })
               .style({
                'stroke': function(e) { return color(e.name); },
                'fill': 'none',
               });

  // add fill
    // dataTypes.append("path")
    //           .attr({
    //             class: "traditionalMediaArea",
    //             d: function(e) { return area(e.values); },
    //             transform: 'translate(0,' + bbDetail.y + ')'                  
    //           })
    //           .style({
    //             'fill': function(e) { return color(e.type); }
    //            });

  /* add dots for each line */
    dataTypes.selectAll('circle').data(function(d) { return d.values;})
      .enter().append('circle')
      .attr({
        class: 'dot',
        cx: function(d) { return xDetailScale(d.date); },
        cy: function(d) { return yDetailScale(d.count); },
        r: 4,
        transform: 'translate(0,' + bbDetail.y + ')'
      })
      .style({
        fill: function(d) { 
          // get color of type (traditional or blog color)
          var typeColor = color(d.name);
          // darken color
          var d3color = d3.rgb(typeColor).darker();
          // return color
          return d3color; 
        }
      })

      // make dot bigger and show tip
      .on('mouseover', function(d){
        d3.select(this)
          .transition()
          .duration(25)
          .attr('r',10);
        tip.show(d);
      })
      // make dot smaller and hide tip
      .on('mouseleave', function(d){
        d3.select(this)
          .transition()
          .duration(25)
          .attr('r',4)
        tip.hide(d);
      });

   /* Storypoints */
    // add intro title and summary
    d3.select("#crisisTitle").html('<h3>Typhoon Haiyan</h3>');
    d3.select('#crisisStory').html('Typhoon Haiyan, known as Typhoon Yolanda in the Philippines, was a powerful tropical cyclone that devastated portions of Southeast Asia, particularly the Philippines, on November 8, 2013. <a href="http://en.wikipedia.org/wiki/Typhoon_Haiyan" class="storySource">&mdash; Wikipedia</a>');

    // add dotted lines
    detailFrame.selectAll('.line')
               .data(storyPoints)
            .enter().append('line')
               .attr({
                  class: 'storyline',
                  x1: function(d) { return xDetailScale(d.date); },
                  x2: function(d) { return xDetailScale(d.date); },
                  y1: -bbDetail.y, // make taller than chart
                  y2: bbDetail.h
                });

    // add triangles
    detailFrame.selectAll('.storyTriangle')
           .data(storyPoints)
        .enter().append('path')
           .attr({
              class: 'storyTriangle',
              transform: function(d){ return 'translate(' + xDetailScale(d.date) + ',' + -bbDetail.y + ')'; },
              d: d3.svg.symbol().type('triangle-down').size(256)
            })
           // add mouseover story details
           .on('mouseover',function(d){
              // convert story date to readable
              var storyDate = parseDateTips(d.date);
              /* Dates */
                // remove any previously shown dates
                d3.select('.crisisDate')
                  .remove();
                // show new date
                d3.select('#crisisTitle')
                  .append('span')
                  .html(storyDate)
                  .attr('class','crisisDate');    

              // add new content to div
              d3.select('#crisisStory').html(d.title);
              /* color and size handling */

                /* Lines */


                /* Triangles */
                // revert to original color on other triangles and shrink
                d3.selectAll('.storyTriangle')
                  .transition()
                  .style('fill','#919191')
                  .attr('d',d3.svg.symbol().type('triangle-down').size(256));
                // change color of current triangle and enlarge
                d3.select(this)
                  .transition()
                  .style('fill','#3D8699')
                  .attr('d',d3.svg.symbol().type('triangle-down').size(512));

           });

 //  /* Tool tips */
    // Initialize tooltip
    var tip = d3.tip()
            .html(function(d) { 
              console.log('d3tip',d);
              // if 1 article, use singular 'article'
                return d.count + " articles on <span class='sourceName'>" + d.name + "</span> during<br>the 30 days prior to " + parseDateTips(d.date); 
            })
            .direction('e')
            .attr('class','d3-tip e');

    // Invoke the tip in the context of your visualization
    detailFrame.call(tip)
  
/* Make legend */
  var legend = svg.selectAll(".legend")
      .data(sources)
    .enter().append("g")
      .attr("class", "legend")
      .attr("transform", function(d, i) { return "translate(0," + i * 28 + ")"; })
      .on('mouseout', function (d) {
        // Restore opacity to all rectangles on mouseout
        d3.selectAll('rect')
          .attr('opacity','1');
      });

  // make color box
  legend.append("rect")
      .attr("x", width - 18)
      .attr("width", 18)
      .attr("height", 18)
      .style("fill", color);

  // make name box
  legend.append("text")
      .attr("x", width - 24)
      .attr("y", 9)
      .attr("dy", ".35em")
      .style("text-anchor", "end")
      .text(function(d) { return d; });
  }

/* button click control. From AmeliaBR on "putting the country on drop down list using d3 via csv file" on StackOverflow */
  // add event handler  
  d3.select("#dataSourceSelect").on("change", sourceChanged);

  // define change function
  function sourceChanged() {

    // get selected value
    var selectedSource = d3.event.target.value;

    // hide one viz or another
    if(selectedSource == 'bbc')
      doBBC();
    else if(selectedSource == 'google')
      doGoogle();
  }

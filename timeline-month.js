/* Setup */
	var allDates, aggregateMediaStats, bbDetail, bbOverview, detailFrame, dateList, blogDates, color, storyPoints, duplicateAllDates, duplicateTraditonalDates, duplicateBlogDates, line, mediaTypes, originalData, padding, parseYear, sources, svg, xAxis, xScale, xOverviewScale, yAxis, yScale;

	var margin = {
	    top: 50,
	    right:200,
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
  aggregateMediaStats = []; // master list for media by type
  mediaTypes = ['Traditional','Independent','Blogs-Social'];

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
    var mediaStats = 'productiondata/haiyan/google-media_stats.csv';
    // var mediaStats = 'productiondata/haiyan-meta/google-media_stats.csv';

  // Blog source
    var blogSource = 'data/haiyan/2014-04-10 12.53.14_haiyan-google-blog_query_stats.csv';

  // start it off!
  getData();

/* Get traditional media data */
  function getData(){

    d3.csv(mediaStats, function(data) {
// console.log('mediaStats: ',data);
      // make data useable in aggregate chart
      originalData = data;

    /* Get news source titles */
      // iterate through .csv to find source names
      var sourceDuplicates = data.map(function(d){
        // get source name without month data
        // var cleanSourceName = d.query_distinct.substring(0, d.query_distinct.length-3);
        return d.site_name;
        // return cleanSourceName;
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
// console.log('data: ',data);
// console.log('sources: ',sources);

    /* add type back in to unique list of sources */
    sources.forEach(function(d){
      // console.log(d);
      // match name to original data to get source type info
      data.forEach(function(e){
        // if source name equals original data name, grab type
        // if(d == e.site_name)
          // console.log('match');
      });
    });
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
            // var cleanSourceName = e.query_distinct.substring(0, e.query_distinct.length-3);
            // if source data equals entry in original data, wrangle it!
            if(d == e.site_name){
            // if(d == cleanSourceName){
              // return object with pertinent data
              currentSource.values.push({ date: e.date_query_end, count: +e.raw_result_count, name: d, type: e.site_type });
            }
          });
          // add name to master array
          allDates.push(currentSource);
      });

      // define color
      color = d3.scale.category20();
      // color = d3.scale.ordinal().domain(sources).range(['#CC1452', '#14A6CC', 'red', 'blue','yellow','brown','green','amber','purple','tomato','sand','chocolate','coral','cyan','darkGray','cornsilk','DarkGreen','DarkCyan','Beige','Azure','AliceBlue','AntiqueWhite']);

      // get aggregate data by media type
      return aggregateData();
    });
  }


/* Aggregate data by media type */
  function aggregateData() {

    // get reported dates for indeces for aggregate chart
      // define var to hold dates
      var reportedDates = [];
      // loop through data to collect all dates
      allDates[0].values.forEach(function(d){
        reportedDates.push(d.date);
      });
    // iterate through news types, if find match, tally
    mediaTypes.forEach(function(d){
      // initialize wrapper array to hold current media type
      var currentMediaType = [];
      // add media type
      currentMediaType.name = d;
      // initialize values array
      currentMediaType.values = [];
      // iterate through all sample dates
      reportedDates.forEach(function(e){
        // start tally
        var count = 0;
        // iterate through all media queries
        originalData.forEach(function(f){
          // if types and dates match, add to list
          if(d == f.site_type && e.getTime() == f.date_query_end.getTime()){
            // increment count
            count = count + +f.raw_result_count;
          }
        });
        // record count with date
        currentMediaType.values.push({ date: e, count: count, name: d });
      });
      // add data to master list
      aggregateMediaStats.push(currentMediaType);
    });
// console.log('aggregateMediaStats: ',aggregateMediaStats);
// console.log('allDates: ',allDates);
    // ruin all progress
    // allDates = aggregateMediaStats;
    // get aggregate data by media type
      return storypoints();
  }

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

	// normal scale
	  xScale = d3.time.scale().domain(d3.extent(dateList, function(d) { return d; })).range([0, bbDetail.w]);  // define the right domain
	// example that translates to the bottom left of our vis space:
	  var detailFrame = svg.append("g").attr({
	      class: 'detailFrame',
        transform: "translate(" + bbDetail.x + "," + bbDetail.y +")",
	  });
	// use that minimum and maximum possible y values for domain in log scale
    yScale = d3.scale.linear().domain([0, d3.max(allDates, function(d) { return d3.max(d.values, function(v) { return v.count; }) })]).range([bbDetail.h, 0]);
    // yScale = d3.scale.pow().exponent(0.3).domain([0, d3.max(allDates, function(d) { return d3.max(d.values, function(v) { return v.count; }) })]).range([bbDetail.h, 0]);
  // x axis
    xAxis = d3.svg.axis()
                  .scale(xScale)
                  .orient('bottom')
                  .ticks(4)
                  .tickFormat(d3.time.format('%b'));
                  // .tickFormat(d3.time.format('%b %d'));

  // y axis for consolidated population line
    yAxis = d3.svg.axis()
                  .scale(yScale)
                  .orient('left')
                  .ticks(6);

    // var area = d3.svg.area()
				// 				     .x(function(d) { return xScale(d.date); })
				// 				     .y0(bbDetail.h)
				// 				     .y1(function(d) { return yScale(d.count); });

    var line = d3.svg.line()
                     .x(function(d) { return xScale(d.date); })
                     .y(function(d) { return yScale(d.count); })
                     .interpolate('linear');

    // could not figure out how to set static y0 and y1 values using d3 line generator
    // var storypointLine = d3.svg.line();
  // add x axis to svg
    detailFrame.append('g')
            .attr({
              class: 'x axis',
              transform: 'translate(0,' + bbDetail.h +')'
            })
            .call(xAxis)

  // add y axis to svg
    detailFrame.append('g')
            .attr({
              class: 'y axis',
            })
            .call(yAxis)
          .append("text")
            .attr('x', -5)
            .attr("y", -45)
            .attr("dy", "30px")
            .style("text-anchor", "end")
            .text("Coverage")

  // bind data
    var mediaSources = svg.selectAll('.mediaSources')
                        .data(allDates)
                      .enter().append('g')
                        .attr('class','mediaSources');

  // add line
    mediaSources.append('path')
               .attr({
                  id: function(d){ console.log(d); return d.name; },
                  d: function(d){ return line(d.values); },
                  transform: 'translate(0,' + bbDetail.y + ')'                  
               })
               .style({
                'stroke': function(e) { return color(e.name); },
                'fill': 'none',
               });

  // add fill
    // mediaSources.append("path")
    //           .attr({
    //             class: "traditionalMediaArea",
    //             d: function(e) { return area(e.values); },
    //             transform: 'translate(0,' + bbDetail.y + ')'                  
    //           })
    //           .style({
    //             'fill': function(e) { return color(e.name); }
    //            });

  /* add dots for each line */
    mediaSources.selectAll('circle').data(function(d) { return d.values;})
      .enter().append('circle')
      .attr({
        class: 'dot',
        cx: function(d) { return xScale(d.date); },
        cy: function(d) { return yScale(d.count); },
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

  /* Add related legend - Functionality based on http://mpf.vis.ywng.cloudbees.net/*/
    mediaSources.append('text').attr({
                  id: function(d){ return d.name; }, // store name for reference to path
                  x: width + 180,
                  y: function(d,i){ return (i * 16) - margin.top/2; },
                  dy: '0.35em',
                  cursor: 'pointer',
                  fill: function(d){ return color(d.name); }
                })
                .style("text-anchor", "end")
                .text(function(d) { return d.name; })
                // change font color to show activation or not
                .on('click',function(d){
                  // store id hook for path hide
                  var pathID = d3.select(this).attr('id');

                  // if active, make gray
                  if(d3.select(this).attr('fill') != '#ccc'){
                    console.log('id: ',pathID);
                    // change text color to gray
                    d3.select(this)
                      .attr('fill','#ccc');
                    // remove line
                    mediaSources.select("path[id='" + pathID + "'").transition()
                             .attr('d',function(e){ return null; })
                  } else{
                    // change color back to color
                    d3.select(this)
                      .attr('fill', function(d){ return color(d.name); });
                    // add line
                    mediaSources.select('path').transition()
                             .attr('d',function(e){ return line(e.values); })
                  }
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
                  x1: function(d) { return xScale(d.date); },
                  x2: function(d) { return xScale(d.date); },
                  y1: -bbDetail.y, // make taller than chart
                  y2: bbDetail.h
                });

    // add triangles
    detailFrame.selectAll('.storyTriangle')
           .data(storyPoints)
        .enter().append('path')
           .attr({
              class: 'storyTriangle',
              transform: function(d){ return 'translate(' + xScale(d.date) + ',' + -bbDetail.y + ')'; },
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
              // console.log('d3tip',d);
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
      .attr({
        'transform': function(d, i) { return 'translate(180,' + ((i * 16) - margin.top/2) + ')'},
        'fill':color
      })
      .on('mouseout', function (d) {
        // Restore opacity to all rectangles on mouseout
        d3.selectAll('rect')
          .attr({
            'opacity':1,
          });
      });

  // make color box
  // legend.append("rect")
  //     .attr("x", width - 16)
  //     .attr("width", 16)
  //     .attr("height", 16)
  //     .style("fill", color);
  
  // add checkbox
  // legend.append('foreignObject')
  //       .attr('class','checkbox')
  //       .attr('width',20)
  //       .attr('height',20)
  //       .attr("x", width - 16)
  //       .attr("dy", ".35em")
  //     .append('xhtml:input')
  //       .attr('type','checkbox')
  //       .attr('value',function(d){ return d })
  //       .attr('checked',true);

  // make name box
  // legend.append("text")
  //     .attr("x", width - 24)
  //     .attr("y", 9)
  //     .attr("dy", ".35em")
  //     .attr('cursor','pointer')
  //     .style("text-anchor", "end")
  //     .text(function(d) { return d })
  //     // change font color to show activation or not
  //     .on('click',function(d){
  //       // if active, make gray
  //       if(d3.select(this.parentNode).attr('fill') != '#ccc'){
  //         d3.select(this.parentNode)
  //           .attr('fill','#ccc');
  //       } else{
  //         d3.select(this.parentNode)
  //           .attr('fill',color);
  //       }
  //     });
  

  
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

/* button click control */
  d3.select("input[value=\"types\"]").on("click", function () { redraw('types'); });
  d3.select("input[value=\"sources\"]").on("click", function () { redraw('sources'); });

/* Redraw graph based on either types or sources of media */
  function redraw(chart) {

    // determine chart type
    if(chart == 'types')
      var chartData = aggregateMediaStats;
    else
      var chartData = allDates;

      // recalculate range
      yScale = d3.scale.linear().domain([0, d3.max(chartData, function(d) {return d3.max(d.values, function(v) { return v.count; }) })]);

      /* remove & add lines while animating transitions*/
      // remove old data
      var mediaSources = svg.selectAll('.mediaSources')
                         .data(chartData)
                       .exit().transition().remove();

      // add any new data
      mediaSources = svg.selectAll('.mediaSources')
                     .data(chartData)
                      .enter().append('g')
                        .attr('class','mediaSources');

      // add line
      mediaSources.selectAll('path')
               .attr({
                  d: function(e){ console.log(e); return line(e.values); },
                  transform: 'translate(0,' + bbDetail.y + ')'                  
               });
  
      // redraw y axis
      d3.select(".y.axis")
        .call(yAxis);

      console.log('no way');
  }

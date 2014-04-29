/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// SETUP
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // for jslint
  /*jslint devel: true*/
  /*global d3:false */

	var allDates, aggregateMediaStats, crisis, dateList, color, mediaSources, storyPoints, line, mediaTypes, sources, summary, visibleDates, xAxis, xScale, yAxis, yScale, xScale2, yAxis2, yScale2;

  // read in date from date_query column
  var parseDateQuery = d3.time.format("%Y-%m-%d").parse;
  // format date for tooltip
  var parseDateTips = d3.time.format("%b %d, %Y");
  // convert storypoints date to js object for graphing on x axis
  var parseStorypoint = d3.time.format("%Y-%m-%d").parse;
  // format date for storypoints
  var parseDateStory = d3.time.format("%b %Y");

  mediaTypes = ['Traditional','Independent','Blogs-Social'];

  // By Media Type svg setup
    var typeMargin = {
        top: 50,
        right:140,
        bottom: 0,
        left: 70
    };

    var typeWidth = 990 - typeMargin.left - typeMargin.right;

    var typeHeight = 450 - typeMargin.bottom - typeMargin.top;

    var typeDetail = {
        x: 0,
        y: 25,
        w: typeWidth,
        h: 350
    };

    var typePadding = 30;

  // By Source svg setup
    var sourceMargin = {
      top: 50,
      right:200,
      bottom: 0,
      left: 70
    };

    var sourceWidth = 990 - sourceMargin.left - sourceMargin.right;

    var sourceHeight = 450 - sourceMargin.bottom - sourceMargin.top;

    var sourceDetail = {
      x: 0,
      y: 25,
      w: sourceWidth,
      h: 350
    };

    var sourcePadding = 30;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ONE TIME
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // Begin By Media Type build

    // build svg and bounding box
    var typeSVG = d3.select("#timelineTypeVis")
      .append("svg")
      .attr({
        class: 'timeline',
        width: typeWidth + typeMargin.left + typeMargin.right,
        height: typeHeight + typeMargin.top + typeMargin.bottom
      }).append("g")
        .attr({
            transform: "translate(" + typeMargin.left + "," + typeMargin.top + ")",
        });

    // build mask
    typeSVG.append('clipPath')
        .attr('id', 'type-chart-area')
        .append('rect')
        .attr({
          x: -typePadding,
          y: -typePadding - 5,
          width: typeDetail.w + typePadding * 12,
          height: typeDetail.h + typePadding * 1.97
        });

  // Begin By Source build

    // build svg and bounding box
      var sourceSVG = d3.select("#timelineSourceVis")
        .append("svg")
        .attr({
          class: 'timeline',
          width: sourceWidth + sourceMargin.left + sourceMargin.right,
          height: sourceHeight + sourceMargin.top + sourceMargin.bottom
        }).append("g")
          .attr({
              transform: "translate(" + sourceMargin.left + "," + sourceMargin.top + ")",
          });

      // build mask
      sourceSVG.append('clipPath')
          .attr('id', 'chart-area')
          .append('rect')
          .attr({
            x: -sourcePadding,
            y: -sourcePadding - 5,
            width: sourceDetail.w + sourcePadding * 12,
            height: sourceDetail.h + sourcePadding * 1.97
          });

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// GET DATA
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  function getData(error, crisisSummary, storypoints, data){

    /* Clear out old data that may remain if previous crisis */
    allDates = []; // master list for date, traditional media count, and blog media count
    aggregateMediaStats = []; // master list for media by type
    dateList = [];
    d3.selectAll('.mediaSources').remove(); // clear both charts
    d3.selectAll('.x.axis').remove();
    d3.selectAll('.y.axis').remove();
    d3.selectAll('.x.axis2').remove();
    d3.selectAll('.y.axis2').remove();
    d3.selectAll('.storyline').remove();
    d3.selectAll('.storyTriangle').remove();

    // make summary global for use in tab changes
    summary = crisisSummary;

    // make storypoints global for use in later function
    storyPoints = storypoints;

    // convert storypoint dates to js objects
    storyPoints.forEach(function(d){
      // change date out with object
      d.date = parseStorypoint(d.date);
    });

    /* Get news source titles */
      // iterate through .csv to find source names
      var sourceDuplicates = data.map(function(d){
        // get source name without month data
        return d.site_name;
      });
      // define new array to hold unique source names
      sources = [];
      // reduce source list to unique names
      sourceDuplicates.forEach(function(d,i){
        // if first in array, can't use indexOf, so just add it
        if(i === 0) {
          sources.push(d);
        }
        else if(sources.indexOf(d) == -1 ) {
          sources.push(d);
        }
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
          // set unique id
            // remove spaces and periods
            var sourceHandle = d.replace(/[\. ]+/g, "");
            // set it
            currentSource.id = sourceHandle;
          // set if visible or not
          currentSource.vis = 1;
          // initialize values array
          currentSource.values = [];
          // add values by looking through original data and finding relevant dates and counts
          data.forEach(function(e){
            // get clean source name from original data
            // if source data equals entry in original data, wrangle it!
            if(d == e.site_name){
              // return object with pertinent data
              currentSource.values.push({ date: e.date_query_end, count: +e.raw_result_count, name: d, type: e.site_type, id: sourceHandle, vis: 1 });
            }
          });
          // add name to master array
          allDates.push(currentSource);
      });
      // define color
      color = d3.scale.category20();

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
        data.forEach(function(f){
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

    // draw charts
    return typeVis();
  }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// BY MEDIA TYPE
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  function typeVis() {
    //define offset so initial storypoint will be included in graph
    var offset = 75;
    xScale = d3.time.scale().domain(d3.extent(dateList, function(d) { return d; })).range([offset, typeDetail.w]);  // define the right domain
    yScale = d3.scale.linear().domain([0, d3.max(aggregateMediaStats, function(d) { return d3.max(d.values, function(v) { return v.count; }) })]).range([typeDetail.h, 0]);

 // example that translates to the bottom left of our vis space:
    var typeFrame = typeSVG.append("g").attr({
        class: 'typeFrame',
        transform: "translate(" + typeDetail.x + "," + typeDetail.y +")",
    });
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

    var line = d3.svg.line()
                     .x(function(d) { return xScale(d.date); })
                     .y(function(d) { return yScale(d.count); })
                     .interpolate('linear');

    // could not figure out how to set static y0 and y1 values using d3 line generator
  // add x axis to svg
    typeFrame.append('g')
            .attr({
              class: 'x axis',
              transform: 'translate(0,' + typeDetail.h  +')'
            })
            .call(xAxis)

  // add x axis extender to allow storypoints to be in graph
    d3.select('.x.axis')
      .append('line')
      .attr({
        x1: 100,
        x2: 0,
        y1: 3,
        y2: 3 
      })
      .style({
        stroke: '#ccc',
        'stroke-width': 6
      })

  // add y axis to svg
    typeFrame.append('g')
            .attr('class', 'y axis')
            .call(yAxis)
          .append("text")
            .attr('x', -5)
            .attr("y", -45)
            .attr("dy", "30px")
            .style("text-anchor", "end")
            .text("# articles")

    // set f var to reference data for this chart
    var typeDates = aggregateMediaStats;

    // make wrapper for lines and legend
    var chartArea = typeSVG.append('g')
                        .attr('clip-path', 'url(#chart-area)');

      // bind data
      var mediaSources = chartArea.selectAll('.mediaSources')
                          .data(typeDates)
                        .enter().append('g')
                          .attr('class','mediaSources');
   
      // add line
        mediaSources.append('path')
                   .attr({
                      class: function(d){ return d.id + ' path'; }, // store name for reference to path
                      id: function(d){ return d.name; },
                      d: function(d){ return line(d.values); },
                      transform: 'translate(0,' + typeDetail.y + ')',
                   })
                   .style({
                    'stroke': function(e) { return color(e.name); },
                    'fill': 'none',
                   });

   /* add dots for each line */
        mediaSources.selectAll('circle').data(function(d) { return d.values;})
          .enter().append('circle')
          .attr({
            class: function(d){ return d.id + " dot"; }, // store name for reference to path
            cx: function(d) { return xScale(d.date)},
            cy: function(d) { return yScale(d.count); },
            r: 4,
            transform: 'translate(0,' + typeDetail.y + ')'
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

  /* Add related legend - Functionality inspired by http://mpf.vis.ywng.cloudbees.net/*/

    mediaSources.append('text').attr({
                  class: function(d){ return d.id + 'legend'; }, // store name for reference to path
                  x: typeWidth + 120,
                  y: function(d,i){ return (i * 16) + typeMargin.top; },
                  dy: '0.35em',
                  // cursor: 'pointer',
                  fill: function(d){ return color(d.name); }
                })
                .style("text-anchor", "end")
                .text(function(d) { return d.name; });

  /* Storypoints */
    // add dotted lines
    typeFrame.selectAll('.line')
               .data(storyPoints)
            .enter().append('line')
               .attr({
                  class: 'storyline',
                  x1: function(d) { return xScale(d.date); },
                  x2: function(d) { return xScale(d.date); },
                  y1: -typeDetail.y, // make taller than chart
                  y2: typeDetail.h
                });

  // add triangles
    typeFrame.selectAll('.storyTriangle')
           .data(storyPoints)
        .enter().append('path')
           .attr({
              class: 'storyTriangle',
              transform: function(d){ return 'translate(' + xScale(d.date) + ',' + -typeDetail.y + ')'; },
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

  /* Tool tips */
    // Initialize tooltip
    var tip = d3.tip()
            .html(function(d) { 
              // if 1 article, use singular 'article'
                return d.count + " <span class='sourceName'>" + d.name + "</span> articles on during<br>the 30 days prior to " + parseDateTips(d.date); 
            })
            .direction('e')
            .attr('class','d3-tip e');

    // Invoke the tip in the context of your visualization
    typeFrame.call(tip);

    // Draw second vis
    sourceVis();
  }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// BY SOURCE
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	function sourceVis() {
    //define offset so initial storypoint will be included in graph
    var offset = 75;
    xScale2 = d3.time.scale().domain(d3.extent(dateList, function(d) { return d; })).range([offset, sourceDetail.w]);  // define the right domain
    yScale2 = d3.scale.linear().domain([0, d3.max(allDates, function(d) { return d3.max(d.values, function(v) { return v.count; }) })]).range([sourceDetail.h, 0]);

 	// example that translates to the bottom left of our vis space:
	  var sourceFrame = sourceSVG.append("g").attr({
	      class: 'sourceFrame',
        transform: "translate(" + sourceDetail.x + "," + sourceDetail.y +")",
	  });

 // x axis
    var xAxis2 = d3.svg.axis()
                  .scale(xScale2)
                  .orient('bottom')
                  .ticks(4)
                  .tickFormat(d3.time.format('%b'));

  // y axis for consolidated population line
    yAxis2 = d3.svg.axis()
                  .scale(yScale2)
                  .orient('left')
                  .ticks(6);

    line = d3.svg.line()
                     .x(function(d) { return xScale2(d.date); })
                     .y(function(d) { return yScale2(d.count); })
                     .interpolate('linear');

  // add x axis to sourceSVG
    sourceFrame.append('g')
            .attr({
              class: 'x axis2',
              transform: 'translate(0,' + sourceDetail.h  +')'
            })
            .call(xAxis2)

  // add x axis extender to allow storypoints to be in graph
    d3.select('.x.axis2')
      .append('line')
      .attr({
        x1: 100,
        x2: 0,
        y1: 3,
        y2: 3
      })
      .style({
        stroke: '#ccc',
        'stroke-width': 6
      })

  // add y axis to sourceSVG
    sourceFrame.append('g')
            .attr('class', 'y axis2')
            .call(yAxis2)
          .append("text")
            .attr('x', -5)
            .attr("y", -45)
            .attr("dy", "30px")
            .style("text-anchor", "end")
            .text("# articles")

  // use that minimum and maximum possible y values for domain in log scale

    // deep copy array for dates currently shown in graph, from which we can remove data and check to draw new y scale
    visibleDates = allDates.map(function(d){
      return {
        id: d.id,
        name: d.name,
        values: d.values.map(function(f){
          return {
            count: f.count,
            date: f.date,
            id: f.id,
            name: f.name,
            type: f.type,
            vis: f.vis
          }
        }),
        vis: d.vis
      }
    });

    // make wrapper for lines and legend
    var chartArea = sourceSVG.append('g')
                        .attr('clip-path', 'url(#chart-area)');

      // bind data
      mediaSources = chartArea.selectAll('.mediaSources')
                          .data(visibleDates)
                        .enter().append('g')
                          .attr('class','mediaSources');
   
      // add line
        mediaSources.append('path')
                   .attr({
                      class: function(d){ return d.id + ' path'; }, // store name for reference to path
                      // id: function(d){ return d.name; },
                      d: function(d){ return line(d.values); },
                      transform: 'translate(0,' + sourceDetail.y + ')',
                   })
                   .style({
                    'stroke': function(e) { return color(e.name); },
                    'fill': 'none',
                   });

   /* add dots for each line */
        mediaSources.selectAll('circle').data(function(d) { return d.values;})
          .enter().append('circle')
          .attr({
            class: function(d){ return d.id + " dot"; }, // store name for reference to path
            cx: function(d) { return xScale2(d.date); },
            cy: function(d) { return yScale2(d.count); },
            r: 4,
            transform: 'translate(0,' + sourceDetail.y + ')'
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

  /* Add related legend - Functionality inspired by http://mpf.vis.ywng.cloudbees.net/*/

    mediaSources.append('text').attr({
                  class: function(d){ return d.id + 'legend'; }, // store name for reference to path
                  // class: 'label',
                  x: sourceWidth + 160,
                  y: function(d,i){ return (i * 16) + 8; },
                  dy: '0.35em',
                  cursor: 'pointer',
                  fill: function(d){ return color(d.name); }
                })
                .style("text-anchor", "end")
                .text(function(d) { return d.name; })
                // change font color to show activation or not
                .on('click',function(d){
                  // if active, make gray
                  if(d3.select(this).attr('fill') != '#ccc'){
                    // change text color to gray
                    d3.select(this)
                      .attr('fill','#ccc');

                    // remove line from data
                      // look through data array and set selected media source's data to null
                      visibleDates.forEach(function(e,j){
                        // if data matches
                        if(e.id == d.id){
                          // make invisible
                          e.vis = 0;
                          // remove data
                          e.values.forEach(function(f){
                            f.count = null;
                            f.vis = 0;
                          });
                        }
                      });
                    
                  } else{
                    // change legend color back to original color
                    d3.select(this)
                      .attr('fill', function(d){ return color(d.name); });

                    // add data back in
                      // look through data array and set selected media source's data to original data
                      visibleDates.forEach(function(e,j){
                        // if data matches
                        if(e.id == d.id){
                          // go through original data and add it back in
                          allDates.forEach(function(f){
                            // look for match
                            if(f.id == e.id){
                              // make visible
                              e.vis = 1;
                              // add data back in
                              e.values = f.values.map(function(f){
                                return {
                                  count: f.count,
                                  date: f.date,
                                  id: f.id,
                                  name: f.name,
                                  type: f.type
                                }
                              })
                            }
                          });
                        }
                      });

                  }
                  // redo y scale
                  yScale2.domain([0, d3.max(visibleDates, function(e) { return d3.max(e.values, function(v) { return v.count; }) })]);

                  // redraw y axis
                  d3.select(".y.axis2").transition().duration(1500).ease('sin-in-out')
                    .call(yAxis2);

                  // redraw other lines
                  mediaSources.selectAll('path').transition().duration(500)
                    // only draw lines that are 'visible' Will cause an error, but best solution so far
                    .attr('d',function(e){ return line(e.values); })
                    // .attr('d',function(e){ if(e.vis==1) { return line(e.values); } else { return null; } });

                  // redraw dots
                  mediaSources.selectAll('circle').data(function(d) { return d.values;})
                    .transition().duration(500)
                    .attr({
                      // cx: function(e) { if(d.vis==1) { return xScale(e.date); } else { return xScale(null); } },
                      cx: function(e) { return xScale2(e.date); },
                      // cx: function(e) { if(d.vis==1) { return yScale(e.count); } else { return yScale(null); } },
                      cy: function(e) { return yScale2(e.count); },
                    })
                    .style({
                      fill: function(e) { 
                        // if not shown
                        if(e.vis == 0)
                          return 'white';
                        else {
                          // get color of type (traditional or blog color)
                          var typeColor = color(e.name);
                          // darken color
                          var d3color = d3.rgb(typeColor).darker();
                          // return color
                          return d3color; 
                        }

                      }
                   })
                });

    // add checkbox
      mediaSources.append('foreignObject')
            .attr({
              width: 20,
              height: 20,
              x: sourceWidth + 170,
              y: function(d,i){ return (i * 16); },
              // dy: '.35em'
            })
          .append('xhtml:input')
            .attr({
              id: function(d){ return d.id; },
              type: 'checkbox',
              value: function(d){ return d },
              checked: true
            })
            .on('click',function(d){
              var clicked = d3.select(this);
              labelClick(clicked);
            });

  /* Storypoints */

    // add dotted lines
    sourceFrame.selectAll('.line')
               .data(storyPoints)
            .enter().append('line')
               .attr({
                  class: 'storyline',
                  x1: function(d) { return xScale2(d.date); },
                  x2: function(d) { return xScale2(d.date); },
                  y1: -sourceDetail.y, // make taller than chart
                  y2: sourceDetail.h
                });

  // add triangles
    sourceFrame.selectAll('.storyTriangle')
           .data(storyPoints)
        .enter().append('path')
           .attr({
              class: 'storyTriangle',
              transform: function(d){ return 'translate(' + xScale2(d.date) + ',' + -sourceDetail.y + ')'; },
              d: d3.svg.symbol().type('triangle-down').size(256)
            })
           // add mouseover story details
           .on('mouseover',function(d){
              // convert story date to readable
              var storyDate = parseDateStory(d.date);
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

  /* Tool tips */
    // Initialize tooltip
    var tip = d3.tip()
            .html(function(d) { 
              // if 1 article, use singular 'article'
                return d.count + " articles on <span class='sourceName'>" + d.name + "</span> during<br>the 30 days prior to " + parseDateTips(d.date); 
            })
            .direction('e')
            .attr('class','d3-tip e');

    // Invoke the tip in the context of your visualization
    sourceFrame.call(tip)


  /* Uncheck Google initially */
    // get google checkbox
    var google = d3.select('#Google');
    // uncheck google checkbox
    google[0][0].checked = false;

    // send it to function that removes line from chart
    labelClick(google);

   //reset Summary
   resetSummary(summary);
  }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Checkboxes
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  function labelClick(clicked) {
    // console.log(clicked.data()[0].id);
    // get value of checkbox
    var isChecked = clicked[0][0].checked;
    // get current data
    var currentData = clicked.data()[0];
    // remove line
    if(isChecked == false){
      // look through data array and set selected media source's data to null
      visibleDates.forEach(function(e,j){
        // if data matches
        if(e.id == currentData.id){
          // make invisible
          e.vis = 0;
          // remove data
          e.values.forEach(function(f){
            f.count = null;
            // f.vis = 0;
          });
        }
      });
      
    } else{
      // add data back in
        // look through data array and set selected media source's data to original data
        visibleDates.forEach(function(e,j){
          // if data matches
          if(e.id == currentData.id){
            // go through original data and add it back in
            allDates.forEach(function(f){
              // look for match
              if(f.id == e.id){
                // make visible
                e.vis = 1;
                // add data back in
                e.values = f.values.map(function(f){
                  return {
                    count: f.count,
                    date: f.date,
                    id: f.id,
                    name: f.name,
                    type: f.type
                  };
                });
              }
            });
          }
        });

    }
    // redo y scale
    yScale2.domain([0, d3.max(visibleDates, function(e) { return d3.max(e.values, function(v) { return v.count; }) })]);

    // redraw y axis
    d3.select(".y.axis2").transition().duration(1500).ease('sin-in-out')
      .call(yAxis2);

    // redraw other lines
    mediaSources.selectAll('path').transition().duration(500)
      // only draw lines that are 'visible' Will cause an error, but best solution so far
      .attr('d',function(e){ return line(e.values); })
      // .attr('d',function(e){ if(e.vis==1) { return line(e.values); } else { return null; } });

    // redraw dots
    mediaSources.selectAll('circle').data(function(d) { return d.values;})
      .transition().duration(500)
      .attr({
        // cx: function(e) { if(d.vis==1) { return xScale(e.date); } else { return xScale(null); } },
        cx: function(e) { return xScale2(e.date); },
        // cx: function(e) { if(d.vis==1) { return yScale(e.count); } else { return yScale(null); } },
        cy: function(e) { return yScale2(e.count); },
      })
      .style({
        fill: function(e) {
          // if not shown
          if(e.vis === 0) {
            return 'white';
          }
          else {
            // get color of type (traditional or blog color)
            var typeColor = color(e.name);
            // darken color
            var d3color = d3.rgb(typeColor).darker();
            // return color
            return d3color;
          }

        }
      });
  }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CRISES COMPARED
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Should be run 1x as does not change.
 * @param error
 * @param data from 'timeline_crisis_compared.csv'
 */
function loadedComparedTimelineCallback(error,data){
    console.log("---- START ::: CRISES COMPARED ---");

    var mediaData = data;
    console.log("mediaData length: "+mediaData.length);
    console.log(mediaData);

    var minMonth = 1, maxMonth = 7;

    var margin = {
        top: 50,
        right:140,
        bottom: 0,
        left: 70
    };

    var width = 990 - margin.left - margin.right;
    var height = 450 - margin.bottom - margin.top;

    var detail = {
        x: 0,
        y: 25,
        w: width,
        h: 350
    };

    // define color
    var color = d3.scale.category20();

    var comparedSVG = d3.select("#timelineComparedVis")
        .append("svg")
        .attr({
            id: 'timeline-compared',
            class: 'timeline',
            width: width + margin.left + margin.right,
            height: height + margin.top + margin.bottom
        }).append("g")
        .attr({
            transform: "translate(" + margin.left + "," + margin.top + ")"
        });

        var xScale = d3.scale.linear().domain([minMonth,maxMonth]).range([0, detail.w]);
        var yScale = d3.scale.linear().domain([-100,100]).range([detail.h, 0]);

        var line = d3.svg.line()
            .x(function(d) {
                return xScale(d.coverage_month);
            })
            .y(function(d) {
                return yScale(d.percent_change);
            })
            .interpolate('linear');

    /* Create axes */
    var xAxis = d3.svg.axis()
        .scale(xScale)
        .orient('top')
        .ticks(maxMonth)
        .tickSize(6, 0, 0)
        .tickFormat(function(d){
            if (d === 1 || d === 7) return "";
            else return d;
        });


    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient('left')
        .ticks(6);

    d3.select('#timeline-compared').select("g").append("g")
        .attr("class", "axis")
        .attr("transform", "translate(0," + ((detail.h)/2) + ")")
        .call(xAxis)
        .append("text")
//            .attr("transform", "rotate(-90)")
            .attr("x",detail.w+2)
            .attr("y", -13)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text("crisis month");

    d3.select('#timeline-compared').select("g").append("g")
        .attr("class", "axis")
        .call(yAxis)
        .append("text")
             .attr("transform", "rotate(-90)")
             .attr("y", 6)
             .attr("dy", ".71em")
             .style("text-anchor", "end")
             .text("% change");

    comparedSVG.selectAll('.axis line, .axis path')
        .style({'stroke': '#CCC', 'fill': '#CCC', 'stroke-width': '1px'});

    comparedSVG.selectAll('.axis text')
        .style({'fill': '#CCC'});

    var series = comparedSVG.selectAll(".series")
        .data(mediaData)
        .enter().append("g")
        .attr("class", "series");

    series.append("path")
        .attr("class", function(d){ return d.name + ' path';})
        .attr("d", function (d) { return line(d.values); })
        .style("stroke", function (d) { return color(d.name); })
        .style("stroke-width", "4px")
        .style("fill", "none");

        /* add dots for each line */
        series.selectAll('circle').data(function(d) { return d.values;})
            .enter().append('circle')
            .attr({
                class: function(d){ return d.name + " dot"; }, //(was d.id) store name for reference to path
                cx: function(d) { return xScale(d.coverage_month); },
                cy: function(d) { return yScale(d.percent_change); },
                r: 4
            })
            .style({
                fill: function(d) {
                    // get color of type (traditional or blog color)
                    var c = color(d.name);
                    // darken color
                    var d3color = d3.rgb(c).darker();
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

        /* Add related legend - Functionality inspired by http://mpf.vis.ywng.cloudbees.net/*/

        series.append('text').attr({
            class: function(d){ return d.name + 'legend'; }, //(was d.id) store name for reference to path
            x: width + 120,
            y: function(d,i){ return (i * 16) + margin.top; },
            dy: '0.35em',
            // cursor: 'pointer',
            fill: function(d){ return color(d.name); }
        })
            .style("text-anchor", "end")
            .text(function(d) { return d.name; });

        /* Tool tips */
        // Initialize tooltip
        var tip = d3.tip()
            .html(function(d) {
                var month = d.coverage_month;
                var c = color(d.name);
                var stayedSame = false;
                if (month > 1) {
                    var changeText = "stayed the same";
                    var changeVal = d.percent_change;
                    if (d.percent_change > 0){
                        changeText = "increased by ";
                    } else if (d.percent_change < 0){
                        changeText = "decreased by ";
                        changeVal = Math.abs(d.percent_change);
                    } else stayedSame = true;
                    return  "<span class='sourceName'  style='color:" + c + ";'>" + d.name + ": </span>from month " + (month - 1) + " to " + month + "<br>coverage "+changeText +(stayedSame? "" : changeVal+"%")+".";
                } else
                return  "<span class='sourceName'  style='color:" + c + ";'>" + d.name + ": </span>at first month of crisis coverage.";
            })
            .direction('e')
            .attr('class','d3-tip e');

        // Invoke the tip in the context of your visualization
        series.call(tip);
    console.log("---- END ::: CRISES COMPARED ---");
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CRISIS SELECT
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

$(document).ready(function() {
    queue()
        .defer(d3.json,"/productiondata/compared_timeline.json")
        .await(loadedComparedTimelineCallback);

    document.getElementById("tab_1_compared").className = "content-tab active";
    addClassNameListener("tab_1_compared", function () {
        var className = document.getElementById("tab_1_compared").className;
        if (className === "content-tab active") {
            console.log("... tab change to tab_1_compared.");
            // add intro title and summary
            resetSummary(summary);
        }
    });

    addClassNameListener("tab_2_type", function () {
        var className = document.getElementById("tab_2_type").className;
        if (className === "content-tab active") {
            console.log("... tab change to tab_2_type.");
            resetSummary(summary);
        }
    });

    addClassNameListener("tab_3_source", function () {
        var className = document.getElementById("tab_3_source").className;
        if (className === "content-tab active") {
            console.log("... tab change to tab_3_source.");
            resetSummary(summary);
        }
    });
} );

  addClassNameListener("crisis_select", function(){
      crisis = window.crisis_select.value;
      console.log("### QUEUE NEW CRISIS ("+crisis+") AFTER CLASS CHANGE ###");
      queue()
          .defer(d3.csv, "/productiondata/"+crisis+"/summary.csv")//storypoints
          .defer(d3.csv, "/productiondata/"+crisis+"/storypoints.csv")//storypoints
          .defer(d3.csv, "/productiondata/"+crisis+"/google-media_stats.csv")//media
          .await(getData);
  });
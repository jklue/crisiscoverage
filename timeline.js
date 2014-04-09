/* Setup */
	var allDates, bbDetail, bbOverview, traditionalDates, blogDates, storyPoints, duplicateAllDates, duplicateTraditonalDates, duplicateBlogDates, padding, parseYear, svg, xDetailScale, xOverviewScale ;

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

  // define color
  color = d3.scale.ordinal().range(['#CC1452', '#14A6CC']);
  // color = d3.scale.category10();

	padding = 30;

	// read in date format from csv, convert to js object to be read by d3
  parseDate = d3.time.format("%Y-%m-%dT%H:%M:%S+00:00").parse;
  // remove hour from date so can aggregate articles by date w/out regard to hour
  parseDateSimple = d3.time.format("%b %d %Y");
  // format date for tooltip
  parseDateTips = d3.time.format("%b %d, %Y");
  // convert storypoints date to js object for graphing on x axis
  parseStorypoint = d3.time.format("%Y-%m-%d").parse;

  // array for all dates
  allDates = []; // master list for date, traditional media count, and blog media count

  traditionalDates = [];
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

/* Get traditional media data */
	d3.csv("data/2014-04-03-12.09.57_all_no_text-no2011.csv", function(data) {
 	
 	// make globally available
    originalData = data;

	  // define colors
	    // var keys = d3.keys(data[0]).filter(function(key) { return key !== "AnalysisDate"; });
      // convert dates to js object and get list of all dates with duplicates
	    originalData.forEach(function(d,i) {
        // convert each date to js date
        d.date_published = parseDate(d.date_published);
        // get all dates if not null
        if(d.date_published != null)
          duplicateTraditonalDates.push(d.date_published);
	    });
      // sort dates
      duplicateTraditonalDates.sort(function(a, b) {
        return d3.ascending(a, b);
      });
    // define previous date
    var previousDate;
    // go through dates and add to previous is date is same
    duplicateTraditonalDates.forEach(function(d,i) {
      // current date without time so can compare by day instead of hour
      var currentDate = parseDateSimple(d);
      // if current date not first element, set date and count
      if(currentDate != previousDate) {
        traditionalDates.push(
          { date: d, count: 1, type: 'traditional' } // set type for coloring dots later on
        );
        // set last date
        previousDate = currentDate;
      } else {
        // add one to previous date
        traditionalDates[traditionalDates.length-1].count++;
      }
    });
    // go get data from blog search
    return blogData();
  });

/* Get blog media data */

  function blogData() {

    d3.csv("data/2014-04-03-12.09.57_all_no_text-no2011-blog.csv", function(data) {
  // console.log(data);
      // convert dates to js object and get list of all dates with duplicates
      data.forEach(function(d,i) {
        // convert each date to js date
        d.date_published = parseDate(d.date_published);
        // get all dates if not null
        if(d.date_published != null)
          duplicateBlogDates.push(d.date_published);
      });
      // sort dates
      duplicateBlogDates.sort(function(a, b) {
        return d3.ascending(a, b);
      });
      // define previous date
      var previousDate;
      // go through dates and add to previous if date is same
      duplicateBlogDates.forEach(function(d,i) {
        // current date without time so can compare by day instead of hour
        var currentDate = parseDateSimple(d);
        // if current date not last date in array (and not first element), set date and count
        if(currentDate != previousDate) {
          blogDates.push(
            { date: d, count: 1, type:'blog' } // setting type for easy dot coloring later on.
          );
          // set last date
          previousDate = currentDate;
        } else {
          // add one to previous date
          blogDates[blogDates.length-1].count++;
        }
      });
      // go get data from story point document
      return mergeData();
    });
  }

/* Merge traditional and blog data */
  function mergeData() {

  // define colors
    color.domain(['traditional','blog']);

  // add blog data to master array
    // wrapper array for blog data
    var blogData = [];
    // prepare blog date data for master array
    blogData.values = blogDates.map(function(d){ return d; });
    // set type
    blogData.type = 'blog';
    // send blog data to master array
    allDates.push(blogData);

  // add traditional data to master array
    // wrapper array for traditional data
    var traditionalData = [];
    // prepare traditional date data for master array
    traditionalData.values = traditionalDates.map(function(d){ return d; });
    // set type
    traditionalData.type = 'traditional';
    // send traditional data to master array
    allDates.push(traditionalData);

    // go get data from story point document
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

	// Reset
		var xDetailAxis, yDetailAxis, yDetailScale;

	// normal scale
	  xDetailScale = d3.time.scale().domain(d3.extent(traditionalDates, function(d) { return d.date; })).range([0, bbDetail.w]);  // define the right domain
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
                  .ticks(7)
                  .tickFormat(d3.time.format('%b'));

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

    function circleMakerX(values) {
      console.log(values);
      return xDetailScale(values.date); 
    }

    function circleMakerY(values) {
      return yDetailScale(values.count); 
    }

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
                'stroke': function(e) { return color(e.type); },
                'fill': 'none',
               });

  // add fill
    dataTypes.append("path")
              .attr({
                class: "traditionalMediaArea",
                d: function(e) { return area(e.values); },
                transform: 'translate(0,' + bbDetail.y + ')'                  
              })
              .style({
                'fill': function(e) { return color(e.type); }
               });

  /* add dots for each line */
  // define color var for use inside next operation
    var mediaType = [];

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
        'fill': function(d) { return color(d.type); }
      })
    // dataTypes.append('circle')
    //   .attr({
    //     class: 'dot',
    //     cx: function(d,i) { return circleMakerX(d.values[i]); },
    //     cy: function(d,i) { return circleMakerY(d.values[i]); },
    //     // cy: function(d) { return yDetailScale(e.count); },
    //     r: 4,
    //     transform: 'translate(0,' + bbDetail.y + ')'
    //   })
    //   .style({
    //     'fill': function(d) { return color(d.type); }
    //   })
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

  // /* Storypoints */
  //   // add intro title and summary
  //   d3.select("#crisisTitle").html('<h3>Typhoon Haiyan</h3>');
  //   d3.select('#crisisStory').html('Typhoon Haiyan, known as Typhoon Yolanda in the Philippines, was a powerful tropical cyclone that devastated portions of Southeast Asia, particularly the Philippines, on November 8, 2013. <a href="http://en.wikipedia.org/wiki/Typhoon_Haiyan" class="storySource">&mdash; Wikipedia</a>');

  //   // add dotted lines
  //   detailFrame.selectAll('.line')
  //              .data(storyPoints)
  //           .enter().append('line')
  //              .attr({
  //                 class: 'storyline',
  //                 x1: function(d) { return xDetailScale(d.date); },
  //                 x2: function(d) { return xDetailScale(d.date); },
  //                 y1: -bbDetail.y, // make taller than chart
  //                 y2: bbDetail.h
  //               });

  //   // add triangles
  //   detailFrame.selectAll('.storyTriangle')
  //          .data(storyPoints)
  //       .enter().append('path')
  //          .attr({
  //             class: 'storyTriangle',
  //             transform: function(d){ return 'translate(' + xDetailScale(d.date) + ',' + -bbDetail.y + ')'; },
  //             d: d3.svg.symbol().type('triangle-down').size(256)
  //           })
  //          // add mouseover story details
  //          .on('mouseover',function(d){
  //             // convert story date to readable
  //             var storyDate = parseDateTips(d.date);
  //             /* Dates */
  //               // remove any previously shown dates
  //               d3.select('.crisisDate')
  //                 .remove();
  //               // show new date
  //               d3.select('#crisisTitle')
  //                 .append('span')
  //                 .html(storyDate)
  //                 .attr('class','crisisDate');    

  //             // add new content to div
  //             d3.select('#crisisStory').html(d.title);
  //             /* color and size handling */

  //               /* Lines */


  //               /* Triangles */
  //               // revert to original color on other triangles and shrink
  //               d3.selectAll('.storyTriangle')
  //                 .transition()
  //                 .style('fill','#919191')
  //                 .attr('d',d3.svg.symbol().type('triangle-down').size(256));
  //               // change color of current triangle and enlarge
  //               d3.select(this)
  //                 .transition()
  //                 .style('fill','#3D8699')
  //                 .attr('d',d3.svg.symbol().type('triangle-down').size(512));

  //          });

 //  /* Tool tips */
 //    // Initialize tooltip
 //    tip = d3.tip()
 //            .html(function(d) { 
 //              // if 1 article, use singular 'article'
 //              if(d.traditional == 1)
 //                return d.traditional + ' article on ' + parseDateTips(d.date); 
 //              // else use 'articles'
 //              else
 //                return d.traditional + ' articles on ' + parseDateTips(d.date); 
 //            })
 //            .direction('e')
 //            .attr('class','d3-tip e');

 //    // Invoke the tip in the context of your visualization
 //    detailFrame.call(tip)

 //  /* add vertical line mouseover - initial code with help from Richard @ http://stackoverflow.com/questions/18882642/d3-js-drawing-a-line-on-linegraph-on-mouseover */

 //    // // define vertical line group to hold line and tooltip
 //    // var hoverLineGroup = svg.append('g')
 //    //                      .attr('class','hover-line');                         
 //    // // define vertical line
 //    // var hoverLine = hoverLineGroup
 //    //     .append('line')
 //    //       .attr('x1',10).attr('x2',10)
 //    //       // set y to height of detail area, adjusting for margin on top
 //    //       .attr('y1',0 + bbDetail.y).attr('y2',bbDetail.h + bbDetail.y)
 //    //       .attr('stroke-width',2)
 //    //       .style('stroke','#CCC');

 //    // // control mousemove event
 //    // d3.select('.timeline').on('mouseover',function(){
 //    //   // console.log('mouseover');
 //    // }).on('mousemove',function(){
 //    //   // console.log('moved', d3.mouse(this));
 //    //   // get x coordinate of mouse and adjust for left margin
 //    //   var mouseX = d3.mouse(this)[0] - margin.left;
 //    //   // if not outside graph bounds
 //    //   if(mouseX > 0 && mouseX < width) {
 //    //     // set x coordinate of line
 //    //     hoverLine.attr('x1',mouseX)
 //    //              .attr('x2',mouseX);
 //    //     // show line
 //    //     hoverLine.style('opacity',1);
 //    //   } else {
 //    //     // disappear line        
 //    //     hoverLine.style('opacity',0);
 //    //   }

 //    // });

 //    // // set hoverline to invisible on page load
 //    // hoverLine.style('opacity',0);

	// // setup actions to take on brush event, scale is same as overview scale, but overview scale not in scope here
	// 	brush = d3.svg.brush().x(xOverviewScale).on("brush", brushed);

	// // add brush to viz
	// 	svg.append("g")
	// 		 .attr("class", "brush")
	// 		 .call(brush)
 //  		.selectAll("rect").attr({
 //    		height: bbOverview.h,
 //    		transform: "translate(0,0)"
	// 		});

 //  /* Brushed */
 //  	function brushed() {
 //      // update detail x scale domain based on brushed extent
 //      xDetailScale.domain(brush.empty() ? xOverviewScale.domain() : brush.extent());
 //      // redraw detail lines and dots
 //      detailFrame.select('.detailArea').attr('d', area);
 //      detailFrame.select('.detailPath').attr('d', areaLine);
 //      detailFrame.selectAll('.dot').attr('cx', function(d) { return xDetailScale(d.date); });
 //      // redraw axis
 //      detailFrame.select('.x.axis').call(xDetailAxis);
 //      // hide data info
 //      d3.select('.factpoints').style('display','none');
 //    }

  }
/* Setup */
	var allDates, bbDetail, bbOverview, traditionalDates, blogDates, storyPoints, duplicateDates, duplicateBlogDates, padding, parseYear, svg, xDetailScale, xOverviewScale ;

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
  duplicateDates = [];
  duplicateBlogDates = [];
	traditionalDates = [];
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
          duplicateDates.push(d.date_published);
	    });
      // sort dates
      duplicateDates.sort(function(a, b) {
        return d3.ascending(a, b);
      });
    // define previous date
    var previousDate;
    // go through dates and add to previous is date is same
    duplicateDates.forEach(function(d,i) {
      // current date without time so can compare by day instead of hour
      var currentDate = parseDateSimple(d);
      // if current date not last date in array (and not first element), set date and count
      if(currentDate != previousDate) {
        traditionalDates.push(
          { date: d, traditional: 1 }
        );
        // set last date
        previousDate = currentDate;
      } else {
        // add one to previous date
        traditionalDates[traditionalDates.length-1].traditional++;
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
  // console.log(duplicateBlogDates);
      // define previous date
      var previousDate;
      // go through dates and add to previous if date is same
      duplicateBlogDates.forEach(function(d,i) {
  // console.log(blogDates);
        // current date without time so can compare by day instead of hour
        var currentDate = parseDateSimple(d);
        // if current date not last date in array (and not first element), set date and count
        if(currentDate != previousDate) {
          blogDates.push(
            { date: d, blog: 1 }
          );
          // set last date
          previousDate = currentDate;
        } else {
          // add one to previous date
          blogDates[blogDates.length-1].blog++;
  // console.log('duplicate date');
        }
      });
  console.log(blogDates);
      
      // go get data from story point document
      return mergeData();
    });
  }
/* Merge traditional and blog data */
  function mergeData() {

    // make new array with date, traditional media count, and blog media count
    allDates = traditionalDates;


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
	  xDetailScale = d3.time.scale().domain(d3.extent(allDates, function(d) { return d.date; })).range([0, bbDetail.w]);  // define the right domain
	// example that translates to the bottom left of our vis space:
	  var detailFrame = svg.append("g").attr({
	      class: 'detailFrame',
        transform: "translate(" + bbDetail.x + "," + bbDetail.y +")",
	  });

	// use that minimum and maximum possible y values for domain in log scale
	  yDetailScale = d3.scale.linear().domain([
	                            d3.min(allDates, function(d) { return d.traditional; }),
	                            d3.max(allDates, function(d) { return d.traditional; })
	                          ]).range([bbDetail.h, 0]);
// console.log(yDetailScale(2));
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
								     .y1(function(d) { return yDetailScale(d.traditional); });

    var areaLine = d3.svg.line()
                     .x(function(d) { return xDetailScale(d.date); })
                     .y(function(d) { return yDetailScale(d.traditional); })
                     .interpolate('linear');

    // could not figure out how to set static y0 and y1 values using d3 line generator
    // var storypointLine = d3.svg.line();

  // add x axis to svg
    detailFrame.append('g')
            .attr({
              class: 'x axis',
              transform: 'translate(0,' + (bbDetail.h) +')'
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

  // add fill
  detailFrame.append("path")
		         .datum(allDates)
		         .attr("class", "traditionalMediaArea")
		         .attr("d", area);

  // add line
    detailFrame.append('path')
               .datum(allDates)
               .attr({
                  class: 'traditionalMediaPath',
                  d: areaLine,
               })
               .style({
                'fill': 'none',
               });

  /* add dots for each line */
    detailFrame.selectAll('.traditionalMediaDot')
       .data(allDates)
    .enter().append('circle')
      .attr({
        class: 'traditionalMediaDot',
        cx: function(d) { return xDetailScale(d.date); },
        cy: function(d) { return yDetailScale(d.traditional); },
        r: 4,
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

  /* Tool tips */
    // Initialize tooltip
    tip = d3.tip()
            .html(function(d) { 
              // if 1 article, use singular 'article'
              if(d.traditional == 1)
                return d.traditional + ' article on ' + parseDateTips(d.date); 
              // else use 'articles'
              else
                return d.traditional + ' articles on ' + parseDateTips(d.date); 
            })
            .direction('e')
            .attr('class','d3-tip e');

    // Invoke the tip in the context of your visualization
    detailFrame.call(tip)

  

  /* add vertical line mouseover - initial code with help from Richard @ http://stackoverflow.com/questions/18882642/d3-js-drawing-a-line-on-linegraph-on-mouseover */

    // // define vertical line group to hold line and tooltip
    // var hoverLineGroup = svg.append('g')
    //                      .attr('class','hover-line');                         
    // // define vertical line
    // var hoverLine = hoverLineGroup
    //     .append('line')
    //       .attr('x1',10).attr('x2',10)
    //       // set y to height of detail area, adjusting for margin on top
    //       .attr('y1',0 + bbDetail.y).attr('y2',bbDetail.h + bbDetail.y)
    //       .attr('stroke-width',2)
    //       .style('stroke','#CCC');

    // // control mousemove event
    // d3.select('.timeline').on('mouseover',function(){
    //   // console.log('mouseover');
    // }).on('mousemove',function(){
    //   // console.log('moved', d3.mouse(this));
    //   // get x coordinate of mouse and adjust for left margin
    //   var mouseX = d3.mouse(this)[0] - margin.left;
    //   // if not outside graph bounds
    //   if(mouseX > 0 && mouseX < width) {
    //     // set x coordinate of line
    //     hoverLine.attr('x1',mouseX)
    //              .attr('x2',mouseX);
    //     // show line
    //     hoverLine.style('opacity',1);
    //   } else {
    //     // disappear line        
    //     hoverLine.style('opacity',0);
    //   }

    // });

    // // set hoverline to invisible on page load
    // hoverLine.style('opacity',0);

	// setup actions to take on brush event, scale is same as overview scale, but overview scale not in scope here
		brush = d3.svg.brush().x(xOverviewScale).on("brush", brushed);

	// add brush to viz
		svg.append("g")
			 .attr("class", "brush")
			 .call(brush)
  		.selectAll("rect").attr({
    		height: bbOverview.h,
    		transform: "translate(0,0)"
			});

  /* Brushed */
  	function brushed() {
      // update detail x scale domain based on brushed extent
      xDetailScale.domain(brush.empty() ? xOverviewScale.domain() : brush.extent());
      // redraw detail lines and dots
      detailFrame.select('.detailArea').attr('d', area);
      detailFrame.select('.detailPath').attr('d', areaLine);
      detailFrame.selectAll('.dot').attr('cx', function(d) { return xDetailScale(d.date); });
      // redraw axis
      detailFrame.select('.x.axis').call(xDetailAxis);
      // hide data info
      d3.select('.factpoints').style('display','none');
    }

  /* Talking Points */
    d3.select('#reset')
      .on('click', function(){


        // remove extent from brush data
        brush.clear();
        // call brush to remove visual
        svg.select('.brush').transition().call(brush);
        // clear detail area
          // update detail x scale domain 
          xDetailScale.domain(xOverviewScale.domain());
          // redraw detail lines and dots
          detailFrame.select('.detailArea').transition().attr('d', area);
          detailFrame.select('.detailPath').transition().attr('d', areaLine);
          detailFrame.selectAll('.dot').transition().attr('cx', function(d) { return xDetailScale(d.date); });
          // redraw axis
          detailFrame.select('.x.axis').transition().call(xDetailAxis);

        // hide any other text
        d3.selectAll('.factpoints').style('display','none');
      })

  /* feb 2012 */
    d3.select('#feblink')
      .on('click', function(){

        // define date boundaries
        var d1 = new Date("Jan 1, 2012")
        var d2 = new Date("Mar 30, 2012")
        // update brush extent
        brush.extent([d1,d2]);
        // redraw brush on overview 
        svg.select('.brush').transition().call(brush);
        // redraw detail view
        xDetailScale.domain(brush.extent());
        // redraw detail lines and dots
        detailFrame.select('.detailArea').transition().attr('d', area);
        detailFrame.select('.detailPath').transition().attr('d', areaLine);
        detailFrame.selectAll('.dot').transition().attr('cx', function(d) { return xDetailScale(d.date); });
        // redraw axis
        detailFrame.select('.x.axis').transition().call(xDetailAxis);
        // show info div
        d3.select('#feb').transition().delay(300).style('display','block');
        console.log('feb clicked');

        // hide any other text
        d3.selectAll('.factpoints').style('display','none');
      });

  /* aug 2012 */
    d3.select('#auglink')
      .on('click', function(){

        // define date boundaries
        var d1 = new Date("Jul 1, 2012")
        var d2 = new Date("Sep 30, 2012")
        // update brush extent
        brush.extent([d1,d2]);
        // redraw brush on overview 
        svg.select('.brush').transition().call(brush);
        // redraw detail view
        xDetailScale.domain(brush.extent());
        // redraw detail lines and dots
        detailFrame.select('.detailArea').transition().attr('d', area);
        detailFrame.select('.detailPath').transition().attr('d', areaLine);
        detailFrame.selectAll('.dot').transition().attr('cx', function(d) { return xDetailScale(d.date); });
        // redraw axis
        detailFrame.select('.x.axis').transition().call(xDetailAxis);
        // show info div
        d3.select('#aug').transition().delay(300).style('display','block');
        console.log('aug clicked');
        
        // hide any other text
        d3.selectAll('.factpoints').style('display','none');      });

  }
/*
 Base globe code from http://bl.ocks.org/mbostock/4183330
 Legend code from https://github.com/jgoodall/d3-colorlegend
 Globe manipulation from http://rveciana.github.io/geoexamples/?page=d3js/d3js_svgcanvas/rotatingSVG.html
    Note: Anything beyond the clipAngle is assigned d="", which I believe should be a valid null value, but flags as an error. (http://stackoverflow.com/questions/17396650/d3-js-parsing-error-rotation-of-orthogonal-map-projection)
 Tabs code from http://code-tricks.com/create-a-simple-html5-tabs-using-jquery/
 Scrollpane from https://github.com/vitch/jScrollPane/blob/master/script/jquery.jscrollpane.min.js
 */

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// COMMON
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var color, min, mean, max; //<-- common required at top
var mediaData = {};//<-- globe required at top
var mediaBarData = [], isCountrySortDescending = true, isResultSortDescending = false;//<-- bar required at top

var maxWidth = 960,
    maxHeight = 650,
    width = maxWidth,
    height = maxHeight-150;

var margin = {
    top: 0,
    right: 0,
    bottom: 0,//legend is at the bottom outside svg.
    left: 0
};

var myColors = {
        Grays: ["#f0f0f0","#d9d9d9"],
        Blues: ["#deebf7","#c6dbef","#9ecae1","#6baed6","#4292c6"],
        Greens: ["#74c476","#41ab5d","#238b45","#006d2c"]
    },
    colorScale =  myColors.Grays.concat(myColors.Blues).concat(myColors.Greens);

/**
 * Add a prototype method to String (careful to escape special chars used by regex)
 * @param find
 * @param replace
 * @returns {string}
 */
String.prototype.replaceAll = function (find, replace) {
    var str = this;
    return str.replace(new RegExp(find.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'), 'g'), replace);
};

/**
 * Be notified when a registered element has a class change.
 * @param elemId
 * @param callback
 */
function addClassNameListener(elemId, callback) {
    var elem = document.getElementById(elemId);
    var lastClassName = elem.className;
    window.setInterval( function() {
        var className = elem.className;
        if (className !== lastClassName) {
            callback();
            lastClassName = className;
        }
    },10);
}

/**
 * Friendly print for numbers, considering decimals.
 * @param x
 * @returns {string}
 */
function numberWithCommas(x) {
    if (isNaN(x)) return x;
    var parts = x.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return parts.join(".");
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// GLOBE ONLY
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var country, world_data;//<-- these must be defined after indicators available.

var svg = d3.select("#overviewVis").append("svg").attr({
        width: width+margin.left+margin.right,
        height: height+margin.top+margin.bottom,
        transform: "translate(" + margin.left + "," + margin.top + ")"
    })
        .on("mousedown", mousedown)
        .on("mousemove", mousemove)
        .on("mouseup", mouseup),

    countries = svg.append("g").attr({
        id: "countries",
        width: width,
        height: height
    }),

    tip = d3.tip()
        .attr('class', 'd3-tip none')
        .offset([-10, 0])
        .html(function(d) {
            var v;
            var myColor = 'white';

            if (!d || !mediaData[d.properties.name] || isNaN(mediaData[d.properties.name].articles)) v = "(no data)";
            else {
                myColor = color(mediaData[d.properties.name].articles);
                v = numberWithCommas(mediaData[d.properties.name].articles);
            }
            return  "<strong>Country: </strong><span style='color:"+myColor+";'><em>"+d.properties.name+"</em></span><span style='color:white;'>, </span><strong>Indicator Value: </strong><span style='color:"+myColor+";'><em>"+v+"</em></span>";
        });

/* lat and lon lines */
var projection = d3.geo.orthographic()
        .scale(250)
        .translate([width / 2, height / 2])
        .clipAngle(90),

    path = d3.geo.path()
        .projection(projection),

// instantiate meridians and parallels with d3's help
    graticule = d3.geo.graticule();

// add meridians and parallels to globe
svg.append('path')
    .datum(graticule)
    .attr('class','graticule')
    .attr('d',path);

var m0,
    o0,
    done;

var velocity = .02,
    then = Date.now(),
    lastClick = 0;

queue()
    .defer(d3.json, "productiondata/globe.json")//world
    .defer(d3.csv, "/productiondata/haiyan/google-country_stats.csv")//media
    .await(loadedDataCallBack);

function loadedDataCallBack(error, world, media) {
    console.log("--- START ::: loadedDataCallback ---");

    world_data = topojson.feature(world, world.objects.countries).features;

    /* convert media data to json object */
    media.forEach(function (d) {
        // set country name as property value
        var name = d.query_distinct.slice(d.query_distinct.indexOf("(") + 1, d.query_distinct.lastIndexOf(")")),
            code = d.query_distinct.slice(0, d.query_distinct.indexOf("(") - 1),
            articles = code === "CS" ? 1 : +d.raw_result_count; //serbian results are consistently odd in API

        //Globe Data needs name to map directly to the world_data countries names !!!
        mediaData[name] = {
            // set country name, code, and number of articles written
            name: name,
            code: code,
            articles: articles
        };

        //Bar Data needs an Array and needs &quot; removed !!!
        mediaBarData.push({
            // set country name, code, and number of articles written
            name: name.replaceAll('&quot;', ''),
            code: code,
            articles: articles
        });
    });
    console.log('mediaData: ', mediaData);

    // quantile scale
    var resultCountData = _.pluck(mediaData, 'articles');

    min = d3.min(resultCountData),
        mean = d3.sum(resultCountData) / resultCountData.length,
        max = d3.max(resultCountData);

    color = d3.scale.quantile()
        .domain([min, mean, max])
        .range(colorScale);

    //Populate country 1x
    country = svg
        .selectAll(".country")
        .data(world_data);

    country.enter()
        .append('path')
        .attr('class', 'country')
        .attr('d', path)
        .on('mouseover', tip.show)
        .on("mousemove", function () {
            return tip
                .style("top", (d3.event.pageY + 16) + "px")
                .style("left", (d3.event.pageX + 16) + "px");
        })
        .on('mouseout', tip.hide);
        country.call(tip);
    renderBarChart();
    renderGlobe();

    /* add chart reorder */
    d3.selectAll("input")
        .on("click", function () {
            reorder(this.value);
            return;
    });

    addClassNameListener("tab_1_globe", function(){
        var className = document.getElementById("tab_1_globe").className;
        if (className === "active"){
//            alert("changed to globe tab, className: "+className);
//            renderGlobe();
        }
    });
    addClassNameListener("tab_2_bar", function(){
        var className = document.getElementById("tab_2_bar").className;
        if (className === "active"){
//            alert("changed to bar tab, className: "+className);

//            isCountrySortDescending = true;
//            isResultSortDescending = false;
//
//            renderBarChart();
        }
    });

    console.log("--- END ::: loadedDataCallback ---");
}

/**
 * RENDER GLOBE (AFTER ALL ELSE IS LOADED)
 */
function renderGlobe() {
    //Fill according to latest results.
    country
        .attr('fill', function (d) {
            if (_.has(mediaData, d.properties.name)) {
                console.log(mediaData[d.properties.name].articles);
                return color(mediaData[d.properties.name].articles);
            } else {
                return 'white';
            }
        });

    //Populate legend
    colorlegend("#legend_globe", color, "quantile", {title: "results by country", boxHeight: 15, boxWidth: 30, fill: false, linearBoxes: 11});
    startAnimation();
}

function stopAnimation() {
    done = true;
}

function startAnimation() {
    done = false;
    then = Date.now();

    d3.timer(function() {
        var angle = velocity * (Date.now() - then);
        projection.rotate([angle,0,0]);
        svg.selectAll("path")
            .attr("d", path.projection(projection));
        return done;
    });
}

function mousedown() {
    if (!done) stopAnimation();
    else if (Date.now()-lastClick < 500) {
        startAnimation();
    }
    lastClick = Date.now();

    m0 = [d3.event.pageX, d3.event.pageY];
    o0 = projection.rotate();
    d3.event.preventDefault();
}

function mousemove() {
    if (m0) {
        var m1 = [d3.event.pageX, d3.event.pageY],
//            o1 = [o0[0] + (m0[0] - m1[0]) / 8, o0[1] + (m1[1] - m0[1]) / 8];
            o1 = [o0[0] + (m1[0]- m0[0]) / 8,0];//no y and rotate with mouse
        projection.rotate(o1);
        svg.selectAll("path").attr("d", path);
    }
}

function mouseup() {
    if (m0) {
        mousemove();
        m0 = null;
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// COUNTRY BAR CHART
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

var defaultBarTextColor = '#636363',
    lightBarTextColor = '#bdbdbd',
    hoverColor = 'orangered';

var marginBar = {
        top: 50,
        bottom: 0,
        left: 0,
        right: 10
    },
    widthBar = maxWidth - marginBar.left - marginBar.right,
    heightBar = maxHeight - marginBar.top - marginBar.bottom,
    countryWidth = 250;

var xScale = d3.scale.pow().exponent(0.1).range([0, widthBar - countryWidth]),
    yScale = d3.scale.ordinal().rangeRoundBands([0, heightBar], .8, 0);

/* Bar rendering vars */
var bar_height = 15,
    bar_padding = 1,
    x_pad = 5,
    y_em = ".95em";

/* Sorting vars */
var groups = null,
    bars = null;

var svgBar = d3.select("#country_bar_chart").append("svg")
        .attr("width", widthBar + marginBar.left + marginBar.right)
        .attr("height", 3000);

    gBar = svgBar.append("g")
        .attr("transform", "translate("+marginBar.left+","+marginBar.top+")");

/**
 * RENDER BAR CHART (AFTER ALL ELSE IS LOADED)
 */
function renderBarChart() {
    stopAnimation();

    var nameData = _.pluck(mediaBarData, 'name');

    xScale.domain([min, max]);
    yScale.domain(nameData);

    if (groups) groups.remove();

    groups = gBar.append("g")
        .selectAll("g")
        .data(mediaBarData)
        .enter()
        .append("g")
        .attr("id", function (d) {
            return "bar_group_" + d.code;
        })
        .attr("transform", function (d) {
            console.log(d);
            return "translate(" + countryWidth + ", " + yScale(d.name) + ")";
        });

    bars = groups.append("rect")
        .attr("class", "bar_rect")
        .attr("id", function (d) {
            return "bar_rect_" + d.code;
        })
        .attr("width", function (d) {
            return xScale(d.articles);
        })
        .attr("height", bar_height - bar_padding)
        .style("fill", function (d) {
            return color(d.articles);
        })
        .on('mouseover', function (d) {
            fill(d, true);
        })
        .on('mouseout', function (d) {
            fill(d, false);
        });

    /* ADD TEXT LABELS */
    //Countries
    groups.append("text")
        .attr("class", "bar_text")
        .attr("id", function (d) {
            return "bar_text_country_" + d.code;
        })
        .attr("x", "-" + x_pad)
        .attr("dy", y_em)
        .style("fill", defaultBarTextColor)
        .text(function (d) {
            return d.name;
        });

    //Result Counts
    groups.append("g").append("text")
        .attr("class", "bar_text")
        .attr("id", function (d) {
            return "bar_text_result_" + d.code;
        })
        .attr("x", function (d) {
            var n = xScale(d.articles) - x_pad;
            if (n < 0) return 5;
            else return n;
        })
        .attr("dy", y_em)
        .style("fill", function (d) {
            var c = color(d.articles);
            if (c === myColors.Greens[3]) return lightBarTextColor;//need to contrast dark green.
            else return defaultBarTextColor;
        })
        .text(function (d) {
            var n = xScale(d.articles) - x_pad;
            if (n < 0) return "0";
            else return d.articles;
        });

    /* Add centered title to outer 'g' element */
    gBar.append("text")
        .attr("class", "bar_title")
        .attr("x", (width / 2))
        .attr("y", 0 - (margin.top / 2)- 10)
        .attr("fill", defaultBarTextColor)
        .text("Results by Country");

    reorder("result");

    d3.select("#country_bar_chart").style('overflow-x','hidden');
    d3.select("#country_bar_chart").style('overflow-y','scroll');
}


/**
 * Fill
 * @param {Object} d
 * @param {boolean} isHover
 */
function fill(d,isHover){
    var group = d3.select("#bar_group_"+ d.code);
    if (group != null) {
        if (isHover){
            group.select('rect').style('fill', hoverColor);
            group.select('text').style('fill', hoverColor);
        }
        else {
            group.select('rect').style('fill', color(d.articles));
            group.select('text').style('fill', defaultBarTextColor);
        }
    }
}

/**
 * Trigger a sort based on Country or Rank
 * @param {String}  radioVal     value of the radio
 */
function reorder(radioVal){
    groups.sort(function(a, b) {
        if (radioVal === "country"){
            if (isCountrySortDescending){
                return d3.ascending(a.name,b.name);
            } else {
                return d3.descending(a.name,b.name);
            }
        } else {
            return internalNumberSort(
                a,parseInt(a.articles),b,parseInt(b.articles),isResultSortDescending);
        }
    });

    /* Toggle state on sorted input.value; reset sort status on non-sorted to trigger ascending next sort. */
    if (radioVal === "country"){
        console.log("... reorder for 'country', descending? "+isCountrySortDescending);
        isCountrySortDescending = isCountrySortDescending ? false : true;
        isResultSortDescending = false;//<-- Note: want to trigger descending sort by default.
    } else {
        console.log("... reordered for 'result', descending? "+isResultSortDescending);
        isResultSortDescending = isResultSortDescending ? false : true;
        isCountrySortDescending = true;
    }

    groups
        .transition()
        .duration(750)
        .delay(function(d, i) { return i * 10; })
        .attr("transform", function(d, i) {
            //translate x,y does not change the x,y but move the element.
            return "translate(" + countryWidth +", " + i*bar_height +")";
        });
    return;
}

/**
 * Special sort considerations for numbers to sort alphabetically by "Country" when number values
 * to compare are equal. This will ALWAYS sort ascending by Country when values for number are equal.
 * @param  {Array} a                   data for a
 * @param  {Number} _a            compare with _b
 * @param  {Array} b                   data for b
 * @param  {Number} _b            compared to _a
 * @param  {boolean} sortAscending   if true, sort ascending; otherwise sort descending
 * @return {int}                  -1 | 0 | 1
 */
function internalNumberSort(a,_a,b,_b,sortAscending){
    // console.log("... comparing number '"+_a+"' to '"+_b+"'");
    if (sortAscending){
        if (!isNaN(_a) && !isNaN(_b) && _a == _b){
            return d3.ascending(a.name,b.name);
        } else {
            return d3.ascending(_a,_b);
        }
    } else {
        if (!isNaN(_a) && !isNaN(_b) && _a == _b){
            return d3.ascending(a.name,b.name);
        } else {
            return d3.descending(_a,_b);
        }
    }
}

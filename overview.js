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
var mediaData = {},  world_data,//<-- globe & map required at top
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

var mediaBarData = [], isCountrySortDescending = true, isResultSortDescending = false;//<-- bar required at top

var maxWidth = 940,
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
//        Grays: ["#f0f0f0","#d9d9d9"],
//        Blues: ["#deebf7","#c6dbef","#9ecae1","#6baed6","#4292c6"],
//        Greens: ["#74c476","#41ab5d","#238b45","#006d2c"]
        Blues: ["#deebf7", "#4292c6"],
        Greens: ["#74c476", "#41ab5d", "#238b45"]
    },
    colorScale =  myColors.Blues.concat(myColors.Greens);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// GLOBE ONLY
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var country;//<-- these must be defined after indicators available.

var svg = d3.select("#overviewGlobe").append("svg").attr({
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
svg.append("g").append('path')
    .datum(graticule)
    .attr('class','graticule')
    .attr('d',path);

var m0,
    o0,
    done,
    shouldResume;

var velocity = .02,
    then = Date.now(),
    lastClick = 0;

/**
 * RENDER GLOBE (AFTER ALL ELSE IS LOADED)
 */
function renderGlobe() {
    //Fill according to latest results.
    country
        .attr('fill', function (d) {
            if (_.has(mediaData, d.properties.name) && mediaData[d.properties.name].articles > 0) {
//                console.log(mediaData[d.properties.name].articles);
                return color(mediaData[d.properties.name].articles);
            } else {
                return 'white';
            }
        });

    //Populate legend
    $('#legend_globe').empty();
    colorlegend("#legend_globe", color, "quantile", {title: "results by country", boxHeight: 15, boxWidth: 30, fill: false, linearBoxes: 5});
    if (!done) startAnimation();
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
// MAP ONLY
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var lastColorClicked,
    country_map;

var svgMap = d3.select("#overviewMap").append("svg").attr({
        width: width+margin.left+margin.right,
        height: height+margin.top+margin.bottom,
        transform: "translate(" + margin.left + "," + margin.top + ")"
    }),

    countries_map = svgMap.append("g").attr({
        id: "countries_map",
        width: width,
        height: height
    }),

    pathMap = d3.geo.path().projection(d3.geo.mercator().translate([width / 2, height / 2]));


/**
 * Multifunctional color method, will handle regular fill and fancy based on clicked color.
 * @param dimOthers {boolean} if false, regular fill.
 * @param colorToPass {String} if dimOthers is true, this color drives selector.
 */
function colorCountry(dimOthers,colorToPass){
    if (dimOthers && colorToPass){

        country_map
            .attr("opacity", function(d) {
                if (lastColorClicked && lastColorClicked === colorToPass) return 1;
                else {
                    var co = color(0);
                    if (d && _.has(mediaData, d.properties.name)) {
                        var c = mediaData[d.properties.name].articles;
                        if (c && !isNaN(c) && c > 0) co = color(c);
                    }

//                console.log("co: "+co+", colorToPass: "+colorToPass+", equal? "+(co === colorToPass));
                    if (co === colorToPass) return 1;
                    else return .3;
                }
            });
        lastColorClicked = colorToPass;
    } else lastColorClicked = undefined;

    country_map.transition()
        .duration(250)
        .style("fill", function(d) {
            if (d && _.has(mediaData, d.properties.name)) {
                var c = mediaData[d.properties.name].articles;
                if (c && !isNaN(c) && c > 0) return color(c);
            } else return "white";
        });
}

/**
 * RENDER MAP (AFTER ALL ELSE IS LOADED)
 */
function renderMap() {

    colorCountry(false,null);

    //Populate legend
    $('#legend_map').empty();
    colorlegend("#legend_map", color, "quantile", {title: "results by country", boxHeight: 15, boxWidth: 30, fill: false, linearBoxes: 5});
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
        right: 50
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

var svgBar, gBar;

/**
 * RENDER BAR CHART (AFTER ALL ELSE IS LOADED)
 */
function renderBarChart() {
    isCountrySortDescending = true;
    isResultSortDescending = false;

    var nameData = _.pluck(mediaBarData, 'name');

    xScale.domain([min, max]);
    yScale.domain(nameData);

    $('#country_bar_chart').empty();
    svgBar = d3.select("#country_bar_chart").append("svg")
        .attr("width", widthBar + marginBar.left + marginBar.right)
        .attr("height", 3000);

    gBar = svgBar.append("g")
            .attr("transform", "translate("+marginBar.left+","+marginBar.top+")");

    groups = gBar.append("g")
        .selectAll("g")
        .data(mediaBarData)
        .enter()
        .append("g")
        .attr("id", function (d) {
            return "bar_group_" + d.code;
        })
        .attr("transform", function (d) {
//            console.log(d);
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
            if (c === myColors.Greens[2]) return lightBarTextColor;//need to contrast dark green.
            else
            return defaultBarTextColor;
        })
        .text(function (d) {
            var n = xScale(d.articles) - x_pad;
            if (n < 0) return "0";
            else return d.articles;
        });

    reorder("result");
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

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// CRISIS SELECT CHANGES AND OTHER PAGE-SPECIFICS
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function loadedDataCallBack(error, world, media, summary) {
    console.log("--- START ::: loadedDataCallback ---");

    mediaData = {};
    mediaBarData = [];
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

    //Populate country for globe
    $('#countries').empty();
    country = countries
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

    //Populate country for map
    $('#countries_map').empty();
    country_map = countries_map
        .selectAll(".country")
        .data(world_data);

    country_map.enter()
        .append('path')
        .attr('class', 'country')
        .attr('d', pathMap)
        .on('mouseover', tip.show)
        .on("mousemove", function () {
            return tip
                .style("top", (d3.event.pageY + 16) + "px")
                .style("left", (d3.event.pageX + 16) + "px");
        })
        .on('mouseout', tip.hide)
        .on('click',function(d){
            console.log(d);
            var rc = mediaData[d.properties.name].articles;
            if (!isNaN(rc)){
                var co = color(rc);
                colorCountry(true,co);
            }
        });
    country_map.call(tip);

    renderGlobe();
    renderMap();
    renderBarChart();

    console.log("--- END ::: loadedDataCallback ---");
}

addClassNameListener("crisis_select", function(){
    var crisis = window.crisis_select.value;
    console.log("### QUEUE NEW CRISIS ("+crisis+") AFTER CLASS CHANGE ###");
    queue()
        .defer(d3.json, "productiondata/globe.json")//world
        .defer(d3.csv, "/productiondata/"+crisis+"/google-country_stats.csv")//media
        .defer(d3.csv, "/productiondata/"+crisis+"/summary.csv")//storypoints
        .await(loadedDataCallBack);
});

/* add chart reorder */
d3.selectAll("input")
    .on("click", function () {
        reorder(this.value);
        return;
    });

$(document).ready(function() {

    addClassNameListener("tab_1_globe", function () {
        var className = document.getElementById("tab_1_globe").className;
        if (className === "content-tab active") {
            console.log("... tab change to tab_1_globe.");
            if (shouldResume) startAnimation();
        }

        //Populate legend
        $('#legend_globe').empty();
        colorlegend("#legend_globe", color, "quantile", {title: "results by country", boxHeight: 15, boxWidth: 30, fill: false, linearBoxes: 5});
        if (!done) startAnimation();
    });

    addClassNameListener("tab_2_map", function () {
        var className = document.getElementById("tab_2_map").className;
        if (className === "content-tab active") {
            console.log("... tab change to tab_2_map.");
            if (!done) {
                stopAnimation();
                shouldResume = true;
            } else shouldResume = false;

            //Populate legend
            $('#legend_map').empty();
            colorlegend("#legend_map", color, "quantile", {title: "results by country", boxHeight: 15, boxWidth: 30, fill: false, linearBoxes: 5});
        }
    });

    addClassNameListener("tab_3_bar", function () {
        var className = document.getElementById("tab_3_bar").className;
        if (className === "content-tab active") {
            console.log("... tab change to tab_3_bar.");
            if (!done) {
                stopAnimation();
                shouldResume = true;
            } else shouldResume = false;
        }
    });
} );
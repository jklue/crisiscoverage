/*
    Base globe code from http://bl.ocks.org/mbostock/4183330
    Legend code from https://github.com/jgoodall/d3-colorlegend
    Trackball code from http://bl.ocks.org/patricksurry/5721459
*/
var
    width = 960,
    height = 500;

var margin = {
    top: 0,
    right: 0,
    bottom: 0,//legend is at the bottom outside svg.
    left: 0
};

var svg = d3.select("#overviewVis").append("svg").attr({
        width: width+margin.left+margin.right,
        height: height+margin.top+margin.bottom,
        transform: "translate(" + margin.left + "," + margin.top + ")"
    })
    .on("mousedown", mousedown)
    .on("mousemove", mousemove)
    .on("mouseup", mouseup),

    mediaData = {}, country, color, //<-- these must be defined after indicators available.

    countries = svg.append("g").attr({
        id: "countries",
        width: width,
        height: height
    }),

    myColors = {
        Grays: ["#f0f0f0","#d9d9d9"],
        Blues: ["#deebf7","#c6dbef","#9ecae1","#6baed6","#4292c6"],
        Greens: ["#74c476","#41ab5d","#238b45","#006d2c"]
    },
    colorScale =  myColors.Grays.concat(myColors.Blues).concat(myColors.Greens),

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

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// GLOBE METHODS
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

queue()
    .defer(d3.json, "productiondata/globe.json")//world
    .defer(d3.csv, "/productiondata/haiyan/google-country_stats.csv")//media
    .await(loadedDataCallBack);

function loadedDataCallBack(error, world, media) {
    console.log("--- START ::: loadedDataCallback ---");

    var world_data = topojson.feature(world, world.objects.countries).features;

    /* convert media data to json object */
    media.forEach(function(d){
        // set country name as property value
        var name =  d.query_distinct.slice(d.query_distinct.indexOf("(") + 1, d.query_distinct.indexOf(")")),
            code = d.query_distinct.slice(0, d.query_distinct.indexOf("(") - 1);

        mediaData[name] = {
            // set country name, code, and number of articles written
            name: name,
            code: code,
            articles: +d.raw_result_count
        };
    });
    console.log('mediaData: ',mediaData);

    // set domain for coloring, grabbing 'articles' from json file using underscore.js
    var lifeExpectancyDomain = d3.extent(_.pluck(mediaData, 'articles'));

    var resultCountData = _.pluck(mediaData, 'articles'),
        min = d3.min(resultCountData),
        mean = d3.sum(resultCountData) / resultCountData.length,
        max = d3.max(resultCountData);

    // quantile scale
    color = d3.scale.quantile()
        .domain([min, mean, max])
        .range(colorScale);

    colorlegend("#legend_globe", color, "quantile", {title: "results by country", boxHeight: 15, boxWidth: 30, fill:false, linearBoxes:11});

    country = svg
        .selectAll(".country")
        .data(world_data);

    country.enter()
        .append('path')
        .attr('class','country')
        .attr('d', path)
        .attr('fill', function(d){
            if(_.has(mediaData, d.properties.name)){
                console.log(mediaData[d.properties.name].articles);
                return color(mediaData[d.properties.name].articles);
            } else {
                return 'white';
            }
        })
        .on('mouseover', tip.show)
        .on("mousemove", function () {
            return tip
                .style("top", (d3.event.pageY + 16) + "px")
                .style("left", (d3.event.pageX + 16) + "px");
        })
        .on('mouseout', tip.hide);

    country.call(tip);

    console.log("--- END ::: loadedDataCallback ---");
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
// TRACKBALL METHODS
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
function trackballAngles(pt) {
    var r = projection.scale();
    var c = projection.translate();
    var x = pt[0] - c[0], y = - (pt[1] - c[1]), ss = x*x + y*y;

    var z = r*r > 2 * ss ? Math.sqrt(r*r - ss) : r*r / 2 / Math.sqrt(ss);

    var lambda = Math.atan2(x, z) * 180 / Math.PI;
    var phi = Math.atan2(y, z) * 180 / Math.PI
    return [lambda, phi];
}

function composedRotation(λ, ϕ, γ, δλ, δϕ) {
    λ = Math.PI / 180 * λ;
    ϕ = Math.PI / 180 * ϕ;
    γ = Math.PI / 180 * γ;
    δλ = Math.PI / 180 * δλ;
    δϕ = Math.PI / 180 * δϕ;

    var sλ = Math.sin(λ), sϕ = Math.sin(ϕ), sγ = Math.sin(γ),
        sδλ = Math.sin(δλ), sδϕ = Math.sin(δϕ),
        cλ = Math.cos(λ), cϕ = Math.cos(ϕ), cγ = Math.cos(γ),
        cδλ = Math.cos(δλ), cδϕ = Math.cos(δϕ);

    var m00 = -sδλ * sλ * cϕ + (sγ * sλ * sϕ + cγ * cλ) * cδλ,
        m01 = -sγ * cδλ * cϕ - sδλ * sϕ,
        m02 = sδλ * cλ * cϕ - (sγ * sϕ * cλ - sλ * cγ) * cδλ,
        m10 = - sδϕ * sλ * cδλ * cϕ - (sγ * sλ * sϕ + cγ * cλ) * sδλ * sδϕ - (sλ * sϕ * cγ - sγ * cλ) * cδϕ,
        m11 = sδλ * sδϕ * sγ * cϕ - sδϕ * sϕ * cδλ + cδϕ * cγ * cϕ,
        m12 = sδϕ * cδλ * cλ * cϕ + (sγ * sϕ * cλ - sλ * cγ) * sδλ * sδϕ + (sϕ * cγ * cλ + sγ * sλ) * cδϕ,
        m20 = - sλ * cδλ * cδϕ * cϕ - (sγ * sλ * sϕ + cγ * cλ) * sδλ * cδϕ + (sλ * sϕ * cγ - sγ * cλ) * sδϕ,
        m21 = sδλ * sγ * cδϕ * cϕ - sδϕ * cγ * cϕ - sϕ * cδλ * cδϕ,
        m22 = cδλ * cδϕ * cλ * cϕ + (sγ * sϕ * cλ - sλ * cγ) * sδλ * cδϕ - (sϕ * cγ * cλ + sγ * sλ) * sδϕ;

    if (m01 != 0 || m11 != 0) {
        γ_ = Math.atan2(-m01, m11);
        ϕ_ = Math.atan2(-m21, Math.sin(γ_) == 0 ? m11 / Math.cos(γ_) : - m01 / Math.sin(γ_));
        λ_ = Math.atan2(-m20, m22);
    } else {
        γ_ = Math.atan2(m10, m00) - m21 * λ;
        ϕ_ = - m21 * Math.PI / 2;
        λ_ = λ;
    }

    return([λ_ * 180 / Math.PI, ϕ_ * 180 / Math.PI, γ_ * 180 / Math.PI]);
}

var m0 = null,
    o0;

function mousedown() {  // remember where the mouse was pressed, in canvas coords
    m0 = trackballAngles(d3.mouse(svg[0][0]));
    o0 = projection.rotate();
    d3.event.preventDefault();
}

function mousemove() {
    if (m0) {  // if mousedown
        var m1 = trackballAngles(d3.mouse(svg[0][0]));
        o1 = composedRotation(o0[0], o0[1], o0[2], m1[0] - m0[0], m1[1] - m0[1])
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

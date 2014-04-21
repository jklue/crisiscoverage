/* much code from http://bl.ocks.org/mbostock/3795040 and James Lafa @ http://blog.james-lafa.fr/how-to-display-in-10-minutes-worldwide-data-on-a-globe-with-d3-js/*/
var width = 960,
    height = 500;

var projection = d3.geo.orthographic()
    .scale(250)
    .translate([width / 2, height / 2])
    .clipAngle(90);

var path = d3.geo.path()
    .projection(projection);

var λ = d3.scale.linear()
    .domain([0, width])
    .range([-180, 180]);

var φ = d3.scale.linear()
    .domain([0, height])
    .range([90, -90]);

var svg = d3.select("#overviewVis").append("svg")
    .attr("width", width)
    .attr("height", height);

/* lat and lon lines */

  // instantiate meridians and parallels with d3's help
  graticule = d3.geo.graticule()

  // add meridians and parallels to globe
  svg.append('path')
     .datum(graticule)
     .attr('class','graticule')
     .attr('d',path);

/* Mouse control */
  svg.on("mousemove", function() {
    var p = d3.mouse(this);
    projection.rotate([λ(p[0]), φ(p[1])]);
    svg.selectAll("path").attr("d", path);
  });

/* Map and other data */
  // Queue function from MBostock for multiple file loading
  queue().defer(d3.json, "/productiondata/globe.json")
         .defer(d3.csv, "/data/overview/2014-04-12 09.33.11_haiyan-google-country_stats_by_query_name.csv")
         .await(loadedDataCallBack);

  // when all data is loaded
  function loadedDataCallBack(error, world, media) {
    // convert world map data to d3 topo
    var countries = topojson.feature(world, world.objects.countries).features;
    /* convert media data to json object */
      // initialize new js object to hold article by country data in format the rest of the script will understand
      var mediaData = {};
      // iterate through each piece of original country/article data
      media.forEach(function(d){
        // set country name as property value
        mediaData[d.query_url] = {
          // set country name and number of articles written
          name: d.query_url,
          articles: +d.result_count
        };
      });
      console.log('mediaData: ',mediaData);
    // var lifeExpectancyDomain = d3.extent(_.pluck(media, 'result_count'));

    // set domain for coloring, grabbing 'age' from json file using underscore.js
    var lifeExpectancyDomain = d3.extent(_.pluck(mediaData, 'articles'));

console.log(d3.extent(_.pluck(mediaData, 'articles')));

// console.log(lifeExpectancyDomain);
    // set color scale

    colorScale = d3.scale.pow().exponent(0.1)
    // colorScale = d3.scale.linear()
        .domain(lifeExpectancyDomain)
        .interpolate(d3.interpolateRgb)
        .range(["#CCC", "#3D8699"]);

    // draw countries
    return svg.selectAll('.land')
       .data(countries)
       .enter()
         .append('path')
         .attr('class','country')
         .attr('d', path)
         .attr('fill', function(d){
          // if country code has an entry in life expectancy data
          if(_.has(mediaData, d.properties.name)){
          // if(_.has(lifeExpectancy, d.id)){
            // fill country with appropriate life expectancy
console.log(mediaData[d.properties.name].articles);

            return colorScale(mediaData[d.properties.name].articles);
          // else color gray
          } else {
              return 'white';
          }
         });
  }

  // d3.json("/data/overview/orthograph.json", function(error, world) {
  //   svg.append("path")
  //       .datum(topojson.feature(world, world.objects.land))
  //       .attr("class", "land")
  //       .attr("d", path);
  // });

/* Only map data*/
// d3.json("/data/overview/orthograph.json", function(error, world) {
//   svg.append("path")
//       .datum(topojson.feature(world, world.objects.land))
//       .attr("class", "land")
//       .attr("d", path);
// });
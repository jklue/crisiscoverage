/* much code from http://bl.ocks.org/mbostock/3795040 and James Lafa @ http://blog.james-lafa.fr/how-to-display-in-10-minutes-worldwide-data-on-a-globe-with-d3-js/*/
var width = 960,
    height = 500;

var projection = d3.geo.orthographic()
    .scale(250)
    .translate([width / 2, height / 2])
    .clipAngle(90);

var path = d3.geo.path()
    .projection(projection);

var svg = d3.select("#overviewVis").append("svg")
    .attr("width", width)
    .attr("height", height)
    .on("mousedown", mousedown)
    .on("mousemove", mousemove)
    .on("mouseup", mouseup);

/* lat and lon lines */

  // instantiate meridians and parallels with d3's help
  graticule = d3.geo.graticule();

  // add meridians and parallels to globe
  svg.append('path')
     .datum(graticule)
     .attr('class','graticule')
     .attr('d',path);

/* Map and other data */
  // Queue function from MBostock for multiple file loading
  queue().defer(d3.json, "/productiondata/globe.json")
         .defer(d3.csv, "/productiondata/haiyan/google-country_stats.csv")
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

    // set domain for coloring, grabbing 'age' from json file using underscore.js
    var lifeExpectancyDomain = d3.extent(_.pluck(mediaData, 'articles'));
    console.log('lifeExpectancyDomain: ',lifeExpectancyDomain);

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

//
function trackballAngles(pt) {
    // based on http://www.opengl.org/wiki/Trackball
    // given a click at (x,y) in canvas coords on the globe (trackball),
    // calculate the spherical coordianates for the point as a rotation around
    // the vertical and horizontal axes

    var r = projection.scale();
    var c = projection.translate();
    var x = pt[0] - c[0], y = - (pt[1] - c[1]), ss = x*x + y*y;


    var z = r*r > 2 * ss ? Math.sqrt(r*r - ss) : r*r / 2 / Math.sqrt(ss);

    var lambda = Math.atan2(x, z) * 180 / Math.PI;
    var phi = Math.atan2(y, z) * 180 / Math.PI
    return [lambda, phi];
}

/*
 This is the cartesian equivalent of the rotation matrix,
 which is the product of the following rotations (in numbered order):
 1. longitude: λ around the y axis (which points up in the canvas)
 2. latitude: -ϕ around the x axis (which points right in the canvas)
 3. yaw:       γ around the z axis (which points out of the screen)

 NB.  If you measure rotations in a positive direction according to the right-hand rule
 (point your right thumb in the positive direction of the rotation axis, and rotate in the
 direction of your curled fingers), then the latitude rotation is negative.

 R(λ, ϕ, γ) =
 [[ sin(γ)sin(λ)sin(ϕ)+cos(γ)cos(λ), −sin(γ)cos(ϕ), −sin(γ)sin(ϕ)cos(λ)+sin(λ)cos(γ)],
 [ −sin(λ)sin(ϕ)cos(γ)+sin(γ)cos(λ), cos(γ)cos(ϕ), sin(ϕ)cos(γ)cos(λ)+sin(γ)sin(λ)],
 [ −sin(λ)cos(ϕ),                    −sin(ϕ),       cos(λ)cos(ϕ)]]

 If you then apply a "trackball rotation" of δλ around the y axis, and -δϕ around the
 x axis, you get this horrible composite matrix:

 R2(λ, ϕ, γ, δλ, δϕ) =
 [[−sin(δλ)sin(λ)cos(ϕ)+(sin(γ)sin(λ)sin(ϕ)+cos(γ)cos(λ))cos(δλ),
 −sin(γ)cos(δλ)cos(ϕ)−sin(δλ)sin(ϕ),
 sin(δλ)cos(λ)cos(ϕ)−(sin(γ)sin(ϕ)cos(λ)−sin(λ)cos(γ))cos(δλ)],
 [−sin(δϕ)sin(λ)cos(δλ)cos(ϕ)−(sin(γ)sin(λ)sin(ϕ)+cos(γ)cos(λ))sin(δλ)sin(δϕ)−(sin(λ)sin(ϕ)cos(γ)−sin(γ)cos(λ))cos(δϕ),
 sin(δλ)sin(δϕ)sin(γ)cos(ϕ)−sin(δϕ)sin(ϕ)cos(δλ)+cos(δϕ)cos(γ)cos(ϕ),
 sin(δϕ)cos(δλ)cos(λ)cos(ϕ)+(sin(γ)sin(ϕ)cos(λ)−sin(λ)cos(γ))sin(δλ)sin(δϕ)+(sin(ϕ)cos(γ)cos(λ)+sin(γ)sin(λ))cos(δϕ)],
 [−sin(λ)cos(δλ)cos(δϕ)cos(ϕ)−(sin(γ)sin(λ)sin(ϕ)+cos(γ)cos(λ))sin(δλ)cos(δϕ)+(sin(λ)sin(ϕ)cos(γ)−sin(γ)cos(λ))sin(δϕ),
 sin(δλ)sin(γ)cos(δϕ)cos(ϕ)−sin(δϕ)cos(γ)cos(ϕ)−sin(ϕ)cos(δλ)cos(δϕ),
 cos(δλ)cos(δϕ)cos(λ)cos(ϕ)+(sin(γ)sin(ϕ)cos(λ)−sin(λ)cos(γ))sin(δλ)cos(δϕ)−(sin(ϕ)cos(γ)cos(λ)+sin(γ)sin(λ))sin(δϕ)]]

 by equating components of the matrics
 (label them [[a00, a01, a02], [a10, a11, a12], [a20, a21, a22]])
 we can find an equivalent rotation R(λ', ϕ', γ') == RC(λ, ϕ, γ, δλ, δϕ) :

 if cos(ϕ') != 0:
 γ' = atan2(-RC01, RC11)
 ϕ' = atan2(-RC21, γ' == 0 ? RC11 / cos(γ') : - RC01 / sin(γ'))
 λ' = atan2(-RC20, RC22)
 else:
 // when cos(ϕ') == 0, RC21 == - sin(ϕ') == +/- 1
 // the solution is degenerate, requiring just that
 //    γ' - λ' = atan2(RC00, RC10) if RC21 == -1 (ϕ' = π/2)
 // or γ' + λ' = atan2(RC00, RC10) if RC21 == 1 (ϕ' = -π/2)
 // so choose:
 γ' = atan2(RC10, RC00) - RC21 * λ
 ϕ' = - RC21 * π/2
 λ' = λ

 */

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
        // we want to find rotate the current projection so that the point at m0 rotates to m1
        // along the great circle arc between them.
        // when the current projection is at rotation(0,0), with the north pole aligned
        // to the vertical canvas axis, and the equator aligned to the horizontal canvas
        // axis, this is easy to do, since D3's longitude rotation corresponds to trackball
        // rotation around the vertical axis, and then the subsequent latitude rotation
        // corresponds to the trackball rotation around the horizontal axis.
        // But if the current projection is already rotated, it's harder.
        // We need to find a new rotation equivalent to the composition of both

        // Choose one of these three update schemes:

        // Best behavior
        o1 = composedRotation(o0[0], o0[1], o0[2], m1[0] - m0[0], m1[1] - m0[1])

        // Improved behavior over original example
        //o1 = [o0[0] + (m1[0] - m0[0]), o0[1] + (m1[1] - m0[1])];

        // Original example from http://mbostock.github.io/d3/talk/20111018/azimuthal.html
        // o1 = [o0[0] - (m0[0] - m1[0]) / 8, o0[1] - (m1[1] - m0[1]) / 8];

        // move to the updated rotation
        projection.rotate(o1);

        // We can optionally update the "origin state" at each step.  This has the
        // advantage that each 'trackball movement' is small, but the disadvantage of
        // potentially accumulating many small drifts (you often see a twist creeping in
        // if you keep rolling the globe around with the mouse button down)
//    o0 = o1;
//    m0 = m1;

        svg.selectAll("path").attr("d", path);
    }
}

function mouseup() {
    if (m0) {
        mousemove();
        m0 = null;
    }
}

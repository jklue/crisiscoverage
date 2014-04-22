var diameter = 320,
    radius = diameter >> 1,
    velocity = .01,
    then = Date.now();

var projection = d3.geo.orthographic()
    .scale(radius - 2)
    .translate([radius, radius])
    .clipAngle(90)
    .precision(0);

var canvas = d3.select("#rotatingGlobe").selectAll("canvas")
    .data(d3.range(1))
  .enter().append("canvas")
    .attr("width", diameter)
    .attr("height", diameter);

var path = d3.geo.path()
    .projection(projection);

d3.json("/productiondata/rotatingglobe.json", function(error, world) {
  var land = topojson.feature(world, world.objects.land),
      globe = {type: "Sphere"};

  d3.timer(function() {
    var angle = velocity * (Date.now() - then);
    canvas.each(function(i) {
      var rotate = [0, 0, 0], context = this.getContext("2d");
      rotate[i] = angle, projection.rotate(rotate);
      context.clearRect(0, 0, diameter, diameter);
      context.beginPath(), path.context(context)(land), context.fill();
      context.beginPath(), path(globe), context.stroke();
    });
  });
});

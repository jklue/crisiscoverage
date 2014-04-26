/*
 * Base globe code from http://bl.ocks.org/mbostock/4183330
 */
var globe, land, borders, countries;
var lastI = 0, i = -1, n = 0;

var diameter = 320,
    radius = diameter >> 1,
    width = diameter,
    height = diameter;

var projection = d3.geo.orthographic()
    .scale(radius - 2)
    .translate([radius, radius])
    .clipAngle(90)
    .precision(0);

var canvas = d3.select("#rotatingGlobe").append("canvas")
    .attr("width", width)
    .attr("height", height);

var c = canvas.node().getContext("2d");

var path = d3.geo.path()
    .projection(projection)
    .context(c);

var title = d3.select("#crisis_name");

queue()
    .defer(d3.json, "productiondata/world-110m.json")
    .defer(d3.tsv, "productiondata/crisis-country-names-alt.tsv")
    .await(ready);

function transition() {
    d3.transition()
        .delay(function(){
//            console.log("lastI: "+lastI+", i: "+i+", n: "+n);
            if ((lastI+1) === n){
                lastI = 0;
                return 5000;
            }
            lastI++;
            return 0;
        })
        .duration(1250)
        .each("start", function () {
            title.text(countries[i = (i + 1) % n].name);
        })
        .tween("rotate", function () {
            var p = d3.geo.centroid(countries[i]),
                r = d3.interpolate(projection.rotate(), [-p[0], -p[1]]);
            return function (t) {
                projection.rotate(r(t));
                c.clearRect(0, 0, width, height);
                c.fillStyle = "#bbb", c.beginPath(), path(land), c.fill();
                // crisis country
                c.fillStyle = "#CC1452", c.beginPath(), path(countries[i]), c.fill();
                // country border
                c.strokeStyle = "#fff", c.lineWidth = .5, c.beginPath(), path(borders), c.stroke();
                // globe outline
                c.strokeStyle = "#ccc", c.lineWidth = 2, c.beginPath(), path(globe), c.stroke();
            };
        })
        .transition()
        .each("end", transition)
    return false;
}


    function ready(error, world, names) {
    globe = {type: "Sphere"};
    land = topojson.feature(world, world.objects.land);
    borders = topojson.mesh(world, world.objects.countries, function(a, b) { return a !== b; });
    countries = topojson.feature(world, world.objects.countries).features;

    countries = countries.filter(function(d) {
        return names.some(function(n) {
            if (d.id == n.id) return d.name = n.name;
        });
    }).sort(function(a, b) {
        return a.name.localeCompare(b.name);
    });

        lastI = 0;
        i = -1;
        n =countries.length;
        setInterval(
            (function transition() {
                d3.transition()
                    .delay(function(){
//            console.log("lastI: "+lastI+", i: "+i+", n: "+n);
                        if ((lastI+1) === n){
                            lastI = 0;
                            return 5000;
                        }
                        lastI++;
                        return 0;
                    })
                    .duration(1250)
                    .each("start", function () {
                        title.text(countries[i = (i + 1) % n].name);
                    })
                    .tween("rotate", function () {
                        var p = d3.geo.centroid(countries[i]),
                            r = d3.interpolate(projection.rotate(), [-p[0], -p[1]]);
                        return function (t) {
                            projection.rotate(r(t));
                            c.clearRect(0, 0, width, height);
                            c.fillStyle = "#bbb", c.beginPath(), path(land), c.fill();
                            // crisis country
                            c.fillStyle = "#CC1452", c.beginPath(), path(countries[i]), c.fill();
                            // country border
                            c.strokeStyle = "#fff", c.lineWidth = .5, c.beginPath(), path(borders), c.stroke();
                            // globe outline
                            c.strokeStyle = "#ccc", c.lineWidth = 1, c.beginPath(), path(globe), c.stroke();
                        };
                    })
                    .transition()
                    .each("end", transition);
            })(),20000);//As long as the number is longer than the actual transition, then you are fine ...
    }




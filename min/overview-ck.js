function renderGlobe(){country.attr("fill",function(t){return _.has(mediaData,t.properties.name)&&mediaData[t.properties.name].articles>0?color(mediaData[t.properties.name].articles):"white"}),$("#legend_globe").empty(),colorlegend("#legend_globe",color,"quantile",{title:"results by country",boxHeight:15,boxWidth:30,fill:!1,linearBoxes:5}),done||startAnimation()}function stopAnimation(){done=!0}function startAnimation(){done=!1,then=Date.now(),d3.timer(function(){var t=velocity*(Date.now()-then);return projection.rotate([t,0,0]),svg.selectAll("path").attr("d",path.projection(projection)),done})}function mousedown(){done?Date.now()-lastClick<500&&startAnimation():stopAnimation(),lastClick=Date.now(),m0=[d3.event.pageX,d3.event.pageY],o0=projection.rotate(),d3.event.preventDefault()}function mousemove(){if(m0){var t=[d3.event.pageX,d3.event.pageY],e=[o0[0]+(t[0]-m0[0])/8,0];projection.rotate(e),svg.selectAll("path").attr("d",path)}}function mouseup(){m0&&(mousemove(),m0=null)}function colorCountry(t,e){t&&e?(country_map.attr("opacity",function(t){if(lastColorClicked&&lastColorClicked===e)return 1;var a=color(0);if(t&&_.has(mediaData,t.properties.name)){var n=mediaData[t.properties.name].articles;n&&!isNaN(n)&&n>0&&(a=color(n))}return a===e?1:.3}),lastColorClicked=e):lastColorClicked=void 0,country_map.transition().duration(250).style("fill",function(t){if(!t||!_.has(mediaData,t.properties.name))return"white";var e=mediaData[t.properties.name].articles;return e&&!isNaN(e)&&e>0?color(e):void 0})}function renderMap(){colorCountry(!1,null),$("#legend_map").empty(),colorlegend("#legend_map",color,"quantile",{title:"results by country",boxHeight:15,boxWidth:30,fill:!1,linearBoxes:5})}function renderBarChart(){isCountrySortDescending=!0,isResultSortDescending=!1;var t=_.pluck(mediaBarData,"name");xScale.domain([min,max]),yScale.domain(t),$("#country_bar_chart").empty(),svgBar=d3.select("#country_bar_chart").append("svg").attr("width",widthBar+marginBar.left+marginBar.right).attr("height",3e3),gBar=svgBar.append("g").attr("transform","translate("+marginBar.left+","+marginBar.top+")"),groups=gBar.append("g").selectAll("g").data(mediaBarData).enter().append("g").attr("id",function(t){return"bar_group_"+t.code}).attr("transform",function(t){return"translate("+countryWidth+", "+yScale(t.name)+")"}),bars=groups.append("rect").attr("class","bar_rect").attr("id",function(t){return"bar_rect_"+t.code}).attr("width",function(t){return xScale(t.articles)}).attr("height",bar_height-bar_padding).style("fill",function(t){return color(t.articles)}).on("mouseover",function(t){fill(t,!0)}).on("mouseout",function(t){fill(t,!1)}),groups.append("text").attr("class","bar_text").attr("id",function(t){return"bar_text_country_"+t.code}).attr("x","-"+x_pad).attr("dy",y_em).style("fill",defaultBarTextColor).text(function(t){return t.name}),groups.append("g").append("text").attr("class","bar_text").attr("id",function(t){return"bar_text_result_"+t.code}).attr("x",function(t){var e=xScale(t.articles)-x_pad;return 0>e?5:e}).attr("dy",y_em).style("fill",function(t){var e=color(t.articles);return e===myColors.Greens[2]?lightBarTextColor:defaultBarTextColor}).text(function(t){var e=xScale(t.articles)-x_pad;return 0>e?"0":t.articles}),reorder("result")}function fill(t,e){var a=d3.select("#bar_group_"+t.code);null!=a&&(e?(a.select("rect").style("fill",hoverColor),a.select("text").style("fill",hoverColor)):(a.select("rect").style("fill",color(t.articles)),a.select("text").style("fill",defaultBarTextColor)))}function reorder(t){groups.sort(function(e,a){return"country"===t?isCountrySortDescending?d3.ascending(e.name,a.name):d3.descending(e.name,a.name):internalNumberSort(e,parseInt(e.articles),a,parseInt(a.articles),isResultSortDescending)}),"country"===t?(console.log("... reorder for 'country', descending? "+isCountrySortDescending),isCountrySortDescending=isCountrySortDescending?!1:!0,isResultSortDescending=!1):(console.log("... reordered for 'result', descending? "+isResultSortDescending),isResultSortDescending=isResultSortDescending?!1:!0,isCountrySortDescending=!0),groups.transition().duration(750).delay(function(t,e){return 10*e}).attr("transform",function(t,e){return"translate("+countryWidth+", "+e*bar_height+")"})}function internalNumberSort(t,e,a,n,r){return r?isNaN(e)||isNaN(n)||e!=n?d3.ascending(e,n):d3.ascending(t.name,a.name):isNaN(e)||isNaN(n)||e!=n?d3.descending(e,n):d3.ascending(t.name,a.name)}function renderCrisesCompared(){var t={top:1,right:1,bottom:6,left:1},e=940-t.left-t.right,a=550-t.top-t.bottom,n=function(t){var e;return e=0===t?"Rank > 10":"Rank: "+t,console.log(e),e},r=d3.scale.category20(),o=d3.select("#countrySankeyVis").append("svg").attr("width",e+t.left+t.right).attr("height",a+t.top+t.bottom).append("g").attr("transform","translate("+t.left+","+t.top+")"),i=d3.sankey().nodeWidth(15).nodePadding(10).size([e,a]),l=i.link();d3.json("/productiondata/compared_overview.json",function(t){function s(t){d3.select(this).attr("transform","translate("+t.x+","+(t.y=Math.max(0,Math.min(a-t.dy,d3.event.y)))+")"),i.relayout(),d.attr("d",l)}i.nodes(t.nodes).links(t.links).layout(32);var d=o.append("g").selectAll(".link").data(t.links).enter().append("path").attr("class","link").attr("d",l).style("stroke-width",function(t){return Math.max(0,t.dy)}).sort(function(t,e){return e.dy-t.dy});d.append("title").text(function(t){return t.source.name+" → "+t.target.name+"\n"+n(t.rank)});var c=o.append("g").selectAll(".node").data(t.nodes).enter().append("g").attr("class","node").attr("transform",function(t){return"translate("+t.x+","+t.y+")"}).call(d3.behavior.drag().origin(function(t){return t}).on("dragstart",function(){this.parentNode.appendChild(this)}).on("drag",s));c.append("rect").attr("height",function(t){return t.dy}).attr("width",i.nodeWidth()).style("fill",function(t){return t.color=r(t.name.replace(/ .*/,""))}).style("stroke",function(t){return d3.rgb(t.color).darker(2)}).append("title").text(function(t){return t.name+"\n"+n(t.rank)}),c.append("text").attr("x",-6).attr("y",function(t){return t.dy/2}).attr("dy",".35em").attr("text-anchor","end").attr("transform",null).text(function(t){return t.name}).filter(function(t){return t.x<e/2}).attr("x",6+i.nodeWidth()).attr("text-anchor","start")})}function resetSummary(){d3.select("#crisisTitle").data(summary).html(function(t){return"<h3>"+t.title+"</h3>"}),d3.select("#crisisStory").data(summary).html(function(t){return"<p>"+t.content+"</p>"})}function loadedDataCallBack(t,e,a,n){console.log("--- START ::: loadedDataCallback ---"),mediaData={},mediaBarData=[],world_data=topojson.feature(e,e.objects.countries).features,a.forEach(function(t){var e=t.query_distinct.slice(t.query_distinct.indexOf("(")+1,t.query_distinct.lastIndexOf(")")),a=t.query_distinct.slice(0,t.query_distinct.indexOf("(")-1),n="CS"===a?1:+t.raw_result_count;mediaData[e]={name:e,code:a,articles:n},mediaBarData.push({name:e.replaceAll("&quot;",""),code:a,articles:n})}),console.log("mediaData: ",mediaData);var r=_.pluck(mediaData,"articles");min=d3.min(r),mean=d3.sum(r)/r.length,max=d3.max(r),color=d3.scale.quantile().domain([min,mean,max]).range(colorScale),$("#countries").empty(),country=countries.selectAll(".country").data(world_data),country.enter().append("path").attr("class","country").attr("d",path).on("mouseover",tip.show).on("mousemove",function(){return tip.style("top",d3.event.pageY+16+"px").style("left",d3.event.pageX+16+"px")}).on("mouseout",tip.hide),country.call(tip),$("#countries_map").empty(),country_map=countries_map.selectAll(".country").data(world_data),country_map.enter().append("path").attr("class","country").attr("d",pathMap).on("mouseover",tip.show).on("mousemove",function(){return tip.style("top",d3.event.pageY+16+"px").style("left",d3.event.pageX+16+"px")}).on("mouseout",tip.hide).on("click",function(t){console.log(t);var e=mediaData[t.properties.name].articles;if(!isNaN(e)){var a=color(e);colorCountry(!0,a)}}),country_map.call(tip),renderGlobe(),renderMap(),renderBarChart(),console.log("--- END ::: loadedDataCallback ---")}var color,min,mean,max,mediaData={},world_data,tip=d3.tip().attr("class","d3-tip none").offset([-10,0]).html(function(t){var e,a="white";return t&&mediaData[t.properties.name]&&!isNaN(mediaData[t.properties.name].articles)?(a=color(mediaData[t.properties.name].articles),e=numberWithCommas(mediaData[t.properties.name].articles)):e="(no data)","<strong>Country: </strong><span style='color:"+a+";'><em>"+t.properties.name+"</em></span><span style='color:white;'>, </span><strong>Indicator Value: </strong><span style='color:"+a+";'><em>"+e+"</em></span>"}),mediaBarData=[],isCountrySortDescending=!0,isResultSortDescending=!1,maxWidth=940,maxHeight=650,width=maxWidth,height=maxHeight-150,margin={top:0,right:0,bottom:0,left:0},myColors={Blues:["#deebf7","#4292c6"],Greens:["#74c476","#41ab5d","#238b45"]},colorScale=myColors.Blues.concat(myColors.Greens),country,svg=d3.select("#overviewGlobe").append("svg").attr({width:width+margin.left+margin.right,height:height+margin.top+margin.bottom,transform:"translate("+margin.left+","+margin.top+")"}).on("mousedown",mousedown).on("mousemove",mousemove).on("mouseup",mouseup),countries=svg.append("g").attr({id:"countries",width:width,height:height}),projection=d3.geo.orthographic().scale(250).translate([width/2,height/2]).clipAngle(90),path=d3.geo.path().projection(projection),graticule=d3.geo.graticule();svg.append("g").append("path").datum(graticule).attr("class","graticule").attr("d",path);var m0,o0,done,shouldResume,velocity=.02,then=Date.now(),lastClick=0,lastColorClicked,country_map,svgMap=d3.select("#overviewMap").append("svg").attr({width:width+margin.left+margin.right,height:height+margin.top+margin.bottom,transform:"translate("+margin.left+","+margin.top+")"}),countries_map=svgMap.append("g").attr({id:"countries_map",width:width,height:height}),pathMap=d3.geo.path().projection(d3.geo.mercator().translate([width/2,height/2])),defaultBarTextColor="#636363",lightBarTextColor="#bdbdbd",hoverColor="orangered",marginBar={top:50,bottom:0,left:0,right:50},widthBar=maxWidth-marginBar.left-marginBar.right,heightBar=maxHeight-marginBar.top-marginBar.bottom,countryWidth=250,xScale=d3.scale.pow().exponent(.1).range([0,widthBar-countryWidth]),yScale=d3.scale.ordinal().rangeRoundBands([0,heightBar],.8,0),bar_height=15,bar_padding=1,x_pad=5,y_em=".95em",groups=null,bars=null,svgBar,gBar;addClassNameListener("crisis_select",function(){var t=window.crisis_select.value;console.log("### QUEUE NEW CRISIS ("+t+") AFTER CLASS CHANGE ###"),queue().defer(d3.json,"productiondata/globe.json").defer(d3.csv,"/productiondata/"+t+"/google-country_stats.csv").defer(d3.csv,"/productiondata/"+t+"/summary.csv").await(loadedDataCallBack)}),d3.selectAll("input").on("click",function(){reorder(this.value)}),$(document).ready(function(){document.getElementById("tab_1_compared").className="content-tab active",addClassNameListener("tab_1_compared",function(){var t=document.getElementById("tab_1_compared").className;"content-tab active"===t&&(console.log("... tab change to tab_1_compared."),done?shouldResume=!1:(stopAnimation(),shouldResume=!0))}),addClassNameListener("tab_2_globe",function(){var t=document.getElementById("tab_2_globe").className;"content-tab active"===t&&(console.log("... tab change to tab_2_globe."),shouldResume&&startAnimation()),$("#legend_globe").empty(),colorlegend("#legend_globe",color,"quantile",{title:"results by country",boxHeight:15,boxWidth:30,fill:!1,linearBoxes:5}),done||startAnimation()}),addClassNameListener("tab_3_map",function(){var t=document.getElementById("tab_3_map").className;"content-tab active"===t&&(console.log("... tab change to tab_3_map."),done?shouldResume=!1:(stopAnimation(),shouldResume=!0),$("#legend_map").empty(),colorlegend("#legend_map",color,"quantile",{title:"results by country",boxHeight:15,boxWidth:30,fill:!1,linearBoxes:5}))}),addClassNameListener("tab_4_bar",function(){var t=document.getElementById("tab_4_bar").className;"content-tab active"===t&&(console.log("... tab change to tab_4_bar."),done?shouldResume=!1:(stopAnimation(),shouldResume=!0))}),renderCrisesCompared()});
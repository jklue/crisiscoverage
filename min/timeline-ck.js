function getData(t,e,a){allDates=[],aggregateMediaStats=[],dateList=[],d3.selectAll(".mediaSources").remove(),d3.selectAll(".x.axis").remove(),d3.selectAll(".y.axis").remove(),d3.selectAll(".x.axis2").remove(),d3.selectAll(".y.axis2").remove(),d3.selectAll(".storyline").remove(),d3.selectAll(".storyTriangle").remove(),storyPoints=e,storyPoints.forEach(function(t){t.date=parseStorypoint(t.date)});var r=a.map(function(t){return t.site_name});sources=[],r.forEach(function(t,e){0===e?sources.push(t):-1==sources.indexOf(t)&&sources.push(t)}),a.forEach(function(t){t.date_query_end=parseDateQuery(t.date_query_end),dateList.push(t.date_query_end)});var n=[];sources.forEach(function(t,e){var r=[];r.name=t;var n=t.replace(/[\. ]+/g,"");r.id=n,r.vis=1,r.values=[],a.forEach(function(e){t==e.site_name&&r.values.push({date:e.date_query_end,count:+e.raw_result_count,name:t,type:e.site_type,id:n,vis:1})}),allDates.push(r)}),color=d3.scale.category20();var i=[];return allDates[0].values.forEach(function(t){i.push(t.date)}),mediaTypes.forEach(function(t){var e=[];e.name=t,e.values=[],i.forEach(function(r){var n=0;a.forEach(function(e){t==e.site_type&&r.getTime()==e.date_query_end.getTime()&&(n+=+e.raw_result_count)}),e.values.push({date:r,count:n,name:t})}),aggregateMediaStats.push(e)}),typeVis()}function typeVis(){var t=35;xScale=d3.time.scale().domain(d3.extent(dateList,function(t){return t})).range([t,typeDetail.w]),yScale=d3.scale.linear().domain([0,d3.max(aggregateMediaStats,function(t){return d3.max(t.values,function(t){return t.count})})]).range([typeDetail.h,0]);var e=typeSVG.append("g").attr({"class":"typeFrame",transform:"translate("+typeDetail.x+","+typeDetail.y+")"});xAxis=d3.svg.axis().scale(xScale).orient("bottom").ticks(4).tickFormat(d3.time.format("%b")),yAxis=d3.svg.axis().scale(yScale).orient("left").ticks(6);var a=d3.svg.line().x(function(t){return xScale(t.date)}).y(function(t){return yScale(t.count)}).interpolate("linear");e.append("g").attr({"class":"x axis",transform:"translate(0,"+typeDetail.h+")"}).call(xAxis),d3.select(".x.axis").append("line").attr({x1:40,x2:0,y1:3,y2:3}).style({stroke:"#ccc","stroke-width":6}),e.append("g").attr("class","y axis").call(yAxis).append("text").attr("x",-5).attr("y",-45).attr("dy","30px").style("text-anchor","end").text("# articles");var r=aggregateMediaStats,n=typeSVG.append("g").attr("clip-path","url(#chart-area)"),i=n.selectAll(".mediaSources").data(r).enter().append("g").attr("class","mediaSources");i.append("path").attr({"class":function(t){return t.id+" path"},id:function(t){return t.name},d:function(t){return a(t.values)},transform:"translate(0,"+typeDetail.y+")"}).style({stroke:function(t){return color(t.name)},fill:"none"}),i.selectAll("circle").data(function(t){return t.values}).enter().append("circle").attr({"class":function(t){return t.id+" dot"},cx:function(t){return xScale(t.date)},cy:function(t){return yScale(t.count)},r:4,transform:"translate(0,"+typeDetail.y+")"}).style({fill:function(t){var e=color(t.name),a=d3.rgb(e).darker();return a}}).on("mouseover",function(t){d3.select(this).transition().duration(25).attr("r",10),s.show(t)}).on("mouseleave",function(t){d3.select(this).transition().duration(25).attr("r",4),s.hide(t)}),i.append("text").attr({"class":function(t){return t.id+"legend"},x:typeWidth+120,y:function(t,e){return 16*e+typeMargin.top},dy:"0.35em",fill:function(t){return color(t.name)}}).style("text-anchor","end").text(function(t){return t.name}),d3.select("#crisisTitle").html("<h3>Typhoon Haiyan</h3>"),d3.select("#crisisStory").html('Typhoon Haiyan, known as Typhoon Yolanda in the Philippines, was a powerful tropical cyclone that devastated portions of Southeast Asia, particularly the Philippines, on November 8, 2013. <a href="http://en.wikipedia.org/wiki/Typhoon_Haiyan" class="storySource">&mdash; Wikipedia</a>'),e.selectAll(".line").data(storyPoints).enter().append("line").attr({"class":"storyline",x1:function(t){return xScale(t.date)},x2:function(t){return xScale(t.date)},y1:-typeDetail.y,y2:typeDetail.h}),e.selectAll(".storyTriangle").data(storyPoints).enter().append("path").attr({"class":"storyTriangle",transform:function(t){return"translate("+xScale(t.date)+","+-typeDetail.y+")"},d:d3.svg.symbol().type("triangle-down").size(256)}).on("mouseover",function(t){var e=parseDateTips(t.date);d3.select(".crisisDate").remove(),d3.select("#crisisTitle").append("span").html(e).attr("class","crisisDate"),d3.select("#crisisStory").html(t.title),d3.selectAll(".storyTriangle").transition().style("fill","#919191").attr("d",d3.svg.symbol().type("triangle-down").size(256)),d3.select(this).transition().style("fill","#3D8699").attr("d",d3.svg.symbol().type("triangle-down").size(512))});var s=d3.tip().html(function(t){return t.count+" <span class='sourceName'>"+t.name+"</span> articles on during<br>the 30 days prior to "+parseDateTips(t.date)}).direction("e").attr("class","d3-tip e");e.call(s),sourceVis()}function sourceVis(){var t=35;xScale2=d3.time.scale().domain(d3.extent(dateList,function(t){return t})).range([t,sourceDetail.w]),yScale2=d3.scale.linear().domain([0,d3.max(allDates,function(t){return d3.max(t.values,function(t){return t.count})})]).range([sourceDetail.h,0]);var e=sourceSVG.append("g").attr({"class":"sourceFrame",transform:"translate("+sourceDetail.x+","+sourceDetail.y+")"}),a=d3.svg.axis().scale(xScale2).orient("bottom").ticks(4).tickFormat(d3.time.format("%b"));yAxis2=d3.svg.axis().scale(yScale2).orient("left").ticks(6),line=d3.svg.line().x(function(t){return xScale2(t.date)}).y(function(t){return yScale2(t.count)}).interpolate("linear"),e.append("g").attr({"class":"x axis2",transform:"translate(0,"+sourceDetail.h+")"}).call(a),d3.select(".x.axis2").append("line").attr({x1:40,x2:0,y1:3,y2:3}).style({stroke:"#ccc","stroke-width":6}),e.append("g").attr("class","y axis2").call(yAxis2).append("text").attr("x",-5).attr("y",-45).attr("dy","30px").style("text-anchor","end").text("# articles"),visibleDates=allDates.map(function(t){return{id:t.id,name:t.name,values:t.values.map(function(t){return{count:t.count,date:t.date,id:t.id,name:t.name,type:t.type,vis:t.vis}}),vis:t.vis}});var r=sourceSVG.append("g").attr("clip-path","url(#chart-area)");mediaSources=r.selectAll(".mediaSources").data(visibleDates).enter().append("g").attr("class","mediaSources"),mediaSources.append("path").attr({"class":function(t){return t.id+" path"},d:function(t){return line(t.values)},transform:"translate(0,"+sourceDetail.y+")"}).style({stroke:function(t){return color(t.name)},fill:"none"}),mediaSources.selectAll("circle").data(function(t){return t.values}).enter().append("circle").attr({"class":function(t){return t.id+" dot"},cx:function(t){return xScale2(t.date)},cy:function(t){return yScale2(t.count)},r:4,transform:"translate(0,"+sourceDetail.y+")"}).style({fill:function(t){var e=color(t.name),a=d3.rgb(e).darker();return a}}).on("mouseover",function(t){d3.select(this).transition().duration(25).attr("r",10),n.show(t)}).on("mouseleave",function(t){d3.select(this).transition().duration(25).attr("r",4),n.hide(t)}),mediaSources.append("text").attr({"class":function(t){return t.id+"legend"},x:sourceWidth+160,y:function(t,e){return 16*e+8},dy:"0.35em",cursor:"pointer",fill:function(t){return color(t.name)}}).style("text-anchor","end").text(function(t){return t.name}).on("click",function(t){"#ccc"!=d3.select(this).attr("fill")?(d3.select(this).attr("fill","#ccc"),visibleDates.forEach(function(e,a){e.id==t.id&&(e.vis=0,e.values.forEach(function(t){t.count=null,t.vis=0}))})):(d3.select(this).attr("fill",function(t){return color(t.name)}),visibleDates.forEach(function(e,a){e.id==t.id&&allDates.forEach(function(t){t.id==e.id&&(e.vis=1,e.values=t.values.map(function(t){return{count:t.count,date:t.date,id:t.id,name:t.name,type:t.type}}))})})),yScale2.domain([0,d3.max(visibleDates,function(t){return d3.max(t.values,function(t){return t.count})})]),d3.select(".y.axis2").transition().duration(1500).ease("sin-in-out").call(yAxis2),mediaSources.selectAll("path").transition().duration(500).attr("d",function(t){return line(t.values)}),mediaSources.selectAll("circle").data(function(t){return t.values}).transition().duration(500).attr({cx:function(t){return xScale2(t.date)},cy:function(t){return yScale2(t.count)}}).style({fill:function(t){if(0==t.vis)return"white";var e=color(t.name),a=d3.rgb(e).darker();return a}})}),mediaSources.append("foreignObject").attr({width:20,height:20,x:sourceWidth+170,y:function(t,e){return 16*e}}).append("xhtml:input").attr({id:function(t){return t.id},type:"checkbox",value:function(t){return t},checked:!0}).on("click",function(t){var e=d3.select(this);labelClick(e)}),d3.select("#crisisTitle").html("<h3>Typhoon Haiyan</h3>"),d3.select("#crisisStory").html('Typhoon Haiyan, known as Typhoon Yolanda in the Philippines, was a powerful tropical cyclone that devastated portions of Southeast Asia, particularly the Philippines, on November 8, 2013. <a href="http://en.wikipedia.org/wiki/Typhoon_Haiyan" class="storySource">&mdash; Wikipedia</a>'),e.selectAll(".line").data(storyPoints).enter().append("line").attr({"class":"storyline",x1:function(t){return xScale2(t.date)},x2:function(t){return xScale2(t.date)},y1:-sourceDetail.y,y2:sourceDetail.h}),e.selectAll(".storyTriangle").data(storyPoints).enter().append("path").attr({"class":"storyTriangle",transform:function(t){return"translate("+xScale2(t.date)+","+-sourceDetail.y+")"},d:d3.svg.symbol().type("triangle-down").size(256)}).on("mouseover",function(t){var e=parseDateTips(t.date);d3.select(".crisisDate").remove(),d3.select("#crisisTitle").append("span").html(e).attr("class","crisisDate"),d3.select("#crisisStory").html(t.title),d3.selectAll(".storyTriangle").transition().style("fill","#919191").attr("d",d3.svg.symbol().type("triangle-down").size(256)),d3.select(this).transition().style("fill","#3D8699").attr("d",d3.svg.symbol().type("triangle-down").size(512))});var n=d3.tip().html(function(t){return t.count+" articles on <span class='sourceName'>"+t.name+"</span> during<br>the 30 days prior to "+parseDateTips(t.date)}).direction("e").attr("class","d3-tip e");e.call(n);var i=d3.select("#Google");i[0][0].checked=!1,labelClick(i)}function labelClick(t){var e=t[0][0].checked,a=t.data()[0];visibleDates.forEach(0==e?function(t,e){t.id==a.id&&(console.log("matches!"),t.vis=0,t.values.forEach(function(t){t.count=null}))}:function(t,e){t.id==a.id&&allDates.forEach(function(e){e.id==t.id&&(t.vis=1,t.values=e.values.map(function(t){return{count:t.count,date:t.date,id:t.id,name:t.name,type:t.type}}))})}),yScale2.domain([0,d3.max(visibleDates,function(t){return d3.max(t.values,function(t){return t.count})})]),d3.select(".y.axis2").transition().duration(1500).ease("sin-in-out").call(yAxis2),mediaSources.selectAll("path").transition().duration(500).attr("d",function(t){return line(t.values)}),mediaSources.selectAll("circle").data(function(t){return t.values}).transition().duration(500).attr({cx:function(t){return xScale2(t.date)},cy:function(t){return yScale2(t.count)}}).style({fill:function(t){if(0===t.vis)return"white";var e=color(t.name),a=d3.rgb(e).darker();return a}})}var allDates,aggregateMediaStats,dateList,color,mediaSources,storyPoints,line,mediaTypes,sources,visibleDates,xAxis,xScale,yAxis,yScale,xScale2,yAxis2,yScale2,parseDateQuery=d3.time.format("%Y-%m-%d").parse,parseDateTips=d3.time.format("%b %d, %Y"),parseStorypoint=d3.time.format("%Y-%m-%d").parse;mediaTypes=["Traditional","Independent","Blogs-Social"];var typeMargin={top:50,right:140,bottom:0,left:70},typeWidth=990-typeMargin.left-typeMargin.right,typeHeight=450-typeMargin.bottom-typeMargin.top,typeDetail={x:0,y:25,w:typeWidth,h:350},typePadding=30,sourceMargin={top:50,right:200,bottom:0,left:70},sourceWidth=990-sourceMargin.left-sourceMargin.right,sourceHeight=450-sourceMargin.bottom-sourceMargin.top,sourceDetail={x:0,y:25,w:sourceWidth,h:350},sourcePadding=30,typeSVG=d3.select("#timelineTypeVis").append("svg").attr({"class":"timeline",width:typeWidth+typeMargin.left+typeMargin.right,height:typeHeight+typeMargin.top+typeMargin.bottom}).append("g").attr({transform:"translate("+typeMargin.left+","+typeMargin.top+")"});typeSVG.append("clipPath").attr("id","type-chart-area").append("rect").attr({x:-typePadding,y:-typePadding-5,width:typeDetail.w+12*typePadding,height:typeDetail.h+1.97*typePadding});var sourceSVG=d3.select("#timelineSourceVis").append("svg").attr({"class":"timeline",width:sourceWidth+sourceMargin.left+sourceMargin.right,height:sourceHeight+sourceMargin.top+sourceMargin.bottom}).append("g").attr({transform:"translate("+sourceMargin.left+","+sourceMargin.top+")"});sourceSVG.append("clipPath").attr("id","chart-area").append("rect").attr({x:-sourcePadding,y:-sourcePadding-5,width:sourceDetail.w+12*sourcePadding,height:sourceDetail.h+1.97*sourcePadding}),$(document).ready(function(){$("#timelineComparedVis").append("<br> [CHART HERE] <br>")}),addClassNameListener("crisis_select",function(){var t=window.crisis_select.value;console.log("### QUEUE NEW CRISIS ("+t+") AFTER CLASS CHANGE ###"),queue().defer(d3.csv,"/productiondata/"+t+"/storypoints.csv").defer(d3.csv,"/productiondata/"+t+"/google-media_stats.csv").await(getData)});
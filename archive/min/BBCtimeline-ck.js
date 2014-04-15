function blogData(){d3.csv("data/2014-04-03-12.09.57_all_no_text-no2011-blog.csv",function(t){t.forEach(function(t,a){t.date_published=parseDate(t.date_published),null!=t.date_published&&duplicateBlogDates.push(t.date_published)}),duplicateBlogDates.sort(function(t,a){return d3.ascending(t,a)});var a;return duplicateBlogDates.forEach(function(t,e){var i=parseDateSimple(t);i!=a?(blogDates.push({date:t,count:1,type:"blog"}),a=i):blogDates[blogDates.length-1].count++}),mergeData()})}function mergeData(){color.domain(["traditional","blog"]);var t=[];t.values=blogDates.map(function(t){return t}),t.type="blog",allDates.push(t);var a=[];return a.values=traditionalDates.map(function(t){return t}),a.type="traditional",allDates.push(a),storypoints()}function storypoints(){d3.csv("data/haiyan/storypoints.csv",function(t){return storyPoints=t,storyPoints.forEach(function(t){t.date=parseStorypoint(t.date)}),detailVis()})}function detailVis(){var t,a,e;xDetailScale=d3.time.scale().domain(d3.extent(traditionalDates,function(t){return t.date})).range([0,bbDetail.w]);var i=BBCsvg.append("g").attr({"class":"detailFrame",transform:"translate("+bbDetail.x+","+bbDetail.y+")"});e=d3.scale.linear().domain([0,d3.max(allDates,function(t){return d3.max(t.values,function(t){return t.count})})]).range([bbDetail.h,0]),t=d3.svg.axis().scale(xDetailScale).orient("bottom").ticks(7).tickFormat(d3.time.format("%b")),a=d3.svg.axis().scale(e).orient("left").ticks(6);var n=d3.svg.area().x(function(t){return xDetailScale(t.date)}).y0(bbDetail.h).y1(function(t){return e(t.count)}),r=d3.svg.line().x(function(t){return xDetailScale(t.date)}).y(function(t){return e(t.count)}).interpolate("linear");i.append("g").attr({"class":"x axis",transform:"translate(0,"+bbDetail.h+")"}).call(t),i.append("g").attr({"class":"y axis"}).call(a).append("text").attr("x",-5).attr("y",-45).attr("dy","30px").style("text-anchor","end").text("Coverage");var l=BBCsvg.selectAll(".dataTypes").data(allDates).enter().append("g").attr("class","dataTypes");l.append("path").attr({"class":"traditionalMediaPath",d:function(t){return console.log(t),r(t.values)},transform:"translate(0,"+bbDetail.y+")"}).style({stroke:function(t){return color(t.type)},fill:"none"}),l.append("path").attr({"class":"traditionalMediaArea",d:function(t){return n(t.values)},transform:"translate(0,"+bbDetail.y+")"}).style({fill:function(t){return color(t.type)}}),l.selectAll("circle").data(function(t){return t.values}).enter().append("circle").attr({"class":"dot",cx:function(t){return xDetailScale(t.date)},cy:function(t){return e(t.count)},r:4,transform:"translate(0,"+bbDetail.y+")"}).style({fill:function(t){var a=color(t.type),e=d3.rgb(a).darker();return e}}).on("mouseover",function(t){d3.select(this).transition().duration(25).attr("r",10),tip.show(t)}).on("mouseleave",function(t){d3.select(this).transition().duration(25).attr("r",4),tip.hide(t)}),d3.select("#crisisTitle").html("<h3>Typhoon Haiyan</h3>"),d3.select("#crisisStory").html('Typhoon Haiyan, known as Typhoon Yolanda in the Philippines, was a powerful tropical cyclone that devastated portions of Southeast Asia, particularly the Philippines, on November 8, 2013. <a href="http://en.wikipedia.org/wiki/Typhoon_Haiyan" class="storySource">&mdash; Wikipedia</a>'),i.selectAll(".line").data(storyPoints).enter().append("line").attr({"class":"storyline",x1:function(t){return xDetailScale(t.date)},x2:function(t){return xDetailScale(t.date)},y1:-bbDetail.y,y2:bbDetail.h}),i.selectAll(".storyTriangle").data(storyPoints).enter().append("path").attr({"class":"storyTriangle",transform:function(t){return"translate("+xDetailScale(t.date)+","+-bbDetail.y+")"},d:d3.svg.symbol().type("triangle-down").size(256)}).on("mouseover",function(t){var a=parseDateTips(t.date);d3.select(".crisisDate").remove(),d3.select("#crisisTitle").append("span").html(a).attr("class","crisisDate"),d3.select("#crisisStory").html(t.title),d3.selectAll(".storyTriangle").transition().style("fill","#919191").attr("d",d3.svg.symbol().type("triangle-down").size(256)),d3.select(this).transition().style("fill","#3D8699").attr("d",d3.svg.symbol().type("triangle-down").size(512))}),tip=d3.tip().html(function(t){return 1==t.count?t.count+" '"+t.type+"' article on "+parseDateTips(t.date):t.count+" '"+t.type+"' articles on "+parseDateTips(t.date)}).direction("e").attr("class","d3-tip e"),i.call(tip)}var allDates,bbDetail,bbOverview,traditionalDates,blogDates,storyPoints,duplicateAllDates,duplicateTraditonalDates,duplicateBlogDates,padding,parseYear,BBCsvg,xDetailScale,xOverviewScale,margin={top:50,right:50,bottom:0,left:100},width=960-margin.left-margin.right,height=400-margin.bottom-margin.top;bbOverview={x:0,y:10,w:width,h:50},bbDetail={x:0,y:25,w:width,h:300},color=d3.scale.ordinal().range(["#CC1452","#14A6CC"]),padding=30,parseDate=d3.time.format("%Y-%m-%dT%H:%M:%S+00:00").parse,parseDateQuery=d3.time.format("%Y-%m-%d").parse,parseDateSimple=d3.time.format("%b %d %Y"),parseDateTips=d3.time.format("%b %d, %Y"),parseStorypoint=d3.time.format("%Y-%m-%d").parse,allDates=[],traditionalDates=[],duplicateTraditonalDates=[],duplicateBlogDates=[],blogDates=[],BBCsvg=d3.select("#BBCtimelineVis").append("svg").attr({"class":"BBCtimeline",width:width+margin.left+margin.right,height:height+margin.top+margin.bottom}).append("g").attr({transform:"translate("+margin.left+","+margin.top+")"}),BBCsvg.append("defs").append("clipPath").attr("id","clip").append("rect").attr("width",width).attr("height",height),d3.csv("data/2014-04-03-12.09.57_all_no_text-no2011.csv",function(t){t.forEach(function(t,a){t.date_published=parseDate(t.date_published),null!=t.date_published&&duplicateTraditonalDates.push(t.date_published)}),duplicateTraditonalDates.sort(function(t,a){return d3.ascending(t,a)});var a;return duplicateTraditonalDates.forEach(function(t,e){var i=parseDateSimple(t);i!=a?(traditionalDates.push({date:t,count:1,type:"traditional"}),a=i):traditionalDates[traditionalDates.length-1].count++}),blogData()});
function currentCrisisEvaluator(){var e=!1;localStorage.getItem("current_crisis")||localStorage.setItem("current_crisis","haiyan"),!window.crisis_select.value||firstTime?(console.log("--> updating new window.crisis_select variable to reflect localStorage: "+localStorage.getItem("current_crisis")),window.crisis_select.value=localStorage.getItem("current_crisis"),firstTime=!1,e=!0):localStorage.getItem("current_crisis")!==window.crisis_select.value&&(console.log("--> updating localStorage 'current_crisis' variable to reflect new selection: "+window.crisis_select.value),localStorage.setItem("current_crisis",window.crisis_select.value),e=!0),e&&(window.crisis_select.className=window.crisis_select.value)}function addClassNameListener(e,t){var s=document.getElementById(e),i=s.className;window.setInterval(function(){var e=s.className;e!==i&&(t(),i=e)},10)}function numberWithCommas(e){if(isNaN(e))return e;var t=e.toString().split(".");return t[0]=t[0].replace(/\B(?=(\d{3})+(?!\d))/g,","),t.join(".")}function resetSummary(e){d3.selectAll(".storyTriangle").transition().style("fill","#919191").attr("d",d3.svg.symbol().type("triangle-down").size(256)),"content-tab active"===document.getElementById("tab_1_compared").className&&void 0!=document.getElementById("overview")?(d3.select("#crisisTitle").html("<h3>Who does what...</h3>"),d3.select("#crisisStory").html("Lorem ipsum dolor set..."),$("#crisis_selector").hide()):"content-tab active"===document.getElementById("tab_1_compared").className&&void 0!=document.getElementById("timeline")?(d3.select("#crisisTitle").html("<h3>Relative Interest In Crises</h3>"),d3.select("#crisisStory").html("Percentage of change in crisis coverage can be compared from month to month to determine increased, same, or decreased media attention aggregated across all Google results."),$("#crisis_selector").hide()):($("#crisis_selector").show(),d3.select("#crisisTitle").data(e).html(function(e){return"<h3>"+e.title+"</h3>"}),d3.select("#crisisStory").data(e).html(function(e){return"<p>"+e.content+"</p>"}))}function showSelector(){$("#crisis_selector").show()}var firstTime=!0;String.prototype.replaceAll=function(e,t){var s=this;return s.replace(new RegExp(e.replace(/[-\/\\^$*+?.()|[\]{}]/g,"\\$&"),"g"),t)};
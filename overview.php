<?php include 'includes/head.html'; ?>

<body id="overview">

	<?php include 'includes/header.html'; ?>

	<style>
	 #overviewGlobe svg, #overviewMap svg{
          /*background: #eeeeee;*/
          /*background: #d9d9d9;*/
      }
	</style>

	<!-- FOR SANKEY -->
	<style>
      #chart {
          height: 500px;
      }

      .node rect {
          cursor: move;
          fill-opacity: .9;
          shape-rendering: crispEdges;
      }

      .node text {
          pointer-events: none;
          text-shadow: 0 1px 0 #fff;
      }

      .link {
          fill: none;
          stroke: #000;
          stroke-opacity: .2;
      }

      .link:hover {
          stroke-opacity: .5;
      }

  </style>

  <div id="overview_content" class="cf">
  	<div class="tabs cf">
  	    <a data-toggle="tab_1_compared">Crises Compared</a>
        <a data-toggle="tab_2_globe">Globe</a>
        <a data-toggle="tab_3_map">Map</a>
        <a data-toggle="tab_4_bar">Bar Chart</a>
    </div>

    <div class="tabContent">
    <!-- Overview Vis and Legend -->
     <div id="tab_1_compared" class="content-tab">
              <section id="section_country_globe">
                <header class="tab_header">
                   <div class="overview_title">Crises Compared</div>
                   <div class="tip">Results for countries providing top-ten results. Drag to rearrange nodes.</div>
                 </header>
                <figure id="countrySankeyVis"></figure>
            	</section>
            </div>

        <div id="tab_2_globe" class="content-tab">
          <section id="section_country_globe">
            <header class="tab_header">
               <div class="overview_title">Results by Country</div>
               <div class="tip">click to manually rotate,double-click auto-rotate.</div>
             </header>
            <figure id="overviewGlobe"></figure>
            <figure id="legend_globe" class="legend"></figure>
        	</section>
        </div>

         <div id="tab_3_map" class="content-tab">
                  <section id="section_country_map">
                    <header class="tab_header">
                       <div class="overview_title">Results by Country</div>
                       <div class="tip">click to isolate other countries with similar results. </div>
                     </header>
                    <figure id="overviewMap"></figure>
                    <figure id="legend_map" class="legend"></figure>
                	</section>
         </div>

        <div id="tab_4_bar" class="content-tab">
          <section id="section_country_bar_chart">
             <header class="tab_header">
               <div class="overview_title">Results by Country</div>
               <div class="tip">
                 <label>sort by </label>
                 <label><input type="radio" name="order" value="country" title="click again to reverse order">country</label>
                 <label><input type="radio" name="order" value="result" title="click again to reverse order" checked>result</label>
               </div>
             </header>
             <figure id="country_bar_chart" class="scroll-pane"></figure>
          </section>
        </div>

    </div>
</div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>

	  <script src="libs/d3.sankey.js"></script>

	<script src="overview.js"></script>
</body>
</html>
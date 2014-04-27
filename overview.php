<?php include 'includes/head.html'; ?>

<body id="overview">

	<?php include 'includes/header.html'; ?>

	<style>
	 #overviewGlobe svg, #overviewMap svg{
          background: #eeeeee;
          /*background: #d9d9d9;*/
      }
	</style>

  <div id="overview_content" class="cf">
  	<div class="tabs cf">
        <a data-toggle="tab_1_globe">Globe</a>
        <a data-toggle="tab_2_map">Map</a>
        <a data-toggle="tab_3_bar">Bar Chart</a>
    </div>

    <div class="tabContent">
    <!-- Overview Vis and Legend -->
        <div id="tab_1_globe" class="content-tab">
          <section id="section_country_globe">
            <header class="tab_header">
               <span class="overview_title">Results by Country</span>
               <span class="tip">click to manually rotate,double-click auto-rotate.</span>
             </header>
            <figure id="overviewGlobe"></figure>
            <figure id="legend_globe" class="legend"></figure>
        	</section>
        </div>

         <div id="tab_2_map" class="content-tab">
                  <section id="section_country_map">
                    <header class="tab_header">
                       <span class="overview_title">Results by Country</span>
                       <span class="tip">click to isolate other countries with similar results. </span>
                     </header>
                    <figure id="overviewMap"></figure>
                    <figure id="legend_map" class="legend"></figure>
                	</section>
         </div>

        <div id="tab_3_bar" class="content-tab">
          <section id="section_country_bar_chart">
             <header class="tab_header">
               <span class="overview_title">Results by Country</span>
               <span class="tip">
                 <label>sort by </label>
                 <label><input type="radio" name="order" value="country" title="click again to reverse order">country</label>
                 <label><input type="radio" name="order" value="result" title="click again to reverse order" checked>result</label>
               </span>
             </header>
             <figure id="country_bar_chart" class="scroll-pane"></figure>
          </section>
        </div>

    </div>
</div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>
	<script src="overview.js"></script>
</body>
</html>
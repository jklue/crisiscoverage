<?php include 'includes/head.html'; ?>

<body id="overview">

<!-- ADDITIONAL JS LIBS -->
<script type="text/javascript" src="libs/jquery-1.10.1.js"></script>
<script type="text/javascript" src="libs/html5jtabs.js"></script>
<script type="text/javascript" src="libs/jquery.jscrollpane.min.js"></script>
<script type="text/javascript" src="libs/jquery.mousewheel.js"></script>
<script type="text/javascript" src="libs/colorlegend.js"></script>

<link type="text/css" href="css/jquery.jscrollpane.css" rel="stylesheet" media="all" />
	
	<?php include 'includes/header.html'; ?>

  <div id="overview_content" class="cf">
  	<div class="tabs cf">
        <a data-toggle="tab_1_globe">Globe</a>
        <a data-toggle="tab_2_bar">Bar Chart</a>
    </div>

    <div class="tabContent">
    <!-- Overview Vis and Legend -->
        <div id="tab_1_globe" class="content-tab">
          <section id="section_country_globe">
            <header class="tab_header">
               <span class="overview_title">Results by Country</span>
               <span class="tip">click to manually rotate,double-click auto-rotate.</span>
             </header>
            <figure id="overviewVis"></figure>
            <figure id="legend_globe" class="legend"></figure>
        	</section>
        </div>

        <div id="tab_2_bar" class="content-tab">
          <section id="section_country_bar_chart">
             <header class="tab_header">
               <span class="overview_title">Results by Country</span>
               <span class="tip">
                 <label>sort by </label>
                 <label><input type="radio" name="order" value="country">country</label>
                 <label><input type="radio" name="order" value="result" checked>result</label>
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
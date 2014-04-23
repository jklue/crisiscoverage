<?php include 'includes/head.html'; ?>

<body id="overview">

  <script src="libs/colorlegend.js"></script>

<!--TODO: STYLE FOR d3 tip FOR THIS PAGE, INCORPORATE INTO CSS ??? -->
<style>
   text{
        font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
        font-size: 9pt;
        font-weight: bold;
   }

  .d3-tip {
        line-height: 1;
        font-weight: bold;
        padding: 12px;
        background: rgba(0, 0, 0, 0.8);
        color: #fff;
        border-radius: 2px;
    }

    /* Creates a small triangle extender for the tooltip */
    .d3-tip:after {
        box-sizing: border-box;
        display: inline;
        font-size: 10px;
        width: 100%;
        line-height: 1;
        background: rgba(0, 0, 0, 0.8);
        content: "\25BC";
        position: absolute;
        text-align: center;
    }

    /* Style northward tooltips differently */
    .d3-tip.n:after {
        margin: -1px 0 0 0;
        top: 100%;
        left: 0;
    }

    /* no arrow */
    .d3-tip.none:after {
        opacity: 0;
    }

    /* legend */
      .legend {
                background-color: #fff;
                width: 350px;
                height: 50px;
               /* border: 1px solid #bbb; */
                margin:0 auto;
            }

            #quantileLegend .colorlegend-labels {
                font-size: 11px;
                fill: black;
            }

</style>
	
	<?php include 'includes/header.html'; ?>
	
	<!-- Overview Vis and Legend -->
	<div id="overviewVis"></div>
	<div id="legend_globe" class="legend"></div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>
	<script src="overview.js"></script>
</body>
</html>
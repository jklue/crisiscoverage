<?php include 'includes/head.html'; ?>

<body id="detail">
	<?php include 'includes/header.html'; ?>

   <!--DETAIL SPECIFIC CSS -->
      <style>

      .small{
        width: 200px;
        margin-right: 10px;
        border-width: 2px;
        float: left;
        margin-top: 40px;
      }

      .large{
        width: 960px;
        margin-right: 10px;
        border-width: 2px;
        float: left;
        margin-top: 30px;
        margin-bottom: 20px;
      }

      #deeper_dialog {
        overflow-y:auto;
        overflow-x:auto;
      }

      #detail_header {
        display: block;
        margin-top: 200px;
        margin-bottom: 10px;
      }

      /* Gradient color1 - color2 - color1 */
      hr.style-one {
          border: 0;
          height: 1px;
          background: #333;
          background-image: -webkit-linear-gradient(left, #ccc, #333, #ccc);
          background-image:    -moz-linear-gradient(left, #ccc, #333, #ccc);
          background-image:     -ms-linear-gradient(left, #ccc, #333, #ccc);
          background-image:      -o-linear-gradient(left, #ccc, #333, #ccc);
      }

      .axis {
      		fill: #CCC;
      	}

      #sub_charts{
          display: block;
          margin-top: 10px;
          margin-bottom: 10px;
      }
      </style>

	<div class="content">
	      <header id="detail_header" class="tab_header">
	        <hr class="style-one">
          <div class="overview_title">Crisis Coverage Detail</div>
          <div class="tip">An in-depth exploration for crisis over months of coverage. Click on a bar for sample
          monthly results.</div>
        </header>

        <div id = "stacked" class = 'large'></div>

        <div id="deeper_dialog">

          <div id="sub_charts">
    		     <div id ="traditional" class="small"></div>
    		     <div id ="blog" class="small"></div>
    		     <div id ="independent" class="small"></div>
          </div>

    		  <div id ="source" class="large"></div>
        </div>
	</div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>

	<!-- Crossfilter Library -->
  <script src="libs/crossfilter.v1.min.js"></script>

  <!-- Modal Dialog for additional detail -->
  <title>jQuery UI Dialog - Basic modal</title>
  	<link rel="stylesheet" href="css/jquery-ui/jquery.ui.all.css">

  	<script src="libs/jquery-ui/jquery.ui.core.js"></script>
  	<script src="libs/jquery-ui/jquery.ui.widget.js"></script>
  	<script src="libs/jquery-ui/jquery.ui.mouse.js"></script>
  	<script src="libs/jquery-ui/jquery.ui.draggable.js"></script>
  	<script src="libs/jquery-ui/jquery.ui.position.js"></script>
  	<script src="libs/jquery-ui/jquery.ui.resizable.js"></script>
  	<script src="libs/jquery-ui/jquery.ui.button.js"></script>
  	<script src="libs/jquery-ui/jquery.ui.dialog.js"></script>

  <!--- Data tables -->
  <script src="libs/jquery.dataTables.min.js"></script>
  <link rel="stylesheet" href="css/jquery.dataTables.css">

    <!-- OVERRIDE ANCHOR CSS AFTER JQUERY.DATATABLES MUCKS -->
    <style>
        .dataTable a:hover{
          color: #af6e76;
        }

        .dataTable a{
          text-decoration: underline;
          color: #636363;

        }
   </style>

  <!-- Details -->
  <script src = 'detail.js'> </script>

</body>
</html>
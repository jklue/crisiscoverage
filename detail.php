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
        padding-top: 10px;
      }

      /* Gradient color1 - color2 - color1 */
      hr.style-one {
          margin-top: 10px;
          border: 0;
          height: 1px;
          background: #333;
          background-image: -webkit-linear-gradient(left, #ccc, #333, #ccc);
          background-image:    -moz-linear-gradient(left, #ccc, #333, #ccc);
          background-image:     -ms-linear-gradient(left, #ccc, #333, #ccc);
          background-image:      -o-linear-gradient(left, #ccc, #333, #ccc);
      }
      </style>

	<div class="content">
	      <header id="detail_header" class="tab_header">
	        <hr class="style-one">
          <div class="overview_title">Crisis Coverage Detail</div>
          <div class="tip">An in-depth exploration for crisis over months of coverage. Click on a bar for more.</div>
        </header>

        <div id = "stacked" class = 'large'></div>

        <div id="deeper_dialog">

          <div class="overview_title">Traditional</div><br>
    		  <div id ="traditional" class="small"></div>

    		  <div class="overview_title">Blogs-Social</div><br>
    		  <div id ="blog" class="small"></div>

    		  <div class="overview_title">Independent</div><br>
    		  <div id ="independent" class="small"></div>

    		  <div id ="source" class="large">
              <table id="table" class="display" cellpadding="0" cellspacing="0" border="0"></table>
          </div>

        </div>
	</div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>

	<!-- Crossfilter Library -->
  <script src="libs/crossfilter.v1.min.js"></script>

  <!--- Data tables -->
  <script src="libs/jquery.dataTables.min.js"></script>
   <link rel="stylesheet" href="css/jquery.dataTables.css">

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

  <!-- Details -->
  <script src = 'detail.js'> </script>

</body>
</html>
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
      </style>

	<div class="content">
	      <header class="tab_header">
          <div class="overview_title">Crisis Details</div>
          <div class="tip">In-depth exploration for crisis over months of coverage. Click on bar for more.</div>
        </header>
    		<div id = "traditional" class = "small">traditional</div>
    		<div id = "Blog" class = "small">blog</div>
    		<div id = "Independant" class = "small">indi</div>

    		<div id = "stacked" class = 'large'><b><center>News Coverage Breakdown</center></b></div>
    		<div id = "source" class = 'large'><b><center>Articles by Month</b></center>
    			<table cellpadding="0" cellspacing="0" border="0" class="display" id="table"></table>
    		</div>
	</div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>

	<!-- Crossfilter Library -->
  <script src="libs/crossfilter.v1.min.js"></script>

  <!--- Data tables -->
  <script src="libs/jquery.dataTables.min.js"></script>
   <link rel="stylesheet" href="css/jquery.dataTables.css">

  <!-- Details -->
  <script src = 'detail.js'> </script>

</body>
</html>
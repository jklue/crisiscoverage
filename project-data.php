<?php include 'includes/head.html'; ?>

<body id="data">
	<?php include 'includes/header.html'; ?>

	<script src="libs/jquery.dataTables.min.js"></script>
  <link rel="stylesheet" href="css/jquery.dataTables.css">

	<div class="content">
		 <div id="data_content" class="cf">
      	<div class="tabs cf">
      	    <a data-toggle="tab_1_turkish-revolt">A Revolt in Turkey</a>
      	    <!--
      	    <a data-toggle="tab_2_pakistan-drought">Drought in Pakistan</a>
      	    <a data-toggle="tab_3_ukraine-protest">Protests in Ukraine</a>
            <a data-toggle="tab_4_haiyan">Typhoon Haiyan</a>
            -->
        </div>

        <div class="tabContent">
        <!-- DATA -->
            <div id="tab_1_turkish_revolt" class="content-tab">
                <section class="country_stats">
                  <span class="overview_title">Google Country Stats</span>
                  <span class="tip">total results by country</span><br>
                  <table id="table_country_turkish_revolt"></table><br>
                </section>
                <section class="media_baseline_stats">
                  <span class="overview_title">Google Media Baseline</span>
                  <span class="tip">total news activity by select sources</span><br>
                  <table id="table_baseline_turkish_revolt"></table><br>
                </section>
                <section class="media_stats">
                  <span class="overview_title">Google Crisis Stats</span>
                  <span class="tip">crisis news activity by select sources</span><br>
                  <table id="table_media_turkish_revolt"></table><br>
                </section>
                <section class="media_results_subset">
                  <span class="overview_title">Google Crisis Sample Results</span>
                  <span class="tip">sampling of crisis news activity by select sources</span><br>
                  <table id="table_sample_turkish_revolt"></table><br>
                </section>
            </div>
        </div>
      </div>
	</div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>
  <script src="project-data.js"></script>
</body>
</html>
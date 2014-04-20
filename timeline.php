<?php include 'includes/head.html'; ?>

<body id="timeline">
	<?php include 'includes/header.html'; ?>

	<div class="content">

		<!-- Crisis Info -->
		<div id="crisisInfo">
			<div id="crisisTitle"></div>	
			<div id="crisisStory"></div>
		</div>

		<!-- Graph controls -->
<!-- 		<form id="dataSourceSelect">
	  Source:
	  	<select name="" id="">
	  		<option name="google" value="google">Google</option>
	  		<option name="bbc" value="bbc">BBC</option>
	  	</select>
	  </form> -->

		<!-- Google Timeline -->
		<div id="timelineVis"></div>

		<form class="chartData">
      <legend>Show graph of...</legend>
      <label><input type="radio" name="graph" value="types"> media types</label>
      <label><input type="radio" name="graph" value="sources" checked> media sources</label>
    </form>

	</div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>
	<script src="timeline-month.js"></script>
	<!-- <script src="timeline.js"></script> -->
	<!-- // <script src="BBCtimeline.js"></script> -->

</body>
</html>
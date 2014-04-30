<?php include 'includes/head.html'; ?>

<body id="overview" class="martini">
	<?php include 'includes/martini-header.html'; ?>

	<div class="content">

		<!-- Tutorial -->
		<div class="tutorial-wrapper">
			<div class="tutorial">
				<h1>World</h1>
				<p><strong>Compared Crises Tab:</strong> Countries providing top-ten results for all of the crises can be
				compared in a
				single sankey diagram, reference other Tabs for full evaluation of response to a given crisis.</p>

				<p><strong>Map Tab:</strong> Results by country, colored on a choropleth map according to level of response.
				Useful to
				general responses to a single crisis at a glance.</p>

				<p><strong>Bar Chart Tab:</strong> Results by country provided as a horizontal bar chart according to level of
				 response.
				Useful to compare the level of response for a single crisis in absolute or numerical terms.</p>

				<nav class="martini">
					<ul>
						<!-- <li><a href="/overview" class="left arrow"><img src="images/icons/leftarrow.png" alt="Left Arrow" title="Skip to Charts">Skip to Charts</a></li> -->
						<li><a href="/timelinemartini" class="right arrow">Next<img src="images/icons/rightarrow.png" alt="Right Arrow" title="Learn More"></a></li>
					</ul>
				</nav>
			</div>
		</div>

		<!-- Overview Vis -->
		<div id="overviewVisGIF" class="tut-gif"></div>

	</div>
	<!-- footer -->
	<?php include 'includes/footer.html'; ?>
</body>
</html>
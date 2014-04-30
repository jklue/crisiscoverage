<?php include 'includes/head.html'; ?>

<body id="data" class="martini">
	<?php include 'includes/martini-header.html'; ?>


	<div class="content">

		<!-- Tutorial -->
		<div class="tutorial-wrapper">
			<div class="tutorial">
				<h1>Project Data</h1>
				<p><em>Offers the primary tables that drive this site, derived from using Google Custom Search API.</em></p>

				<p><strong>Baseline Coverage Stats:</strong> Count of all results (not just crisis) available for each of our
        				specially	tracked sites + all Google.</p>

				<p><strong>Country Stats:</strong> Count of results from each country for a given crisis.</p>

				<p><strong>Crisis Coverage Stats:</strong> Count of crisis results from our specially tracked sites + all Google
				.</p>
				<p><strong>Crisis Sample Results:</strong> For each crisis, a sampling of results from each of our specially
				tracked	sites + all	Google.</p>

				<nav class="martini">
					<ul>
						<!-- <li><a href="/overview" class="left arrow"><img src="images/icons/leftarrow.png" alt="Left Arrow" title="Skip to Charts">Skip to Charts</a></li> -->
						<li><a href="/overview" class="right arrow">Explore<img src="images/icons/rightarrow.png" alt="Right Arrow" title="Learn More"></a></li>
					</ul>
				</nav>
			</div>
		</div>

		<!-- GIF Tutorial -->
		<div id="timelineVisGIF" class="tut-gif"></div>

	</div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>

</body>
</html>
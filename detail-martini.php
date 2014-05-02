<?php include 'includes/head.html'; ?>

<body id="data" class="martini">
	<?php include 'includes/martini-header.html'; ?>


	<div class="content cf">

		<!-- Tutorial -->
		<div class="tutorial-wrapper cf">
			<div class="tutorial">
			<h1>Details</h1>
				<p>The goal of this section was to show more details about the data we gathered. For this this page, we created a custom json that aggregated the 8 different source files produced by the crawler and indexed by month, type, and media source. Using the crossfilter.js, we were able to perform fast searches on the page as well for both displaying the stacked bar chart as well as retrieving the pertinent articles in the article explorer.</p>
				<p>Clicking on the bars reveals a secondary interactive table that shows sample article results for the crisis within the month selected. This gives a taste of the articles our custom query returned, and is limited to 10 articles.</p>
				<nav class="martini">
					<ul>
						<!-- <li><a href="/overview" class="left arrow"><img src="images/icons/leftarrow.png" alt="Left Arrow" title="Skip to Charts">Skip to Charts</a></li> -->
						<li><a href="/project-data-martini" class="right arrow">Data<img src="images/icons/rightarrow.png" alt="Right Arrow" title="Learn More"></a></li>
					</ul>
				</nav>
			</div>
		</div>

		<!-- GIF Tutorial -->
		<div id="detailVisGIF" class="tut-gif"></div>

	</div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>

</body>
</html>
<?php include 'includes/head.html'; ?>

<body id="data" class="martini">
	<?php include 'includes/martini-header.html'; ?>


	<div class="content cf">

		<!-- Tutorial -->
		<div class="tutorial-wrapper cf">
			<div class="tutorial">
			<h1>Details</h1>
				<p>The 'Details' Page gives a more in depth comparison of results from each site by crisis. The primary visualization is a stacked bar chart providing results from each of our tracked sites over the course of the crisis lifespan. On hover, aa tooltip is presented with the total crisis coverage article results as well as the baseline coverage given by that site for any news and then the relative percentage.</p>
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
		<div id="timelineVisGIF" class="tut-gif"></div>

	</div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>

</body>
</html>
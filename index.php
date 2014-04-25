<?php include 'includes/head.html'; ?>

<body id="welcome">

<!--TODO: STYLE FOR #crisis_name FOR THIS PAGE, INCORPORATE INTO CSS ??? -->
<style>

    #crisis_name {
        position: relative;
        top: 220px;
        font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
        font-size: 18px;
        text-align: center;
        width: 220px;
        margin: 0 auto;
    }

</style>

	<!-- Rotating Globe -->
	<div id="rotatingGlobe">
	  <!-- Crisis Name  -->
    <h3 id="crisis_name"></h3>
	</div>

	<nav class="martini">
		<ul>
			<li><a href="/overview" class="left arrow"><img src="images/icons/leftarrow.png" alt="Left Arrow" title="Skip to Charts">Start Exploring</a></li>
			<li><a href="/overviewmartini" class="right arrow">Learn More<img src="images/icons/rightarrow.png" alt="Right Arrow" title="Learn More"></a></li>
		</ul>
	</nav>

	<header>		
		<div class="title">
			<div class="tower">
				<img id="radio-tower" src="images/radio2.3-simple.png" alt="Radio Icon">
				<img id="radio-dot" class="fadeInOut" src="images/radio2.3-dot.png" alt="Radio Dot">
			</div>
			<h2>From Turmoil to Resolution</h2>
			<h3>Media Coverage Over the Life of a Crisis</h3>
		</div>
	</header>

	<?php include 'includes/footer.html'; ?>
	// <script src="index.js"></script>

</body>
</html>
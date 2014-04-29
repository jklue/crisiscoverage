<?php include 'includes/head.html'; ?>

<body id="timeline">
  <?php include 'includes/header.html'; ?>

  <div class="content cf">

    <!-- Crisis Info -->
    <div id="crisisInfo">
      <div id="crisisCompared"></div>
      <div id="crisisTitle"></div>
      <div id="crisisStory"></div>
    </div>

    <!-- Graphs -->
    <div class="tabs cf">
        <a data-toggle="tab_1_compared">Crises Compared</a>
        <a data-toggle="tab_2_type">By Media Type</a>
        <a data-toggle="tab_3_source">By Source</a>
    </div>

    <div class="tabContent">

        <!-- Timeline by source -->
        <div id="tab_1_compared" class="content-tab">
          <div class="overview_title">Crises Compared</div>
          <div class="tip">Percentage of change in results (all Google) from month to month, per crisis.</div>
          <div id="timelineComparedVis"></div>
        </div>

        <!-- Timeline by source -->
        <div id="tab_2_type" class="content-tab">
          <div class="overview_title">Results by Media Type</div>
          <div class="tip">Monthly results aggregated by media type (for tracked sources).</div>
          <div id="timelineTypeVis"></div>
        </div>

        <!-- Timeline by media type -->
        <div id="tab_3_source" class="content-tab">
          <div class="overview_title">Results by Tracked Sources</div>
          <div class="tip">Click on a checkbox below to remove or add to chart.</div>
          <div id="timelineSourceVis"></div>
        </div>
      </div>
    </div>

  </div>

<!-- footer -->
  <?php include 'includes/footer.html'; ?>
  <script src="timeline.js"></script>

</body>
</html>
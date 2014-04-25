<?php include 'includes/head.html'; ?>

<body id="timeline">
  <?php include 'includes/header.html'; ?>

  <div class="content cf">
    
    <!-- Crisis Info -->
    <div id="crisisInfo">
      <div id="crisisTitle"></div>  
      <div id="crisisStory"></div>
    </div>

    <!-- Graphs -->
    <div class="tabs cf">
        <a data-toggle="tab_1_type">By Media Type</a>
        <a data-toggle="tab_2_source">By Source</a>
    </div>

    <div class="tabContent">
        <!-- Timeline by source -->
        <div id="tab_1_type" class="content-tab">
          <div class="tip">Click on source name below to remove or add to chart.</div>
          <div id="timelineTypeVis"></div>
        </div>

        <!-- Timeline by media type -->
        <div id="tab_2_source" class="content-tab">
          <div class="tip">Click on source name below to remove or add to chart.</div>
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
<?php include 'includes/head.html'; ?>

<body id="data">
	<?php include 'includes/header.html'; ?>

	<div id="project_data_content" class="data-content">
    <!-- Current Data Table will be added here. -->
	</div>

<!-- footer -->
	<?php include 'includes/footer.html'; ?>

	<script src="libs/jquery.dataTables.min.js"></script>
  <link rel="stylesheet" href="css/jquery.dataTables.css">

  <!-- OVERRIDE ANCHOR CSS AFTER JQUERY.DATATABLES MUCKS -->
  <style>
   .dataTable a:hover{
          color: #af6e76;
        }

        .dataTable a{
          text-decoration: underline;
          color: #636363;

        }
   </style>
  <script src="project-data.js"></script>

</body>
</html>
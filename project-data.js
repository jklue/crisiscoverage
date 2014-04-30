var tableSelect;

var tableNames = [];
    tableNames['country_stats'] = "google-country_stats.csv";
    tableNames['baseline_stats'] = "google-media-baseline_stats.csv";
    tableNames['crisis_stats'] = "google-media_stats.csv";
    tableNames['crisis_sample'] = "display_google-media_results_subset.csv";

/**
 * Loaded Data Callback.
 * @param error
 * @param media
 */
function loadedDataCallBack(error, media, summary){
    console.log(media);

    var aaData = [];
    var aoColumns = [];
    var firstRun = true;

    media.forEach(function (d) {
        if (firstRun){
            firstRun = false;
            _.keys(d).forEach(function(c){
                aoColumns.push({ "sTitle": c});
            });
        }
        aaData.push(_.values(d));
    });

    console.log(aoColumns);
    console.log(aaData);

    $('#project_data_table').dataTable( {
        "aaData": aaData,
        "aoColumns": aoColumns
    } );

}

/**
 * Load current table for crisis.
 * @param tableName
 */
function loadTable(tableName){

    var crisisName = window.crisis_select.value;
    console.log("--> now loading table for tableName: "+ tableName + " (" + crisisName + ")");

    $('#project_data_content').empty();
    $('#project_data_content').append(
        "<table id='project_data_table'></table>");

    queue()
        .defer(d3.csv, "/productiondata/"+crisisName+"/"+tableNames[tableName])
        .defer(d3.csv, "/productiondata/"+crisisName+"/summary.csv") // summary
        .await(loadedDataCallBack);
}

$(document).ready(function() {

    /* Append table selector to the crisis form */
    $('#crisis_form').append('<label for="data_select">Show Table: </label>\
        <select id="data_select">\
          <option value="baseline_stats" >Baseline Coverage Stats</option>\
          <option value="country_stats" >Country Stats</option>\
          <option value="crisis_stats" >Crisis Coverage Stats</option>\
          <option value="crisis_sample" >Crisis Sample Results</option>\
        </select>\
        <br>');

    if (!tableSelect) tableSelect = document.getElementById('data_select');
    d3.timer(
        function(){
            loadTable(tableSelect.value);
            return true;//run 1x after delay
        }, 2000
    );

    tableSelect.onchange = function(){
        loadTable(this.value);
    };
} );

addClassNameListener("crisis_select", function(){
    if (!tableSelect) tableSelect = document.getElementById('data_select');
    var crisis = window.crisis_select.value;
    console.log("### SETTING TABLE (" + tableSelect.value + ") AFTER NEW CRISIS ("+crisis+") AFTER CLASS CHANGE ###");
    loadTable(tableSelect.value);
});

// show crisis selector
showSelector();

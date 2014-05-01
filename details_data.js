//-----------Global Variables--------------------
var stats_filter = crossfilter();
var details_filter = crossfilter();

//set crisis to current selected crisis
var crisis = document.getElementById('crisis_select').value;
//Dictionary of English names for crisis values
var crisis_dict = {"turkish-revolt": "A Revolt in Turkey","pakistan-drought": "Drought in Pakistan", "ukraine-protest":"Protests in Ukraine", "haiyan":"Typhoon Haiyan"};

//Add Event listner for Crisis Selector 
$( "select" )
  .change(function () {
    $( "select option:selected" ).each(function() {
    	crisis = this.value;
    });
  })
  .change();

//-----------Read in Data-----------------------
queue()
       .defer(d3.json, "productiondata/con-data.json")
       .await(useData);

//----------- Use Data -------------------------

function useData(error, data){
	
	var keys = Object.keys(crisis_dict);
	var details = data[0];
	var stats = data[1];
	//build cross filter objects
	//Populate Crossfilter objects
	$(keys).each(function(i,k){
		//construct stats key
		key = k;
		stats_key = k+"_stats";
		details_key = k + "_details"
		
		//retrieve list of stats objects and keys
		stats_objs = stats[stats_key];
		sobj_keys =  Object.keys(stats_objs);

		//retrieve list of details objects and keys
		d_objs = details[details_key];
		dobj_keys =  Object.keys(d_objs);

		//loop over keys and constuct crossfilter records
		m_records = [];
		$(sobj_keys).each(function(i,k){
			//create record for stats
			row = stats_objs[k];
			m_records.push({
				"crisis": key,
				"c_articles": row.crisis_count,
				"baseline" : row.all_count,
				"date_start" : row.date_start,
				"date_end" : row.date_end,
				"id" : row.id,
				"type": row.type,
				"domain" : row.name
				});
		});

		d_records = []; 
		$(dobj_keys).each(function(i,k){
			//create record for stats
			row = d_objs[k];
			d_records.push({
				"crisis": row.collection,
				"domain": row.name,
				"title" : row.title,
				"type"  : row.type,
				"url"	: row.url,
				"date_start" : row.date_start,
				"date_end" : row.date_end
			});
		});
		
		//
		stats_filter.add(m_records);
		details_filter.add(d_records);
	});
	//---------End Loop -----------------
	
}
//------------end useData()------------------------
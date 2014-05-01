 sources = ['google-media_results_subset.csv', 
 			'google-media_stats.csv',
 			'google-media-baseline_stats.csv'];
 dir = 'productiondata/haiyan/';
 
 document.write("<h1>Data Convertor</h1> Loading the following sources:<br>"  );

 document.write("directory: ", dir, " <ul>");

for(var x =0; x < sources.length; x++){
	document.write("<li>",sources[x], "</li>")
}
document.write("</ul>")

//global variables
var g_results = {};
var con_data = {};

//-------Save to File ------------------/

var saveToFile = function(object, filename){
        var blob, blobText;
        blobText = [JSON.stringify(object)];
        blob = new Blob(blobText, {
            type: "text/plain;charset=utf-8"
        });
        saveAs(blob, filename);
    }

//-------------------------------------

//-------------writetojson------------

var writetojson = function(){
			var final_data = {};
	    	final_data["haiyan_stats"] = con_data;
	    	final_data["haiyan_details"] = g_results;


	    	document.write("printing json file....");
	    	saveToFile(final_data,"haiyan-data.json")

	    	console.log(final_data);
}

//------------word_loop--------------------------------

//-----------------------------------------------------


//----------- Parse Text File Function -----------------
var get_words = function(k,data){
	words = data.toLowerCase().split(/[\s*\.*\,\;\+?\#\|:\-\/\\\[\]\(\)\{\}$%&0-9*]/);
	var word_count = {};
	var ignore = ['and','the','to','a','of','for','as','i','with','it','is','on','that','this','can','in','be','has','if','-','s'];

	for(i in  words){
		word_count[words[i]] ? word_count[words[i]]+=1 : word_count[words[i]]=1;
	}
	
	keys = Object.keys(word_count);
	var results = [];
	$(keys).each(function(i,k){
		if(jQuery.inArray(k,ignore) < 0 && word_count[k] >1 && k != ''){
			results.push({
				text : k,
				weight : word_count[k]
			});
		}
	});
	g_results[k].words = results;
};
//------------------------------------------------------


//---------------------Load All CSV Files ---------------------------------


	d3.csv(dir+'google-media_stats.csv', function(gmstats){
		d3.csv(dir+'google-media-baseline_stats.csv', function(gmbase){
			d3.csv(dir+'google-media_results_subset.csv', function(gmresults){

			//1. Consolidate Google media stats and google media baseline files

			
			//loop through g_baseline, use query_distinct as unique key

			$(gmbase).each(function(d,k){
				id = k["query_distinct"];
				type = k["site_type"];
				name = k["site_name"];
				date_start = k["date_query_start"];
				date_end = k["date_query_end"];
				days_back = k["days_back"];
				all_count = k["raw_result_count"];
				period = k["query_period"];


				con_data[id] = {"id":id, 
								"type" : type,
								"name" : name,
								"date_start" : date_start,
								"date_end": date_end,
								"period":period,
								"all_count" : all_count,
								"crisis_count" : "null"}

			})
			//------------------------------------------------
			//--------Loop through g_stats and populate with crisis count
			$(gmstats).each(function(d,k){
				//check if id exists in con_data, if false add data
				if(!con_data[k["query_distinct"]]){
					id = k["query_distinct"];
					type = k["site_type"];
					name = k["site_name"];
					date_start = k["date_query_start"];
					date_end = k["date_query_end"];
					days_back = k["days_back"];
					crisis_count = k["raw_result_count"];
					period = k["query_period"];

					// populate consolidated data set
					con_data[id] = {"id":id, 
									"type" : type,
									"name" : name,
									"date_start" : date_start,
									"date_end": date_end,
									"period":period,
									"all_count":"null",
									"crisis_count" : all_count}
					
				}
				else{
					id = k["query_distinct"];
					crisis_count = k["raw_result_count"];
					con_data[id]["crisis_count"] = crisis_count;
				}
			})
			//--------------------------------------------------

			//2. Consolidate google media results data

			$(gmresults).each(function(d,k){
				var id = k["doc_id"];
				var collection = k["collection"];
				var date_start = k["date_query_start"];
				var date_end = k["date_query_end"];
				var domain = k["domain"];
				var period = k["query_period"];
				var title = k["title"];
				var url = k["url"];
				var name = k["site_name"];
				var type = k["site_type"];
				var words = "";

				g_results[id] = {
					"collection" : collection,
					"date_start" : date_start, 
					"date_end"	 : date_end, 
					"domain" 	 : domain, 
					"period" 	 : period, 
					"title" 	 : title, 
					"url" 	     : url,
					"name"		 : name,
					"type"		 : type,
					"words"		 : words
				};
			});
			//----------------------------------------

		    var keys = Object.keys(g_results);
		    
		    var url_base = "http://localhost:8888/dhairya_dev/productiondata/haiyan-meta/clean/"

		    var postfix = ".txt";
		    console.log(keys.length)
		    $(keys).each(function (d,k){
	      			//build url
	      			var url = url_base + k + postfix;
	      			$.get(url)
	      	  			.success(function(result) {
	      	  				get_words(k,result);
	      	  			});
	      	 });

		    writetojson();



		 	  
		});
	});
});
//--------------------------------------------------------------------------

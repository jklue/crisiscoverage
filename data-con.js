document.write("Printing json file ...");

sources = ['google-media_results_subset.csv', 
 			'google-media_stats.csv',
 			'google-media-baseline_stats.csv'];
dir = 'productiondata/';

crisis_folders = ['haiyan', 'pakistan-drought', 'turkish-revolt','ukraine-protest'];

//Queue all data files and load in paralell 
queue()
        .defer(d3.csv, "productiondata/haiyan/google-media_results_subset.csv")
        .defer(d3.csv, "productiondata/haiyan/google-media_stats.csv")
        .defer(d3.csv, "productiondata/haiyan/google-media-baseline_stats.csv")
        .defer(d3.csv, "productiondata/pakistan-drought/google-media_results_subset.csv")
        .defer(d3.csv, "productiondata/pakistan-drought/google-media_stats.csv")
        .defer(d3.csv, "productiondata/pakistan-drought/google-media-baseline_stats.csv")
        .defer(d3.csv, "productiondata/turkish-revolt/google-media_results_subset.csv")
        .defer(d3.csv, "productiondata/turkish-revolt/google-media_stats.csv")
        .defer(d3.csv, "productiondata/turkish-revolt/google-media-baseline_stats.csv")
        .defer(d3.csv, "productiondata/ukraine-protest/google-media_results_subset.csv")
        .defer(d3.csv, "productiondata/ukraine-protest/google-media_stats.csv")
        .defer(d3.csv, "productiondata/ukraine-protest/google-media-baseline_stats.csv")
        .await(getData);

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


//-----------------Read in all data files and consolidate --------------
function getData(error,h_m,h_stats,h_base,p_m,p_stats,p_base,t_m, t_stats,t_base,u_m, u_stats, u_base){
        
       var media    = {"haiyan":h_m, "pakistan-drought":p_m, "turkish-revolt": t_m, "ukraine-protest": u_m}; // dictionary of all media file for each crisis
       var stats    = {"haiyan":h_stats, "pakistan-drought":p_stats, "turkish-revolt": t_stats, "ukraine-protest": u_stats}; // dictionary of all stats file for each crisis
       var baseline = {"haiyan":h_base, "pakistan-drought":p_base, "turkish-revolt": t_base, "ukraine-protest": u_base}; // dictionary of all baseline file for each crisis

       //Loop over crisises and consolidate stats and baseline data
       var keys = Object.keys(media);

       var final_stats = {};
       var final_media = {};
       $(keys).each(function(i,k){
                //pull stats data for crisis 
                var s = stats[k];
                //pull baseline data for cris
                var b = baseline[k]
                var m = media[k];
                //store results in dict
                final_stats[k+"_stats"] = con_stats(s,b);
                final_media[k+"_details"] = con_media(m);

       });
       var final = [final_media, final_stats];
       //console.log(final);
        saveToFile(final,"con-data.json");

} 



//function to consolidate stats
function con_stats(stats, base){
var con_data = {}
 //1. Consolidate Google media stats and google media baseline files

//loop through g_baseline, use query_distinct as unique key

        $(base).each(function(d,k){
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

        });
        //------------------------------------------------
        //--------Loop through g_stats and populate with crisis count
        $(stats).each(function(d,k){
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
        return con_data;
};

//Function to consolidate media

function con_media(results){
        var g_results = {};
        $(results).each(function(d,k){
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
                                "date_end"       : date_end, 
                                "domain"         : domain, 
                                "period"         : period, 
                                "title"          : title, 
                                "url"        : url,
                                "name"           : name,
                                "type"           : type
                        };
                });
        return g_results;

}


  
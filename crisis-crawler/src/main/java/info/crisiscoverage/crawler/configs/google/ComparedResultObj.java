package info.crisiscoverage.crawler.configs.google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Strings;

import info.crisiscoverage.crawler.CrawlerConstants.Column;
import info.crisiscoverage.crawler.configs.google.AbstractGoogleConfig.DateRestrict;

public class ComparedResultObj {
	
	/** What are the row headers that are in play */
	final List<Column> rowHeaders;
	
	final Integer rawResultIdx;
	final Integer periodsBackIdx;
	
	/** What are the result headers that are in play */
	final List<Column> resultHeaders;
	
	final Integer comparedResultIdx;
	
	/** What is the value of the query distinct key that should be tested when populating this object*/
	final String queryDistinctKey;
	
	/** What is the Period*/
	final DateRestrict dateRestrict;
	
	/** Key is the PERIOD, value is the row. */
	final Map<Integer,List<String>> rowPeriodValMap = new TreeMap<>();
	
	/** Key is the PERIOD, value is the 'raw_result_count' */
	final Map<Integer,Integer> periodRawResultMap = new TreeMap<>();
	
	/** Key is the PERIOD, value is the 'compared_result_count' NOTE: THIS WILL BE REGENERATED UPON REQUEST. */
	final Map<Integer,Integer> periodCompareResultMap = new TreeMap<>();
	
	int minPeriod = -1;
	int maxPeriod = -1;
	
	/**
	 * Constructor.
	 * @param queryDistinctKey
	 * @param dateRestrict
	 * @param rowHeaders
	 * @param resultHeaders
	 * @throws Exception
	 */
	public ComparedResultObj(
			String queryDistinctKey, DateRestrict dateRestrict, List<Column> rowHeaders, List<Column> resultHeaders) throws Exception{
		this.queryDistinctKey = queryDistinctKey;
		this.dateRestrict = dateRestrict;
		this.rowHeaders = rowHeaders;
		this.resultHeaders = resultHeaders;
		
		//Save time by 1x getting rowHeader indexes.
		rawResultIdx = Integer.valueOf(rowHeaders.indexOf(Column.raw_result_count));
		periodsBackIdx = Integer.valueOf(rowHeaders.indexOf(Column.periods_back));
		
		//Save time by 1x getting resultHeader indexes.
		comparedResultIdx = Integer.valueOf(resultHeaders.indexOf(Column.compared_result_count));
	}
	
	/**
	 * Test for acceptance.
	 * @param row
	 * @return
	 */
	public boolean isAcceptedByQueryDistinctKey(List<String> row){
		String candidate = row.get(rowHeaders.indexOf(Column.query_distinct));
		if (!Strings.isNullOrEmpty(candidate)){
			
			//For other than '' / ALL results
			if (!Strings.isNullOrEmpty(queryDistinctKey)){
				return candidate.startsWith(queryDistinctKey);
			} else if (queryDistinctKey != null){
				DateRestrict dr = DateRestrict.dateRestrictOf(candidate); 
				int periodsBack = DateRestrict.periodsBackOf(candidate);
				if (dr != null && dr.equals(dateRestrict) && periodsBack > -1) return true;
			}
		}
		return false;
	}
	
	/**
	 * Add a row, assumes acceptance testing already done.
	 * @param row
	 */
	public void addRowToPeriodValMap(List<String> row){
		String rawPeriod = row.get(periodsBackIdx.intValue());
		
			try{
				int p =Integer.parseInt(rawPeriod.trim());
				
				String rawResult = row.get(rawResultIdx.intValue()); 
				int r =Integer.parseInt(rawResult.trim());
				
				rowPeriodValMap.put(p,row);
				periodRawResultMap.put(p, r);
				
				if (minPeriod < 1 || p < minPeriod) minPeriod = p;
				if (maxPeriod < 1 || p > maxPeriod) maxPeriod = p;
				
				System.out.println("... for '"+queryDistinctKey+"', adding row with period: "+p+", rawResult: "+r);
				return;
			} catch(Exception e){
				e.printStackTrace();
			}
		
		System.err.println("... unable to add row at #"+rowPeriodValMap.size()+", run acceptance test first -- isAcceptedByQueryDistinctKey().");
	}
	
	/**
	 * This is the heavy-lifting. Go through the rowPeriodValMap and determine the compared_result column and add it to the list.
	 * @param headers
	 * @return List<Map<Column,String>>
	 */
	public List<Map<Column,String>> populateCompareResult(){
		
		List<Map<Column,String>> rows = new ArrayList<>();
		System.out.println("--> POPULATING COMPARE RESULT, minPeriod: "+minPeriod+", maxPeriod: "+maxPeriod);
		if (minPeriod > 0 && maxPeriod > 0){

			//Figure out the compare values.
			periodCompareResultMap.clear();
			
			for(int i = maxPeriod; i >= minPeriod; i--){
				System.out.println("i: "+i);
				int raw = periodRawResultMap.get(Integer.valueOf(i)); 
				int r = raw;
				if (i+1 <=maxPeriod){
						r = raw - periodRawResultMap.get(Integer.valueOf(i+1));
				}
				System.out.println("... for queryDistinctKey: "+queryDistinctKey+" at period: "+i+", compare_result_count: "+r+
						", raw_result_count: '"+raw+"' ... dateRestrict: "+dateRestrict.name());
				
				periodCompareResultMap.put(Integer.valueOf(i), Integer.valueOf(r));
			}
			
			//Add the columns
			for (Map.Entry<Integer,List<String>> entry : rowPeriodValMap.entrySet()){
				Integer p = entry.getKey();
				List<String> row = entry.getValue();
				rows.add(adjustToResultColumns(row,p));
			}
		} else {
			System.err.println("WARNING ::: invalid minPeriod and/or maxPeriod, skipping 'compared_result_count' population.");
		}

		return rows;
	}
	
	/**
	 * Populate the Map with columns requested by results.
	 * @param row
	 * @param period
	 * @return
	 */
	protected Map<Column,String> adjustToResultColumns(List<String> row, Integer period){
		Map<Column,String> map = new HashMap<Column,String>();
		
		for (Column c : resultHeaders){
			String v = ""; 
			if (c.equals(Column.compared_result_count)){
				Integer vI = periodCompareResultMap.get(period);
				if (vI != null){
					System.out.println("... adding compared result: "+vI);
					v = vI.toString();
				} else System.err.println("... no compared_result_count found.");
			} else {
				int idx = rowHeaders.indexOf(c);
				System.out.println("rowHeaders idx is '"+idx+"' for column: "+c.name());
				if (idx > -1){
					v = row.get(idx);
				}
			}
			map.put(c,v == null? "" : v);
		}
		
		return map;
	}

	public String getQueryDistinctKey() {
		return queryDistinctKey;
	}

	public DateRestrict getDateRestrict() {
		return dateRestrict;
	}

	public Map<Integer, List<String>> getRowPeriodValMap() {
		return rowPeriodValMap;
	}

}

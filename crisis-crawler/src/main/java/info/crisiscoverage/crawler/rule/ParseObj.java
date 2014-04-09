package info.crisiscoverage.crawler.rule;

import info.crisiscoverage.crawler.CrawlerConstants;
import info.crisiscoverage.crawler.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;

/**
 * Parent Object for all ParseResults, including Meta.
 * Note: This is primarily for storage not processing.
 * 
 * @author mjohns
 * @param <O> accepted input and result format.
 */
public abstract class ParseObj<O> implements CrawlerConstants{
	
	protected WhichOne whichOneInputs;
	protected WhichOne whichOneResults;
	
	final protected Iterable<O> input;
	
	/* This is an iterable and will be returned as such! */
	final protected Collection<O> result = new LinkedList<O>();
	
	/* ioMap is used to store the original results for each input,
	 * not used in testing for the presence of results.*/
	final protected Map<O,Object> ioMap = new HashMap<O,Object>();
	
	protected String specialInput;
	protected String specialResult;

	/**
	 * Constructor.
	 * @param input
	 */
	public ParseObj(Iterable<O> input){
		this.input = input;
//		System.out.print("... on parseObj contructor, input ... "+sampleInputs());		
	}

	/**
	 * Get input;
	 * @return
	 */
	final public Iterable<O> getInput() {
		return input;
	}
	
	/**
	 * Get result.
	 * @return
	 */
	final public Iterable<O> getResult(){
		return result;
	}
	
	/**
	 * Add result.
	 * @param r O
	 */
	final public void addResultIfUnique(O r){
		if (isValid(r) && !result.contains(r)) result.add(r);
	}
	
	/**
	 * Add unique results.
	 * @param result Iterator<O>
	 */
	final public void addResultsIfUnique(Iterable<O> results){
		if (results == null) return;
		Iterator<O> it = results.iterator();
		while(it.hasNext())
			addResultIfUnique(it.next());
	}
	
	/**
	 * Get specialResult.
	 * @return
	 */
	final public String getSpecialResult() {
		return specialResult;
	}

	/**
	 * Set specialResult.
	 * @param specialResult
	 */
	final public void setSpecialResult(String specialResult) {
		this.specialResult = specialResult;
	}
	
	/**
	 * Get whichOne for results.
	 * @return
	 */
	final public WhichOne getWhichOneResults() {

		if (!isAnyResultValid()) whichOneResults = WhichOne.none_valid;
		else if (isSpecialResultValid()) whichOneResults = WhichOne.special_string;
		else whichOneResults = WhichOne.object;
		
		return whichOneResults;
	}

	/**
	 * Set whichOne for results. Useful for overriding behavior.
	 * @param whichOneResults
	 */
	final public void setWhichOneResults(WhichOne whichOneResults) {
		this.whichOneResults = whichOneResults;
	}	
	
	/**
	 * Are there any matches in results?
	 * @return
	 */
	final public boolean isAnyResultValid(){
		return isSpecialResultValid() || isResultValid();
	}
	
	
	/**
	 * return best input String or null.
	 */
	final public String asStringWhichOneInputs() {
		return asStringWhichOne(getWhichOneInputs(),false);
	}
	
	/**
	 * return best result String or null.
	 */
	final public String asStringWhichOneResults() {
		return asStringWhichOne(getWhichOneResults(),true);
	}
	
	/**
	 * return best String or null.
	 * @param whichOne
	 * @param isResults
	 */
	final public String asStringWhichOne(WhichOne whichOne, boolean isResults) {
		if (whichOne == null || whichOne.equals(WhichOne.none_valid)) return null;
		
		String s = null;
		switch(whichOne){
		case special_string:
			s = isResults? specialResult : specialInput;
			break;
		case object:
			s = isResults? asStringResult() : asStringInput();
			break;
		default:
			s = null;
		}
		
		return s;
	}
	
	/**
	 * Get whichOne for results.
	 * @return
	 */
	final public WhichOne getWhichOneInputs() {

		if (!isAnyInputValid()) whichOneInputs = WhichOne.none_valid;
		else if (isSpecialInputValid()) whichOneInputs = WhichOne.special_string;
		else whichOneInputs = WhichOne.object;
		
		return whichOneInputs;
	}

	/**
	 * Set whichOne for results. Useful for sub-classes to override value.
	 * @param whichOneInputs
	 */
	final public void setWhichOneInputs(WhichOne whichOneInputs) {
		this.whichOneInputs = whichOneInputs;
	}	
	
	/**
	 * Decide whether any input is valid for PROCESSING purposes.
	 * Note: this can be overridden.
	 * @return
	 */
	final public boolean isAnyInputValid(){
		return isSpecialInputValid() || isInputValid();
	}
	
	/**
	 * Write the best / whichOne to file (if valid).
	 * @param file
	 * @throws IOException 
	 */
	final public void writeWhichOneInputsToFile(Path file) throws IOException{
		writeToFile(file,getWhichOneInputs(),false);
	}
	
	/**
	 * Write the best / whichOne to file (if valid).
	 * @param file
	 * @throws IOException 
	 */
	final public void writeWhichOneResultsToFile(Path file) throws IOException{
		writeToFile(file,getWhichOneResults(),true);
	}
	
	/**
	 * Write to file (if valid).
	 * @param file
	 * @param whichOne
	 * @param isResults
	 * @throws IOException 
	 */
	final protected void writeToFile(Path file, WhichOne whichOne, boolean isResults) throws IOException{
		if (file == null || whichOne == null || whichOne.equals(WhichOne.none_valid)) return;

		switch(whichOne){
		case object: 
			if ((isResults && isResultValid()) || (!isResults && isInputValid())){
				IOUtils.write(file,  toIterableTypeString(isResults));
			}	
			return;
		default:
			String s = asStringWhichOne(whichOne, isResults);
			if (Strings.isNullOrEmpty(s)){
				System.err.println("... skipping write null/empty string to file for whichOne: "+whichOne.name()+", isResult? "+isResults);
				return;
			} else {
				IOUtils.write(file, s);
			}
		}
	}
	
	/**
	 * Useful for pretty printing.
	 * @return
	 */
	final public String sampleInputs(){
		return sample(getWhichOneInputs(),false);
	}
	
	/**
	 * Useful for pretty printing.
	 * @return
	 */
	final public String sampleResults(){
		return sample(getWhichOneResults(),true);
	}
	
	/**
	 * Useful for pretty printing.
	 * @param whichOne
	 * @param isResults
	 * @return String
	 */
	final protected String sample(WhichOne whichOne, boolean isResults){
		String s = asStringWhichOne(whichOne,isResults);
		if (Strings.isNullOrEmpty(s))
			return "... no match to rule";
		else return  "... return sample --> '"+IOUtils.subSetOf(s, defaultPrintMax, defaultStringSample)+"'";
	}
	
	/**
	 * Convenience method to flip any iterator into a List.
	 * @param iterator<O>
	 * @return List<O>
	 */
	final protected List<O> iterableAsList(Iterable<O> iterable){
		List<O> list = new ArrayList<O>();
		if (iterable == null) return list;
		Iterator<O> it = iterable.iterator();
		while (it.hasNext()){
			O o = it.next();
			list.add(o);
		}
		return list;
	}
	
	/**
	 * Get ioMap.
	 * @return
	 */
	final public Map<O, Object> getIoMap() {
		return ioMap;
	}
	
	/**
	 * Useful for pretty printing and write to file.
	 * @return
	 */
	final public String asStringInput(){
		return asString(false);
	}
	
	/**
	 * Useful for pretty printing and write to file.
	 * @return
	 */
	final public String asStringResult(){
		return asString(true);
	}
	
	/**
	 * It takes 1+ valid entry in input to be valid.
	 * @return
	 */
	public boolean isInputValid(){
		if (input == null){
			return false;
		}
		Iterator<O> it = input.iterator();
		while (it.hasNext()){
			O o = it.next();
			if (isValid(o)){
				return true;//need 1 valid.
			}
		}
		return false;
	}
	
	/**
	 * It takes 1+ valid entry in result to be valid.
	 * @return
	 */
	public boolean isResultValid(){
		for (O r : result){
			if (isValid(r)) return true;
		}
		return false;
	}
	
	/**
	 * Get specialInput.
	 * @return
	 */
	public String getSpecialInput() {
		return specialInput;
	}

	/**
	 * Set specialInput.
	 * @param specialInput
	 */
	public void setSpecialInput(String specialInput) {
		this.specialInput = specialInput;
	}	
	
	/**
	 * Decide whether stringResult is valid for MATCHING purposes.
	 * Note: this can be overridden.
	 * @return
	 */
	public boolean isSpecialInputValid(){
		return !Strings.isNullOrEmpty(specialInput);
	}
	
	/**
	 * Decide whether stringResult is valid for MATCHING purposes.
	 * Note: this can be overridden.
	 * @return
	 */
	public boolean isSpecialResultValid(){
		return !Strings.isNullOrEmpty(specialResult);
	}
	
	/**
	 * Convert input or result to String.
	 * @param isResults
	 * @return
	 */
	public abstract String asString(boolean isResults);
	
	/**
	 * Decide whether object is valid.
	 * @return
	 */
	public abstract boolean isValid(O o);

	/**
	 * Convert input or result to Iterable<String>. Useful for File write.
	 * @param isResults
	 * @return Iterable<String>
	 */
	public abstract Iterable<String> toIterableTypeString(boolean isResults);
	
	/**
	 * Generate an appropriate output from the input, applying ioMap.
	 * @return
	 */
	public ParseObj<O> newParseObjWithInputFromOutput() {
		ParseObj<O> parseObj = newParseObjWithInputFromOutputConcrete();
		parseObj.getIoMap().putAll(this.getIoMap());
		return parseObj;
	}
	
	/**
	 * Generate an appropriate output from the input for subclasses.
	 * @return ParseObj with an appropriate valid input set (no output!)
	 */
	protected abstract ParseObj<O> newParseObjWithInputFromOutputConcrete();
	
}

package com.ninjadin.pfmobile.non_android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class XmlEditor {
	// Add new data to parent tag, data is added just before the end tag
	final static public void addToParent(File copyFrom, File copyTo, InputStream dataFile,
			String startData, String endData, String parentTag, String parentAttr) throws IOException {
		FileInputStream copyFromStream = new FileInputStream(copyFrom);
		InputStreamReader copyFromSR = new InputStreamReader(copyFromStream);
		BufferedReader sourceReader = new BufferedReader(copyFromSR);
		
		BufferedReader insertData = null;
		if (dataFile != null) {
			InputStreamReader dataFileSR = new InputStreamReader(dataFile);
			insertData = new BufferedReader(dataFileSR);
		}
		
		FileOutputStream copyToStream = new FileOutputStream(copyTo);
		OutputStreamWriter copyToSR = new OutputStreamWriter(copyToStream);
		BufferedWriter destWriter = new BufferedWriter(copyToSR);
		
		// Find the first line of the data to be inserted
		String dataLine = null;
		if (dataFile != null) {
			dataLine = insertData.readLine();
			while (dataLine != null) {
				if (dataLine.trim().startsWith(startData)) {
					break;
				}
				dataLine = insertData.readLine();
			}
		}
		//Find the parent in the source file
		String sourceLine = sourceReader.readLine();
		while (sourceLine != null) {
			if (sourceLine.trim().startsWith("<" + parentTag + " " + parentAttr)) {
				break;
			}
			destWriter.write(sourceLine);
			destWriter.newLine();
			sourceLine = sourceReader.readLine();
		}
		//Find the end of the parent in the source file
		int depth = -1;
		while (sourceLine != null) {
			if (sourceLine.trim().startsWith("<" + parentTag + " ")) {
				depth += 1;
			}
			if (sourceLine.trim().startsWith("</" + parentTag + ">")) {
				if (depth == 0)
					break;
				else
					depth -= 1;
			}
			destWriter.write(sourceLine);
			destWriter.newLine();
			sourceLine = sourceReader.readLine();
		}
		// Insert the new child
		if (dataFile != null) {
			while (dataLine != null) {
				destWriter.write(dataLine);
				destWriter.newLine();
				if (dataLine.trim().startsWith(endData)) {
					break;
				}
				dataLine = insertData.readLine();
			}
		}
		// Copy the remainder of the source file
		while (sourceLine != null) {
			destWriter.write(sourceLine);
			destWriter.newLine();
			sourceLine = sourceReader.readLine();
		}
		destWriter.close();
		sourceReader.close();
		
	}
	
	final static public void replaceParent(File copyFrom, File copyTo, 
			String parentTag, String parentAttrs, String newContent) throws IOException {
		FileInputStream copyFromStream = new FileInputStream(copyFrom);
		InputStreamReader copyFromSR = new InputStreamReader(copyFromStream);
		BufferedReader sourceReader = new BufferedReader(copyFromSR);

		FileOutputStream copyToStream = new FileOutputStream(copyTo);
		OutputStreamWriter copyToSR = new OutputStreamWriter(copyToStream);
		BufferedWriter destWriter = new BufferedWriter(copyToSR);

		String sourceLine = " ";
		while (sourceLine != null) {
			sourceLine = sourceReader.readLine();
			if (sourceLine.trim().startsWith("<" + parentTag + " " + parentAttrs)) {
				break;
			}
			destWriter.write(sourceLine);
			destWriter.newLine();
		}
		destWriter.write(newContent);
		int depth = 0;
		while (sourceLine != null) {
			if (sourceLine.trim().startsWith("<" + parentTag + " "))
				depth += 1;
			if (sourceLine.trim().startsWith("</" + parentTag + ">")) {
				depth -= 1;
			if (depth == 0)
				break;
			}
			sourceLine = sourceReader.readLine();
		}
		sourceLine = sourceReader.readLine();
		while (sourceLine != null) {
			destWriter.write(sourceLine);
			destWriter.newLine();
			sourceLine = sourceReader.readLine();
		}
		sourceReader.close();
		destWriter.close();
	}
	// The only way to insert/replace is to copy the entire source file
	// copies lines "startData -> endData" from dataFile, inclusive
	// replaces lines "insertBefore -> continueOn" from sourceChar
	// if insertBefore == continueOn, this function inserts instead of replacing
	// customBefore is custom data added before the insertion/replacement
	// customAfter is custom data added after the insertion/replacement
	final static public void copyReplace(File copyFrom, File copyTo, InputStream dataFile,  
			String startData, String endData, String insertBefore, String continueOn, 
			String customBefore, String customAfter) throws IOException {
		FileInputStream copyFromStream = new FileInputStream(copyFrom);
		InputStreamReader copyFromSR = new InputStreamReader(copyFromStream);
		BufferedReader sourceReader = new BufferedReader(copyFromSR);
		
		BufferedReader insertData = null;
		if (dataFile != null) {
			InputStreamReader dataFileSR = new InputStreamReader(dataFile);
			insertData = new BufferedReader(dataFileSR);
		}
		
		FileOutputStream copyToStream = new FileOutputStream(copyTo);
		OutputStreamWriter copyToSR = new OutputStreamWriter(copyToStream);
		BufferedWriter destWriter = new BufferedWriter(copyToSR);
		
		// Find the first line of the data to be inserted
		String dataLine = null;
		if (dataFile != null) {
			dataLine = insertData.readLine();
			while (dataLine != null) {
				if (dataLine.trim().startsWith(startData)) {
					break;
				}
				dataLine = insertData.readLine();
			}
		}
		//Find the spot in the sourceChar to insert BEFORE
		String sourceLine = sourceReader.readLine();
		while (sourceLine != null) {
			if (sourceLine.trim().startsWith(insertBefore)) {
				break;
			}
			destWriter.write(sourceLine);
			destWriter.newLine();
			sourceLine = sourceReader.readLine();
		}
		// Insert the new data
		if (customBefore != null) {
			destWriter.write(customBefore);
			destWriter.newLine();
		}
		if (dataFile != null) {
			while (dataLine != null) {
				destWriter.write(dataLine);
				destWriter.newLine();
				if (dataLine.trim().startsWith(endData)) {
					break;
				}
				dataLine = insertData.readLine();
			}
		}
		if (customAfter != null) {
			destWriter.write(customAfter);
			destWriter.newLine();
		}
		// Find the spot to continue copying from
		// note that if continueOn == insertBefore, no lines are lost
		
		while (sourceLine != null) {
			if (sourceLine.trim().startsWith(continueOn)) {
				break;
			}
			sourceLine = sourceReader.readLine();
		}
		// Copy the remainder of the source character
		while (sourceLine != null) {
			destWriter.write(sourceLine);
			destWriter.newLine();
			sourceLine = sourceReader.readLine();
		}
		destWriter.close();
		sourceReader.close();
	}

}

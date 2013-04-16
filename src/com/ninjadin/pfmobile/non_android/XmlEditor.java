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

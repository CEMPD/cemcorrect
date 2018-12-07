package edu.unc.cem.correct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CemReader {
	private File file;
	private BufferedReader reader;

	private long count = 1;
	
	public CemReader(File file) {
		this.file = file;
	}
	
	public void open() throws FileNotFoundException {
		this.reader = new BufferedReader(new FileReader(file));
	}
	
	public void close() throws IOException {
		reader.close();
	}
	
	public void finalize() {
		reader = null;
		
		try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public List<String> read() {
		List<String> allLines = new ArrayList<String>();
		String line = null;
		
		try {
			while ((line = reader.readLine()) != null) { allLines.add(line); count++; }
		} catch (Exception e) {
			System.out.println("Error reading line " + count + "(" + file.getName() + "): " + line);
		} 
		
		return allLines;
	}
	
}

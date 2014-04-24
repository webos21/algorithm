package com.gmail.webos21.aquiz.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * AQBufferedReader : wrapper class of BufferedReader.
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class AQBufferedReader {
	private BufferedReader br;

	/**
	 * Constructor of AQBufferedReader based on the path string of input file
	 * 
	 * @param dataPath
	 *            the path string of input file
	 */
	public AQBufferedReader(String dataPath) {
		try {
			br = new BufferedReader(new FileReader(dataPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Read a line ended with CRLF
	 * 
	 * @return the string of a line
	 */
	public String readLine() {
		String ret = null;
		try {
			ret = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}

	/**
	 * Close the AQBufferedReader
	 */
	public void close() {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			br = null;
		}
	}
}

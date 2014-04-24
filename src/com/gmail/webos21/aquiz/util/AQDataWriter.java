package com.gmail.webos21.aquiz.util;

import java.io.FileWriter;
import java.io.IOException;

/**
 * AQDataWriter : wrapper class of FileWriter.
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class AQDataWriter {
	private static final String CRLF = "\r\n";

	private FileWriter fw;

	/**
	 * Constructor of AQDataWriter based on the path string of result file
	 * 
	 * @param dataPath
	 *            the path string of result file
	 */
	public AQDataWriter(String dataPath) {
		try {
			fw = new FileWriter(dataPath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Write a line with CRLF
	 * 
	 * @param line
	 *            the line string to be written
	 */
	public void writeln(String line) {
		try {
			fw.write(line);
			fw.write(CRLF);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write a string without CRLF
	 * 
	 * @param line
	 *            the string to be written
	 */
	public void write(String line) {
		try {
			fw.write(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Close the AQDataWriter
	 */
	public void close() {
		if (fw != null) {
			try {
				fw.flush();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			fw = null;
		}
	}
}

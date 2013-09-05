package org.xi.aquiz.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AQBufferedReader {
	private BufferedReader br;

	public AQBufferedReader(String dataPath) {
		try {
			br = new BufferedReader(new FileReader(dataPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

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

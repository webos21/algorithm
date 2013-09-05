package org.xi.aquiz.util;

import java.io.FileWriter;
import java.io.IOException;

public class AQDataWriter {
	private static final String CRLF = "\r\n";

	private FileWriter fw;

	public AQDataWriter(String dataPath) {
		try {
			fw = new FileWriter(dataPath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	public void writeln(String line) {
		try {
			fw.write(line);
			fw.write(CRLF);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String line) {
		try {
			fw.write(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

package com.gmail.webos21.aquiz.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.gmail.webos21.aquiz.model.AQModel;

public class BufferedReaderTest implements AQModel {
	@Override
	public void aqRun(String dataPath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(dataPath));
			String lstr;
			while ((lstr = br.readLine()) != null) {
			}
			System.out.println(lstr);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
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
}

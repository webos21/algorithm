package com.gmail.webos21.aquiz.test;

import com.gmail.webos21.aquiz.model.AQModel;
import com.gmail.webos21.aquiz.util.AQBufferedReader;

public class AQBufferedReaderTest implements AQModel {

	@Override
	public void aqRun(String dataPath) {
		AQBufferedReader gbr = new AQBufferedReader(dataPath);
		String lstr;
		while ((lstr = gbr.readLine()) != null) {
		}
		System.out.println(lstr);
		if (gbr != null) {
			gbr.close();
			gbr = null;
		}
	}

}

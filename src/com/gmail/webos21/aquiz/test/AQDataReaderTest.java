package com.gmail.webos21.aquiz.test;

import com.gmail.webos21.aquiz.model.AQModel;
import com.gmail.webos21.aquiz.util.AQDataReader;

@SuppressWarnings("deprecation")
public class AQDataReaderTest implements AQModel {

	@Override
	public void aqRun(String dataPath) {
		AQDataReader dr = new AQDataReader(dataPath);
		String lstr;
		while ((lstr = dr.readLine()) != null) {
		}
		System.out.println(lstr);
		if (dr != null) {
			dr.close();
			dr = null;
		}
	}
}

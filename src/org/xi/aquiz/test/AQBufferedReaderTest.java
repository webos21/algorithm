package org.xi.aquiz.test;

import org.xi.aquiz.model.AQModel;
import org.xi.aquiz.util.AQBufferedReader;

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

package org.xi.aquiz.test;

import org.xi.aquiz.model.AQModel;
import org.xi.aquiz.util.AQDataReader;

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

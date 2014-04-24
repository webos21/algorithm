package com.gmail.webos21.aquiz.gcj.c2014.qr;

import com.gmail.webos21.aquiz.model.AQModel;
import com.gmail.webos21.aquiz.util.AQBufferedReader;
import com.gmail.webos21.aquiz.util.AQDataWriter;
import com.gmail.webos21.aquiz.util.AQMisc;

/**
 * Google Code Jam - Contest2014 - QR
 * 
 * Problem B. Cookie Clicker Alpha
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class CookieClickerAlpha implements AQModel {

	/**
	 * the game objects of cases
	 */
	private GameBean[] gameCases;

	/**
	 * parse the input-data and create the game objects
	 * 
	 * @param dataPath
	 *            the path of the input-data
	 */
	private void parseData(String dataPath) {
		String lstr;
		int setNum;
		int i;

		// create the reader of input-data
		AQBufferedReader aqbr = new AQBufferedReader(dataPath);

		// get the size of data-set
		lstr = aqbr.readLine();
		setNum = Integer.parseInt(lstr);

		// create the cases of games
		gameCases = new GameBean[setNum];
		for (i = 0; setNum > 0; i++, setNum--) {
			lstr = aqbr.readLine();
			String[] m = lstr.split(" ");
			if (m != null && m.length == 3) {
				gameCases[i] = new GameBean((i + 1), m[0], m[1], m[2]);
			}
		}

		// close the reader of input-data
		aqbr.close();
	}

	/**
	 * execute the game-cases by sequential or threads
	 */
	private void processData() {
		// sequential processing of game-cases
		for (int i = 0; i < gameCases.length; i++) {
			gameCases[i].run();
		}
	}

	/**
	 * create the file of output-data
	 * 
	 * @param dataPath
	 *            the path of the input-data
	 */
	private void generateResult(String dataPath) {
		// create the writer of output-data
		AQDataWriter aqdw = new AQDataWriter(AQMisc.getResultFilePath(dataPath));

		// write the result string to file
		for (int i = 0; i < gameCases.length; i++) {
			aqdw.writeln("Case #" + (i + 1) + ": " + gameCases[i].getResult());
		}

		// close the writer of output-data
		aqdw.close();
	}

	/**
	 * the entry method of executing (org.xi.gcj.model.CodeJamModel)
	 * 
	 * @param dataPath
	 *            the path of the input-data
	 */
	@Override
	public void aqRun(String dataPath) {
		parseData(dataPath);
		processData();
		generateResult(dataPath);
	}

	/**
	 * the class of game object
	 */
	private class GameBean implements Runnable {
		private int caseNo;
		private String result;

		double C, F, X;

		public GameBean(int cn, String c, String f, String x) {
			this.caseNo = cn;
			this.C = Double.parseDouble(c);
			this.F = Double.parseDouble(f);
			this.X = Double.parseDouble(x);
		}

		public String getResult() {
			return result;
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();

			// the summary of Game-Case
			sb.append("Case #").append(caseNo).append(": ");
			sb.append(C).append(' ').append(F).append(' ').append(X)
					.append('\n');

			double prevFarmSec = C / 2.0;
			double prevTotalSec = X / 2.0;
			double curFarmSec = 0.0;
			double curTotalSec = 0.0;
			sb.append('\t').append(prevFarmSec).append(' ')
					.append(prevTotalSec).append('\n');

			int cnt = 1;
			while (true) {
				curFarmSec = prevFarmSec + C / (2.0 + cnt * F);
				curTotalSec = prevFarmSec + X / (2.0 + cnt * F);

				sb.append('\t').append(curFarmSec).append(' ')
						.append(curTotalSec).append('\n');

				if (prevTotalSec < curTotalSec) {
					break;
				} else {
					prevFarmSec = curFarmSec;
					prevTotalSec = curTotalSec;
					cnt++;
				}
			}
			result = String.format("%.07f", prevTotalSec);
			sb.append("\tResult : ").append(result).append('\n');

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}
	}

}

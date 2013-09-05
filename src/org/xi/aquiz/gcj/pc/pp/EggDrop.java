package org.xi.aquiz.gcj.pc.pp;

import org.xi.aquiz.model.AQModel;
import org.xi.aquiz.util.AQBufferedReader;
import org.xi.aquiz.util.AQDataWriter;
import org.xi.aquiz.util.AQMisc;

/**
 * Google Code Jam - Practice Contests - Practice Problem
 * 
 * Problem C. Egg Drop
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class EggDrop implements AQModel {

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
		AQBufferedReader gbr = new AQBufferedReader(dataPath);

		// get the size of data-set
		lstr = gbr.readLine();
		setNum = Integer.parseInt(lstr);

		// create the cases of games
		gameCases = new GameBean[setNum];
		for (i = 0; setNum > 0; i++, setNum--) {
			lstr = gbr.readLine();
			String[] m = lstr.split(" ");
			if (m != null && m.length == 3) {
				gameCases[i] = new GameBean((i + 1), Integer.parseInt(m[0]),
						Integer.parseInt(m[1]), Integer.parseInt(m[2]));
			}
		}

		// close the reader of input-data
		gbr.close();
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
		AQDataWriter gdw = new AQDataWriter(AQMisc.getResultFilePath(dataPath));

		// write the result string to file
		for (int i = 0; i < gameCases.length; i++) {
			gdw.writeln("Case #" + (i + 1) + ": " + gameCases[i].getResult());
		}

		// close the writer of output-data
		gdw.close();
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

		private int origF;
		private int origD;
		private int origB;

		private long maxF;
		private int minD;
		private int minB;

		private String result;

		public GameBean(int cn, int origF, int origD, int origB) {
			this.caseNo = cn;

			this.origF = origF;
			this.origD = origD;
			this.origB = origB;
		}

		public String getResult() {
			return result;
		}

		private long getMaxF(int F, int D, int B) {
			if (D < B) {
				throw new IllegalArgumentException("D < B!!!!!!!!");
			}
			long ret = 0;
			int expn = D - 1;
			for (int i = 0; i < B; i++) {
				ret += (1 << expn);
				expn--;
			}
			if ((D - B) > 0) {
				for (int i = 0; i < (D - B); i++) {
					ret -= (1 << i);
				}
			}
			return ret;
		}

		private int getMinD(int F, int B, int x) {
			// if we have only one egg, then the error is same as floors
			if (B == 1) {
				return F;
			}

			// find the x : (F < x ^ B)
			int t = 1;
			for (int i = 1; i < B; i++) {
				t *= x;
			}
			if (t > F) {
				return x;
			} else {
				// recursive-call
				return getMinD(F, B, (x + 1));
			}
		}

		private int getMinB(int F, int D) {
			int modFD = F % D;
			int divFD = F / D;
			int ret = (modFD > 0) ? (divFD + 1) : divFD;
			return ret;
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();

			// the summary of Game-Case
			sb.append("Case #").append(caseNo).append(": ");
			sb.append("origF=").append(origF).append(", ");
			sb.append("origD=").append(origD).append(", ");
			sb.append("origB=").append(origB).append('\n');

			// Get maxF
			maxF = getMaxF(origF, origD, origB);
			sb.append("         Fmax = ").append(maxF).append('\n');

			// Get minD
			minD = getMinD(origF, origB, 1);
			sb.append("         Dmin = ").append(minD).append('\n');

			// Get minB
			minB = getMinB(origF, origD);
			sb.append("         Bmin = ").append(minB).append('\n');

			// set the result
			result = (maxF >= 4294967296L || maxF < 0) ? "-1 " : Long
					.toString(maxF) + " ";
			result += minD + " ";
			result += minB;

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}
	}

}

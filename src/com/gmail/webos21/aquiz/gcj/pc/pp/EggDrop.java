package com.gmail.webos21.aquiz.gcj.pc.pp;

import java.util.Arrays;

import com.gmail.webos21.aquiz.model.AQModel;
import com.gmail.webos21.aquiz.util.AQBufferedReader;
import com.gmail.webos21.aquiz.util.AQDataWriter;
import com.gmail.webos21.aquiz.util.AQMisc;

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
				gameCases[i] = new GameBean((i + 1), Integer.parseInt(m[0]),
						Integer.parseInt(m[1]), Integer.parseInt(m[2]));
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
		genCache();
		parseData(dataPath);
		processData();
		generateResult(dataPath);
	}

	private static final long MAX_F_VALUE = 4294967296l;
	private static final int LARGE_F_VALUE = -1;
	private static final int MAX_B_INDEX = 32;

	private static final int F_CACHE_SIZE = 100000;

	private long[][] cacheFmax;

	private void genCache() {
		cacheFmax = new long[F_CACHE_SIZE][MAX_B_INDEX];
		Arrays.fill(cacheFmax[0], 1);
		for (int d = 1; d < F_CACHE_SIZE; d++) {
			cacheFmax[d][0] = d + 1;
			for (int b = 1; b < MAX_B_INDEX; b++) {
				if ((cacheFmax[d - 1][b] == LARGE_F_VALUE)
						|| (cacheFmax[d - 1][b - 1] == LARGE_F_VALUE)) {
					cacheFmax[d][b] = LARGE_F_VALUE;
				} else {
					cacheFmax[d][b] = cacheFmax[d - 1][b] + 1
							+ cacheFmax[d - 1][b - 1];
					if (cacheFmax[d][b] >= MAX_F_VALUE) {
						cacheFmax[d][b] = LARGE_F_VALUE;
					}
				}
			}
		}
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

		private long getMaxF(int D, int B) {
			if (B > MAX_B_INDEX) {
				B = MAX_B_INDEX;
			}
			if (B == 1) {
				return D;
			}
			if (D > F_CACHE_SIZE) {
				return -1;
			}
			return cacheFmax[D - 1][B - 1];
		}

		private int getMinD(long F, int B, int maxD) {
			for (int d = 1; d <= maxD; d++) {
				long maxF = getMaxF(d, B);
				if ((maxF == LARGE_F_VALUE) || (maxF >= F)) {
					return d;
				}
			}
			throw new IllegalStateException(String.format(
					"D not found, F=%1$d, B=%2$d, Dmax=%3$d", F, B, maxD));

		}

		private int getMinB(long F, int D, int maxB) {
			for (int b = 1; b <= maxB; b++) {
				long maxF = getMaxF(D, b);
				if ((maxF == LARGE_F_VALUE) || (maxF >= F)) {
					return b;
				}
			}
			throw new IllegalStateException(String.format(
					"B not found, F=%1$d, D=%2$d, max B=%3$d", F, D, maxB));
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
			maxF = getMaxF(origD, origB);
			sb.append("         Fmax = ").append(maxF).append('\n');

			// Get minD
			minD = getMinD(origF, origB, origD);
			sb.append("         Dmin = ").append(minD).append('\n');

			// Get minB
			minB = getMinB(origF, origD, origB);
			sb.append("         Bmin = ").append(minB).append('\n');

			// set the result
			result = String.format("%1$d %2$d %3$d", maxF, minD, minB);

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}
	}

}

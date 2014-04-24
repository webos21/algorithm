package com.gmail.webos21.aquiz.gcj.c2014.qr;

import java.util.Arrays;

import com.gmail.webos21.aquiz.model.AQModel;
import com.gmail.webos21.aquiz.util.AQBufferedReader;
import com.gmail.webos21.aquiz.util.AQDataWriter;
import com.gmail.webos21.aquiz.util.AQMisc;

/**
 * Google Code Jam - Contest2014 - QR
 * 
 * Problem D. Deceitful War
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class DeceitfulWar implements AQModel {

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
			String blocks = aqbr.readLine();
			String naomi = aqbr.readLine();
			String ken = aqbr.readLine();
			if (blocks != null && blocks.length() > 0 && naomi != null
					&& naomi.length() > 0 && ken != null && ken.length() > 0) {
				gameCases[i] = new GameBean((i + 1), blocks, naomi, ken);
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

		private int blocks;
		private double[] naomi;
		private double[] ken;

		public GameBean(int cn, String blocks, String naomis, String kens) {
			this.caseNo = cn;
			this.blocks = Integer.parseInt(blocks);
			this.naomi = new double[this.blocks];
			this.ken = new double[this.blocks];

			String[] nb = naomis.split(" ");
			String[] kb = kens.split(" ");
			for (int i = 0; i < this.blocks; i++) {
				naomi[i] = Double.parseDouble(nb[i]);
			}
			for (int i = 0; i < this.blocks; i++) {
				ken[i] = Double.parseDouble(kb[i]);
			}
			Arrays.sort(naomi);
			Arrays.sort(ken);
		}

		public String getResult() {
			return result;
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();

			// the summary of Game-Case
			sb.append("Case #").append(caseNo).append(": ");
			// sb.append("\n\tOrigianl)");
			sb.append("\n\t");
			for (int i = 0; i < blocks; i++) {
				sb.append(naomi[i]).append(' ');
			}
			sb.append("\n\t");
			for (int i = 0; i < blocks; i++) {
				sb.append(ken[i]).append(' ');
			}
			sb.append('\n');

			// check clone
			double tbn[] = naomi.clone();
			double tbk[] = ken.clone();
			// sb.append("\n\tCloned)");
			// sb.append("\n\t");
			// for (int i = 0; i < blocks; i++) {
			// sb.append(tbn[i]).append(' ');
			// }
			// sb.append("\n\t");
			// for (int i = 0; i < blocks; i++) {
			// sb.append(tbk[i]).append(' ');
			// }
			// sb.append('\n');

			// Check the optimal way
			boolean won = false;
			int cnt = 0;

			int optWin = 0;
			int optLose = 0;
			while (optWin + optLose < blocks) {
				double nv = naomi[cnt];
				won = false;
				for (int i = 0; i < blocks; i++) {
					if (nv > tbk[i]) {
						won = true;
						tbk[i] = Double.MAX_VALUE;
						optWin++;
						break;
					}
				}
				if (!won) {
					optLose++;
				}
				cnt++;
			}

			cnt = 0;
			int kenWin = 0;
			int kenLose = 0;
			while (kenWin + kenLose < blocks) {
				double kv = ken[cnt];
				won = false;
				for (int i = 0; i < blocks; i++) {
					if (kv > tbn[i]) {
						won = true;
						tbn[i] = Double.MAX_VALUE;
						kenWin++;
						break;
					}
				}
				if (!won) {
					kenLose++;
				}
				cnt++;
			}

			sb.append("\tOptimal Win : ").append(optWin);
			sb.append(",\tNormal Win : ").append(kenLose);

			// Set Result
			result = optWin + " " + kenLose;

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}
	}

}

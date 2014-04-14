package org.xi.aquiz.gcj.c2014.qr;

import org.xi.aquiz.model.AQModel;
import org.xi.aquiz.util.AQBufferedReader;
import org.xi.aquiz.util.AQDataWriter;
import org.xi.aquiz.util.AQMisc;

/**
 * Google Code Jam - Contest2014 - QR
 * 
 * Problem A. Magic Trick
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class MagicTrick implements AQModel {

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
			String c1 = aqbr.readLine();
			String[] s1 = new String[4];
			s1[0] = aqbr.readLine();
			s1[1] = aqbr.readLine();
			s1[2] = aqbr.readLine();
			s1[3] = aqbr.readLine();

			String c2 = aqbr.readLine();
			String[] s2 = new String[4];
			s2[0] = aqbr.readLine();
			s2[1] = aqbr.readLine();
			s2[2] = aqbr.readLine();
			s2[3] = aqbr.readLine();

			gameCases[i] = new GameBean((i + 1), c1, s1, c2, s2);
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

		private int sel1;
		private int[][] set1;

		private int sel2;
		private int[][] set2;

		public GameBean(int cn, String c1, String[] s1, String c2, String[] s2) {
			this.caseNo = cn;

			this.sel1 = Integer.parseInt(c1);
			this.set1 = new int[4][4];
			for (int i = 0; i < 4; i++) {
				String[] rows = s1[i].split(" ");
				for (int j = 0; j < 4; j++) {
					this.set1[i][j] = Integer.parseInt(rows[j]);
				}
			}

			this.sel2 = Integer.parseInt(c2);
			this.set2 = new int[4][4];
			for (int i = 0; i < 4; i++) {
				String[] rows = s2[i].split(" ");
				for (int j = 0; j < 4; j++) {
					this.set2[i][j] = Integer.parseInt(rows[j]);
				}
			}

		}

		public String getResult() {
			return result;
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();

			// the summary of Game-Case
			sb.append("Case #").append(caseNo).append(": \n");
			sb.append(sel1);
			for (int i = 0; i < 4; i++) {
				sb.append('\t');
				for (int j = 0; j < 4; j++) {
					sb.append(set1[i][j]).append(' ');
				}
				sb.append('\n');
			}
			sb.append(sel2);
			for (int i = 0; i < 4; i++) {
				sb.append('\t');
				for (int j = 0; j < 4; j++) {
					sb.append(set2[i][j]).append(' ');
				}
				sb.append('\n');
			}

			int[] sel1Line = set1[sel1 - 1];
			int[] sel2Line = set2[sel2 - 1];

			sb.append("\tFirst chosen line : ");
			for (int i = 0; i < 4; i++) {
				sb.append(sel1Line[i]).append(' ');
			}
			sb.append('\n');

			sb.append("\tSecond chosen line : ");
			for (int i = 0; i < 4; i++) {
				sb.append(sel2Line[i]).append(' ');
			}
			sb.append('\n');

			int cntFound = 0;
			int foundNum = -1;
			sb.append("Found : ");
			for (int i = 0; i < 4; i++) {
				int checkNum = sel1Line[i];
				for (int j = 0; j < 4; j++) {
					if (sel2Line[j] == checkNum) {
						foundNum = sel2Line[j];
						sb.append(foundNum).append(' ');
						cntFound++;
					}
				}
			}
			sb.append(" => ").append(cntFound);

			if (cntFound == 1) {
				result = Integer.toString(foundNum);
			} else if (cntFound > 1) {
				result = "Bad magician!";
			} else {
				result = "Volunteer cheated!";
			}

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}
	}

}

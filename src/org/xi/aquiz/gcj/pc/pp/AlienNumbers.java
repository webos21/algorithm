package org.xi.aquiz.gcj.pc.pp;

import org.xi.aquiz.model.AQModel;
import org.xi.aquiz.util.AQBufferedReader;
import org.xi.aquiz.util.AQDataWriter;
import org.xi.aquiz.util.AQMisc;

/**
 * Google Code Jam - Practice Contests - Practice Problem
 * 
 * Problem A. Alien Numbers
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class AlienNumbers implements AQModel {

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
		AQDataWriter aqdw = new AQDataWriter(
				AQMisc.getResultFilePath(dataPath));

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
		private String alienNum;
		private String srcLang;
		private String dstLang;
		private String result;

		public GameBean(int cn, String an, String sl, String tl) {
			caseNo = cn;
			alienNum = an;
			srcLang = sl;
			dstLang = tl;
		}

		public String getResult() {
			return result;
		}

		private String DecimalToRadix(int i, int radix) {
			boolean negative = (i < 0);

			char buf[] = new char[33];
			int charPos = 32;

			if (!negative) {
				i = -i;
			}

			while (i <= -radix) {
				buf[charPos--] = dstLang.charAt(-(i % radix));
				i = i / radix;
			}
			buf[charPos] = dstLang.charAt(-i);

			if (negative) {
				buf[--charPos] = '-';
			}

			return new String(buf, charPos, (33 - charPos));
		}

		public void run() {
			StringBuilder sb = new StringBuilder();

			int srcRadix = srcLang.length();
			int dstRadix = dstLang.length();
			int numLen = alienNum.length();

			// the summary of Game-Case
			sb.append("Case #").append(caseNo).append(": ");
			sb.append("an = ").append(alienNum).append('(').append(numLen)
					.append(')');
			sb.append("/ sl = ").append(srcLang).append('(').append(srcRadix)
					.append(')');
			sb.append("/ tl = ").append(dstLang).append('(').append(dstRadix)
					.append(')');
			sb.append('\n');

			// change to decimal
			int numOrig = 0;
			for (int i = 0; i < numLen; i++) {
				sb.append(srcLang.indexOf(alienNum.charAt(i)));
				numOrig += srcLang.indexOf(alienNum.charAt(i))
						* Math.pow(srcRadix, (numLen - i - 1));
			}
			sb.append('\n');
			sb.append("numOrig = ").append(numOrig);

			// change to target radix
			result = DecimalToRadix(numOrig, dstRadix);
			sb.append(" / result = ").append(result);

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}
	}
}

package com.gmail.webos21.aquiz.gcj.c2014.r1a;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Google Code Jam - Contest2014 - Round1A
 * 
 * Problem B. Sample
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class SampleClassB {

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
	 * the entry method of executing (com.gmail.webos21.gcj.model.AQJamModel)
	 * 
	 * @param dataPath
	 *            the path of the input-data
	 */
	public void aqRun(String dataPath) {
		parseData(dataPath);
		processData();
		generateResult(dataPath);
	}

	/**
	 * Entry function of Application - main()
	 * 
	 * @param args
	 *            the string array of the given arguments
	 */
	public static void main(String[] args) {
		SampleClassB mainObject = new SampleClassB();
		mainObject.aqRun(args[0]);
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

	/**
	 * AQBufferedReader : wrapper class of BufferedReader.
	 * 
	 * @author Cheolmin Jo (webos21@gmail.com)
	 */
	private class AQBufferedReader {
		private BufferedReader br;

		/**
		 * Constructor of AQBufferedReader based on the path string of input
		 * file
		 * 
		 * @param dataPath
		 *            the path string of input file
		 */
		public AQBufferedReader(String dataPath) {
			try {
				br = new BufferedReader(new FileReader(dataPath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
		}

		/**
		 * Read a line ended with CRLF
		 * 
		 * @return the string of a line
		 */
		public String readLine() {
			String ret = null;
			try {
				ret = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return ret;
		}

		/**
		 * Close the AQBufferedReader
		 */
		public void close() {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
	}

	/**
	 * AQDataWriter : wrapper class of FileWriter.
	 * 
	 * @author Cheolmin Jo (webos21@gmail.com)
	 */
	private class AQDataWriter {
		private static final String CRLF = "\r\n";

		private FileWriter fw;

		/**
		 * Constructor of AQDataWriter based on the path string of result file
		 * 
		 * @param dataPath
		 *            the path string of result file
		 */
		public AQDataWriter(String dataPath) {
			try {
				fw = new FileWriter(dataPath);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
		}

		/**
		 * Write a line with CRLF
		 * 
		 * @param line
		 *            the line string to be written
		 */
		public void writeln(String line) {
			try {
				fw.write(line);
				fw.write(CRLF);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Write a string without CRLF
		 * 
		 * @param line
		 *            the string to be written
		 */
		@SuppressWarnings("unused")
		public void write(String line) {
			try {
				fw.write(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Close the AQDataWriter
		 */
		public void close() {
			if (fw != null) {
				try {
					fw.flush();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fw = null;
			}
		}
	}

	/**
	 * AQMisc : utility functions.
	 * 
	 * @author Cheolmin Jo (webos21@gmail.com)
	 */
	private static class AQMisc {
		/**
		 * Get the path string of result-file based on the path of input-file
		 * 
		 * @param dataPath
		 *            the path of input-file
		 * @return the path string of result-file
		 */
		public static String getResultFilePath(String dataPath) {
			File f = new File(dataPath);
			if (f.exists()) {
				String dname = f.getAbsolutePath();
				int lastDot = dname.lastIndexOf('.');
				if (lastDot < 0) {
					return dname + "-result";
				} else {
					return dname.substring(0, lastDot) + "-result"
							+ dname.substring(lastDot);
				}
			} else {
				throw new IllegalArgumentException("FileNotFound!!!");
			}
		}

		/**
		 * Get the merged string of the string array by delimiter.
		 * 
		 * @param src
		 *            the string array
		 * @param delim
		 *            the delimiter for merging
		 * @return the merged string of string array
		 */
		@SuppressWarnings("unused")
		public static String mergeArray(String[] src, String delim) {
			if (src.length == 0) {
				return "";
			} else if (src.length == 1) {
				return src[0];
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append(src[0]);
				for (int i = 1; i < src.length; i++) {
					sb.append(delim).append(src[i]);
				}
				return sb.toString();
			}
		}

		/**
		 * Get the merged string of the integer array by delimiter.
		 * 
		 * @param src
		 *            the integer array
		 * @param delim
		 *            the delimiter for merging
		 * @return the merged string of string array
		 */
		@SuppressWarnings("unused")
		public static String mergeArray(int[] src, String delim) {
			if (src.length == 0) {
				return "";
			} else if (src.length == 1) {
				return Integer.toString(src[0]);
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append(src[0]);
				for (int i = 1; i < src.length; i++) {
					sb.append(delim).append(src[i]);
				}
				return sb.toString();
			}
		}

		/**
		 * Get the merged string of the integer-object array by delimiter.
		 * 
		 * @param src
		 *            the integer-object array
		 * @param delim
		 *            the delimiter for merging
		 * @return the merged string of string array
		 */
		@SuppressWarnings("unused")
		public static String mergeArray(Integer[] src, String delim) {
			if (src.length == 0) {
				return "";
			} else if (src.length == 1) {
				return src[0].toString();
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append(src[0]);
				for (int i = 1; i < src.length; i++) {
					sb.append(delim).append(src[i]);
				}
				return sb.toString();
			}
		}
	}
}

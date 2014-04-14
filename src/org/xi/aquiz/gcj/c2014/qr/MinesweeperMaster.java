package org.xi.aquiz.gcj.c2014.qr;

import org.xi.aquiz.model.AQModel;
import org.xi.aquiz.util.AQBufferedReader;
import org.xi.aquiz.util.AQDataWriter;
import org.xi.aquiz.util.AQMisc;

/**
 * Google Code Jam - Contest2014 - QR
 * 
 * Problem C. Minesweeper Master
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class MinesweeperMaster implements AQModel {

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
			aqdw.writeln("Case #" + (i + 1) + ":" + gameCases[i].getResult());
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

		private int rows;
		private int cols;
		private int mines;

		private char[][] mineField;

		public GameBean(int cn, String r, String c, String m) {
			this.caseNo = cn;
			this.rows = Integer.parseInt(r);
			this.cols = Integer.parseInt(c);
			this.mines = Integer.parseInt(m);

			this.mineField = new char[rows][cols];
		}

		public String getResult() {
			return result;
		}

		// use a integer to a 3x3 matrix
		// 1<<9 1<<8 1<<7
		// 1<<6 1<<5 1<<4
		// 1<<3 1<<2 1<<1
		private boolean checkPos(char[][] field, int maxRow, int maxCol, int r,
				int c) {
			int rpos = 0;
			int cpos = 0;

			int result = 0;

			rpos = r - 1;
			cpos = c - 1;
			if (rpos >= 0 && cpos >= 0 && field[rpos][cpos] != '.') {
				result |= (1 << 9);
			}
			rpos = r - 1;
			cpos = c;
			if (rpos >= 0 && cpos >= 0 && field[rpos][cpos] != '.') {
				result |= (1 << 8);
			}
			rpos = r - 1;
			cpos = c + 1;
			if (rpos >= 0 && cpos < maxCol && field[rpos][cpos] != '.') {
				result |= (1 << 7);
			}
			rpos = r;
			cpos = c - 1;
			if (rpos >= 0 && cpos >= 0 && field[rpos][cpos] != '.') {
				result |= (1 << 6);
			}
			rpos = r;
			cpos = c;
			if (rpos >= 0 && cpos >= 0 && field[rpos][cpos] != '.') {
				result |= (1 << 5);
			}
			rpos = r;
			cpos = c + 1;
			if (rpos >= 0 && cpos < maxCol && field[rpos][cpos] != '.') {
				result |= (1 << 4);
			}
			rpos = r + 1;
			cpos = c - 1;
			if (rpos < maxRow && cpos >= 0 && field[rpos][cpos] != '.') {
				result |= (1 << 3);
			}
			rpos = r + 1;
			cpos = c;
			if (rpos < maxRow && cpos >= 0 && field[rpos][cpos] != '.') {
				result |= (1 << 2);
			}
			rpos = r + 1;
			cpos = c + 1;
			if (rpos < maxRow && cpos < maxCol && field[rpos][cpos] != '.') {
				result |= (1 << 1);
			}

			return (result == 0);
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();

			// the summary of Game-Case
			sb.append("Case #").append(caseNo).append(": ");
			sb.append(rows).append(' ').append(cols).append(' ').append(mines);

			// fill the mines
			int remainMine = mines;
			if (rows >= 2 && cols > 2) {
				for (int r = rows - 1; r >= 0; r--) {
					for (int c = cols - 1; c >= 2; c--) {
						if (remainMine > 0) {
							mineField[r][c] = '*';
						} else {
							mineField[r][c] = '.';
						}
						remainMine--;
					}
				}
				for (int r = rows - 1; r >= 0; r--) {
					for (int c = 1; c >= 0; c--) {
						if (remainMine > 0) {
							mineField[r][c] = '*';
						} else {
							mineField[r][c] = '.';
						}
						remainMine--;
					}
				}
			} else {
				for (int r = rows - 1; r >= 0; r--) {
					for (int c = cols - 1; c >= 0; c--) {
						if (remainMine > 0) {
							mineField[r][c] = '*';
						} else {
							mineField[r][c] = '.';
						}
						remainMine--;
					}
				}
			}

			// check conditions
			int spaces = rows * cols - mines;
			if (spaces == 0) {
				result = "\nImpossible";
			} else {
				boolean found = false;
				if (spaces == 1) {
					mineField[0][0] = 'c';
					found = true;
				} else {
					int cnt = 0;
					while (cnt < spaces) {
						int r = (int) (cnt / cols);
						int c = cnt % cols;
						if (checkPos(mineField, rows, cols, r, c)) {
							mineField[r][c] = 'c';
							found = true;
							break;
						}
						cnt++;
					}
				}

				if (!found) {
					result = "\nImpossible";
				} else {
					// result mine-field
					StringBuilder rsb = new StringBuilder();
					for (int r = 0; r < rows; r++) {
						rsb.append('\n');
						for (int c = 0; c < cols; c++) {
							rsb.append(mineField[r][c]);
						}
					}
					// set result
					result = rsb.toString();
				}
			}

			// print result
			sb.append(result);

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}
	}

}

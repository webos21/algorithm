package org.xi.aquiz.gcj.pc.pp;

import org.xi.aquiz.model.AQModel;
import org.xi.aquiz.util.AQBufferedReader;
import org.xi.aquiz.util.AQDataWriter;
import org.xi.aquiz.util.AQMisc;

/**
 * Google Code Jam - Practice Contests - Practice Problem
 * 
 * Problem B. Always Turn Left
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class AlwaysTurnLeft implements AQModel {

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
			if (m != null && m.length == 2) {
				gameCases[i] = new GameBean((i + 1), m[0], m[1]);
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
			System.out.println("[Case #" + (i + 1) + "]");
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
			aqdw.write("Case #" + (i + 1) + ":\n" + gameCases[i].getResult());
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
		// testTile();
		parseData(dataPath);
		processData();
		generateResult(dataPath);
	}

	/**
	 * the class of game object
	 */
	private class GameBean implements Runnable {
		private int caseNo;
		private String fromEntrance;
		private String fromExit;
		private String result;

		public GameBean(int cn, String fn, String fs) {
			this.caseNo = cn;
			this.fromEntrance = fn;
			this.fromExit = fs;
		}

		public String getResult() {
			return result;
		}

		private ExploreInfo explorePath(int trial, ExploreInfo ei, String path,
				StringBuilder sb) {
			int i;

			int hPosMax = ei.hPosMax;
			int hPosMin = ei.hPosMin;
			int vPosMax = ei.vPosMax;
			int vPosMin = ei.vPosMin;
			int hPos = ei.hPos;
			int vPos = ei.vPos;
			HeadingFor dir = ei.dir;

			int pathLen = path.length();

			for (i = 0; i < pathLen; i++) {
				char c = path.charAt(i);
				if (c == 'W' && (i == 0 || i == pathLen - 1)) {
					continue;
				}
				switch (dir) {
				case H4SOUTH:
					if (c == 'W') {
						vPos++;
						vPosMax = Math.max(vPosMax, vPos);
					} else if (c == 'L') {
						dir = HeadingFor.H4EAST;
					} else if (c == 'R') {
						dir = HeadingFor.H4WEST;
					}
					break;
				case H4NORTH:
					if (c == 'W') {
						vPos--;
						vPosMin = Math.min(vPosMin, vPos);
					} else if (c == 'L') {
						dir = HeadingFor.H4WEST;
					} else if (c == 'R') {
						dir = HeadingFor.H4EAST;
					}
					break;
				case H4WEST:
					if (c == 'W') {
						hPos--;
						hPosMin = Math.min(hPosMin, hPos);
					} else if (c == 'L') {
						dir = HeadingFor.H4SOUTH;
					} else if (c == 'R') {
						dir = HeadingFor.H4NORTH;
					}
					break;
				case H4EAST:
					if (c == 'W') {
						hPos++;
						hPosMax = Math.max(hPosMax, hPos);
					} else if (c == 'L') {
						dir = HeadingFor.H4NORTH;
					} else if (c == 'R') {
						dir = HeadingFor.H4SOUTH;
					}
					break;
				case H4NONE:
				default:
					throw new IllegalArgumentException(
							"HeadingFor is invalid!!");

				}
			}

			// logging the entrance or exit
			if (trial == 0) {
				sb.append("         Entrance->Exit : ");
			} else {
				sb.append("         Exit->Entrance: ");
			}

			// logging the positions
			sb.append("hPos = ").append(hPos);
			sb.append(" / hPosMax = ").append(hPosMax);
			sb.append(" / hPosMin = ").append(hPosMin);
			sb.append(" | vPos = ").append(vPos);
			sb.append(" / vPosMax = ").append(vPosMax);
			sb.append(" / vPosMin = ").append(vPosMin);

			if (trial == 0) {
				sb.append(" / Exit Direction = ").append(dir);
			} else {
				sb.append(" / Entrance Direction = ").append(dir);
			}
			sb.append('\n');

			return new ExploreInfo(dir, hPosMax, hPosMin, vPosMax, vPosMin,
					hPos, vPos);
		}

		private ExploreInfo markTile(int go, ExploreInfo ei, String path,
				Tile[][] tiles, StringBuilder sb) {
			int i;

			int hPos = ei.hPos;
			int vPos = ei.vPos;
			HeadingFor dir = ei.dir;

			int pathLen = path.length();

			Tile curTile = tiles[vPos][hPos];

			if (go == 0) {
				sb.append("         fromEntrance :\n");
			} else {
				sb.append("         fromExit :\n");
			}

			char c = 'W';
			for (i = 0; i < pathLen; i++) {
				c = path.charAt(i);
				sb.append("             ");
				sb.append('[');
				if (i < 10) {
					sb.append('0');
				}
				sb.append(i).append('/');
				if (pathLen - 1 < 10) {
					sb.append('0');
				}
				sb.append(pathLen - 1).append("] ");
				sb.append("Position : c");
				sb.append(hPos).append(", r").append(vPos);
				sb.append(" / Direction : ").append(dir);
				sb.append(" / Action : ").append(c).append('\n');

				if (c == 'W') {
					switch (dir) {
					case H4SOUTH:
						if (i != 0) {
							curTile.unsetWallSouth();
							if (i != (pathLen - 1)) {
								vPos++;
							}
						}
						if (i != (pathLen - 1)) {
							curTile = tiles[vPos][hPos];
							curTile.unsetWallNorth();
						}
						break;
					case H4NORTH:
						if (i != 0) {
							curTile.unsetWallNorth();
							if (i != (pathLen - 1)) {
								vPos--;
							}
						}
						if (i != (pathLen - 1)) {
							curTile = tiles[vPos][hPos];
							curTile.unsetWallSouth();
						}
						break;
					case H4WEST:
						if (i != 0) {
							curTile.unsetWallWest();
							if (i != (pathLen - 1)) {
								hPos--;
							}
						}
						if (i != (pathLen - 1)) {
							curTile = tiles[vPos][hPos];
							curTile.unsetWallEast();
						}
						break;
					case H4EAST:
						if (i != 0) {
							curTile.unsetWallEast();
							if (i != (pathLen - 1)) {
								hPos++;
							}
						}
						if (i != (pathLen - 1)) {
							curTile = tiles[vPos][hPos];
							curTile.unsetWallWest();
						}
						break;
					case H4NONE:
					default:
						throw new IllegalArgumentException(
								"HeadingFor is invalid!!");
					}
				} else {
					dir = HeadingFor.changeDirection(dir, c);
				}
			}
			return new ExploreInfo(dir, 0, 0, 0, 0, hPos, vPos);
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();

			// the summary of Game-Case
			sb.append("Case #").append(caseNo).append(": ");
			sb.append("fromEntrance = ").append(fromEntrance).append('(')
					.append(fromEntrance.length()).append(')');
			sb.append(" / fromExit = ").append(fromExit).append('(')
					.append(fromExit.length()).append(')');
			sb.append('\n');

			// the start direction is alway to south
			// because the entrance is on northern wall.
			ExploreInfo eifs = new ExploreInfo(HeadingFor.H4SOUTH, 0, 0, 0, 0,
					0, 0);
			ExploreInfo eit1;
			ExploreInfo eit2;

			// retrieve from Entrance to Exit
			eit1 = explorePath(0, eifs, fromEntrance, sb);

			int tCol1 = eit1.hPosMax - eit1.hPosMin + 1;
			int tRow1 = eit1.vPosMax - eit1.vPosMin + 1;
			int exitCol = (eit1.hPosMin < 0) ? eit1.hPos - eit1.hPosMin
					: eit1.hPos;
			int exitRow = (eit1.vPosMin < 0) ? eit1.vPos - eit1.vPosMin
					: eit1.vPos;
			sb.append("         Tile : c");
			sb.append(tCol1).append(" x r").append(tRow1);
			sb.append(" / Exit Pos : c");
			sb.append(exitCol).append(", r").append(exitRow);
			sb.append('\n');

			ExploreInfo eife = new ExploreInfo(HeadingFor.toOpposite(eit1.dir),
					(tCol1 - 1), 0, (tRow1 - 1), 0, exitCol, exitRow);

			// retrieve from Exit to Entrance
			eit2 = explorePath(1, eife, fromExit, sb);

			int tCol2 = eit2.hPosMax - eit2.hPosMin + 1;
			int tRow2 = eit2.vPosMax - eit2.vPosMin + 1;
			int enterCol = (eit2.hPosMin < 0) ? eit2.hPos - eit2.hPosMin
					: eit2.hPos;
			int enterRow = (eit2.vPosMin < 0) ? eit2.vPos - eit2.vPosMin
					: eit2.vPos;
			sb.append("         Tile : c");
			sb.append(tCol2).append(" x r").append(tRow2);
			sb.append(" / Enter Pos : c");
			sb.append(enterCol).append(", r").append(enterRow);
			sb.append('\n');

			int tileCol = Math.max(tCol1, tCol2);
			int tileRow = Math.max(tRow1, tRow2);

			// create the tiles
			Tile[][] tiles = new Tile[tileRow][tileCol];
			for (int i = 0; i < tileRow; i++) {
				for (int j = 0; j < tileCol; j++) {
					tiles[i][j] = new Tile(i, j);
				}
			}

			// mark the tiles
			eifs.hPos = enterCol;
			eifs.vPos = enterRow;
			try {
				eit1 = markTile(0, eifs, fromEntrance, tiles, sb);
				eife.hPos = eit1.hPos;
				eife.vPos = eit1.vPos;
				eit2 = markTile(1, eife, fromExit, tiles, sb);
			} catch (Exception e) {
				System.out.println(sb);
				e.printStackTrace();
				System.exit(-1);
			}

			// logging and make the result
			sb.append("         Result:\n");
			System.out.println(sb);
			sb.delete(0, sb.length());
			for (int i = 0; i < tileRow; i++) {
				for (int j = 0; j < tileCol; j++) {
					sb.append(tiles[i][j].getGoValue());
				}
				sb.append('\n');
			}
			this.result = sb.toString();

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}
	}

	/**
	 * the enumeration of direction
	 */
	private enum HeadingFor {
		H4NONE(0), H4NORTH(0x01), H4SOUTH(0x02), H4WEST(0x04), H4EAST(0x08);

		private int value;

		private HeadingFor(int val) {
			this.value = val;
		}

		@SuppressWarnings("unused")
		public static HeadingFor valueOf(int val) {
			HeadingFor result = H4NONE;
			final HeadingFor[] dirs = HeadingFor.values();
			for (HeadingFor dir : dirs) {
				if (dir.value == val) {
					result = dir;
					break;
				}
			}
			return result;
		}

		public static HeadingFor changeDirection(HeadingFor curDir, char act) {
			if (act == 'L') {
				switch (curDir) {
				case H4SOUTH:
					return HeadingFor.H4EAST;
				case H4NORTH:
					return HeadingFor.H4WEST;
				case H4WEST:
					return HeadingFor.H4SOUTH;
				case H4EAST:
					return HeadingFor.H4NORTH;
				case H4NONE:
				default:
					throw new IllegalArgumentException(
							"HeadingFor is invalid!!");
				}
			} else if (act == 'R') {
				switch (curDir) {
				case H4SOUTH:
					return HeadingFor.H4WEST;
				case H4NORTH:
					return HeadingFor.H4EAST;
				case H4WEST:
					return HeadingFor.H4NORTH;
				case H4EAST:
					return HeadingFor.H4SOUTH;
				case H4NONE:
				default:
					throw new IllegalArgumentException(
							"HeadingFor is invalid!!");
				}
			}
			throw new IllegalArgumentException("Action is invalid!!");
		}

		public static HeadingFor toOpposite(HeadingFor dir) {
			switch (dir) {
			case H4NORTH:
				return H4SOUTH;
			case H4SOUTH:
				return H4NORTH;
			case H4WEST:
				return H4EAST;
			case H4EAST:
				return H4WEST;
			case H4NONE:
			default:
				return H4NONE;
			}
		}
	}

	private class ExploreInfo {
		public HeadingFor dir;
		public int hPosMax;
		public int hPosMin;
		public int vPosMax;
		public int vPosMin;
		public int hPos;
		public int vPos;

		public ExploreInfo(HeadingFor h4, int hMax, int hMin, int vMax,
				int vMin, int hPos, int vPos) {
			this.dir = h4;
			this.hPosMax = hMax;
			this.hPosMin = hMin;
			this.vPosMax = vMax;
			this.vPosMin = vMin;
			this.hPos = hPos;
			this.vPos = vPos;
		}
	}

	@SuppressWarnings("unused")
	private void testTile() {
		StringBuilder sb = new StringBuilder();
		Tile test = new Tile(0, 0);

		sb.append("===========================\n");
		sb.append("=    Testing the Tile     =\n");
		sb.append("===========================\n");

		// charater 1
		test.setWallSouth();
		test.setWallWest();
		test.setWallEast();
		sb.append("Char#1 : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater 2
		test.reset();
		test.setWallNorth();
		test.setWallWest();
		test.setWallEast();
		sb.append("Char#2 : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater 3
		test.reset();
		test.setWallWest();
		test.setWallEast();
		sb.append("Char#3 : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater 4
		test.reset();
		test.setWallNorth();
		test.setWallSouth();
		test.setWallEast();
		sb.append("Char#4 : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater 5
		test.reset();
		test.setWallSouth();
		test.setWallEast();
		sb.append("Char#5 : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater 6
		test.reset();
		test.setWallNorth();
		test.setWallEast();
		sb.append("Char#6 : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater 7
		test.reset();
		test.setWallEast();
		sb.append("Char#7 : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater 8
		test.reset();
		test.setWallNorth();
		test.setWallSouth();
		test.setWallWest();
		sb.append("Char#8 : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater 9
		test.reset();
		test.setWallSouth();
		test.setWallWest();
		sb.append("Char#9 : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater a
		test.reset();
		test.setWallNorth();
		test.setWallWest();
		sb.append("Char#a : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater b
		test.reset();
		test.setWallWest();
		sb.append("Char#b : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater c
		test.reset();
		test.setWallNorth();
		test.setWallSouth();
		sb.append("Char#c : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater d
		test.reset();
		test.setWallSouth();
		sb.append("Char#d : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater e
		test.reset();
		test.setWallNorth();
		sb.append("Char#e : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		// charater f
		test.reset();
		sb.append("Char#f : Wall-State = ").append(test.getWallValue());
		sb.append(" / Go-Value = ").append(test.getGoValue()).append('\n');

		System.out.println(sb);
	}

	/**
	 * the tile of maze
	 */
	private class Tile {
		private byte wallState;

		private int hPos;
		private int vPos;

		public Tile(int row, int col) {
			// Start with the all-rounded wall
			wallState = 0x0f;
			// Start with the no wall
			// wallState = 0x00;
			vPos = row;
			hPos = col;
		}

		@SuppressWarnings("unused")
		public int getRow() {
			return vPos;
		}

		@SuppressWarnings("unused")
		public int getCol() {
			return hPos;
		}

		public String getGoValue() {
			int ret = (wallState ^ 0x0F);
			return Integer.toHexString(ret);
		}

		public int getWallValue() {
			return wallState;
		}

		public void setWallNorth() {
			wallState |= 0x01;
		}

		public void setWallSouth() {
			wallState |= 0x02;
		}

		public void setWallWest() {
			wallState |= 0x04;
		}

		public void setWallEast() {
			wallState |= 0x08;
		}

		public void unsetWallNorth() {
			wallState &= 0x0e;
		}

		public void unsetWallSouth() {
			wallState &= 0x0d;
		}

		public void unsetWallWest() {
			wallState &= 0x0b;
		}

		public void unsetWallEast() {
			wallState &= 0x07;
		}

		public void reset() {
			wallState = 0;
		}
	}
}

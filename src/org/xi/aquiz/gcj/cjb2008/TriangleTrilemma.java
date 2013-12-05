package org.xi.aquiz.gcj.cjb2008;

import java.util.Arrays;

import org.xi.aquiz.model.AQModel;
import org.xi.aquiz.util.AQBufferedReader;
import org.xi.aquiz.util.AQDataWriter;
import org.xi.aquiz.util.AQMisc;

/**
 * Google Code Jam - Practice Contests - Code Jam Beta 2008
 * 
 * Problem A. Triangle Trilemma
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class TriangleTrilemma implements AQModel {

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
			if (m != null && m.length == 6) {
				gameCases[i] = new GameBean((i + 1), m);
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

		private Point[] points;
		private int state;
		private TriangleType ttype;

		public GameBean(int cn, String[] pts) {
			this.caseNo = cn;

			points = new Point[3];
			for (int i = 0; i < points.length; i++) {
				int x = Integer.parseInt(pts[i * 2]);
				int y = Integer.parseInt(pts[i * 2 + 1]);
				points[i] = new Point(x, y);
			}

			state = 0;
		}

		public String getResult() {
			return result;
		}

		private boolean checkBySameXY(Point[] points, StringBuilder sb) {
			if ((points[0].getX() == points[1].getX() && points[1].getX() == points[2]
					.getX())
					|| (points[0].getY() == points[1].getY() && points[1]
							.getY() == points[2].getY())) {
				// log the reason
				sb.append("\n         Not a triangle: same X or same Y!!");

				// Get the result
				state = 0;
				ttype = TriangleType.valueOf(state);

				// logging and make the result
				sb.append("\n         Result: ").append(ttype);
				this.result = ttype.toString();

				// print out the logs
				sb.append('\n');
				System.out.println(sb);

				return true;
			} else {
				return false;
			}
		}

		private boolean checkByLength(double[] lineLen, StringBuilder sb) {
			if (lineLen[0] + lineLen[1] <= lineLen[2]
					|| lineLen[1] + lineLen[2] <= lineLen[0]
					|| lineLen[2] + lineLen[0] <= lineLen[1]) {
				// log the reason
				sb.append("\n         Not a triangle: one length is greater than sum of the others!!");

				// Get the result
				state = 0;
				ttype = TriangleType.valueOf(state);

				// logging and make the result
				sb.append("\n         Result: ").append(ttype);
				this.result = ttype.toString();

				// print out the logs
				sb.append('\n');
				System.out.println(sb);

				return true;
			} else {
				return false;
			}
		}

		private boolean checkByAngle(double[] angles, StringBuilder sb) {
			if (angles[0] == 180.0 || angles[1] == 180.0 || angles[2] == 180.0) {
				// log the reason
				sb.append("\n         Not a triangle: three points are on a line!!");

				// Get the result
				state = 0;
				ttype = TriangleType.valueOf(state);

				// logging and make the result
				sb.append("\n         Result: ").append(ttype);
				this.result = ttype.toString();

				// print out the logs
				sb.append('\n');
				System.out.println(sb);

				return true;
			} else {
				return false;
			}
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();

			// the summary of Game-Case
			sb.append("Case #").append(caseNo).append(": ");
			sb.append("\n         Points = ").append(Arrays.toString(points));

			// check the triangle by same (x, y)
			if (checkBySameXY(points, sb)) {
				return;
			}

			double[] llens = new double[3];
			int[] lsqrt = new int[3];

			// check the 3-length
			for (int i = 0; i < llens.length; i++) {
				int ni = (i + 1 >= llens.length) ? 0 : i + 1;
				llens[i] = points[i].getDistance(points[ni]);
				lsqrt[i] = points[i].getSquare(points[ni]);
			}
			sb.append("\n         Lines = ").append(Arrays.toString(llens));
			sb.append("\n         Squares = ").append(Arrays.toString(lsqrt));

			// check the triangle by length
			if (checkByLength(llens, sb)) {
				return;
			}

			// isosceles or scalene
			if (lsqrt[0] == lsqrt[1] || lsqrt[1] == lsqrt[2]
					|| lsqrt[2] == lsqrt[0]) {
				state |= 0x010;
			} else {
				state |= 0x020;
			}

			// compute the angles
			double[] thetas = new double[3];
			double[] angles = new double[3];
			thetas[0] = Math.acos((lsqrt[0] + lsqrt[2] - lsqrt[1])
					/ (2 * llens[0] * llens[2]));
			angles[0] = Math.toDegrees(thetas[1]);
			thetas[1] = Math.acos((lsqrt[0] + lsqrt[1] - lsqrt[1])
					/ (2 * llens[0] * llens[1]));
			angles[1] = Math.toDegrees(thetas[2]);
			thetas[2] = Math.acos((lsqrt[1] + lsqrt[2] - lsqrt[0])
					/ (2 * llens[1] * llens[2]));
			angles[2] = Math.toDegrees(thetas[0]);
			sb.append("\n         Thetas = ").append(Arrays.toString(thetas));
			sb.append("\n         Angles = ").append(Arrays.toString(angles));

			// check the triangle by angles
			if (checkByAngle(angles, sb)) {
				return;
			}

			// define by angles
			if (lsqrt[0] + lsqrt[1] == lsqrt[2]
					|| lsqrt[1] + lsqrt[2] == lsqrt[0]
					|| lsqrt[2] + lsqrt[0] == lsqrt[1]) {
				// right triangle
				state |= 0x004;
			} else if (lsqrt[0] + lsqrt[1] < lsqrt[2]
					|| lsqrt[1] + lsqrt[2] < lsqrt[0]
					|| lsqrt[2] + lsqrt[0] < lsqrt[1]) {
				// obtuse triangle
				state |= 0x002;
			} else {
				// acute triangle
				state |= 0x001;
			}

			// Get the result
			ttype = TriangleType.valueOf(state);

			// logging and make the result
			sb.append("\n         Result: ").append(ttype);
			sb.append("(0x0").append(Integer.toHexString(state)).append(')');
			this.result = ttype.toString();

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}

		private class Point {
			private int x;
			private int y;

			public Point(int x, int y) {
				this.x = x;
				this.y = y;
			}

			public int getX() {
				return x;
			}

			public int getY() {
				return y;
			}

			public double getDistance(Point p) {
				int dx = (p.x - this.x);
				int dy = (p.y - this.y);
				return Math.sqrt((dx * dx) + (dy * dy));
			}

			public int getSquare(Point p) {
				int dx = (p.x - this.x);
				int dy = (p.y - this.y);
				return ((dx * dx) + (dy * dy));
			}

			@Override
			public String toString() {
				return "x=" + x + "/y=" + y;
			}
		}
	}

	/**
	 * the enumeration of the triangle types
	 */
	private enum TriangleType {
		TT_NONE(0x0), TT_ACUTE(0x001), TT_OBTUSE(0x002), TT_RIGHT(0x004), TT_ISOSCELES(
				0x010), TT_ISOSCELES_ACUTE(0x011), TT_ISOSCELES_OBTUSE(0x012), TT_ISOSCELES_RIGHT(
				0x014), TT_SCALENE(0x020), TT_SCALENE_ACUTE(0x021), TT_SCALENE_OBTUSE(
				0x022), TT_SCALENE_RIGHT(0x024);

		private int value;

		private TriangleType(int val) {
			this.value = val;
		}

		public static TriangleType valueOf(int val) {
			TriangleType result = TT_NONE;
			final TriangleType[] ttypes = TriangleType.values();
			for (TriangleType tt : ttypes) {
				if (tt.value == val) {
					result = tt;
					break;
				}
			}
			return result;
		}

		@Override
		public String toString() {
			final String nt = "not a triangle";
			if (value == 0) {
				return nt;
			}

			StringBuilder sb = new StringBuilder();

			final String isosceles = "isosceles";
			final String scalene = "scalene";
			if ((value & 0x010) == 0x010) {
				sb.append(isosceles);
			}
			if ((value & 0x020) == 0x020) {
				sb.append(scalene);
			}

			final String acute = "acute";
			final String obtuse = "obtuse";
			final String right = "right";
			if ((value & 0x001) == 0x001) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(acute);
			}
			if ((value & 0x002) == 0x002) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(obtuse);
			}
			if ((value & 0x004) == 0x004) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(right);
			}
			if (sb.length() > 0) {
				sb.append(" triangle");
			}

			return sb.toString();
		}
	}
}

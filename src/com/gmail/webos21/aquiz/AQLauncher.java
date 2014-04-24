package com.gmail.webos21.aquiz;

import com.gmail.webos21.aquiz.gcj.c2014.qr.CookieClickerAlpha;
import com.gmail.webos21.aquiz.gcj.c2014.qr.DeceitfulWar;
import com.gmail.webos21.aquiz.gcj.c2014.qr.MagicTrick;
import com.gmail.webos21.aquiz.gcj.c2014.qr.MinesweeperMaster;
import com.gmail.webos21.aquiz.gcj.cjb2008.ThePriceIsWrong;
import com.gmail.webos21.aquiz.gcj.cjb2008.TriangleTrilemma;
import com.gmail.webos21.aquiz.gcj.pc.pp.AlienNumbers;
import com.gmail.webos21.aquiz.gcj.pc.pp.AlwaysTurnLeft;
import com.gmail.webos21.aquiz.gcj.pc.pp.EggDrop;
import com.gmail.webos21.aquiz.gcj.pc.pp.ShoppingPlan;
import com.gmail.webos21.aquiz.test.AQBufferedReaderTest;

/**
 * Application Launcher for the algorithm quiz
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class AQLauncher {

	private static final String aqlLine = "===================================\n";
	private static final String aqlMark = "=     Algorithm Quiz Launcher     =\n";

	private static StringBuilder sbBuf;

	/**
	 * Print the usage message to console
	 */
	public static void Usage() {
		sbBuf.delete(0, sbBuf.length());
		sbBuf.append(aqlLine);
		sbBuf.append(aqlMark);
		sbBuf.append(aqlLine);
		sbBuf.append("Usage)\n");
		sbBuf.append("java -jar CodingTest input-file.txt");
		System.out.println(sbBuf);
	}

	/**
	 * Print the given arguments to console
	 * 
	 * @param args
	 *            the string array of the given arguments
	 */
	public static void CheckArgs(String[] args) {
		sbBuf.delete(0, sbBuf.length());
		sbBuf.append(aqlLine);
		sbBuf.append(aqlMark);
		sbBuf.append(aqlLine);
		sbBuf.append("Arguments)\n");
		for (int i = 0; i < args.length; i++) {
			sbBuf.append("args[").append(i).append("] ");
			sbBuf.append(args[i]).append("\n");
		}
		System.out.println(sbBuf);
	}

	public static void Profiling(String dataPath) {
		long stime = System.nanoTime();

		// BufferedReaderTest brt = new BufferedReaderTest();
		// brt.gcjRun(dataPath);

		// GCJDataReaderTest gdrt = new GCJDataReaderTest();
		// gdrt.gcjRun(dataPath);

		AQBufferedReaderTest gbrt = new AQBufferedReaderTest();
		gbrt.aqRun(dataPath);

		long etime = System.nanoTime();

		System.out.println("stime = " + stime + " / etime = " + etime + " = "
				+ (etime - stime));
	}

	public static void PC_PP_AlienNumbers(String dataPath) {
		AlienNumbers an = new AlienNumbers();
		an.aqRun(dataPath);
	}

	public static void PC_PP_AlwaysTurnLeft(String dataPath) {
		AlwaysTurnLeft atl = new AlwaysTurnLeft();
		atl.aqRun(dataPath);
	}

	public static void PC_PP_EggDrop(String dataPath) {
		EggDrop eggDrop = new EggDrop();
		eggDrop.aqRun(dataPath);
	}

	public static void PC_PP_ShoppingPlan(String dataPath) {
		ShoppingPlan sp = new ShoppingPlan();
		sp.aqRun(dataPath);
	}

	public static void PC_CJB2008_TriangleTrilemma(String dataPath) {
		TriangleTrilemma tt = new TriangleTrilemma();
		tt.aqRun(dataPath);
	}

	public static void PC_CJB2008_ThePriceIsWrong(String dataPath) {
		ThePriceIsWrong pw = new ThePriceIsWrong();
		pw.aqRun(dataPath);
	}

	public static void C2014_MagicTrick(String dataPath) {
		MagicTrick mt = new MagicTrick();
		mt.aqRun(dataPath);
	}

	public static void C2014_CookieClickerAlpha(String dataPath) {
		CookieClickerAlpha cca = new CookieClickerAlpha();
		cca.aqRun(dataPath);
	}

	public static void C2014_MinesweeperMaster(String dataPath) {
		MinesweeperMaster mm = new MinesweeperMaster();
		mm.aqRun(dataPath);
	}

	public static void C2014_DeceitfulWar(String dataPath) {
		DeceitfulWar dw = new DeceitfulWar();
		dw.aqRun(dataPath);
	}

	/**
	 * Entry function of Application - main()
	 * 
	 * @param args
	 *            the string array of the given arguments
	 */
	public static void main(String[] args) {
		if (sbBuf == null) {
			sbBuf = new StringBuilder();
		}

		if (args.length < 1) {
			Usage();
			// CheckArgs(args);
			return;
		}

		// Profiling
		// Profiling(args[0]);

		// C2014_MagicTrick(args[0]);
		// C2014_CookieClickerAlpha(args[0]);
		C2014_MinesweeperMaster(args[0]);
		// C2014_DeceitfulWar(args[0]);
	}

}

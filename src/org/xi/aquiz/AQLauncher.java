package org.xi.aquiz;

import org.xi.aquiz.gcj.cjb2008.TriangleTrilemma;
import org.xi.aquiz.gcj.pc.pp.AlienNumbers;
import org.xi.aquiz.gcj.pc.pp.AlwaysTurnLeft;
import org.xi.aquiz.gcj.pc.pp.EggDrop;
import org.xi.aquiz.gcj.pc.pp.ShoppingPlan;
import org.xi.aquiz.test.AQBufferedReaderTest;

public class AQLauncher {

	private static final String gcjLine = "===================================\n";
	private static final String gcjMark = "=    Google Code Jam Launcher     =\n";

	private static StringBuilder sbBuf;

	public static void Usage() {
		sbBuf.delete(0, sbBuf.length());
		sbBuf.append(gcjLine);
		sbBuf.append(gcjMark);
		sbBuf.append(gcjLine);
		sbBuf.append("Usage)\n");
		sbBuf.append("java -jar CodingTest input-file.txt");
		System.out.println(sbBuf);
	}

	public static void CheckArgs(String[] args) {
		sbBuf.delete(0, sbBuf.length());
		sbBuf.append(gcjLine);
		sbBuf.append(gcjMark);
		sbBuf.append(gcjLine);
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

		PC_CJB2008_TriangleTrilemma(args[0]);
	}

}

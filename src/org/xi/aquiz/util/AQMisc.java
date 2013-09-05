package org.xi.aquiz.util;

import java.io.File;

public class AQMisc {
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

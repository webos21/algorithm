package org.xi.aquiz.util;

import java.io.File;

/**
 * AQMisc : utility functions.
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class AQMisc {
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

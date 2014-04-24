package com.gmail.webos21.aquiz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * AQDataReader : No call of System.arrayCopy()
 * <p>
 * The size of input file is less than 200kb <br/>
 * (http://code.google.com/codejam/problem-preparation.html)
 * </p>
 * <p>
 * If the file size is greater than 200kb, you must check the variables -
 * <code>AQ_DR_BUFFER_SIZE</code>, <code>AQ_DR_BUFFER_LAST</code>.
 * </p>
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 * 
 * @deprecated This class is not used because of performance issue. Use the
 *             <code>AQBufferedReader</code>.
 */
public class AQDataReader {
	private static final int AQ_DR_BUFFER_SIZE = 32768;
	private static final int AQ_DR_BUFFER_LAST = 32767;

	private static final int AQ_DR_LINEBUF_SIZE = 8192;

	private static final int AQ_DR_THRESHHOLD = 8192;
	private static final int AQ_DR_READ_SIZE = 16384;

	private String filePath;

	private File fileInstance;
	private int filePos;
	private int fileSize;
	private boolean fileEOF;

	private FileInputStream fileIS;

	// { Queue Buffer & Operation variables
	private byte[] dataBuf;
	private int dataBufLen;
	private int dataBufRPos;
	private int dataBufWPos;

	// }

	// Total size of reading
	private int readLen;

	// Line Buffer
	private byte[] lineBuf;

	public AQDataReader(String dataPath) {
		this.dataBuf = new byte[AQ_DR_BUFFER_SIZE];
		this.lineBuf = new byte[AQ_DR_LINEBUF_SIZE];

		this.filePath = dataPath;

		this.fileInstance = new File(this.filePath);
		this.fileSize = (int) fileInstance.length();

		try {
			fileIS = new FileInputStream(this.fileInstance);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new IllegalArgumentException(ioe);
		}
		loadDataBuf();
	}

	private int loadDataBuf() {
		int reqSize = 0;
		int retSize = 0;

		if (fileSize < filePos + AQ_DR_READ_SIZE) {
			reqSize = fileSize - filePos;
			fileEOF = true;
		} else {
			reqSize = AQ_DR_READ_SIZE;
		}

		if (dataBufWPos + reqSize > AQ_DR_BUFFER_SIZE) {
			int cutSize = AQ_DR_BUFFER_SIZE - dataBufWPos;
			try {
				retSize = fileIS.read(dataBuf, dataBufWPos, cutSize);
				retSize += fileIS.read(dataBuf, 0, reqSize - cutSize);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			dataBufWPos = reqSize - cutSize;
		} else {
			try {
				retSize = fileIS.read(dataBuf, dataBufWPos, reqSize);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			dataBufWPos += reqSize;
		}
		if (reqSize != retSize) {
			System.out.println("the real read-size is not matched!");
		}

		filePos += retSize;
		dataBufLen += retSize;

		return retSize;
	}

	public String readLine() {
		int ePos = dataBufRPos;
		int rLen = 0;

		if (readLen >= fileSize) {
			return null;
		}

		while (dataBuf[ePos] != '\n' && dataBuf[ePos] != '\r') {
			lineBuf[rLen++] = dataBuf[ePos];
			ePos = (ePos == AQ_DR_BUFFER_LAST) ? 0 : ePos + 1;
		}

		String ret = new String(lineBuf, 0, rLen);

		// Skip the remained - '\n' or '\r'
		ePos = (ePos == AQ_DR_BUFFER_LAST) ? 0 : ePos + 1;
		rLen++;
		if (dataBuf[ePos] == '\n' || dataBuf[ePos] == '\r') {
			ePos = (ePos == AQ_DR_BUFFER_LAST) ? 0 : ePos + 1;
			rLen++;
		}

		dataBufLen -= rLen;
		dataBufRPos = ePos;

		readLen += rLen;

		if (!fileEOF && dataBufLen < AQ_DR_THRESHHOLD) {
			loadDataBuf();
		}

		return ret;
	}

	public int getFileSize() {
		return fileSize;
	}

	public int getFilePosition() {
		return filePos;
	}

	public void close() {
		if (dataBuf != null) {
			dataBuf = null;
		}
		if (fileIS != null) {
			try {
				fileIS.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			fileIS = null;
		}
		if (fileInstance != null) {
			fileInstance = null;
		}
		if (filePath != null) {
			filePath = null;
		}
	}
}

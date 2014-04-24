package com.gmail.webos21.aquiz.model;

/**
 * Interface for declaring the entry-function
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 * 
 */
public interface AQModel {
	/**
	 * Entry function of the solving process
	 * 
	 * @param dataPath
	 *            the path-string of input-data file
	 */
	public void aqRun(String dataPath);
}

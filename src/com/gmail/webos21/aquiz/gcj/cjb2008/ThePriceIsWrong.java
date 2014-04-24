package com.gmail.webos21.aquiz.gcj.cjb2008;

import java.util.Arrays;
import java.util.Comparator;

import com.gmail.webos21.aquiz.model.AQModel;
import com.gmail.webos21.aquiz.util.AQBufferedReader;
import com.gmail.webos21.aquiz.util.AQDataWriter;
import com.gmail.webos21.aquiz.util.AQMisc;

/**
 * Google Code Jam - Practice Contests - Code Jam Beta 2008
 * 
 * Problem B. The Price is Wrong
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class ThePriceIsWrong implements AQModel {

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
		String lstr, lstr2;
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
			lstr2 = aqbr.readLine();
			String[] products = lstr.split(" ");
			String[] prices = lstr2.split(" ");
			if (products != null && prices != null
					&& products.length == prices.length) {
				gameCases[i] = new GameBean((i + 1), products, prices);
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

		private String products;
		private String prices;

		private Item[] items;

		public GameBean(int cn, String[] products, String[] prices) {
			this.caseNo = cn;
			this.products = AQMisc.mergeArray(products, " ");
			this.prices = AQMisc.mergeArray(prices, " ");
			this.items = new Item[products.length];
			for (int i = 0; i < products.length; i++) {
				items[i] = new Item(products[i], prices[i]);
			}
		}

		public String getResult() {
			return result;
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();

			// the summary of Game-Case
			sb.append("Case #").append(caseNo).append(": \n");
			sb.append("	").append(products).append("\n");
			sb.append("	").append(prices).append("\n");

			// check the price order
			Item[] newOrder = new Item[items.length];
			System.arraycopy(items, 0, newOrder, 0, items.length);
			Arrays.sort(newOrder, new SortByPrice());

			sb.append("Original    : ");
			for (Item item : items) {
				sb.append(item.name).append(" ");
			}
			sb.append("\n");

			sb.append("SortByPrice : ");
			for (Item item : newOrder) {
				sb.append(item.name).append(" ");
			}
			sb.append("\n");

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}

		private class Item {
			String name;
			int price;

			public Item(String name, String price) {
				this.name = name;
				this.price = Integer.parseInt(price);
			}
		}

		private class SortByPrice implements Comparator<Item> {
			@Override
			public int compare(Item o1, Item o2) {
				return (o1.price - o2.price);
			}
		}
	}

}

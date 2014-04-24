package com.gmail.webos21.aquiz.gcj.pc.pp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gmail.webos21.aquiz.model.AQModel;
import com.gmail.webos21.aquiz.util.AQBufferedReader;
import com.gmail.webos21.aquiz.util.AQDataWriter;
import com.gmail.webos21.aquiz.util.AQMisc;

/**
 * Google Code Jam - Practice Contests - Practice Problem
 * 
 * Problem D. Shopping Plan
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class ShoppingPlanTry02 implements AQModel {

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
		int j;

		// create the reader of input-data
		AQBufferedReader aqbr = new AQBufferedReader(dataPath);

		// get the size of data-set
		lstr = aqbr.readLine();
		setNum = Integer.parseInt(lstr);

		// create the cases of games
		gameCases = new GameBean[setNum];
		for (i = 0; setNum > 0; i++, setNum--) {
			// get game info
			lstr = aqbr.readLine();
			String[] gi = lstr.split(" ");

			// make a game case
			if (gi != null && gi.length == 3) {
				gameCases[i] = new GameBean((i + 1), gi[0], gi[1], gi[2]);
			}

			// get and set the shopping list
			lstr = aqbr.readLine();
			gameCases[i].setShoppingList(lstr);

			// get and add the store list
			for (j = 0; j < gameCases[i].getNumStores(); j++) {
				lstr = aqbr.readLine();
				gameCases[i].addStore(j, lstr);
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

	private interface Position {
		public int[] getPosition();
	}

	/**
	 * the class of game object
	 */
	private class GameBean implements Runnable {
		private int caseNo;

		private int numItems;
		private int numStores;
		private int gasPrice;

		private Store[] stores;
		private String[] shoppingList;

		private double[][] dpResult;

		private String result;

		public GameBean(int cn, String ni, String ns, String pg) {
			this.caseNo = cn;

			numItems = Integer.parseInt(ni);
			numStores = Integer.parseInt(ns);
			gasPrice = Integer.parseInt(pg);

			stores = new Store[numStores];
		}

		public String getResult() {
			return result;
		}

		public int getNumStores() {
			return numStores;
		}

		public void setShoppingList(String slist) {
			shoppingList = slist.split(" ");
			if (shoppingList == null || shoppingList.length != numItems) {
				throw new IllegalArgumentException("wrong shopping list!!");
			}
		}

		public void addStore(int idx, String sstr) {
			stores[idx] = new Store(idx, shoppingList, sstr);
		}

		private double calcDistance(Position a, Position b) {
			int[] aPos = a.getPosition();
			int[] bPos = b.getPosition();

			int xDiff = aPos[0] - bPos[0];
			int yDiff = aPos[1] - bPos[1];

			return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
		}

		private String[] dropItemOnSList(String[] src, String delItem) {
			if (src == null) {
				return null;
			}
			if (delItem == null) {
				return src;
			}
			final String[] strArr = new String[0];
			List<String> items = new ArrayList<String>();
			for (String item : src) {
				if (!item.equals(delItem)) {
					items.add(item);
				}
			}
			return items.toArray(strArr);
		}

		private String[] getItems(int bitMask) {
			final int maxVal = (1 << shoppingList.length);
			final String[] strArr = new String[0];
			List<String> items = new ArrayList<String>();
			if (bitMask > maxVal) {
				throw new RuntimeException("Invalid item value!!");
			}
			for (int i = 0; i < shoppingList.length; i++) {
				if ((bitMask & (1 << i)) > 0) {
					items.add(shoppingList[i]);
				}
			}
			return items.toArray(strArr);
		}

		private double minCost(String[] itemsLeft, Position currPos) {
			double current_minimum = Double.POSITIVE_INFINITY;

			System.out.println("itemsLeft = " + Arrays.toString(itemsLeft));

			for (Store store : stores) {
				for (String item : itemsLeft) {
					if (store.getItemPrice(item) != Double.POSITIVE_INFINITY) {
						String[] newLeftItem = dropItemOnSList(itemsLeft, item);
						double candidate = 0;
						if (newLeftItem.length > 0) {
							candidate = minCost(
									dropItemOnSList(itemsLeft, item), store)
									+ store.getItemPrice(item)
									+ calcDistance(currPos, store) * gasPrice;
						} else {
							candidate = store.getItemPrice(item)
									+ calcDistance(currPos, store) * gasPrice;
						}
						System.out.println("candidate = " + candidate);
						current_minimum = Math.min(current_minimum, candidate);
					}
				}
			}

			return current_minimum;

		}

		private void calcDPArray() {
			// the number of stores : stores + home(1)
			int totalStores = stores.length + 1;

			// bit-masked cases
			int totalItems = (1 << shoppingList.length);

			// create the double-array for result
			dpResult = new double[totalItems][totalStores];

			// home position
			ShoppingNode home = new ShoppingNode(null, null);

			// fill (0, 0)
			dpResult[0][0] = 0;

			// fill the price of distance on (0, stores)
			for (int iStore = 1; iStore < totalStores; iStore++) {
				dpResult[0][iStore] = calcDistance(home, stores[iStore - 1])
						* gasPrice;
			}

			// fill the price of items on (items, 0) : home
			for (int iItem = 1; iItem < totalItems; iItem++) {
				dpResult[iItem][0] = 0;
			}

			// calculate the DP result
			for (int iItem = 1; iItem < totalItems; iItem++) {
				String[] items = getItems(iItem);
				for (int iStore = 1; iStore < totalStores; iStore++) {
					dpResult[iItem][iStore] = minCost(items, home);
				}
			}
		}

		private void printDPArray() {
			StringBuilder sb = new StringBuilder();

			int totalStores = stores.length + 1;
			int totalItems = (1 << shoppingList.length);

			for (int iItem = 0; iItem < totalItems; iItem++) {
				for (int iStore = 0; iStore < totalStores; iStore++) {
					sb.append(String.format("%10.2f ", dpResult[iItem][iStore]));
				}
				sb.append('\n');
			}

			System.out.println(sb);
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();

			// the summary of Game-Case
			sb.append("Case #").append(caseNo).append(": ");
			sb.append("\n         PriceOfGas = ").append(gasPrice);
			sb.append("\n         ShoppingList = ");
			sb.append(AQMisc.mergeArray(shoppingList, " "));
			sb.append("\n         Stores = ");
			for (int i = 0; i < stores.length; i++) {
				sb.append("\n              ").append(stores[i]);
			}
			sb.append('\n');

			// calculate the DP array
			calcDPArray();
			printDPArray();

			// log the price of path
			sb.append("\n         Result = ").append(result);

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}

		private class ShoppingNode implements Position {
			private Store store;
			private String item;

			public ShoppingNode(Store s, String i) {
				store = s;
				item = i;
			}

			@Override
			public int[] getPosition() {
				if (store == null) {
					return new int[] { 0, 0 };
				} else {
					return store.getPosition();
				}
			}

			@Override
			public String toString() {
				int[] pos = getPosition();
				StringBuilder sb = new StringBuilder();
				sb.append("\nShoppingNode:Store=");
				if (store == null) {
					sb.append("home");
				} else {
					sb.append(store.getStoreNo());
				}
				sb.append("(x=").append(pos[0]).append(",y=").append(pos[1]);
				sb.append(")/Item=").append(item);
				return sb.toString();
			}
		}

		private class Store implements Position {
			private int storeNo;

			private int posX;
			private int posY;

			private StoreItem[] items;

			public Store(int idx, String[] cmp, String sstr) {
				this.storeNo = idx;
				String[] arr = sstr.split(" ");
				this.posX = Integer.parseInt(arr[0]);
				this.posY = Integer.parseInt(arr[1]);
				int numItems = arr.length - 2;
				items = new StoreItem[numItems];
				for (int i = 2; i < arr.length; i++) {
					int x = i - 2;
					items[x] = new StoreItem(x, cmp, arr[i]);
				}
			}

			public int getStoreNo() {
				return storeNo;
			}

			@Override
			public int[] getPosition() {
				return new int[] { posX, posY };
			}

			@SuppressWarnings("unused")
			public boolean isBuyable(String buyItem) {
				for (StoreItem item : items) {
					if (buyItem.startsWith(item.getName())) {
						return true;
					}
				}
				return false;
			}

			public double getItemPrice(String buyItem) {
				for (StoreItem item : items) {
					if (buyItem.startsWith(item.getName())) {
						return item.getPrice();
					}
				}
				return Double.POSITIVE_INFINITY;
			}

			@SuppressWarnings("unused")
			public double getItemPrice(String[] buyItems) {
				double retPrice = 0;
				for (String iname : buyItems) {
					retPrice += getItemPrice(iname);
				}
				return retPrice;
			}

			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();

				sb.append("Store[").append(storeNo).append("] : ");
				sb.append("x=").append(posX);
				sb.append(", y=").append(posY);
				sb.append(", items={").append(items[0]);
				for (int i = 1; i < items.length; i++) {
					sb.append(", ").append(items[i]);
				}
				sb.append("}");
				return sb.toString();
			}
		}

		private class StoreItem {
			private int itemNo;

			private String name;
			private boolean perishable;
			private int price;

			public StoreItem(int idx, String[] cmp, String cstrItem) {
				this.itemNo = idx;

				String[] arr = cstrItem.split(":");
				if (arr == null || arr.length != 2) {
					throw new IllegalArgumentException(
							"cannot create StoreItem!!");
				}
				this.name = arr[0];
				this.price = Integer.parseInt(arr[1]);
				for (int i = 0; i < cmp.length; i++) {
					if (cmp[i].startsWith(this.name)) {
						this.perishable = cmp[i].endsWith("!");
						break;
					}
				}
			}

			public String getName() {
				return name;
			}

			@SuppressWarnings("unused")
			public boolean isPerishable() {
				return perishable;
			}

			public int getPrice() {
				return price;
			}

			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();

				sb.append("Item[").append(itemNo).append("] : ");
				sb.append(name);
				if (perishable) {
					sb.append('!');
				}
				sb.append("=").append(price);

				return sb.toString();
			}
		}
	}
}

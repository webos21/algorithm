package org.xi.aquiz.gcj.pc.pp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.xi.aquiz.model.AQModel;
import org.xi.aquiz.util.AQBufferedReader;
import org.xi.aquiz.util.AQDataWriter;
import org.xi.aquiz.util.AQMisc;

/**
 * Google Code Jam - Practice Contests - Practice Problem
 * 
 * Problem D. Shopping Plan
 * 
 * @author Cheolmin Jo (webos21@gmail.com)
 */
public class ShoppingPlanTry03 implements AQModel {

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
		public int getX();

		public int getY();
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

		private ShoppingNode homeNode;

		private String result;

		public GameBean(int cn, String ni, String ns, String pg) {
			this.caseNo = cn;

			numItems = Integer.parseInt(ni);
			numStores = Integer.parseInt(ns);
			gasPrice = Integer.parseInt(pg);

			stores = new Store[numStores];

			homeNode = new ShoppingNode(null, null);
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
			int aX = (a == null) ? 0 : a.getX();
			int aY = (a == null) ? 0 : a.getY();

			int bX = (b == null) ? 0 : b.getX();
			int bY = (b == null) ? 0 : b.getY();

			int xDiff = aX - bX;
			int yDiff = aY - bY;

			return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
		}

		private ShoppingNode getMinPriceStore(String[] items, ShoppingNode prev) {
			int storeIdx = -1;
			double minPrice = 9999999;
			String retItem = null;
			ShoppingNode home = new ShoppingNode(null, null);
			for (int i = 0; i < stores.length; i++) {
				Store store = stores[i];
				for (String item : items) {
					double itemPrice = store.getItemPrice(item);
					if (itemPrice > 0) {
						double totalPrice = calcDistance(prev, store)
								* gasPrice + itemPrice;
						if (item.endsWith("!")) {
							// perishable item : must go home
							if (i + 1 < stores.length) {
								totalPrice += calcDistance(store, home)
										+ calcDistance(home, stores[i + 1]);
							} else if (i - 1 >= 0) {
								totalPrice += calcDistance(store, home)
										+ calcDistance(home, stores[i - 1]);
							} else {
								totalPrice += calcDistance(store, home) * 2;
							}
						}
						if (minPrice >= totalPrice) {
							storeIdx = store.getStoreNo();
							minPrice = totalPrice;
							retItem = item;
						}
					}
				}
			}
			return new ShoppingNode(stores[storeIdx], retItem);
		}

		@SuppressWarnings("unused")
		private ShoppingNode getMinPriceStore(String item, ShoppingNode prev) {
			int storeIdx = -1;
			double minPrice = 9999999;
			String retItem = null;
			for (int i = 0; i < stores.length; i++) {
				Store store = stores[i];
				double itemPrice = store.getItemPrice(item);
				if (itemPrice > 0) {
					double totalPrice = calcDistance(prev, store) * gasPrice
							+ itemPrice;
					if (minPrice >= totalPrice) {
						storeIdx = store.getStoreNo();
						minPrice = totalPrice;
						retItem = item;
					}
				}
			}
			return new ShoppingNode(stores[storeIdx], retItem);
		}

		private double calcShoppingPrice(List<ShoppingNode> snList) {
			double accDist = 0;
			double accItemPrice = 0;

			ShoppingNode prev = null;
			for (ShoppingNode node : snList) {
				if (prev != null) {
					accDist += calcDistance(prev, node);
					accItemPrice += node.getItemPrice();
				}
				prev = node;
			}
			return accItemPrice + (accDist * gasPrice);
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

		private ShoppingPath doPhaseConstruction() {
			ShoppingPath snPath = new ShoppingPath();

			// first, add home position
			snPath.addNode(homeNode);

			// start with the full shopping-list
			String[] csn = shoppingList;

			// iterate the shopping items
			for (int i = 0; i < shoppingList.length; i++) {
				ShoppingNode minNode = getMinPriceStore(csn,
						snPath.getPrevNode());
				snPath.addNode(minNode);
				if (minNode.getItem().endsWith("!")) {
					snPath.addNode(homeNode);
				}
				csn = dropItemOnSList(csn, minNode.getItem());
			}

			// last, add home position
			if (snPath.getPrevNode() != homeNode) {
				snPath.addNode(homeNode);
			}

			return snPath;
		}

		private ShoppingPath doPhaseMarketDrop(ShoppingPath sp,
				List<ShoppingPath> spList) {
			ShoppingPath tmp = new ShoppingPath(sp);

			return tmp;
		}

		private ShoppingPath doPhaseMarketAdd(ShoppingPath sp,
				List<ShoppingPath> spList) {
			ShoppingPath tmp = new ShoppingPath(sp);

			return tmp;

		}

		private ShoppingPath doPhaseMarketExchange(ShoppingPath sp,
				List<ShoppingPath> spList) {
			ShoppingPath tmp = new ShoppingPath(sp);

			return tmp;
		}

		private ShoppingPath doPhaseOptimizePath(ShoppingPath sp,
				List<ShoppingPath> spList) {
			ShoppingPath tmp = new ShoppingPath(sp);

			return tmp;
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

			// create the list of the shopping-path to be dumped
			List<ShoppingPath> spList = new ArrayList<ShoppingPath>();

			// create the base shopping-path by greedy methods
			ShoppingPath sp = doPhaseConstruction();

			// log the first path
			sb.append("[Construction Phase]\n");
			sb.append(" - Result Path");
			sb.append(sp);
			sb.append("\n - Dump Path\n   ");
			sb.append(spList.toString());

			// optimize the shopping-path by Market-Drop
			sp = doPhaseMarketDrop(sp, spList);

			// optimize the shopping-path by Market-Add
			sp = doPhaseMarketAdd(sp, spList);

			// optimize the shopping-path by Market-Exchange
			sp = doPhaseMarketExchange(sp, spList);

			// optimize the shopping-path by Renaud-Method
			sp = doPhaseOptimizePath(sp, spList);

			// log the price of path
			result = String.format("%.7f", sp.getTotalPrice());
			sb.append("\n         Result = ").append(result);

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}

		private class ShoppingPath {
			private double totalPrice;

			private ShoppingNode prevNode;
			private List<ShoppingNode> snList;

			public ShoppingPath() {
				totalPrice = 0;
				prevNode = null;
				snList = new ArrayList<ShoppingNode>();
			}

			// constructor for copying
			public ShoppingPath(ShoppingPath sp) {
				this.totalPrice = sp.totalPrice;
				this.prevNode = sp.prevNode;
				this.snList = new ArrayList<ShoppingNode>(sp.snList);
			}

			public double getTotalPrice() {
				return totalPrice;
			}

			public ShoppingNode getPrevNode() {
				return prevNode;
			}

			@SuppressWarnings("unused")
			public double reCalc() {
				totalPrice = calcShoppingPrice(snList);
				return totalPrice;
			}

			public void addNode(ShoppingNode sn) {
				double dist = calcDistance(prevNode, sn);
				totalPrice += sn.getItemPrice() + (dist * gasPrice);
				snList.add(sn);
				prevNode = sn;
			}

			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();
				sb.append("\n   ShoppingPath: price=");
				sb.append(this.totalPrice);
				sb.append(", path=[");
				for (ShoppingNode sn : snList) {
					sb.append("\n                 ");
					sb.append(sn.toString()).append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("\n                 ]");
				return sb.toString();
			}
		}

		private class ShoppingNode implements Position {
			private Store store;
			private String item;

			public ShoppingNode(Store s, String i) {
				store = s;
				item = i;
			}

			public String getItem() {
				return item;
			}

			public double getItemPrice() {
				if (store == null || item == null) {
					return 0;
				}
				return store.getItemPrice(item);
			}

			@Override
			public int getX() {
				if (store == null) {
					return 0;
				} else {
					return store.getX();
				}
			}

			@Override
			public int getY() {
				if (store == null) {
					return 0;
				} else {
					return store.getY();
				}
			}

			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();
				sb.append("ShoppingNode:Store=");
				if (store == null) {
					sb.append("home");
				} else {
					sb.append(store.getStoreNo());
				}
				sb.append("(x=").append(getX()).append(",y=").append(getY());
				sb.append(")/Item=").append(item);
				return sb.toString();
			}
		}

		private class Store implements Position {
			private int storeNo;

			private int posX;
			private int posY;

			private HashMap<String, Integer> itemMap;

			public Store(int idx, String[] cmp, String sstr) {
				// set the index number of store
				this.storeNo = idx;

				// parse the position and item information
				String[] arr = sstr.split(" ");

				// set the information of position
				this.posX = Integer.parseInt(arr[0]);
				this.posY = Integer.parseInt(arr[1]);

				// make the map of items
				itemMap = new HashMap<String, Integer>();

				// parse and set the item information
				for (int i = 2; i < arr.length; i++) {
					// parse the name and price of a item
					String[] itemInfo = arr[i].split(":");
					if (itemInfo == null || itemInfo.length != 2) {
						throw new IllegalArgumentException(
								"cannot create StoreItem!!");
					}

					// temporary variables of the name and price of a item
					String itemName = itemInfo[0];
					int itemPrice = Integer.parseInt(itemInfo[1]);

					// the name of item contains the perishable information.
					boolean itemFound = false;
					for (int j = 0; j < cmp.length; j++) {
						if (cmp[j].startsWith(itemName)) {
							itemMap.put(cmp[j], itemPrice);
							itemFound = true;
							break;
						}
					}

					// If the item is not in the shopping-list, just add it.
					if (!itemFound) {
						itemMap.put(itemName, itemPrice);
					}
				}
			}

			public int getStoreNo() {
				return storeNo;
			}

			@Override
			public int getX() {
				return posX;
			}

			@Override
			public int getY() {
				return posY;
			}

			/**
			 * Does the store has a required item?
			 * 
			 * @param buyItem
			 *            it must be a string in shopping-list
			 * @return If the required item is in store, it will return
			 *         <code>true</code>, otherwise it will return
			 *         <code>false</code>.
			 */
			@SuppressWarnings("unused")
			public boolean isBuyable(String buyItem) {
				Integer price = itemMap.get(buyItem);
				if (price == null) {
					return false;
				} else {
					return true;
				}
			}

			/**
			 * Get the price of the item in this store
			 * 
			 * @param buyItem
			 *            it must be a string in shopping-list
			 * @return If the required item is in store, it will return the
			 *         price of the item, otherwise it will return 0.
			 */
			public double getItemPrice(String buyItem) {
				Integer price = itemMap.get(buyItem);
				if (price == null) {
					return 0;
				} else {
					return price;
				}
			}

			/**
			 * Get the price of the items in this store
			 * 
			 * @param buyItem
			 *            it must be a string-array in shopping-list
			 * @return If the required item is in store, it will return the sum
			 *         of item prices, otherwise it will return 0.
			 */
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
				sb.append(", items={");
				for (Entry<String, Integer> item : itemMap.entrySet()) {
					sb.append(item.getKey()).append('(');
					sb.append(item.getValue()).append("),");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("}");
				return sb.toString();
			}
		}
	}
}

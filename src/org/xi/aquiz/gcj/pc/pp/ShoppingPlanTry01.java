package org.xi.aquiz.gcj.pc.pp;

import java.util.ArrayList;
import java.util.List;

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
public class ShoppingPlanTry01 implements AQModel {

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

	public interface Position {
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
			String[] tmp = slist.split(" ");
			if (tmp == null || tmp.length != numItems) {
				throw new IllegalArgumentException("wrong shopping list!!");
			}
			int cpos = 0;
			shoppingList = new String[tmp.length];
			for (String item : tmp) {
				if (!item.endsWith("!")) {
					shoppingList[cpos] = item;
					cpos++;
				}
			}
			for (String item : tmp) {
				if (item.endsWith("!")) {
					shoppingList[cpos] = item;
					cpos++;
				}
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

		private ShoppingNode getMinPriceStore(String[] items, ShoppingNode prev) {
			int storeIdx = -1;
			double minPrice = 9999999;
			String retItem = null;
			ShoppingNode home = new ShoppingNode(null, null);
			for (int i = 0; i < stores.length; i++) {
				Store store = stores[i];
				for (String item : items) {
					int itemPrice = store.getItemPrice(item);
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

		private ShoppingNode getMinPriceStore(String item, ShoppingNode prev) {
			int storeIdx = -1;
			double minPrice = 9999999;
			String retItem = null;
			ShoppingNode home = new ShoppingNode(null, null);
			for (int i = 0; i < stores.length; i++) {
				Store store = stores[i];
				int itemPrice = store.getItemPrice(item);
				if (itemPrice > 0) {
					double totalPrice = calcDistance(prev, store) * gasPrice
							+ itemPrice;
					if (item.endsWith("!")) {
						totalPrice += calcDistance(store, home) * 2;
					}

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
			String tmp = AQMisc.mergeArray(src, " ");
			tmp = tmp.replaceAll(delItem, "");
			tmp = tmp.replaceAll("  ", " ");
			tmp = tmp.trim();
			return tmp.split(" ");
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();

			List<List<ShoppingNode>> spList = new ArrayList<List<ShoppingNode>>();

			double[] retSnByItem = new double[shoppingList.length];
			double retGreedy = 0;

			double minTotal = 0;

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

			// first, add home position
			ShoppingNode home = new ShoppingNode(null, null);
			ShoppingNode prev = null;

			// change the sequence of buying items
			for (int i = 0; i < shoppingList.length; i++) {
				String[] sitems = new String[shoppingList.length];
				System.arraycopy(shoppingList, i, sitems, 0,
						shoppingList.length - i);
				System.arraycopy(shoppingList, 0, sitems, shoppingList.length
						- i, i);
				for (int j = 0; j < sitems.length; j++) {
					System.out.print(sitems[j] + " ");
				}
				System.out.print('\n');

				// add home first and reset previous
				List<ShoppingNode> snPath = new ArrayList<ShoppingNode>();
				snPath.add(home);
				prev = home;
				for (String item : sitems) {
					ShoppingNode minNode = getMinPriceStore(item, prev);
					snPath.add(minNode);
					prev = minNode;
					if (minNode.getItem().endsWith("!")) {
						snPath.add(home);
						prev = home;
					}
				}
				// check and add home position
				if (prev != home) {
					snPath.add(home);
				}
				// result price
				retSnByItem[i] = calcShoppingPrice(snPath);
				// check minTotal
				if (minTotal == 0) {
					minTotal = retSnByItem[i];
				} else {
					minTotal = Math.min(minTotal, retSnByItem[i]);
				}
				// add the shopping-path to spList
				spList.add(snPath);
			}

			// greedily buy items
			do {
				List<ShoppingNode> snPath = new ArrayList<ShoppingNode>();

				snPath.add(home);
				prev = home;
				String[] csn = shoppingList;
				for (int i = 0; i < shoppingList.length; i++) {
					ShoppingNode minNode = getMinPriceStore(csn, prev);
					snPath.add(minNode);
					prev = minNode;
					if (minNode.getItem().endsWith("!")) {
						snPath.add(home);
						prev = home;
					}
					csn = dropItemOnSList(csn, minNode.getItem());
				}
				// greedy : last, add home position
				if (prev != home) {
					snPath.add(home);
				}
				// result price
				retGreedy = calcShoppingPrice(snPath);
				// check minTotal
				minTotal = Math.min(minTotal, retGreedy);
				// add the shopping-path to spList
				spList.add(snPath);
			} while (false);

			// result price
			result = String.format("%.7f", minTotal);

			// check the list
			// for (int i = 0; i < shoppingList.length; i++) {
			// sb.append("\n         snByItem[").append(i).append("] Cost=");
			// sb.append(String.format("%.7f", retSnByItem[i]));
			// sb.append(", Path=").append(snByItem[i]);
			// }
			// do {
			// sb.append("\n         Greedy Cost=");
			// sb.append(String.format("%.7f", retGreedy));
			// sb.append(", Path=").append(snGreedy);
			// } while (false);

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

			public String getItem() {
				return item;
			}

			public int getItemPrice() {
				if (store == null || item == null) {
					return 0;
				}
				return store.getItemPrice(item);
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

			public int getItemPrice(String buyItem) {
				for (StoreItem item : items) {
					if (buyItem.startsWith(item.getName())) {
						return item.getPrice();
					}
				}
				return -1;
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

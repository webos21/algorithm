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
public class ShoppingPlan implements AQModel {

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
		AQBufferedReader gbr = new AQBufferedReader(dataPath);

		// get the size of data-set
		lstr = gbr.readLine();
		setNum = Integer.parseInt(lstr);

		// create the cases of games
		gameCases = new GameBean[setNum];
		for (i = 0; setNum > 0; i++, setNum--) {
			// get game info
			lstr = gbr.readLine();
			String[] gi = lstr.split(" ");

			// make a game case
			if (gi != null && gi.length == 3) {
				gameCases[i] = new GameBean((i + 1), gi[0], gi[1], gi[2]);
			}

			// get and set the shopping list
			lstr = gbr.readLine();
			gameCases[i].setShoppingList(lstr);

			// get and add the store list
			for (j = 0; j < gameCases[i].getNumStores(); j++) {
				lstr = gbr.readLine();
				gameCases[i].addStore(j, lstr);
			}
		}

		// close the reader of input-data
		gbr.close();
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
		AQDataWriter gdw = new AQDataWriter(AQMisc.getResultFilePath(dataPath));

		// write the result string to file
		for (int i = 0; i < gameCases.length; i++) {
			gdw.writeln("Case #" + (i + 1) + ": " + gameCases[i].getResult());
		}

		// close the writer of output-data
		gdw.close();
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
			shoppingList = slist.split(" ");
			if (shoppingList == null || shoppingList.length != numItems) {
				throw new IllegalArgumentException("wrong shopping list!!");
			}
		}

		public void addStore(int idx, String sstr) {
			stores[idx] = new Store(idx, shoppingList, sstr);
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();
			List<String> tarr = new ArrayList<String>();

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

			// make a decision tree
			List<ShoppingNode> pathList = new ArrayList<ShoppingNode>();
			for (Store store : stores) {
				for (String item : shoppingList) {
					if (store.isBuyable(item)) {
						tarr.add(item);
					}
				}
				ShoppingNode sn = new ShoppingNode(store.getStoreNo(),
						tarr.toArray(new String[0]));
				pathList.add(sn);
				tarr.clear();
			}

			// check the list
			sb.append(pathList);

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}

		private class ShoppingNode {
			private int storeNo;
			private String[] items;

			public ShoppingNode(int storeId, String[] buyItems) {
				this.storeNo = storeId;
				this.items = buyItems;
			}

			public int getStoreNo() {
				return storeNo;
			}

			public String[] getItems() {
				return items;
			}

			@Override
			public boolean equals(Object o) {
				// compare the type of instance
				if (!(o instanceof ShoppingNode)) {
					return false;
				}
				// cast the type
				ShoppingNode cmp = (ShoppingNode) o;
				// compare the storeNo
				if (cmp.storeNo != this.storeNo) {
					return false;
				}
				// compare the length of items
				if (cmp.items.length != this.items.length) {
					return false;
				}
				// compare the contents of items
				for (int i = 0; i < this.items.length; i++) {
					if (this.items[i] != null
							&& !this.items[i].equals(cmp.items[i])) {
						return false;
					}
				}
				return true;
			}

			@Override
			public int hashCode() {
				// set the default value
				final int PRIME = 31;
				int result = 1;
				// add [storeNo]
				result = (result * PRIME) + storeNo;
				// add the length of [items]
				result = (result * PRIME) + items.length;
				// add the contents of [items]
				for (String item : items) {
					result = (result * PRIME)
							+ ((item == null) ? 0 : item.hashCode());
				}
				return result;
			}

			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();
				sb.append("ShoppingNode : Store=").append(storeNo);
				sb.append(", items={");
				sb.append(items[0]);
				for (int i = 1; i < items.length; i++) {
					sb.append(",").append(items[i]);
				}
				sb.append("}");
				return sb.toString();
			}
		}

		private class Store {
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

			public boolean isBuyable(String buyItem) {
				for (StoreItem item : items) {
					if (buyItem.startsWith(item.getName())) {
						return true;
					}
				}
				return false;
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

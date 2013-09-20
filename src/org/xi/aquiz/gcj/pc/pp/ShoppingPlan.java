package org.xi.aquiz.gcj.pc.pp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

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

			// add home as a store
			gameCases[i].addStore(0, "0 0");

			// get and add the store list
			for (j = 1; j < gameCases[i].getNumStores(); j++) {
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

		private String result;

		private final static boolean debugLog = false;

		public GameBean(int cn, String ni, String ns, String pg) {
			this.caseNo = cn;

			numItems = Integer.parseInt(ni);
			numStores = Integer.parseInt(ns) + 1;
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

		private void log(String msg) {
			System.out.println(msg);
		}

		private final double EPS = 1e-10;

		private double measureGraph() {
			int nItems = shoppingList.length;
			int nStores = stores.length;
			int itemLastIdx = (1 << nItems) - 1;

			// initialize the result-array
			double[][] costArray = new double[nStores][(1 << nItems)];

			for (int s = 0; s < nStores; s++) {
				for (int n = 0; n < 1 << nItems; n++) {
					costArray[s][n] = 1e100;
				}
			}

			// Initialize the cheap-cost array
			double[] cheapCost = new double[nItems];
			Arrays.fill(cheapCost, 1e100); // fill the value (10^100)

			// Retrieve and compare the (item-cost + (store-distance*2))
			for (int i = 1; i < nStores; i++) {
				double moveCost = stores[i].getDistance(0, 0) * 2 * gasPrice;
				for (int j = 0; j < nItems; j++) {
					double itemPrice = stores[i].getItemPrice(shoppingList[j]);
					if (itemPrice > 0) {
						double totalCost = moveCost + itemPrice;
						cheapCost[j] = Math.min(cheapCost[j], totalCost);
					}
				}
			}

			// Simple sum of the cheap-cost
			double initTotalCost = 0.0;
			for (int i = 0; i < nItems; i++) {
				initTotalCost += cheapCost[i];
			}

			// Set the simple sum to result-value
			costArray[0][itemLastIdx] = initTotalCost;
			if (debugLog) {
				log("!!simple sum of the cheap-cost list = " + initTotalCost);
			}

			// remove the worst price items on stores
			// (compare the item-cost with the list of cheap cost)
			for (int i = 1; i < nStores; i++) {
				for (int j = 0; j < nItems; j++) {
					double itemPrice = stores[i].getItemPrice(shoppingList[j]);
					if (itemPrice > cheapCost[j]) {
						stores[i].removeItem(shoppingList[j]);
					}
				}
			}

			// print the items of stores after removing
			if (debugLog) {
				log("!!after removing the worst items on stores :");
				for (int i = 1; i < nStores; i++) {
					log("  " + stores[i]);
				}
				log("---------------------------------------------");
			}

			costArray[0][0] = 0.0;
			Queue<Stock> pq = new PriorityQueue<Stock>();
			pq.add(new Stock(0.0, 0, 0, 0, -1));

			while (!pq.isEmpty()) {
				// get a stock from the priority-queue
				Stock s = pq.poll();

				if (debugLog) {
					log(String.format("Examining Stock(%.7f, %d, %d, %d, %d)",
							s.accSpent, s.itemMask, s.storeIdx, s.runCount,
							s.prevStoreIdx));
				}

				if (costArray[0][itemLastIdx] <= s.accSpent) {
					if (debugLog) {
						log(String
								.format("  continue : total cost(%.7f) is lower than this stock(%.7f)",
										costArray[0][itemLastIdx], s.accSpent));
					}
					continue;
				}
				if (costArray[s.storeIdx][s.itemMask] < s.accSpent) {
					if (debugLog) {
						log(String
								.format("  continue : previous result(%.7f) is lower than this stock(%.7f)",
										costArray[s.storeIdx][s.itemMask],
										s.accSpent));
					}
					continue;
				}

				if (s.storeIdx == 0) { // Home
					for (int storeIdx = 1; storeIdx < nStores; storeIdx++) {
						double gc = stores[storeIdx].getDistance(0, 0)
								* gasPrice + s.accSpent;
						if (costArray[storeIdx][s.itemMask] > gc + EPS) {
							costArray[storeIdx][s.itemMask] = gc;
							pq.add(new Stock(gc, s.itemMask, storeIdx,
									s.runCount, s.storeIdx));
							if (debugLog) {
								log(String
										.format(" Push Stock(%.7f, %d, %d, %d, %d) for Home position",
												gc, s.itemMask, storeIdx,
												s.runCount, s.storeIdx));
							}
						} else {
							if (debugLog) {
								log(String
										.format("  skip : previous result(%.7f) is lower than new cost(%.7f)",
												costArray[storeIdx][s.itemMask],
												(gc + EPS)));
							}
						}
					}
				} else { // a Store
					Store aStore = stores[s.storeIdx];
					List<Integer> cmpItems = new ArrayList<Integer>();
					for (int i = 0; i < shoppingList.length; i++) {
						double itemPrice = aStore.getItemPrice(shoppingList[i]);
						// which items are in aStore and the item is NOT masked
						if (itemPrice > 0.0 && ((s.itemMask >> i) & 0x01) == 0) {
							if (debugLog) {
								log("  add the not-masked buyable item[" + i
										+ "] = " + shoppingList[i]);
							}
							cmpItems.add(i);
						}
					}
					if (cmpItems.isEmpty()) {
						if (debugLog) {
							log("  continue : no items in " + aStore + ")");
						}
						continue;
					}

					int cmpItemMask = 1 << cmpItems.size();
					if (debugLog) {
						log("  cmpItemMask( 1<<stk.size() ) = " + cmpItemMask);
					}
					for (int m = 1; m < cmpItemMask; m++) {
						double newCost = s.accSpent;
						int newMask = s.itemMask;
						boolean bPerishable = false;
						int newCount = s.runCount;
						for (int i = 0; (m >> i) > 0; i++) {
							if (((m >> i) & 1) > 0) {
								String itemName = shoppingList[cmpItems.get(i)];
								newCost += aStore.getItemPrice(itemName);
								newMask |= 1 << cmpItems.get(i);
								newCount++;
								if (itemName.endsWith("!")) {
									bPerishable = true;
								}
							}
						}
						if (costArray[s.storeIdx][newMask] < newCost + EPS) {
							if (debugLog) {
								log("  continue : costArray[s.loc][newMask]("
										+ costArray[s.storeIdx][newMask]
										+ ") < newCost+EPS(" + newCost + EPS
										+ ")");
							}
							continue;
						}
						if (bPerishable) {
							double gc = aStore.getDistance(0, 0) * gasPrice
									+ newCost;
							if (costArray[0][newMask] > gc + EPS) {
								costArray[0][newMask] = gc;
								pq.add(new Stock(gc, newMask, 0, newCount,
										s.storeIdx));
								if (debugLog) {
									log(String
											.format(" Push Stock(%.7f, %d, %d, %d, %d) for Perishable item",
													gc, newMask, // storeIdx=0
													0, // continue from home
													newCount, s.storeIdx));
								}
							}
						} else {
							costArray[s.storeIdx][newMask] = newCost;
							for (int i = 0; i < nStores; i++) {
								if (s.storeIdx == i) {
									// skip this store (aStore)
									continue;
								}
								double gc = stores[i].getDistance(aStore)
										* gasPrice + newCost;
								if (costArray[i][newMask] > gc + EPS) {
									costArray[i][newMask] = gc;
									pq.add(new Stock(gc, newMask, i, newCount,
											s.storeIdx));
									if (debugLog) {
										log(String
												.format(" Push Stock(%.7f, %d, %d, %d, %d) for next Stores",
														gc, newMask, i,
														newCount, s.storeIdx));
									}
								}
							}
						}
					}
				}
			}

			if (debugLog) {
				StringBuilder sb = new StringBuilder();

				sb.append("\n         costArray = ").append('\n');
				for (int i = 0; i < nStores; i++) {
					for (int j = 0; j < (itemLastIdx + 1); j++) {
						sb.append(String.format("%10.7f ", costArray[i][j]));
					}
					sb.append('\n');
				}
				System.out.print(sb);
				log(String.format("         Case #%d: %.7f", caseNo,
						costArray[0][itemLastIdx]));
			}

			return costArray[0][itemLastIdx];
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

			// dijkstra-algorithm
			double retVal = measureGraph();

			// log the result price of path
			result = String.format("%.7f", retVal);
			sb.append("         Result = ").append(result);

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}

		private class Stock implements Comparable<Stock> {
			double accSpent;
			int itemMask;
			int storeIdx;
			int runCount;
			int prevStoreIdx;

			public Stock(double dSpent, int nMask, int nStore, int rCount,
					int prevStore) {
				this.accSpent = dSpent;
				this.itemMask = nMask;
				this.storeIdx = nStore;
				this.runCount = rCount;
				this.prevStoreIdx = prevStore;
			}

			@Override
			public int compareTo(Stock other) {
				if (this.accSpent < other.accSpent) {
					return -1;
				} else if (this.accSpent == other.accSpent) {
					return 0;
				} else {
					return 1;
				}
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
			 * Get the distance from the position (x, y)
			 * 
			 * @param x
			 *            x-axis position
			 * @param y
			 *            y-axis position
			 * 
			 * @return the distance of store from the given position (x, y)
			 */
			public double getDistance(int x, int y) {
				int xDiff = posX - x;
				int yDiff = posY - y;

				return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
			}

			/**
			 * Get the distance from the other store
			 * 
			 * @param other
			 *            the other store
			 * 
			 * @return the distance of store from the given store
			 */
			public double getDistance(Store other) {
				int x = (other == null) ? 0 : other.getX();
				int y = (other == null) ? 0 : other.getY();

				int xDiff = posX - x;
				int yDiff = posY - y;

				return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
			}

			public void removeItem(String itemName) {
				itemMap.remove(itemName);
			}

			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();

				sb.append("Store[").append(getStoreNo()).append("] : ");
				sb.append("x=").append(posX);
				sb.append(", y=").append(posY);
				sb.append(", items={");
				for (Entry<String, Integer> item : itemMap.entrySet()) {
					sb.append(item.getKey()).append('(');
					sb.append(item.getValue()).append("),");
				}
				if (sb.charAt(sb.length() - 1) == ',') {
					sb.deleteCharAt(sb.length() - 1);
				}
				sb.append("}");
				return sb.toString();
			}
		}
	}
}

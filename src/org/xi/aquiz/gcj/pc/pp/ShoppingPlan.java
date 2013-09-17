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

			double[] cheapCost = new double[nItems];
			Arrays.fill(cheapCost, 1e100);
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

			double initTotalCost = 0.0;
			for (int i = 0; i < nItems; i++) {
				initTotalCost += cheapCost[i];
			}

			costArray[0][itemLastIdx] = initTotalCost;

			for (int i = 1; i < nStores; i++) {
				for (int j = 0; j < nItems; j++) {
					double itemPrice = stores[i].getItemPrice(shoppingList[j]);
					if (itemPrice > cheapCost[j]) {
						stores[i].removeItem(shoppingList[j]);
					}
				}
			}

			costArray[0][0] = 0.0;
			Queue<Stock> pq = new PriorityQueue<Stock>();
			pq.add(new Stock(0.0, 0, 0, 0, -1));

			while (!pq.isEmpty()) {
				Stock s = pq.poll();

				if (debugLog) {
					System.out.println(String.format(
							"Examining Stock(%.7f, %d, %d, %d, %d)", s.spent,
							s.mask, s.loc, s.bct, s.cf));
				}

				if (costArray[0][itemLastIdx] <= s.spent) {
					if (debugLog) {
						System.out
								.println(String
										.format("costArray[0][itemLastIdx](%.7f) <= s.spent(%.7f)",
												costArray[0][itemLastIdx],
												s.spent));
					}
					continue;
				}

				if (costArray[s.loc][s.mask] < s.spent) {
					if (debugLog) {
						System.out
								.println(String
										.format("costArray[s.loc][s.mask](%.7f) < s.spent(%.7f)",
												costArray[s.loc][s.mask],
												s.spent));
					}
					continue;
				}

				if (s.loc != 0) {
					Store aStore = stores[s.loc];
					List<Integer> stk = new ArrayList<Integer>();
					for (int i = 0; i < shoppingList.length; i++) {
						double itemPrice = aStore.getItemPrice(shoppingList[i]);
						if (debugLog) {
							System.out.println("itemPrice=" + itemPrice
									+ " / s.mask=" + s.mask + " / i=" + i
									+ " / ((s.mask >> i) & 0x01)="
									+ ((s.mask >> i) & 0x01));
						}
						// store do not sale the item
						if (itemPrice > 0.0 && ((s.mask >> i) & 0x01) == 0) {
							if (debugLog) {
								System.out.println("added item = " + i);
							}
							stk.add(i);
						}
					}
					if (stk.isEmpty()) {
						continue;
					}

					int n = 1 << stk.size();
					for (int m = 1; m < n; m++) {
						double cost = s.spent;
						int nm = s.mask;
						int pr = 0;
						int nct = s.bct;
						for (int i = 0; (m >> i) > 0; i++) {
							if (((m >> i) & 1) > 0) {
								String itemName = shoppingList[stk.get(i)];
								cost += aStore.getItemPrice(itemName);
								nm |= 1 << stk.get(i);
								nct++;
								if (itemName.endsWith("!")) {
									pr++;
								}
							}
						}
						if (costArray[s.loc][nm] < cost + EPS) {
							continue;
						}

						if (pr != 0) {
							double gc = aStore.getDistance(0, 0) * gasPrice
									+ cost;
							if (costArray[0][nm] > gc + EPS) {
								costArray[0][nm] = gc;
								pq.add(new Stock(gc, nm, 0, nct, s.loc));
								if (debugLog) {
									System.out
											.println(String
													.format(" Push Stock(%.7f, %d, %d, %d, %d)",
															gc, nm, 0, nct,
															s.loc));
								}
							}
						} else {
							costArray[s.loc][nm] = cost;
							for (int i = 0; i < nStores; i++) {
								if (s.loc == i) {
									continue;
								}
								double gc = stores[i].getDistance(aStore)
										* gasPrice + cost;
								if (costArray[i][nm] > gc + EPS) {
									costArray[i][nm] = gc;
									pq.add(new Stock(gc, nm, i, nct, s.loc));
									if (debugLog) {
										System.out
												.println(String
														.format(" Push Stock(%.7f, %d, %d, %d, %d)",
																gc, nm, i, nct,
																s.loc));
									}
								}
							}
						}
					}
				} else {
					for (int i = 1; i < nStores; i++) {
						double gc = stores[i].getDistance(0, 0) * gasPrice
								+ s.spent;
						if (costArray[i][s.mask] > gc + EPS) {
							costArray[i][s.mask] = gc;
							pq.add(new Stock(gc, s.mask, i, s.bct, 0));
							if (debugLog) {
								System.out.println(String.format(
										" Push Stock(%.7f, %d, %d, %d, %d)",
										gc, s.mask, i, s.bct, 0));
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
				System.out.println(String.format("         Case #%d: %.7f",
						caseNo, costArray[0][itemLastIdx]));
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

			// check the store items after run
			if (debugLog) {
				sb.append("\n         Stores = ");
				for (int i = 0; i < stores.length; i++) {
					sb.append("\n              ").append(stores[i]);
				}
				sb.append('\n');
			}

			// log the price of path
			result = String.format("%.7f", retVal);
			sb.append("         Result = ").append(result);

			// print out the logs
			sb.append('\n');
			System.out.println(sb);
		}

		private class Stock implements Comparable<Stock> {
			double spent;
			int bct;
			int loc;
			int cf;
			int mask;

			public Stock(double sp, int m, int l, int b, int c) {
				spent = sp;
				mask = m;
				loc = l;
				bct = b;
				cf = c;
			}

			@Override
			public int compareTo(Stock other) {
				if (this.spent < other.spent) {
					return -1;
				} else if (this.spent == other.spent) {
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

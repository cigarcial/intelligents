package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017.CEM;

import java.awt.Point;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import unalcol.agents.Action;
import unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017.CEM.TeseoSearch.Node;
import unalcol.agents.simulate.util.Language;

public class TeseoSearchV1 {

	private int[] dx = { 0, 1, 0, -1 };
	private int[] dy = { 1, 0, -1, 0 };

	private int MAX_LEVEL = 20;
	private Language language;
	Map<Point, Long> map;
	Map<Integer, Boolean> foodRec;
	LinkedList<String> cmd;

	public TeseoSearchV1(Language l, Map<Point, Long> m, Map<Integer, Boolean> fr) {
		language = l;
		map = m;
		foodRec = fr;
		cmd = null;
	}

	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS

	public boolean inSearch() {
		if (cmd != null && cmd.size() == 0) {
			cmd = null;
		}
		return cmd != null;
	}

	public String nextAction() {
		return cmd.removeFirst();
	}

	public LinkedList<String> searchCentinel(Point p, int dr, int energyLevel, int type) {
		System.out.println("New search: " + type);
		if (type == 1) {
			return foodAsterisk(p, dr, energyLevel);
		}
		MAX_LEVEL += 2;
		return normalAsterisk(p, dr, energyLevel);
	}

	public void initMap(Point p) {
		if (!map.containsKey(p)) {
			map.put(p, (long) 0);
		}
	}

	public LinkedList<String> foodAsterisk(Point p, int dr, int energyLevel) {

		FoodNode best = new FoodNode(new Point(-1, -1), -1, -1);
		PriorityQueue<FoodNode> structure = new PriorityQueue<FoodNode>();
		Map<Point, Boolean> vis = new HashMap<Point, Boolean>();

		structure.add(new FoodNode(p, 0, dr));
		vis.put(p, true);

		while (structure.size() > 0) {
			FoodNode nx = structure.poll();
			best = best.getBetter(nx);
			if (nx.cmd.size() >= energyLevel) {
				continue;
			}
			for (int i = 0; i < 4; ++i) {

				int k = (i + 4 - nx.dr) % 4;
				int x = nx.pt.x + dx[i];
				int y = nx.pt.y + dy[i];
				Point ps = new Point(x, y);

				initMap(new Point(x, y));
				boolean noVis = !vis.containsKey(ps);
				boolean reach = (1 << i & map.get(nx.pt)) != 0;
				if (reach && noVis) {
					vis.put(ps, true);
					structure.offer(nx.cloneWith(i, k));
				}
			}

		}

		this.cmd = best.cmd;
		System.out.println(this.cmd);
		System.out.println(best.profit);
		return null;
	}

	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA

	class FoodNode implements Comparable<FoodNode> {

		private Point pt;
		private int lvl, dr, profit;
		private LinkedList<String> cmd;

		public FoodNode(Point p, int l, int d) {
			pt = p;
			this.lvl = l;
			this.dr = d;
			this.cmd = new LinkedList<String>();
		}

		public FoodNode cloneWith(int i, int k) {
			Point ps = new Point(this.pt.x + dx[i], this.pt.y + dy[i]);
			FoodNode ret = new FoodNode(ps, this.lvl + 1, (this.dr + k) % 4);
			ret.cmd = (LinkedList<String>) this.cmd.clone();
			for (int j = 0; j < 4; ++j) {
				boolean isNeig = (1 << j & map.get(ps)) != 0;
				// ret.profit += ( isNeig )? 1 : 0;
				boolean unvis = (1 << (j + 4) & map.get(ps)) == 0;
				if (isNeig && unvis) {
					ret.profit++;
				}
			}
			if ((1 << 9 & map.get(ps)) != 0) {
				int foodCode = (int) (map.get(ps) >> 10 & 15);
				boolean c1 = foodRec.containsKey(foodCode) && foodRec.get(foodCode);
				profit += (c1) ? 1000 : 0;
			}

			// ret.profit -= map.get(px).get(py)>>24;
			for (int x = 0; x < k; ++x) {
				ret.cmd.add(language.getAction(3));
			}
			ret.cmd.add(language.getAction(2));
			return ret;
		}

		@Override
		public int compareTo(FoodNode o) {
			if (this.profit != o.profit) {
				return o.profit - this.profit;
			}
			return this.cmd.size() - o.cmd.size();
		}

		public FoodNode getBetter(FoodNode o) {
			return (this.compareTo(o) < 0) ? this : o;
		}

	}

	public LinkedList<String> normalAsterisk(Point p, int dr, int energyLevel) {

		NormalNode best = new NormalNode(new Point(-1, -1), -1, -1);
		PriorityQueue<NormalNode> structure = new PriorityQueue<NormalNode>();
		Map<Point, Boolean> vis = new HashMap<Point, Boolean>();

		structure.add(new NormalNode(p, 0, dr));
		vis.put(p, true);

		while (structure.size() > 0) {
			NormalNode nx = structure.poll();
			best = best.getBetter(nx);
			if (nx.lvl >= MAX_LEVEL) {
				continue;
			}
			for (int i = 0; i < 4; ++i) {

				int k = (i + 4 - nx.dr) % 4;
				int x = nx.pt.x + dx[i];
				int y = nx.pt.y + dy[i];
				Point ps = new Point(x, y);

				initMap(new Point(x, y));
				boolean noVis = !vis.containsKey(ps);
				boolean reach = (1 << i & map.get(nx.pt)) != 0;
				if (reach && noVis) {
					vis.put(ps, true);
					structure.offer(nx.cloneWith(i, k));
				}
			}

		}
		System.out.println(best.cmd);
		this.cmd = best.cmd;
		return null;
	}

	class NormalNode implements Comparable<NormalNode> {

		private Point pt;
		private int lvl, dr, profit;
		private LinkedList<String> cmd;

		public NormalNode(Point p, int l, int d) {
			pt = p;
			this.lvl = l;
			this.dr = d;
			this.cmd = new LinkedList<String>();
		}

		public NormalNode cloneWith(int i, int k) {
			Point ps = new Point(this.pt.x + dx[i], this.pt.y + dy[i]);
			NormalNode ret = new NormalNode(ps, this.lvl + 1, (this.dr + k) % 4);
			ret.cmd = (LinkedList<String>) this.cmd.clone();
			int mul = 1;
			for (int j = 0; j < 4; ++j) {
				boolean isNeig = (1 << j & map.get(ps)) != 0;
				// ret.profit += ( isNeig )? 1 : 0;
				boolean unvis = (1 << (j + 4) & map.get(ps)) == 0;
				if (isNeig && unvis) {
					ret.profit += 30*mul;
					mul++;
				}
			}
			if ((1 << 9 & map.get(ps)) > 0) {
				int foodCode = (int) ((map.get(ps) >> 10) & 15);
				if (!foodRec.containsKey(foodCode)) {
					ret.profit += 6;
				}
				if (foodRec.containsKey(foodCode) && foodRec.get(foodCode)) {
					ret.profit += 10;
				}
			}

			// ret.profit -= map.get(px).get(py)>>24;
			for (int x = 0; x < k; ++x) {
				ret.cmd.add(language.getAction(3));
			}
			ret.cmd.add(language.getAction(2));
			return ret;
		}

		@Override
		public int compareTo(NormalNode o) {
			if (this.profit != o.profit) {
				return o.profit - this.profit;
			}
			return this.cmd.size() - o.cmd.size();
		}

		public NormalNode getBetter(NormalNode o) {
			return (this.compareTo(o) < 0) ? this : o;
		}

	}

}

package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017.CEM;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import unalcol.agents.Action;
import unalcol.agents.simulate.util.Language;

public class TeseoSearch {
	
	private int[] dx = { 0, 1, 0, -1 };
	private int[] dy = { 1, 0, -1, 0 };
	
	private int MAX_LEVEL = 20;
	private Language language ;
	Map<Point,Long> map;
	
	public TeseoSearch(Language l,Map<Point,Long> m){
		language = l;
		map = m;
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

	public LinkedList<String> searchCentinel(Point p,int dr) {
		System.out.println("New search");
		MAX_LEVEL += 2;
		return asterisk(p,dr);
	}
	
	public void initMap(Point p){
		if( !map.containsKey(p) ){
			map.put(p, (long)0);
		}
	}

	public LinkedList<String> asterisk(Point p,int dr) {

		Node best = new Node(new Point(-1,-1), -1, -1);
		PriorityQueue<Node> structure = new PriorityQueue<Node>();
		Map<Point, Boolean> vis = new HashMap<Point, Boolean>();

		structure.add(new Node(p, 0, dr));
		vis.put(p,true);

		while (structure.size() > 0) {
			Node nx = structure.poll();
			best = best.getBetter(nx);
			if (nx.lvl >= MAX_LEVEL) {
				continue;
			}
			for (int i = 0; i < 4; ++i) {

				int k = (i + 4 - nx.dr) % 4;
				int x = nx.pt.x + dx[i];
				int y = nx.pt.y + dy[i];
				Point ps = new Point(x,y);

				initMap(new Point(x, y));
				boolean noVis = !vis.containsKey(ps);
				boolean reach = (1 << i & map.get(nx.pt) ) != 0;
				if (reach && noVis) {
					vis.put(ps, true);
					structure.offer(nx.cloneWith(i, k));

				}
			}

		}
		return best.cmd;
	}
	
	
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	class Node implements Comparable<Node> {

		private Point pt;
		private int lvl, dr, profit;
		private LinkedList<String> cmd;

		public Node(Point p, int l, int d) {
			pt = p;
			this.lvl = l;
			this.dr = d;
			this.cmd = new LinkedList<String>();
		}

		public Node cloneWith(int i, int k) {
			Point ps = new Point(this.pt.x + dx[i], this.pt.y + dy[i]);
			Node ret = new Node(ps, this.lvl + 1, (this.dr + k) % 4);
			ret.cmd = (LinkedList<String>) this.cmd.clone();
			for (int j = 0; j < 4; ++j) {
				boolean isNeig = (1 << j & map.get(ps)) != 0;
				// ret.profit += ( isNeig )? 1 : 0;
				boolean unvis = (1 << (j + 4) & map.get(ps)) == 0;
				if (isNeig && unvis) {
					ret.profit++;
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
		public int compareTo(Node o) {
			if (this.profit != o.profit) {
				return o.profit - this.profit;
			}
			return this.cmd.size() - o.cmd.size();
		}

		public Node getBetter(Node o) {
			return (this.compareTo(o) < 0) ? this : o;
		}

	}
}

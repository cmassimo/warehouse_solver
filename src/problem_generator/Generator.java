import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {

	private int goods_count = 5;
	private int locations_count = 12;
	
	private int slots_volume_lb = 20;
	private int slots_volume_ub = 100;
	private int slots_load_lb = 15;
	private int slots_load_ub = 70;
	
	private int box_volume_lb = 1;
	private int box_volume_ub = 20;
	private int box_weight_lb = 1;
	private int box_weight_ub = 10;
	private int box__per_goods_lb = 1;
	private int box__per_goods_ub = 5;

	public static void main(String[] args) {

		Generator instance = new Generator();
		
		try {
			instance.goods_count = Integer.parseInt(args[0]);
			instance.locations_count = Integer.parseInt(args[1]);
			instance.slots_volume_lb = Integer.parseInt(args[2]);
			instance.slots_volume_ub = Integer.parseInt(args[3]);
			instance.slots_load_lb = Integer.parseInt(args[4]);
			instance.slots_load_ub = Integer.parseInt(args[5]);
			instance.box_volume_lb = Integer.parseInt(args[6]);
			instance.box_volume_ub = Integer.parseInt(args[7]);
			instance.box_weight_lb = Integer.parseInt(args[8]);
			instance.box_weight_ub = Integer.parseInt(args[9]);
			instance.box__per_goods_lb = Integer.parseInt(args[10]);
			instance.box__per_goods_ub = Integer.parseInt(args[11]);
			
		} catch (Exception e) {
			// nothing
		}

		instance.slots_volume_ub -= instance.slots_volume_lb;
		instance.slots_load_ub -= instance.slots_load_lb;
		instance.box_volume_ub -= instance.box_volume_lb;
		instance.box_weight_ub -= instance.box_weight_lb;
		instance.box__per_goods_ub -= instance.box__per_goods_lb;
		
		instance.doWork();
	}
	
	private void doWork(){
		
		int m = goods_count;
		int k = locations_count;

		// capacita` delle location
		String slots_volumes = "";
		String slots_loads = "" ;
		// unita` di prodotto disponibili
		String boxes_volumes = "";
		String boxes_weights = "";
		String boxes_per_goods = "";

		for(int j = 0; j < k; j++) {
			slots_volumes += slots_volume_lb + Math.round(Math.random() * slots_volume_ub) + ",";
			slots_loads += slots_load_lb + Math.round(Math.random() * slots_load_ub) + ",";
		}
		slots_volumes= slots_volumes.substring(0, slots_volumes.length()-1) + "\n";
		slots_loads= slots_loads.substring(0, slots_loads.length()-1) + "\n";

		// init volumi slot
		for(int i = 0; i < m; i++) {
			boxes_volumes += box_volume_lb + Math.round(Math.random() * box_volume_ub) + ",";
			boxes_weights += box_weight_lb + Math.round(Math.random() * box_weight_ub) + ",";
			boxes_per_goods += box__per_goods_lb + Math.round(Math.random() * box__per_goods_ub) + ",";
		}
		boxes_volumes= boxes_volumes.substring(0, boxes_volumes.length()-1) + "\n";
		boxes_weights= boxes_weights.substring(0, boxes_weights.length()-1) + "\n";
		boxes_per_goods= boxes_per_goods.substring(0, boxes_per_goods.length()-1) + "\n";
		
		try {
			FileWriter out = new FileWriter("data.csv");
			BufferedWriter bw = new BufferedWriter(out);

			String line = goods_count + "," + locations_count + "\n";
			bw.write(line, 0, line.length());
			
			bw.write(slots_volumes, 0, slots_volumes.length());
			bw.write(slots_loads, 0, slots_loads.length());
			bw.write(boxes_volumes, 0, boxes_volumes.length());
			bw.write(boxes_weights, 0, boxes_weights.length());
			bw.write(boxes_per_goods, 0, boxes_per_goods.length());
			
			bw.close();
			
		} catch (IOException e) {
			System.out.println("[Simulation] Error: I/O error writing results.csv.");
			e.printStackTrace();
		}
	}
	
}
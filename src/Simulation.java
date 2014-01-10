import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Simulation {
	
	WarehouseProblem wp;
	SimulationResult results;
	
	public Simulation() {};

	public void load_from_file(String filename) {

		wp = new WarehouseProblem();
		
		try {
			FileReader file = new FileReader(filename);
			BufferedReader br = new BufferedReader(file);

			String line = br.readLine();
			String[] values = line.split(",");
			wp.goods_count = Integer.parseInt(values[0]);
			wp.locations_count = Integer.parseInt(values[1]);
			
			wp.slots_volumes = new int[wp.locations_count];
			wp.slots_loads = new int[wp.locations_count];
			wp.boxes_volumes = new int[wp.goods_count];
			wp.boxes_weights = new int[wp.goods_count];
			wp.boxes_per_goods = new int[wp.goods_count];
			
			line = br.readLine();
			values = line.split(",");
			for (int i = 0; i < wp.locations_count; i++) {
				wp.slots_volumes[i]=Integer.parseInt(values[i]);
			}
			
			line = br.readLine();
			values = line.split(",");
			for (int i = 0; i < wp.locations_count; i++) {
				wp.slots_loads[i]=Integer.parseInt(values[i]);
			}
			
			line = br.readLine();
			values = line.split(",");
			for (int i = 0; i < wp.goods_count; i++) {
				wp.boxes_volumes[i]=Integer.parseInt(values[i]);
			}
			
			line = br.readLine();
			values = line.split(",");
			for (int i = 0; i < wp.goods_count; i++) {
				wp.boxes_weights[i]=Integer.parseInt(values[i]);
			}
			
			line = br.readLine();
			values = line.split(",");
			for (int i = 0; i < wp.goods_count; i++) {
				wp.boxes_per_goods[i]=Integer.parseInt(values[i]);
			}
			
			br.close();

		}
		catch (FileNotFoundException fnfe) {
			System.out.println("[Simulation] Error: " + filename + " not found.");
			fnfe.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("[Simulation] Error: I/O error reading " + filename + ".");
			e.printStackTrace();
		}
	}

	public void run() {
		
		results = new SimulationResult();


		WarehouseSolver solver = new WarehouseSolver(wp);
		results = solver.solve();


		try {
			FileWriter out = new FileWriter("results.csv");
			BufferedWriter bw = new BufferedWriter(out);
				
			String line = results.node_count + "," + results.elapsed_seconds;
			bw.write(line, 0, line.length());
			bw.newLine();
			
			bw.close();
			
		} catch (IOException e) {
			System.out.println("[Simulation] Error: I/O error writing results.csv.");
			e.printStackTrace();
		}
	}
}

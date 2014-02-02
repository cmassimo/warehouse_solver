import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Simulation {

	List<WarehouseProblem> wpList;
	SimulationResult results;

	public Simulation() {
		wpList = new ArrayList<WarehouseProblem>();
	};

	public void load_from_file(String filename) {


		try {
			FileReader file = new FileReader(filename);
			BufferedReader br = new BufferedReader(file);

			String line = br.readLine();
			int dim = Integer.parseInt(line);

			for (int j = 0; j < dim; j++) {

				WarehouseProblem wp = new WarehouseProblem();
				line = br.readLine();
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

				br.readLine();

				wpList.add(wp);
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

		try {
			FileWriter out = new FileWriter("results.csv");
			BufferedWriter bw = new BufferedWriter(out);

			for (int i = 0; i < wpList.size(); i++) {

				WarehouseProblem wp=  wpList.get(i);
				WarehouseSolver solver = new WarehouseSolver(wp);
				results = solver.solve();

				if (results != null) {
					String line = wp.goods_count + "," + wp.locations_count + "," + results.node_count + "," + results.elapsed_seconds + "," + results.objective_value + "," + results.solution_exists;
					bw.write(line, 0, line.length());

					bw.newLine();
				}

			}

			bw.close();

		} catch (IOException e) {
			System.out.println("[Simulation] Error: I/O error writing results.csv.");
			e.printStackTrace();
		}
	}
}

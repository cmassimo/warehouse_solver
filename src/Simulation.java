import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Simulation {
	
	int [][] data;
	SimulationResult[] results;
	
	public Simulation() {};

	public void load_from_file(String filename, String count) {
		int problems_count = Integer.parseInt(count);
		try {
			FileReader file = new FileReader(filename);
			BufferedReader br = new BufferedReader(file);

			data = new int[problems_count][2];
			String line;
			int i = 0;

			while ((line = br.readLine()) != null) {
				System.out.println(line);
				String[] values = line.split(",");

				data[i][0] = Integer.parseInt(values[0]);
				data[i][1] = Integer.parseInt(values[1]);

				i++;
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
		results = new SimulationResult[data.length];

		for (int i = 0; i < data.length; i++) {
			WarehouseSolver solver = new WarehouseSolver(data[i]);
			results[i] = solver.solve();
		}

		try {
			FileWriter out = new FileWriter("results.csv");
			BufferedWriter bw = new BufferedWriter(out);

			for (int i = 0; i < results.length; i++) {
				String line = results[i].node_count + "," + results[i].elapsed_seconds;
				bw.write(line, 0, line.length());
				bw.newLine();
			}
			
			bw.close();
			
		} catch (IOException e) {
			System.out.println("[Simulation] Error: I/O error writing results.csv.");
			e.printStackTrace();
		}

	}
}

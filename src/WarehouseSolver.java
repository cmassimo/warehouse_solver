import java.util.Arrays;
import java.util.logging.Level;

import choco.cp.model.CPModel;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.Choco;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.solver.Solver;
import choco.cp.solver.CPSolver;

public class WarehouseSolver {

	public static void main(String[] args) {
		//constants of the problem:
		
		// numero di merci
		int m = 5;
		// numero di location disponibili
		int k = 12;
		// capacita` delle location
		int[] slots_volumes = new int[k];
		int[] slots_loads = new int[k];
 		// unita` di prodotto disponibili
		int[] boxes_volumes = new int[m];
		int[] boxes_weights = new int[m];
		
		int[] boxes_per_goods = new int[m];
		
		// init volumi slot
		String vols = new String();
		String loads = new String();
		for(int j = 0; j < k; j++) {
			slots_volumes[j] = 1 + (int)(Math.random() * ((100 - 1) + 1));
			vols += Integer.toString(slots_volumes[j]) + " ";
			slots_loads[j] = 1 + (int)(Math.random() * ((70 - 1) + 1));
			loads += Integer.toString(slots_loads[j]) + " ";
		}
		System.out.println("Volumi slot:  " + vols);
		System.out.println("Carichi slot: " + loads);
		
		// init volumi slot
		String vols1 = new String();
		String loads1 = new String();
		String qt = new String();
		for(int i = 0; i < m; i++) {
			boxes_volumes[i] = 1 + (int)(Math.random() * ((60 - 1) + 1));
			vols1 += Integer.toString(boxes_volumes[i]) + " ";
			boxes_weights[i] = 1 + (int)(Math.random() * ((50 - 1) + 1));
			loads1 += Integer.toString(boxes_weights[i]) + " ";
			boxes_per_goods[i] = 1 + (int)(Math.random() * ((5 - 1) + 1));
			qt += Integer.toString(boxes_per_goods[i]) + " ";
		}
		System.out.println("Volumi box:   " + vols1);
		System.out.println("Carichi box:  " + loads1);
		System.out.println("# box:        " + qt);
		System.out.println();
		System.out.println();
		
//		// penalita` per posizionamento location vicine
//		float c1 = 0.01f;
//		// penalita` per posizionamento location opposte
//		float c2 = 0.013f;
//		// penalita` per posizionamento location backside
//		float c3 = 0.05f;
//		// penalita` per posizionamento location qualsiasi
//		float c4 = 0.1f;
		
		// Our model
		Model model = new CPModel();

		// y_i,j: unita` di prodotto i nella location j
		IntegerVariable[][] ys = new IntegerVariable[m][k];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				ys[i][j] = Choco.makeIntVar("y_"+i+"_"+j, 0, (boxes_per_goods[i]));
				model.addVariables(ys[i][j]);
			}
		}
		
		IntegerVariable[][] vol_ys = new IntegerVariable[k][m];
		for(int j = 0; j < k; j++){
			for(int i = 0; i < m; i++){
				vol_ys[j][i] = Choco.makeIntVar(("vol_y"+j)+i, 0, (slots_volumes[j]));
				model.addVariables(vol_ys[j][i]);
			}
		}
		
		IntegerVariable[][] weight_ys = new IntegerVariable[k][m];
		for(int j = 0; j < k; j++){
			for(int i = 0; i < m; i++){
				weight_ys[j][i] = Choco.makeIntVar(("weight_y"+j)+i, 0, (slots_loads[j]));
				model.addVariables(weight_ys[j][i]);
			}
		}
		
		//Constraints
		
		// vincolo su y_i,j: La singola merce deve essere posizionata tutta
		Constraint[] rows = new Constraint[m];
		for(int i = 0; i < m; i++){
			rows[i] = Choco.eq(Choco.sum(ys[i]), boxes_per_goods[i]);
		}

		model.addConstraints(rows);
		
		// vincolo sul volume degli slot
		Constraint[] slot_vols = new Constraint[k];
		for(int j = 0; j < k; j++){
			slot_vols[j] = Choco.leq( Choco.sum(vol_ys[j]), slots_volumes[j]);
		}
		
		model.addConstraints(slot_vols);
		
		// vincolo sul volume degli slot
		Constraint[] slot_loads = new Constraint[k];
		for(int j = 0; j < k; j++){
			slot_loads[j] = Choco.leq( Choco.sum(weight_ys[j]), slots_loads[j]);
		}
		
		model.addConstraints(slot_loads);

		//Our solver
		Solver solver = new CPSolver();

		//read the model
		solver.read(model);

		ChocoLogging.setVerbosity(Verbosity.SEARCH);
		
		long tps = System.currentTimeMillis();
		
		//solve the problem
		solver.solve();
		
		//Print the values
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				System.out.print(solver.getVar(ys[i][j]).getVal()+" ");
			}
			System.out.println();
		}
		System.out.println();
		
		//Print the vol_ys values
		for(int j = 0; j < k; j++){
			for(int i = 0; i < m; i++){
				System.out.print(solver.getVar(vol_ys[j][i]).getVal()+" ");
			}
			System.out.println();
		}
		System.out.println();
		
		//Print the weight_ys values
		for(int j = 0; j < k; j++){
			for(int i = 0; i < m; i++){
				System.out.print(solver.getVar(weight_ys[j][i]).getVal()+" ");
			}
			System.out.println();
		}
		
		
//		solver.solveAll();
		System.out.println("tps nreines1 " + (System.currentTimeMillis() - tps) + " nbNode " + solver.getNodeCount());
	
	}


}

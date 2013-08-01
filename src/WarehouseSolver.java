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
	
	public static boolean location_kind(int location, int[] kind) {
		boolean found = false;
		int i = 0;
		
		while (!found && i < kind.length) {
			found = kind[i] == location;
			i++;
		}
		
		return found;
	}

	public static void main(String[] args) {
		//constants of the problem:
		
		// numero di merci
		int m = 5;
		// numero di location disponibili
		int k = 12;
		// location vicine
		int[] T1 = new int[]{1,2,4,5,7,8,10,11};
		// location opposte
		int[] T2 = new int[]{1,2,3,7,8,9};
		// location backside
		int[] T3 = new int[]{4,5,6,10,11,12};
		// capacita` delle location
		int[] Cap = new int[]{5,5,5,5,5,5,5,5,5,5,5,5};
		// unita` di prodotto disponibili
		int[] s = new int[]{9,8,10,9,15};
		// dimensione del singolo box di prodotto (in unita`)
		int[] b = new int[]{3,2,5,3,5};
		
		// penalita` per posizionamento location vicine
		float c1 = 0.01f;
		// penalita` per posizionamento location opposte
		float c2 = 0.013f;
		// penalita` per posizionamento location backside
		float c3 = 0.05f;
		// penalita` per posizionamento location qualsiasi
		float c4 = 0.1f;
		
		// Ciao Git
		
		
		// Our model
		Model model = new CPModel();

		// y_i,j: unita` di prodotto i nella location j
		IntegerVariable[][] ys = new IntegerVariable[m][k];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				ys[i][j] = Choco.makeIntVar(("y"+i)+j, 0, (s[i]/b[i]));
				model.addVariables(ys[i][j]);
			}
		}

		// x_i,j: 1 se qualche box della merce i sta in location j
		IntegerVariable[][] xs = new IntegerVariable[m][k];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				xs[i][j] = Choco.makeIntVar(("x"+i)+j, 0, 1);
//				model.addConstraints(Choco.reifiedConstraint(
//						xs[i][j],
//						Choco.and(Choco.gt(ys[i][j], 0), Choco.eq(Choco.mod(ys[i][j], b[i]), 0)))
//				);
			}
		}
		
		// p1_i,j: 1 se il prodotto i e` in location j e j e` adiacente (sta in T1)
		IntegerVariable[][] p1 = new IntegerVariable[m][k];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				p1[i][j] = Choco.makeIntVar(("p1"+i)+j, 0, 1);
//				model.addConstraints(Choco.reifiedConstraint(
//						p1[i][j],
//						Choco.and(
//								Choco.and(Choco.gt(ys[i][j], 0), Choco.eq(Choco.mod(ys[i][j], b[i]), 0)),
//								Choco.eq(location_kind(j,T1), true)
//								)
//						)
//				);
			}
		}
		
		// p2_i,j: 1 se il prodotto i e` in location j e j e` opposta (sta in T2)
		IntegerVariable[][] p2 = new IntegerVariable[m][k];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				p2[i][j] = Choco.makeIntVar(("p2"+i)+j, 0, 1);
			}
		}
		
		// p3_i,j: 1 se il prodotto i e` in location j e j e` backside (sta in T3)
		IntegerVariable[][] p3 = new IntegerVariable[m][k];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				p3[i][j] = Choco.makeIntVar(("p3"+i)+j, 0, 1);
			}
		}
		
		// p4_i,j: 1 se il prodotto i e` in location j e j non ricade nei casi precedenti
		IntegerVariable[][] p4 = new IntegerVariable[m][k];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				p4[i][j] = Choco.makeIntVar(("p4"+i)+j, 0, 1);
			}
		}
		
		// p5_i,j: 1 se il prodotto i e` solo in location j
		IntegerVariable[][] p5 = new IntegerVariable[m][k];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				p5[i][j] = Choco.makeIntVar(("p5"+i)+j, 0, 1);
			}
		}
		
		//Constraints
		
		// vincolo su y_i,j: La singola merce deve essere posizionata tutta
		Constraint[] rows = new Constraint[m];
		for(int i = 0; i < m; i++){
			rows[i] = Choco.eq(Choco.sum(ys[i]), s[i]);
		}

		model.addConstraints(rows);
		
		// vincolo sulla capacita` delle location sum_j ys[i][j]*b[i]
		Constraint[] loc_cap = new Constraint[k];
		for(int j = 0; j < k; j++){
			IntegerExpressionVariable [] inner = new IntegerExpressionVariable[k];
			for(int i = 0; i < m; i++){
				inner[j] = Choco.constant(0);
			}
			for(int i = 0; i < m; i++){
				inner[j] = Choco.plus(inner[j], Choco.mult(ys[i][j], b[i]));
			}
			loc_cap[j] = Choco.leq(inner[j], Cap[j]);
		}
		
		model.addConstraints(loc_cap);
		

		//Our solver
		Solver solver = new CPSolver();

		//read the model
		solver.read(model);

		ChocoLogging.setVerbosity(Verbosity.SEARCH);
		
		//solve the problem
		solver.solve();
		
		//Print the values
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				System.out.print(solver.getVar(ys[i][j]).getVal()+" ");
			}
			System.out.println();
		}
		
		long tps = System.currentTimeMillis();
		solver.solveAll();
		System.out.println("tps nreines1 " + (System.currentTimeMillis() - tps) + " nbNode " + solver.getNodeCount());
	
	}


}

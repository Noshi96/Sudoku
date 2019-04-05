package pszero.sudoku;



import sac.State;
import sac.StateFunction;

public class HEmptyPlaces extends StateFunction {
	public double calculate(State state){
		Sudoku s=(Sudoku) state;
				
				return s.zeros;
	}
}

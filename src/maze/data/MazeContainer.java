package maze.data;

import java.util.Random;

import maze.generator.MazeGen;

/**
 * The maze that contains all the cells
 * 
 * @author Pierre-André Mudry
 * @version 1.3
 */
public class MazeContainer {
	// The number of cells
	public final int nCellsX, nCellsY;

	// The maze itself
	public MazeElem[][] maze;		
	
	/**
	 * Creates a specific maze
	 * @param x Width
	 * @param y Height
	 * @param mazeID The unique ID of the maze
	 */
	public MazeContainer(int x, int y, int mazeID){			
		nCellsX = x;
		nCellsY = y;
		maze = new MazeElem[x][y];					
		
		// Generate the maze
		MazeGen mg = new MazeGen(x, y, mazeID);

		// Convert the maze to something nicer to work with
		for (int i = 0; i < nCellsX; i++) {
			for (int j = 0; j < nCellsY; j++) {
				MazeElem e = new MazeElem();
				e.wallSouth = mg.wallSouth(i, j);
				e.wallWest = mg.wallWest(i, j);
				e.wallNorth = mg.wallNorth(i, j);
				e.wallEast = mg.wallEast(i, j);
				maze[i][j] = e;
			}
		}

		/**
		 * Generate initial positions for players and for the exit
		 */
		setInitialPositions(true);
	}
	
	/**
	 * Generate a fixed maze (always the same)
	 * @param x Width
	 * @param y Height
	 */
	public MazeContainer(int x, int y) {
		this(x, y, 1234);
	}

	/**
	 * Creates initial positions for the two players.
	 * 
	 * @param fixed Chooses either random position on the first and last column
	 *            or fixed positions for the players
	 */
	protected void setInitialPositions(boolean fixed) {		
		if (fixed) {
			/**
			 * First player top left, second player top right and the exit down
			 * in the middle			 */			
			maze[0][0].p1Present = true;							
			maze[(nCellsX - 1) / 2][nCellsY - 1].isExit = true;
		} else {
			Random rnd = new Random();
			/**
			 * Generate players' positions on the first and last column, resp.
			 * Exit down in the middle
			 */
			maze[0][rnd.nextInt(nCellsY)].p1Present = true;					
			maze[(nCellsX - 1) / 2][nCellsY - 1].isExit = true;
		}
	}
}

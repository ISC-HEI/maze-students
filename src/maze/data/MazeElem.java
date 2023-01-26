package maze.data;

/**
 * Content of a maze cell
 * @author Pierre-André Mudry
 * @version 1.0
 */
public class MazeElem {
	// The walls
	public boolean wallWest;
	public boolean wallNorth;
	public boolean wallEast;
	public boolean wallSouth;

	/**
	 *  Player related
	 */	
	// True if this element is the exit of the maze
	public boolean isExit;	
	// True if player1 is located here
	public boolean p1Present;
	// True if player2 is located here
	public boolean p2Present;
}

package maze.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import maze.data.MazeUtils;

/**
 * Recursive backtracking algorithm for maze construction shamelessly borrowed
 * from Ruby at http://weblog.jamisbuck.org/2010/12/27/maze-generation-recursive-backtracking
 * 
 * @author Pierre-André Mudry
 */
public class MazeGen {

	private final int x;
	private final int y;
	private final int[][] maze;
	private Random rnd;

	/**
	 * Constructor
	 * @param x Width
	 * @param y Height
	 */
	public MazeGen(int x, int y) {
		this(x, y, 1234);
	}
	
	/**
	 * Constructor
	 * @param x Widht
	 * @param y Height
	 * @param seed The seed for the random generator, can be used as a maze ID 
	 */
	public MazeGen(int x, int y, int seed) {
		this.x = x;
		this.y = y;
		maze = new int[this.x][this.y];
		this.rnd = new Random(seed);
		generateMaze(0, 0);			
	}	

	public int getContent(int x, int y) {
		return maze[x][y];
	}

	public boolean wallWest(int x, int y) {
		return (maze[x][y] & DIR.W.bit) == 0;
	}

	public boolean wallNorth(int x, int y) {
		return (maze[x][y] & DIR.N.bit) == 0;
	}

	public boolean wallEast(int x, int y) {
		return (maze[x][y] & DIR.E.bit) == 0;
	}

	public boolean wallSouth(int x, int y) {
		return (maze[x][y] & DIR.S.bit) == 0;
	}

	private void generateMaze(int cx, int cy) {
		DIR[] dirs = DIR.values();
		Collections.shuffle(Arrays.asList(dirs), rnd);
		for (DIR dir : dirs) {
			int nx = cx + dir.dx;
			int ny = cy + dir.dy;
			if (between(nx, x) && between(ny, y) && (maze[nx][ny] == 0)) {
				maze[cx][cy] |= dir.bit;
				maze[nx][ny] |= dir.opposite.bit;
				generateMaze(nx, ny);
			}
		}
	}

	private static boolean between(int v, int upper) {
		return (v >= 0) && (v < upper);
	}

	private enum DIR {
		// Declare some directions
		N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);

		private final int bit; // Position of the bit
		private final int dx; // X before or after
		private final int dy; // Y before or after
		private DIR opposite; // What is it on the other side ?

		// Using a static initializer in order to
		// resolve forward references
		static {
			N.opposite = S;
			S.opposite = N;
			E.opposite = W;
			W.opposite = E;
		}

		// Constructor
		private DIR(int bit, int dx, int dy) {
			this.bit = bit;
			this.dx = dx;
			this.dy = dy;
		}
	};

	public static void main(String[] args) {
		MazeGen maze = new MazeGen(5, 5);
	}

}

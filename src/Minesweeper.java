import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Minesweeper {
	public static final int CELL_UNREVEALED_BOMB = -5;
	public static final int CELL_REVEALED_BOMB = -4;
	public static final int CELL_FLAG_BOMB = -3;
	public static final int CELL_FLAG_NONE_BOMB = -2;
	public static final int CELL_UNREVEALED = -1;

	private JFrame window;
	private IMinesweeperGUI gui;

	private boolean gameover = false;

	public static int BOARD_WIDTH = 10;
	public static int BOARD_HEIGHT = 10;
	private int BOMB_COUNT = 10;

	private int[][] cells;

	private void Reset() {
		Random random = new Random();
		random.setSeed(0);

		// set all cells to unrevealed
		cells = new int[BOARD_WIDTH][BOARD_HEIGHT];
		for (int y = 0; y < BOARD_WIDTH; y++) {
			for (int x = 0; x < BOARD_HEIGHT; x++) {
				cells[y][x] = CELL_UNREVEALED;
			}
		}

		// place bombs
		{
			int bombCount = 0;
			while (bombCount < BOMB_COUNT) {
				int x = (int)(random.nextDouble() * BOARD_WIDTH);
				int y = (int)(random.nextDouble() * BOARD_HEIGHT);

				// prevent placing a bomb on top of another bomb
				if (cells[y][x] != CELL_UNREVEALED_BOMB) {
					cells[y][x] = CELL_UNREVEALED_BOMB;
					bombCount++;
				}
			}
		}

		gui.setCells(cells);
	}

	private Void onClickFlag(Int2D position) {
		if (gameover) { return null; } // ignore clicks after gameover

		// System.out.println("Flagged " + position.x + ", " + position.y);

		int clickedCell = cells[position.y][position.x];

		switch (clickedCell) {
			case CELL_UNREVEALED:
				cells[position.y][position.x] = CELL_FLAG_NONE_BOMB;
				break;
			case CELL_UNREVEALED_BOMB:
				cells[position.y][position.x] = CELL_FLAG_BOMB;
				break;
			case CELL_FLAG_NONE_BOMB:
				cells[position.y][position.x] = CELL_UNREVEALED;
				break;
			case CELL_FLAG_BOMB:
				cells[position.y][position.x] = CELL_UNREVEALED_BOMB;
				break;
			default:
				break;
		}

		gui.setCells(cells);

		return null;
	}
	
	private Void onClickCell(Int2D position) {
		if (gameover) { return null; } // ignore clicks after gameover

		// System.out.println("Clicked " + position.x + ", " + position.y);

		int clickedCell = cells[position.y][position.x];

		if (clickedCell == CELL_UNREVEALED_BOMB) {
			// reveal bomb
			cells[position.y][position.x] = CELL_REVEALED_BOMB;
			gui.setCells(cells);

			gui.setBannerText("You lose!");
			gameover = true;

			return null;
		}

		int[][] newCells = arrayCopyInt(cells);

		Queue<Int2D> queue = new LinkedList<>();
		queue.add(position);

		while (queue.isEmpty() == false) {
			Int2D cellPosition = queue.remove();

			// System.out.println("Checking " + cellPosition.x + ", " + cellPosition.y);

			if (isPositionInBounds(cellPosition) == false) { continue; }

			int cell = newCells[cellPosition.y][cellPosition.x];

			if (cell >= 0) { continue; } // ignore already revealed cells
			if (cell == CELL_REVEALED_BOMB) { continue; }
			if (cell == CELL_UNREVEALED_BOMB) { continue; }
			if (cell == CELL_FLAG_BOMB || cell == CELL_FLAG_NONE_BOMB) { continue; } // ignore flags

			// cell must be unrevealed

			int neighboringBombCount = countNeighboringBombs(cellPosition);
			newCells[cellPosition.y][cellPosition.x] = neighboringBombCount;

			if (neighboringBombCount > 0) {
				continue;
			}

			// propagate to neighboring cells
			Int2D[] neighboringCellPositions = new Int2D[]{
				cellPosition.add(Int2D.UP),
				cellPosition.add(Int2D.UP_RIGHT),
				cellPosition.add(Int2D.RIGHT),
				cellPosition.add(Int2D.DOWN_RIGHT),
				cellPosition.add(Int2D.DOWN),
				cellPosition.add(Int2D.DOWN_LEFT),
				cellPosition.add(Int2D.LEFT),
				cellPosition.add(Int2D.UP_LEFT)
			};

			Collections.addAll(queue, neighboringCellPositions);
		}

		cells = newCells;
		gui.setCells(cells);

		return null;
	}

	Minesweeper() {
		gui = new MinesweeperGUI();

		gui.setOnClickCell(this::onClickCell);
		gui.setOnClickFlag(this::onClickFlag);

		Reset();
	}

	private int countNeighboringBombs(Int2D position) {
		int bombCount = 0;

		Int2D[] neighboringCellPositions = new Int2D[]{
				position.add(Int2D.RIGHT),
				position.add(Int2D.LEFT),
				position.add(Int2D.DOWN_LEFT),
				position.add(Int2D.DOWN),
				position.add(Int2D.DOWN_RIGHT),
				position.add(Int2D.UP_LEFT),
				position.add(Int2D.UP),
				position.add(Int2D.UP_RIGHT)
		};

		for (Int2D neighboringCellPosition : neighboringCellPositions) {
			if (isPositionInBounds(neighboringCellPosition) == false) { continue; }

			int cell = cells[neighboringCellPosition.y][neighboringCellPosition.x];

			if (cell == CELL_UNREVEALED_BOMB || cell == CELL_FLAG_BOMB) {
				bombCount++;
			}
		}

		return bombCount;
	}

	private static boolean isPositionInBounds(Int2D position) {
		return position.x >= 0 && position.x < BOARD_WIDTH && position.y >= 0 && position.y < BOARD_HEIGHT;
	}

	// https://stackoverflow.com/a/50151362/9409623
	public static int[][] arrayCopyInt(int[][] array) {
		return Arrays.stream(array).
						map(el -> el.clone()).toArray(a -> array.clone());
	}
}
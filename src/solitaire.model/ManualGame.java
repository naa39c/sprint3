package solitaire.model;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ManualGame extends SolitaireModel {

	// keeps a record of all jumps
	private List<String> moveHistory = new ArrayList<>();

	public ManualGame(BoardType type, int size) {
		super(type, size);
	}

	@Override
	public void startNewGame(BoardType type, int size) {
		super.startNewGame(type, size);
		if (moveHistory != null) {
			moveHistory.clear();
		}
	}

	@Override
	public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
		if (super.makeMove(fromRow, fromCol, toRow, toCol)) {
			// record the move as a simple string format
			moveHistory.add(fromRow + "," + fromCol + "," + toRow + "," + toCol);
			return true;
		}
		return false;
	}

	// scrambles the pegs and empty spots randomly
	public void randomizeBoard() {
		Random rand = new Random();
		for (int r = 0; r < boardSize; r++) {
			for (int c = 0; c < boardSize; c++) {
				if (gameBoard[r][c] != INVALID) {
					gameBoard[r][c] = rand.nextBoolean() ? PEG : EMPTY;
				}
			}
		}
		// ensure there's an empty hole so the game doesn't instantly end
		gameBoard[boardSize / 2][boardSize / 2] = EMPTY;
	}

	// writes basic game info and the move log to a text file
	public void saveGame(File file) {
		try (PrintWriter writer = new PrintWriter(file)) {
			writer.println(currentType);
			writer.println(boardSize);
			for (String move : moveHistory) {
				writer.println(move);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// rebuilds the board and rapidly executes all logged moves
	public void loadGame(File file) {
		try (Scanner scanner = new Scanner(file)) {
			BoardType loadedType = BoardType.valueOf(scanner.nextLine().trim());
			int loadedSize = Integer.parseInt(scanner.nextLine().trim());

			startNewGame(loadedType, loadedSize);

			while (scanner.hasNextLine()) {
				String[] coords = scanner.nextLine().trim().split(",");
				if (coords.length == 4) {
					makeMove(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]),
							Integer.parseInt(coords[3]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

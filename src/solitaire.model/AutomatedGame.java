package solitaire.model;

public class AutomatedGame extends SolitaireModel {

	public AutomatedGame(SolitaireModel previousGame) {
		super(previousGame);
	}

	// scans the board and executes the first valid move it finds
	public boolean makeAutomatedMove() {
		int[][] directions = { { 2, 0 }, { -2, 0 }, { 0, 2 }, { 0, -2 } };

		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				if (gameBoard[row][col] == PEG) {
					for (int[] dir : directions) {
						int destRow = row + dir[0];
						int destCol = col + dir[1];

						if (isValidMove(row, col, destRow, destCol)) {
							makeMove(row, col, destRow, destCol);
							return true;
						}
					}
				}
			}
		}
		return false; // out of moves
	}
}

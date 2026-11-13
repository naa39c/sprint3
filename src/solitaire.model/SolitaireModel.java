package solitaire.model;

public abstract class SolitaireModel {

	public enum BoardType {
		ENGLISH, HEXAGON, DIAMOND
	}

	public static final int INVALID = -1;
	public static final int EMPTY = 0;
	public static final int PEG = 1;

	protected int[][] gameBoard;
	protected int boardSize;
	protected BoardType currentType;

	public SolitaireModel(BoardType type, int size) {
		startNewGame(type, size);
	}

	// copy constructor used for autoplay mode
	public SolitaireModel(SolitaireModel other) {
		this.boardSize = other.boardSize;
		this.currentType = other.currentType;
		this.gameBoard = new int[boardSize][boardSize];
		for (int row = 0; row < boardSize; row++) {
			System.arraycopy(other.gameBoard[row], 0, this.gameBoard[row], 0, boardSize);
		}
	}

	public void startNewGame(BoardType type, int size) {
		this.currentType = type;
		this.boardSize = size;
		this.gameBoard = new int[size][size];

		if (type == BoardType.ENGLISH) {
			initializeEnglishBoard();
		} else if (type == BoardType.HEXAGON) {
			initializeHexagonBoard();
		}
	}

	private void initializeEnglishBoard() {
		int armLength = boardSize / 3;
		int center = boardSize / 2;

		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				boolean inCorner = (row < armLength || row >= boardSize - armLength)
						&& (col < armLength || col >= boardSize - armLength);

				if (inCorner) {
					gameBoard[row][col] = INVALID;
				} else if (row == center && col == center) {
					gameBoard[row][col] = EMPTY;
				} else {
					gameBoard[row][col] = PEG;
				}
			}
		}
	}

	// builds the hexagon shape using manhattan distance from the center
	private void initializeHexagonBoard() {
		int center = boardSize / 2;
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				int distance = Math.abs(row - center) + Math.abs(col - center);

				if (distance > (boardSize / 2) + 1) {
					gameBoard[row][col] = INVALID;
				} else if (row == center && col == center) {
					gameBoard[row][col] = EMPTY;
				} else {
					gameBoard[row][col] = PEG;
				}
			}
		}
	}

	public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
		if (isOutOfBounds(fromRow, fromCol) || isOutOfBounds(toRow, toCol)) {
			return false;
		}
		if (gameBoard[fromRow][fromCol] != PEG || gameBoard[toRow][toCol] != EMPTY) {
			return false;
		}

		int rowDiff = toRow - fromRow;
		int colDiff = toCol - fromCol;

		boolean isVertical = Math.abs(rowDiff) == 2 && colDiff == 0;
		boolean isHorizontal = Math.abs(colDiff) == 2 && rowDiff == 0;

		if (isVertical || isHorizontal) {
			int midRow = fromRow + (rowDiff / 2);
			int midCol = fromCol + (colDiff / 2);
			return gameBoard[midRow][midCol] == PEG;
		}

		return false;
	}

	public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
		if (isValidMove(fromRow, fromCol, toRow, toCol)) {
			gameBoard[fromRow][fromCol] = EMPTY;
			gameBoard[toRow][toCol] = PEG;

			int midRow = fromRow + (toRow - fromRow) / 2;
			int midCol = fromCol + (toCol - fromCol) / 2;
			gameBoard[midRow][midCol] = EMPTY;

			return true;
		}
		return false;
	}

	public boolean isGameOver() {
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				if (gameBoard[row][col] == PEG) {
					if (isValidMove(row, col, row + 2, col) || isValidMove(row, col, row - 2, col)
							|| isValidMove(row, col, row, col + 2) || isValidMove(row, col, row, col - 2)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean isOutOfBounds(int row, int col) {
		return row < 0 || row >= boardSize || col < 0 || col >= boardSize || gameBoard[row][col] == INVALID;
	}

	public int getCell(int row, int col) {
		return isOutOfBounds(row, col) ? INVALID : gameBoard[row][col];
	}
}

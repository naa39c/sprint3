package solitaire.model;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SolitaireModelTest {

	private SolitaireModel model;

	@BeforeEach
	void setUp() {
		// Sets up a fresh manual board before each test runs
		model = new ManualGame(SolitaireModel.BoardType.ENGLISH, 7);
	}

	// Sprint 2 Tests

	@Test
	void testInitialBoardSetup() {
		assertEquals(SolitaireModel.EMPTY, model.getCell(3, 3), "Center should be empty.");
		assertEquals(SolitaireModel.INVALID, model.getCell(0, 0), "Corner should be invalid.");
		assertEquals(SolitaireModel.PEG, model.getCell(3, 2), "Playable spaces should have pegs.");
	}

	@Test
	void testValidMoveExecution() {
		assertTrue(model.isValidMove(3, 1, 3, 3), "Move should be valid.");
		assertTrue(model.makeMove(3, 1, 3, 3), "Move should execute successfully.");

		assertEquals(SolitaireModel.EMPTY, model.getCell(3, 1), "Starting position should be empty.");
		assertEquals(SolitaireModel.EMPTY, model.getCell(3, 2), "Jumped peg should be removed.");
		assertEquals(SolitaireModel.PEG, model.getCell(3, 3), "Destination should have a peg.");
	}

	// --- ChatGPT Generated Tests ---

	@Test
	void testInvalidDiagonalMoveIsRejectedWithoutChangingBoard() {
		// Copy the original board state
		int[][] originalBoard = new int[7][7];
		for (int row = 0; row < 7; row++) {
			for (int col = 0; col < 7; col++) {
				originalBoard[row][col] = model.getCell(row, col);
			}
		}

		// Attempt a diagonal jump from (1, 3) to (3, 5).
		// (1, 3) is a peg, but diagonal jumps are illegal.
		boolean result = model.makeMove(1, 3, 3, 5);
		assertFalse(result, "A diagonal move should be rejected.");

		// Verify that every cell remains unchanged
		for (int row = 0; row < 7; row++) {
			for (int col = 0; col < 7; col++) {
				assertEquals(originalBoard[row][col], model.getCell(row, col),
						"Board changed at row " + row + ", col " + col);
			}
		}
	}

	@Test
	void testIsGameOverWhenNoValidJumpsRemain() throws Exception {
		// Create a custom board with no possible horizontal or vertical jumps.
		int[][] customBoard = new int[7][7];

		// Mark every cell invalid initially.
		for (int row = 0; row < 7; row++) {
			for (int col = 0; col < 7; col++) {
				customBoard[row][col] = SolitaireModel.INVALID;
			}
		}

		// Place isolated pegs and empty holes.
		customBoard[1][3] = SolitaireModel.PEG;
		customBoard[3][3] = SolitaireModel.EMPTY;
		customBoard[5][3] = SolitaireModel.PEG;
		customBoard[3][1] = SolitaireModel.PEG;
		customBoard[3][5] = SolitaireModel.PEG;

		// Inject the custom board into the model using Java Reflection
		Field boardField = SolitaireModel.class.getDeclaredField("gameBoard");
		boardField.setAccessible(true);
		boardField.set(model, customBoard);

		assertTrue(model.isGameOver(), "Game should be over when no valid jumps remain.");
	}
	@Test
	void testRandomizeBoardChangesState() {
		ManualGame manualModel = (ManualGame) model;
		int initialPegCount = 0;
		for (int r = 0; r < 7; r++) {
			for (int c = 0; c < 7; c++) {
				if (manualModel.getCell(r, c) == SolitaireModel.PEG) initialPegCount++;
			}
		}
		
		manualModel.randomizeBoard();
		
		int newPegCount = 0;
		for (int r = 0; r < 7; r++) {
			for (int c = 0; c < 7; c++) {
				if (manualModel.getCell(r, c) == SolitaireModel.PEG) newPegCount++;
			}
		}
		// The odds of a random boolean assignment perfectly matching the exact starting peg count are near zero
		assertNotEquals(initialPegCount, newPegCount, "Randomize should alter the total number of pegs on the board.");
	}

	@Test
	void testAutomatedGameExecutesValidMove() {
		// Set up an automated game using the standard English board from setUp()
		AutomatedGame autoGame = new AutomatedGame(model);
		
		// The standard English board always has available starting moves
		boolean moveWasMade = autoGame.makeAutomatedMove();
		
		assertTrue(moveWasMade, "The AI should successfully find and execute at least one valid starting move.");
	}
}

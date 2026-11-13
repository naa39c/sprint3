package solitaire.controller;

import java.io.File;
import javax.swing.Timer;
import solitaire.model.AutomatedGame;
import solitaire.model.ManualGame;
import solitaire.model.SolitaireModel;
import solitaire.view.SolitaireView;

// view to logic
public class SolitaireController {

	private SolitaireModel model;
	private final SolitaireView view;
	private int selectedRow = -1;
	private int selectedCol = -1;
	private Timer autoPlayTimer;

	// constructs controller and sets to view
	public SolitaireController(SolitaireModel model, SolitaireView view) {
		this.model = model;
		this.view = view;
		this.view.setController(this);
	}

	// mouse click to set peg/move
	public void handleBoardClick(int row, int col) {
		// set to ignore clicks if the AI is playing
		if (model instanceof AutomatedGame)
			return;

		if (model.getCell(row, col) == SolitaireModel.INVALID) {
			return;
		}

		if (selectedRow == -1 && selectedCol == -1) {
			if (model.getCell(row, col) == SolitaireModel.PEG) {
				selectedRow = row;
				selectedCol = col;
			}
		} else {
			if (model.makeMove(selectedRow, selectedCol, row, col)) {
				if (model.isGameOver()) {
					view.gameOverMessage();
				}
			}
			selectedRow = -1;
			selectedCol = -1;
		}
		view.refreshBoard();
	}

	// resets game and forces new view
	public void startNewGame(SolitaireModel.BoardType type, int size) {
		stopAutoplay();
		model = new ManualGame(type, size); // instantiate subclass
		view.setModel(model); // FIXED: Syncs the view with the new model
		selectedRow = -1;
		selectedCol = -1;
		view.refreshBoard();
	}

	// scrambles the board
	public void triggerRandomize() {
		if (model instanceof ManualGame) {
			((ManualGame) model).randomizeBoard();
			view.refreshBoard();
			if (model.isGameOver()) {
				view.gameOverMessage();
			}
		}
	}

	// initiates AI loop
	public void triggerAutoplay() {
		if (model instanceof ManualGame) {
			model = new AutomatedGame(model);
			view.setModel(model); // FIXED: Syncs the view with the AI model
		}

		autoPlayTimer = new Timer(500, event -> {
			boolean moved = ((AutomatedGame) model).makeAutomatedMove();
			view.refreshBoard();

			if (!moved || model.isGameOver()) {
				stopAutoplay();
				view.gameOverMessage();
			}
		});
		autoPlayTimer.start();
	}

	private void stopAutoplay() {
		if (autoPlayTimer != null && autoPlayTimer.isRunning()) {
			autoPlayTimer.stop();
		}
	}

	public void saveGameToFile(File file) {
		if (model instanceof ManualGame) {
			((ManualGame) model).saveGame(file);
		}
	}

	public void loadGameFromFile(File file) {
		stopAutoplay();
		if (!(model instanceof ManualGame)) {
			model = new ManualGame(SolitaireModel.BoardType.ENGLISH, 7);
			view.setModel(model); // sync the view with the loaded manual model
		}
		((ManualGame) model).loadGame(file);
		view.refreshBoard();
	}
}

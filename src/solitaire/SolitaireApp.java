package solitaire;

import javax.swing.SwingUtilities;
import solitaire.controller.SolitaireController;
import solitaire.model.ManualGame;
import solitaire.model.SolitaireModel;
import solitaire.view.SolitaireView;

// main that implements the actual solitaire app
public class SolitaireApp {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			// starts as manual game by default
			SolitaireModel model = new ManualGame(SolitaireModel.BoardType.ENGLISH, 7);
			SolitaireView view = new SolitaireView(model);
			new SolitaireController(model, view);
			view.setLocationRelativeTo(null);
			view.setVisible(true);
		});
	}
}

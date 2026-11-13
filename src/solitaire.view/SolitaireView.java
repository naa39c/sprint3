package solitaire.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import solitaire.controller.SolitaireController;
import solitaire.model.SolitaireModel;

// Draws the main window, side menus, and the peg board
public class SolitaireView extends JFrame {

	private SolitaireModel gameModel;
	private SolitaireController gameController;
	private final BoardDisplayPanel boardPanel;

	private final JTextField sizeInput;
	private final JRadioButton englishOption;
	private final JRadioButton hexagonOption;
	private final JRadioButton diamondOption;

	// sets up the main game window and all the buttons
	public SolitaireView(SolitaireModel model) {
		this.gameModel = model;
		setTitle("Nicolas's Solitaire - Sprint 3");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(850, 600);
		setLayout(new BorderLayout(10, 10));

		// left side menu for board types
		JPanel leftMenu = new JPanel(new BorderLayout());
		JPanel radioGroupPanel = new JPanel(new GridLayout(4, 1));
		radioGroupPanel.add(new JLabel("Board Type:"));

		englishOption = new JRadioButton("English", true);
		hexagonOption = new JRadioButton("Hexagon");
		diamondOption = new JRadioButton("Diamond");

		ButtonGroup typesGroup = new ButtonGroup();
		typesGroup.add(englishOption);
		typesGroup.add(hexagonOption);
		typesGroup.add(diamondOption);

		radioGroupPanel.add(englishOption);
		radioGroupPanel.add(hexagonOption);
		radioGroupPanel.add(diamondOption);
		leftMenu.add(radioGroupPanel, BorderLayout.NORTH);

		JCheckBox recordToggle = new JCheckBox("Record game");
		leftMenu.add(recordToggle, BorderLayout.SOUTH);
		add(leftMenu, BorderLayout.WEST);

		// top bar for board size and the new game buttons
		JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topBar.add(new JLabel("Board size:"));
		sizeInput = new JTextField("7", 3);
		topBar.add(sizeInput);

		JButton btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(event -> handleNewGameClick());
		topBar.add(btnNewGame);

		// Sprint 3 Additions
		JButton btnRandomize = new JButton("Randomize");
		btnRandomize.addActionListener(event -> {
			if (gameController != null)
				gameController.triggerRandomize();
		});
		topBar.add(btnRandomize);

		JButton btnAutoplay = new JButton("Autoplay");
		btnAutoplay.addActionListener(event -> {
			if (gameController != null)
				gameController.triggerAutoplay();
		});
		topBar.add(btnAutoplay);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(event -> handleSaveClick());
		topBar.add(btnSave);

		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(event -> handleLoadClick());
		topBar.add(btnLoad);

		add(topBar, BorderLayout.NORTH);

		// the actual area where the pegs are drawn
		boardPanel = new BoardDisplayPanel();
		boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(boardPanel, BorderLayout.CENTER);
	}

	// wires up the controller to listen for mouse clicks
	public void setController(SolitaireController controller) {
		this.gameController = controller;
	}

	// allows the controller to sync the view when generating AI/Manual boards
	public void setModel(SolitaireModel newModel) {
		this.gameModel = newModel;
	}

	// forces the screen to update after a move
	public void refreshBoard() {
		boardPanel.repaint();
	}

	// pops up when no valid jumps are left
	public void gameOverMessage() {
		JOptionPane.showMessageDialog(this, "Game over, no remaining moves");
	}

	// reads what the user typed/selected and restarts the game
	private void handleNewGameClick() {
		if (gameController == null) {
			return;
		}
		int requestedSize = 7;
		try {
			requestedSize = Integer.parseInt(sizeInput.getText().trim());
		} catch (NumberFormatException error) {
			JOptionPane.showMessageDialog(this, "Invalid board size. Set to 7 instead");
			sizeInput.setText("7");
		}

		SolitaireModel.BoardType selectedType = SolitaireModel.BoardType.ENGLISH;
		if (hexagonOption.isSelected()) {
			selectedType = SolitaireModel.BoardType.HEXAGON;
		} else if (diamondOption.isSelected()) {
			selectedType = SolitaireModel.BoardType.DIAMOND;
		}

		gameController.startNewGame(selectedType, requestedSize);
	}

	private void handleSaveClick() {
		if (gameController != null) {
			JFileChooser chooser = new JFileChooser();
			if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				gameController.saveGameToFile(chooser.getSelectedFile());
			}
		}
	}

	private void handleLoadClick() {
		if (gameController != null) {
			JFileChooser chooser = new JFileChooser();
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				gameController.loadGameFromFile(chooser.getSelectedFile());
			}
		}
	}

	// custom panel that handles the math for drawing the circles
	private class BoardDisplayPanel extends JPanel {
		private static final int CELL_WIDTH = 50;
		private static final int PEG_WIDTH = 36;
		private static final int OFFSET = (CELL_WIDTH - PEG_WIDTH) / 2;

		public BoardDisplayPanel() {
			setPreferredSize(new Dimension(500, 500));

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					if (gameController != null) {
						int clickedCol = event.getX() / CELL_WIDTH;
						int clickedRow = event.getY() / CELL_WIDTH;
						gameController.handleBoardClick(clickedRow, clickedCol);
					}
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			int totalRows = getHeight() / CELL_WIDTH;
			int totalCols = getWidth() / CELL_WIDTH;

			for (int row = 0; row < totalRows; row++) {
				for (int col = 0; col < totalCols; col++) {
					int pegState = gameModel.getCell(row, col);

					if (pegState == SolitaireModel.INVALID) {
						continue;
					}
					int xPos = (col * CELL_WIDTH) + OFFSET;
					int yPos = (row * CELL_WIDTH) + OFFSET;

					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(xPos, yPos, PEG_WIDTH, PEG_WIDTH);
					g.setColor(Color.BLACK);
					g.drawOval(xPos, yPos, PEG_WIDTH, PEG_WIDTH);

					if (pegState == SolitaireModel.PEG) {
						g.setColor(Color.BLUE);
						g.fillOval(xPos, yPos, PEG_WIDTH, PEG_WIDTH);
					}
				}
			}
		}
	}
}

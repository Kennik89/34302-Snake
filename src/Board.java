import java.awt.*;

import javax.swing.*;

public class Board extends JPanel {

	private static final long serialVersionUID = 1L;

	// Boardets dimensioner
	public static final int COL_COUNT = 80;
	public static final int ROW_COUNT = 80;
	public static final int TILE_SIZE = 8;

	// Font
	private static final Font FONT = new Font("Helvetica", Font.BOLD, 25);

	// Initialisering af spilklient
	private SnakeMainClient game;

	// TileArray
	private TileType[] tiles;

	// Laver et board
	public Board(SnakeMainClient game) {

		this.game = game;
		this.tiles = new TileType[ROW_COUNT * COL_COUNT];

		// Vores foretrukne st¿rrelse pŒ Spillets lækkerhed)
		setPreferredSize(new Dimension(COL_COUNT * TILE_SIZE, ROW_COUNT
				* TILE_SIZE));
		setBackground(new Color(254, 246, 226)); // Random senere
	}

	// T¿mmer board
	public void clearBoard() {
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = null;
		}
	}

	// Sætter vores tiles hvor de skal være :=)
	public void setTile(Point point, TileType type) {
		setTile(point.x, point.y, type);
	}

	// Sætter vores tiles hvor de skal være numero 2 ;-))))
	public void setTile(int x, int y, TileType type) {
		tiles[y * ROW_COUNT + x] = type;
	}

	// FŒr at vide hvor den rigtige tile skal være placeret
	public TileType getTile(int x, int y) {
		return tiles[y * ROW_COUNT + x];
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Vi laver lige et loop til at tegne vores tile hvis det ikke er null
		for (int x = 0; x < COL_COUNT; x++) {
			for (int y = 0; y < ROW_COUNT; y++) {
				TileType type = getTile(x, y);
				if (type != null) {
					drawTile(x * TILE_SIZE, y * TILE_SIZE, type, g);
				}
			}
		}

		if (game.isGameOver() || game.isNewGame()) {
			g.setColor(new Color(19, 129, 186));

			// Vi finder centrum af boardet
			int centerX = getWidth() / 2;
			int centerY = getHeight() / 2;

			// Meddelelser til spilleren
			String largeMessage = null;
			String smallMessage = null;
			if (game.isNewGame()) {
				largeMessage = "SNAKE GAME!";
				smallMessage = "Press Enter to Start";
			} else if (game.isGameOver()) {
				largeMessage = "GAME OVER!";
				smallMessage = "Press Enter to Restart";
			}

			g.setFont(FONT);
			g.drawString(largeMessage, centerX
					- g.getFontMetrics().stringWidth(largeMessage) / 2,
					centerY - 50);
			g.drawString(smallMessage, centerX
					- g.getFontMetrics().stringWidth(smallMessage) / 2,
					centerY + 50);

		}
	}

	private void drawTile(int x, int y, TileType type, Graphics g) {
		// Vi benytter os af switch-case metode for at simplificere vores kode.
		// Ellers skulle vi skrive en masse un¿dvendig kode :)

		switch (type) {

		// Case til hul
		case Space:
			g.setColor(new Color(100, 100, 100));
			g.fillOval(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
			break;

		// Case til vores hoved/start
		case Head:
			g.setColor(new Color(107, 123, 4)); // Random senere
			g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
			break;

		// Case til kroppen
		case Body:
			g.setColor(new Color(171, 197, 7)); // Random senere
			g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
			break;
		}
	}
}
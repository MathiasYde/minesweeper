import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Function;

public class MinesweeperBoard extends JPanel implements MouseListener {
	private static final int CELL_SIZE = 16;
	private static final int CELL_SCALE = 4;
	private static final int CELL_SCALED_SIZE = CELL_SIZE * CELL_SCALE;

	private Font bannerFont;
	private String bannerText;

	private Function<Int2D, Void> onClickCellCallback;
	private Function<Int2D, Void> onClickFlagCallback;

	private int[][] cells;

	private HashMap<Integer, Image> tileImages;

	public void setBannerText(String bannerText) {
		this.bannerText = bannerText;
		repaint();
	}

	public void setCells(int[][] cells) {
		this.cells = cells;
		repaint();
	}

	public MinesweeperBoard() {
		addMouseListener(this);

		bannerFont = new Font("Comic Sans", Font.BOLD, 48);

		tileImages = new HashMap<>() {{
			put(Minesweeper.CELL_UNREVEALED, loadImage("assets/tiles/tile_unrevealed.png"));
			put(Minesweeper.CELL_FLAG_BOMB, loadImage("assets/tiles/tile_flag.png"));
			put(Minesweeper.CELL_FLAG_NONE_BOMB, loadImage("assets/tiles/tile_flag.png"));
			put(Minesweeper.CELL_REVEALED_BOMB, loadImage("assets/tiles/tile_bomb.png"));
			put(Minesweeper.CELL_UNREVEALED_BOMB, loadImage("assets/tiles/tile_unrevealed.png")); // unrevealed bombs are drawn as unrevealed tiles
			//put(Minesweeper.CELL_UNREVEALED_BOMB, loadImage("assets/tiles/tile_bomb.png")); // debug
			put(0, loadImage("assets/tiles/tile_empty.png"));
			put(1, loadImage("assets/tiles/tile_1.png"));
			put(2, loadImage("assets/tiles/tile_2.png"));
			put(3, loadImage("assets/tiles/tile_3.png"));
			put(4, loadImage("assets/tiles/tile_4.png"));
			put(5, loadImage("assets/tiles/tile_5.png"));
			put(6, loadImage("assets/tiles/tile_6.png"));
			put(7, loadImage("assets/tiles/tile_7.png"));
			put(8, loadImage("assets/tiles/tile_8.png"));
		}};
	}

	private Image loadImage(String filepath) {
		try {
			BufferedImage image = ImageIO.read(new File(filepath));
			return image.getScaledInstance(CELL_SCALED_SIZE, CELL_SCALED_SIZE, Image.SCALE_SMOOTH);
		} catch (IOException e) {
			System.out.println("ERROR: Could not find image at path: " + filepath);
			return null;
		}
	}

	public void setOnClickCell(Function<Int2D, Void> callback) {
		onClickCellCallback = callback;
	}

	public void setOnClickFlag(Function<Int2D, Void> callback) {
		onClickFlagCallback = callback;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// draw tiles
		for (int y = 0; y < Minesweeper.BOARD_HEIGHT; y++) {
			for (int x = 0; x < Minesweeper.BOARD_WIDTH; x++) {
				int cell = cells[y][x];
				Image image = tileImages.get(cell);

				g.drawImage(
								image,
								x * CELL_SCALED_SIZE,
								y * CELL_SCALED_SIZE,
								null
				);
			}
		}

		// draw banner text
		if (bannerText != null) {
			// semi transparent bar behind the text
			int bannerHeight = getHeight() / 3;

			g.setColor(new Color(0, 0, 0, 0.5f));
			g.fillRect(0, getHeight() / 2 - bannerHeight / 2, getWidth(), bannerHeight);

			// big font
			g.setFont(bannerFont);

			// center the text
			FontMetrics metrics = g.getFontMetrics();
			int x = (getWidth() - metrics.stringWidth(bannerText)) / 2;
			int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

			g.setColor(Color.WHITE);
			g.drawString(bannerText, x, y);
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(
						Minesweeper.BOARD_WIDTH * CELL_SCALED_SIZE,
						Minesweeper.BOARD_HEIGHT * CELL_SCALED_SIZE
		);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Int2D position = new Int2D(
						e.getX() / (CELL_SCALED_SIZE),
						e.getY() / (CELL_SCALED_SIZE)
		);

		switch (e.getButton()) {
			case MouseEvent.BUTTON1: // primary button, left
				onClickCellCallback.apply(position);
				break;
			case MouseEvent.BUTTON3: // secondary button, right
				onClickFlagCallback.apply(position);
				break;
			default:
				return;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
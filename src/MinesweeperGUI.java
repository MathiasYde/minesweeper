import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class MinesweeperGUI extends JFrame implements IMinesweeperGUI {
    private MinesweeperBoard board;

    public MinesweeperGUI() {
        setTitle("Minesweeper");

        board = new MinesweeperBoard();
        add(board);

        Dimension size = board.getPreferredSize();
        setSize(size.width + 16, size.height + 39);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void setOnClickCell(Function<Int2D, Void> callback) {
        board.setOnClickCell(callback);
    }

    @Override
    public void setOnClickFlag(Function<Int2D, Void> callback) {
        board.setOnClickFlag(callback);
    }

    @Override
    public void setCells(int[][] cells) {
        board.setCells(cells);
    }

    @Override
    public void setBannerText(String text) {
        board.setBannerText(text);
    }
}
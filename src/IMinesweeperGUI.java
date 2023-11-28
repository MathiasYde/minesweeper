import java.util.function.Function;

public interface IMinesweeperGUI {
    void setOnClickCell(Function<Int2D, Void> callback);

    void setOnClickFlag(Function<Int2D, Void> callback);

    void setCells(int[][] cells);

    void setBannerText(String text);
}

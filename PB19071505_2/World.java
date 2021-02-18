package PB19071505_2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class World extends Pane {
    private int max;    //edge length of square world
    private SimpleIntegerProperty time = new SimpleIntegerProperty();                           //how much time have pased
    private ArrayList<ArrayList<Rectangle>> cells = new ArrayList<ArrayList<Rectangle>>();      //rectangular cells
    private ArrayList<ArrayList<Boolean>> next = new ArrayList<ArrayList<Boolean>>();           //used in step run
    private SimpleIntegerProperty cellsnum = new SimpleIntegerProperty();                       //how many cells alive
    private SimpleDoubleProperty proportion = new SimpleDoubleProperty();                       //living cells' percentage
    Rule rule;          //how the cells live and die

    World(int max) {
        super();

        time.set(0);

        if (max > 0)
            this.max = max;
        else{
            this.max = 80;
            max = 80;
        }

        //cells' position binds the world
        for (int i = 0; i < max; i++) {
            cells.add(new ArrayList<Rectangle>());
            next.add(new ArrayList<Boolean>());
            for (int j = 0; j < max; j++) {
                cells.get(i).add(new Rectangle());
                next.get(i).add(false);
                Rectangle c = cells.get(i).get(j);
                c.setFill(Color.WHITE);
                c.xProperty().bind(this.widthProperty().multiply(i).divide(max));
                c.yProperty().bind(this.heightProperty().multiply(j).divide(max));
                c.widthProperty().bind(this.widthProperty().divide(max).subtract(1));
                c.heightProperty().bind(this.heightProperty().divide(max).subtract(1));
                this.getChildren().add(c);
            }
        }

        cellsnum.set(0);
        proportion.set(0.0);

        rule = new Rule();
    }

    World() {
        this(100);
    }

    //set the cell at (x,y) live/die
    void setCell(Double x, Double y, boolean state) {
        int xindex = (int) (x / (this.getWidth() / max));
        int yindex = (int) (y / (this.getHeight() / max));

        if (xindex < 0 || xindex >= max || yindex < 0 || yindex >= max)
            return;

        next.get(xindex).set(yindex, state);
        if (cells.get(xindex).get(yindex).getFill().equals(Color.WHITE) && state == true) {
            cells.get(xindex).get(yindex).setFill(Color.BLACK);
            cellsnum.set(cellsnum.get() + 1);
        }
        else if (cells.get(xindex).get(yindex).getFill().equals(Color.BLACK) && state == false) {
            cells.get(xindex).get(yindex).setFill(Color.WHITE);
            cellsnum.set(cellsnum.get() - 1);
        }

        proportion.set((double) cellsnum.get() / max / max);
    }

    //run for once
    void oneStep() {

        time.set(time.get() + 1);
        //get rule's number
        int r = rule.getRadius();
        int ab = rule.getAliveBottom();
        int at = rule.getAliveTop();
        int rb = rule.getReproduceBottom();
        int rt = rule.getReproduceTop();
        double op = rule.getOccurProbability();
        double dp = rule.getDisappearProbability();

        //for each cell, judge whether it will live/die
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {

                //count how many cells is around
                int count = 0;
                for (int k = -r; k <= r; k++) {
                    for (int l = -r; l <= r; l++) {

                        if ((k == 0 && l == 0) || i + k < 0 || i + k >= max || j + l < 0 || j + l >= max)
                            continue;

                        if (cells.get(i + k).get(j + l).getFill().equals(Color.BLACK))
                            count++;

                    }
                }

                //judge whether the cell will die/live according to count 
                if (cells.get(i).get(j).getFill().equals(Color.BLACK)) {
                    if (count < ab || count > at)
                        next.get(i).set(j, false);
                }
                else {
                    if (count >= rb && count <= rt)
                        next.get(i).set(j, true);
                }

                //live/die in random
                if (next.get(i).get(j) == true) {
                    if (Math.random() < dp)
                        next.get(i).set(j, false);
                }
                else {
                    if (Math.random() < op)
                        next.get(i).set(j, true);
                }

            }
        }

        //set cells to next
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {

                if (cells.get(i).get(j).getFill().equals(Color.WHITE) && next.get(i).get(j).equals(true)) {
                    cells.get(i).get(j).setFill(Color.BLACK);
                    cellsnum.set(cellsnum.get() + 1);
                }
                else if (cells.get(i).get(j).getFill().equals(Color.BLACK) && next.get(i).get(j).equals(false)) {
                    cells.get(i).get(j).setFill(Color.WHITE);
                    cellsnum.set(cellsnum.get() - 1);
                }

            }
        }

        proportion.set((double) cellsnum.get() / max / max);

    }

    //kill all cells
    void clean() {
        time.set(0);
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                cells.get(i).get(j).setFill(Color.WHITE);
                next.get(i).set(j, false);
            }
        }
        cellsnum.set(0);
        proportion.set(0);
    }

    //set a example in the center
    void example(String s) {
        int middle = max / 2;
        clean();
        int[][] a;
        switch (s) {

            case "板凳":
                a = new int[][] { 
                    { 1, 1 }, 
                    { 1, 1 } 
                };
                break;

            case "蜂巢":
                a = new int[][] { 
                    { 0, 1, 1, 0 }, 
                    { 1, 0, 0, 1 }, 
                    { 0, 1, 1, 0 } 
                };
                break;

            case "面包":
                a = new int[][] { 
                    { 0, 1, 1, 0 }, 
                    { 1, 0, 0, 1 }, 
                    { 0, 1, 0, 1 }, 
                    { 0, 0, 1, 0 } 
                };
                break;

            case "船":
                a = new int[][] { 
                    { 1, 1, 0 }, 
                    { 1, 0, 1 }, 
                    { 0, 1, 0 } 
                };
                break;

            case "花":
                a = new int[][] { 
                    { 0, 1, 0 }, 
                    { 1, 0, 1 }, 
                    { 0, 1, 0 } 
                };
                break;


            case "信号灯 (周期 2)":
                a = new int[][] { 
                    { 1, 1, 1 } 
                };
                break;

            case "蟾蜍 (周期) 2)":
                a = new int[][] { 
                    { 0, 1, 1, 1 }, 
                    { 1, 1, 1, 0 } 
                };
                break;

            case "烽火 (周期 2)":
                a = new int[][] { 
                    { 1, 1, 0, 0 }, 
                    { 1, 1, 0, 0 }, 
                    { 0, 0, 1, 1 }, 
                    { 0, 0, 1, 1 } 
                };
                break;

            case "脉冲星 (周期 3)":
                a = new int[][] { 
                    { 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0 }, 
                    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1 }, 
                    { 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1 },
                    { 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1 }, 
                    { 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0 },
                    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
                    { 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0 },
                    { 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1 }, 
                    { 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1 },
                    { 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1 }, 
                    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0 } 
                };
                break;

            case "慨影 (周期 15)":
                a = new int[][] { 
                    { 1, 1, 1 }, 
                    { 1, 0, 1 }, 
                    { 1, 1, 1 }, 
                    { 1, 1, 1 }, 
                    { 1, 1, 1 }, 
                    { 1, 1, 1 },
                    { 1, 0, 1 }, 
                    { 1, 1, 1 }, 
                };
                break;


            case "滑翔机":
                a = new int[][] { 
                    { 0, 1, 0 }, 
                    { 0, 0, 1 }, 
                    { 1, 1, 1 } 
                };
                break;

            case "小型太空船":
                a = new int[][] { 
                    { 0, 1, 1, 1, 1 }, 
                    { 1, 0, 0, 0, 1 }, 
                    { 0, 0, 0, 0, 1 }, 
                    { 1, 0, 0, 1, 0 } 
                };
                break;

            case "太空船":
                a = new int[][] { 
                    { 0, 1, 1, 1, 1, 1 }, 
                    { 1, 0, 0, 0, 0, 1 }, 
                    { 0, 0, 0, 0, 0, 1 },
                    { 1, 0, 0, 0, 1, 0 }, 
                    { 0, 0, 1, 0, 0, 0 } };
                break;

            case "大型太空船":
                a = new int[][] { 
                    { 0, 1, 1, 1, 1, 1, 1 }, 
                    { 1, 0, 0, 0, 0, 0, 1 }, 
                    { 0, 0, 0, 0, 0, 0, 1 },
                    { 1, 0, 0, 0, 0, 1, 0 }, 
                    { 0, 0, 1, 1, 0, 0, 0 }, 
                };
                break;

            default:
                return;

        }

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {

                if (a[i][j] == 1) {

                    if (middle + j - a[0].length / 2 >= 0 && middle + j - a[0].length / 2 < max
                            && middle + i - a.length / 2 >= 0 && middle + i - a.length / 2 < max) {
                        cells.get(middle + j - a[0].length / 2).get(middle + i - a.length / 2).setFill(Color.BLACK);
                        next.get(middle + j - a[0].length / 2).set(middle + i - a.length / 2, true);
                        cellsnum.set(cellsnum.get() + 1);
                    }

                }

            }
        }

        proportion.set((double) cellsnum.get() / max / max);
    }

    //draw an example throw an 2 dimension array (the array is from a file)
    void example(ArrayList<ArrayList<Integer>> a) {
        int middle = max / 2;
        clean();

        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < a.get(0).size(); j++) {

                if (a.get(i).get(j) == 1) {

                    if (middle + j - a.get(0).size() / 2 >= 0 && middle + j - a.get(0).size() / 2 < max
                            && middle + i - a.size() / 2 >= 0 && middle + i - a.size() / 2 < max) {
                        cells.get(middle + j - a.get(0).size() / 2).get(middle + i - a.size() / 2).setFill(Color.BLACK);
                        next.get(middle + j - a.get(0).size() / 2).set(middle + i - a.size() / 2, true);
                        cellsnum.set(cellsnum.get() + 1);
                    }

                }

            }
        }

        proportion.set((double) cellsnum.get() / max / max);
    }
 
    void setScale(Integer newValue) {
        clean();
        this.getChildren().clear();
        cells.clear();
        next.clear();
        max = newValue;

        for (int i = 0; i < max; i++) {

            cells.add(new ArrayList<Rectangle>());
            next.add(new ArrayList<Boolean>());
            for (int j = 0; j < max; j++) {

                cells.get(i).add(new Rectangle());
                next.get(i).add(false);
                Rectangle c = cells.get(i).get(j);
                c.setFill(Color.WHITE);
                c.xProperty().bind(this.widthProperty().multiply(i).divide(max));
                c.yProperty().bind(this.heightProperty().multiply(j).divide(max));
                c.widthProperty().bind(this.widthProperty().divide(max).subtract(1));
                c.heightProperty().bind(this.heightProperty().divide(max).subtract(1));
                this.getChildren().add(c);

            }

        }
    }

    void save(File file) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
        osw.write(String.valueOf(max));
        osw.write("\n");
        for (int i = 0; i < max; i++) {

            for (int j = 0; j < max; j++) {

                if (next.get(j).get(i) == true)
                    osw.write("1");
                else
                    osw.write("0");

            }

            if (i < max - 1)
                osw.write("\n");
        }
        osw.close();
    }

    void setRule(String s) {
        switch (s) {
            case "康威生命游戏":
                rule.setRule(1, 3, 3, 2, 3, 0.0, 0.0);
                break;
        }
    }

    IntegerProperty timeProperty() { return time; }

    IntegerProperty cellsnumProperty() { return cellsnum; }

    DoubleProperty proportionProperty() { return proportion; }

    class Rule {

        private int radius;

        private int reproduceTop;
        private int reproduceBottom;

        private int aliveTop;
        private int aliveBottom;

        private double occurProbability;
        private double disappearProbability;

        public Rule(int radius, int reproduceBottom, int reproduceTop, int aliveBottom, int aliveTop,
                double occurProbability, double disappearProbability) {
            this.radius = radius;
            this.reproduceBottom = reproduceBottom;
            this.reproduceTop = reproduceTop;
            this.aliveBottom = aliveBottom;
            this.aliveTop = aliveTop;
            this.occurProbability = occurProbability;
            this.disappearProbability = disappearProbability;
        }

        public Rule() {
            this(1, 3, 3, 2, 3, 0.0, 0.0);
        }

        void setRule(int radius, int reproduceBottom, int reproduceTop, int aliveBottom, int aliveTop,
                double occurProbability, double disappearProbability) {
            this.radius = radius;
            this.reproduceBottom = reproduceBottom;
            this.reproduceTop = reproduceTop;
            this.aliveBottom = aliveBottom;
            this.aliveTop = aliveTop;
            this.occurProbability = occurProbability;
            this.disappearProbability = disappearProbability;
        }

        public int getRadius() { return radius; }

        public void setRadius(int radius) { this.radius = radius; }

        public int getReproduceTop() { return reproduceTop; }

        public void setReproduceTop(int reproduceTop) { this.reproduceTop = reproduceTop; }

        public int getReproduceBottom() { return reproduceBottom; }

        public void setReproduceBottom(int reproduceBottom) { this.reproduceBottom = reproduceBottom; }

        public int getAliveTop() { return aliveTop; }

        public void setAliveTop(int aliveTop) { this.aliveTop = aliveTop; }

        public int getAliveBottom() { return aliveBottom; }

        public void setAliveBottom(int aliveBottom) { this.aliveBottom = aliveBottom; }

        public double getOccurProbability() { return occurProbability; }

        public void setOccurProbability(double occurProbability) { this.occurProbability = occurProbability; }

        public double getDisappearProbability() { return disappearProbability; }

        public void setDisappearProbability(double disappearProbability) { this.disappearProbability = disappearProbability; }

    }

}

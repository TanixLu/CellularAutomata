package ChineseVersion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

/***
*TO DO:
*paint              done
*some my rules      done
*fix "/1000"        done
*help document      done
*no grids           done
*auto rate improve  done
***/

public class CellularAutomata extends Application {

    boolean drawflag = true;    //whether I can draw cells
    double ratenum = 20;        //how fastly it runs automatically (left add, right delete)

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        //open or save button (they have shorcuts below)
        Button open = new Button("打开");
        Button save = new Button("保存");


        //display some examples
        Label examplesLabel = new Label("一些模型");
        ChoiceBox<Object> examplesChoiceBox = new ChoiceBox<Object>();
        examplesChoiceBox.getItems().addAll(
            "未选择",
            new Separator(), 
            "板凳", "蜂巢", "面包", "船", "花",
            new Separator(), 
            "信号灯 (周期 2)", "蟾蜍 (周期 2)", "烽火 (周期 2)", "脉冲星 (周期 3)","慨影 (周期 15)",
            new Separator(),
            "滑翔机", "小型太空船","太空船", "大型太空船"
        );
        examplesChoiceBox.setValue("未选择");


        //set edge lenth of the square world 
        Label scale = new Label("规模");
        ChoiceBox<Integer> scaleChoiceBox = new ChoiceBox<Integer>();
        scaleChoiceBox.getItems().addAll(10, 20, 50, 100, 150, 200, 300);
        scaleChoiceBox.setValue(100);


        //set rule of the world
        Label rules = new Label("规则");
        ChoiceBox<String> rulesChoiceBox = new ChoiceBox<String>();
        rulesChoiceBox.getItems().addAll("未选择", "康威生命游戏");
        rulesChoiceBox.setValue("康威生命游戏");


        //set neighborhood radius of cell
        Label radius = new Label("半径");
        ChoiceBox<Integer> radiusChoiceBox = new ChoiceBox<Integer>();
        radiusChoiceBox.getItems().addAll(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        radiusChoiceBox.setValue(1);


        //set how many cells around a dead cell can make it alive
        Label reproduce = new Label("繁殖条件");

        Label reproduceTop = new Label("上界");
        TextField reproduceTopTextField = new TextField();
        reproduceTopTextField.setText("3");
        reproduceTopTextField.setPrefWidth(100);

        Label reproduceBottom = new Label("下界");
        TextField reproduceBottomTextField = new TextField();
        reproduceBottomTextField.setText("3");
        reproduceBottomTextField.setPrefWidth(100);


        //set how many cells around a living cell can keep it alive
        Label aliveConditions = new Label("生存条件");

        Label aliveTop = new Label("上界");
        TextField aliveTopTextField = new TextField();
        aliveTopTextField.setText("3");
        aliveTopTextField.setPrefWidth(100);

        Label aliveBottom = new Label("下界");
        TextField aliveBottomTextField = new TextField();
        aliveBottomTextField.setText("2");
        aliveBottomTextField.setPrefWidth(100);


        //set how much probability a dead cell will be alive after every step
        Label randomOccur = new Label("随机产生");

        Label occurProbability = new Label("概率(‰)");
        TextField occurProbabilityTextField = new TextField();
        occurProbabilityTextField.setText("0");
        occurProbabilityTextField.setPrefWidth(100);


        //set how much probability a living cell will die after every step
        Label randomDisappear = new Label("随机消失");

        Label disappearProbability = new Label("概率(‰)");
        TextField disappearProbabilityTextField = new TextField();
        disappearProbabilityTextField.setText("0");
        disappearProbabilityTextField.setPrefWidth(100);



        //run automatically
        CheckBox auto = new CheckBox("自动演化");
        //run for once
        Button onestep = new Button("演化一步");
        //kill all cells
        Button clean = new Button("清屏");

        VBox vb = new VBox(10);
        vb.getChildren().addAll(auto, onestep, clean);

        //how fastly it runs automatically
        TextField rate = new TextField("20");
        rate.setPromptText("rate");
        rate.setPrefWidth(100);

        Label persecond = new Label("/s");

        //whether I can draw cells
        CheckBox draw = new CheckBox("绘画");
        draw.setSelected(true);


        //menu that controls the world
        AnchorPane menu = new AnchorPane();
        menu.setStyle("-fx-background-color:#F5F5F5");
        menu.getChildren().addAll(
            open, save, 
            examplesLabel, examplesChoiceBox, 
            scale, scaleChoiceBox, 
            rules,rulesChoiceBox,
            radius, radiusChoiceBox,
            reproduce, reproduceTop, reproduceTopTextField,reproduceBottom, reproduceBottomTextField,
            aliveConditions, aliveTop, aliveTopTextField, aliveBottom,aliveBottomTextField,
            randomOccur, occurProbability, occurProbabilityTextField,
            randomDisappear,disappearProbability, disappearProbabilityTextField,
            vb, rate, persecond, draw
        );


        /*****menu layout*****/
        AnchorPane.setLeftAnchor(open, 10.0);
        AnchorPane.setTopAnchor(open, 20.0);
        AnchorPane.setLeftAnchor(save, 80.0);
        AnchorPane.setTopAnchor(save, 20.0);


        AnchorPane.setLeftAnchor(examplesLabel, 15.0);
        AnchorPane.setTopAnchor(examplesLabel, 65.0);
        AnchorPane.setLeftAnchor(examplesChoiceBox, 10.0);
        AnchorPane.setTopAnchor(examplesChoiceBox, 100.0);


        AnchorPane.setLeftAnchor(scale, 15.0);
        AnchorPane.setTopAnchor(scale, 150.0);
        AnchorPane.setLeftAnchor(scaleChoiceBox, 70.0);
        AnchorPane.setTopAnchor(scaleChoiceBox, 145.0);


        AnchorPane.setLeftAnchor(rules, 15.0);
        AnchorPane.setTopAnchor(rules, 190.0);
        AnchorPane.setLeftAnchor(rulesChoiceBox, 70.0);
        AnchorPane.setTopAnchor(rulesChoiceBox, 185.0);


        AnchorPane.setLeftAnchor(radius, 15.0);
        AnchorPane.setTopAnchor(radius, 230.0);
        AnchorPane.setLeftAnchor(radiusChoiceBox, 70.0);
        AnchorPane.setTopAnchor(radiusChoiceBox, 225.0);


        AnchorPane.setLeftAnchor(reproduce, 15.0);
        AnchorPane.setTopAnchor(reproduce, 270.0);

        AnchorPane.setLeftAnchor(reproduceTop, 35.0);
        AnchorPane.setTopAnchor(reproduceTop, 300.0);
        AnchorPane.setLeftAnchor(reproduceTopTextField, 95.0);
        AnchorPane.setTopAnchor(reproduceTopTextField, 300.0);

        AnchorPane.setLeftAnchor(reproduceBottom, 35.0);
        AnchorPane.setTopAnchor(reproduceBottom, 330.0);
        AnchorPane.setLeftAnchor(reproduceBottomTextField, 95.0);
        AnchorPane.setTopAnchor(reproduceBottomTextField, 330.0);


        AnchorPane.setLeftAnchor(aliveConditions, 15.0);
        AnchorPane.setTopAnchor(aliveConditions, 370.0);

        AnchorPane.setLeftAnchor(aliveTop, 35.0);
        AnchorPane.setTopAnchor(aliveTop, 400.0);
        AnchorPane.setLeftAnchor(aliveTopTextField, 95.0);
        AnchorPane.setTopAnchor(aliveTopTextField, 400.0);

        AnchorPane.setLeftAnchor(aliveBottom, 35.0);
        AnchorPane.setTopAnchor(aliveBottom, 430.0);
        AnchorPane.setLeftAnchor(aliveBottomTextField, 95.0);
        AnchorPane.setTopAnchor(aliveBottomTextField, 430.0);


        AnchorPane.setLeftAnchor(randomOccur, 15.0);
        AnchorPane.setTopAnchor(randomOccur, 500.0);

        AnchorPane.setLeftAnchor(occurProbability, 45.0);
        AnchorPane.setTopAnchor(occurProbability, 530.0);
        AnchorPane.setLeftAnchor(occurProbabilityTextField, 130.0);
        AnchorPane.setTopAnchor(occurProbabilityTextField, 530.0);


        AnchorPane.setLeftAnchor(randomDisappear, 15.0);
        AnchorPane.setTopAnchor(randomDisappear, 560.0);

        AnchorPane.setLeftAnchor(disappearProbability, 45.0);
        AnchorPane.setTopAnchor(disappearProbability, 590.0);
        AnchorPane.setLeftAnchor(disappearProbabilityTextField, 130.0);
        AnchorPane.setTopAnchor(disappearProbabilityTextField, 590.0);


        AnchorPane.setLeftAnchor(vb, 30.0);
        AnchorPane.setBottomAnchor(vb, 30.0);

        AnchorPane.setRightAnchor(rate, 60.0);
        AnchorPane.setBottomAnchor(rate, 103.5);
        AnchorPane.setRightAnchor(persecond, 40.0);
        AnchorPane.setBottomAnchor(persecond, 108.0);

        AnchorPane.setRightAnchor(draw, 30.0);
        AnchorPane.setBottomAnchor(draw, 30.0);
        /*****menu layout done*****/


        //World externs Pane
        World world = new World(100);
        world.setStyle("-fx-background-color:#000000");


        //some properties
        Label cellsnum = new Label("0");
        Label cells = new Label("细胞");
        cellsnum.textProperty().bind(world.cellsnumProperty().asString());

        Label proportion = new Label("0%");
        proportion.textProperty().bind(world.proportionProperty().multiply(100).asString().concat("%"));

        Label time = new Label("时间");
        Label t = new Label("0");
        t.textProperty().bind(world.timeProperty().asString());

        AnchorPane property = new AnchorPane();
        property.setStyle("-fx-background-color:#F5F5F5");
        property.getChildren().addAll(cellsnum, cells, proportion, t, time);

        //property layout
        AnchorPane.setRightAnchor(cellsnum, 65.0);
        AnchorPane.setBottomAnchor(cellsnum, 100.0);
        AnchorPane.setRightAnchor(cells, 30.0);
        AnchorPane.setBottomAnchor(cells, 100.0);
        AnchorPane.setRightAnchor(proportion, 30.0);
        AnchorPane.setBottomAnchor(proportion, 80.0);

        AnchorPane.setRightAnchor(time, 30.0);
        AnchorPane.setBottomAnchor(time, 50.0);
        AnchorPane.setRightAnchor(t, 30.0);
        AnchorPane.setBottomAnchor(t, 30.0);


        BorderPane root = new BorderPane();
        root.setLeft(menu);
        root.setCenter(world);
        root.setRight(property);

        Scene scene = new Scene(root, 1400, 800);
        menu.prefWidthProperty().bind(scene.widthProperty().multiply(3).divide(14));
        property.prefWidthProperty().bind(scene.widthProperty().multiply(3).divide(14));


        /*****action of items*****/
        onestep.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                world.oneStep();
            }
        });

        AutoRunService auto_service = new AutoRunService();
        auto.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue == true)
                    auto_service.restart();
                else
                    auto_service.cancel();
            }
        });
        auto_service.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                world.oneStep();
            }
        });

        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                open(world);
            }
        });

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                save(world);
            }
        });

        examplesChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                world.example(newValue.toString());
            }
        });

        scaleChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                auto_service.cancel();
                auto.setSelected(false);
                world.setScale(newValue);
            }
        });

        rulesChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                switch (newValue) {
                    case "Conway's game of life":
                        radiusChoiceBox.setValue(1);
                        reproduceBottomTextField.setText("3");
                        reproduceTopTextField.setText("3");
                        aliveBottomTextField.setText("2");
                        aliveTopTextField.setText("3");
                        occurProbabilityTextField.setText("0");
                        disappearProbabilityTextField.setText("0");
                        break;
                }
            }
        });

        radiusChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                world.rule.setRadius(newValue);
            }
        });

        reproduceTopTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.isEmpty()) {
                    world.rule.setReproduceTop(0);
                    return;
                }
                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isinteger = pattern.matcher(newValue);
                if (!isinteger.matches())
                    reproduceTopTextField.setText(oldValue);
                world.rule.setReproduceTop(Integer.parseInt(reproduceTopTextField.getText()));
            }
        });

        reproduceBottomTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.isEmpty()) {
                    world.rule.setReproduceBottom(0);
                    return;
                }
                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isinteger = pattern.matcher(newValue);
                if (!isinteger.matches())
                    reproduceBottomTextField.setText(oldValue);
                world.rule.setReproduceBottom(Integer.parseInt(reproduceBottomTextField.getText()));
            }
        });

        aliveTopTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.isEmpty()) {
                    world.rule.setAliveTop(0);
                    return;
                }
                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isinteger = pattern.matcher(newValue);
                if (!isinteger.matches())
                    aliveTopTextField.setText(oldValue);
                world.rule.setAliveTop(Integer.parseInt(aliveTopTextField.getText()));
            }
        });

        aliveBottomTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.isEmpty()) {
                    world.rule.setAliveBottom(0);
                    return;
                }
                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isinteger = pattern.matcher(newValue);
                if (!isinteger.matches())
                    aliveBottomTextField.setText(oldValue);
                world.rule.setAliveBottom(Integer.parseInt(aliveBottomTextField.getText()));
            }
        });

        occurProbabilityTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.isEmpty()) {
                    world.rule.setOccurProbability(0.0);
                    return;
                }
                Pattern pattern = Pattern.compile("[0-9]*[.]{0,1}[0-9]*");
                Matcher isdouble = pattern.matcher(newValue);
                if (!isdouble.matches())
                    occurProbabilityTextField.setText(oldValue);
                world.rule.setOccurProbability(Double.parseDouble(newValue) / 1000);
            }
        });

        disappearProbabilityTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.isEmpty()) {
                    world.rule.setDisappearProbability(0.0);
                    return;
                }
                Pattern pattern = Pattern.compile("[0-9]*[.]{0,1}[0-9]*");
                Matcher isdouble = pattern.matcher(newValue);
                if (!isdouble.matches())
                    disappearProbabilityTextField.setText(oldValue);
                world.rule.setDisappearProbability(Double.parseDouble(newValue) / 1000);
            }
        });

        rate.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.isEmpty()) {
                    ratenum = 1;
                    return;
                }
                Pattern pattern = Pattern.compile("[0-9]*[.]{0,1}[0-9]*");
                Matcher isdouble = pattern.matcher(newValue);
                if (!isdouble.matches())
                    rate.setText(oldValue);
                ratenum = Double.parseDouble(rate.getText());
            }
        });

        clean.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                world.clean();
            }
        });

        draw.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                drawflag = newValue;
            }
        });

        world.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (drawflag == false)
                    return;
                if (event.getButton().equals(MouseButton.PRIMARY))
                    world.setCell(event.getX(), event.getY(), true);
                if (event.getButton().equals(MouseButton.SECONDARY))
                    world.setCell(event.getX(), event.getY(), false);
            }
        });

        world.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (drawflag == false)
                    return;
                if (event.getButton().equals(MouseButton.PRIMARY))
                    world.setCell(event.getX(), event.getY(), true);
                if (event.getButton().equals(MouseButton.SECONDARY))
                    world.setCell(event.getX(), event.getY(), false);
            }
        });

        
        //shortcuts
        KeyCombination openKC = new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN);
        scene.getAccelerators().put(openKC, new Runnable() {
            @Override
            public void run() {
                open(world);
            }
        });

        KeyCombination saveKC = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
        scene.getAccelerators().put(saveKC, new Runnable() {
            @Override
            public void run() {
                save(world);
            }
        });


        primaryStage.setTitle("元胞自动机");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    class AutoRunService extends Service<Number> {
        @Override
        protected Task<Number> createTask() {
            Task<Number> task = new Task<Number>() {
                @Override
                protected Number call() throws Exception {
                    int i = 0;
                    while (true) {
                        if (this.isCancelled())
                            break;
                        this.updateValue(i++);
                        if (ratenum <= 0)
                            ratenum = 1;
                        Thread.sleep((int) (1000 / ratenum));
                    }
                    return null;
                }
            };
            return task;
        }
    }

    public void open(World world) {
        Stage stage = new Stage();
        FileChooser fc = new FileChooser();
        fc.setTitle("打开");
        fc.getExtensionFilters().add(new ExtensionFilter("example", "*.txt"));
        File file = fc.showOpenDialog(stage);

        if (file == null)
            return;

        try {
            int max;
            BufferedReader br;
            br = new BufferedReader(new FileReader(file));

            max = Integer.parseInt(br.readLine());

            if (max <= 0 || max > 300) {
                br.close();
                return;
            }

            world.setScale(max);

            ArrayList<ArrayList<Integer>> a = new ArrayList<ArrayList<Integer>>();
            int width = 0, height = 0;
            String l;

            l = br.readLine();
            width = l.length();
            if (width == 0) {
                br.close();
                return;
            }
            a.add(new ArrayList<Integer>());
            for (int i = 0; i < l.length(); i++)
                a.get(height).add(Integer.parseInt(l.substring(i, i + 1)));
            height++;

            while ((l = br.readLine()) != null) {
                if (l.length() != width) {
                    br.close();
                    return;
                }
                a.add(new ArrayList<Integer>());
                for (int i = 0; i < l.length(); i++)
                    a.get(height).add(Integer.parseInt(l.substring(i, i + 1)));
                height++;
            }

            br.close();
            world.example(a);

        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public void save(World world) {
        Stage stage = new Stage();
        FileChooser fc = new FileChooser();
        fc.setTitle("保存");
        fc.getExtensionFilters().add(new ExtensionFilter("example", "*.txt"));
        fc.setInitialFileName("example");
        File file = fc.showSaveDialog(stage);

        try {
            if (file != null)
                world.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

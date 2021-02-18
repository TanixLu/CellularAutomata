package PB19071505;

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
import javafx.scene.control.TextArea;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser.ExtensionFilter;

/***
*TO DO:
*paint              done
*some my rules      done
*fix "/1000"        done
*no grids           done
*auto rate improve  done
*back and cycle     
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
        Button open = new Button("open");
        Button save = new Button("save");
        //help
        Button help = new Button("help");

        //display some examples
        Label examplesLabel = new Label("examples of patterns (in Conway's rule)");
        ChoiceBox<Object> examplesChoiceBox = new ChoiceBox<Object>();
        examplesChoiceBox.getItems().addAll(
            "none",
            new Separator(), 
            "block", "beehive", "loaf", "boat", "tub",
            new Separator(), 
            "blinker (period 2)", "toad (period 2)", "beacon (period 2)", "pulsar (period 3)","penta decathlon (period 15)",
            new Separator(),
            "glider", "light-weight spaceship (LWSS)","middle-weight-spaceship (MWSS)", "heavy-weight spaceship (HWSS)"
        );
        examplesChoiceBox.setValue("none");


        //set edge lenth of the square world 
        Label scale = new Label("scale");
        ChoiceBox<Integer> scaleChoiceBox = new ChoiceBox<Integer>();
        scaleChoiceBox.getItems().addAll(10, 20, 50, 100, 150, 200, 300);
        scaleChoiceBox.setValue(100);


        //set rule of the world
        Label rules = new Label("rules");
        ChoiceBox<String> rulesChoiceBox = new ChoiceBox<String>();
        rulesChoiceBox.getItems().addAll(
            "none", 
            "Conway's game of life",
            "core (paint and run)",
            "square and wave (p&r)",
            "paper cutting (p&r)",
            "pyramid (p&r)",
            "picture frame (p&r)",
            "grid (p&r)",
            "amazed 4 (p&r)",
            "rain (run and wait)"
        );
        rulesChoiceBox.setValue("Conway's game of life");


        //set neighborhood radius of cell
        Label radius = new Label("radius");
        ChoiceBox<Integer> radiusChoiceBox = new ChoiceBox<Integer>();
        radiusChoiceBox.getItems().addAll(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        radiusChoiceBox.setValue(1);
        Label neighbor = new Label("8 neighbors");


        //set how many cells around a dead cell can make it alive
        Label reproduce = new Label("reproduce");

        Label reproduceTop = new Label("top");
        TextField reproduceTopTextField = new TextField();
        reproduceTopTextField.setText("3");
        reproduceTopTextField.setPrefWidth(100);

        Label reproduceBottom = new Label("bottom");
        TextField reproduceBottomTextField = new TextField();
        reproduceBottomTextField.setText("3");
        reproduceBottomTextField.setPrefWidth(100);


        //set how many cells around a living cell can keep it alive
        Label aliveConditions = new Label("alive conditions");

        Label aliveTop = new Label("top");
        TextField aliveTopTextField = new TextField();
        aliveTopTextField.setText("3");
        aliveTopTextField.setPrefWidth(100);

        Label aliveBottom = new Label("bottom");
        TextField aliveBottomTextField = new TextField();
        aliveBottomTextField.setText("2");
        aliveBottomTextField.setPrefWidth(100);


        //some properties
        Label cellsnum = new Label("0");
        Label cells = new Label("cells");
        
        Label proportion = new Label("0%");
        
        Label time = new Label("time");
        Label t = new Label("0");

        Button periodButton = new Button("period");
        Label period = new Label("infinity");
        

        //set how much probability a dead cell will be alive after every step
        Label randomOccur = new Label("random occur");

        Label occurProbability = new Label("probability(‰)");
        TextField occurProbabilityTextField = new TextField();
        occurProbabilityTextField.setText("0");
        occurProbabilityTextField.setPrefWidth(100);


        //set how much probability a living cell will die after every step
        Label randomDisappear = new Label("random disappear");

        Label disappearProbability = new Label("probability(‰)");
        TextField disappearProbabilityTextField = new TextField();
        disappearProbabilityTextField.setText("0");
        disappearProbabilityTextField.setPrefWidth(100);



        //run automatically
        CheckBox auto = new CheckBox("auto");
        //run for once
        Button onestep = new Button("one step");
        //back for once
        Button back = new Button("back");
        Label backtime = new Label("0 left");
        //kill all cells
        Button clean = new Button("clean");

        VBox vb = new VBox(10);
        vb.getChildren().addAll(auto, onestep, back, clean);

        //make all cells alive
        Button paint = new Button("paint");

        //how fast it runs automatically
        TextField rate = new TextField("20");
        rate.setPromptText("rate");
        rate.setPrefWidth(100);

        Label persecond = new Label("/s");

        //grid on/off
        CheckBox grid = new CheckBox("grid");
        grid.setSelected(true);

        //whether I can draw cells
        CheckBox draw = new CheckBox("draw");
        draw.setSelected(true);


        //menu that controls the world
        AnchorPane menu = new AnchorPane();
        menu.setStyle("-fx-background-color:#F5F5F5");
        menu.getChildren().addAll(
            open, save, help,
            examplesLabel, examplesChoiceBox, 
            scale, scaleChoiceBox, 
            rules,rulesChoiceBox,
            radius, radiusChoiceBox, neighbor,
            reproduce, reproduceTop, reproduceTopTextField,reproduceBottom, reproduceBottomTextField,
            aliveConditions, aliveTop, aliveTopTextField, aliveBottom,aliveBottomTextField,
            cellsnum, cells, proportion, t, time, periodButton, period,
            randomOccur, occurProbability, occurProbabilityTextField,
            randomDisappear,disappearProbability, disappearProbabilityTextField,
            vb, backtime, paint, rate, persecond, grid, draw
        );


        /*****menu layout*****/
        AnchorPane.setLeftAnchor(open, 10.0);
        AnchorPane.setTopAnchor(open, 20.0);
        AnchorPane.setLeftAnchor(save, 80.0);
        AnchorPane.setTopAnchor(save, 20.0);
        AnchorPane.setLeftAnchor(help, 150.0);
        AnchorPane.setTopAnchor(help, 20.0);


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
        AnchorPane.setLeftAnchor(neighbor, 150.0);
        AnchorPane.setTopAnchor(neighbor, 230.0);


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


        AnchorPane.setRightAnchor(cellsnum, 45.0);
        AnchorPane.setTopAnchor(cellsnum, 300.0);
        AnchorPane.setRightAnchor(cells, 10.0);
        AnchorPane.setTopAnchor(cells, 300.0);
        AnchorPane.setRightAnchor(proportion, 10.0);
        AnchorPane.setTopAnchor(proportion, 320.0);
        AnchorPane.setRightAnchor(time, 10.0);
        AnchorPane.setTopAnchor(time, 350.0);
        AnchorPane.setRightAnchor(t, 10.0);
        AnchorPane.setTopAnchor(t, 370.0);
        AnchorPane.setRightAnchor(periodButton, 10.0);
        AnchorPane.setTopAnchor(periodButton, 400.0);
        AnchorPane.setRightAnchor(period, 10.0);
        AnchorPane.setTopAnchor(period, 440.0);


        AnchorPane.setLeftAnchor(randomOccur, 15.0);
        AnchorPane.setTopAnchor(randomOccur, 500.0);

        AnchorPane.setLeftAnchor(occurProbability, 45.0);
        AnchorPane.setTopAnchor(occurProbability, 530.0);
        AnchorPane.setLeftAnchor(occurProbabilityTextField, 160.0);
        AnchorPane.setTopAnchor(occurProbabilityTextField, 530.0);


        AnchorPane.setLeftAnchor(randomDisappear, 15.0);
        AnchorPane.setTopAnchor(randomDisappear, 560.0);

        AnchorPane.setLeftAnchor(disappearProbability, 45.0);
        AnchorPane.setTopAnchor(disappearProbability, 590.0);
        AnchorPane.setLeftAnchor(disappearProbabilityTextField, 160.0);
        AnchorPane.setTopAnchor(disappearProbabilityTextField, 590.0);


        AnchorPane.setLeftAnchor(vb, 30.0);
        AnchorPane.setTopAnchor(vb, 640.0);

        AnchorPane.setLeftAnchor(backtime, 110.0);
        AnchorPane.setTopAnchor(backtime, 715.0);

        AnchorPane.setLeftAnchor(paint, 100.0);
        AnchorPane.setTopAnchor(paint, 751.0);

        AnchorPane.setRightAnchor(rate, 60.0);
        AnchorPane.setTopAnchor(rate, 637.0);
        AnchorPane.setRightAnchor(persecond, 40.0);
        AnchorPane.setTopAnchor(persecond, 641.0);

        AnchorPane.setRightAnchor(grid, 37.0);
        AnchorPane.setTopAnchor(grid, 715.0);

        AnchorPane.setRightAnchor(draw, 30.0);
        AnchorPane.setTopAnchor(draw, 748.0);
        /*****menu layout done*****/


        //World externs Pane
        World world = new World(100);
        world.setStyle("-fx-background-color:#000000");

        cellsnum.textProperty().bind(world.cellsnumProperty().asString());
        proportion.textProperty().bind(world.proportionProperty().multiply(100).asString("%.2f").concat("%"));
        t.textProperty().bind(world.timeProperty().asString());

        BorderPane root = new BorderPane();
        root.setLeft(menu);
        root.setCenter(world);

        Scene scene = new Scene(root, 1100, 800);
        menu.prefWidthProperty().bind(scene.widthProperty().multiply(3).divide(11));


        /*****action of items*****/
        onestep.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                world.oneStep();
            }
        });

        
        backtime.textProperty().bind(world.backtimeProperty().asString().concat(" left"));
        
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
        
        back.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                auto.setSelected(false);
                auto_service.cancel();
                if(event.getButton().equals(MouseButton.PRIMARY))world.back();
                else if(event.getButton().equals(MouseButton.SECONDARY)){
                    for(int i = 0; i < 10; i++)world.back();
                }
                else if(event.getButton().equals(MouseButton.MIDDLE)){
                    for(int i = 0; i < 100; i++)world.back();
                }
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


        help.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                Stage stage = new Stage(StageStyle.UTILITY);
                stage.setTitle("help");
                stage.setWidth(370);
                stage.setHeight(520);
                stage.initOwner(primaryStage);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(new TextArea(
                    "black cell is alive\n\n"+
                    "white cell is dead\n\n"+
                    "radius controls how many neighbors it have\n\n"+
                    "if a dead cell have [bottom,top] neighbors\n\n"+
                    "    it will be reproduced\n\n"+
                    "if a living cell has [bottom,top] neighbors\n\n"+
                    "    it will continue to be alive\n\n"+
                    "else\n\n"+
                    "    it will die\n\n"+
                    "\n\n"+
                    "left to draw right to kill\n\n"+
                    "drag supported"
                )));
                stage.show();
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
                grid.setSelected(true);
            }
        });

        rulesChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int[] a;
                double[] b;
                switch (newValue) {
                    case "Conway's game of life":
                        a = new int[]{1, 3, 3, 2, 3};
                        b = new double[]{0.0, 0.0};
                        break;
                    
                    case "core (paint and run)":
                        a = new int[]{1, 3, 3, 5, 8};
                        b = new double[]{0.0, 0.0};
                        break;

                    case "square and wave (p&r)":
                        a = new int[]{1, 3, 3, 6, 8};
                        b = new double[]{0.0, 0.0};
                        break;

                    case "paper cutting (p&r)":
                        a = new int[]{1, 3, 3, 3, 7};
                        b = new double[]{0.0, 0.0};
                        break;

                    case "pyramid (p&r)":
                        a = new int[]{1, 2, 3, 0, 5};
                        b = new double[]{0.0, 0.0};
                        break;

                    case "picture frame (p&r)":
                        a = new int[]{1, 4, 5, 0, 5};
                        b = new double[]{0.0, 0.0};
                        break;

                    case "grid (p&r)":
                        a = new int[]{1, 4, 5, 0, 7};
                        b = new double[]{0.0, 0.0};
                        break;

                    case "amazed 4 (p&r)":
                        a = new int[]{2, 2, 3, 0, 11};
                        b = new double[]{0.0, 0.0};
                        break;

                    case "rain (run and wait)":
                        a = new int[]{1, 3, 3, 2, 3};
                        b = new double[]{10.0, 10.0};
                        break;
                    
                    default:
                        return;
                }
                radiusChoiceBox.setValue(a[0]);
                reproduceBottomTextField.setText(String.valueOf(a[1]));
                reproduceTopTextField.setText(String.valueOf(a[2]));
                aliveBottomTextField.setText(String.valueOf(a[3]));
                aliveTopTextField.setText(String.valueOf(a[4]));
                occurProbabilityTextField.setText(String.valueOf(b[0]));
                disappearProbabilityTextField.setText(String.valueOf(b[1]));
            }
        });

        radiusChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                world.rule.setRadius(newValue);
                neighbor.setText(String.valueOf(4*newValue*newValue+4*newValue) + " neighbors");
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

        periodButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                period.setText(world.getPeriodText());
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

        paint.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
                world.paint();
			}
        });

        draw.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                drawflag = newValue;
            }
        });

        grid.selectedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                world.setGrid(newValue);
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


        primaryStage.setTitle("CellularAutomata");
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
                        long t0 = System.currentTimeMillis();
                        if (this.isCancelled())
                            break;
                        this.updateValue(i++);
                        if (ratenum <= 0)
                            ratenum = 1;
                        while(System.currentTimeMillis() - t0 < (long)(1000 / ratenum));
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
        fc.setTitle("open");
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
        fc.setTitle("save");
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

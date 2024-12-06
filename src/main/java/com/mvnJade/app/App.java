package com.mvnJade.app;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mvnJade.app.world.World;

import jade.core.ProfileImpl;
import jade.core.Runtime;

/**
 * Hello world!
 *
 */
public class App extends Application {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private boolean stop = false;
    ContainerController cc = Runtime.instance().createMainContainer(new ProfileImpl());
    Object[] agentArgs = {};

    public void ok() {
        System.out.println("Hello World!");
        System.out.println("Conceiner created");
        World newWorld = World.getInstance(30);

        try {
            launchAnt(1);
            newWorld.play();
            executor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (stop==true) {
                            throw new Exception("Stop");
                        }
                        launchAnt(2);
                    } catch (Exception e) {
                        executor.shutdown();
                    }
                }
            }, 1000, 100, TimeUnit.MILLISECONDS);

            // TODO: Start gui and add multiple ants
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    void launchAnt(int number) throws Exception {
        AgentController ag = cc.createNewAgent("testAnt" + number, "com.mvnJade.app.agents.Ant", agentArgs);
        ag.start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");

        VBox root = new VBox();

        // primaryStage.setScene(new Scene(root, 500, 500));
        // primaryStage.show();
        TilePane tilePane = new TilePane();
        tilePane.setPrefColumns(3);
        tilePane.setPrefRows(3);
        tilePane.setTileAlignment(Pos.CENTER);

        tilePane.getChildren().addAll(
                new Rectangle(50, 50, Color.RED),
                new Rectangle(50, 50, Color.GREEN),
                new Rectangle(50, 50, Color.BLUE),
                new Rectangle(50, 50, Color.YELLOW),
                new Rectangle(50, 50, Color.CYAN),
                new Rectangle(50, 50, Color.PURPLE),
                new Rectangle(50, 50, Color.BROWN),
                new Rectangle(50, 50, Color.PINK),
                new Rectangle(50, 50, Color.ORANGE));

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
                // tilePane.getChildren().
                tilePane.getChildren().clear();
                ok();
            }
        });

        root.getChildren().add(tilePane);
        root.getChildren().add(btn);
        Scene scene = new Scene(root);
        scene.setFill(Color.LIGHTGRAY);
        primaryStage.setTitle("3x3");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}

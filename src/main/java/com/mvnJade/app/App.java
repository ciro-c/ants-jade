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
 * Ant Colony Optimization Simulation
 */
public class App extends Application {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    ContainerController cc = Runtime.instance().createMainContainer(new ProfileImpl());
    Object[] agentArgs = {};
    int antNumberToLaunch = 1000;
    int antsLaunched = 0;
    int worldSize = 30;
    boolean isRunning = false;

    public void startSimulation() {
        System.out.println("Starting ACO Simulation");
        System.out.println("Container created");
        World newWorld = World.getInstance(worldSize);

        try {
            newWorld.play();
            isRunning = true;
            executor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (antsLaunched >= antNumberToLaunch) {
                            throw new Exception("All ants launched");
                        }
                        launchAnt(antsLaunched++);
                    } catch (Exception e) {
                        executor.shutdown();
                        System.out.println(e.getMessage());
                    }
                }
            }, 200, 50, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stopSimulation() {
        if (isRunning) {
            executor.shutdown();
            World.getInstance().stop();
            isRunning = false;
            System.out.println("Simulation stopped");
        }
    }

    void launchAnt(int number) throws Exception {
        AgentController ag = cc.createNewAgent("ant" + number, "com.mvnJade.app.agents.Ant", agentArgs);
        ag.start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button startBtn = new Button();
        startBtn.setText("Start Simulation");
        
        Button stopBtn = new Button();
        stopBtn.setText("Stop Simulation");
        stopBtn.setDisable(true);

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        TilePane controlPane = new TilePane();
        controlPane.setPrefColumns(2);
        controlPane.setAlignment(Pos.CENTER);
        controlPane.setHgap(10);
        
        controlPane.getChildren().addAll(startBtn, stopBtn);

        startBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startSimulation();
                startBtn.setDisable(true);
                stopBtn.setDisable(false);
            }
        });
        
        stopBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stopSimulation();
                startBtn.setDisable(false);
                stopBtn.setDisable(true);
            }
        });

        root.getChildren().add(controlPane);
        
        Scene scene = new Scene(root, 300, 200);
        scene.setFill(Color.LIGHTGRAY);
        primaryStage.setTitle("Ant Colony Optimization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

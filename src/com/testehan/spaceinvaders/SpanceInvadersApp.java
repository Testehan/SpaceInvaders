package com.testehan.spaceinvaders;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class SpanceInvadersApp extends Application {

    private Pane root = new Pane();
    private double time = 0;
    private Sprite player = new Sprite(300,700,40,40,"player",Color.DARKBLUE);

    private Parent createContent(){
        root.setPrefSize(600,800);

        root.getChildren().add(player);
//        root.getChildren().add(new Circle(300,300,20, Paint.valueOf("red")));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };

        timer.start();

        nextLevel();

        return root;
    }

    private void nextLevel() {
        for (int i = 0; i<5;i++){
            Sprite enemy = new Sprite(90+i*100,150,30,30,"enemy",Color.RED);
            root.getChildren().add(enemy);
        }
    }

    private List<Sprite> getSprites(){
        return root.getChildren().stream()
                .map(n -> (Sprite)n)
                .collect(Collectors.toList());
    }

    private void update() {
        time+=0.016;

        getSprites().forEach( s -> {
            switch (s.getType()){
                case "enemybullet":
                    s.moveDown();
                    if (s.getBoundsInParent().intersects(player.getBoundsInParent())){
                        player.setDead(true);
                        s.setDead(true);
                    }
                    break;
                case "playerbullet":
                    s.moveUp();
                    getSprites().stream().filter(e -> e.getType().equals("enemy")).forEach(enemy -> {
                        if (s.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            enemy.setDead(true);
                            s.setDead(true);
                        }
                    });
                    break;
                case "enemy":
                    if (time>2){
                        if (Math.random()<0.3){
                            shoot(s);
                        }
                    }
                    break;

            }
        });

        root.getChildren().removeIf(n -> {
            Sprite s = (Sprite) n;
            return s.isDead();
        });

        if (time>2){
            time=0;
        }

    }


    private void shoot(Sprite whoShoot) { // character that shoot the bullet
        if (!whoShoot.isDead()) {
            Sprite bullet = new Sprite((int) whoShoot.getTranslateX() + 20, (int) whoShoot.getTranslateY(), 5, 20, whoShoot.getType() + "bullet", Color.BLACK);
            root.getChildren().add(bullet);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()){
                case A:
                    player.moveLeft();
                    break;
                case D:
                    player.moveRight();
                    break;
                case SPACE:
                    shoot(player);
                    break;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }



}

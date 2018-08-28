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

    public static final String ENEMY = "enemy";
    public static final String PLAYER = "player";
    public static final String ENEMY_BULLET = "enemybullet";
    public static final String PLAYER_BULLET = "playerbullet";
    public static final String BULLET = "bullet";

    private Pane root = new Pane();
    private double time = 0;
    private Sprite player = new Sprite(300,700,40,40, PLAYER,Color.DARKBLUE);

    private Parent createContent(){
        root.setPrefSize(600,800);

        root.getChildren().add(player);

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
            final int x = 90 + i * 100;
            Sprite enemy = new Sprite(x,150,30,30, ENEMY,Color.RED);
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
                case ENEMY_BULLET:
                    s.moveDown();
                    if (s.getBoundsInParent().intersects(player.getBoundsInParent())){
                        player.setDead(true);
                        s.setDead(true);
                    }
                    break;
                case PLAYER_BULLET:
                    s.moveUp();
                    getSprites().stream().filter(e -> e.getType().equals(ENEMY)).forEach(enemy -> {
                        if (s.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            enemy.setDead(true);
                            s.setDead(true);
                        }
                    });
                    break;
                case ENEMY:
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
            Sprite bullet = new Sprite((int) whoShoot.getTranslateX() + 20, (int) whoShoot.getTranslateY(), 5, 20, whoShoot.getType() + BULLET, Color.BLACK);
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

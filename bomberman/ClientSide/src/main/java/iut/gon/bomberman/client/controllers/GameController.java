package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.ai.AISTRATEGIES;
import iut.gon.bomberman.client.ai.Ai;
import iut.gon.bomberman.client.ai.HeatMap;
import iut.gon.bomberman.client.sound.SoundManager;
import iut.gon.bomberman.client.ai.AISTRATEGIES;
import iut.gon.bomberman.client.ai.Ai;
import iut.gon.bomberman.client.ai.HeatMap;
import iut.gon.bomberman.client.view.LabRenderer;
import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.DFSGenerator;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Direction;
import iut.gon.bomberman.common.model.player.Joueur;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class GameController {

    @FXML
    private Canvas gameCanvas;

    @FXML
    private UiController uiController;

    private GraphicsContext gc;
    private final LabRenderer renderer = new LabRenderer();
    private Labyrinthe labyrinthe;

    private Joueur joueur;
    private BombManager bombManager;
    private AnimationTimer gameLoop;
    private boolean isGameOver = false;
    private boolean isVictory = false;
    private boolean deathAnimationComplete = false;
    private long deathAnimationStartTime = -1;
    private static final long DEATH_ANIMATION_DURATION = 1000;

    private boolean victorySoundPlayed = false;
    private boolean defeatSoundPlayed = false;
    private boolean victoryAnimationComplete = false;
    private long victoryAnimationStartTime = -1;
    private static final long VICTORY_ANIMATION_DURATION = 1500;

    private Image heartImage;
    private Image bombImage;

    private final Set<KeyCode> input = new HashSet<>();
    private long lastNanoTime = -1;
    private boolean spaceWasPressed = false;
    private boolean escWasPressed = false;
    private boolean isPaused = false;

    private double debugTimer = 0;
    private ArrayList<Ai> listBots = new ArrayList<Ai>();



    @FXML
    public void initialize() {
        // Charge l'image du cœur
        try {
            String resourcePath = Objects.requireNonNull(
                getClass().getResource("/iut/gon/bomberman/client/assets/heart.png")
            ).toExternalForm();
            heartImage = new Image(resourcePath, 30, 30, true, true);
        } catch (NullPointerException e) {
            System.err.println("Impossible de charger l'image du cœur : " + e.getMessage());
        }
        
        // Charge l'image de la bombe
        try {
            String bombPath = iut.gon.bomberman.client.GameSettings.getSelectedBombPath();
            String resourcePath = Objects.requireNonNull(
                    getClass().getResource(bombPath)
            ).toExternalForm();
            bombImage = new Image(resourcePath, 30, 30, true, true);
        } catch (NullPointerException e) {
            System.err.println("Impossible de charger l'image de la bombe personnalisée");
        }

        // ...existing code...
        DFSGenerator generator = new DFSGenerator();
        this.labyrinthe = generator.createLabyrinthe(21, 21);

        this.gc = gameCanvas.getGraphicsContext2D();
        this.bombManager = new BombManager();

        this.joueur = new Joueur(1, "Gorke");

        String savedSkin = iut.gon.bomberman.client.GameSettings.getSelectedSkinPath();
        this.joueur.setSkinPath(savedSkin);

        renderer.updateAssets();

        this.joueur.setX(1);
        this.joueur.setY(1);

        gameCanvas.setWidth(labyrinthe.getWidth() * 32);
        gameCanvas.setHeight(labyrinthe.getHeight() * 32);
        gameCanvas.setFocusTraversable(true);

        gameCanvas.setOnKeyPressed(e -> input.add(e.getCode()));
        gameCanvas.setOnKeyReleased(e -> {
            input.remove(e.getCode());
            if (e.getCode() == KeyCode.SPACE) spaceWasPressed = false;
            if (e.getCode() == KeyCode.ESCAPE) escWasPressed = false;
        });

        this.gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNanoTime < 0) {
                    lastNanoTime = now;
                    return;
                }
                double deltaTime = (now - lastNanoTime) / 1_000_000_000.0;
                lastNanoTime = now;

                update(deltaTime);
                try {
                    render();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        gameLoop.start();
    }

    public void setConfig(int tailleMap, List<AISTRATEGIES> strategies) {

        DFSGenerator generator = new DFSGenerator();
        this.labyrinthe = generator.createLabyrinthe(tailleMap, tailleMap);

        gameCanvas.setWidth(labyrinthe.getWidth() * 32);
        gameCanvas.setHeight(labyrinthe.getHeight() * 32);

        HeatMap heatMap = new HeatMap(tailleMap, tailleMap);

        for (int i = 0; i < strategies.size(); i++) {
            int placementX = tailleMap - 2;
            int placementY = tailleMap - 2;

            switch (i){
                case 1:
                    placementX = 1;
                    break;
                case 2:
                    placementY = 1;
                    break;
                case 3:
                    placementY = placementX = (int) ((double) tailleMap / 2 + 0.5);
                    break;
            }
            listBots.add(new Ai(new Joueur(i + 1, "Bot " + i, placementX, placementY), labyrinthe, strategies.get(i), this, heatMap, bombManager));
        }
    }

    private void handleInputs(double deltaTime) {
        double dx = 0;
        double dy = 0;


        // Détection des directions
        if (input.contains(KeyCode.Z) || input.contains(KeyCode.UP)) {
            dy--;
            joueur.setDirection(Direction.UP);
        } else if (input.contains(KeyCode.S) || input.contains(KeyCode.DOWN)) {
            dy++;
            joueur.setDirection(Direction.DOWN);
        } else if (input.contains(KeyCode.Q) || input.contains(KeyCode.LEFT)) {
            dx--;
            joueur.setDirection(Direction.LEFT);
        } else if (input.contains(KeyCode.D) || input.contains(KeyCode.RIGHT)) {
            dx++;
            joueur.setDirection(Direction.RIGHT);
        } else {
            // Aucun mouvement
            joueur.setDirection(Direction.IDLE);
        }
        if (dx != 0 || dy != 0) {
            // deltaTime pour que la vitesse soit constante
            joueur.move(dx, dy, deltaTime, labyrinthe, bombManager);
        } else {
            joueur.setDirection(Direction.IDLE);
        }
        // Pose de bombe (Verrouillage par spaceWasPressed pour éviter le spam)
        if (input.contains(KeyCode.SPACE) && !spaceWasPressed) {
            spaceWasPressed = true;
            bombManager.placeBomb(joueur, joueur.getExplosionRange(), labyrinthe);
        }
    }

    private boolean checkVictoryCondition() {
       if(!joueur.isAlive())return false;

       for (Ai bot : listBots){
           if (bot.getPlayer().isAlive()) return false;
       }

       return true;
    }

    private void update(double deltaTime) {
        if (input.contains(KeyCode.ESCAPE) && !escWasPressed) {
            escWasPressed = true;
            if (isVictory || (isGameOver && deathAnimationComplete)) {
                goBackToMenu();
                return;
            } else if (!isGameOver && !isVictory) {
                isPaused = !isPaused;
            }
        }

        if (isPaused) {
            return;
        }

        if (isVictory || isGameOver) {
            if (isGameOver && deathAnimationStartTime > 0) {
                long elapsed = System.currentTimeMillis() - deathAnimationStartTime;
                if (elapsed >= DEATH_ANIMATION_DURATION) deathAnimationComplete = true;
            }

            if (isVictory && !victorySoundPlayed) {
                SoundManager.getInstance().playVictory();
                victorySoundPlayed = true;
            }
            if (isGameOver && !defeatSoundPlayed) {
                SoundManager.getInstance().playDefeat();
                defeatSoundPlayed = true;
            }
            if (isVictory && victoryAnimationStartTime > 0) {
                long elapsed = System.currentTimeMillis() - victoryAnimationStartTime;
                if (elapsed >= VICTORY_ANIMATION_DURATION) victoryAnimationComplete = true;
            }
            return;
        }

        //prepare liste pour bombManager
            List<Joueur> players = new ArrayList<>();

            players.add(joueur);
            listBots.forEach(bot -> players.add(bot.getPlayer()));


        boolean anExplosionHappened = bombManager.update(deltaTime, labyrinthe, players);

        if (anExplosionHappened) {
            SoundManager.getInstance().playExplosion();
        }

        if (joueur.isAlive() && !isVictory) {
            handleInputs(deltaTime);
            if (joueur.checkBonus(labyrinthe)) {
                SoundManager.getInstance().playBonus();
            }
        }

        //tue l'ia si ses pv sont à 0 ou moins, et update son comportement
        for(Ai bot : listBots){
            bot.update(deltaTime, new Joueur[]{bot.getPlayer(), joueur});
            if (bot.getPlayer().getPv() <= 0) {
                bot.getPlayer().setAlive(false);
            }
        }

        if (!isVictory && checkVictoryCondition()) {
            isVictory = true;
            victoryAnimationStartTime = System.currentTimeMillis();
        }

        List<Joueur> targets = new ArrayList<>();
        if (joueur.isAlive()) targets.add(joueur);

        for(Ai bot : listBots){
            if (bot.getPlayer().isAlive()) targets.add(bot.getPlayer());
        }

        if (joueur.getPv() <= 0 && joueur.isAlive()) {
            joueur.setAlive(false);
        }

        if (!joueur.isAlive() && !isGameOver) {
            this.isGameOver = true;
            deathAnimationStartTime = System.currentTimeMillis();
        }


        if (uiController != null) {
            uiController.updatePlayerStats(joueur);
        }

        debugTimer += deltaTime;
        if (debugTimer >= 1.0) { // On affiche toutes les 1 seconde
            System.out.println("\nDEBUG VITESSE");
            // Calcul de la vitesse théorique (Base * Multiplier)
            System.out.println(String.format("[%s] Speed: %.2f | Range: %d",
                    joueur.getNom(), joueur.getSpeed_multiplier(), joueur.getExplosionRange()));

            for(Ai bot : listBots){
                System.out.println(String.format("[%s] Speed: %.2f | Range: %d",
                        bot.getPlayer().getNom(), bot.getPlayer().getSpeed_multiplier(), bot.getPlayer().getExplosionRange()));
            }

            debugTimer = 0;
        }
    }

    private void render() throws InterruptedException {

        //check si on doit mettre le jeux en pauseet afficher le menu correspondant
        if (joueur.isAlive() && isPaused) {
            drawPauseMenu();
            return;
        }


        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        renderer.draw(gc, labyrinthe);
        renderer.drawBombs(gc, bombManager.getBombs());
        renderer.drawExplosions(gc, bombManager.getExplosionCells());

        renderer.drawPlayer(gc, joueur);

        for (Ai bot : listBots){
            renderer.drawPlayer(gc, bot.getPlayer());
        }

        drawStatsBar(gc, joueur.getNb_bombes(), joueur.getPv(), joueur.getExplosionRange(), joueur.getSpeed_multiplier());
        
        // Afficher l'écran VICTORY
        if (isVictory) {
            if (victoryAnimationComplete) {
                drawVictoryOverScreen();
            }
        }
        // Afficher l'écran GAME OVER uniquement après que l'animation de mort soit complète
        else if (isGameOver) {
            if (deathAnimationComplete){
                drawGameOverScreen();
            }
        }
    }

    private void drawGameOverScreen() {
        gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.setFill(javafx.scene.paint.Color.RED);
        gc.setFont(javafx.scene.text.Font.font("Arial", 50));
        gc.fillText("GAME OVER", gameCanvas.getWidth()/2 - 140, gameCanvas.getHeight()/2);
        
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));
        gc.fillText("Appuyez sur ESC pour retourner au menu", gameCanvas.getWidth()/2 - 200, gameCanvas.getHeight()/2 + 60);
    }

    /**
     *  Affiche l'écran de pause
     * */
    private void drawPauseMenu(){
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0,0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 50));
        gc.fillText("PAUSE", gameCanvas.getWidth()/2 - 60, gameCanvas.getHeight()/2);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 24));
        gc.fillText("Appuyez sur ESC pour reprendre", gameCanvas.getWidth()/2 - 150, gameCanvas.getHeight()/2 + 60);
    }

    private void drawVictoryOverScreen(){
        gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.setFill(javafx.scene.paint.Color.GREEN);
        gc.setFont(javafx.scene.text.Font.font("Arial", 50));
        gc.fillText("VICTORY \uD83C\uDFC6", gameCanvas.getWidth()/2 - 140, gameCanvas.getHeight()/2);

        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));
        gc.fillText("Appuyez sur ESC pour retourner au menu", gameCanvas.getWidth()/2 - 200, gameCanvas.getHeight()/2 + 60);

    }

    /**
     * Affiche une barre bleu foncé arrondie en bas avec les bombes et les cœurs centrés
     *
     * @param gc GraphicsContext pour dessiner
     * @param bombs Nombre de bombes disponibles
     * @param hearts Nombre de cœurs disponibles
     */
    private void drawStatsBar(GraphicsContext gc, int bombs, int hearts, int range, float speed) {
        double barHeight = 35;
        double barY = gameCanvas.getHeight() - barHeight - 5;
        double barWidth = 320;
        double barX = (gameCanvas.getWidth() - barWidth) / 2.0;

        gc.setFill(javafx.scene.paint.Color.rgb(25, 50, 100, 0.9));
        gc.fillRoundRect(barX, barY, barWidth, barHeight, 15, 15);

        gc.setStroke(javafx.scene.paint.Color.rgb(50, 100, 200));
        gc.setLineWidth(2);
        gc.strokeRoundRect(barX, barY, barWidth, barHeight, 15, 15);

        double elementY = barY + (barHeight - 25) / 2;

        drawStatItem(gc, bombImage, bombs, barX + 15, elementY, "x");
        drawStatItem(gc, heartImage, hearts, barX + 85, elementY, "x");

        gc.setFill(speed > 1.0f ? javafx.scene.paint.Color.GOLD : javafx.scene.paint.Color.LIGHTGRAY);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 13));
        String speedTxt = String.format("SPEED x%.1f", speed);
        gc.fillText(speedTxt, barX + 155, barY + 23);

        gc.setFill(javafx.scene.paint.Color.ORANGERED);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 13));
        gc.fillText("RANGE: " + range, barX + 240, barY + 23);
    }


    /**
     * Affiche un élément de statistique (image + compteur)
     * @param gc GraphicsContext
     * @param image Image à afficher
     * @param count Nombre à afficher
     * @param x Position X
     * @param y Position Y
     * @param label Texte
     */
    private void drawStatItem(GraphicsContext gc, Image image, int count, double x, double y, String label) {
        if (image == null) return;

        gc.drawImage(image, x, y, 25, 25);

        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 16));
        gc.fillText(label + " " + count, x + 30, y + 18);
    }

    /**
     * Affiche les cœurs et les bombes côte à côte au centre du canvas
     * @param gc GraphicsContext pour dessiner
     * @param hearts Nombre de cœurs à afficher
     */
    private void drawHearts(GraphicsContext gc, int hearts) {
        if (heartImage == null) return;

        // Calcule le centre du canvas
        double centerX = gameCanvas.getWidth() / 2.0;
        double centerY = 15;

        // Calcule la largeur totale des cœurs
        int heartsWidth = hearts * 35;

        // Position de départ des cœurs (alignés à droite du centre)
        double xStart = centerX - heartsWidth - 20; // -20 pour l'espace entre cœurs et bombes

        for (int i = 0; i < hearts; i++) {
            gc.drawImage(heartImage, xStart + (i * 35), centerY);
        }
    }

    /**
     * Affiche les bombes disponibles du joueur avec un style amélioré
     * @param gc GraphicsContext pour dessiner
     * @param bombs Nombre de bombes à afficher
     */
    private void drawBombs(GraphicsContext gc, int bombs) {
        if (bombImage == null) return;

        // Calcule le centre du canvas
        double centerX = gameCanvas.getWidth() / 2.0;
        double centerY = 15;

        // Position de départ des bombes (alignées à gauche du centre)
        double xStart = centerX + 20; // +20 pour l'espace entre cœurs et bombes

        for (int i = 0; i < bombs; i++) {
            gc.drawImage(bombImage, xStart + (i * 35), centerY);
        }
    }

    /**
     * Dessine un fond semi-transparent avec bordure pour les statistiques
     * @param gc GraphicsContext pour dessiner
     * @param hearts Nombre de cœurs
     * @param bombs Nombre de bombes
     */
    private void drawStatsBackground(GraphicsContext gc, int hearts, int bombs) {
        double centerX = gameCanvas.getWidth() / 2.0;

        // Calcule les dimensions du conteneur
        int heartsWidth = hearts * 35;
        int bombsWidth = bombs * 35;
        int totalWidth = heartsWidth + bombsWidth + 60; // +60 pour l'espace entre et les padding
        int height = 60;

        double bgX = centerX - (totalWidth / 2.0);
        double bgY = 5;

        // Fond semi-transparent
        gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.3));
        gc.fillRoundRect(bgX, bgY, totalWidth, height, 10, 10);

        // Bordure
        gc.setStroke(javafx.scene.paint.Color.rgb(255, 255, 255, 0.6));
        gc.setLineWidth(2);
        gc.strokeRoundRect(bgX, bgY, totalWidth, height, 10, 10);
    }

    /**
     * Retourne au menu principal, la partie est stoppée
     * */
    public void goBackToMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        try {
            this.escWasPressed = false;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/launcher.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Bomberman - Menu Principal");
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur lors du retour au menu : " + e.getMessage());
        }
    }
}


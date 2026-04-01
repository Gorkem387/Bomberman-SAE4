package iut.gon.bomberman.client.controllers;

import iut.gon.bomberman.client.network.NetworkManager;
import iut.gon.bomberman.client.view.LabRenderer;
import iut.gon.bomberman.common.model.Mess.MessageType;
import iut.gon.bomberman.common.model.Mess.MoveRequest;
import iut.gon.bomberman.common.model.Mess.PlaceBombRequest;
import iut.gon.bomberman.common.model.Mess.BombUpdate;
import iut.gon.bomberman.common.model.labyrinthe.Bomb;
import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.DFSGenerator;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Direction;
import iut.gon.bomberman.common.model.player.Joueur;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import iut.gon.bomberman.common.model.DTO.*;
import iut.gon.bomberman.common.model.Mess.*;
import java.util.*;

public class OnlineGameController {

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private final LabRenderer renderer = new LabRenderer();
    private Labyrinthe labyrinthe;
    private Joueur localPlayer;
    private final Map<Integer, Joueur> remotePlayers = new HashMap<>();
    private BombManager bombManager; // Utilisé ici comme conteneur pour le rendu
    private AnimationTimer gameLoop;
    private boolean isGameOver = false;
    private boolean isVictory = false;
    private boolean deathAnimationComplete = false;
    private long deathAnimationStartTime = -1;
    private static final long DEATH_ANIMATION_DURATION = 1000;
    private boolean escWasPressed = false;

    private final Set<KeyCode> input = new HashSet<>();
    private long lastNanoTime = -1;
    private boolean spaceWasPressed = false;

    private javafx.scene.image.Image heartImage;
    private javafx.scene.image.Image bombImage;

    @FXML
    public void initialize() {
        this.gc = gameCanvas.getGraphicsContext2D();
        this.bombManager = new BombManager();

        try {
            String resourcePath = Objects.requireNonNull(
                getClass().getResource("/iut/gon/bomberman/client/assets/heart.png")
            ).toExternalForm();
            heartImage = new javafx.scene.image.Image(resourcePath, 30, 30, true, true);
        } catch (NullPointerException e) {
            System.err.println("Impossible de charger l'image du cœur : " + e.getMessage());
        }

        try {
            String bombPath = iut.gon.bomberman.client.GameSettings.getSelectedBombPath();
            String resourcePath = Objects.requireNonNull(
                    getClass().getResource(bombPath)
            ).toExternalForm();
            bombImage = new javafx.scene.image.Image(resourcePath, 30, 30, true, true);
        } catch (NullPointerException e) {
            System.err.println("Impossible de charger l'image de la bombe personnalisée");
        }

        NetworkManager nm = NetworkManager.getInstance();
        this.localPlayer = new Joueur(nm.getLocalPlayerId(), nm.getLocalPlayerName());

        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(e -> input.add(e.getCode()));
        gameCanvas.setOnKeyReleased(e -> {
            input.remove(e.getCode());
            if (e.getCode() == KeyCode.SPACE) spaceWasPressed = false;
            if (e.getCode() == KeyCode.ESCAPE) escWasPressed = false;
        });

        // --- SYNCHRONISATION DES JOUEURS ---
        nm.addServerMessageListener(MessageType.GAME_UPDATE, msg -> {
            if (msg instanceof JoueurMisAJourDTO update) {
                Platform.runLater(() -> {
                    for (MinimDTO pos : update.positionsAll) {
                        if (pos.getId() == localPlayer.getId()) {
                            if (localPlayer.isAlive()) {
                                localPlayer.setX(pos.getX() / 100.0);
                                localPlayer.setY(pos.getY() / 100.0);
                                localPlayer.setPv(pos.getPv());
                                localPlayer.setNb_bombes(pos.getNb_bombes());
                                localPlayer.setExplosionRange(pos.getRange());
                                localPlayer.setSpeed_multiplier(pos.getSpeed());

                                if (localPlayer.getPv() <= 0) {
                                    localPlayer.setAlive(false);
                                }
                            }
                        } else {
                            Joueur remote = remotePlayers.computeIfAbsent(pos.getId(), id -> new Joueur(id, "Player_" + id));
                            if (remote.isAlive()) {
                                remote.setX(pos.getX() / 100.0);
                                remote.setY(pos.getY() / 100.0);
                                remote.setPv(pos.getPv());
                                remote.setNb_bombes(pos.getNb_bombes());
                                remote.setExplosionRange(pos.getRange());
                                remote.setSpeed_multiplier(pos.getSpeed());

                                if (remote.getPv() <= 0) {
                                    remote.setAlive(false);
                                }
                            }
                        }
                    }

                    // Vérifier condition de victoire
                    boolean anyRemoteAlive = false;
                    for (Joueur r : remotePlayers.values()) {
                        if (r.isAlive()) {
                            anyRemoteAlive = true;
                            break;
                        }
                    }
                    if (!anyRemoteAlive && !remotePlayers.isEmpty() && localPlayer.isAlive()) {
                        isVictory = true;
                    }

                });
            }
        });

        // --- SYNCHRONISATION DES BOMBES --- //
        nm.addServerMessageListener(MessageType.BOMB_UPDATE, msg -> {
            if (msg instanceof BombUpdate update) {
                Platform.runLater(() -> {
                    // Update bombs list
                    List<Bomb> newBombs = new ArrayList<>();
                    for (BombUpdate.BombDTO b : update.getActiveBombs()) {
                        // find owner
                        Joueur owner = (b.playerId == localPlayer.getId()) ? localPlayer : remotePlayers.get(b.playerId);
                        Bomb nmB = new Bomb(b.x, b.y, 2, owner);
                        nmB.setSolid(b.isSolid);
                        newBombs.add(nmB);
                    }
                    bombManager.setBombs(newBombs);
                    bombManager.setExplosionCells(update.getActiveExplosions());

                    // Mettre à jour le labyrinthe s'il est fourni (murs détruits)
                    if (update.getLabyrinthe() != null) {
                        this.labyrinthe = update.getLabyrinthe();
                    }
                });
            }
        });

        // Loop principal (rendu)
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
                render();
            }
        };
        gameLoop.start();
    }

    private void handleInputs() {
        double dx = 0;
        double dy = 0;

        if (input.contains(KeyCode.Z) || input.contains(KeyCode.UP)) dy--;
        if (input.contains(KeyCode.S) || input.contains(KeyCode.DOWN)) dy++;
        if (input.contains(KeyCode.Q) || input.contains(KeyCode.LEFT)) dx--;
        if (input.contains(KeyCode.D) || input.contains(KeyCode.RIGHT)) dx++;

        if (dx != 0 || dy != 0) {
            NetworkManager.getInstance().send(new MoveRequest(dx, dy));
        }

        if (input.contains(KeyCode.SPACE) && !spaceWasPressed) {
            spaceWasPressed = true;
            NetworkManager.getInstance().send(new PlaceBombRequest());
        }
    }

    private void update(double deltaTime) {
        if (input.contains(KeyCode.ESCAPE) && !escWasPressed) {
            escWasPressed = true;
            if (isVictory || (isGameOver && deathAnimationComplete)) {
                goBackToMenu();
                return;
            }
        }

        if (isVictory || isGameOver) {
            if (isGameOver && deathAnimationStartTime > 0) {
                long elapsed = System.currentTimeMillis() - deathAnimationStartTime;
                if (elapsed >= DEATH_ANIMATION_DURATION) deathAnimationComplete = true;
            }
            return;
        }

        if (!localPlayer.isAlive() && !isGameOver) {
            this.isGameOver = true;
            deathAnimationStartTime = System.currentTimeMillis();
        }

        if (localPlayer.isAlive() && !isVictory) {
            handleInputs();
        }
    }

    private void render() {
        if (labyrinthe == null) return;

        // Efface tout
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Dessine le décor et les effets
        renderer.draw(gc, labyrinthe);
        renderer.drawBombs(gc, bombManager.getBombs());
        renderer.drawExplosions(gc, bombManager.getExplosionCells());

        // Dessine Le joueur local seulement si l'ID est prêt
        if (localPlayer.getId() != -1 && localPlayer.isAlive()) {
            renderer.drawPlayer(gc, localPlayer);
        }

        // Dessine les autres joueurs
        for (Joueur remote : remotePlayers.values()) {
            // Dessine pas le remote s'il a le même ID que nous
            if (remote.getId() != localPlayer.getId() && remote.isAlive()) {
                renderer.drawPlayer(gc, remote);
            }
        }

        if (localPlayer.isAlive()) {
            drawStatsBar(gc, localPlayer.getNb_bombes(), localPlayer.getPv(), localPlayer.getExplosionRange(), localPlayer.getSpeed_multiplier());
        }

        if (isVictory) {
            drawVictoryOverScreen();
        } else if (isGameOver) {
            if (deathAnimationComplete) {
                drawGameOverScreen();
            }
        }
    }

    private void drawVictoryOverScreen() {
        gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        gc.setFill(javafx.scene.paint.Color.GREEN);
        gc.setFont(javafx.scene.text.Font.font("Arial", 50));
        gc.fillText("VICTORY \ud83c\udfc6", gameCanvas.getWidth() / 2 - 140, gameCanvas.getHeight() / 2);
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));
        gc.fillText("Appuyez sur ESC pour retourner au menu", gameCanvas.getWidth() / 2 - 200, gameCanvas.getHeight() / 2 + 60);
    }

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

    private void drawStatItem(GraphicsContext gc, javafx.scene.image.Image image, int count, double x, double y, String label) {
        if (image == null) return;
        gc.drawImage(image, x, y, 25, 25);
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 16));
        gc.fillText(label + " " + count, x + 30, y + 18);
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

    public void goBackToMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        NetworkManager.getInstance().disconnect();
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/launcher.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            javafx.stage.Stage stage = (javafx.stage.Stage) gameCanvas.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Bomberman - Menu Principal");
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur lors du retour au menu : " + e.getMessage());
        }
    }
    
    // Setter pour le labyrinthe (appelé lors de l'INIT_GAME)
    public void setLabyrinthe(Labyrinthe laby) {
        this.labyrinthe = laby;
        Platform.runLater(() -> {
            gameCanvas.setWidth(laby.getWidth() * 32);
            gameCanvas.setHeight(laby.getHeight() * 32);
        });
    }

    public void initPlayers(List<InitGameMessage.PlayerInitDTO> players) {
        String myName = NetworkManager.getInstance().getLocalPlayerName();

        for (InitGameMessage.PlayerInitDTO dto : players) {
            if (dto.name != null && dto.name.equals(myName)) {
                // On s'assure d'avoir l'ID définitif donné par le serveur
                NetworkManager.getInstance().setLocalPlayerId(dto.id);
                this.localPlayer.setId(dto.id);
                
                // On met à jour le localPlayer
                localPlayer.setNom(dto.name);
                localPlayer.setX(dto.startX);
                localPlayer.setY(dto.startY);
                localPlayer.setAlive(true);
            } else {
                // Un autre joueur
                Joueur remote = remotePlayers.computeIfAbsent(dto.id, id -> new Joueur(id, dto.name));
                remote.setX(dto.startX);
                remote.setY(dto.startY);
                remote.setAlive(true);
            }
        }
    }
}

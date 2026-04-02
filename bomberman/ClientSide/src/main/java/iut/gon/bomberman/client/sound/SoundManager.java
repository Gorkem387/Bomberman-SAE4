package iut.gon.bomberman.client.sound;

import javafx.scene.media.AudioClip;
import java.util.Objects;

public class SoundManager {

    private static SoundManager instance;

    private AudioClip explosionSound;
    private AudioClip bonusSound;
    private AudioClip victorySound;
    private AudioClip defeatSound;
    // private AudioClip placeBombSound;

    private SoundManager() {
        // Chargement des sons
        explosionSound = loadSound("Explosion_sound.mp3");
        bonusSound = loadSound("Bonus_sound.mp3");
        victorySound = loadSound("Victory_sound.mp3");
        defeatSound = loadSound("Lose_sound.mp3");
    }

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    /**
     * Permet d'ajouter du son au jeu
     * @param fileName
     * @return
     */
    private AudioClip loadSound(String fileName) {
        String path = "/iut/gon/bomberman/client/assets/sounds/" + fileName;
        return new AudioClip(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
    }

    /**
     * Joue le son pour l'explosion
     */
    public void playExplosion() { explosionSound.play(); }

    /**
     * Joue le son pour les bonus
     */
    public void playBonus() { bonusSound.play(); }

    /**
     * Joue le son lorsque le joueur gagne
     */
    public void playVictory() { victorySound.play(); }

    /**
     * Joue le son lorsque le joueur perd
     */
    public void playDefeat() { defeatSound.play(); }
}

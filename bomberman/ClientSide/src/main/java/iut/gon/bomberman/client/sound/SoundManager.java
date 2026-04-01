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

    private AudioClip loadSound(String fileName) {
        String path = "/iut/gon/bomberman/client/assets/sounds/" + fileName;
        return new AudioClip(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
    }

    public void playExplosion() { explosionSound.play(); }
    public void playBonus() { bonusSound.play(); }
    public void playVictory() { victorySound.play(); }
    public void playDefeat() { defeatSound.play(); }
}

package api.ui;

import javafx.scene.media.AudioClip;

public class AudioMine {

    public String audio = null;

    // Doesn't take file in as filepath but as URI string
    // WILL NOT WORK WITH NORMAL FILEPATH
    public AudioMine(String audio) {
        this.audio = audio;
    }

    public void play() {
        AudioClip clip = new AudioClip(audio);
        clip.play();
    }
}

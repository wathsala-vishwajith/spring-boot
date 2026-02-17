package structural.adapter;

/**
 * Target interface - the interface that client expects
 */
public interface MediaPlayer {
    void play(String audioType, String fileName);
}

package structural.adapter;

/**
 * Adaptee interface - existing interface that needs adapting
 */
public interface AdvancedMediaPlayer {
    void playVlc(String fileName);
    void playMp4(String fileName);
}

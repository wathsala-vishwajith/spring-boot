package structural.adapter;

/**
 * Concrete Adaptee - VLC player implementation
 */
public class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing VLC file: " + fileName);
    }

    @Override
    public void playMp4(String fileName) {
        // Do nothing - VLC player can't play MP4 in this example
    }
}

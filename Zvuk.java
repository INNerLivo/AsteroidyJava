import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Clip;


/**
 * Spusta zvuky <br />
 * Pouzite kody z tutorialov : <br />
 * https://www.ryisnow.online/2021/04/java-for-beginner-how-to-play-audio.html <br />
 * https://stackoverflow.com/questions/953598/audio-volume-control-increase-or-decrease-in-java <br />
 */

public class Zvuk {
    private static Zvuk zvukSingleton;
    
    public static Zvuk dajZvuk() {
        if (Zvuk.zvukSingleton == null) {
            Zvuk.zvukSingleton = new Zvuk();
        }
        return Zvuk.zvukSingleton;
    }

    /**
     * Stačí napísať názov zvuku. <br />
     * Ale berie iba .wav súbory. <br />
     */
    public void spustiZvuk(String nazovZvuku) {
        Clip clip;
        try {
            File subor = new File("Zvuky\\" + nazovZvuku + ".wav");
            AudioInputStream otvorenyZvuk = AudioSystem.getAudioInputStream(subor);
            clip = AudioSystem.getClip();
            clip.open(otvorenyZvuk);

            clip.setFramePosition(0);
            FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-20.0f);
              // Nechceme aby sme ohluchli, nie?
            clip.start();
        } catch (Exception e) {

        }
    }
    
}


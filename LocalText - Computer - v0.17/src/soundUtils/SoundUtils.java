package soundUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


/**
 * This is a class for playing sound files on demand.
 * @author jordan
 *
 */
public class SoundUtils {
	public static void main(String [] args) {
		playClip("/sound.wav");
	}
	/**
	 * Plays a clip that is stored within the jar file.
	 * @param filePath
	 */
	public static void playClip(String filePath) {
		try {
			URL fileurl = SoundUtils.class.getClass().getResource(filePath);
			AudioInputStream ais = AudioSystem.getAudioInputStream(fileurl);
			AudioFormat format = ais.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class,format);
			Clip clip = (Clip)AudioSystem.getLine(info);
			clip.open(ais);
			clip.start();
			while(clip.isRunning()) {
				Thread.yield();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}  catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
			
	}
	public File getResourceFile(String filename) {
		//InputStream fileStream = SoundUtils.class.getResourceAsStream(filename);
		return null;
	}
}

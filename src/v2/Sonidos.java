package v2;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sonidos{

	private Clip clip;

	public Sonidos(String src){
		try{
		    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(src));
		    clip = AudioSystem.getClip();
		    clip.open(audioInputStream);
		}catch(Exception e){e.printStackTrace();}
	}

	protected synchronized void play(){
		try{
			clip.start();
		}catch(Exception e){e.printStackTrace();}
	}

	protected synchronized void stop(){
		try{
			clip.stop();
		}catch(Exception e){e.printStackTrace();}
	}
	
	protected synchronized void loop(){
		try{
			clip.loop(clip.LOOP_CONTINUOUSLY);
		}catch(Exception e){e.printStackTrace();}
	}
}

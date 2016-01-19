package v2;

import java.awt.Image;
import java.io.IOException;
import java.util.Stack;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class Sonidos{

	private Clip clip;
	AudioInputStream audioInputStream; 
	
	private static Clip impulsoNave;
	private static Clip disparo;
	private static Clip enemyBig;
	private static Clip enemySmall;
	private static Clip explosionSmall;		//asteroide mini muere
	private static Clip explosionMed;		//asteroide medio muere
	private static Clip explosionBig;		//asteroide Big muere
	private static Clip sonidoJuego;
	private static final String impulsoNaveSRC = "/sound/thrust.wav";
	private static final String disparoSRC = "/sound/fire.wav";
	private static final String enemyBigSRC = "/sound/saucerBig.wav";
	private static final String enemySmallSRC = "/sound/saucerSmall.wav";
	private static final String explosionSmallSRC = "/sound/bangSmall.wav";
	private static final String explosionMedSRC = "/sound/bangMedium.wav";
	private static final String explosionBigSRC = "/sound/bangLarge.wav";
	private static final String sonidoJuegoSRC = "/sound/beatLooped.wav";
	protected Stack<Clip> listaSonidos = new Stack<>();

	///////////////////////////// setters & getters //////////////////////////////////
	protected Clip getDisparoMisil(){return this.disparo;}
	protected Clip getImpulso(){return this.impulsoNave;}

	//TODO: cargar todos los sonidos al inicio

	public Sonidos(){
		cargaSonidos();

/*
		try{
		    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(src));
		    clip = AudioSystem.getClip();
		    clip.open(audioInputStream);
		}catch(Exception e){e.printStackTrace();}
*/
	}

	protected synchronized void play(Clip clip){
		
		try{			
			if(clip.isOpen()){
				System.out.println("clip is opened...");
				clip.start();
				clip.setMicrosecondPosition(0);		//rebobina el sonido, y se puede reproducir de nuevo
			}
		}
		catch(Exception e){e.printStackTrace();}
	}

	protected synchronized void stop(Clip clip){
		try{clip.stop(); clip.flush();
		}catch(Exception e){e.printStackTrace();}
	}
	
	protected synchronized void loop(Clip clip){
		try{clip.loop(Clip.LOOP_CONTINUOUSLY);
		}catch(Exception e){e.printStackTrace();}
	}

	private void cargaSonidos(){

		
		try{

			audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(impulsoNaveSRC));
		    impulsoNave = AudioSystem.getClip();
		    impulsoNave.open(audioInputStream);
		    
		    //clip.open():
		    //Invoking this method on a line which is already open is illegal and may result in an IllegalStateException. 
		    //Note that some lines, once closed, cannot be reopened. 
		    //Attempts to reopen such a line will always result in a LineUnavailableException.

		    audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(disparoSRC));
		    disparo = AudioSystem.getClip();
		    disparo.open(audioInputStream);
		    
		}catch(Exception e){e.printStackTrace();}
	}
}

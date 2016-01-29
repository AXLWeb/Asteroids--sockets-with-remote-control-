package Asteroids_sockets;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sonidos{

	private static AudioInputStream audioInputStream; 
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

	///////////////////////////// setters & getters //////////////////////////////////
	protected Clip getImpulso(){return Sonidos.impulsoNave;}
	protected Clip getDisparoMisil(){return Sonidos.disparo;}
	protected Clip getEnemyBig(){return Sonidos.enemyBig;}
	protected Clip getEnemySmall(){return Sonidos.enemySmall;}
	protected Clip getExploBig(){return Sonidos.explosionBig;}
	protected Clip getExploMed(){return Sonidos.explosionMed;}
	protected Clip getExploSmall(){return Sonidos.explosionSmall;}
	protected Clip getSonidoJuego(){return Sonidos.sonidoJuego;}
	

	public Sonidos(){
		cargaSonidos();
	}

	protected synchronized void play(Clip clip){
		try{			
			if(clip.isOpen()){
				clip.start();
				clip.setMicrosecondPosition(0);		//rebobina el sonido
				Thread.sleep(10);
			}
		}catch(Exception e){e.printStackTrace();}
	}

	protected synchronized void stop(Clip clip){
		try{
			clip.stop(); 
			clip.flush();
		}catch(Exception e){e.printStackTrace();}
	}
	
	protected synchronized void loop(Clip clip){
		try{
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}catch(Exception e){e.printStackTrace();}
	}

	private void cargaSonidos(){
		try{
			audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(impulsoNaveSRC));
		    impulsoNave = AudioSystem.getClip();
		    impulsoNave.open(audioInputStream);

		    audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(disparoSRC));
		    disparo = AudioSystem.getClip();
		    disparo.open(audioInputStream);
		    
		    audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(enemyBigSRC));
		    enemyBig = AudioSystem.getClip();
		    enemyBig.open(audioInputStream);
		    
		    audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(enemySmallSRC));
		    enemySmall = AudioSystem.getClip();
		    enemySmall.open(audioInputStream);
		    
		    audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(explosionSmallSRC));
		    explosionSmall = AudioSystem.getClip();
		    explosionSmall.open(audioInputStream);
		    
		    audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(explosionMedSRC));
		    explosionMed = AudioSystem.getClip();
		    explosionMed.open(audioInputStream);
		    
		    audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(explosionBigSRC));
		    explosionBig = AudioSystem.getClip();
		    explosionBig.open(audioInputStream);
		    
		    audioInputStream = AudioSystem.getAudioInputStream(Launcher.class.getResource(sonidoJuegoSRC));
		    sonidoJuego = AudioSystem.getClip();
		    sonidoJuego.open(audioInputStream);

		}catch(Exception e){e.printStackTrace();}
	}
}

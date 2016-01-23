package v2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class pantNombre extends JPanel implements Runnable, ActionListener{

	private static final long serialVersionUID = 1L;
	private Frame frame;
	private Generador g;
	//private Mapa mapa;
	private boolean running; // listo;
	private JButton btnListo;
	private JTextField txtIndicaTuNombre;
	private String nombreJugador;
	
	
	///////////////	setters & getters	//////////////////////////////
	public boolean isRunning(){return this.running;}
	
	public pantNombre(Generador g, Frame frame) {
		this.g = g;
		this.frame = frame;
		//this.mapa = g.getMapa();
		this.frame.setBounds(100, 100, 500, 400);
		//this.frame.setResizable(false);
		this.frame.setTitle("Asteroids");
		this.frame.setBackground(Color.BLACK);
		this.frame.getContentPane().add(this);
		
		this.running = true;
		this.setVisible(true);
		
		setPreferredSize(new Dimension(500,400));
		setBounds(0, 0, this.frame.getWidth(), this.frame.getHeight());
		//setBounds(0, 0, 500, 400);	//JPanel
		requestFocus();
		setBackground(Color.BLACK);
		setFocusable(true);
		setLayout(null);
		repaint();
		
		btnListo = new JButton("Listo");
		btnListo.setBackground(Color.DARK_GRAY);
		btnListo.setForeground(Color.WHITE);
		btnListo.setFont(Launcher.arcade);
		btnListo.setActionCommand("listo");
		btnListo.addActionListener(this);
		btnListo.setBounds(210, 186, 91, 23);
		add(btnListo);
		
		txtIndicaTuNombre = new JTextField("AAA");
		txtIndicaTuNombre.setBorder(new LineBorder(Color.YELLOW, 2, true));
		txtIndicaTuNombre.setFont(Launcher.arcade);
		txtIndicaTuNombre.setBounds(178, 135, 154, 23);
		txtIndicaTuNombre.requestFocusInWindow();
		add(txtIndicaTuNombre);
		
		this.frame.pack();
	}
	
	
	@Override
	public void run() {
		while (running){

			this.setVisible(true);
			repaint();

			try {Thread.sleep(1000);} 
			catch (Exception e){e.printStackTrace();}
		}

		killAll();
		this.setVisible(false);
		this.frame.pack();		//lo minimiza
	}

	private void killAll() {
		this.running = false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("listo")) {
			//listo = true;
			running = false;
			nombreJugador = txtIndicaTuNombre.getText();
			this.g.getNave().setNombreJugador(nombreJugador);
		}
	}

}

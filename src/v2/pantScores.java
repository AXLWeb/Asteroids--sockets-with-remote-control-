package v2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.Canvas;
import java.awt.Font;
import javax.swing.JButton;


public class pantScores extends JPanel implements Runnable, ActionListener{

	private static final String src = "src/stats/stats.csv";
	private Frame frame;
	private Generador g;
	private Puntos scores;
	private boolean running, volver, jugar;
	private JTable table;
	private DefaultTableModel model;
	private JButton btnVolver, btnJugar;
	private ArrayList<Jugador> listaJugadores = new ArrayList<Jugador>();
	
	
	///////////////	setters & getters	//////////////////////////////
	public boolean isVisible(){return this.running;}

	public pantScores(Generador g, Frame frame){
		setBackground(Color.BLACK);
		this.g = g;
		this.frame = frame;
		this.volver = false;
		this.jugar = false;
		this.frame.setBounds(100, 100, 500, 400);
		//this.frame.setResizable(false);
		this.frame.setTitle("Ranking de puntuaciones");
		this.frame.setBackground(Color.BLACK);
		this.frame.getContentPane().add(this);
		this.running = true;
		this.setVisible(true);

		this.setBounds(0, 0, this.frame.getWidth(), this.frame.getHeight());
		//setBounds(0, 0, 500, 316);	//JPanel
		requestFocus();
		setFocusable(true);
		setLayout(null);

		btnVolver = new JButton("Volver");
		btnVolver.setBackground(Color.DARK_GRAY);
		btnVolver.setForeground(Color.WHITE);
		btnVolver.setActionCommand("volver");
		btnVolver.addActionListener(this);
		btnVolver.setBounds(60, this.frame.getHeight()-100, 91, 23);
		add(btnVolver);

		btnJugar = new JButton("Jugar");
		btnJugar.setBackground(Color.DARK_GRAY);
		btnJugar.setForeground(Color.WHITE);
		btnJugar.addActionListener(this);
		btnJugar.setActionCommand("jugar");
		btnJugar.setBounds(this.frame.getWidth()/2, this.frame.getHeight()-100, 91, 23);
		
		add(btnJugar);

		model = new DefaultTableModel();
		String header[] = new String[] { "NOMBRE", "PUNTOS" };
		model.setColumnIdentifiers(header);
		model.addRow(header);

		/*
   		TableCellRenderer r = new DefaultTableCellRenderer();
  		((Component)r).setBackground(Color.YELLOW);
   		((Component)r).setForeground(Color.red);
   		table.getColumnModel().getColumn(0).setCellRenderer(r);
   		//table.getColumnModel().getColumn(3).setCellRenderer(r);

		*/
		
		table = new JTable(model);
		table.setGridColor(Color.WHITE);
		//table.setBounds(10, 50, 500, 400);
		table.setBounds(10, 50, this.frame.getWidth(), this.frame.getHeight());
		table.setFont(Launcher.fuente);
		table.setBackground(Color.BLACK);
		table.setForeground(Color.WHITE);
		table.setBorder(new LineBorder(new Color(0, 0, 0), 2));

		add(table);
	}
	
	
	
	
	

	@Override
	public void run() {

		System.out.println("run de pantScores");
		muestraScores();

		while (running){
			try {Thread.sleep(1000);} 
			catch (Exception e){e.printStackTrace();}
		}
		
		if(volver){
			//TODO pantInicial
			running = false;
			System.out.println("volviendo..");
			this.setVisible(false);
			this.g.generaMapaPrevio();
		}
		if(jugar){
			//TODO matar JPanel
			running = false;
			System.out.println("jugar de nuevo");
			this.setVisible(false);
			
			this.g.iniciarJuego();
			//this.g = new Generador();
		}
		
		killAll();
		this.setVisible(false);
	}
	

	protected void muestraScores(){
		String[] separador = null;
		String line;

		try {
			FileReader fr = new FileReader(src);
			BufferedReader br = new BufferedReader(fr);

			br.readLine();	//omite TITULO

            while((line = br.readLine()) != null) {

            	separador = line.split(";");
            	System.out.println("<<"+separador[0]+"; "+separador[1]);
            	
            	//Jugador player = new Jugador(separador[0], Integer.valueOf(separador[1]));
            	//listaJugadores.add(player);

            	model.addRow(separador);
            }
    		
            fr.close();
            br.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Archivo de estadisticas no encontrado en "+src);
		}
	}
	
	
	public void killAll() {
		this.running = false;
		this.setOpaque(false);
		this.removeAll();
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("volver")) {
			System.out.println("volverrrr");
			volver = true;
			running = false;
		}
		else if(e.getActionCommand().equals("jugar")) {
			System.out.println("jugarrrr");
			jugar = true;
			running = false;
		}
	}
}

package Asteroids_sockets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import java.awt.Component;


public class pantScores extends JPanel implements Runnable, ActionListener{

	private static final long serialVersionUID = 1L;
	private static final String src = "src/stats/stats.csv";
	private Frame frame;
	private Generador g;
	private boolean running, volver, jugar;
	private JTable table;
	private DefaultTableModel model;
	private JButton btnVolver, btnJugar;
	private JTextField txtUserNotLogged;

	
	///////////////	setters & getters	//////////////////////////////
	public boolean isRunning(){return this.running;}

	public pantScores(Generador g, Frame frame){
		setBackground(Color.BLACK);
		this.g = g;
		this.frame = frame;
		this.volver = false;
		this.jugar = false;
		this.frame.setBounds(100, 100, 500, 400);
		
		//this.frame.setResizable(false);
		this.frame.setTitle("Puntuaciones");
		this.frame.setBackground(Color.BLACK);
		this.frame.getContentPane().add(this);
		this.running = true;
		setVisible(true);
		setPreferredSize(new Dimension(500,500));
		setBounds(0, 0, this.frame.getWidth(), this.frame.getHeight());
		requestFocus();
		setFocusable(true);
		setLayout(null);

		btnVolver = new JButton("Volver");
		btnVolver.setFont(Launcher.arcade);
		btnVolver.setBackground(Color.DARK_GRAY);
		btnVolver.setForeground(Color.WHITE);
		btnVolver.setActionCommand("volver");
		btnVolver.addActionListener(this);
		btnVolver.setBounds(60,frame.getHeight()-75,99,25);
		add(btnVolver);

		btnJugar = new JButton("Jugar");
		btnJugar.setFont(Launcher.arcade);
		btnJugar.setBackground(Color.DARK_GRAY);
		btnJugar.setForeground(Color.WHITE);
		btnJugar.setActionCommand("jugar");
		btnJugar.addActionListener(this);
		btnJugar.setBounds(this.frame.getWidth()/2,frame.getHeight()-75,99,25);
		add(btnJugar);

		txtUserNotLogged = new JTextField();
		txtUserNotLogged.setText("  \rNo user logged in unable to PLAY game!!");
		txtUserNotLogged.setRequestFocusEnabled(false);
		txtUserNotLogged.setFont(Launcher.arcade);
		txtUserNotLogged.setBackground(Color.DARK_GRAY);
		txtUserNotLogged.setForeground(Color.RED);
		txtUserNotLogged.setActionCommand("userLog");
		txtUserNotLogged.setBounds(94, 453, 330, 30);
		txtUserNotLogged.setVisible(false);
		add(txtUserNotLogged);

		model = new DefaultTableModel();
		String header[] = new String[] { "NOMBRE", "PUNTOS" };
		model.setColumnIdentifiers(header);
		model.addRow(header);

		table = new JTable(model);
		table.setGridColor(Color.WHITE);
		table.setBounds(10, 50, this.frame.getWidth(), this.frame.getHeight());
		table.setFont(Launcher.courierNew);
		table.setBackground(Color.BLACK);
		table.setForeground(Color.WHITE);
		table.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		add(table);
		
		

		this.frame.pack();
	}



	@Override
	public void run() {
		repaint();
		muestraScores();

		while (running){
			try {Thread.sleep(1000);} 
			catch (Exception e){e.printStackTrace();}
		}
		
		if(volver){
			this.setVisible(false);
			this.frame.pack();
			this.g.iniciarMapaPrevio();
		}

		if(jugar){
			this.setVisible(false);
			this.frame.pack();
			this.g.iniciarJuego();
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
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("volver")) {
			volver = true;
			running = false;
		}
		else if(e.getActionCommand().equals("jugar")) {
			System.out.println("user logging "+g.getServer().isLogged());
			if(g.getServer().isLogged()){
				jugar = true;
				running = false;
			}
			else{
				txtUserNotLogged.setVisible(true);
			}

		}
	}
}

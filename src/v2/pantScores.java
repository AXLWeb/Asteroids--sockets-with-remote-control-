package v2;

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


public class pantScores extends JPanel implements Runnable, ActionListener{

	private static final long serialVersionUID = 1L;
	private static final String src = "src/stats/stats.csv";
	private Frame frame;
	private Generador g;
	private boolean running, volver, jugar;
	private JTable table;
	private DefaultTableModel model;
	private JButton btnVolver, btnJugar;
	//private Puntos scores;
	//private ArrayList<Jugador> listaJugadores = new ArrayList<Jugador>();
	
	
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
		setPreferredSize(new Dimension(500,700));
		setBounds(0, 0, this.frame.getWidth(), this.frame.getHeight());
		//setBounds(0, 0, 500, 316);	//JPanel
		requestFocus();
		setFocusable(true);
		setLayout(null);

		btnVolver = new JButton("Volver");
		btnVolver.setFont(Launcher.arcade);
		btnVolver.setBackground(Color.DARK_GRAY);
		btnVolver.setForeground(Color.WHITE);
		btnVolver.setActionCommand("volver");
		btnVolver.addActionListener(this);
		add(btnVolver);

		btnJugar = new JButton("Jugar");
		btnJugar.setFont(Launcher.arcade);
		btnJugar.setBackground(Color.DARK_GRAY);
		btnJugar.setForeground(Color.WHITE);
		btnJugar.setActionCommand("jugar");
		btnJugar.addActionListener(this);
		add(btnJugar);


		model = new DefaultTableModel();
		String header[] = new String[] { "NOMBRE", "PUNTOS" };
		model.setColumnIdentifiers(header);
		model.addRow(header);

		table = new JTable(model);
		table.setGridColor(Color.WHITE);
		//table.setBounds(10, 50, 500, 400);
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
			this.frame.pack();		//lo minimiza
			this.g.iniciarMapaPrevio();
		}

		if(jugar){
			this.setVisible(false);
			this.frame.pack();		//lo minimiza
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

            btnVolver.setBounds(60,table.getHeight()+5,99,25);
            btnJugar.setBounds(this.frame.getWidth()/2,table.getHeight()+5,99,25);

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
			jugar = true;
			running = false;
		}
	}
}

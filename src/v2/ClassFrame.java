package v2;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ClassFrame extends JPanel {

	private static final long serialVersionUID = -7003490063433128838L;

	private Frame frame;
	private Generador g;
	private String nombre;
	private JTextArea editTextArea;

	public ClassFrame(Generador g, Frame frame) {
		this.g = g;
		this.frame = frame;

		repaint();
		validate();

		setBorder(BorderFactory.createLineBorder(Color.red));
		setPreferredSize(new Dimension(300, 100));
		setMaximumSize(new Dimension(999, 999));
		setVisible(true);
		//setBounds(100, 100, 100, 100);
		//setBounds(g.getMapa().getWidth()/2, g.getMapa().getHeight()/2, 100, 100);
		//setBounds(g.getFrame().getWidth()/2, g.getFrame().getHeight()/2, 100, 100);

		setOpaque(false); // Set to true to see it
		setSize(100, 100);
		//setLocation(g.getFrame().getWidth()/2, g.getFrame().getHeight()/2);
		//setLayout(null);

		editTextArea = new JTextArea("Indica tu nombre");
		editTextArea.setBounds(1, 79, 298, 20);
		editTextArea.setMaximumSize(new Dimension(130, 20));
		//editTextArea.setBounds(g.getMapa().getWidth()/2, g.getMapa().getHeight()/2, 100, 100);
		editTextArea.setBackground(Color.BLUE);
		editTextArea.setForeground(Color.WHITE);
		add(editTextArea);
	}


	
	
	
	
	
}
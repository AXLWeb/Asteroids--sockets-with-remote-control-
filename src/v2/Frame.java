package v2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.xml.crypto.dsig.keyinfo.KeyValue;

public class Frame extends JFrame {

	/////////////// setters & getters //////////////////////////////
	public void setMapa(Mapa mapa) {this.getContentPane().add(mapa);}

	// Constructor inicial del frame.
	public Frame() {
		setTitle("Asteroids");
		setFocusable(true);
		requestFocus();
		setVisible(true);
		pack();
		repaint();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}

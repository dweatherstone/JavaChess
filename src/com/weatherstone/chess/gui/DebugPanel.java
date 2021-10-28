package com.weatherstone.chess.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class DebugPanel extends JPanel implements Observer {
	
	private static final Dimension CHAT_PANEL_DIMENSION = new Dimension(600, 150);
	private final JTextArea jTextArea;
	
	public DebugPanel() {
		super(new BorderLayout());
		this.jTextArea = new JTextArea("");
		add(this.jTextArea);
		setPreferredSize(CHAT_PANEL_DIMENSION);
		validate();
		setVisible(true);
	}
	
	public void redo() {
		validate();
	}

	@Override
	public void update(Observable o, Object arg) {
		this.jTextArea.setText(arg.toString().trim());
		redo();
	}

}

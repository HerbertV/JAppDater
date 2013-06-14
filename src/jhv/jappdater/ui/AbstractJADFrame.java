/*
 *  __  __      
 * /\ \/\ \  __________   
 * \ \ \_\ \/_______  /\   
 *  \ \  _  \  ____/ / /  
 *   \ \_\ \_\ \ \/ / / 
 *    \/_/\/_/\ \ \/ /  
 *             \ \  /
 *              \_\/
 *
 * -----------------------------------------------------------------------------
 * @author: Herbert Veitengruber 
 * @version: 1.0.0
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 2013 Herbert Veitengruber 
 *
 * Licensed under the MIT license:
 * http://www.opensource.org/licenses/mit-license.php
 */
package jhv.jappdater.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import jhv.jappdater.event.UpdateEvent;
import jhv.jappdater.event.UpdateEventListener;
import jhv.jappdater.event.UpdateEventProcessor;

/**
 * AbstractJADFrame
 */
public abstract class AbstractJADFrame 
		extends JFrame 
		implements UpdateEventListener
{
	// ============================================================================
	//  Constants
	// ============================================================================

	private static final long serialVersionUID = -6934179713789811325L;

	
	// ============================================================================
	//  Variables
	// ============================================================================

	protected UpdateEventProcessor uep;
	
	protected JTextArea txtOutput;
	
	protected JButton btnDone;
	
	/**
	 * executor
	 */
	protected ExecutorService executor;
	
	// ============================================================================
	//  Constructor
	// ============================================================================

	/**
	 * Constructor
	 * 
	 * @param l
	 */
	public AbstractJADFrame(
			UpdateEventListener l
		)
	{
		super();
		
		this.getContentPane().setLayout(new BorderLayout());
		
		JScrollPane scroll = new JScrollPane();
		this.getContentPane().add(scroll,BorderLayout.CENTER);
		
		txtOutput = new JTextArea();
		txtOutput.setEditable(false);
		scroll.setViewportView(txtOutput);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		this.getContentPane().add(panel, BorderLayout.SOUTH );
		
		btnDone = new JButton();
		btnDone.setEnabled(false);
		panel.add(btnDone);
		btnDone.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) 
				{
					close(null);
				}
			});
		
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
	        	public void windowClosing(WindowEvent e) {
	        		close(e);
	        	}
	        });
		
		uep = new UpdateEventProcessor();
		if( l != null )
			uep.addUpdateEventListener(l);
		
		uep.addUpdateEventListener(this);
		
		this.setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
			);
		
	}

	// ============================================================================
	//  Functions
	// ============================================================================
	
	
	/**
	 * close
	 * 
	 * @param e
	 */
	protected void close(WindowEvent e) 
	{
		if( executor != null )
			this.executor.shutdown();
		
		this.dispose();
	}


	@Override
	public void handleUpdateEvent(UpdateEvent event) 
	{
		this.setCursor(
				Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
			);
		btnDone.setEnabled(true);
		
		if( event.getType() == UpdateEvent.EXCEPTION )
			txtOutput.append(event.getException().getMessage());
	}
}

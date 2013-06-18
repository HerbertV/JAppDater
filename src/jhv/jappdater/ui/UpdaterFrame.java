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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import jhv.jappdater.VersionXML;
import jhv.jappdater.event.UpdateEvent;
import jhv.jappdater.event.UpdateEventListener;
import jhv.jappdater.task.UpdaterTask;

/**
 * CheckerFrame
 * 
 * JFrame that displays the current update check step. 
 */
public class UpdaterFrame 
		extends AbstractJADFrame
{
	// ============================================================================
	//  Constants
	// ============================================================================

	private static final long serialVersionUID = -444544613513431540L;

	// ============================================================================
	//  Variables
	// ============================================================================
	
	private JProgressBar progressBar;
	
	private Properties properties;
	
	private boolean updateFinsished = false;
	
	// ============================================================================
	//  Constructors
	// ============================================================================

	/**
	 * Constructor.
	 * 
	 * @param localXML
	 * @param tempXML
	 * @param p contains all label texts.
	 * @param l listener
	 */
	public UpdaterFrame(
			final VersionXML localXML,
			final VersionXML tempXML,
			Properties p, 
			UpdateEventListener l
		) 
	{
		super(l);
		this.properties = p;
		
		this.setTitle(
				tempXML.getApplicationName()
					+ " "
					+ p.getProperty("updater.title", "updater.title")
					+ " "
					+ tempXML.getApplicationVersion()
			);
		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		
		btnDone.setText(p.getProperty("updater.btn.done"));
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.getContentPane().add(panel,BorderLayout.NORTH);
		
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(280,20));
		progressBar.setStringPainted(true);
		
		panel.add(progressBar);
		
		SwingUtilities.invokeLater(new Runnable(){
				public void run() 
				{
					executor = Executors.newSingleThreadExecutor();
					executor.execute(new UpdaterTask(
							localXML,
							tempXML,
							txtOutput,
							progressBar,
							properties,
							uep
						));
				}
			});
	}
	
	@Override
	protected void close(WindowEvent we)
	{
		String startup = properties.getProperty("app.startup");
			
		if( startup != null )
		{
			if( updateFinsished )
				startup += " -jadfinished";
			try {
				Runtime.getRuntime().exec(startup);
			} catch (IOException e) {
				// ignore
			}
		}
		super.close(we);
		System.exit(0);
	}
	
	@Override
	public void handleUpdateEvent(UpdateEvent event) 
	{
		if( event.getType() == UpdateEvent.FINISHED )
			updateFinsished = true;
		
		super.handleUpdateEvent(event);
	}
}

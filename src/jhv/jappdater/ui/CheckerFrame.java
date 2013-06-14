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

import java.util.Properties;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import jhv.jappdater.VersionXML;
import jhv.jappdater.event.UpdateEventListener;
import jhv.jappdater.task.CheckerTask;

/**
 * CheckerFrame
 * 
 * JFrame that displays the current update check step. 
 */
public class CheckerFrame 
		extends AbstractJADFrame
{
	// ============================================================================
	//  Constants
	// ============================================================================

	private static final long serialVersionUID = -444544613513431540L;

	
	// ============================================================================
	//  Constructors
	// ============================================================================

	/**
	 * Constructor.
	 * 
	 * @param xml
	 * @param p contains all label texts.
	 * @param l listener
	 */
	public CheckerFrame(
			final VersionXML xml,
			final Properties p, 
			UpdateEventListener l
		) 
	{
		super(l);
		
		this.setTitle(
				xml.getApplicationName()
					+ " "
					+ p.getProperty("checker.title", "checker.title")
			);
		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		
		txtOutput.append(
				p.getProperty("checker.msg.currentVersion") + " "
					+ xml.getApplicationVersion() 
					+ "\n\n"
			);
		
		btnDone.setText(p.getProperty("checker.btn.done"));
		
		SwingUtilities.invokeLater(new Runnable(){
				public void run() 
				{
					executor = Executors.newSingleThreadExecutor();
					executor.execute(new CheckerTask(xml, txtOutput, p, uep));
				}
			});
	}

}

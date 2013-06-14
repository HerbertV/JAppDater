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
package jhv.jappdater;

import java.util.Properties;

import javax.swing.SwingUtilities;

import jhv.jappdater.event.UpdateEventListener;
import jhv.jappdater.ui.CheckerFrame;

/**
 * Checker
 * 
 * Call checkForUpdates from your application.
 * An UpdateEvent is fired if the check is done or fails.
 */
public class Checker 
{
	/**
	 * checkForUpdates
	 * 
	 * uses default properties
	 * 
	 * @param l
	 * @throws Exception
	 */
	public static void checkForUpdates(UpdateEventListener l) 
			throws Exception 
	{
		Properties p = Updater.loadProperties(Updater.DEFAULT_PROPERTIES);		
		checkForUpdates(p,l);
	}
	
	/**
	 * checkForUpdates
	 * 
	 * @param p
	 * @param l
	 * @throws Exception
	 */
	public static void checkForUpdates(
			final Properties p, 
			final UpdateEventListener l
		) throws Exception 
	{
		// we need to load this first.
		final VersionXML localXML =  new VersionXML();
		localXML.loadLocalFile(
				p.getProperty("local.folder")
					+p.getProperty("local.index.fileName")
			);
		
		SwingUtilities.invokeLater(new Runnable(){
				public void run() 
				{
					CheckerFrame f = new CheckerFrame(localXML,p,l);
					f.setVisible(true);
				}
			});
	}
	
	/**
	 * static main
	 * 
	 * starts the checker with default properties
	 * 
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try 
		{
			checkForUpdates(null);
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
		
}

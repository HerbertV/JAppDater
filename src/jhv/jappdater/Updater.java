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

import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.SwingUtilities;

import jhv.jappdater.ui.UpdaterFrame;

/**
 * Updater
 * 
 * In your application call:
 * 
 * Runtime.getRuntime().exec("java -jar jAppDater.jar [optional: properties filename]");
 * Don't forget:
 * System.exit(0);
 * 
 * So your application is closed and the Updater runs in its own process and can
 * update all your application files.
 */
public class Updater
{

	// ============================================================================
	//  Constants
	// ============================================================================

	/**
	 * jAppDater Version
	 */
	public static final String VERSION = "1.0.1";
	
	public static final String DEFAULT_PROPERTIES = "resources/en_GB/default.properties";
	
	
	// ============================================================================
	//  Functions
	// ============================================================================

	/**
	 * loadProperties
	 * 
	 * @param propfile
	 * @return
	 */
	public static Properties loadProperties(String propfile)
	{
		Properties p = new Properties();
		try 
		{
			p.load(Checker.class.getClassLoader().getResourceAsStream(
					propfile
				));
			return p;
		} catch( Exception e ) {
			try
			{
				FileInputStream fis = new FileInputStream(
						propfile
					);
				p.load(fis);
				fis.close();
        		return p;
        		
			} catch( Exception e2 ) {
				// ignore
			}
		}
		return null;
	}
	
	/**
	 * updateNow
	 * 
	 * @param p
	 * @throws Exception
	 */
	public static void updateNow(final Properties p)
			throws Exception 
	{
		// we need to load this first.
		final VersionXML localXML = new VersionXML();
		localXML.loadLocalFile(
				p.getProperty("local.folder")
					+p.getProperty("local.index.fileName")
			);

		final VersionXML tempXML = new VersionXML();
		tempXML.loadLocalFile(
				p.getProperty("temp.folder")
					+p.getProperty("local.index.fileName")
			);
		
		SwingUtilities.invokeLater(new Runnable(){
				public void run() 
				{
					UpdaterFrame f = new UpdaterFrame(
							localXML,
							tempXML,
							p,
							null
						);
					f.setVisible(true);
				}
			});
	}
	
	/**
	 * static main
	 * 
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Properties p;
		if( args.length == 1 )
		{
			p = loadProperties(args[0]);
		} else {
			p = loadProperties(DEFAULT_PROPERTIES);
		}
		
		try 
		{
			updateNow(p);
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

}

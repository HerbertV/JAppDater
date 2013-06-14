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
package jhv.jappdater.task;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import jhv.jappdater.VersionXML;
import jhv.jappdater.event.UpdateEventProcessor;

/**
 * CheckForUpdateTask
 * 
 * SwingWorker Task that downloads the remote index file and checks 
 * if an update is available.
 */
public class CheckerTask
		extends SwingWorker<String, String> 
{
	// ============================================================================
	//  Variables
	// ============================================================================

	private JTextArea txtOutput;
	
	private Properties properties;
	
	private VersionXML localXML;
	
	private VersionXML remoteXML;
	
	private boolean isUpdateAvailable = false;
	
	private UpdateEventProcessor uep;

	
	// ============================================================================
	//  Constructors
	// ============================================================================
	
	/**
	 * CheckForUpdateTask
	 * 
	 * @param local
	 * @param output
	 * @param p
	 * @param uep
	 */
	public CheckerTask(
			VersionXML local,
			JTextArea output,
			Properties p,
			UpdateEventProcessor uep
		)
	{
		this.localXML = local;
		this.txtOutput = output;
		this.properties = p;
		this.uep = uep;
	}

	// ============================================================================
	//  Functions
	// ============================================================================
	
	@Override
	protected String doInBackground() 
			throws Exception 
	{
		publish(
				this.properties.getProperty("checker.msg.connecting")
					+ "\n" + localXML.getApplicationURL().getProtocol()
					+ "://"+ localXML.getApplicationURL().getHost()
					+ ":" + localXML.getApplicationURL().getPort()
					+ "\n"
			);
		try 
		{
			Thread.sleep(500);
		} catch( InterruptedException e ) {
			// ignore
		}
		
		try
		{ 
			publish(
					this.properties.getProperty("checker.msg.downloading")
						+ "\n" + localXML.getApplicationURL().getFile()
						+ "\n"
				);
			doLoadServerFile();
						
			publish(this.properties.getProperty("checker.msg.checking"));
			doAllChecks();
			
		} catch( Exception e ) {
			uep.fireUpdateEventException(e);
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * doLoadServerFile 
	 * 
	 * load file from servers
	 * 
	 * @throws Exception
	 */
	private void doLoadServerFile() 
			throws Exception
	{
		remoteXML = new VersionXML();
		remoteXML.loadServerFile(
				localXML.getApplicationURL()
			);
	}
	
	/**
	 * doAllChecks
	 * 
	 * validate everything and update check
	 * 
	 * @throws Exception
	 */
	private void doAllChecks() 
			throws Exception
	{
		localXML.validateXML();
		remoteXML.validateXML();
		
		if( !localXML.compareApplicationNames(remoteXML) )
			throw new Exception(
					"jAppDater Exception: remote update file does not match local file!"
				);
			
		isUpdateAvailable = localXML.compareVersions(remoteXML);
		if( isUpdateAvailable )
		{
			File tempFolder = new File(properties.getProperty("temp.folder"));
			if( !tempFolder.exists() )
				tempFolder.mkdir();
			
			remoteXML.saveTo(
					properties.getProperty("temp.folder")
						+ properties.getProperty("local.index.fileName")
				);
			publish(
					properties.getProperty("checker.msg.update")
					+ remoteXML.getApplicationVersion()
				);
		} else {
			publish(
					properties.getProperty("checker.msg.noupdate")
				);
		}
		
	}
	
	
	
	@Override
	protected void process(List<String> chunks) 
	{
		 for( String line : chunks)
			 txtOutput.append(line + "\n");
	}
	
	@Override
	protected void done()
	{
		try 
		{
			this.get();
			
			if( isUpdateAvailable )
			{
				uep.fireUpdateEventUpdateAvailable(
						remoteXML.getApplicationVersion()
					);
				
			} else {
				uep.fireUpdateEventNoUpdateAvailable();
			}
			
		} catch( Exception e ) {
			uep.fireUpdateEventException(e);
		} 
	}

}

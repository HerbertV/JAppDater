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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jhv.jappdater.UpdateResource;
import jhv.jappdater.VersionXML;
import jhv.jappdater.event.UpdateEventProcessor;

/**
 * CheckForUpdateTask
 * 
 * SwingWorker Task that downloads the remote index file and checks 
 * if an update is available.
 */
public class UpdaterTask
		extends SwingWorker<String, String> 
{
	// ============================================================================
	//  Constants
	// ============================================================================

	private static final int BUFFER = 1024;
	
	
	// ============================================================================
	//  Variables
	// ============================================================================

	private JTextArea txtOutput;
	
	private JProgressBar progressBar;
	
	private Properties properties;
	
	private VersionXML localXML;
	
	private VersionXML remoteXML;
	
	private boolean isUpdateAvailable = false;
	
	private UpdateEventProcessor uep;
	
	private int progress = 0;

	
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
	public UpdaterTask(
			VersionXML local,
			VersionXML remote,
			JTextArea output,
			JProgressBar progressBar,
			Properties p,
			UpdateEventProcessor uep
		)
	{
		this.localXML = local;
		this.remoteXML = remote;
		
		this.txtOutput = output;
		this.progressBar = progressBar;
		
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
		try 
		{
			Thread.sleep(500);
		} catch( InterruptedException e ) {
			// ignore
		}
		
		publish(this.properties.getProperty("updater.msg.start")+ "\n") ;
		
		try
		{ 
			publish( this.properties.getProperty("updater.msg.comparing")) ;
			List<UpdateResource> resources =  doGenerateResourceList();
			
			publish( "\n" + this.properties.getProperty("updater.msg.downloading") );
			doDownload(resources);
			
			
			publish( "\n" + this.properties.getProperty("updater.msg.installing") );
			doInstall(resources);
			
			publish( "\n" + this.properties.getProperty("updater.msg.cleanup") );
			
			// first move remote to local
			String localFileName = properties.getProperty("local.folder")
					+ properties.getProperty("local.index.fileName");
			localXML = null;
			remoteXML.saveTo(localFileName);
			
			doCleanTempFolder(new File(this.properties.getProperty("temp.folder")));
			
			// done
			publish( "\n" + this.properties.getProperty("updater.msg.finished") );
			
		} catch( Exception e ) {
			uep.fireUpdateEventException(e);
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * doGenerateResourceList
	 * 
	 * @return
	 */
	private List<UpdateResource> doGenerateResourceList()
	{
		List<UpdateResource> list = new ArrayList<UpdateResource>();
		progress = 0;
		
		// remote and local can have different sizes
		// if a new file is added from remote.
		NodeList localFiles = localXML.getFileNodes();
		NodeList remoteFiles = remoteXML.getFileNodes();
		int filecount = remoteFiles.getLength();
		
		Element local;
		Element remote;
		UpdateResource resource;
		
		for( int i=0; i<filecount; i++ )
		{
			remote = (Element)remoteFiles.item(i);
			
			progress = (int) ( ((float)i+1)/filecount *100.0f );
			// just update the Progress
			publish("");
			
			// find the referring local file
			for( int j=0; j<localFiles.getLength(); j++ )
			{
				local = (Element)localFiles.item(i);
				if( local.getAttribute("ID").equals(remote.getAttribute("ID")) )
				{
					String rv = remote.getAttribute("version");
					String rc = remote.getAttribute("checksum");
					boolean needsUpdate = false;		
					if( rv.equals("") && rc.equals("") )
					{
						// always update
						needsUpdate = true;
					} else {
						String lv = local.getAttribute("version");
						String lc = local.getAttribute("checksum");
						
						if( lv.compareTo(rv) < 0 )
							needsUpdate = true;
						
						if( !lc.equals(rc) )
							needsUpdate = true;
					}
					
					if( needsUpdate )
					{
						boolean unpack = false;
						if( remote.getAttribute("unpack").equals("true") )
							unpack = true;
						
						resource = new UpdateResource(
								remote.getAttribute("src"), 
								remote.getAttribute("dest"), 
								unpack
							);
						publish(
								properties.getProperty("updater.msg.needsupdate")
								+ " " + resource.getSrc() 
							);
						list.add(resource);
					}
					break;
				}
			}
		}
		
		return list;
	}
	
	/**
	 * doDownload
	 * 
	 * @param resources
	 * @throws IOException
	 */
	private void doDownload(List<UpdateResource> resources) 
			throws IOException 
	{
		int filecount = resources.size();
		UpdateResource r;
		progress = 0;
		
		String tempfolder = properties.getProperty("temp.folder");
		
		for( int i=0; i<filecount; i++ )
		{
			progress = (int) ( ((float)i+1)/filecount *100.0f );
			
			r = resources.get(i);
			String filename = r.getDestFileName();
			if( filename == null || filename.equals("") )
				filename = r.getSrcFileName();
			
			File temp = new File( tempfolder + r.getDestPath() + filename );
			
			// skip existing files since they are already downloaded on a previous attempt.
			if( temp.exists() )
				continue;
			
			// create folders
			Files.createDirectories(
					new File( tempfolder + r.getDestPath()).toPath()
				);
			
			// download
			BufferedInputStream in = null;
	    	FileOutputStream fout = null;
	    	try
	    	{
	    		URL url = new URL(remoteXML.getServerBasePath() + r.getSrc());
	    		
	    		publish(
	    				properties.getProperty("updater.msg.download")
	    					+ r.getSrc()
	    			);
				
	    		in = new BufferedInputStream(url.openStream());
	    		fout = new FileOutputStream(temp);

	    		byte data[] = new byte[BUFFER];
	    		int count;
	    		while( (count = in.read(data, 0, BUFFER)) != -1 )
	    			fout.write(data, 0, count);
	    		
	    	}
	    	finally
	    	{
	    		if (in != null)
	    			in.close();
	    		if (fout != null)
	    			fout.close();
	    	}
		}
	}
	
	private void doInstall(List<UpdateResource> resources) 
			throws ZipException, IOException 
	{
		int filecount = resources.size();
		UpdateResource r;
		progress = 0;
		
		String tempfolder = properties.getProperty("temp.folder");
		
		for( int i=0; i<filecount; i++ )
		{
			progress = (int) ( ((float)i+1)/filecount *100.0f );
			
			r = resources.get(i);
			String filename = r.getDestFileName();
			
			if( filename == null || filename.equals("") )
				filename = r.getSrcFileName();
			
			File temp = new File( tempfolder + r.getDestPath() + filename );
			
			if( r.needUnpack() )
			{
				publish(
	    				properties.getProperty("updater.msg.unpack")
	    					+ filename
	    			);
				doUnzip(r, temp);
				
			} else {
				publish(
	    				properties.getProperty("updater.msg.install")
	    					+ filename
	    			);
				Files.createDirectories(new File( r.getDestPath()).toPath());
				Files.move(
						temp.toPath(), 
						new File( r.getDestPath(), filename ).toPath(), 
						StandardCopyOption.REPLACE_EXISTING
					);
			}
		}
	}
	
	/**
	 * doUnzip
	 * 
	 * @param r
	 * @param file
	 * @throws ZipException
	 * @throws IOException
	 */
	private void doUnzip(UpdateResource r, File file) 
			throws ZipException, IOException
	{
		ZipFile zip = new ZipFile(file);
		String destPath = r.getDestPath();
		
		// create folders
		Files.createDirectories(new File(destPath).toPath());
		
		Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

		while( zipFileEntries.hasMoreElements() )
		{
			ZipEntry entry = zipFileEntries.nextElement();
			String currentEntry = entry.getName();
			
			// dir found create if not exists
			if( entry.isDirectory() )
			{
				Files.createDirectories(new File(destPath+ currentEntry ).toPath());
				continue;
			}
			
			int count;
			byte data[] = new byte[BUFFER];
			
			File destFile = new File(destPath, currentEntry);
			BufferedInputStream in = new BufferedInputStream(zip.getInputStream(entry));
			FileOutputStream fout = new FileOutputStream(destFile);
			BufferedOutputStream dest = new BufferedOutputStream(fout,BUFFER);

			while( (count = in.read(data, 0, BUFFER)) != -1 ) 
				dest.write(data, 0, count);
			
			dest.flush();
			dest.close();
			in.close();
		}
		zip.close();
	}
	
	/**
	 * doCleanTempFolder
	 * 
	 * cleans up everything still there.
	 * 
	 * @param path
	 */
	private void doCleanTempFolder(File path)
	{
		if( !path.exists() )
			return;
		
		for( File f : path.listFiles() )
		{
			if( f.isDirectory() ) 
				doCleanTempFolder(f);
			
			f.delete();
		}
		path.delete();
	}
	
	@Override
	protected void process(List<String> chunks) 
	{
		 for( String line : chunks)
			 if( !line.equals("") )
				 txtOutput.append(line + "\n");
		 
		 progressBar.setValue(progress);
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

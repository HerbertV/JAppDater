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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * VersionXML
 * 
 * Represents the version index file.
 */
public class VersionXML 
{
	// ============================================================================
	//  Variables
	// ============================================================================

	private Document document;
	
	private Element docElement;

	private boolean isLocal;

	
	// ============================================================================
	//  Constructors
	// ============================================================================

	/**
	 * Constructor
	 */
	public VersionXML() 
	{
		
	}

	// ============================================================================
	//  Functions
	// ============================================================================

	/**
	 * loadLocalFile
	 * 
	 * loads the version index file from local hard disk
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void loadLocalFile(String file)
			throws Exception
	{
		isLocal = true;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
	
		File f = new File(file);
		document = db.parse( f );
		docElement = document.getDocumentElement();
	}
	
	/**
	 * loadServerFile
	 * 
	 * loads the version index file from a server
	 * 
	 * @param url
	 * @throws Exception
	 */
	public void loadServerFile(URL url)
			throws Exception
	{
		isLocal = false;
		
		URLConnection connection = url.openConnection();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		document = db.parse( connection.getInputStream() );
		docElement = document.getDocumentElement();
	}
	
	/**
	 * returns false if the xml is not a jAppDater file
	 * 
	 * @return
	 * @throws Exception
	 */
	public void validateXML() throws Exception
	{
		if( !"JAppDater".equals( docElement.getNodeName()) )
		{
			if( isLocal )
				throw new Exception("Local jAppDater index file is not valid!");
			else
				throw new Exception("Remote jAppDater index file is not valid!");
		}
		
		// wrong version
		if( !Updater.VERSION.equals(docElement.getAttribute("version")) )
		{
			if( isLocal )
				throw new Exception("Local jAppDater index file is has wrong version!");
			else
				throw new Exception("Remote jAppDater index file is has wrong version!");
		}
	}
	
	/**
	 * compareVersions
	 * 
	 * compares the versions of the application (not the files).
	 * if the local file is out dated it returns true.
	 * And update is available
	 * 
	 * @param v
	 * @return
	 */
	public boolean compareVersions(VersionXML v)
	{
		VersionXML local;
		VersionXML remote;
		
		if( v.isLocal )
		{
			local = v;
			remote = this;
		} else {
			local = this;
			remote = v;
		}
		
		String vl = local.getApplicationVersion();
		String vr = remote.getApplicationVersion();
		
		if( vl.compareTo(vr) < 0 )
			return true;
				
		return false;
	}
	
	/**
	 * compareApplicationNames
	 * 
	 * returns true if the names match
	 * 
	 * @param v
	 * @return
	 */
	public boolean compareApplicationNames(VersionXML v)
	{
		String name1 = this.getApplicationName();
		String name2 = v.getApplicationName();
		
		if( name1.equals(name2) )
			return true;
		
		return false;
	}
	
	/**
	 * getApplicationVersion
	 * 
	 * @return
	 */
	public String getApplicationVersion()
	{
		NodeList nodes = docElement.getElementsByTagName("application");
		
		if( nodes == null || nodes.getLength() != 1 )
			return "";
		
		Element ele = (Element) nodes.item(0);
		
		return ele.getAttribute("version");
	}
	
	/**
	 * getApplicationName
	 * 
	 * @return
	 */
	public String getApplicationName()
	{
		NodeList nodes = docElement.getElementsByTagName("application");
		
		if( nodes == null || nodes.getLength() != 1 )
			return "";
		
		Element ele = (Element) nodes.item(0);
		
		return ele.getAttribute("name");
	}
	
	/**
	 * getApplicationURL
	 * 
	 * the server url
	 * 
	 * @return
	 * @throws MalformedURLException 
	 */
	public URL getApplicationURL() 
			throws MalformedURLException
	{
		NodeList nodes = docElement.getElementsByTagName("application");
		
		if( nodes == null || nodes.getLength() != 1 )
			return null;
		
		Element ele = (Element) nodes.item(0);
		
		return new URL(ele.getAttribute("server")+ele.getAttribute("index"));
	}
	
	/**
	 * getServerBasePath
	 * 
	 * @return
	 */
	public String getServerBasePath() 
			throws MalformedURLException
	{
		NodeList nodes = docElement.getElementsByTagName("application");
		
		if( nodes == null || nodes.getLength() != 1 )
			return null;
		
		Element ele = (Element) nodes.item(0);
		
		return ele.getAttribute("server");
	}
	
	/**
	 * getFileNodes
	 * 
	 * returns the nodelist containing all files.
	 * 
	 * @return
	 */
	public NodeList getFileNodes()
	{
		return docElement.getElementsByTagName("file");
	}
	
	
	/**
	 * saveTo
	 * 
	 * @param path
	 * @throws TransformerException
	 */
	public void saveTo(String pathAndFile) 
			throws TransformerException
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(
				pathAndFile
			));
		 
		transformer.transform(source, result);
	}

}

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

/**
 * UpdateResource
 */
public class UpdateResource 
{
	private String src;
	
	private String dest;
	
	private boolean unpack;
	
	/**
	 * Constructor
	 * 
	 * @param src
	 * @param dest
	 * @param unpack
	 */
	public UpdateResource(
			String src, 
			String dest, 
			boolean unpack
		) 
	{
		this.src = src;
		this.dest = dest;
		this.unpack = unpack;
	}
	
	public String getSrcFileName()
	{
		return src.substring((src.lastIndexOf('/')+1), src.length());
	}
	
	public String getSrc() 
	{
		return src;
	}

	public String getDestFileName()
	{
		return dest.substring((dest.lastIndexOf('/')+1), dest.length());
	}
	
	public String getDestPath()
	{
		if( dest.lastIndexOf('/') == -1 )
			return "";
		
		return dest.substring(0,dest.lastIndexOf('/'));
	}
	
	public String getDest() 
	{
		return dest;
	}

	public boolean needUnpack() 
	{
		return unpack;
	}

	

}

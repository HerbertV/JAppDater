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
package jhv.jappdater.event;

import java.util.EventObject;

/**
 * UpdateEvent
 */
public class UpdateEvent
		extends EventObject 
{

	// ============================================================================
	//  Constants
	// ============================================================================

	private static final long serialVersionUID = 7224471515967762095L;
	
	public static final int EXCEPTION = -1;
	
	public static final int FINISHED = 1;
	
	public static final int UPDATE_AVAILABLE = 2;
	
	public static final int NO_UPDATE_AVAILABLE = 3;
	
	
	// ============================================================================
	//  Variables
	// ============================================================================

	private int type = 0;
	
	private Exception exception;
	
	private String version;
	
	
	// ============================================================================
	//  Constructors
	// ============================================================================

	/**
	 * Constructor 1.
	 * creates the finished event
	 * 
	 * @param source
	 */
	public UpdateEvent(Object source) 
	{
		super(source);
		this.type = FINISHED;
	}

	/**
	 * Constructor 2.
	 * creates the exception event
	 * 
	 * @param source
	 * @param e
	 */
	public UpdateEvent(Object source, Exception e) 
	{
		super(source);
		
		this.type = EXCEPTION;
		this.exception = e;
	}
	
	/**
	 * Constructor 3.
	 * returns if there is an update available or not
	 * 
	 * @param source
	 * @param type
	 * @param version
	 */
	public UpdateEvent(Object source, int type, String version) 
	{
		super(source);
		
		this.type = type;
		this.version = version;
	}
	
	
	// ============================================================================
	//  Functions
	// ============================================================================

	/**
	 * getType
	 * 
	 * EXCEPTION, FINISHED, UPDATE_AVAILABLE, NO_UPDATE_AVAILABLE
	 * 
	 * @return
	 */
	public int getType()
	{
		return this.type;
	}
	
	/**
	 * getException
	 * 
	 * @return
	 */
	public Exception getException()
	{
		return this.exception;
	}
	
	/**
	 * getVersion
	 * 
	 * returns the new version from the server
	 * 
	 * @return
	 */
	public String getVersion()
	{
		return this.version;
	}
}

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.Runnable;

import javax.swing.SwingUtilities;

/**
 * UpdateEventProcessor
 */
public class UpdateEventProcessor 
{
	// ============================================================================
	//  Variables
	// ============================================================================

	private List<UpdateEventListener> listeners 
			= new ArrayList<UpdateEventListener>();
	
	// ============================================================================
	//  Constructors
	// ============================================================================

	/**
	 * Constructor
	 */
	public UpdateEventProcessor() 
	{
		
	}
	
	// ============================================================================
	//  Functions
	// ============================================================================

	/**
	 * addEventListener
	 * 
	 * @param listener
	 */
	public synchronized void addUpdateEventListener(
			UpdateEventListener listener
		)  
	{
		listeners.add(listener);
	}

	/**
	 * removeUpdateEventListener
	 * 
	 * @param listener
	 */
	public synchronized void removeUpdateEventListener(
			UpdateEventListener listener
		)
	{
		listeners.remove(listener);
	}
	
	/**
	 * callListeners
	 * 
	 * @param event
	 */
	private synchronized void callListeners(final UpdateEvent event)
	{
		final Iterator<UpdateEventListener> i = listeners.iterator();
	
		// since we call this from a swing worker thread
		SwingUtilities.invokeLater(new Runnable(){
				public void run()
				{
					while( i.hasNext() )
						((UpdateEventListener) i.next()).handleUpdateEvent(event);
				}
			});
	}
	
	/**
	 * fireUpdateEventFinished
	 */
	public synchronized void fireUpdateEventFinished() 
	{
		UpdateEvent event = new UpdateEvent(this);
		callListeners(event);
	}
	
	/**
	 * fireUpdateEventException
	 * 
	 * @param e
	 */
	public synchronized void fireUpdateEventException(Exception e) 
	{
		UpdateEvent event = new UpdateEvent(this,e);
		callListeners(event);
	}
	
	/**
	 * fireUpdateEventUpdateAvailable
	 * 
	 * @param v
	 */
	public synchronized void fireUpdateEventUpdateAvailable(String v) 
	{
		UpdateEvent event = new UpdateEvent(
				this,
				UpdateEvent.UPDATE_AVAILABLE,
				v
			);
		callListeners(event);
	}
	
	/**
	 * fireUpdateEventNoUpdateAvailable
	 */
	public synchronized void fireUpdateEventNoUpdateAvailable() 
	{
		UpdateEvent event = new UpdateEvent(
				this,
				UpdateEvent.NO_UPDATE_AVAILABLE,
				null
			);
		callListeners(event);
	}
	
}

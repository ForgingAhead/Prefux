/*  
 * Copyright (c) 2004-2013 Regents of the University of California.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3.  Neither the name of the University nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * Copyright (c) 2014 Martin Stockhammer
 */
package prefux.controls;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import prefux.visual.VisualItem;

import java.util.EventListener;


/**
 * Listener interface for processing user interface events on a Display.
 * 
 * @author alan newberger
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author Martin Stockhammer
 */
public interface Control extends EventListener
{
    /**
     * Represents the use of the left mouse button
     */
    public static final MouseButton LEFT_MOUSE_BUTTON = MouseButton.PRIMARY;
    /**
     * Represents the use of the middle mouse button
     */
    public static final MouseButton MIDDLE_MOUSE_BUTTON = MouseButton.MIDDLE;
    /**
     * Represents the use of the right mouse button
     */
    public static final MouseButton RIGHT_MOUSE_BUTTON = MouseButton.SECONDARY;
    
    /**
     * Indicates if this Control is currently enabled for item events.
     * 
     * @return true if the control is enabled, false if disabled
     */
    public boolean isItemEventEnabled();
    
    /**
     * Sets the enabled status for item events of this control.
     * @param enabled true to enable the control, false to disable it
     */
    public void setItemEventEnabled(boolean enabled);

    /**
     * Indicates if this Control is currently enabled for non item events.
     *
     * @return
     */
    public boolean isNonItemEventEnabled();

    /**
     * Sets the enabled status for non item events of this control.
     * @param enabled true to enable the control, false to disable it
     */
    public void setNonItemEventEnabled(boolean enabled);

    // -- Actions performed on VisualItems ------------------------------------

    /**
     * Invoked when a event occurs on the visual item.
     */
    public void itemEvent(VisualItem item, Event e);
    
    
    /**
     * Invoked when a event occurs on the display but not on any item.
     * @param e
     */
    public void event(Event e);
  
    /**
     * Returns the event type this control listens to
     * @return
     */
    public EventType<? extends Event> getEventType();
  

} // end of interface ControlListener

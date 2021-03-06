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
package prefux.action;

import java.util.logging.Logger;

import prefux.Visualization;
import prefux.activity.Activity;

/**
 * <p>Actions are building blocks that perform any number of operations on a
 * Visualization, typically processing VisualItems to set various visual
 * attributes. This class is a base implementation for Action instances.
 * Developers can subclass this class and implement the <code>run</code> method
 * to create their own custom Actions.</p>
 * 
 * <p>After instantiating an Action, you should register it with a particular
 * Visualization before running it. Use the
 * {@link prefux.Visualization#putAction(String, Action)} to do this. This
 * will ensure that the Action is configured to use that Visualization. If
 * an Action is part of an {@link ActionList} or {@link ActionSwitch}, it is
 * sufficient to only register that CompositeAction with the Visualization
 * -- all contained Action instances will be configured appropriately. You
 * can then run the Actions using the {@link prefux.Visualization#run(String)}
 * method and other similar methods of the {@link prefux.Visualization} class.
 * </p>
 * 
 * <p>As a subclass of Activity, Actions can be of two kinds. 
 * <i>Run-once</i> action lists have
 * a duration value of zero, and simply run once when scheduled. Actions
 * with a duration greater than zero can be executed multiple times, waiting
 * a specified step time between each execution until the activity has run for
 * its full duration. A duration of Activity.INFINITE will result in a
 * continually re-running Action.</p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public abstract class Action extends Activity {
    
    private final static Logger s_logger 
        = Logger.getLogger(Action.class.getName());
    
    /** A reference to the visualization processed by this Action. */
    protected Visualization m_vis;
    
    /**
     * Creates an action instance with zero duration. This Action will only
     * run once if invoked.
     */
    public Action() {
        this(null);
    }
    
    /**
     * Create a new Action with a specified duration.
     * @param duration the Action duration in milliseconds
     */
    public Action(long duration) {
        super(duration, Activity.DEFAULT_STEP_TIME);
    }
    
    /**
     * Create a new Action with a specified duration and step time.
     * @param duration the Action duration in milliseconds
     * @param stepTime the time to wait between invocation of the Action
     */
    public Action(long duration, long stepTime) {
        super(duration, stepTime);
    }
    
    /**
     * Create a new Action with a specified Visualization and zero duration.
     * @param vis the Visualization this Action should process. If this
     * Action is registered with another Visualization, this value will
     * be overwritten.
     */
    public Action(Visualization vis) {
        this(vis, 0);
    }
    
    /**
     * Create a new Action with a specified Visualization and duration.
     * @param vis the Visualization this Action should process. If this
     * Action is registered with another Visualization, this value will
     * be overwritten.
     * @param duration the Action duration in milliseconds
     */
    public Action(Visualization vis, long duration) {
        super(duration, Activity.DEFAULT_STEP_TIME);
        m_vis = vis;
    }
    
    /**
     * Create a new Action with a specified Visualization, duration and
     * step time.
     * @param vis the Visualization this Action should process. If this
     * Action is registered with another Visualization, this value will
     * be overwritten.
     * @param duration the Action duration in milliseconds
     * @param stepTime the time to wait between invocation of the Action
     */
    public Action(Visualization vis, long duration, long stepTime) {
        super(duration, stepTime);
        m_vis = vis;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Runs this Action, triggering whatever processing this Action performs.
     * Subclass this method to create custom Actions.
     * @param frac the fraction of this Action's duration that has elapsed.
     */
    public abstract void run(double frac);

    /**
     * Runs this Action (as an Activity). Called by the Activity super-class.
     * @see prefux.activity.Activity#run(long)
     */
    protected void run(long elapsedTime) {
        Visualization vis = getVisualization();
        if ( vis != null ) {
            synchronized (vis) {
                run(getPace(elapsedTime));
            }
        } else {
            s_logger.info("Running unsynchronized Action");
            run(getPace(elapsedTime));
        }
    }
    
    /**
     * Return the Visualization processed by this Action.
     * @return the {@link prefux.Visualization} instance.
     */
    public Visualization getVisualization() {
        return m_vis;
    }
    
    /**
     * Set the Visualization processed by this Action.
     * @return the {@link prefux.Visualization} to process.
     */
    public void setVisualization(Visualization vis) {
        m_vis = vis;
    }

} // end of class Action

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
package prefux.action.animate;

import prefux.Display;
import prefux.action.ItemAction;
import prefux.data.util.Point2D;
import prefux.util.MathLib;
import prefux.visual.VisualItem;


/**
 * Animator that interpolates between starting and ending display locations
 * by linearly interpolating between polar coordinates.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class PolarLocationAnimator extends ItemAction {
    
    private Point2D m_anchor = new Point2D();
    private String  m_linear = null;
    
    // temp variables
    private double ax, ay, sx, sy, ex, ey, x, y;
    private double dt1, dt2, sr, st, er, et, r, t, stt, ett;
    
    /**
     * Creates a PolarLocationAnimator that operates on all VisualItems
     * within a Visualization.
     */
    public PolarLocationAnimator() {
        super();
    }
    
    /**
     * Creates a PolarLocationAnimator that operates on VisualItems
     * within the specified group.
     * @param group the data group to process
     */
    public PolarLocationAnimator(String group) {
        super(group);
    }
    
    /**
     * Creates a PolarLocationAnimator that operates on VisualItems
     * within the specified group, while using regular linear interpolation
     * (in Cartesian (x,y) coordinates rather than polar coordinates) for
     * items also contained within the specified linearGroup. 
     * @param group the data group to process
     * @param linearGroup the group of items that should be interpolated
     * in Cartesian (standard x,y) coordinates rather than polar coordinates.
     * Note that this animator will not process any items in 
     * <code>linearGroup</code> that are not also in <code>group</code>.
     */
    public PolarLocationAnimator(String group, String linearGroup) {
        super(group);
        m_linear = linearGroup;
    }

    private void setAnchor() {
        Display d = getVisualization().getDisplay(0);
        m_anchor = new Point2D(d.getWidth()/2,d.getHeight()/2);
        d.getAbsoluteCoordinate(m_anchor);
        ax = m_anchor.getX();
        ay = m_anchor.getY();
    }

    /**
     * @see prefux.action.Action#run(double)
     */
    public void run(double frac) {
        setAnchor();
        super.run(frac);
    }
    
    /**
     * @see prefux.action.ItemAction#process(prefux.visual.VisualItem, double)
     */
    public void process(VisualItem item, double frac) {
        if ( m_linear != null && item.isInGroup(m_linear) ) {
            // perform linear interpolation instead
            double s = item.getStartX();
            item.setX(s + frac*(item.getEndX()-s));
            s = item.getStartY();
            item.setY(s + frac*(item.getEndY()-s));
            return;
        }
        
        // otherwise, interpolate in polar coordinates
        sx = item.getStartX() - ax;
        sy = item.getStartY() - ay;
        ex = item.getEndX() - ax;
        ey = item.getEndY() - ay;
            
        sr = Math.sqrt(sx*sx + sy*sy);
        st = Math.atan2(sy,sx);
            
        er = Math.sqrt(ex*ex + ey*ey);
        et = Math.atan2(ey,ex);
        
        stt = st < 0 ? st+MathLib.TWO_PI : st;
        ett = et < 0 ? et+MathLib.TWO_PI : et;
            
        dt1 = et - st;
        dt2 = ett - stt;
            
        if ( Math.abs(dt1) < Math.abs(dt2) ) {
            t = st + frac * dt1;
        } else {
            t = stt + frac * dt2;
        }
        r = sr + frac * (er - sr);
                        
        x = Math.round(ax + r*Math.cos(t));
        y = Math.round(ay + r*Math.sin(t));
            
        item.setX(x);
        item.setY(y);
    }

} // end of class PolarLocationAnimator

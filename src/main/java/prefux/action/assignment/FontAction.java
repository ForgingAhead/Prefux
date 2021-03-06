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
package prefux.action.assignment;

//import java.awt.Font;
import java.util.logging.Logger;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import prefux.action.EncoderAction;
import prefux.data.expression.Predicate;
import prefux.data.expression.parser.ExpressionParser;
import prefux.util.FontLib;
import prefux.visual.VisualItem;


/**
 * <p>Assignment Action that assigns font values to VisualItems.
 * By default, a FontAction simply sets each VisualItem to use a default 
 * 10 point sans-serif font (10 point sans-serif). Clients can change this
 * default value to achieve uniform font assignment, or can add any number
 * of additional rules for font assignment.
 * Rules are specified by a Predicate instance which, if returning true, will
 * trigger that rule, causing either the provided font value or the result of
 * a delegate FontAction to be applied. Rules are evaluated in the order in
 * which they are added to the FontAction, so earlier rules will have
 * precedence over rules added later.
 * </p>
 * 
 * <p>In addition, subclasses can simply override {@link #getFont(VisualItem)}
 * to achieve custom font assignment. In some cases, this may be the simplest
 * or most flexible approach.</p>
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class FontAction extends EncoderAction {

    protected javafx.scene.text.Font defaultFont = FontLib.getFont("SansSerif",10);
    
    /**
     * Create a new FontAction that processes all data groups.
     */
    public FontAction() {
        super();
    }
    
    /**
     * Create a new FontAction that processes the specified group.
     * @param group the data group to process
     */
    public FontAction(String group) {
        super(group);
    }
    
    /**
     * Create a new FontAction that processes the specified group.
     * @param group the data group to process
     * @param defaultFont the default Font to assign
     */
    public FontAction(String group, Font defaultFont) {
        super(group);
        this.defaultFont = defaultFont;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Set the default font to be assigned to items. Items will be assigned
     * the default font if they do not match any registered rules.
     * @param f the default font to use
     */
    public void setDefaultFont(Font f) {
        defaultFont = f;
    }
    
    /**
     * Get the default font assigned to items.
     * @return the default font
     */
    public Font getDefaultFont() {
        return defaultFont;
    }
    
    /**
     * Add a font mapping rule to this FontAction. VisualItems that match
     * the provided predicate will be assigned the given font value (assuming
     * they do not match an earlier rule).
     * @param p the rule Predicate 
     * @param font the font
     */
    public void add(Predicate p, Font font) {
        super.add(p, font);
    }

    /**
     * Add a font mapping rule to this FontAction. VisualItems that match
     * the provided expression will be assigned the given font value (assuming
     * they do not match an earlier rule). The provided expression String will
     * be parsed to generate the needed rule Predicate.
     * @param expr the expression String, should parse to a Predicate. 
     * @param font the font
     * @throws RuntimeException if the expression does not parse correctly or
     * does not result in a Predicate instance.
     */
    public void add(String expr, Font font) {
        Predicate p = (Predicate)ExpressionParser.parse(expr);
        super.add(p, font);       
    }
    
    /**
     * Add a font mapping rule to this FontAction. VisualItems that match
     * the provided predicate will be assigned the font value returned by
     * the given FontAction's getFont() method.
     * @param p the rule Predicate 
     * @param f the delegate FontAction to use
     */
    public void add(Predicate p, FontAction f) {
        super.add(p, f);
    }

    /**
     * Add a font mapping rule to this FontAction. VisualItems that match
     * the provided expression will be assigned the given font value (assuming
     * they do not match an earlier rule). The provided expression String will
     * be parsed to generate the needed rule Predicate.
     * @param expr the expression String, should parse to a Predicate. 
     * @param f the delegate FontAction to use
     * @throws RuntimeException if the expression does not parse correctly or
     * does not result in a Predicate instance.
     */
    public void add(String expr, FontAction f) {
        Predicate p = (Predicate)ExpressionParser.parse(expr);
        super.add(p, f);
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * @see prefux.action.ItemAction#process(prefux.visual.VisualItem, double)
     */
    public void process(VisualItem item, double frac) {
    	javafx.scene.text.Font f = getFont(item);
        javafx.scene.text.Font o = item.getFont();
        item.setStartFont(o);
        item.setEndFont(f);
        item.setFont(f);
    }
    
    /**
     * Returns the Font to use for a given VisualItem. Subclasses should
     * override this method to perform customized font assignment.
     * @param item the VisualItem for which to get the Font
     * @return the Font for the given item
     */
    public javafx.scene.text.Font getFont(VisualItem item) {
        Object o = lookup(item);
        if ( o != null ) {
            if ( o instanceof FontAction ) {
                return ((FontAction)o).getFont(item);
            } else if ( o instanceof Font ) {
                return (javafx.scene.text.Font)o;
            } else {
                Logger.getLogger(this.getClass().getName())
                    .warning("Unrecognized Object from predicate chain.");
            }
        }
        return defaultFont;   
    }

} // end of class FontAction

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
package prefux.visual.tuple;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Font;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import prefux.Visualization;
import prefux.data.Graph;
import prefux.data.Table;
import prefux.data.Tuple;
import prefux.data.event.EventConstants;
import prefux.data.event.TableListener;
import prefux.data.tuple.TableTuple;
import prefux.data.tuple.TupleSet;
import prefux.data.util.Rectangle2D;
import prefux.render.Renderer;
import prefux.visual.VisualItem;
import prefux.visual.VisualTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * VisualItem implementation that uses data values from a backing VisualTable.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class TableVisualItem extends TableTuple<VisualTable> implements
        VisualItem, TableListener, ChangeListener<Number> {
    private static final Logger log = LogManager.getLogger(TableVisualItem.class);
    private final DoubleProperty xProp = new SimpleDoubleProperty();
    private final DoubleProperty yProp = new SimpleDoubleProperty();
    private final DoubleProperty startXProp = new SimpleDoubleProperty();
    private final DoubleProperty startYProp = new SimpleDoubleProperty();
    private final DoubleProperty endXProp = new SimpleDoubleProperty();
    private final DoubleProperty endYProp = new SimpleDoubleProperty();
    private final DoubleProperty sizeProp = new SimpleDoubleProperty();
    private final IntegerProperty fillColorProp = new SimpleIntegerProperty();
    private final Map<String, DoubleProperty> DOUBLE_PROPERTIES = new HashMap<String, DoubleProperty>() {
        private static final long serialVersionUID = -2801283956649359986L;

        {
            put(VisualItem.X, xProp);
            put(VisualItem.Y, yProp);
            put(VisualItem.STARTX, startXProp);
            put(VisualItem.ENDX, endXProp);
            put(VisualItem.STARTY, startYProp);
            put(VisualItem.ENDY, endYProp);
            put(VisualItem.SIZE, sizeProp);
        }
    };
    private final Map<String, IntegerProperty> INT_PROPERTIES = new HashMap<String, IntegerProperty>() {
        private static final long serialVersionUID = -9113767856174748105L;

        {
            put(VisualItem.FILLCOLOR, fillColorProp);
        }
    };
    private Node node;
    private boolean ignoreTableUpdate = false;

    public TableVisualItem() {
    }

    /**
     * Initialize a new TableVisualItem for the given table and row. This method
     * is used by the appropriate TupleManager instance, and should not be
     * called directly by client code, unless by a client-supplied custom
     * TupleManager.
     *
     * @param table the data Table
     * @param graph ignored by this class
     * @param row   the table row index
     */
    protected void init(VisualTable table, Graph graph, int row) {
        m_table = table;
        m_row = m_table.isValidRow(row) ? row : -1;
        m_table.addTableListener(this);
        // PROPERTIES.entrySet().forEach(en -> {
        // en.getValue().addListener(this);
        // });
    }

    /**
     * @see prefux.visual.VisualItem#getVisualization()
     */
    public Visualization getVisualization() {
        return m_table.getVisualization();
    }

    /**
     * @see prefux.visual.VisualItem#getGroup()
     */
    public String getGroup() {
        return m_table.getGroup();
    }

    /**
     * @see prefux.visual.VisualItem#isInGroup(java.lang.String)
     */
    public boolean isInGroup(String group) {
        return getVisualization().isInGroup(this, group);
    }

    /**
     * @see prefux.visual.VisualItem#getSourceData()
     */
    public TupleSet getSourceData() {
        VisualTable vt = m_table;
        return vt.getVisualization().getSourceData(vt.getGroup());
    }

    /**
     * @see prefux.visual.VisualItem#getSourceTuple()
     */
    public Tuple getSourceTuple() {
        VisualTable vt = m_table;
        return vt.getVisualization().getSourceTuple(this);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("VisualItem[").append(getGroup());
        sbuf.append(",").append(m_row).append(',');
        VisualTable vt = m_table;
        int local = vt.getLocalColumnCount();
        int inherited = vt.getColumnCount() - local;
        for (int i = 0; i < inherited; ++i) {
            if (i > 0)
                sbuf.append(',');
            String name = vt.getColumnName(local + i);
            sbuf.append(name);
            sbuf.append('=');
            if (vt.canGetString(name))
                sbuf.append(vt.getString(m_row, name));
            else
                sbuf.append(vt.get(m_row, name).toString());
        }
        sbuf.append(']');

        return sbuf.toString();
    }

    // ------------------------------------------------------------------------
    // VisualItem Methods

    /**
     * @see prefux.visual.VisualItem#render(java.awt.Graphics2D)
     */
    public void render(Parent g) {
        getRenderer().render(g, this);
    }

    /**
     * @see prefux.visual.VisualItem#getRenderer()
     */
    public Renderer getRenderer() {
        return getVisualization().getRenderer(this);
    }

    /**
     * @see prefux.visual.VisualItem#validateBounds()
     */
    public Rectangle2D validateBounds() {
        if (isValidated())
            return getBounds();

        Visualization v = getVisualization();

        setValidated(true);

        // report damage from the new bounds and return
        Rectangle2D bounds = getBounds();
        v.damageReport(this, bounds);
        return bounds;
    }

    // -- Boolean Flags -------------------------------------------------------

    /**
     * @see prefux.visual.VisualItem#isValidated()
     */
    public boolean isValidated() {
        return m_table.isValidated(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setValidated(boolean)
     */
    public void setValidated(boolean value) {
        m_table.setValidated(m_row, value);
    }

    /**
     * @see prefux.visual.VisualItem#isVisible()
     */
    public boolean isVisible() {
        return m_table.isVisible(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setVisible(boolean)
     */
    public void setVisible(boolean value) {
        m_table.setVisible(m_row, value);
    }

    /**
     * @see prefux.visual.VisualItem#isStartVisible()
     */
    public boolean isStartVisible() {
        return m_table.isStartVisible(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setStartVisible(boolean)
     */
    public void setStartVisible(boolean value) {
        m_table.setStartVisible(m_row, value);
    }

    /**
     * @see prefux.visual.VisualItem#isEndVisible()
     */
    public boolean isEndVisible() {
        return m_table.isEndVisible(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setEndVisible(boolean)
     */
    public void setEndVisible(boolean value) {
        m_table.setEndVisible(m_row, value);
    }

    /**
     * @see prefux.visual.VisualItem#isInteractive()
     */
    public boolean isInteractive() {
        return m_table.isInteractive(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setInteractive(boolean)
     */
    public void setInteractive(boolean value) {
        m_table.setInteractive(m_row, value);
    }


    /**
     * @see prefux.visual.VisualItem#isExpanded()
     */
    public boolean isExpanded() {
        return m_table.isExpanded(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setExpanded(boolean)
     */
    public void setExpanded(boolean value) {
        m_table.setExpanded(m_row, value);
    }

    /**
     * @see prefux.visual.VisualItem#isFixed()
     */
    public boolean isFixed() {
        return m_table.isFixed(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setFixed(boolean)
     */
    public void setFixed(boolean value) {
        m_table.setFixed(m_row, value);
    }

    /**
     * @see prefux.visual.VisualItem#isHighlighted()
     */
    public boolean isHighlighted() {
        return m_table.isHighlighted(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setHighlighted(boolean)
     */
    public void setHighlighted(boolean value) {
        m_table.setHighlighted(m_row, value);
    }

    /**
     * @see prefux.visual.VisualItem#isHover()
     */
    public boolean isHover() {
        return m_table.isHover(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setHover(boolean)
     */
    public void setHover(boolean value) {
        m_table.setHover(m_row, value);
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefux.visual.VisualItem#getX()
     */
    public double getX() {
        return m_table.getX(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setX(double)
     */
    public void setX(double x) {
        m_table.setX(m_row, x);
    }

    /**
     * @see prefux.visual.VisualItem#getY()
     */
    public double getY() {
        return m_table.getY(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setY(double)
     */
    public void setY(double y) {
        m_table.setY(m_row, y);
    }

    /**
     * @see prefux.visual.VisualItem#getStartX()
     */
    public double getStartX() {
        return m_table.getStartX(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setStartX(double)
     */
    public void setStartX(double x) {
        m_table.setStartX(m_row, x);
    }

    /**
     * @see prefux.visual.VisualItem#getStartY()
     */
    public double getStartY() {
        return m_table.getStartY(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setStartY(double)
     */
    public void setStartY(double y) {
        m_table.setStartY(m_row, y);
    }

    /**
     * @see prefux.visual.VisualItem#getEndX()
     */
    public double getEndX() {
        return m_table.getEndX(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setEndX(double)
     */
    public void setEndX(double x) {
        m_table.setEndX(m_row, x);
    }

    /**
     * @see prefux.visual.VisualItem#getEndY()
     */
    public double getEndY() {
        return m_table.getEndY(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setEndY(double)
     */
    public void setEndY(double y) {
        m_table.setEndY(m_row, y);
    }

    /**
     * @see prefux.visual.VisualItem#getBounds()
     */
    public Rectangle2D getBounds() {
        if (!isValidated()) {
            return validateBounds();
        }
        return m_table.getBounds(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setBounds(double, double, double, double)
     */
    public void setBounds(double x, double y, double w, double h) {
        m_table.setBounds(m_row, x, y, w, h);
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefux.visual.VisualItem#getStrokeColor()
     */
    public int getStrokeColor() {
        return m_table.getStrokeColor(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setStrokeColor(int)
     */
    public void setStrokeColor(int color) {
        m_table.setStrokeColor(m_row, color);
    }

    /**
     * @see prefux.visual.VisualItem#getStartStrokeColor()
     */
    public int getStartStrokeColor() {
        return m_table.getStartStrokeColor(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setStartStrokeColor(int)
     */
    public void setStartStrokeColor(int color) {
        m_table.setStartStrokeColor(m_row, color);
    }

    /**
     * @see prefux.visual.VisualItem#getEndStrokeColor()
     */
    public int getEndStrokeColor() {
        return m_table.getEndStrokeColor(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setEndStrokeColor(int)
     */
    public void setEndStrokeColor(int color) {
        m_table.setEndStrokeColor(m_row, color);
    }

    /**
     * @see prefux.visual.VisualItem#getFillColor()
     */
    public int getFillColor() {
        return m_table.getFillColor(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setFillColor(int)
     */
    public void setFillColor(int color) {
        m_table.setFillColor(m_row, color);
    }

    /**
     * @see prefux.visual.VisualItem#getStartFillColor()
     */
    public int getStartFillColor() {
        return m_table.getStartFillColor(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setStartFillColor(int)
     */
    public void setStartFillColor(int color) {
        m_table.setStartFillColor(m_row, color);
    }

    /**
     * @see prefux.visual.VisualItem#getEndFillColor()
     */
    public int getEndFillColor() {
        return m_table.getEndFillColor(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setEndFillColor(int)
     */
    public void setEndFillColor(int color) {
        m_table.setEndFillColor(m_row, color);
    }

    /**
     * @see prefux.visual.VisualItem#getTextColor()
     */
    public int getTextColor() {
        return m_table.getTextColor(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setTextColor(int)
     */
    public void setTextColor(int color) {
        m_table.setTextColor(m_row, color);
    }

    /**
     * @see prefux.visual.VisualItem#getStartTextColor()
     */
    public int getStartTextColor() {
        return m_table.getStartTextColor(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setStartTextColor(int)
     */
    public void setStartTextColor(int color) {
        m_table.setStartTextColor(m_row, color);
    }

    /**
     * @see prefux.visual.VisualItem#getEndTextColor()
     */
    public int getEndTextColor() {
        return m_table.getEndTextColor(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setEndTextColor(int)
     */
    public void setEndTextColor(int color) {
        m_table.setEndTextColor(m_row, color);
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefux.visual.VisualItem#getSize()
     */
    public double getSize() {
        return m_table.getSize(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setSize(double)
     */
    public void setSize(double size) {
        m_table.setSize(m_row, size);
    }

    /**
     * @see prefux.visual.VisualItem#getStartSize()
     */
    public double getStartSize() {
        return m_table.getStartSize(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setStartSize(double)
     */
    public void setStartSize(double size) {
        m_table.setStartSize(m_row, size);
    }

    /**
     * @see prefux.visual.VisualItem#getEndSize()
     */
    public double getEndSize() {
        return m_table.getEndSize(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setEndSize(double)
     */
    public void setEndSize(double size) {
        m_table.setEndSize(m_row, size);
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefux.visual.VisualItem#getShape()
     */
    public int getShape() {
        return m_table.getShape(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setShape(int)
     */
    public void setShape(int shape) {
        m_table.setShape(m_row, shape);
    }

    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------

    /**
     * @see prefux.visual.VisualItem#getFont()
     */
    public Font getFont() {
        return m_table.getFont(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setFont(java.awt.Font)
     */
    public void setFont(Font font) {
        m_table.setFont(m_row, font);
    }

    /**
     * @see prefux.visual.VisualItem#getStartFont()
     */
    public Font getStartFont() {
        return m_table.getStartFont(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setStartFont(java.awt.Font)
     */
    public void setStartFont(Font font) {
        m_table.setStartFont(m_row, font);
    }

    /**
     * @see prefux.visual.VisualItem#getEndFont()
     */
    public Font getEndFont() {
        return m_table.getEndFont(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setEndFont(java.awt.Font)
     */
    public void setEndFont(Font font) {
        m_table.setEndFont(m_row, font);
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefux.visual.VisualItem#getDOI()
     */
    public double getDOI() {
        return m_table.getDOI(m_row);
    }

    /**
     * @see prefux.visual.VisualItem#setDOI(double)
     */
    public void setDOI(double doi) {
        m_table.setDOI(m_row, doi);
    }

    @Override
    public String getStyle() {
        return m_table.getStyle(m_row);
    }

    public void setStyle(String style) {
        m_table.setStyle(m_row, style);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void setNode(Node node) {
        this.node = node;

    }

    @Override
    public DoubleProperty xProperty() {
        return xProp;
    }

    @Override
    public DoubleProperty yProperty() {
        return yProp;
    }

    @Override
    public DoubleProperty startXProperty() {
        return startXProp;
    }

    @Override
    public DoubleProperty startYProperty() {
        return startYProp;
    }

    @Override
    public DoubleProperty endXProperty() {
        return endXProp;
    }

    @Override
    public DoubleProperty endYProperty() {
        return endYProp;
    }

    @Override
    public DoubleProperty sizeProperty() {
        return sizeProp;
    }

    @Override
    public IntegerProperty fillColorProperty() {
        return fillColorProp;
    }

    @Override
    public void tableChanged(Table t, int start, int end, int col, int type) {
        if (!ignoreTableUpdate && type == EventConstants.UPDATE
                && (start == m_row) && (start == end)) {
            String colName = getColumnName(col);
            if (DOUBLE_PROPERTIES.containsKey(colName)) {
                Platform.runLater(() -> {
                    DOUBLE_PROPERTIES.get(colName).set(t.getDouble(m_row, col));
                });
            } else if (INT_PROPERTIES.containsKey(colName)) {
                Platform.runLater(() -> {
                    INT_PROPERTIES.get(colName).set(t.getInt(m_row, col));
                });
            }
        }

    }

    @Override
    public void changed(ObservableValue<? extends Number> observable,
                        Number oldValue, Number newValue) {
        ignoreTableUpdate = true;
        try {
            log.debug("Value changed " + observable + " / " + oldValue + " / "
                    + newValue);
            boolean found = false;
            for (Entry<String, DoubleProperty> en : DOUBLE_PROPERTIES
                    .entrySet()) {
                if (observable == en.getValue()) {
                    log.debug("Property found");
                    setDouble(en.getKey(), newValue.doubleValue());
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (Entry<String, IntegerProperty> en : INT_PROPERTIES
                        .entrySet()) {
                    if (observable == en.getValue()) {
                        log.debug("Property found");
                        setInt(en.getKey(), newValue.intValue());
                        found = true;
                        break;
                    }
                }
            }
        } finally {
            ignoreTableUpdate = false;
        }

    }

} // end of class TableVisualItem

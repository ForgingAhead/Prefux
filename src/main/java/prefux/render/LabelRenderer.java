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
package prefux.render;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Translate;
import prefux.Constants;
import prefux.visual.VisualItem;

/**
 * Renderer that draws a label, which consists of a text string, an image, or
 * both.
 * <p>
 * <p>
 * When created using the default constructor, the renderer attempts to use text
 * from the "label" field. To use a different field, use the appropriate
 * constructor or use the {@link #setTextField(String)} method. To perform
 * custom String selection, subclass this Renderer and override the
 * {@link #getText(VisualItem)} method. When the text field is <code>null</code>
 * , no text label will be shown. Labels can span multiple lines of text,
 * determined by the presence of newline characters ('\n') within the text
 * string.
 * </p>
 * <p>
 * <p>
 * By default, no image is shown. To show an image, the image field needs to be
 * set, either using the appropriate constructor or the
 * {@link #setImageField(String)} method. The value of the image field should be
 * a text string indicating the location of the image file to use. The string
 * should be either a URL, a file located on the current classpath, or a file on
 * the local filesystem. If found, the image will be managed internally by an
 * {@link ImageFactory} instance, which maintains a cache of loaded images.
 * </p>
 * <p>
 * <p>
 * The position of the image relative to text can be set using the
 * {@link #setImagePosition(int)} method. Images can be placed to the left,
 * right, above, or below the text. The horizontal and vertical alignments of
 * either the text or the image can be set explicitly using the appropriate
 * methods of this class (e.g., {@link #setHorizontalTextAlignment(int)}). By
 * default, both the text and images are centered along both the horizontal and
 * vertical directions.
 * </p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class LabelRenderer extends AbstractShapeRenderer {

    public static final String DEFAULT_STYLE_CLASS = "prefux-label";
    protected FxImageFactory m_images = null;
    protected String m_delim = "\n";
    protected String m_labelName = "label";
    protected String m_imageName = null;
    protected int m_xAlign = Constants.CENTER;
    protected int m_yAlign = Constants.CENTER;
    protected int m_hTextAlign = Constants.CENTER;
    protected int m_vTextAlign = Constants.CENTER;
    protected int m_hImageAlign = Constants.CENTER;
    protected int m_vImageAlign = Constants.CENTER;
    protected int m_imagePos = Constants.LEFT;
    protected int m_horizBorder = 2;
    protected int m_vertBorder = 0;
    protected int m_imageMargin = 2;
    protected int m_arcWidth = 0;
    protected int m_arcHeight = 0;
    protected int m_maxTextWidth = -1;
    protected String m_text; // label text
    protected Label txt;
    protected ImageView img;
    protected Pane pane;

    /**
     * Create a new LabelRenderer. By default the field "label" is used as the
     * field name for looking up text, and no image is used.
     */
    public LabelRenderer() {
    }

    /**
     * Create a new LabelRenderer. Draws a text label using the given text data
     * field and does not draw an image.
     *
     * @param textField the data field for the text label.
     */
    public LabelRenderer(String textField) {
        this.setTextField(textField);
    }

    /**
     * Create a new LabelRenderer. Draws a text label using the given text data
     * field, and draws the image at the location reported by the given image
     * data field.
     *
     * @param textField  the data field for the text label
     * @param imageField the data field for the image location. This value in the data
     *                   field should be a URL, a file within the current classpath, a
     *                   file on the filesystem, or null for no image. If the
     *                   <code>imageField</code> parameter is null, no images at all
     *                   will be drawn.
     */
    public LabelRenderer(String textField, String imageField) {
        setTextField(textField);
        setImageField(imageField);
    }

    // ------------------------------------------------------------------------

    /**
     * Get the field name to use for text labels.
     *
     * @return the data field for text labels, or null for no text
     */
    public String getTextField() {
        return m_labelName;
    }

    /**
     * Set the field name to use for text labels.
     *
     * @param textField the data field for text labels, or null for no text
     */
    public void setTextField(String textField) {
        m_labelName = textField;
    }

    /**
     * Sets the maximum width that should be allowed of the text label. A value
     * of -1 specifies no limit (this is the default).
     *
     * @param maxWidth the maximum width of the text or -1 for no limit
     */
    public void setMaxTextWidth(int maxWidth) {
        m_maxTextWidth = maxWidth;
    }

    /**
     * Returns the text to draw. Subclasses can override this class to perform
     * custom text selection.
     *
     * @param item the item to represent as a <code>String</code>
     * @return a <code>String</code> to draw
     */
    protected String getText(VisualItem item) {
        String s = null;
        if (item.canGetString(m_labelName)) {
            return item.getString(m_labelName);
        }
        return s;
    }

    // ------------------------------------------------------------------------
    // Image Handling

    /**
     * Get the data field for image locations. The value stored in the data
     * field should be a URL, a file within the current classpath, a file on the
     * filesystem, or null for no image.
     *
     * @return the data field for image locations, or null for no images
     */
    public String getImageField() {
        return m_imageName;
    }

    /**
     * Set the data field for image locations. The value stored in the data
     * field should be a URL, a file within the current classpath, a file on the
     * filesystem, or null for no image. If the <code>imageField</code>
     * parameter is null, no images at all will be drawn.
     *
     * @param imageField the data field for image locations, or null for no images
     */
    public void setImageField(String imageField) {
        if (imageField != null)
            m_images = new FxImageFactory();
        m_imageName = imageField;
    }

    /**
     * Sets the maximum image dimensions, used to control scaling of loaded
     * images. This scaling is enforced immediately upon loading of the image.
     *
     * @param width  the maximum width of images (-1 for no limit)
     * @param height the maximum height of images (-1 for no limit)
     */
    public void setMaxImageDimensions(int width, int height) {
        if (m_images == null)
            m_images = new FxImageFactory();
        m_images.setMaxImageDimensions(width, height);
    }

    /**
     * Returns a location string for the image to draw. Subclasses can override
     * this class to perform custom image selection beyond looking up the value
     * from a data field.
     *
     * @param item the item for which to select an image to draw
     * @return the location string for the image to use, or null for no image
     */
    protected String getImageLocation(VisualItem item) {
        return item.canGetString(m_imageName) ? item.getString(m_imageName)
                : null;
    }

    /**
     * Get the image to include in the label for the given VisualItem.
     *
     * @param item the item to get an image for
     * @return the image for the item, or null for no image
     */
    protected Image getImage(VisualItem item) {
        String imageLoc = getImageLocation(item);
        return (imageLoc == null ? null : m_images.getImage(imageLoc));
    }

    /**
     * @see prefux.render.AbstractShapeRenderer#getRawShape(prefux.visual.VisualItem)
     */
    protected Node getRawShape(VisualItem item, boolean bind) {
        pane = new StackPane();
        m_text = getText(item);
        txt = new Label(m_text);
        img = new ImageView(getImage(item));
        pane.getChildren().add(img);
        pane.getChildren().add(txt);
        if (bind) {
            Platform.runLater(() -> {
                pane.layoutXProperty().bind(item.xProperty());
                pane.layoutYProperty().bind(item.yProperty());
            });
        }
        return pane;
    }

    /**
     * Returns the image factory used by this renderer.
     *
     * @return the image factory
     */
    public FxImageFactory getImageFactory() {
        if (m_images == null)
            m_images = new FxImageFactory();
        return m_images;
    }

    /**
     * Sets the image factory used by this renderer.
     *
     * @param ifact the image factory
     */
    public void setImageFactory(FxImageFactory ifact) {
        m_images = ifact;
    }

    // ------------------------------------------------------------------------

    /**
     * Get the horizontal text alignment within the layout. One of
     * {@link prefux.Constants#LEFT}, {@link prefux.Constants#RIGHT}, or
     * {@link prefux.Constants#CENTER}. The default is centered text.
     *
     * @return the horizontal text alignment
     */
    public int getHorizontalTextAlignment() {
        return m_hTextAlign;
    }

    /**
     * Set the horizontal text alignment within the layout. One of
     * {@link prefux.Constants#LEFT}, {@link prefux.Constants#RIGHT}, or
     * {@link prefux.Constants#CENTER}. The default is centered text.
     *
     * @param halign the desired horizontal text alignment
     */
    public void setHorizontalTextAlignment(int halign) {
        if (halign != Constants.LEFT && halign != Constants.RIGHT
                && halign != Constants.CENTER)
            throw new IllegalArgumentException(
                    "Illegal horizontal text alignment value.");
        m_hTextAlign = halign;
    }

    /**
     * Get the vertical text alignment within the layout. One of
     * {@link prefux.Constants#TOP}, {@link prefux.Constants#BOTTOM}, or
     * {@link prefux.Constants#CENTER}. The default is centered text.
     *
     * @return the vertical text alignment
     */
    public int getVerticalTextAlignment() {
        return m_vTextAlign;
    }

    /**
     * Set the vertical text alignment within the layout. One of
     * {@link prefux.Constants#TOP}, {@link prefux.Constants#BOTTOM}, or
     * {@link prefux.Constants#CENTER}. The default is centered text.
     *
     * @param valign the desired vertical text alignment
     */
    public void setVerticalTextAlignment(int valign) {
        if (valign != Constants.TOP && valign != Constants.BOTTOM
                && valign != Constants.CENTER)
            throw new IllegalArgumentException(
                    "Illegal vertical text alignment value.");
        m_vTextAlign = valign;
    }

    /**
     * Get the horizontal image alignment within the layout. One of
     * {@link prefux.Constants#LEFT}, {@link prefux.Constants#RIGHT}, or
     * {@link prefux.Constants#CENTER}. The default is a centered image.
     *
     * @return the horizontal image alignment
     */
    public int getHorizontalImageAlignment() {
        return m_hImageAlign;
    }

    /**
     * Set the horizontal image alignment within the layout. One of
     * {@link prefux.Constants#LEFT}, {@link prefux.Constants#RIGHT}, or
     * {@link prefux.Constants#CENTER}. The default is a centered image.
     *
     * @param halign the desired horizontal image alignment
     */
    public void setHorizontalImageAlignment(int halign) {
        if (halign != Constants.LEFT && halign != Constants.RIGHT
                && halign != Constants.CENTER)
            throw new IllegalArgumentException(
                    "Illegal horizontal text alignment value.");
        m_hImageAlign = halign;
    }

    /**
     * Get the vertical image alignment within the layout. One of
     * {@link prefux.Constants#TOP}, {@link prefux.Constants#BOTTOM}, or
     * {@link prefux.Constants#CENTER}. The default is a centered image.
     *
     * @return the vertical image alignment
     */
    public int getVerticalImageAlignment() {
        return m_vImageAlign;
    }

    /**
     * Set the vertical image alignment within the layout. One of
     * {@link prefux.Constants#TOP}, {@link prefux.Constants#BOTTOM}, or
     * {@link prefux.Constants#CENTER}. The default is a centered image.
     *
     * @param valign the desired vertical image alignment
     */
    public void setVerticalImageAlignment(int valign) {
        if (valign != Constants.TOP && valign != Constants.BOTTOM
                && valign != Constants.CENTER)
            throw new IllegalArgumentException(
                    "Illegal vertical text alignment value.");
        m_vImageAlign = valign;
    }

    /**
     * Get the image position, determining where the image is placed with
     * respect to the text. One of {@link Constants#LEFT},
     * {@link Constants#RIGHT}, {@link Constants#TOP}, or
     * {@link Constants#BOTTOM}. The default is left.
     *
     * @return the image position
     */
    public int getImagePosition() {
        return m_imagePos;
    }

    /**
     * Set the image position, determining where the image is placed with
     * respect to the text. One of {@link Constants#LEFT},
     * {@link Constants#RIGHT}, {@link Constants#TOP}, or
     * {@link Constants#BOTTOM}. The default is left.
     *
     * @param pos the desired image position
     */
    public void setImagePosition(int pos) {
        if (pos != Constants.TOP && pos != Constants.BOTTOM
                && pos != Constants.LEFT && pos != Constants.RIGHT
                && pos != Constants.CENTER)
            throw new IllegalArgumentException("Illegal image position value.");
        m_imagePos = pos;
    }

    // ------------------------------------------------------------------------

    /**
     * Get the horizontal alignment of this node with respect to its x, y
     * coordinates.
     *
     * @return the horizontal alignment, one of {@link prefux.Constants#LEFT},
     * {@link prefux.Constants#RIGHT}, or
     * {@link prefux.Constants#CENTER}.
     */
    public int getHorizontalAlignment() {
        return m_xAlign;
    }

    /**
     * Set the horizontal alignment of this node with respect to its x, y
     * coordinates.
     *
     * @param align the horizontal alignment, one of {@link prefux.Constants#LEFT}
     *              , {@link prefux.Constants#RIGHT}, or
     *              {@link prefux.Constants#CENTER}.
     */
    public void setHorizontalAlignment(int align) {
        m_xAlign = align;
    }

    /**
     * Get the vertical alignment of this node with respect to its x, y
     * coordinates.
     *
     * @return the vertical alignment, one of {@link prefux.Constants#TOP},
     * {@link prefux.Constants#BOTTOM}, or
     * {@link prefux.Constants#CENTER}.
     */
    public int getVerticalAlignment() {
        return m_yAlign;
    }

    /**
     * Set the vertical alignment of this node with respect to its x, y
     * coordinates.
     *
     * @param align the vertical alignment, one of {@link prefux.Constants#TOP},
     *              {@link prefux.Constants#BOTTOM}, or
     *              {@link prefux.Constants#CENTER}.
     */
    public void setVerticalAlignment(int align) {
        m_yAlign = align;
    }

    /**
     * Returns the amount of padding in pixels between the content and the
     * border of this item along the horizontal dimension.
     *
     * @return the horizontal padding
     */
    public int getHorizontalPadding() {
        return m_horizBorder;
    }

    /**
     * Sets the amount of padding in pixels between the content and the border
     * of this item along the horizontal dimension.
     *
     * @param xpad the horizontal padding to set
     */
    public void setHorizontalPadding(int xpad) {
        m_horizBorder = xpad;
    }

    /**
     * Returns the amount of padding in pixels between the content and the
     * border of this item along the vertical dimension.
     *
     * @return the vertical padding
     */
    public int getVerticalPadding() {
        return m_vertBorder;
    }

    /**
     * Sets the amount of padding in pixels between the content and the border
     * of this item along the vertical dimension.
     *
     * @param ypad the vertical padding
     */
    public void setVerticalPadding(int ypad) {
        m_vertBorder = ypad;
    }

    /**
     * Get the padding, in pixels, between an image and text.
     *
     * @return the padding between an image and text
     */
    public int getImageTextPadding() {
        return m_imageMargin;
    }

    /**
     * Set the padding, in pixels, between an image and text.
     *
     * @param pad the padding to use between an image and text
     */
    public void setImageTextPadding(int pad) {
        m_imageMargin = pad;
    }

    @Override
    public String getDefaultStyle() {
        return DEFAULT_STYLE_CLASS;
    }

    public void translate(double x, double y) {
        addTransform(new Translate(x, y));

    }

} // end of class LabelRenderer

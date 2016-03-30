package pv.render.awt.gl;


import com.jogamp.opengl.GL2;

public class FBOTexture {

    public final int fid;
    public final int tid;
    public final int width;
    public final int height;
    public final int format = GL2.GL_RGBA;

    public FBOTexture(int frameBufferID, int textureID, int width, int height) {
        this.fid = frameBufferID;
        this.tid = textureID;
        this.width = width;
        this.height = height;
    }

    public boolean init(GL2 gl) {
        //gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tid);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, format, width, height, 0, format,
                GL2.GL_UNSIGNED_BYTE, null);
        //BufferUtil.newByteBuffer(width*height*4));
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_TEXTURE_2D, tid, 0);

        gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

        int status = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
        if (status == GL2.GL_FRAMEBUFFER_COMPLETE) {
            gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
            return true;
        } else {
            return false;
        }
    }

    public void begin(GL2 gl, boolean clear) {
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, fid);
        //gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
        //gl.glViewport(0, 0, width, height);
        if (clear) {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        }

        GLRenderer r = GLRenderer.instance();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, r.width(), 0, r.height(), 0, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public void end(GL2 gl) {
        GLRenderer r = GLRenderer.instance();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, r.width(), r.height(), 0, 0, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        //gl.glPopAttrib();
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
    }

    public void draw(GL2 gl, float x, float y) {
        draw(gl, x, y, width, height);
    }

    public void draw(GL2 gl, float x, float y, float w, float h) {
        gl.glColor4f(1, 1, 1, 1);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tid);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0, 0);
        gl.glVertex2f(x + 0, y + 0);
        gl.glTexCoord2f(1, 0);
        gl.glVertex2f(x + w, y + 0);
        gl.glTexCoord2f(1, 1);
        gl.glVertex2f(x + w, y + h);
        gl.glTexCoord2f(0, 1);
        gl.glVertex2f(x + 0, y + h);
        gl.glEnd();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }

    public void bind(GL2 gl) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tid);
        //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, wrap_s);
        //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, wrap_t);
        //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, mag_filter);
        //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, min_filter);
        //gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, mode);
        //gl.glEnable(GL2.GL_TEXTURE_2D);
    }

    public void unbind(GL2 gl) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }

    public void free(GL2 gl) {
        int[] ids = {tid};
        gl.glDeleteTextures(1, ids, 0);
    }
}

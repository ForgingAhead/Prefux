package pv.render.awt.gl;


import com.jogamp.opengl.GL2;

public class FrameBuffer {

    public final int id;
    public final int[] ids = {0};

    public FrameBuffer(GL2 gl) {
        gl.glGenFramebuffers(1, ids, 0);
        id = ids[0];
    }

    public FBOTexture createTexture(GL2 gl, int width, int height) {
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, id);
        gl.glGenTextures(1, ids, 0);
        int tid = ids[0];
        return new FBOTexture(id, tid, width, height);
    }
}

package pv.render.awt.gl;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import pv.util.IOLib;


public class Images {

	private static Images s_instance = new Images();
	public static Images instance() { return s_instance; }
	
	private int _maxThreads = 8; // TODO move to properties
	private int _threadCount = 0;
	private Map<String,ImageEntry> _map = new HashMap<String,ImageEntry>();
	private LinkedList<ImageEntry> _queue = new LinkedList<ImageEntry>();
	
	public Images() {
		
	}
	
	public Images(int maxThreads) {
		_maxThreads = maxThreads;
	}
	
	// -----
	
	public BufferedImage getImage(String location) {
		ImageEntry e = get(location);
		return (e==null || !e.loaded) ? null : e.image;
	}
	
	public TextureData getTextureData(String location) {
		ImageEntry e = get(location);
		return (e==null || !e.loaded) ? null : e.tdata;
	}
	
	public Texture getTexture(String location, GL2 gl) {
		ImageEntry e = get(location);
		return (e==null || !e.loaded) ? null : e.texture(gl);
	}
	
	public ImageEntry get(String location) {
		ImageEntry e = _map.get(location);
		if (e == null) {
			_map.put(location, e = new ImageEntry(location));
			load(e);
		}
		return e;
	}
	
	public boolean remove(String location, GL2 gl) {
		ImageEntry e = _map.remove(location);
		if (e != null) {
			e.dispose(gl);
			return true;
		} else {
			return false;
		}
	}
	
	private void load(ImageEntry e) {
		boolean newThread = false;
		synchronized (_queue) {
			_queue.add(e);
			newThread = (_threadCount < _maxThreads);
		}
		if (newThread) new Loader().start();
	}
	
	public static class ImageEntry {
		boolean loaded = false;
		String location = null;
		
		BufferedImage image = null;
		TextureData tdata = null;
		Texture texture = null;
		
		public ImageEntry(String location) {
			this.location = location;
		}
		
		public Texture texture(GL2 gl) {
			if (texture == null) {
				texture = TextureIO.newTexture(tdata);
				texture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
				texture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
			}
			return texture;
		}
		
		public void dispose(GL2 gl) {
			if (texture != null) texture.destroy(gl);
		}
	}
	
	public class Loader extends Thread {
		public void run() {
			while (true) {
				ImageEntry e = null;
				synchronized (_queue) {
					if (_queue.isEmpty()) {
						_threadCount--;
						return;
					}
					e = _queue.removeFirst();
				}
				try {
					URL url = IOLib.urlFromString(e.location);
					e.image = ImageIO.read(url);
					e.tdata = TextureIO.newTextureData(GLProfile.getDefault(),url, true, "img");
					e.loaded = true;
				} catch (Exception ex) {
					e.image = null;
					e.tdata = null;
					e.loaded = false;
				}
			}
			
		}
	}
	
}

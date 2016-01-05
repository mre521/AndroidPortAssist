package jar;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.objectweb.asm.ClassReader;


public abstract class JarEntries {
	
	public JarEntries(File file) throws IOException {
		JarInputStream jis = new JarInputStream(new FileInputStream(file));
		
		readEntries(jis);
		
		jis.close();
	}
	
	public JarEntries(JarInputStream jis) throws IOException {
		readEntries(jis);
	}
	
	private void readEntries(JarInputStream jis) throws IOException {
		JarEntry je;

		while((je=jis.getNextJarEntry()) != null) {
			processJarEntry(jis, je);
		}
	}
	
	abstract protected void processJarEntry(InputStream is, JarEntry je) throws IOException;
	
	/* Idea from http://stackoverflow.com/a/1264737 */
	protected static byte[] readFully(InputStream is) throws IOException {
		final int BLOCKSIZE = 8192;
		byte[] block = new byte[BLOCKSIZE];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		int len;
		while((len=is.read(block)) != -1) {
			baos.write(block, 0, len);
		}
		
		baos.flush();
		return baos.toByteArray();
	}
	
}

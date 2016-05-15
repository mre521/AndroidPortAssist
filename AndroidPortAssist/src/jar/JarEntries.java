/*******************************************************************************
 *     AndroidPortAssist, a Java application porting tool
 *     Copyright (C) 2016 
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
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
	
	public JarEntries() {
		
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

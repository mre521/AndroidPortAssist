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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.objectweb.asm.ClassReader;

public class JarClasses extends JarEntries {

	public JarClasses(File file) throws IOException {
		super(file);
	}
	
	public JarClasses(Map<String, Entry> entries) {
		this.entries = entries;
	}

	@Override
	protected void processJarEntry(InputStream is, JarEntry je) throws IOException {
		String name;
		byte[] data;
		name = je.getName();
		if(name.endsWith(".class")) {
			data = readFully(is);
			name = name.replace(".class", "");
			if(entries == null) entries = new HashMap<String,Entry>();
			entries.put(name, new Entry(data,name));
		}
	}
	
	public void writeJar(File file) throws IOException {
		JarOutputStream jos = new JarOutputStream(new FileOutputStream(file));
		
		JarEntry je;
		for(Entry entry: entries.values()) {
			je = new JarEntry(entry.getName() + ".class");
			je.setSize(entry.getData().length);
			jos.putNextEntry(je);
			
			jos.write(entry.getData());
		}
		
		jos.close();
	}
	
	public boolean containsClass(String name) {
		return entries.containsKey(name);
	}
	
	public Entry getEntry(String name) {
		return entries.get(name);
	}
	
	public Entry[] getEntries() {
		return entries.values().toArray(new Entry[0]);
	}
	
	public static class Entry {
		public Entry(byte[] data, String name) {
			this.name = name;
			this.data = data;
			
			classReader = new ClassReader(data);
		}
	
		public String getName() {
			return name;
		}
		public byte[] getData() {
			return data;
		}
		public ClassReader getClassReader() {
			return classReader;
		}

		protected String name;
		protected byte[] data;
		protected ClassReader classReader;
			
	}
	
	Map<String, Entry> entries;
}

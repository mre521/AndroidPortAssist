package jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

import org.objectweb.asm.ClassReader;

public class JarClasses extends JarEntries {

	public JarClasses(File file) throws IOException {
		super(file);
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
	
	public boolean containsClass(String name) {
		return entries.containsKey(name);
	}
	
	public Entry getEntry(String name) {
		return entries.get(name);
	}
	
	public Entry[] getEntries() {
		return entries.values().toArray(new Entry[0]);
	}
	
	public class Entry {
		Entry(byte[] data, String name) {
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

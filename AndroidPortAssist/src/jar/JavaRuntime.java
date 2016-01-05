package jar;

import java.io.File;
import java.io.IOException;

public class JavaRuntime extends JarClasses {

	public JavaRuntime(File directory) throws IOException {
		super(new File(directory.getPath() + LIBDIR + RUNTIMEJAR));
		//this.version = version;
		this.directory = directory;
	}
	
	public int getVersion() {
		return version;
	}
	
	int version;
	File directory;
	
	private static final String LIBDIR = File.separator + "lib" + File.separator;
	private static final String RUNTIMEJAR = "rt.jar";

}

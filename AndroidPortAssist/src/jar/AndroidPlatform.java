package jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class AndroidPlatform extends JarClasses {
	
	public AndroidPlatform(File directory, int version) throws IOException {
		super(new File(directory.getPath() + File.separator + ANDROIDJAR));
		this.version = version;
		this.directory = directory;
	}
	
	public int getVersion() {
		return version;
	}
	
	int version;
	File directory;
	
	private static final String ANDROIDJAR = "android.jar";
}

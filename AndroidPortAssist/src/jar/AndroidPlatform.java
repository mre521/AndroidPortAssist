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

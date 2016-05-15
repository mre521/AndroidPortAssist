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

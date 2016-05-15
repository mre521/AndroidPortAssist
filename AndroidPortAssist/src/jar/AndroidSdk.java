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
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AndroidSdk {
	
	public AndroidSdk(String path) throws FileNotFoundException {
		sdkDir = new File(path);
		
		if(!sdkDir.exists() || !sdkDir.isDirectory()) {
			throw new FileNotFoundException("Provided SDK directory does not exist");
		}
		
		StringBuilder pathBuilder = new StringBuilder(path);
		if(!path.endsWith(File.separator)) {
			pathBuilder.append(File.separator);
		}
		pathBuilder.append(PLATFORMS_DIR_NAME);
		pathBuilder.append(File.separator);
		
		platformsDir = new File(pathBuilder.toString());
		if(!platformsDir.exists() || !platformsDir.isDirectory()) {
			throw new FileNotFoundException("SDK platforms directory does not exist");
		}
		
		FileFilter platformDirFilter = new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory() && arg0.getName().startsWith(PLATFORM_PREFIX);
			}
		};
		
		File[] dirs = platformsDir.listFiles(platformDirFilter);
		if(dirs.length == 0) {
			throw new FileNotFoundException("No Android platforms available");
		}
		
		int version;
		platforms = new HashMap<Integer,File>();
		for(File dir: dirs) {
			version = Integer.parseInt(dir.getName().replace(PLATFORM_PREFIX, ""));
			platforms.put(version, dir);
		}
	}
	
	public boolean hasPlatform(int version) {
		return platforms.containsKey(version);
	}
	
	public AndroidPlatform getPlatform(int version) throws IOException {
		if(!hasPlatform(version)) {
			throw new FileNotFoundException("SDK does not have platform version " + version);
		}
		
		return new AndroidPlatform(platforms.get(version),version);
	}
	
	String sdkPath;
	File sdkDir;
	
	String platformsPath;
	File platformsDir;
	
	Map<Integer,File> platforms;
	
	private static final String PLATFORMS_DIR_NAME = "platforms";
	private static final String PLATFORM_PREFIX = "android-";

}

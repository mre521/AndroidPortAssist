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
package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import analysis.ClassReference;
import analysis.ClassReferenceReducer;
import analysis.ReferenceGenerator;
import analysis.RuntimeTemplateGenerator;
import jar.AndroidPlatform;
import jar.AndroidSdk;
import jar.JarClasses;
import jar.JavaRuntime;

public class Main {
	public static void main(String[] args) {
		/* usage:
		 * java -jar main.Main input.jar /path/to/android/sdk platformversion </path/to/java/jre>
		 */
		String sdkPath;
		String inputPath;
		AndroidPlatform androidPlatform = null;
		JavaRuntime javaRuntime = null;
		JarClasses inputClasses = null;
		
		int platformVersion;
		String javaHome = System.getProperty("java.home");
		
		if(args.length >= 3){
			inputPath = args[0];
			sdkPath = args[1];
			platformVersion = Integer.parseInt(args[2]);
			if(args.length >= 4) {
				javaHome = args[3];
			}
			
			boolean loadingSucceeded = false;
			
			try {
				System.out.println("Using Android platform version " + platformVersion);
				androidPlatform = getAndroidPlatform(sdkPath, platformVersion);
	
				System.out.println("Using Java Runtime at " + javaHome);
				javaRuntime = new JavaRuntime(new File(javaHome));
				
				File inputFile = new File(inputPath);
				System.out.println("Loading input, " + inputFile.getName());
				inputClasses = new JarClasses(inputFile);
				
				loadingSucceeded = true;
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			
			if(loadingSucceeded) {
				JarClasses jc = inputClasses;
				Collection<ClassReference> references = null;
				boolean referencesSucceeded = true;
				
				int round = 1;
				do {
					System.out.println("Round " + round);
					System.out.println("Creating reference generator");
					JarClasses[] classPath = {jc,javaRuntime};
					ReferenceGenerator refGen = new ReferenceGenerator(jc, classPath);
					System.out.println("Running reference generator");
					refGen.go();
					
					System.out.println("Creating reference reducer");
					ClassReferenceReducer reducer = new ClassReferenceReducer(refGen.getCollection());
					
					System.out.println("Reducing with respect to Java runtime");
					/* remove references to classes not in the Java runtime */
					reducer.reduceFrom(javaRuntime, false);
					System.out.println("Reducing with respect to Android platform");
					/* remove references to remaining classes that are in the Android platform */
					reducer.reduceFrom(androidPlatform, true);
					
					Collection<ClassReference> refs = reducer.getCollection();
					
					if(refs.size() == 0) {
						break;
					}
					
					if(references == null) {
						references = refs;
					}
					else {
						for(ClassReference ref: refs) {
							if(references.contains(ref)) {
								Iterator<ClassReference> it = references.iterator();
								while(it.hasNext()) {
									ClassReference cr = it.next();
									if(cr.equals(ref)) {
										cr.combine(ref);
										break;
									}
								}
							}
							else {
								references.add(ref);
							}
						}
					}
					
					RuntimeTemplateGenerator gen;
					JarClasses thisRound = null;
					
					try {
						gen = new RuntimeTemplateGenerator(refs, javaRuntime);
						thisRound = gen.generateTemplate();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						referencesSucceeded = false;
						break;
					}
					
					round++;
					jc = thisRound;
				}
				while(true);
				
				if(referencesSucceeded) {
					JarClasses template = null;
					boolean templateSucceeded = true;
					
					try {
						template = new RuntimeTemplateGenerator(references, javaRuntime).generateTemplate();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						templateSucceeded = false;
					}
					
					if(templateSucceeded) {
						int lastIndex = inputPath.lastIndexOf(File.separator);
						StringBuilder path = new StringBuilder("");
						
						if(lastIndex != -1) {
							path.append(inputPath.substring(0,lastIndex+1));
						}
						path.append("template.jar");
						
						try {
							template.writeJar(new File(path.toString()));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for(ClassReference ref: references) {
							ref.print(System.out);
						}
					}
				}
			}
		}
		else {
			System.out.println("java -jar apa.jar /path/to/input.jar /path/to/android/sdk platformversion </path/to/java/jre>");
		}
	}
	
	static AndroidPlatform getAndroidPlatform(String sdkPath, int version) throws IOException {
		AndroidSdk sdk = null;
		AndroidPlatform platform;
		
		sdk = new AndroidSdk(sdkPath);
		platform = sdk.getPlatform(version);

		return platform;
	}
}

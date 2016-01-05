package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import analysis.ClassReference;
import analysis.ClassReferenceReducer;
import analysis.ReferenceGenerator;
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
				System.out.println("Creating reference generator");
				JarClasses[] classPath = {inputClasses,javaRuntime};
				ReferenceGenerator refGen = new ReferenceGenerator(inputClasses, classPath);
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

				for(ClassReference ref: refs) {
					ref.print(System.out);
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

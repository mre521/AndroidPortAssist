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
package analysis;

import jar.JarClasses;
import jar.JarClasses.Entry;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class RuntimeTemplateGenerator {
	
	public RuntimeTemplateGenerator(Collection<ClassReference> references, JarClasses javaRuntime) throws ClassNotFoundException {
		this.references = references;
		
		classes = new HashMap<String,ClassReader>();
		
		JarClasses.Entry entry;
		
		for(ClassReference ref: references) {
			if((entry = javaRuntime.getEntry(ref.getName())) == null) {
				throw new ClassNotFoundException("Class " + ref.getName() + " not found in supplied Java runtime.");
			}
			
			classes.put(ref.getName(), entry.getClassReader());
		}

	}
	
	public JarClasses generateTemplate() {
		ClassReader classReader;
		ClassVisitor classVisitor;
		ClassWriter classWriter;
		
		Map<String, JarClasses.Entry> entries = new HashMap<String, JarClasses.Entry>();
		
		for(ClassReference ref: references) {
			classReader = classes.get(ref.getName());
			
			classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			classVisitor = getClassTransformer(ref, classWriter);
			
			classReader.accept(classVisitor,ClassReader.SKIP_FRAMES);
			
			entries.put(ref.getName(), new JarClasses.Entry(classWriter.toByteArray(), ref.getName()));
		}

		return new JarClasses(entries);
	}
	
	private ClassVisitor getClassTransformer(ClassReference ref, ClassVisitor cv) {
		return new RuntimeClassTransformer(Opcodes.ASM4, cv, ref);
	}
	
	Collection<ClassReference> references;
	Map<String,ClassReader> classes;
}

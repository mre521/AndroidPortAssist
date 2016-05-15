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

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassReference {
	
	public ClassReference(String name) {
		this.name = name;
		methods = new HashMap<String,MethodReference>();
		fields = new HashMap<String,FieldReference>();
	}
	
	public void combine(ClassReference ref) {
		/* only do it if the parameter corresponds to the same class as this */
		if(ref == this) {
			for(MethodReference mRef: ref.methods.values()) {
				addMethod(mRef.getName(), mRef.getDesc());
			}
			
			for(FieldReference fRef: ref.fields.values()) {
				addField(fRef.getName());
			}
		}
	}
	
	public void addField(String name) {
		if(!fields.containsKey(name))
			fields.put(name, new FieldReference(name));
	}
	
	public void addMethod(String name, String desc) {
		if(!methods.containsKey(name+desc))
			methods.put(name+desc, new MethodReference(name, desc));
	}
	
	public String getName() {
		return name;
	}
	
	public FieldReference getField(String name) {
		return fields.get(name);
	}
	
	public MethodReference getMethod(String name, String desc) {
		return methods.get(name+desc);
	}
	
	@Override
	public boolean equals(Object c) {
		return (c instanceof ClassReference && name.equals(((ClassReference)c).name));
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
		
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public void print(PrintStream ps) {
		ps.println("class " + name);
		ps.println("\tMethods:");
		for(MethodReference m: methods.values()) {
			ps.println("\t\t" + m.getName() + "(" + m.getDesc() + ")");
		}
		ps.println("\tFields:");
		for(FieldReference f: fields.values()) {
			ps.println("\t\t" + f.getName());
		}
		ps.println();
	}
	
	private String name;
	private Map<String,MethodReference> methods;
	private Map<String, FieldReference> fields;
}

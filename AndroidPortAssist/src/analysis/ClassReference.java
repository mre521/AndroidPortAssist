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

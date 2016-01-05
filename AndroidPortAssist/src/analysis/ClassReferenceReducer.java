package analysis;

import jar.JarClasses;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassReferenceReducer {
	
	
	public ClassReferenceReducer(Collection<ClassReference> refSet){
		collection = new HashSet();
		collection.addAll(refSet);
		
	}
	
	public void reduceFrom(JarClasses classes, boolean removeIfContains) {
		String name;
		boolean contains;
		
		Set removed = new HashSet();
		
		for(ClassReference reference: collection) {
			name = reference.getName();
			contains = classes.containsClass(name);
			
			if(contains && removeIfContains || (!contains && !removeIfContains)) {
				removed.add(reference);
			}
		}
		
		collection.removeAll(removed);
	}
	
	public Collection<ClassReference> getCollection() {
		return collection;
	}
	
	Collection<ClassReference> collection;
}

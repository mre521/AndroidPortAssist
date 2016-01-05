package analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.Type;

import jar.JarClasses;
import jar.JarClasses.Entry;

public class ReferenceGenerator {
	
	ClassNode[] nodes;
	JarClasses[] classPath;
	List<Map<String,ClassNode>> classPathNodes;

	public ReferenceGenerator(JarClasses input, JarClasses[] classpath) {
		nodes = classNodesFromJarClasses(input);
		classPath = classpath;
		classReferenceMap = new HashMap<String,ClassReference>();
		
		classPathNodes = new ArrayList<Map<String,ClassNode>>();
		for(JarClasses jc: classpath) {
			classPathNodes.add(new HashMap<String,ClassNode>());
		}
	}
	
	/**
	 * Create a {@link ClassNode} from an {@link Entry}
	 * @param e entry
	 * @return
	 */
	private static ClassNode classNodeFromEntry(Entry e) {
		ClassReader cr;
		ClassNode node;

		cr = new ClassReader(e.getData());
		node = new ClassNode();
		cr.accept(node, ClassReader.SKIP_FRAMES);
		return node;
	}
	
	private static ClassNode[] classNodesFromJarClasses(JarClasses jarClasses) {
		Entry[] entries = jarClasses.getEntries();
		LinkedList<ClassNode> nodeList = new LinkedList<ClassNode>();
		
		for(Entry e: entries) {
			nodeList.add(classNodeFromEntry(e));
		}
		
		return nodeList.toArray(new ClassNode[0]);
	}
	
	private static ClassNode classNodeFromJarClasses(String name, JarClasses jarClasses) {
		return classNodeFromEntry(jarClasses.getEntry(name));
	}
	
	/**
	 * Adds a {@link ClassReference} for the given class name if we haven't already.
	 * @param name name of the class
	 * @return {@link ClassReference} for the specified class
	 */
	private ClassReference addClassRef(String name) {
		if(name == null) {
			name = "java/lang/Object";
		}
		
		ClassReference ref;
		
		if(classReferenceMap.containsKey(name) == false) {
			ref = new ClassReference(name);
			classReferenceMap.put(name, ref);
		}
		else {
			ref = classReferenceMap.get(name);
		}
		
		return ref;
	}
	
	/**
	 * Adds a {@link MethodReference}
	 * @param className
	 * @param name
	 * @param desc
	 */
	private void addMethodRef(String className, String name, String desc) {
		ClassReference classReference = addClassRef(className);
		classReference.addMethod(name, desc);
	}
	
	/**
	 * Adds a {@link FieldReference} with the name of the field and of the owner class
	 * @param className field owner name
	 * @param name field name
	 */
	private void addFieldRef(String className, String name) {
		ClassReference classReference = addClassRef(className);
		classReference.addField(name);
	}
	
	/**
	 * Runs the analysis
	 */
	public void go() {
		for(ClassNode node: nodes) {
			refInterfaces((List<MethodNode>)node.methods, (List<String>)node.interfaces);
			refSuperclass((List<MethodNode>)node.methods, node.superName);
			refMethods((List<MethodNode>)node.methods);
			refFields((List<FieldNode>)node.fields);
		}
	}
	
	/**
	 * Gets a Collection of all found ClassReferences 
	 * @return
	 */
	public Collection<ClassReference> getCollection() {
		return classReferenceMap.values();
	}
	
	/**
	 * Add a class reference for the each implemented interface and method references for overridden methods
	 * @param classMethods array of methods in the current class being analyzed
	 * @param ifaces list of interfaced implemented by the current class being analyzed
	 */
	private void refInterfaces(List<MethodNode> classMethods, List<String> ifaces) {
		for(String iface: ifaces) {
			addClassRef(iface);
			refOverriddenMethods(classMethods, iface);
		}
	}
	
	/**
	 * Add a class reference for the superclass and method references for overridden methods
	 * @param classMethods array of methods in the current class being analyzed
	 * @param superName name of the superclass
	 */
	private void refSuperclass(List<MethodNode> classMethods, String superName) {
		addClassRef(superName);
		refOverriddenMethods(classMethods, superName);
	}
	
	/**
	 * Find and add method references resulting from overridden methods of the superclass or an implemented interface
	 * @param classMethods array of methods in the current class being analyzed
	 * @param superOrInterfaceName name of the superclass or implemented interface to be examined
	 */
	private void refOverriddenMethods(List<MethodNode> classMethods, String superOrInterfaceName) {
		ClassNode node;
		Map<String,ClassNode> map;
		JarClasses classes;
		
		MethodNode inheritedMethod;
		
		do {
			int i = 0;
			node = null;
			for(i = 0; i < classPathNodes.size(); i++) {
				map = classPathNodes.get(i);
				classes = classPath[i];
				
				if(map.containsKey(superOrInterfaceName)) {
					node = map.get(superOrInterfaceName);
				}
				else {
					if(classes.containsClass(superOrInterfaceName)) {
						node = classNodeFromJarClasses(superOrInterfaceName, classes);
						map.put(superOrInterfaceName, node);
					}
					else {
						continue;
					}
				}
			}
			
			if(node != null) {
				for(Object superNodeObject: node.methods) {
					inheritedMethod = (MethodNode)superNodeObject;
					for(MethodNode method: classMethods) {
						if(method.name.equals(inheritedMethod.name) && method.desc.equals(inheritedMethod.desc)) {
							addMethodRef(superOrInterfaceName, inheritedMethod.name, inheritedMethod.desc);
						}
					}
				}
				superOrInterfaceName = node.superName;
			}
		}
		while(node != null && node.superName != null);
	}
	
	private void refMethods(List<MethodNode> methods) {
		for(MethodNode node: methods) {
			refExceptions((List<String>)node.exceptions);
			refMethodDesc(node.desc);
			refTryCatchBlocks((List<TryCatchBlockNode>)node.tryCatchBlocks);
			refInstructions(node.instructions);
		}
	}
	
	private void refExceptions(List<String> exceptions) {
		for(String except: exceptions) {
			addClassRef(except);
		}
	}
	
	private void addTypeClassRef(Type type) {
		if(type.getSort() == Type.OBJECT) {
			addClassRef(type.getInternalName());
		}
	}
	
	private void refMethodDesc(String desc) {
		Type[] args = Type.getArgumentTypes(desc);
		for(Type arg: args) {
			addTypeClassRef(arg);
		}
		
		Type retType = Type.getReturnType(desc);
		addTypeClassRef(retType);
	}
	
	private void refTryCatchBlocks(List<TryCatchBlockNode> blocks) {
		for(TryCatchBlockNode block: blocks) {
			addClassRef(block.type);
		}
	}
	
	private void refFields(List<FieldNode> fields) {
		Type type;
		for(FieldNode field: fields) {
			type = Type.getType(field.desc);
			addTypeClassRef(type);
		}
	}
	
	private void refInstructions(InsnList insnList) {
		AbstractInsnNode insn;
		
		insn = insnList.getFirst();
		
		Object cst;
		
		while(insn != null) {
			switch(insn.getType()) {
			case AbstractInsnNode.FIELD_INSN:
				//addTypeClassRef(Type.getType(((FieldInsnNode)insn).desc));
				addFieldRef(((FieldInsnNode)insn).owner, ((FieldInsnNode)insn).name);
				break;
			/*case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
				
				break;*/
			/*case AbstractInsnNode.LDC_INSN:
				cst = ((LdcInsnNode)insn).cst;
				if(cst instanceof Type) {
					addTypeClassRef((Type)cst);
				}
				break;*/
			case AbstractInsnNode.METHOD_INSN:
				addMethodRef(((MethodInsnNode)insn).owner, ((MethodInsnNode)insn).name, ((MethodInsnNode)insn).desc);
				break;
			case AbstractInsnNode.MULTIANEWARRAY_INSN:
				addTypeClassRef(Type.getType(((MultiANewArrayInsnNode)insn).desc));
				break;
			case AbstractInsnNode.TYPE_INSN:
				addTypeClassRef(Type.getType(((TypeInsnNode)insn).desc));
				break;
			}
			insn = insn.getNext();
		}
	}
	
	Map<String,ClassReference> classReferenceMap;
}

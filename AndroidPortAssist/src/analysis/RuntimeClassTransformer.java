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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

public class RuntimeClassTransformer extends ClassVisitor {
	
	int api;

	public RuntimeClassTransformer(int api, ClassVisitor cv, ClassReference ref) {
		super(api, cv);

		this.api = api;
		this.ref = ref;
	}
	
	public FieldVisitor visitField(int access,
            String name,
            String desc,
            String signature,
            Object value) {
		
		if(ref.getField(name) != null) {
			return super.visitField(access,name,desc,signature,value);
		}
		else {
			return null;
		}
		
	}
	
	public MethodVisitor visitMethod(int access,
            String name,
            String desc,
            String signature,
            String[] exceptions) {
		
		MethodReference mRef;
		
		mRef = ref.getMethod(name, desc);
		if(mRef != null) {
			return new TemplateMethodAdapter(api, super.visitMethod(access,name,desc,signature,exceptions), Type.getReturnType(desc));
		}
		else {
			return null;
		}
	}
	
	public void visitInnerClass(String name,
            String outerName,
            String innerName,
            int access) {
		//System.out.println("name: " + name + ", outerName: " + outerName + ", innerName: " + innerName);
	}
	
	class TemplateMethodAdapter extends MethodVisitor {

		public TemplateMethodAdapter(int api, MethodVisitor mv, Type returnType) {
			super(api, mv); 
			
			this.returnType = returnType;
		}
		
		private void createReturnStatement() {
			
			int loadOpcode;
			int returnOpcode = returnType.getOpcode(Opcodes.IRETURN);
			
			switch(returnType.getSort()) {
			case Type.DOUBLE:
				loadOpcode = Opcodes.DCONST_0;
				break;
			case Type.FLOAT:
				loadOpcode = Opcodes.FCONST_0;
				break;
			case Type.BOOLEAN:	
			case Type.BYTE:
			case Type.CHAR:
			case Type.INT:
			case Type.SHORT:
				loadOpcode = Opcodes.ICONST_0;
				break;
			case Type.LONG:
				loadOpcode = Opcodes.LCONST_0;
				break;
			case Type.OBJECT:
			case Type.ARRAY:
				loadOpcode = Opcodes.ACONST_NULL;
				break;
			case Type.VOID:
			default:
				loadOpcode = -1;
				break;
			}
			
			if(loadOpcode != -1) {
				super.visitInsn(loadOpcode);
			}
			
			super.visitInsn(returnOpcode);
		}
		
		public void visitCode() {
			super.visitCode();
			createReturnStatement();
		}
		
		@Override
		public void visitFieldInsn(int opcode,
                String owner,
                String name,
                String desc) {
			
		}
		
		@Override
		public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
			
		}
		
		@Override
		public void visitIincInsn(int var,
		        int increment) {
			
		}
		
		@Override
		public void visitInsn(int opcode) {
			
		}

		@Override
		public AnnotationVisitor visitInsnAnnotation(int typeRef,
		        TypePath typePath,
		        String desc,
		        boolean visible) {
					return null;
			
		}
		
		@Override
		public void	visitIntInsn(int opcode, int operand) {
			
		}

		@Override
		public void visitInvokeDynamicInsn(String name,
		        String desc,
		        Handle bsm,
		        Object... bsmArgs) {
			
		}

		@Override
		public void visitJumpInsn(int opcode,
		        Label label) {
			
		}

		@Override
		public void visitLabel(Label label) {
			
		}

		@Override
		public void visitLdcInsn(Object cst) {
			
		}

		@Override
		public void visitLineNumber(int line,
		        Label start) {
			
		}

		@Override
		public void visitLocalVariable(String name,
		        String desc,
		        String signature,
		        Label start,
		        Label end,
		        int index) {
			
		}

		@Override
		public AnnotationVisitor visitLocalVariableAnnotation(int typeRef,
		        TypePath typePath,
		        Label[] start,
		        Label[] end,
		        int[] index,
		        String desc,
		        boolean visible) {
			return null;
		}

		@Override
		public void visitLookupSwitchInsn(Label dflt,
		        int[] keys,
		        Label[] labels) {
			
		}

		@Override
		public void visitMaxs(int maxStack,
		        int maxLocals) {
			
		}

		@Override
		public void visitMethodInsn(int opcode,
                String owner,
                String name,
                String desc,
                boolean itf) {
			
		}
		
		@Override
		public void visitMultiANewArrayInsn(String desc,
		        int dims) {
			
		}

		@Override
		public void visitTableSwitchInsn(int min,
                int max,
                Label dflt,
                Label... labels) {
			
		}
		
		@Override
		public AnnotationVisitor visitTryCatchAnnotation(int typeRef,
		        TypePath typePath,
		        String desc,
		        boolean visible) {
			return null;
		}

		@Override
		public void visitTryCatchBlock(Label start,
                Label end,
                Label handler,
                String type) {
			
		}
		
		@Override
		public void	visitTypeInsn(int opcode, String type) {
			
		}
		
		@Override
		public void visitVarInsn(int opcode, int var) {
			
		}
		
		Type returnType;
	}
	
	
	ClassReference ref;
}

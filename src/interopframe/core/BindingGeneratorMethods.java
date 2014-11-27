package interopframe.core;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class BindingGeneratorMethods {
	
	protected ArrayList<Method> getMethods(String interfaceName) {
		try {
			Class<?> c = Class.forName(interfaceName);
			Method m[] = c.getDeclaredMethods();
			ArrayList<Method> methods = new ArrayList<Method>();
			for (int i = 0; i < m.length; i++)
				methods.add(m[i]);
			return methods;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<String> mountMethodsSignatures(String interfaceName) {
		ArrayList<Method> methods = getMethods(interfaceName);
		ArrayList<String> methodReturn = new ArrayList<String>();

		for (int i = 0; i < methods.size(); i++) {
			String signature = "";
			Method m = methods.get(i);
			String s[] = m.toString().split(" ");			
			String returnType = m.getReturnType().getCanonicalName().toString();
			String nameMethod = m.getName();
			Class<?>[] parameterTypes = m.getParameterTypes();

			signature = s[0] + " " + returnType + " " + nameMethod + "(";

			if (parameterTypes != null) {
				for (int j = 0; j < parameterTypes.length; j++) {
					signature += parameterTypes[j].getCanonicalName() + " p"+j;
					if (j != parameterTypes.length - 1) {
						signature += ", ";
					}
				}
			}
			signature += ")";
			Class<?>[] clazz = m.getExceptionTypes();
			if (clazz.length != 0) {				
				signature += " throws ";
				for (int k=0; k<clazz.length; k++) {
					signature += clazz[k].getCanonicalName();
					if (k != clazz.length - 1) {
						signature += ", ";
					}
				}
			}
			methodReturn.add(signature);
		}
		return methodReturn;
	}
}

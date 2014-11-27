package interopframe.core;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class CodeGeneratorMethods {
	
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
}
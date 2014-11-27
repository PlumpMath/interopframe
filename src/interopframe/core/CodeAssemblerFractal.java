package interopframe.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import interopframe.api.Parameters;
import interopframe.utils.CustomClassLoader;

public class CodeAssemblerFractal implements ICodeAssembler {
	
	public CodeAssemblerFractal() {
		CustomClassLoader.addPath("compiled/fractal");
	}

	public void mountClient(Parameters p, Object runtime) {
		Object o = null;
		Method bindFc = null;
		Method lookupFc = null;
		Method listFc = null;		
		Object proxy = null;
		String[] fc = null;
		try {
			Class<?> clazz = Class.forName(p.getCanonicalClassNameClient());
			o = clazz.cast(runtime);
			bindFc = clazz.getDeclaredMethod("bindFc", new Class[] { String.class, Object.class });			
			lookupFc = clazz.getDeclaredMethod("lookupFc", new Class[] { String.class });			
			listFc = clazz.getDeclaredMethod("listFc", (Class<?>[])null);			
			fc = (String[]) listFc.invoke(o, (Object[])null);

			Class<?> cl = Class.forName(p.getCanonicalProxyName());
			proxy = cl.newInstance();

		} catch (ClassNotFoundException e ) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		boolean ok = false;
		for (int i=0; i<fc.length; i++) {
			try {					
				Object aux = lookupFc.invoke(o, new Object[] { new String(fc[i]) });
				if (aux == null) {
					ok = true;
					bindFc.invoke(o, new Object[] { new String(fc[i]),  proxy});
				}
			} catch (ClassCastException | InvocationTargetException | IllegalAccessException e) {
				System.out.println(e.getLocalizedMessage());
				ok = false;
			} 
			if (ok) {
				break;
			}
		}
	}

	public void mountServer(Parameters p, Object runtime) {
		try {		
			Class<?> clazz = Class.forName(p.getCanonicalSkeletonName());
			Object o = clazz.newInstance();
			Method bindFc = clazz.getDeclaredMethod("bindFc", new Class[] { String.class, Object.class });

			Class<?> cl = Class.forName(p.getCanonicalClassNameServer());

			Object o2 = cl.cast(runtime);

			bindFc.invoke(o, new Object[] { new String(p.getClassNameServer()), o2 });

			Method run = clazz.getDeclaredMethod("run", (Class<?>[]) null);

			run.invoke(o, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
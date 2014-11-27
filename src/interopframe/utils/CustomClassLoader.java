package interopframe.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class CustomClassLoader {

	public CustomClassLoader() {

	}

	public static void addPath(String path) {
		File dir = new File(path);
		dir.mkdirs();
		URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> clazz= URLClassLoader.class;
		Method method;
		try {
			method = clazz.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(classLoader, new Object[] { new File(path).toURI().toURL() });
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}

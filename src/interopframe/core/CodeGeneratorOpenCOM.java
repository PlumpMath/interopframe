package interopframe.core;

import interopframe.api.Parameters;
import interopframe.utils.CustomClassLoader;
import interopframe.utils.StringJavaFileObject;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class CodeGeneratorOpenCOM extends CodeGeneratorMethods implements ICodeGenerator {
	private VelocityEngine ve;
	private IBindingGenerator bind;
	private Parameters p;
	
	public CodeGeneratorOpenCOM(Parameters p, IBindingGenerator bind) {
		this.p = p;
		this.bind = bind;
		this.ve = new VelocityEngine();
		this.ve.init();
		CustomClassLoader.addPath("lib/templates");
	}
	
	public StringWriter getWriterSkeleton() {
		Template t = this.ve.getTemplate("lib/templates/SkeletonTemplate_OpenCOM.vm");
		
		VelocityContext context = new VelocityContext(bind.getSkeletonContext());
		
		context.put("packageName", this.p.getPackageInterfaceNameServer());
		context.put("className", this.p.getClassNameServer());
		context.put("interfaceName", this.p.getInterfaceNameServer());
		ArrayList<String> methods = mountSkeletonImplMethods(p.getCanonicalInterfaceNameServer(), p.getClassNameServer());
		context.put("methods", methods);
		
		StringWriter writerSkeleton = new StringWriter();
		t.merge(context, writerSkeleton);		
		return writerSkeleton;
	}
	
	public StringWriter getWriterProxy() {
		Template t = this.ve.getTemplate("lib/templates/ProxyTemplate_OpenCOM.vm");
		
		VelocityContext context = new VelocityContext(bind.getProxyContext());
		
		context.put("packageName", this.p.getPackageInterfaceNameClient());
		context.put("packageNameISkeleton", this.p.getPackageInterfaceNameServer());
		context.put("className", this.p.getClassNameServer());
		context.put("interfaceName", this.p.getInterfaceNameClient());
		
		StringWriter writerProxy = new StringWriter();
		t.merge(context, writerProxy);
		return writerProxy;
	}
	
	public void compileProxy() {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager manager = compiler.getStandardFileManager(null,	null, null);
		JavaFileObject strFileISkeleton = new StringJavaFileObject("ISkeleton"+this.p.getClassNameServer(), bind.getWriterISkeleton().toString());				
		JavaFileObject strFileProxy = new StringJavaFileObject("Proxy"+this.p.getClassNameServer(), getWriterProxy().toString());
		
		Iterable<? extends JavaFileObject> units = Arrays.asList(strFileISkeleton, strFileProxy);
		File dir = new File("compiled/opencom");
		dir.mkdirs();
		String[] opts = new String[] { "-d", "compiled/opencom" };
		CompilationTask task = compiler.getTask(null, manager, null, Arrays.asList(opts), null, units);
		task.call();
	}
	
	public void compileSkeleton() {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager manager = compiler.getStandardFileManager(null,	null, null);
		JavaFileObject strFileISkeleton = new StringJavaFileObject("ISkeleton"+this.p.getClassNameServer(), bind.getWriterISkeleton().toString());
		JavaFileObject strFileSkeleton = new StringJavaFileObject("Skeleton"+this.p.getClassNameServer(), getWriterSkeleton().toString());		
		
		Iterable<? extends JavaFileObject> units = Arrays.asList(strFileISkeleton, strFileSkeleton);
		File dir = new File("compiled/opencom");
		dir.mkdirs();
		String[] opts = new String[] { "-d", "compiled/opencom" };
		CompilationTask task = compiler.getTask(null, manager, null, Arrays.asList(opts), null, units);
		task.call();
	}
	
	public ArrayList<String> mountSkeletonImplMethods(String interfaceName, String className) {

		// Inicializa um ArrayList de métodos e um ArrayList para o retorno.
		String retorno = "";
		ArrayList<Method> methods = getMethods(interfaceName);
		ArrayList<String> methodReturn = new ArrayList<String>();

		// Varre o ArrayList enquanto houverem métodos.
		for (int i = 0; i < methods.size(); i++) {

			// Inicializa as Strings que vão compor o método escrito em Java.
			retorno = "";
			String body = "";
			String signature = "";

			// Pega o modificador de visibilidade, o tipo de retorno, o nome do
			// método e os parâmetros, utilizando a Java Reflection API.
			Method m = methods.get(i);
			String s[] = m.toString().split(" ");
			String returnType = m.getReturnType().getCanonicalName().toString();
			String nameMethod = m.getName();
			Class<?>[] parameterTypes = m.getParameterTypes();

			// Escreve o início da assinatura do método.
			signature = s[0] + " " + returnType + " " + nameMethod + "(";

			if (returnType != "void") {
				body += "		return this.rc" + className + ".m_pIntf." + nameMethod + "(";
			} else {
				body += "		this.rc" + className + ".m_pIntf." + nameMethod	+ "(";
			}

			// Se existem parâmetros do método.
			if (parameterTypes != null) {
				// Utiliza um vetor auxiliar para servir de nome das variáveis
				// do parâmetro.
				for (int j = 0; j < parameterTypes.length; j++) {
					signature += parameterTypes[j].getCanonicalName() + " p"+j;
					body += "p" + j;
					if (j != parameterTypes.length - 1) {
						signature += ", ";
						body += ", ";
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
			signature += " {\n";
			body += ");\n	}";
			retorno += signature + body;
			methodReturn.add(retorno);
		}
		return methodReturn;
	}	
}
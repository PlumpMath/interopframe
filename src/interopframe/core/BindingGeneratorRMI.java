package interopframe.core;

import interopframe.api.Parameters;
import interopframe.api.ParametersBinding;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class BindingGeneratorRMI extends BindingGeneratorMethods implements IBindingGenerator {
	
	private VelocityEngine ve;
	private Parameters parameters;
	private ParametersBinding parametersRMI;	
	
	public BindingGeneratorRMI(Parameters parameters, ParametersBinding parametersRMI) {
		this.parameters = parameters;
		this.parametersRMI = parametersRMI;
		this.ve = new VelocityEngine();
		this.ve.init();
	}
	
	public StringWriter getWriterISkeleton() {
		Template t = this.ve.getTemplate("lib/templates/ISkeletonTemplate_RMI.vm");
		ArrayList<String> methods = this.mountMethodsSignatures(this.parameters.getCanonicalInterfaceNameServer());
		VelocityContext context = new VelocityContext();
		context.put("packageName", this.parameters.getPackageInterfaceNameServer());
		context.put("className", this.parameters.getClassNameServer());
		context.put("methods", methods);
		StringWriter writerISkeleton = new StringWriter();
		t.merge(context, writerISkeleton);
		return writerISkeleton;
	}	
	
	public VelocityContext getSkeletonContext() {
		VelocityContext context = new VelocityContext();		
		context.put("bindingImports", "import java.rmi.RemoteException;\nimport java.rmi.server.UnicastRemoteObject;\nimport interopframe.utils.ServerPadraoRMI;");
		context.put("registerObject", 
		"	public void registerObject() {\n"+
		"		try {\n"+
		"			ISkeleton"+parameters.getClassNameServer()+" stub = (ISkeleton"+parameters.getClassNameServer()+") UnicastRemoteObject.exportObject(this, 0);\n"+
		"			ServerPadraoRMI.registry.rebind(\"Skeleton"+parameters.getClassNameServer()+"\", stub);\n"+
		"			Thread.sleep(10);\n"+
		"		} catch (RemoteException re) {\n"+
		"			re.printStackTrace();\n"+
		"		} catch (InterruptedException ie) {\n"+
		"			ie.printStackTrace();\n"+
		"		}\n"+
		"	}");
		return context;
	}
	
	public VelocityContext getProxyContext() {
		VelocityContext context = new VelocityContext();
		context.put("bindingImports", "import java.rmi.RemoteException;\nimport java.rmi.registry.LocateRegistry;\nimport java.rmi.registry.Registry;");
		context.put("startStub", 
		"	public void startStub() {\n"+
		"		try {\n"+
		"			Registry registry = LocateRegistry.getRegistry(\""+parametersRMI.getHostName()+"\", "+this.parametersRMI.getPort()+");\n"+
		"			this.stub = (ISkeleton"+parameters.getClassNameServer()+") registry.lookup(\"Skeleton"+parameters.getClassNameServer()+"\");\n"+
		"		} catch (Exception ex) {\n"+
		"			ex.printStackTrace();\n"+
		"		}\n"+
		"	}");
		ArrayList<String> methods = mountProxyMethods(parameters.getCanonicalInterfaceNameServer());
		context.put("methods", methods);
		return context;
	}
	
	public ArrayList<String> mountProxyMethods(String interfaceName) {

		// Inicializa um ArrayList de m�todos e um ArrayList para o retorno.
		String retorno = "";
		ArrayList<Method> methods = getMethods(interfaceName);
		ArrayList<String> methodReturn = new ArrayList<String>();

		// Varre o ArrayList enquanto houverem m�todos.
		for (int i = 0; i < methods.size(); i++) {

			// Inicializa as Strings que v�o compor o m�todo escrito em Java.
			retorno = "";
			String signature = "";
			String body = "";
			String end = "";

			// Pega o modificador de visibilidade, o tipo de retorno, o nome do
			// m�todo e os par�metros, utilizando a Java Reflection API.
			Method m = methods.get(i);			
			String s[] = m.toString().split(" ");
			String returnType = m.getReturnType().getCanonicalName().toString();
			String nameMethod = m.getName();
			Class<?>[] parameterTypes = m.getParameterTypes();

			// Escreve o in�cio da assinatura do m�todo.
			signature = "	" + s[0] + " " + returnType + " " + nameMethod + "(";

			// Se o tipo de retorno � diferente de void.
			if (returnType != "void") {
				// Se o tipo de retorno � um tipo num�rico.
				if (returnType.equals("bit")
						|| returnType.equals("byte")
						|| returnType.equals("short")
						|| returnType.equals("int")
						|| returnType.equals("long")
						|| returnType.equals("float")
						|| returnType.equals("double")) {
					body += "		" + returnType + " resultado = 0;";
				}
				// Se for boolean.
				else if (returnType.equals("boolean")) {
					body += "		" + returnType + " resultado = false;";
				}
				// Se n�o for num�rico nem boolean.
				else {
					body += "		" + returnType + " resultado = null;";
				}
				body += "\n		try {\n			resultado = stub." + nameMethod + "(";
				end += "		return resultado;\n";
			}
			// Se o m�todo � sem retorno, do tipo void.
			else {
				body += "		try {\n			stub." + nameMethod + "(";
			}

			// Se existem par�metros do m�todo.
			if (parameterTypes != null) {
				// Utiliza um vetor auxiliar para servir de nome das vari�veis
				// do par�metro.
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
			body += ");\n		} catch (RemoteException re) {\n			re.printStackTrace();\n		}\n";
			end += "	}\n";
			retorno += signature + body + end;
			methodReturn.add(retorno);
		}
		return methodReturn;
	}
	
	@Override
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
			signature += " throws RemoteException";
			if (clazz.length != 0) {
				signature += ", ";
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
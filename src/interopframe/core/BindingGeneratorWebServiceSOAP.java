package interopframe.core;

import interopframe.api.Parameters;
import interopframe.api.ParametersBinding;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class BindingGeneratorWebServiceSOAP extends BindingGeneratorMethods implements IBindingGenerator {

	private VelocityEngine ve;	
	private Parameters parameters;
	private ParametersBinding parametersWebServiceSOAP;
	
	public BindingGeneratorWebServiceSOAP(Parameters parameters, ParametersBinding parametersWebServiceSOAP) {
		this.parameters = parameters;
		this.parametersWebServiceSOAP = parametersWebServiceSOAP;
		this.ve = new VelocityEngine();
		this.ve.init();		
	}
	
	public StringWriter getWriterISkeleton() {
		Template t = this.ve.getTemplate("lib/templates/ISkeletonTemplate_WebServiceSOAP.vm");
		ArrayList<String> methods = this.mountMethodsSignatures(this.parameters.getCanonicalInterfaceNameServer());		
		VelocityContext context = new VelocityContext();		
		context.put("packageName", this.parameters.getPackageInterfaceNameServer());
		context.put("className", this.parameters.getClassNameServer());
		context.put("methods", methods);
		StringWriter writerISkeleton = new StringWriter();
		t.merge(context, writerISkeleton);
		return writerISkeleton;
	}

	@Override
	public VelocityContext getSkeletonContext() {
		VelocityContext context = new VelocityContext();		
		context.put("bindingImports", "import javax.jws.WebService;\nimport javax.xml.ws.Endpoint;");
		context.put("bindingAnnotations", "@WebService(endpointInterface = \""+parameters.getPackageInterfaceNameServer()+".ISkeleton"+parameters.getClassNameServer()+"\")");
		context.put("registerObject", 
		"	public void registerObject() {\n"+
		"		Endpoint.publish(\"http://"+parametersWebServiceSOAP.getHostName()+":"+parametersWebServiceSOAP.getPort()+"/"+parameters.getClassNameServer()+"\", this);\n"+
		"	}");
		return context;
	}

	@Override
	public VelocityContext getProxyContext() {
		VelocityContext context = new VelocityContext();		
		context.put("bindingImports", "import java.net.MalformedURLException;\nimport java.net.URL;\nimport javax.xml.namespace.QName;\nimport javax.xml.ws.Service;");
		context.put("startStub", 
		"	public void startStub() {\n"+
		"		try {\n"+
		"			URL url = new URL(\"http://"+parametersWebServiceSOAP.getHostName()+":"+parametersWebServiceSOAP.getPort()+"/"+parameters.getClassNameServer()+"?wsdl\");\n"+
		"			QName qname = new QName(\"http://"+inverterString(parameters.getPackageInterfaceNameServer())+"/\", \"Skeleton"+parameters.getClassNameServer()+"Service\");\n"+			
		"			Service service = Service.create(url, qname);\n"+
		"			this.stub = service.getPort(ISkeleton"+parameters.getClassNameServer()+".class);\n"+
		"		} catch (MalformedURLException e) {\n"+
		"			e.printStackTrace();\n"+
		"		}\n"+
		"	}");
		ArrayList<String> methods = mountProxyMethods(parameters.getCanonicalInterfaceNameServer());
		context.put("methods", methods);
		return context;
	}
	
	private String inverterString(String recebida) {
		String aux=""; 
		StringTokenizer a = new StringTokenizer(recebida); 
		while(a.hasMoreTokens()) { 
			aux = a.nextToken(".") + "." + aux;		
		}		
		aux = aux.substring(0, aux.length()-1);
		return aux;
	}
	
	public ArrayList<String> mountProxyMethods(String interfaceName) {

		// Inicializa um ArrayList de métodos e um ArrayList para o retorno.
		String retorno = "";
		ArrayList<Method> methods = getMethods(interfaceName);
		ArrayList<String> methodReturn = new ArrayList<String>();

		// Varre o ArrayList enquanto houverem métodos.
		for (int i = 0; i < methods.size(); i++) {

			// Inicializa as Strings que vão compor o método escrito em Java.
			retorno = "";
			String signature = "";
			String body = "";			

			// Pega o modificador de visibilidade, o tipo de retorno, o nome do
			// método e os parâmetros, utilizando a Java Reflection API.
			Method m = methods.get(i);
			String s[] = m.toString().split(" ");
			String returnType = m.getReturnType().getCanonicalName().toString();
			String nameMethod = m.getName();
			Class<?>[] parameterTypes = m.getParameterTypes();

			// Escreve o início da assinatura do método.
			signature = "	" + s[0] + " " + returnType + " " + nameMethod + "(";

			// Se o tipo de retorno é diferente de void.
			if (returnType != "void") {
				body += "		return stub." + nameMethod + "(";								
			}
			// Se o método é sem retorno, do tipo void.
			else {
				body += "		stub." + nameMethod + "(";
			}

			// Se existem parâmetros do método.
			if (parameterTypes != null) {				
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
			body += ");\n	}\n";
			retorno += signature + body;
			methodReturn.add(retorno);
		}
		return methodReturn;
	}
}

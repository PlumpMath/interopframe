package interopframe.core;

import interopframe.api.Parameters;
import interopframe.api.ParametersBinding;

import java.lang.reflect.Constructor;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class DistributedConfigurator implements IDistributedConfigurator {
	
	private Parameters parameters;
	private ParametersBinding pBinding;
	private Object runtime;
	IDistributedConfigurator stub;
	
	public DistributedConfigurator() {		
	}
	
	public void setParameters(Parameters parameters, ParametersBinding pBinding, Object runtime) {
		this.parameters = parameters;
		this.pBinding = pBinding;
		this.runtime = runtime;
	}
	
	public void setParametersServer(Parameters parameters, ParametersBinding pBinding) {
		this.parameters = parameters;
		this.pBinding = pBinding;
	}
	
	public void setRuntimeServer(Object runtime) {
		this.runtime = runtime;
	}
	
	@Override
	public ArrayList<Object> getParameters() {
		ArrayList<Object> list = new ArrayList<>();
		list.add(this.parameters);
		list.add(this.pBinding);			
		return list;
	}
	
	public void registerServer(int port) {
		try {		   
		    IDistributedConfigurator stub = (IDistributedConfigurator) UnicastRemoteObject.exportObject(this, 0);
		    Registry registry = LocateRegistry.createRegistry(port);
		    registry.rebind("DistributedConfigurator", stub);
		    Thread.sleep(10);		    
		} catch (Exception e) {
		    System.err.println("Server exception: " + e.toString());
		    e.printStackTrace();
		}
	}
	
	public void startStub(String hostName, int port) {
		try {			
			Registry registry = LocateRegistry.getRegistry(hostName, port);
			stub = (IDistributedConfigurator) registry.lookup("DistributedConfigurator");			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	

	@Override
	public void generateCodeServer() {
		try {
			Constructor<?> ctorBind = pBinding.getBindingGeneratorClass().getDeclaredConstructor(Parameters.class, ParametersBinding.class);
		    ctorBind.setAccessible(true);
		    IBindingGenerator bind = (IBindingGenerator) ctorBind.newInstance(parameters, pBinding);
		    
		    Constructor<?> ctorComp = parameters.getGeneratorClassServer().getDeclaredConstructor(Parameters.class, IBindingGenerator.class);
		    ctorComp.setAccessible(true);
		    ICodeGenerator co = (ICodeGenerator) ctorComp.newInstance(parameters, bind);		    
			co.compileSkeleton();
			System.err.println("Código Servidor Compilado com Sucesso");
		} catch (Exception e) {	
			e.printStackTrace();
		}		
	}

	@Override
	public void generateCodeClient() {
		try {
			Constructor<?> ctorBind = pBinding.getBindingGeneratorClass().getDeclaredConstructor(Parameters.class, ParametersBinding.class);
		    ctorBind.setAccessible(true);
		    IBindingGenerator bind = (IBindingGenerator) ctorBind.newInstance(parameters, pBinding);
		    
		    Constructor<?> ctorComp = parameters.getGeneratorClassClient().getDeclaredConstructor(Parameters.class, IBindingGenerator.class);
		    ctorComp.setAccessible(true);
		    ICodeGenerator co = (ICodeGenerator) ctorComp.newInstance(parameters, bind);
			co.compileProxy();
			System.err.println("Código Cliente Compilado com Sucesso");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mountCodeServer() {
		try{
			Constructor<?> ctorComp = parameters.getAssemblerClassServer().getDeclaredConstructor();
		    ctorComp.setAccessible(true);
		    ICodeAssembler co = (ICodeAssembler) ctorComp.newInstance();			
			co.mountServer(parameters, runtime);
			System.err.println("Código Servidor Carregado com Sucesso");
			System.out.println("Host: "+pBinding.getHostName()+" ... Porta: "+pBinding.getPort());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mountCodeClient() {
		try{
			Constructor<?> ctorComp = parameters.getAssemblerClassClient().getDeclaredConstructor();
		    ctorComp.setAccessible(true);
		    ICodeAssembler co = (ICodeAssembler) ctorComp.newInstance();
			co.mountClient(parameters, runtime);
			System.err.println("Código Cliente Carregado com Sucesso");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
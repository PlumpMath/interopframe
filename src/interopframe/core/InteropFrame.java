package interopframe.core;

import interopframe.api.IFrame;
import interopframe.api.Parameters;
import interopframe.api.ParametersBinding;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class InteropFrame implements IFrame {
	
	private DistributedConfigurator dc;
	private Parameters parameters;
	private ParametersBinding pBinding;
	
	//Instanciar Cliente	
	public InteropFrame() {
		dc = new DistributedConfigurator();
		dc.startStub("localhost", 6666);
	}
	
	public InteropFrame(int port) {
		dc = new DistributedConfigurator();
		dc.startStub("localhost", port);
	}
	
	public InteropFrame(String host, int port) {
		dc = new DistributedConfigurator();
		dc.startStub(host, port);
	}
	
	//Instanciar Servidor
	public InteropFrame(Object serverRuntime) {
		dc = new DistributedConfigurator();
		dc.registerServer(6666);
		dc.setRuntimeServer(serverRuntime);
	}
	
	public InteropFrame(Object serverRuntime, int port) {
		dc = new DistributedConfigurator();
		dc.registerServer(port);
		dc.setRuntimeServer(serverRuntime);
	}
	
	@Override
	public void remoteBinding (Object clientRuntime) {
		try {
			dc.setParameters(this.parameters, this.pBinding, clientRuntime);
			dc.generateCodeClient();
			dc.stub.setParametersServer(this.parameters, this.pBinding);
			dc.stub.generateCodeServer();
			dc.stub.mountCodeServer();
			dc.mountCodeClient();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setParameters(String canonicalClassNameClient, String canonicalInterfaceNameClient, String componentModelClient,
			String canonicalClassNameServer, String canonicalInterfaceNameServer, String componentModelServer) {
		Parameters parameters = new Parameters(canonicalClassNameClient, canonicalInterfaceNameClient, componentModelClient,
				canonicalClassNameServer, canonicalInterfaceNameServer, componentModelServer);
		this.parameters = parameters;
	}
	
	@Override
	public void setParametersBinding(String bindingType, String hostName, int port) {
		ParametersBinding pBinding = new ParametersBinding(bindingType, hostName, port);
		this.pBinding = pBinding;
	}
	@Override
	public void setParametersBinding(String bindingType, String hostName) {
		ParametersBinding pBinding = new ParametersBinding(bindingType, hostName, 6767);
		this.pBinding = pBinding;
	}
	@Override
	public void setParametersBinding(String bindingType) {
		ParametersBinding pBinding = new ParametersBinding(bindingType, "localhost", 6767);
		this.pBinding = pBinding;
	}	
	
	public void startServer(int port) {
		dc.registerServer(port);
	}	
	
	public void connectServer(Parameters parameters, ParametersBinding pBinding, Object runtime) {
		dc.setParameters(parameters, pBinding, runtime);		
		dc.generateCodeServer();
		dc.mountCodeServer();
	}	
	
	public void startClient(String host, int port) {
		dc.startStub(host, port);
	}	
	
	public void connectClient(Object runtime) {
		ArrayList<Object> array;
		try {
			array = dc.stub.getParameters();
			dc.setParameters((Parameters)array.get(0), (ParametersBinding)array.get(1), runtime);
			dc.generateCodeClient();					
			dc.mountCodeClient();
		} catch (RemoteException e) {			
			e.printStackTrace();
		}
	}
}
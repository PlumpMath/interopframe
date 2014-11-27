package interopframe.api;

public interface IFrame {	
	public abstract void remoteBinding(Object runtime);
	public abstract void setParameters(String canonicalClassNameClient, String canonicalInterfaceNameClient, String componentModelClient,
			String canonicalClassNameServer, String canonicalInterfaceNameServer, String componentModelServer);
	public abstract void setParametersBinding(String bindingType, String hostName, int port);
	public abstract void setParametersBinding(String bindingType, String hostName);
	public abstract void setParametersBinding(String bindingType);
	public void startServer(int port);
	public void connectServer(Parameters parameters, ParametersBinding pBinding, Object runtime);
	public void startClient(String host, int port);
	public void connectClient(Object runtime);
}
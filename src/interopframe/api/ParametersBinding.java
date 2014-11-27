package interopframe.api;

import java.io.Serializable;

public class ParametersBinding implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String hostName;
	private int port;
	private String bindingType;

	public ParametersBinding(String bindingType, String hostName, int port) {
		super();
		this.hostName = hostName;
		this.port = port;
		this.bindingType = bindingType;
	}

	public ParametersBinding(String bindingType, String hostName) {
		super();
		this.hostName = hostName;
		this.port = 6767;
		this.bindingType = bindingType;
	}

	public ParametersBinding(String bindingType) {
		this.hostName = "localhost";
		this.port = 6767;
		this.bindingType = bindingType;
	}

	public String getHostName() {
		return hostName;
	}	

	public int getPort() {
		return port;
	}
	
	public String getBindingType() {
		return bindingType;
	}
	
	public Class<?> getBindingGeneratorClass() {
		Class<?> clazz;
		try {
			clazz = Class.forName("interopframe.core.BindingGenerator"+bindingType);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
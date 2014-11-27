package interopframe.api;

import java.io.Serializable;

public class Parameters implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String classNameClient;
	private String interfaceNameClient;
	private String packageClassNameClient;
	private String packageInterfaceNameClient;
	private String componentModelClient;
	
	private String classNameServer;
	private String interfaceNameServer;
	private String packageClassNameServer;
	private String packageInterfaceNameServer;
	private String componentModelServer;

	public Parameters(String canonicalClassNameClient, String canonicalInterfaceNameClient, String componentModelClient,
			String canonicalClassNameServer, String canonicalInterfaceNameServer, String componentModelServer) {		
		this.setPackageClassNameClient(canonicalClassNameClient);
		this.setPackageInterfaceNameClient(canonicalInterfaceNameClient);
		this.componentModelClient = componentModelClient;
		this.setPackageClassNameServer(canonicalClassNameServer);
		this.setPackageInterfaceNameServer(canonicalInterfaceNameServer);
		this.componentModelServer = componentModelServer;
	}

	private void setPackageClassNameClient(String canonicalNameClient) {
		String aux[] = canonicalNameClient.split("\\.");
		String retorno = "";
		this.classNameClient = aux[aux.length - 1];
		for (int i = 0; i < aux.length - 1; i++) {
			retorno += aux[i];
			if (i != aux.length - 2)
				retorno += ".";
		}
		this.packageClassNameClient = retorno;
	}

	private void setPackageInterfaceNameClient(String canonicalNameClient) {
		String aux[] = canonicalNameClient.split("\\.");
		String retorno = "";
		this.interfaceNameClient = aux[aux.length - 1];
		for (int i = 0; i < aux.length - 1; i++) {
			retorno += aux[i];
			if (i != aux.length - 2)
				retorno += ".";
		}
		this.packageInterfaceNameClient = retorno;
	}
	
	private void setPackageClassNameServer(String canonicalNameServer) {
		String aux[] = canonicalNameServer.split("\\.");
		String retorno = "";
		this.classNameServer = aux[aux.length - 1];
		for (int i = 0; i < aux.length - 1; i++) {
			retorno += aux[i];
			if (i != aux.length - 2)
				retorno += ".";
		}
		this.packageClassNameServer = retorno;
	}

	private void setPackageInterfaceNameServer(String canonicalNameServer) {
		String aux[] = canonicalNameServer.split("\\.");
		String retorno = "";
		this.interfaceNameServer = aux[aux.length - 1];
		for (int i = 0; i < aux.length - 1; i++) {
			retorno += aux[i];
			if (i != aux.length - 2)
				retorno += ".";
		}
		this.packageInterfaceNameServer = retorno;
	}

	public String getClassNameClient() {
		return classNameClient;
	}	

	public String getInterfaceNameClient() {
		return interfaceNameClient;
	}	

	public String getComponentModelClient() {
		return componentModelClient;
	}
	
	public String getPackageClassNameClient() {
		return packageClassNameClient;
	}

	public String getPackageInterfaceNameClient() {
		return packageInterfaceNameClient;
	}	
	
	
	public String getClassNameServer() {
		return classNameServer;
	}	

	public String getInterfaceNameServer() {
		return interfaceNameServer;
	}	

	public String getComponentModelServer() {
		return componentModelServer;
	}
	
	public String getPackageClassNameServer() {
		return packageClassNameServer;
	}

	public String getPackageInterfaceNameServer() {
		return packageInterfaceNameServer;
	}
	
	
	public String getCanonicalSkeletonName() {
		return packageInterfaceNameServer+".Skeleton"+classNameServer;
	}
	
	public String getCanonicalProxyName() {
		return packageInterfaceNameClient+".Proxy"+classNameServer;
	}
	
	public String getCanonicalClassNameClient() {
		return packageClassNameClient+"."+classNameClient;
	}
	
	public String getCanonicalInterfaceNameClient() {
		return packageInterfaceNameClient+"."+interfaceNameClient;
	}
	
	public String getCanonicalClassNameServer() {
		return packageClassNameServer+"."+classNameServer;
	}
	
	public String getCanonicalInterfaceNameServer() {
		return packageInterfaceNameServer+"."+interfaceNameServer;
	}
	
	public Class<?> getGeneratorClassClient() {
		Class<?> clazz;
		try {
			clazz = Class.forName("interopframe.core.CodeGenerator"+componentModelClient);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Class<?> getGeneratorClassServer() {
		Class<?> clazz;
		try {
			clazz = Class.forName("interopframe.core.CodeGenerator"+componentModelServer);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Class<?> getAssemblerClassClient() {
		Class<?> clazz;
		try {
			clazz = Class.forName("interopframe.core.CodeAssembler"+componentModelClient);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Class<?> getAssemblerClassServer() {
		Class<?> clazz;
		try {
			clazz = Class.forName("interopframe.core.CodeAssembler"+componentModelServer);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
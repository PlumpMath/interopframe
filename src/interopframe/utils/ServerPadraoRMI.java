package interopframe.utils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerPadraoRMI {
	public static Registry registry;

	public ServerPadraoRMI(int port) {
		if (ServerPadraoRMI.registry == null) {
			try {
				ServerPadraoRMI.registry = LocateRegistry.createRegistry(port);
			} catch (RemoteException re) {
				re.printStackTrace();
			}
		}
	}
	
	public ServerPadraoRMI() {
		if (ServerPadraoRMI.registry == null) {
			try {
				ServerPadraoRMI.registry = LocateRegistry.createRegistry(6767);
			} catch (RemoteException re) {
				re.printStackTrace();
			}
		}
	}
}

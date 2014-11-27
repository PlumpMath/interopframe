package interopframe.core;

import interopframe.api.Parameters;
import interopframe.api.ParametersBinding;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IDistributedConfigurator extends Remote {	
	public void generateCodeServer() throws RemoteException;
	public void generateCodeClient() throws RemoteException;
	public void mountCodeServer() throws RemoteException;
	public void mountCodeClient() throws RemoteException;
	public ArrayList<Object> getParameters() throws RemoteException;
	public void setParametersServer(Parameters parameters, ParametersBinding pBinding) throws RemoteException;
}
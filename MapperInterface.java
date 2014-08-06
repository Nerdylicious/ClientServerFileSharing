/**
 * MapperInterface.java
 *
 * PURPOSE:			The interface of Mapper.
 *
 */

import java.rmi.*;

public interface MapperInterface extends Remote{

	public boolean register(String key, String machineName, String fileName) throws RemoteException;
	public boolean deregister(String key) throws RemoteException;
	public String[] getSources(String searchKey) throws RemoteException;
}

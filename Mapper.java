/**
 * Mapper.java
 *
 * PURPOSE:			Provides functionality for the registry of metadata.
 *
 */

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class Mapper extends UnicastRemoteObject implements MapperInterface{
	
	private Hashtable<String, Info> map;
	
	public Mapper() throws RemoteException{
		map = new Hashtable<String, Info>();
	}
	
	public synchronized boolean register(String key, String machineName, String fileName) throws RemoteException{
		
		boolean success = true;
		if(!map.containsKey(key)){
			Info info = new Info(machineName, fileName);
			map.put(key, info);
		}
		else{
			success = false;
		}
		
		return success;
	}
	
	public synchronized boolean deregister(String key) throws RemoteException{
	
		boolean success = true;

		Info result = map.remove(key);
		if(result == null){
			success = false;
		}

		return success;
	}
	
	public synchronized String[] getSources(String searchKey) throws RemoteException{
		
		ArrayList<String> list = new ArrayList<String>();
		
		for(Map.Entry<String, Info> pair: map.entrySet()){
			if(pair.getKey().toLowerCase().contains(searchKey.toLowerCase())){
				list.add(pair.getKey() + "," + pair.getValue().getMachineName() + "," + pair.getValue().getFileName());
			}
		}
		
		String[] sources = new String[list.size()];
		for(int i = 0; i < list.size(); i ++){
			sources[i] = list.get(i);
		}
		
		return sources;
	}
}

/**
 * Server.java
 *
 * NOTE:			This server must run on owl.cs.umanitoba.ca
 *
 */

import java.rmi.*;

public class Server {

	public static void main (String[] argv) {
		
		try { 
			Naming.rebind("//owl.cs.umanitoba.ca:3111/Mapper", new Mapper());
			System.out.println ("\n\nMapping Server is ready.\n");
		}
		catch (Exception e) {
			System.out.println ("Server failed: " + e);  
		}
	}
}

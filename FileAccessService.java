/**
 * FileAccessService.java
 *
 * PURPOSE:			Part of the client that provides the server
 *					functionality. It allows other peers to retrieve 
 *					files from the machine the client program is running on.
 *
 */

import java.net.*;
import java.io.*;

public class FileAccessService implements Runnable{

	public FileAccessService(){ }

	//runs concurrently with the text based interface in Client.java
	public void run(){
		
		BufferedReader br = null;
		InetAddress addr = null;
		ServerSocket servSock = null;
		Socket cliSock = null;
		
		try{
			addr = InetAddress.getLocalHost();
			servSock = new ServerSocket(3110, 5, addr);
		}
		catch(Exception e){
			System.out.println("Creation of ServerSocket failed.");
			System.exit(1);
		}
				
		while(true){
				
			try{
				cliSock = servSock.accept();
			}
			catch(Exception e){
				System.out.println("Accept failed.");
				System.exit(1);
			}
			
			try {
				br = new BufferedReader(new InputStreamReader(cliSock.getInputStream()));
			} catch (Exception e) {
				System.out.println("Couldn't create socket input stream.");
				System.exit(1);
			}
			
			try{

				String fileName = br.readLine();			
				File file = new File(fileName);
				
				if(file.isFile() && file.canRead()){

						byte[] temp = new byte[(int)file.length()];
			
						FileInputStream filestream = new FileInputStream(file);
						BufferedInputStream bufstream = new BufferedInputStream(filestream);
			
						bufstream.read(temp, 0, temp.length);
						OutputStream outstream = cliSock.getOutputStream();
			
						outstream.write(temp, 0, temp.length);
						outstream.flush();
				
						outstream.close();
						bufstream.close();
						filestream.close();
				}
				
				br.close();
				cliSock.close();
			}
			catch(Exception e){
				System.out.println("Error reading/writing file.");
				System.exit(1);
			}
		}
	}
}

/**
 * Client.java
 *
 * PURPOSE:			The client that has a text based user interface.
 * NOTE: 			The other part of the client that provides it's server role is
 *					in FileAccessService.java
 *
 */

import java.io.*;
import java.lang.*;
import java.net.*;
import java.rmi.*;
import java.util.*;

public class Client{
	
	public static void main(String[] argv){
	
		try{
			MapperInterface mapper = (MapperInterface)Naming.lookup("//owl.cs.umanitoba.ca:3111/Mapper");
			
			//the FileAccessService is the code that gives this client it's server role (allowing peers to get files from this peer)
			Thread myThread = new Thread(new FileAccessService());
			myThread.start();
			
			int fileSize = 8000000;
			int length;
			int totalLength = 0;
			
			InetAddress addr = null;
			PrintWriter writer = null;
			Socket sock = null;			
			
			Scanner input = new Scanner(System.in);
			String in;
			String[] values;
			String[] sources = null;
			
			String regexOp1 = "^[a-zA-Z0-9\\s]{1,20},\\S+,\\S+$";
			String regexOp2Op3 = "^[a-zA-Z0-9\\s]{1,20}$";
			String regexOp4 = "\\d+,\\S+";
			
			System.out.println("\nPlease select an option:\nOption 1 - register code source\n" + 
				"Option 2 - de-register code source\nOption 3 - retrieve information\n" + 
				"Option 4 - retrieve code\nOption 5 - quit program");

			boolean is_populated = false;
			
			while(true){
			
				System.out.print("\nEnter an option (1/2/3/4/5): ");
				String option = input.nextLine();
				
				if(option.equals("1")){
					
					System.out.println("\nInstructions: To register a code source enter a\n" + 
						"key, machine name and filename\nin this format: key,machineName,fileName " +
						"(no spaces)\nmachineName must be in the format: bird.cs.umanitoba.ca\n");
						
					System.out.print("Enter key,machineName,fileName: ");
					
					in = input.nextLine();

					if(in.matches(regexOp1)){
					
						values = in.split(",");					
						boolean success = mapper.register(values[0], values[1], values[2]);
					
						if(success){
							System.out.println("\n<Registration Successful>");
						}
						else{
							System.out.println("\n<Error: An entry with this key is already specified " + 
								"in the repository\nPlease choose a different key>");
						}
					}
					else{
						System.out.println("\n<Error: key,machineName,fileName triple is not in " +
							"the correct format>");
					}
				}
				else if(option.equals("2")){
				
					System.out.println("\nInstructions: To de-register a code source enter\n" + 
						"a key in this format: key\n");
					
					System.out.print("Enter key: ");
					in = input.nextLine();
					
					if(in.matches(regexOp2Op3)){
				
						boolean success = mapper.deregister(in);
					
						if(success){
							System.out.println("\n<De-registration Successful>");
						}
						
						else{
							System.out.println("\n<Error: An entry with this key does not exist " + 
								"in the repository\nPlease choose a different key>");
						}
					}
					else{
						System.out.println("\n<Error: key is not in the correct format>");
					}

				}
				else if(option.equals("3")){
				
					System.out.println("\nInstructions: To retrieve information enter\na key in this format: key\n");
					
					System.out.print("Enter key: ");
					in = input.nextLine();
					
					if(in.matches(regexOp2Op3)){
										
						sources = mapper.getSources(in);
					
						if(sources.length > 0){
					
							System.out.println("\nFound (" + sources.length + ") code sources " + 
								"containing the key \"" + in + "\"");
							
							System.out.println("Entry #i: key, machineName, fileName\n");
						
							for(int i = 0; i < sources.length; i ++){
						
								System.out.println("Entry #" + i + ": " + sources[i]);
							}
						
							is_populated = true;
						}
						else{
							System.out.println("\nNo code sources were found containing the key \"" + in + "\"");
						}
					}
					else{
						System.out.println("\n<Error: key is not in the correct format>");
					}
				}

				else if(option.equals("4")){
				
					if(is_populated){
					
						System.out.println("\nInstructions: To retrieve the code source specify " + 
							"the entry number and\na new file name in this format: entry#,filename\n");
					
						System.out.print("Enter entry#,filename: ");
						in = input.nextLine();
					
						//all code for retrieving a file from a peer
						if(in.matches(regexOp4)){
						
							values = in.split(",");
							int entry = Integer.parseInt(values[0]);

							if((entry >= 0) && (entry < sources.length)){
							
								String localFileName = values[1];
						
								String source = sources[entry];
								values = source.split(",");
						
								String remoteMachineName = values[1];
								String remoteFileName = values[2];

								try{
									addr = InetAddress.getByName(remoteMachineName);
									sock = new Socket(addr, 3110);
								}
								catch(Exception e){
									System.out.println("Creation of client's Socket failed. Please " + 
										"make sure that the client program is running on " + remoteMachineName +
										" make sure that the machinename is in the format: bird.cs.umanitoba.ca");
									System.exit(1);
								}
						
								try{
									writer = new PrintWriter(sock.getOutputStream(), true);
								}
								catch(Exception e){
									System.out.println("Socket output stream failed.");
									System.exit(1);
								}
						
								//send the name of the file we want from the server
								writer.println(remoteFileName);
						
								byte[] temp = new byte[fileSize];

									InputStream instream = sock.getInputStream();

									length = instream.read(temp, 0, temp.length);
									totalLength = length;
									
									if(length == -1){
										System.out.println("\n<Error: The file " + remoteFileName + 
											" does not exist or is not readable on " + remoteMachineName + ">"); 
									}
									else{
						
										while(length > -1){
						
											length = instream.read(temp, totalLength, (temp.length - totalLength));
							
											if(length > 0){
												totalLength += length;
											}
										}
						
										File file = new File(localFileName);
										FileOutputStream filestream = new FileOutputStream(file);
										BufferedOutputStream bufstream = new BufferedOutputStream(filestream);
										
										bufstream.write(temp, 0, totalLength);
										bufstream.flush();
										
										bufstream.close();
										filestream.close();
										
										System.out.println("\n<File retrieved successfully from " + remoteMachineName + ">");
									}
						
									totalLength = 0;

									instream.close();
									writer.close();
									sock.close();

							}
							else{
								System.out.println("\n<Error: entry# is not valid>");
							}				
						}
						else{
							System.out.println("\n<Error: entry#,filename double is not in the correct format>");
						}
					}
					else{
						System.out.println("<Error: You must view the source list first using Option 3>");
					}
					
				}
				else if(option.equals("5")){
				
					System.out.println("\nGoodbye!\n");
					System.exit(0);
				}
				else{
					System.out.println("\n<Error: Please enter a valid option>");
				}
			}
		
		}
		catch(Exception e){		
			System.out.println("Client exception: " + e);
		}
		
	}

}

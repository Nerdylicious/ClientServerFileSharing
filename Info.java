/**
 * Info.java
 *
 */

import java.util.*;

public class Info{

	private String machineName;
	private String fileName;
	
	public Info(String machineName, String fileName){
	
		this.machineName = machineName;
		this.fileName = fileName;
	}
	
	public String getMachineName(){
	
		return this.machineName;
	}
	
	public String getFileName(){
	
		return this.fileName;
	}
}

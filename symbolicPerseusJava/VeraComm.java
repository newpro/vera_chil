import java.io.*;

public class VeraComm {
	private static BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	
	public static void send(String msg){
		try{
			System.out.println("DUMP: " + msg);//DUMP ensure it is a SNAP recommandation, not a info
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String readLine(){
		String s = null;
		try{
			s = bufferRead.readLine();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return s;
	}
	
	//test
	
	public static void main(String[] args) {
		while(true){
			String s = readLine();
			if(s.equals("ENDOPERATION")) {
				send("SNAP end");
				break;
			}
			send(s);
		}
		/*
		String s = readLine();
		send(s);
		s = readLine();
		if(s.equals("ENDOPERATION")) {
			send("end here");
		}
		*/
		return;
	}
	
	
}

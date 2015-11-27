import java.io.*;

public class VeraComm {
	private static BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	public static void send(String msg){
		try{
			System.out.println("from vera: " + msg);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String receive(){
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
			String s = receive();
			if(s.equals("ENDOFMESSAGE")) break;
			send(s);
		}
		return;
	}
}

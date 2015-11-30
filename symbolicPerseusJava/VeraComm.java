import java.io.*;

public class VeraComm {
	public static void send(String msg){
		System.out.println(msg);
		System.out.println("X");
	}
	
	public static String readLine(){
		String s = null;
		try {
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			s = bufferRead.readLine();
			String line = s;
			while(line.equals("X")==false) {
				s = line;
				line = bufferRead.readLine();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	//test
	
	public static void main(String[] args) {
		while(true){
			String s = readLine();
			System.out.println("JAVA:" + s + "/end");
			if(s.equals("ENDOPERATION")) {
				send("SNAP end");
				break;
			}
			send(s);
		}
		return;
	}
	
	
}

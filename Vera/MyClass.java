import java.io.*;

public class MyClass {
	public static void main(String[] args) {
		try {
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			//PrintWriter writer = new PrintWriter("result.txt", "UTF-8");
			String s = bufferRead.readLine();
			while(s.equals("ENDOFMESSAGE")==false) {
				//writer.println(s);
				Thread.sleep(1000);
				System.out.println("from python: " + s);//send to python
				s = bufferRead.readLine();
			}
			//writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}


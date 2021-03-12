package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MafiaClient {

	public static void main(String[] args) throws IOException {
		if(args.length !=2) {
			System.out.println("사용법 : 클라이언트실행은 \'java 패키지명.파일명 ip주소 포트번호\' 형식으로 입력");
			System.exit(1);
		}
		try {
			Socket s1 = new Socket(args[0], Integer.parseInt(args[1]));
			System.out.println("게임 연결...");
			DataOutputStream dos = new DataOutputStream(s1.getOutputStream());
			DataInputStream dis = new DataInputStream(s1.getInputStream());
			new EnterGUI(dis, dos, s1) {
				public void closeWork() throws IOException{
					System.exit(0);
				}
			};
		} catch (Exception e) {
			System.out.println("here");
		}
	}
}
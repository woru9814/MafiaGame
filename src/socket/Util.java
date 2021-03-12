package socket;

import java.util.ArrayList;
import java.util.List;

public class Util {
	static String[] newarr(String str) {
		List<String> arr = new ArrayList<String>();// !join은 누가 들어왔다는 의미임으로 길이가 고정인 배열이 아닌 리스트로 받을준비
		for (String tt : str.split("!")) {// 전체 String내용을 "!"로 잘라내여 바로 하나씩 tt에 담음
			if (!tt.equals("")) {// 시작을 !로 시작했기때문에 빈 문자열이 만들어져서 그 내용을 제외하는 내용
				tt.split("　");
				arr.add(tt);// 빈 문자열이 아닌 tt내용을 list에 추가
			}
		}
		String[] a = new String[arr.size()];// 리스트 길이만큼의 배열을 생성
		for (int i = 0; i < arr.size(); i++) {// 리스트의 길이만큼 배열에 넣기위한부분
			a[i] = arr.get(i);
		}
		return a;
	}

	public static int[] newint(String str) {
		List<String> STR = new ArrayList<String>();
		for (String str2 : str.split("/")) {
			STR.add(str2);
		}
		int[] arr = new int[STR.size()];
		for (int i=0;i<STR.size();i++) {
			arr[i] = Integer.parseInt(STR.get(i));
		}
		return arr;
	}
}

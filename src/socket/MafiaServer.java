package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vo.GameSet;

class ServerClass {
	boolean test = false;// timethread 에서 낮/밤/투표시간이 바뀔때마다 초기화를 위한 임시변수
	static String Mafia_Ch = "";//마피아 지목대상
	static String Police_Ch = "";//경찰 지목대상
	static String Doctor_Ch = "";//의사 지목대상
	static int Players = 0;//게임 시작했을시 플레이어수
	static boolean game = false; // '게임시작' 버튼 누를시 true, 게임 실행
	static boolean gameStart = true; // 게임시작 시 나오는 문구 출력을 위한 변수
	static int team_Mafia_count = 2;//게임 시작후 마피아팀 숫자 /초기 2
	static int team_Citizen_count = 6;//게임 시작후 시민팀 숫자 /초기 6
	static boolean VOTE = false;//현재 투표상태인지
	static int cnt = 0;//투표 총원 카운트
	static int maxN = -1;// 현재 최다 득표 표수
	int gameSet; // 1 = 낮 시간
	int vote_user_count = 0;
	// 2 = 밤 시간
	// 3 = 투표 시간
	ArrayList<ThreadServerClass> threadList = new ArrayList<ThreadServerClass>();
	//접속자가 담긴 리스트
	ArrayList<ThreadServerClass> deathList = new ArrayList<ThreadServerClass>();
	//죽은사람이 담긴 리스트
	ArrayList<ThreadServerClass> MafiaList = new ArrayList<ThreadServerClass>();
	//마피아가 담긴 리스트
	private String userlist = "";//클라이언트에 현재 접속인원의 닉네임 출력용 변수
	GameSet Gs = new GameSet();//게임 설정 초기선언
	static int Day;//낮시간
	static int Night;//밤시간
	static int VoteTime = 60;//투표시간

	public ServerClass(int portno) throws IOException {
		Socket s1 = null;
		ServerSocket ss1 = new ServerSocket(portno);
		ThreadTimerClass ttc = new ThreadTimerClass();
		System.out.println("서버가동");
		ttc.start();

		while (true) {
			s1 = ss1.accept();
			System.out.println("접속주소 : " + s1.getInetAddress() + ", 접속포트 : " + s1.getPort());
			ThreadServerClass tServer1 = new ThreadServerClass(s1);
			tServer1.start();
			threadList.add(tServer1);

			System.out.println("접속자 수 : " + threadList.size() + "명");
		}
	}

	class ThreadTimerClass extends Thread {

		public void run() {
			try {
				while (true) {//계속 돌면서 game변수가 true인지 false인지 계속 체크
					System.out.print("");//이유는 모르지만 이게 없으면 쓰레드 안돌아감
					if (game) {//true
						if (gameStart) {
							sendChat("!chat- 게임이 시작되었습니다. -\n- 게임은 낮으로 시작됩니다. -\n- 마피아를 가려내기 위한 대화를 시작해주세요. -");
							gameSet = 1;
							gameStart = false;
						}
						Thread.sleep(1000);
						// 낮
						if (gameSet == 1) {
							if (!test) {//낮의 시작
								sendDay(gameSet);//낮이라는걸 클라에 전송
								Day = Gs.getDay();
								vote_user_count = 0;
								test = true;//시작을 해서 다시 true로
							}
							Day -= 1;
							if (Day <= 0) { // 낮의 끝
								test = false;//낮이 끝나고 다음 시작을 위해 false
								sendChat("!chat- 해가 지고있습니다. -");
								gameSet = 3;
								Day = Gs.getDay();
							} else if (Day <= 30  && (Day % 5 == 0 || Day <= 5)) {
								sendChat("!chat- 낮 시간이 " + Day + "초 남았습니다. -");
							}

							// 밤
						} else if (gameSet == 2) {
							if (!test) {
								sendDay(gameSet);
								sendChat("!chat- 어두운 밤이 찾아왔습니다. -\n- 시민들은 숨을 죽이고 해가 뜨기를 기다립니다. -");
								Night = Gs.getNight();
								test = true;
								for (ThreadServerClass tmp : threadList) {
									tmp.skil_Action = false;
									if (tmp.myjob != "시민" && !tmp.death) {
										tmp.dos.writeUTF("!Btn_ON");
									} else
										tmp.dos.writeUTF("!OK");
								}
							}
							Night -= 1;
							if (Night <= 0) { // 밤의 끝
								test = false;
								for (ThreadServerClass tmp : threadList) {
									if (!tmp.myjob.equals("시민"))
										tmp.dos.writeUTF("!VoAction?");// 행동 했니?
									// 클라이언트에서 행동하면 true 안하면 false false일 경우 강제로 행동 실행
									Thread.sleep(500);
								}
								sendChat("!btn_off");// 모두의 버튼 비활성화
//----밤이 끝나고 마피아와 의사의 선택을 비교해 킬처리 하는부분----------------------------------------------------------------
								if (!Mafia_Ch.equals("")) {//마피아가 선택했는지
									if (Mafia_Ch.equals(Doctor_Ch)) {//마피아의 선택과 의사의 선택이 같음처리
										sendChat("!chat의사가 마피아의 공격으로부터 플레이어를 지켜냈습니다.");
									} else {//선택이 다름 처리
										for (ThreadServerClass tmp : threadList) {//모든 리스트를 불러오고
											if (tmp.nickname.equals(Mafia_Ch)) {//마피아가 선택한 닉네임과 일치하는 쓰레드을 찾기위함
												tmp.death = true;//해당 쓰레드 사망처리
												deathList.add(tmp);//사망리스트에 추가
												if (tmp.myjob == "마피아") {//죽은사람이 마피아라면
													team_Mafia_count -= 1;//남은 마피아 숫자를 줄임
												} else
													team_Citizen_count -= 1;//아니면 시민을 줄임
												tmp.dos.writeUTF("!OK");
											}
										}
										sendChat("!killmsg" + Mafia_Ch + "님이 밤중에 마피아의 공격을 받고 사망하셨습니다.");
									}
								}
//------------------------------------------------------------------------------------------------------------
								Mafia_Ch = ""; //
								Police_Ch = "";//// 밤이 끝나기전 각자의 선택을 초기화
								Doctor_Ch = "";//
								sendChat("!chat- 밤이 끝났습니다. -");
								sendChat("!chat- 해가 떴습니다. -");
								Night = Gs.getNight();
								gameSet = 1;
								Progress();
							} else if (Night <= 20 && (Night % 5 == 0 || Night <= 5)) {
								sendChat("!chat- 밤 시간이 " + Night + "초 남았습니다. -");
							}

						} else if (gameSet == 3) {
							if (!test) {
								sendDay(gameSet);
								sendChat("!vote_st");
								VoteTime = 60;
								cnt = 0;
								VOTE = true;
								for (ThreadServerClass tmp : threadList) {
									tmp.vote_count = 0;
								}
								sendChat("!chat- 마피아를 가려내는 투표 시간입니다. -\n- 생존자들은 투표를 시작해주세요. -");
								test = true;
							}
							VoteTime -= 1;
							if (VoteTime <= 0 || Players - deathList.size() == cnt) {
								test = false;
								if (Players - deathList.size() != cnt) {
									for (ThreadServerClass tmp : threadList) {
										tmp.dos.writeUTF("!VoAction?");
									}
								}
								sendChat("!chat- 투표가 끝났습니다. -");
								gameSet = 2;
								VOTE = false;
//-------------------------최다 득표자를 찾기위한 부분 ----------------------------------
								maxN = -1;//최다득표숫자
								ThreadServerClass tmpclass = null;//리스트를 돌면서 최다득표자를 담기위한 임시변수
								boolean tmp2 = false;//같은 표수의 사람이 있으면 사망처리 미진행을 위해 만듬
								for (ThreadServerClass tmp : threadList) {//모든 쓰레드리스트를 불러와
									if (maxN < tmp.vote_count) {//현재 최다득표숫자보다 크다면
										tmpclass = tmp;//해당 쓰레드를 임시변수에 할당
										maxN = tmp.vote_count;//최다득표수를 해당 쓰레드가 가진 표수와 같게만듬
										tmp2 = false;//같은 표수사람이 없음
									} else if (maxN == tmp.vote_count) {//최다득표수와 같은 쓰레드가 나오면
										tmp2 = true;//같은 표수가 있다고 저장
									}
								}
								//----------------------------------------------
								if (tmp2)//체크가 끝난후 tmp2가 true라면
									tmpclass = null;//최다득표자를 null로 초기화시키고
								sendKill(tmpclass);//해당 메서드에 전달

								Progress();//투표 처리후 마피아수와 시민수를 체크해 게임이 끝났는지 판단하는 메서드
							} else if (Day <= 10) {
								sendChat("!chat- 투표 시간이 " + VoteTime + "초 남았습니다. -");
							}
						}
					}
				}
			} catch (IOException | InterruptedException e) {
			}
		}

	}

	class ThreadServerClass extends Thread {
		boolean vost = false;
		String myjob = "시민";
		boolean skil_Action = false;
		boolean death = false;
		Socket s1;
		DataInputStream dis;
		DataOutputStream dos;
		int vote_count = 0;
		String nickname = "";
		String read;

		public ThreadServerClass(Socket s1) throws IOException {
			this.s1 = s1;
			dis = new DataInputStream(s1.getInputStream());
			dos = new DataOutputStream(s1.getOutputStream());
		}

		@Override
		public void run() {
			try {
				if (dis != null) {
					while (true) {//닉네임 중복체크
						boolean Nick_Check = false;//중복이 있다면 true
						nickname = dis.readUTF();
						if (!game) {//게임 진행여부 (게임중 입장을 막기위함)
							for (ThreadServerClass tmp : threadList) {
								if (nickname.equals(tmp.nickname) && !s1.equals(tmp.s1))
									Nick_Check = true;
								//받은 닉네임과 일치하는 닉네임을 쓰레드리스트에서 검색후 발견시 true
							}
							if (!Nick_Check) {//미발견시 실행
								userlist += "!" + nickname + "　";
								if (threadList.size() == 1)//최초 접속자면 호스트로 연결
									dos.writeUTF("!Success!Host");
								else if (threadList.size() > 1)//그후엔 유저로 연결
									dos.writeUTF("!Success!User");
								break;
							} else if (Nick_Check) {
								dos.writeUTF("!Fail");//닉네임 중복되었다는 메시지 전달
							}
						} else {
							dos.writeUTF("!StartErr");//게임 진행중이라는 메시지 전달
						}
					}
				}
				while (dis != null) {
					if (!death) {
						if (dis != null && VOTE && game) {//투표진행부분
							String read = dis.readUTF();
							if (read.contains("!vote_run")) {
								String str = read.substring(9);
								if (str.equals("!skip")) {
									cnt++;
									dos.writeUTF("!vote_skip");
								} else {
									dos.writeUTF("!chat" + str + "님을 투표하셨습니다.");
									for (ThreadServerClass tmp : threadList) {
										if (tmp.nickname.equals(str)) {
											cnt++;
											tmp.vote_count++;
										}
									}
								}
								dos.writeUTF("!btn_off");
							}
						} else if (dis != null && !VOTE) {//투표 미진행
							read = dis.readUTF();
							if (gameSet == 2 && game) {//밤일경우 트수직업 능력사용
								if (myjob.equals("마피아")) {
									if (read.contains("!userChat")) {
										if (team_Mafia_count > 1)
											mafiaChat(read);
										else
											dos.writeUTF("혼자임");
									} else if (read.contains("!Choice")) {
										String str = read.substring(7);
										Mafia_Ch = str;
										dos.writeUTF("!Choice");
									} else if (read.contains("!dsadsaaew")) {
										if (team_Mafia_count > 1)
											mafiaChat(read);
									}
								} else if (myjob.equals("경찰") && !skil_Action) {
									if (read.contains("!Choice")) {
										String str = read.substring(7);
										Police_Ch = str;
										for (ThreadServerClass tmp : threadList) {
											if (tmp.nickname.equals(str)) {
												dos.writeUTF("!Choice");
												String imsi = tmp.myjob == "마피아" ? "마피아" : "시민";
												dos.writeUTF("!chat" + str + "님은 [" + imsi + "]팀 입니다.");
											}
										}
										skil_Action = true;
									}
								} else if (myjob.equals("의사") && !skil_Action) {
									if (read.contains("!Choice")) {
										String str = read.substring(7);
										dos.writeUTF("!chat" + str + "님을 선택 하셨습니다.");
										dos.writeUTF("!Choice");
										Doctor_Ch = str;
										skil_Action = true;
									}
								}
								//밤일경우 끝
							} else if (read.contains("!chat")) {//서버챗
								sendChat(read);
							} else if (read.contains("!userChat")) {//유저챗
								sendChat(read);
							} else if (read.contains("!set")) {//셋팅
								String str = read.substring(4);
								int[] intarr = Util.newint(str);
								Gs.setDay(intarr[0]);
								Gs.setNight(intarr[1]);
								Gs.setMf_count(intarr[2]);
								Gs.setPl_count(intarr[3]);
								Gs.setDt_count(intarr[4]);
								Gs.setCt_count(intarr[5]);
								sendChat("!chat- 게임 세팅 변경! -");
							} else if (read.contains("!CheakSuc")) {
								//닉네임 중복체크 성공후 들어온사람이있으면 모두의 유저리스트를 다시보이게함
								sendChat("!join" + userlist);
							} else if (read.contains("!readarr")) {//설정 눌렀을시 현재 게임설정을 바로 보이게 하기위해 전송
								dos.writeUTF(Gs.toString());
							} else if (read.contains("!start") && !game) {//게임시작
								//sendChat("!chat"); ??왜있는지 몰라서 일단 주석처리
								boolean job = JobSet();//직업할당에 성공하면 true반환
								if (job) {//할당 성공시 게임 시작을 위해 셋팅
									Players = threadList.size();
									game = true;
									gameStart = true;
									sendChat("!start게임을 시작합니다.\n" + "게임의 낮시간은 " + Gs.getDay() + "초이고, " + "밤시간은 "
											+ Gs.getNight() + "초이며, " + "마피아는 " + Gs.getMf_count() + "명, " + "경찰은 "
											+ Gs.getPl_count() + "명, " + "의사는 " + Gs.getDt_count() + "명," + "시민은 "
											+ Gs.getCt_count() + "명입니다.\n");
								} else {
									dos.writeUTF("!chat서버 시작 오류!!");
								}
							} else if (read.contains("!vote_st") && game) {// 투표 시작
								if (vost) {
									dos.writeUTF("!chat이미 누른 버튼입니다.");
								} else {
									vote_user_count ++;
									if(vote_user_count > (Players - deathList.size()) / 2) {
										sendChat("!chat- 과반수 이상이 동의하여 낮을 건너뜁니다. -");
										test = false;
										gameSet = 3;
									}else {
										sendChat("!chat- 누군가가 낮을 건너뛰기를 원합니다 -\n- ( "+ vote_user_count +" : " + (Players - deathList.size()) + " )");
									}
									vost = false;
								}
							} else if (read.contains("!OK")) {
								// 서버 - 클라이언트간 빈 내용을 주고받아 i/o 초기화
							}
						}
					} else if (death && game) {//게임 진행중 죽은사람의 채팅
						read = dis.readUTF();
						if(read.contains("!userChat")) {
							//유저챗을 받으면 사망자끼리만 전달
							deathChat(read);
						}
					}
				}
			} catch (IOException e) {
			} finally {
				for (int i = 0; i < threadList.size(); i++) {
					try {
						if (s1.equals(threadList.get(i).s1)) {
							if (game && !death) {//해당 쓰레드가 사망 상태가 아닌경우
								deathList.add(threadList.get(i));
								threadList.get(i).death = true;
								//죽은사람에 담고 사망처리
								if (threadList.get(i).myjob == "마피아") {
									team_Mafia_count -= 1;
								} else {
									team_Citizen_count -= 1;
								}
							}
							threadList.remove(i);
							userlist = userlist.replaceAll("!" + nickname + "　", "");// 나간사람의 닉네임을 String 배열 형태와 같은
							Progress();
							//유저리스트를 업데이트하고 게임 진행상태 체크후
						}
						//전달하기위해 if문 밖으로 
						sendChat("!out" + userlist);// 업데이트된 목록을 클라이언트로 보내고 !out에 대한 행동 처리
						dos.close(); dis.close(); s1.close();
					} catch (IOException e) {
					}
				}
				System.out.println("접속자 수 : " + threadList.size() + "명");
			}
		}

	}

	public void GameEnd() throws IOException {//게임 종료 메서드
		game = false;
		VOTE = false;
		for (ThreadServerClass tmp : threadList) {
			tmp.myjob = "시민";
			tmp.death = false;
			tmp.vote_count = 0;
			tmp.dos.writeUTF("!GameEnd");
		}
		Mafia_Ch = "";
		Police_Ch = "";
		Doctor_Ch = "";
		team_Mafia_count = 2;
		team_Citizen_count = 6;
		cnt = 0;
		maxN = -1;// 현재 최다 득표 표수
		gameSet = 1;// 게임을 '낮'으로 미리 설정
		Day = Gs.getDay(); // '낮' 시간 초기화
		Night = Gs.getNight(); // '밤' 시간 초기화
		VoteTime = 30; // '투표' 시간 초기화
		gameStart = true; // 게임시작 시 나오는 문구 안나오게 하기 위한 초기화

		Gs = new GameSet();
		deathList = new ArrayList<ThreadServerClass>();
		MafiaList = new ArrayList<ThreadServerClass>();
	}

	public void deathChat(String chat) throws IOException {//죽은사람 채팅 메서드
		for (ThreadServerClass tmp : deathList) {
			tmp.dos.writeUTF(chat);
		}
	}

	public void mafiaChat(String chat) throws IOException {//마피아 챗 메서드
		for (ThreadServerClass tmp : MafiaList) {
			tmp.dos.writeUTF(chat);
		}
	}

	public void sendChat(String chat) throws IOException {//메세지 전송 메서드
		for (ThreadServerClass tmp : threadList) {
			if(tmp.death && (chat.contains("!chat") || chat.contains("!userChat") || chat.contains("!out") || chat.contains("!killmsg"))) tmp.dos.writeUTF(chat);
			else if (!tmp.death) tmp.dos.writeUTF(chat);
			else if (!game) tmp.dos.writeUTF(chat);
		}
	}

	public void sendJob(List<String> job) throws IOException {//랜덤 할당된 직업을 클라에 전송
		for (int i = 0; i < threadList.size(); i++) {
			threadList.get(i).dos.writeUTF("!YouJob" + job.get(i));
			threadList.get(i).myjob = job.get(i);
			if (job.get(i) == "마피아") {
				team_Mafia_count += 1;
				MafiaList.add(threadList.get(i));
			} else {
				team_Citizen_count += 1;
			}
		}
	}

	public boolean JobSet() throws IOException {//게임 시작시 직업 할당하는부분 인원이 안맞으면 false반환
		int[] temp = { Gs.getMf_count(), Gs.getPl_count(), Gs.getDt_count(), Gs.getCt_count() };
		int Job = 1; // 1부터 마피아
		List<String> JobArr = new ArrayList<String>();
		for (int i : temp) {
			for (int o = 0; o < i; o++) {
				if (Job == 1)
					JobArr.add("마피아");
				if (Job == 2)
					JobArr.add("경찰");
				if (Job == 3)
					JobArr.add("의사");
				if (Job == 4)
					JobArr.add("시민");
			}
			Job++;
		}
		if (threadList.size() == JobArr.size()) {
			game = true;
			team_Citizen_count = 0;
			team_Mafia_count = 0;
			Collections.shuffle(JobArr);
			sendJob(JobArr);
			return true;
		} else
			System.out.println("유저수 != 직업");
		return false;
	}

	public void Progress() throws IOException {//게임 진행상황 체크
		if (team_Mafia_count >= team_Citizen_count || team_Mafia_count == 0) {
			if (team_Mafia_count == 0) {
				// 시민승
			} else {
				// 마피아승
			}
			GameEnd();
		}
	}

	public void sendDay(int i) throws IOException {//시간을 클라에 보내기 위함
		String str = i == 1 ? "Day" : i == 2 ? "Night" : i == 3 ? "VoteTime" : "";
		for (ThreadServerClass tmp : threadList) {
			if(!tmp.death)
			tmp.dos.writeUTF("!SetTime" + str);
		}
	}

	public void sendKill(ThreadServerClass TSClass) throws IOException {//죽은사람을 보내기위함
		String str = "아무도 죽지 않았습니다.";
		if (TSClass != null) {
			str = TSClass.nickname;
			TSClass.death = true;
			deathList.add(TSClass);
			if (TSClass.myjob == "마피아") {
				team_Mafia_count -= 1;
			} else
				team_Citizen_count -= 1;
			TSClass.dos.writeUTF("!OK");
		}
		for (ThreadServerClass tmp : threadList) {
			tmp.vote_count = 0;
			if(TSClass == null)	tmp.dos.writeUTF("!killmsg" + str);
			else tmp.dos.writeUTF("!killmsg" + str + "님이 투표로 처형당했습니다");
		}
	}
}

public class MafiaServer {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("사용법 : 서버 실행은 \'java 패키지명.파일명 포트번호\' 형식으로 입력");
		}

		new ServerClass(Integer.parseInt(args[0]));
	}

}
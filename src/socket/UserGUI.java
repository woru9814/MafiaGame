package socket;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class UserGUI extends JFrame implements Runnable, ActionListener {
	String time = "";
	boolean Act = false;
	Socket s1;
	JPanel contentPane;
	JTextField txt_Job;
	DataInputStream dis;
	DataOutputStream dos;
	String nickname;
	JTextField chat = new JTextField();
	JTextPane croom = new JTextPane();
	
	JList list = new JList();
	JList list2 = new JList();

	JTextField nameField = new JTextField();
	JScrollPane scrollPane = new JScrollPane(croom);
	JButton skipButton = new JButton("skip");
	JButton exitButton = new JButton("게임종료");
	JTextArea tip = new JTextArea();
	JButton killButton = new JButton("kill");
	JButton btnvotestart = new JButton("투표시작");
	ImageIcon image = new ImageIcon("img/ChatGUI.jpg");

	int listIndex; // JList 에서 선택한 값의 수를 알기위한 변수
	String itemtest = "";
	int result; // YES OR NO 중 선택한 것
	
	String[] s;
	String[] alive;
	String str2 = "";
	List<String> arr2;

	public UserGUI(DataOutputStream dos, DataInputStream dis, Socket s1, String nickname) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.dos = dos;
		this.dis = dis;
		this.s1 = s1;
		this.nickname = nickname;

		this.setTitle("마피아게임");
		setBounds(100, 100, 1000, 800);
		contentPane = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(image.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);

		chat.setBounds(93, 702, 754, 49);
		contentPane.add(chat);
		chat.setColumns(10);

		// 채팅전송 버튼 Listener
		JButton SendButton = new JButton("전송");
		SendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dos.writeUTF("!userChat" + nickname + " : " + chat.getText());// !chat는 채팅이라는걸 서버에 알리기위해
				} catch (IOException e2) {
				}
				chat.setText("");
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// JList Selected

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					if (time.equals("Night") && txt_Job.getText().equals("마피아")) {
						try {
							itemtest = ((String) list.getModel().getElementAt(list.getSelectedIndex())).replaceAll("　",
									"");
							
							dos.writeUTF("!dsadsaaew" + Integer.toString((int) list.getSelectedIndex()));
						} catch (IOException e1) {
						}
					} else {
						itemtest = ((String) list.getModel().getElementAt(list.getSelectedIndex())).replaceAll("　", "");
						list.getModel().getSize();
//						Object str2 = list.getModel().getElementAt(list.getSelectedIndex());
//						result2 = list.getSelectedIndex();
					}
				}
				System.out.println(itemtest);
			}
		});
		killButton.setEnabled(false);

		// 'kill' 버튼 클릭
		killButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (listIndex >= 0) {
					// item = listSelected.getModel().getElementAt(listIndex);
					result = JOptionPane.showConfirmDialog(null, itemtest + "을 선택하시겠습니까?", "확인",
							JOptionPane.YES_NO_OPTION);
					// 선택없이 창을 닫은 경우
					if (result == JOptionPane.CLOSED_OPTION) {
					}
					// "예"를 선택한 경우
					else if (result == JOptionPane.YES_OPTION) {
						try {
							if (killButton.getText().equals("투표")) {
								dos.writeUTF("!vote_run" + itemtest);
								Act = true;
							} else if (killButton.getText().equals("지목")) {
								dos.writeUTF("!Choice" + itemtest);
								Act = true;
							}

						} catch (IOException e1) {
						}
						// "아니오"를 선택한 경우
					} else {

					}
				}
			}
		});

		// 종료하기 버튼 Listener

		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(contentPane, "게임을 종료하시겠습니까?", "확인",  JOptionPane.YES_NO_OPTION);
				
				if(result == JOptionPane.CLOSED_OPTION) {
					JOptionPane.showMessageDialog(contentPane, "취소 되었습니다.");
				} else if(result == JOptionPane.YES_OPTION) {
					dispose();
					System.exit(0);
					setVisible(false);
				}

			}
		});

		// Skip 버튼 Listener (미완성)
		skipButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					dos.writeUTF("!vote_run!skip");
					Act = true;
				} catch (IOException e1) {
				}
			}
		});

		killButton.setBackground(Color.BLACK);
		killButton.setForeground(Color.RED);
		killButton.setBounds(846, 543, 134, 39);
		contentPane.add(killButton);

		txt_Job = new JTextField();
		txt_Job.setText("직업");
		txt_Job.setBounds(93, 660, 107, 32);
		txt_Job.setEditable(false);
		contentPane.add(txt_Job);
		txt_Job.setColumns(10);

		scrollPane.setBounds(0, 303, 847, 345);
		croom.setBorder(null);
		croom.setBackground(new Color(0, 0, 0, 120));
		croom.setForeground(Color.WHITE);
		croom.setBounds(0, 347, 847, 303);
		croom.setFont(new Font("굴림", Font.BOLD, 12));
		croom.setOpaque(false);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		croom.setEditable(false);

		JLabel lblNewLabel = new JLabel("MAFIA GAME");
		lblNewLabel.setForeground(Color.ORANGE);
		lblNewLabel.setFont(new Font("Arial Black", Font.PLAIN, 29));
		lblNewLabel.setBounds(351, 36, 270, 66);
		contentPane.add(lblNewLabel);

		JLabel chatting = new JLabel("\uB300\uD654\r\n\uB0B4\uC6A9");
		chatting.setForeground(Color.YELLOW);
		chatting.setFont(new Font("굴림", Font.BOLD, 18));
		chatting.setBounds(0, 702, 84, 49);
		contentPane.add(chatting);

		JLabel JobLabel_2 = new JLabel("\uC9C1\uC5C5");
		JobLabel_2.setForeground(Color.YELLOW);
		JobLabel_2.setFont(new Font("굴림", Font.BOLD, 18));
		JobLabel_2.setBounds(37, 658, 84, 32);
		contentPane.add(JobLabel_2);

		skipButton.setBackground(Color.BLACK);
		skipButton.setForeground(Color.YELLOW);
		skipButton.setBounds(846, 600, 134, 39);
		skipButton.setEnabled(false);
		contentPane.add(skipButton);

		exitButton.setBackground(Color.BLACK);
		exitButton.setForeground(Color.YELLOW);
		exitButton.setBounds(883, 36, 97, 54);
		contentPane.add(exitButton);

		tip.setFont(new Font("굴림", Font.ITALIC, 13));
		tip.setBackground(Color.BLACK);
		tip.setForeground(Color.YELLOW);
		tip.setText(
				"1. \uACBD\uCC30\uC77C \uACBD\uC6B0 \uC2DC\uBBFC\uB4E4\uC5D0\uAC8C \uBC1D\uD788\uB294 \uAC83\uC774 \uC720\uB9AC\uD558\uB2E4.\r\n    \uB9C8\uD53C\uC544\uC5D0\uAC8C \uC9C0\uBAA9\uB2F9\uD558\uC5EC \uC8FD\uC9C0 \uC54A\uAE30 \uC704\uD568\uC774\uB2E4.\r\n\r\n2. \uC758\uC0AC\uC778 \uACBD\uC6B0 \uC790\uC2E0\uC774 \uC758\uC0AC\uC784\uC744 \uACF5\uAC1C\uD558\uC9C0 \uC54A\uB294 \uAC83\uC774 \uC720\uB9AC\uD558\uB2E4. \r\n   \uACBD\uCC30\uC774 \uACF5\uAC1C\uB418\uC5C8\uC744 \uB54C \uB9C8\uD53C\uC544\uB85C \uBD80\uD130\uC9C0\uCF1C\uC57C \uD558\uB294 \uC778\uC6D0\uC774 2\uC774 \uB418\uC5B4 \uBD88\uB9AC\uD558\uAE30 \uB54C\uBB38\uC774\uB2E4. \r\n   \uACBD\uC6B0\uC5D0 \uB530\uB77C \uBC1D\uD600\uC57C \uD558\uB294 \uACBD\uC6B0\uB3C4 \uC788\uB2E4.\r\n\r\n3. \uBCF5\uC0AC \uBD99\uC5EC\uB123\uAE30 (\uC790\uC2E0\uC758 \uC9C1\uC5C5, \uC9C1\uC5C5\uC0C1 \uB728\uB294 \uBB38\uAD6C \uB4F1) \uB294 \uB9E4\uB108\uAC00 \uC544\uB2C8\uB2E4.\r\n\r\n4. \uB3C4\uBC30 \uD22C\uD45C (\uBAA8\uC870\uB9AC \uD639\uC740 \uB9CE\uC740 \uB2E4\uC218\uC5D0\uAC8C \uD22C\uD45C\uD558\uB294 \uAC83) \uB294 \uB9E4\uB108\uAC00 \uC544\uB2C8\uB2E4.");
		tip.setBounds(36, 134, 643, 205);
		tip.setEditable(false);
		tip.setOpaque(false);
		contentPane.add(tip);

		JLabel lblNewLabel_3 = new JLabel("**\uAC8C\uC784 \uD50C\uB808\uC774\uD301**");
		lblNewLabel_3.setBackground(new Color(240, 240, 240));
		lblNewLabel_3.setForeground(Color.YELLOW);
		lblNewLabel_3.setBounds(36, 107, 164, 15);
		contentPane.add(lblNewLabel_3);

		list2.setVisible(false);
		list.setBorder(new LineBorder(new Color(255, 255, 255), 1, true));
		list.setBounds(846, 303, 134, 243);
		list.setOpaque(false);
		contentPane.add(list);
		
		btnvotestart.setFont(new Font("굴림체", Font.PLAIN, 15));
		btnvotestart.setForeground(Color.YELLOW);
		btnvotestart.setBackground(Color.BLACK);

		// ------임시투표시작---------------------------
		btnvotestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dos.writeUTF("!vote_st");
				} catch (IOException e1) {
				}
			}
		});
		btnvotestart.setBounds(852, 268, 128, 23);
		contentPane.add(btnvotestart);
		// ------------------------------------------

		
		
		SendButton.setBackground(Color.BLACK);
		SendButton.setForeground(Color.YELLOW);
		SendButton.setBounds(859, 702, 74, 49);
		contentPane.add(SendButton);

		contentPane.setSize(800, 1000);
		contentPane.setVisible(true);
		contentPane.add(scrollPane);
		chat.addActionListener(this);
		getContentPane().add(contentPane);
		
		nameField.setText(nickname);
		nameField.setEditable(false);
		nameField.setColumns(10);
		nameField.setBounds(405, 660, 107, 32);
		
		contentPane.add(nameField);
		
		JLabel label = new JLabel("닉네임");
		label.setForeground(Color.YELLOW);
		label.setFont(new Font("굴림", Font.BOLD, 18));
		label.setBounds(331, 658, 62, 32);
		contentPane.add(label);
		
		
		// ------------------------------------------

		this.setSize(1000, 800);
		this.setVisible(true);
		this.setResizable(false); // 창 크기 수정 X

		Thread th = new Thread(this);
		th.start();
	}
	
	

	
	
	// 채팅창에서 글씨색 바꾸기 메소드
	public void addColoredText(JTextPane pane, String text, Color color) {
		//align == StyleConstants.ALIGN_(위치)
		StyledDocument doc = pane.getStyledDocument();
		Style style = pane.addStyle("Color Style", null);
		StyleConstants.setForeground(style, color);
		//StyleConstants.setAlignment(style, align);
		try {
			doc.insertString(doc.getLength(), text, style);
			//doc.setParagraphAttributes(doc.getLength(), 1, style, false);
		} catch (BadLocationException e) {

		}
		int lastLine = croom.getDocument().getLength();
		croom.setCaretPosition(lastLine);
	}

	public void actionPerformed(ActionEvent e) {// 채팅 입력후 엔터 이벤트
		if (e.getSource() == chat) {
			try {
				dos.writeUTF("!userChat" + nickname + " : " + chat.getText());// !chat는 채팅이라는걸 서버에 알리기위해
			} catch (IOException e2) {
			}
			chat.setText("");
		}
	}

	public void run() {
		try {

			while (true) {
				String strServer = dis.readUTF();// 서버와 마찬가지로 전체 while중에 단1번만 실행 readUTF
				if (strServer == null) {
					// addColoredText(croom, "\n" + "종료", Color.RED); 애초에 나가면 볼수있는게 없음
					return;// == break;
				} else if (strServer.contains("!VoAction?")) {
					if (!Act && time.equals("VoteTime")) {
						dos.writeUTF("!vote_run!skip");
						Act = true;
					} else if (!Act && time.equals("Night")) {
						dos.writeUTF("!Choice" + itemtest);
						Act = true;
					}
				} else if (strServer.contains("!OK")) { // 쓰레드에서 대기중인 readUTF를 풀기위해
					dos.writeUTF("!OK"); // 강제로 !OK전송
				} else if (strServer.contains("!SetTime")) {
					String str = strServer.substring(8);
					if (str != "")
						time = str;
					dos.writeUTF("!OK");
				} else if (strServer.contains("!btn_off")) {
					killButton.setEnabled(false);
					skipButton.setEnabled(false);
					Act = true;
				} else if (strServer.contains("!Btn_ON")) {
					if (!txt_Job.getText().equals("마피아")) {
						killButton.setText("지목");
						killButton.setEnabled(true);
					}
					if (!txt_Job.getText().equals("시민")) {
						Act = false;
					}
				} else if (strServer.contains("!GameEnd")) {
					dos.writeUTF("!OK");
					addColoredText(croom, "\n게임 종료!", Color.RED);
					list.setBounds(846, 303, 134, 243);
					list.setOpaque(false);
					skipButton.setEnabled(false);
					exitButton.setEnabled(true);
					killButton.setEnabled(false);
					list2.setEnabled(false);
					list2.setVisible(false);
					txt_Job.setText("직업");
				} else if (strServer.contains("!killmsg")) {
					String str = strServer.substring(8);
					
					if(!(str.contains("님"))) {
						System.out.println(str);					
					}else {
//					alive = new String[s.length];
//					arr2 = new ArrayList<String>();
//					for(String st : alive) {
//						arr2.add("생존");
//					}
						int n = str.indexOf("님");
						str2 = str.substring(0, n);
					for(int i =0;i<list.getModel().getSize() ; i++) {
						if (str2.equals((list.getModel().getElementAt(i)).toString().replaceAll("　", ""))) {
							String de = ((list.getModel().getElementAt(i)).toString().replaceAll("　", ""));
							System.out.println("");
							System.out.println("");
							arr2.set(i, "사망");
						}
					}
					for(int i=0; i<arr2.size(); i++) {
						alive[i] = arr2.get(i);
					}
					list2.setModel(new AbstractListModel() {
						String[] values = alive;// 만들어진 배열을 JFrame List에 출력
						
						public int getSize() {
							return values.length;
						}

						public Object getElementAt(int index) {
							return values[index];
						}
					});
					}
					//addColoredText(croom, "\n" + str + "님이 죽었습니다.", Color.RED);
					//서버에서 받기때문에 주석처리
					Act = false;
					dos.writeUTF("!OK");// 서버쪽에 실행중인 대기상태를 풀기위함(쓰레드부분)
					if (nickname.equals(str)) {
						skipButton.setEnabled(false);
						killButton.setEnabled(false);
						exitButton.setEnabled(false);
						addColoredText(croom, "- 당신은 죽었습니다. -", Color.RED);
						dos.writeUTF("!OK");

					} else
						addColoredText(croom, "\n- " + str + ". -", Color.RED);
				} else if (strServer.contains("!chat")) {// !chat 내용
					String str = strServer.substring(5);// !chat의 길이인 5까지 제거
					addColoredText(croom, "", Color.RED);
					addColoredText(croom, "\n" + str, Color.RED);
				} else if (strServer.contains("!userChat")) {// 유저들의 채팅
					String str = strServer.substring(9);//
					addColoredText(croom, "\n" + str, Color.WHITE);
				} else if (strServer.contains("!join")) {// !join 내용
					String str = strServer.substring(5);// !join의 길이인 5까지 제거
					String[] findName = str.split("!");
					addColoredText(croom, "\n- " + findName[findName.length - 1] + "님이 입장하셨습니다. -", Color.RED);
					
					s = Util.newarr(str);// 배열 만드는부분을 메서드화
					list.setModel(new AbstractListModel() {
						String[] values = s;// 만들어진 배열을 JFrame List에 출력

						public int getSize() {
							return values.length;
						}

						public Object getElementAt(int index) {
							return values[index];
						}
					});
					
				} else if (strServer.contains("!out")) {// !out 내용
					list.setSelectedIndex(0);
					String str = strServer.substring(4);// !out의 길이인 4까지 제거
					s = Util.newarr(str);// 배열 만드는부분을 메서드화
					alive = new String[s.length];
					List<String> arr3 = new ArrayList<String>();
					for(int i=0; i<alive.length; i++) {
						arr3.add(arr2.get(i));
					}
					
					for(int i=0; i<alive.length; i++) {
						System.out.println(arr2.get(i));
						if(arr3.get(i).equals(str2)) {
							arr3.get(i).replaceAll("생존","사망");
						}
					}
					for(int i=0; i<arr3.size(); i++) {
						alive[i] = arr3.get(i);
					}
					list.setModel(new AbstractListModel() {
						String[] values = s;// 만들어진 배열을 JFrame List에 출력

						public int getSize() {
							return values.length;
						}

						public Object getElementAt(int index) {
							return values[index];
						}
					});
					
					list2.setModel(new AbstractListModel() {
						String[] values = alive;// 만들어진 배열을 JFrame List에 출력
						
						public int getSize() {
							return values.length;
						}

						public Object getElementAt(int index) {
							return values[index];
						}
					});
				} else if (strServer.contains("!set")) {
					String str = strServer.substring(4);
					int[] arr = Util.newint(str);
					new GameSetGUI(dos, arr).setVisible(true);
				} else if (strServer.contains("!start")) {
					alive = new String[s.length];
					arr2 = new ArrayList<String>();
					list.setBounds(883, 303, 97, 243);
					list2.setVisible(true);
					list2.setBorder(new LineBorder(new Color(255, 255, 255), 1, true));
					list2.setBounds(847, 304, 34, 242);
					list2.setOpaque(false);
					list2.setEnabled(false);
					contentPane.add(list2);

					for(String st : alive) {
						arr2.add("생존");
					}
					for(int i=0; i<arr2.size(); i++) {
						alive[i] = arr2.get(i);
					}
					list2.setModel(new AbstractListModel() {
						String[] values = alive;// 만들어진 배열을 JFrame List에 출력
						
						public int getSize() {
							return values.length;
						}

						public Object getElementAt(int index) {
							return values[index];
						}
					});
					
					String str = strServer.substring(6);
					croom.setText("");
					addColoredText(croom, "\n" + str, Color.RED);
				} else if (strServer.contains("!vote_skip")) {
					addColoredText(croom, "\n-스킵하셨습니다-", Color.RED);
				} else if (strServer.contains("!vote_st")) {
					killButton.setText("투표");
					Act = false;
					killButton.setEnabled(true);
					skipButton.setEnabled(true);
					time = "VoteTime";
					dos.writeUTF("!OK");
				} else if (strServer.contains("!YouJob")) {
					String str = strServer.substring(7);
					txt_Job.setText(str);
				} else if (strServer.contains("!Choice")) {
					killButton.setEnabled(false);
				} else if (strServer.contains("!dsadsaaew")) {
					list.setSelectedIndex(Integer.parseInt(strServer.replaceAll("[^0-9]", "")));
					itemtest = ((String) list.getModel().getElementAt(list.getSelectedIndex())).replaceAll("　", "");
					dos.writeUTF("!OK");
				}
			}
		} catch (IOException e) {
		}		
	}	
}

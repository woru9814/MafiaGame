package socket;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import vo.GameSet;

public class GameSetGUI extends JFrame {
	private JPanel contentPane;
	GameSet gs;
	private JSpinner sp_doctor;
	private JSpinner sp_police;
	private JSpinner sp_mafia;
	private JSpinner sp_citizen;
	private JSpinner sp_day;
	private JSpinner sp_night;
	JLabel lbday = new JLabel("낮시간");
	
	JButton btnok = new JButton("설정하기");
	JLabel lbnight = new JLabel("밤시간");
	JLabel lbmafia = new JLabel("마피아");
	JLabel lbpolice = new JLabel("경찰");
	JLabel lbdoctor = new JLabel("의사");
	JLabel lbcitizen = new JLabel("시민");
	JLabel lbtime = new JLabel("시간설정");
	JLabel lbpersonnel = new JLabel("인원설정");
	
	public GameSetGUI() {
	}

	public GameSetGUI(DataOutputStream dos, int[] arr) {
		this.setVisible(true);
		this.setResizable(false);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		btnok.setBounds(157, 220, 97, 23);
		contentPane.add(btnok);
		

	
		lbday.setBounds(101, 41, 42, 15);
		contentPane.add(lbday);

		
		lbnight.setBounds(259, 41, 57, 15);
		contentPane.add(lbnight);

		
		lbmafia.setBounds(34, 129, 57, 15);
		contentPane.add(lbmafia);

		
		lbpolice.setBounds(140, 129, 57, 15);
		contentPane.add(lbpolice);

		
		lbdoctor.setBounds(259, 129, 57, 15);
		contentPane.add(lbdoctor);

		
		lbcitizen.setBounds(351, 129, 57, 15);
		contentPane.add(lbcitizen);

		
		lbtime.setFont(new Font("굴림", Font.BOLD, 15));
		lbtime.setBounds(173, 10, 97, 15);
		contentPane.add(lbtime);

		
		lbpersonnel.setFont(new Font("굴림", Font.BOLD, 15));
		lbpersonnel.setBounds(167, 103, 74, 15);
		contentPane.add(lbpersonnel);
		


		sp_day = new JSpinner();
		sp_day.setModel(new SpinnerNumberModel(new Integer(arr[0]), new Integer(60), new Integer(300), new Integer(30)));
		sp_day.setBounds(88, 66, 59, 24);
		contentPane.add(sp_day);

		sp_night = new JSpinner();
		sp_night.setModel(new SpinnerNumberModel(new Integer(arr[1]), new Integer(10), new Integer(120), new Integer(5)));
		sp_night.setBounds(257, 66, 59, 24);
		contentPane.add(sp_night);

		sp_mafia = new JSpinner();
		sp_mafia.setModel(new SpinnerNumberModel(new Integer(arr[2]), new Integer(1), new Integer(2), new Integer(1)));
		sp_mafia.setBounds(32, 154, 42, 33);
		contentPane.add(sp_mafia);

		sp_police = new JSpinner();
		sp_police.setModel(new SpinnerNumberModel(new Integer(arr[3]), new Integer(1), new Integer(1), new Integer(1)));
		sp_police.setBounds(132, 154, 42, 33);
		contentPane.add(sp_police);

		sp_doctor = new JSpinner();
		sp_doctor.setModel(new SpinnerNumberModel(new Integer(arr[4]), new Integer(0), new Integer(1), new Integer(1)));
		sp_doctor.setBounds(259, 154, 42, 33);
		contentPane.add(sp_doctor);

		sp_citizen = new JSpinner();
		sp_citizen.setModel(new SpinnerNumberModel(new Integer(arr[5]), new Integer(1), new Integer(4), new Integer(1)));
		sp_citizen.setBounds(347, 154, 42, 33);
		contentPane.add(sp_citizen);

		btnok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gs = new GameSet((int) sp_day.getValue(), (int) sp_night.getValue(), (int) sp_mafia.getValue(),
						(int) sp_police.getValue(), (int) sp_doctor.getValue(), (int) sp_citizen.getValue());

				try {
					dos.writeUTF(gs.toString());
					// dos.flush();
				} catch (IOException e1) {

				}

				dispose();
				setVisible(false);

			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				setVisible(false);
			}
		});
	}
}
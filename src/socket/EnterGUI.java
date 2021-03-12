package socket;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class EnterGUI extends JFrame implements Runnable {

   private JPanel contentPane;
   private String nic;
   private JTextField inputNic = new JTextField();
   private JTextArea gameExplain = new JTextArea();
   private JTextArea jobExplain = new JTextArea();
   private DataInputStream dis;
   private DataOutputStream dos;
   private Socket s1;
   ImageIcon image = new ImageIcon("./img/EnterGUI.png");
   
   public EnterGUI(DataInputStream dis, DataOutputStream dos, Socket s1) {
      this.dis = dis;
      this.dos = dos;
      this.s1=s1;
      this.setTitle("마피아게임");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 1000, 800);
      contentPane = new JPanel() {
         public void paintComponent(Graphics g) {
            g.drawImage(image.getImage(), 0, 0, null);
            setOpaque(false);
            super.paintComponent(g);
         }
      };
      contentPane.setBackground(Color.BLACK);
      contentPane.setForeground(Color.BLACK);
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      contentPane.setLayout(null);

      JLabel lblNewLabel = new JLabel("MAFIA GAME");
      lblNewLabel.setForeground(Color.ORANGE);
      lblNewLabel.setFont(new Font("Arial Black", Font.PLAIN, 79));
      lblNewLabel.setBounds(216, 34, 620, 179);
      contentPane.add(lblNewLabel);

      inputNic.setBounds(421, 225, 160, 40);
      contentPane.add(inputNic);
      inputNic.setColumns(10);

      JLabel lblNewLabel_1 = new JLabel("   \uB2C9\uB124\uC784\uC744 \uC785\uB825\uD558\uC138\uC694.");
      lblNewLabel_1.setForeground(Color.WHITE);
      lblNewLabel_1.setBounds(421, 266, 136, 27);
      contentPane.add(lblNewLabel_1);

      JButton startButton = new JButton("입장하기");
      startButton.setFont(new Font("굴림", Font.BOLD, 15));
      startButton.setBackground(Color.WHITE);
      startButton.setForeground(Color.ORANGE);
      startButton.setBounds(421, 302, 161, 40);
      contentPane.add(startButton);

      gameExplain.setBorder(new LineBorder(new Color(255, 255, 255), 1, true));
      gameExplain.setFont(new Font("맑은 고딕", Font.BOLD, 15));
      gameExplain.setBackground(Color.BLACK);
      gameExplain.setForeground(Color.WHITE);
      gameExplain.setText(
            "                     \uAC8C\uC784 \uC124\uBA85\r\n\r\n  \uB9C8\uD53C\uC544\uCC57\uC740 \uC2DC\uBBFC\uD300\uACFC \uB9C8\uD53C\uC544\uD300\uC73C\uB85C\r\n  \uB098\uB258\uC5B4 \uC11C\uB85C \uC8FD\uACE0 \uC8FD\uC774\uB294 \uAC8C\uC784\uC785\uB2C8\uB2E4.\r\n\r\n  \uB9C8\uD53C\uC544\uD300\uC740 \uBC24\uB9C8\uB2E4 \uC2DC\uBBFC\uD300\uC744 \uC8FD\uC5EC\r\n  \uC2DC\uBBFC\uD300\uACFC \uB9C8\uD53C\uC544\uD300\uC758 \uC0DD\uC874\uC22B\uC790\uAC00\r\n  \uB3D9\uC77C\uD574\uC9C0\uBA74 \uC2B9\uB9AC\uD569\uB2C8\uB2E4.\r\n\r\n  \uC2DC\uBBFC\uD300\uC740 \uB0AE\uC774 \uB418\uBA74 \uD22C\uD45C\uB97C \uD1B5\uD574\r\n  \uC6A9\uC758\uC790\uB97C \uCC98\uD615\uD558\uC5EC \uB9C8\uD53C\uC544\uD300\uC744 \uBAA8\uB450 \r\n  \uC81C\uAC70\uD558\uBA74 \uC2B9\uB9AC\uD569\uB2C8\uB2E4.");
      gameExplain.setBounds(170, 432, 280, 277);
      gameExplain.setEditable(false);
      gameExplain.setOpaque(false);
      contentPane.add(gameExplain);

      jobExplain.setBorder(new LineBorder(new Color(255, 255, 255), 1, true));
      jobExplain.setFont(new Font("맑은 고딕", Font.BOLD, 15));
      jobExplain.setBackground(Color.BLACK);
      jobExplain.setForeground(Color.WHITE);
      jobExplain.setText(
            "                            \uC9C1\uC5C5 \uC124\uBA85\r\n\r\n    * \uC9C1\uC5C5\uC740 \uAC8C\uC784\uC2DC\uC791 \uC2DC \uB79C\uB364\uC73C\uB85C \uBD80\uC5EC\uB429\uB2C8\uB2E4. *\r\n\r\n\uB9C8\uD53C\uC544  =  \uC874\uC7AC\uB97C \uC228\uAE30\uACE0 \uBC24\uB9C8\uB2E4 \uC2DC\uBBFC\uC744 \uC81C\uAC70\uD569\uB2C8\uB2E4.\r\n\r\n \uACBD\uCC30   = \uBC24\uB9C8\uB2E4 \uC9C0\uBAA9\uD55C \uC720\uC800\uC758 \uC9C1\uC5C5\uC744 \uC54C\uC544\uB0C5\uB2C8\uB2E4.\r\n\r\n \uC758\uC0AC   = \uB9C8\uD53C\uC544\uB85C\uBD80\uD130\uC758 \uC2DC\uBBFC \uACF5\uACA9\uC744 \uB9C9\uC544\uB0B4\uC90D\uB2C8\uB2E4.\r\n\r\n \uC2DC\uBBFC   =  \uCD94\uB9AC\uB97C \uD1B5\uD574 \uB9C8\uD53C\uC544\uB97C \uCC3E\uC544\uB0B4\uACE0 \uC0DD\uC874\uD569\uB2C8\uB2E4.");
      jobExplain.setBounds(517, 432, 373, 277);
      jobExplain.setEditable(false);
      jobExplain.setOpaque(false);
      contentPane.add(jobExplain);

      contentPane.setVisible(true);
      this.add(contentPane);
      this.setVisible(true);
      this.setResizable(false);

      // 입장하기 버튼
      startButton.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent e) {
            try {
               nic = inputNic.getText();
               dos.writeUTF(nic);
            } catch (IOException e1) {
            }
         }
      });

      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            dispose();
            System.exit(0);
            setVisible(false);
         }
      });
      Thread th = new Thread(this);
      th.start();

   }

   @Override
   public void run() {

      try {
         while (true) {
            String str = dis.readUTF();
            if(str.contains("!Success")) {
               dispose();
               setVisible(false);
               if (str.contains("!Host")) new HostGUI(dos, dis, s1, nic).setVisible(true);
               else if (str.contains("!User")) new UserGUI(dos, dis, s1, nic).setVisible(true);
               dos.writeUTF("!CheakSuc");
               break;
            } else if(str.equals("!StartErr")) {
               JOptionPane.showMessageDialog(null, "이미 게임 중 입니다.");
            } else if (str.equals("!Fail")) {
               JOptionPane.showMessageDialog(null, "닉네임 중복!!");
            }
         }
      } catch (IOException e) {

      }

   }
}
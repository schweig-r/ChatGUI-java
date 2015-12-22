import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class Chat {
	
	static JTextField setPort, ip, message, userID;
	static JTextArea text;
	static int port;
	static String ipAddress;
	static String user;
	
	public static boolean getPort() {
		try {
			port = Integer.parseInt(setPort.getText());
		} catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Wrong port", "Error: Port number", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	public static void getIp() {
		ipAddress = ip.getText();
	}
	
	static boolean getUserID() {
		if(userID.getText() == "") {
			JOptionPane.showMessageDialog(null, "Username can't be empty", "Error: Username", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(userID.getText().length() > 25) {
			JOptionPane.showMessageDialog(null, "Username too long", "Error: Username", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else{
			user = userID.getText();
		}
		return true;
	}
	
	static void mainWindow(){
		JFrame f = new JFrame("ChatApp");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setPreferredSize(new Dimension(300,80));
		JButton server = new JButton("Serwer");
		JButton client = new JButton("Client");
		
		server.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				loginServerWindow(f);
			}
		});
		
		client.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				loginClientWindow(f);
			}
		});
		JPanel panel = new JPanel();
		panel.add(server);
		panel.add(client);
		f.add(panel);
		
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
	
	public static void loginClientWindow(JFrame f) {
		f.getContentPane().removeAll();
		f.setPreferredSize(new Dimension(400,80));
		f.setTitle("ChatApp - Client");
		setPort = new JTextField("Numer portu");
		setPort.setPreferredSize(new Dimension(100, 20));;
		ip = new JTextField("localhost");
		userID = new JTextField("Name");
		userID.setPreferredSize(new Dimension(80,20));
		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				getIp();
				if(getPort()) {
					if(getUserID()) {
						new Client(port, ipAddress, user);
						f.dispose();
					}
				}
			}
		});
		
		JPanel panel = new JPanel();
		panel.add(setPort);
		panel.add(ip);
		panel.add(userID);
		panel.add(start);
		f.add(panel);
		f.getContentPane().validate();
		f.repaint();
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
	
	static void loginServerWindow(JFrame f) {
		f.getContentPane().removeAll();
		f.setPreferredSize(new Dimension(400,80));
		f.setTitle("ChatApp - Server");
		setPort = new JTextField("Numer portu");
		setPort.setPreferredSize(new Dimension(100, 20));;
		userID = new JTextField("Name");
		userID.setPreferredSize(new Dimension(80,20));
		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(getPort()) {
					if(getUserID()) {
						new Server(port, user);
						f.dispose();
					}
				}
			}
		});
		JPanel panel = new JPanel();
		panel.add(setPort);
		panel.add(userID);
		panel.add(start);
		f.add(panel);
		f.getContentPane().validate();
		f.repaint();
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainWindow();
			}
		});
	}
}
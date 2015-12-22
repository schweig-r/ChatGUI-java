import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;


class Server extends JFrame implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField message;
	JTextPane text;
	private ServerSocket serverSocket;
	private int port;
	Socket servermsg;
	Socket serverfile;
	PrintWriter pw;
	private String userID, clientID;
	private String ipAddress;
	StyledDocument doc;
	Style labelStyle;
	 
	public Server(int port, String userID) {
		this.port = port;
		this.userID = userID;
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		} catch(UnknownHostException e) {
			e.printStackTrace();
		}
		serverWindow();   
	}

	
	public void run() {
		try {
			serverSocket = new ServerSocket(port);  
			try {
				doc.insertString(doc.getLength(), "Waiting for connection on port: " + port + "\n", labelStyle);
				servermsg = serverSocket.accept();
				doc.insertString(doc.getLength(), "Connected\n", labelStyle);
			}catch(BadLocationException badLocationException) {
				badLocationException.printStackTrace();
			}
			BufferedReader br=new BufferedReader(new InputStreamReader(servermsg.getInputStream()));
			pw = new PrintWriter(servermsg.getOutputStream(),true);
			String s;
			clientID = br.readLine();
			pw.println(userID);
				while((s = br.readLine()) != null) {			// RECEIVE NEW MESSAGES
					if(s.equals("001122003399L")) {				// RECEIVE FILE
						serverfile = serverSocket.accept();
						s = br.readLine();
						String typeFile = s.substring(s.length() - 3, s.length());
						if(typeFile.equals("jpg") || typeFile.equals("gif") || typeFile.equals("png") || typeFile.equals("bmp")) {	// IF IMAGE PUT IN CHAT WINDOW
							BufferedImage image = ImageIO.read(serverfile.getInputStream());
							int width = image.getWidth(), height = image.getHeight();
							if(image.getWidth() > 800 || image.getHeight() > 600) {		// CHANGE IMAGE SIZE
								width = image.getWidth() / 4;
								height = image.getHeight() / 4;
							}
							BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
							Graphics2D g = bi.createGraphics();
							g.drawImage(image, 0, 0, width, height, null);
							g.dispose();	
							g.setComposite(AlphaComposite.Src);
	
							g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
							g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
							g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							text.insertIcon(new ImageIcon(bi));						// INSERT IMAGE IN CHAT WINDOW
							try {
								doc.insertString(doc.getLength(), "\n", labelStyle);
							}catch(BadLocationException badLocationException) {
								badLocationException.printStackTrace();
							}
							serverfile.close();
						}
						else {
							try {
								doc.insertString(doc.getLength(), "<" + clientID + "> " + " sends the file \"" + s +"\"\n", labelStyle);
							}catch(BadLocationException badLocationException) {
								badLocationException.printStackTrace();
							}
							File fileToSave = null;
							JFileChooser fileChooser = new JFileChooser(".");		// FILECHOOSER AND SAVING INCOMING FILE
							fileChooser.setDialogTitle("Save file");
							fileChooser.setSelectedFile(new File(s));
							int userSelection = fileChooser.showSaveDialog(getParent());
							if (userSelection == JFileChooser.APPROVE_OPTION) {
								
								fileToSave = fileChooser.getSelectedFile();
								try {
									doc.insertString(doc.getLength(), "Saved as file: " + fileToSave.getAbsolutePath() + "\n", labelStyle);
								}catch(BadLocationException badLocationException) {
									badLocationException.printStackTrace();
								}
							}
							FileOutputStream fos = new FileOutputStream(fileToSave);
							byte[] buffer = new byte[1024];
							int count;
							InputStream in = serverfile.getInputStream();
							while((count=in.read(buffer)) >0){
								fos.write(buffer, 0, count);
							}
							fos.close();
							serverfile.close();
						}
					}
					else {			// INSERT MASSAGE TO CHAT WINDOW
						try {
							doc.insertString(doc.getLength(),"<" + clientID +"> " + s + "\n", labelStyle);
						}catch(BadLocationException badLocationException) {
							badLocationException.printStackTrace();
						}
					}
				}
			servermsg.close();
		}catch(SocketException e) {
			try {
				doc.insertString(doc.getLength(), "Connection lost", labelStyle);
			}catch(BadLocationException badLocationException) {
				badLocationException.printStackTrace();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void serverWindow() {	
		JFrame f = new JFrame("Server " + ipAddress + ":" + port + "    Username: " + userID);
		f.setPreferredSize(new Dimension(500, 300));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		StyleContext context = new StyleContext();
		doc = new DefaultStyledDocument(context);
		labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
		
		text = new JTextPane(doc);
		text.setMinimumSize(new Dimension(200, 200));
		text.setBackground(java.awt.Color.GRAY);
		JScrollPane scrollPane = new JScrollPane(text);
		DefaultCaret caret = (DefaultCaret)text.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);			
		scrollPane.setViewportView(text);
		
		text.setEditable(false);
		
		
		message = new JTextField();
			
		message.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					doc.insertString(doc.getLength(), "<" + userID + "> " + message.getText() + "\n", labelStyle);
				}catch(BadLocationException badLocationException) {
					badLocationException.printStackTrace();
				}
				pw.println(message.getText());
				message.setText("");
			}
		});
		
		JButton button = new JButton("Wyslij plik");
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				JFileChooser fileChooser = new JFileChooser(new File("."));
				int returnValue = fileChooser.showOpenDialog(getParent());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					pw.println("001122003399L");
					pw.println(selectedFile.getName());
					long length = selectedFile.length();
					if (length > Integer.MAX_VALUE) {
						try {
							doc.insertString(doc.getLength(), "File is too large.\n", labelStyle);
						}catch(BadLocationException badLocationException) {
							badLocationException.printStackTrace();
						}
					}
					try {
						serverfile = serverSocket.accept();
						int count;
						byte[] buffer = new byte[1024];

						OutputStream out = serverfile.getOutputStream();
						BufferedInputStream in = new BufferedInputStream(new FileInputStream(selectedFile));
						while ((count = in.read(buffer)) > 0) {
							out.write(buffer, 0, count);
						}
						out.flush();
						in.close();
						serverfile.close();
					}catch(Exception e ) {
						e.printStackTrace();
					}
				}
			}
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(message);
		panel.add(button);
		Container contentPane = getContentPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(panel, BorderLayout.PAGE_END);
		
		f.add(contentPane);
		f.pack();
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
			
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		}
	}


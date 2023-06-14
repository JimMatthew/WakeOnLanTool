package wolGui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class wolGui extends JFrame{

	public static void main(String args[]) {
		@SuppressWarnings("unused")
		wolGui w = new wolGui();
	}

	private static final long serialVersionUID = 1L;
	private JPanel panel = new JPanel();
	private JTextField textFieldMac;
	private JTextField statusField;
	private JComboBox<String> comboBoxMac = new JComboBox<String>();
	private JButton btnSendFreeMac;
	private JButton btnSendComboMac;
	private JButton btnSave;
	private JLabel lblEnterMac;
	private String filename = "wol.conf";
	
	public wolGui() {
		setTitle("Wake on Lan Tool");
		setSize(399, 299);
		panel.setLayout(null);
		getContentPane().add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		textFieldMac = new JTextField();
		textFieldMac.setBounds(12, 139, 152, 19);
		panel.add(textFieldMac);
		textFieldMac.setColumns(10);
		
		btnSendFreeMac = new JButton("Send");
		btnSendFreeMac.setBackground(new Color(153, 193, 241));
		btnSendFreeMac.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSendFreeMac.setBounds(261, 136, 117, 25);
		panel.add(btnSendFreeMac);
		
		lblEnterMac = new JLabel("Enter MAC");
		lblEnterMac.setBounds(12, 112, 199, 15);
		panel.add(lblEnterMac);
		
		comboBoxMac.setBounds(12, 56, 212, 24);
		panel.add(comboBoxMac);
		
		btnSendComboMac = new JButton("Send");
		btnSendComboMac.setBackground(new Color(153, 193, 241));
		btnSendComboMac.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSendComboMac.setBounds(261, 56, 117, 25);
		panel.add(btnSendComboMac);
		
		btnSave = new JButton("Save");
		btnSave.setBackground(new Color(153, 193, 241));
		btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSave.setBounds(12, 170, 117, 25);
		panel.add(btnSave);
		
		statusField = new JTextField();
		statusField.setFont(new Font("Dialog", Font.BOLD, 14));
		statusField.setBounds(12, 219, 366, 19);
		statusField.setEditable(false);
		panel.add(statusField);
		statusField.setColumns(10);
		setupListeners();
		loadSavedMac();
		setVisible(true);
	}
	
	private void setupListeners() {
		btnSendComboMac.addActionListener(event -> sendWolPressed((String)comboBoxMac.getSelectedItem()));
		btnSendFreeMac.addActionListener(event -> sendWolPressed(textFieldMac.getText()));
		btnSave.addActionListener(event -> saveMac());
	}
	
	private void sendWolPressed(String mac) {
		if (wolSender.isValidMac(mac)) {
			try {
				wolSender.sendWolPacket(mac);
				setStatus("Wake Up sent to host: "+ mac);
			} catch (IOException e) {
				setStatus("Error Sending WOL packet");
			}	
		} else {
			setStatus("Cannot Send. Invalid MAC Address Specified");
		}
	}
	
	private void loadSavedMac() {
		try {
			comboBoxMac.removeAllItems();
			for (String s:Files.readAllLines(Paths.get(filename))) {
				comboBoxMac.addItem(s);
			}
		} catch (IOException e) {
			// No Save File Exists
		}
	}
	
	private void saveMac() {
		if (wolSender.isValidMac(textFieldMac.getText())) {
			try {
				if (Files.exists(Paths.get(filename))) {
					Files.writeString(Paths.get(filename), textFieldMac.getText()+'\n', StandardOpenOption.valueOf("APPEND"));
				} else {
					Files.writeString(Paths.get(filename), textFieldMac.getText()+'\n');
				}
				setStatus("MAC Address Saved");
				loadSavedMac();
			} catch (IOException e) {
				setStatus("Error Saving MAC Address File");
				e.printStackTrace();
			}
		} else {
			setStatus("Cannot Save. Invalid MAC Address Specified");
		}
	}
	
	private void setStatus(String status) {
		statusField.setText(status);
	}
}


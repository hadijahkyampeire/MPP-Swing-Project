package librarysystem;

import dataaccess.DataAccessFacade;
import dataaccess.User;

import java.awt.*;
import javax.swing.*;

public class LoginWindow extends JFrame implements LibWindow {
	public static final LoginWindow INSTANCE = new LoginWindow();

	private boolean isInitialized = false;
	private JPanel mainPanel, upperHalf, middleHalf, middlePanel, formPanel;
	private JTextField username;
	private JPasswordField password;
	private JButton loginButton;

	/* This class is a singleton */
	private LoginWindow() {}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void isInitialized(boolean val) {
		isInitialized = val;
	}

	public void init() {
		mainPanel = new JPanel(new BorderLayout(20, 20)); // Less spacing
		defineUpperHalf();
		defineMiddleHalf();

		mainPanel.add(upperHalf, BorderLayout.NORTH);
		mainPanel.add(middleHalf, BorderLayout.CENTER);
		getContentPane().add(mainPanel);

		isInitialized(true);
		pack();
	}

	private void defineUpperHalf() {
		upperHalf = new JPanel(new BorderLayout());
		JLabel loginLabel = new JLabel("Login", SwingConstants.CENTER);
		Util.adjustLabelFont(loginLabel, Color.BLUE.darker(), true);
		upperHalf.add(loginLabel, BorderLayout.CENTER);
	}

	private void defineMiddleHalf() {
		middleHalf = new JPanel(new BorderLayout());
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		middleHalf.add(separator, BorderLayout.SOUTH);

		defineMiddlePanel();
		middleHalf.add(middlePanel, BorderLayout.CENTER);
	}

	private void defineMiddlePanel() {
		middlePanel = new JPanel(new BorderLayout());

		// Image Panel (Left)
		JPanel imagePanel = new JPanel();
		JLabel imageLabel = new JLabel(new ImageIcon("src/librarysystem/library.jpg"));
		imagePanel.add(imageLabel);

		// Form Panel (Right)
		formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS)); // Stack elements
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Reduce spacing

		// Input Fields
		JPanel inputFields = new JPanel(new GridLayout(2, 2, 5, 5));
		inputFields.add(new JLabel("UserID:"));
		username = new JTextField(10);
		inputFields.add(username);

		inputFields.add(new JLabel("Password:"));
		password = new JPasswordField(10);
		inputFields.add(password);

		// Submit Button (Close to Form)
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // No extra spacing
		loginButton = new JButton("Submit");
		addLoginButtonListener(loginButton);
		buttonPanel.add(loginButton);

		// Add Components in Order
		formPanel.add(inputFields);
		formPanel.add(Box.createVerticalStrut(5)); // Small gap
		formPanel.add(buttonPanel);

		// Add Image and Form Side by Side
		middlePanel.add(imagePanel, BorderLayout.WEST);
		middlePanel.add(formPanel, BorderLayout.CENTER);
	}

	private void addLoginButtonListener(JButton butn) {
		butn.addActionListener(evt -> {
			String user = username.getText().trim();
			String pass = new String(password.getPassword()).trim();

			User loggedInUser = new DataAccessFacade().authenticateUser(user, pass);
			if (loggedInUser != null) {
				JOptionPane.showMessageDialog(this, "Successful Login");

				// Close login window
				this.dispose();

				// Open the corresponding dashboard
				switch (loggedInUser.getAuthorization()) {
					case LIBRARIAN:
						LibrarianDashboard.open();
						break;
					case ADMIN:
						AdminDashboard.open();
						break;
					case BOTH:
						ManagerDashboard.open();
						break;
				}
			} else {
				JOptionPane.showMessageDialog(this, "Incorrect credentials.");
			}
		});
	}
}

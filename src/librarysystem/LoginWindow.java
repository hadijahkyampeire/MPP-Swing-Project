package librarysystem;

import dataaccess.DataAccessFacade;
import dataaccess.User;
import librarysystem.librarian.LibrarianDashboard;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel inputFields = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		inputFields.add(new JLabel("UserID:"), gbc);

		gbc.gridx = 1;
		username = new JTextField();
		username.setPreferredSize(new Dimension(150, 30));
		inputFields.add(username, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		inputFields.add(new JLabel("Password:"), gbc);

		gbc.gridx = 1;
		password = new JPasswordField();
		password.setPreferredSize(new Dimension(150, 30));
		inputFields.add(password, gbc);

		formPanel.add(inputFields);

		// Submit Button
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
		loginButton = new JButton("Submit");

		// Style Button
		loginButton.setOpaque(true);
		loginButton.setContentAreaFilled(true);
		loginButton.setBorderPainted(false);
		loginButton.setPreferredSize(new Dimension(120, 35));
		loginButton.setBackground(new Color(0, 31, 63));
		loginButton.setForeground(Color.WHITE);
		loginButton.setFont(new Font("Arial", Font.BOLD, 14));
		loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		loginButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

		// Hover Effect
		loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				loginButton.setBackground(new Color(0, 64, 128));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				loginButton.setBackground(new Color(0, 31, 63));
			}
		});

		// Initially disable the login button
		loginButton.setEnabled(false);

		addLoginButtonListener(loginButton);
		buttonPanel.add(loginButton);

		// Add Components in Order
		formPanel.add(inputFields);
		formPanel.add(Box.createVerticalStrut(2));
		formPanel.add(buttonPanel);

		// Add Image and Form Side by Side
		middlePanel.add(imagePanel, BorderLayout.WEST);
		middlePanel.add(formPanel, BorderLayout.CENTER);

		// Add document listeners to enable/disable the login button
		DocumentListener docListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateLoginButtonState();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateLoginButtonState();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateLoginButtonState();
			}
		};

		username.getDocument().addDocumentListener(docListener);
		password.getDocument().addDocumentListener(docListener);
	}

	// Method to enable/disable login button
	private void updateLoginButtonState() {
		String user = username.getText().trim();
		String pass = new String(password.getPassword()).trim();
		loginButton.setEnabled(!user.isEmpty() && !pass.isEmpty());
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

				// Clear the input fields after successful login
				this.username.setText("");  // Clear username field
				this.password.setText("");  // Clear password field
			} else {
				JOptionPane.showMessageDialog(this, "Incorrect credentials.");
			}
		});
	}
}

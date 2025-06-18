import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LIMSForgotPasswordUI {
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("LIMS System - Password Recovery");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setUndecorated(false);
        frame.getContentPane().setBackground(new Color(70, 120, 200));
        frame.setLayout(null);

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = screenSize.width / 2;
        int centerY = screenSize.height / 2;

        // Create header label
        JLabel headerLabel = new JLabel("LIMS Password Recovery", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBounds(0, 20, screenSize.width, 40);
        frame.add(headerLabel);

        // Real-time clock label in top-left corner
        JLabel clockLabel = new JLabel();
        clockLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        clockLabel.setForeground(Color.WHITE);
        clockLabel.setBounds(20, 20, 300, 30);
        frame.add(clockLabel);

        // Timer to update clock every second
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd yyyy | HH:mm:ss");
            clockLabel.setText(formatter.format(new Date()));
        });
        timer.start();

        // Create password recovery panel
        JPanel recoveryPanel = new JPanel();
        recoveryPanel.setLayout(null);
        recoveryPanel.setSize(400, 350);
        recoveryPanel.setBackground(Color.WHITE);
        recoveryPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        recoveryPanel.setBounds(centerX - 200, centerY - 175, 400, 350); // Centered

        // Heading
        JLabel titleLabel = new JLabel("Reset Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        titleLabel.setBounds(0, 30, 400, 30);
        recoveryPanel.add(titleLabel);

        // Instruction text
        JLabel instructionLabel = new JLabel("<html><center>Enter your email and we'll send you a link to reset your password</center></html>");
        instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        instructionLabel.setBounds(50, 70, 300, 40);
        recoveryPanel.add(instructionLabel);

        // Email label and field
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailLabel.setBounds(75, 120, 250, 20);
        recoveryPanel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(75, 145, 250, 35);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        recoveryPanel.add(emailField);

        // Send Reset Link button
        JButton resetButton = new JButton("Send Reset Link");
        resetButton.setBounds(75, 200, 250, 35);
        resetButton.setBackground(Color.BLACK);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        recoveryPanel.add(resetButton);

        // Back to login label with functionality
        JLabel backLabel = new JLabel("<HTML><U>Back to Log In</U></HTML>");
        backLabel.setForeground(Color.GRAY);
        backLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backLabel.setBounds(150, 250, 100, 20);
        backLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LIMSNavigator.showLoginUI(); // Return to login screen
            }
        });
        recoveryPanel.add(backLabel);

        // Add panel and show frame
        frame.add(recoveryPanel);
        frame.setVisible(true);
    }
}

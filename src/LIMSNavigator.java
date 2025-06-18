import javax.swing.*;

public class LIMSNavigator {
    private static JFrame currentFrame;

    public static void showLoginUI() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            try {
                LIMSLoginUI.main(null);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error opening login screen: " + e.getMessage(),
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void showSignupUI() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            try {
                LIMSSignUpUI.main(null);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error opening signup screen: " + e.getMessage(),
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void showForgotPasswordUI() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            try {
                LIMSForgotPasswordUI.main(null);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error opening forgot password screen: " + e.getMessage(),
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void setCurrentFrame(JFrame frame) {
        currentFrame = frame;
    }
}

package edu.ezip.ing1.pds;

import com.formdev.flatlaf.FlatDarkLaf;
import edu.ezip.ing1.pds.business.dto.*;
import edu.ezip.ing1.pds.client.commons.*;
import edu.ezip.ing1.pds.services.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class MainFrontEnd {

    private static final String NETWORK_CONFIG_FILE = "network.yaml";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Erreur FlatLaf : " + e.getMessage());
        }

        SwingUtilities.invokeLater(MainFrontEnd::afficherConnexion);
    }

    public static void afficherConnexion() {
        JFrame frame = new JFrame("Connexion - CampusConnect");
        frame.setSize(900, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JLabel title = new JLabel("CampusConnect");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.LIGHT_GRAY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setOpaque(false);

        JLabel emailLabel = new JLabel("Adresse e-mail :");
        emailLabel.setForeground(Color.LIGHT_GRAY);
        JTextField emailField = new JTextField();

        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordLabel.setForeground(Color.LIGHT_GRAY);
        JPasswordField passwordField = new JPasswordField();

        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        panel.add(formPanel);
        panel.add(Box.createVerticalStrut(20));

        JButton loginButton = new JButton("Se connecter");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(30, 100, 220));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setMaximumSize(new Dimension(200, 40));

        panel.add(loginButton);

        frame.setContentPane(panel);
        frame.setVisible(true);

        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, NETWORK_CONFIG_FILE);
                final UtilisateurService utilisateurService = new UtilisateurService(networkConfig);

                boolean identifiantsOK = utilisateurService.checkConnexion(email, password);

                if (identifiantsOK) {
                    frame.dispose();

                    if (email.equalsIgnoreCase("admin@cc.com") && password.equals("Admin00@")) {
                        lancerMainApp(networkConfig); // Admin interface
                    } else if (email.equalsIgnoreCase("ag@cc.com") && password.equals("Agent00@")) {
                        new AgentUI(networkConfig).afficherCapteurs(); // Agent interface
                    } else {
                        InterfaceUtilisateurUI.afficher(networkConfig); // Standard user interface
                    }

                } else {
                    JOptionPane.showMessageDialog(frame, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erreur réseau : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void lancerMainApp(NetworkConfig networkConfig) throws IOException, InterruptedException {
        final CapteurService capteurService = new CapteurService(networkConfig);
        final UtilisateurService utilisateurService = new UtilisateurService(networkConfig);
        final ReservationService reservationService = new ReservationService(networkConfig);

        Capteurs capteurs = capteurService.selectCapteurs();
        Utilisateurs utilisateurs = utilisateurService.selectUtilisateurs();
        Reservations reservations = reservationService.selectReservations();

        JFrame frame = new JFrame("CAMPUS CONNECT - Admin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        JLabel titleLabel = new JLabel("Bienvenue dans l'application de gestion scolaire CAMPUS CONNECT !");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton capteurButton = createStyledButton("Capteurs");
        JButton utilisateurButton = createStyledButton("Utilisateurs");
        JButton reservationButton = createStyledButton("Réservations");

        capteurButton.addActionListener((ActionEvent e) -> new CapteurUI(networkConfig).afficherCapteurs(capteurs));
        utilisateurButton.addActionListener((ActionEvent e) -> new UtilisateurUI(networkConfig).afficherUtilisateurs(utilisateurs));
        reservationButton.addActionListener((ActionEvent e) -> new ReservationUI(networkConfig).afficherReservations(reservations));

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(capteurButton);
        panel.add(Box.createVerticalStrut(15));
        panel.add(utilisateurButton);
        panel.add(Box.createVerticalStrut(15));
        panel.add(reservationButton);

        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setFocusPainted(false);
        btn.setForeground(Color.CYAN);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return btn;
    }

    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color color1 = new Color(10, 10, 30);
            Color color2 = new Color(30, 50, 80);
            GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
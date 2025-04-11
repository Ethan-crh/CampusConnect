package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.*;
import edu.ezip.ing1.pds.client.commons.*;
import edu.ezip.ing1.pds.services.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import java.util.UUID;

public class MainFrontEnd {

    private static final String NETWORK_CONFIG_FILE = "network.yaml";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> afficherConnexion());
    }

    //FENÊTRE DE CONNEXION
    private static void afficherConnexion() {
        JFrame frame = new JFrame("Connexion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        try {
            ImageIcon icon = new ImageIcon(new URL("https://cdn-icons-png.flaticon.com/512/149/149071.png"));
            Image image = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 50));
        }

        //Formulaire de connexion
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Se connecter");

        panel.add(new JLabel("Adresse e-mail :"));
        panel.add(emailField);
        panel.add(new JLabel("Mot de passe :"));
        panel.add(passwordField);

        frame.add(iconLabel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.add(loginButton, BorderLayout.SOUTH);

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
                    lancerMainApp(networkConfig);
                } else {
                    JOptionPane.showMessageDialog(frame, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erreur réseau : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }


    //FENÊTRE PRINCIPALE
    private static void lancerMainApp(NetworkConfig networkConfig) throws IOException, InterruptedException {
        final CapteurService capteurService = new CapteurService(networkConfig);
        final UtilisateurService utilisateurService = new UtilisateurService(networkConfig);
        final ReservationService reservationService = new ReservationService(networkConfig);

        Capteurs capteurs = capteurService.selectCapteurs();
        Utilisateurs utilisateurs = utilisateurService.selectUtilisateurs();
        Reservations reservations = reservationService.selectReservations();

        JFrame frame = new JFrame("CAMPUS CONNECT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Bienvenue dans l'application de gestion scolaire CAMPUS CONNECT !");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton utilisateurButton = new JButton("Utilisateurs");
        JButton reservationButton = new JButton("Réservations");
        JButton capteurButton = new JButton("Capteurs");

        capteurButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        utilisateurButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        reservationButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        capteurButton.addActionListener((ActionEvent e) -> new CapteurUI(networkConfig).afficherCapteurs(capteurs));
        utilisateurButton.addActionListener((ActionEvent e) -> new UtilisateurUI(networkConfig).afficherUtilisateurs(utilisateurs));
        reservationButton.addActionListener((ActionEvent e) -> new ReservationUI(networkConfig).afficherReservations(reservations));

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(capteurButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(utilisateurButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(reservationButton);
        panel.add(Box.createVerticalStrut(10));

        frame.add(panel);
        frame.setVisible(true);
    }
}

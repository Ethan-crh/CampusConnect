package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.client.commons.NetworkConfig;

import javax.swing.*;
import java.awt.*;

public class InterfaceUtilisateurUI {

    public static void afficher(NetworkConfig config) {
        JFrame frame = new JFrame("Espace Utilisateur - CampusConnect");
        frame.setSize(900, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel welcomeLabel = new JLabel("Bienvenue dans votre espace personnel !");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton profilButton = createButton("Mon Profil");
        JButton reservationsButton = createButton("Mes Réservations");
        JButton deconnexionButton = createButton("Se Déconnecter");

        deconnexionButton.addActionListener(e -> {
            frame.dispose();
            MainFrontEnd.afficherConnexion(); // Retour à la page de connexion
        });

        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(profilButton);
        panel.add(Box.createVerticalStrut(15));
        panel.add(reservationsButton);
        panel.add(Box.createVerticalStrut(30));
        panel.add(deconnexionButton);

        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(250, 40));
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

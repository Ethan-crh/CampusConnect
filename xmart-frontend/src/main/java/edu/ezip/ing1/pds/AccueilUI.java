package edu.ezip.ing1.pds;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class AccueilUI {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Erreur FlatLaf : " + e.getMessage());
        }

        SwingUtilities.invokeLater(AccueilUI::afficherAccueil);
    }

    public static void afficherAccueil() {
        JFrame frame = new JFrame("CampusConnect");
        frame.setSize(900, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // ---------- LOGO EN HAUT À GAUCHE ----------
        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setOpaque(false);
        topBarPanel.setPreferredSize(new Dimension(0, 60)); // Fixe la hauteur pour éviter qu'il pousse le reste

        JLabel logoLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(AccueilUI.class.getResource("/images/CC.png"));
            Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // tu peux changer la taille ici
            logoLabel.setIcon(new ImageIcon(image));
        } catch (Exception ex) {
            logoLabel.setText("CampusConnect");
            logoLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            logoLabel.setForeground(Color.WHITE);
        }
        logoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topBarPanel.add(logoLabel, BorderLayout.WEST); // gauche

        panel.add(topBarPanel, BorderLayout.NORTH);


        // ---------- CONTENU CENTRAL ----------
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("CAMPUS CONNECT");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("APPLICATION GESTIONNAIRE DE L’ÉCOLE");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(200, 200, 200));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = createButton("SE CONNECTER");
        JButton signupBtn = createButton("CRÉER UN COMPTE");

        loginBtn.addActionListener(e -> {
            frame.dispose();
            MainFrontEnd.afficherConnexion(); // Ouvre l'écran de connexion
        });

        signupBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Fonction non disponible", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(title);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(subtitle);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(loginBtn);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(signupBtn);
        centerPanel.add(Box.createVerticalGlue());

        panel.add(centerPanel, BorderLayout.CENTER);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(Color.CYAN);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
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
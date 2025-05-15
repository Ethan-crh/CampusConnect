package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Capteur;
import edu.ezip.ing1.pds.business.dto.Capteurs;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.CapteurService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.UUID;

public class AgentUI {

    private final CapteurService capteurService;

    public AgentUI(NetworkConfig networkConfig) {
        this.capteurService = new CapteurService(networkConfig);
    }

    public void afficherCapteurs() {
        JFrame frame = new JFrame("Agent - Gestion des Capteurs");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel titleLabel = new JLabel("Bienvenue dans l'espace Agent - Gestion des Capteurs");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton listerButton = createStyledButton("Liste des Capteurs");
        JButton creerButton = createStyledButton("Créer un Capteur");
        JButton deconnexionButton = createStyledButton("Se Déconnecter");

        listerButton.addActionListener((ActionEvent e) -> afficherListeCapteurs());
        creerButton.addActionListener((ActionEvent e) -> afficherFormulaireCreation());
        deconnexionButton.addActionListener(e -> {
            frame.dispose();
            MainFrontEnd.afficherConnexion();
        });

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(listerButton);
        panel.add(Box.createVerticalStrut(15));
        panel.add(creerButton);
        panel.add(Box.createVerticalStrut(30));
        panel.add(deconnexionButton);

        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private void afficherListeCapteurs() {
        JFrame frame = new JFrame("Liste des Capteurs");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);

        String[] columnNames = {"ID Capteur", "Statut", "Présence", "Problème Détecté"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        try {
            Capteurs capteurs = capteurService.selectCapteurs();
            tableModel.setRowCount(0);

            if (capteurs != null && capteurs.getCapteurs() != null && !capteurs.getCapteurs().isEmpty()) {
                for (Capteur capteur : capteurs.getCapteurs()) {
                    Object[] row = {
                            capteur.getId(),
                            capteur.getStatut() ? "Actif" : "Inactif",
                            capteur.getPresence() ? "Présence détectée" : "Aucune présence",
                            capteur.getDetectionProbleme() ? "Problème détecté" : "OK"
                    };
                    tableModel.addRow(row);
                }
            } else {
                tableModel.addRow(new Object[]{"", "Aucun capteur trouvé", "", ""});
            }
        } catch (InterruptedException | IOException e) {
            JOptionPane.showMessageDialog(frame, "Erreur lors du chargement des capteurs : " + e.getMessage());
        }

        frame.setVisible(true);
    }

    private void afficherFormulaireCreation() {
        JFrame frame = new JFrame("Créer un Capteur");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField idField = new JTextField(UUID.randomUUID().toString());
        idField.setEditable(false);

        JComboBox<String> statutBox = new JComboBox<>(new String[]{"Actif", "Inactif"});
        JComboBox<String> presenceBox = new JComboBox<>(new String[]{"Oui", "Non"});
        JComboBox<String> problemeBox = new JComboBox<>(new String[]{"Oui", "Non"});

        panel.add(new JLabel("ID Capteur :"));
        panel.add(idField);
        panel.add(new JLabel("Statut :"));
        panel.add(statutBox);
        panel.add(new JLabel("Présence :"));
        panel.add(presenceBox);
        panel.add(new JLabel("Problème Détecté :"));
        panel.add(problemeBox);

        JButton createButton = new JButton("Créer");
        createButton.addActionListener(e -> {
            Capteur capteur = new Capteur();
            capteur.setId(idField.getText());
            capteur.setStatut("Actif".equals(statutBox.getSelectedItem()));
            capteur.setPresence("Oui".equals(presenceBox.getSelectedItem()));
            capteur.setDetectionProbleme("Oui".equals(problemeBox.getSelectedItem()));

            try {
                capteurService.insertCapteur(capteur);
                JOptionPane.showMessageDialog(frame, "Capteur créé avec succès !");
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de la création : " + ex.getMessage());
            }
        });

        panel.add(new JLabel());
        panel.add(createButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
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
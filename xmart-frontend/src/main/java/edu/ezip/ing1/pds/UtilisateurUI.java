package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Utilisateur;
import edu.ezip.ing1.pds.business.dto.Utilisateurs;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.UtilisateurService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class UtilisateurUI {

    private final UtilisateurService utilisateurService;

    public UtilisateurUI(NetworkConfig networkConfig) {
        this.utilisateurService = new UtilisateurService(networkConfig);
    }

    public void afficherUtilisateurs(Utilisateurs utilisateurs) {
        JFrame frame = new JFrame("Gestion des Utilisateurs");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        JLabel titleLabel = new JLabel("Gestion des Utilisateurs");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton afficherButton = createStyledButton("Liste des Utilisateurs");
        JButton creerButton = createStyledButton("Créer Nouveau Utilisateur");

        afficherButton.addActionListener(e -> afficherListeUtilisateurs());
        creerButton.addActionListener(e -> afficherFormulaireCreation());

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(afficherButton);
        panel.add(Box.createVerticalStrut(15));
        panel.add(creerButton);

        frame.setContentPane(panel);
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

    private void afficherListeUtilisateurs() {
        Utilisateurs utilisateurs;
        try {
            utilisateurs = utilisateurService.selectUtilisateurs();
        } catch (InterruptedException | IOException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors du chargement des utilisateurs : " + e.getMessage());
            return;
        }

        JFrame frame = new JFrame("Liste des Utilisateurs");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);

        String[] columnNames = {"ID", "Nom d'utilisateur", "Email", "Nom", "Prénom"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        if (utilisateurs != null && utilisateurs.getUtilisateurs() != null) {
            for (Utilisateur u : utilisateurs.getUtilisateurs()) {
                tableModel.addRow(new Object[]{
                        u.getIdUtilisateur(), u.getNomUtilisateur(), u.getEmail(), u.getNom(), u.getPrenom()
                });
            }
        } else {
            tableModel.addRow(new Object[]{"", "Aucun utilisateur trouvé", "", "", ""});
        }

        JButton modifierBtn = new JButton("Modifier");
        JButton supprimerBtn = new JButton("Supprimer");

        modifierBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Sélectionnez un utilisateur.");
                return;
            }
            Utilisateur u = new Utilisateur();
            u.setIdUtilisateur((int) tableModel.getValueAt(row, 0));
            u.setNomUtilisateur((String) tableModel.getValueAt(row, 1));
            u.setEmail((String) tableModel.getValueAt(row, 2));
            u.setNom((String) tableModel.getValueAt(row, 3));
            u.setPrenom((String) tableModel.getValueAt(row, 4));
            afficherFormulaireModification(u, tableModel, row);
        });

        supprimerBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Sélectionnez un utilisateur.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame, "Supprimer cet utilisateur ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Utilisateur u = new Utilisateur();
                    u.setIdUtilisateur((int) tableModel.getValueAt(row, 0));
                    utilisateurService.deleteUtilisateur(u);
                    tableModel.removeRow(row);
                    JOptionPane.showMessageDialog(frame, "Utilisateur supprimé.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Erreur : " + ex.getMessage());
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(modifierBtn);
        buttonPanel.add(supprimerBtn);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void afficherFormulaireCreation() {
        JFrame frame = new JFrame("Créer un Utilisateur");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        JTextField nomUtilisateurField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();

        panel.add(new JLabel("Nom d'utilisateur :")); panel.add(nomUtilisateurField);
        panel.add(new JLabel("Email :")); panel.add(emailField);
        panel.add(new JLabel("Mot de passe :")); panel.add(passwordField);
        panel.add(new JLabel("Nom :")); panel.add(nomField);
        panel.add(new JLabel("Prénom :")); panel.add(prenomField);

        JButton createBtn = new JButton("Créer");
        panel.add(new JLabel()); // pour aligner
        panel.add(createBtn);

        createBtn.addActionListener(e -> {
            String nomUtilisateur = nomUtilisateurField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();

            if (nomUtilisateur.isEmpty() || email.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Tous les champs doivent être remplis.");
            } else {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setNomUtilisateur(nomUtilisateur);
                utilisateur.setEmail(email);
                utilisateur.setPassword(password);
                utilisateur.setNom(nom);
                utilisateur.setPrenom(prenom);
                try {
                    String result = utilisateurService.insertUtilisateur(utilisateur);
                    if ("Email déjà utilisé".equals(result)) {
                        JOptionPane.showMessageDialog(frame, "Email déjà utilisé.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Utilisateur créé !");
                        frame.dispose();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Erreur : " + ex.getMessage());
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private void afficherFormulaireModification(Utilisateur utilisateur, DefaultTableModel model, int row) {
        JFrame frame = new JFrame("Modifier Utilisateur");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        JTextField nomUtilisateurField = new JTextField(utilisateur.getNomUtilisateur());
        JTextField emailField = new JTextField(utilisateur.getEmail());
        JTextField passwordField = new JTextField();
        JTextField nomField = new JTextField(utilisateur.getNom());
        JTextField prenomField = new JTextField(utilisateur.getPrenom());

        panel.add(new JLabel("Nom d'utilisateur :")); panel.add(nomUtilisateurField);
        panel.add(new JLabel("Email :")); panel.add(emailField);
        panel.add(new JLabel("Mot de passe (laisser vide pour ne pas modifier) :")); panel.add(passwordField);
        panel.add(new JLabel("Nom :")); panel.add(nomField);
        panel.add(new JLabel("Prénom :")); panel.add(prenomField);

        JButton updateBtn = new JButton("Enregistrer");
        panel.add(new JLabel()); panel.add(updateBtn);

        updateBtn.addActionListener(e -> {
            utilisateur.setNomUtilisateur(nomUtilisateurField.getText());
            utilisateur.setEmail(emailField.getText());
            utilisateur.setNom(nomField.getText());
            utilisateur.setPrenom(prenomField.getText());

            if (!passwordField.getText().isEmpty()) {
                utilisateur.setPassword(passwordField.getText());
            }

            try {
                utilisateurService.updateUtilisateur(utilisateur);
                JOptionPane.showMessageDialog(frame, "Utilisateur mis à jour !");
                model.setValueAt(utilisateur.getNomUtilisateur(), row, 1);
                model.setValueAt(utilisateur.getEmail(), row, 2);
                model.setValueAt(utilisateur.getNom(), row, 3);
                model.setValueAt(utilisateur.getPrenom(), row, 4);
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur : " + ex.getMessage());
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    // Reprise du panel dégradé (identique à AccueilUI)
    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color c1 = new Color(10, 10, 30);
            Color c2 = new Color(30, 50, 80);
            GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

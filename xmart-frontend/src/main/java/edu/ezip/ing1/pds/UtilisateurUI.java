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

    private UtilisateurService utilisateurService;

    public UtilisateurUI(NetworkConfig networkConfig) {
        this.utilisateurService = new UtilisateurService(networkConfig);
    }

    public void afficherUtilisateurs(Utilisateurs utilisateurs) {
        JFrame frame = new JFrame("Gestion des Utilisateurs");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);

        GradientPanel gradientPanel = new GradientPanel();
        gradientPanel.setLayout(new BoxLayout(gradientPanel, BoxLayout.Y_AXIS));
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(100, 300, 100, 300));

        JLabel title = new JLabel("Gestion des Utilisateurs");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.LIGHT_GRAY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        gradientPanel.add(title);
        gradientPanel.add(Box.createVerticalStrut(30));

        JButton afficherButton = createStyledButton("Liste des Utilisateurs");
        JButton creerButton = createStyledButton("Créer Nouveau Utilisateur");

        afficherButton.addActionListener(e -> afficherListeUtilisateurs());
        creerButton.addActionListener(e -> afficherFormulaireCreation());

        gradientPanel.add(afficherButton);
        gradientPanel.add(Box.createVerticalStrut(15));
        gradientPanel.add(creerButton);

        frame.setContentPane(gradientPanel);
        frame.setVisible(true);
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
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Nom d'utilisateur", "Email", "Nom", "Prénom"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        if (utilisateurs != null && utilisateurs.getUtilisateurs() != null && !utilisateurs.getUtilisateurs().isEmpty()) {
            for (Utilisateur utilisateur : utilisateurs.getUtilisateurs()) {
                Object[] row = {
                        utilisateur.getIdUtilisateur(),
                        utilisateur.getNomUtilisateur(),
                        utilisateur.getEmail(),
                        utilisateur.getNom(),
                        utilisateur.getPrenom()
                };
                tableModel.addRow(row);
            }
        } else {
            tableModel.addRow(new Object[]{"", "Aucun utilisateur trouvé", "", "", ""});
        }

        JButton modifierButton = createDefaultButton("Modifier Utilisateur");
        modifierButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Veuillez sélectionner un utilisateur.");
                return;
            }

            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nomUtilisateur = (String) tableModel.getValueAt(selectedRow, 1);
            String email = (String) tableModel.getValueAt(selectedRow, 2);
            String nom = (String) tableModel.getValueAt(selectedRow, 3);
            String prenom = (String) tableModel.getValueAt(selectedRow, 4);

            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setIdUtilisateur(id);
            utilisateur.setNomUtilisateur(nomUtilisateur);
            utilisateur.setEmail(email);
            utilisateur.setNom(nom);
            utilisateur.setPrenom(prenom);

            afficherFormulaireModification(utilisateur, tableModel, selectedRow);
        });

        JButton supprimerButton = createDefaultButton("Supprimer Utilisateur");
        supprimerButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Veuillez sélectionner un utilisateur à supprimer.");
                return;
            }

            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nomUtilisateur = (String) tableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Voulez-vous vraiment supprimer l'utilisateur " + nomUtilisateur + " ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Utilisateur utilisateur = new Utilisateur();
                    utilisateur.setIdUtilisateur(id);

                    utilisateurService.deleteUtilisateur(utilisateur);
                    JOptionPane.showMessageDialog(frame, "Utilisateur supprimé avec succès !");
                    tableModel.removeRow(selectedRow);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Erreur lors de la suppression : " + ex.getMessage());
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(modifierButton);
        panel.add(supprimerButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void afficherFormulaireCreation() {
        JFrame frame = new JFrame("Créer un Utilisateur");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10));

        panel.add(new JLabel("Nom d'utilisateur :"));
        JTextField nomUtilisateurField = new JTextField();
        panel.add(nomUtilisateurField);

        panel.add(new JLabel("Email :"));
        JTextField emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Mot de passe :"));
        JTextField passwordField = new JTextField();
        panel.add(passwordField);

        panel.add(new JLabel("Nom :"));
        JTextField nomField = new JTextField();
        panel.add(nomField);

        panel.add(new JLabel("Prénom :"));
        JTextField prenomField = new JTextField();
        panel.add(prenomField);

        JButton createButton = createDefaultButton("Créer");
        panel.add(createButton);

        createButton.addActionListener(e -> {
            String nomUtilisateur = nomUtilisateurField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();

            if (nomUtilisateur.isEmpty() || email.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else if (nomUtilisateur.contains(" ")) {
                JOptionPane.showMessageDialog(frame, "Le nom d'utilisateur ne doit pas contenir d'espaces.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                JOptionPane.showMessageDialog(frame, "Format email invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                JOptionPane.showMessageDialog(frame, "Format mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setNomUtilisateur(nomUtilisateur);
                utilisateur.setEmail(email);
                utilisateur.setPassword(password);
                utilisateur.setNom(nom);
                utilisateur.setPrenom(prenom);

                try {
                    String response = utilisateurService.insertUtilisateur(utilisateur);
                    if ("Email déjà utilisé".equals(response)) {
                        JOptionPane.showMessageDialog(frame, "Cet email est déjà utilisé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Utilisateur créé avec succès !");
                        frame.dispose();
                    }
                } catch (InterruptedException | IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Erreur lors de la création de l'utilisateur : " + ex.getMessage());
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private void afficherFormulaireModification(Utilisateur utilisateur, DefaultTableModel tableModel, int selectedRow) {
        JFrame frame = new JFrame("Modifier un Utilisateur");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));

        panel.add(new JLabel("ID Utilisateur :"));
        JTextField idField = new JTextField(String.valueOf(utilisateur.getIdUtilisateur()));
        idField.setEditable(false);
        panel.add(idField);

        panel.add(new JLabel("Nom d'utilisateur :"));
        JTextField nomUtilisateurField = new JTextField(utilisateur.getNomUtilisateur());
        panel.add(nomUtilisateurField);

        panel.add(new JLabel("Email :"));
        JTextField emailField = new JTextField(utilisateur.getEmail());
        panel.add(emailField);

        panel.add(new JLabel("Mot de passe (Laissez vide pour ne pas changer) :"));
        JTextField passwordField = new JTextField();
        panel.add(passwordField);

        panel.add(new JLabel("Nom :"));
        JTextField nomField = new JTextField(utilisateur.getNom());
        panel.add(nomField);

        panel.add(new JLabel("Prénom :"));
        JTextField prenomField = new JTextField(utilisateur.getPrenom());
        panel.add(prenomField);

        JButton updateButton = createDefaultButton("Modifier");
        panel.add(updateButton);

        updateButton.addActionListener(e -> {
            try {
                String nomUtilisateur = nomUtilisateurField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();
                String nom = nomField.getText().trim();
                String prenom = prenomField.getText().trim();

                if (nomUtilisateur.isEmpty() || email.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Tous les champs obligatoires doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (nomUtilisateur.contains(" ")) {
                    JOptionPane.showMessageDialog(frame, "Le nom d'utilisateur ne doit pas contenir d'espaces.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                    JOptionPane.showMessageDialog(frame, "Format email invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.isEmpty() && !password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                    JOptionPane.showMessageDialog(frame, "Format du mot de passe incorrect. Il doit contenir au moins une majuscule, un chiffre et 8 caractères minimum.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                if (!email.equals(utilisateur.getEmail()) && utilisateurService.emailExiste(email, utilisateur.getIdUtilisateur())) {
                    JOptionPane.showMessageDialog(frame, "Cet email est déjà utilisé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                if (utilisateurService.nomUtilisateurExiste(nomUtilisateur, utilisateur.getIdUtilisateur())) {
                    JOptionPane.showMessageDialog(frame, "Ce nom d'utilisateur est déjà utilisé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                utilisateur.setNomUtilisateur(nomUtilisateur);
                utilisateur.setEmail(email);
                utilisateur.setNom(nom);
                utilisateur.setPrenom(prenom);
                if (!password.isEmpty()) {
                    utilisateur.setPassword(password);
                }


                utilisateurService.updateUtilisateur(utilisateur);
                JOptionPane.showMessageDialog(frame, "Utilisateur modifié avec succès !");


                tableModel.setValueAt(nomUtilisateur, selectedRow, 1);
                tableModel.setValueAt(email, selectedRow, 2);
                tableModel.setValueAt(nom, selectedRow, 3);
                tableModel.setValueAt(prenom, selectedRow, 4);

                frame.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erreur lors de la modification : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }


    // Bouton style
    private static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setFocusPainted(false);
        btn.setForeground(Color.CYAN);
        btn.setBackground(new Color(0, 0, 0, 0)); // fond transparent
        btn.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return btn;
    }

    // bouton normal
    private static JButton createDefaultButton(String text) {
        return new JButton(text); // Bouton classique sans style
    }

    // panel degrade
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
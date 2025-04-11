package edu.ezip.ing1.pds;

import edu.ezip.ing1.pds.business.dto.Utilisateur;
import edu.ezip.ing1.pds.business.dto.Utilisateurs;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.UtilisateurService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class UtilisateurUI {

    private UtilisateurService utilisateurService;

    public UtilisateurUI(NetworkConfig networkConfig) {
        this.utilisateurService = new UtilisateurService(networkConfig);
    }

    public void afficherUtilisateurs(Utilisateurs utilisateurs) {
        // Créer la fenêtre principale avec deux boutons
        JFrame frame = new JFrame("Gestion des Utilisateurs");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Créer un panel avec un layout vertical
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));

        // Boutons pour afficher la liste des utilisateurs et créer un nouveau utilisateur
        JButton afficherButton = new JButton("Liste des Utilisateurs");
        JButton creerButton = new JButton("Créer Nouveau Utilisateur");


        // ActionListener pour afficher la liste des utilisateurs
        afficherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Appeler la méthode pour afficher la liste des utilisateurs
                afficherListeUtilisateurs();
            }
        });

        // ActionListener pour créer un nouveau utilisateur
        creerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ouvrir un formulaire pour créer un nouvel utilisateur
                afficherFormulaireCreation();
            }
        });

        // Ajouter les boutons au panel
        panel.add(afficherButton);
        panel.add(creerButton);

        // Ajouter le panel à la fenêtre principale
        frame.add(panel);

        // Afficher la fenêtre principale
        frame.setVisible(true);
    }

    // Méthode pour afficher la liste des utilisateurs
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
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Nom d'utilisateur", "Email", "Nom", "Prénom"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        // Ajouter les données des utilisateurs
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

        // Ajout du bouton "Modifier"
        JButton modifierButton = new JButton("Modifier Utilisateur");
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

        //Ajout du bouton "Supprimer"
        JButton supprimerButton = new JButton("Supprimer Utilisateur");
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

                    //Supprimer la ligne du tableau
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



    // Méthode pour afficher un formulaire de création d'utilisateur
    private void afficherFormulaireCreation() {
        JFrame frame = new JFrame("Créer un Utilisateur");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        // Panel pour contenir les champs du formulaire
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10));

        // Champs pour saisir les informations de l'utilisateur
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

        // Bouton pour valider la création
        JButton createButton = new JButton("Créer");
        panel.add(createButton);

        // ActionListener pour valider la création
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Récupérer les données des champs
                String nomUtilisateur = nomUtilisateurField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();
                String nom = nomField.getText().trim();
                String prenom = prenomField.getText().trim();

                // Vérifier si tous les champs sont remplis
                if (nomUtilisateur.isEmpty() || email.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);}

                else if (nomUtilisateur.contains(" ")) {
                    JOptionPane.showMessageDialog(frame, "Le nom d'utilisateur ne doit pas contenir d'espaces.", "Erreur", JOptionPane.ERROR_MESSAGE);}

                else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")){
                    JOptionPane.showMessageDialog(frame, "Format email invalide", "Erreur", JOptionPane.ERROR_MESSAGE);}

                else if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")){
                    JOptionPane.showMessageDialog(frame, "Format mot de passe incorrecte", "Erreur", JOptionPane.ERROR_MESSAGE);}
                else {


                    // Créer un objet Utilisateur
                    Utilisateur utilisateur = new Utilisateur();
                    utilisateur.setNomUtilisateur(nomUtilisateur);
                    utilisateur.setEmail(email);
                    utilisateur.setPassword(password);
                    utilisateur.setNom(nom);
                    utilisateur.setPrenom(prenom);



                // Appeler la méthode du service pour ajouter l'utilisateur à la base de données
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
                }}
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private void afficherFormulaireModification(Utilisateur utilisateur, DefaultTableModel tableModel, int selectedRow) {
        JFrame frame = new JFrame("Modifier un Utilisateur");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
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

        JButton updateButton = new JButton("Modifier");
        panel.add(updateButton);

        updateButton.addActionListener(e -> {
            try {
                String nomUtilisateur = nomUtilisateurField.getText();
                String email = emailField.getText();
                String password = passwordField.getText();
                String nom = nomField.getText();
                String prenom = prenomField.getText();

                // Mise à jour de l'utilisateur
                utilisateur.setNomUtilisateur(nomUtilisateur);
                utilisateur.setEmail(email);
                utilisateur.setNom(nom);
                utilisateur.setPrenom(prenom);

                if (!password.isEmpty()) {
                    utilisateur.setPassword(password);
                }

                utilisateurService.updateUtilisateur(utilisateur);
                JOptionPane.showMessageDialog(frame, "Utilisateur modifié avec succès !");

                // Mettre à jour la ligne dans le tableau
                tableModel.setValueAt(nomUtilisateur, selectedRow, 1);
                tableModel.setValueAt(email, selectedRow, 2);
                tableModel.setValueAt(password.isEmpty() ? tableModel.getValueAt(selectedRow, 3) : password, selectedRow, 3);
                tableModel.setValueAt(nom, selectedRow, 4);
                tableModel.setValueAt(prenom, selectedRow, 5);

                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de la modification : " + ex.getMessage());
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }


}

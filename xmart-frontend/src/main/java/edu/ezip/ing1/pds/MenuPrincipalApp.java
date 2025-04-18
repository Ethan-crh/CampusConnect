package edu.ezip.ing1.pds;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MenuPrincipalApp extends JFrame {
    private static String nomFichier = "accounts.log";
    private static ArrayList<Capteur> appareils = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();


    public static void main(String[] args) {
        // Ajout des appareils connectés
        appareils.add(new Salle("INFO1"));
        appareils.add(new Salle("INFO2"));
        appareils.add(new Salle("INFO3"));
        appareils.add(new Salle("INFO4"));
        appareils.add(new Salle("INFO5"));
        appareils.add(new Salle("TD3"));
        appareils.add(new Salle("TD5"));
        appareils.add(new Salle("TD7"));

        SwingUtilities.invokeLater(MenuPrincipalApp::new);

        SwingUtilities.invokeLater(MenuPrincipalApp::creerInterface);
    }

    private static void creerInterface() {
        JFrame frame = new JFrame("CampusConnect");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Création du panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Ajout des composants
        JLabel labelUsername = new JLabel("Nom d'utilisateur :");
        JTextField texteUsername = new JTextField();

        JLabel labelPassword = new JLabel("Mot de Passe :");
        JPasswordField textePassword = new JPasswordField();

        panel.add(labelUsername);
        panel.add(texteUsername);
        panel.add(labelPassword);
        panel.add(textePassword);

        JButton boutonConnexion = new JButton("Connexion");
        JButton boutonInscription = new JButton("Créer un compte");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(boutonConnexion);
        buttonPanel.add(boutonInscription);


        // Gestionnaires d'événements

        boutonConnexion.addActionListener(e -> {
            String username = texteUsername.getText();
            char[] password = textePassword.getPassword();

            try (BufferedReader fileBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(nomFichier)))) {
                String line;
                boolean authReussi = false;

                while ((line = fileBufferedReader.readLine()) != null) {
                    String[] parts = line.split(", ");
                    if (parts.length == 2) {
                        String fileUsername = parts[0].split(": ")[1];
                        String filePassword = parts[1].split(": ")[1];

                        if (fileUsername.equals(username) && filePassword.equals(new String(password))) {
                            authReussi = true;
                            break;
                        }
                    }
                }

                if (authReussi) {
                    JOptionPane.showMessageDialog(frame, "Connexion réussie !");
                    frame.dispose();
                    menuPrincipalIHM();
                } else {
                    JOptionPane.showMessageDialog(frame, "Nom d'utilisateur ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de la connexion.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });


        boutonInscription.addActionListener(e -> creerCompteIHM());

        // Ajout des panels à la fenêtre
        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

    }



    // Création de compte

    private static void creerCompteIHM() {
        JFrame frame = new JFrame("Créer un compte");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblUsername = new JLabel("Nom d'utilisateur :");
        JTextField txtUsername = new JTextField();

        JLabel lblPassword = new JLabel("Mot de passe :");
        JPasswordField txtPassword = new JPasswordField();

        JLabel lblConfirmPassword = new JLabel("Confirmer le mot de passe");
        JPasswordField txtConfirmPassword = new JPasswordField();

        panel.add(lblUsername);
        panel.add(txtUsername);
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(lblConfirmPassword);
        panel.add(txtConfirmPassword);


        JButton boutonCreer = new JButton("Créer");
        JButton boutonRetour = new JButton("Retour");

        JPanel boutonPanel = new JPanel();
        boutonPanel.add(boutonCreer);
        boutonPanel.add(boutonRetour);

        boutonCreer.addActionListener(e -> {
            String username = txtUsername.getText();
            char[] password = txtPassword.getPassword();
            char [] confirmPassword = txtConfirmPassword.getPassword();

            if (!Arrays.equals(password, confirmPassword)) {
                JOptionPane.showMessageDialog(frame,"Les mots de passe ne correspondent pas",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nomFichier, true)))) {
                bufferedWriter.write("Username: " + username + ", Password: " + new String(password));
                bufferedWriter.newLine();
                bufferedWriter.flush();
                JOptionPane.showMessageDialog(frame, "Compte créé avec succès !");
                frame.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de la création du compte.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });


        boutonRetour.addActionListener(e -> frame.dispose());

        // Ajout des panels à la fenêtre
        frame.add(panel, BorderLayout.CENTER);
        frame.add(boutonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }




    private static void menuPrincipalIHM() {
        JFrame frame = new JFrame("Menu Principal");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblBienvenue = new JLabel("Bienvenue dans le menu principal !");
        lblBienvenue.setHorizontalAlignment(SwingConstants.CENTER); // Centrer horizontalement
        lblBienvenue.setVerticalAlignment(SwingConstants.TOP);      // Placer en haut
        panel.add(lblBienvenue);

        JButton btnCapteur = new JButton("Capteur");
        btnCapteur.addActionListener(e -> CapteurIHM());
        JButton btnReservation = new JButton("Réservation d'une salle");
        btnReservation.addActionListener(e -> ReservationIHM());
        /*JButton btnRadiateur = new JButton("Radiateur");
        btnRadiateur.addActionListener(e -> RadiateurIHM());*/
        JButton btnQuitter = new JButton("Quitter");
        btnQuitter.addActionListener(e -> frame.dispose());

        panel.add(btnCapteur);
        //panel.add(btnRadiateur);
        panel.add(btnReservation);
        panel.add(btnQuitter);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void ReservationIHM() {
// Fenêtre pour ajouter une planification
        JFrame frame = new JFrame("Réservation d'une salle");
        frame.setSize(450, 450);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Ajout des composants
        JLabel labellibelle = new JLabel("Libellé :");
        final String libelle[] = {"Examen","Etudes", "Hackaton", "Partiels", "Projet Sirius"};
        JComboBox<String> combolibelle = new JComboBox<>(libelle);
        panel.add(labellibelle);
        panel.add(combolibelle);

        JLabel labelsalle = new JLabel("Nom de la salle :");
        final String salle[] = { "INFO1", "INFO2","INFO3","TD1","TD2", "TD3","TD5"};
        JComboBox<String> combosalle = new JComboBox<>(salle);
        panel.add(labelsalle);
        panel.add(combosalle);

        JLabel labeldescription = new JLabel("Description :");
        JTextField txtDescription = new JTextField();
        panel.add(labeldescription);
        panel.add(txtDescription);

        JLabel labeldate = new JLabel("Date de la réservation :");
        JDateChooser dateresa = new JDateChooser();
        panel.add(labeldate);
        panel.add(dateresa);

        JLabel labelheuredeb = new JLabel("Heure de début (format HH:mm) :");
        JTextField txtheuredeb = new JTextField();
        panel.add(labelheuredeb);
        panel.add(txtheuredeb);

        JLabel labelheurefin = new JLabel("Heure de fin (format HH:mm) :");
        JTextField txtheurefin = new JTextField();
        panel.add(labelheurefin);
        panel.add(txtheurefin);

        JButton btnAfficherPlanificationSalle = new JButton("Réservations existantes");
        btnAfficherPlanificationSalle.addActionListener(e -> afficherReservationsIHM());
        panel.add(btnAfficherPlanificationSalle);

        JButton btnAjouter = new JButton("Ajouter");
        JButton btnRetour = new JButton("Retour");
        btnRetour.addActionListener(e -> frame.dispose());

        btnAjouter.addActionListener(e -> {
            String libel = combolibelle.getSelectedItem().toString();
            String nom = combosalle.getSelectedItem().toString();
            String description = txtDescription.getText();
            Date date = dateresa.getDate(); // Récupérer la date correctement
            String heuredeb = txtheuredeb.getText();
            String heurefin = txtheurefin.getText();

            if (date == null || heuredeb.isEmpty() || heurefin.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Veuillez remplir tous les champs s'il vous plaît", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Ajouter la réservation à la liste
                Reservation nouvelleReservation = new Reservation(libel, nom, description, date, heuredeb, heurefin, "Nom utilisateur");
                reservations.add(nouvelleReservation);

                // Message de confirmation
                String message = String.format(
                        "Réservation ajoutée pour la salle %s (%s) :\n" +
                                "Libellé : %s\n" +
                                "Description : %s\n" +
                                "Date : %s\n" +
                                "Heure de début : %s\n" +
                                "Heure de fin : %s",
                        nom, libel, libel, description, date.toString(), heuredeb, heurefin
                );

                JOptionPane.showMessageDialog(frame, message);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Une erreur est survenue.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnRetour);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        btnAfficherPlanificationSalle.addActionListener(e -> afficherReservationsIHM());
        btnRetour.addActionListener(e -> frame.dispose());


        frame.setVisible(true);

    }

   /* private static void RadiateurIHM() {
        JFrame frame = new JFrame("Radiateur");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Ajout des composants

        JButton btnReglerTemperature = new JButton("Régler température");
        btnReglerTemperature.addActionListener(e -> reglerTemperatureIHM());
        JButton btnAjouterPlanification = new JButton("Ajouter planification");
        btnAjouterPlanification.addActionListener(e -> ajouterPlanificationIHM());
        JButton btnAfficherPlanification = new JButton("Afficher planification");
        btnAfficherPlanification.addActionListener(e -> afficherPlanificationIHM());
        JButton btnConsommation = new JButton("Consommation");
        btnConsommation.addActionListener(e -> afficherConsommationIHM());
        JButton btnDetecterProbleme = new JButton("Détecter problème");
        btnDetecterProbleme.addActionListener(e -> detecterProblemeIHM());

        panel.add(btnReglerTemperature);
        panel.add(btnAjouterPlanification);
        panel.add(btnAfficherPlanification);
        panel.add(btnConsommation);
        panel.add(btnDetecterProbleme);




        JButton btnRetour = new JButton("Retour");



        btnRetour.addActionListener(e -> frame.dispose());

        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);

    }*/

    private static void CapteurIHM() {
        JFrame frame = new JFrame("Capteur");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Ajout des composants
        JButton btnPresence = new JButton("Présence");
        btnPresence.addActionListener(e -> detecterPresenceIHM());
        JButton btnProbleme = new JButton("Détection Problème");
        btnProbleme.addActionListener(e -> detecterProblemeCapteurIHM());
        JButton btnStatut= new JButton("Statut");
        btnStatut.addActionListener( e -> detecterStatutIHM());

        panel.add(btnProbleme);
        panel.add(btnPresence);
        panel.add(btnStatut);

        JButton btnRetour = new JButton("Retour");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRetour);

        btnRetour.addActionListener(e -> frame.dispose());

        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

    }

    private static void detecterStatutIHM() {
        // Fenêtre pour afficher l'état du problème
        JFrame frame = new JFrame("Statut de la salle");
        frame.setSize(500, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Demande à l'utilisateur de choisir un appareil
        JLabel lblNomAppareil = new JLabel("Nom de la salle:");
        JTextField txtNomAppareil = new JTextField();
        panel.add(lblNomAppareil);
        panel.add(txtNomAppareil);

        // Création des boutons pour détecter un problème
        JButton boutonPresence = new JButton("Détecter le statut");
        JButton boutonRetour = new JButton("Retour");

        JPanel boutonPanel = new JPanel();
        boutonPanel.add(boutonPresence);
        boutonPanel.add(boutonRetour);


        boutonPresence.addActionListener(e -> {
            String nomAppareil = txtNomAppareil.getText();
            Capteur appareil = chercherAppareilParNom(nomAppareil);

            if (appareil != null) {
                // Simule un problème aléatoire (true ou false)
                boolean probleme = Math.random() > 1; // Valeur aléatoire pour démonstration

                appareil.detecterProbleme(probleme);

                String message = probleme ? appareil.getNom() + " la salle est fermé." : appareil.getNom() + " la salle est encore ouverte.";
                JOptionPane.showMessageDialog(frame, message);
            } else {
                JOptionPane.showMessageDialog(frame, "Salle non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Bouton pour fermer la fenêtre

        boutonRetour.addActionListener(e -> frame.dispose());

        frame.add(panel, BorderLayout.CENTER);
        frame.add(boutonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        frame.setVisible(true);
    }

    private static void detecterPresenceIHM() {
        // Fenêtre pour afficher l'état du problème
        JFrame frame = new JFrame("Détection de Présence");
        frame.setSize(500, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Demande à l'utilisateur de choisir un appareil
        JLabel lblNomAppareil = new JLabel("Nom de la salle:");
        JTextField txtNomAppareil = new JTextField();
        panel.add(lblNomAppareil);
        panel.add(txtNomAppareil);

        // Création des boutons pour détecter un problème
        JButton boutonPresence = new JButton("Détecter Présence");
        JButton boutonRetour = new JButton("Retour");

        JPanel boutonPanel = new JPanel();
        boutonPanel.add(boutonPresence);
        boutonPanel.add(boutonRetour);


        boutonPresence.addActionListener(e -> {
            String nomAppareil = txtNomAppareil.getText();
            Capteur appareil = chercherAppareilParNom(nomAppareil);

            if (appareil != null) {
                // Simule un problème aléatoire (true ou false)
                boolean probleme = Math.random() > 1; // Valeur aléatoire pour démonstration

                appareil.detecterProbleme(probleme);

                String message = probleme ? appareil.getNom() + " la salle n'est pas vide." : appareil.getNom() + " il n'y a personne.";
                JOptionPane.showMessageDialog(frame, message);
            } else {
                JOptionPane.showMessageDialog(frame, "Salle non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Bouton pour fermer la fenêtre

        boutonRetour.addActionListener(e -> frame.dispose());

        frame.add(panel, BorderLayout.CENTER);
        frame.add(boutonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        frame.setVisible(true);
    }



    private static void detecterProblemeCapteurIHM() {
        // Fenêtre pour afficher l'état du problème
        JFrame frame = new JFrame("Détection de Problème");
        frame.setSize(500, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Demande à l'utilisateur de choisir un appareil
        JLabel lblNomAppareil = new JLabel("Nom de la salle :");
        JTextField txtNomAppareil = new JTextField();
        panel.add(lblNomAppareil);
        panel.add(txtNomAppareil);

        // Création des boutons pour détecter un problème
        JButton boutonProbleme = new JButton("Détecter Problème");
        JButton boutonRetour = new JButton("Retour");

        JPanel boutonPanel = new JPanel();
        boutonPanel.add(boutonProbleme);
        boutonPanel.add(boutonRetour);


        boutonProbleme.addActionListener(e -> {
            String nomAppareil = txtNomAppareil.getText();
            Capteur appareil = chercherAppareilParNom(nomAppareil);

            if (appareil != null) {
                // Simule un problème aléatoire (true ou false)
                boolean probleme = Math.random() > 0.5; // Valeur aléatoire pour démonstration

                appareil.detecterProbleme(probleme);

                String message = probleme ? "Le capteur dans le salle " + appareil.getNom() + " a un problème détecté, il faut aller voir." : "Le capteur dans la salle " + appareil.getNom() + " fonctionne normalement.";
                JOptionPane.showMessageDialog(frame, message);
            } else {
                JOptionPane.showMessageDialog(frame, "Salle non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Bouton pour fermer la fenêtre

        boutonRetour.addActionListener(e -> frame.dispose());

        frame.add(panel, BorderLayout.CENTER);
        frame.add(boutonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        frame.setVisible(true);
    }

    /*private static void reglerTemperatureIHM() {
        JFrame frame = new JFrame("Régler Température");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Ajout des composants
        JLabel lblNomAppareil = new JLabel("La salle :");
        JTextField txtNomAppareil = new JTextField();
        JLabel lblTemperature = new JLabel("Température :");
        JTextField txtTemperature = new JTextField();

        panel.add(lblNomAppareil);
        panel.add(txtNomAppareil);
        panel.add(lblTemperature);
        panel.add(txtTemperature);

        JButton btnRegler = new JButton("Régler");
        JButton btnRetour = new JButton("Retour");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRegler);
        buttonPanel.add(btnRetour);

        btnRegler.addActionListener(e -> {
            String nom = txtNomAppareil.getText();
            try {
                double temperature = Double.parseDouble(txtTemperature.getText());
                Capteur appareil =  chercherAppareilParNom(nom);
                if (appareil != null) {
                    appareil.setTemperature(temperature);
                    JOptionPane.showMessageDialog(frame, "Température réglée avec succès pour le radiateur dans la salle " + appareil.getNom() + " à " + temperature + "°C !");
                } else {
                    JOptionPane.showMessageDialog(frame, "Radiateur introuvable dans cette salle.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Température invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRetour.addActionListener(e -> frame.dispose());

        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

    }*/

    private static Capteur chercherAppareilParNom(String nom) {
        for (Capteur appareil : appareils) {
            if (appareil.getNom().equalsIgnoreCase(nom)) {
                return appareil;
            }
        }
        return null;
    }


   /* private static void afficherPlanificationIHM() {
        // Fenêtre pour afficher la planification
        JFrame frame = new JFrame("Planification des radiateurs");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Affichage de la planification pour chaque appareil
        for (Capteur appareil : appareils) {
            JLabel labelAppareil = new JLabel(appareil.getNom());
            labelAppareil.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(labelAppareil);

            HashMap<String, Double> planification = appareil.getPlanification();
            if (planification.isEmpty()) {
                JLabel labelNoPlanification = new JLabel("Aucune planification définie.");
                labelNoPlanification.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(labelNoPlanification);
            } else {
                // Affichage de chaque heure et température dans la planification
                planification.forEach((heure, temp) -> {
                    JLabel labelPlanification = new JLabel("Heure: " + heure + " - Température: " + temp + "°C");
                    labelPlanification.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panel.add(labelPlanification);
                });
            }
        }

        // Bouton pour fermer la fenêtre
        JButton btnRetour = new JButton("Retour");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRetour);

        btnRetour.addActionListener(e -> frame.dispose());

        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }*/

    private static void afficherReservationsIHM() {
        JFrame frame = new JFrame("Liste des Réservations");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (reservations.isEmpty()) {
            JLabel labelNoReservations = new JLabel("Aucune réservation disponible.");
            labelNoReservations.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(labelNoReservations);
        } else {
            for (Reservation reservation : reservations) {
                JPanel resPanel = new JPanel();
                resPanel.setLayout(new GridLayout(0, 1));

                JLabel labelSalle = new JLabel("Salle : " + reservation.getSalle());
                JLabel labelLibelle = new JLabel("Libellé : " + reservation.getLibelle());
                JLabel labelDescription = new JLabel("Description : " + reservation.getDescription());
                JLabel labelDate = new JLabel("Date : " + reservation.getDate().toString());
                JLabel labelHeureDebut = new JLabel("Heure début : " + reservation.getHeureDebut());
                JLabel labelHeureFin = new JLabel("Heure fin : " + reservation.getHeureFin());
                JLabel labelNomResa = new JLabel("Réservé par : " + reservation.getNomresa());

                resPanel.add(labelSalle);
                resPanel.add(labelLibelle);
                resPanel.add(labelDescription);
                resPanel.add(labelDate);
                resPanel.add(labelHeureDebut);
                resPanel.add(labelHeureFin);
                resPanel.add(labelNomResa);

                panel.add(resPanel);
                panel.add(Box.createVerticalStrut(5)); // Espacement entre les réservations
                panel.add(new JSeparator(SwingConstants.HORIZONTAL));
            }
        }

        JButton btnFermer = new JButton("Fermer");
        JButton btnSupprimer = new JButton("Supprimer");
        btnFermer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSupprimer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFermer.addActionListener(e -> frame.dispose());
        panel.add(btnFermer);
        panel.add(btnSupprimer);

        JPanel boutonPanel = new JPanel();
        boutonPanel.add(btnFermer);
        boutonPanel.add(btnSupprimer);
        frame.add(panel, BorderLayout.CENTER);
        frame.add(boutonPanel, BorderLayout.SOUTH);

        frame.add(new JScrollPane(panel)); // Barre de défilement

        frame.setVisible(true);
    }

    /*private static void afficherConsommationIHM() {
        // Fenêtre pour afficher la consommation
        JFrame frame = new JFrame("Consommation des radiateurs dans chaque salle");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        // Affichage de la consommation de chaque appareil
        for ( Capteur appareil : appareils) {
            String consomm = appareil.getNom() + " : " + appareil.getConsommation() + " kWh";
            JLabel label = new JLabel(consomm);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(label);
        }

        // Bouton pour fermer la fenêtre
        JButton btnRetour = new JButton("Retour");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRetour);

        btnRetour.addActionListener(e -> frame.dispose());

        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }


    private static void detecterProblemeIHM() {
        // Fenêtre pour afficher l'état du problème
        JFrame frame = new JFrame("Détection de Problème");
        frame.setSize(500, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Demande à l'utilisateur de choisir un appareil
        JLabel lblNomAppareil = new JLabel("La salle :");
        JTextField txtNomAppareil = new JTextField();
        panel.add(lblNomAppareil);
        panel.add(txtNomAppareil);

        // Création des boutons pour détecter un problème
        JButton boutonProbleme = new JButton("Détecter Problème");
        JButton boutonRetour = new JButton("Retour");

        JPanel boutonPanel = new JPanel();
        boutonPanel.add(boutonProbleme);
        boutonPanel.add(boutonRetour);


        boutonProbleme.addActionListener(e -> {
            String nomAppareil = txtNomAppareil.getText();
            Capteur appareil = chercherAppareilParNom(nomAppareil);

            if (appareil != null) {
                // Simule un problème aléatoire (true ou false)
                boolean probleme = Math.random() > 0.5; // Valeur aléatoire pour démonstration

                appareil.detecterProbleme(probleme);

                String message = probleme ? "Le radiateur dans la salle " + appareil.getNom() + " a un problème est détecté." : "Le radiateur dans la salle "+ appareil.getNom() + " fonctionne normalement.";
                JOptionPane.showMessageDialog(frame, message);
            } else {
                JOptionPane.showMessageDialog(frame, "Appareil non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Bouton pour fermer la fenêtre

        boutonRetour.addActionListener(e -> frame.dispose());

        frame.add(panel, BorderLayout.CENTER);
        frame.add(boutonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        frame.setVisible(true);
    }*/

    private static void ajouterPlanificationIHM() {
        // Fenêtre pour ajouter une planification
        JFrame frame = new JFrame("Ajouter Planification");
        frame.setSize(500, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Ajout des composants
        JLabel lblNomAppareil = new JLabel("La salle:");
        JTextField txtNomAppareil = new JTextField();
        JLabel lblHeure = new JLabel("Heure (format HH:mm) :");
        JTextField txtHeure = new JTextField();
        JLabel lblTemperature = new JLabel("Température :");
        JTextField txtTemperature = new JTextField();

        panel.add(lblNomAppareil);
        panel.add(txtNomAppareil);
        panel.add(lblHeure);
        panel.add(txtHeure);
        panel.add(lblTemperature);
        panel.add(txtTemperature);

        JButton btnAjouter = new JButton("Ajouter");
        JButton btnRetour = new JButton("Retour");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnRetour);


        btnAjouter.addActionListener(e -> {
            String nom = txtNomAppareil.getText();
            String heure = txtHeure.getText();
            try {
                double temperature = Double.parseDouble(txtTemperature.getText());
                Capteur appareil = chercherAppareilParNom(nom);
                if (appareil != null) {
                    appareil.ajouterPlanification(heure, temperature);
                    JOptionPane.showMessageDialog(frame, "Planification ajoutée pour le radiateur dans la salle " + appareil.getNom() + " à " + heure + " avec une température de " + temperature + "°C !");
                } else {
                    JOptionPane.showMessageDialog(frame, "Appareil introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Température invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRetour.addActionListener(e -> frame.dispose());

        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

}

abstract class Capteur {
    private String nom;

    public Capteur(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    private HashMap<String, Double> planification = new HashMap<>();

    private double temperature;

    public double getConsommation() {
        return temperature * 1.5;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void ajouterPlanification(String heure, double temp) {
        planification.put(heure, temp);
    }

    public HashMap<String, Double> getPlanification() {
        return planification;
    }

    public void detecterProbleme(boolean probleme) {
    }
}


class Salle extends Capteur {
    public Salle(String nom) {
        super(nom);
    }
}

class Reservation {
    private String libelle;
    private String salle;
    private String description;
    private Date date;
    private String heureDebut;
    private String heureFin;
    private String nomresa;

    // Constructeur prenant tous les paramètres
    public Reservation(String libelle, String salle, String description, Date date, String heureDebut, String heureFin, String nomresa) {
        this.libelle = libelle;
        this.salle = salle;
        this.description = description;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.nomresa = nomresa;
    }

    // Getters
    public String getLibelle() {
        return libelle;
    }

    public String getSalle() {
        return salle;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public String getNomresa() {
        return nomresa;
    }
}
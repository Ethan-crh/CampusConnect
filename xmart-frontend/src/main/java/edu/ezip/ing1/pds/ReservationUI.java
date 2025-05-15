package edu.ezip.ing1.pds;

import com.toedter.calendar.JDateChooser;
import edu.ezip.ing1.pds.business.dto.Reservation;
import edu.ezip.ing1.pds.business.dto.Reservations;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.ReservationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class ReservationUI {

    private ReservationService reservationService;

    public ReservationUI(NetworkConfig networkConfig) {
        this.reservationService = new ReservationService(networkConfig);
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Format date

    public void afficherReservations(Reservations reservations) {
        // Créer la fenêtre principale avec deux boutons
        JFrame frame = new JFrame("Gestion des Réservations");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);

        // Créer un panel avec un layout vertical
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));

        // Boutons pour afficher la liste des réservations et créer une nouvelle réservation
        JButton afficherButton = new JButton("Liste des Réservations");
        JButton creerButton = new JButton("Créer Nouvelle Réservation");

        // ActionListener pour afficher la liste des réservations
        afficherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Appeler la méthode pour afficher la liste des réservations
                afficherListeReservations();
            }
        });

        // ActionListener pour créer une nouvelle réservation
        creerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ouvrir un formulaire pour créer une nouvelle réservation
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

    // Méthode pour afficher la liste des réservations
    private void afficherListeReservations() {

        Reservations reservations;
        try {
            reservations = reservationService.selectReservations();
        } catch (InterruptedException | IOException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors du chargement des réservations : " + e.getMessage());
            return;
        }

        JFrame frame = new JFrame("Liste des Réservations");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Nom", "Date", "Heure de début", "Heure de fin", "Type", "Description"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        // Ajouter les données des réservations
        if (reservations != null && reservations.getReservations() != null && !reservations.getReservations().isEmpty()) {
            for (Reservation reservation : reservations.getReservations()) {
                Object[] row = {
                        reservation.getId(),
                        reservation.getName(),
                        dateFormat.format(reservation.getDate()),
                        reservation.getHeuredeb(),
                        reservation.getHeurefin(),
                        reservation.getType(),
                        reservation.getDescription()
                };
                tableModel.addRow(row);
            }
        } else {
            // Cas d'un manque de réservation
            tableModel.addRow(new Object[]{"", "Aucune réservation trouvée", "", "", "", "", ""});
        }

        // Ajout du bouton "Modifier"
        JButton modifierButton = new JButton("Modifier Réservation");
        modifierButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Veuillez sélectionner une réservation.");
                return;
            }

            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nom = (String) tableModel.getValueAt(selectedRow, 1);
            String dateStr = (String) tableModel.getValueAt(selectedRow, 2);
            Date date = null;
            try {
                date = dateFormat.parse(dateStr);
            } catch (ParseException wrongdate) {
                throw new RuntimeException(wrongdate);
            }
            Time heureDebut = (Time) tableModel.getValueAt(selectedRow, 3);
            Time heureFin = (Time) tableModel.getValueAt(selectedRow, 4);
            String type = (String) tableModel.getValueAt(selectedRow, 5);
            String description = (String) tableModel.getValueAt(selectedRow, 6);

            Reservation reservation = new Reservation();
            reservation.setId(id);
            reservation.setName(nom);
            reservation.setDate(date);
            reservation.setHeuredeb(Time.valueOf(String.valueOf(heureDebut)));
            reservation.setHeurefin(Time.valueOf(String.valueOf(heureFin)));
            reservation.setType(type);
            reservation.setDescription(description);

            afficherFormulaireModification(reservation, tableModel, selectedRow);
        });

        // Ajout du bouton "Supprimer"
        JButton supprimerButton = new JButton("Supprimer Réservation");
        supprimerButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Veuillez sélectionner une réservation à supprimer.");
                return;
            }

            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nom = (String) tableModel.getValueAt(selectedRow, 1);


            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Voulez-vous vraiment supprimer la réservation " + nom + " ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Reservation reservation = new Reservation();
                    reservation.setId(id);

                    reservationService.deleteReservation(reservation);
                    JOptionPane.showMessageDialog(frame, "Réservation supprimée avec succès !");

                    // Supprimer la ligne du tableau
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

    // Méthode pour afficher un formulaire de création de réservation
    private void afficherFormulaireCreation() {
        JFrame frame = new JFrame("Créer une Réservation");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        // Panel pour contenir les champs du formulaire
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 10, 10));

        // Champs pour saisir les informations de la réservation
        panel.add(new JLabel("Nom :"));
        JTextField nomField = new JTextField();
        panel.add(nomField);

        panel.add(new JLabel("Date (yyyy-MM-dd) :"));
        JDateChooser dateField = new JDateChooser();
        dateField.setDateFormatString("yyyy-MM-dd");
        panel.add(dateField);

        panel.add(new JLabel("Heure de début (HH:mm) :"));
        JTextField heureDebutField = new JTextField();
        panel.add(heureDebutField);

        panel.add(new JLabel("Heure de fin (HH:mm) :"));
        JTextField heureFinField = new JTextField();
        panel.add(heureFinField);

        panel.add(new JLabel("Type :"));
        String[] types = {"Cours", "Examen", "Révision", "Réunion", "Autre"};
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        panel.add(typeComboBox);

        panel.add(new JLabel("Description :"));
        JTextField descriptionField = new JTextField();
        panel.add(descriptionField);

        // Bouton pour valider la création
        JButton createButton = new JButton("Créer");
        panel.add(createButton);

        // ActionListener pour valider la création
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nom = nomField.getText();
                    Date date = dateField.getDate();
                    String heureDebutStr = heureDebutField.getText();
                    String heureFinStr = heureFinField.getText();
                    String type = (String) typeComboBox.getSelectedItem();
                    String description = descriptionField.getText();

// Spécificités fonctionnelles : Pas de cases vides
                    if (nom.isEmpty() || date == null || heureDebutStr.isEmpty() || heureFinStr.isEmpty() || type.isEmpty() || description.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Tous les champs doivent être remplis.");
                        return;
                    }
// SP : Format date correspondant à des chiffres puis vérification du format date
                   /* if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        JOptionPane.showMessageDialog(frame, "Le format de la date est incorrect. Format attendu : yyyy-MM-dd");
                        return;
                    }

                    try {
                        LocalDate.parse(dateStr);
                    } catch (DateTimeParseException dateexception) {
                        JOptionPane.showMessageDialog(frame, "La date est invalide. Elle doit exister réellement.");
                        return;
                    }*/

                    // Spécificités fonctionnelles : Nom, moins de 80 caractères
                    if (nom.length() > 80) {
                        JOptionPane.showMessageDialog(frame, "Le nom ne doit pas dépasser 80 caractères.");
                        return;
                    }

                    // SP : Date ne peut être dans le passé
                    if (date.before(new Date())) {
                        JOptionPane.showMessageDialog(frame, "La réservation ne peut pas être dans le passé.");
                        return;
                    }

                    Time heureDebut = Time.valueOf(heureDebutStr + ":00");
                    Time heureFin = Time.valueOf(heureFinStr + ":00");

                    // SP : Heure de début ne peut pas être après heure de fin
                    if (heureDebut.after(heureFin)) {
                        JOptionPane.showMessageDialog(frame, "L'heure de début ne peut pas être après l'heure de fin.");
                        return;
                    }


                    // Créer un objet Reservation
                    Reservation reservation = new Reservation();
                    reservation.setName(nom);
                    reservation.setDate(date);
                    reservation.setHeuredeb(heureDebut);
                    reservation.setHeurefin(heureFin);
                    reservation.setType(type);
                    reservation.setDescription(description);

                    // Appeler la méthode du service pour ajouter la réservation à la base de données
                    reservationService.insertReservation(reservation);
                    JOptionPane.showMessageDialog(frame, "Réservation créée avec succès !");
                } catch (IllegalArgumentException iaEx) {
                    JOptionPane.showMessageDialog(frame, "Erreur de format pour l'heure : " + iaEx.getMessage());
                } catch (IOException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(frame, "Erreur lors de la création de la réservation : " + ex.getMessage());
                }


                frame.dispose();  // Fermer le formulaire après création
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private void afficherFormulaireModification(Reservation reservation, DefaultTableModel tableModel, int selectedRow) {
        JFrame frame = new JFrame("Modifier une Réservation");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));

        panel.add(new JLabel("ID Réservation :"));
        JTextField idField = new JTextField(String.valueOf(reservation.getId()));
        idField.setEditable(false);
        panel.add(idField);

        panel.add(new JLabel("Nom :"));
        JTextField nomField = new JTextField(reservation.getName());
        panel.add(nomField);

        panel.add(new JLabel("Date (yyyy-MM-dd) :"));
        JDateChooser dateField = new JDateChooser();
        dateField.setDateFormatString("yyyy-MM-dd");
        dateField.setDate(reservation.getDate());
        panel.add(dateField);

        panel.add(new JLabel("Heure de début (HH:mm) :"));
        JTextField heureDebutField = new JTextField(reservation.getHeuredeb().toString());
        panel.add(heureDebutField);

        panel.add(new JLabel("Heure de fin (HH:mm) :"));
        JTextField heureFinField = new JTextField(reservation.getHeurefin().toString());
        panel.add(heureFinField);

        panel.add(new JLabel("Type :"));
        String[] types = {"Cours", "Examen", "Révision", "Réunion", "Autre"};
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        panel.add(typeComboBox);

        panel.add(new JLabel("Description :"));
        JTextField descriptionField = new JTextField(reservation.getDescription());
        panel.add(descriptionField);


        JButton updateButton = new JButton("Mettre à jour");
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String nom = nomField.getText();
                    Date selectedDate = dateField.getDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(selectedDate);
                    calendar.add(Calendar.DATE, 1); // ajouter un jour
                    Date date = calendar.getTime();
                    String heureDebutStr = heureDebutField.getText();
                    String heureFinStr = heureFinField.getText();
                    String type = (String) typeComboBox.getSelectedItem();
                    String description = descriptionField.getText();

                    // Spécificités fonctionnelles : Pas de cases vides
                    if (nom.isEmpty() || date == null || heureDebutStr.isEmpty() || heureFinStr.isEmpty() || type.isEmpty() || description.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Tous les champs doivent être remplis.");
                        return;
                    }

                    // Spécificités fonctionnelles : Nom, moins de 80 caractères
                    if (nom.length() > 80) {
                        JOptionPane.showMessageDialog(frame, "Le nom ne doit pas dépasser 80 caractères.");
                        return;
                    }

                    // SP : Date ne peut être dans le passé
                    if (date.before(new Date())) {
                        JOptionPane.showMessageDialog(frame, "La réservation ne peut pas être dans le passé.");
                        return;
                    }

                    Time heureDebut = Time.valueOf(heureDebutStr + ":00");
                    Time heureFin = Time.valueOf(heureFinStr + ":00");

                    // SP : Heure de début ne peut pas être après heure de fin
                    if (heureDebut.after(heureFin)) {
                        JOptionPane.showMessageDialog(frame, "L'heure de début ne peut pas être après l'heure de fin.");
                        return;
                    }

                    reservation.setName(nomField.getText());
                    reservation.setDate(date);
                    reservation.setHeuredeb(Time.valueOf(heureDebutField.getText() + ":00"));
                    reservation.setHeurefin(Time.valueOf(heureFinField.getText() + ":00"));
                    reservation.setType((String) typeComboBox.getSelectedItem());
                    reservation.setDescription(descriptionField.getText());

                    reservationService.updateReservation(reservation);
                    tableModel.setValueAt(reservation.getName(), selectedRow, 1);
                    tableModel.setValueAt(reservation.getDate(), selectedRow, 2);
                    tableModel.setValueAt(reservation.getHeuredeb(), selectedRow, 3);
                    tableModel.setValueAt(reservation.getHeurefin(), selectedRow, 4);
                    tableModel.setValueAt(reservation.getType(), selectedRow, 5);
                    tableModel.setValueAt(reservation.getDescription(), selectedRow, 6);
                    JOptionPane.showMessageDialog(frame, "Réservation mise à jour avec succès !");
                    frame.dispose();
                } catch (IllegalArgumentException iaEx) {
                    JOptionPane.showMessageDialog(frame, "Erreur de format pour l'heure : " + iaEx.getMessage());
                } catch (IOException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(frame, "Erreur lors de la mise à jour : " + ex.getMessage());
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}

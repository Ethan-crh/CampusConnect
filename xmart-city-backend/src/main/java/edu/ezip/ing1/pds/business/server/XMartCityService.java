package edu.ezip.ing1.pds.business.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import edu.ezip.ing1.pds.business.dto.Student;
//import edu.ezip.ing1.pds.business.dto.Students;
import edu.ezip.ing1.pds.business.dto.Utilisateur;
import edu.ezip.ing1.pds.business.dto.Utilisateurs;
import edu.ezip.ing1.pds.business.dto.Capteur;
import edu.ezip.ing1.pds.business.dto.Capteurs;
import edu.ezip.ing1.pds.business.dto.Reservation;
import edu.ezip.ing1.pds.business.dto.Reservations;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class XMartCityService {

    private final static String LoggingLabel = "B u s i n e s s - S e r v e r";
    private final Logger logger = LoggerFactory.getLogger(LoggingLabel);

    private enum Queries {
        //SELECT_ALL_STUDENTS("SELECT t.name, t.firstname, t.groupname, t.id FROM students t"),
        //INSERT_STUDENT("INSERT into students (name, firstname, groupname) values (?, ?, ?)");
        SELECT_ALL_UTILISATEURS("SELECT * FROM Utilisateur"),
        INSERT_UTILISATEUR("INSERT INTO Utilisateur (nom_utilisateur, nom, prenom, email, password) VALUES (?, ?, ?, ?, ?)"),
        UPDATE_UTILISATEUR("UPDATE Utilisateur SET nom_utilisateur = ?, nom = ?, prenom = ?, email = ?, password = ? WHERE id_utilisateur = ?"),
        DELETE_UTILISATEUR("DELETE FROM Utilisateur WHERE id_utilisateur = ?"),
        SELECT_UTILISATEUR_BY_EMAIL("SELECT * FROM Utilisateur WHERE email = ?"),
        SELECT_UTILISATEUR_BY_EMAIL_PASSWORD("SELECT * FROM Utilisateur WHERE email = ? AND password = ?"),
        SELECT_UTILISATEUR_BY_EMAIL_EXCLUDING_ID("SELECT * FROM Utilisateur WHERE email = ? AND id_utilisateur != ?"),
        SELECT_UTILISATEUR_BY_NOM_UTILISATEUR_EXCLUDING_ID("SELECT * FROM Utilisateur WHERE nom_utilisateur = ? AND id_utilisateur != ?"),

        SELECT_ALL_CAPTEURS("SELECT * FROM Capteurs "),
        INSERT_CAPTEUR("INSERT INTO Capteurs (id_capteur , statut, presence , detection_probleme) VALUES (?, ?, ?, ?)"),
        UPDATE_CAPTEUR("UPDATE Capteurs SET statut = ?, presence = ?, detection_probleme = ? WHERE id_capteur = ?"),
        DELETE_CAPTEUR("DELETE FROM Capteurs WHERE id_capteur = ?"),
        SELECT_ALL_RESERVATIONS("SELECT * FROM Reservations"),
        INSERT_RESERVATION("INSERT INTO Reservations (Name_resa, Date_resa, Heure_deb_resa, Heure_fin_resa , Type_resa, Description_resa) VALUES (?, ?, ?, ?, ?, ?)"),
        UPDATE_RESERVATION ("UPDATE Reservations SET Name_resa = ?, Date_resa = ?, Heure_deb_resa = ?, Heure_fin_resa = ?, Type_resa = ?, Description_resa = ? WHERE Id_resa = ?"),
        DELETE_RESERVATION("DELETE FROM Reservations WHERE Id_resa = ?");
        private final String query;

        private Queries(final String query) {
            this.query = query;
        }
    }

   /* private enum Queries1 {
        SELECT_ALL_CAPTEURS("SELECT * FROM Capteur "),
        INSERT_CAPTEUR("INSERT into Capteurs (id_capteur, statut, presence, detection_probleme) values (?, ?, ?, ?)");
        private final String query1;

        private Queries1(final String query) {
            this.query1 = query;
        }
    }

    private enum Queries2 {
        SELECT_ALL_RESERVATIONS("SELECT t.id, t.name, t.date, t.heuredeb, t.heurefin, t.type, t.description FROM reservation t"),
        INSERT_RESERVATION("INSERT INTO Reservations (id, name, date, heuredeb, heurefin, type, description) VALUES (?, ?, ?, ?, ?, ?, ?)");
        private final String query2;

        private Queries2(final String query) {
            this.query2 = query;
        }
    }*/

    public static XMartCityService inst = null;
    public static final XMartCityService getInstance()  {
        if(inst == null) {
            inst = new XMartCityService();
        }
        return inst;
    }

    private XMartCityService() {

    }

    public final Response dispatch(final Request request, final Connection connection)
            throws InvocationTargetException, IllegalAccessException, SQLException, IOException {
        Response response = null;

        final Queries queryEnum = Enum.valueOf(Queries.class, request.getRequestOrder());
        switch(queryEnum) {

            case SELECT_ALL_UTILISATEURS:
                response = selectAllUtilisateurs(request, connection);
                break;
            case SELECT_UTILISATEUR_BY_EMAIL:
                response = selectConectionCondition(request, connection);
                break;
            case SELECT_UTILISATEUR_BY_EMAIL_PASSWORD:
                response = checkConnexionUtilisateur(request, connection);
                break;
            case INSERT_UTILISATEUR:
                response = insertUtilisateur(request, connection);
                break;
            case UPDATE_UTILISATEUR:
                response = updateUtilisateur(request, connection);
                break;
            case DELETE_UTILISATEUR:
                response = deleteUtilisateur(request, connection);
                break;
            case SELECT_UTILISATEUR_BY_EMAIL_EXCLUDING_ID:
                response = selectUtilisateurByEmailExcludingId(request, connection);
                break;
            case SELECT_UTILISATEUR_BY_NOM_UTILISATEUR_EXCLUDING_ID:
                response = selectUtilisateurByNomExcludingId(request, connection);
                break;
            case SELECT_ALL_CAPTEURS:
                response = selectAllCapteurs(request, connection);
                break;
            case INSERT_CAPTEUR:
                response = insertCapteur(request, connection);
                break;
            case UPDATE_CAPTEUR:
                response = updateCapteur(request, connection);
                break;
            case DELETE_CAPTEUR:
                response = deleteCapteur(request, connection);
                break;
            case SELECT_ALL_RESERVATIONS:
                response = selectAllReservations(request, connection);
                break;
            case INSERT_RESERVATION:
                response = insertReservation(request, connection);
                break;
            case UPDATE_RESERVATION:
                response = updateReservation(request, connection);
                break;
            case DELETE_RESERVATION:
                response = deleteReservation(request, connection);
                break;
            default:
                break;


        }

        return response;
    }



    private Response insertUtilisateur(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Utilisateur utilisateur = objectMapper.readValue(request.getRequestBody(), Utilisateur.class);
        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_UTILISATEUR.query);
        stmt.setString(1, utilisateur.getNomUtilisateur());
        stmt.setString(2, utilisateur.getNom());
        stmt.setString(3, utilisateur.getPrenom());
        stmt.setString(4, utilisateur.getEmail());
        stmt.setString(5, utilisateur.getPassword());
        stmt.executeUpdate();


        return new Response(request.getRequestId(), objectMapper.writeValueAsString(utilisateur));
    }

    private Response insertCapteur(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Capteur capteur = objectMapper.readValue(request.getRequestBody(), Capteur.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_CAPTEUR.query);
        stmt.setString(1, capteur.getId());
        stmt.setBoolean(2, capteur.getPresence());
        stmt.setBoolean(3, capteur.getStatut());
        stmt.setBoolean(4, capteur.getDetectionProbleme());
        stmt.executeUpdate();

        /*final Statement stmt2 = connection.createStatement();
        final ResultSet res = stmt2.executeQuery("SELECT LAST_INSERT_ID()");
        res.next();

        capteur.setId(res.getString(1));*/

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(capteur));
    }
    private Response insertReservation(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Reservation reservation = objectMapper.readValue(request.getRequestBody(), Reservation.class);


        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_RESERVATION.query);
        stmt.setString(1, reservation.getName());
        stmt.setDate(2, new java.sql.Date(reservation.getDate().getTime()));
        stmt.setTime(3, reservation.getHeuredeb());
        stmt.setTime(4, reservation.getHeurefin());
        stmt.setString(5, reservation.getType());
        stmt.setString(6, reservation.getDescription());
        stmt.executeUpdate();

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(reservation));
    }


    private Response selectAllUtilisateurs(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_UTILISATEURS.query);
        Utilisateurs utilisateurs = new Utilisateurs();
        while (res.next()) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setIdUtilisateur(res.getInt(1));
            utilisateur.setNomUtilisateur(res.getString(2));
            utilisateur.setEmail(res.getString(3));
            utilisateur.setNom(res.getString(6));
            utilisateur.setPrenom(res.getString(7));

            utilisateurs.add(utilisateur);
        }

        if (utilisateurs.getUtilisateurs().isEmpty()) {
            return new Response(request.getRequestId(), "Aucun patient trouvé");
        }
        return new Response(request.getRequestId(), objectMapper.writeValueAsString(utilisateurs));
    }

    public Response  selectConectionCondition (Request request, Connection connection) throws IOException, SQLException {
        final ObjectMapper objectMapper = new ObjectMapper();

        String email;

        Utilisateur utilisateur= objectMapper.readValue(request.getRequestBody(), Utilisateur.class);
        email = utilisateur.getEmail();

        final PreparedStatement stmt = connection.prepareStatement(Queries.SELECT_UTILISATEUR_BY_EMAIL.query);
        stmt.setString(1, email);


        ResultSet res = stmt.executeQuery();

        Utilisateurs utilisateurs = new Utilisateurs();
        while (res.next()) {
            Utilisateur et = new Utilisateur();

            et.setEmail(res.getString("email"));

            utilisateurs.add(et);
        }

        res.close();
        stmt.close();

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(utilisateurs));
    }

    private Response checkConnexionUtilisateur(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Utilisateur utilisateur = objectMapper.readValue(request.getRequestBody(), Utilisateur.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.SELECT_UTILISATEUR_BY_EMAIL_PASSWORD.query);
        stmt.setString(1, utilisateur.getEmail());
        stmt.setString(2, utilisateur.getPassword());

        ResultSet res = stmt.executeQuery();
        Utilisateurs utilisateurs = new Utilisateurs();

        while (res.next()) {
            Utilisateur u = new Utilisateur();
            u.setIdUtilisateur(res.getInt("id_utilisateur"));
            u.setNomUtilisateur(res.getString("nom_utilisateur"));
            u.setEmail(res.getString("email"));
            utilisateurs.add(u);
        }

        res.close();
        stmt.close();

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(utilisateurs));
    }


    private Response selectUtilisateurByEmailExcludingId(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        Utilisateur utilisateur = objectMapper.readValue(request.getRequestBody(), Utilisateur.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.SELECT_UTILISATEUR_BY_EMAIL_EXCLUDING_ID.query);
        stmt.setString(1, utilisateur.getEmail());
        stmt.setInt(2, utilisateur.getIdUtilisateur());

        ResultSet res = stmt.executeQuery();
        Utilisateurs utilisateurs = new Utilisateurs();

        while (res.next()) {
            Utilisateur u = new Utilisateur();
            u.setIdUtilisateur(res.getInt("id_utilisateur"));
            utilisateurs.add(u);
        }

        res.close();
        stmt.close();

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(utilisateurs));
    }



    private Response updateUtilisateur(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Utilisateur utilisateur = objectMapper.readValue(request.getRequestBody(), Utilisateur.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_UTILISATEUR.query);
        stmt.setString(1, utilisateur.getNomUtilisateur());
        stmt.setString(2, utilisateur.getNom());
        stmt.setString(3, utilisateur.getPrenom());
        stmt.setString(4, utilisateur.getEmail());
        stmt.setString(5, utilisateur.getPassword());
        stmt.setInt(6, utilisateur.getIdUtilisateur());

        int rowsUpdated = stmt.executeUpdate();

        if (rowsUpdated == 0) {
            return new Response(request.getRequestId(), "Utilisateur non trouvé ou aucune mise à jour effectuée.");
        }

        return new Response(request.getRequestId(), "Utilisateur mis à jour avec succès.");
    }

    private Response deleteUtilisateur(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Utilisateur utilisateur = objectMapper.readValue(request.getRequestBody(), Utilisateur.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_UTILISATEUR.query);
        stmt.setInt(1, utilisateur.getIdUtilisateur());

        int rowsDeleted = stmt.executeUpdate();

        if (rowsDeleted == 0) {
            return new Response(request.getRequestId(), "Utilisateur non trouvé ou déjà supprimé.");
        }

        return new Response(request.getRequestId(), "Utilisateur supprimé avec succès.");
    }


    private Response selectUtilisateurByNomExcludingId(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        Utilisateur utilisateur = objectMapper.readValue(request.getRequestBody(), Utilisateur.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.SELECT_UTILISATEUR_BY_NOM_UTILISATEUR_EXCLUDING_ID.query);
        stmt.setString(1, utilisateur.getNomUtilisateur());
        stmt.setInt(2, utilisateur.getIdUtilisateur());

        ResultSet res = stmt.executeQuery();
        Utilisateurs utilisateurs = new Utilisateurs();

        while (res.next()) {
            Utilisateur u = new Utilisateur();
            u.setIdUtilisateur(res.getInt("id_utilisateur"));
            utilisateurs.add(u);
        }

        res.close();
        stmt.close();

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(utilisateurs));
    }





    private Response selectAllCapteurs(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_CAPTEURS.query);
        Capteurs capteurs = new Capteurs();
        while (res.next()) {
            Capteur capteur = new Capteur();
            capteur.setId(res.getString(1));
            capteur.setStatut(res.getBoolean(2));
            capteur.setPresence(res.getBoolean(3));
            capteur.setDetectionProbleme(res.getBoolean(4));
            capteurs.add(capteur);
        }
        return new Response(request.getRequestId(), objectMapper.writeValueAsString(capteurs));
    }

    private Response updateCapteur(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Capteur capteur = objectMapper.readValue(request.getRequestBody(), Capteur.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_CAPTEUR.query);
        stmt.setBoolean(1, capteur.getStatut());
        stmt.setBoolean(2, capteur.getPresence());
        stmt.setBoolean(3, capteur.getDetectionProbleme());
        stmt.setString(4, capteur.getId());

        int rowsUpdated = stmt.executeUpdate();

        if (rowsUpdated == 0) {
            return new Response(request.getRequestId(), "Capteur non trouvé ou aucune mise à jour effectuée.");
        }

        return new Response(request.getRequestId(), "Capteur mis à jour avec succès.");
    }

    private Response deleteCapteur(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Capteur capteur = objectMapper.readValue(request.getRequestBody(), Capteur.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_CAPTEUR.query);
        stmt.setString(1, capteur.getId());

        int rowsDeleted = stmt.executeUpdate();

        if (rowsDeleted == 0) {
            return new Response(request.getRequestId(), "Capteur non trouvé ou déjà supprimé.");
        }

        return new Response(request.getRequestId(), "Capteur supprimé avec succès.");
    }


    private Response selectAllReservations(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_RESERVATIONS.query);
        Reservations reservations = new Reservations();

        while (res.next()) {
            Reservation reservation = new Reservation();

            reservation.setName(res.getString(1));
            reservation.setDate(res.getDate(2));
            reservation.setHeuredeb(res.getTime(3));
            reservation.setHeurefin(res.getTime(4));
            reservation.setType(res.getString(5));
            reservation.setDescription(res.getString(6));
            reservation.setId(res.getInt(9));
            reservations.add(reservation);
        }

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(reservations));
    }


    private Response updateReservation(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Reservation reservation = objectMapper.readValue(request.getRequestBody(), Reservation.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.UPDATE_RESERVATION.query);
        stmt.setString(1, reservation.getName());
        stmt.setDate(2, new java.sql.Date(reservation.getDate().getTime()));
        stmt.setString(3, reservation.getHeuredeb().toString());
        stmt.setString(4, reservation.getHeurefin().toString());
        stmt.setString(5, reservation.getType());
        stmt.setString(6, reservation.getDescription());
        stmt.setString(7, String.valueOf(reservation.getId()));

        int rowsUpdated = stmt.executeUpdate();

        if (rowsUpdated == 0) {
            return new Response(request.getRequestId(), "Reservation non trouvée ou aucune mise à jour effectuée.");
        }

        return new Response(request.getRequestId(), "Reservation mise à jour avec succès.");
    }

    private Response deleteReservation(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Reservation reservation = objectMapper.readValue(request.getRequestBody(), Reservation.class);

        final PreparedStatement stmt = connection.prepareStatement(Queries.DELETE_RESERVATION.query);
        stmt.setInt(1, reservation.getId());


        int rowsDeleted = stmt.executeUpdate();

        if (rowsDeleted == 0) {
            return new Response(request.getRequestId(), "Reservation non trouvée ou déjà supprimée.");
        }

        return new Response(request.getRequestId(), "Reservation supprimée avec succès.");
    }
}
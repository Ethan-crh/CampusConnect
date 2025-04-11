package edu.ezip.ing1.pds.services;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.ezip.ing1.pds.business.dto.Utilisateur;
import edu.ezip.ing1.pds.business.dto.Utilisateurs;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.requests.InsertUtilisateursClientRequest;
import edu.ezip.ing1.pds.requests.SelectAllUtilisateursClientRequest;
import edu.ezip.ing1.pds.requests.UpdateUtilisateursClientRequest;
import edu.ezip.ing1.pds.requests.DeleteUtilisateursClientRequest;

public class UtilisateurService {

    private final static String LoggingLabel = "FrontEnd - UtilisateurService";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);

    final String insertRequestOrder = "INSERT_UTILISATEUR";
    final String selectRequestOrder = "SELECT_ALL_UTILISATEURS";
    final String updateRequestOrder = "UPDATE_UTILISATEUR";
    final String deleteRequestOrder = "DELETE_UTILISATEUR";
    final String selectByEmailRequestOrder = "SELECT_UTILISATEUR_BY_EMAIL";

    private final NetworkConfig networkConfig;

    public UtilisateurService(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    /**
     * Insère un utilisateur
     */
    public String insertUtilisateur(Utilisateur utilisateur) throws InterruptedException, IOException {
        if (checkEmailExists(utilisateur.getEmail())) {
            return "Email déjà utilisé";
        }
        return processUtilisateur(utilisateur, insertRequestOrder);
    }

    /**
     * Met à jour un utilisateur.
     */
    public void updateUtilisateur(Utilisateur utilisateur) throws InterruptedException, IOException {
        processUtilisateur(utilisateur, updateRequestOrder);
    }

    /**
     * Supprime un utilisateur.
     */
    public void deleteUtilisateur(Utilisateur utilisateur) throws InterruptedException, IOException {
        processUtilisateur(utilisateur, deleteRequestOrder);
    }

    /**
     * Méthode générique qui traite une requête d'insertion, de modification ou de suppression.
     */
    private String processUtilisateur(Utilisateur utilisateur, String requestOrder) throws InterruptedException, IOException {
        final Deque<ClientRequest> utilisateurRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonifiedUtilisateur = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(utilisateur);

        logger.trace("Utilisateur en JSON : {}", jsonifiedUtilisateur);

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(requestOrder);
        request.setRequestContent(jsonifiedUtilisateur);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        ClientRequest utilisateurRequest;
        // requetes
        if ("INSERT_UTILISATEUR".equals(requestOrder)) {
            utilisateurRequest = new InsertUtilisateursClientRequest(networkConfig, 0, request, utilisateur, requestBytes);
        } else if ("UPDATE_UTILISATEUR".equals(requestOrder)) {
            utilisateurRequest = new UpdateUtilisateursClientRequest(networkConfig, 0, request, utilisateur, requestBytes);
        } else if ("DELETE_UTILISATEUR".equals(requestOrder)) {
            utilisateurRequest = new DeleteUtilisateursClientRequest(networkConfig, 0, request, utilisateur, requestBytes);
        } else {
            throw new IllegalArgumentException("Requête non supportée : " + requestOrder);
        }

        utilisateurRequests.push(utilisateurRequest);

        while (!utilisateurRequests.isEmpty()) {
            final ClientRequest processedRequest = utilisateurRequests.pop();
            processedRequest.join();

            final Utilisateur processedUtilisateur = (Utilisateur) processedRequest.getInfo();
            logger.debug("Thread {} terminé : {} {} {}  --> {}",
                    processedRequest.getThreadName(),
                    processedUtilisateur.getNom(), processedUtilisateur.getPrenom(), processedUtilisateur.getEmail(),
                    processedRequest.getResult());

            if ("INSERT_UTILISATEUR".equals(requestOrder)) {
                return (String) processedRequest.getResult();
            }
        }

        return "OK"; // Si update ou delete
    }

    /**
     * Récupère tous les utilisateurs.
     */
    public Utilisateurs selectUtilisateurs() throws InterruptedException, IOException {
        final Deque<ClientRequest> utilisateurRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(selectRequestOrder);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        logger.debug("Envoi de la requête SELECT_ALL_UTILISATEURS avec ID {}", requestId);

        final SelectAllUtilisateursClientRequest utilisateurRequest = new SelectAllUtilisateursClientRequest(
                networkConfig, 0, request, null, requestBytes);
        utilisateurRequests.push(utilisateurRequest);

        if (!utilisateurRequests.isEmpty()) {
            final ClientRequest joinedUtilisateurRequest = utilisateurRequests.pop();
            joinedUtilisateurRequest.join();
            Object result = joinedUtilisateurRequest.getResult();

            if (result instanceof Utilisateurs) {
                Utilisateurs utilisateurs = (Utilisateurs) result;
                logger.info(" {} utilisateurs récupérés.", utilisateurs.getUtilisateurs().size());
                return utilisateurs;
            } else {
                logger.warn(" Aucun utilisateur trouvé.");
                return null;
            }
        } else {
            logger.error("Aucun utilisateur récupéré.");
            return null;
        }
    }

    /**
     * Vérifie si un email existe déjà en base.
     */
    public boolean checkEmailExists(String email) throws InterruptedException, IOException {
        final Deque<ClientRequest> utilisateurRequests = new ArrayDeque<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);

        final String jsonifiedUtilisateur = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(utilisateur);

        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(selectByEmailRequestOrder);
        request.setRequestContent(jsonifiedUtilisateur);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);

        final SelectAllUtilisateursClientRequest utilisateurRequest = new SelectAllUtilisateursClientRequest(
                networkConfig, 0, request, utilisateur, requestBytes);
        utilisateurRequests.push(utilisateurRequest);

        if (!utilisateurRequests.isEmpty()) {
            final ClientRequest joinedUtilisateurRequest = utilisateurRequests.pop();
            joinedUtilisateurRequest.join();
            Object result = joinedUtilisateurRequest.getResult();

            if (result instanceof Utilisateurs) {
                Utilisateurs utilisateurs = (Utilisateurs) result;
                return !utilisateurs.getUtilisateurs().isEmpty();
            }
        }
        return false;
    }
}

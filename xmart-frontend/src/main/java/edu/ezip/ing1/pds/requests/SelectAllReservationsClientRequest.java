package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Reservation;
import edu.ezip.ing1.pds.business.dto.Reservations;
import edu.ezip.ing1.pds.business.dto.Utilisateurs;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;

public class SelectAllReservationsClientRequest extends ClientRequest<Object, Reservations> {

    public SelectAllReservationsClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Object info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public Reservations readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(body, Reservations.class);
    }
}
package dao;

import model.Hackathon;

import java.util.List;

public interface HackathonDAO {
    void creaHackathon(Hackathon h);
    List<Hackathon> findAll();
    List<Hackathon> findByOrganizzatore(String email);



    void invitaGiudice(String titoloHackathon, String organizzatoreEmail, String giudiceEmail);

    /**
     * Restituisce le email dei giudici già invitati per (titolo, organizzatore).
     */
    List<String> listaGiudiciInvitati(String titoloHackathon, String organizzatoreEmail);
}


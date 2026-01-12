package dao;

import model.Hackathon;

import java.util.List;

public interface HackathonDAO {
    void creaHackathon(Hackathon h);
    List<Hackathon> findAll();
    List<Hackathon> findByOrganizzatore(String email);
    Long findIdByTitolo(String titolo);



    void invitaGiudice(String titoloHackathon, String organizzatoreEmail, String giudiceEmail);

    List<String> listaGiudiciInvitati(String titoloHackathon, String organizzatoreEmail);

    List<Hackathon> findByUtenteIscritto(String emailUtente);

    List<Hackathon> findInvitiPerGiudice(String emailGiudice);

    boolean accettaInvitoGiudice(String titoloHackathon, String giudiceEmail);

    List<Hackathon> findAssegnatiPerGiudice(String emailGiudice);



}


package dao;

import model.Hackathon;

import java.util.List;

/**
 * DAO per la gestione degli hackathon e delle operazioni collegate.
 * <p>
 * Qui ci sono sia operazioni "base" (creazione e query) sia operazioni di piattaforma:
 * invito giudici, lista inviti, accettazione invito giudice e recupero hackathon assegnati.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Hackathon
 */

public interface HackathonDAO {
    /**
     * Salva un nuovo hackathon nel database.
     *
     * @param h oggetto hackathon con tutti i dati necessari
     * @see model.Hackathon
     */

    void creaHackathon(Hackathon h);
    /**
     * Recupera tutti gli hackathon presenti nel database.
     *
     * @return lista hackathon (vuota se non ce ne sono)
     */

    List<Hackathon> findAll();
    /**
     * Recupera gli hackathon creati da uno specifico organizzatore.
     *
     * @param email email dell'organizzatore
     * @return lista hackathon creati da quell'organizzatore
     */

    List<Hackathon> findByOrganizzatore(String email);

    /**
     * Trova l'id di un hackathon a partire dal suo titolo.
     *
     * @param titolo titolo dell'hackathon
     * @return id dell'hackathon, oppure {@code null} se non esiste
     */

    Long findIdByTitolo(String titolo);


    /**
     * Invia (registra) un invito ad un giudice per un hackathon.
     *
     * @param titoloHackathon titolo dell'hackathon
     * @param organizzatoreEmail email dell'organizzatore che invita
     * @param giudiceEmail email del giudice invitato
     */


    void invitaGiudice(String titoloHackathon, String organizzatoreEmail, String giudiceEmail);

    /**
     * Elenca i giudici invitati da un organizzatore per un dato hackathon.
     *
     * @param titoloHackathon titolo dell'hackathon
     * @param organizzatoreEmail email dell'organizzatore
     * @return lista email dei giudici invitati (vuota se nessuno)
     */

    List<String> listaGiudiciInvitati(String titoloHackathon, String organizzatoreEmail);

    /**
     * Recupera gli hackathon a cui un utente risulta iscritto.
     *
     * @param emailUtente email dell'utente
     * @return lista hackathon a cui è iscritto
     */

    List<Hackathon> findByUtenteIscritto(String emailUtente);
    /**
     * Recupera gli hackathon per cui un giudice ha ricevuto un invito (non per forza già accettato).
     *
     * @param emailGiudice email del giudice
     * @return lista hackathon invitati al giudice
     */

    List<Hackathon> findInvitiPerGiudice(String emailGiudice);
    /**
     * Accetta l'invito del giudice per un hackathon.
     * <p>
     * Dopo l'accettazione, l'hackathon risulta tra quelli assegnati al giudice.
     *
     * @param titoloHackathon titolo dell'hackathon
     * @param giudiceEmail email del giudice
     * @return true se l'invito viene accettato, false se non esiste/già accettato/non valido
     */

    boolean accettaInvitoGiudice(String titoloHackathon, String giudiceEmail);

    /**
     * Recupera gli hackathon assegnati ad un giudice (tipicamente dopo accettazione invito).
     *
     * @param emailGiudice email del giudice
     * @return lista hackathon assegnati al giudice
     */

    List<Hackathon> findAssegnatiPerGiudice(String emailGiudice);



}


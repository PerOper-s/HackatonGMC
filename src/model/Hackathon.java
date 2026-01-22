package model;

import java.util.ArrayList;
import java.util.List;


/**
 * Modello di dominio per un Hackathon.
 * <p>
 * Contiene i dati principali (titolo, sede, date, limiti) e mantiene i riferimenti
 * alle entità collegate (organizzatore, iscrizioni, giudici invitati, problemi e classifica).
 * <p>
 * Nota: in questo modello alcune date sono gestite come String (formato UI), mentre nel DB
 * vengono poi convertite quando necessario.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Organizzatore
 * @see model.Iscrizione
 * @see model.Giudice
 * @see model.Problema
 * @see model.Classifica
 */

public class Hackathon {
    private String titolo;
    private String inizio;
    private String sede;
    private int maxPartecipanti;
    private int maxGrandezzaTeam;
    private String descrizioneProblem;
    private String inizioIscrizioni;
    private String fineIscrizioni;

    // Riferimenti associati
    private Organizzatore organizzatore;
    private List<Iscrizione> iscrizioni;
    private List<Giudice> giudiciInvitati;
    private List<Classifica> classifica;
    private List<Problema> problemi;



    /**
     * Crea un hackathon con i dati principali e inizializza le liste associate.
     *
     * @param titolo titolo identificativo dell'hackathon
     * @param sede sede dell'evento
     * @param organizzatore organizzatore che lo crea/gestisce
     * @param maxPartecipanti limite massimo iscritti
     * @param maxGrandezzaTeam dimensione massima di un team
     * @param inizio data/ora di inizio (gestita come stringa nel modello)
     * @param inizioIscrizioni data inizio iscrizioni (stringa)
     * @param fineIscrizioni data fine iscrizioni (stringa)
     */

    public Hackathon(String titolo, String sede, Organizzatore organizzatore,
                     int maxPartecipanti, int maxGrandezzaTeam, String inizio,
                     String inizioIscrizioni, String fineIscrizioni) {
        this.titolo = titolo;
        this.organizzatore = organizzatore;
        this.maxPartecipanti = maxPartecipanti;
        this.maxGrandezzaTeam = maxGrandezzaTeam;

        this.inizio = inizio;
        this.sede = sede;
        this.descrizioneProblem = "";
        this.inizioIscrizioni = inizioIscrizioni;
        this.fineIscrizioni = fineIscrizioni;

        this.iscrizioni = new ArrayList<>();
        this.giudiciInvitati = new ArrayList<>();
        this.classifica = new ArrayList<>();
        this.problemi = new ArrayList<>();
    }


    /**
     * @return numero di iscrizioni attualmente presenti in memoria per questo hackathon
     */

    public int getIscrizioniCount() {
        return iscrizioni.size();
    }

    /**
     * @return email dell'organizzatore associato all'hackathon
     */


    public String getOrganizzatore() {
        return organizzatore.getMail();
    }

    /**
     * @return sede dell'hackathon
     */


    public String getSede () {
        return sede;
    }

    /**
     * @return numero massimo di partecipanti consentiti
     */

    public int getMaxPartecipanti() {
        return maxPartecipanti;
    }

    /**
     * @return titolo dell'hackathon
     */

    public String getTitolo() {
        return titolo;
    }

    /**
     * @return dimensione massima consentita per un team
     */


    public int getMaxGrandezzaTeam() {
        return maxGrandezzaTeam;
    }

    /**
     * @return data/ora di inizio (come stringa nel modello)
     */


    public String getInizio() {
        return inizio;
    }

    /**
     * @return data inizio iscrizioni (come stringa nel modello)
     */

    public String getInizioIscrizioni() {
        return inizioIscrizioni;
    }

    /**
     * @return data fine iscrizioni (come stringa nel modello)
     */

    public String getFineIscrizioni() {
        return fineIscrizioni;
    }

    /**
     * @return lista dei giudici invitati all'hackathon
     */

    public List<Giudice> getGiudiciInvitati() {
        return giudiciInvitati;
    }

    /**
     * Aggiunge un partecipante all'hackathon creando una nuova iscrizione.
     *
     * @param utente utente che si iscrive
     * @param data data dell'iscrizione (stringa)
     * @see model.Iscrizione
     */

    public void aggiungiPartecipante(Utente utente, String data) {
        Iscrizione iscr = new Iscrizione(data, utente, this);
        iscrizioni.add(iscr);
        System.out.println("Utente " + utente.getMail() + " iscritto all'hackathon \"" + this.titolo + "\"");
    }

    /**
     * Aggiunge un giudice all'elenco dei giudici invitati (evitando duplicati).
     *
     * @param giudice giudice da invitare/aggiungere
     */

    public void aggiungiGiudice(Giudice giudice) {
        if (!giudiciInvitati.contains(giudice)) {
            giudiciInvitati.add(giudice);
        }
    }


    /**
     * Registra un problema pubblicato per l'hackathon.
     *
     * @param problema problema pubblicato da un giudice
     * @see model.Problema
     */

    // Aggiunge un nuovo problema pubblicato da un giudice
    public void aggiungiProblema(Problema problema) {
        problemi.add(problema);
    }

    /**
     * Aggiunge un team tra i partecipanti e lo inserisce in classifica con punteggio iniziale 0
     * (se non è già presente).
     *
     * @param team team da aggiungere
     * @see model.Classifica
     */
    public void aggiungiTeam(Team team) {
        // Evita duplicati in classifica
        boolean exists = false;
        for (Classifica c : classifica) {
            if (c.getTeam() == team) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            classifica.add(new Classifica(team, 0));
        }
    }

    // Aggiorna il punteggio di un team nella classifica
    public void aggiornaPunteggio(Team team, int punteggio) {
        for (Classifica c : classifica) {
            if (c.getTeam() == team) {
                c.setPunteggio(punteggio);
                return;
            }
        }
        // Se il team non era presente, lo aggiunge ora
        classifica.add(new Classifica(team, punteggio));
    }

    /**
     * Stampa su console la classifica attuale dell'hackathon.
     * <p>
     * Metodo utile soprattutto per debug/test.
     */

    public void stampaClassifica() {
        System.out.println("Classifica per hackathon \"" + titolo + "\":");
        for (Classifica c : classifica) {
            Team team = c.getTeam();
            System.out.println(" - Team (membri: " + team.getNumeroMembri() + ") punteggio: " + c.getPunteggio());
        }
    }
}

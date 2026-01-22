package model;

import java.util.ArrayList;
import java.util.List;


/**
 * Team partecipante ad un hackathon.
 * <p>
 * Contiene nome team, lista membri e documenti di progress.
 * Può avere un voto finale (per la classifica).
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Hackathon
 * @see model.Documento
 * @see model.Utente
 */

public class Team {
    private List<Utente> membri;
    private List<Documento> progressi;
    private int votoFinale;
    private Hackathon hackathon;
    private String nome;


    /**
     * Crea un team con un nome e inizializza le liste interne.
     *
     */

    public Team(Hackathon hackathon) {
        this.membri = new ArrayList<>();
        this.progressi = new ArrayList<>();
        this.votoFinale = 0;
        this.hackathon = hackathon;
        if (hackathon != null) {
            hackathon.aggiungiTeam(this);
        }
    }

    public Team(Utente primoMembro, Hackathon hackathon) {
        this(hackathon);
        if (primoMembro != null) {
            membri.add(primoMembro);
        }
    }

    // Verifica se il team ha raggiunto il numero massimo di membri
    public boolean èPieno() {
        if (hackathon == null) return false;
        return membri.size() >= hackathon.getMaxGrandezzaTeam();
    }

    /**
     * Aggiunge un membro al team (versione modello).
     *
     * @param utente utente da aggiungere come membro
     */

    public void aggiungiMembro(Utente utente) {
        if (utente == null) return;
        if (membri.contains(utente)) {
            System.out.println("Utente " + utente.getMail() + " è già nel team.");
            return;
        }
        if (èPieno()) {
            System.out.println("Il team è al completo, impossibile aggiungere nuovi membri.");
        } else {
            membri.add(utente);
            System.out.println("Utente " + utente.getMail() + " aggiunto al team.");
        }
    }

    /**
     * Aggiunge un documento di progress alla lista del team.
     *
     * @param doc documento caricato dal team
     * @see model.Documento
     */

    public void aggiungiDocumento(Documento doc) {
        if (doc != null) {
            progressi.add(doc);
        }
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public int getNumeroMembri() {
        return membri.size();
    }

    /** @return voto finale del team ) */


    public int getVotoFinale() {
        return votoFinale;
    }

    /**
     * Imposta il voto finale del team (usato per la classifica).
     *
     * @param votoFinale voto finale assegnato
     */


    public void setVotoFinale(int votoFinale) {
        this.votoFinale = votoFinale;
    }


    /** @return nome del team */

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}



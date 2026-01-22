package model;


/**
 * Oggetto "info" usato per mostrare i team in dashboard.
 * <p>
 * È un DTO: contiene dati già pronti per la UI (nome team, titolo hackathon, id, ecc.)
 * senza portarsi dietro tutta la struttura del modello.
 *
 * @author Gruppo ...
 * @version 1.0
 */

public class TeamInfo {
    private long id;
    private String nomeTeam;
    private String titoloHackathon;
    private String emailCreatore;
    private int numeroMembri;


    /**
     * Crea un TeamInfo con i dati essenziali per visualizzazione e controlli in UI.
     *
     * @param id id del team
     * @param nomeTeam nome del team
     * @param titoloHackathon titolo dell'hackathon
     */

    public TeamInfo(long id, String nomeTeam, String titoloHackathon,
                    String emailCreatore, int numeroMembri) {
        this.id = id;
        this.nomeTeam = nomeTeam;
        this.titoloHackathon = titoloHackathon;
        this.emailCreatore = emailCreatore;
        this.numeroMembri = numeroMembri;
    }

    public long getId() {
        return id;
    }

    public String getNomeTeam() {
        return nomeTeam;
    }

    public String getTitoloHackathon() {
        return titoloHackathon;
    }

    public String getEmailCreatore() {
        return emailCreatore;
    }

    public int getNumeroMembri() {
        return numeroMembri;
    }

    public boolean isCreatore(String emailUtente) {
        return emailUtente != null && emailUtente.equalsIgnoreCase(emailCreatore);
    }
}

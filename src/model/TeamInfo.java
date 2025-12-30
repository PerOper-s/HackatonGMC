package model;

public class TeamInfo {
    private long id;
    private String nomeTeam;
    private String titoloHackathon;
    private String emailCreatore;
    private int numeroMembri;

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

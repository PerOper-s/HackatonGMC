package model;

public class Documento {
    private String contenuto;
    private Team team;
    private String data;

    public Documento(String contenuto, Team team, String data) {
        this.contenuto = contenuto;
        this.team = team;
        this.data = data;
        if (team != null) {
            team.aggiungiDocumento(this);
        }
    }

    public String getContenuto() {
        return contenuto;
    }

    public Team getTeam() {
        return team;
    }

    public String getData() {
        return data;
    }
}

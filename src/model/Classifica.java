package model;

public class Classifica {
    private Team team;
    private int punteggio;

    public Classifica(Team team, int punteggio) {
        this.team = team;
        this.punteggio = punteggio;
    }

    public Team getTeam() {
        return team;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }
}

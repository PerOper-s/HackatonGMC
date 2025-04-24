package model;

import java.util.ArrayList;
import java.util.List;

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

    // Costruttore con i campi fondamentali
    public Hackathon(String titolo, Organizzatore organizzatore, int maxPartecipanti, int maxGrandezzaTeam) {
        this.titolo = titolo;
        this.organizzatore = organizzatore;
        this.maxPartecipanti = maxPartecipanti;
        this.maxGrandezzaTeam = maxGrandezzaTeam;
        // Valori di default per gli altri campi
        this.inizio = "";
        this.sede = "";
        this.descrizioneProblem = "";
        this.inizioIscrizioni = "";
        this.fineIscrizioni = "";

        this.iscrizioni = new ArrayList<>();
        this.giudiciInvitati = new ArrayList<>();
        this.classifica = new ArrayList<>();
        this.problemi = new ArrayList<>();
    }

    public String getTitolo() {
        return titolo;
    }

    public int getMaxGrandezzaTeam() {
        return maxGrandezzaTeam;
    }

    // Aggiunge un partecipante all'hackathon, creando un'iscrizione con la data fornita
    public void aggiungiPartecipante(Utente utente, String data) {
        Iscrizione iscr = new Iscrizione(data, utente, this);
        iscrizioni.add(iscr);
        System.out.println("Utente " + utente.getMail() + " iscritto all'hackathon \"" + this.titolo + "\"");
    }

    // Aggiunge un giudice all'elenco dei giudici invitati
    public void aggiungiGiudice(Giudice giudice) {
        if (!giudiciInvitati.contains(giudice)) {
            giudiciInvitati.add(giudice);
        }
    }

    // Aggiunge un nuovo problema pubblicato da un giudice
    public void aggiungiProblema(Problema problema) {
        problemi.add(problema);
    }

    // Aggiunge un nuovo team partecipante (inizialmente con punteggio 0 in classifica)
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

    // Stampa la classifica finale dell'hackathon (team e punteggi)
    public void stampaClassifica() {
        System.out.println("Classifica per hackathon \"" + titolo + "\":");
        for (Classifica c : classifica) {
            Team team = c.getTeam();
            System.out.println(" - Team (membri: " + team.getNumeroMembri() + ") punteggio: " + c.getPunteggio());
        }
    }
}

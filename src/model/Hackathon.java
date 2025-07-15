package model;

import java.util.ArrayList;
import java.util.List;

public class Hackathon {
    private String titolo;
    private String inizio;
    private String sede;
    private int maxPartecipanti;
    private int maxGrandezzaTeam;
    private String inizioIscrizioni;
    private String fineIscrizioni;
    private Organizzatore organizzatore;

    private List<Iscrizione> iscrizioni = new ArrayList<>();
    private List<Giudice> giudiciInvitati = new ArrayList<>();
    private List<Classifica> classifica = new ArrayList<>();
    private List<Problema> problemi = new ArrayList<>();

    public Hackathon(String titolo, Organizzatore organizzatore, int maxPartecipanti, int maxGrandezzaTeam, String sede, String inizio, String inizioIscrizioni, String fineIscrizioni) {
        this.titolo = titolo;
        this.organizzatore = organizzatore;
        this.maxPartecipanti = maxPartecipanti;
        this.maxGrandezzaTeam = maxGrandezzaTeam;
        this.inizio = inizio;
        this.sede = sede;
        this.inizioIscrizioni = inizioIscrizioni;
        this.fineIscrizioni = fineIscrizioni;
    }

    public String getTitolo() {
        return titolo;
    }

    public int getMaxGrandezzaTeam() {
        return this.maxGrandezzaTeam;
    }

    public String getDescrizione() {
        return "Hackathon: " + titolo + "\n" +
                "Organizzatore: " + organizzatore.getMail() + "\n" +
                "Sede: " + sede + "\n" +
                "Inizio: " + inizio + "\n" +
                "Inizio Iscrizioni: " + inizioIscrizioni + "\n" +
                "Fine Iscrizioni: " + fineIscrizioni + "\n" +
                "Max Partecipanti: " + maxPartecipanti + "\n" +
                "Max Membri Team: " + maxGrandezzaTeam;
    }

    public void aggiungiPartecipante(Utente utente, String data) {
        Iscrizione iscr = new Iscrizione(data, utente, this);
        iscrizioni.add(iscr);
        System.out.println("Utente " + utente.getMail() + " iscritto all'hackathon \"" + this.titolo + "\"");
    }

    public void aggiungiGiudice(Giudice giudice) {
        if (!giudiciInvitati.contains(giudice)) {
            giudiciInvitati.add(giudice);
        }
    }

    public void aggiungiProblema(Problema problema) {
        problemi.add(problema);
    }

    public void aggiungiTeam(Team team) {
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

    public void aggiornaPunteggio(Team team, int punteggio) {
        for (Classifica c : classifica) {
            if (c.getTeam() == team) {
                c.setPunteggio(punteggio);
                return;
            }
        }
        classifica.add(new Classifica(team, punteggio));
    }

    public void stampaClassifica() {
        System.out.println("Classifica per hackathon \"" + titolo + "\":");
        for (Classifica c : classifica) {
            Team team = c.getTeam();
            System.out.println(" - Team (membri: " + team.getNumeroMembri() + ") punteggio: " + c.getPunteggio());
        }
    }
}
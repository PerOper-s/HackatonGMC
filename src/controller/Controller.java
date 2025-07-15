package controller;

import gui.DashboardGiudice;
import gui.DashboardOrganizzatore;
import gui.DashboardUtente;
import gui.Home;
import model.Giudice;
import model.Hackathon;
import model.Organizzatore;
import model.Utente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Controller {

    private JFrame frame;
    private Home loginFrame;
    private ButtonGroup gruppoRuoli;
    private JRadioButton utenteRadioButton;
    private JRadioButton organizzatoreRadioButton;
    private JRadioButton giudiceRadioButton;

    private int stepCreazione;
    private final ArrayList<String> datiCreazioneHackathon = new ArrayList<>();
    private final String[] prompts = {
            "Titolo:", "Sede:", "Data Inizio (GG/MM/AAAA):", "Inizio Iscrizioni (GG/MM/AAAA):",
            "Fine Iscrizioni (GG/MM/AAAA):", "Max Partecipanti:", "Max Membri Team:"
    };

    public Controller() {
        frame = new JFrame("Hackaton");
        loginFrame = new Home();
        frame.setContentPane(loginFrame.getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);

        gruppoRuoli = new ButtonGroup();
        utenteRadioButton = loginFrame.getUtenteRadioButton();
        organizzatoreRadioButton = loginFrame.getOrganizzatoreRadioButton();
        giudiceRadioButton = loginFrame.getGiudiceRadioButton();
        gruppoRuoli.add(utenteRadioButton);
        gruppoRuoli.add(organizzatoreRadioButton);
        gruppoRuoli.add(giudiceRadioButton);

        aggiungiListeners();
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

    public void aggiungiListeners() {
        loginFrame.getLoginBtn().addActionListener(e -> {
            String emailInserita = loginFrame.getEmailTextField().getText();
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
            JLabel messaggioErrore = loginFrame.getMessaggioErrore();
            messaggioErrore.setText("");

            if (gruppoRuoli.getSelection() == null) {
                messaggioErrore.setText("Seleziona un ruolo.");
                return;
            }
            if (emailInserita.isEmpty() || emailInserita.equals("Email")) {
                messaggioErrore.setText("Il campo email non può essere vuoto.");
                return;
            }
            if (!emailInserita.matches(emailRegex)) {
                messaggioErrore.setText("Formato email non valido.");
                return;
            }

            Utente nuovoUtente;
            if (organizzatoreRadioButton.isSelected()) {
                nuovoUtente = new Organizzatore(emailInserita);
            } else if (giudiceRadioButton.isSelected()) {
                nuovoUtente = new Giudice(emailInserita);
            } else {
                nuovoUtente = new Utente(emailInserita);
            }

            nuovoUtente.registrati();
            frame.dispose();

            if (nuovoUtente instanceof Organizzatore) {
                gestisciDashboardOrganizzatore((Organizzatore) nuovoUtente);
            }
        });
    }

    private void gestisciDashboardOrganizzatore(Organizzatore organizzatore) {
        DashboardOrganizzatore vistaDashboard = new DashboardOrganizzatore(organizzatore.getMail());
        JFrame frameDashboard = new JFrame("HackatonDashboard - Organizzatore");
        frameDashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameDashboard.setContentPane(vistaDashboard.getDashboardOrganizzatore());
        frameDashboard.setResizable(false);
        frameDashboard.pack();
        frameDashboard.setLocationRelativeTo(null);
        frameDashboard.setVisible(true);

        vistaDashboard.getPannelloLogico().setVisible(false);

        vistaDashboard.getCreaHackaton().addActionListener(e -> {
            stepCreazione = 0;
            datiCreazioneHackathon.clear();
            vistaDashboard.getPannelloLogico().setVisible(true);
            aggiornaUIWizard(vistaDashboard);
        });

        vistaDashboard.getAvantiButton().addActionListener(e -> {
            String input = vistaDashboard.getFieldScrittura().getText();
            if (input.trim().isEmpty()) {
                vistaDashboard.getMessaggioErroreOrg().setText("Il campo non può essere vuoto.");
                return;
            }
            vistaDashboard.getMessaggioErroreOrg().setText("");
            if (stepCreazione < datiCreazioneHackathon.size()) {
                datiCreazioneHackathon.set(stepCreazione, input);
            } else {
                datiCreazioneHackathon.add(input);
            }
            stepCreazione++;
            aggiornaUIWizard(vistaDashboard);
        });

        vistaDashboard.getIndietroButton().addActionListener(e -> {
            if (stepCreazione > 0) {
                stepCreazione--;
                aggiornaUIWizard(vistaDashboard);
            }
        });

        vistaDashboard.getCreaButton().addActionListener(e -> {
            try {
                String titolo = datiCreazioneHackathon.get(0);
                String sede = datiCreazioneHackathon.get(1);
                String inizio = datiCreazioneHackathon.get(2);
                String inizioIscrizioni = datiCreazioneHackathon.get(3);
                String fineIscrizioni = datiCreazioneHackathon.get(4);
                int maxPartecipanti = Integer.parseInt(datiCreazioneHackathon.get(5));
                int maxTeam = Integer.parseInt(datiCreazioneHackathon.get(6));

                Hackathon hackathonCreato = organizzatore.creaHackathon(titolo, maxPartecipanti, maxTeam, sede, inizio, inizioIscrizioni, fineIscrizioni);
                JOptionPane.showMessageDialog(frameDashboard, "Hackathon '" + hackathonCreato.getTitolo() + "' creato!");

                vistaDashboard.getPannelloLogico().setVisible(false);
                stepCreazione = 0;
                datiCreazioneHackathon.clear();

            } catch (NumberFormatException ex) {
                vistaDashboard.getMessaggioErroreOrg().setText("I campi 'Max Partecipanti' e 'Max Team' devono essere numeri.");
            } catch (IndexOutOfBoundsException ex) {
                vistaDashboard.getMessaggioErroreOrg().setText("Completa tutti i passaggi prima di creare.");
            }
        });
    }

    private void aggiornaUIWizard(DashboardOrganizzatore vista) {
        JLabel promptLabel = vista.getMessaggioErroreOrg();
        JTextField inputField = vista.getFieldScrittura();
        JLabel anteprimaLabel = vista.getAreaDiTesto();

        StringBuilder anteprima = new StringBuilder("<html><u>Riepilogo Hackathon:</u><br>");
        for (int i = 0; i < datiCreazioneHackathon.size(); i++) {
            anteprima.append("<b>").append(prompts[i]).append("</b> ").append(datiCreazioneHackathon.get(i)).append("<br>");
        }
        anteprima.append("</html>");
        anteprimaLabel.setText(anteprima.toString());

        boolean isWizardFinito = (stepCreazione >= prompts.length);

        promptLabel.setText(isWizardFinito ? "Dati completi. Conferma la creazione." : prompts[stepCreazione]);
        inputField.setVisible(!isWizardFinito);
        vista.getAvantiButton().setVisible(!isWizardFinito);
        vista.getCreaButton().setVisible(isWizardFinito);
        vista.getIndietroButton().setVisible(stepCreazione > 0);

        if (!isWizardFinito) {
            if (stepCreazione < datiCreazioneHackathon.size()) {
                inputField.setText(datiCreazioneHackathon.get(stepCreazione));
            } else {
                inputField.setText("");
            }
            inputField.requestFocusInWindow();
        }
    }
}
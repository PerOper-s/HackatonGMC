package controller;

import gui.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import dao.UtenteDAO;
import daoImpl.UtenteDAOImpl;
import dao.HackathonDAO;
import daoImpl.HackathonDAOImpl;
import java.util.List;
import model.Hackathon;
import model.*;


/**
 * Controller principale del progetto (parte "Control" del pattern BCE).
 * <p>
 * Qui collego la GUI Swing (login + dashboard) con la logica: quando clicco bottoni o inserisco testo,
 * questo controller decide cosa fare e usa i DAO per parlare col database.
 * <p>
 * Dentro ci sono anche i "wizard" a passi (passoX + datiX): servono per guidare l'utente step-by-step
 * con Avanti/Indietro/Conferma senza aprire 100 finestre diverse.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see gui.Home
 * @see gui.DashboardUtente
 * @see gui.DashboardGiudice
 * @see gui.DashboardOrganizzatore
 */
public class Controller {

    /**
     * Riferimenti principali alla UI e stato dei wizard.
     * <p>
     * - frame / frame2: finestre Swing (login e dashboard).<br>
     * - loginFrame + radioButton: componenti del login.<br>
     * - dashboardX: schermate principali per ogni ruolo.<br>
     * - passoX + datiX: stato dei wizard (step corrente + input raccolti).
     *
     * @see javax.swing.JFrame
     * @see java.util.ArrayList
     */


    private static JFrame frame;
    private static JFrame frame2;
    private Home loginFrame;
    private ButtonGroup gruppoRuoli;
    private JRadioButton utenteRadioButton;
    private JRadioButton organizzatoreRadioButton;
    private JRadioButton giudiceRadioButton;
    private DashboardUtente dashboardUtente;
    private DashboardGiudice dashboardGiudice;
    private DashboardOrganizzatore dashboardOrganizzatore;
    private int passoCreazione;
    private ArrayList<String> datiHackaton;
    private ArrayList<Hackathon> listaHackathon = new ArrayList<>();
    private int passoInvitoGiudice;
    private ArrayList<String> datiInvitoGiudice;
    private int passoIscrizioneU = -1;
    private ArrayList<String> datiIscrizioneU = new ArrayList<>();
    private int passoTeamU = -1;
    private ArrayList<String> datiTeamU = new ArrayList<>();
    private int passoDocumentoU = -1;
    private ArrayList<String> datiDocumentoU = new ArrayList<>();
    private int passoInvitoG = -1;
    private java.util.List<String> datiInvitoG = new java.util.ArrayList<>();
    private int passoProblemaG = -1;
    private ArrayList<String> datiProblemaG = new ArrayList<>();
    private int passoTeamG = -1;
    private ArrayList<String> datiTeamG = new ArrayList<>();
    private int passoClassificaG = -1;
    private ArrayList<String> datiClassificaG = new ArrayList<>();
    private int passoInvitoTeamU = -1;
    private ArrayList<String> datiInvitoTeamU = new ArrayList<>();
    private int passoInvitiRicevutiU = -1;
    private ArrayList<String> datiInvitiRicevutiU = new ArrayList<>();
    private Hackathon hackathonSelezionato;


    /**
     * Costruisce il Controller e apre la schermata di login.
     * <p>
     * Qui preparo il frame, carico la GUI di login e poi chiamo {@link #aggiungiListeners()}
     * per attivare i bottoni (altrimenti la finestra si vede ma non “fa nulla”).
     *
     * @see #aggiungiListeners() #aggiungiListeners()
     */
    public Controller () {
    frame = new JFrame("Hackaton");
    loginFrame = new Home();
    frame.setContentPane(loginFrame.getPanel1());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    aggiungiListeners();
    loginFrame.getPanel1().setFocusable(true);
    frame.requestFocusInWindow();
    frame.setResizable(false);

    gruppoRuoli = new ButtonGroup();
    utenteRadioButton = loginFrame.getUtenteRadioButton();
    organizzatoreRadioButton = loginFrame.getOrganizzatoreRadioButton();
    giudiceRadioButton = loginFrame.getGiudiceRadioButton();

    gruppoRuoli.add(utenteRadioButton);
    gruppoRuoli.add(organizzatoreRadioButton);
    gruppoRuoli.add(giudiceRadioButton);

    }


    /**
     * Collego tutti i listener della schermata di login.
     * <p>
     * - Focus sul campo email (placeholder e pulizia messaggi).<br>
     * - Click sul bottone login: valida email/ruolo, registra se non esiste e apre la dashboard corretta.
     *
     * @see #gestisciDashboardUtente(model.Utente) #gestisciDashboardUtente(model.Utente)
     * @see #gestisciDashboardGiudice(model.Giudice) #gestisciDashboardGiudice(model.Giudice)
     * @see #gestisciDashboardOrganizzatore(model.Organizzatore) #gestisciDashboardOrganizzatore(model.Organizzatore)
     */
    public void aggiungiListeners() {
       JTextField campoEmail = loginFrame.getEmailTextField();
       JPanel loginPanel = loginFrame.getPanel1();
       JButton loginButton = loginFrame.getLoginBtn();
       JLabel messaggioErrore = loginFrame.getMessaggioErrore();


       campoEmail.addFocusListener(new FocusListener() {

           @Override
           public void focusGained(FocusEvent e) {
               messaggioErrore.setText("");
               if (campoEmail.getText().equals("Email")) {
                   campoEmail.setText("");
                   campoEmail.setForeground(new java.awt.Color(255, 255, 255));
               }
           }
           @Override
            public void focusLost(FocusEvent e) {
                if (campoEmail.getText().isEmpty()) {
                    campoEmail.setText("Email");
                    campoEmail.setForeground(new java.awt.Color(255, 255, 255));
                }
            }
       });

       // meccanismo di login

        loginButton.addActionListener(e -> {
            String email = campoEmail.getText().trim();
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

            if (gruppoRuoli.getSelection() == null) {
                messaggioErrore.setText("Seleziona un ruolo");
                return;
            }

            if (email.equals("Email") || email.isEmpty()) {
                messaggioErrore.setText("Inserisci un'email valida");
                return;
            }

            if (!email.matches(emailRegex)) {
                messaggioErrore.setText("Formato email non valido.");
                return;
            }

            String ruolo = null;
            if (utenteRadioButton.isSelected()) ruolo = "utente";
            else if (organizzatoreRadioButton.isSelected()) ruolo = "organizzatore";
            else if (giudiceRadioButton.isSelected()) ruolo = "giudice";

            dao.UtenteDAO dao = new daoImpl.UtenteDAOImpl();
            if (!dao.esisteUtente(email, ruolo)) {
                dao.registraUtente(email, ruolo);
                System.out.println("Utente registrato nel DB.");
            } else {
                System.out.println("Utente già esistente.");
            }

            frame.dispose();

            if (ruolo.equals("utente")) {
                Utente nuovoUtente = new Utente(email);
                dashboardUtente = new DashboardUtente(nuovoUtente.getMail());
                gestisciDashboardUtente(nuovoUtente);
            }

            if (ruolo.equals("organizzatore")) {
                Organizzatore nuovoUtente = new Organizzatore(email);
                dashboardOrganizzatore = new DashboardOrganizzatore(nuovoUtente.getMail());
                gestisciDashboardOrganizzatore(nuovoUtente);
            }

            if (ruolo.equals("giudice")) {
                Giudice nuovoUtente = new Giudice(email);
                dashboardGiudice = new DashboardGiudice(nuovoUtente.getMail());
                gestisciDashboardGiudice(nuovoUtente);
            }
        });


        loginPanel.addMouseListener(new MouseAdapter(){

           @Override
           public void mousePressed(MouseEvent e){
               loginPanel.requestFocusInWindow();

           }


        });

        }

    /**
     * Gestisce la dashboard dell'utente (listener + wizard della parte utente).
     * <p>
     * Qui imposto la finestra dell'utente e collego i pulsanti: hackathon disponibili,
     * i miei team, inviti team, documenti, classifica, ecc.
     *
     * @param utente l'utente attualmente loggato (usato per email e permessi)
     * @see gui.DashboardUtente
     */


    private void gestisciDashboardUtente(Utente utente){

        // ===== Setup iniziale Dashboard Utente =====
// Creo e configuro la finestra (frame2) con la dashboard dell'utente.
// Qui imposto content pane, chiusura, dimensioni, posizione e blocco resize.
// Poi aggiorno il messaggio di benvenuto usando l'email dell'utente loggato.


        JLabel messaggioBenvenuto = dashboardUtente.getMessaggioBenvenuto();
            frame2 = new JFrame("HackatonDashboard - Utente" );
            frame2.setContentPane(dashboardUtente.getDashboardUtente());
            frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame2.pack();
            frame2.setLocationRelativeTo(null);
            frame2.setVisible(true);
            frame2.setResizable(false);

            messaggioBenvenuto.setText("Utente, " + utente.getMail() + " ");



            // visualizzazione hackaton disponibili + iscrizione
            dashboardUtente.getHackatonDisponibili().addActionListener(e -> {
                // Mostra pannello elenco
                dashboardUtente.getPannelloLogico().setVisible(true);
                dashboardUtente.getScrollPaneVisualizza().setVisible(true);

                // Wizard iscrizione: step iniziale
                passoIscrizioneU = 0;
                datiIscrizioneU.clear();
                passoTeamU = -1;
                datiTeamU.clear();
                dashboardUtente.getAreaDiTesto().setVisible(true);
                dashboardUtente.getFieldScrittura().setVisible(true);
                dashboardUtente.getAvantiButton().setVisible(true);
                dashboardUtente.getIndietroButton().setVisible(true);
                dashboardUtente.getIscrivitiButton().setVisible(false);
                dashboardUtente.getIscrivitiButton().setText("Iscriviti");
                dashboardUtente.getMessaggioErroreOrg().setVisible(true);
                aggiornaGuidaDashboardUtente();
                dashboardUtente.getAreaDiTesto().setText("<html><b>Iscrizione</b><br><br><b>Titolo:</b> —</html>");aggiornaGuidaDashboardUtente();
                dashboardUtente.getFieldScrittura().setText("");
                dashboardUtente.getFieldScrittura().requestFocusInWindow();

                // Configura textArea
                JTextArea textArea = dashboardUtente.getTextAreaVisualizza();
                JScrollPane scrollPane = dashboardUtente.getScrollPaneVisualizza();
                Color bg = dashboardUtente.getPannelloLogico().getBackground();
                textArea.setEditable(false);
                textArea.setFocusable(false);
                textArea.setBackground(bg);
                textArea.setForeground(Color.WHITE);
                textArea.setLineWrap(false);
                textArea.setWrapStyleWord(false);
                scrollPane.getViewport().setBackground(bg);
                scrollPane.setBorder(null);


                textArea.setText(""); // pulisco
                textArea.append("--- Hackathon Disponibili (iscrizioni aperte oggi) ---\n\n");

                // Carica e filtra con LocalDate
                HackathonDAO dao = new HackathonDAOImpl();
                java.util.List<model.Hackathon> tutti = dao.findAll();

                LocalDate oggi = today();
                boolean trovato = false;

                for (model.Hackathon h : tutti) {
                    LocalDate inizio = parseLocalDateFlex(h.getInizioIscrizioni());
                    LocalDate fine   = parseLocalDateFlex(h.getFineIscrizioni());
                    if (inizio == null || fine == null) {
                        // se vuoi debug, decommenta:
                        // textArea.append("⚠ Date non valide per \""+h.getTitolo()+"\" ("+h.getInizioIscrizioni()+" → "+h.getFineIscrizioni()+")\n");
                        continue;
                    }
                    boolean apertoOggi = (!oggi.isBefore(inizio) && !oggi.isAfter(fine));
                    if (apertoOggi) {
                        trovato = true;
                        textArea.append("Titolo: " + h.getTitolo() + "\n");
                        textArea.append("Organizzatore: " + h.getOrganizzatore() + "\n");


                        LocalDate inizioHack = parseLocalDateFlex(h.getInizio());
                        if (inizioHack != null) {
                            textArea.append("Inizio hackathon: " + formatDMY(inizioHack) + "\n");
                        } else if (h.getInizio() != null) {
                            // fallback se il parse fallisce ma la stringa c'è
                            textArea.append("Inizio hackathon: " + h.getInizio() + "\n");
                        }
                        textArea.append("Iscrizioni: " + formatDMY(inizio) + " → " + formatDMY(fine) + "\n");
                        textArea.append("-------------------------------------\n");
                    }
                }

                if (!trovato) {

                    textArea.append("Al momento non ci sono hackathon con iscrizioni aperte.\n");
                }

                // refresh layout
                scrollPane.revalidate();
                scrollPane.repaint();
            });

            // Focus sul campo input utente: resetta errori e rimette la guida giusta
            JTextField inputFieldU = dashboardUtente.getFieldScrittura();
            inputFieldU.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    JLabel labelInput = dashboardUtente.getMessaggioErroreOrg();

                    // Se era in errore , lo resetto alla guida normale per lo step corrente
                    if (labelInput.getForeground().equals(new Color(180, 26, 0))) {
                        aggiornaGuidaDashboardUtente();
                    } else {
                        // altrimenti lascio il testo com'è,  colore bianco
                        labelInput.setForeground(Color.WHITE);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    // non facciamo niente quando esce dal campo
                }
            });



            // ===== I miei Hackaton + Creazione Team =====
            dashboardUtente.getIMieiHackaton().addActionListener(e -> {
                // 1) Nessuna sovrapposizione con l’iscrizione:
                passoIscrizioneU = -1;
                datiIscrizioneU.clear();

                // attivo il wizard team
                passoTeamU = 0;
                datiTeamU.clear();

                // 2) attivo pannello logico e scrollPane
                dashboardUtente.getPannelloLogico().setVisible(true);
                dashboardUtente.getScrollPaneVisualizza().setVisible(true);
                dashboardUtente.getAreaDiTesto().setVisible(true);
                dashboardUtente.getFieldScrittura().setVisible(true);
                dashboardUtente.getAvantiButton().setVisible(true);
                dashboardUtente.getIndietroButton().setVisible(true);
                dashboardUtente.getIscrivitiButton().setVisible(false);
                dashboardUtente.getIscrivitiButton().setText("Crea Team");

                JTextArea textArea = dashboardUtente.getTextAreaVisualizza();
                JScrollPane scrollPane = dashboardUtente.getScrollPaneVisualizza();
                Color bg = dashboardUtente.getPannelloLogico().getBackground();

                textArea.setEditable(false);
                textArea.setFocusable(false);
                textArea.setBackground(bg);
                textArea.setForeground(Color.WHITE);
                textArea.setLineWrap(false);
                textArea.setWrapStyleWord(false);
                scrollPane.getViewport().setBackground(bg);
                scrollPane.setBorder(null);

                // 3) Carica SOLO gli hackathon a cui l'utente è iscritto
                textArea.setText(""); // pulisci
                textArea.append("--- I miei Hackathon ---\n\n");

                HackathonDAO dao = new HackathonDAOImpl();
                java.util.List<model.Hackathon> miei = dao.findByUtenteIscritto(utente.getMail());

                if (miei.isEmpty()) {
                    textArea.append("Non sei iscritto ad alcun hackathon.\n");
                    // blocco il wizard team
                    passoTeamU = -1;
                    JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                    guida.setForeground(new Color(180,26,0));
                    guida.setText("Iscriviti prima ad un hackathon per creare un team.");
                    dashboardUtente.getFieldScrittura().setVisible(false);
                    dashboardUtente.getAvantiButton().setVisible(false);
                    dashboardUtente.getIndietroButton().setVisible(false);
                    return;
                }

                for (model.Hackathon h : miei) {
                    textArea.append(
                            "Titolo: " + h.getTitolo() + "\n" +
                                    "Sede: " + h.getSede() + "\n" +
                                    "Organizzatore: " + h.getOrganizzatore() + "\n" +
                                    "Inizio: " + h.getInizio() + "\n" +
                                    "Iscrizioni: " + h.getInizioIscrizioni() + " → " + h.getFineIscrizioni() + "\n\n"
                    );
                }

                // 4) reset indicatori wizard
                aggiornaGuidaDashboardUtente();
                dashboardUtente.getAreaDiTesto().setText(
                        "<html><b>Creazione Team</b><br><br><b>Titolo hackathon:</b> —<br><b>Nome team:</b> —</html>"
                );
                dashboardUtente.getFieldScrittura().setText("");
                dashboardUtente.getFieldScrittura().requestFocusInWindow();
            });


            // ===== I miei Team =====
            dashboardUtente.getIMieiTeam().addActionListener(e -> {
                passoIscrizioneU = -1;
                datiIscrizioneU.clear();
                passoTeamU = -1;
                datiTeamU.clear();

                passoDocumentoU = 0;
                datiDocumentoU.clear();


                dashboardUtente.getPannelloLogico().setVisible(true);
                dashboardUtente.getScrollPaneVisualizza().setVisible(true);
                dashboardUtente.getAreaDiTesto().setVisible(true);
                dashboardUtente.getTextAreaVisualizza().setVisible(true);


                dashboardUtente.getFieldScrittura().setVisible(true);
                dashboardUtente.getAvantiButton().setVisible(true);
                dashboardUtente.getIndietroButton().setVisible(true);
                dashboardUtente.getIscrivitiButton().setVisible(false);
                dashboardUtente.getIscrivitiButton().setText("Carica documento");


                JTextArea textArea = dashboardUtente.getTextAreaVisualizza();
                JScrollPane scrollPane = dashboardUtente.getScrollPaneVisualizza();
                Color bg = dashboardUtente.getPannelloLogico().getBackground();

                textArea.setEditable(false);
                textArea.setFocusable(false);
                textArea.setBackground(bg);
                textArea.setForeground(Color.WHITE);
                textArea.setLineWrap(false);
                textArea.setWrapStyleWord(false);
                scrollPane.getViewport().setBackground(bg);
                scrollPane.setBorder(null);

                textArea.setText("");
                textArea.append("--- I miei Team ---\n\n");

                // Setup parte procedurale sopra
                dashboardUtente.getAreaDiTesto().setText(
                        "<html><b>Caricamento documento</b><br><br>" +
                                "<b>Hackathon:</b> —<br>" +
                                "<b>Team:</b> —<br>" +
                                "<b>Contenuto:</b> —</html>"
                );

                dashboardUtente.getFieldScrittura().setText("");
                aggiornaGuidaDashboardUtente();
                dashboardUtente.getFieldScrittura().requestFocusInWindow();


                dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                java.util.List<model.TeamInfo> mieiTeam = tdao.findTeamsByUtente(utente.getMail());

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();

                if (mieiTeam.isEmpty()) {
                    textArea.append("Non fai parte di alcun team.\n");

                    // disattivo il wizard documento
                    passoDocumentoU = -1;
                    datiDocumentoU.clear();


                    dashboardUtente.getFieldScrittura().setVisible(false);
                    dashboardUtente.getAvantiButton().setVisible(false);
                    dashboardUtente.getIndietroButton().setVisible(false);
                    dashboardUtente.getIscrivitiButton().setVisible(false);


                    guida.setForeground(Color.WHITE);
                    guida.setText("");


                    dashboardUtente.getAreaDiTesto().setText("<html><b>I miei team</b></html>");
                    return;
                }

                // preparo DAO extra fuori dal ciclo (sopra al for)
                dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                dao.DocumentoDAO ddao = new daoImpl.DocumentoDAOImpl();
                dao.ProblemaDAO pdao = new daoImpl.ProblemaDAOImpl();
                dao.CommentoDAO cdao = new daoImpl.CommentoDAOImpl();
                java.util.List<model.Hackathon> tuttiHackathon = hdao.findAll();

                for (model.TeamInfo info : mieiTeam) {

                    String titoloHackathon = info.getTitoloHackathon();
                    String nomeTeam = info.getNomeTeam();

                    // --- Data di inizio hackathon ---
                    String dataInizioStr = "—";
                    Long hackathonId = null;
                    if (titoloHackathon != null) {
                        String titoloNorm = titoloHackathon.trim();

                        model.Hackathon trovato = null;
                        for (model.Hackathon h : tuttiHackathon) {
                            String t = (h.getTitolo() == null) ? "" : h.getTitolo().trim();
                            if (t.equalsIgnoreCase(titoloNorm)) {
                                trovato = h;
                                break;
                            }
                        }

                        if (trovato != null) {
                            LocalDate inizio = parseLocalDateFlex(trovato.getInizio());
                            if (inizio != null) {
                                dataInizioStr = formatDMY(inizio);
                            } else {
                                dataInizioStr = trovato.getInizio(); // fallback stringa grezza
                            }

                            hackathonId = hdao.findIdByTitolo(trovato.getTitolo());
                        }
                    }

                    textArea.append("Hackathon: " + titoloHackathon + "\n");
                    textArea.append("Inizio: " + dataInizioStr + "\n");
                    textArea.append("Team: " + nomeTeam);
                    if (info.isCreatore(utente.getMail())) {
                        textArea.append(" (creatore)");
                    }
                    textArea.append("\n");

                    // --- Membri ---
                    textArea.append("Membri (" + info.getNumeroMembri() + "): ");

                    java.util.List<String> membri = tdao.findMembriTeam(info.getId());
                    if (membri.isEmpty()) {
                        textArea.append("nessuno\n");
                    } else {
                        for (int i = 0; i < membri.size(); i++) {
                            textArea.append(membri.get(i));
                            if (i < membri.size() - 1) {
                                textArea.append(", ");
                            }
                        }
                        textArea.append("\n");
                    }

                    // --- Ultimo documento caricato (se c'è) ---
                    if (hackathonId != null) {
                        String contenuto = ddao.trovaUltimoDocumento(info.getId(), hackathonId);

                        if (contenuto == null || contenuto.trim().isEmpty()) {
                            textArea.append("Ultimo documento: nessun documento caricato.\n");
                        } else {
                            String anteprima = anteprimaDocumento(contenuto);
                            textArea.append("Ultimo documento:\n");
                            textArea.append(anteprima + "\n");
                        }
                    } else {
                        textArea.append("Ultimo documento: dati hackathon non disponibili.\n");
                    }

                    // --- Problema e commenti dei giudici ---
                    if (hackathonId != null) {
                        // PROBLEMA
                        // PROBLEMI (stampa numerata)
                        java.util.List<model.Problema> problemi = pdao.trovaTuttiPerHackathon(hackathonId);

                        if (problemi.isEmpty()) {
                            textArea.append("Problemi: nessun problema pubblicato.\n");
                        } else {
                            int i = 1;
                            for (model.Problema p : problemi) {
                                String descr = (p.getDescrizione() == null) ? "" : p.getDescrizione().replace("\n", " ").trim();
                                if (descr.length() > 150) descr = descr.substring(0, 150) + "...";

                                String emailGiudice = (p.getGiudice() != null && p.getGiudice().getMail() != null)
                                        ? p.getGiudice().getMail()
                                        : "?";

                                textArea.append("Problema " + i + ") \"" + descr + "\" giudice: " + emailGiudice + "\n");
                                i++;
                            }
                        }


                        // COMMENTI
                        java.util.List<model.CommentoInfo> commenti =
                                cdao.findCommentiPerTeam(hackathonId, info.getId());

                        if (commenti.isEmpty()) {
                            textArea.append("Commenti: nessun commento.\n");
                        } else {
                            textArea.append("Commenti:\n");
                            for (model.CommentoInfo c : commenti) {
                                String testo = c.getContenuto();
                                if (testo != null && testo.length() > 150) {
                                    testo = testo.substring(0, 150) + "...";
                                }
                                textArea.append("Giudice " + c.getGiudiceEmail()
                                        + ": \"" + testo + "\"\n");
                            }
                        }
                    }

                    textArea.append("\n");


                    textArea.append("\n"); // separatore tra team
                }


                aggiornaGuidaDashboardUtente();
            });

            // ===== Invia invito Team =====
            dashboardUtente.getInviaInvitoButton().addActionListener(e -> {

                // reset altri wizard (per non sovrapporre bottoni/step)
                passoIscrizioneU = -1; datiIscrizioneU.clear();
                passoTeamU = -1; datiTeamU.clear();
                passoDocumentoU = -1; datiDocumentoU.clear();

                // attivo wizard invito team
                passoInvitoTeamU = 0;
                datiInvitoTeamU.clear();

                dashboardUtente.getPannelloLogico().setVisible(true);
                dashboardUtente.getScrollPaneVisualizza().setVisible(true);
                dashboardUtente.getAreaDiTesto().setVisible(true);
                dashboardUtente.getTextAreaVisualizza().setVisible(true);

                dashboardUtente.getFieldScrittura().setVisible(true);
                dashboardUtente.getAvantiButton().setVisible(true);
                dashboardUtente.getIndietroButton().setVisible(true);

                dashboardUtente.getIscrivitiButton().setVisible(false);
                dashboardUtente.getIscrivitiButton().setText("Invita");

                dashboardUtente.getAreaDiTesto().setText(
                        "<html><b>Invia invito Team</b><br><br><b>Team:</b> —<br><b>Email:</b> —</html>"
                );

                aggiornaGuidaDashboardUtente();

                JTextArea textArea = dashboardUtente.getTextAreaVisualizza();
                JScrollPane scrollPane = dashboardUtente.getScrollPaneVisualizza();
                Color bg = dashboardUtente.getPannelloLogico().getBackground();

                textArea.setEditable(false);
                textArea.setFocusable(false);
                textArea.setBackground(bg);
                textArea.setForeground(Color.WHITE);
                textArea.setLineWrap(false);
                textArea.setWrapStyleWord(false);
                scrollPane.getViewport().setBackground(bg);
                scrollPane.setBorder(null);

                textArea.setText("");
                textArea.append("--- I miei Team (Inviti) ---\n\n");

                dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                java.util.List<model.TeamInfo> mieiTeam = tdao.findTeamsByUtente(utente.getMail());

                if (mieiTeam.isEmpty()) {
                    textArea.append("Non fai parte di alcun team.\n");
                    // niente wizard se non hai team
                    passoInvitoTeamU = -1;
                    datiInvitoTeamU.clear();
                    dashboardUtente.getFieldScrittura().setVisible(false);
                    dashboardUtente.getAvantiButton().setVisible(false);
                    dashboardUtente.getIndietroButton().setVisible(false);
                    dashboardUtente.getIscrivitiButton().setVisible(false);
                    dashboardUtente.getMessaggioErroreOrg().setText("");
                    dashboardUtente.getAreaDiTesto().setText("<html><b>Invia invito Team</b></html>");
                    return;
                }

                for (model.TeamInfo info : mieiTeam) {
                    textArea.append("Hackathon: " + info.getTitoloHackathon() + "\n");
                    textArea.append("Team: \"" + info.getNomeTeam() + "\"\n");

                    java.util.List<String> inv = tdao.findInvitiInviati(info.getId());
                    if (inv.isEmpty()) {
                        textArea.append("Inviti inviati: (nessuno)\n");
                    } else {
                        textArea.append("Inviti inviati:\n");
                        for (String mail : inv) {
                            textArea.append("  Utente: \"" + mail + "\"\n");
                        }
                    }
                    textArea.append("-------------------------------------\n");
                }

                dashboardUtente.getFieldScrittura().setText("");
                dashboardUtente.getFieldScrittura().requestFocusInWindow();
            });






            dashboardUtente.getAvantiButton().addActionListener(e -> {

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                guida.setForeground(Color.WHITE);


                // --------------------------------------------------------
// 0) WIZARD INVITO TEAM (Invia invito al mio team)
// --------------------------------------------------------
                if (passoInvitoTeamU >= 0) {

                    // STEP 0: nome team
                    if (passoInvitoTeamU == 0) {
                        String nomeTeamInput = dashboardUtente.getFieldScrittura().getText();
                        String nomeTeam = (nomeTeamInput == null) ? "" : nomeTeamInput.trim();

                        if (nomeTeam.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Il nome del team non può essere vuoto.");
                            return;
                        }

                        dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                        java.util.List<model.TeamInfo> mieiTeam = tdao.findTeamsByUtente(utente.getMail());

                        model.TeamInfo selezionato = null;
                        for (model.TeamInfo info : mieiTeam) {
                            if (info.getNomeTeam() != null && info.getNomeTeam().trim().equalsIgnoreCase(nomeTeam)) {
                                selezionato = info;
                                break;
                            }
                        }

                        if (selezionato == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Team non trovato tra i tuoi.");
                            return;
                        }

                        // ✅ Team definitivi: dopo fine iscrizioni non puoi inviare inviti
                        String titoloHack = selezionato.getTitoloHackathon();
                        LocalDate fineIscr = fineIscrizioniByTitolo(titoloHack);
                        if (fineIscr != null && today().isAfter(fineIscr)) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Iscrizioni chiuse: non puoi inviare inviti per questo hackathon.");
                            return;
                        }




                        datiInvitoTeamU.clear();
                        datiInvitoTeamU.add(String.valueOf(selezionato.getId())); // 0 teamId
                        datiInvitoTeamU.add(selezionato.getNomeTeam());          // 1 nomeTeam

                        passoInvitoTeamU = 1;

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Invia invito Team</b><br><br><b>Team:</b> " + selezionato.getNomeTeam() +
                                        "<br><b>Email:</b> —</html>"
                        );

                        aggiornaGuidaDashboardUtente();
                        dashboardUtente.getFieldScrittura().setText("");
                        dashboardUtente.getFieldScrittura().requestFocusInWindow();
                        return;
                    }

                    // STEP 1: email invitato (solo controllo, poi appare pulsante "Invita")
                    if (passoInvitoTeamU == 1) {

                        String emailInput = dashboardUtente.getFieldScrittura().getText();
                        String emailInvitato = (emailInput == null) ? "" : emailInput.trim();

                        if (emailInvitato.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Email vuota.");
                            return;
                        }

                        if (emailInvitato.equalsIgnoreCase(utente.getMail())) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Non puoi invitare te stesso.");
                            return;
                        }
                        // ✅ Team definitivi: ricavo titolo hackathon dal tuo team e controllo fine iscrizioni
                        long teamId = Long.parseLong(datiInvitoTeamU.get(0)); // 0 = teamId
                        dao.TeamDAO tdaoTmp = new daoImpl.TeamDAOImpl();
                        java.util.List<model.TeamInfo> mieiTeam = tdaoTmp.findTeamsByUtente(utente.getMail());

                        model.TeamInfo infoTeam = null;
                        for (model.TeamInfo ti : mieiTeam) {
                            if (ti.getId() == teamId) {
                                infoTeam = ti;
                                break;
                            }
                        }

                        if (infoTeam != null) {
                            LocalDate fineIscr = fineIscrizioniByTitolo(infoTeam.getTitoloHackathon());
                            if (fineIscr != null && today().isAfter(fineIscr)) {
                                guida.setForeground(new Color(180, 26, 0));
                                guida.setText("Iscrizioni chiuse: inviti bloccati per questo hackathon.");
                                return;
                            }
                        }


                        // check: deve essere un UTENTE (no giudice/organizzatore)
                        dao.UtenteDAO udao = new daoImpl.UtenteDAOImpl();
                        if (!udao.esisteUtente(emailInvitato, "utente")) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Email non associata a un utente.");
                            return;
                        }

                        // salva email
                        if (datiInvitoTeamU.size() == 2) datiInvitoTeamU.add(emailInvitato); // 2 email
                        else datiInvitoTeamU.set(2, emailInvitato);

                        passoInvitoTeamU = 2;

                        String nomeTeam = datiInvitoTeamU.get(1);
                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Invia invito Team</b><br><br><b>Team:</b> " + nomeTeam +
                                        "<br><b>Email:</b> " + emailInvitato + "</html>"
                        );

                        // mostra bottone finale "Invita"
                        dashboardUtente.getAvantiButton().setVisible(false);
                        dashboardUtente.getIscrivitiButton().setVisible(true);
                        dashboardUtente.getIscrivitiButton().setText("Invita");

                        aggiornaGuidaDashboardUtente();
                        return;
                    }

                    return; // fine ramo invito team
                }


                // --------------------------------------------------------
                if (passoInvitiRicevutiU >= 0) {

                    if (passoInvitiRicevutiU == 0) {
                        String input = dashboardUtente.getFieldScrittura().getText();
                        input = (input == null) ? "" : input.trim();

                        long invitoId;
                        try {
                            invitoId = Long.parseLong(input);
                        } catch (NumberFormatException ex) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("ID non valido (solo numeri).");
                            return;
                        }

                        // verifica che esista tra gli inviti ricevuti
                        dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                        java.util.List<model.InvitoTeamInfo> inviti = tdao.findInvitiRicevuti(utente.getMail());

                        model.InvitoTeamInfo scelto = null;
                        for (model.InvitoTeamInfo inv : inviti) {
                            if (inv.getIdInvito() == invitoId) { scelto = inv; break; }
                        }

                        if (scelto == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Invito non trovato o non più disponibile.");
                            return;
                        }

                        datiInvitiRicevutiU.clear();
                        datiInvitiRicevutiU.add(String.valueOf(invitoId));

                        passoInvitiRicevutiU = 1;

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Invito selezionato</b><br>" +
                                        "ID: <b>" + invitoId + "</b><br>" +
                                        "Hackathon: " + scelto.getTitoloHackathon() + "<br>" +
                                        "Team: \"" + scelto.getNomeTeam() + "\"</html>"
                        );

                        dashboardUtente.getAvantiButton().setVisible(false);
                        dashboardUtente.getIscrivitiButton().setVisible(true);
                        dashboardUtente.getIscrivitiButton().setText("Accetta");

                        aggiornaGuidaDashboardUtente();
                        return;
                    }

                    return;
                }




                if (passoIscrizioneU >= 0) {

                    if (passoIscrizioneU == 0) {
                        String titoloInput = dashboardUtente.getFieldScrittura().getText();
                        String titolo = (titoloInput == null) ? "" : titoloInput.trim();
                        if (titolo.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Il campo non può essere vuoto.");
                            return;
                        }

                        // trova hackathon per titolo (trim + ignore case) tra TUTTI quelli disponibili
                        dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                        java.util.List<model.Hackathon> tutti = hdao.findAll();
                        model.Hackathon target = null;
                        for (model.Hackathon h : tutti) {
                            String t = (h.getTitolo() == null) ? "" : h.getTitolo().trim();
                            if (t.equalsIgnoreCase(titolo)) {
                                target = h;
                                break;
                            }
                        }
                        if (target == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Hackathon non trovata.");
                            return;
                        }

                        // ✅ Team definitivi: dopo fine iscrizioni non puoi creare team
                        LocalDate fineIscr = parseLocalDateFlex(target.getFineIscrizioni());
                        if (fineIscr != null && today().isAfter(fineIscr)) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Iscrizioni chiuse: non puoi creare team per questo hackathon.");
                            return;
                        }


                        // verifica finestra iscrizioni con LocalDate
                        java.time.LocalDate oggi = today();
                        java.time.LocalDate inizio = parseLocalDateFlex(target.getInizioIscrizioni());
                        java.time.LocalDate fine = parseLocalDateFlex(target.getFineIscrizioni());
                        if (inizio == null || fine == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Formato date non valido in questo hackathon.");
                            return;
                        }
                        boolean apertoOggi = (!oggi.isBefore(inizio) && !oggi.isAfter(fine)); // estremi inclusi
                        if (!apertoOggi) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Iscrizioni non aperte oggi per questo hackathon.");
                            return;
                        }

                        // ID reale dal DB
                        Long hackathonId = hdao.findIdByTitolo(target.getTitolo());
                        if (hackathonId == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                            return;
                        }

                        // controlli iscrizione (già iscritto / capienza)
                        dao.IscrizioneDAO idao = new daoImpl.IscrizioneDAOImpl();
                        if (idao.isIscritto(hackathonId, utente.getMail())) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Sei già iscritto a questo hackathon.");
                            return;
                        }
                        int iscritti = idao.countIscritti(hackathonId);
                        if (iscritti >= target.getMaxPartecipanti()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Capienza raggiunta: impossibile iscriversi.");
                            return;
                        }

                        // riepilogo + mostra "Iscriviti"
                        datiIscrizioneU.clear();
                        datiIscrizioneU.add(target.getTitolo());
                        passoIscrizioneU = 1;

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Iscrizione</b><br><br>"
                                        + "<b>Titolo:</b> " + target.getTitolo() + "<br>"
                                        + "<b>Organizzatore:</b> " + target.getOrganizzatore() + "<br>"
                                        + "<b>Iscrizioni:</b> " + formatDMY(inizio) + " → " + formatDMY(fine) + "<br>"
                                        + "</html>"
                        );
                        aggiornaGuidaDashboardUtente();

                        dashboardUtente.getAvantiButton().setVisible(false);
                        dashboardUtente.getIscrivitiButton().setVisible(true);
                        dashboardUtente.getIscrivitiButton().setText("Iscriviti");
                        dashboardUtente.getFieldScrittura().setText("");
                    }

                    return; // fine ramo iscrizione
                }

                // --------------------------------------------------------
                // 2) WIZARD TEAM (I miei Hackaton → Crea Team)
                // --------------------------------------------------------
                if (passoTeamU >= 0) {

                    // STEP 0: inserimento titolo hackathon
                    if (passoTeamU == 0) {
                        String titoloInput = dashboardUtente.getFieldScrittura().getText();
                        String titolo = (titoloInput == null) ? "" : titoloInput.trim();

                        if (titolo.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Il titolo non può essere vuoto.");
                            return;
                        }

                        // cerca l'hackathon SOLO tra quelli dove l'utente è iscritto
                        dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                        java.util.List<model.Hackathon> miei = hdao.findByUtenteIscritto(utente.getMail());
                        model.Hackathon target = null;
                        for (model.Hackathon h : miei) {
                            String t = (h.getTitolo() == null) ? "" : h.getTitolo().trim();
                            if (t.equalsIgnoreCase(titolo)) {
                                target = h;
                                break;
                            }
                        }
                        if (target == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Non risulti iscritto a un hackathon con questo titolo.");
                            return;
                        }

                        Long hackathonId = hdao.findIdByTitolo(target.getTitolo());
                        if (hackathonId == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                            return;
                        }

                        // controlla che l'utente NON abbia già un team per quell'hackathon
                        dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                        if (tdao.utenteHaTeam(hackathonId, utente.getMail())) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Hai già un team per questo hackathon.");
                            return;
                        }

                        // passa allo step successivo (nome team)
                        passoTeamU = 1;
                        datiTeamU.clear();
                        datiTeamU.add(target.getTitolo()); // salvo titolo hackathon

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Creazione Team</b><br><br>"
                                        + "<b>Titolo hackathon:</b> " + target.getTitolo() + "<br>"
                                        + "<b>Nome team:</b> —</html>"
                        );
                        aggiornaGuidaDashboardUtente();
                        dashboardUtente.getFieldScrittura().setText("");
                        dashboardUtente.getFieldScrittura().requestFocusInWindow();
                        return;
                    }

                    // STEP 1: inserimento nome team
                    if (passoTeamU == 1) {
                        if (datiTeamU.isEmpty()) return; // safety

                        String nomeTeamInput = dashboardUtente.getFieldScrittura().getText();
                        String nomeTeam = (nomeTeamInput == null) ? "" : nomeTeamInput.trim();

                        if (nomeTeam.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Il nome del team non può essere vuoto.");
                            return;
                        }

                        String titoloHackathon = datiTeamU.get(0);

                        dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                        Long hackathonId = hdao.findIdByTitolo(titoloHackathon);
                        if (hackathonId == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                            return;
                        }

                        dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                        if (tdao.esisteTeamConNome(hackathonId, nomeTeam)) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("Esiste già un team con questo nome in questo hackathon.");
                            return;
                        }

                        // Riepilogo + mostra "Crea Team"
                        passoTeamU = 2;
                        if (datiTeamU.size() == 1) datiTeamU.add(nomeTeam);
                        else datiTeamU.set(1, nomeTeam);

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Creazione Team</b><br><br>"
                                        + "<b>Titolo hackathon:</b> " + titoloHackathon + "<br>"
                                        + "<b>Nome team:</b> " + nomeTeam + "</html>"
                        );
                        aggiornaGuidaDashboardUtente();
                        dashboardUtente.getAvantiButton().setVisible(false);
                        dashboardUtente.getIscrivitiButton().setVisible(true);
                        dashboardUtente.getIscrivitiButton().setText("Crea Team");
                        dashboardUtente.getFieldScrittura().setText("");
                        return;
                    }

                    return; // fine ramo team
                }

                // --------------------------------------------------------
                // 3) WIZARD DOCUMENTO (I miei Team → Carica documento)
                // --------------------------------------------------------
                if (passoDocumentoU >= 0) {

                    // STEP 0: inserimento nome team
                    if (passoDocumentoU == 0) {
                        String nomeTeamInput = dashboardUtente.getFieldScrittura().getText();
                        String nomeTeam = (nomeTeamInput == null) ? "" : nomeTeamInput.trim();

                        if (nomeTeam.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Il nome del team<br>non può essere vuoto.</html>");
                            return;
                        }

                        dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                        java.util.List<model.TeamInfo> mieiTeam = tdao.findTeamsByUtente(utente.getMail());

                        model.TeamInfo selezionato = null;
                        for (model.TeamInfo info : mieiTeam) {
                            if (info.getNomeTeam() != null &&
                                    info.getNomeTeam().trim().equalsIgnoreCase(nomeTeam)) {
                                selezionato = info;
                                break; // assumiamo un solo team con quel nome
                            }
                        }

                        if (selezionato == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Nessun team trovato<br>con questo nome.</html>");
                            return;
                        }

                        String titoloHackathon = selezionato.getTitoloHackathon();

                        // Recupero l'hackathon per verificare la data di inizio
                        dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                        java.util.List<model.Hackathon> hackathonUtente =
                                hdao.findByUtenteIscritto(utente.getMail());

                        model.Hackathon hTarget = null;
                        for (model.Hackathon h : hackathonUtente) {
                            if (h.getTitolo() != null &&
                                    h.getTitolo().trim().equalsIgnoreCase(titoloHackathon.trim())) {
                                hTarget = h;
                                break;
                            }
                        }

                        if (hTarget == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Impossibile recuperare<br>i dati dell'hackathon.</html>");
                            return;
                        }

                        // Controllo: documento solo da quando l'hackathon è iniziato
                        java.time.LocalDate oggi = today();
                        java.time.LocalDate inizioHackathon = parseLocalDateFlex(hTarget.getInizio());
                        if (inizioHackathon == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Data di inizio hackathon<br>non valida.</html>");
                            return;
                        }

                        if (oggi.isBefore(inizioHackathon)) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Puoi caricare documenti<br>solo da inizio hackathon.</html>");
                            return;
                        }

                        // Recupero ID reali dal DB
                        Long hackathonId = hdao.findIdByTitolo(titoloHackathon);
                        if (hackathonId == null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Impossibile recuperare<br>l'ID dell'hackathon.</html>");
                            return;
                        }
                        long teamId = selezionato.getId();

                        // Salvo dati del wizard
                        datiDocumentoU.clear();
                        datiDocumentoU.add(nomeTeam);                     // 0
                        datiDocumentoU.add(titoloHackathon);              // 1
                        datiDocumentoU.add(String.valueOf(teamId));       // 2
                        datiDocumentoU.add(String.valueOf(hackathonId));  // 3

                        passoDocumentoU = 1;

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Caricamento documento</b><br><br>" +
                                        "<b>Hackathon:</b> " + titoloHackathon + "<br>" +
                                        "<b>Team:</b> " + nomeTeam + "<br>" +
                                        "<b>Contenuto:</b> —</html>"
                        );
                        aggiornaGuidaDashboardUtente();
                        dashboardUtente.getFieldScrittura().setText("");
                        dashboardUtente.getFieldScrittura().requestFocusInWindow();
                        return;
                    }

                    // STEP 1: inserimento contenuto documento
                    if (passoDocumentoU == 1) {
                        if (datiDocumentoU.size() < 4) return;

                        String contenutoInput = dashboardUtente.getFieldScrittura().getText();
                        String contenuto = (contenutoInput == null) ? "" : contenutoInput.trim();

                        if (contenuto.isEmpty()) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Il contenuto del documento<br>non può essere vuoto.</html>");
                            return;
                        }

                        // salvo contenuto in posizione 4
                        if (datiDocumentoU.size() == 4) datiDocumentoU.add(contenuto);
                        else datiDocumentoU.set(4, contenuto);

                        passoDocumentoU = 2;

                        String nomeTeam = datiDocumentoU.get(0);
                        String titoloHackathon = datiDocumentoU.get(1);


                        String contenutoPreview = limitaPerAreaDiTesto(contenuto, 120);

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Caricamento documento</b><br><br>" +
                                        "<b>Hackathon:</b> " + titoloHackathon + "<br>" +
                                        "<b>Team:</b> " + nomeTeam + "<br>" +
                                        "<b>Contenuto:</b> " + contenutoPreview + "</html>"
                        );


                        dashboardUtente.getAvantiButton().setVisible(false);
                        dashboardUtente.getIscrivitiButton().setVisible(true);
                        dashboardUtente.getIscrivitiButton().setText("Carica documento");
                        dashboardUtente.getFieldScrittura().setText("");
                        aggiornaGuidaDashboardUtente();
                        return;
                    }

                    return; // fine ramo documento
                }

                // nessun wizard attivo → non fare nulla
            });




            dashboardUtente.getIndietroButton().addActionListener(e -> {

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                guida.setForeground(Color.WHITE);
                aggiornaGuidaDashboardUtente();
                if (passoInvitiRicevutiU >= 0) {

                    // se ero nella conferma, torno a inserire ID
                    if (passoInvitiRicevutiU == 1) {
                        passoInvitiRicevutiU = 0;
                        datiInvitiRicevutiU.clear();

                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getAreaDiTesto().setText("<html><b>Inviti Team ricevuti</b></html>");
                        dashboardUtente.getFieldScrittura().setText("");

                        aggiornaGuidaDashboardUtente();
                        return;
                    }

                    // se ero già al passo 0, ricarico la lista
                    dashboardUtente.getVisualizzaInviti().doClick();
                    return;
                }

// --------------------------------------------------------
                // 0) wizard INVITO TEAM
                if (passoInvitoTeamU >= 0) {

                    // da step 2 (invita) torno a step 1 (email)
                    if (passoInvitoTeamU == 2) {
                        passoInvitoTeamU = 1;

                        if (datiInvitoTeamU.size() >= 3) datiInvitoTeamU.remove(2);

                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);

                        String nomeTeam = (datiInvitoTeamU.size() > 1) ? datiInvitoTeamU.get(1) : "—";
                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Invia invito Team</b><br><br><b>Team:</b> " + nomeTeam +
                                        "<br><b>Email:</b> —</html>"
                        );

                        dashboardUtente.getFieldScrittura().setText("");
                        aggiornaGuidaDashboardUtente();
                        return;
                    }

                    // da step 1 (email) torno a step 0 (nome team)
                    if (passoInvitoTeamU == 1) {
                        passoInvitoTeamU = 0;
                        datiInvitoTeamU.clear();

                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Invia invito Team</b><br><br><b>Team:</b> —<br><b>Email:</b> —</html>"
                        );

                        dashboardUtente.getFieldScrittura().setText("");
                        aggiornaGuidaDashboardUtente();
                        return;
                    }

                    // step 0: resto allo step 0 (non esco)
                    dashboardUtente.getFieldScrittura().setText("");
                    aggiornaGuidaDashboardUtente();
                    return;
                }


                // 0) wizard DOCUMENTO
                if (passoDocumentoU >= 0) {

                    if (passoDocumentoU == 1) {
                        // torna alla scelta del team
                        passoDocumentoU = 0;
                        datiDocumentoU.clear();

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Caricamento documento</b><br><br>" +
                                        "<b>Hackathon:</b> —<br>" +
                                        "<b>Team:</b> —<br>" +
                                        "<b>Contenuto:</b> —</html>"
                        );
                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getFieldScrittura().setText("");
                        aggiornaGuidaDashboardUtente();
                        return;
                    }

                    if (passoDocumentoU == 2) {
                        // torna a inserimento contenuto
                        passoDocumentoU = 1;

                        String nomeTeam = (datiDocumentoU.size() > 0) ? datiDocumentoU.get(0) : "—";
                        String titoloHackathon = (datiDocumentoU.size() > 1) ? datiDocumentoU.get(1) : "—";
                        String contenuto = (datiDocumentoU.size() > 4) ? datiDocumentoU.get(4) : "";

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Caricamento documento</b><br><br>" +
                                        "<b>Hackathon:</b> " + titoloHackathon + "<br>" +
                                        "<b>Team:</b> " + nomeTeam + "<br>" +
                                        "<b>Contenuto:</b> —</html>"
                        );

                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getFieldScrittura().setText(contenuto);
                        aggiornaGuidaDashboardUtente();
                        return;
                    }
                }


                // 1) wizard TEAM
                if (passoTeamU >= 0) {

                    if (passoTeamU == 1) {
                        // torna alla scelta hackathon
                        passoTeamU = 0;
                        datiTeamU.clear();

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Creazione Team</b><br><br>"
                                        + "<b>Titolo hackathon:</b> —<br>"
                                        + "<b>Nome team:</b> —</html>"
                        );
                        aggiornaGuidaDashboardUtente();
                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getFieldScrittura().setText("");
                        dashboardUtente.getFieldScrittura().requestFocusInWindow();
                        return;
                    }

                    if (passoTeamU == 2) {
                        // torna all'inserimento nome team
                        passoTeamU = 1;
                        String titoloHackathon = datiTeamU.isEmpty() ? "—" : datiTeamU.get(0);

                        dashboardUtente.getAreaDiTesto().setText(
                                "<html><b>Creazione Team</b><br><br>"
                                        + "<b>Titolo hackathon:</b> " + titoloHackathon + "<br>"
                                        + "<b>Nome team:</b> —</html>"
                        );
                        aggiornaGuidaDashboardUtente();
                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getFieldScrittura().setText("");
                        dashboardUtente.getFieldScrittura().requestFocusInWindow();
                        return;
                    }
                }

                // 2) wizard ISCRIZIONE
                if (passoIscrizioneU >= 0) {
                    // torna sempre allo step 0
                    passoIscrizioneU = 0;
                    datiIscrizioneU.clear();

                    dashboardUtente.getAreaDiTesto().setText(
                            "<html><b>Iscrizione</b><br><br><b>Titolo:</b> —</html>"
                    );
                    aggiornaGuidaDashboardUtente();
                    dashboardUtente.getIscrivitiButton().setVisible(false);
                    dashboardUtente.getAvantiButton().setVisible(true);
                    dashboardUtente.getFieldScrittura().setText("");
                    dashboardUtente.getFieldScrittura().requestFocusInWindow();
                }
            });


            dashboardUtente.getIscrivitiButton().addActionListener(e -> {

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                guida.setForeground(java.awt.Color.WHITE);


                // --------------------------------------------------------
                if (passoInvitiRicevutiU == 1 && !datiInvitiRicevutiU.isEmpty()) {

                    long invitoId = Long.parseLong(datiInvitiRicevutiU.get(0));

                    dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                    boolean ok = tdao.accettaInvitoTeam(invitoId, utente.getMail());

                    if (!ok) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Non posso accettare:<br>team pieno / hai già team in quell'hackathon / invito non valido.</html>");
                        return;
                    }

                    guida.setForeground(Color.WHITE);
                    guida.setText("Invito accettato ✅");

                    // reset e ricarica lista
                    passoInvitiRicevutiU = 0;
                    datiInvitiRicevutiU.clear();

                    dashboardUtente.getIscrivitiButton().setVisible(false);
                    dashboardUtente.getAvantiButton().setVisible(true);
                    dashboardUtente.getFieldScrittura().setText("");

                    dashboardUtente.getVisualizzaInviti().doClick();
                    return;
                }



                // --------------------------------------------------------
// INVIA INVITO TEAM (bottone rinominato "Invita")
// --------------------------------------------------------
                if (passoInvitoTeamU == 2 && datiInvitoTeamU.size() >= 3) {

                    long teamId = Long.parseLong(datiInvitoTeamU.get(0));
                    String nomeTeam = datiInvitoTeamU.get(1);
                    String emailInvitato = datiInvitoTeamU.get(2);

                    // ✅ Team definitivi: controllo chiusura iscrizioni anche al click finale
                    dao.TeamDAO tdaoTmp = new daoImpl.TeamDAOImpl();
                    java.util.List<model.TeamInfo> mieiTeam = tdaoTmp.findTeamsByUtente(utente.getMail());

                    model.TeamInfo infoTeam = null;
                    for (model.TeamInfo ti : mieiTeam) {
                        if (ti.getId() == teamId) { infoTeam = ti; break; }
                    }

                    if (infoTeam != null) {
                        LocalDate fineIscr = fineIscrizioniByTitolo(infoTeam.getTitoloHackathon());
                        if (fineIscr != null && today().isAfter(fineIscr)) {
                            guida.setForeground(new java.awt.Color(180, 26, 0));
                            guida.setText("Iscrizioni chiuse: inviti bloccati per questo hackathon.");
                            return;
                        }
                    }


                    try {
                        dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                        boolean ok = tdao.inviaInvitoTeam(teamId, emailInvitato, utente.getMail());

                        if (!ok) {
                            guida.setForeground(new java.awt.Color(180, 26, 0));
                            guida.setText("<html>Invito non valido:<br>già invitato / già in team / team pieno / ha già team.</html>");
                            return;
                        }

                        guida.setForeground(Color.WHITE);
                        guida.setText("Invito inviato ✅");

                        // aggiorna output
                        JTextArea ta = dashboardUtente.getTextAreaVisualizza();
                        ta.append("Invito inviato: Team \"" + nomeTeam + "\" → Utente: \"" + emailInvitato + "\"\n");

                        // torno allo step email (stesso team) per invitare un altro
                        passoInvitoTeamU = 1;
                        datiInvitoTeamU.remove(2);

                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getFieldScrittura().setText("");
                        aggiornaGuidaDashboardUtente();
                        return;

                    } catch (Exception ex) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("Errore invio invito: " + ex.getMessage());
                        return;
                    }
                }


                // --------------------------------------------------------
// 0) Conferma CARICAMENTO DOCUMENTO
// --------------------------------------------------------
                if (passoDocumentoU == 2 && datiDocumentoU.size() >= 5) {

                    String nomeTeam = datiDocumentoU.get(0);
                    String titoloHackathon = datiDocumentoU.get(1);
                    long teamId = Long.parseLong(datiDocumentoU.get(2));
                    long hackathonId = Long.parseLong(datiDocumentoU.get(3));
                    String contenuto = datiDocumentoU.get(4);

                    try {
                        dao.DocumentoDAO ddao = new daoImpl.DocumentoDAOImpl();
                        ddao.salvaDocumento(teamId, hackathonId, contenuto, java.time.LocalDateTime.now());

                        javax.swing.JOptionPane.showMessageDialog(frame2,
                                "Documento caricato per il team \"" + nomeTeam +
                                        "\" nell'hackathon \"" + titoloHackathon + "\".",
                                "Documento caricato",
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);

                        // reset wizard documento
                        passoDocumentoU = -1;
                        datiDocumentoU.clear();
                        dashboardUtente.getFieldScrittura().setText("");
                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);

                        // ricarico "I miei Team"
                        dashboardUtente.getIMieiTeam().doClick();

                    } catch (Exception ex) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("<html>Errore durante il caricamento<br>del documento: " + ex.getMessage() + "</html>");
                    }
                    return;
                }




                // --------------------------------------------------------
                // 1) Conferma CREAZIONE TEAM
                // --------------------------------------------------------
                if (passoTeamU == 2 && datiTeamU.size() >= 2) {

                    String titoloHackathon = datiTeamU.get(0);
                    String nomeTeam = datiTeamU.get(1);

                    // ✅ Team definitivi: dopo fine iscrizioni non puoi creare team
                    dao.HackathonDAO hdaoCheck = new daoImpl.HackathonDAOImpl();
                    model.Hackathon hTmp = null;
                    for (model.Hackathon h : hdaoCheck.findAll()) {
                        if (h.getTitolo() != null && h.getTitolo().trim().equalsIgnoreCase(titoloHackathon.trim())) {
                            hTmp = h;
                            break;
                        }
                    }
                    if (hTmp != null) {
                        LocalDate fineIscr = parseLocalDateFlex(hTmp.getFineIscrizioni());
                        if (fineIscr != null && today().isAfter(fineIscr)) {
                            guida.setForeground(new java.awt.Color(180, 26, 0));
                            guida.setText("Iscrizioni chiuse: non puoi creare team.");
                            return;
                        }
                    }


                    try {
                        dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                        Long hackathonId = hdao.findIdByTitolo(titoloHackathon);
                        if (hackathonId == null) {
                            guida.setForeground(new java.awt.Color(180, 26, 0));
                            guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                            return;
                        }

                        dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();

                        // sicurezza extra
                        if (tdao.utenteHaTeam(hackathonId, utente.getMail())) {
                            guida.setForeground(new java.awt.Color(180, 26, 0));
                            guida.setText("Hai già un team per questo hackathon.");
                            return;
                        }

                        tdao.creaTeam(nomeTeam, hackathonId, utente.getMail());

                        javax.swing.JOptionPane.showMessageDialog(frame2,
                                "Team \"" + nomeTeam + "\" creato con successo per \"" + titoloHackathon + "\".",
                                "Team creato",
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);

                        // reset stato wizard team
                        passoTeamU = -1;
                        datiTeamU.clear();

                        guida.setText("Team creato.");
                        dashboardUtente.getIscrivitiButton().setVisible(false);
                        dashboardUtente.getAvantiButton().setVisible(true);
                        dashboardUtente.getFieldScrittura().setText("");
                        // Ricarico la schermata "I miei Hackaton"
                        dashboardUtente.getIMieiHackaton().doClick();


                    } catch (Exception ex) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("Errore durante la creazione del team: " + ex.getMessage());
                    }
                    return;
                }

                // --------------------------------------------------------
                // 2) Conferma ISCRIZIONE
                // --------------------------------------------------------
                if (passoIscrizioneU != 1 || datiIscrizioneU.isEmpty()) return;

                String titolo = datiIscrizioneU.get(0);

                try {
                    // 1) ritrova hackathon per sicurezza
                    dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                    java.util.List<model.Hackathon> tutti = hdao.findAll();
                    model.Hackathon target = null;
                    for (model.Hackathon h : tutti) {
                        String t = (h.getTitolo() == null) ? "" : h.getTitolo().trim();
                        if (t.equalsIgnoreCase(titolo.trim())) {
                            target = h;
                            break;
                        }
                    }
                    if (target == null) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("Hackathon non trovata.");
                        return;
                    }

                    Long hackathonId = hdao.findIdByTitolo(target.getTitolo());
                    if (hackathonId == null) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("Impossibile recuperare l'ID dell'hackathon.");
                        return;
                    }

                    dao.IscrizioneDAO idao = new daoImpl.IscrizioneDAOImpl();
                    if (idao.isIscritto(hackathonId, utente.getMail())) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("Sei già iscritto.");
                        return;
                    }
                    int iscritti = idao.countIscritti(hackathonId);
                    if (iscritti >= target.getMaxPartecipanti()) {
                        guida.setForeground(new java.awt.Color(180, 26, 0));
                        guida.setText("Capienza raggiunta.");
                        return;
                    }

                    // 4) INSERT iscrizione
                    java.time.LocalDate oggi = java.time.LocalDate.now();
                    idao.iscrivi(hackathonId, utente.getMail(), oggi);

                    javax.swing.JOptionPane.showMessageDialog(frame2,
                            "Iscrizione completata per \"" + target.getTitolo() + "\".",
                            "Iscrizione completata",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);

                    // reset wizard iscrizione
                    passoIscrizioneU = -1;
                    datiIscrizioneU.clear();
                    guida.setText("Iscrizione completata.");

                    dashboardUtente.getIscrivitiButton().setVisible(false);
                    dashboardUtente.getAvantiButton().setVisible(true);
                    dashboardUtente.getFieldScrittura().setText("");
                    dashboardUtente.getHackatonDisponibili().doClick();


                } catch (Exception ex) {
                    guida.setForeground(new java.awt.Color(180, 26, 0));
                    guida.setText("Errore durante l'iscrizione: " + ex.getMessage());
                }

            });

            // ===== Classifica =====
            dashboardUtente.getClassificaButton().addActionListener(e -> {
                // disattivo tutti i wizard
                passoIscrizioneU = -1;
                datiIscrizioneU.clear();
                passoTeamU = -1;
                datiTeamU.clear();
                passoDocumentoU = -1;
                datiDocumentoU.clear();

                // mostro solo pannello logico + scrollPane
                dashboardUtente.getPannelloLogico().setVisible(true);
                dashboardUtente.getScrollPaneVisualizza().setVisible(true);
                dashboardUtente.getTextAreaVisualizza().setVisible(true);
                dashboardUtente.getAreaDiTesto().setVisible(true);

                dashboardUtente.getFieldScrittura().setVisible(false);
                dashboardUtente.getAvantiButton().setVisible(false);
                dashboardUtente.getIndietroButton().setVisible(false);
                dashboardUtente.getIscrivitiButton().setVisible(false);

                JLabel guida = dashboardUtente.getMessaggioErroreOrg();
                guida.setForeground(Color.WHITE);
                guida.setText(""); // solo lettura, niente guida wizard

                JTextArea textArea = dashboardUtente.getTextAreaVisualizza();
                JScrollPane scrollPane = dashboardUtente.getScrollPaneVisualizza();
                Color bg = dashboardUtente.getPannelloLogico().getBackground();

                textArea.setEditable(false);
                textArea.setFocusable(false);
                textArea.setBackground(bg);
                textArea.setForeground(Color.WHITE);
                textArea.setLineWrap(false);
                textArea.setWrapStyleWord(false);
                scrollPane.getViewport().setBackground(bg);
                scrollPane.setBorder(null);

                textArea.setText("");
                textArea.append("--- Classifiche dei miei hackathon ---\n\n");

                // 1) prendo i team dell'utente
                dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                java.util.List<model.TeamInfo> mieiTeam = tdao.findTeamsByUtente(utente.getMail());



                // 2) raggruppo per titolo hackathon
                java.util.Map<String, java.util.List<String>> mieiTeamPerHackathon = new java.util.HashMap<>();
                for (model.TeamInfo info : mieiTeam) {
                    String titoloHackathon = info.getTitoloHackathon();
                    mieiTeamPerHackathon
                            .computeIfAbsent(titoloHackathon, k -> new java.util.ArrayList<>())
                            .add(info.getNomeTeam());
                }

                dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                dao.ClassificaDAO cdao = new daoImpl.ClassificaDAOImpl();
                dao.VotoDAO vdao = new daoImpl.VotoDAOImpl();


                // 3) per ogni hackathon, stampo la classifica
                // 3) prendo gli hackathon a cui l'utente è iscritto
                java.util.List<model.Hackathon> hackathonIscritti = hdao.findByUtenteIscritto(utente.getMail());

                if (hackathonIscritti.isEmpty()) {
                    textArea.append("Non sei iscritto ad alcun hackathon.\n");
                    scrollPane.revalidate();
                    scrollPane.repaint();
                    return;
                }

// 4) per ogni hackathon iscritto, stampo la classifica completa (tutti i team)
                for (model.Hackathon h : hackathonIscritti) {
                    String titoloHackathon = h.getTitolo();
                    Long hackathonId = hdao.findIdByTitolo(titoloHackathon);
                    if (hackathonId == null) continue;

                    textArea.append("Hackathon: " + titoloHackathon + "\n");

                    int inseriti = vdao.countVotiInseriti(hackathonId);
                    int attesi = vdao.countVotiAttesi(hackathonId);

                    if (!vdao.votiCompleti(hackathonId)) {
                        textArea.append("  Classifica non ancora disponibile.\n");
                        textArea.append("  Voti inseriti: " + inseriti + " / " + attesi + "\n\n");
                        continue;
                    }

                    java.util.List<model.Classifica> classifica = cdao.findClassificaByHackathon(hackathonId);

                    if (classifica.isEmpty()) {
                        textArea.append("  Nessun voto registrato.\n\n");
                        continue;
                    }

                    // nomi dei team dell'utente SOLO per mostrare "<-- il tuo team"
                    java.util.List<String> mieiNomiTeam =
                            mieiTeamPerHackathon.getOrDefault(titoloHackathon, java.util.Collections.emptyList());

                    for (model.Classifica riga : classifica) {
                        String nomeTeam = riga.getTeam().getNome();
                        int punteggio = riga.getPunteggio();

                        boolean mio = mieiNomiTeam.contains(nomeTeam);

                        textArea.append("Team: \"" + nomeTeam + "\"");
                        if (mio) textArea.append("  <-- il tuo team");
                        textArea.append("\n");

                        textArea.append("Voto totale: \"" + punteggio + "\"\n\n");
                    }
                }


                scrollPane.revalidate();
                scrollPane.repaint();
            });


            dashboardUtente.getVisualizzaInviti().addActionListener(e -> {

                // reset altri wizard
                passoIscrizioneU = -1; datiIscrizioneU.clear();
                passoTeamU = -1; datiTeamU.clear();
                passoDocumentoU = -1; datiDocumentoU.clear();
                passoInvitoTeamU = -1; datiInvitoTeamU.clear();

                // wizard inviti ricevuti
                passoInvitiRicevutiU = 0;
                datiInvitiRicevutiU.clear();

                dashboardUtente.getPannelloLogico().setVisible(true);
                dashboardUtente.getScrollPaneVisualizza().setVisible(true);
                dashboardUtente.getTextAreaVisualizza().setVisible(true);
                dashboardUtente.getAreaDiTesto().setVisible(true);

                dashboardUtente.getFieldScrittura().setVisible(true);
                dashboardUtente.getAvantiButton().setVisible(true);
                dashboardUtente.getIndietroButton().setVisible(true);

                dashboardUtente.getIscrivitiButton().setVisible(false);
                dashboardUtente.getIscrivitiButton().setText("Accetta");

                dashboardUtente.getAreaDiTesto().setText("<html><b>Inviti Team ricevuti</b></html>");

                JTextArea ta = dashboardUtente.getTextAreaVisualizza();
                JScrollPane sp = dashboardUtente.getScrollPaneVisualizza();
                Color bg = dashboardUtente.getPannelloLogico().getBackground();

                ta.setEditable(false);
                ta.setFocusable(false);
                ta.setBackground(bg);
                ta.setForeground(Color.WHITE);
                ta.setLineWrap(false);
                ta.setWrapStyleWord(false);
                sp.getViewport().setBackground(bg);
                sp.setBorder(null);

                ta.setText("");
                ta.append("--- Inviti Team ricevuti ---\n\n");

                dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                java.util.List<model.InvitoTeamInfo> inviti = tdao.findInvitiRicevuti(utente.getMail());

                if (inviti.isEmpty()) {
                    ta.append("Non hai inviti al momento.\n");
                    passoInvitiRicevutiU = -1;
                    dashboardUtente.getFieldScrittura().setVisible(false);
                    dashboardUtente.getAvantiButton().setVisible(false);
                    dashboardUtente.getIndietroButton().setVisible(false);
                    dashboardUtente.getMessaggioErroreOrg().setText("");
                    return;
                }

                for (model.InvitoTeamInfo inv : inviti) {
                    ta.append("ID: " + inv.getIdInvito() + "\n");
                    ta.append("Hackathon: " + inv.getTitoloHackathon() + "\n");
                    ta.append("Team: \"" + inv.getNomeTeam() + "\"\n");
                    ta.append("Invitante: " + inv.getInvitanteEmail() + "\n");
                    ta.append("-------------------------------------\n");
                }

                aggiornaGuidaDashboardUtente();
                dashboardUtente.getFieldScrittura().setText("");
                dashboardUtente.getFieldScrittura().requestFocusInWindow();
            });








        }

    /**
     * Gestisce la dashboard del giudice.
     * <p>
     * Qui imposto la finestra del giudice e collego i pulsanti principali:
     * hackathon assegnati, pubblicazione problemi, visualizzazione documenti, commenti e inserimento voti.
     * Tutte le operazioni lato giudice passano da questo metodo.
     *
     * @param giudice giudice attualmente loggato (identificato tramite email)
     * @see gui.DashboardGiudice
     * @see dao.ProblemaDAO
     * @see dao.CommentoDAO
     * @see dao.VotoDAO
     */


    private void gestisciDashboardGiudice(Giudice giudice){
        JLabel messaggioBenvenuto = dashboardGiudice.getMessaggioBenvenuto();
        frame2 = new JFrame("HackatonDashboard - Giudice" );
        frame2.setContentPane(dashboardGiudice.getDashboardGiudice());
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.pack();
        frame2.setLocationRelativeTo(null);
        frame2.setVisible(true);
        frame2.setResizable(false);

        JTextField inputFieldG = dashboardGiudice.getFieldScrittura();
        if (inputFieldG != null) {
            inputFieldG.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    JLabel labelInput = dashboardGiudice.getMessaggioErroreOrg();
                    if (labelInput == null) return;

                    // Se era in errore (rosso), ripristino la guida corretta per lo step corrente
                    if (labelInput.getForeground().equals(new Color(180, 26, 0))) {
                        aggiornaGuidaDashboardGiudice();
                    } else {
                        // altrimenti mi assicuro solo che il colore sia bianco
                        labelInput.setForeground(Color.WHITE);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    // qui non serve fare nulla
                }
            });
        }
        // messaggio in alto
        if (messaggioBenvenuto != null) {
            messaggioBenvenuto.setText("Giudice, " + giudice.getMail() + " ");
        }

        // ===== BOTTONE "VISUALIZZA INVITI" =====
        dashboardGiudice.getVisualizzaInvitiButton().addActionListener(e -> {

            resetWizardGiudice();


            JPanel pannelloLogico = dashboardGiudice.getPannelloLogico();
            if (pannelloLogico != null) {
                pannelloLogico.setVisible(true);
            }

            JScrollPane scroll = dashboardGiudice.getScrollPaneVisualizza();
            JTextArea textArea = dashboardGiudice.getTextAreaVisualizza();
            JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
            JLabel guida = dashboardGiudice.getMessaggioErroreOrg();

            // titolo procedurale sopra
            if (areaDiTesto != null) {
                areaDiTesto.setVisible(true);
                areaDiTesto.setText(
                        "<html><b>Accettazione invito</b><br><br>" +
                                "<b>Hackathon:</b> —</html>"
                );
            }

            // guida passo 0
            if (guida != null) {
                guida.setForeground(Color.WHITE);
                guida.setText("<html>Inserisci il titolo<br>dell'hackathon che vuoi accettare.</html>");
            }

            // campo input + bottoni wizard
            JTextField field = dashboardGiudice.getFieldScrittura();
            if (field != null) {
                field.setVisible(true);
                field.setText("");
                field.requestFocusInWindow();
            }

            JButton avanti = dashboardGiudice.getAvantiButton();
            if (avanti != null) {
                avanti.setVisible(true);
            }
            JButton indietro = dashboardGiudice.getIndietroButton();
            if (indietro != null) {
                indietro.setVisible(false); // se vuoi dopo gli dai una funzione
            }
            JButton iscriviti = dashboardGiudice.getIscrivitiButton();
            if (iscriviti != null) {
                iscriviti.setVisible(false);
            }

            // prepara textArea / scroll con la lista inviti
            if (scroll != null) scroll.setVisible(true);
            if (textArea == null) return;

            Color bg = pannelloLogico != null
                    ? pannelloLogico.getBackground()
                    : dashboardGiudice.getDashboardGiudice().getBackground();

            textArea.setVisible(true);
            textArea.setEditable(false);
            textArea.setFocusable(false);
            textArea.setBackground(bg);
            textArea.setForeground(Color.WHITE);
            textArea.setLineWrap(false);
            textArea.setWrapStyleWord(false);
            if (scroll != null) {
                scroll.getViewport().setBackground(bg);
                scroll.setBorder(null);
            }

            textArea.setText("");
            textArea.append("--- Inviti come giudice ---\n\n");

            dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
            java.util.List<model.Hackathon> inviti =
                    hdao.findInvitiPerGiudice(giudice.getMail());

            if (inviti.isEmpty()) {
                textArea.append("Non hai inviti al momento.\n");
            } else {
                for (model.Hackathon h : inviti) {
                    textArea.append("Titolo: " + h.getTitolo() + "\n");
                    textArea.append("Organizzatore: " + h.getOrganizzatore() + "\n");
                    textArea.append("Sede: " + h.getSede() + "\n");
                    textArea.append("Inizio: " + h.getInizio() + "\n");
                    textArea.append("Iscrizioni: " + h.getInizioIscrizioni()
                            + " → " + h.getFineIscrizioni() + "\n");
                    textArea.append("----------------------------------------\n");
                }
            }

            if (scroll != null) {
                scroll.revalidate();
                scroll.repaint();
            }
        });
        // ===== BOTTONE "I MIEI HACKATHON" (pubblica problema) =====
        dashboardGiudice.getIMieiHackaton().addActionListener(e -> {
            resetWizardGiudice();
            passoProblemaG = 0;




            JPanel pannelloLogico = dashboardGiudice.getPannelloLogico();
            if (pannelloLogico != null) pannelloLogico.setVisible(true);

            JScrollPane scroll = dashboardGiudice.getScrollPaneVisualizza();
            if (scroll != null) scroll.setVisible(true);

            JTextArea textArea = dashboardGiudice.getTextAreaVisualizza();
            if (textArea != null) {
                textArea.setText("");
                textArea.append("--- Hackathon assegnati (Giudice) ---\n\n");

                dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                java.util.List<model.Hackathon> assegnati = hdao.findAssegnatiPerGiudice(giudice.getMail());
                if (assegnati.isEmpty()) {
                    textArea.append("Nessun hackathon assegnato.\n");
                } else {
                    for (model.Hackathon h : assegnati) {
                        textArea.append("Titolo: " + h.getTitolo() + "\n");
                        textArea.append("Sede: " + h.getSede() + "\n");
                        textArea.append("Inizio: " + h.getInizio() + "\n");
                        textArea.append("----------------------------------------\n");
                    }
                }
            }

            JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
            if (areaDiTesto != null) {
                areaDiTesto.setVisible(true);
                areaDiTesto.setText("<html><b>Pubblica problema</b><br><br><b>Hackathon:</b> —</html>");
            }

            JLabel guida = dashboardGiudice.getMessaggioErroreOrg();
            if (guida != null) {
                guida.setForeground(Color.WHITE);
                guida.setText("<html>Inserisci il titolo<br>dell'hackathon (assegnato a te).</html>");
            }

            JTextField field = dashboardGiudice.getFieldScrittura();
            if (field != null) {
                field.setVisible(true);
                field.setText("");
            }

            JButton avanti = dashboardGiudice.getAvantiButton();
            JButton indietro = dashboardGiudice.getIndietroButton();
            JButton conferma = dashboardGiudice.getIscrivitiButton();

            if (avanti != null) avanti.setVisible(true);
            if (indietro != null) indietro.setVisible(false);
            if (conferma != null) {
                conferma.setVisible(false);
                conferma.setText("Pubblica");
            }
        });


// ===== BOTTONE "I MIEI TEAM" (commento / voto) =====
        dashboardGiudice.getIMieiTeam().addActionListener(e -> {
            resetWizardGiudice();
            passoTeamG = 0;


            JPanel pannelloLogico = dashboardGiudice.getPannelloLogico();
            if (pannelloLogico != null) pannelloLogico.setVisible(true);

            JScrollPane scroll = dashboardGiudice.getScrollPaneVisualizza();
            if (scroll != null) scroll.setVisible(true);

            JTextArea textArea = dashboardGiudice.getTextAreaVisualizza();
            if (textArea != null) {
                textArea.setText("");
                textArea.append("--- Selezione Hackathon (Giudice) ---\n\n");

                dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                java.util.List<model.Hackathon> assegnati = hdao.findAssegnatiPerGiudice(giudice.getMail());

                if (assegnati.isEmpty()) {
                    textArea.append("Nessun hackathon assegnato.\n");
                } else {
                    textArea.append("Hackathon disponibili:\n");
                    for (model.Hackathon h : assegnati) {
                        textArea.append(" - " + h.getTitolo() + "\n");
                    }
                    textArea.append("\nInserisci il titolo nel campo e premi Avanti.\n");
                }
            }

            JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
            if (areaDiTesto != null) {
                areaDiTesto.setVisible(true);
                areaDiTesto.setText("<html><b>Team</b><br><br><b>Hackathon:</b> —</html>");
            }

            aggiornaGuidaDashboardGiudice();

            JTextField field = dashboardGiudice.getFieldScrittura();
            if (field != null) {
                field.setVisible(true);
                field.setText("");
            }

            JButton avanti = dashboardGiudice.getAvantiButton();
            JButton indietro = dashboardGiudice.getIndietroButton();
            JButton conferma = dashboardGiudice.getIscrivitiButton();

            if (avanti != null) avanti.setVisible(true);
            if (indietro != null) indietro.setVisible(false);
            if (conferma != null) conferma.setVisible(false);
        });


// ===== BOTTONE "CLASSIFICA" =====
        // ===== Classifica (Giudice) =====
        dashboardGiudice.getClassificaButton().addActionListener(e -> {

            // disattivo wizard inviti (se era attivo)
            resetWizardGiudice();
            passoClassificaG = 0;



            // vista solo lettura
            dashboardGiudice.getPannelloLogico().setVisible(true);
            dashboardGiudice.getScrollPaneVisualizza().setVisible(true);
            dashboardGiudice.getTextAreaVisualizza().setVisible(true);
            dashboardGiudice.getAreaDiTesto().setVisible(false);

            dashboardGiudice.getFieldScrittura().setVisible(false);
            dashboardGiudice.getAvantiButton().setVisible(false);
            dashboardGiudice.getIndietroButton().setVisible(false);
            dashboardGiudice.getIscrivitiButton().setVisible(false);

            JLabel guida = dashboardGiudice.getMessaggioErroreOrg();
            guida.setForeground(Color.WHITE);
            guida.setText("");

            JTextArea textArea = dashboardGiudice.getTextAreaVisualizza();
            JScrollPane scrollPane = dashboardGiudice.getScrollPaneVisualizza();
            Color bg = dashboardGiudice.getPannelloLogico().getBackground();

            textArea.setEditable(false);
            textArea.setFocusable(false);
            textArea.setBackground(bg);
            textArea.setForeground(Color.WHITE);
            textArea.setLineWrap(false);
            textArea.setWrapStyleWord(false);
            scrollPane.getViewport().setBackground(bg);
            scrollPane.setBorder(null);

            dashboardGiudice.getAreaDiTesto().setText("<html><b>Classifiche</b></html>");

            textArea.setText("");
            textArea.append("--- Classifiche hackathon assegnati ---\n\n");

            dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
            dao.ClassificaDAO cdao = new daoImpl.ClassificaDAOImpl();
            dao.VotoDAO vdao = new daoImpl.VotoDAOImpl();

            java.util.List<model.Hackathon> assegnati = hdao.findAssegnatiPerGiudice(giudice.getMail());
            if (assegnati.isEmpty()) {
                textArea.append("Non hai hackathon assegnati.\n");
                scrollPane.revalidate();
                scrollPane.repaint();
                return;
            }

            for (model.Hackathon h : assegnati) {
                String titolo = h.getTitolo();
                Long hackathonId = hdao.findIdByTitolo(titolo);
                if (hackathonId == null) continue;

                textArea.append("Hackathon: " + titolo + "\n");

                int inseriti = vdao.countVotiInseriti(hackathonId);
                int attesi = vdao.countVotiAttesi(hackathonId);

                if (!vdao.votiCompleti(hackathonId)) {
                    textArea.append("  Classifica non ancora disponibile.\n");
                    textArea.append("  Voti inseriti: " + inseriti + " / " + attesi + "\n\n");
                    continue;
                }

                java.util.List<model.Classifica> classifica = cdao.findClassificaByHackathon(hackathonId);
                if (classifica.isEmpty()) {
                    textArea.append("  Nessun voto registrato.\n\n");
                    continue;
                }

                // formato come UTENTE: Team + Voto totale, ordinato DESC già nel DAO
                for (model.Classifica riga : classifica) {
                    String nomeTeam = riga.getTeam().getNome();
                    int punteggio = riga.getPunteggio();

                    textArea.append("Team: \"" + nomeTeam + "\"\n");
                    textArea.append("Voto totale: \"" + punteggio + "\"\n\n");
                }
            }

            scrollPane.revalidate();
            scrollPane.repaint();
        });



        dashboardGiudice.getAvantiButton().addActionListener(e2 -> {

            JLabel guida = dashboardGiudice.getMessaggioErroreOrg();
            if (guida != null) guida.setForeground(Color.WHITE);

            // -------------------------
            // 1) WIZARD INVITI (tuo)
            // -------------------------
            // -------------------------
// 1) WIZARD INVITI (STEP 0)
// -------------------------
            if (passoInvitoG >= 0) {

                // Avanti serve solo nello step 0 (in step 1 c'è "Partecipa")
                if (passoInvitoG != 0) return;

                JTextField field = dashboardGiudice.getFieldScrittura();
                if (field == null) return;

                String titolo = (field.getText() == null) ? "" : field.getText().trim();
                if (titolo.isEmpty()) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Il titolo non può<br>essere vuoto.</html>");
                    }
                    return;
                }

                HackathonDAO hdao = new HackathonDAOImpl();
                List<Hackathon> inviti = hdao.findInvitiPerGiudice(giudice.getMail());

                Hackathon target = null;
                for (Hackathon h : inviti) {
                    if (h.getTitolo() != null && h.getTitolo().trim().equalsIgnoreCase(titolo)) {
                        target = h;
                        break;
                    }
                }

                if (target == null) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Non risulti invitato<br>a un hackathon con questo titolo.</html>");
                    }
                    return;
                }

                // salvo titolo scelto e passo allo step 1
                datiInvitoG.clear();
                datiInvitoG.add(target.getTitolo());
                passoInvitoG = 1;

                JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
                if (areaDiTesto != null) {
                    areaDiTesto.setText("<html><b>Accettazione invito</b><br><br><b>Hackathon:</b> " + target.getTitolo() + "</html>");
                }

                if (guida != null) {
                    guida.setForeground(Color.WHITE);
                    guida.setText("<html>Clicca 'Partecipa'<br>per accettare l'invito.</html>");
                }

                JButton avanti = dashboardGiudice.getAvantiButton();
                JButton indietro = dashboardGiudice.getIndietroButton();
                JButton iscriviti = dashboardGiudice.getIscrivitiButton();

                if (avanti != null) avanti.setVisible(false);
                if (indietro != null) indietro.setVisible(true);
                if (iscriviti != null) {
                    iscriviti.setVisible(true);
                    iscriviti.setText("Partecipa");
                }

                field.setText("");
                return;
            }


            // -------------------------
            // 2) WIZARD PUBBLICA PROBLEMA
            // step 0: titolo hackathon -> avanti
            // -------------------------
            if (passoProblemaG == 0) {
                JTextField field = dashboardGiudice.getFieldScrittura();
                if (field == null) return;

                String titolo = (field.getText() == null) ? "" : field.getText().trim();
                if (titolo.isEmpty()) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Il titolo non può<br>essere vuoto.</html>");
                    }
                    return;
                }

                dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                java.util.List<model.Hackathon> assegnati = hdao.findAssegnatiPerGiudice(giudice.getMail());

                model.Hackathon target = null;
                for (model.Hackathon h : assegnati) {
                    if (h.getTitolo() != null && h.getTitolo().trim().equalsIgnoreCase(titolo)) {
                        target = h;
                        break;
                    }
                }

                if (target == null) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Hackathon non valido<br>(non assegnato a te).</html>");
                    }
                    return;
                }

                Long hackId = hdao.findIdByTitolo(target.getTitolo());
                if (hackId == null) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Errore: ID hackathon<br>non trovato.</html>");
                    }
                    return;
                }

                datiProblemaG.clear();
                datiProblemaG.add(target.getTitolo());
                datiProblemaG.add(String.valueOf(hackId));
                passoProblemaG = 1;

                JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
                if (areaDiTesto != null) {
                    areaDiTesto.setText("<html><b>Pubblica problema</b><br><br><b>Hackathon:</b> " + target.getTitolo() + "</html>");
                }

                if (guida != null) {
                    guida.setText("<html>Inserisci la descrizione<br>del problema e clicca 'Pubblica'.</html>");
                }

                JButton avanti = dashboardGiudice.getAvantiButton();
                JButton indietro = dashboardGiudice.getIndietroButton();
                JButton pubb = dashboardGiudice.getIscrivitiButton();

                if (avanti != null) avanti.setVisible(false);
                if (indietro != null) indietro.setVisible(true);
                if (pubb != null) {
                    pubb.setVisible(true);
                    pubb.setText("Pubblica");
                }

                field.setText("");
                return;
            }

            // -------------------------
            // 3) WIZARD TEAM
            // step 0: titolo hackathon
            // step 1: id team
            // step 2: azione (1 commento, 2 voto)
            // -------------------------
            if (passoTeamG == 0) {
                JTextField field = dashboardGiudice.getFieldScrittura();
                if (field == null) return;

                String titolo = (field.getText() == null) ? "" : field.getText().trim();
                if (titolo.isEmpty()) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Il titolo non può<br>essere vuoto.</html>");
                    }
                    return;
                }

                dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                java.util.List<model.Hackathon> assegnati = hdao.findAssegnatiPerGiudice(giudice.getMail());

                model.Hackathon target = null;
                for (model.Hackathon h : assegnati) {
                    if (h.getTitolo() != null && h.getTitolo().trim().equalsIgnoreCase(titolo)) {
                        target = h;
                        break;
                    }
                }

                if (target == null) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Hackathon non valido<br>(non assegnato a te).</html>");
                    }
                    return;
                }

                Long hackId = hdao.findIdByTitolo(target.getTitolo());
                if (hackId == null) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Errore: ID hackathon<br>non trovato.</html>");
                    }
                    return;
                }

                datiTeamG.clear();
                datiTeamG.add(target.getTitolo());
                datiTeamG.add(String.valueOf(hackId));
                passoTeamG = 1;

                // stampa team
                JTextArea ta = dashboardGiudice.getTextAreaVisualizza();
                if (ta != null) {
                    ta.setText("");
                    ta.append("--- Team per hackathon: " + target.getTitolo() + " ---\n\n");

                    dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                    java.util.List<model.TeamInfo> teams = tdao.findTeamsByHackathon(hackId);

                    if (teams.isEmpty()) {
                        ta.append("Nessun team registrato.\n");
                    } else {
                        dao.DocumentoDAO ddao = new daoImpl.DocumentoDAOImpl();
                        dao.CommentoDAO cdao = new daoImpl.CommentoDAOImpl();

                        for (model.TeamInfo t : teams) {
                            ta.append("ID: " + t.getId() + " | Team: " + t.getNomeTeam() + "\n");
                            ta.append("Creatore: " + t.getEmailCreatore() + " | Membri: " + t.getNumeroMembri() + "\n");

                            String ultimo = ddao.trovaUltimoDocumento(t.getId(), hackId);
                            if (ultimo == null) ultimo = "(nessun documento)";
                            ta.append("Ultimo documento: " + ultimo + "\n");

                            java.util.List<model.CommentoInfo> comm = cdao.findCommentiPerTeam(hackId, t.getId());
                            if (comm.isEmpty()) {
                                ta.append("Commenti: (nessuno)\n");
                            } else {
                                ta.append("Commenti:\n");
                                for (model.CommentoInfo ci : comm) {
                                    ta.append(" - " + ci.getGiudiceEmail() + ": " + ci.getContenuto() + "\n");
                                }
                            }
                            ta.append("----------------------------------------\n");
                        }
                    }
                }

                JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
                if (areaDiTesto != null) {
                    areaDiTesto.setText("<html><b>Team</b><br><br><b>Hackathon:</b> " + target.getTitolo() +
                            "<br><b>Step:</b> inserisci ID team</html>");
                }

                aggiornaGuidaDashboardGiudice();

                JButton indietro = dashboardGiudice.getIndietroButton();
                if (indietro != null) indietro.setVisible(true);

                field.setText("");
                return;
            }

            if (passoTeamG == 1) {
                JTextField field = dashboardGiudice.getFieldScrittura();
                if (field == null) return;

                String teamTxt = (field.getText() == null) ? "" : field.getText().trim();
                long teamId;
                try {
                    teamId = Long.parseLong(teamTxt);
                } catch (NumberFormatException ex) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Inserisci un ID team<br>numerico valido.</html>");
                    }
                    return;
                }

                long hackId = Long.parseLong(datiTeamG.get(1));

                // verifica che il team appartenga a quell'hackathon
                dao.TeamDAO tdao = new daoImpl.TeamDAOImpl();
                java.util.List<model.TeamInfo> teams = tdao.findTeamsByHackathon(hackId);
                boolean ok = false;
                for (model.TeamInfo t : teams) {
                    if (t.getId() == teamId) { ok = true; break; }
                }
                if (!ok) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Team non trovato<br>per questo hackathon.</html>");
                    }
                    return;
                }

                // salva teamId
                while (datiTeamG.size() > 2) datiTeamG.remove(datiTeamG.size() - 1);
                datiTeamG.add(String.valueOf(teamId));

                passoTeamG = 2;

                JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
                if (areaDiTesto != null) {
                    areaDiTesto.setText("<html><b>Team</b><br><br><b>Hackathon:</b> " + datiTeamG.get(0) +
                            "<br><b>Team ID:</b> " + teamId +
                            "<br><b>Step:</b> 1=commento, 2=voto</html>");
                }

                aggiornaGuidaDashboardGiudice();
                field.setText("");
                return;
            }

            if (passoTeamG == 2) {
                JTextField field = dashboardGiudice.getFieldScrittura();
                if (field == null) return;

                String scelta = (field.getText() == null) ? "" : field.getText().trim();
                if (!scelta.equals("1") && !scelta.equals("2")) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Scrivi 1 (commento)<br>o 2 (voto).</html>");
                    }
                    return;
                }

                while (datiTeamG.size() > 3) datiTeamG.remove(datiTeamG.size() - 1);
                datiTeamG.add(scelta); // azione

                passoTeamG = 3;

                JButton avanti = dashboardGiudice.getAvantiButton();
                JButton conferma = dashboardGiudice.getIscrivitiButton();
                if (avanti != null) avanti.setVisible(false);
                if (conferma != null) {
                    conferma.setVisible(true);
                    conferma.setText(scelta.equals("1") ? "Invia" : "Vota");
                }

                JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
                if (areaDiTesto != null) {
                    areaDiTesto.setText("<html><b>Team</b><br><br><b>Hackathon:</b> " + datiTeamG.get(0) +
                            "<br><b>Team ID:</b> " + datiTeamG.get(2) +
                            "<br><b>Step:</b> inserisci " + (scelta.equals("1") ? "commento" : "voto (0-10)") +
                            "</html>");
                }

                aggiornaGuidaDashboardGiudice();
                field.setText("");
                return;
            }

            // -------------------------
            // 4) CLASSIFICA
            // step 0: titolo hackathon -> mostra subito
            // -------------------------
            if (passoClassificaG == 0) {
                JTextField field = dashboardGiudice.getFieldScrittura();
                if (field == null) return;

                String titolo = (field.getText() == null) ? "" : field.getText().trim();
                if (titolo.isEmpty()) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Il titolo non può<br>essere vuoto.</html>");
                    }
                    return;
                }

                dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
                java.util.List<model.Hackathon> assegnati = hdao.findAssegnatiPerGiudice(giudice.getMail());

                model.Hackathon target = null;
                for (model.Hackathon h : assegnati) {
                    if (h.getTitolo() != null && h.getTitolo().trim().equalsIgnoreCase(titolo)) {
                        target = h;
                        break;
                    }
                }
                if (target == null) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Hackathon non valido<br>(non assegnato a te).</html>");
                    }
                    return;
                }

                Long hackId = hdao.findIdByTitolo(target.getTitolo());
                if (hackId == null) return;

                JTextArea ta = dashboardGiudice.getTextAreaVisualizza();
                if (ta != null) {
                    ta.setText("");
                    ta.append("--- Classifica: " + target.getTitolo() + " ---\n\n");
                    dao.VotoDAO vdao = new daoImpl.VotoDAOImpl();
                    int inseriti = vdao.countVotiInseriti(hackId);
                    int attesi = vdao.countVotiAttesi(hackId);

                    if (!vdao.votiCompleti(hackId)) {
                        ta.append("Classifica non ancora disponibile.\n");
                        ta.append("Voti inseriti: " + inseriti + " / " + attesi + "\n");
                        ta.append("(La piattaforma pubblica la classifica solo dopo aver acquisito tutti i voti.)\n");
                        field.setText("");
                        return;
                    }


                    dao.ClassificaDAO cdao = new daoImpl.ClassificaDAOImpl();
                    java.util.List<model.Classifica> classifica = cdao.findClassificaByHackathon(hackId);

                    int pos = 1;
                    for (model.Classifica r : classifica) {
                        ta.append(pos + ") " + r.getTeam().getNome() + "  -  " + r.getPunteggio() + "\n");
                        pos++;
                    }
                }

                JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
                if (areaDiTesto != null) {
                    areaDiTesto.setText("<html><b>Classifica</b><br><br><b>Hackathon:</b> " + target.getTitolo() + "</html>");
                }

                field.setText("");
                return;
            }
        });


        dashboardGiudice.getIscrivitiButton().addActionListener(e3 -> {

            // 1) INVITI (STEP 1) -> accetta invito
            if (passoInvitoG == 1 && !datiInvitoG.isEmpty()) {

                String titoloHackathon = datiInvitoG.get(0);

                HackathonDAO hdao = new HackathonDAOImpl();
                boolean ok = hdao.accettaInvitoGiudice(titoloHackathon, giudice.getMail());

                JLabel guida2 = dashboardGiudice.getMessaggioErroreOrg();
                if (guida2 != null) guida2.setForeground(Color.WHITE);

                if (!ok) {
                    if (guida2 != null) {
                        guida2.setForeground(new Color(180, 26, 0));
                        guida2.setText("<html>Errore nell'accettazione<br>dell'invito.</html>");
                    }
                    return;
                }

                // successo: torno allo step 0 e ricarico la lista inviti
                passoInvitoG = 0;
                datiInvitoG.clear();

                JButton avanti = dashboardGiudice.getAvantiButton();
                JButton indietro = dashboardGiudice.getIndietroButton();
                JButton iscriviti = dashboardGiudice.getIscrivitiButton();
                JTextField field = dashboardGiudice.getFieldScrittura();

                if (avanti != null) avanti.setVisible(true);
                if (indietro != null) indietro.setVisible(false);
                if (iscriviti != null) iscriviti.setVisible(false);

                if (field != null) field.setText("");

                if (guida2 != null) {
                    guida2.setForeground(Color.WHITE);
                    guida2.setText("<html>Invito accettato ✅<br>Seleziona un altro hackathon.</html>");
                }

                // refresh lista inviti (senza toccare i listener)
                JTextArea ta = dashboardGiudice.getTextAreaVisualizza();
                if (ta != null) {
                    ta.setText("--- Inviti come giudice ---\n\n");
                    List<Hackathon> inviti = hdao.findInvitiPerGiudice(giudice.getMail());
                    if (inviti.isEmpty()) {
                        ta.append("Non hai inviti al momento.\n");
                    } else {
                        for (Hackathon h : inviti) {
                            ta.append("Titolo: " + h.getTitolo() + "\n");
                            ta.append("Organizzatore: " + h.getOrganizzatore() + "\n");
                            ta.append("Sede: " + h.getSede() + "\n");
                            ta.append("Inizio: " + h.getInizio() + "\n");
                            ta.append("---------------------------------\n");
                        }
                    }
                }

                return;
            }


            // 2) PUBBLICA PROBLEMA
            if (passoProblemaG == 1 && datiProblemaG.size() >= 2) {
                JTextField field = dashboardGiudice.getFieldScrittura();
                JLabel guida = dashboardGiudice.getMessaggioErroreOrg();
                if (guida != null) guida.setForeground(Color.WHITE);

                String descr = (field.getText() == null) ? "" : field.getText().trim();
                if (descr.isEmpty()) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Descrizione vuota.</html>");
                    }
                    return;
                }

                long hackId = Long.parseLong(datiProblemaG.get(1));

                dao.ProblemaDAO pdao = new daoImpl.ProblemaDAOImpl();
                pdao.pubblicaProblema(hackId, giudice.getMail(), descr);

                JTextArea ta = dashboardGiudice.getTextAreaVisualizza();
                if (ta != null) ta.append("\n✅ Problema pubblicato.\n");

                // torna a step 0 (stesso wizard)
                passoProblemaG = 0;
                datiProblemaG.clear();

                JButton avanti = dashboardGiudice.getAvantiButton();
                JButton indietro = dashboardGiudice.getIndietroButton();
                JButton pubb = dashboardGiudice.getIscrivitiButton();
                if (avanti != null) avanti.setVisible(true);
                if (indietro != null) indietro.setVisible(false);
                if (pubb != null) pubb.setVisible(false);

                JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
                if (areaDiTesto != null) areaDiTesto.setText("<html><b>Pubblica problema</b><br><br><b>Hackathon:</b> —</html>");

                aggiornaGuidaDashboardGiudice();
                field.setText("");
                return;
            }

            // 3) TEAM (commento / voto)
            if (passoTeamG == 3 && datiTeamG.size() >= 4) {
                JTextField field = dashboardGiudice.getFieldScrittura();
                JLabel guida = dashboardGiudice.getMessaggioErroreOrg();
                if (guida != null) guida.setForeground(Color.WHITE);

                long hackId = Long.parseLong(datiTeamG.get(1));
                long teamId = Long.parseLong(datiTeamG.get(2));
                String azione = datiTeamG.get(3);

                String input = (field.getText() == null) ? "" : field.getText().trim();
                if (input.isEmpty()) {
                    if (guida != null) {
                        guida.setForeground(new Color(180, 26, 0));
                        guida.setText("<html>Campo vuoto.</html>");
                    }
                    return;
                }

                if (azione.equals("1")) {
                    dao.CommentoDAO cdao = new daoImpl.CommentoDAOImpl();
                    cdao.salvaCommento(hackId, teamId, giudice.getMail(), input);
                } else {
                    int voto;
                    try {
                        voto = Integer.parseInt(input);
                    } catch (NumberFormatException ex) {
                        if (guida != null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Voto non valido.</html>");
                        }
                        return;
                    }
                    if (voto < 0 || voto > 10) {
                        if (guida != null) {
                            guida.setForeground(new Color(180, 26, 0));
                            guida.setText("<html>Il voto deve essere 0-10.</html>");
                        }
                        return;
                    }

                    dao.VotoDAO vdao = new daoImpl.VotoDAOImpl();
                    vdao.salvaVoto(hackId, teamId, giudice.getMail(), voto);
                }

                JTextArea ta = dashboardGiudice.getTextAreaVisualizza();
                if (ta != null) ta.append("\n✅ Operazione completata.\n");

                // torna a step 1 (stesso hackathon, scegli un altro team)
                String titoloHack = datiTeamG.get(0);
                String hackIdStr = datiTeamG.get(1);

                datiTeamG.clear();
                datiTeamG.add(titoloHack);
                datiTeamG.add(hackIdStr);
                passoTeamG = 1;

                JButton avanti = dashboardGiudice.getAvantiButton();
                JButton conferma = dashboardGiudice.getIscrivitiButton();
                if (avanti != null) avanti.setVisible(true);
                if (conferma != null) conferma.setVisible(false);

                JLabel areaDiTesto = dashboardGiudice.getAreaDiTesto();
                if (areaDiTesto != null) {
                    areaDiTesto.setText("<html><b>Team</b><br><br><b>Hackathon:</b> " + titoloHack +
                            "<br><b>Step:</b> inserisci ID team</html>");
                }

                aggiornaGuidaDashboardGiudice();
                field.setText("");
                return;
            }
        });


        dashboardGiudice.getIndietroButton().addActionListener(e -> {

            // Utility riferimenti UI (con null-check)
            JTextField field = dashboardGiudice.getFieldScrittura();
            if (field != null) field.setText("");

            JLabel guida = dashboardGiudice.getMessaggioErroreOrg();
            if (guida != null) {
                guida.setForeground(Color.WHITE);
                guida.setText("");
            }

            JButton avanti = dashboardGiudice.getAvantiButton();
            JButton indietro = dashboardGiudice.getIndietroButton();
            JButton conferma = dashboardGiudice.getIscrivitiButton();

            // =====================================================
            // 1) WIZARD INVITI (Visualizza Inviti)
            // step 1 -> step 0
            // step 0 -> chiude wizard
            // =====================================================
            if (passoInvitoG >= 0) {

                if (passoInvitoG == 1) {
                    // Torno allo step 0 (reinserisci titolo hackathon)
                    passoInvitoG = 0;
                    datiInvitoG.clear();

                    if (avanti != null) avanti.setVisible(true);
                    if (conferma != null) conferma.setVisible(false);
                    if (indietro != null) indietro.setVisible(false);

                    if (dashboardGiudice.getAreaDiTesto() != null) {
                        dashboardGiudice.getAreaDiTesto()
                                .setText("<html><b>Inviti</b><br><br><b>Hackathon:</b> —</html>");
                    }

                    aggiornaGuidaDashboardGiudice();
                    return;
                }

                // passoInvitoG == 0  -> esco dal wizard inviti
                passoInvitoG = -1;
                datiInvitoG.clear();

                if (avanti != null) avanti.setVisible(false);
                if (conferma != null) conferma.setVisible(false);
                if (indietro != null) indietro.setVisible(false);
                if (field != null) field.setVisible(false);

                if (dashboardGiudice.getAreaDiTesto() != null) dashboardGiudice.getAreaDiTesto().setText("");
                if (guida != null) guida.setText("");

                return;
            }

            // =====================================================
            // 2) WIZARD PUBBLICA PROBLEMA (I miei Hackathon)
            // step 1 -> step 0
            // step 0 -> chiude wizard
            // =====================================================
            if (passoProblemaG >= 0) {

                if (passoProblemaG == 1) {
                    // Torno a step 0 (reinserisci titolo hackathon)
                    passoProblemaG = 0;
                    datiProblemaG.clear();

                    if (avanti != null) avanti.setVisible(true);
                    if (conferma != null) conferma.setVisible(false);
                    if (indietro != null) indietro.setVisible(false);

                    if (dashboardGiudice.getAreaDiTesto() != null) {
                        dashboardGiudice.getAreaDiTesto()
                                .setText("<html><b>Pubblica problema</b><br><br><b>Hackathon:</b> —</html>");
                    }

                    aggiornaGuidaDashboardGiudice();
                    return;
                }

                // passoProblemaG == 0 -> esco dal wizard problema
                passoProblemaG = -1;
                datiProblemaG.clear();

                if (avanti != null) avanti.setVisible(false);
                if (conferma != null) conferma.setVisible(false);
                if (indietro != null) indietro.setVisible(false);
                if (field != null) field.setVisible(false);

                if (dashboardGiudice.getAreaDiTesto() != null) dashboardGiudice.getAreaDiTesto().setText("");
                if (guida != null) guida.setText("");

                return;
            }

            // =====================================================
            // 3) WIZARD TEAM (I miei Team)
            // step 3 -> step 2 (scelta azione)
            // step 2 -> step 1 (reinserisci team id)
            // step 1 -> step 0 (reinserisci titolo hackathon)
            // step 0 -> chiude wizard
            // =====================================================
            if (passoTeamG >= 0) {

                // Step 3 -> Step 2
                if (passoTeamG == 3) {
                    passoTeamG = 2;

                    // Quando torno a step 2: si usa Avanti, non Conferma
                    if (avanti != null) avanti.setVisible(true);
                    if (conferma != null) conferma.setVisible(false);
                    if (indietro != null) indietro.setVisible(true);

                    // (opzionale) tolgo l'azione salvata se presente
                    if (datiTeamG.size() >= 4) {
                        while (datiTeamG.size() > 3) datiTeamG.remove(datiTeamG.size() - 1);
                    }

                    aggiornaGuidaDashboardGiudice();
                    return;
                }

                // Step 2 -> Step 1
                if (passoTeamG == 2) {
                    passoTeamG = 1;

                    if (avanti != null) avanti.setVisible(true);
                    if (conferma != null) conferma.setVisible(false);
                    if (indietro != null) indietro.setVisible(true);

                    // (opzionale) tolgo teamId/azione se avevi salvato troppo
                    if (datiTeamG.size() > 2) {
                        while (datiTeamG.size() > 2) datiTeamG.remove(datiTeamG.size() - 1);
                    }

                    aggiornaGuidaDashboardGiudice();
                    return;
                }

                // Step 1 -> Step 0
                if (passoTeamG == 1) {
                    passoTeamG = 0;

                    if (avanti != null) avanti.setVisible(true);
                    if (conferma != null) conferma.setVisible(false);
                    if (indietro != null) indietro.setVisible(false);

                    // reset dati (riparti da titolo hackathon)
                    datiTeamG.clear();

                    aggiornaGuidaDashboardGiudice();
                    return;
                }

                // Step 0 -> chiude wizard team
                passoTeamG = -1;
                datiTeamG.clear();

                if (avanti != null) avanti.setVisible(false);
                if (conferma != null) conferma.setVisible(false);
                if (indietro != null) indietro.setVisible(false);
                if (field != null) field.setVisible(false);

                if (dashboardGiudice.getAreaDiTesto() != null) dashboardGiudice.getAreaDiTesto().setText("");
                if (guida != null) guida.setText("");

                return;
            }

            // =====================================================
            // 4) WIZARD CLASSIFICA
            // step 0 -> chiude wizard
            // =====================================================
            if (passoClassificaG >= 0) {
                passoClassificaG = -1;
                datiClassificaG.clear();

                if (avanti != null) avanti.setVisible(false);
                if (conferma != null) conferma.setVisible(false);
                if (indietro != null) indietro.setVisible(false);
                if (field != null) field.setVisible(false);

                if (dashboardGiudice.getAreaDiTesto() != null) dashboardGiudice.getAreaDiTesto().setText("");
                if (guida != null) guida.setText("");
            }
        });



    }

    /**
     * Gestisce la dashboard dell'organizzatore.
     * <p>
     * Qui l'organizzatore può creare hackathon e invitare giudici.
     * Gestisco anche i wizard relativi alla creazione dell'hackathon (passoCreazione/datiHackaton)
     * e all'invito dei giudici (passoInvitoGiudice/datiInvitoGiudice).
     *
     * @param organizzatore organizzatore loggato (email usata per legare hackathon creati)
     * @see gui.DashboardOrganizzatore
     * @see dao.HackathonDAO
     * @see dao.UtenteDAO
     */


    private void gestisciDashboardOrganizzatore(Organizzatore organizzatore){
            JLabel messaggioBenvenuto2 = dashboardOrganizzatore.getMessaggioBenvenuto2();
            frame2 = new JFrame("HackatonDashboard - Organizzatore" );
            frame2.setContentPane(dashboardOrganizzatore.getDashboardOrganizzatore());
            frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame2.pack();
            frame2.setLocationRelativeTo(null);
            frame2.setVisible(true);
            frame2.setResizable(false);
            dashboardOrganizzatore.getDashboardOrganizzatore().requestFocusInWindow();

            messaggioBenvenuto2.setText("Organizzatore, " + organizzatore.getMail() + " ");
            JTextField inputField = dashboardOrganizzatore.getFieldScrittura();

            inputField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    JLabel labelInput = dashboardOrganizzatore.getMessaggioErroreOrg();
                    labelInput.setForeground(Color.WHITE);

                    // ---> INVITA GIUDICI: usa le sue domande
                    if (passoInvitoGiudice >= 0) {
                        String[] domandeInvito = {
                                "Inserisci il titolo dell'hackathon.",
                                "Inserisci l'email del giudice."
                        };
                        if (passoInvitoGiudice < domandeInvito.length) {
                            labelInput.setText(domandeInvito[passoInvitoGiudice]);
                        }
                        return;
                    }

                    // ---> CREA HACKATHON: domande originali
                    String[] domandeCrea = {
                            "Inserisci il titolo.",
                            "Inserisci la sede.",
                            "Inserisci il n° max di partecipanti.",
                            "Inserisci la grandezza max del team.",
                            "Inserisci la data di inizio (GG/MM/AAAA).",
                            "Data inizio iscrizioni (GG/MM/AAAA)."
                    };

                    if (passoCreazione < domandeCrea.length) {
                        labelInput.setText(domandeCrea[passoCreazione]);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) { /* no-op */ }
            });


            dashboardOrganizzatore.getCreaHackaton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dashboardOrganizzatore.getPannelloLogico().setVisible(true);
                    dashboardOrganizzatore.getScrollPaneVisualizza().setVisible(false);
                    dashboardOrganizzatore.getAreaDiTesto().setVisible(true);
                    dashboardOrganizzatore.getFieldScrittura().setVisible(true);
                    dashboardOrganizzatore.getIndietroButton().setVisible(true);
                    dashboardOrganizzatore.getMessaggioErroreOrg().setVisible(true);
                    passoCreazione = 0;
                    datiHackaton = new ArrayList<>();
                    gestisciPannelloLogicoDashboardOrganizzatore();
                    passoInvitoGiudice = -1;
                    datiInvitoGiudice.clear();

                }
            });


            // Listener per il pulsante "Create" nella dashboard dell'organizzatore
            dashboardOrganizzatore.getCreateButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // === Flusso INVITO GIUDICE (quando abbiamo titolo + email) ===
                    if (passoInvitoGiudice >= 0 && datiInvitoGiudice.size() == 2) {
                        String titolo = datiInvitoGiudice.get(0);
                        String emailGiudice = datiInvitoGiudice.get(1);

                        // Verifica che l'hackathon appartenga all'organizzatore (usando lista caricata dal DB)
                        Hackathon selezionato = null;
                        for (Hackathon h : listaHackathon) {
                            if (h.getTitolo().equalsIgnoreCase(titolo) &&
                                    h.getOrganizzatore().equals(organizzatore.getMail())) {
                                selezionato = h; break;
                            }
                        }
                        if (selezionato == null) {
                            JOptionPane.showMessageDialog(frame2, "Hackathon inesistente o non tua.", "Errore", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        HackathonDAO dao = new HackathonDAOImpl();
                        // Duplicati su DB
                        List<String> giaInvitati = dao.listaGiudiciInvitati(titolo, organizzatore.getMail());
                        if (giaInvitati.stream().anyMatch(s -> s.equalsIgnoreCase(emailGiudice))) {
                            JOptionPane.showMessageDialog(frame2, "Giudice già invitato a \"" + titolo + "\".", "Attenzione", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        // Inserimento invito su DB
                        dao.invitaGiudice(titolo, organizzatore.getMail(), emailGiudice);

                        // (facoltativo) anche nel model in RAM
                        Giudice nuovoGiudice = new Giudice(emailGiudice);
                        organizzatore.invitaGiudici(nuovoGiudice, selezionato);

                        JOptionPane.showMessageDialog(frame2, "Giudice " + emailGiudice + " invitato a \"" + titolo + "\"!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                        // Reset mini-wizard invito (rimani nella sezione Invita)
                        passoInvitoGiudice = 0;            // ← torna allo step del titolo
                        datiInvitoGiudice.clear();
                        dashboardOrganizzatore.getFieldScrittura().setText("");
                        dashboardOrganizzatore.getCreateButton().setVisible(false);
                        dashboardOrganizzatore.getAvantiButton().setVisible(true);
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(Color.WHITE);
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Inserisci il titolo del tuo hackathon");
                        gestisciPannelloLogicoInvitoGiudice();
                        // refresh elenco titoli nello scroll
                        HackathonDAO dao2 = new HackathonDAOImpl();
                        List<Hackathon> mieiRefresh = dao2.findByOrganizzatore(organizzatore.getMail());
                        listaHackathon.clear();
                        listaHackathon.addAll(mieiRefresh);

                        JTextArea textArea = dashboardOrganizzatore.getTextAreaVisualizza();
                        textArea.setText("");
                        if (mieiRefresh.isEmpty()) {
                            textArea.setText("Non hai ancora creato hackathon.");
                        } else {
                            textArea.append("--- I tuoi Hackathon ---\n\n");
                            for (Hackathon hh : mieiRefresh) {
                                textArea.append(hh.getTitolo() + "\n");
                            }
                        }
                        return;
                    }

                    // === Flusso CREAZIONE HACKATHON (esistente, NON toccare) ===
                    if (passoCreazione == 6) {
                        String titolo = datiHackaton.get(0);
                        String sede = datiHackaton.get(1);
                        int maxPartecipanti = Integer.parseInt(datiHackaton.get(2));
                        int maxTeamSize = Integer.parseInt(datiHackaton.get(3));
                        String dataInizio = datiHackaton.get(4);
                        String inizioIscr = datiHackaton.get(5);

                        // fine iscrizioni = 2 giorni prima dell'inizio hackathon
                        LocalDate inizioHack = parseLocalDateFlex(dataInizio);
                        String fineIscr = formatDMY(inizioHack.minusDays(2));

                        Hackathon h = new Hackathon(titolo, sede, organizzatore, maxPartecipanti, maxTeamSize, dataInizio, inizioIscr, fineIscr);
                        HackathonDAO dao = new HackathonDAOImpl();
                        dao.creaHackathon(h);
                        listaHackathon.add(h);

                        JOptionPane.showMessageDialog(frame2, "Hackathon creato con successo!");
                        datiHackaton.clear();
                        passoCreazione = 0;
                        dashboardOrganizzatore.getPannelloLogico().setVisible(false);
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("");
                    }

                }
            });




            dashboardOrganizzatore.getAvantiButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String inputUtente = dashboardOrganizzatore.getFieldScrittura().getText().trim();

                    if (inputUtente.isEmpty()) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Il campo non può essere vuoto.");
                        return;
                    }

                    // === INVITA GIUDICE ===
                    if (passoInvitoGiudice >= 0) {
                        if (passoInvitoGiudice == 0) {
                            // valida titolo ∈ tuoi hackathon
                            Hackathon scelto = null;
                            for (Hackathon h : listaHackathon) {
                                if (h.getTitolo().equalsIgnoreCase(inputUtente) &&
                                        h.getOrganizzatore().equals(organizzatore.getMail())) { scelto = h; break; }
                            }
                            if (scelto == null) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("<html>Hackathon non trovata<br>o non appartiene all'organizzatore.</html>");
                                return;
                            }
                            if (datiInvitoGiudice.size() == 0) datiInvitoGiudice.add(inputUtente);
                            else datiInvitoGiudice.set(0, inputUtente);

                            passoInvitoGiudice = 1;
                            dashboardOrganizzatore.getFieldScrittura().setText("");

                            // guida/riepilogo: delega al pannello logico
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(Color.WHITE);
                            gestisciPannelloLogicoInvitoGiudice();
                            return;
                        }

                        if (passoInvitoGiudice == 1) {
                            String emailGiudice = inputUtente;
                            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
                            if (!emailGiudice.matches(emailRegex)) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("Formato email non valido.");
                                return;
                            }
                            // GIUDICE ESISTENTE
                            UtenteDAO udao = new UtenteDAOImpl();
                            boolean esisteGiudice = udao.esisteUtente(emailGiudice, "giudice");
                            if (!esisteGiudice) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("Giudice non esistente");
                                return;
                            }

                            if (datiInvitoGiudice.size() == 1) datiInvitoGiudice.add(emailGiudice);
                            else if (datiInvitoGiudice.size() >= 2) datiInvitoGiudice.set(1, emailGiudice);

                            passoInvitoGiudice = 2; // stato conferma ("Invita")
                            dashboardOrganizzatore.getFieldScrittura().setText("");

                            // guida/riepilogo: delega al pannello logico (mostrerà 'Invita')
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(Color.WHITE);
                            gestisciPannelloLogicoInvitoGiudice();
                            return;
                        }

                        // se si preme Avanti in stato conferma, ignora
                        return;
                    }

                    // === CREA HACKATHON (codice esistente, invariato) ===
                    // ** Flusso creazione hackathon (esistente) **
                    if ((passoCreazione == 2 || passoCreazione == 3) && !inputUtente.matches("\\d+")) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Errore: inserisci un valore numerico.");
                        return;
                    }

// ✅ nuovo check formato data con java.time
                    if ((passoCreazione == 4 || passoCreazione == 5) && !isDateValidFlex(inputUtente)) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Formato data non valido. Usa GG/MM/AAAA.");
                        return;
                    }


// ✅ nuove regole con LocalDate (niente ParseException qui)
                    if (passoCreazione == 4) {
                        // Data inizio hackathon: NON nel passato
                        LocalDate dataInizioHackathon = parseLocalDateFlex(inputUtente);
                        if (dataInizioHackathon.isBefore(today())) {
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                            dashboardOrganizzatore.getMessaggioErroreOrg().setText("La data dell'hackathon non può essere nel passato.");
                            return;
                        }
                    }

                    if (passoCreazione == 5) {
                        // Inizio iscrizioni:
                        // - NON nel passato
                        // - Può coincidere col giorno dell'evento
                        // - NON può essere DOPO l'inizio evento
                        LocalDate dataInizioIscr = parseLocalDateFlex(inputUtente);
                        LocalDate dataInizioHackathon = parseLocalDateFlex(datiHackaton.get(4));

                        if (dataInizioIscr.isBefore(today())) {
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                            dashboardOrganizzatore.getMessaggioErroreOrg().setText("L'inizio delle iscrizioni non può essere nel passato.");
                            return;
                        }
                        if (dataInizioIscr.isAfter(dataInizioHackathon)) { // ← nuova regola
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                            dashboardOrganizzatore.getMessaggioErroreOrg().setText("Le iscrizioni non possono iniziare dopo l'inizio dell'hackathon.");
                            return;
                        }
                    }






                    datiHackaton.add(inputUtente);
                    passoCreazione++;
                    gestisciPannelloLogicoDashboardOrganizzatore();
                }
            });




            dashboardOrganizzatore.getIndietroButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (passoInvitoGiudice >= 0) {
                        if (passoInvitoGiudice > 0) {
                            int lastIndex = datiInvitoGiudice.size() - 1;
                            String valorePrecedente = "";
                            if (lastIndex >= 0) valorePrecedente = datiInvitoGiudice.remove(lastIndex);
                            passoInvitoGiudice--;

                            // reset eventuale rosso e delega a pannello logico per guida/riepilogo
                            dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(Color.WHITE);
                            gestisciPannelloLogicoInvitoGiudice();

                            dashboardOrganizzatore.getFieldScrittura().setText(valorePrecedente);
                        } else {
                            dashboardOrganizzatore.getPannelloLogico().setVisible(false);
                            passoInvitoGiudice = -1;
                            datiInvitoGiudice.clear();
                        }
                        return;
                    }

                    // CREA HACKATHON (esistente)
                    if (passoCreazione > 0) {
                        String valorePrecedente = datiHackaton.get(datiHackaton.size() - 1);
                        datiHackaton.remove(datiHackaton.size() - 1);
                        passoCreazione--;
                        gestisciPannelloLogicoDashboardOrganizzatore();
                        dashboardOrganizzatore.getFieldScrittura().setText(valorePrecedente);
                    }
                }
            });




            dashboardOrganizzatore.getVisualizzaHackathon().addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   dashboardOrganizzatore.getPannelloLogico().setVisible(true);
                   dashboardOrganizzatore.getAvantiButton().setVisible(false);
                   dashboardOrganizzatore.getIndietroButton().setVisible(false);
                   dashboardOrganizzatore.getCreateButton().setVisible(false);
                   dashboardOrganizzatore.getFieldScrittura().setVisible(false);
                   dashboardOrganizzatore.getAreaDiTesto().setVisible(false);
                   dashboardOrganizzatore.getMessaggioErroreOrg().setVisible(false);
                   dashboardOrganizzatore.getScrollPaneVisualizza().setVisible(true);

                   JTextArea textArea = dashboardOrganizzatore.getTextAreaVisualizza();
                   JScrollPane scrollPane = dashboardOrganizzatore.getScrollPaneVisualizza();
                   textArea.setEditable(false);
                   textArea.setFocusable(false);
                   textArea.setText(""); // Pulisci l'area di testo
                   textArea.setLineWrap(true);
                   textArea.setWrapStyleWord(true);
                   scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                   scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                   Color coloreSfondo = dashboardOrganizzatore.getPannelloLogico().getBackground();

                   textArea.setEditable(false);
                   textArea.setBackground(coloreSfondo);
                   textArea.setForeground(Color.WHITE);
                   scrollPane.getViewport().setBackground(coloreSfondo);
                   scrollPane.setBorder(null);

                   HackathonDAO dao = new HackathonDAOImpl();
                   List<Hackathon> listaDalDB = dao.findAll();

                   if (listaDalDB.isEmpty()) {
                       textArea.setText("Nessun hackathon è stato ancora creato.");
                   } else {
                       textArea.setText("--- Hackathon Disponibili ---\n\n");
                       for (Hackathon hack : listaDalDB) {
                           textArea.append("Titolo: " + hack.getTitolo() + "\n");
                           textArea.append("Sede: " + hack.getSede() + "\n");
                           textArea.append("Max Partecipanti: " + hack.getMaxPartecipanti() + "\n");
                           textArea.append("Max Grandezza Team: " + hack.getMaxGrandezzaTeam() + "\n");
                           textArea.append("Data Inizio: " + hack.getInizio() + "\n");
                           textArea.append("Inizio Iscrizioni: " + hack.getInizioIscrizioni() + "\n");
                           textArea.append("Fine Iscrizioni: " + hack.getFineIscrizioni() + "\n");
                           textArea.append("Organizzatore: " + hack.getOrganizzatore() + "\n");
                           textArea.append("-------------------------------------\n");
                       }
                   }

               }

           });

            dashboardOrganizzatore.getInvitaGiudiciButton().addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    // mostra i pannelli necessari
                    dashboardOrganizzatore.getPannelloLogico().setVisible(true);
                    dashboardOrganizzatore.getScrollPaneVisualizza().setVisible(true);
                    dashboardOrganizzatore.getAreaDiTesto().setVisible(true);
                    dashboardOrganizzatore.getFieldScrittura().setVisible(true);
                    dashboardOrganizzatore.getAvantiButton().setVisible(true);
                    dashboardOrganizzatore.getIndietroButton().setVisible(true);
                    dashboardOrganizzatore.getCreateButton().setVisible(false);
                    dashboardOrganizzatore.getMessaggioErroreOrg().setVisible(true);
                    

                    // stile guida
                    JLabel guida = dashboardOrganizzatore.getMessaggioErroreOrg();
                    guida.setForeground(Color.WHITE);

                    // carica SOLO i titoli dei tuoi hackathon
                    HackathonDAO dao = new HackathonDAOImpl();
                    List<Hackathon> miei = dao.findByOrganizzatore(organizzatore.getMail());
                    listaHackathon.clear();
                    listaHackathon.addAll(miei);

                    JTextArea textArea = dashboardOrganizzatore.getTextAreaVisualizza();
                    JScrollPane scrollPane = dashboardOrganizzatore.getScrollPaneVisualizza();

                    textArea.setEditable(false);
                    textArea.setFocusable(false);
                    textArea.setText("");

                    // evita barre inutili
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

                    Color sfondo = dashboardOrganizzatore.getPannelloLogico().getBackground();
                    textArea.setBackground(sfondo);
                    textArea.setForeground(Color.WHITE);
                    scrollPane.getViewport().setBackground(sfondo);
                    scrollPane.setBorder(null);

                    if (miei.isEmpty()) {
                        textArea.setText("Non hai ancora creato hackathon.");
                    } else {
                        textArea.append("--- I tuoi Hackathon ---\n\n");
                        for (Hackathon h : miei) textArea.append(h.getTitolo() + "\n");
                    }

                    // (ri)inizializza il mini-wizard
                    datiInvitoGiudice = new ArrayList<>();
                    passoInvitoGiudice = 0;
                    hackathonSelezionato = null;
                    dashboardOrganizzatore.getFieldScrittura().setText("");

                    // lascia che sia il pannello logico a impostare guida e riepilogo
                    gestisciPannelloLogicoInvitoGiudice();
                }
            });








        }

    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("d/M/uuuu");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Ritorna la data di oggi (senza orario) usando il fuso orario di sistema.
     * Funziona in client no server
     * Mi serve per fare controlli sulle finestre temporali (iscrizioni aperte/chiuse, ecc.).
     *
     * @return data di oggi come {@link java.time.LocalDate}
     * @see java.time.ZoneId
     */


    private LocalDate today() {
        return LocalDate.now(ZoneId.systemDefault());
    }

    /**
     * Converte una stringa data in {@link java.time.LocalDate}.
     * <p>
     * Accetto due formati:
     * - dd/MM/yyyy (quello che scrive l'utente in input)
     * - ISO yyyy-MM-dd (utile se arriva dal DB o da altre parti)
     *
     * @param s stringa data (es. "24/04/2026" oppure "2026-04-24")
     * @return la data convertita, oppure {@code null} se il formato non è valido
     * @see java.time.format.DateTimeFormatter
     */


    private LocalDate parseLocalDateFlex(String s) {
        if (s == null) return null;
        s = s.trim();
        try {
            return LocalDate.parse(s, DMY);
        } catch (DateTimeParseException ignore) {
            try {
                return LocalDate.parse(s, ISO);
            } catch (DateTimeParseException ignore2) {
                return null;
            }
        }
    }

    /**
     * Controlla se una stringa può essere interpretata come data valida
     * usando {@link #parseLocalDateFlex(String)}.
     *
     * @param s stringa data inserita dall'utente
     * @return true se la data è valida, false altrimenti
     * @see #parseLocalDateFlex(String)
     */


    private boolean isDateValidFlex(String s) {
        return parseLocalDateFlex(s) != null;
    }

    /**
     * Converte una {@link java.time.LocalDate} in stringa nel formato "dd/MM/yyyy"
     * per mostrarla in modo leggibile nella UI.
     *
     * @param d data da formattare
     * @return stringa formattata, oppure stringa vuota se {@code d} è null
     */


    private String formatDMY(LocalDate d) {
        return d == null ? "" : d.format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
    }


    /**
     * Aggiorna il pannello logico dell'organizzatore durante la creazione di un hackathon.
     * <p>
     * In base a {@code passoCreazione} mostra la domanda corretta, aggiorna il riepilogo HTML
     * e decide quali bottoni rendere visibili (Avanti / Indietro / Crea).
     * Qui viene anche mostrata la "fine iscrizioni" calcolata automaticamente.
     *
     * @see #gestisciDashboardOrganizzatore(model.Organizzatore)
     */


    private void gestisciPannelloLogicoDashboardOrganizzatore() {
                JLabel labelIstruzioni = dashboardOrganizzatore.getAreaDiTesto();
                JLabel labelInput = dashboardOrganizzatore.getMessaggioErroreOrg();
                JTextField inputField = dashboardOrganizzatore.getFieldScrittura();
                JButton avantiBtn = dashboardOrganizzatore.getAvantiButton();
                JButton indietroBtn = dashboardOrganizzatore.getIndietroButton();
                JButton creaBtn = dashboardOrganizzatore.getCreateButton();

                labelInput.setForeground(Color.WHITE);
                indietroBtn.setVisible(passoCreazione > 0);
                avantiBtn.setVisible(true);
                creaBtn.setVisible(false);
                inputField.setText("");
                inputField.requestFocusInWindow();

                String[] etichetteCampi = {"Titolo:", "Sede:", "Max Partecipanti:", "Max Grandezza Team:", "Data Inizio:", "Inizio Iscrizioni:", "Fine Iscrizioni:"};
                String[] domande = {
                "Inserisci il titolo.",
                "Inserisci la sede.",
                "Inserisci il n° max di partecipanti.",
                "Inserisci la grandezza max del team.",
                "Inserisci la data di inizio (GG/MM/AAAA).",
                "Data inizio iscrizioni (GG/MM/AAAA)."
        };

                String formText = "<html>Creazione Hackathon<br><br>";
                for (int i = 0; i < etichetteCampi.length; i++) {
                    formText += etichetteCampi[i] + " ";
                    if (i < datiHackaton.size()) {
                        formText += datiHackaton.get(i);
                    } else {
                        // Mostra fine iscrizioni calcolata (se abbiamo già la data inizio hackathon)
                        if (i == 6 && datiHackaton.size() >= 5) {
                            LocalDate inizioHack = parseLocalDateFlex(datiHackaton.get(4));
                            if (inizioHack != null) {
                                formText += formatDMY(inizioHack.minusDays(2));
                            }
                        }
                    }

                    formText += "<br>";
                }
                formText += "</html>";
                labelIstruzioni.setText(formText);

                if (passoCreazione < domande.length) {
                    labelInput.setText(domande[passoCreazione]);
                } else {
                    labelInput.setText("Clicca 'Crea' per confermare.");
                    avantiBtn.setVisible(false);
                    creaBtn.setVisible(true);
                }
            }


    /**
     * Aggiorna il pannello logico dell'organizzatore per l'invito dei giudici.
     * <p>
     * Gestisce il mini-wizard di invito giudice (step + dati raccolti) e aggiorna la guida testuale
     * per far capire cosa inserire all'utente.
     *
     * @see #gestisciDashboardOrganizzatore(model.Organizzatore)
     */


    private void gestisciPannelloLogicoInvitoGiudice() {

        JLabel labelIstruzioni = dashboardOrganizzatore.getAreaDiTesto();
        JLabel labelInput = dashboardOrganizzatore.getMessaggioErroreOrg();
        JTextField inputField = dashboardOrganizzatore.getFieldScrittura();
        JButton avantiBtn = dashboardOrganizzatore.getAvantiButton();
        JButton indietroBtn = dashboardOrganizzatore.getIndietroButton();
        JButton creaBtn = dashboardOrganizzatore.getCreateButton();


        labelInput.setForeground(Color.WHITE);
        inputField.setText("");
        inputField.requestFocusInWindow();

        indietroBtn.setVisible(passoInvitoGiudice > 0);
        avantiBtn.setVisible(true);
        creaBtn.setVisible(false);


        String[] etichetteCampi = {"Hackathon:", "Email Giudice:"};
        String[] domande = {
                "Inserisci il titolo dell'hackathon.",
                "Inserisci l'email del giudice."
        };

        String formText = "<html>Invito Giudice<br><br>";
        for (int i = 0; i < etichetteCampi.length; i++) {
            formText += etichetteCampi[i] + " ";
            if (i < datiInvitoGiudice.size()) {
                formText += datiInvitoGiudice.get(i);
            }
            formText += "<br>";
        }
        formText += "</html>";
        labelIstruzioni.setText(formText);


        if (passoInvitoGiudice < domande.length) {
            labelInput.setText(domande[passoInvitoGiudice]);
        } else {

            labelInput.setText("Clicca 'Invita' per confermare.");
            avantiBtn.setVisible(false);
            creaBtn.setVisible(true);
            creaBtn.setText("Invita");
        }
    }


    /**
     * Aggiorna la scritta di guida (label) nella dashboard utente.
     * <p>
     * Serve soprattutto quando l'utente riclicca sul campo input dopo un errore: in base al wizard attivo
     * (iscrizione, team, inviti, documenti, ecc.) rimetto la guida corretta dello step corrente.
     */

    private void aggiornaGuidaDashboardUtente() {
        if (dashboardUtente == null) return;

        JLabel guida = dashboardUtente.getMessaggioErroreOrg();
        guida.setForeground(Color.WHITE);



        if (passoInvitiRicevutiU >= 0) {
            guida.setForeground(Color.WHITE);
            if (passoInvitiRicevutiU == 0) {
                guida.setText("<html>Inserisci l'<b>ID</b> dell'invito da accettare.</html>");
            } else if (passoInvitiRicevutiU == 1) {
                guida.setText("<html>Clicca <b>Accetta</b> per confermare.</html>");
            }
            return;
        }


        if (passoInvitoTeamU >= 0) {
            if (passoInvitoTeamU == 0) {
                guida.setText("<html>Inserisci il <b>nome del team</b><br>tra quelli elencati.</html>");
            } else if (passoInvitoTeamU == 1) {
                guida.setText("<html>Inserisci l'<b>email</b> dell'utente da invitare.</html>");
            } else if (passoInvitoTeamU == 2) {
                guida.setText("<html>Clicca <b>Invita</b> per confermare.</html>");
            }
            return;
        }


        // Wizard ISCRIZIONE (Hackaton disponibili)
        if (passoIscrizioneU >= 0) {
            if (passoIscrizioneU == 0) {
                guida.setText("<html>Inserisci il titolo<br>dell'hackathon.</html>");
            } else if (passoIscrizioneU == 1) {
                guida.setText("<html>Clicca 'Iscriviti'<br>per confermare.</html>");
            }
            return;
        }

        // Wizard TEAM (I miei Hackaton → Crea Team)
        if (passoTeamU >= 0) {
            if (passoTeamU == 0) {
                guida.setText("<html>Inserisci il titolo<br>dell'hackathon.</html>");
            } else if (passoTeamU == 1) {
                guida.setText("<html>Inserisci il nome<br>del team.</html>");
            } else if (passoTeamU == 2) {
                guida.setText("<html>Clicca 'Crea Team'<br>per confermare.</html>");
            }
            return;
        }
        if (passoDocumentoU >= 0) {

            guida.setForeground(Color.WHITE);

            if (passoDocumentoU == 0) {
                guida.setText("<html>Inserisci il nome<br>del team.</html>");
            } else if (passoDocumentoU == 1) {
                guida.setText("<html>Scrivi il contenuto<br>del documento.</html>");
            } else if (passoDocumentoU == 2) {
                guida.setText("<html>Clicca 'Carica documento'<br>per confermare.</html>");
            }
            return;
        }
        if (passoInvitoTeamU >= 0) {
            if (passoInvitoTeamU == 0) {
                guida.setText("<html>Inserisci il <b>nome del team</b><br>(tra quelli sotto).</html>");
            } else if (passoInvitoTeamU == 1) {
                guida.setText("<html>Inserisci l'<b>email</b><br>dell'utente da invitare.</html>");
            }
            return;
        }

        // Nessun wizard attivo
        guida.setText("");
    }

    /**
     * Aggiorna la guida testuale nella dashboard giudice.
     * <p>
     * In base al wizard attivo (hackathon assegnati, pubblicazione problema, commenti, voti, ecc.)
     * imposta il messaggio corretto per lo step corrente.
     * Serve anche quando l'utente riclicca sul campo input dopo un errore: rimetto la guida “giusta”
     * invece di lasciare l'errore rosso.
     *
     * @see #gestisciDashboardGiudice(model.Giudice)
     * @see #resetWizardGiudice()
     */
    void aggiornaGuidaDashboardGiudice() {
        if (dashboardGiudice == null) return;

        JLabel guida = dashboardGiudice.getMessaggioErroreOrg();
        if (guida == null) return;

        guida.setForeground(Color.WHITE);

        // 1) INVITI
        if (passoInvitoG >= 0) {
            if (passoInvitoG == 0) {
                guida.setText("<html>Inserisci il titolo<br>dell'hackathon che vuoi accettare.</html>");
            } else if (passoInvitoG == 1) {
                guida.setText("<html>Clicca 'Partecipa'<br>per accettare l'invito.</html>");
            }
            return;
        }

        // 2) PUBBLICA PROBLEMA
        if (passoProblemaG >= 0) {
            if (passoProblemaG == 0) {
                guida.setText("<html>Inserisci il titolo<br>dell'hackathon (assegnato a te).</html>");
            } else if (passoProblemaG == 1) {
                guida.setText("<html>Inserisci la descrizione<br>del problema e clicca 'Pubblica'.</html>");
            }
            return;
        }

        // 3) TEAM (commento/voto)
        if (passoTeamG >= 0) {
            if (passoTeamG == 0) {
                guida.setText("<html>Inserisci il titolo<br>dell'hackathon (assegnato a te).</html>");
            } else if (passoTeamG == 1) {
                guida.setText("<html>Inserisci l'ID del team<br>su cui vuoi agire.</html>");
            } else if (passoTeamG == 2) {
                guida.setText("<html>Scrivi 1 per commentare<br>o 2 per votare, poi 'Avanti'.</html>");
            } else if (passoTeamG == 3) {
                guida.setText("<html>Inserisci testo (commento)<br>oppure voto (0-10) e conferma.</html>");
            }
            return;
        }

        // 4) CLASSIFICA
        if (passoClassificaG >= 0) {
            guida.setText("<html>Inserisci il titolo<br>dell'hackathon per vedere la classifica.</html>");
            return;
        }

        guida.setText("");
    }

    /**
     * Reset completo dei wizard lato giudice.
     * <p>
     * Riporto tutti i "passoX" del giudice a -1 e pulisco le liste datiX, così una funzione non interferisce
     * con un'altra (es. voti vs commenti vs problema).
     */


    private void resetWizardGiudice() {
        passoInvitoG = -1;
        datiInvitoG.clear();

        passoProblemaG = -1;
        datiProblemaG.clear();

        passoTeamG = -1;
        datiTeamG.clear();

        passoClassificaG = -1;
        datiClassificaG.clear();
    }

    /**
     * Recupera la fine iscrizioni di un hackathon dato il suo titolo.
     * <p>
     * Mi serve per applicare la regola "team definitivi": dopo la fine iscrizioni non si possono
     * creare team / inviare inviti / accettare inviti.
     *
     * @param titoloHackathon titolo dell'hackathon (case-insensitive)
     * @return data di fine iscrizioni, oppure {@code null} se l'hackathon non esiste o la data non è leggibile
     * @see #today()
     * @see #parseLocalDateFlex(String)
     */

    private LocalDate fineIscrizioniByTitolo(String titoloHackathon) {
        if (titoloHackathon == null) return null;
        String t = titoloHackathon.trim();

        dao.HackathonDAO hdao = new daoImpl.HackathonDAOImpl();
        for (model.Hackathon h : hdao.findAll()) {
            if (h.getTitolo() != null && h.getTitolo().trim().equalsIgnoreCase(t)) {
                return parseLocalDateFlex(h.getFineIscrizioni());
            }
        }
        return null;
    }


    /**
     * Crea una piccola anteprima del documento (da mostrare nella UI).
     * <p>
     * Taglia il contenuto a:
     * - massimo 5 righe
     * - circa 300 caratteri
     * così la textArea non esplode e l’utente capisce subito di che documento si tratta.
     *
     * @param contenuto testo completo del documento
     * @return anteprima del contenuto (eventualmente troncata con "...")
     */

    private String anteprimaDocumento(String contenuto) {
        if (contenuto == null) return "";
        contenuto = contenuto.trim();
        if (contenuto.isEmpty()) return "";

        int maxRighe = 5;
        int maxChar = 300;

        String[] righe = contenuto.split("\\R"); // split su \n, \r\n, ecc.
        StringBuilder sb = new StringBuilder();
        int righeUsate = 0;

        for (String riga : righe) {
            if (righeUsate >= maxRighe) break;

            // se supero il limite di caratteri, taglio l'ultima riga
            if (sb.length() + riga.length() + 1 > maxChar) {
                int spazio = maxChar - sb.length();
                if (spazio > 0) {
                    if (sb.length() > 0) sb.append("\n");
                    if (riga.length() > spazio) {
                        sb.append(riga, 0, spazio);
                    } else {
                        sb.append(riga);
                    }
                }
                righeUsate++;
                break;
            } else {
                if (sb.length() > 0) sb.append("\n");
                sb.append(riga);
                righeUsate++;
            }
        }

        if (sb.length() < contenuto.length()) {
            sb.append("...");
        }

        return sb.toString();
    }
    /**
     * Limita una stringa ad un massimo di caratteri, utile per non “rompere” la UI.
     * <p>
     * Se la stringa supera {@code max}, ritorno una versione tagliata con "..." finale.
     *
     * @param s stringa originale
     * @param max massimo numero di caratteri consentiti
     * @return stringa pronta da stampare (tagliata se necessario)
     */

    private String limitaPerAreaDiTesto(String s, int max) {
        if (s == null) return "";
        String trimmed = s.trim();
        if (trimmed.length() <= max) return trimmed;
        return trimmed.substring(0, max) + "...";
    }

}

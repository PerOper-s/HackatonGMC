package controller;

import gui.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.Date;

import model.*;
public class Controller {


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

       loginButton.addActionListener(new ActionListener () {

           public void actionPerformed(ActionEvent e) {
               String emailInserita = campoEmail.getText();
               String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
               boolean emailValida = false;
               boolean ruoloSelezionato = false;

               if (gruppoRuoli.getSelection() == null) {
                   messaggioErrore.setText("Seleziona un ruolo");
                   return;
               } else {
                   messaggioErrore.setText("");
                   ruoloSelezionato = true;
               }

               if (emailInserita.equals("Email") || emailInserita.isEmpty()) {
                   messaggioErrore.setText("Inserisci un'email valida");
                   return;
               }
               if (!emailInserita.matches(emailRegex)) {
                   messaggioErrore.setText("Formato email non valido.");
                   return;
               } else {
                   messaggioErrore.setText("");
                   emailValida = true;
               }

               Utente nuovoUtente = null;
               String ruoloSelezionatoTesto = "";
               if (ruoloSelezionato == true && emailValida == true) {

                   if (utenteRadioButton.isSelected()) {
                       nuovoUtente = new Utente(emailInserita);
                          ruoloSelezionatoTesto = "Utente";
                   } else if (organizzatoreRadioButton.isSelected()) {
                       nuovoUtente = new Organizzatore(emailInserita);
                          ruoloSelezionatoTesto = "Organizzatore";
                   } else if (giudiceRadioButton.isSelected()) {
                       nuovoUtente = new Giudice(emailInserita);
                          ruoloSelezionatoTesto = "Giudice";
                   }

                   if (nuovoUtente != null) {

                       nuovoUtente.registrati();
                       frame.dispose();

                       if (ruoloSelezionatoTesto.equals("Utente")) {
                          dashboardUtente = new DashboardUtente(nuovoUtente.getMail());
                            gestisciDashboardUtente(nuovoUtente);
                            return;
                       }

                       if (ruoloSelezionatoTesto.equals("Organizzatore")) {
                           dashboardOrganizzatore = new DashboardOrganizzatore(nuovoUtente.getMail());
                           gestisciDashboardOrganizzatore((Organizzatore) nuovoUtente);
                           return;
                       }

                       if (ruoloSelezionatoTesto.equals("Giudice")) {

                          dashboardGiudice = new DashboardGiudice(nuovoUtente.getMail());
                            gestisciDashboardGiudice((Giudice) nuovoUtente);
                            return;
                       }
                   }


               }

           }

       });

       loginPanel.addMouseListener(new MouseAdapter(){

           @Override
           public void mousePressed(MouseEvent e){
               loginPanel.requestFocusInWindow();

           }


        });

        }
        private void gestisciDashboardUtente(Utente utente){
            JLabel messaggioBenvenuto = dashboardUtente.getMessaggioBenvenuto();
            frame2 = new JFrame("HackatonDashboard - Utente" );
            frame2.setContentPane(dashboardUtente.getDashboardUtente());
            frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame2.pack();
            frame2.setLocationRelativeTo(null);
            frame2.setVisible(true);
            frame2.setResizable(false);

            messaggioBenvenuto.setText("Utente, " + utente.getMail() + " ");
        }

        private void gestisciDashboardGiudice(Giudice giudice){
            JLabel messaggioBenvenuto1 = dashboardGiudice.getMessaggioBenvenuto1();
            frame2 = new JFrame("HackatonDashboard - Giudice" );
            frame2.setContentPane(dashboardGiudice.getDashboardGiudice());
            frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame2.pack();
            frame2.setLocationRelativeTo(null);
            frame2.setVisible(true);
            frame2.setResizable(false);

            messaggioBenvenuto1.setText("Giudice, " + giudice.getMail() + " ");
        }

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
            JPanel pannelloLogico = dashboardOrganizzatore.getPannelloLogico();
            JTextField inputField = dashboardOrganizzatore.getFieldScrittura();

            inputField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    JLabel labelInput = dashboardOrganizzatore.getMessaggioErroreOrg();
                    labelInput.setForeground(Color.WHITE);

                    String[] domande = {
                            "Inserisci il titolo.",
                            "Inserisci la sede.",
                            "Inserisci il n° max di partecipanti.",
                            "Inserisci la grandezza max del team.",
                            "Inserisci la data di inizio (GG/MM/AAAA).",
                            "Data inizio iscrizioni (GG/MM/AAAA).",
                            "Data fine iscrizioni (GG/MM/AAAA)."
                    };

                    if (passoCreazione < domande.length) {
                        labelInput.setText(domande[passoCreazione]);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                }
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

                }
            });


            dashboardOrganizzatore.getCreateButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String titolo = datiHackaton.get(0);
                    String sede = datiHackaton.get(1);
                    int maxPartecipanti = Integer.parseInt(datiHackaton.get(2));
                    int maxGrandezzaTeam = Integer.parseInt(datiHackaton.get(3));
                    String inizio = datiHackaton.get(4);
                    String inizioIscrizioni = datiHackaton.get(5);
                    String fineIscrizioni = datiHackaton.get(6);

                    Hackathon nuovoHackathon = organizzatore.creaHackathon(titolo, sede, maxPartecipanti, maxGrandezzaTeam, inizio, inizioIscrizioni, fineIscrizioni);
                    listaHackathon.add(nuovoHackathon);

                    JOptionPane.showMessageDialog(frame2, "Hackathon \"" + nuovoHackathon.getTitolo() + "\" creato con successo");

                }
            });

            dashboardOrganizzatore.getAvantiButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    String inputUtente = dashboardOrganizzatore.getFieldScrittura().getText().trim();
                    if (inputUtente.isEmpty()) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Il campo non può essere vuoto.");
                        return;
                    }
                    if ((passoCreazione == 2 || passoCreazione == 3) && !inputUtente.matches("\\d+")) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Errore: Inserisci un valore numerico.");
                        return;
                    }
                    if ((passoCreazione == 4 || passoCreazione == 5 || passoCreazione == 6) && !isDateValid(inputUtente)) {
                        dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                        dashboardOrganizzatore.getMessaggioErroreOrg().setText("Formato data non valido. Usa GG/MM/AAAA.");
                        return;
                    }


                    try {

                        if (passoCreazione == 4) {
                            if (sdf.parse(inputUtente).before(getOggiSenzaOrario())) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("La data dell'hackathon non può essere nel passato.");
                                return;
                            }
                        }


                        if (passoCreazione == 5) {
                            Date dataInizioIscrizioni = sdf.parse(inputUtente);
                            Date dataInizioHackathon = sdf.parse(datiHackaton.get(4));
                            if (dataInizioIscrizioni.before(getOggiSenzaOrario())) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("L'inizio iscrizioni non può essere nel passato.");
                                return;
                            }
                            if (!dataInizioIscrizioni.before(dataInizioHackathon)) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("Le iscrizioni devono iniziare prima dell'hackathon.");
                                return;
                            }
                        }


                        if (passoCreazione == 6) {
                            Date dataFineIscrizioni = sdf.parse(inputUtente);
                            Date dataInizioIscrizioni = sdf.parse(datiHackaton.get(5));
                            Date dataInizioHackathon = sdf.parse(datiHackaton.get(4));
                            if (!dataFineIscrizioni.after(dataInizioIscrizioni)) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("La fine deve essere dopo l'inizio delle iscrizioni.");
                                return;
                            }
                            if (!dataFineIscrizioni.before(dataInizioHackathon)) {
                                dashboardOrganizzatore.getMessaggioErroreOrg().setForeground(new Color(180, 26, 0));
                                dashboardOrganizzatore.getMessaggioErroreOrg().setText("Le iscrizioni devono finire prima dell'hackathon.");
                                return;
                            }
                        }
                    } catch (java.text.ParseException ex) {
                        return;
                    }
                    datiHackaton.add(inputUtente);
                    passoCreazione++;
                    gestisciPannelloLogicoDashboardOrganizzatore();
                }
            });

            dashboardOrganizzatore.getIndietroButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (passoCreazione > 0) {
                        String valorePrecedente = datiHackaton.getLast();
                        datiHackaton.removeLast();
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
                   Color coloreSfondo = dashboardOrganizzatore.getPannelloLogico().getBackground();

                   textArea.setEditable(false);
                   textArea.setBackground(coloreSfondo);
                   textArea.setForeground(Color.WHITE);
                   scrollPane.getViewport().setBackground(coloreSfondo);
                   scrollPane.setBorder(null);

                   if (listaHackathon.isEmpty()) {
                       textArea.setText("Nessun hackathon è stato ancora creato.");
                   } else {
                       textArea.setText("--- Hackathon Disponibili ---\n\n");
                       for (Hackathon hack : listaHackathon) {
                           textArea.append("Titolo: " + hack.getTitolo() + "\n");
                           textArea.append("Sede: " + hack.getSede() + "\n");
                           textArea.append("Max Partecipanti: " + hack.getMaxPartecipanti() + "\n");
                            textArea.append("Max Grandezza Team: " + hack.getMaxGrandezzaTeam() + "\n");
                            textArea.append("Data Inizio: " + hack.getInizio() + "\n");
                            textArea.append("Inizio Iscrizioni: " + hack.getInizioIscrizioni() + "\n");
                            textArea.append("Fine Iscrizioni: " + hack.getFineIscrizioni() + "\n");
                            textArea.append("Partecipanti Iscritti: " + hack.getIscrizioniCount() + "\n");
                            textArea.append("Organizzatore: " + hack.getOrganizzatore() + "\n");
                           textArea.append("-------------------------------------\n");
                       }
                   }
               }

           });

    }
                private boolean isDateValid(String date) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.setLenient(false);
                    try {
                        sdf.parse(date);
                        return true;
                    } catch (java.text.ParseException e) {
                        return false;
                    }

    }

                private Date getOggiSenzaOrario() {
                    // Usa il Calendar per impostare ore, minuti, secondi e millisecondi a zero
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
                    cal.set(java.util.Calendar.MINUTE, 0);
                    cal.set(java.util.Calendar.SECOND, 0);
                    cal.set(java.util.Calendar.MILLISECOND, 0);
                    return cal.getTime();
                }


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
                String[] domande = {"Inserisci il titolo.", "Inserisci la sede.", "Inserisci il n° max di partecipanti.", "Inserisci la grandezza max del team.", "Inserisci la data di inizio (GG/MM/AAAA).", "Data inizio iscrizioni (GG/MM/AAAA).", "Data fine iscrizioni (GG/MM/AAAA)."};

                String formText = "<html>Creazione Hackathon<br><br>";
                for (int i = 0; i < etichetteCampi.length; i++) {
                    formText += etichetteCampi[i] + " ";
                    if (i < datiHackaton.size()) {
                        formText += datiHackaton.get(i);
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
    }

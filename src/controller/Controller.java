package controller;

import gui.*;
import javax.swing.*;
import java.awt.event.*;
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

            messaggioBenvenuto2.setText("Organizzatore, " + organizzatore.getMail() + " ");
    }




    }

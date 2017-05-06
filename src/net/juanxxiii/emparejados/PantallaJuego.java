package net.juanxxiii.emparejados;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Juego de parejas (incompleto)
 *
 * @author FPA
 */
public class PantallaJuego extends javax.swing.JPanel {

    private static final int NUM_CARTAS = 18;//Número de cartas en la partida
    private int numeroCartas = 0;//Número decartas en el mazo
    private ArrayList<String> baraja = new ArrayList();//Baraja con todas las cartas
    private ArrayList<String> parejas = new ArrayList();//Parejas de la partida
    private int[] estado = new int[NUM_CARTAS];//Estado de las cartas
    private static final int REVERSO = 0;//Estados de las cartas
    private static final int ANVERSO = 1;//Estados de las cartas
    //La variable primera contiene la primera carta levantada de cada par
    //Con valor null indica que no se ha levantado la primera carta aún
    private static JLabel primeraJLabel = null;
    private String primeraNombre = null;
    private int primeraIndice = -1;
    private int numeroParejas=0;//Número de parejas conseguido
    /**
     * Constructor
     */
    public PantallaJuego() {
        initComponents();
        initGame();
        cargarCartas();
        agregarPareja();
        crearUI();
    }

    /**
     * Inicializa el juego
     */
    private void initGame() {
        //Inicializa el array de cartas y el array de estado de las cartas
        for (int i = 0; i < NUM_CARTAS; i++) {
            parejas.add(null);
            estado[i] = REVERSO;
        }
    }

    /**
     * Carga las cartas en la baraja. Los nombres de las cartas están en el
     * fichero cartas.txt.
     */
    private void cargarCartas() {
        try {
            InputStream in = this.getClass().getResourceAsStream("/assets/cartas.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String nombre;
            while ((nombre = br.readLine()) != null) {
                if (nombre.trim().length() > 0) {
                    baraja.add(nombre);
                    numeroCartas++;
                }
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Agrega una nueva pareja al juego
     */
    private void agregarPareja() {
        int aleatorio;//Para seleccionar la posición en el array de parejas
        String nuevaCarta;
        for (int i = 0; i < NUM_CARTAS / 2; i++) {//HAY QUE HACER NUM_CARTAS/2 PARES
            //Se genera la nueva carta
            nuevaCarta = generarCartaNoUsada();
            //Se busca una posición libre en parejas y se agrega la carta (2 veces)
            for (int j = 0; j < 2; j++) {
                do {
                    aleatorio = (int) (Math.random() * NUM_CARTAS);
                    if (parejas.get(aleatorio) == null) {
                        parejas.set(aleatorio, nuevaCarta);
                        break;
                    }
                } while (parejas.get(aleatorio) != null);
            }
        }
    }

    /**
     * Genera una carta del mazo, comprobando que no esté entre las que ya se
     * han utilizado en las parejas
     *
     * @return Carta generada
     */
    private String generarCartaNoUsada() {
        int aleatorio;//Para seleccionar una carta al azar del mazo
        do {
            aleatorio = (int) (Math.random() * numeroCartas);
        } while (parejas.contains(baraja.get(aleatorio)));
        return baraja.get(aleatorio);
    }

    /**
     * Crea las cartas y agrega los listener
     */
    private void crearUI() {
        Carta carta;
        for (int i = 0; i < NUM_CARTAS; i++) {
            carta = new Carta(i);
            //LISTENER
            carta.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int pos = ((Carta) e.getComponent()).getPos();
                    if (estado[pos] == REVERSO) {
                        estado[pos] = ANVERSO;
                        ((JLabel) e.getComponent()).setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/" + parejas.get(pos))));
                        if (primeraJLabel == null) {
                            //Ha levantado la primera carta
                            primeraJLabel = (JLabel) e.getComponent();
                            primeraNombre = parejas.get(pos);
                            primeraIndice = pos;
                        } else {
                            //Ha levantado la segunda carta
                            if (!parejas.get(pos).equals(primeraNombre)) {
                                //No hay pareja, esperamos y damos la vuelta
                                new Thread() {
                                    public void run() {
                                        try {
                                            sleep(500);
                                            ((JLabel) e.getComponent()).setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/back.gif")));
                                            primeraJLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/back.gif")));
                                            primeraJLabel = null;
                                            primeraNombre = null;
                                            primeraIndice = -1;
                                        } catch (InterruptedException ex) {
                                        }
                                    }
                                }.start();
                                //Las dejo en estado REVERSO
                                estado[pos] = REVERSO;
                                estado[primeraIndice] = REVERSO;
                            } else {
                                //Hay pareja. Hacemos reset y contamos +1
                                primeraJLabel = null;
                                primeraNombre = null;
                                primeraIndice = -1;
                                numeroParejas++;
                                if (numeroParejas==NUM_CARTAS/2){
                                    JOptionPane.showMessageDialog(PantallaJuego.this,"¡ENHORABUENA!", 
                                    "EmparejadosJ23", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                            
                        }
                    }
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                    int pos = ((Carta) e.getComponent()).getPos();
                    if (estado[pos] == REVERSO) {
                        ((JLabel) e.getComponent()).setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/backwhite.gif")));
                    }
                }

                public void mouseExited(MouseEvent e) {
                    int pos = ((Carta) e.getComponent()).getPos();
                    if (estado[pos] == REVERSO) {
                        ((JLabel) e.getComponent()).setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/back.gif")));
                    }
                }
            });
            //ASIGNACION DE ICONO
            carta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/back.gif"))); // NOI18N
            add(carta);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridLayout(3, 6));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

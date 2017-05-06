package net.juanxxiii.emparejados;

import javax.swing.JLabel;

/**
 * Representa una carta, compuesto por un JLabel y un identificador de posición
 * para poder posteriormente identificar el JLabel pulsado.
 * 
 * @author FPA
 */
public class Carta extends JLabel {
    private int pos;
    /**
     * Constructor
     * 
     * @param pos Posición dentro de la partida de juego 
     */
    public Carta(int pos) {
        super();
        this.pos = pos;
    }
    public int getPos() {
        return pos;
    }
    public void setPos(int pos) {
        this.pos = pos;
    }
}

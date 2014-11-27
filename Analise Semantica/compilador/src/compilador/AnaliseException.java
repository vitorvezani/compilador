/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

/**
 *
 * @author Vitor Rodrigo Vezani && Pedro Henrique Grandin
 */
public class AnaliseException extends Exception {
private final int nLinha;

    public AnaliseException(String sMessage, int nLinha) {
        super(sMessage);
        this.nLinha = nLinha;
    }

    /**
     * @return the nLinha
     */
    public int getnLinha() {
        return nLinha;
    }

}

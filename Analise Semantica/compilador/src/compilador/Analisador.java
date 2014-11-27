/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

/**
 *
 * @author Vitor Rodrigo Vezani && Pedro Henrique Grandin
 */
abstract public class Analisador {
    
    public final class Token {

    public String sLexema, sSimbolo;

    public Token(String sLexema, String sSimbolo) {

        this.sLexema = sLexema;

        this.sSimbolo = sSimbolo;

    }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

/**
 *
 * @author Vitor Rodrigo Vezani && Pedro Henrique Grandin
 */
public class Simbolo {

    public String identificador, tipo;
    public char nivel;
    public Integer rotulo, endereco;

    public Simbolo(String lexema, String tipo, char nivel, int rotulo ,int endereco) {
        
        this.identificador = lexema;
        
        this.tipo = tipo;
        
        this.nivel = nivel;
        
        this.rotulo = rotulo;
        
        this.endereco = endereco;
    }

    public Simbolo(String lexema, String tipo, int currAd) {
        
        this.identificador = lexema;
        
        this.tipo = tipo;
        
        this.endereco = currAd;
    }
    
    public Simbolo(String lexema, String tipo) {
        
        this.identificador = lexema;
        
        this.tipo = tipo;
    }
    
    public Simbolo(String lexema, String tipo, char nivel, int rotulo) {
        
        this.identificador = lexema;

        this.tipo = tipo;
        
        this.nivel = nivel;
        
        this.rotulo = rotulo;
    }
}
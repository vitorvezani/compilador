/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilidades;

import compilador.Analisador;
import compilador.Simbolo;
import java.util.ArrayList;

/**
 *
 * @author Vitor
 */
public final class Log {

    private static final boolean indDebbug = false;

    public static void exibeExpressao(String tipoExpressao, ArrayList<Analisador.Token> expressao) {
        if (indDebbug) {
            System.out.println("/******************************/");
            System.out.println(tipoExpressao);
            for (Analisador.Token token : expressao) {
                System.out.println("Lexema: " + token.sLexema + " - Simbolo: " + token.sSimbolo);
            }
            System.out.println("/******************************/");
        }
    }

    public static void PrintToken(String sLexema, String sSimbolo) {
        if (indDebbug) {
            System.out.println(sLexema + " - " + sSimbolo);
        }
    }

    public static void printarTabelaSimbolosFinal(ArrayList<Simbolo> lListaSimbolos) {
        if (indDebbug) {
            for (Simbolo simbolo : lListaSimbolos) {
                System.out.println("'" + simbolo.identificador + "' '" + simbolo.tipo + "' '" + simbolo.nivel + "' '" + simbolo.rotulo + "' '" + simbolo.endereco + "'\n");
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import utilidades.Log;
import java.util.ArrayList;

/**
 *
 * @author Vitor Rodrigo Vezani && Pedro Henrique Grandin
 *
 */
public class AnalisadorLexico extends Analisador {

    private final String sCodigoFonte;
    private Character sCurrentChar;
    private int nCurrPosition;
    private ArrayList<Token> lTokenList;
    private String sLexema, sSimbolo;
    private int nLinha;
    private int nLinhaAbreParenteses;

    public AnalisadorLexico(String CodigoFonte) {
        this.nLinha = 1;
        this.lTokenList = new ArrayList<>();
        this.sCodigoFonte = CodigoFonte;
        this.nCurrPosition = 0;
        lerCaracter();
    }

    public Token getNextToken() throws AnaliseException {
        while (((sCurrentChar == '{') || (sCurrentChar == ' ') || (sCurrentChar == '\t') || (sCurrentChar == '\n')) && (nCurrPosition <= sCodigoFonte.length())) {
            if (sCurrentChar == '{') {
                // Guarda o numero que abriu o ultimo parenteses
                nLinhaAbreParenteses = getnLinha();
                while (sCurrentChar != '}' && nCurrPosition <= sCodigoFonte.length()) {
                    if (sCurrentChar != '}' && nCurrPosition == sCodigoFonte.length()) {
                        raiseError("Erro1", nLinha , "Linha: " + nLinhaAbreParenteses + " - Caracter '}' experado!");
                    } else {
                        lerCaracter();
                    }
                }
                lerCaracter();
            }
            while ((sCurrentChar == ' ' || (sCurrentChar == '\t') || (sCurrentChar == '\n')) && nCurrPosition <= sCodigoFonte.length()) {
                lerCaracter();
            }
        }

        if (sCurrentChar != null && sCurrentChar != ' ' && sCurrentChar != '\t' && sCurrentChar != '\n') {
            pegaToken();
            insereLista();
            Log.PrintToken(sLexema, sSimbolo);
            return new Token(sLexema, sSimbolo);
        }
        return null;
    }

    private void lerCaracter() {

        if (nCurrPosition < sCodigoFonte.length()) {
            sCurrentChar = sCodigoFonte.charAt(nCurrPosition);

            if (sCurrentChar == '\n') {
                nLinha++;
            }
        }

        nCurrPosition++;
    }

    private void pegaToken() throws AnaliseException {
        if (Character.isDigit(sCurrentChar)) {
            tratarDigito();
        } else if (Character.isLetter(sCurrentChar)) {
            trataIdentificador();
        } else if (sCurrentChar == ':') {
            trataAtribuicao();
        } else if (sCurrentChar == '+' || sCurrentChar == '-' || sCurrentChar == '*') {
            trataOperadorAritmetico();
        } else if (sCurrentChar == '>' || sCurrentChar == '<' || sCurrentChar == '=' || sCurrentChar == '!') {
            trataOperadorRelacional();
        } else if (sCurrentChar == ';' || sCurrentChar == ',' || sCurrentChar == '(' || sCurrentChar == ')' || sCurrentChar == '.') {
            trataPontuacao();
        } else {
            raiseError("Erro3", nLinha , "Linha: " + getnLinha() + " - Caracter '" + sCurrentChar + "' inválido!");
        }
    }

    private void insereLista() {
        lTokenList.add(new Token(sLexema, sSimbolo));
    }

    private void tratarDigito() {
        String num;

        num = sCurrentChar.toString();
        lerCaracter();

        while (Character.isDigit(sCurrentChar) && nCurrPosition <= sCodigoFonte.length()) {
            num = num + sCurrentChar.toString();
            lerCaracter();
        }

        sLexema = num;
        sSimbolo = "snumero";

    }

    private void trataIdentificador() {
        String id;

        id = sCurrentChar.toString();
        lerCaracter();

        while ((Character.isLetterOrDigit(sCurrentChar) || sCurrentChar == '_') && nCurrPosition <= sCodigoFonte.length()) {
            id = id + sCurrentChar.toString();
            lerCaracter();
        }

        sLexema = id;

        switch (id) {
            case "programa":
                sSimbolo = "sprograma";
                break;
            case "se":
                sSimbolo = "sse";
                break;
            case "entao":
                sSimbolo = "sentao";
                break;
            case "senao":
                sSimbolo = "ssenao";
                break;
            case "enquanto":
                sSimbolo = "senquanto";
                break;
            case "faca":
                sSimbolo = "sfaca";
                break;
            case "inicio":
                sSimbolo = "sinicio";
                break;
            case "fim":
                sSimbolo = "sfim";
                break;
            case "escreva":
                sSimbolo = "sescreva";
                break;
            case "leia":
                sSimbolo = "sleia";
                break;
            case "var":
                sSimbolo = "svar";
                break;
            case "inteiro":
                sSimbolo = "sinteiro";
                break;
            case "booleano":
                sSimbolo = "sbooleano";
                break;
            case "verdadeiro":
                sSimbolo = "sverdadeiro";
                break;
            case "falso":
                sSimbolo = "sfalso";
                break;
            case "procedimento":
                sSimbolo = "sprocedimento";
                break;
            case "funcao":
                sSimbolo = "sfuncao";
                break;
            case "div":
                sSimbolo = "sdiv";
                break;
            case "e":
                sSimbolo = "se";
                break;
            case "ou":
                sSimbolo = "sou";
                break;
            case "nao":
                sSimbolo = "snao";
                break;
            default:
                sSimbolo = "sidentificador";
        }
    }

    private void trataAtribuicao() {
        String id;

        id = sCurrentChar.toString();
        lerCaracter();
        if (sCurrentChar == '=') {
            id = id + sCurrentChar.toString();
            sSimbolo = "satribuicao";
            lerCaracter();
        } else {
            sSimbolo = "sdoispontos";
        }
        sLexema = id;
    }

    private void trataOperadorAritmetico() {
        if (sCurrentChar == '+') {
            sSimbolo = "smais";
        } else if (sCurrentChar == '-') {
            sSimbolo = "smenos";
        } else if (sCurrentChar == '*') {
            sSimbolo = "smult";
        }

        sLexema = sCurrentChar.toString();
        lerCaracter();
    }

    private void trataOperadorRelacional() throws AnaliseException {
        String id;

        id = sCurrentChar.toString();

        if (sCurrentChar == '=') {
            sSimbolo = "sig";
            lerCaracter();
        } else if (sCurrentChar == '>') {
            lerCaracter();
            if (sCurrentChar == '=') {
                sSimbolo = "smaiorig";
                id = id + sCurrentChar.toString();
                lerCaracter();
            } else {
                sSimbolo = "smaior";
            }
        } else if (sCurrentChar == '<') {
            lerCaracter();
            if (sCurrentChar == '=') {
                sSimbolo = "smenorig";
                id = id + sCurrentChar.toString();
                lerCaracter();
            } else {
                sSimbolo = "smenor";
            }
        } else if (sCurrentChar == '!') {
            lerCaracter();
            if (sCurrentChar == '=') {
                sSimbolo = "sdif";
                id = id + sCurrentChar.toString();
                lerCaracter();
            } else {
                raiseError("Erro4", nLinha , "Linha: " + getnLinha() + " - Caracter '" + sCurrentChar + "' inválido!");
            }
        }

        sLexema = id;
    }

    private void trataPontuacao() {
        if (sCurrentChar == '(') {
            sSimbolo = "sabre_parenteses";
        } else if (sCurrentChar == ')') {
            sSimbolo = "sfecha_parenteses";
        } else if (sCurrentChar == ';') {
            sSimbolo = "sponto_virgula";
        } else if (sCurrentChar == ',') {
            sSimbolo = "svirgula";
        } else if (sCurrentChar == '.') {
            sSimbolo = "sponto";
        }

        sLexema = sCurrentChar.toString();
        lerCaracter();
    }

    /**
     * @return the nLinha
     */
    public int getnLinha() {
        return nLinha;
    }
    
    private void raiseError(String numErro,int nLinha ,String msgErro) throws AnaliseException {
    System.out.println(numErro);
    throw new AnaliseException(msgErro, nLinha);
    }
}
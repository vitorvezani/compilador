/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import compilador.Analisador.Token;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 *
 * @author Vitor Rodrigo Vezani && Pedro Henrique Grandin
 */
public class AnalisadorSemantico {

    private static ArrayList<Simbolo> lListaSimbolos;

    /**
     * @return the lListaSimbolos
     */
    public static ArrayList<Simbolo> getlListaSimbolos() {
        return lListaSimbolos;
    }

    public AnalisadorSemantico() {
        lListaSimbolos = new ArrayList<>();
    }

    void insereTabela(String lexema, String tipo, int currAd) {
        lListaSimbolos.add(new Simbolo(lexema, tipo, currAd));
    }

    void insereTabela(String lexema, String tipo) {
        lListaSimbolos.add(new Simbolo(lexema, tipo));
    }

    void insereTabela(String lexema, String tipo, char nivel, int rotulo) {
        lListaSimbolos.add(new Simbolo(lexema, tipo, nivel, rotulo));
    }
    
    void insereTabela(String lexema, String tipo, char nivel, int rotulo, int endereco) {
        lListaSimbolos.add(new Simbolo(lexema, tipo, nivel, rotulo, endereco));
    }

    boolean pesquisaDeclVarFuncTabela(String lexema) {
        for (Simbolo simbolo : lListaSimbolos) {
            if (simbolo.identificador.equals(lexema) && (simbolo.tipo.equals("inteiro") || simbolo.tipo.equals("funcao inteiro"))) {
                return true;
            }
        }
        return false;
    }

    boolean pesquisaDeclProcTabela(String lexema) {
        for (Simbolo simbolo : lListaSimbolos) {
            if (simbolo.identificador.equals(lexema)) {
                return true;
            }
        }
        return false;
    }

    boolean pesquisaDeclFuncTabela(String lexema) {
        for (Simbolo simbolo : lListaSimbolos) {
            if (simbolo.identificador.equals(lexema)) {
                return true;
            }
        }
        return false;
    }

    boolean pesquisaDuplicVarTabela(String lexema) {

        // Pesquisa por variavel duplicada no nivel.
        int i = lListaSimbolos.size() - 1;

        while (i >= 0 && lListaSimbolos.get(i).nivel != 'L') {

            if (lListaSimbolos.get(i).identificador.equals(lexema)) {
                return true;
            }

            i--;
        }

        //Pesquisa por identificador duplicado na tabela, que não seja do tipo variavel.
        for (Simbolo simbolo : lListaSimbolos) {
            if (simbolo.identificador.equals(lexema) && (simbolo.tipo.equals("procedimento") || simbolo.tipo.equals("nomedeprograma") || simbolo.tipo.equals("funcao inteiro") || simbolo.tipo.equals("funcao booleano"))) {
                return true;
            }
        }

        return false;
    }

    void colocaTipoTabela(String tipo) {

        int i = lListaSimbolos.size() - 1;

        while ("variavel".equals(lListaSimbolos.get(i).tipo)) {

            lListaSimbolos.get(i).tipo = tipo;

            i--;
        }
    }

    boolean pesquisaDeclVarTabela(String lexema) {
        for (Simbolo simbolo : lListaSimbolos) {
            if (simbolo.identificador.equals(lexema) && simbolo.tipo.equals("inteiro")) {
                return true;
            }
        }
        return false;
    }

    boolean pesquisaDeclTabela(String lexema) {
        for (Simbolo simbolo : lListaSimbolos) {
            if (simbolo.identificador.equals(lexema) && !simbolo.tipo.equals("nomedeprograma")) {
                return true;
            }
        }
        return false;
    }

    void desempilha() {
        int i = lListaSimbolos.size() - 1;

        while (lListaSimbolos.get(i).nivel != 'L') {
            lListaSimbolos.remove(i);
            i--;
        }

        lListaSimbolos.get(i).nivel = 'U';

    }

    void insereTipo(String tipo) {
        lListaSimbolos.get(lListaSimbolos.size() - 1).tipo = tipo;
    }

    Integer pesquisaTabela(String lexema) {
        for (int i = lListaSimbolos.size() - 1; i >= 0; i--) {
            if (lListaSimbolos.get(i).identificador.equals(lexema)) {
                return i;
            }
        }
        return null;
    }

    Integer pesquisaEndereco(String lexema) {
        for (int i = lListaSimbolos.size() - 1; i >= 0; i--) {
            if (lListaSimbolos.get(i).identificador.equals(lexema)) {
                    return lListaSimbolos.get(i).endereco;
            }
        }
        return null;
    }

    public Simbolo buscarSimbolo(String lexema) {
        for (Simbolo simbolo : lListaSimbolos) {
            if (simbolo.identificador.equals(lexema)) {
                return simbolo;
            }
        }
        return null;
    }

    Integer pesquisaRotulo(String lexema) {
        for (int i = lListaSimbolos.size() - 1; i >= 0; i--) {
            if (lListaSimbolos.get(i).identificador.equals(lexema)) {
                return lListaSimbolos.get(i).rotulo;
            }
        }
        return null;
    }

    String retornaTipoFuncao(Integer index) {
        return lListaSimbolos.get(index).tipo;
    }

    ArrayList<Token> posOrdem(ArrayList<Token> expressao) {

        ArrayList<Token> expressaoPosOrder = new ArrayList();
        Stack<Token> pilha = new Stack();
        Token tToken;
        HashMap<String, Integer> prioridades = setPrioridadesOperadores();

        for (int i = 0; i < expressao.size(); i++) {

            tToken = expressao.get(i);

            if (AnalisadorSintatico.isOperando(tToken.sSimbolo)) {
                expressaoPosOrder.add(tToken);
            } else if (AnalisadorSintatico.isOperador(tToken.sSimbolo)) {
                while (!pilha.empty() && prioridades.get(pilha.peek().sSimbolo) > prioridades.get(tToken.sSimbolo)) {
                    expressaoPosOrder.add(pilha.pop());
                }
                pilha.push(tToken);
            } else if ("sabre_parenteses".equals(tToken.sSimbolo)) {
                pilha.push(tToken);
            } else if ("sfecha_parenteses".equals(tToken.sSimbolo)) {
                while (!"sabre_parenteses".equals(pilha.peek().sSimbolo)) {
                    expressaoPosOrder.add(pilha.pop());
                }
                pilha.pop();
            }

        }

        while (!pilha.empty()) {
            expressaoPosOrder.add(pilha.pop());
        }

        return expressaoPosOrder;
    }

    public void analisaExpressao(Simbolo sinSimbolo, int nLinha, ArrayList<Token> expressao) throws AnaliseException {

        ArrayList<String> tipos = new ArrayList<>();

        for (Token tToken : expressao) {
            if (AnalisadorSintatico.isOperador(tToken.sSimbolo)) {
                if ("snao".equals(tToken.sSimbolo) || "spositivo".equals(tToken.sSimbolo) || "snegativo".equals(tToken.sSimbolo)) {
                    continue;
                }
                if ("se".equals(tToken.sSimbolo) || "sou".equals(tToken.sSimbolo)) //RECEBE BOOLEANOS
                {
                    if (!"booleano".equals(tipos.get(tipos.size() - 1)) || !"booleano".equals(tipos.get(tipos.size() - 2))) {
                        raiseError("Erro37", nLinha ,"Linha: " + nLinha + " - Ambos identificadores/constantes devem ser do mesmo tipo.");
                    }
                    tipos.remove(tipos.size() - 1);
                }

                if ("smaior".equals(tToken.sSimbolo)
                        || "smaiorig".equals(tToken.sSimbolo)
                        || "smenor".equals(tToken.sSimbolo)
                        || "smenorig".equals(tToken.sSimbolo)
                        || "sig".equals(tToken.sSimbolo)
                        || "sdif".equals(tToken.sSimbolo)) //RECEBE 2 INTEIROS
                {
                    if (!"inteiro".equals(tipos.get(tipos.size() - 1)) || !"inteiro".equals(tipos.get(tipos.size() - 2))) {
                        raiseError("Erro38",nLinha,"Linha: " +nLinha+ " - Ambos identificadores/constantes devem ser do mesmo tipo.");
                    }

                    tipos.remove(tipos.size() - 1);
                    tipos.remove(tipos.size() - 1);
                    tipos.add("booleano");
                }

                if ("smais".equals(tToken.sSimbolo)
                        || "smenos".equals(tToken.sSimbolo)
                        || "smult".equals(tToken.sSimbolo)
                        || "sdiv".equals(tToken.sSimbolo)) {
                    if (!"inteiro".equals(tipos.get(tipos.size() - 1)) || !"inteiro".equals(tipos.get(tipos.size() - 2))) {
                        raiseError("Erro39",nLinha,"Linha: " +nLinha+ " - Ambos identificadores/constantes devem ser do mesmo tipo.");
                    }

                    tipos.remove(tipos.size() - 1); //GERA INTEIROS
                }

            } else {
                if ("snumero".equals(tToken.sSimbolo)) {
                    tipos.add("inteiro");
                }
                if ("sidentificador".equals(tToken.sSimbolo)) {
                    Simbolo sSimbolo;
                    sSimbolo = buscarSimbolo(tToken.sLexema);
                    if ("funcao inteiro".equals(sSimbolo.tipo) || "inteiro".equals(sSimbolo.tipo)) {
                        tipos.add("inteiro");
                    } else if ("funcao booleano".equals(sSimbolo.tipo) || "booleano".equals(sSimbolo.tipo)) {
                        tipos.add("booleano");
                    }
                }
                if ("sverdadeiro".equals(tToken.sSimbolo) || "sfalso".equals(tToken.sSimbolo)) {
                    tipos.add("booleano");
                }
            }
        }

        if ("funcao inteiro".equals(sinSimbolo.tipo) || "inteiro".equals(sinSimbolo.tipo)) {
            if (!"inteiro".equals(tipos.get(0))) {
                raiseError("Erro40", nLinha, "Linha: " +nLinha+ " - expressao de tipo incompativel - Retorno inteiro esperado.");
            }
        } else if ("funcao booleano".equals(sinSimbolo.tipo) || "booleano".equals(sinSimbolo.tipo)) {
            if (!"booleano".equals(tipos.get(0))) {
                raiseError("Erro41",nLinha, "Linha: " +nLinha+ " - expressao de tipo incompativel - Retorno booleano esperado.");
            }
        }
    }

    private HashMap setPrioridadesOperadores() {

        HashMap<String, Integer> prioridades = new HashMap<>();

        //the higher the better
        prioridades.put("sabre_parenteses", 0);
        prioridades.put("sou", 1);
        prioridades.put("se", 2);
        prioridades.put("smaior", 3);
        prioridades.put("smaiorig", 3);
        prioridades.put("smenor", 3);
        prioridades.put("smenorig", 3);
        prioridades.put("sig", 3);
        prioridades.put("sdif", 3);
        prioridades.put("smais", 4);
        prioridades.put("smenos", 4);
        prioridades.put("smult", 5);
        prioridades.put("sdiv", 5);
        prioridades.put("snao", 6);
        prioridades.put("snegativo", 6);
        prioridades.put("spositivo", 6);

        return prioridades;
    }

    private void raiseError(String numErro, int nLinha, String msgErro) throws AnaliseException {
        System.out.println(numErro);
        throw new AnaliseException(msgErro, nLinha);
    }
}
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
public class AnalisadorSintatico extends Analisador {
    
    private Token tToken;

    private String sVarAt;

    private ArrayList<Integer> lVarNumList;
    private ArrayList<Token> lExpressao;

    private AnalisadorSemantico analiseSemantica;
    private AnalisadorLexico analiseLexica;
    private GeradorCodigo geraCodigo;

    private int nRotulo, nVarNum, i, nCurrAd, nEndereco;

    private char nivel;

    public AnalisadorSintatico(String sCodigoFonte) {
        analiseLexica = new AnalisadorLexico(sCodigoFonte);
        analiseSemantica = new AnalisadorSemantico();
        geraCodigo = new GeradorCodigo();
        lExpressao = new ArrayList<>();
        lVarNumList = new ArrayList<>();
        nCurrAd = 0;
        nEndereco = 0;
    }

    public void analiseSintatica() throws AnaliseException {
        nRotulo = 1;
        tToken = analiseLexica.getNextToken();

        if ("sprograma".equals(tToken.sSimbolo)) {

            geraCodigo.gera("START");

            tToken = analiseLexica.getNextToken();
            if ("sidentificador".equals(tToken.sSimbolo)) {
                analiseSemantica.insereTabela(tToken.sLexema, "nomedeprograma");
                tToken = analiseLexica.getNextToken();
                if ("sponto_virgula".equals(tToken.sSimbolo)) {
                    analisaBloco();
                    if (tToken != null && "sponto".equals(tToken.sSimbolo)) {
                        if (true) {
                            
                            for (i = lVarNumList.size() - 1; lVarNumList.size() > 0; i--) {

                                if (lVarNumList.get(i) != -1) {
                                    geraCodigo.gera("DALLOC", (nCurrAd - lVarNumList.get(i)) + "", lVarNumList.get(i) + "");
                                    nCurrAd = nCurrAd - lVarNumList.get(i);
                                    nEndereco = nEndereco - lVarNumList.get(i);

                                }
                                lVarNumList.remove(i);
                            }

                            geraCodigo.gera("HLT");
                            
                            tToken = analiseLexica.getNextToken();
                            
                            if (tToken != null && tToken.sSimbolo != "sponto") {

                                raiseError("Erro36", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Código após final de programa");
                            }

                            geraCodigo.criaFile();

                        } else {
                            raiseError("Erro1", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sponto' esperado");
                        }
                    } else {
                        raiseError("Erro2", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sponto' esperado");
                    }
                } else {
                    raiseError("Erro3", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sponto_virgula' esperado");
                }
            } else {
                raiseError("Erro4", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sidentificador' esperado");
            }
        } else {
            raiseError("Erro5", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sprograma' esperado");
        }
    }

    private void analisaBloco() throws AnaliseException {
        tToken = analiseLexica.getNextToken();

        analisaEtVariaveis();
        analisaSubrotinas();
        analisaComandos();

    }

    private void analisaEtVariaveis() throws AnaliseException {
        if (("svar").equals(tToken.sSimbolo)) {
            lVarNumList.add(-1);
            tToken = analiseLexica.getNextToken();
            if (("sidentificador").equals(tToken.sSimbolo)) {
                while (("sidentificador").equals(tToken.sSimbolo)) {
                    analisaVariaveis();
                    if (("sponto_virgula").equals(tToken.sSimbolo)) {
                        tToken = analiseLexica.getNextToken();
                    } else {
                        raiseError("Erro6", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sponto_virgula' esperado");
                    }
                }
                
                geraCodigo.gera("ALLOC", nCurrAd + "", nVarNum + "");
                lVarNumList.add(nVarNum);
                nCurrAd += nVarNum;
                nVarNum = 0;
                
            } else {
                raiseError("Erro7", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sidentificador' esperado");
            }
        }
    }

    private void analisaVariaveis() throws AnaliseException {
        do {
            if ("sidentificador".equals(tToken.sSimbolo)) {
                if (!analiseSemantica.pesquisaDuplicVarTabela(tToken.sLexema)) { // Se não encontrou na tabela
                    analiseSemantica.insereTabela(tToken.sLexema, "variavel", nEndereco);
                    tToken = analiseLexica.getNextToken();
                    nVarNum++;
                    nEndereco++;
                    if ("svirgula".equals(tToken.sSimbolo) || "sdoispontos".equals(tToken.sSimbolo)) {
                        if ("svirgula".equals(tToken.sSimbolo)) {
                            tToken = analiseLexica.getNextToken();
                            if ("sdoispontos".equals(tToken.sSimbolo)) {
                                raiseError("Erro8", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sidentificador' esperado");
                            }
                        }
                    } else {
                        raiseError("Erro9", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'svirgula' ou 'sdoispontos' esperado");
                    }
                } else {
                    raiseError("Erro10", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo " + tToken.sLexema + " duplicado!");
                }
            } else {
                raiseError("Erro11", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sidentificador' esperado");
            }
        } while (!"sdoispontos".equals(tToken.sSimbolo));

        tToken = analiseLexica.getNextToken();
        analisaTipo();
    }

    private void analisaTipo() throws AnaliseException {
        if (!"sinteiro".equals(tToken.sSimbolo) && !"sbooleano".equals(tToken.sSimbolo)) {
            raiseError("Erro12", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sinteiro' ou 'sbooleano' esperado");
        } else {
            analiseSemantica.colocaTipoTabela(tToken.sLexema);
            tToken = analiseLexica.getNextToken();
        }
    }

    private void analisaComandos() throws AnaliseException {
        if ("sinicio".equals(tToken.sSimbolo)) {
            tToken = analiseLexica.getNextToken();
            analisaComandoSimples();
            while (!"sfim".equals(tToken.sSimbolo)) {
                if ("sponto_virgula".equals(tToken.sSimbolo)) {
                    tToken = analiseLexica.getNextToken();
                    if (!"sfim".equals(tToken.sSimbolo)) {
                        analisaComandoSimples();
                    }
                } else {
                    raiseError("Erro13", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sponto_virgula' esperado");
                }
            }
            tToken = analiseLexica.getNextToken();
        } else {
            raiseError("Erro14", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sinicio' esperado");
        }
    }

    private void analisaComandoSimples() throws AnaliseException {
        switch (tToken.sSimbolo) {
            case "sidentificador":
                if (analiseSemantica.pesquisaDeclTabela(tToken.sLexema)) {
                    sVarAt = tToken.sLexema;
                    analisaAtribChprocedimento();
                } else {
                    raiseError("Erro15", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo '" + tToken.sLexema + "' não declarado!");
                }
                break;
            case "sse":
                analisaSe();
                break;
            case "senquanto":
                analisaEnquanto();
                break;
            case "sleia":
                analisaLeia();
                break;
            case "sescreva":
                analisaEscreva();
                break;
            default:
                analisaComandos();
                break;
        }
    }

    private void analisaAtribChprocedimento() throws AnaliseException {
        Simbolo sSimbolo = analiseSemantica.buscarSimbolo(tToken.sLexema);
        tToken = analiseLexica.getNextToken();
        if ("satribuicao".equals(tToken.sSimbolo)) {
            analisaAtribuicao(sSimbolo);
        } else {
            chamadaProcedimento();
        }
    }

    private void analisaLeia() throws AnaliseException {
        tToken = analiseLexica.getNextToken();

        if ("sabre_parenteses".equals(tToken.sSimbolo)) {
            tToken = analiseLexica.getNextToken();
            if ("sidentificador".equals(tToken.sSimbolo)) {
                if (analiseSemantica.pesquisaDeclVarTabela(tToken.sLexema)) {

                    geraCodigo.gera("RD");
                    
                     if (analiseSemantica.pesquisaRotulo(tToken.sLexema) == null)  //se nao for funcao
                            geraCodigo.gera("STR", analiseSemantica.pesquisaEndereco(tToken.sLexema) + "");
                     else
                            geraCodigo.gera("CALL", "L" + analiseSemantica.pesquisaRotulo(tToken.sLexema) + "");

                    tToken = analiseLexica.getNextToken();
                    if ("sfecha_parenteses".equals(tToken.sSimbolo)) {
                        tToken = analiseLexica.getNextToken();
                    } else {
                        raiseError("Erro16", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sfecha_parenteses' esperado");
                    }
                } else {
                    raiseError("Erro17", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + "Identificador não foi encontrado ou não é função ou variavel do tipo inteiro");
                }
            } else {
                raiseError("Erro18", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sidentificador' esperado");
            }
        } else {
            raiseError("Erro19", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sabre_parenteses' esperado");
        }
    }

    private void analisaEscreva() throws AnaliseException {
        tToken = analiseLexica.getNextToken();
        if ("sabre_parenteses".equals(tToken.sSimbolo)) {
            tToken = analiseLexica.getNextToken();
            if ("sidentificador".equals(tToken.sSimbolo)) {
                if (analiseSemantica.pesquisaDeclVarFuncTabela(tToken.sLexema)) {
                    //TODO - Verificar geracao de código
                   
                    if (analiseSemantica.pesquisaRotulo(tToken.sLexema) == null)  //se nao for funcao
                            geraCodigo.gera("LDV", analiseSemantica.pesquisaEndereco(tToken.sLexema) + "");
                    else
                            geraCodigo.gera("CALL", "L" + analiseSemantica.pesquisaRotulo(tToken.sLexema) + "");

                    geraCodigo.gera("PRN");

                    tToken = analiseLexica.getNextToken();

                    if ("sfecha_parenteses".equals(tToken.sSimbolo)) {
                        tToken = analiseLexica.getNextToken();
                    } else {
                        raiseError("Erro20", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sfecha_parenteses' esperado");
                    }
                } else {
                    raiseError("Erro21", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + "Identificador " + tToken.sLexema + " não declarado \nou diferente de funcao ou variavel inteiro");
                }
            }
        } else {
            raiseError("Erro22", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sabre_parenteses' esperado");
        }
    }

    private void analisaEnquanto() throws AnaliseException {
        int auxrot1, auxrot2;
        auxrot1 = nRotulo;

        geraCodigo.geraR("L" + nRotulo);
        nRotulo++;
        tToken = analiseLexica.getNextToken();

        analisaExpressao();
        substituiUnarios();

        Log.exibeExpressao("Expressão Normal:", lExpressao);
        lExpressao = analiseSemantica.posOrdem(lExpressao);

        Log.exibeExpressao("Expressão PosOrdem:", lExpressao);
        analiseSemantica.analisaExpressao(new Simbolo("operEnquanto", "booleano"), analiseLexica.getnLinha(), lExpressao); //Para comando enquanto deve retornar booleano

        geraCodigo.geraExpressao(lExpressao, analiseSemantica);

        lExpressao.removeAll(lExpressao);

        if ("sfaca".equals(tToken.sSimbolo)) {
            auxrot2 = nRotulo;
            //Gera("",JMPF,rotulo,"") //salta se falso
            geraCodigo.gera("JMPF", "L" + nRotulo);
            nRotulo++;
            tToken = analiseLexica.getNextToken();
            analisaComandoSimples();
            //Gera("",JMP,auxrot1,"") {retorna início loop}
            geraCodigo.gera("JMP", "L" + auxrot1);
            //Gera(auxrot2,NULL,"","") {fim do while}
            geraCodigo.geraR("L" + auxrot2);
        } else {
            raiseError("Erro23", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sfaca' esperado");
        }
    }

    private void analisaSe() throws AnaliseException {

        int auxRot = nRotulo;
        
        tToken = analiseLexica.getNextToken();
        analisaExpressao();

        substituiUnarios();

        //exibeExpressao( "Expressão Normal:" );
        lExpressao = analiseSemantica.posOrdem(lExpressao);

        //exibeExpressao( "Expressão PosOrdem:" );
        analiseSemantica.analisaExpressao(new Simbolo("operSe", "booleano"), analiseLexica.getnLinha(), lExpressao); //analise semantica da expressao deve dar booleano

        geraCodigo.geraExpressao(lExpressao, analiseSemantica);

        lExpressao.removeAll(lExpressao);

        geraCodigo.gera("JMPF", "L" + nRotulo);
        nRotulo++;

        if ("sentao".equals(tToken.sSimbolo)) {
            tToken = analiseLexica.getNextToken();
            analisaComandoSimples();

            geraCodigo.gera("JMP", "L" + nRotulo);
            nRotulo++;

            if ("ssenao".equals(tToken.sSimbolo)) {
                //GERA(L2, NULL)
                geraCodigo.geraR("L" + auxRot);

                tToken = analiseLexica.getNextToken();
                analisaComandoSimples();
            }
            else{
                geraCodigo.geraR("L" + auxRot);
            }
            
        } else {
            raiseError("Erro24", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sentao' esperado");
        }

        //GERA(L1, NULL)
        geraCodigo.geraR("L" + (nRotulo-1));
    }

    private void analisaSubrotinas() throws AnaliseException {
        int auxrot = 0, flag;
        flag = 0;
        if ("sprocedimento".equals(tToken.sSimbolo) || "sfuncao".equals(tToken.sSimbolo)) {
            auxrot = nRotulo;
            //GERA(´ ´,JMP,rotulo,´ ´) //Salta sub-rotinas
            geraCodigo.gera("JMP", "L" + nRotulo);
            nRotulo++;
            flag = 1;
        }
        while ("sprocedimento".equals(tToken.sSimbolo) || "sfuncao".equals(tToken.sSimbolo)) {
            if ("sprocedimento".equals(tToken.sSimbolo)) {
                analisaDeclaracaoProcedimento();
            } else {
                analisaDeclaracaoFuncao();
            }
            if ("sponto_virgula".equals(tToken.sSimbolo)) {
                tToken = analiseLexica.getNextToken();
            } else {
                raiseError("Erro25", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sponto_virgula' esperado");
            }
        }
        if (flag == 1) {
            //Gera(auxrot,NULL,´ ´,´ ´) //início do principal
            geraCodigo.geraR("L" + auxrot);
        }
    }

    private void analisaDeclaracaoProcedimento() throws AnaliseException {
        tToken = analiseLexica.getNextToken();
        nivel = 'L'; //marca ou novo galho
        lVarNumList.add(-2);
        if ("sidentificador".equals(tToken.sSimbolo)) {
            if (!analiseSemantica.pesquisaDeclProcTabela(tToken.sLexema)) { // não encontrou
                analiseSemantica.insereTabela(tToken.sLexema, "procedimento", nivel, nRotulo); //guarda na TabSimb
                //Gera(rotulo,NULL,´ ´,´ ´) //CALL irá buscar este rótulo na TabSimb
                geraCodigo.geraR("L" + nRotulo);
                nRotulo++;
                tToken = analiseLexica.getNextToken();
                if ("sponto_virgula".equals(tToken.sSimbolo)) {
                    analisaBloco();

                    i = lVarNumList.size() - 1;
                    if ( lVarNumList.get(i) == -2){
                        lVarNumList.remove(i);
                    }
                    else  {
                        geraCodigo.gera("DALLOC", (nCurrAd - lVarNumList.get(i)) + "", lVarNumList.get(i) + "");
                        nCurrAd = nCurrAd - lVarNumList.get(i);
                        nEndereco = nEndereco - lVarNumList.get(i);
                        lVarNumList.remove(i); i--;
                        lVarNumList.remove(i); i--;
                        lVarNumList.remove(i);
                    }
                    
                    geraCodigo.geraReturn();
                    
                } else {
                    raiseError("Erro26", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sponto_virgula' esperado");
                }
            } else {
                raiseError("Erro27", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + "Identificador '" + tToken.sLexema + "' já foi declarado");
            }
        } else {
            raiseError("Erro28", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sidentificador' esperado");
        }
        analiseSemantica.desempilha();
    }

    private void analisaDeclaracaoFuncao() throws AnaliseException {
        tToken = analiseLexica.getNextToken();
        nivel = 'L'; //marca um novo galho
        lVarNumList.add(-2);
        if ("sidentificador".equals(tToken.sSimbolo)) {
            if (!analiseSemantica.pesquisaDeclFuncTabela(tToken.sLexema)) {
                analiseSemantica.insereTabela(tToken.sLexema, "funcao", nivel, nRotulo);
                geraCodigo.geraR("L" + nRotulo);
                nRotulo++;
                tToken = analiseLexica.getNextToken();
                if ("sdoispontos".equals(tToken.sSimbolo)) {
                    tToken = analiseLexica.getNextToken();
                    if ("sinteiro".equals(tToken.sSimbolo) || "sbooleano".equals(tToken.sSimbolo)) {
                        if ("sinteiro".equals(tToken.sSimbolo)) {
                            analiseSemantica.insereTipo("funcao inteiro");
                        } else {
                            analiseSemantica.insereTipo("funcao booleano");
                        }
                        tToken = analiseLexica.getNextToken();
                        if ("sponto_virgula".equals(tToken.sSimbolo)) {
                            analisaBloco();

                            i = lVarNumList.size() - 1;
                            if ( lVarNumList.get(i) == -2){
                                geraCodigo.geraReturnF();
                                lVarNumList.remove(i);
                            }
                            else  {
                                
                                geraCodigo.geraReturnF((nCurrAd - lVarNumList.get(i)), lVarNumList.get(i));
                                nCurrAd = nCurrAd - lVarNumList.get(i);
                                nEndereco = nEndereco - lVarNumList.get(i);
                                lVarNumList.remove(i); i--;
                                lVarNumList.remove(i); i--;
                                lVarNumList.remove(i);
                            }

                            
                        }
                    } else {
                        raiseError("Erro29", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sinteiro' ou 'sbooleano' esperado");
                    }
                } else {
                    raiseError("Erro30", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sdoispontos' esperado");
                }
            } else {
                raiseError("Erro31", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Identificaor '" + tToken.sLexema + "' já declarado!");
            }
        } else {
            raiseError("Erro32", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sidentificador' esperado");
        }
        analiseSemantica.desempilha();
    }

    private void analisaExpressao() throws AnaliseException {

        analisaExpressaoSimples();
        if ("smaior".equals(tToken.sSimbolo) || "smaiorig".equals(tToken.sSimbolo) || "sig".equals(tToken.sSimbolo) || "smenor".equals(tToken.sSimbolo) || "smenorig".equals(tToken.sSimbolo) || "sdif".equals(tToken.sSimbolo)) {
            lExpressao.add(tToken);
            tToken = analiseLexica.getNextToken();
            analisaExpressaoSimples();
        }
    }

    private void analisaExpressaoSimples() throws AnaliseException {
        if ("smais".equals(tToken.sSimbolo) || "smenos".equals(tToken.sSimbolo)) {
            lExpressao.add(tToken);
            tToken = analiseLexica.getNextToken();
        }
        analisaTermo();
        while ("smais".equals(tToken.sSimbolo) || "smenos".equals(tToken.sSimbolo) || "sou".equals(tToken.sSimbolo)) {
            lExpressao.add(tToken);
            tToken = analiseLexica.getNextToken();
            analisaTermo();
        }
    }

    private void analisaTermo() throws AnaliseException {
        analisaFator();
        while ("smult".equals(tToken.sSimbolo) || "sdiv".equals(tToken.sSimbolo) || "se".equals(tToken.sSimbolo)) {
            lExpressao.add(tToken);
            tToken = analiseLexica.getNextToken();
            analisaFator();
        }
    }

    private void analisaFator() throws AnaliseException {
        Integer index;

        if ("sidentificador".equals(tToken.sSimbolo)) {        //Variável ou Função
            index = analiseSemantica.pesquisaTabela(tToken.sLexema);
            if (index != null) {
                if (("funcao inteiro".equals(analiseSemantica.retornaTipoFuncao(index))) || ("funcao booleano".equals(analiseSemantica.retornaTipoFuncao(index)))) {
                    lExpressao.add(tToken);
                    sVarAt = tToken.sLexema;
                    analisaChamadaFuncao();
                } else {
                    lExpressao.add(tToken);
                    tToken = analiseLexica.getNextToken();
                }
            } else {
                raiseError("Erro33", analiseLexica.getnLinha(), "Linha: " + analiseLexica.getnLinha() + " - Identificador '" + tToken.sLexema + "' não declarado");
            }
        } else if ("snumero".equals(tToken.sSimbolo)) { //Numero
            lExpressao.add(tToken);
            tToken = analiseLexica.getNextToken();
        } else if ("snao".equals(tToken.sSimbolo)) { //Nao
            lExpressao.add(tToken);
            tToken = analiseLexica.getNextToken();
            analisaFator();
        } else if ("sabre_parenteses".equals(tToken.sSimbolo)) { //Expressão entre parenteses
            lExpressao.add(tToken);
            tToken = analiseLexica.getNextToken();
            analisaExpressao();
            if ("sfecha_parenteses".equals(tToken.sSimbolo)) {
                lExpressao.add(tToken);
                tToken = analiseLexica.getNextToken();
            } else {
                raiseError("Erro34",analiseLexica.getnLinha(), "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'sfecha_parenteses' esperado");
            }
        } else if ("verdadeiro".equals(tToken.sLexema) || "falso".equals(tToken.sLexema)) {
            lExpressao.add(tToken);
            tToken = analiseLexica.getNextToken();
        } else {
            raiseError("Erro35", analiseLexica.getnLinha() , "Linha: " + analiseLexica.getnLinha() + " - Simbolo 'verdadeiro' ou 'falso' esperado");
        }
    }

    private void chamadaProcedimento() {
        geraCodigo.gera("CALL", "L" + analiseSemantica.pesquisaRotulo(sVarAt));
    }

    private void analisaChamadaFuncao() throws AnaliseException {
        tToken = analiseLexica.getNextToken();

        //geraCodigo.gera("CALL", "L" + analiseSemantica.pesquisaRotulo(sVarAt));
    }

    private void analisaAtribuicao(Simbolo sSimbolo) throws AnaliseException {

        tToken = analiseLexica.getNextToken();

        analisaExpressao();

        substituiUnarios();

        //exibeExpressao("Expressão Normal:");
        lExpressao = analiseSemantica.posOrdem(lExpressao);

        //exibeExpressao("Expressão PosOrdem:");
        geraCodigo.geraExpressao(lExpressao, analiseSemantica);

        analiseSemantica.analisaExpressao(sSimbolo, analiseLexica.getnLinha(), lExpressao);

        lExpressao.removeAll(lExpressao);

        if (analiseSemantica.pesquisaRotulo(sSimbolo.identificador) == null)  //se nao for funcao
            geraCodigo.gera("STR", analiseSemantica.pesquisaEndereco(sSimbolo.identificador) + "");
    }

    private void substituiUnarios() {

        for (int i = 0; i < lExpressao.size(); i++) {
            if ("smais".equals(lExpressao.get(i).sSimbolo) || "smenos".equals(lExpressao.get(i).sSimbolo)) {
                if (i - 1 == -1 || "sabre_parenteses".equals(lExpressao.get(i - 1).sSimbolo) || isOperador(lExpressao.get(i - 1).sSimbolo)) {
                    switch (lExpressao.get(i).sSimbolo) {
                        case "smais":
                            lExpressao.get(i).sSimbolo = "spositivo";
                            break;
                        case "smenos":
                            lExpressao.get(i).sSimbolo = "snegativo";
                            break;
                    }
                }
            }
        }
    }

    public static boolean isOperando(String simbolo) {
        if ("sidentificador".equals(simbolo)
                || "snumero".equals(simbolo)
                || "sverdadeiro".equals(simbolo)
                || "sfalso".equals(simbolo)) {
            return true;
        }
        return false;
    }

    public static boolean isOperador(String simbolo) {
        if ("sou".equals(simbolo)
                || "se".equals(simbolo)
                || "snao".equals(simbolo)
                || "smaior".equals(simbolo)
                || "smaiorig".equals(simbolo)
                || "smenor".equals(simbolo)
                || "smenorig".equals(simbolo)
                || "sig".equals(simbolo)
                || "sdif".equals(simbolo)
                || "smais".equals(simbolo)
                || "smenos".equals(simbolo)
                || "smult".equals(simbolo)
                || "sdiv".equals(simbolo)
                || "spositivo".equals(simbolo)
                || "snegativo".equals(simbolo)) {
            return true;
        }
        return false;
    }

    private void raiseError(String numErro, int nLinha , String msgErro) throws AnaliseException {
        System.out.println(numErro);
        throw new AnaliseException(msgErro, nLinha);
    }
}
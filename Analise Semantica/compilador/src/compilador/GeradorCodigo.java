/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vitor Rodrigo Vezani && Pedro Henrique Grandin
 */
public class GeradorCodigo {
    
    private String sCodigoFinal;
    private FileWriter fileWriter;
    
    public GeradorCodigo() {
        
        sCodigoFinal = new String();
    }
    
    public void gera(String instrucao){
        
        sCodigoFinal = sCodigoFinal + "\t" + instrucao + System.getProperty("line.separator");
    }
    
    public void gera(String instrucao, String atributo1, String atributo2){
        
        sCodigoFinal =  sCodigoFinal + "\t" + instrucao + "\t" + atributo1 + " " + atributo2 + System.getProperty("line.separator");
    }
    
    public void gera(String instrucao, String atributo1){
        
        sCodigoFinal =  sCodigoFinal + "\t" + instrucao + "\t" + atributo1 + System.getProperty("line.separator");
    }
    
    public void geraR(String rotulo){
        
        sCodigoFinal = sCodigoFinal + rotulo + "\tNULL" + System.getProperty("line.separator");
    }
    
    public void geraReturn(){
        
        sCodigoFinal = sCodigoFinal + "\tRETURN" + System.getProperty("line.separator");
    }
    
     public void geraReturnF(){
        
        sCodigoFinal = sCodigoFinal + "\tRETURNF" + System.getProperty("line.separator");
    }
    
    public void geraReturnF(int m, int n){
        
        sCodigoFinal = sCodigoFinal + "\tRETURNF" + "\t" + m + " " + n + System.getProperty("line.separator");
    }
    
    public void gera(String rotulo, String instrucao, String atributo1, String atributo2){
        
        sCodigoFinal =  sCodigoFinal + rotulo + "\t" + instrucao + "\t" + atributo1 + " " + atributo2 + System.getProperty("line.separator");
    }
    
    public void geraExpressao(ArrayList<Analisador.Token> expressao, AnalisadorSemantico analiseSemantica){
        
        String simbolo;
        String valor;
        
        for (int i = 0; i < expressao.size(); i++) {

            simbolo = expressao.get(i).sSimbolo;
            valor = expressao.get(i).sLexema;
            switch (simbolo) {
                case "smais":
                    this.gera("ADD");
                    break;
                case "smenos":
                    this.gera("SUB");
                    break;
                case "smult":
                    this.gera("MULT");
                    break;
                case "sdiv":
                    this.gera("DIVI");
                    break;
                case "snegativo":
                    this.gera("INV");
                    break;
                case "se":
                    this.gera("AND");
                    break;    
                case "sou":
                    this.gera("OR");
                    break;
                case "snao":
                    this.gera("NEG");
                    break;
                case "smenor":
                    this.gera("CME");
                    break;
                case "smaior":
                    this.gera("CMA");
                    break;
                case "sig":
                    this.gera("CEQ");
                    break;
                case "sdif":
                    this.gera("CDIF");
                    break;
                case "smenorig":
                    this.gera("CMEQ");
                    break;
                case "smaiorig":
                    this.gera("CMAQ");
                    break;
                case "snumero":
                    this.gera("LDC", valor);
                    break;
                case "sverdadeiro":
                    this.gera("LDC", "1");
                    break;
                case "sfalso":
                    this.gera("LDC", "0");
                    break;
                case "sidentificador":
                    if (analiseSemantica.pesquisaRotulo(valor) == null){ // verifica se não é função
                        this.gera("LDV", analiseSemantica.pesquisaEndereco(valor) + "");
                    }
                    else{
                        this.gera("CALL", "L" + analiseSemantica.pesquisaRotulo(valor));
                    }
                    break;
            }
        
        }
    }
    
    public void criaFile() {
        
        try {
            String Prop = System.getProperty("user.home") + "\\Desktop\\teste_objeto.obj" ;
            fileWriter = new FileWriter(Prop);
            fileWriter.write(sCodigoFinal);
            fileWriter.close();
            System.out.println("Código final gerado!\n");
        } catch (IOException ex) {
            Logger.getLogger(GeradorCodigo.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
}

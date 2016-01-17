package v2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Puntos {

	private int AstMini, AstMed, AstBig, Enemy, total;
	private static final int valorKillAstMini = 100;
	private static final int valorKillAstMed = 50;
	private static final int valorKillAstBig = 20;
	private static final int valorKillEnemy = 200;
	private static final String src = "src/stats/stats.csv";

	////////////// setter & getters //////////
	public int getAstMini() {return AstMini;}
	public int getAstMed() {return AstMed;}
	public int getAstBig() {return AstBig;}
	public int getEnemy() {return Enemy;}
	public int getTotal() {sumaTodosLosPuntos(); return this.total;}
	public void setAstMini(int astMini) {AstMini = astMini;}
	public void setAstMed(int astMed) {AstMed = astMed;}
	public void setAstBig(int astBig) {AstBig = astBig;}
	public void setEnemy(int enemy) {Enemy = enemy;}

	public Puntos(){
		this.AstMini = this.AstMed = this.AstBig = this.Enemy=0;
	}

	protected void killAstMini(){setAstMini(getAstMini()+valorKillAstMini);}
	protected void killAstMed(){setAstMini(getAstMini()+valorKillAstMed);}
	protected void killAstBig(){setAstMini(getAstMini()+valorKillAstBig);}
	protected void killEnemy(){setAstMini(getAstMini()+valorKillEnemy);}
	protected void sumaTodosLosPuntos(){this.total = getAstMini()+getAstMed()+getAstBig()+getEnemy();}

	protected void leerStats(){
				
		try {
			FileReader fr = new FileReader(String.valueOf(Launcher.class.getResource(src)));
			BufferedReader br = new BufferedReader(fr);

			String line;
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }

            fr.close();
            br.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Archivo de estadisticas no encontrado en "+src);
		}
	}

	protected void writeStats(){

        try {
            FileWriter fw = new FileWriter(src);
            BufferedWriter bw = new BufferedWriter(fw);

            // Note that write() does not automatically append a newline character.
            bw.write("Hello there,");
            bw.write(" here is some text.");
            bw.newLine();
            bw.write("We are writing");
            bw.write(" the text to the file.");
            
            //TODO: leer src, append en memoria, write TODO again


            fw.close();
            bw.close();
        }
        catch(IOException ex) {
            System.out.println("Error writing to file '"+ src + "'"); ex.printStackTrace();
        }
	}
}

package v2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Puntos {

	private int AstMini, AstMed, AstBig, Enemy, EnemySmall, total;
	private static final int valorKillAstBig = 20;
	private static final int valorKillAstMed = 50;
	private static final int valorKillAstMini = 100;
	private static final int valorKillEnemy = 200;
	private static final int valorKillEnemySmall = 1000;
	private static final String src = "src/stats/stats.csv";

	////////////// setter & getters //////////
	public int getAstMini() {return AstMini;}
	public int getAstMed() {return AstMed;}
	public int getAstBig() {return AstBig;}
	public int getEnemy() {return Enemy;}
	public int getEnemySmall() {return EnemySmall;}
	public int getTotal() {sumaTodosLosPuntos(); return this.total;}
	public void setAstMini(int astMini) {AstMini = astMini;}
	public void setAstMed(int astMed) {AstMed = astMed;}
	public void setAstBig(int astBig) {AstBig = astBig;}
	public void setEnemy(int enemy) {Enemy = enemy;}
	public void setEnemySmall(int n) {EnemySmall = n;}

	public Puntos(){
		this.AstMini = this.AstMed = this.AstBig = this.Enemy=0;
	}

	protected void killAstMini(){setAstMini(getAstMini()+valorKillAstMini);}
	protected void killAstMed(){setAstMini(getAstMini()+valorKillAstMed);}
	protected void killAstBig(){setAstMini(getAstMini()+valorKillAstBig);}
	protected void killEnemy(){setAstMini(getAstMini()+valorKillEnemy);}
	protected void killEnemySmall(){setAstMini(getAstMini()+valorKillEnemySmall);}
	protected void sumaTodosLosPuntos(){this.total = getAstMini()+getAstMed()+getAstBig()+getEnemy();}

	protected String leerStats(){
		String s="";
		try {
			FileReader fr = new FileReader(src);
			BufferedReader br = new BufferedReader(fr);
			String line;

			//br.readLine(); //saltar la primera linea (titulo CSV)

            while((line = br.readLine()) != null) {
            	s += line+"\n";
            }

            fr.close();
            br.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Archivo de estadisticas no encontrado en "+src);
		}
		return s;
	}

	protected void writeStats(String puntos, String nombre){

		FileWriter fw = null;
		BufferedWriter bw = null;

        try {
            fw = new FileWriter(src, true);		//boolean indicating whether or not to append the data written.
            bw = new BufferedWriter(fw);
            bw.write(nombre+";"+puntos);
            bw.newLine();
        }
        catch(IOException ex) {
        	 ex.printStackTrace();
            System.out.println("Error writing to file '"+ src + "'");
        }
        finally{
            try{
            	fw.flush(); bw.flush();
                fw.close(); bw.close();
            }catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error while flushing/closing fileWriter !!!");
            }
        }
	}


}

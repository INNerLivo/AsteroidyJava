
/**
 * Hlavny nepriatel, tento objekt sa hrac snazi nicit <br />
 */
public class Asteroid {
    private int x;
    private int y;
    private double uhol;
    private int rychlost;

    private VelkostAsteroidu velkost;
    
    private Obrazok textura;
    private Platno platno;

    public Asteroid(int x, int y, double uhol, VelkostAsteroidu velkost) {
        this.x = x;
        this.y = y;
        this.uhol = uhol;
        
        this.platno = Platno.dajPlatno();
        String nazovTextury = null;
        
        this.velkost = velkost;
        
        if (velkost == VelkostAsteroidu.MALY) {
            nazovTextury = "Asteroid_maly";
            this.rychlost = 4;
        } else if (velkost == VelkostAsteroidu.STREDNY) {
            nazovTextury = "Asteroid_stredny";
            this.rychlost = 3;
        } else if (velkost == VelkostAsteroidu.VELKY) {
            nazovTextury = "Asteroid_velky";
            this.rychlost = 2;
        }
        
        this.textura = new Obrazok(nazovTextury);
        this.textura.zmenPolohu(this.x, this.y);
        this.textura.zmenUhol(this.uhol);
        // initialise instance variables
    }
    
    /**
     * Vyvolava sa každý tik.
     * Vykonáva pohyb a kontroluje kolízie s loďkou a so strelou.
     * Ak áno tak sa zničí.
     */
    public void chod() {
        int velkostPola = this.platno.getVelkostPola();
        
        if (this.koliduje()) {
            this.znicSa();    
            return;
        }
        
        this.x += Math.cos(Math.toRadians(this.uhol)) * this.rychlost;
        this.y += Math.sin(Math.toRadians(this.uhol)) * this.rychlost;


        if (this.x > velkostPola) {
            this.x = 0;
        } else if (this.x < 0.1) {
            this.x = velkostPola;
        }
        if (this.y > velkostPola) {
            this.y = 0;
        } else if (this.y < 0.1) {
            this.y = velkostPola;
        }

        this.textura.zmenPolohu(this.x, this.y);
    }
    
    /**
     * Ničí asteroid tak, že sa rozdelí na dve menšie časti
     * alebo sa zničí kompletne.
     * V každom páde pridáva bod.
     */
    public void znicSa() {
        Menu.dajMenu().pridajBody(1);
        Zvuk.dajZvuk().spustiZvuk("Asteroid");
        
        String nazovTextury = null;
        this.uhol += 11;
        
        if (this.velkost == VelkostAsteroidu.MALY) {
            this.textura.skry();
            Ovladanie.dajOvladanie().znicAsteroid(this);
            // znicenie
            return;
        } else if (this.velkost == VelkostAsteroidu.STREDNY) {
            nazovTextury = "Asteroid_maly";
            this.rychlost = 4;
            this.velkost = VelkostAsteroidu.MALY;
            Ovladanie.dajOvladanie().vyvtorAsteroid(this.x, this.y, this.uhol - 22, this.velkost);
        } else if (this.velkost == VelkostAsteroidu.VELKY) {
            nazovTextury = "Asteroid_stredny";
            this.rychlost = 3;
            this.velkost = VelkostAsteroidu.STREDNY;
            Ovladanie.dajOvladanie().vyvtorAsteroid(this.x, this.y, this.uhol - 22, this.velkost);
        }

        this.textura.zmenObrazok(nazovTextury);
        this.textura.zmenPolohu(this.x, this.y);
        this.textura.zmenUhol(this.uhol);
    }
    
    /**
     * Kontroluje kolízie s ľoďkou alebo so strelou.
     * A vracia booleanovu hodnotu.
     */
    public boolean koliduje() {
        /*
         * Je to lepšie mať tu to napísanú kolíziu ako individuálne pri loďke a strele
         * lebo by to zbytočne zaťažilo program.
         */
        Ovladanie ovl = Ovladanie.dajOvladanie();
        Strela strela = ovl.getStrela();
        Lodka lodka = ovl.getLodka();
       
        int velkostEnumu = this.velkost.getVelkost() / 2;
       
        if (strela != null) {
            int xStrely = strela.getPolohuX();
            int yStrely = strela.getPolohuY();
            if (xStrely + 4 >= this.x - velkostEnumu  && xStrely - 4 <= this.x + velkostEnumu &&
                yStrely + 4 >= this.y - velkostEnumu  && yStrely - 4 <= this.y + velkostEnumu) {
                strela.znicSa();
                return true;
            }
        }
       
        if (lodka != null && (!lodka.jeMrtva())) {
            int xLodky = lodka.getPolohuX();
            int yLodky = lodka.getPolohuY();
            if (xLodky + 8 >= this.x - velkostEnumu  && xLodky - 8 <= this.x + velkostEnumu &&
                yLodky + 8 >= this.y - velkostEnumu  && yLodky - 8 <= this.y + velkostEnumu) {
               // Takyto sposob zapisu opravuje jednu chybu, kde sa asteroid nevymaze po prehre hry
                return lodka.znicSa();
            }
        }
       
        return false;
    }
    
    /**
     * Na rozdiel od metódy znicSa()
     * táto metóda nerozdeluje asteroidy na menšie kúsky ale rovno sa ich zbaví.
     * Je vyvolaná počas ukončenia hry.
     */
    public void odstranSa() {
        this.textura.skry();
        Ovladanie.dajOvladanie().znicAsteroid(this);
    }
}

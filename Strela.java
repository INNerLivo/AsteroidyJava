
/**
 * Pouziva sa na znicenie asteoridov <br />
 */
public class Strela {

    private int x;
    private int y;
    private double uhol;
    private int delta; // <- Pouzita pre prekrocenu vzdialenost. Ak prekroci staticku premennu DOSTREL : znici sa automaticky

    //private Hra hra;
    private Platno platno;
    private Obrazok textura;

    private static final int RYCHLOST = 15;
    private static final int DOSTREL = 300; // maximalny dostrel
    
    public Strela( int x, int y, double uhol) {
        //this.hra = hra;
        this.platno = Platno.dajPlatno();
        this.x = x;
        this.y = y;
        this.uhol = uhol;
        this.delta = 0;
        
        this.textura = new Obrazok("Strela");
        this.textura.zmenPolohu(x, y);
        this.textura.zmenUhol(uhol);
    }
    
    /**
     * Vyvolava sa kazdy tik. <br />
     * Tu rata aj svoj dostrel, a keď prekročí maximalnu hodnotu dostrelu <br />
     * strela sa automaticky vymaže. <br />
     */
    public void chod() {
        int velkostPola = Platno.dajPlatno().getVelkostPola();
        this.x += Math.cos(Math.toRadians(this.uhol)) * RYCHLOST;
        this.y += Math.sin(Math.toRadians(this.uhol)) * RYCHLOST;

        this.delta += RYCHLOST;
        if (this.delta > DOSTREL) {
            this.znicSa();
            return;
        }

        if (this.x > velkostPola) {
            this.x -= velkostPola;
        }
        if (this.y > velkostPola) {
            this.y -= velkostPola;
        }
        if (this.y < 0) {
            this.y += velkostPola;
        }
        if (this.x < 0) {
            this.x += velkostPola;
        }

        this.textura.zmenPolohu(this.x, this.y);
    }
    
    public void znicSa() {
        //this.ovladanie.znicStrelu();
        this.textura.skry();
        Ovladanie.dajOvladanie().znicStrelu();
        
    }

    public int getPolohuX() {
        return this.x;
    }
    
    public int getPolohuY() {
        return this.y;
    }
    
    public int getDelta() {
        return this.delta;
    }
    
} 

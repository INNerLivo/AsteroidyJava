
/**
 * Tento objekt ovlada hrac <br />
 * chod() - vyvolaná každý krok cez Ovladanie() a ovlada pohyb lode <br />
 * zrychuj() - ak hrac drzi klavesu UP alebo W tak sa lod zacne hybat <br />
 */
public class Lodka {
    private int x;
    private int y;
    private double uhol;
    private double rychlostX;
    private double rychlostY;
    private int zivoty;
    private int cakaj;
    private int velkostPola;
    private boolean mrtva;
    
    private Platno platno;
    private static Lodka lodkaSingleton;
    private Obrazok textura;
    
    private static final int MAX_RYCHLOST = 12;
    
    /**
     * Vytvara singleton Lodky.
     */
    public static Lodka dajLodku() {
        if (Lodka.lodkaSingleton == null) {
            Lodka.lodkaSingleton = new Lodka();
        }
        return Lodka.lodkaSingleton;
    }
    
    /**
     * Zobrazí sa v strede poľa a je otočená na pravú stranu.
     */
    public Lodka() {
        this.platno = Platno.dajPlatno();
        this.velkostPola = this.platno.getVelkostPola();
        this.zivoty = Menu.dajMenu().getZivoty();
        this.mrtva = false;
        this.x = this.velkostPola / 2;
        this.y = this.velkostPola / 2;
        this.uhol = 0;
        this.cakaj = 0;

        this.rychlostX = 0;
        this.rychlostY = 0;
        
        this.textura = new Obrazok("Lodka");
        this.textura.zmenPolohu(this.x, this.y);
        this.textura.zmenUhol(this.uhol);
    }
    
    /**
     * Túto metódu neovláda hráč. <br />
     * Vyvoláva sa každý tik a zachováva pohyb loďky i keď hráč nedrží príslušnú klávesu <br />
     * Ešte obsahuje respawn timer, keď loďka je zničená. <br />
     */
    public void chod() {

        if (this.mrtva) {
            // respawnuje lodku
            this.cakaj -= 1;
            if (this.cakaj <= 0 && this.zivoty >= 0) {
                this.mrtva = false;
                this.rychlostX = 0;
                this.rychlostY = 0;
                this.x = this.velkostPola / 2;
                this.y = this.velkostPola / 2;
                this.textura.zobraz();
            }
            return;
        }

        this.x += this.rychlostX;
        this.y += this.rychlostY;
        
        if (Math.abs(this.rychlostX) > 0.1) {
            this.rychlostX -= 0.1 * (int)Math.signum(this.rychlostX);
        } else {
            this.rychlostX = 0;
        }
        
        if (Math.abs(this.rychlostY) > 0.1) {
            this.rychlostY -= 0.1 * (int)Math.signum(this.rychlostY);
        } else {
            this.rychlostY = 0;
        }
         
        // premiestnenie na druhu stranu pola
        
        if (this.x > velkostPola) {
            this.x -= this.velkostPola;
        }
        if (this.y > velkostPola) {
            this.y -= this.velkostPola;
        }
        if (this.y < 0) {
            this.y += this.velkostPola;
        }
        if (this.x < 0) {
            this.x += this.velkostPola;
        }

        this.textura.zmenPolohu(this.x, this.y);
    }

    /**
     * Túto metódu vyvoláva hráč pomocou klávesy W alebo šípky hore. <br />
     * Pridáva rýchlosť.
     */
    public void zrychluj() {

        if (this.mrtva) {
            return;
        }

        if (Math.abs(this.rychlostX) < MAX_RYCHLOST) {
            this.rychlostX += 0.25 * Math.cos(Math.toRadians(this.uhol));
        } else {
            this.rychlostX = MAX_RYCHLOST * (int)Math.signum(this.rychlostX);
        }
        if (Math.abs(this.rychlostY) < MAX_RYCHLOST) {
            this.rychlostY += 0.25 * Math.sin(Math.toRadians(this.uhol));
        }  else {
            this.rychlostY = MAX_RYCHLOST * (int)Math.signum(this.rychlostY);
        }
    }
    
    /**
     * Otáča loďku
     */
    public void otocSa(double oKolkoStupnov) {
        if (this.mrtva) {
            return;
        }

        this.uhol += oKolkoStupnov;
        if (this.uhol >= 360) { 
            this.uhol -= 360;
        } else if (this.uhol < 0) { 
            this.uhol += 360;
        }

        this.textura.zmenUhol(this.uhol);
    }
    
    /**
     * Zničenie loďky pri náraze s asteroidom.
     */
    public boolean znicSa() {
        this.mrtva = true;
        this.zivoty -= 1;
        this.cakaj = 100; // caka 100 krokov
        Zvuk.dajZvuk().spustiZvuk("Death");
        Menu.dajMenu().nastavZivoty(this.zivoty);
        this.textura.skry();
        
        
        if (this.zivoty < 0) {
            // GAME OVER
            Menu.dajMenu().prehra();
            return false;
        }
        return true;
    }
    
    public void koniecHry() {
        this.textura.skry();
    }
    
    /**
     * Vracia celkovú rýchlosť lodky. <br />
     * Použitá pri kolízií s asteroidom.
     */
    public double getCelkovaRychlost() {
        double absolutnaHodnotaX = Math.abs(this.rychlostX);
        double absolutnaHodnotaY = Math.abs(this.rychlostY);
        double celkovaRychlost = Math.max(absolutnaHodnotaX, absolutnaHodnotaY); 
        return celkovaRychlost;
    }
    
    public double getUhol() {
        return this.uhol;
    }
    
    public int getPolohuX() {
        return this.x;
    }
    
    public int getPolohuY() {
        return this.y;
    }
    
    public boolean jeMrtva() {
        return this.mrtva;
    }
    
}

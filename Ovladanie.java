import java.util.ArrayList; 

/**
 * Srdce programu, táto trieda je spravovaná manažerom <br />
 * a vďaka tomu komunikuje aj s ostatnými triedami ako napr. pohyb lodky. <br />  
 */
public class Ovladanie {
    private static Ovladanie ovladanieSingleton;
    
    private Zvuk zvuk;
    private Menu menu;
    private Manazer manazer;
    
    private Lodka lodka;
    private Strela strela;
    private ArrayList<Asteroid> asteroidy = new ArrayList<Asteroid>(16);
    
    /**
     * Vytvara singleton  
     */
    public static Ovladanie dajOvladanie() {
        if (Ovladanie.ovladanieSingleton == null) {
            Ovladanie.ovladanieSingleton = new Ovladanie();
        }
        return Ovladanie.ovladanieSingleton;
    }
    
    public Ovladanie() {
        this.manazer = new Manazer();
        this.manazer.spravujObjekt(this);
        //this.setHra(this.manazer.getHra());
        this.zvuk = Zvuk.dajZvuk();
        this.zvuk.spustiZvuk("Click");
       
    }
    
    // HRA
    /**
     * Hra komunikuje s triedou menu a pýta sa jej, či hráč nadobudol záujem o hraní tejto hry 
     * (Viď „public boolean spustiHru()“ triede Menu).  
     * Ak hráč chce si hru zahrať, vytvorí loďku a asteroidy. 
     */
    public void spustiHru() {
        boolean hraSaSpustila = Menu.dajMenu().spustiHru();
        if (hraSaSpustila) {

            this.lodka = null;
            this.lodka = new Lodka();

            this.strela = null;
       
            this.znicVsetkyAsteroidy();
            this.vygenerujAsteroidy();
        
        }
    }
    
    /**
     * Podobne ako v metóde SpustiHru(), 
     * len robí čistý opak : ak hráč nechce daľej hrať, tak funkcia sa zbaví všetkých asteroidov a loďky. Podobne ako v predošlej funkcií, len robí čistý opak : ak hráč nechce daľej hrať, tak funkcia sa zbaví všetkých asteroidov a loďky. 
     */
    public void vypniHru() {
        boolean hraSaVypla = Menu.dajMenu().vypniHru();
        if (hraSaVypla) {
            Menu.dajMenu().nastavStavHry(false);
            this.lodka.koniecHry();
            this.lodka = null;
            if (this.strela != null) {
                this.strela.znicSa();
                this.strela = null;
            }
            this.znicVsetkyAsteroidy();
        }
    }
    
    // VYTVARANIE A NICENIE OBJEKTOV
    
    /**
     * Vytvára asteroid na danej súradnici s konkretnym uhlom a velkostou <br />
     * Pred vytvorením asteoridu ešte skontroluje, či loďka sa nenachádza na danej súradnici, ak hej tak sa asteorid vytvorí v strede herného poľa.  <br /> 
     */
    public void vyvtorAsteroid(int x, int y, double uhol, VelkostAsteroidu velkost) {
        if (Menu.dajMenu().hraSpustena()) {
            Lodka l = this.lodka;
            int hodnotaEnumu = velkost.getVelkost();

            if ((l != null) && (!l.jeMrtva())) {
                int xLodky = l.getPolohuX();
                int yLodky = l.getPolohuY();
                if (xLodky + 8 >= x - hodnotaEnumu  && xLodky - 8 <= x + hodnotaEnumu &&
                    yLodky + 8 >= y - hodnotaEnumu  && yLodky - 8 <= y + hodnotaEnumu) {
                        // Osetrujeme aby sa nevytvoril asteroid tam, kde sa nachadza lodka
                    int velkostPola = Platno.dajPlatno().getVelkostPola();
                    x = velkostPola / 2;
                    y = velkostPola / 2;
                } 
            }
            
            this.asteroidy.add( new Asteroid(x, y, uhol, velkost)  );
          
        }
    }
    
    /**
     * Vytvára 6 asteroidov. <br />
     * Táto metóda sa používa, keď hráč spustí novú hru alebo zničil všetky asteroidy <br />
     */
    public void vygenerujAsteroidy() {
        int velkostPola = Platno.dajPlatno().getVelkostPola();
        
        // lava strana
        this.vyvtorAsteroid(45, 45, 45, VelkostAsteroidu.VELKY);
        this.vyvtorAsteroid(135, velkostPola / 2, 315, VelkostAsteroidu.VELKY);
        this.vyvtorAsteroid(90, velkostPola - 90, 300, VelkostAsteroidu.VELKY);
        
        // lava strana
        this.vyvtorAsteroid(velkostPola - 90, 22, 100, VelkostAsteroidu.VELKY);
        this.vyvtorAsteroid(velkostPola - 60, velkostPola / 2 + 12, 125, VelkostAsteroidu.VELKY);
        this.vyvtorAsteroid(velkostPola - 145, velkostPola - 55, 200, VelkostAsteroidu.VELKY);
    }
    
    public void znicAsteroid(Asteroid asteroid) {
        this.asteroidy.remove(asteroid);
        
        if (this.asteroidy.isEmpty()) {
            this.vygenerujAsteroidy(); // Nove kolo
        }
    }
    
    /**
     * Zničí a vymaže všetky asteroidy. <br />
     * Táto metóda sa vyvoláva práve vtedy, keď hráč dohral hru. <br />
     */
    public void znicVsetkyAsteroidy() {
        if (!this.asteroidy.isEmpty()) {
            int pocetAsteroidov = this.asteroidy.size();
            for (int i = 0; i < pocetAsteroidov; i++) {
                this.asteroidy.get(0).odstranSa();
                /*
                 * Namiesto .get(n) som dal .get(0) lebo funkcia odstranSa uz vlastne obsahuje kod, kde sa dany asteroid vymaze.
                 */
            }
            this.asteroidy.clear();
        }
    }

    public void znicStrelu() {
        this.strela = null;
    }
    
    // OVLADANIE LODKY -------------------------------
    
    /**
     * Pri stisknutí šípky hore alebo klávesy W povie lodke aby sa pohla  <br />
     */
    public void chod() {
        boolean hraJeSpustena = Menu.dajMenu().hraSpustena();
        if (hraJeSpustena) {
            this.getLodka().zrychluj();
        }
    }

    /**
     * Povie loďke aby sa otočila do zadaného smeru.  <br />
     */
    public void otocSa(int protiSmeruHodin) {
        boolean hraJeSpustena = Menu.dajMenu().hraSpustena();
        /**
         * Namiesto booleanu som použil integer lebo manažer posiela správy s integerovými parametrami
         * a nechcel som vytvárať novú a skoro rovnakú funkciu, ktorá posiela booleanove parametre
         */
        if (hraJeSpustena) {
            this.getLodka().otocSa(5 - 10 * protiSmeruHodin);
        }
    }
    
    /**
     * Vytvorí strelu na mieste loďky a zmaže tú predošlú. <br />
     * Ešte kontroluje, či ľoďka stojí na jednom mieste. <br />
     * Ak áno tak strieľa rýchlejšie.  <br />
     */
    public void strielaj() {
        boolean hraJeSpustena = Menu.dajMenu().hraSpustena();
        if (hraJeSpustena) {
            Lodka l = this.getLodka(); 
            if (l.jeMrtva()) {
                return;
            }
        
            if ((this.strela == null) || 
                ((this.strela.getDelta() > 120) && (l.getCelkovaRychlost() < 0.2))) {
                    /*
                     * Tu to som spravil taku pikosku, ze ak lod stoji na jednom mieste, striela rychlejsie ale ma mensi dostrel
                     */
                if (this.strela != null) {
                    this.strela.znicSa();
                }
                this.zvuk.spustiZvuk("Strela");
                this.strela = new Strela(l.getPolohuX(), l.getPolohuY(), l.getUhol());
            }
        }
    }
    
    // TIK
    
    /**
     * Komunikuje s objektami ale iba v tedy ak hra nie je pauznutá  <br />
     */
    public void tik() {
        boolean hraJeSpustena = Menu.dajMenu().hraSpustena();
        boolean hraJePauznuta = Menu.dajMenu().hraPauznuta();
        if (hraJeSpustena && !hraJePauznuta) {
            
            this.getLodka().chod();
            if (this.getStrela() != null) {
                this.getStrela().chod();
            }
            if (!this.asteroidy.isEmpty()) {
                int pocetAsteroidov = this.asteroidy.size();
                for (int i = 0; i < pocetAsteroidov; i++) {
                    this.asteroidy.get(i).chod();
                }
            }

        }
    }
    
    // GETTERE
    
    public Lodka getLodka() throws NullPointerException {
        if (this.lodka == null) {
            return null;
        }
        return this.lodka;
    }

    public Strela getStrela() throws NullPointerException {
        if (this.strela == null) {
            return null;
        }
        return this.strela;
    }
    
}

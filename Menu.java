
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Stara sa o funkcionalitu hry. <br />
 * Hlavne spusta hru a vypina ju <br />
 */
public class Menu {
    private static Menu menuSingleton;
    private boolean hraJeSpustena = false;
    private boolean hraJePauznuta = false;
    private int skore;
    private int zivoty;
    
    private Obrazok titulok;
    
    private Platno platno;
    
    public static Menu dajMenu() {
        if (Menu.menuSingleton == null) {
            Menu.menuSingleton = new Menu();
        }
        return Menu.menuSingleton;
    }
    
    public Menu() {
        this.platno = Platno.dajPlatno();
        int stred = this.platno.getVelkostPola() / 2;
        
        this.titulok = new Obrazok("Titulok");
        this.titulok.zmenPolohu(stred, stred);
        
    }
   
    /**
     * Táto metóda vyvoláva okienko, <br />
     * ktoré si pýta koľko životov si prajete mať počas hry <br />
     * Vracia booleanovu hodnotu do Ovladania, či sa hra spustila alebo nie. <br />
     */
    public boolean spustiHru() {
        if (!this.hraJeSpustena) {
            // Vypni hru-
            this.hraJePauznuta = true;
            JPanel okienko = new JPanel();
            JTextField pocetZivotov = new JTextField(2);

            okienko.add(new JLabel("Pocet zivotov : "));
            okienko.add(pocetZivotov);

            Zvuk.dajZvuk().spustiZvuk("Click");

            int input = JOptionPane.showConfirmDialog(null, okienko, "Zapnutie hry", 
                    JOptionPane.OK_CANCEL_OPTION);
            if (input == JOptionPane.CANCEL_OPTION ) {
                this.hraJePauznuta = false;
                return false;
            } else {
                this.hraJePauznuta = false;
                this.skore = 0;
                this.nastavZivoty(Integer.parseInt(pocetZivotov.getText()));
                this.nastavStavHry(true);
                Zvuk.dajZvuk().spustiZvuk("Start");
                this.titulok.skry();
                return this.nastavStavHry(true);
            }

        }
        
        return false;
    }
    
    /**
     * Podobne ako pri metóde spustiHru() <br />
     * Tu to sa okno pýta, či chcete hru zrušiť. <br />
     * Metóda vracia booleanovu hodnotu do ovládania, či sa hra vypla alebo nie. <br />
     */
    public boolean vypniHru() {
        Zvuk.dajZvuk().spustiZvuk("Click");
        if (!this.hraJeSpustena) {
            this.hraJePauznuta = true;
            // Vypni hru-
            int input = JOptionPane.showOptionDialog(null, "Chcete naozaj vypnut hru?", "Vypnutie", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
            if (input == JOptionPane.NO_OPTION) {
                this.hraJePauznuta = false;
                return false;
            } else {
                System.exit(0);
            }

        } else {
            this.hraJePauznuta = true;
            int input = JOptionPane.showOptionDialog(null, "Chcete naozaj zrusit rozohratu hru?", "Vypnutie", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
            if (input == JOptionPane.NO_OPTION) {
                this.hraJePauznuta = false;
                return false;
            } else {
                Zvuk.dajZvuk().spustiZvuk("Finish");
                if (this.skore > 0) {
                    JOptionPane.showMessageDialog(null, "Skoncili ste hru. Vase skore je : " + this.skore + "00", "KONIEC HRY", 
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
                this.hraJePauznuta = false;
                this.titulok.zobraz();
                return true;
            }

        }
        return false;
    }
    
    /**
     * Metóda je vyvolaná pri prehre hry (Zomriete s posledním životom) <br />
     * Tu sa ukáže okno, ktoré vás informuje o prehre a koľko bodov ste získali <br />
     * Potom sa vás pýta, či chete hru hrať od znova alebo ju chcete ukončiť.
     */
    public void prehra() {
        int input = JOptionPane.showOptionDialog(null, "Prehrali ste! Vase skore je : " + this.skore + "00 \n Chcete hrat od znova?", "KONIEC HRY", 
                                                 JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
        this.hraJePauznuta = true;
        if (input == JOptionPane.NO_OPTION) {
            System.exit(0);
        } else {
            Ovladanie ovl = Ovladanie.dajOvladanie();
            this.hraJeSpustena = false;
            this.hraJePauznuta = false;
            ovl.spustiHru();
        }
    }
    
    /** 
     * Nastavi zapnutie alebo vypnutie hry
     */
    public boolean nastavStavHry(boolean zapni) {
        this.hraJeSpustena = zapni;
        return this.hraJeSpustena;
    }
    
    public boolean hraSpustena() {
        return this.hraJeSpustena;
    }
    
    public boolean hraPauznuta() {
        return this.hraJePauznuta;
    }
    
    public int getSkore() {
        return this.skore;
    }
    
    public int getZivoty() {
        return this.zivoty;
    }
    
    /**
     * Pridáva body. <br />
     * Za kazdych 50 bodov (uzivatel vidi 5000) dostane hrac dalsi zivot. <br />
     */
    public void pridajBody(int body) {
        this.skore += body;
        if ((this.skore > 0) && (this.skore % 50 == 0)) {
            this.nastavZivoty(this.zivoty + 1);
            Zvuk.dajZvuk().spustiZvuk("Zivot");
        }
        
        this.platno.zmenTitulok(this.skore * 100, this.zivoty);

    }
    
    public void nastavZivoty(int zivoty) {
        this.zivoty = zivoty;
        this.platno.zmenTitulok(this.skore * 100, this.zivoty);
    }
    
    
    public void resetujSkore() {
        this.skore = 0;
    }
    
}

import java.util.ArrayList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * Automaticky posiela spravy danym objektom:<br />
 * chod() - pri stlaceni klavesy UP alebo W<br />
 * otocSa() - pri stlaceni klavesy LEFT,RIGHT alebo A,D<br />
 * strielaj() - pri stlaceni klavesy SPACE alebo CTRL<br />
 * spustiHru() - pri stlaceni klavesy ENTER<br />
 * vypniHru() - pri stlaceni klavesy ESC<br />
 * tik() - kazdych 0,1 sekundy<br />
 */
public class Manazer {
    private ArrayList<Object> spravovaneObjekty;
    private ArrayList<Integer> vymazaneObjekty;
    private ManazerKlaves manazerKlaves;
    private long oldTick;
    private static final long TICK_LENGTH = 10000000; // zmenil som tik
    
    //private Hra hra;
    /**
     * Tento kod som prerobil podla kodu z tejto stranky
     * https://stackoverflow.com/questions/2623995/swings-keylistener-and-multiple-keys-pressed-at-the-same-time
     * a potom samozrejme prerobeny
     */
    private class ManazerKlaves extends KeyAdapter {

        private final Set<Integer> stisknuteKlavesy = new HashSet<>();
        
        @Override
        public synchronized void keyPressed(KeyEvent event) {
            this.stisknuteKlavesy.add(event.getKeyCode());
        }
        
        @Override   
        public synchronized void keyReleased(KeyEvent event) { 
            this.stisknuteKlavesy.remove(event.getKeyCode());
        }
          
        /** Tato funkcia sa vykonava kazdy krok vdaka Manazera Casovaca
         *  Potom takto sa hra krasne ovlada
         */
        public void vykonajKlavesovuAkciu() {
            if (Menu.dajMenu().hraPauznuta()) {
                return;
            }
            if (!this.stisknuteKlavesy.isEmpty()) {
                for (Iterator<Integer> it = this.stisknuteKlavesy.iterator(); it.hasNext();) {
                    switch (it.next()) {
                        case KeyEvent.VK_W:
                        case KeyEvent.VK_UP:
                            Manazer.this.posliSpravu("chod");
                            break;
                        case KeyEvent.VK_A:
                        case KeyEvent.VK_LEFT:
                            Manazer.this.posliSpravu("otocSa", 1);
                            break;
                        case KeyEvent.VK_D:
                        case KeyEvent.VK_RIGHT:
                            Manazer.this.posliSpravu("otocSa", 0);
                            break;
                        case KeyEvent.VK_CONTROL:
                        case KeyEvent.VK_SPACE:
                            Manazer.this.posliSpravu("strielaj");
                            break;
                        case KeyEvent.VK_ENTER:
                            Manazer.this.posliSpravu("spustiHru");
                            this.stisknuteKlavesy.remove(KeyEvent.VK_ENTER);
                            break;
                        case KeyEvent.VK_ESCAPE:
                            Manazer.this.posliSpravu("vypniHru");
                            this.stisknuteKlavesy.remove(KeyEvent.VK_ESCAPE);
                            break;
                    }
                }
            }
        }
    }   

    private class ManazerCasovaca implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            long newTick = System.nanoTime();
            if (newTick - Manazer.this.oldTick >= Manazer.TICK_LENGTH || newTick < Manazer.TICK_LENGTH) {
                Manazer.this.oldTick = (newTick / Manazer.TICK_LENGTH) * Manazer.TICK_LENGTH;
                Manazer.this.posliSpravu("tik");
                Manazer.this.manazerKlaves.vykonajKlavesovuAkciu();
            }
        }
    }
    
    private void posliSpravu(String selektor) {
        for (Object adresat : this.spravovaneObjekty) {
            try {
                if (adresat != null) { 
                    Method sprava = adresat.getClass().getMethod(selektor);
                    sprava.invoke(adresat);
                }
            } catch (SecurityException e) {
                this.doNothing();
            } catch (NoSuchMethodException e) {
                this.doNothing();
            } catch (IllegalArgumentException e) {
                this.doNothing();
            } catch (IllegalAccessException e) {
                this.doNothing();
            } catch (InvocationTargetException e) {
                this.doNothing();
            }
        }
        this.removeDeletedObjects();
    }
    
    private void posliSpravu(String selektor, int prvyParameter) {
        /**
         * Tu to som sa zbavil druhehoParametra, táto hra ho proste nepotrebovala
         */
        
        for (Object adresat : this.spravovaneObjekty) {
            try {
                if (adresat != null) {
                    Method sprava = adresat.getClass().getMethod(selektor, Integer.TYPE);
                    sprava.invoke(adresat, prvyParameter);
                }
            } catch (SecurityException e) {
                this.doNothing();
            } catch (NoSuchMethodException e) {
                this.doNothing();
            } catch (IllegalArgumentException e) {
                this.doNothing();
            } catch (IllegalAccessException e) {
                this.doNothing();
            } catch (InvocationTargetException e) {
                this.doNothing();
            }
        }
        this.removeDeletedObjects();
    }
    
    private void doNothing() {
        
    }
    
    private void removeDeletedObjects() {
        if (this.vymazaneObjekty.size() > 0) {
            Collections.sort(this.vymazaneObjekty, Collections.reverseOrder());
            for (int i = this.vymazaneObjekty.size() - 1; i >= 0; i--) {
                this.spravovaneObjekty.remove(this.vymazaneObjekty.get(i));
            }
            this.vymazaneObjekty.clear();
        }        
    }
    
    /**
     * Vytvori novy manazer, ktory nespravuje zatial ziadne objekty.
     */
    public Manazer() {
        this.spravovaneObjekty = new ArrayList<Object>();
        this.vymazaneObjekty = new ArrayList<Integer>();
        this.manazerKlaves = new ManazerKlaves();
        Platno.dajPlatno().addKeyListener(this.manazerKlaves);
        Platno.dajPlatno().addTimerListener(new ManazerCasovaca());
    }
    
    /**
     * Manazer bude spravovat dany objekt.
     */
    public void spravujObjekt(Object objekt) {
        this.spravovaneObjekty.add(objekt); 
    }
    
    /**
     * Manazer prestane spravovat dany objekt.
     */
    public void prestanSpravovatObjekt(Object objekt) {
        int index = this.spravovaneObjekty.indexOf(objekt);
        if (index >= 0) {
            this.spravovaneObjekty.set(index, null);
            this.vymazaneObjekty.add(index);
        }
    }
    
    /*
     * Ďalšie úpravy sú : Zbavil som sa manažera myši.
     * 
     */
    
    
}


/**
 * Vytvoril Ivan Nosal 5ZYI13 <br />
 * Pouzite tutorialy : <br />
 * https://www.youtube.com/watch?v=7kMXr2AJLPA&t=700s <br />
 * 
 * Pouzite objekty z cviceni : Manazer , Platno, Obrazok
 */
public class HraMain {
    
    private Ovladanie ovladanie;
    private Menu menu;
    
    public static void main(String[] args) {
        Ovladanie ovladanie = Ovladanie.dajOvladanie();
        Menu menu = Menu.dajMenu();
    }

}

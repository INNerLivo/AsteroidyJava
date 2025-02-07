
/**
 * Hodnoty su použité pri rozoznávaní veľkosti asteroidu.  <br />
 * MALY(24), <br />
 * STREDNY(40), <br />
 * VELKY(64); <br />
 */
public enum VelkostAsteroidu {
    MALY(24),
    STREDNY(40),
    VELKY(64);

    private int velkost;
    
    VelkostAsteroidu(int velkost) {
        this.velkost = velkost;
    }
    
    public int getVelkost() {
        return this.velkost;
    }
    
    
}

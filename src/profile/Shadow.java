package profile;

public class Shadow {
   
    public int start;
    public int end;
    public int width;
    /*
     * Shadow strength is calculated as number of absolute deviations below the median
     */
    public int strength;
    
    public Shadow(int start, int end, int strength, int width) {
        this.start = start;
        this.end = end;
        this.strength = strength;
        this.width = width;
    }
}

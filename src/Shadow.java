
public class Shadow {
   
    int start;
    int end;
    int width;
    /*
     * Shadow strength is calculated as number of absolute deviations below the median
     */
    int strength;
    
    public Shadow(int start, int end, int strength, int width) {
        this.start = start;
        this.end = end;
        this.strength = strength;
        this.width = width;
    }
}

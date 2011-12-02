package profile;
public class Peak {
    
    public int start;
    public int end;

    public double amplitude;
    
    Peak(int start, int end, double[] icov) {
        this.start = start;
        this.end = end;
        
        calculateAmplitude(icov);
    }
    
    private void calculateAmplitude(double[] icov) {
        double max = 0;
        for(int i = start; i <= end; i++) {
            if(icov[i] > max) {
                max = icov[i];
            }
        }
        
        amplitude = max;
    }
}

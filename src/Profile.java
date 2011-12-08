import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;


public class Profile {
    
    private final double AMPLITUDE_COMPARISON_FACTOR = 2.0;

    private double[] originalProfile = null;
    protected double[] arr = null;
    private double[] sortedProfile = null;
    private double[] profileDeviations = null;
    protected double[] icov = null;
    private double[] sortedICOV = null;
    private double[] icovDeviations = null;
    private double profileMedian;
    private double profileVariance;
    private double profileMAD;
    private double icovMedian;
    private double icovMAD;
    private double profileMean;
    
    private boolean meanIsSet = false;
    private boolean medianIsSet = false;
    private boolean madIsSet = false;
    private boolean varianceIsSet = false;
    
    private List<Valley> valleys;
    private List<Peak> peaks;
    private List<Shadow> shadows;
    
    private SradProcessor sradProcessor = null;
    
    Profile(ImagePlus img, Rectangle r) {
        this.arr = toProfileArray(img, r);
        originalProfile = new double[arr.length];
        System.arraycopy(arr, 0, originalProfile, 0, arr.length);
        icov = new double[arr.length];
        
        //TODO: remove this?
        getProfileMedian();

        //have to call updateICOV functions here so we can get initial epsilon value
        updateICOV();
        updateICOVMedian();
        updateICOVMAD();
        sradProcessor = new SradProcessor();
        
        valleys = new ArrayList<Valley>();
        peaks = new ArrayList<Peak>();
    }
    
    /**
     * This method converts an imagePlus into a double array, where each element of the double array
     * is the mean of the corresponding pixel column from the original image.
     * 
     * @param img
     * @return
     */
    private double[] toProfileArray(ImagePlus img, Rectangle r) {
        byte[] pixels = (byte[]) img.getProcessor().getPixels();
        int width = img.getWidth();
        
        int offset, i;
        Roi roi = img.getRoi();
        if(roi == null) {
            roi = new Roi(0, 0, img.getWidth(), img.getHeight());
        }
        double[] resultArr = new double[r.width];
        
        for (int y = r.y; y < (r.y + r.height); y++) {
            offset = y * width;
            for (int x = r.x; x < (r.x + r.width); x++) {
                i = offset + x;

                int val = pixels[i];
                if(val < 0) {
                    val = val & 0xFF;
                }
                resultArr[x-r.x] += val;
            }
        }
        
        for(int j = 0; j < width; j++) {
            resultArr[j] = resultArr[j] / r.height;
        }
        
        return resultArr;
    }
    
    /**
     * Returns the median of the profile.  Returns -1 if the profile is null
     * 
     * @return
     */
    public double getProfileMedian() {
        if(!medianIsSet) {
            setProfileMedian();
        }
        return profileMedian;
    }
    
    //TODO: Think about how to do this without copying an array, 
    //      or delaying the arraycopy til later
    private void setProfileMedian() {
        medianIsSet = true;
        
        sortedProfile = new double[arr.length];
        System.arraycopy(arr, 0, sortedProfile, 0, arr.length);
        Arrays.sort(sortedProfile);
        profileMedian = sortedProfile[sortedProfile.length/2];
    }
    
    /**
     * This method returns the variance of the profile.  Normally variance is calculated
     * as Var(x) = E[(X - mean)^2] however in this case we are calculating it as
     * Var(x) = E[(X - median)^2]
     * 
     * @return variance of the profile
     */
    public double getProfileVariance() {
        if(!medianIsSet) {
            setProfileMedian();
        }
        if (!varianceIsSet) {
            setProfileVariance();
        }
        
        return profileVariance;
    }
    
    private void setProfileVariance() {
        varianceIsSet = true;
        
        setMean();
        
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += (arr[i] - profileMedian) * (arr[i] - profileMedian);
//            sum += (arr[i] - profileMean) * (arr[i] - profileMean);
        }
        
        profileVariance = sum / arr.length;
    }
    
    private void setMean() {
        int sum = 0;
        for(int i = 0; i < arr.length; i++) {
            sum += arr[i]; 
        }
        
        profileMean = (sum / arr.length);
    }
    
    public double getProfileSTD() {
        if(!varianceIsSet) {
            setProfileVariance();
        }
        
        return Math.sqrt(profileVariance);
        
    }
    
    public double getProfileMax() {
        double max = 0;
        for(int i = 0; i < arr.length; i++) {
            if(arr[i] > max) 
                max = arr[i];
        }
        return max;
    }
    
    public double getProfileMin() {
        double min = Double.MAX_VALUE;
        for(int i = 0; i < arr.length; i++) {
            if(arr[i] < min)
                min = arr[i];
        }
        return min;
    }
    
    public double getICOVMax() {
        double max = 0;
        for(int i = 0; i < icov.length; i++) {
            if(icov[i] > max)
                max = icov[i];
        }
        return max;
    }
    
    public double getICOVMin() {
        double min = Double.MAX_VALUE;
        for(int i = 0; i < icov.length; i++) {
            if(icov[i] < min)
                min = icov[i];
        }
        return min;
    }
    
    /**
     * Returns the median absolute deviation of the profile.  Returns -1 if the 
     * profile is null
     * 
     * @return
     */
    public double getProfileMAD() {
        if(!medianIsSet || sortedProfile == null) {
            getProfileMedian();
        }
        
        if(!madIsSet || profileDeviations == null) {
            setProfileMAD();
        }
        
        return profileMAD;
    }
    
    private void setProfileMAD() {
        profileDeviations = new double[sortedProfile.length];
        
        for(int i = 0; i < sortedProfile.length; i++) {
            profileDeviations[i] = abs(profileMedian - sortedProfile[i]);
        }
        
        Arrays.sort(profileDeviations);
        profileMAD = profileDeviations[profileDeviations.length/2];
        
        madIsSet = true;
    }
    
    /**
     * Initializes the ICOV array
     * 
     * @param profile
     * @return
     */
    private void updateICOV() {
        for (int i = 0; i < arr.length; i++) {
                double gradMag = QualityControlHelper.gradientMagnitude(arr, i);
                double laplacian = QualityControlHelper.laplacian(arr, i);
                icov[i] = sqrt(abs(.5 * pow((gradMag / arr[i]), 2)
                        - (1 / 16) * pow(laplacian / arr[i], 2))
                        / (pow(1 + (1 / 4) * laplacian / arr[i], 2)));
        }
    }
    
    
    /**
     * Returns the median of the ICOV of the profile
     * 
     * @return
     */
    public double getICOVMedian() {
        if (sortedICOV == null) {
            updateICOVMedian();
        }
        
        return icovMedian;
    }
    
    private void updateICOVMedian() {
        if (sortedICOV == null) {
            sortedICOV = new double[icov.length];
        }
        System.arraycopy(icov, 0, sortedICOV, 0, icov.length);
        Arrays.sort(sortedICOV);
        icovMedian = sortedICOV[sortedICOV.length / 2];
    }
    
    /**
     * Get's the median absolute deviation of the ICOV of the profile.  
     * 
     * @return
     */
    public double getICOVMAD() {
       //median must be calculated first, so check if sortedICOV is null
        if(sortedICOV == null) {
            getICOVMedian();
        }
        
        if(icovDeviations == null) {
            updateICOVMAD();
        }
        
        return icovMAD;
    }
    
    private void updateICOVMAD() {
        if(icovDeviations == null)
            icovDeviations = new double[sortedICOV.length];
        
        for(int i = 0; i < sortedICOV.length; i++) {
            icovDeviations[i] = abs(icovMedian - sortedICOV[i]);
        }
        Arrays.sort(icovDeviations);
        icovMAD = icovDeviations[icovDeviations.length/2];
    }
    

    
    public double[] getArr() {
        return arr;
    }
    
    public double[] getICOV() {
        return icov;
    }
    
    private void setPeaksAndValleys() {
        calculateValleys();
        calculatePeaks();
    }
    
    private void calculateValleys() {
        int valleyStartIndex = -1;
        int i = 0;
        boolean inValley = false;
        
        if(!medianIsSet) {
            setProfileMedian();
        }
        if(!madIsSet) {
            setProfileMAD();
        }
        
        
        double valleyMin = Double.MAX_VALUE;
        do
        {
            if(isValleyStart(i, inValley)) {
                valleyStartIndex = i;
                inValley = true;
                valleyMin = Double.MAX_VALUE;
            } else if (isValleyEnd(i, inValley)) {
                //Checking to see if the next column is a valley start so that we can join the two
                //instead of making two separate, adjacent valleys
                if(!isValleyStart(i+1, false)) {
                    valleys.add(new Valley(valleyStartIndex, i, valleyMin));
                    inValley = false;
                }
            }
            
            if(inValley && arr[i] < valleyMin) {
                valleyMin = arr[i];
            }
            
            i++;
        } while( i < arr.length );
        
    }
    
    private boolean isValleyStart(int i, boolean inValley) {
        if(i > arr.length -1 ) {
            return false;
        } 
        
        if(i == 0 && arr[i] <= (profileMedian - profileMAD)) {
            return true;
        } else if (arr[i] <= (profileMedian - profileMAD) && !inValley) {
            return true;
        } 
        
        return false;
    }
    
    private boolean isValleyEnd(int i, boolean inValley) {
        if(i == (arr.length-1) && inValley) {
            return true;
        } else if(arr[i] >= (profileMedian - profileMAD) && inValley) {
            return true;
        }
        return false;
    }
    
    //TODO: Figure out more elegant way to get peaks/valleys 
    private void calculatePeaks() {
        
        int peakStartIndex = -1;
        int i = 0;
        boolean inPeak = false;
        do {
            if(isPeakStart(i, inPeak)) {
                peakStartIndex = i;
                inPeak = true;
            } else if (isPeakEnd(i, inPeak)) {
                peaks.add(new Peak(peakStartIndex, i, icov));
                inPeak = false;
            }
            i++;
        } while( i < arr.length );
    }
    
    private boolean isPeakStart(int i, boolean inPeak) {
        if(i == 0 && icov[i] >= (icovMedian + icovMAD)) {
            return true;
        } else if ((icov[i] >= (icovMedian + icovMAD)) && !inPeak) {
            return true;
        } 
        return false;
    }
    
    private boolean isPeakEnd(int i, boolean inPeak) {
        if(i == (icov.length-1) && inPeak) {
            return true;
        } else if(icov[i] <= (icovMedian + icovMAD) && inPeak) {
            return true;
        }
        return false;
    }

    private void calculateShadows() {
        Peak leftPeak = null, rightPeak = null;
        if(shadows == null) {
            shadows = new ArrayList<Shadow>();
        } else {
            shadows.clear();
        }
            
        for(Valley v : valleys) {
            leftPeak = null; rightPeak = null;
            int quarter = ((v.end - v.start)/4);
            int leftQuartileEnd = v.start + quarter;
            int rightQuartileStart = v.end - quarter;
            for(Peak p : peaks) {
                if(p.start < leftQuartileEnd && p.end > v.start) {
                    leftPeak = p;
                } else if(p.end > rightQuartileStart && p.start < v.end) {
                    rightPeak = p;
                }
            }
            if(leftPeak != null && rightPeak != null && peaksAreUnique(leftPeak, rightPeak)) {
                if(amplitudeCompare(leftPeak, rightPeak))
                shadows.add(new Shadow(v.start, v.end, (int) ((profileMedian - v.min)/ profileMAD), (v.end - v.start)));
            }
        }
    }
    
    private boolean amplitudeCompare(Peak leftPeak, Peak rightPeak) {
        if(!(leftPeak.amplitude > (rightPeak.amplitude * AMPLITUDE_COMPARISON_FACTOR) ||
             leftPeak.amplitude < (rightPeak.amplitude / AMPLITUDE_COMPARISON_FACTOR)))
            return true;
        return false;
    }

    //TODO: make an equals function in Peak
    private boolean peaksAreUnique(Peak leftPeak, Peak rightPeak) {
        if(leftPeak.start != rightPeak.start && leftPeak.end != rightPeak.end) {
            return true;
        }
        return false;
    }
    
    //TODO: optimize this
    public boolean indexIsInValley(int i) {
        for(Valley v : valleys) {
            if(i >= v.start && i <= v.end) {
                return true;
            }
        }
        return false;
    }
    
    //TODO: optimize this
    public boolean indexIsInPeak(int i) {
        for(Peak p : peaks) {
            if(i >= p.start && i <= p.end) {
                return true;
            }
        }
        return false;
    }

    //TODO: optimize this
    public boolean indexIsInShadow(int i) {
        for(Shadow s : shadows) {
            if(i >= s.start && i <= s.end) {
                return true;
            }
        }
        return false;
    }
    
    public int getWidth() {
        return arr.length;
    }
    
    public void clearValleys() {
        valleys.clear();
    }
    
    public void clearPeaks() {
        peaks.clear();
    }
    
    public void clearShadows() {
        shadows = null;
    }
    
    public List<Valley> getValleys() {
        return valleys;
    }
    
    public List<Peak> getPeaks() {
        return peaks;
    }
    
    public List<Shadow> getShadows() {
        if(shadows == null) {
            calculateShadows();
        }
        return shadows;
    }
    
    public void restoreOriginalProfile() {
        System.arraycopy(originalProfile, 0, arr, 0, originalProfile.length);
        //updating icov values to restore those to the original as well
        updateICOV();
        updateICOVMedian();
        updateICOVMAD();
        clearPeaks();
        clearValleys();
        clearShadows();
        meanIsSet = false;
        medianIsSet = false;
        madIsSet = false;
        varianceIsSet = false;
        //TODO: figure out what to do with sradProcessor here
    }
    
    public void applySrad() {
        sradProcessor.applySrad();
    }
    
    public void applySrad(double epsilon, double timestep, double sensitivity) {
        sradProcessor.epsilon = epsilon;
        sradProcessor.timestep = timestep;
        sradProcessor.sensitivity = sensitivity;
        sradProcessor.applySrad();
    }
    
    public double getSradEpsilon() {
        return sradProcessor.epsilon;
    }
    
    public double getSradTimestep() {
        return sradProcessor.timestep;
    }
    
    public double getSradSensitivity() {
        return sradProcessor.sensitivity;
    }
    
    public void setSradEpsilon(Double epsilon) {
        sradProcessor.epsilon = epsilon;
    }
    
    public void setSradTimestep(Double timestep) {
        sradProcessor.timestep = timestep;
    }
    
    public void setSradSensitivity(Double sensitivity) {
        sradProcessor.sensitivity = sensitivity;
    }
    
    private class SradProcessor {

        private double epsilon;
        private double timestep;
        private double sensitivity;

        private double[] divergence;
        private double[] diffCoeff;
        
        /*
         * Originally made this call this(double, double, double) but I couldn't figure out
         * a way to pass it the starting epsilon value since getQnot() isn't static and
         * the call to the other constructor must be the first call of the constructor
         */
        SradProcessor() {
            this.sensitivity = 4.0;
            this.epsilon = getQnot()/8;
            this.timestep = 0.1;
            divergence = new double[arr.length];
            diffCoeff = new double[arr.length];
        }
        
        private void applySrad() {
            double qnot = getQnot();
            int i = 0;
            
            while(true) {
              updateICOV();
              updateICOVMedian();
              updateICOVMAD();
              qnot = getQnot();
              if (qnot < epsilon || i == 100000) {
                  if(i == 100000) {
                      IJ.error("Hit max number of iterations");
                  } else {
                      System.out.println("Finished SRAD");
                  }
                  break;
              }
              updateDiffCoeff(qnot);
              updateDivergence();
              updateProfile(timestep);
              i++;
            } 
            
            setPeaksAndValleys();
        }
        
        
        private void updateProfile(double timestep) {
            for(int i = 0; i < arr.length; i++) {
                arr[i] = (arr[i] + (timestep/4) * divergence[i]);
            }
        }

        private void updateDivergence() {
            for(int i = 1; i < arr.length-1; i++) {
                divergence[i] = diffCoeff[i] * (arr[i+1] - arr[i]) + diffCoeff[i-1]*(arr[i-1] - arr[i]);
            }
        }

        private void updateDiffCoeff(double qnot) {
            for(int i = 1; i < icov.length-1; i++) {
                diffCoeff[i] = exp(-(pow(icov[i],2) - pow(qnot,2)) / (pow(qnot,2) * (1 + pow(qnot,2))));
            }
        }

        private double getQnot() {
            return icovMedian + (sensitivity * sensitivityFactor());
        }
        
        private double sensitivityFactor() {
            double[] tmpIcov = new double[icov.length];
            for(int i = 0; i < icov.length; i++) {
                tmpIcov[i] = abs(icov[i] - icovMedian);
            }
            Arrays.sort(tmpIcov);
            return tmpIcov[tmpIcov.length/2];
        }
    }

    

}

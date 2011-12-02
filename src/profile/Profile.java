package profile;

import static java.lang.Math.abs;
import ij.ImagePlus;
import ij.gui.Roi;

import java.awt.Rectangle;
import java.util.Arrays;

public class Profile {

    protected double[] arr;
    protected double[] sortedProfile = null;
    protected double[] profileDeviations = null;
    
    protected double profileMedian;
    protected double profileMAD;
    protected double profileMean;
    
    private float percentile;

    protected boolean medianIsSet = false;
    protected boolean madIsSet = false;
    private boolean meanIsSet = false;

    public Profile(float[] arr) {
        this(floatToDoubleArray(arr));
    }
    
    public Profile(ImagePlus img, Rectangle r) {
        this(toProfileArray(img, r));
    }
    
    public Profile(double[] arr) {
        this.arr = arr;
    }
    
    /**
     * This method converts an imagePlus into a double array, where each element of the double array
     * is the mean of the corresponding pixel column from the original image.
     * 
     * @param img
     * @return
     */
    protected static double[] toProfileArray(ImagePlus img, Rectangle r) {
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
        
        for(int j = 0; j < resultArr.length; j++) {
            resultArr[j] = resultArr[j] / r.height;
        }
        
        return resultArr;
    }
    
    
    protected static double[] floatToDoubleArray(float[] arr) {
        double[] result = new double[arr.length];
        for(int i = 0; i < arr.length; i++) {
            result[i] = arr[i];
        }
        return result;
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
    
    protected void setProfileMAD() {
        profileDeviations = new double[sortedProfile.length];
        
        for(int i = 0; i < sortedProfile.length; i++) {
            profileDeviations[i] = abs(profileMedian - sortedProfile[i]);
        }
        
        Arrays.sort(profileDeviations);
        profileMAD = profileDeviations[profileDeviations.length/2];
        
        madIsSet = true;
    }

    public double getProfileMedian() {
        if(!medianIsSet) {
            setProfileMedian();
        }
        return profileMedian;
    }
    
    public double getProfileMean() {
        if(!meanIsSet ) {
            setProfileMean();
        }
        return profileMean;
    }
    
    //TODO: Think about how to do this without copying an array, 
    //      or delaying the arraycopy til later
    protected void setProfileMedian() {
        sortedProfile = new double[arr.length];
        System.arraycopy(arr, 0, sortedProfile, 0, arr.length);
        Arrays.sort(sortedProfile);
        
        profileMedian = sortedProfile[sortedProfile.length/2];
        medianIsSet = true;
    }
    
    private void setProfileMean() {
        double sum = 0;
        for(int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        
        profileMean = sum/arr.length;
        meanIsSet = true;
    }

    public double[] getArr() {
        return arr;
    }
    
    public int getWidth() {
        return arr.length;
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
    
}

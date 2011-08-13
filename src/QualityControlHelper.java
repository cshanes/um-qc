/**
 *
 * @author chris
 */
import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

import static java.lang.Math.*;

public class QualityControlHelper {

    /**
     * Returns the gradient magnitude of a given profile at index i
     * 
     * @param prof
     * @param i
     * @return
     */
    public static double gradientMagnitude(double[] prof, int i) {
        if(i == 0) {
            return sqrt( pow(prof[i+1] - prof[i], 2) ) / sqrt(2);
        } else if (i == (prof.length - 1)) {
            return sqrt( pow(prof[i] - prof[i-1], 2) ) / sqrt(2);
        } 
        return sqrt( pow(prof[i+1] - prof[i], 2) + pow(prof[i] - prof[i-1], 2) ) / sqrt(2);
    }
    
    /**
     * Returns the laplacian of a given profile at index i
     * 
     * @param prof
     * @param i
     * @return
     */
    public static double laplacian(double[] prof, int i) {
        if(i == (prof.length -1)) {
            return prof[i] - 2*prof[i];
        }
        return prof[i+1] + prof[i] - 2*prof[i];
    }
    
    /**
     * 
     */
    
    public static ImagePlus getMedianImageFromStack(ImageStack stack) {
        int width = stack.getWidth();
        int height = stack.getHeight();
        int stackSize = stack.getSize();
        ImagePlus resultImg = NewImage.createByteImage("Test", width, height, 1, NewImage.FILL_BLACK);
        ArrayList<byte[]> medianLists = new ArrayList<byte[]>(width * height);
        byte[] curSlice = new byte[width * height];
        
        
        //TODO: speed improvement here would be to use a min-max-median heap to store
        // the values instead of a 2D array that is sorted later
        int offset, i;
        for(int n = 1; n <= stackSize; n++) {
            curSlice = (byte[]) stack.getPixels(n);
            for(int y = 0; y < height; y++) {
                offset = y * width;
                for(int x = 0; x < width; x++) {
                    i = offset + x;
                    if(medianLists.size() < width * height) {
                        medianLists.add(new byte[stackSize]);
                    }
                    (medianLists.get(i))[n-1] = curSlice[i];
                }
            }
        }
        
        ImageProcessor ip = resultImg.getProcessor().createProcessor(width, height);
        byte[] resultArr = (byte[]) ip.getPixels();
        for(int j = 0; j < medianLists.size(); j++) {
            //convert to int array first so that it sorts correctly since all bytes are signed in Java
            int[] arr = toIntArray((byte[]) medianLists.get(j));
            Arrays.sort(arr);
            resultArr[j] = (byte) arr[arr.length/2];
        }
        resultImg.setProcessor(ip);
        
        return resultImg;
    }
    
    private static int[] toIntArray(byte[] arr) {
        int[] result = new int[arr.length];
        for(int i = 0; i < arr.length; i++) {
            result[i] = arr[i] & 0xFF;
        }
        return result;
    }
    
    public static Rectangle findOptimalDepthRegion(ImagePlus img) {
        double cost, maxCost = 0;
        int maxY = 0, maxHeight = 0;
        int halfHeight = img.getHeight()/2;
        
        for(int i = 30; i < halfHeight; i++) {
            for(int j = i+50; j < halfHeight; j++) {
                cost = getProfileCost(img, i, j);
                if(cost > maxCost) {
                    maxCost = cost;
                    maxY = i;
                    maxHeight = j - i;
                }
            }
        }
        
        return new Rectangle(0, maxY, img.getWidth(), maxHeight);
    }
    
    private static double getProfileCost(ImagePlus img, int y, int yBottom) {
        ImagePlus tmpImg = new ImagePlus(img.getTitle(), img.getProcessor());
//        tmpImg.setRoi(0, y, img.getWidth(), yBottom - y);
        Profile profile = new Profile(tmpImg, new Rectangle(0, y, img.getWidth(), yBottom - y));
        return profile.getProfileSTD() - profile.getProfileMAD();
    }
    
    //TODO: add progress bar stuff in this function
    public static ImagePlus createMedianImageFromStackSelection(ImageStack stack, Rectangle selection) {
        return getMedianImageFromStack(stack);
    }
    
    public static void exportProfile(Profile profile, File file) throws IOException {
        FileWriter fw = new FileWriter(file);
        for(int i = 0; i < profile.arr.length; i++) {
            fw.write(String.valueOf(profile.arr[i]) + "\n");
            fw.flush();
        }
        fw.close();
    }
    
}

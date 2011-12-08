import static org.junit.Assert.*;

import java.awt.Rectangle;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import org.junit.Test;

import profile.SradProfile;

public class ProfileTest {
    
    @Test
    public void toProfileArray_correctMean() {
        int width = 10;
        int height = 5;
        
        ImagePlus img = new ImagePlus();
        ImageProcessor imgProc = new ByteProcessor(width, height);
        byte[] pixels = (byte[]) imgProc.getPixels();
        
        for(int y = 0; y < height; y++) {
            int offset = y * width;
            for(int x = 0; x < width; x++) {
                if(y < height/2) 
                    pixels[offset + x] = 10;
                else
                    pixels[offset + x] = 0;
            }
        }
        
        img = new ImagePlus("test", imgProc);
        Rectangle r = new Rectangle(0, 0, width, height);
        SradProfile p = new SradProfile(img, r);
        
        double[] pArray = p.getArr();
        assertEquals((10.0 * 2) / height, pArray[0], 0.1);
    }

}

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import jpaul.DataStructs.UnionFind;

import static java.lang.Math.*;

import ij.ImageStack;
import ij.process.ImageProcessor;

public class TrimUltrasoundHelper {

//    public static ImageStack convertRGBToGreyscale(ImageStack stack) {
//        int[] sliceArr;
//        int offset, j;
//        int width = stack.getWidth();
//        int height = stack.getHeight();
//        
//        byte[] byteArr = null;
//        
//        for(int i = 1; i <= stack.getSize(); i++) {
//            sliceArr = (int[]) stack.getProcessor(i).getPixels();
//            for(int y = 0; y < height; y++) {
//                offset = y * width;
//                for(int x = 0; x < width; x++) {
//                    int blue = sliceArr[offset + x] & 0x000000FF;
//                    int green = (sliceArr[offset + x] & 0x0000FF00) >> 8;
//                    int red = (sliceArr[offset + x] & 0x00FF0000) >> 16;
//                    /* Using weights for the R, G, and B components.  Values were taken from
//                     * MATLAB's documentation on the rgb2gray function.  See 
//                     * http://www.mathworks.com/help/toolbox/images/ref/rgb2gray.html
//                     */
//                    int average = (int) ((.11 * blue) + (.59 * green) + (.3 * red));
//                    sliceArr[offset+x] = 0xFF000000 + (average << 16) + (average << 8) + average;
//                }
//            }
//        }
//        return null;
//    }
//    
//    private static void convertByteStackToIntStack(ImageStack stack) {
//        byte[] curPixels = null;
//        int[] newPixels = null;
//        ColorModel cm = null;
//        for(int i = 1; i <= stack.getSize(); i++) {
//            curPixels = (byte[]) stack.getProcessor(i).getPixels();
//            cm = stack.getProcessor(i).getColorModel();
//            stack.getProcessor(i).convertToRGB();
//        }
//        
//    }
    
//    private static BufferedImage convertByteImgToInt(BufferedImage img) {
//        int width = img.getWidth(); int height = img.getHeight();
//        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        int[] intBuf = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
//        byte[] buf = ((DataBufferByte) img.getRaster().getDataBuffer()).getDatTa();
//        for(int i = 0; i < width * height; i++) {
//            intBuf[i] = byteToInt(buf[i]);
//        }
//        return result;
//    }
//    
//    private static int byteToInt(byte b) {
//        return (int)(b & 0xFF);
//    }
//    
//    private static void convertByteStackToIntStack(ImagePlus img) {
//        StackConverter sc = new StackConverter(img);
//        sc.convertToGray32();
//        ImageConverter ic = new ImageConverter(img);
//        ic.convertToGray32();
//    }
    
    /**
     * 
     * 
     * @param stack
     * @param trimEdges this parameter specifies whether the returned selection has the 
     *          edges trimmed or not
     * @return
     */
    public static Rectangle findScanOutline(ImageStack stack, boolean trimEdges) {
        byte[] reducedDiffMask = getReducedDiffMask(stack);
        
        UnionFind<Integer> uf = createConnectedComponents(reducedDiffMask, 
                                    stack.getWidth(), stack.getHeight());
        
        //find largest connected component
        Collection<Set<Integer>> sets = uf.allNonTrivialEquivalenceClasses();
        Iterator<Set<Integer>> it = sets.iterator();
        //If nothing is found, select the whole image
        int width = stack.getWidth();
        int height = stack.getHeight();
        if(!it.hasNext()) {
            return new Rectangle(0, 0, width, height);
        }
        int maxWidth = 0; int maxHeight = 0;
        int x = -1; int y = -1;
        
        while(it.hasNext()) {
            Set<Integer> s = it.next();
            int setWidth = getMaxX(s, width) - getMinX(s, width);
            int setHeight = getMaxY(s, width) - getMinY(s, width);
            if((setWidth * setHeight) > (maxWidth * maxHeight)) {
                maxWidth = setWidth;
                maxHeight = setHeight;
                x = getMinX(s, width);
                y = getMinY(s, width);
            }
        }
        
        //trim the selection to cut off 5% on the edges.  We do this to remove artifacts from
        // the ultrasound machine
        if(trimEdges) {
            return new Rectangle((int) (x + (maxWidth*.05)), y, (int)(maxWidth - maxWidth*.1), maxHeight);
        }
        return new Rectangle(x, y, maxWidth, maxHeight);
    }
    
    private static UnionFind<Integer> createConnectedComponents(
            byte[] img, int width, int height) {
        UnionFind<Integer> result = new UnionFind<Integer>();
        
        //label the first row
        for(int i = 1; i < width; i++) {
            if(img[i] > 0 && img[i-1] > 0)
                result.union(i, (i-1));
        }
        
        int offset;
        //label rest of rows
        for(int y = 1; y < height; y++) {
            offset = y * width;
            int lastRow = (y-1) * width;
            if(img[offset] > 0 && img[lastRow] > 0) {
                result.union(offset, lastRow);
            }
            
            for(int x = 1; x < width; x++) {
                Integer label = null;
                //get label from pixel on left if in the same blob
                if(img[offset + x] > 0 && img[offset + x - 1] > 0) {
                    label = result.find(offset + x - 1);
                    result.union(label, offset + x);
                }
                //check above pixel
                if(img[lastRow + x] > 0 && img[offset + x] > 0) {
                    if(label != null) {
                        result.union(label, lastRow + x);
                    } else {
                        result.union(lastRow + x, offset + x);
                    }
                }
            }
        }
        
        return result;
    }

    private static byte[] getReducedDiffMask(ImageStack stack) {
        byte[] sliceArr;
        byte[] nextSliceArr;
        byte[] reducedDiffMask = new byte[stack.getWidth() * stack.getHeight()];
        
        for(int i = 1; i < stack.getSize(); i++) { 
            sliceArr = (byte[]) stack.getProcessor(i).getPixels();
            nextSliceArr = (byte[]) stack.getProcessor(i+1).getPixels();
            
            for(int j = 0; j < sliceArr.length; j++) {
                reducedDiffMask[j] += abs(nextSliceArr[j] - sliceArr[j]);
                //Have to check to see if the value is negative because it's possible that the byte value
                //will overflow and be negative, which will mess up the createConnectedComponents function
                if(reducedDiffMask[j] < 0) {
                    reducedDiffMask[j] *= -1;
                }
            }
        }
        
        return reducedDiffMask;
    }
    
    static BufferedImage createDiffMaskImage(int[] diffMask, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte bibuf[] = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        
        int offset;
        for(int y = 0; y < height; y++) {
            offset = y * width;
            for(int x = 0; x < width; x++) {
                if(diffMask[offset + x] > 0)
                    bibuf[offset + x] = (byte)255;
                else 
                    bibuf[offset + x] = 0;
            }
        }
        
        return bi;
    }
    
    public static ImageStack createNewCroppedImageStack(ImageStack stack, Rectangle r) {
        ImageProcessor ip = null;
        ImageStack croppedStack = new ImageStack(r.width, r.height);
        
        for(int i = 1; i <= stack.getSize(); i++) {
            ip = stack.getProcessor(i);
            ip.setRoi(r);
            croppedStack.addSlice(String.valueOf(i), ip.crop());
        }
        
        return croppedStack;
    }
    
    private static Integer getMinX(Set<Integer> set, int width) {
        Integer min = Integer.MAX_VALUE;
        
        for(Integer i : set) {
            if( i % width < min ) {
                min = i % width;
            }
        }
        
        return min;
    }
    
    private static Integer getMaxX(Set<Integer> set, int width) {
        Integer max = -1;
        
        for(Integer i : set) {
            if( i % width > max ) {
                max = i % width;
            }
        }
        
        return max;
    }
    
    private static Integer getMinY(Set<Integer> set, int width) {
        Integer min = Integer.MAX_VALUE;
        
        for(Integer i : set) {
            if( i / width < min ) {
                min = i / width;
            }
        }
        
        return min;
    }
    
    private static Integer getMaxY(Set<Integer> set, int width) {
        Integer max = -1;
        
        for(Integer i : set) {
            if( i / width > max ) {
                max = i / width;
            }
        }
        
        return max;
    }
}

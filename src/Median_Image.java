import util.QualityControlHelper;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.process.StackConverter;


public class Median_Image {

    public Median_Image() {
        this(WindowManager.getCurrentImage());
    }
    
    public Median_Image(ImagePlus img) {
        
        if(img == null) {
            IJ.noImage();
            return;
        }
        
        if(img.getNSlices() < 2) {
            IJ.error("Median Image plugin must be run on a stack");
            return;
        }
        
        if(img.getType() != ImagePlus.GRAY8) {
            StackConverter sc = new StackConverter(img);
            sc.convertToGray8();
        }
        
        ImagePlus medianImg = QualityControlHelper.getMedianImageFromStack(img.getImageStack());
        ImageWindow window = new ImageWindow(medianImg);
        WindowManager.setCurrentWindow(window);
    }
}

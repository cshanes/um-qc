
public interface SelectionModifier {

    public void setYValue(int val);
    
    public void setHeightValue(int val);
    
    /**
     * This function returns true or false whether the object that
     * implements this interface has X values (x and width) 
     * 
     * @return true if object has x and width values
     */
    public boolean hasXValues();
    
    public void setXValue(int val);
    
    public void setWidthValue(int val);
    
}

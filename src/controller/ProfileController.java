package controller;

public class ProfileController extends AbstractSelectionController {
    
    public static final String PROFILE_MAD_PROPERTY = "MAD";
    public static final String PROFILE_PERCENTILE_PROPERTY = "Percentile";
    public static final String PROFILE_USE_PERCENTILE_PROPERTY = "UsePercentile";
    
    public void changeProfileMad(int newMAD) {
        setModelProperty(PROFILE_MAD_PROPERTY, newMAD);
    }
    
    public void changeProfilePercentile(double newP) {
        setModelProperty(PROFILE_PERCENTILE_PROPERTY, newP);
    }
    
    public void changeProfileUsePercentile(boolean usePercentile) {
        setModelProperty(PROFILE_USE_PERCENTILE_PROPERTY, usePercentile);
    }
    
}

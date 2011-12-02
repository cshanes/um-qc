package model;

import profile.Profile;
import controller.ProfileController;

public class ProfileModel extends AbstractModel {

    private int multiplier = 1;
    private float percentile = 0.1f;
    private boolean usePercentile = false;

    public void setMAD(Integer m) {
        int oldM = this.multiplier;
        this.multiplier = m;

        firePropertyChange(ProfileController.PROFILE_MAD_PROPERTY, oldM, m);
    }

    public void setPercentile(Float f) {
        float oldF = this.percentile;
        this.percentile = f;

        firePropertyChange(ProfileController.PROFILE_PERCENTILE_PROPERTY, oldF, f);
    }

    public void setUsePercentile(Boolean b) {
        boolean oldB = this.usePercentile;
        this.usePercentile = b;

        firePropertyChange(ProfileController.PROFILE_USE_PERCENTILE_PROPERTY, oldB, b);
    }

}

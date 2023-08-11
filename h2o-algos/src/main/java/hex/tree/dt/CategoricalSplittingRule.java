package hex.tree.dt;


import java.util.Arrays;

public class CategoricalSplittingRule extends AbstractSplittingRule {

  public CategoricalSplittingRule(int featureIndex, boolean[] mask, double criterionValue) {
    _featureIndex = featureIndex;
    // categories for the left split - bitmask
    _mask = mask;
    _criterionValue = criterionValue;
  }

  public CategoricalSplittingRule(boolean[] mask) {
    _featureIndex = -1; // tmp value, non initialized yet
    _mask = mask;
    _criterionValue = -1; // tmp value, non initialized yet
  }
  
  private final boolean[] _mask;

  public boolean[] getMask() {
    return _mask;
  }


  @Override
  public String toString() {
    return "x" + _featureIndex + " in [" + Arrays.toString(_mask) + "]";
  }

  // true for left, false for right
  public boolean routeSample(double[] sample) {
    int category = (int) sample[_featureIndex];
    return _mask[category];
  }
}

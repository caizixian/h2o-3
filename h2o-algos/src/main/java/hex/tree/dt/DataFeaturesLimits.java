package hex.tree.dt;

import water.fvec.Frame;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Features limits for the whole dataset.
 */
public class DataFeaturesLimits {
    // limits for each feature
    private final List<AbstractFeatureLimits> _featuresLimits;

    public DataFeaturesLimits(final List<AbstractFeatureLimits> featureLimits) {
        this._featuresLimits = featureLimits;
    }

    public DataFeaturesLimits(final double[][] featureLimits) {
        this._featuresLimits = Arrays.stream(featureLimits)
                .map(dd -> new NumericFeatureLimits(dd[0], dd[1]))
                .collect(Collectors.toList());
    }

    public DataFeaturesLimits clone() {
        return new DataFeaturesLimits(_featuresLimits.stream().map(AbstractFeatureLimits::clone).collect(Collectors.toList()));
    }

    /**
     * Creates new instance of limits with updated min.
     *
     * @param selectedFeature feature index to update min
     * @param newMin          new min value for feature
     * @return clone with updated min
     */
    public DataFeaturesLimits updateMin(final int selectedFeature, final double newMin) {
        DataFeaturesLimits clone = new DataFeaturesLimits(
                _featuresLimits.stream().map(AbstractFeatureLimits::clone).collect(Collectors.toList()));
        ((NumericFeatureLimits) clone._featuresLimits.get(selectedFeature)).setNewMin(newMin);
        return clone;
    }

    /**
     * Creates new instance of limits with updated max.
     *
     * @param selectedFeature feature index to update max
     * @param newMax          new max value for feature
     * @return clone with updated max
     */
    public DataFeaturesLimits updateMax(final int selectedFeature, final double newMax) {
        DataFeaturesLimits clone = new DataFeaturesLimits(
                _featuresLimits.stream().map(AbstractFeatureLimits::clone).collect(Collectors.toList()));
        ((NumericFeatureLimits) clone._featuresLimits.get(selectedFeature)).setNewMax(newMax);
        return clone;
    }

    public AbstractFeatureLimits getFeatureLimits(int featureIndex) {
        return _featuresLimits.get(featureIndex);
    }

    /**
     * Serialize limits to 2D double array depending on the features types, so it can be passed to MR task
     *
     * @return
     */
    public double[][] toDoubles() {
        return _featuresLimits.stream()
                .map(AbstractFeatureLimits::toDoubles)
                .toArray(double[][]::new);
    }

    public static double[][] defaultLimits(Frame train) {
        return IntStream.range(0, train.numCols() - 1 /*exclude the last prediction column*/)
                .mapToObj(train::vec).map(v -> v.isNumeric() 
                        ? new double[]{(-1) * Double.MAX_VALUE, Double.MAX_VALUE}
                        : new double[v.cardinality()])
               .toArray(double[][]::new);
    }

    /**
     * Get count of features.
     * @return count of features
     */
    public int featuresCount() {
        return _featuresLimits.size();
    }

    public boolean equals(DataFeaturesLimits other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.featuresCount() != featuresCount()) {
            return false;
        }

        for (int i = 0; i < _featuresLimits.size(); i++) {
            if (!_featuresLimits.get(i).equals(other._featuresLimits.get(i))) {
                return false;
            }
        }
        return true;
    }


}

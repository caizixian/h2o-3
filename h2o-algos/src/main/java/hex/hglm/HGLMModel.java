package hex.hglm;

import hex.DataInfo;
import hex.Model;
import hex.ModelMetrics;
import hex.deeplearning.DeepLearningModel;
import hex.glm.GLM;
import hex.glm.GLMModel;
import water.Key;
import water.fvec.Frame;

import java.io.Serializable;

import static hex.glm.GLMModel.GLMParameters.Family.gaussian;
import static hex.hglm.HGLMModel.HGLMParameters.Method.EM;

public class HGLMModel extends Model<HGLMModel, HGLMModel.HGLMParameters, HGLMModel.HGLMModelOutput> {

  /**
   * Full constructor
   *
   * @param selfKey
   * @param parms
   * @param output
   */
  public HGLMModel(Key<HGLMModel> selfKey, HGLMParameters parms, HGLMModelOutput output) {
    super(selfKey, parms, output);
  }

  @Override
  public ModelMetrics.MetricBuilder makeMetricBuilder(String[] domain) {
    return null;
  }

  @Override
  protected double[] score0(double[] data, double[] preds) {
    return new double[0];
  }

  public static class HGLMParameters extends Model.Parameters {
    public GLMModel.GLMParameters.Family _family = gaussian;
    public GLMModel.GLMParameters.Family _random_family = gaussian;
    public Method _method = EM;
    public double[] _startval;
    public Serializable _missing_values_handling = GLMModel.GLMParameters.MissingValuesHandling.MeanImputation;
    public int _max_iterations = -1;
    public boolean _random_intercept = true;
    public Key<Frame> _plug_values = null;
    public boolean _generate_scoring_history = false;
    public boolean _remove_collinear_columns = false;
    public String[] _random_columns;  // store predictors that have random components in the coefficients
    public String _group_column;
    public boolean _use_all_factor_levels = false;
    public boolean _standardize = true;

    @Override
    public String algoName() {
      return "HGLM";
    }

    @Override
    public String fullName() {
      return "Hierarchical Generalized Linear Model";
    }

    @Override
    public String javaName() {
      return HGLMModel.class.getName();
    }

    @Override
    public long progressUnits() {
      return 1;
    }

    public enum Method {EM}; // EM: expectation maximization

    public GLMModel.GLMParameters.MissingValuesHandling missingValuesHandling() {
      if (_missing_values_handling instanceof GLMModel.GLMParameters.MissingValuesHandling)
        return (GLMModel.GLMParameters.MissingValuesHandling) _missing_values_handling;
      assert _missing_values_handling instanceof DeepLearningModel.DeepLearningParameters.MissingValuesHandling;
      switch ((DeepLearningModel.DeepLearningParameters.MissingValuesHandling) _missing_values_handling) {
        case MeanImputation:
          return GLMModel.GLMParameters.MissingValuesHandling.MeanImputation;
        case Skip:
          return GLMModel.GLMParameters.MissingValuesHandling.Skip;
        default:
          throw new IllegalStateException("Unsupported missing values handling value: " + _missing_values_handling);
      }
    }

    public DataInfo.Imputer makeImputer() {
      if (missingValuesHandling() == GLMModel.GLMParameters.MissingValuesHandling.PlugValues) {
        if (_plug_values == null || _plug_values.get() == null) {
          throw new IllegalStateException("Plug values frame needs to be specified when Missing Value Handling = PlugValues.");
        }
        return new GLM.PlugValuesImputer(_plug_values.get());
      } else { // mean/mode imputation and skip (even skip needs an imputer right now! PUBDEV-6809)
        return new DataInfo.MeanImputer();
      }
    }
  }
  
  public static class HGLMModelOutput extends Model.Output {
    public DataInfo _dinfo;
    final GLMModel.GLMParameters.Family _family;
    final GLMModel.GLMParameters.Family _random_family;
    public String[] _coefficient_names;
    public String[] _random_coefficient_names;
    public long _training_time_ms;
    double[] _beta;   // fixed coefficients
    double[] _ubeta;  // random coefficients
    
    
    public HGLMModelOutput(HGLM b, DataInfo dinfo) {
       super(b, dinfo._adaptedFrame);
       _dinfo = dinfo;
       _domains = dinfo._adaptedFrame.domains();
       _family = b._parms._family;
       _random_family = b._parms._random_family;
    }
  }
}

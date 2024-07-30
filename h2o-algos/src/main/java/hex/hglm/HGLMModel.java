package hex.hglm;

import hex.Model;
import hex.ModelMetrics;
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

    public static enum Method {EM}; // EM: expectation maximization
  }
  
  public static class HGLMModelOutput extends Model.Output {
    
  }
}

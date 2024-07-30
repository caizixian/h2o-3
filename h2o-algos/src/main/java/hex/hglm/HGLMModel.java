package hex.hglm;

import hex.Model;
import hex.ModelMetrics;
import water.Key;

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

    public static enum Method {"EM"}; // EM: expectation maximization
  }
  
  public static class HGLMModelOutput extends Model.Output {
    
  }
}

package hex.hglm;

import org.junit.Test;
import water.TestUtil;

public class GLMBasicTestHGLM extends TestUtil {

  @Test
  public void testSemiconductor() {
/*    try {
      Scope.enter();
      Frame fr = parseTestFile("smalldata/glm_test/semiconductor.csv");
      fr.replace(0, fr.vec(0).toCategoricalVec()).remove();
      DKV.put(fr);
      Scope.track(fr);
      GLMParameters parms = new GLMParameters();
      parms._train = fr._key;
      parms._response_column = "y";
      parms._ignored_columns = new String[]{"x2", "x4", "Device"};
      parms._ignore_const_cols = true;
      parms._family = Family.gaussian;
      parms._link = GLMParameters.Link.identity;
      parms._HGLM = true;
      parms._rand_family = new Family[]{Family.gaussian};
      parms._rand_link = new GLMParameters.Link[]{GLMParameters.Link.identity};
      parms._random_columns = new int[]{0};
      parms._calc_like = true;

      // just make sure it runs
      GLMModel model = new GLM(parms).trainModel().get();
      Scope.track_generic(model);
      ModelMetricsHGLMGaussianGaussian mmetrics = (ModelMetricsHGLMGaussianGaussian) model._output._training_metrics;
      Scope.track_generic(mmetrics);
      assertEquals(363.6833, mmetrics._hlik, 1e-4);
      System.out.println("**************** testSemiconductor test completed. ****************");
    } finally {
      Scope.exit();
    }*/
  }

  @Test
  public void testMultiChunkData(){
/*    try {
      Scope.enter();
      Frame fr = parseTestFile("smalldata/glm_test/HGLM_5KRows_100Z.csv");
      fr.replace(0, fr.vec(0).toCategoricalVec()).remove();
      fr.replace(1, fr.vec(1).toCategoricalVec()).remove();
      fr.replace(2, fr.vec(2).toCategoricalVec()).remove();
      fr.replace(3, fr.vec(3).toCategoricalVec()).remove();
      DKV.put(fr);
      Scope.track(fr);
      GLMParameters parms = new GLMParameters();
      parms._train = fr._key;
      parms._response_column = "response";
      parms._ignored_columns = new String[]{"Z"};
      parms._ignore_const_cols = true;
      parms._family = Family.gaussian;
      parms._link = GLMParameters.Link.identity;
      parms._HGLM=true;
      parms._rand_family = new Family[] {Family.gaussian};
      parms._rand_link = new GLMParameters.Link[] {GLMParameters.Link.identity};
      parms._random_columns = new int[]{0};
      parms._calc_like = true;

      // just make sure it runs
      GLMModel model = new GLM(parms).trainModel().get();
      Scope.track_generic(model);
      ModelMetricsHGLMGaussianGaussian mmetrics = (ModelMetricsHGLMGaussianGaussian) model._output._training_metrics;
      Scope.track_generic(mmetrics);
      assertEquals(-23643.3076231, mmetrics._hlik, 1e-4);
    } finally {
      Scope.exit();
    }*/
  }
}

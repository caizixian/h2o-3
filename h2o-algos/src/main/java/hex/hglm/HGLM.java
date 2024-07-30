package hex.hglm;

import hex.ModelBuilder;
import hex.ModelCategory;
import water.H2O;
import water.Key;
import water.exceptions.H2OModelBuilderIllegalArgumentException;
import water.fvec.Frame;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static hex.glm.GLMModel.GLMParameters.Family.gaussian;
import static hex.hglm.HGLMModel.HGLMParameters.Method.EM;

public class HGLM extends ModelBuilder<HGLMModel, HGLMModel.HGLMParameters, HGLMModel.HGLMModelOutput> {
  @Override
  public ModelCategory[] can_build() {
    return new ModelCategory[]{ModelCategory.Regression};
  }

  @Override
  public boolean isSupervised() {
    return true;
  }

  @Override
  public BuilderVisibility builderVisibility() {
    return BuilderVisibility.Experimental;
  }

  @Override
  public boolean havePojo() {
    return false;
  }

  @Override
  public boolean haveMojo() {
    return true;
  }
  
  public HGLM(boolean startup_once) {
    super(new HGLMModel.HGLMParameters(), startup_once);
  }
  
  protected HGLM(HGLMModel.HGLMParameters parms) {
    super(parms);
    init(false);
  }
  
  public HGLM(HGLMModel.HGLMParameters parms, Key<HGLMModel> key) {
    super(parms, key);
    init(false);
  }
  
  @Override
  protected ModelBuilder<HGLMModel, HGLMModel.HGLMParameters, HGLMModel.HGLMModelOutput>.Driver trainModelImpl() {
    return new HGLMDriver();
  }

  @Override
  public void init(boolean expensive) {
    if (_parms._nfolds > 0 || _parms._fold_column != null)
      error("nfolds or _fold_coumn", " cross validation is not supported in HGLM right now.");
    
    if (null != _parms._family && !gaussian.equals(_parms._family))
      error("family", " only Gaussian families are supported now");
    
    if (null != _parms._method && EM.equals(_parms._method))
      error("method", " only EM (expectation maximization) is supported for now.");
    
    super.init(expensive);
    if (error_count() > 0)
      throw H2OModelBuilderIllegalArgumentException.makeFromBuilder(HGLM.this);
    if (expensive) {
      if (_parms._max_iterations == 0)
        error("_max_iterations", H2O.technote(2, "if specified, must be >= 1."));
      
      if (_parms._group_column == null) {
        error("group_column", " column used to generate level 2 units is missing");
      } else {
        Frame trainFrame = train();
        List<String> columnNames = Arrays.stream(trainFrame.names()).collect(Collectors.toList());
        if (!columnNames.contains(_parms._group_column))
          error("group_column", " is not found in the training frame.");
        
        if (trainFrame.vec())
      }
      
    }
  }

  private class HGLMDriver extends Driver {

    @Override
    public void computeImpl() {
      init(true);
      if (error_count()>0)
        throw H2OModelBuilderIllegalArgumentException.makeFromBuilder(HGLM.this);
      
    }
  }
  
}

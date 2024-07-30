package hex.hglm;

import hex.ModelBuilder;
import hex.ModelCategory;
import water.Key;

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
      error("_nfods or _fod_coumn", " cross validation is not supported in HGLM right now.");
    
    if (null != _parms._family && )
      
    
  }

  private class HGLMDriver extends Driver {

    @Override
    public void computeImpl() {
      init(true);
      
    }
  }
  
}

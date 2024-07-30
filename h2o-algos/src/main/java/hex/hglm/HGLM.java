package hex.hglm;

import hex.ModelBuilder;
import hex.ModelCategory;

public class HGLM extends ModelBuilder<HGLMModel, HGLMModel.HGLMParameters, HGLMModel.HGLMModelOutput> {
  @Override
  public ModelCategory[] can_build() {
    return new ModelCategory[]{ModelCategory.Regression};
  }
  
  @Override
  protected ModelBuilder<HGLMModel, HGLMModel.HGLMParameters, HGLMModel.HGLMModelOutput>.Driver trainModelImpl() {
    return null;
  }



  @Override
  public boolean isSupervised() {
    return true;
  }
  
  @Override
  protected HGLM(HGLMModel.HGLMParameters parms) {
    super(parms);
  }
}

package hex.hglm;

import hex.ModelBuilder;
import hex.ModelCategory;

public class HGLM extends ModelBuilder<HGLMModel, HGLMModel.HGLMParameters, HGLMModel.HGLMModelOutput> {
  protected HGLM(HGLMModel.HGLMParameters parms) {
    super(parms);
  }

  @Override
  protected ModelBuilder<HGLMModel, HGLMModel.HGLMParameters, HGLMModel.HGLMModelOutput>.Driver trainModelImpl() {
    return null;
  }

  @Override
  public ModelCategory[] can_build() {
    return new ModelCategory[0];
  }

  @Override
  public boolean isSupervised() {
    return true;
  }
}

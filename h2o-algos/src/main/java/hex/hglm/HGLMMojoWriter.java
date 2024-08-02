package hex.hglm;

import hex.ModelMojoWriter;

import java.io.IOException;

public class HGLMMojoWriter extends ModelMojoWriter<HGLMModel, HGLMModel.HGLMParameters, HGLMModel.HGLMModelOutput> {
  @Override
  public String mojoVersion() {
    return "1.00";
  }
  
  @SuppressWarnings("unused")
  public HGLMMojoWriter() {}
  
  public HGLMMojoWriter(HGLMModel model) {
    super(model);
  }
  
  @Override
  protected void writeModelData() throws IOException {
    writekv("use_all_factor_levels", model._parms._use_all_factor_levels);
  }
}

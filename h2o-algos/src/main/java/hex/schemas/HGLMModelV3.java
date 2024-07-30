package hex.schemas;

import hex.Model;
import hex.glm.GLMModel;
import hex.hglm.HGLMModel;
import water.api.schemas3.ModelOutputSchemaV3;
import water.api.schemas3.ModelSchemaV3;

import static hex.glm.GLMModel.GLMParameters.Family.gaussian;

public class HGLMModelV3 extends ModelSchemaV3<HGLMModel, HGLMModelV3, HGLMModel.HGLMParameters, HGLMV3.HGLMParametersV3, 
        HGLMModel.HGLMModelOutput, HGLMModelV3.HGLMModelOutputV3> {
  public static final class HGLMModelOutputV3 extends ModelOutputSchemaV3<HGLMModel.HGLMModelOutput, HGLMModelOutputV3> {
    
  }
  
  public HGLMV3.HGLMParametersV3 createParametersSchema() { return new HGLMV3.HGLMParametersV3(); }
  public HGLMModelOutputV3 createOutputSchema() { return new HGLMModelOutputV3(); }
  
  @Override
  public HGLMModel createImpl() {
    HGLMModel.HGLMParameters parms = parameters.createImpl();
    return new HGLMModel(model_id.key(), parms, null);
  }
  
  public static class HGLMParameters extends Model.Parameters {
    public boolean _standardize = false;
    public GLMModel.GLMParameters.Family _family = gaussian;
    public GLMModel.GLMParameters.Link _link = GLMModel.GLMParameters.Link.family_default;

    @Override
    public String algoName() {
      return "HGLM";
    }

    @Override
    public String fullName() {
      return "Hierarchical Generalized Model";
    }

    @Override
    public String javaName() {
      return HGLMModel.class.getName();
    }

    @Override
    public long progressUnits() {
      return 1;
    }
  }
}

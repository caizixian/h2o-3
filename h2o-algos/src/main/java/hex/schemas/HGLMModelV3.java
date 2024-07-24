package hex.schemas;

import hex.hglm.HGLMModel;
import water.api.schemas3.ModelOutputSchemaV3;
import water.api.schemas3.ModelSchemaV3;

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
}

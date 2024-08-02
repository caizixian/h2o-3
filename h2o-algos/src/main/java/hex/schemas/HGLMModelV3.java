package hex.schemas;

import hex.hglm.HGLMModel;
import water.api.API;
import water.api.schemas3.ModelOutputSchemaV3;
import water.api.schemas3.ModelSchemaV3;
import water.api.schemas3.TwoDimTableV3;

public class HGLMModelV3 extends ModelSchemaV3<HGLMModel, HGLMModelV3, HGLMModel.HGLMParameters, HGLMV3.HGLMParametersV3, 
        HGLMModel.HGLMModelOutput, HGLMModelV3.HGLMModelOutputV3> {
  public static final class HGLMModelOutputV3 extends ModelOutputSchemaV3<HGLMModel.HGLMModelOutput, HGLMModelOutputV3> {
    @API(help="Table of Fixed Coefficients")
    TwoDimTableV3 fixed_coefficients_table;
    
    @API(help="Table of Random Coefficients")
    TwoDimTableV3 random_coefficients_table;

    @API(help="Table of Fixed Coefficients standardized")
    TwoDimTableV3 fixed_coefficients_table_standardized;

    @API(help="Table of Random Coefficients standardized")
    TwoDimTableV3 random_coefficients_table_standardized;
    
  }
  
  public HGLMV3.HGLMParametersV3 createParametersSchema() { return new HGLMV3.HGLMParametersV3(); }
  public HGLMModelOutputV3 createOutputSchema() { return new HGLMModelOutputV3(); }
  
  @Override
  public HGLMModel createImpl() {
    HGLMModel.HGLMParameters parms = parameters.createImpl();
    return new HGLMModel(model_id.key(), parms, null);
  }
}

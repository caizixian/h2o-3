package hex.hglm;

import hex.DataInfo;
import hex.ModelBuilder;
import hex.ModelCategory;
import hex.glm.GLMModel;
import water.DKV;
import water.H2O;
import water.Key;
import water.exceptions.H2OModelBuilderIllegalArgumentException;
import water.fvec.Frame;
import water.rapids.Rapids;
import water.rapids.Val;

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

      Frame trainFrame = train();
      List<String> columnNames = Arrays.stream(trainFrame.names()).collect(Collectors.toList());
      if (_parms._group_column == null) {
        error("group_column", " column used to generate level 2 units is missing");
      } else {
        if (!columnNames.contains(_parms._group_column))
          error("group_column", " is not found in the training frame.");
        else if (!trainFrame.vec(_parms._group_column).isCategorical())
          error("group_column", " should be a categorical column.");
      }
      
      if (_parms._random_columns == null) {
        error("random_columns", " must contain columns with random effect and cannot be empty.");
      } else {
        boolean goodRandomColumns = (Arrays.stream(_parms._random_columns).filter(x -> columnNames.contains(x)).count()
                == _parms._random_columns.length);
        if (!goodRandomColumns)
          error("random_columns", " can only contain columns in the training frame.");
      }
      
      if (valid() != null)
        error("validatiion_frame", " is not supported at this point.");
    }
  }

  private class HGLMDriver extends Driver {
    DataInfo _dinfo = null;
    
    // this method will first sort the training frame according to the group_column.  Before sorting happens, any
    // NA's in the training frame will be imputed with the mode.
    Frame adaptTrain() {
      Frame trainingFrame = train();
      // replace NAs in enum and numerical columns with mode/mean
      Val val = Rapids.exec(String.format("(na.replace.mean.mode %s)", trainingFrame._key));
      Frame trainingFrameNoNA = val.getFrame();
      // sort the frame with respect to the group_column
      int groupColumnIndex = (Arrays.stream(trainingFrame.names()).collect(Collectors.toList())).indexOf(_parms._group_column);
      Frame sortedTrainingFrame = trainingFrameNoNA.sort(new int[]{groupColumnIndex});
      trainingFrameNoNA.remove();
      return sortedTrainingFrame;
    }

    @Override
    public void computeImpl() {
      init(true);
      if (error_count()>0)
        throw H2OModelBuilderIllegalArgumentException.makeFromBuilder(HGLM.this);
      // generate the new training frame which contains the predictors with fixed coefficients, sorted according to
      // the grouping dictated by the group_column
      Frame newTFrame = new Frame(rebalance(adaptTrain(), false, Key.make()+".temprory.train"));
      DKV.put(newTFrame);
      _job.update(0, "Initializing HGLM model training");
      
      HGLMModel model = null;
      
      try {
        _dinfo = new DataInfo(newTFrame, null, 1, _parms._use_all_factor_levels,  _parms._standardize ?
                DataInfo.TransformType.STANDARDIZE : DataInfo.TransformType.NONE, DataInfo.TransformType.NONE,
                _parms.missingValuesHandling() == GLMModel.GLMParameters.MissingValuesHandling.Skip,
                _parms.missingValuesHandling() == GLMModel.GLMParameters.MissingValuesHandling.MeanImputation
                        || _parms.missingValuesHandling() == GLMModel.GLMParameters.MissingValuesHandling.PlugValues,
                _parms.makeImputer(), false, hasWeightCol(), hasOffsetCol(), hasFoldCol(), null);
        DKV.put(_dinfo._key, _dinfo);
        model = new HGLMModel(dest(), _parms, new HGLMModel.HGLMModelOutput(HGLM.this, _dinfo));
        model.write_lock(_job);
        _job.update(1, "Starting to build HGLM model...");
                
        
      } finally {
        if (model != null) {
          DKV.remove(newTFrame._key);
        }
        model.update(_job);
        model.unlock(_job);
      }
    }
  }
}

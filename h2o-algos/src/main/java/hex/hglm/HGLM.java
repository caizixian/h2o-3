package hex.hglm;

import hex.DataInfo;
import hex.ModelBuilder;
import hex.ModelCategory;
import hex.glm.GLMModel;
import water.*;
import water.exceptions.H2OModelBuilderIllegalArgumentException;
import water.fvec.Frame;
import water.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static hex.glm.GLMModel.GLMParameters.Family.gaussian;
import static hex.glm.GLMModel.GLMParameters.MissingValuesHandling.MeanImputation;
import static hex.glm.GLMModel.GLMParameters.MissingValuesHandling.Skip;
import static hex.hglm.HGLMModel.HGLMParameters.Method.EM;
import static hex.hglm.HGLMUtils.readRandomEffectInitFrame;
import static water.util.ArrayUtils.gaussianVector;

public class HGLM extends ModelBuilder<HGLMModel, HGLMModel.HGLMParameters, HGLMModel.HGLMModelOutput> {
  long _startTime;  // model building start time;
  private transient ComputationStateHGLM _state;
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
    
    if (null != _parms._missing_values_handling && 
            (MeanImputation != _parms._missing_values_handling ||
                    Skip != _parms._missing_values_handling))
      error("mising_values_handling", " only MeanImputation and Skip are supported at this point.");
    
    if (_parms._tau_u_var_init < 0)
      error("tau_u_var_init", "if set, must > 0.0.");

    if (_parms._tau_e_var_init < 0)
      error("tau_e_var_init", "if set, must > 0.0.");
    
    if (_parms._seed == 0)
      error("seed", "cannot be set to any number except zero.");
    
    if (_parms._beta_epsilon < 0)
      error("beta_epsilon", "if specified, must >= 0.0.");

    if (_parms._objective_epsilon < 0)
      error("objective_epsilon", "if specified, must >= 0.0.");
    
    super.init(expensive);
    if (error_count() > 0)
      throw H2OModelBuilderIllegalArgumentException.makeFromBuilder(HGLM.this);
    if (expensive) {
      if (_parms._max_iterations == 0) {
        warn("max_iterations", "for HGLM, must be >= 1 (or -1 for unlimited or default setting) " +
                "to obtain proper model.  Setting it to be 0 will only return the correct coefficient names and an empty" +
                " model.");
        warn("_max_iterations", H2O.technote(2 , "for HGLM, if specified, must be >= 1 or == -1."));
      }
      
      if (_parms._max_iterations == -1)
        _parms._max_iterations = 500;

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
      if (!_parms._use_all_factor_levels)
        _parms._random_intercept = true;
    }
  }

  private class HGLMDriver extends Driver {
    DataInfo _dinfo = null;
    String[] _fixedCoeffNames;
    String[] _randomCoefNames;
    String[] _level2UnitNames;

    @Override
    public void computeImpl() {
      _startTime = System.currentTimeMillis();
      init(true);
      if (error_count()>0)
        throw H2OModelBuilderIllegalArgumentException.makeFromBuilder(HGLM.this);

      _job.update(0, "Initializing HGLM model training");
      HGLMModel model = null;
      
      try {
        /***
         * Need to do the following things:
         * 1. Generate all the various coefficient names;
         * 2. Initialize the coefficient values (fixed and random)
         * 3. Set modelOutput fields.
         */
        initDinfoColumnNames();
        initComputationStateHGLM();

        model = new HGLMModel(dest(), _parms, new HGLMModel.HGLMModelOutput(HGLM.this, _dinfo));
        model.write_lock(_job);
        _job.update(1, "Starting to build HGLM model...");
        if (EM == _parms._method)     
          fitEM(model);
        model._output.setModelOutputFields(_state);
        model._output._start_time = _startTime;
        model._output._training_time_ms = System.currentTimeMillis()-_startTime;
      } finally {
        model.update(_job);
        model.unlock(_job);
      }
    }
    
    void initDinfoColumnNames() {
      // _dinfo._adaptedFrame will contain group_column.  Check and make sure clients will pass that along as well.
      _dinfo = new DataInfo(_train.clone(), null, 1, _parms._use_all_factor_levels,  _parms._standardize ?
              DataInfo.TransformType.STANDARDIZE : DataInfo.TransformType.NONE, DataInfo.TransformType.NONE,
              _parms.missingValuesHandling() == Skip,
              _parms.missingValuesHandling() == MeanImputation
                      || _parms.missingValuesHandling() == GLMModel.GLMParameters.MissingValuesHandling.PlugValues,
              _parms.makeImputer(), false, hasWeightCol(), hasOffsetCol(), hasFoldCol(), null);
      DKV.put(_dinfo._key, _dinfo);
      // assign coefficient names for fixed, random and group column
      List<String> allCoeffNames = Arrays.stream(_dinfo.coefNames()).collect(Collectors.toList());
      String groupCoeffStarts = _parms._group_column+".";
      List<String> groupCoeffNames =allCoeffNames.stream().filter(x -> x.startsWith(groupCoeffStarts)).collect(Collectors.toList());
      _level2UnitNames = groupCoeffNames.stream().toArray(String[]::new);
      
      // fixed Coefficients are all coefficient names excluding group_column
      List<String> fixedCoeffNames = allCoeffNames.stream().filter(x -> !groupCoeffNames.contains(x)).collect(Collectors.toList());
      fixedCoeffNames.add("intercept");
      _fixedCoeffNames = fixedCoeffNames.stream().toArray(String[]::new);
      List<String> randomPredictorNames = new ArrayList<>();
      // random coefficients names
      for (String coefName : _parms._random_columns) {
        String startCoef = coefName + ".";
        randomPredictorNames.addAll(allCoeffNames.stream().filter(x -> x.startsWith(startCoef) || x.equals(coefName)).collect(Collectors.toList()));
      }
      if (_parms._random_intercept)
        randomPredictorNames.add("intercept");  // set intercept to be the last element
      _randomCoefNames = randomPredictorNames.stream().toArray(String[]::new);
    }
    
    void initComputationStateHGLM() {
      // need to initialize the coefficients, fixed and random
      if (_parms._seed == -1)
        _parms._seed = new Random().nextLong();
      Log.info("Random seed: "+_parms._seed);
      
      double[][] ubeta = new double[_level2UnitNames.length][_randomCoefNames.length];
      double[] beta;
      Random random = new Random(_parms._seed);
      if ( null != _parms._initial_random_effects) {  // read in initial random values
        List<String> randomCoeffNames = Arrays.stream(_randomCoefNames).collect(Collectors.toList());
        
        Frame randomEffects = DKV.getGet(_parms._initial_random_effects);
        Scope.track(randomEffects);
        if (randomEffects.numRows() != ubeta.length || randomEffects.numCols() != ubeta[0].length)
          error("initial_random_effects", "Initial random coefficients must be a double[][] array" +
                  " of size "+randomEffects.numRows()+" rows and "+randomEffects.numCols()+" columns but is not.");
        if (error_count() > 0)
          throw H2OModelBuilderIllegalArgumentException.makeFromBuilder(HGLM.this);

        readRandomEffectInitFrame(randomEffects, ubeta, randomCoeffNames);
      } else {  // randomly generating random initial values
         gaussianVector(random, ubeta, _level2UnitNames.length, _randomCoefNames.length);
      }
      // copy over initial fixed coefficient values
      if (null != _parms._initial_fixed_effects) {
        if (_parms._initial_fixed_effects.length != _fixedCoeffNames.length)
          error("initial_fixed_effects", " must be an double[] array of size "+_fixedCoeffNames.length);
        if (error_count() > 0)
          throw H2OModelBuilderIllegalArgumentException.makeFromBuilder(HGLM.this);
        beta = _parms._initial_fixed_effects;  
      } else {
        beta = new double[_fixedCoeffNames.length];
        beta[beta.length-1] = train().vec(_parms._response_column).mean();
      }
      
      double tauEVar, tauUVar;
      if (_parms._tau_e_var_init != 0.0)
        tauEVar = _parms._tau_e_var_init;
      else
        tauEVar = Math.abs(random.nextGaussian());
      
      if (_parms._tau_u_var_init != 0.0)
        tauUVar = _parms._tau_u_var_init;
      else
        tauUVar = Math.abs(random.nextGaussian());
        
      _state = new ComputationStateHGLM(_job, _parms, _dinfo, beta, ubeta, tauUVar, tauEVar, _fixedCoeffNames, 
              _randomCoefNames, _level2UnitNames, 0);
    }

    /**
     * Build HGLM model using EM (Expectation Maximization).
     */
    void fitEM(HGLMModel model) {
      int iteration = 0;
      
      try {
        // grab current value of fixed beta, tauEVar, tauUVar
        // estimate CDSS and get the estimate the random beta
        // substitue estimated CDSS and estimate new fixed beta, tauEVar, tauUVar
        // save newly estimated fixed beta, tauEVar, tauUVar into _state
        // check if stopping conditions are satisfied
      } catch (Exception e) {
        
      }
    }
  }
}

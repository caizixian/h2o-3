package hex.schemas;

import hex.glm.GLMModel.GLMParameters;
import hex.glm.GLMModel.GLMParameters.Solver;
import hex.hglm.HGLM;
import hex.hglm.HGLMModel;
import water.api.API;
import water.api.API.Direction;
import water.api.API.Level;
import water.api.schemas3.KeyV3.FrameKeyV3;
import water.api.schemas3.ModelParametersSchemaV3;

public class HGLMV3 extends ModelBuilderSchema<HGLM, HGLMV3, HGLMV3.HGLMParametersV3> {

  public static final class HGLMParametersV3 extends ModelParametersSchemaV3<HGLMModel.HGLMParameters, HGLMParametersV3> {
    public static final String[] fields = new String[]{
            "model_id",
            "training_frame",
            "validation_frame",
            "checkpoint",
            "export_checkpoints_dir",
            "seed",
            "response_column",
            "ignored_columns",
            "random_columns",
            "ignore_const_cols",
            "score_each_iteration",
            "score_iteration_interval",
            "offset_column",
            "weights_column",
            "family",
            "rand_family", // distribution of random component, array
            "method",
            "standardize",
            "missing_values_handling",
            "plug_values",
            "compute_p_values",
            "remove_collinear_columns",
            "max_iterations",
            "objective_epsilon",
            "beta_epsilon",
            "gradient_epsilon",
            "startval",  // initial starting values for fixed and randomized coefficients, double array
            "calc_like",
            "obj_reg",
            "max_runtime_secs",
            "custom_metric_func",
            "generate_scoring_history",
    };

    @API(help = "Seed for pseudo random number generator (if applicable).", gridable = true)
    public long seed;

    // Input fields
    @API(help = "Family. Use binomial for classification with logistic regression, others are for regression problems.",
            values = {"AUTO", "gaussian"}, level = Level.critical)
    // took tweedie out since it's not reliable
    public GLMParameters.Family family;
    
    @API(help = "Random Component Family array.  One for each random component. Only support gaussian for now.",
            values ={"[gaussian]"},
            level = Level.critical, gridable=true)
    public GLMParameters.Family[] rand_family;

    @API(help = "AUTO will set the solver based on given data and the other parameters. IRLSM is fast on on problems" +
            " with small number of predictors and for lambda-search with L1 penalty, L_BFGS scales better for datasets" +
            " with many columns.", values = {"AUTO", "IRLSM", "L_BFGS","COORDINATE_DESCENT_NAIVE", 
            "COORDINATE_DESCENT", "GRADIENT_DESCENT_LH", "GRADIENT_DESCENT_SQERR"}, level = Level.critical)
    public Solver method;

    @API(help = "Number of lambdas to be used in a search." +
    " Default indicates: If alpha is zero, with lambda search" +
    " set to True, the value of nlamdas is set to 30 (fewer lambdas" +
    " are needed for ridge regression) otherwise it is set to 100.", level = Level.critical)
    public int nlambdas;
    
    @API(help = "Perform scoring for every score_iteration_interval iterations.", level = Level.secondary)
    public int score_iteration_interval;

    @API(help = "Standardize numeric columns to have zero mean and unit variance.", level = Level.critical,
            gridable = true)
    public boolean standardize;

    @API(help = "Handling of missing values. Either MeanImputation, Skip or PlugValues.", 
            values = { "MeanImputation", "Skip", "PlugValues" }, level = API.Level.expert, 
            direction=API.Direction.INOUT, gridable = true)
    public GLMParameters.MissingValuesHandling missing_values_handling;

    @API(help = "Plug Values (a single row frame containing values that will be used to impute missing values of the" +
            " training/validation frame, use with conjunction missing_values_handling = PlugValues).", 
            direction = API.Direction.INPUT)
    public FrameKeyV3 plug_values;

    @API(help = "Maximum number of iterations.  Value should >=1.  A value of 0 is only set when only the model " +
            "coefficient names and model coefficient dimensions are needed.", level = Level.secondary)
    public int max_iterations;

    @API(help = "Converge if beta changes less (using L-infinity norm) than beta esilon. ONLY applies to IRLSM solver."
            , level = Level.expert)
    public double beta_epsilon;

    @API(help = "Converge if  objective value changes less than this."+ " Default (of -1.0) indicates: If lambda_search"+
            " is set to True the value of objective_epsilon is set to .0001. If the lambda_search is set to False" +
            " and lambda is equal to zero, the value of objective_epsilon is set to .000001, for any other value" +
            " of lambda the default value of objective_epsilon is set to .0001.", level = API.Level.expert)
    public double objective_epsilon;

    @API(help="Likelihood divider in objective value computation, default (of -1.0) will set it to 1/nobs.")
    public double obj_reg;
    
    @API(help="Method used to estimate the dispersion parameter for Tweedie, Gamma and Negative Binomial only.",
            level = Level.secondary, values={"deviance", "pearson", "ml"})
    public GLMParameters.DispersionMethod dispersion_parameter_method;

    @API(help = "double array to initialize fixed and random coefficients for HGLM, coefficients for GLM.  If " +
            "standardize is true, the standardized coefficients should be used.  Otherwise, use the regular " +
            "coefficients.", gridable=true)
    public double[] startval;
    
    @API(help = "random columns indices for HGLM.")
    public int[] random_columns;

    @API(help = "if true, will return likelihood function value.") // not gridable
    public boolean calc_like;

    @API(help="In case of linearly dependent columns, remove the dependent columns.", level = Level.secondary)
    public boolean remove_collinear_columns; // _remove_collinear_columns

    @API(help="If set to true, will generate scoring history for GLM.  This may significantly slow down the algo.", 
            level = Level.secondary, direction = Direction.INPUT)
    public boolean generate_scoring_history;  // if enabled, will generate scoring history for iterations specified in
                                              // scoring_iteration_interval and score_every_iteration
    /////////////////////
  }
}

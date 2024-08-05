package hex.schemas;

import hex.glm.GLMModel.GLMParameters;
import hex.glm.GLMModel.GLMParameters.Solver;
import hex.hglm.HGLM;
import hex.hglm.HGLMModel;
import water.api.API;
import water.api.API.Direction;
import water.api.API.Level;
import water.api.schemas3.KeyV3;
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
            "random_family", // distribution of random component, array
            "method",
            "standardize",
            "remove_collinear_columns",
            "max_iterations",
            "objective_epsilon",
            "beta_epsilon",
            "startval",  // initial starting values for fixed and randomized coefficients, double array
            "calc_like",
            "obj_reg",
            "max_runtime_secs",
            "custom_metric_func",
            "generate_scoring_history",
            "random_intercept", 
            "group_column",
            "tau_u_var_init",
            "tau_e_var_init",
            "initial_random_effects",
            "initial_fixed_effects"
    };

    @API(level = API.Level.expert, direction = API.Direction.INOUT, gridable=true,
            help = "An array that contains initial values of the fixed effects coefficient.")
    public KeyV3.FrameKeyV3 initial_fixed_effects;

    @API(level = API.Level.expert, direction = API.Direction.INOUT, gridable=true,
            help = "A H2OFrame id that contains initial values of the random effects coefficient.")
    public KeyV3.FrameKeyV3 initial_random_effects;

    @API(help = "Initial variance of random coefficient effects."
            , level = Level.expert, gridable = true)
    public double tau_u_var_init;

    @API(help = "Initial variance of random noise."
            , level = Level.expert, gridable = true)
    public double tau_e_var_init;

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
    public GLMParameters.Family[] random_family;

    @API(help = "AUTO will set the solver based on given data and the other parameters. IRLSM is fast on on problems" +
            " with small number of predictors and for lambda-search with L1 penalty, L_BFGS scales better for datasets" +
            " with many columns.", values = {"AUTO", "IRLSM", "L_BFGS","COORDINATE_DESCENT_NAIVE", 
            "COORDINATE_DESCENT", "GRADIENT_DESCENT_LH", "GRADIENT_DESCENT_SQERR"}, level = Level.critical)
    public Solver method;
    
    @API(help = "Perform scoring for every score_iteration_interval iterations.", level = Level.secondary)
    public int score_iteration_interval;

    @API(help = "Standardize numeric columns to have zero mean and unit variance.", level = Level.critical,
            gridable = true)
    public boolean standardize;

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
    @API(help="if true, will allow random component to the GLM coefficients.", direction=Direction.INPUT, gridable=true)
    public boolean random_intercept;
    
    @API(help="group_column is the column that is categorical and used to generate the groups in HGLM")
    public String group_column;
    /////////////////////
  }
}

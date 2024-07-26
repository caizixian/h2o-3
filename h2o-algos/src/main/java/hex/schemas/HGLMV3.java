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
import water.api.schemas3.StringPairV3;

public class HGLMV3 extends ModelBuilderSchema<HGLM, HGLMV3, HGLMV3.HGLMParametersV3> {

  public static final class HGLMParametersV3 extends ModelParametersSchemaV3<HGLMModel.HGLMParameters, HGLMParametersV3> {
    public static final String[] fields = new String[]{
            "model_id",
            "training_frame",
            "validation_frame",
            "nfolds",
            "checkpoint",
            "export_checkpoints_dir",
            "seed",
            "keep_cross_validation_models",
            "keep_cross_validation_predictions",
            "keep_cross_validation_fold_assignment",
            "fold_assignment",
            "fold_column",
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
            "solver",
            "standardize",
            "missing_values_handling",
            "plug_values",
            "compute_p_values",
            "remove_collinear_columns",
            "max_iterations",
            "objective_epsilon",
            "beta_epsilon",
            "gradient_epsilon",
            "link",
            "rand_link", // link function for random components, array
            "startval",  // initial starting values for fixed and randomized coefficients, double array
            "calc_like",
            "max_active_predictors",
            "interactions",
            "interaction_pairs",
            "obj_reg",
            "stopping_rounds",
            "stopping_metric",
            "stopping_tolerance",
            // dead unused args forced here by backwards compatibility, remove in V4
            "balance_classes",
            "class_sampling_factors",
            "max_after_balance_size",
            "max_confusion_matrix_size",
            "max_runtime_secs",
            "custom_metric_func",
            "generate_scoring_history",
            "auc_type",
            "dispersion_epsilon",
            "fix_dispersion_parameter",
            "generate_variable_inflation_factors",
            "dispersion_learning_rate",
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

    @API(help = "Dispersion learning rate is only valid for tweedie family dispersion parameter estimation using ml. " +
            "It must be > 0.  This controls how much the dispersion parameter estimate is to be changed when the" +
            " calculated loglikelihood actually decreases with the new dispersion.  In this case, instead of setting" +
            " new dispersion = dispersion + change, we set new dispersion = dispersion + dispersion_learning_rate * change. " +
            "Defaults to 0.5.", level = Level.expert, gridable = true)
    public double dispersion_learning_rate;

    @API(help = "AUTO will set the solver based on given data and the other parameters. IRLSM is fast on on problems" +
            " with small number of predictors and for lambda-search with L1 penalty, L_BFGS scales better for datasets" +
            " with many columns.", values = {"AUTO", "IRLSM", "L_BFGS","COORDINATE_DESCENT_NAIVE", 
            "COORDINATE_DESCENT", "GRADIENT_DESCENT_LH", "GRADIENT_DESCENT_SQERR"}, level = Level.critical)
    public Solver solver;

    @API(help="Stop early when there is no more relative improvement on train or validation (if provided).")
    public boolean early_stopping;

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
    
    @API(help = "Restrict coefficients (not intercept) to be non-negative.")
    public boolean non_negative;

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

    @API(help = "Converge if  objective changes less (using L-infinity norm) than this, ONLY applies to L-BFGS" +
            " solver. Default (of -1.0) indicates: If lambda_search is set to False and lambda is equal to zero, the" +
            " default value of gradient_epsilon is equal to .000001, otherwise the default value is .0001. If " +
            "lambda_search is set to True, the conditional values above are 1E-8 and 1E-6 respectively.",
            level = API.Level.expert)
    public double gradient_epsilon;

    @API(help="Likelihood divider in objective value computation, default (of -1.0) will set it to 1/nobs.")
    public double obj_reg;

    @API(help = "Link function.", level = Level.secondary, values = {"family_default", "identity", "logit", "log",
            "inverse", "tweedie", "ologit"}) //"oprobit", "ologlog": will be supported.
    public GLMParameters.Link link;
    
    @API(help="Method used to estimate the dispersion parameter for Tweedie, Gamma and Negative Binomial only.",
            level = Level.secondary, values={"deviance", "pearson", "ml"})
    public GLMParameters.DispersionMethod dispersion_parameter_method;
    
    @API(help = "Link function array for random component in HGLM.", values = {"[identity]", "[family_default]"},
            level = Level.secondary, gridable=true)   
    public GLMParameters.Link[] rand_link; // link function for random components

    @API(help = "double array to initialize fixed and random coefficients for HGLM, coefficients for GLM.  If " +
            "standardize is true, the standardized coefficients should be used.  Otherwise, use the regular " +
            "coefficients.", gridable=true)
    public double[] startval;
    
    @API(help = "random columns indices for HGLM.")
    public int[] random_columns;

    @API(help = "if true, will return likelihood function value.") // not gridable
    public boolean calc_like;

    @API(help="Only used for Tweedie, Gamma and Negative Binomial GLM.  If set, will use the dispsersion parameter" +
            " in init_dispersion_parameter as the standard error and use it to calculate the p-values. Default to" +
            " false.", level=Level.expert)
    public boolean fix_dispersion_parameter;
    
    @API(help="Only used for Tweedie, Gamma and Negative Binomial GLM.  Store the initial value of dispersion " +
            "parameter.  If fix_dispersion_parameter is set, this value will be used in the calculation of p-values.",
            level=Level.expert, gridable=true)
    public double init_dispersion_parameter;
    
    @API(help = "Prior probability for y==1. To be used only for logistic regression iff the data has been sampled and" +
            " the mean of response does not reflect reality.", level = Level.expert)
    public double prior;
    
    @API(help="Maximum number of active predictors during computation. Use as a stopping criterion" +
    " to prevent expensive model building with many predictors." + " Default indicates: If the IRLSM solver is used," +
    " the value of max_active_predictors is set to 5000 otherwise it is set to 100000000.", direction = Direction.INPUT,
            level = Level.expert)
    public int max_active_predictors = -1;

    @API(help="A list of predictor column indices to interact. All pairwise combinations will be computed for the " +
            "list.", direction=Direction.INPUT, level=Level.expert)
    public String[] interactions;

    @API(help="A list of pairwise (first order) column interactions.", direction=Direction.INPUT, level=Level.expert)
    public StringPairV3[] interaction_pairs;

    // dead unused args, formely inherited from supervised model schema
    /**
     * For imbalanced data, balance training data class counts via
     * over/under-sampling. This can result in improved predictive accuracy.
     */
    @API(help = "Balance training data class counts via over/under-sampling (for imbalanced data).",
            level = API.Level.secondary, direction = API.Direction.INOUT)
    public boolean balance_classes;

    /**
     * Desired over/under-sampling ratios per class (lexicographic order).
     * Only when balance_classes is enabled.
     * If not specified, they will be automatically computed to obtain class balance during training.
     */
    @API(help = "Desired over/under-sampling ratios per class (in lexicographic order). If not specified, sampling" +
            " factors will be automatically computed to obtain class balance during training. Requires balance_classes.",
            level = API.Level.expert, direction = API.Direction.INOUT)
    public float[] class_sampling_factors;

    /**
     * When classes are balanced, limit the resulting dataset size to the
     * specified multiple of the original dataset size.
     */
    @API(help = "Maximum relative size of the training data after balancing class counts (can be less than 1.0)." +
            " Requires balance_classes.", /* dmin=1e-3, */ level = API.Level.expert, direction = API.Direction.INOUT)
    public float max_after_balance_size;

    /** For classification models, the maximum size (in terms of classes) of
     *  the confusion matrix for it to be printed. This option is meant to
     *  avoid printing extremely large confusion matrices.  */
    @API(help = "[Deprecated] Maximum size (# classes) for confusion matrices to be printed in the Logs.",
            level = API.Level.secondary, direction = API.Direction.INOUT)
    public int max_confusion_matrix_size;

    @API(help="Request p-values computation, p-values work only with IRLSM solver.", level = Level.secondary)
    public boolean compute_p_values;

    @API(help="In case of linearly dependent columns, remove the dependent columns.", level = Level.secondary)
    public boolean remove_collinear_columns; // _remove_collinear_columns

    @API(help = "If changes in dispersion parameter estimation or loglikelihood value is smaller than " +
            "dispersion_epsilon, will break out of the dispersion parameter estimation loop using maximum " +
            "likelihood.", level = API.Level.secondary, direction = API.Direction.INOUT)
    public double dispersion_epsilon;
    
    @API(help = "Control the maximum number of iterations in the dispersion parameter estimation loop using maximum" +
            " likelihood.", level = API.Level.secondary, direction = API.Direction.INOUT)
    public int max_iterations_dispersion;

    @API(help="If set to true, will generate scoring history for GLM.  This may significantly slow down the algo.", 
            level = Level.secondary, direction = Direction.INPUT)
    public boolean generate_scoring_history;  // if enabled, will generate scoring history for iterations specified in
                                              // scoring_iteration_interval and score_every_iteration
    /////////////////////
  }
}

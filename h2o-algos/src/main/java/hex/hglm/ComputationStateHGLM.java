package hex.hglm;

import hex.DataInfo;
import water.Job;

public class ComputationStateHGLM {
  final int _nFixedBetas;   // fixed coefficient length including inactive predictors
  final int _nRandomBetas;  // random coefficient length including inactive predictors
  public final HGLMModel.HGLMParameters _parms;
  int _iter;
  private DataInfo _activeData;
  private double[] _beta; // fixed coefficients
  private double[][] _ubeta; // random coefficients;
  final DataInfo _dinfo;
  private final Job _job;
  double _tauUSq; // variance of random coefficients effects;
  double _tauESq; // variance of random noise
  String[] _fixed_cofficient_names;
  String[] _random_coefficient_names;
  String[] _group_column_names;
  
  public ComputationStateHGLM(Job job, HGLMModel.HGLMParameters parms, DataInfo dinfo, double[] beta, double[][] ubeta) {
    _job = job;
    _parms = parms;
    _dinfo = dinfo;
    _beta = beta;
    _ubeta = ubeta;
    _nFixedBetas = beta.length;
    _nRandomBetas = ubeta.length;
    _activeData = _dinfo;
  }
}

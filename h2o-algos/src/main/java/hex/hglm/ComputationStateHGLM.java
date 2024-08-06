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
  double _tauUVar = 0; // variance of random coefficients effects;
  double _tauEVar = 0; // variance of random noise
  String[] _fixedCofficientNames; // include intercept
  String[] _randomCoefficientNames; // include intercept only if random effect is in intercept
  String[] _groupColumnNames; // enum levels of group column
  int _numLevel2Unit;
  
  public ComputationStateHGLM(Job job, HGLMModel.HGLMParameters parms, DataInfo dinfo, double[] beta, double[][] ubeta,
                              double tauUSq, double tauESq, String[] fixedCoeffNames, String[] randomCoeffNames, 
                              String[] groupColNames, int iter) {
    _job = job;
    _parms = parms;
    _dinfo = dinfo;
    _beta = beta.clone();
    _ubeta = ubeta;
    _nFixedBetas = beta.length;
    _nRandomBetas = ubeta[0].length;
    _numLevel2Unit = ubeta.length;
    _activeData = _dinfo;
    _iter = iter;
    _fixedCofficientNames = fixedCoeffNames;
    _groupColumnNames = groupColNames;
    _randomCoefficientNames = randomCoeffNames;
    _tauUVar = tauUSq;
    _tauEVar = tauESq;
  }
  
  public double[] get_beta() { return _beta; }
  public double[][] get_ubeta() { return _ubeta; }
  public double get_tauUVar() { return _tauUVar; }
  public double get_tauEVar() { return _tauEVar; }
  public String[] get_fixedCofficientNames() { return _fixedCofficientNames; }
  public String[] get_randomCoefficientNames() { return _randomCoefficientNames; }
  public String[] get_groupColumnNames() { return _groupColumnNames; }
}

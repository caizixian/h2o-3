package water.rapids.ast.prims.mungers;

import hex.CreateFrame;
import org.junit.Test;
import org.junit.runner.RunWith;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.rapids.Rapids;
import water.rapids.Session;
import water.rapids.Val;
import water.runner.CloudSize;
import water.runner.H2ORunner;

@RunWith(H2ORunner.class)
@CloudSize(1)
public class AstNaReplaceMeanModeTest extends TestUtil {
  
  @Test
  public void TestNaReplaceModeMean() {
    Scope.enter();
    try {
      CreateFrame cf = new CreateFrame();
      cf.rows = 100;
      cf.cols = 10;
      cf.categorical_fraction = 0.2;
      cf.integer_fraction = 0.2;
      cf.time_fraction = 0.0;
      cf.string_fraction = 0.2;
      cf.binary_fraction = 0.0;
      cf.missing_fraction = 0.01;
      cf.seed = 12345;
      Frame testFrame = cf.execImpl().get();
      Scope.track(testFrame);
      
      Session sess = new Session();
      Val val = Rapids.exec("(na.replace.mean.mode testFrame)");
      Frame naReplaced = Scope.track(val.getFrame());
      
      // check when there is na is the original test frame, the new frame should contain the mean for numerical, mode
      // for categorical and no change for all other column types.
      
    } finally {
      Scope.exit();
    }
  }
}

package hex.pipeline;

import hex.pipeline.TransformerChain.Completer;
import water.Futures;
import water.Iced;
import water.Key;
import water.fvec.Frame;

public abstract class DataTransformer<T extends DataTransformer> extends Iced<T> {
  
  public enum FrameType {
    Training,
    Validation,
    Scoring
  }
  
  String _id;
  boolean _enabled = true;  // flag allowing to enable/disable transformers dynamically esp. in pipelines (can be used as a pipeline hyperparam in grids).

  public DataTransformer() {
    this("data_transformer"+Key.rand());
  }
  
  public DataTransformer(String id) {
    _id = id; 
  }
  
  @SuppressWarnings("unchecked")
  public T id(String id) {
    _id = id;
    return (T) this;
  }
  
  public String id() {
    return _id;
  }
  
  public void prepare(PipelineContext context) {
    if (!_enabled) return;
    doPrepare(context);
  }
  
  protected void doPrepare(PipelineContext context) {} 
  
  void prepare(PipelineContext context, TransformerChain chain) {
    assert chain != null;
    prepare(context);
    chain.nextPrepare(context);
  }
  
  public void cleanup() {
    cleanup(new Futures());
  }
  void cleanup(Futures futures) {}
  
  public Frame transform(Frame fr) {
    return transform(fr, FrameType.Scoring, null);
  }
  
  public Frame transform(Frame fr, FrameType type, PipelineContext context) {
    if (!_enabled) return fr;
    return doTransform(fr, type, context);
  }

  /**
   * Transformers must implement this method. They can ignore type and context if they're not context-sensitive.
   * @param fr
   * @param type
   * @param context
   * @return
   */
  protected abstract Frame doTransform(Frame fr, FrameType type, PipelineContext context);
  
  <R> R transform(Frame[] frames, FrameType[] types, PipelineContext context, Completer<R> completer, TransformerChain chain) {
    assert frames != null;
    assert types != null;
    assert frames.length == types.length;
    assert chain != null;
    Frame[] transformed = new Frame[frames.length];
    for (int i=0; i<frames.length; i++) {
      Frame fr = frames[i];
      FrameType type = types[i];
      Frame trfr = fr == null ? null : transform(fr, type, context);
      if (context != null && context._tracker != null) {
        context._tracker.apply(trfr, fr, type, context, this);
      } 
      transformed[i] = trfr;
    }
    return chain.nextTransform(transformed, types, context, completer);
  }

}

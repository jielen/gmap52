package com.anyi.gp.taglib;

import com.anyi.gp.Delta;


public interface ITrackViewData {
  public void prepare(String rootNodeId);
  public Delta getFlowData();
  public Delta getNodeData();
}

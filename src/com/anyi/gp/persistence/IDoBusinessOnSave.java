package com.anyi.gp.persistence;

import java.util.Map;

import com.anyi.gp.BusinessException;
import com.anyi.gp.pub.BoolMessage;

public interface IDoBusinessOnSave {
  public void doBeforeSave(String busiParams, Map tableMap) throws BusinessException;

  public void doAfterSave(String busiParams, BoolMessage saveBM, Map tableMap)
    throws BusinessException;
}

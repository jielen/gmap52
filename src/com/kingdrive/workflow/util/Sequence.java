package com.kingdrive.workflow.util;

import java.sql.Connection;

import com.kingdrive.framework.db.SequenceManager;

public class Sequence {
  public static final String SEQ_ACTION = "SEQ_ACTION";

  public static final String SEQ_ACTION_HISTORY = "SEQ_ACTION_HISTORY";

  public static final String SEQ_COMPANY = "SEQ_COMPANY";

  public static final String SEQ_CURRENT_TASK = "SEQ_CURRENT_TASK";

  public static final String SEQ_DELEGATION = "SEQ_DELEGATION";

  public static final String SEQ_DELEGATION_HISTORY = "SEQ_DELEGATION_HISTORY";

  public static final String SEQ_DOCUMENT = "SEQ_DOCUMENT";

  public static final String SEQ_GRAPHICS = "SEQ_GRAPHICS";

  public static final String SEQ_GRAPHICS_LINK_POINT = "SEQ_GRAPHICS_LINK_POINT";

  public static final String SEQ_INSTANCE = "SEQ_INSTANCE";

  public static final String SEQ_LINK = "SEQ_LINK";

  public static final String SEQ_LINK_STATE = "SEQ_LINK_STATE";

  public static final String SEQ_NODE = "SEQ_NODE";

  public static final String SEQ_NODE_STATE = "SEQ_NODE_STATE";

  public static final String SEQ_ORG = "SEQ_ORG";

  public static final String SEQ_ORG_POSITION = "SEQ_ORG_POSITION";

  public static final String SEQ_PASS = "SEQ_PASS";

  public static final String SEQ_POSITION = "SEQ_POSITION";

  public static final String SEQ_ROLE = "SEQ_ROLE";

  public static final String SEQ_STAFF = "SEQ_STAFF";

  public static final String SEQ_STATE = "SEQ_STATE";

  public static final String SEQ_STATE_VALUE = "SEQ_STATE_VALUE";

  public static final String SEQ_TASK_EXECUTOR = "SEQ_TASK_EXECUTOR";

  public static final String SEQ_TEMPLATE = "SEQ_TEMPLATE";

  public static final String SEQ_VARIABLE = "SEQ_VARIABLE";

  public static final String SEQ_VARIABLE_VALUE = "SEQ_VARIABLE_VALUE";

  public Sequence() {
  }

  public static int fetch(String name) {
    return (int) SequenceManager.getSequenceManager().getNextId(name);
  }
}

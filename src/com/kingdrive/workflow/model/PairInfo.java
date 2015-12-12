package com.kingdrive.workflow.model;

public class PairInfo implements java.io.Serializable {
  private String id = null;

  private boolean idModifyFlag;

  private String content = null;

  private boolean contentModifyFlag;

  private String reference = null;

  private boolean referenceModifyFlag;

  private String action = "";
	private boolean defaultPathModifyFlag;
	private String defaultPath="";

	public static final String TYPE_DEFAULT="1";
  public PairInfo() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.idModifyFlag = false;
    this.contentModifyFlag = false;
    this.referenceModifyFlag = false;
  }

  public void setId(String id) {
    this.id = id;
    this.idModifyFlag = true;
  }

  public String getId() {
    return this.id;
  }

  public void setContent(String content) {
    this.content = content;
    this.contentModifyFlag = true;
  }

  public String getContent() {
    return this.content;
  }

  public void setReference(String reference) {
    this.reference = reference;
    this.referenceModifyFlag = true;
  }

  public String getReference() {
    return this.reference;
  }

  public boolean getIdModifyFlag() {
    return this.idModifyFlag;
  }

  public boolean getContentModifyFlag() {
    return this.contentModifyFlag;
  }

  public boolean getReferenceModifyFlag() {
    return this.referenceModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
    /**
     * @return Returns the defaultPath.
     */
    public String getDefaultPath() {
        return defaultPath;
    }
    /**
     * @param defaultPath The defaultPath to set.
     */
    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }
    /**
     * @return Returns the defaultPathModifyFlag.
     */
    public boolean getDefaultPathModifyFlag() {
        return defaultPathModifyFlag;
    }
}

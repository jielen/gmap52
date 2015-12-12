package com.anyi.gp.taglib.components;


public class NumericBox extends TextBox {
  
  private double maxvalue = 0x7FFFFFFF;

  private double minvalue = -0x7FFFFFFF;

  private int length = 20;

  private int scale = 0;

  private boolean iskilo = false;

  public NumericBox(){
    super();
    this.setBoxtype("NumericBox");
    this.setFieldtype("NUM");
    this.setAlign("right");
  }

  public void init() {
    super.init();
    this.setMaxlen(MAX_LEN);
    this.setMinlen(MIN_LEN);
    if (this.isIsfromdb()) {
      this.setMaxvalue(this.getFieldmeta().getMaxValue());
      this.setMinvalue(this.getFieldmeta().getMinValue());
      this.setLength(this.getFieldmeta().getLength());
      this.setScale(this.getFieldmeta().getDecLength());
      this.setIskilo(this.getFieldmeta().getKiloStyle());
    }
  }

  protected String makeAttr() {
    StringBuffer voSBuf = new StringBuffer();
    voSBuf.append(super.makeAttr());
    voSBuf.append("maxvalue=" + this.maxvalue + " ");
    voSBuf.append("minvalue=" + this.minvalue + " ");
    voSBuf.append("length=" + this.length + " ");
    voSBuf.append("scale=" + this.scale + " ");
    voSBuf.append("iskilo=" + this.iskilo + " ");
    return voSBuf.toString();
  }

  public double getMaxvalue() {
    return maxvalue;
  }

  public void setMaxvalue(double maxvalue) {
    this.maxvalue = maxvalue;
  }

  public double getMinvalue() {
    return minvalue;
  }

  public void setMinvalue(double minvalue) {
    this.minvalue = minvalue;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public int getScale() {
    return scale;
  }

  public void setScale(int scale) {
    this.scale = scale;
  }

  public boolean isIskilo() {
    return iskilo;
  }

  public void setIskilo(boolean iskilo) {
    this.iskilo = iskilo;
  }
}

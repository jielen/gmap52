package com.anyi.gp.taglib.components;

public class ImageBox extends FileBox {

	private final static String ZOOM_TYPE_ORIGINAL = "original";

	private final static String ZOOM_TYPE_FIXED_SIZE = "fixedsize";

	private String zoomtype = ZOOM_TYPE_ORIGINAL;

	private int imgwidth = 300;

	private int imgheight = 200;

	private int maxwidth = 0;

	private int maxheight = 0;

	private boolean isstretch = false;

	private boolean isboxbordervisiblewithimg = false;

	private String imgborderstyle = "solid";

	private int imgborderwidth = 0;

	private String imgbordercolor = "#6fe6e3";

	public ImageBox() {
		super();
		this.setBoxtype("ImageBox");
		this.setMaxlen(MAX_LEN);
		this.setMinlen(MIN_LEN);
	}

	protected String makeOtherTD() {
		return super.makeOtherTD();
	}

	protected String makeImageTD() {
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("<td colspan='3' id='FileBoxImagePanel' ");
		voBuf.append("style='");
		//voBuf.append("position:absolute;");
		//voBuf.append("left:0px;top:0px;");
		voBuf.append("overflow:hidden;");
		voBuf.append("display:none;");
		voBuf.append("border:");
		voBuf.append(this.imgborderstyle + " " + this.imgborderwidth + "px "
				+ this.imgbordercolor + ";");
		if (this.getZoomtype().equals(ZOOM_TYPE_FIXED_SIZE)) {
			voBuf.append("width:" + this.getImgwidth() + "px;");
			voBuf.append("height:" + this.getImgheight() + "px;");
		}
		voBuf.append("'>\n");

		voBuf.append("<img name='imageInput' id='imageInput' ");
		voBuf.append("class='clsImageBoxImage'");
		voBuf.append(" style='");
		if (this.getZoomtype().equals(ZOOM_TYPE_FIXED_SIZE)) {
			voBuf.append("width:" + (this.getImgwidth() - this.imgborderwidth)
					+ "px;");
			voBuf.append("height:"
					+ (this.getImgheight() - this.imgborderwidth) + "px;");
		}
		voBuf.append("'>\n");
		voBuf.append("</td></tr>\n");
		voBuf.append("<tr valign=\"bottom\">");
		return voBuf.toString();
	}

	protected String makeAttr() {
		StringBuffer voBuf = new StringBuffer();
		voBuf.append(super.makeAttr());
		voBuf.append("zoomtype='" + this.zoomtype + "' ");
		voBuf.append("imgwidth='" + this.imgwidth + "' ");
		voBuf.append("imgheight='" + this.imgheight + "' ");
		voBuf.append("maxwidth='" + this.maxwidth + "' ");
		voBuf.append("maxheight='" + this.maxheight + "' ");
		voBuf.append("isstretch='" + this.isstretch + "' ");
		voBuf.append("isboxbordervisiblewithimg='"
				+ this.isboxbordervisiblewithimg + "' ");
		voBuf.append("imgborderstyle='" + this.imgborderstyle + "' ");
		voBuf.append("imgborderwidth='" + this.imgborderwidth + "' ");
		voBuf.append("imgbordercolor='" + this.imgbordercolor + "' ");
		return voBuf.toString();
	}

	public void setIspopup(boolean ispopup) {
	}

	public void setImgbordercolor(String imgbordercolor) {
		this.imgbordercolor = imgbordercolor;
	}

	public void setImgborderstyle(String imgborderstyle) {
		this.imgborderstyle = imgborderstyle;
	}

	public void setImgborderwidth(int imgborderwidth) {
		this.imgborderwidth = imgborderwidth;
	}

	public void setIsboxbordervisiblewithimg(boolean isboxbordervisiblewithimg) {
		this.isboxbordervisiblewithimg = isboxbordervisiblewithimg;
	}

	public void setIsstretch(boolean isstretch) {
		this.isstretch = isstretch;
	}

	private String getZoomtype() {
		return zoomtype;
	}

	public void setZoomtype(String zoomtype) {
		this.zoomtype = zoomtype;
	}

	private int getImgheight() {
		return imgheight;
	}

	public void setImgheight(int imgheight) {
		this.imgheight = imgheight;
	}

	private int getImgwidth() {
		return imgwidth;
	}

	public void setImgwidth(int imgwidth) {
		this.imgwidth = imgwidth;
	}

	public void setIszoom(boolean iszoom) {
		if (iszoom)
			this.setZoomtype(ZOOM_TYPE_FIXED_SIZE);
		else
			this.setZoomtype(ZOOM_TYPE_ORIGINAL);
	}

	public void setMaxheight(int maxheight) {
		this.maxheight = maxheight;
	}

	public void setMaxwidth(int maxwidth) {
		this.maxwidth = maxwidth;
	}
}

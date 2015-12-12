package com.anyi.gp.taglib;

import com.anyi.gp.taglib.components.ImageBox;
import com.anyi.gp.taglib.components.TextBox;

public class ImageBoxTag extends FileBoxTag {

  private static final long serialVersionUID = -7511043237040333908L;

	protected TextBox makeO(){
	  return new ImageBox();
	}
	
	public void setIspopup(boolean ispopup) {
	}

	public void setImgbordercolor(String imgbordercolor) {
		((ImageBox)this.getO()).setImgbordercolor(imgbordercolor);
	}

	public void setImgborderstyle(String imgborderstyle) {
		((ImageBox)this.getO()).setImgborderstyle(imgborderstyle);
	}

	public void setImgborderwidth(int imgborderwidth) {
		((ImageBox)this.getO()).setImgborderwidth(imgborderwidth);
	}

	public void setIsboxbordervisiblewithimg(boolean isboxbordervisiblewithimg) {
		((ImageBox)this.getO()).setIsboxbordervisiblewithimg(isboxbordervisiblewithimg);
	}

	public void setIsstretch(boolean isstretch) {
		((ImageBox)this.getO()).setIsstretch(isstretch);
	}

	public void setZoomtype(String zoomtype) {
		((ImageBox)this.getO()).setZoomtype(zoomtype);
	}

	public void setImgheight(int imgheight) {
		((ImageBox)this.getO()).setImgheight(imgheight);
	}

	public void setImgwidth(int imgwidth) {
		((ImageBox)this.getO()).setImgwidth(imgwidth);
	}

	public void setIszoom(boolean iszoom) {
	  ((ImageBox)getO()).setIszoom(iszoom);
	}

	public void setMaxheight(int maxheight) {
		((ImageBox)this.getO()).setMaxheight(maxheight);
	}

	public void setMaxwidth(int maxwidth) {
		((ImageBox)this.getO()).setMaxwidth(maxwidth);
	}
}

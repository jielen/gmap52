package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.anyi.gp.Pub;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.license.LicenseManager;
import com.anyi.gp.pub.RightUtil;
import com.anyi.gp.taglib.ResourceManager;

public class Toolbar extends AbstractMenu implements Component {

	private final static Logger log = Logger.getLogger(Toolbar.class);

	private String cssclass = "";

	private String style = "";

	private String oninit = "";

	private Page ownerPage = null;

	private PopupMenu popupMenu = new PopupMenu(this);

	public Toolbar() {
	}

	public void setOwnerPage(Page ownerPage) {
		this.ownerPage = ownerPage;
	}

	public Page getOwnerPage() {
		return this.ownerPage;
	}

	public void writeHTML(Writer out) throws IOException {
		init();
		try {
			out.write(make());
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
		popupMenu.write(out);
	}

	private void init() {
		if (this.getId() == null || this.getId().length() == 0) {
			this.setId("id_" + Pub.getUID() + this.getIdsuffix());
		}
	}

	private String make() {
		StringBuffer voBuf = new StringBuffer();
		voBuf.append(this.makeOuterPanel_1());
		voBuf.append(this.makeCalls(this.ownerPage.getCurrRequest()));
		voBuf.append(this.makeOuterPanel_2());
		return voBuf.toString();
	}

	protected String makeCallsHtml(NodeList nodes, Map callMap
    , String userId, String coCode, String orgCode, String posiCode) {
		StringBuffer result = new StringBuffer();
		result
				.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"clsToolbarTable4\" >\n");
		result.append("<tr>\n");
		result.append("<td width=\"11px\"><img border=\"0\" src=\"").append(
				IMG_TOOLBAR_LEFT);
		result.append("\"></img></td>\n");
		result.append("<td id=\"CallsAreaTD\" background=\"").append(
				IMG_TOOLBAR_MIDDLE);
		result
				.append("\" valign=\"center\" style=\"overflow:hidden;width:100%;\" >\n");
		result
				.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size:9pt;\">\n");
		result.append("<tr id=\"CallsTR\">\n");
		
    Set funcSet = RightUtil.getAllowedFuncs(userId, getComponame(), coCode, orgCode, posiCode);
		for (int i = 0, j = nodes.getLength(); i < j; i++) {
			Element node = (Element) nodes.item(i);
			String id = node.getAttribute("id");
			String type = node.getAttribute("type");
			String caption = node.getAttribute("caption");
			String tip = node.getAttribute("tip");
			String accesskey = node.getAttribute("accesskey");
			String img = node.getAttribute("img");
			boolean isgranttoall = Boolean.valueOf(
					node.getAttribute("isgranttoall")).booleanValue();
			if (img == null || img.equals("")) {
				img = (String) ResourceManager.getToolbarImgMap().get(id);
			}
			if (img == null || img.equals("")) {
				img = IMG_ICO_DEFAULT;
			}

      LicenseManager licenseManager = (LicenseManager) ApplusContext.getBean("licenseManager");
      if (id.equals("fexport")) {
        if (!licenseManager.canExport()) {
          continue;
        }
      }
      
			if (isAllowCall(funcSet, id, isgranttoall, callMap, userId, coCode, orgCode, posiCode)) {
				result.append("<td>\n");
				result.append(this.makeCommandCall(id, type, caption, tip,
						accesskey, img));
				result.append("</td>\n");
			}
		}
		result.append("<td id=\"BlankTDOfToolbar5\" width=\"100%\">\n");
		result.append("</td>\n");
		result.append("</tr>\n");
		result.append("</table>\n");
		result.append("</td>\n");
		result
				.append("<td width=\"14px\"><img id=\"MoreCallsImg\" border=\"0\" src=\"");
		result.append(IMG_TOOLBAR_RIGHT);
		result.append("\" onclick='Toolbar_MoreCallsImg_onclick(\""
				+ this.getId() + "\")'></img></td>\n");
		result.append("</tr>\n");
		result.append("</table>\n");
		return result.toString();
	}

	protected String getToolbarId() {
		return this.getId();
	}

	public void setComponame(String componame) {
		super.setComponame(componame);
		this.popupMenu.setComponame(componame);
	}

	public void setIsfromdb(boolean isfromdb) {
		super.setIsfromdb(isfromdb);
		this.popupMenu.setIsfromdb(isfromdb);
	}

	public String getCssclass() {
		return cssclass;
	}

	public void setCssclass(String cssclass) {
		this.cssclass = cssclass;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getOninit() {
		return oninit;
	}

	public void setOninit(String oninit) {
		this.oninit = oninit;
	}

	public void setBodytext(String bodytext) {
		super.setBodytext(bodytext);
		this.popupMenu.setBodytext(bodytext);
	}

	protected String getPopupMenuId() {
		return this.popupMenu.getId();
	}

	public void writeInitScript(Writer out) throws IOException {
		out.write(popupMenu.makeJS());
		out.write(makeJS());
	}

	private String makeJS() {
		StringBuffer voBuf = new StringBuffer();
		voBuf.append("var voTB = new Toolbar('" + this.getId() + "');\n");
		voBuf.append("voTB.resize();\n");
		voBuf.append("PageX.regCtrlObj(\"" + this.getId() + "\", voTB);\n");
		if (this.getOninit() != null && this.getOninit().length() > 10) {
			voBuf.append("var me = voTB;\n");
			voBuf.append(this.getOninit() + ";\n");
			voBuf.append("me=null;\n");
			voBuf.append("voTB=null;\n");
		}
		return voBuf.toString();
	}
	public void setFilterFuncMap(Map filterFuncMap) {
		super.setFilterFuncMap(filterFuncMap);
		popupMenu.setFilterFuncMap(filterFuncMap);
	}
}

package com.anyi.gp.license;

import java.util.ArrayList;
import java.util.List;

import com.anyi.gp.desktop.Title;

public class SpecialProduct {
	private static List SPECIAL_PRO;
	
	static {
		SPECIAL_PRO = new ArrayList();
		SPECIAL_PRO.add("FAS");
		SPECIAL_PRO.add("PRS");
		SPECIAL_PRO.add("RPS");
		SPECIAL_PRO.add("BIA");
	}
	
	public static List getProductName(List productList) {
		List products = new ArrayList();
		int prosSize = productList.size();
		String product = "";
		for (int i = 0; i < prosSize; i++) {
			product = (String)productList.get(i);
			if (SPECIAL_PRO.contains(product)) {
				if (product.equals("BIA")) {
					products.add("BZ");
					products.add("GI");
				}
				product = product.substring(0, product.length() - 1);
			}
			products.add(product);
		}
		return products;
	}
	
	public static List filterTitle(List titles, List product) {
		List result = new ArrayList();
		Title title = null;
		for (int i = 0; i < titles.size(); i++) {
			title = (Title) titles.get(i);
			String titleId = title.getTitleId();
			if ("GRP_FI_FA".equals(titleId) || "GRP_FI_FA_CZ".equals(titleId)) {
				if (!product.contains("FAS")) {
					continue;
				}
			}
			if ("GRP_GFI_FA".equals(titleId) || "GRP_GFI_FA_CZ".equals(titleId)) {
				if (!product.contains("FA")) {
					continue;
				}
			}
			if ("GRP_FI_PR".equals(titleId) || "GRP_FI_PR_CZ".equals(titleId)) {
				if (!product.contains("PRS")) {
					continue;
				}
			}
			if ("GRP_GFI_PR".equals(titleId) || "GRP_GFI_PR_CZ".equals(titleId)) {
				if (!product.contains("PR")) {
					continue;
				}
			}
//			if ("GRP_FI_RP".equals(titleId) || "GRP_FI_RP_CZ".equals(titleId)) {
//				if (!product.contains("RPS")) {
//					continue;
//				}
//			}
//			if ("GRP_GFI_RP".equals(titleId) || "GRP_GFI_RP_CZ".equals(titleId)) {
//				if (!product.contains("RP")) {
//					continue;
//				}
//			}
			result.add(title);
		}
		return result;		
	}
}

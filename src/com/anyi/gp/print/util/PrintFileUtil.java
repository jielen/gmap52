package com.anyi.gp.print.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.anyi.gp.context.ApplusContext;

public class PrintFileUtil {

	private final int Interval = 48; // hours

	private final String PrintFiles = "PrintFiles";

	private final static Logger log = Logger.getLogger(PrintFileUtil.class);

	public PrintFileUtil() {
	}

	/**
	 * 读取服务器上存储的文件
	 * 
	 * @param fileName
	 *            String
	 * @return byte[]
	 */

	public static byte[] getPrintPdfFile(String fileName) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream is = null;
		File file = null;
		try {
			file = new File(fileName);
			if (file != null && file.exists() && file.canRead()) {
				long length = file.length();
				byte[] buff = new byte[(int) length / 7];
				is = new FileInputStream(fileName);
				if (is != null) {
					while ((is.read(buff)) != -1) {
						baos.write(buff);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return baos.toByteArray();
	}

	/**
	 * 删除服务器上缓存的文件
	 */

	public static void deletePrintPdfFiles() {
		PrintFileUtil printFileUtil = new PrintFileUtil();
		String filePath = printFileUtil.getPrintFilesPath();
		printFileUtil.deletePrintFiles(filePath);
	}

	public void deletePrintFiles(String filePath) {
		PrintFileUtil.createDirectory(filePath);
		File fileDir = new File(filePath);
		if (fileDir != null && fileDir.isDirectory()) {
			File[] files = fileDir.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (this.isNeedDeleted(files[i])) {
						PrintFileUtil.deleteFile(files[i]);
					}
				}
			}
		}
	}

	/*
	 * 新建目录，只能建一层下级子目录. @param filePath 新建的子目录的路径；
	 */
	public static void createDirectory(String filePath) {
		File filepath = new File(filePath);
		if (!filepath.exists()) {
			filepath.mkdir();
		}
	}

	/**
	 * 判断服务器上的PDF文件是否需要删除
	 * 
	 * @param file
	 *            File
	 * @return boolean
	 */
	public boolean isNeedDeleted(File file) {
		boolean flag = true;
		if (file != null) {
			String fileName = file.getName();
			if (fileName != null) {
				int index = fileName.indexOf(".");
				if (index != -1) {
					fileName = fileName.substring(0, index);
					if (getElapsedHours(fileName) < Interval) {
						flag = false;
					}
				}
			}
		}
		return flag;
	}

	public int getElapsedHours(String time) {
		int elapsedHours = 0;
		try {
			long time1 = new Long(time).longValue();
			long time2 = new Date().getTime();
			long difference = Math.abs(time2 - time1);
			difference = difference / 1000 / 3600;
			elapsedHours = new Long(difference).intValue();
		} catch (NumberFormatException ex) {
			elapsedHours = Interval;
		}
		return elapsedHours;
	}

	public String getFileID() {
		return String.valueOf(new Date().getTime());
	}

	/**
	 * 获取放置PDF文件的路径
	 * 
	 * @return String
	 */
	public String getPrintFilesPath() {
		String filePath = ApplusContext.getEnvironmentConfig().get(
				"reportspath");
		if (!filePath.endsWith(File.separator)) {
			filePath += File.separator;
		}
		filePath += PrintFiles;
		if (!filePath.endsWith(File.separator)) {
			filePath += File.separator;
		}
		return filePath;
	}

	/**
	 * 
	 * @return String
	 */
	public static String getJasperReportPath() {
		String sReportsPath = ApplusContext.getEnvironmentConfig().get(
				"reportspath");
		if (!sReportsPath.endsWith(File.separator)) {
			sReportsPath += File.separator;
		}
		return sReportsPath;
	}

	/**
	 * 
	 * @param f
	 *            File
	 * @return String
	 */
	public static String getFileName(File f) {
		String fileName = "";
		if (f != null) {
			String path = f.getAbsolutePath();
			fileName = path.substring(path.lastIndexOf(File.separator) + 1);
			fileName = fileName.substring(0, fileName.indexOf("."));
		}
		return fileName;
	}

	/**
	 * 
	 * @return
	 */
	public static String getAbsoluteJasperReportPath() {
		String jasperReportPath = getJasperReportPath();
		File file = new File(jasperReportPath);
		String path = file.getAbsolutePath();
		path += File.separator;
		return path;
	}

	/**
	 * 
	 * @param fileName
	 */
	public static void deleteFile(String fileName) {
		if (fileName != null && fileName.trim().length() > 0) {
			File file = new File(fileName);
			deleteFile(file);
		}
	}

	/**
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		try {
			if (file != null && file.exists()) {
				boolean flag = file.delete();
				if (!flag) {
					log.debug("文件" + file.getName() + "删除不成功！");
					System.out.println("文件" + file.getName() + "删除不成功！");
				}
			}
		} catch (RuntimeException e) {
			log.debug("文件" + file.getName() + "删除不成功！" + e.getMessage());
			System.out.println("文件" + file.getName() + "删除不成功！"
					+ e.getMessage());
		}
	}
}

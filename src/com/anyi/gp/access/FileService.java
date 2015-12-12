package com.anyi.gp.access;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.bean.AsFile;
import com.anyi.gp.core.dao.BaseDao;

/**
 * 
 * TODO: 作为接口，分别派生FSFileService和DBFileServie来处理文件系统和数据库
 */
public class FileService {

	private final Logger log = Logger.getLogger(FileService.class);

	private static final String DEF_FILE_UPDATE_TYPE = "FS";

	private static final String FILE_UPDATE_TYPE_FS = "FS";

	private static final String SQL_INSERT_FILE = "gmap-common.insertAsFile";

	private static final String SQL_INSERT_UPLOADFILE = "gmap-common.insertAsUpload";

	private static final String SQL_DELETE_FILE = "gmap-common.deleteAsFileById";

	private static final String SQL_DELETE_UPLOADFILE = "gmap-common.deleteAsResourceById";

	private static final String SQL_GET_FILE = "gmap-common.getAsFileById";

	private BaseDao dao;

	public FileService() {
	}

	public FileService(BaseDao dao) {
		this.dao = dao;
	}

	public void uploadFile(AsFile file) {

		String fileUpdateType = ApplusContext.getEnvironmentConfig().get(
				"fileUpdateType");
		if (fileUpdateType == null || fileUpdateType.length() == 0) {
			fileUpdateType = DEF_FILE_UPDATE_TYPE;
		}

		if (FILE_UPDATE_TYPE_FS.equalsIgnoreCase(fileUpdateType)) {
			String tempFilesPath = ApplusContext.getEnvironmentConfig().get(
					"fileUploadPath");

			File path = new File(tempFilesPath);
			if (!path.exists()) {
				path.mkdir();
			}

			File tempFile = new File(tempFilesPath + "/" + file.getFileId());
			FileOutputStream os = null;
			try {
				os = new FileOutputStream(tempFile);
				os.write(file.getFileContent());
				file.setFileContent(null);// 不保存数据库中
			} catch (FileNotFoundException e1) {
				log.error(e1);
			} catch (IOException e) {
				log.error(e);
			} catch (Exception e) {
				log.error(e);
				throw new RuntimeException(e);
			} finally{
        try {
          os.close();
        } catch (IOException e) {
          log.error(e);
        }
      }
		}

		try {
			dao.insert(SQL_INSERT_FILE, file);
		} catch (SQLException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}

	public void uploadResource(AsFile file) {

		String fileUpdateType = ApplusContext.getEnvironmentConfig().get(
				"fileUpdateType");
		if (fileUpdateType == null || fileUpdateType.length() == 0) {
			fileUpdateType = DEF_FILE_UPDATE_TYPE;
		}

		if (FILE_UPDATE_TYPE_FS.equalsIgnoreCase(fileUpdateType)) {
			String tempFilesPath = ApplusContext.getEnvironmentConfig().get(
					"downloadPath");
			
			File path = new File(tempFilesPath);
			if (!path.exists()) {
				path.mkdir();
			}

			File tempFile = new File(tempFilesPath + "/" + file.getFileId());
			FileOutputStream os = null;
			try {
				os = new FileOutputStream(tempFile);
				os.write(file.getFileContent());
				file.setFileContent(null);// 不保存数据库中
			} catch (FileNotFoundException e1) {
				log.error(e1);
			} catch (IOException e) {
				log.error(e);
			} catch (Exception e) {
				log.error(e);
				throw new RuntimeException(e);
			}finally{
        try {
          os.close();
        } catch (IOException e) {
          log.error(e);
        }
      }
		}

		try {
			dao.insert(SQL_INSERT_UPLOADFILE, file);
		} catch (SQLException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}

	public void deleteFile(AsFile file) {
		try {
			dao.delete(SQL_DELETE_FILE, file.getFileId());
		} catch (SQLException e) {
			log.error(e);
			throw new RuntimeException(e);
		}

		String fileUpdateType = ApplusContext.getEnvironmentConfig().get(
				"fileUpdateType");
		if (fileUpdateType == null || fileUpdateType.length() == 0) {
			fileUpdateType = DEF_FILE_UPDATE_TYPE;
		}

		if (FILE_UPDATE_TYPE_FS.equalsIgnoreCase(fileUpdateType)) {
			String tempFilesPath = ApplusContext.getEnvironmentConfig().get(
					"fileUploadPath");

			File path = new File(tempFilesPath);
			if (!path.exists()) {
				return;
			}

			File tempFile = new File(tempFilesPath + "/" + file.getFileId());
			if (tempFile.exists() == true)
				tempFile.delete();
		}

	}

	public void deleteResource(AsFile file) {
		try {
			dao.delete(SQL_DELETE_UPLOADFILE, file.getFileId());
		} catch (SQLException e) {
			log.error(e);
			throw new RuntimeException(e);
		}

		String fileUpdateType = ApplusContext.getEnvironmentConfig().get(
				"fileUpdateType");
		if (fileUpdateType == null || fileUpdateType.length() == 0) {
			fileUpdateType = DEF_FILE_UPDATE_TYPE;
		}

		if (FILE_UPDATE_TYPE_FS.equalsIgnoreCase(fileUpdateType)) {
			String tempFilesPath = ApplusContext.getEnvironmentConfig().get(
					"downloadPath");

			File path = new File(tempFilesPath);
			if (!path.exists()) {
				return;
			}

			File tempFile = new File(tempFilesPath + "/" + file.getFileId());
			if (tempFile.exists() == true)
				tempFile.delete();
		}

	}

	public AsFile downloadFile(String fileid) {
		AsFile file = null;
		try {
			file = (AsFile) dao.queryForObject(SQL_GET_FILE, fileid);
		} catch (SQLException e) {
			log.error(e);
			throw new RuntimeException(e);
		}

		if (file == null) {
			return null;
		}

		String fileUpdateType = ApplusContext.getEnvironmentConfig().get(
				"fileUpdateType");
		if (fileUpdateType == null || fileUpdateType.length() == 0) {
			fileUpdateType = DEF_FILE_UPDATE_TYPE;
		}

		if (FILE_UPDATE_TYPE_FS.equalsIgnoreCase(fileUpdateType)) {
			String tempFilesPath = ApplusContext.getEnvironmentConfig().get(
					"fileUploadPath");
			File path = new File(tempFilesPath);
			if (!path.exists()) {
				log.error("目录" + tempFilesPath + "不存在");
				throw new RuntimeException("目录" + tempFilesPath + "不存在");
			}

			File tempFile = new File(tempFilesPath + "/" + fileid);
      FileInputStream is = null;
			try {
				is = new FileInputStream(tempFile);
				int len = (new Long(tempFile.length()).intValue());
				byte[] filedata = new byte[len];
				is.read(filedata);
				file.setFileContent(filedata);
			} catch (FileNotFoundException e1) {
				log.error(e1);
			} catch (IOException e) {
				log.error(e);
			} catch (Exception e) {
				log.error(e);
				throw new RuntimeException(e);
			} finally{
        try {
          is.close();
        } catch (IOException e) {
          log.error(e);
        }
      }
		}
		return file;
	}
}

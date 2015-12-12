package com.anyi.gp.print;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.JRPrintElementIndex;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRRenderable;
import net.sf.jasperreports.engine.JRWrappingSvgRenderer;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

/**
 * HTML格式输出类
 * <p>
 * Title: JRPrintHtmlExporter
 * </p>
 * <p>
 * Description: 根据元素所在区域决定是否显示边框，当在分组区时是表格元素，显示边框；否则，不显示。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: UFGOV
 * </p>
 *
 * @author zhanyw
 * @version 1.0
 */
public class JRPrintHtmlExporter extends JRHtmlExporter{

  public JRPrintHtmlExporter(){
  }

  /**
   * 继承JRHtmlExporter 修改：当元素为空时，返回空格"&nbsp;"，使表格正常输出
   *
   * @throws JRException
   */
  public void exportReport() throws JRException{
    progressMonitor = (JRExportProgressMonitor)parameters.get(JRExporterParameter.
        PROGRESS_MONITOR);

    /*   */
    setOffset();

    /*   */
    setClassLoader();

    /*   */
    setInput();

    /*   */
    if(!isModeBatch){
      setPageRange();
    }

    htmlHeader = (String)parameters.get(JRHtmlExporterParameter.HTML_HEADER);
    betweenPagesHtml = (String)parameters.get(JRHtmlExporterParameter.
        BETWEEN_PAGES_HTML);
    htmlFooter = (String)parameters.get(JRHtmlExporterParameter.HTML_FOOTER);

    imagesDir = (File)parameters.get(JRHtmlExporterParameter.IMAGES_DIR);
    if(imagesDir == null){
      String dir = (String)parameters.get(JRHtmlExporterParameter.IMAGES_DIR_NAME);
      if(dir != null){
        imagesDir = new File(dir);
      }
    }

    Boolean isRemoveEmptySpaceParameter = (Boolean)parameters.get(
        JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS);
    if(isRemoveEmptySpaceParameter != null){
      isRemoveEmptySpace = isRemoveEmptySpaceParameter.booleanValue();
    }

    Boolean isWhitePageBackgroundParameter = (Boolean)parameters.get(
        JRHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND);
    if(isWhitePageBackgroundParameter != null){
      isWhitePageBackground = isWhitePageBackgroundParameter.booleanValue();
    }

    Boolean isOutputImagesToDirParameter = (Boolean)parameters.get(
        JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR);
    if(isOutputImagesToDirParameter != null){
      isOutputImagesToDir = isOutputImagesToDirParameter.booleanValue();
    }

    String uri = (String)parameters.get(JRHtmlExporterParameter.IMAGES_URI);
    if(uri != null){
      imagesURI = uri;
    }

    encoding = (String)parameters.get(JRExporterParameter.CHARACTER_ENCODING);
    if(encoding == null){
      encoding = "UTF-8";
    }

    rendererToImagePathMap = new HashMap();
    imagesToProcess = new ArrayList();
    isPxImageLoaded = false;

    //backward compatibility with the IMAGE_MAP parameter
    imageNameToImageDataMap = (Map)parameters.get(JRHtmlExporterParameter.IMAGES_MAP);
//		if (imageNameToImageDataMap == null)
//		{
//			imageNameToImageDataMap = new HashMap();
//		}
    //END - backward compatibility with the IMAGE_MAP parameter

    Boolean isWrapBreakWordParameter = (Boolean)parameters.get(
        JRHtmlExporterParameter.IS_WRAP_BREAK_WORD);
    if(isWrapBreakWordParameter != null){
      isWrapBreakWord = isWrapBreakWordParameter.booleanValue();
    }

    sizeUnit = (String)parameters.get(JRHtmlExporterParameter.SIZE_UNIT);
    if(sizeUnit == null){
      sizeUnit = JRHtmlExporterParameter.SIZE_UNIT_PIXEL;
    }

    Boolean isUsingImagesToAlignParameter = (Boolean)parameters.get(
        JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN);
    if(isUsingImagesToAlignParameter == null){
      isUsingImagesToAlignParameter = Boolean.TRUE;
    }

    if(isUsingImagesToAlignParameter.booleanValue()){
      emptyCellStringProvider = new StringProvider(){
        public String getStringForCollapsedTD(Object value){
          return "><img src=\"" + value + "px\"";
        }

        public String getStringForEmptyTD(Object value){
          return "<img src=\"" + value + "px\" border=0>";
        }
      };

      loadPxImage();
    } else{
      emptyCellStringProvider = new StringProvider(){
        public String getStringForCollapsedTD(Object value){
          return "";
        }

        public String getStringForEmptyTD(Object value){
          return "&nbsp;";
        }
      };
    }


    fontMap = (Map)parameters.get(JRExporterParameter.FONT_MAP);

    StringBuffer sb = (StringBuffer)parameters.get(JRExporterParameter.
        OUTPUT_STRING_BUFFER);
    if(sb != null){
      try{
        writer = new StringWriter();
        exportReportToWriter();
        sb.append(writer.toString());
      } catch(IOException e){
        throw new JRException("Error writing to StringBuffer writer : "
                              + jasperPrint.getName(), e);
      } finally{
        if(writer != null){
          try{
            writer.close();
          } catch(IOException e){
          }
        }
      }
    } else{
      writer = (Writer)parameters.get(JRExporterParameter.OUTPUT_WRITER);
      if(writer != null){
        try{
          exportReportToWriter();
        } catch(IOException e){
          throw new JRException("Error writing to writer : " + jasperPrint.getName(),
                                e);
        }
      } else{
        OutputStream os = (OutputStream)parameters.get(JRExporterParameter.
            OUTPUT_STREAM);
        if(os != null){
          try{
            writer = new OutputStreamWriter(os, encoding);
            exportReportToWriter();
          } catch(IOException e){
            throw new JRException("Error writing to OutputStream writer : "
                                  + jasperPrint.getName(), e);
          }
        } else{
          File destFile = (File)parameters.get(JRExporterParameter.OUTPUT_FILE);
          if(destFile == null){
            String fileName = (String)parameters.get(JRExporterParameter.
                OUTPUT_FILE_NAME);
            if(fileName != null){
              destFile = new File(fileName);
            } else{
              throw new JRException("No output specified for the exporter.");
            }
          }

          try{
            os = new FileOutputStream(destFile);
            writer = new OutputStreamWriter(os, encoding);
          } catch(IOException e){
            throw new JRException("Error creating to file writer : "
                                  + jasperPrint.getName(), e);
          }

          if(imagesDir == null){
            imagesDir = new File(destFile.getParent(), destFile.getName() + "_files");
          }

          if(isOutputImagesToDirParameter == null){
            isOutputImagesToDir = true;
          }

          if(imagesURI == null){
            imagesURI = imagesDir.getName() + "/";
          }

          try{
            exportReportToWriter();
          } catch(IOException e){
            throw new JRException("Error writing to file writer : "
                                  + jasperPrint.getName(), e);
          } finally{
            if(writer != null){
              try{
                writer.close();
              } catch(IOException e){
              }
            }
          }
        }
      }
    }

    if(isOutputImagesToDir){
      if(imagesDir == null){
        throw new JRException(
            "The images directory was not specified for the exporter.");
      }

      if(isPxImageLoaded || (imagesToProcess != null && imagesToProcess.size() > 0)){
        if(!imagesDir.exists()){
          imagesDir.mkdir();
        }

        if(isPxImageLoaded){
          JRRenderable pxRenderer = JRImageRenderer.getInstance(
              "net/sf/jasperreports/engine/images/pixel.GIF",
              JRImage.ON_ERROR_TYPE_ERROR);
          byte[] imageData = pxRenderer.getImageData();

          File imageFile = new File(imagesDir, "px");
          FileOutputStream fos = null;

          try{
            fos = new FileOutputStream(imageFile);
            fos.write(imageData, 0, imageData.length);
          } catch(IOException e){
            throw new JRException("Error writing to image file : " + imageFile, e);
          } finally{
            if(fos != null){
              try{
                fos.close();
              } catch(IOException e){
              }
            }
          }
        }

        for(Iterator it = imagesToProcess.iterator(); it.hasNext(); ){
          JRPrintElementIndex imageIndex = (JRPrintElementIndex)it.next();

          JRPrintImage image = getImage(jasperPrintList, imageIndex);
          JRRenderable renderer = image.getRenderer();
          if(renderer.getType() == JRRenderable.TYPE_SVG){
            renderer = new JRWrappingSvgRenderer(renderer,
                new Dimension(image.getWidth(), image.getHeight()),
                image.getBackcolor());
          }

          byte[] imageData = renderer.getImageData();

          File imageFile = new File(imagesDir, getImageName(imageIndex));
          FileOutputStream fos = null;

          try{
            fos = new FileOutputStream(imageFile);
            fos.write(imageData, 0, imageData.length);
          } catch(IOException e){
            throw new JRException("Error writing to image file : " + imageFile, e);
          } finally{
            if(fos != null){
              try{
                fos.close();
              } catch(IOException e){
              }
            }
          }
        }
      }
    }

    /*   */
    resetClassLoader();
  }

  /**
   *
   * @param line JRPrintLine
   * @param gridCell JRExporterGridCell
   * @throws IOException
   */

  protected void exportLine(JRPrintLine line, JRExporterGridCell gridCell) throws
      IOException{

    writeCellTDStart(gridCell);

    if(line.getForecolor().getRGB() != Color.white.getRGB()){
      writer.write(" bgcolor=#");
      String hexa = Integer.toHexString(line.getForecolor().getRGB() & colorMask).
                    toUpperCase();
      hexa = ("000000" + hexa).substring(hexa.length());
      writer.write(hexa);
    }

    writer.write(">");

    if(line.getHeight() != 1){
      writer.write(emptyCellStringProvider.getStringForEmptyTD(imagesURI));
    }
    writer.write("</td>\n");
  }

}

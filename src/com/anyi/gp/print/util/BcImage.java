package com.anyi.gp.print.util;

import java.awt.image.BufferedImage;
import net.sourceforge.barbecue.*;
import net.sourceforge.barbecue.linear.code39.Code39Barcode;
import net.sourceforge.barbecue.output.OutputException;

public class BcImage {

  private static Barcode bc = null;

  public BcImage() {
  }

  public static Barcode getBarcode() {
    return bc;
  }

  public static BufferedImage getBarcodeImage(int type, Object aText,
    boolean showText, boolean checkSum) {
    try {
      String text = ((String) (aText));
      switch (type) {
      case 0: // '\0'
        bc = BarcodeFactory.create2of7(text);
        break;

      case 1: // '\001'
        bc = BarcodeFactory.create3of9(text, checkSum);
        break;

      case 2: // '\002'
        bc = BarcodeFactory.createBookland(text);
        break;

      case 3: // '\003'
        bc = BarcodeFactory.createCodabar(text);
        break;

      case 4: // '\004'
        bc = BarcodeFactory.createCode128(text);
        break;

      case 5: // '\005'
        bc = BarcodeFactory.createCode128A(text);
        break;

      case 6: // '\006'
        bc = BarcodeFactory.createCode128B(text);
        break;

      case 7: // '\007'
        bc = BarcodeFactory.createCode128C(text);
        break;

      case 8: // '\b'
        bc = BarcodeFactory.createCode39(text, checkSum);
        break;

      case 9: // '\t'
        bc = BarcodeFactory.createEAN128(text);
        break;

      case 10: // '\n'
        bc = BarcodeFactory.createEAN13(text);
        break;

      case 11: // '\013'
        bc = BarcodeFactory.createGlobalTradeItemNumber(text);
        break;

      case 12: // '\f'
        bc = BarcodeFactory.createInt2of5(text, checkSum);
        break;

      case 13: // '\r'
        bc = BarcodeFactory.createMonarch(text);
        break;

      case 14: // '\016'
        bc = BarcodeFactory.createNW7(text);
        break;

      case 15: // '\017'
        bc = BarcodeFactory.createPDF417(text);
        break;

      case 16: // '\020'
        bc = BarcodeFactory.createSCC14ShippingCode(text);
        break;

      case 17: // '\021'
        bc = BarcodeFactory.createShipmentIdentificationNumber(text);
        break;

      case 18: // '\022'
        bc = BarcodeFactory.createSSCC18(text);
        break;

      case 19: // '\023'
        bc = BarcodeFactory.createStd2of5(text, checkSum);
        break;

      case 20: // '\024'
        bc = BarcodeFactory.createUCC128("", text);
        break;

      case 21: // '\025'
        bc = BarcodeFactory.createUPCA(text);
        break;

      case 22: // '\026'
        bc = BarcodeFactory.createUSD3(text, checkSum);
        break;

      case 23: // '\027'
        bc = BarcodeFactory.createUSD4(text);
        break;

      case 24: // '\030'
        bc = BarcodeFactory.createUSPS(text);
        break;
      case 25:
        bc = new Code39Barcode(text, checkSum, true);
      }
      bc.setDrawingText(showText);
      return BarcodeImageHandler.getImage(bc);
    } catch (BarcodeException e) {
      e.printStackTrace();
    } catch (OutputException e) {
      e.printStackTrace();
    }
    return null;
  }

}

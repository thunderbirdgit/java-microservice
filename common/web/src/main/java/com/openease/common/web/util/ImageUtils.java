package com.openease.common.web.util;

import com.openease.common.util.exception.GeneralUtilException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Image utilities
 *
 * @author Alan Czajkowski
 */
public class ImageUtils {

  private static final transient Logger LOG = LogManager.getLogger(ImageUtils.class);

  private ImageUtils() {
    // not publicly instantiable
  }

  public static Dimension getImageDimensions(InputStream inputStream) throws GeneralUtilException {
    Dimension dimension = null;

    try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream)) {
      if (imageInputStream != null) {
        IIORegistry iioRegistry = IIORegistry.getDefaultInstance();
        Iterator<ImageReaderSpi> iter = iioRegistry.getServiceProviders(ImageReaderSpi.class, true);
        boolean imageDecoded = false;
        while (iter.hasNext() && !imageDecoded) {
          ImageReaderSpi readerSpi = iter.next();
          if (readerSpi.canDecodeInput(imageInputStream)) {
            ImageReader reader = readerSpi.createReaderInstance();
            try {
              reader.setInput(imageInputStream);
              int width = reader.getWidth(reader.getMinIndex());
              LOG.debug("image width: {}", () -> width);
              int height = reader.getHeight(reader.getMinIndex());
              LOG.debug("image height: {}", () -> height);
              dimension = new Dimension(width, height);
            } finally {
              reader.dispose();
            }
            imageDecoded = true;
          }
        }
        if (!imageDecoded) {
          throw new GeneralUtilException("Cannot find decoder for this image");
        }
      } else {
        throw new GeneralUtilException("Cannot open stream for this image");
      }
    } catch (IOException ioe) {
      LOG.error(ioe::getMessage, ioe);
      throw new GeneralUtilException(ioe.getMessage(), ioe);
    }

    return dimension;
  }

}

package com.openease.common.util;

import com.openease.common.util.exception.GeneralUtilException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Resource utilities
 *
 * @author Alan Czajkowski
 */
public final class ResourceUtils {

  private static final transient Logger LOG = LogManager.getLogger(ResourceUtils.class);

  private static final String ERROR_MSG_RESOURCE_PATH_BLANK = "Unable to read resource, resource name cannot be empty";
  private static final String TEMP_FILENAME_PREFIX = "jvmTempFile_";

  private ResourceUtils() {
    // not publicly instantiable
  }

  /**
   * Return the resource URL object.
   *
   * @param resourcePath full path of the resource (if null, throws {@link IllegalArgumentException})
   *
   * @return {@link URL} representing the resource
   */
  public static URL getUrl(String resourcePath) {
    if (isBlank(resourcePath)) {
      throw new IllegalArgumentException(ERROR_MSG_RESOURCE_PATH_BLANK);
    }

    return ResourceUtils.class.getResource(resourcePath);
  }

  /**
   * Return the resource's absolute path.
   *
   * @param resourcePath full path of the resource (if null, throws {@link IllegalArgumentException})
   *
   * @return {@link String} absolute path of the resource
   */
  public static String getPath(String resourcePath) {
    return getUrl(resourcePath).getPath();
  }

  /**
   * Return the a file representing the resource.
   *
   * @param resourcePath full path of the resource (if null, throws {@link IllegalArgumentException})
   *
   * @return {@link File} file representing the resource
   */
  public static File getAsFile(String resourcePath) {
    return new File(getUrl(resourcePath).getPath());
  }

  /**
   * Reads a resource from the classpath into a temp file on the filesystem.
   * (this file should be deleted when it is no longer needed)
   *
   * @param resourcePath full path of the resource (if null, throws {@link IllegalArgumentException})
   *
   * @return (temp) {@link File} containing the resource
   */
  public static File copyResourceToTempFile(String resourcePath) {
    LOG.trace("Copying resource [{}] into temporary file", resourcePath);

    if (isBlank(resourcePath)) {
      throw new IllegalArgumentException(ERROR_MSG_RESOURCE_PATH_BLANK);
    }

    File file;
    try {
      file = File.createTempFile(TEMP_FILENAME_PREFIX, null);
    } catch (IOException ioe) {
      LOG.error("Unable to create temp file on filesystem, check that you have write access", ioe);
      throw new GeneralUtilException("Unable to create temp file on filesystem, check that you have write access", ioe);
    }

    LOG.debug("Reading resource [{}] and writing to temporary file: {}", () -> resourcePath, file::getAbsolutePath);

    try (FileOutputStream fileOutputStream = new FileOutputStream(file); BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
      pipeResourceIntoBufferedOutputStream(resourcePath, bufferedOutputStream);
    } catch (IOException ioe) {
      LOG.error(ioe::getMessage, ioe);
      throw new GeneralUtilException(ioe.getMessage(), ioe);
    }

    return file;
  }

  /**
   * Reads a resource from the classpath and returns it in a {@link String}.
   *
   * @param resourcePath full path of the resource (if null, throws {@link IllegalArgumentException})
   *
   * @return {@link String} containing the contents of the resource
   */
  public static String readResourceIntoString(String resourcePath) {
    LOG.trace("Reading resource [{}] into memory (String)", resourcePath);

    if (isBlank(resourcePath)) {
      throw new IllegalArgumentException(ERROR_MSG_RESOURCE_PATH_BLANK);
    }

    String resourceInString;
    try (StringWriter stringWriter = new StringWriter(); BufferedWriter bufferedWriter = new BufferedWriter(stringWriter)) {
      pipeResourceIntoBufferedWriter(resourcePath, bufferedWriter);
      resourceInString = stringWriter.toString();
    } catch (IOException ioe) {
      LOG.error(ioe::getMessage, ioe);
      throw new GeneralUtilException(ioe.getMessage(), ioe);
    }

    return resourceInString;
  }

  /**
   * Reads a resource into a {@link BufferedReader}.
   *
   * @param resourcePath full path of the resource (if null, throws {@link IllegalArgumentException})
   *
   * @return {@link BufferedReader} containing the contents of the resource
   */
  public static BufferedReader readResourceIntoBufferedReader(String resourcePath) {
    InputStream resourceInputStream = readResourceIntoInputStream(resourcePath);
    InputStreamReader inputStreamReader = new InputStreamReader(resourceInputStream, UTF_8);
    return new BufferedReader(inputStreamReader);
  }

  /**
   * Reads a resource into a {@link BufferedInputStream}.
   *
   * @param resourcePath full path of the resource (if null, throws {@link IllegalArgumentException})
   *
   * @return {@link BufferedInputStream} containing the contents of the resource
   */
  public static BufferedInputStream readResourceIntoBufferedInputStream(String resourcePath) {
    InputStream resourceInputStream = readResourceIntoInputStream(resourcePath);
    return new BufferedInputStream(resourceInputStream);
  }

  /**
   * Pipes a resource into a {@link BufferedWriter}.
   *
   * @param resourcePath   full path of the resource (if null, throws {@link IllegalArgumentException})
   * @param bufferedWriter {@link BufferedWriter} to be written to
   */
  public static void pipeResourceIntoBufferedWriter(String resourcePath, BufferedWriter bufferedWriter) {
    try (BufferedReader bufferedReader = readResourceIntoBufferedReader(resourcePath)) {
      pipe(bufferedReader, bufferedWriter);
    } catch (IOException e) {
      LOG.error(e::getMessage, e);
      throw new GeneralUtilException("Exception occurred while piping input into output", e);
    }
  }

  /**
   * Pipes a resource into a {@link BufferedOutputStream}.
   *
   * @param resourcePath         full path of the resource (if null, throws {@link IllegalArgumentException})
   * @param bufferedOutputStream {@link BufferedOutputStream} to be written to
   */
  public static void pipeResourceIntoBufferedOutputStream(String resourcePath, BufferedOutputStream bufferedOutputStream) {
    try (BufferedInputStream bufferedInputStream = readResourceIntoBufferedInputStream(resourcePath)) {
      pipe(bufferedInputStream, bufferedOutputStream);
    } catch (IOException e) {
      LOG.error(e::getMessage, e);
      throw new GeneralUtilException("Exception occurred while piping input into output", e);
    }
  }

  /**
   * Reads a resource into an {@link InputStream}.
   *
   * @param resourcePath full path of the resource (if null, throws {@link IllegalArgumentException})
   *
   * @return {@link InputStream} containing the contents of the resource
   */
  public static InputStream readResourceIntoInputStream(String resourcePath) {
    if (isBlank(resourcePath)) {
      throw new IllegalArgumentException(ERROR_MSG_RESOURCE_PATH_BLANK);
    }

    InputStream resourceInputStream = ResourceUtils.class.getResourceAsStream(resourcePath);

    if (resourceInputStream == null) {
      String errorMessage = "Unable to read resource into input stream, please check that resource exists";
      LOG.error("{}: {}", errorMessage, resourcePath);
      throw new IllegalArgumentException(errorMessage);
    }

    return resourceInputStream;
  }

  /**
   * Pipes a {@link BufferedReader} into a {@link BufferedWriter}
   *
   * @param input  {@link BufferedReader}
   * @param output {@link BufferedWriter}
   *
   * @throws IOException propagates from {@link BufferedReader} and {@link BufferedWriter}
   */
  private static void pipe(BufferedReader input, BufferedWriter output) throws IOException {
    int character;
    while ((character = input.read()) != -1) {
      output.write(character);
    }
    output.flush();
  }

  /**
   * Pipes a {@link BufferedInputStream} into a {@link BufferedOutputStream}
   *
   * @param input  {@link BufferedInputStream}
   * @param output {@link BufferedOutputStream}
   *
   * @throws IOException propagates from {@link BufferedInputStream} and {@link BufferedOutputStream}
   */
  private static void pipe(BufferedInputStream input, BufferedOutputStream output) throws IOException {
    int character;
    while ((character = input.read()) != -1) {
      output.write(character);
    }
    output.flush();
  }

}

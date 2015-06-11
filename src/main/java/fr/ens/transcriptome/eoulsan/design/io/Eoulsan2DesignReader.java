/*
 *                  Eoulsan development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License version 2.1 or
 * later and CeCILL-C. This should be distributed with the code.
 * If you do not have a copy, see:
 *
 *      http://www.gnu.org/licenses/lgpl-2.1.txt
 *      http://www.cecill.info/licences/Licence_CeCILL-C_V1-en.txt
 *
 * Copyright for this code is held jointly by the Genomic platform
 * of the Institut de Biologie de l'École Normale Supérieure and
 * the individual authors. These should be listed in @author doc
 * comments.
 *
 * For more information on the Eoulsan project and its aims,
 * or to join the Eoulsan Google group, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/eoulsan
 *
 */

package fr.ens.transcriptome.eoulsan.design.io;

import static org.python.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Splitter;

import fr.ens.transcriptome.eoulsan.Globals;
import fr.ens.transcriptome.eoulsan.design.Design;
import fr.ens.transcriptome.eoulsan.design.DesignFactory;
import fr.ens.transcriptome.eoulsan.design.Experiment;
import fr.ens.transcriptome.eoulsan.design.ExperimentSample;
import fr.ens.transcriptome.eoulsan.design.ExperimentSampleMetadata;
import fr.ens.transcriptome.eoulsan.design.Sample;

/**
 * This class define a design reader for Eoulsan 2 design file.
 * @since 2.0
 * @author Xavier Bauquet
 */

public class Eoulsan2DesignReader implements DesignReader {

  static final String SAMPLE_ID_FIELDNAME = "SampleId";
  static final String SAMPLE_NAME_FIELDNAME = "SampleName";
  static final String EXPERIMENT_FIELD_PREFIX = "Exp.";
  static final String PROJECT_NAME_SUFFIX = "projectName";

  static final char EQUAL_SEPARATOR = '=';
  static final char TAB_SEPARATOR = '\t';
  static final char DOT_SEPARATOR = '.';

  private final InputStream is;

  private final Splitter trimTabSplitter = Splitter.on(TAB_SEPARATOR)
      .omitEmptyStrings();
  private final Splitter tabSplitter = Splitter.on(TAB_SEPARATOR).trimResults();
  private final Splitter dotSplitter = Splitter.on(DOT_SEPARATOR).trimResults();

  //
  // Parsing Format header
  //

  /**
   * Parse the header of the new design file including the informations about
   * the design, the genome, the annotations and the informations about the
   * experiments.
   * @param design the design object
   * @param line the line read from your design file
   * @throws IOException
   */
  private void parseHeader(final Design design, final String line)
      throws IOException {

    final int equalPos = line.indexOf(EQUAL_SEPARATOR);

    // Verify that there is only one key and one value
    if (equalPos == -1) {
      throw new IOException("Found a field with two value in the design.");
    }

    final String key = line.substring(0, equalPos).trim();
    final String value = line.substring(equalPos + 1).trim();

    // if the key is empty
    if ("".equals(key)) {
      throw new IOException("Found an empty field name in design file header.");
    }
    // if the value is empty
    if ("".equals(value)) {
      throw new IOException("Found an empty field value in design file header.");
    }

    // If it is an experiment field or a design field
    if (key.startsWith(EXPERIMENT_FIELD_PREFIX)) {
      readExpMetadata(key, value, design);

    } else {
      readDesignMetadata(key, value, design);
    }
  }

  /**
   * Read the experiment metadata from the header part
   * @param key the key of the experiment metadata read
   * @param value the value of the experiment metadata read
   * @param design the design object
   * @throws IOException
   */
  private void readExpMetadata(String key, String value, Design design)
      throws IOException {

    // split the experiment key to extract the
    final List<String> expField = this.dotSplitter.splitToList(key);

    if (expField.size() != 3) {
      throw new IOException("The experiment key is invalide.");
    }

    String expId = expField.get(1);
    String expKey = expField.get(2);

    // Create the experiment if doesn't exist
    if (!design.containsExperiment(expId)) {
      design.addExperiment(expId);

    }

    if (PROJECT_NAME_SUFFIX.equals(expKey)) {

      // Set for experiment name
      design.getExperiment(expId).setName(value);
    } else {

      // Set for other experiment metadata
      if (design.getExperiment(expId).getMetadata().contains(key)) {
        throw new IOException(
            "There is two or more metadata with the same key \""
                + key + "\" in the experiment: " + expId + " file header.");
      }

      design.getExperiment(expId).getMetadata().set(expKey, value);
    }
  }

  /**
   * Read the design metadata from the header.
   * @param key the key of the design metadata
   * @param value the value of the design metadata
   * @param design the design object
   * @throws IOException
   */
  private void readDesignMetadata(String key, String value, Design design)
      throws IOException {

    // If the field name already exist
    if (design.getMetadata().contains(key)) {
      throw new IOException(
          "There is two or more metadata with the same key \""
              + key + "\" in design file header.");
    }
    design.getMetadata().set(key, value);
  }

  //
  // Columns parsing
  //

  /**
   * Parse the column including the information by sample.
   * @param design the design object
   * @param columnNames the name of the columns
   * @param line the line read from your design file
   * @throws IOException
   */
  private void parseColumns(final Design design,
      final List<String> columnNames, final String line, final boolean firstLine)
      throws IOException {

    final List<String> splitLine = this.tabSplitter.splitToList(line);
    // System.out.print(splitLine);
    // System.out.print('\n');

    if (firstLine) {

      // Save the column names
      columnNames.addAll(splitLine);

      final int sampleIdPos = columnNames.indexOf(SAMPLE_ID_FIELDNAME);

      if (sampleIdPos == -1) {
        throw new IOException("Invalid file format: "
            + "No \"" + SAMPLE_ID_FIELDNAME + "\" field found.");
      }

      // Check if the SampleId column is the first one
      if (sampleIdPos != 0) {
        throw new IOException("Invalid file format: "
            + "The \"" + SAMPLE_ID_FIELDNAME
            + "\" field is not the first field.");
      }

      final int sampleNamePos = columnNames.indexOf(SAMPLE_NAME_FIELDNAME);

      if (sampleNamePos == -1) {
        throw new IOException("Invalid file format: "
            + "No \"" + SAMPLE_NAME_FIELDNAME + "\" field found.");
      }

      // Check if the SampleName column is the second one
      if (sampleNamePos != 1) {
        throw new IOException("Invalid file format: "
            + "The \"" + SAMPLE_NAME_FIELDNAME
            + "\" field is not the second field.");
      }

    } else {

      if (splitLine.size() != columnNames.size()) {

        // Check if the line has the same size than the number of column
        throw new IOException("Invalid file format: "
            + "Found " + splitLine.size() + " fields whereas "
            + columnNames.size() + " are required in line: " + line);
      }

      // Save the sampleId
      final String sampleId = splitLine.get(0);
      final String sampleName = splitLine.get(1);
      final Sample sample = design.addSample(sampleId);
      sample.setName(sampleName);

      final Iterator<String> nameIterator = columnNames.iterator();
      final Iterator<String> valueIterator = splitLine.iterator();

      while (nameIterator.hasNext() && valueIterator.hasNext()) {

        // Iterate over the line fields
        final String columnName = nameIterator.next();
        final String columnValue = valueIterator.next();

        if (SAMPLE_ID_FIELDNAME.equals(columnName)
            || SAMPLE_NAME_FIELDNAME.equals(columnName)) {
          continue;
        }

        if (columnName.startsWith(EXPERIMENT_FIELD_PREFIX)) {

          // Test if it's a ExperimentSampleMetadata
          readExperimentSampleMetadata(columnName, columnValue, design, sample);

        } else {

          // Or a SampleMetadata
          readSampleMetadata(columnName, columnValue, design, sample);
        }
      }
    }
  }

  /**
   * Read sample metadata.
   * @param columnName the column name
   * @param columnValue the value
   * @param design the design object
   * @param sampleId the sample id
   * @throws IOException
   */
  private void readSampleMetadata(String columnName, String columnValue,
      Design design, Sample sample) throws IOException {

    if (sample.getMetadata().contains(columnName)) {

      // If the field name already exist
      throw new IOException(
          "There is two or more metadata with the same key \""
              + columnName + "\" in design file header.");
    }
    sample.getMetadata().set(columnName, columnValue);

  }

  /**
   * Read sample metadata experiment referring to a specific experiment.
   * @param columnName the column name
   * @param columnValue the value
   * @param design the design object
   * @param sampleId the sample id
   * @throws IOException
   */
  private void readExperimentSampleMetadata(String columnName,
      String columnValue, Design design, Sample sample) throws IOException {

    // split the column name
    final List<String> expField = this.dotSplitter.splitToList(columnName);

    if (expField.size() != 3) {

      // Check if the experiment key doesn't contain more that 3 entry
      throw new IOException("The experiment key is invalide.");
    }

    // getters
    String expId = expField.get(1);
    String expKey = expField.get(2);

    if (!design.containsExperiment(expId)) {

      // Check if the experiment exists
      throw new IOException("The experiment" + expId + "doesn't exist.");
    }

    final Experiment experiment = design.getExperiment(expId);

    if (!experiment.containsSample(sample)) {

      // Add the sample to the experiment if not in yet
      experiment.addSample(sample);
    }

    final ExperimentSample experimentSample =
        experiment.getExperimentSample(sample);
    final ExperimentSampleMetadata experimentSampleMetadata =
        experimentSample.getMetadata();

    if (experimentSampleMetadata.contains(expKey)) {

      // If the field name already exist
      throw new IOException(
          "There is two or more metadata with the same key \""
              + expKey + "\" in design file header.");
    }

    // Add the experiment sample metadata
    experimentSampleMetadata.set(expKey, columnValue);
  }

  //
  // Read method
  //

  @Override
  public Design read() throws IOException {

    final Design design = DesignFactory.createEmptyDesign();

    boolean header = true;
    boolean firstLine = true;

    final List<String> columnNames = new ArrayList<>();

    final BufferedReader br =
        new BufferedReader(new InputStreamReader(this.is,
            Globals.DEFAULT_CHARSET));

    try {

      final StringBuilder lineBuffer = new StringBuilder();

      String line = null;

      while ((line = br.readLine()) != null) {

        // Trim trailing tabular
        if (header) {

          final List<String> fields = this.trimTabSplitter.splitToList(line);

          if (fields.size() == 1) {
            line = fields.get(0);
          }
        }

        // Concatenate lines that ends with "\\"
        if (header && line.endsWith("\\")) {
          lineBuffer.append(line.substring(0, line.length() - 1));
          continue;
        }

        lineBuffer.append(line);
        line = lineBuffer.toString();

        // go through the lines of the design file
        final String trimmedLine = line.trim();
        lineBuffer.setLength(0);

        if ("".equals(trimmedLine)
            || trimmedLine.startsWith("#") || trimmedLine.startsWith("[")) {

          // If the line is empty or begin by # or [ this line is ignored
          continue;
        }

        if (header && line.indexOf('\t') != -1) {

          // Test if the line is in the header or in the column
          header = false;
        }

        if (header) {

          // Read the Header
          parseHeader(design, line);
        } else {

          // Read the columns
          parseColumns(design, columnNames, line, firstLine);

          if (firstLine) {
            firstLine = false;
          }

        }
      }

    } catch (IOException e) {

      throw new IOException("Error while reading the file: " + e.getMessage(),
          e);
    }

    try {
      br.close();
    } catch (IOException e) {
      throw new IOException("Error while closing the file: " + e.getMessage(),
          e);
    }

    return design;
  }

  //
  // Constructors
  //

  /**
   * Public constructor.
   * @param file file to read
   * @throws FileNotFoundException if the file cannot be found
   * @throws IOException if an error occurs while reading the file or if the
   *           file is null.
   */
  public Eoulsan2DesignReader(final File file) throws FileNotFoundException {

    checkNotNull(file, "the file argument cannot be null");

    this.is = new FileInputStream(file);
  }

  /**
   * Public constructor
   * @param is Input stream to read
   * @throws IOException if the stream is null
   */
  public Eoulsan2DesignReader(final InputStream is) throws IOException {

    checkNotNull(is, "the is argument cannot be null");

    this.is = is;
  }

  /**
   * Public constructor
   * @param filename File to read
   * @throws IOException if the stream is null
   * @throws FileNotFoundException if the file doesn't exist
   */
  public Eoulsan2DesignReader(final String filename) throws IOException,
      FileNotFoundException {

    checkNotNull(filename, "the filename argument cannot be null");

    this.is = new FileInputStream(filename);
  }

}

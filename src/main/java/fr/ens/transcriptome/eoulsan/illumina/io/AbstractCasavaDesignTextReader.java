/*
 *                      Nividic development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.eoulsan.illumina.io;

import java.io.IOException;
import java.util.List;

import fr.ens.transcriptome.eoulsan.illumina.CasavaDesign;
import fr.ens.transcriptome.eoulsan.illumina.CasavaSample;

/**
 * This class allow to easily write reader for CasavaDesign in text format.
 * @author Laurent Jourdren
 */
public abstract class AbstractCasavaDesignTextReader implements
    CasavaDesignReader {

  private static final String[] FIELDNAMES = new String[] {"FCID", "Lane",
      "SampleID", "SampleRef", "Index", "Description", "Control", "Recipe",
      "Operator", "SampleProject"};

  private CasavaDesign design;
  private boolean firstLine = true;

  protected void parseLine(final List<String> fields) throws IOException {

    if (design == null)
      this.design = new CasavaDesign();

    checkFieldNumber(fields);

    if (firstLine) {
      this.firstLine = false;

      for (int i = 0; i < fields.size(); i++)
        if (!FIELDNAMES[i].toLowerCase().equals(fields.get(i).toLowerCase()))
          throw new IOException("Invalid field name: " + fields.get(i));

      return;
    }

    final CasavaSample sample = new CasavaSample();

    sample.setFlowCellId(fields.get(0));
    sample.setLane(parseLane(fields.get(1)));
    sample.setSampleId(fields.get(2));
    sample.setSampleRef(fields.get(3));
    sample.setIndex(fields.get(4));
    sample.setDescription(fields.get(5));
    sample.setControl(parseControlField(fields.get(6)));
    sample.setRecipe(fields.get(7));
    sample.setOperator(fields.get(8));
    sample.setSampleProject(fields.get(9));

    design.addSample(sample);
  }

 

  private static final boolean parseControlField(final String value)
      throws IOException {

    if ("".equals(value))
      throw new IOException("Empty value in the control field");

    if ("Y".equals(value) || "y".equals(value))
      return true;

    if ("N".equals(value) || "n".equals(value))
      return false;

    throw new IOException("Invalid value for the control field: " + value);
  }

  private static final void checkFieldNumber(final List<String> fields)
      throws IOException {

    if (fields.size() == 10)
      return;

    if (fields.size() < 10)
      throw new IOException("Invalid number of field (" + fields.size() + "), 10 excepted.");

    for (int i = 10; i < fields.size(); i++)
      if (!"".equals(fields.get(i).trim()))
        throw new IOException("Invalid number of field (" + fields.size() + "), 10 excepted.");

  }

  private static final int parseLane(final String s) throws IOException {

    if (s == null)
      return 0;

    final double d;
    try {
      d = Double.parseDouble(s);

    } catch (NumberFormatException e) {
      throw new IOException("Invalid lane number: " + s);
    }

    final int result = (int) d;

    if (d - result > 0)
      throw new IOException("Invalid lane number: " + s);

    return result;
  }

  protected CasavaDesign getDesign() {

    return design;
  }

}
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

package fr.ens.transcriptome.eoulsan.bio.expressioncounters;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * This class define a service to retrieve an ExpressionCounter.
 * @since 1.2
 * @author Claire Wallon
 */
public class ExpressionCounterService {

  private static ExpressionCounterService service;

  //
  // Static method
  //

  /**
   * Retrieve the singleton static instance of ExpressionCounterService.
   * @return A ExpressionCounterService instance
   */
  public static synchronized ExpressionCounterService getInstance() {

    if (service == null) {
      service = new ExpressionCounterService();
    }

    return service;
  }

  //
  // Instance methods
  //

  /**
   * Get ExpressionCounter.
   * @param counterName name of the counter to get
   * @return an ExpressionCounter
   */
  public ExpressionCounter getCounter(final String counterName) {

    if (counterName == null) {
      return null;
    }

    final String counterNameLower = counterName.toLowerCase();

    final Iterator<ExpressionCounter> it =
        ServiceLoader.load(ExpressionCounter.class).iterator();

    while (it.hasNext()) {

      final ExpressionCounter counter = it.next();

      if (counterNameLower.equals(counter.getCounterName().toLowerCase())) {
        return counter;
      }
    }

    return null;
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private ExpressionCounterService() {
  }

}

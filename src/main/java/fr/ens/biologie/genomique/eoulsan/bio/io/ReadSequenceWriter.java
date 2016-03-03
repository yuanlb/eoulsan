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
 * of the Institut de Biologie de l'École normale supérieure and
 * the individual authors. These should be listed in @author doc
 * comments.
 *
 * For more information on the Eoulsan project and its aims,
 * or to join the Eoulsan Google group, visit the home page
 * at:
 *
 *      http://outils.genomique.biologie.ens.fr/eoulsan
 *
 */

package fr.ens.biologie.genomique.eoulsan.bio.io;

import java.io.Closeable;
import java.io.IOException;

import fr.ens.biologie.genomique.eoulsan.bio.ReadSequence;

/**
 * This interface define the methods to implements for ReadSequenceWriter.
 * @since 1.0
 * @author Laurent Jourdren
 */
public interface ReadSequenceWriter extends Closeable {

  /**
   * Write a read.
   * @param readSequence the read to write
   * @throws IOException if an error occurs while writing data
   */
  void write(ReadSequence readSequence) throws IOException;

}

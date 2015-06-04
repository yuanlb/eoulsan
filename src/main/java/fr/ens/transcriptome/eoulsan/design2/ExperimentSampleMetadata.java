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


package fr.ens.transcriptome.eoulsan.design2;

import java.io.Serializable;

/**
 * This class defines the experiment sample metadata.
 * @author Xavier Bauquet
 * @since 2.0
 */

public class ExperimentSampleMetadata extends AbstractMetadata implements Serializable{
  
  /** Serialization version UID. */
  private static final long serialVersionUID = 4079804296437126108L;
  
  // constants
  public static final String REPTECHGROUP_KEY = "RepTechGroup";
  public static final String REFERENCE_KEY ="Reference";
  public static final String CONDITION_KEY = "Condition";
  
  //
  // Getters
  //
  
  /**
   * Get the RepTechGroup.
   * @return the RepTechGroup
   */
  public String getRepTechGroup(){
    return get(REPTECHGROUP_KEY);
  }
  
  /**
   * Get the reference.
   * @return the reference
   */
  public String getReference(){
    return get(REFERENCE_KEY);
  }
  
  /**
   * Get the condition.
   * @return the condition
   */
  public String getCondition(){
    return get(CONDITION_KEY);
  }
  //
  // Setters
  //
  
  /**
   * Set the ReptechGroup.
   * @param newReptechGroup the new ReptechGroup
   */
  public void setRepTechGroup(String newReptechGroup){
    set(REPTECHGROUP_KEY, newReptechGroup);
  }
  
  /**
   * Set the reference.
   * @param newReptechGroup the new reference
   */
  public void setReference(String newReference){
    set(REFERENCE_KEY, newReference);
  }
  
  /**
   * Set the condition.
   * @param newCondition the new condition
   */
  public void setCondition(String newCondition){
    set(CONDITION_KEY, newCondition);
  }
  
  //
  // Contains
  //
  
  /**
   * Test if the RepTechGroup field exists.
   * @return true if the RepTechGroup field exists
   */
  public boolean containsRepTechGroup(){
    return contains(REPTECHGROUP_KEY);
  }
  
  /**
   * Test if the reference field exists.
   * @return true if the reference field exists
   */
  public boolean containsReference() {
    return contains(REFERENCE_KEY);
  }
  
  /**
   * Test if the condition field exists.
   * @return true if the condition field exists
   */
  public boolean containsCondition(){
    return contains(CONDITION_KEY);
  }
  
  //
  // Constructor
  //
  
  public ExperimentSampleMetadata(){
  }
}

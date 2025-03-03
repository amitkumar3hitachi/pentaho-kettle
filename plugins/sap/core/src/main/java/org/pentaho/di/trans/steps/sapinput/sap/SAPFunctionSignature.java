/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.di.trans.steps.sapinput.sap;

import java.util.Collection;
import java.util.Vector;

public class SAPFunctionSignature {

  private Collection<SAPField> input = new Vector<SAPField>();
  private Collection<SAPField> output = new Vector<SAPField>();

  @Override
  public String toString() {
    return "SAPFunctionSignature [input=" + input + ", output=" + output + "]";
  }

  public Collection<SAPField> getInput() {
    return input;
  }

  public void setInput( Collection<SAPField> input ) {
    this.input = input;
  }

  public void addInput( SAPField input ) {
    this.input.add( input );
  }

  public Collection<SAPField> getOutput() {
    return output;
  }

  public void setOutput( Collection<SAPField> output ) {
    this.output = output;
  }

  public void addOutput( SAPField output ) {
    this.output.add( output );
  }

}

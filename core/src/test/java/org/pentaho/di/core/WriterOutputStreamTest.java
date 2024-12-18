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

package org.pentaho.di.core;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Writer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WriterOutputStreamTest {
  Writer writer = mock( Writer.class );
  final String encoding = "UTF8";

  @Before
  public void setup() {
    writer = mock( Writer.class );
  }

  @Test
  public void testConstructor() {
    WriterOutputStream stream = new WriterOutputStream( writer );
    assertSame( writer, stream.getWriter() );
    stream = new WriterOutputStream( writer, encoding );
    assertSame( writer, stream.getWriter() );
    assertSame( encoding, stream.getEncoding() );
  }

  @Test
  public void testWrite() throws IOException {
    WriterOutputStream stream = new WriterOutputStream( writer );
    stream.write( 68 );
    stream.write( "value".getBytes(), 1, 3 );
    stream.write( "value".getBytes() );
    stream.flush();
    stream.close();
    verify( writer ).append( new String( new byte[] { (byte) 68 } ) );
    verify( writer ).append( "alu" );
    verify( writer ).append( "value" );
    verify( writer ).flush();
    verify( writer ).close();
    assertNull( stream.getWriter() );

    writer = mock( Writer.class );
    WriterOutputStream streamWithEncoding = new WriterOutputStream( writer, encoding );
    streamWithEncoding.write( "value".getBytes( encoding ) );
    verify( writer ).append( "value" );
  }
}

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


package org.pentaho.di.core.util;

import java.security.SecureRandom;
import java.util.Random;

public class UUID4Util {
  /** SecureRandom (or Random as failover) used to generate UUID's */
  private static Random random;

  /** Used to build output as hex. Adapted from org.apache.commons.id.Hex */
  private static final char[] DIGITS = {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  /**
   * Constructor. Initializes random generator, attempting first to use SecureRandom, then failing over to Random.
   */
  public UUID4Util() {
    try {
      random = SecureRandom.getInstance( "SHA1PRNG", "SUN" );
    } catch ( Exception e ) {
      random = new Random();
    }
  }

  /**
   * Generate byte array using random generator. Code adapted from org.apache.commons.id.uuid.VersionFourGenerator.java
   *
   * @return
   */
  private byte[] getBytes() {
    byte[] raw = new byte[16];
    random.nextBytes( raw );
    raw[6] &= 0x0F;
    raw[6] |= ( 4 << 4 );
    raw[8] &= 0x3F; // 0011 1111
    raw[8] |= 0x80; // 1000 0000
    return raw;
  }

  /**
   * Turn a byte array into a version four UUID string. Adapted from org.apache.commons.id.uuid.UUID.java
   *
   * @param raw
   * @return
   */
  private String getUUIDString( byte[] raw ) {
    StringBuilder buf = new StringBuilder( new String( encodeHex( raw ) ) );
    while ( buf.length() != 32 ) {
      buf.insert( 0, "0" );
    }
    buf.ensureCapacity( 32 );
    buf.insert( 8, '-' );
    buf.insert( 13, '-' );
    buf.insert( 18, '-' );
    buf.insert( 23, '-' );
    return buf.toString();
  }

  /**
   * Converts an array of bytes into an array of characters representing the hexidecimal values of each byte in order.
   * The returned array will be double the length of the passed array, as it takes two characters to represent any given
   * byte.
   *
   * Adapted from org.apache.commons.id.Hex
   *
   * @param data
   *          a byte[] to convert to Hex characters
   * @return A char[] containing hexidecimal characters
   */
  private static char[] encodeHex( byte[] data ) {
    int l = data.length;
    char[] out = new char[l << 1];
    for ( int i = 0, j = 0; i < l; i++ ) {
      out[j++] = DIGITS[( 0xF0 & data[i] ) >>> 4];
      out[j++] = DIGITS[0x0F & data[i]];
    }
    return out;
  }

  /**
   * Generates a string representation of a version four UUID.
   *
   * @return
   */
  public String getUUID4AsString() {
    return getUUIDString( getBytes() );
  }
}

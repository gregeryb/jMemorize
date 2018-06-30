/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang;

import java.util.Arrays;

/**
 * <p>
 * Operations on {@link java.lang.String} that are <code>null</code> safe.</p>
 *
 * <ul>
 * <li><b>IsEmpty/IsBlank</b>
 * - checks if a String contains text</li>
 * <li><b>Trim/Strip</b>
 * - removes leading and trailing whitespace</li>
 * <li><b>Equals</b>
 * - compares two strings null-safe</li>
 * <li><b>startsWith</b>
 * - check if a String starts with a prefix null-safe</li>
 * <li><b>endsWith</b>
 * - check if a String ends with a suffix null-safe</li>
 * <li><b>IndexOf/LastIndexOf/Contains</b>
 * - null-safe index-of checks
 * <li><b>IndexOfAny/LastIndexOfAny/IndexOfAnyBut/LastIndexOfAnyBut</b>
 * - index-of any of a set of Strings</li>
 * <li><b>ContainsOnly/ContainsNone/ContainsAny</b>
 * - does String contains only/none/any of these characters</li>
 * <li><b>Substring/Left/Right/Mid</b>
 * - null-safe substring extractions</li>
 * <li><b>SubstringBefore/SubstringAfter/SubstringBetween</b>
 * - substring extraction relative to other strings</li>
 * <li><b>Split/Join</b>
 * - splits a String into an array of substrings and vice versa</li>
 * <li><b>Remove/Delete</b>
 * - removes part of a String</li>
 * <li><b>Replace/Overlay</b>
 * - Searches a String and replaces one String with another</li>
 * <li><b>Chomp/Chop</b>
 * - removes the last part of a String</li>
 * <li><b>LeftPad/RightPad/Center/Repeat</b>
 * - pads a String</li>
 * <li><b>UpperCase/LowerCase/SwapCase/Capitalize/Uncapitalize</b>
 * - changes the case of a String</li>
 * <li><b>CountMatches</b>
 * - counts the number of occurrences of one String in another</li>
 * <li><b>IsAlpha/IsNumeric/IsWhitespace/IsAsciiPrintable</b>
 * - checks the characters in a String</li>
 * <li><b>DefaultString</b>
 * - protects against a null input String</li>
 * <li><b>Reverse/ReverseDelimited</b>
 * - reverses a String</li>
 * <li><b>Abbreviate</b>
 * - abbreviates a string using ellipsis</li>
 * <li><b>Difference</b>
 * - compares Strings and reports on their differences</li>
 * <li><b>LevensteinDistance</b>
 * - the number of changes needed to change one String into another</li>
 * </ul>
 *
 * <p>
 * The <code>StringUtils</code> class defines certain words related to String
 * handling.</p>
 *
 * <ul>
 * <li>null - <code>null</code></li>
 * <li>empty - a zero-length string (<code>""</code>)</li>
 * <li>space - the space character (<code>' '</code>, char 32)</li>
 * <li>whitespace - the characters defined by
 * {@link Character#isWhitespace(char)}</li>
 * <li>trim - the characters &lt;= 32 as in {@link String#trim()}</li>
 * </ul>
 *
 * <p>
 * <code>StringUtils</code> handles <code>null</code> input Strings quietly.
 * That is to say that a <code>null</code> input will return <code>null</code>.
 * Where a <code>boolean</code> or <code>int</code> is being returned details
 * vary by method.</p>
 *
 * <p>
 * A side effect of the <code>null</code> handling is that a
 * <code>NullPointerException</code> should be considered a bug in
 * <code>StringUtils</code> (except for deprecated methods).</p>
 *
 * <p>
 * Methods in this class give sample code to explain their operation. The symbol
 * <code>*</code> is used to indicate any input including <code>null</code>.</p>
 *
 * <p>
 * #ThreadSafe#</p>
 *
 * @see java.lang.String
 * @author Apache Software Foundation
 * @author <a href="http://jakarta.apache.org/turbine/">Apache Jakarta
 * Turbine</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author Daniel L. Rall
 * @author <a href="mailto:gcoladonato@yahoo.com">Greg Coladonato</a>
 * @author <a href="mailto:ed@apache.org">Ed Korthof</a>
 * @author <a href="mailto:rand_mcneely@yahoo.com">Rand McNeely</a>
 * @author <a href="mailto:fredrik@westermarck.com">Fredrik Westermarck</a>
 * @author Holger Krauth
 * @author <a href="mailto:alex@purpletech.com">Alexander Day Chaffee</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author Arun Mammen Thomas
 * @author Gary Gregory
 * @author Phil Steitz
 * @author Al Chou
 * @author Michael Davey
 * @author Reuben Sivan
 * @author Chris Hyzer
 * @author Scott Johnson
 * @since 1.0
 * @version $Id: StringUtils.java 1058365 2011-01-13 00:04:49Z niallp $
 */
//@Immutable
public class StringUtils {
 // Performance testing notes (JDK 1.4, Jul03, scolebourne)
 // Whitespace:
 // Character.isWhitespace() is faster than WHITESPACE.indexOf()
 // where WHITESPACE is a string of all whitespace characters
 //
 // Character access:
 // String.charAt(n) versus toCharArray(), then array[n]
 // String.charAt(n) is about 15% worse for a 10K string
 // They are about equal for a length 50 string
 // String.charAt(n) is about 4 times better for a length 3 string
 // String.charAt(n) is best bet overall
 //
 // Append:
 // String.concat about twice as fast as StringBuffer.append
 // (not sure who tested this)

 /**
  * The empty String <code>""</code>.
  *
  * @since 2.0
  */
 public static final String EMPTY = "";
 /**
  * Represents a failed index search.
  *
  * @since 2.1
  */
 public static final int INDEX_NOT_FOUND = -1;
 /**
  * <p>
  * The maximum size to which the padding constant(s) can expand.</p>
  */
 private static final int PAD_LIMIT = 8192;

 // ContainsNone
 //-----------------------------------------------------------------------
 /**
  * <p>
  * Checks that the String does not contain certain characters.</p>
  *
  * <p>
  * A <code>null</code> String will return <code>true</code>. A
  * <code>null</code> invalid character array will return <code>true</code>. An
  * empty String (length()=0) always returns true.</p>
  *
  * <pre>
  * StringUtils.containsNone(null, *)       = true
  * StringUtils.containsNone(*, null)       = true
  * StringUtils.containsNone("", *)         = true
  * StringUtils.containsNone("ab", '')      = true
  * StringUtils.containsNone("abab", 'xyz') = true
  * StringUtils.containsNone("ab1", 'xyz')  = true
  * StringUtils.containsNone("abz", 'xyz')  = false
  * </pre>
  *
  * @param str the String to check, may be null
  * @param searchChars an array of invalid chars, may be null
  * @return true if it contains none of the invalid chars, or is null
  * @since 2.0
  */
 public static boolean containsNone(String str, char[] searchChars) {
  if (str == null || searchChars == null) {
   return true;
  }
  int csLen = str.length();
  int csLast = csLen - 1;
  int searchLen = searchChars.length;
  int searchLast = searchLen - 1;
  for (int i = 0; i < csLen; i++) {
   char ch = str.charAt(i);
   for (int j = 0; j < searchLen; j++) {
    if (searchChars[j] == ch) {
     if (CharUtils.isHighSurrogate(ch)) {
      if (j == searchLast) {
       // missing low surrogate, fine, like String.indexOf(String)
       return false;
      }
      if (i < csLast && searchChars[j + 1] == str.charAt(i + 1)) {
       return false;
      }
     } else {
      // ch is in the Basic Multilingual Plane
      return false;
     }
    }
   }
  }
  return true;
 }
 // ContainsAny
 //-----------------------------------------------------------------------

 /**
  * <p>
  * Checks if the String contains any character in the given set of
  * characters.</p>
  *
  * <p>
  * A <code>null</code> String will return <code>false</code>. A
  * <code>null</code> or zero length search array will return
  * <code>false</code>.</p>
  *
  * <pre>
  * StringUtils.containsAny(null, *)                = false
  * StringUtils.containsAny("", *)                  = false
  * StringUtils.containsAny(*, null)                = false
  * StringUtils.containsAny(*, [])                  = false
  * StringUtils.containsAny("zzabyycdxx",['z','a']) = true
  * StringUtils.containsAny("zzabyycdxx",['b','y']) = true
  * StringUtils.containsAny("aba", ['z'])           = false
  * </pre>
  *
  * @param str the String to check, may be null
  * @param searchChars the chars to search for, may be null
  * @return the <code>true</code> if any of the chars are found,
  * <code>false</code> if no match or null input
  * @since 2.4
  */
 public static boolean containsAny(String str, char[] searchChars) {
  if (str.isEmpty() || searchChars.length == 0) {
   return false;
  }
  int csLength = str.length();
  int searchLength = searchChars.length;
  int csLast = csLength - 1;
  int searchLast = searchLength - 1;
  for (int i = 0; i < csLength; i++) {
   char ch = str.charAt(i);
   for (int j = 0; j < searchLength; j++) {
    if (searchChars[j] == ch) {
     if (CharUtils.isHighSurrogate(ch)) {
      if (j == searchLast) {
       // missing low surrogate, fine, like String.indexOf(String)
       return true;
      }
      if (i < csLast && searchChars[j + 1] == str.charAt(i + 1)) {
       return true;
      }
     } else {
      // ch is in the Basic Multilingual Plane
      return true;
     }
    }
   }
  }
  return false;
 }

}

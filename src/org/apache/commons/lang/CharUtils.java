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

/**
 * <p>
 * Operations on char primitives and Character objects.</p>
 *
 * <p>
 * This class tries to handle <code>null</code> input gracefully. An exception
 * will not be thrown for a <code>null</code> input. Each method documents its
 * behaviour in more detail.</p>
 *
 * <p>
 * #ThreadSafe#</p>
 *
 * @author Apache Software Foundation
 * @since 2.1
 * @version $Id: CharUtils.java 1056988 2011-01-09 17:58:53Z niallp $
 */
public class CharUtils {

 // ----------------- Following code copied from Apache Harmony (Character class)
 /**
  * Indicates whether {@code ch} is a high- (or leading-) surrogate code unit
  * that is used for representing supplementary characters in UTF-16 encoding.
  *
  * @param ch the character to test.
  * @return {@code true} if {@code ch} is a high-surrogate code unit;
  * {@code false} otherwise.
  */
 static boolean isHighSurrogate(char ch) {
  return ('\uD800' <= ch && '\uDBFF' >= ch);
 }

}

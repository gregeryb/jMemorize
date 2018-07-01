/*
 * jMemorize - Learning made easy (and fun) - A Leitner flashcards tool
 * Copyright(C) 2004-2008 Riad Djemili and contributors
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package jmemorize.core;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * A card is made up of two card sides which can contain various contents, the
 * most important being text.
 *
 * @author djemili
 */
public final class CardSide implements Cloneable {

 private String m_text;

 public CardSide() {
 }

 public CardSide(String text) {
  setText(text);
 }

 public String getText() {
  return m_text;
 }

 public String getRowText() {
  return getUnformattedText();
 }

 public String getUnformattedText() {
  return StringEscapeUtils.unescapeHtml(
   String.join(" ",
    m_text.replaceAll("<.*?>", " ")
     .split("\\s+"))
    .trim());
 }

 /**
  * Note that using this method won't modify the modification date of the card.
  * Use {@link Card#setSides(String, String)} instead for modifications done by
  * the user.
  */
 public void setText(String text) {
  if (text.equals(m_text)) {
   return;
  }
  m_text = text;
 }

 /**
  * @return the unformatted string representation of the formatted text.
  * @see java.lang.Object#toString()
  */
 public String toString() {
  return m_text;
 }

 /*
  * (non-Javadoc) @see java.lang.Object#clone()
  */
 public Object clone() throws CloneNotSupportedException {
  CardSide cardSide = new CardSide();
  cardSide.m_text = m_text;
  return cardSide;
 }

}

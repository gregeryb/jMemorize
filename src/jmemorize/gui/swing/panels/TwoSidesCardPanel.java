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
package jmemorize.gui.swing.panels;

import java.awt.Font;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jmemorize.core.Settings;
import jmemorize.core.Settings.CardFontObserver;
import jmemorize.gui.LC;
import jmemorize.gui.Localization;
import jmemorize.gui.swing.CardFont;
import jmemorize.gui.swing.CardFont.FontType;

/**
 * A card panel with two text card sides.
 *
 * @author djemili
 */
public class TwoSidesCardPanel extends CardPanel implements CardFontObserver {

 private CardSidePanel m_frontSide = new CardSidePanel();
 private CardSidePanel m_backSide = new CardSidePanel();

 public TwoSidesCardPanel(boolean showCategoryBox) {
  this(showCategoryBox, true);
 }

 public TwoSidesCardPanel(boolean showCategoryBox, boolean showSecondCardSide) {
  super(showCategoryBox);
  addCardSide(Localization.get(LC.FRONTSIDE), m_frontSide);
  if (showSecondCardSide) {
   addCardSide(Localization.get(LC.FLIPSIDE), m_backSide);
  }
  Settings.addCardFontObserver(this);
  Settings.setCardFont(this, FontType.CARD_FRONT, FontType.CARD_FLIP);
  DocumentListener docListener = new DocumentListener() {
   @Override
   public void changedUpdate(DocumentEvent e) {
    notifyTextObservers();
   }

   @Override
   public void insertUpdate(DocumentEvent e) {
    notifyTextObservers();
   }

   @Override
   public void removeUpdate(DocumentEvent e) {
    notifyTextObservers();
   }

  };
  m_frontSide.addDocumentListener(docListener);
  m_backSide.addDocumentListener(docListener);
 }

 public void setSecondCardSide(CardSidePanel cardSidePanel) {
  // remove old second card side
  m_backSide = cardSidePanel;
  // add new second card side
 }

 /**
  * Sets front- and backside and focuses text area for frontside.
  */
 public void setTextSides(String frontside, String backside) {
  m_frontSide.setText(frontside);
  m_backSide.setText(backside);
  m_frontSide.home();
  m_backSide.home();
  m_frontSide.requestFocus();
 }

 /**
  * @return The text inside of the front side textpane.
  */
 public String getFrontText() {
  return m_frontSide.getText();
 }

 /**
  * @return The text inside of the back side textpane.
  */
 public String getBackText() {
  return m_backSide.getText();
 }

 /**
  * @return The text inside of the front side textpane.
  */
 public String getFrontUnformattedText() {
  return m_frontSide.getUnformattedText();
 }

 /**
  * @return The text inside of the back side textpane.
  */
 public String getBackUnformattedText() {
  return m_backSide.getUnformattedText();
 }

 /**
  * Clears both text areas and give the first focus.
  */
 public void reset() {
  setTextSides("", "");
  //m_frontSide.getTextPane().requestFocus();
 }

 /**
  * Enable/Disable flipped card sides mode. Enabling this mode will use the
  * front side font on the flip side panel and the flip side font on the front
  * side panel.
  *
  * (This is needed because CardPanel is also used to present the answers in
  * learn sessions.)
  *
  * @param enable <code>true</code> to enable flipped sides mode.
  * <code>false</code> to disable.
  */
 public void setFlipped(boolean enable) {
  if (enable != m_flippedCardSides) {
   Font font = m_frontSide.getFont();
   m_frontSide.setFont(m_backSide.getFont());
   m_backSide.setFont(font);
   m_flippedCardSides = enable;
  }
 }

 /**
  * @return true if the card panel currently contains a saveable/valid card.
  */
 public boolean isValidCard() {
  boolean validFront
   = m_frontSide.getText().length() > 0;
  boolean validBack
   = m_backSide.getText().length() > 0;
  return validFront && validBack;
 }

 /*
  * (non-Javadoc) @see jmemorize.core.Settings.CardFontObserver
  */
 public void fontChanged(FontType type, CardFont font) {
  if (type == FontType.CARD_FRONT && m_frontSide != null) {
   m_frontSide.setCardFont(font);
  } else if (type == FontType.CARD_FLIP && m_backSide != null) {
   m_backSide.setCardFont(font);
  }
 }

}

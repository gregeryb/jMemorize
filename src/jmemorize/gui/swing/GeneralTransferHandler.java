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
package jmemorize.gui.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import jmemorize.core.Card;
import jmemorize.core.Category;
import jmemorize.core.Main;
import jmemorize.gui.swing.widgets.CardTable;
import jmemorize.gui.swing.widgets.CategoryTree;

/**
 * Organizes datatransfers between the card table and the category tree.
 *
 * @author djemili
 */
public class GeneralTransferHandler extends TransferHandler {

 public class CardsTransferable implements Transferable {

  private List<Card> m_cards;

  public CardsTransferable(List<Card> cards) {
   m_cards = cards;
  }

  public Object getTransferData(DataFlavor flavor) throws
   UnsupportedFlavorException {
   if (!isDataFlavorSupported(flavor)) {
    throw new UnsupportedFlavorException(flavor);
   }
   if (CARDS_FLAVOR.equals(flavor)) {
    return m_cards;
   }
   StringBuffer buffer = new StringBuffer();
   for (Card card : m_cards) {
    buffer.append(card.getFrontSide().getText());
    buffer.append(" - ");
    buffer.append(card.getBackSide().getText());
    buffer.append('\n');
   }
   return buffer.toString();
  }

  public DataFlavor[] getTransferDataFlavors() {
   return new DataFlavor[]{CARDS_FLAVOR, DataFlavor.stringFlavor};
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
   return CARDS_FLAVOR.equals(flavor) || DataFlavor.stringFlavor.equals(flavor);
  }

 }

 public class CategoryTransferable implements Transferable {

  private Category m_category;

  public CategoryTransferable(Category category) {
   m_category = category;
  }

  public Object getTransferData(DataFlavor flavor) throws
   UnsupportedFlavorException {
   if (!isDataFlavorSupported(flavor)) {
    throw new UnsupportedFlavorException(flavor);
   }
   if (CATEGORY_FLAVOR.equals(flavor)) {
    return m_category;
   }
   return m_category.getName();
  }

  public DataFlavor[] getTransferDataFlavors() {
   return new DataFlavor[]{CATEGORY_FLAVOR, DataFlavor.stringFlavor};
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
   return CATEGORY_FLAVOR.equals(flavor) || DataFlavor.stringFlavor.equals(
    flavor);
  }

 }
 /**
  * Represents a formatted text and it source document. We need the source for
  * CUT-operations where we need to remove the formatted section from the
  * original document.
  */
 public final static DataFlavor CARDS_FLAVOR
  = new DataFlavor(Card.class, "Card"); //$NON-NLS-1$
 public final static DataFlavor CATEGORY_FLAVOR
  = new DataFlavor(Category.class, "Category"); //$NON-NLS-1$

 public GeneralTransferHandler() {
 }


 /*
  * @see javax.swing.TransferHandler
  */
 public int getSourceActions(JComponent c) {
  return COPY_OR_MOVE;
 }

 /*
  * @see javax.swing.TransferHandler
  */
 public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
  if (comp instanceof CategoryTree) {
   for (int i = 0; i < transferFlavors.length; i++) {
    if (transferFlavors[i] == CARDS_FLAVOR || transferFlavors[i]
     == CATEGORY_FLAVOR) {
     return true;
    }
   }
  }
  return false;
 }

 /*
  * @see javax.swing.TransferHandler
  */
 @SuppressWarnings("unchecked")
 public boolean importData(JComponent comp, Transferable t) {
  try {
   Category targetCategory;
   if (comp instanceof CategoryTree) {
    CategoryTree tree = (CategoryTree) comp;
    targetCategory = tree.getSelectedCategory();
   } else if (comp instanceof CardTable) {
    CardTable table = (CardTable) comp;
    targetCategory = table.getView().getCategory();
   } else {
    return false;
   }
   if (t.isDataFlavorSupported(CARDS_FLAVOR)) {
    List<Card> cards = (List<Card>) t.getTransferData(CARDS_FLAVOR);
    for (Card card : cards) {
     targetCategory.addCard((Card) card.clone(), card.getLevel());
    }
    return true;
   } else if (t.isDataFlavorSupported(CATEGORY_FLAVOR)) {
    Category category = (Category) t.getTransferData(CATEGORY_FLAVOR);
    if (!category.contains(targetCategory)) {
     targetCategory.addCategoryChild(copyCategories(category));
     return true;
    } else {
     return false;
    }
   }
  } catch (Exception e) {
  }
  return false;
 }

 /*
  * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
  */
 protected Transferable createTransferable(JComponent c) {
  if (c instanceof CardTable) {
   CardTable table = (CardTable) c;
   return new CardsTransferable(table.getSelectedCards());
  } else if (c instanceof CategoryTree) {
   CategoryTree tree = (CategoryTree) c;
   Category category = tree.getSelectedCategory();
   // dont allow operations with root category
   return category.getParent() != null ? new CategoryTransferable(category) : null;
  }
  return null;
 }

 /*
  * @see javax.swing.TransferHandler#exportDone
  */
 @SuppressWarnings("unchecked")
 protected void exportDone(JComponent source, Transferable data, int action) {
  if (action != MOVE) {
   return;
  }
  try {
   if (data.isDataFlavorSupported(CARDS_FLAVOR)) {
    CardTable table = (CardTable) source;
    Category category = table.getView().getCategory();
    List<Card> cards = (List<Card>) data.getTransferData(CARDS_FLAVOR);
    for (Card card : cards) {
     category.removeCard(card);
    }
   } else if (data.isDataFlavorSupported(CATEGORY_FLAVOR)) {
    Category category = (Category) data.getTransferData(CATEGORY_FLAVOR);
    category.remove();
   }
  } catch (Exception e) {
  }
 }

 private Category copyCategories(Category original) throws
  CloneNotSupportedException {
  Category copy = new Category(original.getName());
  // first copy categories..
  for (Category category : original.getChildCategories()) {
   copy.addCategoryChild(copyCategories(category));
  }
  // ..then copy cards
  for (int i = 0; i < original.getNumberOfDecks(); i++) {
   for (Card card : original.getLocalCards(i)) {
    copy.addCard((Card) card.clone(), i);
   }
  }
  return copy;
 }

}

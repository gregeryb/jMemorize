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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledEditorKit.StyledTextAction;
import jmemorize.gui.swing.ColorConstants;
import jmemorize.gui.swing.widgets.CategoryComboBox;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.Sizes;

/**
 * A panel that displays the front and flip side of a card.
 *
 * @author djemili
 */
public class CardPanel extends JPanel {

 /**
  * A interface that allows to listen for textchanges to the card side text
  * panes. Use {@link CardPanel#addTextObserver} method to hook it to the
  * CardPanel.
  */
 public interface CardPanelObserver {

  public void onTextChanged();

 }

 
 private class ShowCardSideButton extends JButton implements ActionListener {

  private String m_text;
  private int[] m_sides;

  public ShowCardSideButton(String text, int... sides) {
   m_text = text;
   m_sides = sides;
   m_showSideButtons.add(this);
   setBackground(ColorConstants.CARD_SIDE_BAR_COLOR);
   addActionListener(this);
  }

  public void actionPerformed(ActionEvent e) {
   for (int i = 0; i < m_cardSidesPanel.getComponentCount(); i++) {
    setCardSideVisible(i, hasSide(i));
   }
   updateCardSideButtons();
  }

  public boolean hasSide(int index) {
   for (int i = 0; i < m_sides.length; i++) {
    if (m_sides[i] == index) {
     return true;
    }
   }
   return false;
  }

  private void updateText() {
   boolean highlight = true;
   for (int i = 0; i < m_cardSidesPanel.getComponentCount(); i++) {
    highlight &= hasSide(i) == isCardSideVisible(i);
   }
   String name = highlight ? "[" + m_text + "]" : m_text;
   setText("  " + name + "  ");
  }

 }
 protected boolean m_flippedCardSides = false;
 private boolean m_verticalLayout = true;
 private CategoryComboBox m_categoryBox = new CategoryComboBox();
 private List<CardPanelObserver> m_observers = new LinkedList<>();
 private List<CardSidePanel> m_cardSides = new LinkedList<>();
 private List<ShowCardSideButton> m_showSideButtons = new LinkedList<>();
 private JPanel m_cardSidesPanel;
 private boolean editable;

 /**
  * Creates new form EditCardPanel
  */
 public CardPanel(boolean allowEdits) {
  editable = allowEdits;
  initComponent();
  updateCardSideButtons();
 }

 public final void addCardSide(String title, JComponent component) {
  JPanel cardSideWithTitle = wrapCardSide(title, component);
  if (component instanceof CardSidePanel) {
   CardSidePanel cardSide = (CardSidePanel) component;
   m_cardSides.add(cardSide);
   cardSide.setEditable(editable);
  }
  m_cardSidesPanel.add(cardSideWithTitle);
  updateCardSideButtons();
  updateCardSideBorders();
 }

 public void removeCardSide(int index) {
  m_cardSidesPanel.getComponent(index);
  m_cardSidesPanel.remove(index);
  // TODO
 }

 public void setCardSideVisible(int index, boolean visible) {
  m_cardSidesPanel.getComponent(index).setVisible(visible);
  updateCardSideBorders();
  updateCardSideButtons();
 }

 public void setCardSideEnabled(int index, boolean enabled) {
  for (ShowCardSideButton button : m_showSideButtons) {
   if (button.hasSide(index)) {
    button.setEnabled(enabled);
   }
  }
//        m_showSideButtons.get(index).setEnabled(enabled);
 }

 public boolean isCardSideVisible(int index) {
  if (index >= m_cardSidesPanel.getComponentCount()) {
   return false;
  }
  return m_cardSidesPanel.getComponent(index).isVisible();
 }

 /**
  * @param editable <code>true</code> if front/back side textpanes should be
  * editable. <code>false</code> otherwise.
  */
 public void setEditable(boolean editable) {
  this.editable = editable;
  for (CardSidePanel cardSide : m_cardSides) {
   cardSide.setEditable(editable);
  }
 }

 public List<CardSidePanel> getCardSides() {
  return Collections.unmodifiableList(m_cardSides);
 }

 public CategoryComboBox getCategoryComboBox() {
  return m_categoryBox;
 }

 /**
  * Adds a text observer that will be triggered when the text of the frontside
  * textpane or backside textpane is changed by the users key input.
  *
  * @param observer The text observer that is to be added as observer.
  */
 public void addObserver(CardPanelObserver observer) {
  m_observers.add(observer);
 }

 /**
  * Notify all observers that the text of the frontside textpane or backside
  * textpane has been changed by the users keyinput.
  */
 protected void notifyTextObservers() {
  for (CardPanelObserver observer : m_observers) {
   observer.onTextChanged();
  }
 }

 private void updateCardSideButtons() {
  for (ShowCardSideButton action : m_showSideButtons) {
   action.updateText();
  }
 }

 private void updateCardSideBorders() {
  int margin = Sizes.dialogUnitYAsPixel(3, this);
  int mx = 0;
  int my = 0;
  if (m_verticalLayout) {
   my = margin;
  } else {
   mx = margin;
  }
  boolean addBorder = false;
  for (int i = 0; i < m_cardSidesPanel.getComponentCount(); i++) {
   Component comp = m_cardSidesPanel.getComponent(i);
   if (!(comp instanceof JPanel)) {
    continue;
   }
   JPanel sidePanel = (JPanel) comp;
   if (addBorder) {
    sidePanel.setBorder(new EmptyBorder(my, mx, 0, 0));
   } else {
    sidePanel.setBorder(null);
   }
   if (sidePanel.isVisible()) {
    addBorder = true;
   }
  }
 }

 private JPanel wrapCardSide(String title, JComponent cardSide) {
  FormLayout layout = new FormLayout(
   //            "38dlu, 3dlu, d:grow", // columns //$NON-NLS-1$
   "d:grow", // columns //$NON-NLS-1$
   "fill:20dlu:grow"); // rows //$NON-NLS-1$
  CellConstraints cc = new CellConstraints();
  DefaultFormBuilder builder = new DefaultFormBuilder(layout);
//        builder.addLabel(title, cc.xy(1, 1, "left, top")); //$NON-NLS-1$
//        builder.add(cardSide, cc.xy(3, 1 ));
  builder.add(cardSide, cc.xy(1, 1));
  return builder.getPanel();
 }

 private void initComponent() {
  setLayout(new BorderLayout());
  JPanel topPanel = new JPanel();
  topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
  topPanel.add(buildInnerPanel(buildSetSidesToolbar()));
  add(topPanel, BorderLayout.NORTH);
  m_cardSidesPanel = new JPanel();
  m_cardSidesPanel.setLayout(new BoxLayout(m_cardSidesPanel, BoxLayout.Y_AXIS));
  add(m_cardSidesPanel, BorderLayout.CENTER);
 }

 private JToolBar buildSetSidesToolbar() {
  JToolBar toolBar = new JToolBar();
  toolBar.add(new ShowCardSideButton("Frontside/Flipside", 0, 1));
  toolBar.add(new ShowCardSideButton("Frontside", 0));
  toolBar.add(new ShowCardSideButton("Flipside", 1));
  toolBar.setBorder(new EtchedBorder());
  toolBar.setBackground(ColorConstants.CARD_SIDE_BAR_COLOR);
  toolBar.setFloatable(false);
  return toolBar;
 }

 private JPanel buildCategoryPanel() {
  CellConstraints cc = new CellConstraints();
  DefaultFormBuilder builder;
  FormLayout layout = new FormLayout(
   //            "38dlu, 3dlu, d:grow", // columns //$NON-NLS-1$
   "d:grow", // columns //$NON-NLS-1$
   "p, 3dlu"); // rows //$NON-NLS-1$
  builder = new DefaultFormBuilder(layout);
//        builder.addLabel(Localization.get(LC.CATEGORY), cc.xy ( 1, 1));
//        builder.add(m_categoryBox, cc.xy(3, 1));
  builder.add(m_categoryBox, cc.xy(1, 1));
  return builder.getPanel();
 }

 private JPanel buildInnerPanel(Component comp) {
  CellConstraints cc = new CellConstraints();
  DefaultFormBuilder builder;
  FormLayout layout = new FormLayout(
   //            "38dlu, 3dlu, d:grow", // columns //$NON-NLS-1$
   "d:grow", // columns //$NON-NLS-1$
   "p, 3dlu"); // rows //$NON-NLS-1$
  builder = new DefaultFormBuilder(layout);
//        builder.add(comp, cc.xy (3, 1));
  builder.add(comp, cc.xy(1, 1));
  return builder.getPanel();
 }

}

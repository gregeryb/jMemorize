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

import com.hexidec.ekit.EkitCore;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.io.File;
import java.net.MalformedURLException;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.StyleSheet;
import jmemorize.core.Main;
import jmemorize.gui.CardTextPane;
import jmemorize.gui.swing.CardFont;
import jmemorize.gui.swing.ColorConstants;

/**
 * @author djemili
 */
public class CardSidePanel extends JPanel  {

 private JRootPane root = new JRootPane();
 private CardTextPane pane = new CardTextPane();
 private CardFont m_cardFont;

 public CardSidePanel() {
  initComponents();
 }

 /**
  * @return The text inside of the Frontside textpane.
  */
 public String getText() {
  return pane.getDocumentText();
 }

 public String getUnformattedText() {
  String s = "";
  try {
   s = pane.getHTMLDoc().getText(0, pane.getHTMLDoc().getLength());
  } catch (BadLocationException ex) {
  }
  return s;
 }

 public void requestFocus() {
  pane.requestFocus();
 }
 
 public void setCardFont(CardFont cardFont) {
  m_cardFont = cardFont;
  StyleSheet sheet = pane.getHTMLEditorKit().getStyleSheet();
  sheet.removeStyle("body");
  Font f = m_cardFont.getFont();
  sheet.addRule(new StringBuffer("body { font-size: ").append(f.getSize())
   .append("pt")
   .append("; font-family: ").append(f.getName()).append("; }").toString());
 }

 /**
  * @param text
  * @return
  */
 public Document setText(String text) {
  try {
   Main main = Main.getInstance();
   File file = main.getLesson().getFile();
   if (file != null) {
    pane.getHTMLDoc().setBase(file.getParentFile().toURI().toURL());
    pane.setImageBaseDir(file.getParentFile());
   }
  } catch (MalformedURLException ex) {
  }
  pane.setText(text);
  // scroll to top
  pane.scrollRectToVisible(new Rectangle());
  return pane.getHTMLDoc();
 }
public void addDocumentListener( DocumentListener docListener){
 pane.getHTMLDoc().addDocumentListener(docListener);
}
 public void showToolbars() {
 }

 public void setEditable(boolean editable) {
  pane.setEditable(editable);
  root.getContentPane().removeAll();
  /*
   * Add the components to the app
   */
  root.getContentPane().setLayout(new GridBagLayout());
  GridBagConstraints gbc = new GridBagConstraints();
  //gbc.gridheight = 1;
  gbc.gridwidth = 1;
  gbc.gridx = 1;
  gbc.weightx = 1.0;
  if (editable) {
   gbc.anchor = GridBagConstraints.NORTH;
   gbc.fill = GridBagConstraints.HORIZONTAL;
   gbc.weighty = 0.0;
   gbc.gridy = 1;
   root.getContentPane().add(pane.getToolBarMain(true), gbc);
   gbc.gridy = 2;
   root.getContentPane().add(pane.getToolBarFormat(true), gbc);
   gbc.gridy = 3;
   root.getContentPane().add(pane.getToolBarStyles(true), gbc);
   JMenuBar menu = pane.getCustomMenuBar(new String[]{
    EkitCore.KEY_MENU_EDIT,
    EkitCore.KEY_MENU_VIEW,
    EkitCore.KEY_MENU_FONT,
    EkitCore.KEY_MENU_FORMAT,
    EkitCore.KEY_MENU_SEARCH,
    EkitCore.KEY_MENU_INSERT,
    EkitCore.KEY_MENU_TABLE,
    EkitCore.KEY_MENU_FORMS,
    EkitCore.KEY_MENU_HELP
   });
   root.setJMenuBar(menu);
   root.setEnabled(false);
  } else {
   root.setJMenuBar(null);
  }
  gbc.anchor = GridBagConstraints.SOUTH;
  gbc.fill = GridBagConstraints.BOTH;
  gbc.weighty = 1.0;
  gbc.gridy = 4;
  root.getContentPane().add(pane, gbc);
 }

 private void initComponents() {

  pane.setBackground(ColorConstants.CARD_PANEL_COLOR);
  JPanel mainPanel = new JPanel(new BorderLayout());
  mainPanel.add(root, BorderLayout.CENTER);
  Color color = UIManager.getColor("InternalFrame.borderShadow"); //$NON-NLS-1$
  if (color == null) {
   color = new Color(167, 166, 170);
  }
  Border border = new LineBorder(color);
  mainPanel.setBorder(border);
  setLayout(new BorderLayout());
  add(mainPanel, BorderLayout.CENTER);
 }

 void home() {
  pane.home();
 }

}

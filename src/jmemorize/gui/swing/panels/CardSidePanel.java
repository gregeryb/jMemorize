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

import com.sun.glass.ui.Cursor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import jmemorize.core.Main;
import jmemorize.gui.CardHTMLEditorKit;
import jmemorize.gui.CardTextPane;
import jmemorize.gui.swing.CardFont;
import jmemorize.gui.swing.ColorConstants;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author djemili
 */
public class CardSidePanel extends JPanel {

 private enum Mode {
  TEXT
 };
 private JPanel m_contentPanel;
 private CardTextPane pane = new CardTextPane();
 private JScrollPane m_textScrollPane = new JScrollPane(pane);
 private CardFont m_cardFont;

 public CardSidePanel() {
  initComponents();
  setImageMode(Mode.TEXT);
 }

 /**
  * @return The text inside of the Frontside textpane.
  */
 public String getText() {
  return pane.getText();
 }

 public String getUnformattedText() {
  String s = "";
  try {
   s = pane.getDocument().getText(0, pane.getDocument().getLength());
  } catch (BadLocationException ex) {
  }
  return s;
 }

 public void setEditable(boolean editable) {
  pane.setEditable(editable);
 }

 public void requestFocus() {
  pane.requestFocus();
 }

 public void setCardFont(CardFont cardFont) {
  m_cardFont = cardFont;
  HTMLEditorKit kit = (HTMLEditorKit) pane.getEditorKit();
  StyleSheet sheet = kit.getStyleSheet();
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
    Path path = FileSystems.getDefault().getPath(file.getParent());
    ((HTMLDocument) pane.getDocument()).setBase(path.toUri().toURL());
   }
  } catch (MalformedURLException ex) {
  }
  pane.setText(text);
  // scroll to top
  pane.scrollRectToVisible(new Rectangle());
  return pane.getDocument();
 }

 public void addCaretListener(CaretListener listener) {
  pane.addCaretListener(listener);
 }

 public JEditorPane getTextPane() {
  return pane;
 }

 private void setImageMode(Mode mode) {
  m_contentPanel.removeAll();
  switch (mode) {
   case TEXT:
    m_contentPanel.setLayout(new BorderLayout());
    m_contentPanel.add(m_textScrollPane, BorderLayout.CENTER);
    pane.requestFocus();
    break;
  }
  pane.validate();
  m_contentPanel.validate();
  m_contentPanel.repaint();
 }

 private void initComponents() {
  HTMLEditorKit kit = new CardHTMLEditorKit();
  kit.setDefaultCursor(java.awt.Cursor.getPredefinedCursor(Cursor.CURSOR_TEXT));
  pane.setEditorKit(kit);
  pane.setBackground(ColorConstants.CARD_PANEL_COLOR);
  m_textScrollPane.setHorizontalScrollBarPolicy(
   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
  m_textScrollPane.setBorder(null);
  m_contentPanel = new JPanel(new BorderLayout());
  JPanel mainPanel = new JPanel(new BorderLayout());
  mainPanel.add(m_contentPanel, BorderLayout.CENTER);
  // we want to use the default scrollpane border
  Color color = UIManager.getColor("InternalFrame.borderShadow"); //$NON-NLS-1$
  if (color == null) {
   color = new Color(167, 166, 170);
  }
  Border border = new LineBorder(color);
  mainPanel.setBorder(border);
  setLayout(new BorderLayout());
  add(mainPanel, BorderLayout.CENTER);
 }

 public void addImage(File file) {
  pane.replaceSelection("");
  HTMLEditorKit kit = (HTMLEditorKit) pane.getEditorKit();
  HTMLDocument doc = (HTMLDocument) pane.getDocument();
  String url = StringEscapeUtils.escapeHtml(file.getName());
  String img_tag = "<img src=\"" + url + "\"/>";
  try {
   kit.insertHTML(doc, pane.getCaretPosition(), img_tag, 0, 0, null);
  } catch (BadLocationException | IOException ex) {
  }
 }

}

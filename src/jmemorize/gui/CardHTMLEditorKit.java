/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmemorize.gui;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author Gregery Barton
 */
public class CardHTMLEditorKit extends HTMLEditorKit {

 private StyleSheet style_sheet;

 public CardHTMLEditorKit() {
  style_sheet = new StyleSheet();
 }

 @Override
 public StyleSheet getStyleSheet() {
  return style_sheet;
 }

 @Override
 public void setStyleSheet(StyleSheet s) {
  style_sheet = s;
 }

}

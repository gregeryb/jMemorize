/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmemorize.gui;

import com.hexidec.ekit.EkitCore;
import java.awt.event.FocusEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;

/**
 *
 * @author Gregery Barton
 */
public class CardTextPane extends EkitCore {

 private static final String TOOLBAR = "CT|CP|PS|SP|UN|RE|SP|FN|SP|UC|UM|SP|SR|*|BL|IT|UD|SP|SK|SU|SB|SP|AL|AC|AR|AJ|SP|UL|OL|SP|LK|*|ST|SP|FO";

 public CardTextPane() {
  super(false, "", null, true, false, true, true, null, null, false, true,
   TOOLBAR, true);
 }

 public void setText(String t) {
  setDocumentText(t);
  /*
   * change absolute image paths to relative, filename only.
   */
  URL base = getHTMLDoc().getBase();
  if (base != null) {
   Element[] elements = getHTMLDoc().getRootElements();
   List<String> paths = get_image_paths(elements);
   String text = getDocumentText();
   for (String src_path : paths) {
    try {
     URL url = new URL(src_path);
     Path path = FileSystems.getDefault().getPath(
      url.getPath().replaceAll("^\\/+", ""));
     String rel = path.getFileName().toString();
     text = text.replace(src_path, rel);
    } catch (MalformedURLException ex) {
    }
   }
   if (!paths.isEmpty()) {
    setDocumentText(text);
   }
  }
 }

 private List<String> get_image_paths(Element[] elements) {
  List<String> paths = new ArrayList<>();
  for (int i = 0; i < elements.length; i++) {
   Element element = elements[i];
   get_image_paths(element, paths);
  }
  return paths;
 }

 private void get_image_paths(Element e, List<String> paths) {
  AttributeSet attrs = e.getAttributes();
  Object elementName
   = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
  Object o = (elementName != null)
   ? null : attrs.getAttribute(StyleConstants.NameAttribute);
  if (o instanceof HTML.Tag) {
   HTML.Tag kind = (HTML.Tag) o;
   if (kind == HTML.Tag.IMG) {
    String src = (String) e.getAttributes().getAttribute(HTML.Attribute.SRC);
    if (src.substring(0, 6).equalsIgnoreCase("file:/")) {
     paths.add(src);
    }
   }
  }
  for (int i = 0; i < e.getElementCount(); ++i) {
   Element s = e.getElement(i);
   get_image_paths(s, paths);
  }
 }

 @Override
 public void focusLost(FocusEvent fe) {
  super.focusLost(fe);
  if (fe.getOppositeComponent() instanceof JTextComponent) {
   disableInterface();
  }
 }

 @Override
 public void focusGained(FocusEvent fe) {
  enableInterface();
  super.focusGained(fe);
 }

}

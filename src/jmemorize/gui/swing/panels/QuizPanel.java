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
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import jmemorize.core.Card;
import jmemorize.core.CardSide;
import jmemorize.core.Category;
import jmemorize.core.CategoryObserver;
import jmemorize.core.Events;
import jmemorize.core.Main;
import jmemorize.core.Settings;
import jmemorize.core.learn.LearnSession;
import jmemorize.core.learn.LearnSessionObserver;
import jmemorize.core.learn.LearnSession.LearnCardObserver;
import jmemorize.gui.LC;
import jmemorize.gui.Localization;
import jmemorize.gui.swing.CardFont;
import jmemorize.gui.swing.Quiz;
import jmemorize.gui.swing.CardFont.FontType;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

/**
 * @author djemili
 */
public class QuizPanel extends JPanel implements Events,
 LearnCardObserver, CategoryObserver, LearnSessionObserver, HyperlinkListener {

 //  swing elements
 private TwoSidesCardPanel m_questionCardPanel = new TwoSidesCardPanel(false,
  false);
 private Quiz quiz = new ThinkQuiz();
 private JTextPane prompt = new JTextPane();
 private JPanel m_barPanel = new JPanel();
 private LearnSession m_session;
 private Card m_currentCard;
 private boolean m_showFlipped;
 private boolean m_frontWasVisible;
 private JCheckBox m_categoryCheckBox = new JCheckBox(
  Localization.get(LC.LEARN_SHOW_CATEGORY));
 private JTextField m_categoryField = new JTextField();
 private boolean m_isShowQuestion;
 private boolean m_isShowAnswer;
 private static String PREFS_SHOW_CARD_CATEGORY = "show.card-category"; //$NON-NLS-1$

 public QuizPanel() {
  initComponents();
  Main.getInstance().addLearnSessionObserver(this);
 }

 /*
  * (non-Javadoc) @see jmemorize.core.learn.LearnSessionObserver
  */
 public void sessionStarted(LearnSession session) {
  m_session = session;
  session.addObserver(this);
  m_session.getCategory().addObserver(this);
 }

 /*
  * (non-Javadoc) @see jmemorize.core.learn.LearnSessionObserver
  */
 public void sessionEnded(LearnSession session) {
  m_isShowQuestion = false;
  m_isShowAnswer = false;
  m_session.getCategory().removeObserver(this);
 }

 /**
  * Show the card.
  *
  * @param flipped <code>true</code> if card should be shown with reversed sides
  * (that is the frontside will be shown as flipside and vice versa)
  * <code>false</code> otherwise.
  */
 public void nextCardFetched(Card card, boolean flipped) {
  m_currentCard = card;
  m_showFlipped = flipped;
  updateFonts();
  updateCardSidePanels();
  updateCategoryField();
  showQuestion();
 }

 /*
  * (non-Javadoc) @see jmemorize.core.CategoryObserver
  */
 public void onCardEvent(int type, Card card, Category category, int deck) {
  if (card == m_currentCard) {
   if (type == EDITED_EVENT) {
    updateCardSidePanels();
   }
   if (type == MOVED_EVENT) {
    updateCategoryField();
   }
  }
 }

 /*
  * (non-Javadoc) @see jmemorize.core.CategoryObserver
  */
 public void onCategoryEvent(int type, Category category) {
  assert false; // no category events should occur while learning
 }

 private void updateFonts() {
  CardFont frontFont = Settings.loadFont(FontType.LEARN_FRONT);
  CardFont flipFont = Settings.loadFont(FontType.LEARN_FLIP);
  CardFont questionFont = !m_showFlipped ? frontFont : flipFont;
  CardFont answerFont = !m_showFlipped ? flipFont : frontFont;
  m_questionCardPanel.fontChanged(FontType.CARD_FRONT, questionFont);
  m_questionCardPanel.fontChanged(FontType.CARD_FLIP, answerFont);
  quiz.setQuestionFont(questionFont);
  quiz.setAnswerFont(answerFont);
 }

 private void initComponents() {
  m_questionCardPanel.setEditable(false);
  m_questionCardPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
  m_questionCardPanel.addCardSide(Localization.get(LC.FLIPSIDE), quiz
   .getVisual());
  setLayout(new BorderLayout());
  m_barPanel.setLayout(new CardLayout());
  m_barPanel.add(prompt);
  JPanel mainCardPanel = new JPanel(new BorderLayout());
  mainCardPanel.setBorder(new EtchedBorder());
  JPanel catPanel = buildCategoryPanel();
  mainCardPanel.add(catPanel, BorderLayout.NORTH);
  mainCardPanel.add(m_questionCardPanel, BorderLayout.CENTER);
  mainCardPanel.add(m_barPanel, BorderLayout.SOUTH);
  add(mainCardPanel, BorderLayout.CENTER);
  HTMLEditorKit kit = new HTMLEditorKit();
  prompt.setContentType("text/html");
  prompt.setEditable(false);
  kit.getStyleSheet().addRule(
   "body {" + "	font-size: 24px;" + "text-align: left;" + "	margin-left: 7pc;"
   + "}");
  kit.getStyleSheet().addRule(
   "table {" + "	margin-left: 15pc;" + "}"
  );
  prompt.addHyperlinkListener(this);
 }

 @Override
 public void hyperlinkUpdate(HyperlinkEvent e) {
  if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
   String obj = (String) e.getDescription();
   switch (obj) {
    case "show":
     if (m_isShowQuestion) {
      showAnswer();
     }
     break;
    case "skip":
     if (m_isShowQuestion) {
      m_session.cardSkipped();
     }
     break;
    case "yes":
     if (m_isShowAnswer) {
      m_session.cardChecked(true, m_showFlipped);
     }
     break;
    case "no":
     if (m_isShowAnswer) {
      m_session.cardChecked(false, m_showFlipped);
     }
     break;
   }
  }
 }

 /**
  * Fills the text panes with the card side texts of the currently shown card.
  */
 private void updateCardSidePanels() {
  if (m_currentCard != null) {
   CardSide questionSide = !m_showFlipped
    ? m_currentCard.getFrontSide()
    : m_currentCard.getBackSide();
   CardSide answerSide = !m_showFlipped
    ? m_currentCard.getBackSide()
    : m_currentCard.getFrontSide();
   m_questionCardPanel
    .setTextSides(questionSide.getText(), answerSide.getText());
   quiz.showQuestion(answerSide);
  }
  m_questionCardPanel.setFlipped(m_showFlipped);
 }

 private void updateCategoryField() {
  m_categoryField.setEnabled(m_categoryCheckBox.isSelected());
  m_categoryField.setText(m_categoryCheckBox.isSelected()
   ? m_currentCard.getCategory().getPath()
   + " (" + m_currentCard.getLevel() + ")" : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
 }

 private void showQuestion() {
  if (m_isShowAnswer) {
   m_frontWasVisible = m_questionCardPanel.isCardSideVisible(0)
    && m_questionCardPanel.isCardSideVisible(1);
  }
  m_questionCardPanel.setCardSideVisible(0, true);
  m_questionCardPanel.setCardSideVisible(1, false);
  m_questionCardPanel.setCardSideEnabled(1, false);
  m_isShowQuestion = true;
  m_isShowAnswer = false;
  prompt.setText(
   ("<body class=\"body\">\n" + " \n" + "  <p>@try</p>\n"
    + "  <table border=\"0\">\n" + "    <tr>\n"
    + "      <td><a href=\"show\">@show</a></td>|\n"
    + "      <td><a href=\"skip\">@skip</a></td>\n" + "    </tr>\n"
    + "  </table>\n" + "</body>")
    .replace("@try", quiz.getHelpText())
    .replace("@show", Localization.get(LC.LEARN_SHOW))
    .replace("@skip", Localization.get(LC.LEARN_SKIP)));
 }

 private void showAnswer() {
  m_questionCardPanel.setCardSideVisible(0, m_frontWasVisible);
  m_questionCardPanel.setCardSideVisible(1, true);
  m_questionCardPanel.setCardSideEnabled(1, true);
  m_isShowQuestion = false;
  m_isShowAnswer = true;
  float result = quiz.showAnswer();
  if (result >= 0) // HACK
  {
   m_session.cardChecked((result >= 0.5f), m_showFlipped);
  }
  prompt.setText(
   ("<body class=\"body\">\n" + " \n" + "  <p>@ask</p>\n"
    + "  <table border=\"0\">\n" + "    <tr>\n"
    + "      <td><a href=\"yes\">@yes</a></td>|\n"
    + "      <td><a href=\"no\">@no</a></td>\n" + "    </tr>\n" + "  </table>\n"
    + "</body>")
    .replace("@ask", Localization.get(LC.LEARN_DID_YOU_KNOW))
    .replace("@yes", Localization.get(LC.LEARN_YES))
    .replace("@no", Localization.get(LC.LEARN_NO)));
 }

 private JPanel buildCategoryPanel() {
  // prepare category field and checkbox
  m_categoryCheckBox.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
    boolean showCategory = m_categoryCheckBox.isSelected();
    Main.USER_PREFS.putBoolean(PREFS_SHOW_CARD_CATEGORY, showCategory);
    updateCategoryField();
   }

  });
  m_categoryField.setEditable(false);
  boolean showCat = Main.USER_PREFS.getBoolean(PREFS_SHOW_CARD_CATEGORY, true);
  m_categoryCheckBox.setSelected(showCat);
  // build it using the forms layout
  FormLayout layout = new FormLayout(
   "38dlu, 3dlu, p:grow, 3dlu, right:p", // columns //$NON-NLS-1$
   "20px");                              // rows    //$NON-NLS-1$
  CellConstraints cc = new CellConstraints();
  DefaultFormBuilder builder = new DefaultFormBuilder(layout);
  builder.setBorder(new EmptyBorder(10, 10, 7, 10));
  builder.addLabel(Localization.get(LC.CATEGORY), cc.xy(1, 1));
  builder.add(m_categoryField, cc.xy(3, 1));
  builder.add(m_categoryCheckBox, cc.xy(5, 1));
  return builder.getPanel();
 }

}

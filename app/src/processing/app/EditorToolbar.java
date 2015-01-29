/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyirght (c) 2012-15 The Processing Foundation
  Copyright (c) 2004-12 Ben Fry and Casey Reas
  Copyright (c) 2001-04 Massachusetts Institute of Technology

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, version 2.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package processing.app;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Run/Stop button plus Mode selection
 */
abstract public class EditorToolbar extends JPanel {
  static final int HIGH = 80;
  
  protected Editor editor;
  protected Base base;
  protected Mode mode;

  protected EditorButton runButton;
  protected EditorButton stopButton;
  protected EditorButton currentButton;
  
  protected Box box;
  protected JLabel label;
  
//  int GRADIENT_TOP = 192;
//  int GRADIENT_BOTTOM = 246;
  protected Image backgroundGradient;
  protected Image reverseGradient;
  
  
  public EditorToolbar(Editor editor) {
    this.editor = editor;
    base = editor.getBase();
    mode = editor.getMode();
    
    //setOpaque(false);
    //gradient = createGradient();
    //System.out.println(gradient);
    
    backgroundGradient = mode.getGradient("header", 400, HIGH);
    reverseGradient = mode.getGradient("reversed", 100, EditorButton.DIM);
    
    runButton = new EditorButton(mode, 
                                 "/lib/toolbar/run",
                                 Language.text("toolbar.run"), 
                                 Language.text("toolbar.present")) {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        handleRun();
      }
    };
    
    stopButton = new EditorButton(mode,
                                  "/lib/toolbar/stop",
                                  Language.text("toolbar.stop")) {                            
      
      @Override
      public void actionPerformed(ActionEvent e) {
        handleStop();
      }
    };
    
    box = Box.createHorizontalBox();
    box.add(runButton);
    
    label = new JLabel();
    label.setFont(mode.getFont("toolbar.sketch.font"));
    label.setForeground(mode.getColor("toolbar.sketch.color"));
    box.add(label);
    currentButton = runButton;
    
    box.add(Box.createHorizontalGlue());
    Component items = createModeButtons();
    if (items != null) {
      box.add(items);
    }
    ModeSelector ms = new ModeSelector(); 
    box.add(ms);
    
    setLayout(new BorderLayout());
    add(box, BorderLayout.CENTER);
  }
  
  
  public void paintComponent(Graphics g) {
//    super.paintComponent(g);
    Dimension size = getSize();
    g.drawImage(backgroundGradient, 0, 0, size.width, size.height, this);
  }
  
  
  public Component createModeButtons() {
    return null;
  }
  
  
//  public Component createModeSelector() {
//    return new ModeSelector();
//  }
  
  
  protected void swapButton(EditorButton replacement) {
    if (currentButton != replacement) {
      box.remove(0);
      box.add(replacement, 0);
      box.revalidate();
      box.repaint();  // may be needed
      currentButton = replacement;
    }
  }
  
  
  public void activateRun() { 
    //runButton.setPressed(true);
//    Rectangle bounds = runButton.getBounds();
//    remove(runButton);
    swapButton(stopButton);
  }
  
  
  public void deactivateRun() { 
    
  }
  
  
  public void activateStop() { 
  }
  
  
  public void deactivateStop() {
    swapButton(runButton);
  }
  
  
  abstract public void handleRun();
  
  
  abstract public void handleStop();
  

  public Dimension getPreferredSize() {
    return new Dimension(super.getPreferredSize().width, HIGH);
  }
  
  
  public Dimension getMinimumSize() {
    return new Dimension(super.getMinimumSize().width, HIGH);
  }
  
  
  public Dimension getMaximumSize() {
    return new Dimension(super.getMaximumSize().width, HIGH);
  }
  
  
  class ModeSelector extends JPanel {
    Image offscreen;
    int width, height;
    
    String title; 
    Font titleFont;
    Color titleColor;
    int titleAscent;
    int titleWidth;
    
    final int MODE_GAP_WIDTH = 13;
    final int ARROW_GAP_WIDTH = 6;
    final int ARROW_WIDTH = 8;
    final int ARROW_TOP = 21;
    final int ARROW_BOTTOM = 29;

    int[] triangleX = new int[3];
    int[] triangleY = new int[] { ARROW_TOP, ARROW_TOP, ARROW_BOTTOM };
    

    @SuppressWarnings("deprecation")
    public ModeSelector() {
      title = mode.getTitle(); //.toUpperCase();
      titleFont = mode.getFont("mode.title.font");
      titleColor = mode.getColor("mode.title.color");
      
      // getGraphics() is null and no offscreen yet
      titleWidth = getToolkit().getFontMetrics(titleFont).stringWidth(title);
      
//      setOpaque(false);
    }
    
    @Override
    public void paintComponent(Graphics screen) {
//      Toolkit.debugOpacity(this);

      Dimension size = getSize();
      width = 0;
      if (width != size.width || height != size.height) {
        if (Toolkit.highResDisplay()) {
          offscreen = createImage(size.width*2, size.height*2);
        } else {
          offscreen = createImage(size.width, size.height);
        }
        width = size.width;
        height = size.height;
      }
      
      Graphics g = offscreen.getGraphics();
      /*Graphics2D g2 =*/ Toolkit.prepareGraphics(g);
      //Toolkit.clearGraphics(g, width, height);
//      g.clearRect(0, 0, width, height);
//      g.setColor(Color.GREEN);
//      g.fillRect(0, 0, width, height);
      
      g.setFont(titleFont);
      if (titleAscent == 0) {
        titleAscent = (int) Toolkit.getAscent(g); //metrics.getAscent();
      }
      FontMetrics metrics = g.getFontMetrics();
      titleWidth = metrics.stringWidth(title);
      
      g.drawImage(reverseGradient, 0, 0, width, height, this);
      
      g.setColor(titleColor);
      g.drawString(title, MODE_GAP_WIDTH, (height + titleAscent) / 2);
      
      int x = MODE_GAP_WIDTH + titleWidth + ARROW_GAP_WIDTH;
      triangleX[0] = x;
      triangleX[1] = x + ARROW_WIDTH;
      triangleX[2] = x + ARROW_WIDTH/2;
      g.fillPolygon(triangleX, triangleY, 3);
      
//      screen.clearRect(0, 0, width, height);
      screen.drawImage(offscreen, 0, 0, width, height, this);
//      screen.setColor(Color.RED);
//      screen.drawRect(0, 0, width-1, height-1);
    }
  
    @Override
    public Dimension getPreferredSize() {
      return new Dimension(MODE_GAP_WIDTH + titleWidth + 
                           ARROW_GAP_WIDTH + ARROW_WIDTH + MODE_GAP_WIDTH, 
                           EditorButton.DIM);
    }
    
    @Override
    public Dimension getMinimumSize() {
      return getPreferredSize();
    }
    
    @Override
    public Dimension getMaximumSize() {
      return getPreferredSize();
    }
  }
}


//public abstract class EditorToolbar extends JComponent implements MouseInputListener, KeyListener {
//
//  /** Width of each toolbar button. */
//  static final int BUTTON_WIDTH = 27;
//  /** The amount of space between groups of buttons on the toolbar. */
//  static final int BUTTON_GAP = 5;
//  /** Size (both width and height) of the buttons in the source image. */
//  static final int BUTTON_IMAGE_SIZE = 33;
//
//  static final int INACTIVE = 0;
//  static final int ROLLOVER = 1;
//  static final int ACTIVE   = 2;
//
//
//  Image offscreen;
//  int width, height;
//
//  Color bgColor;
//  boolean hiding;
//  Color hideColor;
//
//  protected Button rollover;
//
//  Font statusFont;
//  int statusAscent;
//  Color statusColor;
//  
//  boolean shiftPressed;
//
//  // what the mode indicator looks like
//  Color modeButtonColor;
//  Font modeTextFont;
//  int modeTextAscent;
//  Color modeTextColor;
//  String modeTitle;
//  int modeX1, modeY1;
//  int modeX2, modeY2;
//  JMenu modeMenu;
//  
//  protected ArrayList<Button> buttons;
//
//  static final int ARROW_WIDTH = 7;
//  static final int ARROW_HEIGHT = 6;
//  static Image modeArrow;
//
//  
//  public EditorToolbar(Editor editor, Base base) {  //, JMenu menu) {
//    this.editor = editor;
//    this.base = base;
////    this.menu = menu;
//
//    buttons = new ArrayList<Button>();
//    rollover = null;
//
//    mode = editor.getMode();
//    bgColor = mode.getColor("buttons.bgcolor");
//    statusFont = mode.getFont("buttons.status.font");
//    statusColor = mode.getColor("buttons.status.color");
////    modeTitle = mode.getTitle().toUpperCase();
//    modeTitle = mode.getTitle();
//    modeTextFont = mode.getFont("mode.button.font");
//    modeButtonColor = mode.getColor("mode.button.color");
//    
//    hiding = Preferences.getBoolean("buttons.hide.image");
//    hideColor = mode.getColor("buttons.hide.color");
//
//    if (modeArrow == null) {
//      String suffix = Toolkit.highResDisplay() ? "-2x.png" : ".png";
//      modeArrow = Toolkit.getLibImage("mode-arrow" + suffix);
//    }
//
//    addMouseListener(this);
//    addMouseMotionListener(this);
//  }
//
//
//  /** Load images and add toolbar buttons */
//  abstract public void init();
//
//
//  /**
//   * Load button images and slice them up. Only call this from paintComponent,  
//   * or when the comp is displayable, otherwise createImage() might fail.
//   * (Using BufferedImage instead of createImage() nowadays, so that may 
//   * no longer be relevant.) 
//   */
//  public Image[][] loadImages() {
//    int res = Toolkit.highResDisplay() ? 2 : 1;
//    
//    String suffix = null; 
//    Image allButtons = null;
//    // Some modes may not have a 2x version. If a mode doesn't have a 1x 
//    // version, this will cause an error... they should always have 1x.
//    if (res == 2) {
//      suffix = "-2x.png";
//      allButtons = mode.loadImage("theme/buttons" + suffix);
//      if (allButtons == null) {
//        res = 1;  // take him down a notch
//      }
//    }
//    if (res == 1) {
//      suffix = ".png";
//      allButtons = mode.loadImage("theme/buttons" + suffix);
//      if (allButtons == null) {
//        // use the old (pre-2.0b9) file name
//        suffix = ".gif";
//        allButtons = mode.loadImage("theme/buttons" + suffix);
//      }
//    }
//
//    int count = allButtons.getWidth(this) / BUTTON_WIDTH*res;
//    Image[][] buttonImages = new Image[count][3];
//    
//    for (int i = 0; i < count; i++) {
//      for (int state = 0; state < 3; state++) {
//        Image image = new BufferedImage(BUTTON_WIDTH*res, Preferences.GRID_SIZE*res, BufferedImage.TYPE_INT_ARGB);
//        Graphics g = image.getGraphics();
//        g.drawImage(allButtons, 
//                    -(i*BUTTON_IMAGE_SIZE*res) - 3, 
//                    (state-2)*BUTTON_IMAGE_SIZE*res, null);
//        g.dispose();
//        buttonImages[i][state] = image;
//      }
//    }
//    
//    return buttonImages;
//  }
//  
//  
////  abstract static public String getTitle(int index, boolean shift);
//
//
//  @Override
//  public void paintComponent(Graphics screen) {
//    if (buttons.size() == 0) {
//      init();
//    }
//
//    Dimension size = getSize();
//    if ((offscreen == null) ||
//        (size.width != width) || (size.height != height)) {
//      if (Toolkit.highResDisplay()) {
//        offscreen = createImage(size.width*2, size.height*2);
//      } else {
//        offscreen = createImage(size.width, size.height);
//      }
//
//      width = size.width;
//      height = size.height;
//
//      int offsetX = 3;
//      for (Button b : buttons) {
//        b.left = offsetX;
//        if (b.gap) {
//          b.left += BUTTON_GAP;
//        }
//        b.right = b.left + BUTTON_WIDTH; 
//        offsetX = b.right;
//      }
////      for (int i = 0; i < buttons.size(); i++) {
////        x1[i] = offsetX;
////        if (i == 2) x1[i] += BUTTON_GAP;
////        x2[i] = x1[i] + BUTTON_WIDTH;
////        offsetX = x2[i];
////      }
//    }
//    Graphics g = offscreen.getGraphics();
//    /*Graphics2D g2 =*/ Toolkit.prepareGraphics(g);
//
//    g.setColor(hiding ? hideColor : bgColor);
//    g.fillRect(0, 0, width, height);
////    if (backgroundImage != null) {
////      g.drawImage(backgroundImage, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, null);
////    }
////    if (!hiding) {
////      mode.drawBackground(g, 0);
////    }
//
////    for (int i = 0; i < buttonCount; i++) {
////      g.drawImage(stateImage[i], x1[i], y1, null);
////    }
//    for (Button b : buttons) {
//      g.drawImage(b.stateImage, b.left, 0, BUTTON_WIDTH, Preferences.GRID_SIZE, null);
//    }
//
//    g.setColor(statusColor);
//    g.setFont(statusFont);
//    if (statusAscent == 0) {
//      statusAscent = (int) Toolkit.getAscent(g);
//    }
//
//    // If I ever find the guy who wrote the Java2D API, I will hurt him.
////    Graphics2D g2 = (Graphics2D) g;
////    FontRenderContext frc = g2.getFontRenderContext();
////    float statusW = (float) statusFont.getStringBounds(status, frc).getWidth();
////    float statusX = (getSize().width - statusW) / 2;
////    g2.drawString(status, statusX, statusY);
//
////    if (currentRollover != -1) {
//    if (rollover != null) {
//      //int statusY = (BUTTON_HEIGHT + g.getFontMetrics().getAscent()) / 2;
//      int statusY = (Preferences.GRID_SIZE + statusAscent) / 2;
//      //String status = shiftPressed ? titleShift[currentRollover] : title[currentRollover];
//      String status = shiftPressed ? rollover.titleShift : rollover.title;
//      g.drawString(status, buttons.size() * BUTTON_WIDTH + 3 * BUTTON_GAP, statusY);
//    }
//
//    g.setFont(modeTextFont);
//    FontMetrics metrics = g.getFontMetrics();
//    if (modeTextAscent == 0) {
//      modeTextAscent = (int) Toolkit.getAscent(g); //metrics.getAscent();
//    }
//    int modeTextWidth = metrics.stringWidth(modeTitle);
//    final int modeGapWidth = 8;
//    final int modeBoxHeight = 20;
//    modeX2 = getWidth() - 16;
//    modeX1 = modeX2 - (modeGapWidth + modeTextWidth + modeGapWidth + ARROW_WIDTH + modeGapWidth);
////    modeY1 = 8; //(getHeight() - modeBoxHeight) / 2;
//    modeY1 = (getHeight() - modeBoxHeight) / 2;
//    modeY2 = modeY1 + modeBoxHeight; //modeY1 + modeH + modeGapV*2;
//    g.setColor(modeButtonColor);
//    g.drawRect(modeX1, modeY1, modeX2 - modeX1, modeY2 - modeY1 - 1);
//    
//    g.drawString(modeTitle, 
//                 modeX1 + modeGapWidth, 
//                 modeY1 + (modeBoxHeight + modeTextAscent) / 2);
//                 //modeY1 + modeTextAscent + (modeBoxHeight - modeTextAscent) / 2);
//    g.drawImage(modeArrow, 
//                modeX2 - ARROW_WIDTH - modeGapWidth, 
//                modeY1 + (modeBoxHeight - ARROW_HEIGHT) / 2, 
//                ARROW_WIDTH, ARROW_HEIGHT, null);
//
////    g.drawLine(modeX1, modeY2, modeX2, modeY2);
////    g.drawLine(0, size.height, size.width, size.height);
////    g.fillRect(modeX1 - modeGapWidth*2,  modeY1, modeGapWidth, modeBoxHeight);
//    
//    screen.drawImage(offscreen, 0, 0, size.width, size.height, null);
//
//    // dim things out when not enabled (not currently in use) 
////    if (!isEnabled()) {
////      screen.setColor(new Color(0, 0, 0, 100));
////      screen.fillRect(0, 0, getWidth(), getHeight());
////    }
//  }
//
//  
//  protected void checkRollover(int x, int y) {
//    Button over = findSelection(x, y);
//    if (over != null) {
//      //        if (state[sel] != ACTIVE) {
//      if (over.state != ACTIVE) {
//        //          setState(sel, ROLLOVER, true);
//        over.setState(ROLLOVER, true);
//        //          currentRollover = sel;
//        rollover = over;
//      }
//    }
//  }
//
//
//  public void mouseMoved(MouseEvent e) {
//    if (!isEnabled()) return;
//
//    // ignore mouse events before the first paintComponent() call
//    if (offscreen == null) return;
//
//    // TODO this isn't quite right, since it's gonna kill rollovers too
////    if (state[OPEN] != INACTIVE) {
////      // avoid flicker, since there should be another update event soon
////      setState(OPEN, INACTIVE, false);
////    }
//
//    int x = e.getX();
//    int y = e.getY();
//
//    if (rollover != null) {
//      //if (y > TOP && y < BOTTOM && x > rollover.left && x < rollover.right) {
//      if (y > 0 && y < getHeight() && x > rollover.left && x < rollover.right) {
//        // nothing has changed
//        return;
//
//      } else {
//        if (rollover.state == ROLLOVER) {
//          rollover.setState(INACTIVE, true);
//        }
//        rollover = null;
//      }
//    }
//    checkRollover(x, y);
//  }
//
//
//  public void mouseDragged(MouseEvent e) { }
//
//
////  public void handleMouse(MouseEvent e) {
////    int x = e.getX();
////    int y = e.getY();
////
//////    if (currentRollover != -1) {
////    if (rollover != null) {
//////      if ((x > x1[currentRollover]) && (y > y1) &&
//////          (x < x2[currentRollover]) && (y < y2)) {
////      if (y > y1 && y < y2 && x > rollover.left && x < rollover.right) {
////        // nothing has changed
////        return;
////
////      } else {
//////        setState(currentRollover, INACTIVE, true);
////        rollover.setState(INACTIVE, true);
//////        currentRollover = -1;
////        rollover = null;
////      }
////    }
//////    int sel = findSelection(x, y);
////    Button over = findSelection(x, y);
////    if (over != null) {
//////      if (state[sel] != ACTIVE) {
////      if (over.state != ACTIVE) {
//////        setState(sel, ROLLOVER, true);
////        over.setState(ROLLOVER, true);
//////        currentRollover = sel;
////        rollover = over;
////      }
////    }
////  }
//
//
////  private int findSelection(int x, int y) {
////    // if app loads slowly and cursor is near the buttons
////    // when it comes up, the app may not have time to load
////    if ((x1 == null) || (x2 == null)) return -1;
////
////    for (int i = 0; i < buttonCount; i++) {
////      if ((y > y1) && (x > x1[i]) &&
////          (y < y2) && (x < x2[i])) {
////        //System.out.println("sel is " + i);
////        return i;
////      }
////    }
////    return -1;
////  }
//
//  
//  private Button findSelection(int x, int y) {
//    // if app loads slowly and cursor is near the buttons
//    // when it comes up, the app may not have time to load
//    if (offscreen != null && y > 0 && y < getHeight()) {
//      for (Button b : buttons) {
//        if (x > b.left && x < b.right) {
//          return b;
//        }
//      }
//    }
//    return null;
//  }
//
//
////  private void setState(int slot, int newState, boolean updateAfter) {
////    if (buttonImages != null) {
////      state[slot] = newState;
////      stateImage[slot] = buttonImages[which[slot]][newState];
////      if (updateAfter) {
////        repaint();
////      }
////    }
////  }
//
//
//  public void mouseEntered(MouseEvent e) {
////    handleMouse(e);
//  }
//
//
//  public void mouseExited(MouseEvent e) {
////    // if the 'open' popup menu is visible, don't register this,
////    // because the popup being set visible will fire a mouseExited() event
////    if ((popup != null) && popup.isVisible()) return;
//    // this might be better
//    if (e.getComponent() != this) {
//      return;
//    }
//
//    // TODO another weird one.. come back to this
////    if (state[OPEN] != INACTIVE) {
////      setState(OPEN, INACTIVE, true);
////    }
////    handleMouse(e);
//    
//    // there is no more rollover, make sure that the rollover text goes away
////    currentRollover = -1;
//    if (rollover != null) {
//      if (rollover.state == ROLLOVER) {
//        rollover.setState(INACTIVE, true);
//      }
//      rollover = null;
//    }
//  }
//
////  int wasDown = -1;
//
//
//  public void mousePressed(MouseEvent e) {
//    // ignore mouse presses so hitting 'run' twice doesn't cause problems
//    if (isEnabled()) {
//      int x = e.getX();
//      int y = e.getY();
//      if (x > modeX1 && x < modeX2 && y > modeY1 && y < modeY2) {
//        JPopupMenu popup = editor.getModeMenu().getPopupMenu();
//        popup.show(this, x, y);
//      }
//      
//      // Need to reset the rollover here. If the window isn't active, 
//      // the rollover wouldn't have been updated.
//      // http://code.google.com/p/processing/issues/detail?id=561
//      checkRollover(x, y);
//      if (rollover != null) {
//        //handlePressed(rollover);
//        handlePressed(e, buttons.indexOf(rollover));
//      }
//    }
//  }
//  
//  
//  public void mouseClicked(MouseEvent e) { }
//
//
//  public void mouseReleased(MouseEvent e) { }
//
//
////  public void handlePressed(Button b) {
////    handlePressed(buttons.indexOf(b));
////  }
//  
//  
//  abstract public void handlePressed(MouseEvent e, int index);
//  
//  
//  /**
//   * Set a particular button to be active.
//   */
//  public void activate(int what) {
////    setState(what, ACTIVE, true);
//    buttons.get(what).setState(ACTIVE, true);
//  }
//
//
//  /**
//   * Set a particular button to be active.
//   */
//  public void deactivate(int what) {
////    setState(what, INACTIVE, true);
//    buttons.get(what).setState(INACTIVE, true);
//  }
//
//
//  public Dimension getPreferredSize() {
//    return getMinimumSize();
//  }
//
//
//  public Dimension getMinimumSize() {
//    return new Dimension((buttons.size() + 1)*BUTTON_WIDTH, Preferences.GRID_SIZE);
//  }
//
//
//  public Dimension getMaximumSize() {
//    return new Dimension(3000, Preferences.GRID_SIZE);
//  }
//
//
//  public void keyPressed(KeyEvent e) {
//    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
//      shiftPressed = true;
//      repaint();
//    }
//  }
//
//
//  public void keyReleased(KeyEvent e) {
//    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
//      shiftPressed = false;
//      repaint();
//    }
//  }
//
//
//  public void keyTyped(KeyEvent e) { }
//
//  
//  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
//
//  
//  public void addButton(String title, String shiftTitle, Image[] images, boolean gap) {
//    Button b = new Button(title, shiftTitle, images, gap);
//    buttons.add(b);
//  }
//  
//
//  public class Button {
//    /** Button's description. */ 
//    String title;
//    /** Description of alternate behavior when shift is down. */ 
//    String titleShift;
//    /** Three state images. */
//    Image[] images;
//    /** Current state value, one of ACTIVE, INACTIVE, ROLLOVER. */
//    int state;
//    /** Current state image. */
//    Image stateImage;
//    /** Left and right coordinates. */
//    int left, right;
//    /** Whether there's a gap before this button. */
//    boolean gap;
//    
////    JPopupMenu popup;
////    JMenu menu;
//
//
//    public Button(String title, String titleShift, Image[] images, boolean gap) {
//      this.title = title;
//      this.titleShift = titleShift;
//      this.images = images;
//      this.gap = gap;
//      
//      state = INACTIVE;
//      stateImage = images[INACTIVE];
//    }
//    
//    
////    public void setMenu(JMenu menu) {
////      this.menu = menu;
////    }
//
//    
//    public void setState(int newState, boolean updateAfter) {
//      state = newState;
//      stateImage = images[newState];
//      if (updateAfter) {
//        repaint();
//      }
//    }
//  }
//}
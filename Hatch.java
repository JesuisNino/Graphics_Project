import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class Hatch extends JFrame implements ActionListener {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private Hatch_GLEventListener glEventListener;
  private final FPSAnimator animator; 
  private Camera camera;

  public static void main(String[] args) {
    Hatch b1 = new Hatch("Hatch");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  public Hatch(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Hatch_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);
    
    JMenuBar menuBar=new JMenuBar();
    this.setJMenuBar(menuBar);
      JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
    menuBar.add(fileMenu);

    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
      JButton b = new JButton("camera X");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("camera Z");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Left00");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Left01");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Left02");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Right00");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Right01");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Right02");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Turn on 01");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Turn off 01");
      b.addActionListener(this);
      p.add(b);
    b = new JButton("Turn on 02");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Turn off 02");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Turn spotLight off 01");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Turn spotLight on 01");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Turn spotLight off 02");
    b.addActionListener(this);
    p.add(b);
    b = new JButton("Turn spotLight on 02");
    b.addActionListener(this);
    p.add(b);
    this.add(p, BorderLayout.WEST);
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }
  
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase("camera X")) {
      camera.setCamera(Camera.CameraType.X);
      canvas.requestFocusInWindow();
    }
    else if (e.getActionCommand().equalsIgnoreCase("camera Z")) {
      camera.setCamera(Camera.CameraType.Z);
      canvas.requestFocusInWindow();
    }
    else if (e.getActionCommand().equalsIgnoreCase("Left00")) {
      glEventListener.lampState(0,0);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Left01")) {
      glEventListener.lampState(1,0);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Left02")) {
      glEventListener.lampState(2,0);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Right00")) {
      glEventListener.lampState(0,1);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Right01")) {
      glEventListener.lampState(1,1);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Right02")) {
      glEventListener.lampState(2,1);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Turn on 01")) {
      glEventListener.setLight(0,true);
    }
    else if (e.getActionCommand().equalsIgnoreCase("Turn off 01")) {
      glEventListener.setLight(0,false);
    }else if (e.getActionCommand().equalsIgnoreCase("Turn on 02")) {
      glEventListener.setLight(1,true);
    }else if (e.getActionCommand().equalsIgnoreCase("Turn off 02")) {
      glEventListener.setLight(1,false);
    }else if (e.getActionCommand().equalsIgnoreCase("Turn spotLight on 01")) {
      glEventListener.setLight(2,true);
    }else if (e.getActionCommand().equalsIgnoreCase("Turn spotLight off 01")) {
      glEventListener.setLight(2,false);
    }else if (e.getActionCommand().equalsIgnoreCase("Turn spotLight on 02")) {
      glEventListener.setLight(3,true);
    }else if (e.getActionCommand().equalsIgnoreCase("Turn spotLight off 02")) {
      glEventListener.setLight(3,false);
    }
    else if(e.getActionCommand().equalsIgnoreCase("quit"))
      System.exit(0);
  }
  
}
 
class MyKeyboardInput extends KeyAdapter  {
  private Camera camera;
  
  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }
  
  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;
  
  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }
  
    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiers()==MouseEvent.BUTTON1_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }
}
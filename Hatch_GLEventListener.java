import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import sun.tools.jconsole.Tab;

public class Hatch_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Hatch_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
  private boolean animation = false;
  private double savedTime = 0;

  private int currentState = 0;
  private int state,lampNum, currentLampNum;
  private int[] lowerAngleZL = {45, -45, -30};
  private int[] lowerAngleYL = {0, 60, -90};
  private int[] upperAngleL = {-100, -20, 75};
  private int[] headAngleL = {75, 70, -40};

  private int[] lowerAngleZR = {-45, 60, 10};
  private int[] lowerAngleYR = {0, -70, 90};
  private int[] upperAngleR = {100, -10, -30};
  private int[] headAngleR = {-75, -50, 20};

  public void lampState(int n, int lampNumber) {
    animation = true;
    lamp.setState(n);
    state = n;
    lamp.setLampNum(lampNumber);
    lampNum = lampNumber;
    startTime = getSeconds() - savedTime;
  }

  public void setLight(int lightNum, int i){
    if (i == 0){
      light.material.setAmbient(0f, 0f, 0f);
      light.material.setDiffuse(0f, 0f, 0f);
      light.material.setSpecular(0f, 0f, 0f);
    }else{
      light.material.setAmbient(1f, 1f, 1f);
      light.material.setDiffuse(1f, 1f, 1f);
      light.material.setSpecular(1f, 1f, 1f);
    }
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Room room;
  private Light light, light_general01, light_general02;
  private Egg egg;
  private Table table;
  private Lamp lamp;

  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/egg.jpeg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/egg_specular.jpeg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureSky1 = TextureLibrary.loadTexture(gl, "textures/cloud.jpg");
    int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/woodenFloor.jpg");
    int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/snake_body.jpg");
    int[] textureId9 = TextureLibrary.loadTexture(gl, "textures/base.jpg");
    int[] textureId10 = TextureLibrary.loadTexture(gl, "textures/surface_specular.jpg");
    int[] textureId11 = TextureLibrary.loadTexture(gl, "textures/eyes.jpeg");
    int[] textureId12 = TextureLibrary.loadTexture(gl, "textures/snake_body2.jpeg");
    int[] textureId13 = TextureLibrary.loadTexture(gl, "textures/snake_hair.jpeg");
    int[] textureId14 = TextureLibrary.loadTexture(gl, "textures/snake_headL.jpeg");
    int[] textureId15 = TextureLibrary.loadTexture(gl, "textures/snake_headR.jpeg");
        
    light = new Light(gl,2);
    light.setCamera(camera);

    light_general01 = new Light(gl,1);
    light_general01.setCamera(camera);

    light_general02 = new Light(gl,1);
    light_general02.setCamera(camera);

    // Initialise the room, table, egg and lamp
    room = new Room(gl, camera, light, textureId7, textureId0,textureSky1);
    table = new Table(gl, camera, light, textureId3, textureId4, textureId9, textureId10);
    egg = new Egg(gl, camera, light, textureId1, textureId2);
    lamp = new Lamp(gl, camera, light, textureId10, textureId8, textureId12,
                    textureId11, textureId13, textureId14, textureId15, textureId9);

  }


  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    light_general01.setPosition(8, 11 ,-8);
    light_general01.render(gl);
    light_general02.setPosition(-8, 11 ,8);
    light_general02.render(gl);
    room.render(gl);
    moveCloud(gl);
    table.render(gl);
    egg.render(gl);
    updateEgg();
    if(animation && (lampNum != lamp.getCurrentLampNum() || lamp.getCurrentState() != state)) {
      if (lampNum == 0) moveLamp(0, lowerAngleYL[state] - lowerAngleYL[lamp.getCurrentState()], lowerAngleZL[state] - lowerAngleZL[lamp.getCurrentState()], upperAngleL[state] - upperAngleL[lamp.getCurrentState()], headAngleL[state] - headAngleL[lamp.getCurrentState()]);
      if (lampNum == 1) moveLamp(1, lowerAngleYR[state] - lowerAngleYR[lamp.getCurrentState()], lowerAngleZR[state] - lowerAngleZR[lamp.getCurrentState()], upperAngleR[state] - upperAngleR[lamp.getCurrentState()], headAngleR[state] - headAngleR[lamp.getCurrentState()]);
    }
    lamp.render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    room.dispose(gl);
    table.dispose(gl);
    lamp.dispose(gl);
  }

  private void updateEgg() {
    double elapsedTime = getSeconds()-startTime;
    egg.updateEgg(elapsedTime);
  }

  private void moveLamp(int pose, float lowerAngleY, float lowerAngleZ, float upperAngle, float headAngle){
    double elapsedTime = getSeconds()-startTime;
    lamp.moveLamp(pose, lowerAngleY,lowerAngleZ,upperAngle,headAngle,elapsedTime);
  }

  private void moveCloud(GL3 gl) {
    double elapsedTime = getSeconds()-startTime;
    room.moveCloud(gl,elapsedTime);
  }
  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);
  }

  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
}
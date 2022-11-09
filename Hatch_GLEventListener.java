import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
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

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    backWall.dispose(gl);
    leftWall.dispose(gl);
    rightWall.dispose(gl);
    table.dispose(gl);
    tableLeg1.dispose(gl);
    tableLeg2.dispose(gl);
    tableLeg3.dispose(gl);
    tableLeg4.dispose(gl);
    base.dispose(gl);
    sphere.dispose(gl);
    cube.dispose(gl);
    cube2.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
  private boolean animation = false;
  private double savedTime = 0;
   
  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }
   
  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
  }
   
  public void incXPosition() {
    xPosition += 0.5f;
    if (xPosition>5f) xPosition = 5f;
    updateMove();
  }
   
  public void decXPosition() {
    xPosition -= 0.5f;
    if (xPosition<-5f) xPosition = -5f;
    updateMove();
  }
 
  private void updateMove() {
    lampMoveTranslate.setTransform(Mat4Transform.translate(xPosition,0,0));
    lampMoveTranslate.update();
  }

  private int currentState=2;
  private int state;
  private int[] lowerAngleZL = {45, -45, -30};
  private int[] lowerAngleYL = {0, 60, -90};
  private int[] upperAngleL = {-100, -20, 75};
  private int[] headAngleL = {75, 70, -40};
  public void lampState(int n) {
    animation = true;
    state = n;
    startTime = getSeconds()-savedTime;
  }

  private void moveLamp(float lowerAngleY, float lowerAngleZ, float upperAngle, float headAngle){
    double elapsedTime = getSeconds()-startTime;
    float rotateAngleUpper = upperAngleL[currentState]+upperAngle*(float)Math.sin(elapsedTime);
    float rotateAngleHead = headAngleL[currentState]+headAngle*(float)Math.sin(elapsedTime);
    float rotateAngleLowerZ = lowerAngleZL[currentState]+lowerAngleZ*(float)Math.sin(elapsedTime);
    float rotateAngleLowerY = lowerAngleYL[currentState]+lowerAngleY*(float)Math.sin(elapsedTime);
    if ((float)Math.sin(elapsedTime) < 0.99) {
      lowerBranchRotateZ.setTransform(Mat4Transform.rotateAroundZ(rotateAngleLowerZ));
      lowerBranchRotateY.setTransform(Mat4Transform.rotateAroundY(rotateAngleLowerY));
      upperBranchRotate.setTransform(Mat4Transform.rotateAroundZ(rotateAngleUpper));
      headRotate.setTransform(Mat4Transform.rotateAroundZ(rotateAngleHead));
      lampRootL.update();
    }else{
      animation = false;
      currentState = state;
    }
    }

   
  public void raisedBranchs() {
    stopAnimation();
    Hatch_GLEventListener.this.lowerBranchRotateZ.setTransform(Mat4Transform.rotateAroundX(0));
    Hatch_GLEventListener.this.lowerBranchRotateZ.update();
    upperBranchRotate.setTransform(Mat4Transform.rotateAroundX(0));
    upperBranchRotate.update();
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Model floor, backWall, leftWall, rightWall, sphere, base, table, tableLeg1, tableLeg2, tableLeg3, tableLeg4, cube, cube2;
  private Light light;
  private SGNode eggRoot, lampRootL, lampRootR;
  
  private float xPosition = 0;
  private TransformNode jumpY, translateX, rotateAllY, rotateAllZ;
  private TransformNode lowerBranchRotateZ, lowerBranchRotateY;
  private TransformNode upperBranchRotate, headRotate;
  private TransformNode lampMoveTranslate;
  private float rotateAllAngleStart = 5, rotateAllAngleY = rotateAllAngleStart ,rotateAllAngleZ = rotateAllAngleStart;
  private float jumpYStart = 0, jumpYHeight = jumpYStart;
  private float jumpHeght = 0.3f;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");
    int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/woodenFloor.jpg");
        
    light = new Light(gl);
    light.setCamera(camera);
    
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
    floor = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId7);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(16,1f,10);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,5f,-8f), modelMatrix);
    backWall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId0);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(16,1f,10);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-8f,5f,0), modelMatrix);
    leftWall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId0);

    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(16,1f,10);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(8f,5f,0), modelMatrix);
    rightWall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId0);
    
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);


    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    cube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);
    
    cube2 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId5, textureId6); 

    // table
    float wholeHeight = 5.5f;
    float tableHeight = 0.5f;
    float tableWidth = 4f;
    float tableDepth = 4f;

    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,wholeHeight/2,0.5f), Mat4Transform.translate(tableWidth-0.5f,0.5f,tableWidth-0.5f));
    tableLeg1 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);

    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,wholeHeight/2,0.5f), Mat4Transform.translate(tableWidth-0.5f,0.5f,-tableWidth+0.5f));
    tableLeg2 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);

    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,wholeHeight/2,0.5f), Mat4Transform.translate(-tableWidth+0.5f,0.5f,tableWidth-0.5f));
    tableLeg3 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);

    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,wholeHeight/2,0.5f), Mat4Transform.translate(-tableWidth+0.5f,0.5f,-tableWidth+0.5f));
    tableLeg4 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);

    wholeHeight = wholeHeight + 0.5f;
    modelMatrix = Mat4.multiply(Mat4Transform.scale(tableWidth,tableHeight,tableDepth), Mat4Transform.translate(0,wholeHeight,0));
    table = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);

    wholeHeight = (wholeHeight + tableHeight)*2 + 0.5f;
    modelMatrix = Mat4.multiply(Mat4Transform.scale(1.5f,tableHeight/2,1.5f), Mat4Transform.translate(0, wholeHeight,0));
    base = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);

    // egg

    eggRoot = new NameNode("egg");
    jumpY = new TransformNode("jump", Mat4Transform.translate(0,jumpYHeight,0));
    translateX = new TransformNode("translate", Mat4Transform.translate(xPosition,0,0));
    rotateAllY = new TransformNode("rotateAroundZ", Mat4Transform.rotateAroundX(0));
    rotateAllZ = new TransformNode("rotateAroundZ", Mat4Transform.rotateAroundZ(0));

    NameNode egg = new NameNode("egg");
    Mat4 m = Mat4Transform.scale(2,2.5f,2);
    m = Mat4.multiply(m, Mat4Transform.translate(0, 1.9f,0));
    TransformNode makeEgg = new TransformNode("scale(2,2.5f,2);translate(0, 1.9f,0)", m);
    ModelNode eggNode = new ModelNode("Sphere(egg)", sphere);

    eggRoot.addChild(translateX);
      translateX.addChild(jumpY);
        jumpY.addChild(rotateAllY);
         rotateAllY.addChild(rotateAllZ);
          rotateAllZ.addChild(egg);
            egg.addChild(makeEgg);
              makeEgg.addChild(eggNode);

    eggRoot.update();

    // Left Lamp
    
    float jointScale = 0.5f;
    float branchLength = 2.5f;
    float branchScale = 0.3f;
    float baseHeight = 0.5f;
    float baseScale = 1.5f;
    float headLength = 1f;
    float headScale = 0.5f;
    float stickLength = 1.5f;
    float stickScale = 0.1f;
    
    lampRootL = new NameNode("root");
    lampMoveTranslate = new TransformNode("lamp transform",Mat4Transform.translate(xPosition - 5,0,0));

    TransformNode lampTranslate = new TransformNode("lamp transform",Mat4Transform.translate(0,0,0));

    NameNode joint = new NameNode("joint");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(jointScale, jointScale, jointScale));
      m = Mat4.multiply(m, Mat4Transform.translate(0,(branchLength)*2,0));
      TransformNode jointTransform = new TransformNode("joint transform", m);
        ModelNode jointShape = new ModelNode("Sphere(joint)", sphere);

    NameNode lampBase = new NameNode("lamp base");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(baseScale,baseHeight,baseScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lampBaseTransform = new TransformNode("lamp base transform", m);
    ModelNode lampBaseShape = new ModelNode("Cube(lamp base)", cube);

    TransformNode translateToTop01 = new TransformNode("translate base and lower branch",Mat4Transform.translate(0f,0.5f,0f));
    lowerBranchRotateY = new TransformNode("rotate lower branch Y", Mat4Transform.rotateAroundY(lowerAngleYL[currentState]));
    lowerBranchRotateZ = new TransformNode("rotate lower branch Z", Mat4Transform.rotateAroundZ(lowerAngleZL[currentState]));

    NameNode lowerBranch = new NameNode("lower branch");
      m = Mat4Transform.scale(branchScale, branchLength, branchScale);
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode makeLowerBranch = new TransformNode("lower branch transform", m);
      ModelNode cube0Node = new ModelNode("Sphere(0)", sphere);

    upperBranchRotate = new TransformNode("rotate upper branch",Mat4Transform.rotateAroundZ(upperAngleL[currentState]));
    TransformNode translateToTop02 = new TransformNode("translate two branches",Mat4Transform.translate(0,2.5f,0));
    NameNode upperBranch = new NameNode("upper branch");
      m = Mat4Transform.scale(branchScale, branchLength, branchScale);
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode makeUpperBranch = new TransformNode("upper branch transform", m);
      ModelNode cube1Node = new ModelNode("Sphere(1)", sphere);

    TransformNode translateToTop03 = new TransformNode("translate",Mat4Transform.translate(0,branchLength,0));
    headRotate = new TransformNode("rotate lamp head",Mat4Transform.rotateAroundZ(headAngleL[currentState]));

    NameNode lampHead = new NameNode("lamp head");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(headLength,headScale,headScale));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
      TransformNode lampHeadTransform = new TransformNode("lamp head transform", m);
      ModelNode lampHeadShape = new ModelNode("Cube(lamp head)", cube);
//
//    NameNode stick = new NameNode("stick");
//    m = new Mat4(1);
//    m = Mat4.multiply(m, Mat4Transform.scale(stickLength,stickScale,stickScale));
//    m = Mat4.multiply(m, Mat4Transform.translate(-jointScale,32,0));
//    TransformNode stickTransform = new TransformNode("stick transform", m);
//    ModelNode stickShape = new ModelNode("Cube(stick)", cube);

    lampRootL.addChild(lampMoveTranslate);
      lampMoveTranslate.addChild(lampTranslate);
        lampTranslate.addChild(lampBase);
        lampBase.addChild(lampBaseTransform);
        lampBaseTransform.addChild(lampBaseShape);
        lampBase.addChild(translateToTop01);
          translateToTop01.addChild(lowerBranchRotateY);
          lowerBranchRotateY.addChild(lowerBranchRotateZ);
            lowerBranchRotateZ.addChild(lowerBranch);
              lowerBranch.addChild(makeLowerBranch);
                makeLowerBranch.addChild(cube0Node);
              lowerBranch.addChild(joint);
                joint.addChild(jointTransform);
                  jointTransform.addChild(jointShape);
              lowerBranch.addChild(translateToTop02);
                translateToTop02.addChild(upperBranchRotate);
                  upperBranchRotate.addChild(upperBranch);
                    upperBranch.addChild(makeUpperBranch);
                      makeUpperBranch.addChild(cube1Node);
                  upperBranch.addChild(translateToTop03);
                    translateToTop03.addChild(headRotate);
                      headRotate.addChild(lampHead);
                        lampHead.addChild(lampHeadTransform);
                        lampHeadTransform.addChild(lampHeadShape);

    lampRootL.update();  // IMPORTANT - don't forget this
    //lampRootL.print(0, false);
    //System.exit(0);
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    floor.render(gl);
    backWall.render(gl);
    leftWall.render(gl);
    rightWall.render(gl);
    base.render(gl);
    table.render(gl);
    tableLeg1.render(gl);
    tableLeg2.render(gl);
    tableLeg3.render(gl);
    tableLeg4.render(gl);
    updateEgg();
    if (animation && currentState!= state) moveLamp(lowerAngleYL[state]-lowerAngleYL[currentState], lowerAngleZL[state]-lowerAngleZL[currentState], upperAngleL[state]-upperAngleL[currentState],headAngleL[state]-headAngleL[currentState]);
    lampRootL.draw(gl);
    eggRoot.draw(gl);

  }

  private void updateEgg() {
    double elapsedTime = getSeconds()-startTime;
    rotateAllAngleY = rotateAllAngleStart * (float) Math.sin(elapsedTime * 6);
    rotateAllAngleZ = rotateAllAngleStart * (float) Math.cos(elapsedTime * 4);
    rotateAllY.setTransform(Mat4Transform.rotateAroundX(rotateAllAngleY));
    rotateAllZ.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngleZ));
    jumpYHeight = jumpYStart + jumpHeght + (float) Math.sin(elapsedTime * 12) * jumpHeght;
    jumpY.setTransform(Mat4Transform.translate(0, jumpYHeight, 0));
    eggRoot.update(); // IMPORTANT – the scene graph has changed
  }

  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
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
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

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    room.dispose(gl);
    table.dispose(gl);
    sphere.dispose(gl);
    cube.dispose(gl);
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

  public void lampState(int n, int lamp) {
    animation = true;
    state = n;
    lampNum = lamp;
    startTime = getSeconds() - savedTime;
  }

  private void moveLamp(int lamp, float lowerAngleY, float lowerAngleZ, float upperAngle, float headAngle){
    double elapsedTime = getSeconds()-startTime;
    float rotateAngleUpper, rotateAngleHead,rotateAngleLowerZ,rotateAngleLowerY;

    if (lamp==0 && (float)Math.sin(elapsedTime) < 0.99) {
      currentLampNum = 0;
      rotateAngleUpper = upperAngleL[currentState] + upperAngle * (float) Math.sin(elapsedTime);
      rotateAngleHead = headAngleL[currentState] + headAngle * (float) Math.sin(elapsedTime);
      rotateAngleLowerZ = lowerAngleZL[currentState] + lowerAngleZ * (float) Math.sin(elapsedTime);
      rotateAngleLowerY = lowerAngleYL[currentState] + lowerAngleY * (float) Math.sin(elapsedTime);
      lowerBranchRotateZL.setTransform(Mat4Transform.rotateAroundZ(rotateAngleLowerZ));
      lowerBranchRotateYL.setTransform(Mat4Transform.rotateAroundY(rotateAngleLowerY));
      upperBranchRotateL.setTransform(Mat4Transform.rotateAroundZ(rotateAngleUpper));
      headRotateL.setTransform(Mat4Transform.rotateAroundZ(rotateAngleHead));
      lampRootL.update();
    }else if (lamp==1 && (float)Math.sin(elapsedTime) < 0.99) {
      currentLampNum = 1;
      rotateAngleUpper = upperAngleR[currentState] + upperAngle * (float) Math.sin(elapsedTime);
      rotateAngleHead = headAngleR[currentState] + headAngle * (float) Math.sin(elapsedTime);
      rotateAngleLowerZ = lowerAngleZR[currentState] + lowerAngleZ * (float) Math.sin(elapsedTime);
      rotateAngleLowerY = lowerAngleYR[currentState] + lowerAngleY * (float) Math.sin(elapsedTime);
      lowerBranchRotateZR.setTransform(Mat4Transform.rotateAroundZ(rotateAngleLowerZ));
      lowerBranchRotateYR.setTransform(Mat4Transform.rotateAroundY(rotateAngleLowerY));
      upperBranchRotateR.setTransform(Mat4Transform.rotateAroundZ(rotateAngleUpper));
      headRotateR.setTransform(Mat4Transform.rotateAroundZ(rotateAngleHead));
      lampRootR.update();
    }else {
      currentState = state;
      animation = false;
      }

    }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Model sphere, cube, cube2;
  private Room room;
  private Light light;
  private Egg egg;
  private Table table;
  private SGNode lampRootL, lampRootR;
  
  private float xPosition = 0;
  private TransformNode lowerBranchRotateZL, lowerBranchRotateYL, upperBranchRotateL, headRotateL;
  private TransformNode lowerBranchRotateZR, lowerBranchRotateYR, upperBranchRotateR, headRotateR;
  private TransformNode lampMoveTranslate;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    textureSky1 = TextureLibrary.loadTexture(gl, "textures/cloud2.jpg");
    textureSky2 = TextureLibrary.loadTexture(gl, "textures/cloud2_specular.jpg");
    int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/woodenFloor.jpg");
    int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/snake_body.jpg");
    int[] textureId9 = TextureLibrary.loadTexture(gl, "textures/base.jpg");
        
    light = new Light(gl);
    light.setCamera(camera);

    // Initialise the room, table and egg
    room = new Room(gl, camera, light, textureId7, textureId0);
    table = new Table(gl, camera, light, textureId3, textureId4);
    egg = new Egg(gl, camera, light, textureId1, textureId2);

    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.5f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 50.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);
    Model snakeBody = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId8);
    Model eye = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId9);

    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.5f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 50.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    cube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);
    Model snakeHead = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId8);
    Model baseCube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId9);


    // Left Lamp

    float jointScale = 0.5f;
    float branchLength = 2.5f;
    float branchScale = 0.3f;
    float baseHeight = 0.5f;
    float baseScale = 1.5f;
    float headLength = 1f;
    float headScale = 0.5f;
    float stickLength = 1.7f;
    float stickScale = 0.15f;

    lampRootL = new NameNode("root");
    lampMoveTranslate = new TransformNode("lamp transform",Mat4Transform.translate(xPosition - 5,0,0));

    TransformNode lampTranslateL = new TransformNode("lamp transform",Mat4Transform.translate(0,0,0));

    NameNode jointL = new NameNode("joint");
      Mat4 m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(jointScale, jointScale, jointScale));
      m = Mat4.multiply(m, Mat4Transform.translate(0,(branchLength)*2,0));
      TransformNode jointTransformL = new TransformNode("joint transform", m);
        ModelNode jointShapeL = new ModelNode("Sphere(joint)", snakeBody);

    NameNode lampBaseL = new NameNode("lamp base");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(baseScale,baseHeight,baseScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lampBaseTransformL = new TransformNode("lamp base transform", m);
    ModelNode lampBaseShapeL = new ModelNode("Cube(lamp base)", baseCube);

    TransformNode translateToTop01L = new TransformNode("translate base and lower branch",Mat4Transform.translate(0f,0.5f,0f));
    lowerBranchRotateYL = new TransformNode("rotate lower branch Y", Mat4Transform.rotateAroundY(lowerAngleYL[0]));
    lowerBranchRotateZL = new TransformNode("rotate lower branch Z", Mat4Transform.rotateAroundZ(lowerAngleZL[0]));

    NameNode lowerBranchL = new NameNode("lower branch");
      m = Mat4Transform.scale(branchScale, branchLength, branchScale);
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode makeLowerBranchL = new TransformNode("lower branch transform", m);
      ModelNode lowerBranchShapeL = new ModelNode("Sphere(0)", snakeBody);

    upperBranchRotateL = new TransformNode("rotate upper branch",Mat4Transform.rotateAroundZ(upperAngleL[0]));
    TransformNode translateToTop02L = new TransformNode("translate two branches",Mat4Transform.translate(0,2.5f,0));
    NameNode upperBranchL = new NameNode("upper branch");
      m = Mat4Transform.scale(branchScale, branchLength, branchScale);
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode makeUpperBranchL = new TransformNode("upper branch transform", m);
      ModelNode upperBranchShapeL = new ModelNode("Sphere(1)", snakeBody);

    TransformNode translateToTop03L = new TransformNode("translate",Mat4Transform.translate(0,branchLength,0));
    headRotateL = new TransformNode("rotate lamp head",Mat4Transform.rotateAroundZ(headAngleL[0]));
    NameNode lampHeadL = new NameNode("lamp head");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(headLength,headScale,headScale));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
      TransformNode lampHeadTransformL = new TransformNode("lamp head transform", m);
      ModelNode lampHeadShapeL = new ModelNode("Cube(lamp head)", snakeHead);

    TransformNode translateToTop04L = new TransformNode("translate",Mat4Transform.translate(-0.7f,1.8f,0));
    TransformNode stickRotate1 = new TransformNode("rotate stick",Mat4Transform.rotateAroundZ(140));
    NameNode stick1 = new NameNode("stick1");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(stickScale,stickLength,stickScale));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
      TransformNode stickTransform1 = new TransformNode("stick transform", m);
      ModelNode stickShape1 = new ModelNode("stick", snakeBody);

    TransformNode translateToTop05L = new TransformNode("translate",Mat4Transform.translate(-0.9f,2.4f,0));
    TransformNode stickRotate2 = new TransformNode("rotate stick",Mat4Transform.rotateAroundZ(100));
    NameNode stick2 = new NameNode("stick2");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(stickScale,stickLength,stickScale));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
      TransformNode stickTransform2 = new TransformNode("stick transform", m);
      ModelNode stickShape2 = new ModelNode("stick", snakeBody);

    float eyeScale = 0.4f;

    TransformNode translateToTop06L = new TransformNode("translate",Mat4Transform.translate(-0.1f,0.45f,0.2f));
    NameNode eye1L = new NameNode("eye1");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
    TransformNode eyeTransform1L = new TransformNode("eye transform", m);
    ModelNode eyeShape1L = new ModelNode("eye shape", eye);

    TransformNode translateToTop07L = new TransformNode("translate",Mat4Transform.translate(-0.1f,0.45f,-0.2f));
    NameNode eye2L = new NameNode("eye1");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
    TransformNode eyeTransform2L = new TransformNode("eye transform", m);
    ModelNode eyeShape2L = new ModelNode("eye shape", eye);

    lampRootL.addChild(lampMoveTranslate);
      lampMoveTranslate.addChild(lampTranslateL);
        lampTranslateL.addChild(lampBaseL);
        lampBaseL.addChild(lampBaseTransformL);
        lampBaseTransformL.addChild(lampBaseShapeL);
        lampBaseL.addChild(translateToTop01L);
          translateToTop01L.addChild(lowerBranchRotateYL);
          lowerBranchRotateYL.addChild(lowerBranchRotateZL);
            lowerBranchRotateZL.addChild(lowerBranchL);
              lowerBranchL.addChild(makeLowerBranchL);
                makeLowerBranchL.addChild(lowerBranchShapeL);
              lowerBranchL.addChild(jointL);
                jointL.addChild(jointTransformL);
                  jointTransformL.addChild(jointShapeL);
              lowerBranchL.addChild(translateToTop04L);
                translateToTop04L.addChild(stickRotate1);
                  stickRotate1.addChild(stick1);
                    stick1.addChild(stickTransform1);
                      stickTransform1.addChild(stickShape1);
              lowerBranchL.addChild(translateToTop05L);
                translateToTop05L.addChild(stickRotate2);
                  stickRotate2.addChild(stick2);
                    stick2.addChild(stickTransform2);
                      stickTransform2.addChild(stickShape2);
              lowerBranchL.addChild(translateToTop02L);
                translateToTop02L.addChild(upperBranchRotateL);
                  upperBranchRotateL.addChild(upperBranchL);
                    upperBranchL.addChild(makeUpperBranchL);
                      makeUpperBranchL.addChild(upperBranchShapeL);
                     makeUpperBranchL.addChild(upperBranchShapeL);
                    upperBranchL.addChild(translateToTop03L);
                      translateToTop03L.addChild(headRotateL);
                        headRotateL.addChild(lampHeadL);
                          lampHeadL.addChild(lampHeadTransformL);
                            lampHeadTransformL.addChild(lampHeadShapeL);
                          lampHeadL.addChild(translateToTop06L);
                            translateToTop06L.addChild(eye1L);
                                eye1L.addChild(eyeTransform1L);
                                  eyeTransform1L.addChild(eyeShape1L);
                          lampHeadL.addChild(translateToTop07L);
                            translateToTop07L.addChild(eye2L);
                                eye2L.addChild(eyeTransform2L);
                                  eyeTransform2L.addChild(eyeShape2L);

    lampRootL.update();  // IMPORTANT - don't forget this
    //lampRootL.print(0, false);
    //System.exit(0);

    // Right Lamp

    jointScale = 0.7f;
    branchLength = 3.3f;
    branchScale = 0.4f;
    baseHeight = 0.5f;
    baseScale = 3f;
    headLength = 2f;
    headScale = 0.7f;

    lampRootR = new NameNode("root");
    lampMoveTranslate = new TransformNode("lamp transform",Mat4Transform.translate(xPosition + 5,0,0));

    TransformNode lampTranslateR = new TransformNode("lamp transform",Mat4Transform.translate(0,0,0));

    NameNode jointR = new NameNode("joint");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(jointScale, jointScale, jointScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,4.7f,0));
    TransformNode jointTransformR = new TransformNode("joint transform", m);
    ModelNode jointShapeR = new ModelNode("Sphere(joint)", sphere);

    NameNode lampBaseR = new NameNode("lamp base");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(baseScale,baseHeight,baseScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode lampBaseTransformR = new TransformNode("lamp base transform", m);
    ModelNode lampBaseShapeR = new ModelNode("Cube(lamp base)", baseCube);

    TransformNode translateToTop01R = new TransformNode("translate base and lower branch",Mat4Transform.translate(0f,0.5f,0f));
    lowerBranchRotateYR = new TransformNode("rotate lower branch Y", Mat4Transform.rotateAroundY(lowerAngleYR[0]));
    lowerBranchRotateZR = new TransformNode("rotate lower branch Z", Mat4Transform.rotateAroundZ(lowerAngleZR[0]));

    NameNode lowerBranchR = new NameNode("lower branch");
    m = Mat4Transform.scale(branchScale, branchLength, branchScale);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeLowerBranchR = new TransformNode("lower branch transform", m);
    ModelNode lowerBranchShapeR = new ModelNode("Sphere(0)", sphere);

    upperBranchRotateR = new TransformNode("rotate upper branch",Mat4Transform.rotateAroundZ(upperAngleR[0]));
    TransformNode translateToTop02R = new TransformNode("translate two branches",Mat4Transform.translate(0,3.6f,0));
    NameNode upperBranchR = new NameNode("upper branch");
    m = Mat4Transform.scale(branchScale, branchLength, branchScale);
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode makeUpperBranchR = new TransformNode("upper branch transform", m);
    ModelNode upperBranchShapeR = new ModelNode("Sphere(1)", sphere);

    TransformNode translateToTop03R = new TransformNode("translate",Mat4Transform.translate(0,3.5f,0));
    headRotateR = new TransformNode("rotate lamp head",Mat4Transform.rotateAroundZ(headAngleR[0]));

    NameNode lampHeadR = new NameNode("lamp head");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(headLength,headScale,headScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
    TransformNode lampHeadTransformR = new TransformNode("lamp head transform", m);
    ModelNode lampHeadShapeR = new ModelNode("Cube(lamp head)", cube);

    eyeScale = 0.6f;

    TransformNode translateToTop06R = new TransformNode("translate",Mat4Transform.translate(-0.1f,0.2f,0.6f));
    NameNode eye1R = new NameNode("eye1");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
    TransformNode eyeTransform1R = new TransformNode("eye transform", m);
    ModelNode eyeShape1R = new ModelNode("eye shape", eye);

    TransformNode translateToTop07R = new TransformNode("translate",Mat4Transform.translate(-0.1f,0.2f,-0.6f));
    NameNode eye2R = new NameNode("eye2");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
    TransformNode eyeTransform2R = new TransformNode("eye transform", m);
    ModelNode eyeShape2R = new ModelNode("eye shape", eye);

    lampRootR.addChild(lampMoveTranslate);
      lampMoveTranslate.addChild(lampTranslateR);
       lampTranslateR.addChild(lampBaseR);
        lampBaseR.addChild(lampBaseTransformR);
          lampBaseTransformR.addChild(lampBaseShapeR);
        lampBaseR.addChild(translateToTop01R);
          translateToTop01R.addChild(lowerBranchRotateYR);
            lowerBranchRotateYR.addChild(lowerBranchRotateZR);
              lowerBranchRotateZR.addChild(lowerBranchR);
                lowerBranchR.addChild(makeLowerBranchR);
                  makeLowerBranchR.addChild(lowerBranchShapeR);
                lowerBranchR.addChild(jointR);
                  jointR.addChild(jointTransformR);
                    jointTransformR.addChild(jointShapeR);
                lowerBranchR.addChild(translateToTop02R);
                  translateToTop02R.addChild(upperBranchRotateR);
                    upperBranchRotateR.addChild(upperBranchR);
                      upperBranchR.addChild(makeUpperBranchR);
                        makeUpperBranchR.addChild(upperBranchShapeR);
                      upperBranchR.addChild(translateToTop03R);
                        translateToTop03R.addChild(headRotateR);
                          headRotateR.addChild(lampHeadR);
                            lampHeadR.addChild(lampHeadTransformR);
                              lampHeadTransformR.addChild(lampHeadShapeR);
                            lampHeadR.addChild(translateToTop06R);
                              translateToTop06R.addChild(eye1R);
                                eye1R.addChild(eyeTransform1R);
                                  eyeTransform1R.addChild(eyeShape1R);
                            lampHeadR.addChild(translateToTop07R);
                              translateToTop07R.addChild(eye2R);
                                eye2R.addChild(eyeTransform2R);
                                  eyeTransform2R.addChild(eyeShape2R);

    lampRootR.update();  // IMPORTANT - don't forget this
    //lampRootR.print(0, false);
    //System.exit(0);
  }

  private  Shader skyShader;
  private int[] textureSky1 = new int[1];
  private int[] textureSky2 = new int[1];
  private int[] vertexArrayId = new int[1];
  private int[] indicesSky = {0,1,2};

  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    room.render(gl);
    table.render(gl);
    egg.render(gl);
    updateEgg();
    if(animation && (lampNum != currentLampNum || currentState != state)) {
      if (lampNum == 0) moveLamp(0, lowerAngleYL[state] - lowerAngleYL[currentState], lowerAngleZL[state] - lowerAngleZL[currentState], upperAngleL[state] - upperAngleL[currentState], headAngleL[state] - headAngleL[currentState]);
      if (lampNum == 1) moveLamp(1, lowerAngleYR[state] - lowerAngleYR[currentState], lowerAngleZR[state] - lowerAngleZR[currentState], upperAngleR[state] - upperAngleR[currentState], headAngleR[state] - headAngleR[currentState]);
    }
    lampRootL.draw(gl);
    lampRootR.draw(gl);
  }

  private void updateEgg() {
    double elapsedTime = getSeconds()-startTime;
    egg.updateEgg(elapsedTime);
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
import gmaths.*;
import com.jogamp.opengl.*;

/**
 * This class stores the Robot
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class LeftLamp{

    private Model sphere;

    private SGNode lampRootL;
    private float xPosition = 0;
    private TransformNode lowerBranchRotateZL, lowerBranchRotateYL, upperBranchRotateL, headRotateL;
    private TransformNode lampMoveTranslate;


    public LeftLamp(GL3 gl, Camera camera, Light light, int[] textureId1, int[] textureId2) {

        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.5f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 50.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        sphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);
        Model snakeBody = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1);
        Model snakeHead = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1);
        Model baseCube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId2);

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
        NameNode stick2 = new NameNode("stick1");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(stickScale,stickLength,stickScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        TransformNode stickTransform2 = new TransformNode("stick transform", m);
        ModelNode stickShape2 = new ModelNode("stick", snakeBody);


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

        lampRootL.update();  // IMPORTANT - don't forget this
    }

    private int currentState = 0;
    private int state,lampNum, currentLampNum;
    private int[] lowerAngleZL = {45, -45, -30};
    private int[] lowerAngleYL = {0, 60, -90};
    private int[] upperAngleL = {-100, -20, 75};
    private int[] headAngleL = {75, 70, -40};

    private void moveLamp(int lamp, float lowerAngleY, float lowerAngleZ, float upperAngle, float headAngle, double elapsedTime) {
        float rotateAngleUpper, rotateAngleHead, rotateAngleLowerZ, rotateAngleLowerY;
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
    }

    public void render(GL3 gl) {
        lampRootL.update();
        lampRootL.draw(gl);
    }

    public void dispose(GL3 gl) {
        sphere.dispose(gl);
    }
}




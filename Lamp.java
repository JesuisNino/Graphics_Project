/* I declare that this code is my own work */
/* Author Xiaofeng Hu xhu73@sheffield.ac.uk */

import gmaths.*;
import com.jogamp.opengl.*;
import java.util.List;

public class Lamp {

    private Model cube;
    private SGNode lampRootL, lampRootR;

    private TransformNode lowerBranchRotateZL, lowerBranchRotateYL, upperBranchRotateL, headRotateL;
    private TransformNode lowerBranchRotateZR, lowerBranchRotateYR, upperBranchRotateR, headRotateR;
    private TransformNode lampMoveTranslate,bulbTransformR,lightTransformL;

    Shader shader;
    List<Light> lightList;

    public Lamp(GL3 gl, Camera camera,List<Light> lightList, Shader shader, int[] textureId1, int[] textureId2, int[] textureId3,
                int[] textureId4, int[] textureId5, int[] textureId6, int[] textureId7, int[] textureId8, int[] textureId9) {
        // initialise the model for the two lamps
        this.lightList = lightList;
        this.shader = shader;
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.5f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 50.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        Model snakeBodyL = new Model(gl, camera, lightList, shader, material, modelMatrix, mesh, textureId2, textureId1);
        Model snakeBodyR = new Model(gl, camera, lightList, shader, material, modelMatrix, mesh, textureId3, textureId1);
        Model eye = new Model(gl, camera, lightList, shader, material, modelMatrix, mesh, textureId4, textureId1);
        Model hair = new Model(gl, camera, lightList, shader, material, modelMatrix, mesh, textureId5, textureId1);
        Model snakeJointL = new Model(gl, camera, lightList, shader, material, modelMatrix, mesh, textureId6, textureId1);

        mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        material = new Material(new Vec3(1.0f, 0.5f, 0.5f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        cube = new Model(gl, camera, lightList, shader, material, modelMatrix, mesh, textureId3, textureId4);
        Model snakeHeadL = new Model(gl, camera, lightList, shader, material, modelMatrix, mesh, textureId6, textureId1);
        Model snakeHeadR = new Model(gl, camera, lightList, shader, material, modelMatrix, mesh, textureId7, textureId1);
        Model baseCube = new Model(gl, camera, lightList, shader, material, modelMatrix, mesh, textureId8, textureId1);
        Model bulb = new Model(gl, camera, lightList, shader, material, modelMatrix, mesh, textureId9, textureId1);

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
        lampMoveTranslate = new TransformNode("lamp transform",Mat4Transform.translate(-5,0,0));

        TransformNode lampTranslateL = new TransformNode("lamp transform",Mat4Transform.translate(0,0,0));

        NameNode jointL = new NameNode("joint");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(jointScale, jointScale, jointScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,(branchLength)*2,0));
        TransformNode jointTransformL = new TransformNode("joint transform", m);
        ModelNode jointShapeL = new ModelNode("Sphere(joint)", snakeJointL);

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
        ModelNode lowerBranchShapeL = new ModelNode("Sphere(0)", snakeBodyL);

        upperBranchRotateL = new TransformNode("rotate upper branch",Mat4Transform.rotateAroundZ(upperAngleL[0]));
        TransformNode translateToTop02L = new TransformNode("translate two branches",Mat4Transform.translate(0,2.5f,0));
        NameNode upperBranchL = new NameNode("upper branch");
        m = Mat4Transform.scale(branchScale, branchLength, branchScale);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode makeUpperBranchL = new TransformNode("upper branch transform", m);
        ModelNode upperBranchShapeL = new ModelNode("Sphere(1)", snakeBodyL);

        TransformNode translateToTop03L = new TransformNode("translate",Mat4Transform.translate(0,branchLength,0));
        headRotateL = new TransformNode("rotate lamp head",Mat4Transform.rotateAroundZ(headAngleL[0]));
        NameNode lampHeadL = new NameNode("lamp head");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(headLength,headScale,headScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        TransformNode lampHeadTransformL = new TransformNode("lamp head transform", m);
        ModelNode lampHeadShapeL = new ModelNode("Cube(lamp head)", snakeHeadL);

        TransformNode translateToTop04L = new TransformNode("translate",Mat4Transform.translate(-0.7f,1.8f,0));
        TransformNode stickRotate1 = new TransformNode("rotate stick",Mat4Transform.rotateAroundZ(140));
        NameNode stick1 = new NameNode("stick1");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(stickScale,stickLength,stickScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        TransformNode stickTransform1 = new TransformNode("stick transform", m);
        ModelNode stickShape1 = new ModelNode("stick", snakeBodyL);

        TransformNode translateToTop05L = new TransformNode("translate",Mat4Transform.translate(-0.9f,2.4f,0));
        TransformNode stickRotate2 = new TransformNode("rotate stick",Mat4Transform.rotateAroundZ(100));
        NameNode stick2 = new NameNode("stick2");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(stickScale,stickLength,stickScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        TransformNode stickTransform2 = new TransformNode("stick transform", m);
        ModelNode stickShape2 = new ModelNode("stick", snakeBodyL);

        float eyeScale = 0.4f;

        TransformNode translateToTop06L = new TransformNode("translate",Mat4Transform.translate(-0.1f,0.45f,0.2f));
        NameNode eye1L = new NameNode("eye1");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundY(-90));
        TransformNode eyeTransform1L = new TransformNode("eye transform", m);
        ModelNode eyeShape1L = new ModelNode("eye shape", eye);

        TransformNode translateToTop07L = new TransformNode("translate",Mat4Transform.translate(-0.1f,0.45f,-0.2f));
        NameNode eye2L = new NameNode("eye1");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundY(-90));
        TransformNode eyeTransform2L = new TransformNode("eye transform", m);
        ModelNode eyeShape2L = new ModelNode("eye shape", eye);

        TransformNode translateToTop08L = new TransformNode("translate",Mat4Transform.translate(0.6f,0,0));
        NameNode bulbL = new NameNode("bulbL");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(0.3f, 0.2f, 0.2f));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        TransformNode bulbTransformL = new TransformNode("light",m);
        ModelNode bulbShape = new ModelNode("eye shape", bulb);

        lightTransformL = new TransformNode("eye transform", Mat4Transform.translate(5,0,0));

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
        lampHeadL.addChild(translateToTop08L);
        translateToTop08L.addChild(bulbL);
        bulbL.addChild(bulbTransformL);
        bulbTransformL.addChild(bulbShape);
        bulbL.addChild(lightTransformL);

        lampRootL.update();

        // Right Lamp

        jointScale = 0.7f;
        branchLength = 3.3f;
        branchScale = 0.4f;
        baseHeight = 0.5f;
        baseScale = 3f;
        headLength = 2f;
        headScale = 0.7f;

        lampRootR = new NameNode("root");
        lampMoveTranslate = new TransformNode("lamp transform",Mat4Transform.translate(5,0,0));

        TransformNode lampTranslateR = new TransformNode("lamp transform",Mat4Transform.translate(0,0,0));

        NameNode jointR = new NameNode("joint");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(jointScale, jointScale, jointScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,4.7f,0));
        TransformNode jointTransformR = new TransformNode("joint transform", m);
        ModelNode jointShapeR = new ModelNode("Sphere(joint)", snakeBodyR);

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
        ModelNode lowerBranchShapeR = new ModelNode("Sphere(0)", snakeBodyR);

        upperBranchRotateR = new TransformNode("rotate upper branch",Mat4Transform.rotateAroundZ(upperAngleR[0]));
        TransformNode translateToTop02R = new TransformNode("translate two branches",Mat4Transform.translate(0,3.6f,0));
        NameNode upperBranchR = new NameNode("upper branch");
        m = Mat4Transform.scale(branchScale, branchLength, branchScale);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        TransformNode makeUpperBranchR = new TransformNode("upper branch transform", m);
        ModelNode upperBranchShapeR = new ModelNode("Sphere(1)", snakeBodyR);

        TransformNode translateToTop03R = new TransformNode("translate",Mat4Transform.translate(0,3.5f,0));
        headRotateR = new TransformNode("rotate lamp head",Mat4Transform.rotateAroundZ(headAngleR[0]));

        NameNode lampHeadR = new NameNode("lamp head");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(headLength,headScale,headScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        TransformNode lampHeadTransformR = new TransformNode("lamp head transform", m);
        ModelNode lampHeadShapeR = new ModelNode("Cube(lamp head)", snakeHeadR);

        eyeScale = 0.6f;

        TransformNode translateToTop06R = new TransformNode("translate",Mat4Transform.translate(-0.1f,0.2f,0.6f));
        NameNode eye1R = new NameNode("eye1");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundY(90));
        TransformNode eyeTransform1R = new TransformNode("eye transform", m);
        ModelNode eyeShape1R = new ModelNode("eye shape", eye);

        TransformNode translateToTop07R = new TransformNode("translate",Mat4Transform.translate(-0.1f,0.2f,-0.6f));
        NameNode eye2R = new NameNode("eye2");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(eyeScale, eyeScale, eyeScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundY(90));

        TransformNode eyeTransform2R = new TransformNode("eye transform", m);
        ModelNode eyeShape2R = new ModelNode("eye shape", eye);

        float hairScale = 1.5f;
        TransformNode translateToTop08R = new TransformNode("translate",Mat4Transform.translate(1f,0.5f,0));
        TransformNode hairRotateR = new TransformNode("rotate stick",Mat4Transform.rotateAroundZ(40));
        NameNode hairR = new NameNode("hairR");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(hairScale,hairScale,hairScale));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        TransformNode hairTransformR = new TransformNode("stick transform", m);
        ModelNode hairShapeR = new ModelNode("stick", hair);

        TransformNode translateToTop09R = new TransformNode("translate",Mat4Transform.translate(-1.1f,0,0));
        NameNode bulbR = new NameNode("bulbL");
        m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(0.3f, 0.4f, 0.4f));
        m = Mat4.multiply(m, Mat4Transform.translate(0,0,0));
        bulbTransformR = new TransformNode("eye transform", m);
        bulbShape = new ModelNode("eye shape", bulb);

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
        lampHeadR.addChild(translateToTop08R);
        translateToTop08R.addChild(hairRotateR);
        hairRotateR.addChild(hairR);
        hairR.addChild(hairTransformR);
        hairTransformR.addChild(hairShapeR);
        lampHeadR.addChild(translateToTop09R);
        translateToTop09R.addChild(bulbR);
        bulbR.addChild(bulbTransformR);
        bulbTransformR.addChild(bulbShape);

        lampRootR.update();

        // Spotlight of two lamps
        float[] f = lightTransformL.worldTransform.toFloatArrayForGLSL();
        this.lightList.get(2).setPosition(new Vec3(f[12], f[13], f[14]));
        this.lightList.get(2).setDirection(lightTransformL.worldTransform.getDir());
        f = bulbTransformR.worldTransform.toFloatArrayForGLSL();
        this.lightList.get(3).setPosition(new Vec3(f[12], f[13], f[14]));
        this.lightList.get(3).setDirection(bulbTransformR.worldTransform.getDir());
    }

    public boolean animation = false;

    private int currentStateL = 0, currentStateR = 0;
    private int state,lampNum, currentLampNum;
    private int[] lowerAngleZL = {45, -45, -30};
    private int[] lowerAngleYL = {0, 60, -180};
    private int[] upperAngleL = {-100, -20, 75};
    private int[] headAngleL = {75, 70, -40};

    private int[] lowerAngleZR = {-45, 60, 10};
    private int[] lowerAngleYR = {0, -70, -180};
    private int[] upperAngleR = {100, -10, -60};
    private int[] headAngleR = {-75, -50, 50};

    public void setState(int i){
        this.state=i;
    }

    public void setLampNum(int i){
        this.lampNum=i;
    }

    public int getCurrentLampNum(){
        return currentLampNum;
    }

    public int getCurrentStateL(){
        return currentStateL;
    }

    public int getCurrentStateR(){
        return currentStateR;
    }

    public void moveLamp(int lamp, float lowerAngleY, float lowerAngleZ, float upperAngle, float headAngle, double elapsedTime){
        float rotateAngleUpper, rotateAngleHead,rotateAngleLowerZ,rotateAngleLowerY;
        if (lamp==0 && (float)Math.sin(elapsedTime) < 0.99) {
            // moving the left lamp
            currentLampNum = 0;
            rotateAngleUpper = upperAngleL[currentStateL] + upperAngle * (float) Math.sin(elapsedTime);
            rotateAngleHead = headAngleL[currentStateL] + headAngle * (float) Math.sin(elapsedTime);
            rotateAngleLowerZ = lowerAngleZL[currentStateL] + lowerAngleZ * (float) Math.sin(elapsedTime);
            rotateAngleLowerY = lowerAngleYL[currentStateL] + lowerAngleY * (float) Math.sin(elapsedTime);
            lowerBranchRotateZL.setTransform(Mat4Transform.rotateAroundZ(rotateAngleLowerZ));
            lowerBranchRotateYL.setTransform(Mat4Transform.rotateAroundY(rotateAngleLowerY));
            upperBranchRotateL.setTransform(Mat4Transform.rotateAroundZ(rotateAngleUpper));
            headRotateL.setTransform(Mat4Transform.rotateAroundZ(rotateAngleHead));
            lampRootL.update();
            float[] f = lightTransformL.worldTransform.toFloatArrayForGLSL();
            this.lightList.get(2).setPosition(new Vec3(f[12], f[13], f[14]));
            Vec3 direction =lightTransformL.worldTransform.getDir();
            if(this.lightList.get(2).getPose()==0){
                if(currentStateL==1){
                    // 1 -> 0
                    direction.x = direction.x + (float) Math.sin(elapsedTime);
                    direction.z = direction.z - 1+(float) Math.sin(elapsedTime);
                }else if (currentStateL==2){
                    // 2 -> 0
                    if ((float) Math.sin(elapsedTime) < 0.64) {
                        direction.x = direction.x - (1-(float) Math.sin(elapsedTime));
                        direction.z = direction.z + (float) Math.sin(elapsedTime);
                    }else{
                        direction.x = direction.x + (1-(float) Math.sin(elapsedTime));
                        direction.z = direction.z + (1-(float) Math.sin(elapsedTime));
                    }
                }
            }
            if(this.lightList.get(2).getPose()==1){
                if(currentStateL==0){
                    // 0 -> 1
                    direction.x = direction.x + 1-(float) Math.sin(elapsedTime);
                    direction.z = direction.z - (float) Math.sin(elapsedTime);
                }else if (currentStateL==2){
                    // 2 -> 1
                    if ((float) Math.sin(elapsedTime) < 0.5) {
                        direction.x = direction.x - (1-(float) Math.sin(elapsedTime));
                        direction.z = direction.z + (float) Math.sin(elapsedTime);
                    }else{
                        direction.x = direction.x + (2f-(float) Math.sin(elapsedTime));
                        direction.z = direction.z + (3-5*(float) Math.sin(elapsedTime));
                    }
                }
            }
            if(this.lightList.get(2).getPose()==2){
                if(currentStateL==0){
                    // 0 -> 2
                    if ((float) Math.sin(elapsedTime) < 0.39) {
                        direction.x = direction.x + (float) Math.sin(elapsedTime);
                        direction.z = direction.z + (float) Math.sin(elapsedTime);
                    }else{
                        direction.x = direction.x - (float) Math.sin(elapsedTime);
                        direction.z = direction.z + (1-(float) Math.sin(elapsedTime));
                    }
                }else if (currentStateL==1){
                    // 1 -> 2
                    if ((float) Math.sin(elapsedTime) < 0.5) {
                        direction.x = direction.x + 4;
                        direction.z = direction.z - 0.5f+4* (float) Math.sin(elapsedTime);
                    }else{
                        direction.x = direction.x - (float) Math.sin(elapsedTime);
                        direction.z = direction.z + (1-(float) Math.sin(elapsedTime));
                    }
                }
            }
            this.lightList.get(2).setDirection(direction);
        }else if (lamp==1 && (float)Math.sin(elapsedTime) < 0.99) {
            // moving the right lamp
            currentLampNum = 1;
            rotateAngleUpper = upperAngleR[currentStateR] + upperAngle * (float) Math.sin(elapsedTime);
            rotateAngleHead = headAngleR[currentStateR] + headAngle * (float) Math.sin(elapsedTime);
            rotateAngleLowerZ = lowerAngleZR[currentStateR] + lowerAngleZ * (float) Math.sin(elapsedTime);
            rotateAngleLowerY = lowerAngleYR[currentStateR] + lowerAngleY * (float) Math.sin(elapsedTime);
            lowerBranchRotateZR.setTransform(Mat4Transform.rotateAroundZ(rotateAngleLowerZ));
            lowerBranchRotateYR.setTransform(Mat4Transform.rotateAroundY(rotateAngleLowerY));
            upperBranchRotateR.setTransform(Mat4Transform.rotateAroundZ(rotateAngleUpper));
            headRotateR.setTransform(Mat4Transform.rotateAroundZ(rotateAngleHead));
            lampRootR.update();
            float[] f = bulbTransformR.worldTransform.toFloatArrayForGLSL();
            this.lightList.get(3).setPosition(new Vec3(f[12], f[13], f[14]));
            Vec3 direction = bulbTransformR.worldTransform.getDir();
            if(this.lightList.get(3).getPose()==0){
                if(currentStateR==1){
                    // 1 -> 0
                    direction.x = direction.x - (float) Math.sin(elapsedTime);
                    direction.z = direction.z - 1+(float) Math.sin(elapsedTime);
                }else if (currentStateR==2){
                    // 2 -> 0
                    if ((float) Math.sin(elapsedTime) < 0.8) {
                        direction.x = direction.x + (1-(float) Math.sin(elapsedTime));
                        direction.z = direction.z - (float) Math.sin(elapsedTime);
                    }else{
                        direction.x = direction.x + (1-(float) Math.sin(elapsedTime));
                        direction.z = direction.z - (1-(float) Math.sin(elapsedTime));
                    }
                }
            }
            if(this.lightList.get(3).getPose()==1){
                if(currentStateR==0){
                    // 0 -> 1
                    direction.x = direction.x - 1-(float) Math.sin(elapsedTime);
                    direction.z = direction.z - 3*(float) Math.sin(elapsedTime);
                }else if (currentStateR==2){
                    // 2 -> 1
                    direction.x = direction.x + (1-(float) Math.sin(elapsedTime));
                    direction.z = direction.z - (float) Math.sin(elapsedTime);
                }
            }
            if(this.lightList.get(3).getPose()==2){
                if(currentStateR==0){
                    // 0 -> 2
                    if ((float) Math.sin(elapsedTime) < 0.55) {
                        direction.x = direction.x - (1-(float) Math.sin(elapsedTime));
                        direction.z = direction.z - (float) Math.sin(elapsedTime);
                    }else{
                        direction.x = direction.x + (float) Math.sin(elapsedTime);
                        direction.z = direction.z - 3*(1-(float) Math.sin(elapsedTime));
                    }
                }else if (currentStateR==1){
                    // 1 -> 2
                    direction.x = direction.x + (float) Math.sin(elapsedTime);
                    direction.z = direction.z - (1-(float) Math.sin(elapsedTime));
                }
            }
            this.lightList.get(3).setDirection(direction);

        }else {
            if (currentLampNum == 0) currentStateL = state;
            if (currentLampNum == 1) currentStateR = state;
            animation = false;
        }

    }

    public void render(GL3 gl) {
        lampRootL.update();
        lampRootR.update();
        lampRootL.draw(gl);
        lampRootR.draw(gl);
    }

    public void dispose(GL3 gl) {
        cube.dispose(gl);
    }
}


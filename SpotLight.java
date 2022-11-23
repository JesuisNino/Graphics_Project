import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class SpotLight {

    public Material material;
    private Vec3 position;
    public float rotateZ,rotateY;
    private Mat4 model;
    private Shader shader;
    private Camera camera;
    private SGNode lightRootL;
    private TransformNode lightMoveTranslate, rotateNodeZL, rotateNodeYL, lightL;

    public SpotLight(GL3 gl, int i) {
        lightRootL = new NameNode("root");
        lightMoveTranslate = new TransformNode("lamp transform",Mat4Transform.translate(-5,0,0));

        material = new Material();
        material.setAmbient(1f, 1f, 1f);
        material.setDiffuse(1f, 1f, 1f);
        material.setSpecular(1f, 1f, 1f);
        position = new Vec3(-4f,3f,0f);
        model = new Mat4(1);
        model = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), model);
        model = Mat4.multiply(Mat4Transform.translate(position), model);
        shader = new Shader(gl, "vs_light_01.txt", "fs_light_01.txt");
        lightL = new TransformNode("lightL", model);
        rotateNodeYL = new TransformNode("rotate left light Y",Mat4Transform.rotateAroundY(0));
        rotateNodeZL = new TransformNode("rotate left light Z",Mat4Transform.rotateAroundZ(0));

        lightRootL.addChild(lightMoveTranslate);
        lightMoveTranslate.addChild(rotateNodeYL);
        rotateNodeYL.addChild(rotateNodeZL);
        rotateNodeZL.addChild(lightL);

        lightL.update();

        fillBuffers(gl);
    }

    public void setPosition(Vec3 v) {
        position.x = v.x;
        position.y = v.y;
        position.z = v.z;
    }

    public void setRotate(int z, int y){
        rotateZ = z;
        rotateY = y;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setMaterial(Material m) {
        material = m;
    }

    public Material getMaterial() {
        return material;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public boolean animation = false;

    private int currentState = 0;
    private int state;
    private int[] lightAngleZL = {35, 30, -30};
    private int[] lightAngleYL = {0, 60, -90};


    public void setState(int i){
        this.state=i;
    }

    public int getCurrentState(){
        return currentState;
    }

    public void moveSpot(int lamp, float angleY, float angleZ, double elapsedTime){
        float rotateAngleZ,rotateAngleY;
        if (lamp==0 && (float)Math.sin(elapsedTime) < 0.99) {
            rotateAngleZ = lightAngleZL[currentState] + angleZ * (float) Math.sin(elapsedTime);
            rotateAngleY = lightAngleYL[currentState] + angleY * (float) Math.sin(elapsedTime);
            lightRootL.update();
        }else if (lamp==1 && (float)Math.sin(elapsedTime) < 0.99) {

        }else {
            currentState = state;
            animation = false;
        }


            lightRootL.update();
    }

    public void render(GL3 gl) {

        lightRootL.update();
        lightRootL.draw(gl);

        Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), model));

        shader.use(gl);
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        gl.glDeleteBuffers(1, vertexBufferId, 0);
        gl.glDeleteVertexArrays(1, vertexArrayId, 0);
        gl.glDeleteBuffers(1, elementBufferId, 0);
    }

    // ***************************************************
    /* THE DATA
     */
    // anticlockwise/counterclockwise ordering

    private float[] vertices = new float[] {  // x,y,z
            -0.5f, -0.5f, -0.5f,  // 0
            -0.5f, -0.5f,  0.5f,  // 1
            -0.5f,  0.5f, -0.5f,  // 2
            -0.5f,  0.5f,  0.5f,  // 3
            0.5f, -0.5f, -0.5f,  // 4
            0.5f, -0.5f,  0.5f,  // 5
            0.5f,  0.5f, -0.5f,  // 6
            0.5f,  0.5f,  0.5f   // 7
    };

    private int[] indices =  new int[] {
            0,1,3, // x -ve
            3,2,0, // x -ve
            4,6,7, // x +ve
            7,5,4, // x +ve
            1,5,7, // z +ve
            7,3,1, // z +ve
            6,4,0, // z -ve
            0,2,6, // z -ve
            0,4,5, // y -ve
            5,1,0, // y -ve
            2,3,7, // y +ve
            7,6,2  // y +ve
    };

    private int vertexStride = 3;
    private int vertexXYZFloats = 3;

    // ***************************************************
    /* THE LIGHT BUFFERS
     */

    private int[] vertexBufferId = new int[1];
    private int[] vertexArrayId = new int[1];
    private int[] elementBufferId = new int[1];

    private void fillBuffers(GL3 gl) {
        gl.glGenVertexArrays(1, vertexArrayId, 0);
        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glGenBuffers(1, vertexBufferId, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
        FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);

        int stride = vertexStride;
        int numXYZFloats = vertexXYZFloats;
        int offset = 0;
        gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
        gl.glEnableVertexAttribArray(0);

        gl.glGenBuffers(1, elementBufferId, 0);
        IntBuffer ib = Buffers.newDirectIntBuffer(indices);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
        gl.glBindVertexArray(0);
    }

}
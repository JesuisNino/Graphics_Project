import gmaths.*;
import com.jogamp.opengl.*;

/**
 * This class stores the Robot
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class Room{

    private Model floor, backWall, leftWall, rightWall;

    public Room(GL3 gl, Camera camera, Light light, int[] textureId1, int[] textureId2, int[] textureId3) {
        Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
        Material material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 50.0f);
        Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
        floor = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1);

        mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        shader = new Shader(gl, "vs_sky.txt", "fs_sky.txt");
        material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 50.0f);
        modelMatrix = Mat4Transform.scale(16,1f,10);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0,5f,-8f), modelMatrix);
        backWall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3);

        mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
        material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 50.0f);
        modelMatrix = Mat4Transform.scale(16,1f,10);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(-8f,5f,0), modelMatrix);
        leftWall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId2);

        mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
        material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 50.0f);
        modelMatrix = Mat4Transform.scale(16,1f,10);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(-90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(8f,5f,0), modelMatrix);
        rightWall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId2);

    }



    public void render(GL3 gl) {
        floor.render(gl);
        rightWall.render(gl);
        leftWall.render(gl);
        backWall.render(gl);
    }

    public void moveCloud(GL3 gl, double elapsedTime){
        double t = elapsedTime*0.1;  // *0.1 slows it down a bit
        float offsetX = (float)(t - Math.floor(t));
        float offsetY = 0.0f;
        backWall.shader.setFloat(gl, "offset", offsetX, offsetY);
    }

    public void dispose(GL3 gl) {
        floor.dispose(gl);
        rightWall.dispose(gl);
        leftWall.dispose(gl);
        backWall.dispose(gl);
    }

    private double getSeconds() {
        return System.currentTimeMillis()/1000.0;
    }
}


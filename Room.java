import gmaths.*;
import com.jogamp.opengl.*;

import java.util.List;

/* I declare that this code is my own work */
/* Author Xiaofeng Hu xhu73@sheffield.ac.uk */

public class Room{

    private Model floor, window, leftWall, rightWall, frame;

    public Room(GL3 gl, Camera camera,  List<Light> light, int[] textureId1, int[] textureId2, int[] textureId3) {
        // make a room and a moving cloud with window frame
        Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
        Material material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 50.0f);
        Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
        floor = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1);

        mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
        material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 50.0f);
        modelMatrix = Mat4Transform.scale(16,1f,10);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0,5f,-8f), modelMatrix);
        frame = new Model(gl, camera, light, shader, material, modelMatrix, mesh,textureId1);

        mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        shader = new Shader(gl, "vs_sky.txt", "fs_sky.txt");
        material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 50.0f);
        modelMatrix = Mat4Transform.scale(15,1f,9);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0,5f,-8f), modelMatrix);
        window = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3);

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
        window.render(gl);
        frame.render(gl);
        window.render(gl);

    }

    public void moveCloud(GL3 gl, double elapsedTime){
        double t = elapsedTime*0.1;  // *0.1 slows it down a bit
        float offsetX = (float)(t - Math.floor(t));
        float offsetY = 0.0f;
        window.shader.setFloat(gl, "offset", offsetX, offsetY);
    }

    public void dispose(GL3 gl) {
        floor.dispose(gl);
        rightWall.dispose(gl);
        leftWall.dispose(gl);
        window.dispose(gl);
        frame.dispose(gl);
    }

}


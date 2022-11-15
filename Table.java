import gmaths.*;
import com.jogamp.opengl.*;

/**
 * This class stores the Robot
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class Table{

    private Model base, table, tableLeg1, tableLeg2, tableLeg3, tableLeg4;

    public Table(GL3 gl, Camera camera, Light light, int[] textureId1, int[] textureId2) {
        // table
        float wholeHeight = 5.5f;
        float tableHeight = 0.5f;
        float tableWidth = 4f;
        float tableDepth = 4f;

        Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,wholeHeight/2,0.5f), Mat4Transform.translate(tableWidth-0.5f,0.5f,tableWidth-0.5f));
        tableLeg1 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);

        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,wholeHeight/2,0.5f), Mat4Transform.translate(tableWidth-0.5f,0.5f,-tableWidth+0.5f));
        tableLeg2 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);

        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,wholeHeight/2,0.5f), Mat4Transform.translate(-tableWidth+0.5f,0.5f,tableWidth-0.5f));
        tableLeg3 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);

        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,wholeHeight/2,0.5f), Mat4Transform.translate(-tableWidth+0.5f,0.5f,-tableWidth+0.5f));
        tableLeg4 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);

        wholeHeight = wholeHeight + 0.5f;
        modelMatrix = Mat4.multiply(Mat4Transform.scale(tableWidth,tableHeight,tableDepth), Mat4Transform.translate(0,wholeHeight,0));
        table = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);

        wholeHeight = (wholeHeight + tableHeight)*2 + 0.5f;
        modelMatrix = Mat4.multiply(Mat4Transform.scale(1.5f,tableHeight/2,1.5f), Mat4Transform.translate(0, wholeHeight,0));
        base = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);

    }


    public void render(GL3 gl) {
        table.render(gl);
        tableLeg1.render(gl);
        tableLeg2.render(gl);
        tableLeg3.render(gl);
        tableLeg4.render(gl);
        base.render(gl);
    }

    public void dispose(GL3 gl) {
        table.dispose(gl);
        tableLeg1.dispose(gl);
        tableLeg2.dispose(gl);
        tableLeg3.dispose(gl);
        tableLeg4.dispose(gl);
        base.dispose(gl);
    }
}


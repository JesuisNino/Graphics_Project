import gmaths.*;
import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class Model {
  
  private Mesh mesh;
  private int[] textureId1; 
  private int[] textureId2; 
  private Material material;
  public Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;


  public List<Light> getLightList() {
    return lightList;
  }

  public void setLightList(List<Light> lightList) {
    this.lightList = lightList;
  }

  private List<Light> lightList = new ArrayList<>();
  
  public Model(GL3 gl, Camera camera, List<Light> lightList, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.lightList = lightList;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
  }
  
  public Model(GL3 gl, Camera camera, List<Light> lightList, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(gl, camera, lightList, shader, material, modelMatrix, mesh, textureId1, null);
  }
  
  public Model(GL3 gl, Camera camera, List<Light> lightList, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh) {
    this(gl, camera, lightList, shader, material, modelMatrix, mesh, null, null);
  }
  
  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }
  
  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());
    for(int i =0;i<lightList.size();i++) {
      Light light = lightList.get(i);
      int n = i;
      shader.setInt(gl,"lightOff["+n+"]",light.isEnabled()?1:0);
      shader.setVec3(gl, "light["+n+"].position", light.getPosition());
      shader.setVec3(gl, "light["+n+"].ambient", light.getMaterial().getAmbient());
      shader.setVec3(gl, "light["+n+"].diffuse", light.getMaterial().getDiffuse());
      shader.setVec3(gl, "light["+n+"].specular", light.getMaterial().getSpecular());

      shader.setVec3(gl, "material.ambient", material.getAmbient());
      shader.setVec3(gl, "material.diffuse", material.getDiffuse());
      shader.setVec3(gl, "material.specular", material.getSpecular());
      shader.setFloat(gl, "material.shininess", material.getShininess());

      if (light.getClass().equals(SpotLight.class)) {

        n = i -2;
        shader.setInt(gl,"spotLightOff["+n+"]",light.isEnabled()?1:0);
        shader.setFloat(gl, "spotlight_intensity", 1);
        shader.setVec3(gl, "spotLight["+n+"].position", light.getPosition());

        shader.setVec3(gl, "spotLight["+n+"].ambient", new Vec3(1, 1, 1));
        shader.setVec3(gl, "spotLight["+n+"].diffuse", new Vec3(0.8f, 0.8f, 0.8f));
        shader.setVec3(gl, "spotLight["+n+"].specular", new Vec3(1.0f, 1.0f, 1.0f));


        shader.setVec3(gl, "spotLight["+n+"].direction", light.getDirection());
        shader.setFloat(gl, "spotLight["+n+"].constant", 1.0f);
        shader.setFloat(gl, "spotLight["+n+"].linear", 0.09f);
        shader.setFloat(gl, "spotLight["+n+"].quadratic", 0.032f);
        shader.setFloat(gl, "spotLight["+n+"].cutOff", (float) Math.cos(Math.toRadians(12.5)));
        shader.setFloat(gl, "spotLight["+n+"].outerCutOff", (float) Math.cos(Math.toRadians(15)));

      }
    }
    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
    }
    if (textureId2!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
    }
    mesh.render(gl);
  } 
  
  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }
  
  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) gl.glDeleteBuffers(1, textureId1, 0);
    if (textureId2!=null) gl.glDeleteBuffers(1, textureId2, 0);
  }
  
}
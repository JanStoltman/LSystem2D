package app.scenes;

import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.DisplayMode;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.Vector;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class Estocastic implements GLEventListener {

    private GLAutoDrawable autoDrawable;
    public static DisplayMode dm, dm_old;
    private GLU glu = new GLU();
    private int texture;

    private Vector<String> productions;
    private float angle;
    private int currentProduction;

    private static final float DEGTORAD = 0.0174532925199432957f;

    public Estocastic(Vector<String> productions, float angle) {
        this.productions = productions;
        this.angle = angle;
        this.currentProduction = 0;
    }

    @Override
    public void init(GLAutoDrawable drawable) {

        final GL2 gl = drawable.getGL().getGL2();

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

        gl.glEnable(GL2.GL_TEXTURE_2D);
        try {
            File im = new File("textures/background.jpg");
            com.jogamp.opengl.util.texture.Texture t = TextureIO.newTexture(im, true);
            texture = t.getTextureObject(gl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        this.autoDrawable = drawable;

        final GL2 gl = this.autoDrawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -5.0f);
        //gl.glClearColor(1, 1, 1, 1);

        // Background
        //this.drawBackground();

        // Tree
        gl.glPushMatrix();
        gl.glScalef(0.05f, 0.05f, 0.05f);
        gl.glColor3d(0, 1, 0);
        this.drawLSystem(productions.get(1), -3, -40);
        this.drawLSystem(productions.get(2), -1, -40);
        this.drawLSystem(productions.get(1), 0, -40);
        this.drawLSystem(productions.get(1), 2, -40);
        this.drawLSystem(productions.get(2), 3, -40);

        this.drawLSystem(productions.get(currentProduction), 1, -40);
        gl.glPopMatrix();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        final GL2 gl = drawable.getGL().getGL2();

        if (height <= 0) {
            height = 1;
        }

        final float h = (float) width / (float) height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    private class Node {

        public double x_ = 0.0f;
        public double y_ = 0.0f;
        public double angle_ = 0.0f;
    }

    private void drawLine(double x1, double y1, double x2, double y2) {

        final GL2 gl = this.autoDrawable.getGL().getGL2();

        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3d(x1, y1, 0);
        gl.glVertex3d(x2, y2, 0);
        gl.glEnd();
    }

    public void drawLSystem(String word, double initX, double initY) {

        double rotAngle = 90.0f;
        double xo = initX;
        double yo = initY;
        double xf = xo;
        double yf = yo;

        Stack<Node> nodes = new Stack<>();

        int i = 0;

        while (i < word.length()) {

            switch (word.charAt(i)) {

                case 'F':
                    xf = xo + 1.0f * Math.cos(rotAngle * DEGTORAD);
                    yf = yo + 1.0f * Math.sin(rotAngle * DEGTORAD);
                    this.drawLine(xo, yo, xf, yf);
                    xo = xf;
                    yo = yf;
                    break;

                case '[':
                    Node currentNode = new Node();
                    currentNode.x_ = xf;
                    currentNode.y_ = yf;
                    currentNode.angle_ = rotAngle;
                    nodes.push(currentNode);
                    break;

                case ']':
                    xo = nodes.peek().x_;
                    yo = nodes.peek().y_;
                    xf = xo;
                    yf = yo;
                    rotAngle = nodes.peek().angle_;
                    nodes.pop();
                    break;

                case '+':
                    rotAngle += angle;
                    break;

                case '-':
                    rotAngle -= angle;
                    break;
            }

            i++;
        }
    }

    private void drawBackground() {

        final GL2 gl = this.autoDrawable.getGL().getGL2();

        float size = 2.6f;
        float x = size * 1.56f;
        float y = size;

        System.out.println("x = " + x);
        System.out.println("y = " + y);

        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(x, y, -1.0f);

        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(-x, y, -1.0f);

        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(-x, -y, -1.0f);

        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(x, -y, -1.0f);
        gl.glEnd();
    }

    private void drawEjes() {

        final GL2 gl = this.autoDrawable.getGL().getGL2();

        gl.glBegin(GL2.GL_LINES);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(-50.0f, 0.0f, 0.0f);
        gl.glVertex3f(50.0f, 0.0f, 0.0f);

        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(0.0f, -50.0f, 0.0f);
        gl.glVertex3f(0.0f, 50.0f, 0.0f);

        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(0.0f, 0.0f, -50.0f);
        gl.glVertex3f(0.0f, 0.0f, 50.0f);
        gl.glEnd();
    }

    public int getCurrentProduction() {
        return currentProduction;
    }

    public void setCurrentProduction(int currentProduction) {
        this.currentProduction = currentProduction;
        System.out.println("Nivel de produccion: " + currentProduction);
    }

}

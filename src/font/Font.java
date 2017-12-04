package font;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;

import assets.Assets;
import render.Camera;
import render.Shader;
import render.Texture;

public class Font {
	
	private static String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!?,/%$()+-:;'\"";
	
	GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
	Camera cam = new Camera(vid.width(), vid.height());

	private Shader shader;
	
	private Matrix4f transform = new Matrix4f();
	
	//Font textures///////////////////////////////
	private Texture a = new Texture("font/0.png");
	private Texture b = new Texture("font/1.png");
	private Texture c = new Texture("font/2.png");
	private Texture d = new Texture("font/3.png");
	private Texture e = new Texture("font/4.png");
	private Texture f = new Texture("font/5.png");
	private Texture g = new Texture("font/6.png");
	private Texture h = new Texture("font/7.png");
	private Texture i = new Texture("font/8.png");
	private Texture j = new Texture("font/9.png");
	private Texture k = new Texture("font/10.png");
	private Texture l = new Texture("font/11.png");
	private Texture m = new Texture("font/12.png");
	private Texture n = new Texture("font/13.png");
	private Texture o = new Texture("font/14.png");
	private Texture p = new Texture("font/15.png");
	private Texture q = new Texture("font/16.png");
	private Texture r = new Texture("font/17.png");
	private Texture s = new Texture("font/18.png");
	private Texture t = new Texture("font/19.png");
	private Texture u = new Texture("font/20.png");
	private Texture v = new Texture("font/21.png");
	private Texture w = new Texture("font/22.png");
	private Texture x = new Texture("font/23.png");
	private Texture y = new Texture("font/24.png");
	private Texture z = new Texture("font/25.png");
	private Texture n0 = new Texture("font/26.png");
	private Texture n1 = new Texture("font/27.png");
	private Texture n2 = new Texture("font/28.png");
	private Texture n3 = new Texture("font/29.png");
	private Texture n4 = new Texture("font/30.png");
	private Texture n5 = new Texture("font/31.png");
	private Texture n6 = new Texture("font/32.png");
	private Texture n7 = new Texture("font/33.png");
	private Texture n8 = new Texture("font/34.png");
	private Texture n9 = new Texture("font/35.png");
	
	private Texture period = new Texture("font/36.png");
	private Texture bang = new Texture("font/37.png");
	private Texture question = new Texture("font/38.png");
	private Texture comma = new Texture("font/39.png");
	private Texture backSlash = new Texture("font/40.png");
	private Texture percent = new Texture("font/41.png");
	private Texture money = new Texture("font/42.png");
	private Texture openPren = new Texture("font/43.png");
	private Texture closePren = new Texture("font/44.png");
	private Texture plus = new Texture("font/45.png");
	private Texture minus = new Texture("font/46.png");
	private Texture colon = new Texture("font/47.png");
	private Texture semiColon = new Texture("font/48.png");
	private Texture apostrophe = new Texture("font/49.png");
	private Texture quote = new Texture("font/50.png");
	//////////////////////////////////////////////////
	
	private Texture[] glyphs;
	
	public Font(Shader shader) {

		glyphs = new Texture[52];
		
		//glyphs//////////////////////
		
		glyphs[0] = a;
		glyphs[1] = b;
		glyphs[2] = c;
		glyphs[3] = d;
		glyphs[4] = e;
		glyphs[5] = f;
		glyphs[6] = g;
		glyphs[7] = h;
		glyphs[8] = i;
		glyphs[9] = j;
		glyphs[10] = k;
		glyphs[11] = l;
		glyphs[12] = m;
		glyphs[13] = n;
		glyphs[14] = o;
		glyphs[15] = p;
		glyphs[16] = q;
		glyphs[17] = r;
		glyphs[18] = s;
		glyphs[19] = t;
		glyphs[20] = u;
		glyphs[21] = v;
		glyphs[22] = w;
		glyphs[23] = x;
		glyphs[24] = y;
		glyphs[25] = z;
		glyphs[26] = n0;
		glyphs[27] = n1;
		glyphs[28] = n2;
		glyphs[29] = n3;
		glyphs[30] = n4;
		glyphs[31] = n5;
		glyphs[32] = n6;
		glyphs[33] = n7;
		glyphs[34] = n8;
		glyphs[35] = n9;
		
		glyphs[36] = period;
		glyphs[37] = bang;
		glyphs[38] = question;
		glyphs[39] = comma;
		glyphs[40] = backSlash;
		glyphs[41] = percent;
		glyphs[42] = money;
		glyphs[43] = openPren;
		glyphs[44] = closePren;
		glyphs[45] = plus;
		glyphs[46] = minus;
		glyphs[47] = colon;
		glyphs[48] = semiColon;
		glyphs[49] = apostrophe;
		glyphs[50] = quote;
		///////////////////////////////
		
		this.shader = shader;
	}
	
	public void render(String msg, Vector2f position, Vector2f scale, Vector3f color) {
		
		msg = msg.toUpperCase();
    	int length = msg.length();
    	for(int i = 0; i<length; i++){
    		int c = letters.indexOf(msg.charAt(i));
    		if(c < 0) continue;
		
			transform.identity().translate((position.x+(i*(scale.x*2)))-msg.length()*scale.x, position.y, 0).scale(scale.x, scale.y, 1);
		
			shader.bind();
			shader.setUniform("sampler", 0);
			shader.setUniform("fontColor", new Vector3f(color.x/255, color.y/255, color.z/255));
			shader.setUniform("projection", cam.getProjection().mul(transform));
		
    		glyphs[c].bind(0);
    		Assets.getModel().render();
    	}
	}

}

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.Font;
import java.awt.font.*;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.google.gson.annotations.*;
import com.google.gson.internal.*;
import com.google.gson.internal.bind.*;
import com.google.gson.reflect.*;
import com.google.gson.stream.*;

import java.io.*;
/**
 * Servlet implementation class FontReconstruction
 */
@WebServlet("/FontReconstruction")
public class FontReconstruction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FontReconstruction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
				
				//userList = new ArrayList<>();

				List<FontData> fontDataList = new ArrayList<>();
				
				String outFont = "";
				
				
				System.out.println("start");
				if(request.getParameter("font") != null){
					outFont = request.getParameter("font");
				}else{
					outFont = "a";
				}
				ArrayList path;
				FontOutlineSystem fos;
				fos = new FontOutlineSystem("ƒqƒ‰ƒMƒmŠpƒS", 100);
				path = fos.convert(outFont, 0, 0);
			
				System.out.println(gson.toJson(path));
				float x=0, y=0, ox=0, oy=0;
				for(int i = 0; i<path.size();i++){
					
					FontPoint fp = (FontPoint)path.get(i);
				    ox = x;
				    oy = y;
				    x = fp.x;
				    y = fp.y;
				    if (fp.mode==FontPoint.DRAW) {
				    	//System.out.println(ox); 
				    }else{
				    	//System.out.println("MOVE");
				    }
				}
				
				try{
					//FileWriter fw = new FileWriter("C:/Users/bfp_p008/Desktop/javaOut/font.json",false);
					String callback = request.getParameter("callback");
					
					
					//pw.close();
					System.out.println("complete font json");
					response.setContentType("application/json charaset=utf-8");
					response.setHeader("Access-Control-Allow-Origin", "*");
					
					PrintWriter pw = response.getWriter();
					pw.println(callback + "(" + gson.toJson(path) + ")");
					//response.getWriter().write(gson.toJson(path).toString());
				}catch(IOException ex){
					ex.printStackTrace();
				}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}


class FontPoint {
	  static final int MOVE = PathIterator.SEG_MOVETO;
	  static final int DRAW = PathIterator.SEG_LINETO;
	  float x, y;
	  int mode;
	  String lineType;
	  FontPoint() {
	    this(0f,0f,MOVE,"");
	  }
	  FontPoint(float x, float y) {
	    this(x,y,MOVE,"");
	  }
	  FontPoint(float x, float y, int mode, String lineType) {
	    this.x = x;
	    this.y = y;
	    this.mode = mode;
	    this.lineType = lineType;
	  }
	}

class FontData{
	public String type;
	public float x;
	public float y;
	
	public FontData(String type, float x, float y){
		this.type = type;
		this.x = x;
		this.y = y;
	}
}

class FontOutlineSystem {
	Font font;
	BufferedImage img;
	Graphics2D g2d;
	FontRenderContext frc;
	FontData fontData;
	
	FontOutlineSystem(){
		
	}
	
	FontOutlineSystem(String fontName, int fontSize){
		img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		
		frc = g2d.getFontRenderContext();
		loadFont(fontName, fontSize);
	}
	
	void loadFont(String name, int size){
		font = new Font(name,Font.PLAIN,size);
	}
	
	ArrayList convert(String text, float xo, float yo){
		ArrayList al = new ArrayList<>();
		if(font==null) return al;
		
		float [] seg = new float[6];
		float x=0, y=0, mx=0, my=0;
		GlyphVector gv = font.createGlyphVector(frc, text);
		Shape glyph = gv.getOutline(xo, yo);
		PathIterator pi = glyph.getPathIterator(null);
		String lineType = "";
		
		
		while(!pi.isDone()){
			int segtype = pi.currentSegment(seg);
			int mode = 0;
			switch(segtype){
			
			
			case PathIterator.SEG_MOVETO:
				//System.out.println("PathIterator.SEG_MOVETO");
				x = mx = seg[0];
				y = my = seg[1];
				lineType = "MOVETO";
				mode = FontPoint.MOVE;
				break;
				
			case PathIterator.SEG_LINETO:
				//System.out.println("PathIterator.SEG_LINETO");
				x = seg[0];
				y = seg[1];
				lineType = "LINETO";
				mode = FontPoint.DRAW;
				break;
				
			case PathIterator.SEG_QUADTO:
				//System.out.println("PathIterator.SEG_QUADTO"); 
				x = seg[0];
				y = seg[1];
				lineType = "QUADTO";
				mode = FontPoint.DRAW;
				break;
				
			case PathIterator.SEG_CUBICTO:
				//System.out.println("PathIterator.SEG_CUBICTO"); 
				x = seg[0];
				y = seg[1];
				lineType = "CUBICTO";
				mode = FontPoint.DRAW;
				break;
				
			case PathIterator.SEG_CLOSE:
				lineType = "CLOSETO";
				//System.out.println("PathIterator.SEG_CLOSE");
				x = mx;
				y = my;
				mode = FontPoint.DRAW;
				break;
				
				
			}
			
			al.add(new FontPoint(x,y,mode,lineType));
			pi.next();
		}
		
		return al;
	}
	
}

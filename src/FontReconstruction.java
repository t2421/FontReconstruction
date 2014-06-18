
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
import java.awt.GraphicsEnvironment;
import java.awt.font.*;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;

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
		String outFont = "";
		String fontName = "";
		int dataType = 0;
		int fontSize = 0;
		request.setCharacterEncoding("utf-8");
		if(request.getParameter("font") != null){
			outFont = URLDecoder.decode(request.getParameter("font"),"utf-8");
			dataType = Integer.parseInt(request.getParameter("dataType"));
			fontName = URLDecoder.decode(request.getParameter("fontName"),"utf-8");
			fontSize = Integer.parseInt(request.getParameter("fontSize"));
		}else{
			outFont = "a";
			fontName = "Serif";
			fontSize = 30;
		}
		
		
		ArrayList path;
		FontOutlineSystem fos;
		fos = new FontOutlineSystem(fontName, fontSize);
		path = fos.convert(outFont, 0, 0);
		
		try{
			String callback = request.getParameter("callback");
			response.setContentType("application/json charaset=utf-8");
			response.setHeader("Access-Control-Allow-Origin", "*");
			PrintWriter pw = response.getWriter();
			System.out.println(dataType);
			if(dataType==1){
				pw.println(callback + "(" + gson.toJson(path) + ")");
			}else{
				pw.println(gson.toJson(path));
			}
			
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
  String lineType;
  float [] points = new float[6];

  FontPoint(String lineType,float[] points) {
    this.lineType = lineType;
    this.points = points;
  }
}

class FontOutlineSystem {
	Font font;
	BufferedImage img;
	Graphics2D g2d;
	FontRenderContext frc;
	
	FontOutlineSystem(){
		
	}
	
	FontOutlineSystem(String fontName, int fontSize){
		img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		
		
		loadFont(fontName, fontSize);
	}
	
	void loadFont(String name, int size){
		
		font = new Font(name,Font.PLAIN,size);
		Font [] fonts=GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for(int i = 0;i<fonts.length;i++){
			System.out.println(fonts[i].getName());
			if(fonts[i].getName().equals(name)){
				font = fonts[i];
				font = 	font.deriveFont(Font.PLAIN,size);
			}
		}
		
		//font = fonts[i]
	}
	
	ArrayList convert(String text, float xo, float yo){
		ArrayList al = new ArrayList<FontPoint>();
		if(font==null) return al;
		
		float [] seg = new float[6];
		float x=0, y=0, mx=0, my=0;
		
		frc = new FontRenderContext(new AffineTransform(),false,false);
		GlyphVector gv = font.createGlyphVector(frc, text);
		Shape glyph = gv.getOutline(xo, yo);
		PathIterator pi = glyph.getPathIterator(null);
		String lineType = "";
		
		
		while(!pi.isDone()){
			seg = new float[6];
			int segtype = pi.currentSegment(seg);
			int mode = 0;
			switch(segtype){
			
			
			case PathIterator.SEG_MOVETO:

				lineType = "MOVETO";
				break;
				
			case PathIterator.SEG_LINETO:

				lineType = "LINETO";
				
				break;
				
			case PathIterator.SEG_QUADTO:
				lineType = "QUADTO";
				
				break;
				
			case PathIterator.SEG_CUBICTO:
				lineType = "CUBICTO";
				
				break;
				
			case PathIterator.SEG_CLOSE:
				lineType = "CLOSETO";
				
				break;
			
			}
			al.add(new FontPoint(lineType,seg.clone()));
			pi.next();
		}
		Font [] fonts=GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for(int i = 0;i<fonts.length;i++){
			//System.out.println(fonts[i]);
		}

		
		return al;
	}
	
}

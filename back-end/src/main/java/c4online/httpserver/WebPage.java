package c4online.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class WebPage extends HttpServlet{
	static final String ROOT = "src/main/resources/front-end/";
	final String htmlFilePath;
	
	public WebPage(String _htmlFilePath) {
		htmlFilePath = _htmlFilePath;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		serveHtml(resp);
	}
	
	public void serveHtml(HttpServletResponse resp){
		String responseHtml = "";
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(ROOT + htmlFilePath))) {
			for (String line; (line = br.readLine()) != null;) {
	            responseHtml += line+"\n";
	        }
		} catch (Exception e) {
			System.err.println("Unable to open html file at: " + ROOT+htmlFilePath);
			e.printStackTrace();
		}
		
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_OK);
		try {
			resp.getWriter().write(responseHtml);
		} catch (IOException e) {
			System.err.println("Unable to serve opened html file");
			e.printStackTrace();
		}
	}
}

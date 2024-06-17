package c4online.httpserver.service;

import c4online.httpserver.WebPage;

@SuppressWarnings("serial")
public class HomeServlet extends WebPage{

	public HomeServlet(String _htmlFilePath) {
		super(_htmlFilePath, WebPage.auth_type.AUTH_ALLOW_ALL);
		
	}
	
}

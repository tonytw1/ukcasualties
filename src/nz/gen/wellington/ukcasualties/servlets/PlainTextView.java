package nz.gen.wellington.ukcasualties.servlets;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

public class PlainTextView implements View {

	public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/xml");
        response.setCharacterEncoding("UTF8");
        
        response.getOutputStream().print((String) model.get("text"));
		response.getOutputStream().flush();
	}

	public String getContentType() {
		return "text/xml";
	}

}

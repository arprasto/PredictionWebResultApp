package wasdev.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.watson.scavenger.CloudantNoSQLDB.DBCommunicator;
import com.ibm.watson.scavenger.CloudantNoSQLDB.JSonDocumentTemplateClass;

@WebServlet("/ResultServlet")
public class ResultServlet extends HttpServlet 
{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("html");
		DBCommunicator dbsvc = (DBCommunicator)req.getServletContext().getAttribute("db_svc");
		StringBuffer javascript = new StringBuffer();
		StringBuffer htmlbody = new StringBuffer();
		StringBuffer wholepage = new StringBuffer();		
		wholepage.append("<html><title>Scavenger Image results</title>");
		javascript.append("<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js\"></script>"
				+ "<script type=\"text/javascript\">"
				+ "$(document).ready(function(){setInterval(function(){cache_clear()},"+req.getServletContext().getAttribute("web_page_refresh_interval")+"); }); function cache_clear(){ window.location.reload(true);}"
				+ "function loadJPG(){");
		htmlbody.append("<body onload=\"javascript:loadJPG();\"><div id=\"images\"><table>");
		List<JSonDocumentTemplateClass> lst = dbsvc.getAllIMGsBase64();
		if(lst.size()>0){
		for(JSonDocumentTemplateClass img_file:lst){
			javascript.append(""
					+ "var "+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf("."))+"_img64=\"data:image/jpg;base64,"+img_file.getImg_base64()+"\";"
					+ "var "+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf("."))+"_jpg=new Image();"
					+ img_file.getImg_id().substring(0,img_file.getImg_id().indexOf("."))+"_jpg.src = "+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf("."))+"_img64;"
					+ img_file.getImg_id().substring(0,img_file.getImg_id().indexOf("."))+"_jpg.style.height = '150px';"
					+ img_file.getImg_id().substring(0,img_file.getImg_id().indexOf("."))+"_jpg.style.width = '150px';"
					+ "document.getElementById('"+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf("."))+"_img').appendChild("+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf("."))+"_jpg);");
			htmlbody.append("<tr><div id=\""+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf("."))+"_div\">");
			String html_result = img_file.getImg_result_html().replaceAll("<html><body>","").replaceAll("</html></body>", "");
			htmlbody.append("<td style='width: 30%;'><div id=\""+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf("."))+"_img\" height=\"110\" width=\"110\"\"/></td>");
			htmlbody.append("<td>"+html_result+"</td>");
			htmlbody.append("</div></td></tr>");
		}}
		else{
			htmlbody.append("<h1>no images upload yet in DB");
		}
		javascript.append("}</script>");
		htmlbody.append("</body>");
		wholepage.append(javascript.toString()+htmlbody.toString()+"</html>");
		resp.getWriter().println(wholepage.toString());
	}
}

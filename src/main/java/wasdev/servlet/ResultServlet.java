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
		javascript.append(""
				+ "<script type=\"text/javascript\">"
				+ "var time = new Date().getTime();function refresh() { if(new Date().getTime() - time >= "+req.getServletContext().getAttribute("web_page_refresh_interval")+") window.location.reload(true); else setTimeout(refresh, 10000); } setTimeout(refresh, 10000);"
				+ "function loadJPG(){");
		
		List<JSonDocumentTemplateClass> scores_lst = dbsvc.getAllScores((String)req.getServletContext().getAttribute("app_id"));
		int total_score=0;
		String random_img_obj_str = null;
		for(JSonDocumentTemplateClass lst : scores_lst){
			total_score = total_score + lst.getScore();
			random_img_obj_str = lst.getRandom_img_obj_str();
		}
		
		htmlbody.append("<body onload=\"javascript:loadJPG();\">"
				+ "<h1>Total score : "+total_score+"</h1><br/><h1>Allowable Objs : "+random_img_obj_str+"</h1><br/>"
				+ "<div id=\"images\"><table>");

		List<JSonDocumentTemplateClass> lst = dbsvc.getAllIMGsBase64((String)req.getServletContext().getAttribute("app_id"));
		if(lst.size()>0){
		for(JSonDocumentTemplateClass img_file:lst){
			javascript.append(""
					+ "var "+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf(".")).replaceAll("-","_")+"_img64=\"data:image/jpg;base64,"+img_file.getImg_base64()+"\";"
					+ "var "+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf(".")).replaceAll("-","_")+"_jpg=new Image();"
					+ img_file.getImg_id().substring(0,img_file.getImg_id().indexOf(".")).replaceAll("-","_")+"_jpg.src = "+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf(".")).replaceAll("-","_")+"_img64;"
					+ img_file.getImg_id().substring(0,img_file.getImg_id().indexOf(".")).replaceAll("-","_")+"_jpg.style.height = '150px';"
					+ img_file.getImg_id().substring(0,img_file.getImg_id().indexOf(".")).replaceAll("-","_")+"_jpg.style.width = '150px';"
					+ "document.getElementById('"+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf(".")).replaceAll("-","_")+"_img').appendChild("+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf(".")).replaceAll("-","_")+"_jpg);");
			htmlbody.append("<tr><div id=\""+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf(".")).replaceAll("-","_")+"_div\">");
			String html_result = img_file.getImg_result_html().replaceAll("<html><body>","").replaceAll("</html></body>", "");
			htmlbody.append("<td style='width: 30%;'><div id=\""+img_file.getImg_id().substring(0,img_file.getImg_id().indexOf(".")).replaceAll("-","_")+"_img\" height=\"160\" width=\"160\"\"/></td>");
			htmlbody.append("<td>"+html_result+"</td>");
			htmlbody.append("</div></td></tr>");
		}}
		else{
			htmlbody.append("<h1>No images upload yet in DB");
		}
		javascript.append("}</script>");
		htmlbody.append("</body>");
		wholepage.append(javascript.toString()+htmlbody.toString()+"</html>");
		resp.getWriter().println(wholepage.toString());
	}
}

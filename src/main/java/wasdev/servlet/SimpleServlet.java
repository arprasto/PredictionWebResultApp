package wasdev.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.scavenger.CloudantNoSQLDB.DBCommunicator;
import com.ibm.watson.scavenger.util.PatchedCredentialUtils;
import com.ibm.watson.scavenger.visualrecognition.ImageAnalysis;

@WebServlet("/RootServlet")
public class SimpleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
	@Override
	public void init(ServletConfig config) throws ServletException {
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("properties/application.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String db_name = prop.getProperty("db_name","scavengerimagesdb").trim();
		if(db_name.equals(""))
		{
			db_name = "scavengerimagesdb";
		}
		
		String web_page_refresh_interval = prop.getProperty("web_page_refresh_interval","10000").trim();
		if(web_page_refresh_interval.equals(""))
		{
			web_page_refresh_interval = "10000";
		}
		
		String db_svc_plan = prop.getProperty("db_svc_plan","null").trim(),
				vr_svc_plan = prop.getProperty("vr_svc_plan","null").trim();
		if(db_svc_plan.equals("")) db_svc_plan="null";
		if(vr_svc_plan.equals("")) vr_svc_plan="null";
		
		config.getServletContext().setAttribute("db_name",db_name);
		config.getServletContext().setAttribute("db_svc_plan",db_svc_plan);
		config.getServletContext().setAttribute("vr_svc_plan",vr_svc_plan);
		config.getServletContext().setAttribute("web_page_refresh_interval",web_page_refresh_interval);
		
		
	}

	/**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String vr_svc_plan = null,db_svc_plan=null;
        if(request.getServletContext().getAttribute("vr_svc_plan").toString().equals("null"))
        	vr_svc_plan = null;
        if(request.getServletContext().getAttribute("db_svc_plan").toString().equals("null"))
        	db_svc_plan = null;
        
        String db_name = request.getServletContext().getAttribute("db_name").toString();
        System.out.println(db_name+"::::::::::::"+PatchedCredentialUtils.getDBuname(db_svc_plan)+"::::::::::::::::"+PatchedCredentialUtils.getDBpass(db_svc_plan)+":::::::::::"+PatchedCredentialUtils.getDBurl(db_svc_plan));
        
        request.getServletContext().setAttribute("vrr_svc",
        		new ImageAnalysis(PatchedCredentialUtils.getVRAPIKey(vr_svc_plan),
        				VisualRecognition.VERSION_DATE_2016_05_20,
        				PatchedCredentialUtils.getVRurl(vr_svc_plan)));
   		try {
   			request.getServletContext().setAttribute("db_svc",
   				new DBCommunicator(PatchedCredentialUtils.getDBuname(db_svc_plan),
   							PatchedCredentialUtils.getDBpass(db_svc_plan),
   							PatchedCredentialUtils.getDBurl(db_svc_plan),
   							db_name));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 		

        //response.getWriter().println((request.getServletContext().getAttribute("vrr_svc") instanceof ImageAnalysis));
        //response.getWriter().println((request.getServletContext().getAttribute("db_svc") instanceof DBCommunicator));
        
        /*for(JSonDocumentTemplateClass doc:PredictionApp.getInstance().dbsvc.getAllIMGsBase64())
        response.getWriter().print(doc.getImg_base64());*/
   		
   		RequestDispatcher reqd = request.getRequestDispatcher("/ResultServlet");
   		reqd.forward(request, response);
    }
}

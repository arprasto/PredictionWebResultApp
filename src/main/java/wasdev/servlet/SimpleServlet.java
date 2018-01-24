package wasdev.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.watson.WatsonVRTraining.CloudantNoSQLDB.DBCommunicator;
import com.ibm.watson.WatsonVRTraining.util.PatchedCredentialUtils;

@WebServlet("/RootServlet")
public class SimpleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Logger log = Logger.getLogger(SimpleServlet.class.getName());
    
	@Override
	public void init(ServletConfig config) throws ServletException {
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("properties/application.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			log.log(Level.SEVERE,"can not read properties file, continuing with default values");
			prop.put("db_name", "predictionresultdb");
			prop.put("web_page_refresh_interval","10000");
			prop.put("db_svc_plan","null");
			prop.put("vr_svc_plan","null");
		}
		String db_name = prop.getProperty("db_name","predictionresultdb").trim();
		if(db_name.equals(""))
		{
			db_name = "predictionresultdb";
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
   		System.out.println(request.getContextPath()+":"+request.getRequestURL()+":"+request.getLocalAddr()+":"+request.getRealPath("ResultServlet")+":"+request.getRemoteAddr()
   		+":"+request.getRemoteAddr()+":"+request.getRequestURI()+":"+request.getServletPath());
        response.setContentType("text/html");
        String vr_svc_plan = null,db_svc_plan=null;
        if(request.getServletContext().getAttribute("vr_svc_plan").toString().equals("null"))
        	vr_svc_plan = null;
        if(request.getServletContext().getAttribute("db_svc_plan").toString().equals("null"))
        	db_svc_plan = null;
        
        String db_name = request.getServletContext().getAttribute("db_name").toString();
        String app_id = request.getParameter("app_id_txt").trim();
        System.out.println(app_id+":::::::::::::::"+db_name+"::::::::::::"+PatchedCredentialUtils.getDBuname(db_svc_plan)+"::::::::::::::::"+PatchedCredentialUtils.getDBpass(db_svc_plan)+":::::::::::"+PatchedCredentialUtils.getDBurl(db_svc_plan));
        
   		try {
   			request.getServletContext().setAttribute("db_svc",
   				new DBCommunicator(PatchedCredentialUtils.getDBuname(db_svc_plan),
   							PatchedCredentialUtils.getDBpass(db_svc_plan),
   							PatchedCredentialUtils.getDBurl(db_svc_plan),
   							db_name));
   			request.getServletContext().setAttribute("app_id",app_id);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 		
   		
   		RequestDispatcher reqd = request.getRequestDispatcher("/ResultServlet");
   		reqd.forward(request, response);
    }
}

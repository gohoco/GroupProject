<html>
<body>
<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="error.jsp"%>

<%@page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@page import="Search.*" %>

    
<%
   if(request.getParameter("txtname") != null && request.getParameter("txtname") != "")
   {
        String input = request.getParameter("txtname");
        out.println("The results of <b>"+input+"</b> are:<hr/>");
        String stopwordtxt = application.getRealPath("/") + "stopwords.txt";
        SearchEngine se = new SearchEngine(stopwordtxt);
        Vector<String> result_id = new Vector<String>();
        result_id = se.search(input);
        if(result_id.size() > 0)
        {
            out.println("<table>");
            for(String r:result_id)
            {
                PageInfoStruct pis = se.getPageInfoStruct(r);
                Vector<InvertPosting> ip = se.getTopFiveFeqInvertPosting(r);
                Vector<String> pl = se.getParentLink(r);
                Vector<String> cl = se.getChildLink(r);
                
                
                out.println("<tr><td valign=\"top\">"+se.getScore(r)+"</td>");
                out.println("<td>");
                
                out.println("<a href=\""+pis.getURL()+"\"> "+pis.getTitle()+"</a><br/>");
			    out.println("<a href=\""+pis.getURL()+"\"> "+pis.getURL()+"</a><br/>");
			    out.println(pis.getLastModification()+", "+pis.getPageSize()+"<br/>");
                
                for(InvertPosting in:ip)
                {
                    System.out.print(in.word_id +" "+in.freq+";");
                    out.print(in.word_id + " " + in.freq +"; ");
                }
                out.println("<br/>");
                
                for(String p:pl)
                    out.println(p + "<br/>");
                
                for(String c:cl)
				    out.println(c + "<br/>");
                
                out.println("<br/></td></tr>");
                
            }
            out.println("</table>");

        }
        else
        {
            out.println("No match result");    
        }
    
        
   }
   else
   {
        out.println("Invalid input!");
   }
%>
    
</body>
</html>
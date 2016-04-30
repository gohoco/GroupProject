<html>
<body>
<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="error.jsp"%>

<%@page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@page import="Search.*" %>

    
<%
   if(request.getParameter("txtname") != null && request.getParameter("txtname") != "")
   {
        out.println("The results are:<hr/>");
        String input = request.getParameter("txtname");
        String stopwordtxt = application.getRealPath("/") + "stopwords.txt";
        SearchEngine se = new SearchEngine(stopwordtxt);
        Vector<String> result_id = new Vector<String>();
        result_id = se.search(input);
        if(result_id.size() > 0)
        {
for(int i=0; i<result_id.size(); i++){
                                 
                                 
                                 
                                 
}

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
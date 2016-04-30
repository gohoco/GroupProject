<html>
<body>
<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="error.jsp"%>

<%@page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@page import="Search.*" %>

    
<%
   if(request.getParameter("txtname") != null)
   {
out.println("not null");
   
String file = application.getRealPath("/") + "stopwords.txt";
//SearchEngine se = new SearchEngine(file);
//se.search("egrnuoewrhgoer");
   
   
StopStem ss = new StopStem(file);
String temp = "ally";
temp = ss.stem("eating");
out.println("temp :"+temp);
out.println(ss.isStopWord("about"));
out.println(file);
File myFile = new File(file);
out.println(myFile.exists());
        
   }
   else
   {
        out.println("null");
   }
%>
    
</body>
</html>
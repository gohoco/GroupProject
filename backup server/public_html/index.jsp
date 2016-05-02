<html>
<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Result</title>

    <!-- Bootstrap Core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="css/agency.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
    <link href='https://fonts.googleapis.com/css?family=Kaushan+Script' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Droid+Serif:400,700,400italic,700italic' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Roboto+Slab:400,100,300,700' rel='stylesheet' type='text/css'>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    <!-- wow.js -->
    <link rel="stylesheet" href="css/animate.css">
    <script src="js/wow.min.js"></script>
    <script>
        new WOW().init();
    </script>

</head>
    
<body>
<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="error.jsp"%>

<%@page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@page import="Search.*" %>

    
<%
   if(request.getParameter("txtname") != null && request.getParameter("txtname") != "")
   {
        String input = request.getParameter("txtname");
        String stopwordtxt = application.getRealPath("/") + "stopwords.txt";
        SearchEngine se = new SearchEngine(stopwordtxt);
        Vector<String> result_id = new Vector<String>();
        result_id = se.search(input);

        out.println("Your searching history: ");
        
        HashSet<String> hs = new HashSet<String>();
        String cookie_string = "";
        Cookie ck[] = request.getCookies();
        if(ck == null || ck.length == 0)
        {
            out.println("This is your first search!");
        }
        else
        {
            for(int i = 0; i<ck.length ; i++)
            {
                if(ck[i].getName().equals("m_history"))
                {
                    cookie_string = ck[i].getValue();
                    break;
                }
                    //out.println("<a href=\"index.jsp?txtname="+ck[i].getValue()+"\">" + ck[i].getValue() + "</a>" +" | ");       
            }
            String[] cs_array = cookie_string.split("__");
            for(int i = 0; i<cs_array.length;i++)
                hs.add(cs_array[i]);
            for(String s:hs)
                out.println("<a href=\"index.jsp?txtname="+s+"\">" + s + "</a>" +" | ");

            out.println("<br/><hr/>");                            
        }
        Cookie newck = new Cookie("m_history",input+"__"+cookie_string);
        newck.setMaxAge(60*60);
        response.addCookie(newck);                               
                                       
        out.println("<p>There are <b>"+result_id.size() +"</b> result(s) about <b>"+input+"</b> are:<hr/></p>");
        if(result_id.size() > 0)
        {
            out.println("<div class=\"wow bounceInLeft\" >");
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
                
                String temp = "";
                for(InvertPosting in:ip)
                {
                    //System.out.print("<a href=\"index.jsp?txtname="+in.word_id+"\">" + in.word_id + "</a>" +" "+in.freq+";");
                    //out.print(in.word_id + " " + in.freq +"; ");
                    out.print("<a href=\"index.jsp?txtname="+in.word_id+"\">" + in.word_id + "</a>" +" "+in.freq+";");
                    temp += in.word_id + " ";
                }
                out.println("<br/>");
                
                for(String p:pl)
                    out.println(p + "<br/>");
                
                for(String c:cl)
				    out.println(c + "<br/>");
    
                out.println("<input type=\"button\" onclick=\"location.href='http://52.193.232.178:8080/intrasearch/index.jsp?txtname=" +temp+ "';\" value=\"Get similar pages\" >" + "</input>");
                
                out.println("<br/></td></tr>");
                
                
            }
            out.println("</table>");
            out.println("</div>");

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

    <!-- jQuery -->
    <script src="js/jquery.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="js/bootstrap.min.js"></script>

    <!-- Plugin JavaScript -->
    <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>
    <script src="js/classie.js"></script>
    <script src="js/cbpAnimatedHeader.js"></script>

    <!-- Contact Form JavaScript -->
    <script src="js/jqBootstrapValidation.js"></script>
    <!--<script src="js/contact_me.js"></script>-->

    <!-- Custom Theme JavaScript -->
    <script src="js/agency.js"></script>
</body>
</html>
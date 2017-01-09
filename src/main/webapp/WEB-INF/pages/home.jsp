<!-- mail: rohitw@amdocs.com -->
<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta name="viewport" content="initial-scale=1, maximum-scale=1">
        <script type="text/javascript" src="${pageContext.request.contextPath}/webjars/jquery/2.1.3/jquery.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.2-2/js/bootstrap.min.js"></script>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Home</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/style.css">
		<link rel='stylesheet' href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.2-2/css/bootstrap.min.css'>
        <script>
            jQuery(document).ready(function($)
            {
                $("#header").load("${pageContext.request.contextPath}/static/css/header.jsp");
                $("#footer").load("${pageContext.request.contextPath}/static/css/footer.html");
            });
        </script>
    </head>
    <body>
        <div id="header"></div>
        <div id="wrap">
            <!-- page contents start here -->
            <h1>Dev Home</h1>
            <p>
            ${message}<br/>
            <!-- page contents end here -->
        </div>
        <div id="footer"></div>
    </body>
</html>
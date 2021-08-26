<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true" session="true"%>
<%@ taglib prefix="ctg" uri="customtags"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title id = "page_title">
            <ctg:loc property="main.app_title"/>: <ctg:loc property="general.pages.error"/>
        </title>
        <link rel="icon" type="image/png" href="${contextPath}/images/favicon-200x200.png" sizes="200x200">
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/error.css" />
    </head>
    <body>
        <div id="container">
            <div id="content">
                <h1><ctg:loc property="general.labels.error_header"/></h1>
                <div class="error_msg"><ctg:loc property="general.messages.error500"/></div>
                <a href="${contextPath}" id="go_home" class="noselect">&rarr; &#127968; <ctg:loc property="general.labels.site_map"/> &larr;</a>
            </div>
        </div>
    </body>
</html>

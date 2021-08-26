<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true" session="true"%>
<%@ taglib prefix="c" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ctg" uri="customtags"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title id = "page_title">
            <ctg:loc property="main.app_title"/>: <ctg:loc property="general.pages.error"/>
        </title>
    </head>
    <body>
        <h1><ctg:loc property="general.labels.error_header"/></h1>
    </body>
</html>

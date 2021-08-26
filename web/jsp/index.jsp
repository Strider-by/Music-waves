<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page session="true" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><ctg:loc property="main.app_title"/></title>
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/site_map.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/access_denied_msg_box.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/messages.css" />
        <link rel="icon" type="image/png" href="${contextPath}/images/favicon-200x200.png" sizes="200x200">
        <script src="${contextPath}/js/site_map.js" charset="utf-8"></script>
        <script src="${contextPath}/js/messages.js" charset="utf-8"></script>
        <script>
            window.ctx = "${contextPath}";
            window.onload = function ()
            {
                init();
            };
        </script> 
    </head>
    <body>
        <c:choose>
            <c:when test="${ empty user }">
                <div id="not_logged_message_box">
                    <div id="not_logged_message">
                        <ctg:loc property="profile.changepassword.messages.not_logged"/>
                    </div>
                    <div id="not_logged_button_container">
                        <a href="${contextPath}/login"><ctg:loc property="not_logged_in.labels.goto_login_page"/></a> 
                        <a href=""><ctg:loc property="not_logged_in.labels.reload_page"/></a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div id="main">
                    <div class="outer_container">
                        <div class="inner_container">
                            <div class="map_block noselect" id="account_block">
                                <div class="map_item" id="personal_data_link"><ctg:loc property="general.pages.personal_data"/></div>
                                <div class="map_item" id="change_password_link"><ctg:loc property="general.pages.change_password"/></div>
                                <div class="map_item" id="logout_link"><ctg:loc property="general.labels.logout"/></div>
                            </div>
                            <c:if test="${ user.role != 'NOT_REGISTERED_USER' }">
                                <div class="map_block noselect" id="listen_block">
                                    <div class="map_item" id="music_search_link"><ctg:loc property="general.pages.music_search"/></div>
                                    <div class="map_item" id="my_playlists_link"><ctg:loc property="general.pages.my_playlists"/></div>
                                    <div class="map_item" id="listen_link"><ctg:loc property="general.pages.listen"/></div>
                                </div>
                            </c:if>
                            <c:if test="${ user.role == 'CURATOR' || user.role == 'ADMINISTRATOR' }">
                                <div class="map_block noselect" id="moderator_level_block">
                                    <div class="map_item" id="music_compound_link"><ctg:loc property="general.pages.music_compound"/></div>
                                    <div class="map_item invisible"></div>
                                    <div class="map_item invisible"></div>
                                </div>
                            </c:if>
                            <c:if test="${ user.role == 'ADMINISTRATOR' }">
                                <div class="map_block noselect" id="administrator_level_block">
                                    <div class="map_item" id="users_link"><ctg:loc property="general.pages.users"/></div>
                                    <div class="map_item invisible"></div>
                                    <div class="map_item invisible"></div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </body>
</html>

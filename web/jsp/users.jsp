<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page session="true" %>
<c:set var="avatarsDir" scope="page" value="${contextPath}/static/images/avatars/"/>

<html>
    <head>
        <title id = "page_title">
            <ctg:loc property="main.app_title"/>: <ctg:loc property="general.pages.users"/>
        </title>
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/users.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/messages.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/access_denied_msg_box.css" />
        <link rel="icon" type="image/png" href="${contextPath}/images/favicon-200x200.png" sizes="200x200">
        <script src="${contextPath}/js/long-press.js" charset="utf-8"></script>
        <script src="${contextPath}/js/users.js" charset="utf-8"></script>
        <script src="${contextPath}/js/messages.js" charset="utf-8"></script>
        <script>
            window.ctx = "${contextPath}";
            window.textbundle = {};
            window.onload = function () {
                run();
            };
            window.avatarPath = "${contextPath}/static/images/avatars/";

            window.textbundle = {};
            window.textbundle.notLoggedIn = "<ctg:loc property="general.messages.not_logged"/>";
            window.textbundle.requestFailed = "<ctg:loc property="general.messages.request_failed"/>";
            window.textbundle.invalidData = "<ctg:loc property="general.messages.server_side_verification_failed"/>";
            window.textbundle.insufficientRights = "<ctg:loc property="general.messages.insufficient_rights"/>";
            window.textbundle.serverSideError = "<ctg:loc property="general.messages.server_side_error"/>";
            window.textbundle.userNotFound = "<ctg:loc property="general.messages.user_not_found"/>";

            function parseRoleDatabaseEquivalent(code)
            {
                switch (code)
                {
                    case 0:
                        return "<ctg:loc property="general.roles.unregistered"/>";
                    case 1:
                        return "<ctg:loc property="general.roles.user"/>";
                    case 2:
                        return "<ctg:loc property="general.roles.curator"/>";
                    case 3:
                        return "<ctg:loc property="general.roles.administrator"/>";
                }
            }
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
            <c:when test="${ user.role != 'ADMINISTRATOR' }">
                <div id="insufficient_rights_message_box">
                    <div id="insufficient_rights_message">
                        <ctg:loc property="general.messages.insufficient_rights"/>
                    </div>
                    <div id="insufficient_rights_button_container">
                        <a href="${contextPath}/login"><ctg:loc property="not_logged_in.labels.goto_login_page"/></a> 
                        <a href=""><ctg:loc property="not_logged_in.labels.reload_page"/></a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="upper_menu menu">
                    <a href="./" id="goto_index_page" title="<ctg:loc property="general.labels.site_map"/>">&#127968;</a>
                </div>    



                <div id="main" class="flex">
                    <div id="outer_container">
                        <div class="form_name noselect">
                            <ctg:loc property="users.labels.form_name"/>
                        </div>
                        <div class ="flex">
                            <div class="flex">
                                <div id="search_area">
                                    <div>
                                        <div id="navigation">
                                            <div id="navigation_label">
                                                <ctg:loc property="general.labels.navigation"/>
                                            </div>
                                            <button class="navigation_button" id="first_page_button">&lt;&lt;</button> 
                                            <button class="navigation_button" id="previous_page_button">&lt;</button>
                                            <button class="navigation_button" id="next_page_button">&gt;</button>
                                            <span id="number_blog_divider">|</span>
                                            <input type="number" min="1" id="page_number" value ="1"> 
                                            <button class="navigation_button" id="load_list_button">&ldca; | &olarr;</button>
                                        </div>
                                        <div id="header_filter_block">
                                            <div id="header_subblock" class="flex">
                                                <input class="id_cell" tabindex="-1" value="id">
                                                <input class="email_cell" tabindex="-1" value="<ctg:loc property="general.labels.email"/>">
                                                <input class="nickname_cell" tabindex="-1" value="<ctg:loc property="general.labels.nickname"/>">
                                                <input class="role_cell" tabindex="-1" value="<ctg:loc property="general.labels.role"/>">
                                                <input class="date_cell" tabindex="-1" value="<ctg:loc property="general.labels.register_date"/>">
                                                <div class="button_cell">
                                                    <div class="exclamationmark bordered invisible" id="search_wrn">!</div>
                                                </div>
                                            </div>
                                            <div id="filter_subblock">
                                                <input class="id_cell" id="id_filter">
                                                <input class="email_cell" id="email_filter">
                                                <input class="nickname_cell" id="nickname_filter">
                                                <select class="role_cell" id="role_filter">
                                                    <option value="-1">&nbsp;</option>
                                                    <option value="0"><ctg:loc property="general.roles.unregistered"/></option>
                                                    <option value="1"><ctg:loc property="general.roles.user"/></option>
                                                    <option value="2"><ctg:loc property="general.roles.curator"/></option>
                                                    <option value="3"><ctg:loc property="general.roles.administrator"/></option>
                                                </select> 
                                                <input class="date_cell" id="date_filter">
                                                <button class="button_cell" id="clean_filter_fields_button">x</button>
                                            </div>
                                        </div>
                                        <hr class="divider">
                                        <div id="search_results">
                                        </div>

                                    </div>
                                </div>
                                <div id="user_profile" class="">
                                    <div id="avatar">
                                    </div>
                                    <div id="profile_data">
                                        <div id="profile_props">
                                            <div class="flex">
                                                <div class="left_upper_label">
                                                    id
                                                </div>
                                                <div class="read_access">
                                                    * <ctg:loc property="general.labels.read_only"/>
                                                </div>
                                            </div>
                                            <input id="profile_id" readonly>
                                            <div class="flex">
                                                <div class="left_upper_label">
                                                    <ctg:loc property="general.labels.email"/>
                                                </div>
                                                <div class="value_restraints" title="<ctg:loc property="general.messages.common_naming_rules"/>">
                                                    ?
                                                </div>
                                            </div>
                                            <input id="profile_email">
                                            <div class="flex">
                                                <div class="left_upper_label">
                                                    <ctg:loc property="general.labels.nickname"/>
                                                </div>
                                                <div class="value_restraints" title="<ctg:loc property="profile.personaldata.messages.nickname_body_hints"/>">
                                                    ?
                                                </div>
                                            </div>
                                            <input id="profile_nickname">
                                            <div class="flex">
                                                <div class="left_upper_label">
                                                    <ctg:loc property="general.labels.name"/>
                                                </div>
                                                <div class="read_access">
                                                    * <ctg:loc property="general.labels.read_only"/>
                                                </div>
                                            </div>
                                            <input id="profile_name" readonly>
                                            <div class="left_upper_label">
                                                <ctg:loc property="general.labels.role"/>
                                            </div>
                                            <select id="profile_role">
                                                <option value="0"><ctg:loc property="general.roles.unregistered"/></option>
                                                <option value="1"><ctg:loc property="general.roles.user"/></option>
                                                <option value="2"><ctg:loc property="general.roles.curator"/></option>
                                                <option value="3"><ctg:loc property="general.roles.administrator"/></option>
                                            </select>
                                        </div>
                                        <div id="profile_controls">
                                            <button id="save_button"><ctg:loc property="general.buttons.save"/></button>
                                            <div class="exclamationmark bordered invisible" id="update_wrn">!</div>
                                            <button id="cancel_button"><ctg:loc property="general.buttons.cancel"/></button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="bottom_menu menu">
                </div>
            </c:otherwise>
        </c:choose> 
    </body>
</html>

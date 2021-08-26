<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c"%>

<html>
    <head>
        <title id = "page_title">
            <ctg:loc property="main.app_title"/>: 
            <ctg:loc property="general.pages.change_password"/></title>
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/change_password.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/access_denied_msg_box.css" />
        <link rel="icon" type="image/png" href="${contextPath}/images/favicon-200x200.png" sizes="200x200">
        <script src="${contextPath}/js/change_password.js" charset="utf-8"></script>
        <script>window.ctx = "${contextPath}";</script>
    </head>
    <body>

        <c:choose>
            <c:when test="${ empty user }">
                <div id="not_logged_message_box">
                    <div id="not_logged_message">
                        <ctg:loc property="profile.changepassword.messages.not_logged"/>
                    </div>
                    <div id="not_logged_button_container">
                        <a id="goto_login__button" href="${contextPath}/login"><ctg:loc property="not_logged_in.labels.goto_login_page"/></a> 
                        <a id="reload_page__button" href=""><ctg:loc property="not_logged_in.labels.reload_page"/></a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="upper_menu menu">
                    <a href="./" id="goto_index_page" title="<ctg:loc property="general.labels.site_map"/>">&#127968;</a>
                </div>

                <div id="main">

                    <div id="form" class="main_container">
                        <div class="form_name noselect">
                            <ctg:loc property="profile.changepassword.labels.form_name"/>
                        </div>


                        <div class="form form_back" id="reg_part">
                            <form id="profile">
                                <label for="password_old" id="old_password_label" class="label noselect">
                                    <ctg:loc property="profile.changepassword.labels.old_password"/>
                                    <span id="password_old_star">*</span></label><br />
                                <input id="password_old" name="password" type="password" class="input_field">
                                <div class="dist_1"></div>
                                <label for="password" id="new_password_label" class="label noselect">
                                    <ctg:loc property="profile.changepassword.labels.new_password"/>
                                    <span id="password_new_star">*</span></label><br />
                                <input id="password" name="password" type="password" class="input_field" 
                                       placeholder="<ctg:loc property="profile.changepassword.placeholders.new_password"/>">
                                <div class="dist_1"></div>

                                <label for="password" id="new_password_label_repeated" class="label noselect">
                                    <ctg:loc property="profile.changepassword.labels.new_password_repeated"/> 
                                    <span id="password_repeat_star">*</span></label><br />
                                <input id="password2" name="password2" type="password" class="input_field" 
                                       placeholder="<ctg:loc property="profile.changepassword.placeholders.new_password_repeated"/>">
                                <div class="dist_1"></div>
                            </form>
                        </div>

                        <div id = "helper" class = "invisible"></div>
                        <div class="dist_3"></div>


                        <div class="form_control_area">
                            <div id="show_hidden_area_box">
                                <input id="show_hidden__checkbox" type="checkbox">
                                <label for="show_hidden__checkbox" id ="show_hidden_label" class = "noselect">
                                    <ctg:loc property="profile.changepassword.labels.show_hidden"/>
                                </label>
                            </div>
                            <button class="submit_button" id="confirm__button">
                                <ctg:loc property="profile.changepassword.buttons.confirm"/>
                            </button>
                        </div>
                    </div>


                </div>



                <form id ="messages" style="display: none">
                    <input id="empty_orig_password" value = "<ctg:loc property="profile.changepassword.messages.empty_old_password"/>">
                    <input id="check_orig_password" value = "<ctg:loc property="profile.changepassword.messages.invalid_old_password"/>">
                    <input id="check_new_password" value = "<ctg:loc property="profile.changepassword.messages.invalid_new_password"/>">
                    <input id="check_conf_password" value = "<ctg:loc property="profile.changepassword.messages.invalid_conf_password"/>">

                    <input id="not_logged_in" value = "<ctg:loc property="profile.changepassword.messages.not_logged"/>">
                    <input id="psw_successfully_changed" value = "<ctg:loc property="profile.changepassword.messages.success"/>">
                    <input id="request_failed" value = "<ctg:loc property="profile.changepassword.messages.request_failed"/>">
                </form>


                <div class="bottom_menu menu">

                </div>
            </c:otherwise>
        </c:choose> 
    </body>
</html>

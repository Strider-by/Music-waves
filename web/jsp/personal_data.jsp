<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page session="true" %>

<c:set var="userIsLogged" scope="page" value="${sessionScope.user != null}"/>
<c:set var="avatarsDir" scope="page" value="${contextPath}/static/images/avatars/"/>

<c:set var="role" scope="page" value="unregistered"/>

<%-- if we aren't logged - we actually can't see this page so checking if user is logged --%>
<%-- on working app not really neccessary, but for build and debug purposes we need this check to avoid error --%>
<c:if test = "${userIsLogged}">

    <%-- depending on if user has avatar set --%>
    <c:choose>
        <c:when test="${sessionScope.user.avatarFileName == null || empty sessionScope.user.avatarFileName}">
            <c:set var="avatarFileName" value="default-avatar"/>  
        </c:when>    
        <c:otherwise>
            <c:set var="avatarFileName" value="${sessionScope.user.avatarFileName}"/>
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${sessionScope.user.nickname != null && !empty sessionScope.user.nickname}">
            <c:set var="nickname" scope="page" value="${sessionScope.user.nickname}"/>
        </c:when>    
        <c:otherwise>
            <c:set var="nickname" scope="page" value="---"/>
        </c:otherwise>
    </c:choose> 

    <c:set var="userId" scope="page" value="${sessionScope.user.id}"/>
    <c:set var="role" scope="page" value="${sessionScope.user.role.propertyKey}"/>
    <c:set var="avatarPath" scope="page" value="${avatarsDir}${avatarFileName}"/>
    <c:set var="registerDate" scope="page" value="${sessionScope.user.registerDate}"/>
    <c:set var="email" scope="page" value="${sessionScope.user.email}"/>
    <c:set var="firstName" scope="page" value="${sessionScope.user.firstName}"/>
    <c:set var="lastName" scope="page" value="${sessionScope.user.lastName}"/>
    <c:set var="language" scope="page" value="${sessionScope.user.language.databaseEquivalent}"/>
    <c:set var="country" scope="page" value="${sessionScope.user.country.databaseEquivalent}"/>

    <%-- <c:set var="unknownSexSelected" scope="page" 
           value="${sessionScope.user.sex  == 'UNKNOWN'}"/>
    <c:set var="maleSexSelected" scope="page" 
           value="${sessionScope.user.sex  == 'MALE'}"/>
    <c:set var="femaleSexSelected" scope="page" 
           value="${sessionScope.user.sex  == 'FEMALE'}"/> --%>

    <c:if test = "${sessionScope.user.sex  == 'UNKNOWN'}">
        <c:set var="unknownSexChecked" value = "checked"/>
    </c:if>
    <c:if test = "${sessionScope.user.sex  == 'MALE'}">
        <c:set var="maleSexChecked" value = "checked"/>
    </c:if>
    <c:if test = "${sessionScope.user.sex  == 'FEMALE'}">
        <c:set var="femaleSexChecked" value = "checked"/>
    </c:if>


</c:if>



<html>
    <head>
        <title id = "page_title">
            <ctg:loc property="main.app_title"/>: 
            <ctg:loc property="general.pages.personal_data"/></title>
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/personal_data.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/access_denied_msg_box.css" />
        <link rel="icon" type="image/png" href="${contextPath}/images/favicon-200x200.png" sizes="200x200">
        <script src="${contextPath}/js/personal_data.js" charset="utf-8"></script>
        <script>
            window.ctx = "${contextPath}";
            window.avatarDir = "${avatarsDir}";
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
                <div class="upper_menu menu">
                    <a href="./" id="goto_index_page" title="<ctg:loc property="general.labels.site_map"/>">&#127968;</a>
                </div> 



                <div id="main">
                    <div class ="central_container">

                        <div class = "left_neighbour">
                            <div class ="profile_block avatar_block">
                                <div class ="avatar">
                                    <img id = "avatar_img" src="${avatarPath}" alt="your avatar" class = "avatar_pic">
                                </div>
                            </div>
                            <div class ="set_avatar_block">
                                <button id ="change_user_avatar" class ="submit_button"><ctg:loc property="profile.personaldata.buttons.set_avatar"/></button> 
                                <div class = "questionmark bordered" title="<ctg:loc property="profile.personaldata.messages.avatar_hints"/>">?</div>
                                <div class = "exclamationmark bordered invisible" id ="avatar_wrn">!</div>
                            </div>
                            <input type="file" name="avatar_input" id="avatar_input" style = "display: none">


                            <div class ="profile_block">
                                <label class="shortened_label noselect">
                                    <ctg:loc property="profile.personaldata.labels.nickname"/>
                                </label>
                                <div class="personal_data_property">
                                    <div class="nickname_value_block">
                                        <div>${nickname}</div>
                                        <div class="hash_symbol">#</div>
                                        <div class="nickname_postfix">${userId}</div>
                                    </div>
                                </div>
                                <div class = "questionmark" title="<ctg:loc property="profile.personaldata.messages.nickname_hints"/>">?</div>
                            </div>

                            <div class ="profile_block">
                                <label class="shortened_label noselect">
                                    <ctg:loc property="profile.personaldata.labels.role"/>
                                </label>
                                <div class="personal_data_property">
                                    <ctg:loc property="profile.personaldata.roles.${role}"/>
                                </div>
                                <div class = "questionmark" title="<ctg:loc property="profile.personaldata.messages.role_hints"/>">?</div>
                            </div>


                            <div class ="profile_block">
                                <label class="shortened_label noselect">
                                    <ctg:loc property="profile.personaldata.labels.register_date"/>
                                </label>
                                <div class="personal_data_property">
                                    ${registerDate}
                                </div>
                                <div class = "questionmark invisible">?</div>
                            </div>
                        </div>  

                        <div id="form" class="main_container">





                            <div class="form_name noselect">
                                <ctg:loc property="profile.personaldata.labels.form_name"/>
                            </div>


                            <div class="form form_back" id="reg_part">
                                <form id="personal_data" action="personal-data">
                                </form>    


                                <label for="email" class="label noselect">
                                    <ctg:loc property="profile.personaldata.labels.email"/>
                                </label>
                                <div class="flex">
                                    <div id="email" class="pseudo_input">
                                        ${email}
                                    </div>
                                    <div class = "questionmark bordered" title="<ctg:loc property="profile.personaldata.messages.email_hints"/>">?</div>
                                </div>
                                <div class="dist_1"></div>

                                <label for="nickname" class="label noselect">
                                    <ctg:loc property="profile.personaldata.labels.nickname_body"/>
                                    <span id="nickname_star">*</span>
                                </label>
                                <div class="flex">
                                    <input id="nickname" class="input_field" value="${nickname}" maxlength="15">
                                    <div class = "questionmark bordered" title="<ctg:loc property="profile.personaldata.messages.nickname_body_hints"/>">?</div>
                                </div>

                                <div class="dist_1"></div>

                                <label for="first_name" class="label noselect">
                                    <ctg:loc property="profile.personaldata.labels.first_name"/>
                                </label>
                                <div class="flex">
                                    <input id="first_name" class="input_field" value="${firstName}"  maxlength="40">
                                    <div class = "questionmark bordered" title="<ctg:loc property="profile.personaldata.messages.firstname_hints"/>">?</div>
                                </div>
                                <div class="dist_1"></div>

                                <label for="last_name" class="label noselect">
                                    <ctg:loc property="profile.personaldata.labels.last_name"/>
                                </label>
                                <div class="flex">
                                    <input id="last_name" class="input_field" value="${lastName}"  maxlength="40">
                                    <div class = "questionmark bordered" title="<ctg:loc property="profile.personaldata.messages.lastname_hints"/>">?</div>
                                </div>

                                <div class="dist_1"></div>

                                <label for="sex" class="label noselect">
                                    <ctg:loc property="profile.personaldata.labels.sex"/>
                                </label>
                                <div class ="pseudo_input">

                                    <label for="sex_unknown">
                                        <input type="radio" id="sex_unknown"
                                               name="sex" value="0" disabled ${unknownSexChecked}>
                                        <span><ctg:loc property="profile.personaldata.labels.sex.unknown"/></span>
                                    </label>


                                    <label for="sex_male">
                                        <input type="radio" id="sex_male"
                                               name="sex" value="1" ${maleSexChecked}>
                                        <span><ctg:loc property="profile.personaldata.labels.sex.male"/></span>
                                    </label>

                                    <label for="sex_female">
                                        <input type="radio" id="sex_female"
                                               name="sex" value="2" ${femaleSexChecked}>
                                        <span><ctg:loc property="profile.personaldata.labels.sex.female"/></span>
                                    </label>
                                </div>
                                <div class="dist_1"></div>

                                <label for="country" class="label noselect">
                                    <ctg:loc property="profile.personaldata.labels.country"/>
                                </label>
                                <select name="country" id="country" class = "select">
                                    <option selected disabled value="0"><ctg:loc property="profile.personaldata.select.country"/></option>
                                    <ctg:countries selectedOptionId="${country}"/>
                                </select>

                                <%--             <select name="country">
                                    <c:forEach items="${countries}" var="country">
                                        <option value="${country.code}" ${param.country eq country.code ? 'selected' : ''}>${country.name}</option>
                                    </c:forEach>
                                </select> --%>

                                <div class="dist_1"></div>

                                <label for="language" class="label noselect">
                                    <ctg:loc property="profile.personaldata.labels.language"/>
                                </label>
                                <select name="language" id="language" class = "select">
                                    <option selected disabled value="0"><ctg:loc property="profile.personaldata.select.language"/></option>
                                    <ctg:languages selectedOptionId="${language}"/>
                                </select>

                                <div class="dist_1"></div>



                            </div>

                            <div class="dist_3"></div>


                            <div class="form_control_area">

                                <div class = "exclamationmark bordered invisible" id="p_data_wrn">!</div>
                                <button class="submit_button" id="sava_data">
                                    <ctg:loc property="profile.personaldata.buttons.save"/>
                                </button>
                            </div>
                        </div>

                        <div class = "right_neighbour"></div>
                    </div>
                </div>



                <form id ="hints" style="display: none">
                    <input id="nickname_body_hints" value = "<ctg:loc property="profile.personaldata.messages.nickname_body_hints"/>">
                </form>

                <form id ="warnings" style="display: none">
                    <input id="avatar_warnings" value = "<ctg:loc property="profile.personaldata.warnings.avatar_warnings"/>">
                    <input id="nickname_body_warnings" value = "<ctg:loc property="profile.personaldata.warnings.nickname_body_warnings"/>">
                    <input id="name_warnings" value = "<ctg:loc property="profile.personaldata.warnings.name_warnings"/>">
                    <input id="lastname_warnings" value = "<ctg:loc property="profile.personaldata.warnings.lastname_warnings"/>">
                    <input id="not_logged" value = "<ctg:loc property="profile.personaldata.warnings.not_logged"/>">
                    <input id="request_failed" value = "<ctg:loc property="profile.personaldata.warnings.request_failed"/>">
                    <input id="invalid_data" value = "<ctg:loc property="profile.personaldata.warnings.server_side_check_failed"/>">
                </form>


                <div class="bottom_menu menu">

                </div>

            </c:otherwise>
        </c:choose> 
    </body>
</html>

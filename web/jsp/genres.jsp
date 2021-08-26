<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page session="true" %>



<html>
    <head>
        <title id = "page_title">
            <ctg:loc property="main.app_title"/>: 
            <ctg:loc property="genres.title"/></title>
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/genres.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/access_denied_msg_box.css" />
        <link rel="icon" type="image/png" href="${contextPath}/images/favicon-200x200.png" sizes="200x200">
        <script src="${contextPath}/js/genres.js" charset="utf-8"></script>
        <script>
            window.ctx = "${contextPath}";
            window.textbundle = {};
            window.textbundle.active = "<ctg:loc property="genres.labels.genre_activity_state.active"/>";
            window.textbundle.inactive = "<ctg:loc property="genres.labels.genre_activity_state.inactive"/>";
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
            <c:when test="${ user.role != 'ADMINISTRATOR' &&  user.role != 'CURATOR' }">
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

                <%-- TODO -> replace with proper upper menu tag --%>
                <div class="upper_menu menu">
                    <div class="upper_menu menu">1</div>
                    <div class="upper_menu1 menu">1</div>
                    <button id="btn_1">Profile</button>
                    <button id="btn_2">My music</button>
                    <button id="btn_3">func 3</button>

                </div>

                <div class="upper_menu menu">
                    <div class="upper_menu menu">1</div>
                    <div class="upper_menu1 menu">1</div>
                    <button id="btn_4">Security</button>
                    <button id="btn_5">Personal info</button>
                    <button id="btn_6">func 3</button>

                </div>   



                <div id="main">
                    <div id="searchbox">
                        <div class="search_controls step_away_bottom">
                            <div class="search_left_controls">
                                <div class="step_away_bottom area_inner_header"><ctg:loc property="genres.labels.search"/></div>
                                <div class="flex">
                                    <input id="search_field" class="step_away_bottom">
                                    <button class="step_away_left step_away_bottom" id="cleanse_search_field">x</button>
                                </div>
                                <div class="flex">
                                    <button id="search" class="step_away_bottom"><ctg:loc property="genres.buttons.search"/></button>
                                    <div class="exclamationmark step_away_left step_away_bottom invisible" id="search_warning">!</div>
                                </div>
                                <div class="search_options">
                                    <label>
                                        <input type="radio" name="search_type" value="0" checked>
                                        <span><ctg:loc property="genres.labels.search_type_contains"/></span>
                                    </label>
                                    <label>
                                        <input type="radio" name="search_type" value="1">
                                        <span><ctg:loc property="genres.labels.search_type_strict"/></span>
                                    </label>
                                </div> 

                                <div class="search_options">
                                    <label>
                                        <input type="radio" name="search_active" value="0" checked>
                                        <span><ctg:loc property="genres.labels.search_genre_activity_all"/></span>
                                    </label>
                                    <label>
                                        <input type="radio" name="search_active" value="1">
                                        <span><ctg:loc property="genres.labels.search_genre_activity_active"/></span>
                                    </label>
                                    <label>
                                        <input type="radio" name="search_active" value="2">
                                        <span><ctg:loc property="genres.labels.search_genre_activity_inactive"/></span>
                                    </label>

                                </div> 

                            </div>


                            <div class="search_right_controls">
                                <div class="step_away_bottom area_inner_header"><ctg:loc property="genres.labels.navigation"/></div>
                                <div id="navigation_area">
                                    <div id="page_number_area">
                                        <div><ctg:loc property="genres.labels.page"/></div><input type="number" min="1" value="1" id="page_number">
                                    </div>
                                    <div class="navigation">
                                        <div class="flex navigation_buttons">
                                            <button class="navigate_button" id="1st_page"><<</button>
                                            <button class="navigate_button" id="prev_page"><</button>
                                            <button class="navigate_button" id="next_page">></button><br/>
                                        </div>
                                    </div>

                                </div>

                            </div>
                        </div>

                        <div class="search_result step_away_bottom" id="search_result">
                        </div>

                        <div class="step_away_top area_external_header"><ctg:loc property="genres.labels.edit_existing"/></div>
                        <div class="edit_area flex" id="edit_area">
                            <div class="step_away_top step_away_left">
                                <div class="flex step_away_bottom">
                                    <div class="std_cell"><ctg:loc property="genres.labels.id"/></div>
                                    <input id="edit_id" class="instance_data_field" disabled>
                                </div>
                                <div class="flex step_away_bottom">
                                    <div class="std_cell"><ctg:loc property="genres.labels.name"/></div>
                                    <input id="edit_name" class="instance_data_field" maxlength="45">
                                    <div class="questionmark step_away_left" id="edit_block_name_tips" title="<ctg:loc property="genres.messages.genre_name_requirements"/>">?</div>
                                    <div class="exclamationmark step_away_left invisible" id="edit_block_name_warning">!</div>
                                </div>
                                <div class="flex">
                                    <div class="std_cell"><ctg:loc property="genres.labels.activity_state"/></div>
                                    <select size="1" id="edit_state" class="instance_data_field">
                                        <option disabled selected value = "-1"><ctg:loc property="genres.labels.activity_state"/></option>
                                        <option value="0"><ctg:loc property="genres.labels.genre_activity_state.active"/></option>
                                        <option value="1"><ctg:loc property="genres.labels.genre_activity_state.inactive"/></option>
                                    </select>
                                </div>
                            </div>
                            <div id="edit_buttons_block" class="step_away_top step_away_right">
                                <div class="keep_right">
                                    <div class="exclamationmark step_away_right step_away_bottom invisible" id="edit_block_save_warning">!</div>
                                    <button id="save_record_being_edited" class="edit_button block step_away_bottom">
                                        <ctg:loc property="genres.buttons.save"/>
                                    </button>
                                </div>
                                <button id="open_record_for_edit_button" class="edit_button block step_away_bottom invisible">
                                    not_used
                                </button>
                                <div class="keep_right">
                                    <button id="cancel_edit_button" class="edit_button block step_away_bottom">
                                        <ctg:loc property="genres.buttons.cancel_edit"/>
                                    </button>
                                </div>

                            </div>
                        </div>

                        <div class="step_away_top area_external_header"><ctg:loc property="genres.labels.create_new"/></div>
                        <div class="new_instance_area" id="new_instance_area">

                            <div class="step_away_top step_away_left">

                                <div class="flex step_away_bottom">
                                    <div class="std_cell"><ctg:loc property="genres.labels.name"/></div>
                                    <input class="instance_data_field" id="new_instance_name" maxlength="45">
                                    <div class="questionmark step_away_left" id="new_instance_block_name_tips"  title="<ctg:loc property="genres.messages.genre_name_requirements"/>">?</div>
                                    <div class="exclamationmark step_away_left invisible"  id="new_instance_block_name_warning">!</div>
                                </div>
                                <div class="flex step_away_bottom">
                                    <div class="std_cell"><ctg:loc property="genres.labels.activity_state"/></div>
                                    <select size="1" class="instance_data_field" id="new_instance_state">
                                        <option value="0"><ctg:loc property="genres.labels.genre_activity_state.active"/></option>
                                        <option value="1"><ctg:loc property="genres.labels.genre_activity_state.inactive"/></option>
                                    </select>
                                </div>
                            </div>
                            <div id="new_instance_buttons_block" class="step_away_top step_away_right">
                                <div class="flex">
                                    <div class="exclamationmark step_away_right step_away_bottom invisible" id="new_instance_block_create_warning">!</div>
                                    <button id="create_new_instance" class="edit_button block step_away_bottom">
                                        <ctg:loc property="genres.buttons.create"/>
                                    </button>
                                </div>
                                <div class="keep_right">
                                    <button id="cleanse_new_instance_block" class="edit_button block step_away_bottom">
                                        <ctg:loc property="genres.buttons.cleanse"/>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>


                <div class="bottom_menu menu">
                    bottom
                </div>




                <form id ="text_bundle" style="display: none">
                    <input id="not_logged" value = "<ctg:loc property="general.messages.not_logged"/>">
                    <input id="request_failed" value = "<ctg:loc property="general.messages.request_failed"/>">
                    <input id="invalid_data" value = "<ctg:loc property="general.messages.server_side_verification_failed"/>">
                    <input id="insufficient_rights" value = "<ctg:loc property="general.messages.insufficient_rights"/>">
                    <input id="server_side_error" value = "<ctg:loc property="general.messages.server_side_error"/>">
                    <input id="genre_name_already_in_use" value = "<ctg:loc property="genres.messages.name_already_in_use"/>">
                </form>

            </c:otherwise>
        </c:choose> 
    </body>
</html>

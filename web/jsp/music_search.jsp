<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page session="true" %>
<c:set var="avatarsDir" scope="page" value="${contextPath}/static/images/avatars/"/>

<html>
    <head>
        <title id = "page_title">
            <ctg:loc property="main.app_title"/>: <ctg:loc property="music_search.labels.form_name"/>
        </title>
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/music_search.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/access_denied_msg_box.css" />
        <link rel="icon" type="image/png" href="${contextPath}/images/favicon-200x200.png" sizes="200x200">
        <script src="${contextPath}/js/long-press.js" charset="utf-8"></script>
        <script src="${contextPath}/js/music_search.js" charset="utf-8"></script>
        <script>
            window.ctx = "${contextPath}";
            window.textbundle = {};
            window.onload = function () {
                run();
            };
            window.artistImagePath = "${contextPath}/static/images/artists/";
            window.albumImagePath = "${contextPath}/static/images/albums/";
            window.trackFilePath = "${contextPath}/static/tracks/";

            window.textbundle = {};
            window.textbundle.notLoggedIn = "<ctg:loc property="general.messages.not_logged"/>";
            window.textbundle.requestFailed = "<ctg:loc property="general.messages.request_failed"/>";
            window.textbundle.invalidData = "<ctg:loc property="general.messages.server_side_verification_failed"/>";
            window.textbundle.insufficientRights = "<ctg:loc property="general.messages.insufficient_rights"/>";
            window.textbundle.serverSideError = "<ctg:loc property="general.messages.server_side_error"/>";
            window.textbundle.userNotFound = "<ctg:loc property="general.messages.user_not_found"/>";

            window.textbundle.albums = "<ctg:loc property="general.labels.albums"/>";
            window.textbundle.artists = "<ctg:loc property="general.labels.artists"/>";
            window.textbundle.tracks = "<ctg:loc property="general.labels.tracks"/>";

            window.textbundle.album = "<ctg:loc property="general.labels.album"/>";
            window.textbundle.artist = "<ctg:loc property="general.labels.artist"/>";
            window.textbundle.track = "<ctg:loc property="general.labels.track"/>";
            window.textbundle.year = "<ctg:loc property="general.labels.year"/>";


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



                <div id="main" class="flex">
                    <div id="outer_container">
                        <div id ="header_container">
                            <div id="form_name_container">
                                <div class="form_name noselect">
                                    <ctg:loc property="music_search.labels.form_name"/>
                                </div>
                            </div>
                            <div id="music_player">
                                <audio id="player" class="undisplayable">
                                    <source src="">
                                </audio>
                                <div id="progress_container" class="undisplayable">
                                    <div id="progress_bar"></div>
                                </div>
                                <div id=sound_controls>
                                    <button id="decr_volume">-</button>
                                    <div id="sound_container">
                                        <div id="sound_bar"></div>
                                    </div>
                                    <button id="encr_volume">+</button>
                                </div>
                                <div id="timeScope">
                                    <div id="control_buttons">
                                        <button id="play_b">&#9658;</button>
                                        <button id="pause_b">&#10074;&#10074;</button>
                                        <button id="stop_b">&#11035;</button>
                                    </div>
                                    <div id="player_time">
                                        <span id="currentTime">0:00:00</span>
                                        <span class="invisible">&nbsp;|&nbsp;</span>
                                        <span class="invisible" id="totalTime">0:00:00</span>
                                    </div>
                                </div>

                            </div>
                        </div>
                        <div class ="flex">
                            <div class="flex">
                                <div id="search_area">
                                    <div id="search_input">
                                        <div id="controls">
                                            <div id="search_string_block">
                                                <input id="search_string">
                                                <button id="cleanse_search_button">&#9747;</button>
                                                <button id="execute_search_button" class="undisplayable">&#128269;</button>
                                            </div>
                                            <div id="search_pages_control_area">
                                                <input type="number" min="1" id="page_number" value="1">
                                                <button id="apply_page_number">&crarr; | &orarr;</button>
                                                <button id="goto_start_button">&LeftArrowBar;</button>
                                                <button id="goto_back_button">&slarr;</button>
                                                <button id="goto_forward_button">&srarr;</button>
                                                <button id="goto_end_button" class="undisplayable">&RightArrowBar;</button>
                                            </div>
                                        </div>
                                        <div id="items_found">
                                            <div class="search_subblock noselect" id="artsits_found"><ctg:loc property="general.labels.artists"/>: <span id="artists_found_val">0</span>
                                            </div>
                                            <div class="search_subblock noselect" id="albums_found"><ctg:loc property="general.labels.albums"/>: <span id="albums_found_val">0</span>
                                            </div>
                                            <div class="search_subblock noselect" id="tracks_found"><ctg:loc property="general.labels.tracks"/>: <span id="tracks_found_val">0</span>
                                            </div>
                                        </div>
                                        <div id="search_results">

                                        </div>
                                    </div>
                                </div>
                                <div id="playlists_area">
                                    <div>
                                        <div id="select_playlist_area"> 
                                            <div class="area_inner_header"><ctg:loc property="general.labels.playlists"/></div>
                                            <div class="flex">
                                                <input id="playlist_name_filter" maxlength="20">
                                                <button id="clean_playlists_filter">&#10005;</button>
                                                <button id="create_new_playlist">+</button>
                                                <div class="questionmark" 
                                                     title="<ctg:loc property="general.titles.entity_naming_limitation.20_symbols_max"/>">?</div>
                                            </div>
                                            <div class="flex">
                                                <select id="availible_playlists">
                                                </select>
                                                <button id="reload_playlists_list">&orarr;</button>
                                                <button id="use_playlist">&crarr;</button>
                                            </div>
                                        </div>
                                        <div id="tracks_list">
                                            <div class="area_inner_header" id="current_playlist_name">
                                                <span id="used_playlist_name"></span>
                                                <div id="used_playlist_id" class="undisplayable"></div>
                                                <div id="playlist_control_block">
                                                    <button id="save_playlist">&#128190;</button>
                                                    <button id="reload_playlist">&orarr;</button>
                                                    <button id="cleanse_playlist">&#9249;*</button>
                                                </div>
                                            </div>
                                            <div id="track_items">

                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <dialog id="dialog_window" class="message">
                    <div id="dialog_window_header">
                        <span id="close_dialog_window" class="noselect">&#10005;</span>
                    </div>
                    <div id="dialog_window_content_container">
                        <h3></h3>
                        <p></p>
                    </div>
                </dialog>
                <div class="bottom_menu menu">
                </div>
            </c:otherwise>
        </c:choose> 
    </body>
</html>

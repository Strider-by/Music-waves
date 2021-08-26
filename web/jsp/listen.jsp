<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page session="true" %>

<html>
    <head>
        <title id = "page_title">
            <ctg:loc property="main.app_title"/>: <ctg:loc property="general.pages.listen"/>
        </title>
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/listen.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/access_denied_msg_box.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/messages.css" />
        <link rel="icon" type="image/png" href="${contextPath}/images/favicon-200x200.png" sizes="200x200">
        <script src="${contextPath}/js/listen.js" charset="utf-8"></script>
        <script src="${contextPath}/js/messages.js" charset="utf-8"></script>
        <script>
            window.ctx = "${contextPath}";
            window.textbundle = {};
            window.onload = function () {
                init();
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
                    <div id="form_container">
                        <div id="playlists">
                            <div class="area_header noselect"><ctg:loc property="general.labels.playlist"/></div>
                            <select id="playlists_list"></select>
                            <div id="tracks"></div>
                        </div>
                        <div id="music_player_container">
                            <div id="music_player">
                                <audio id="player" class="undisplayable">
                                    <source src="">
                                </audio>
                                <div id="playlistId" class="undisplayable"></div>
                                <div id="playlistItemNumber" class="undisplayable"></div>

                                <div id="control_buttons">
                                    <div id="buttons_1">
                                        <button id="play_b">&#9658;</button>
                                        <button id="pause_b">&#10074;&#10074;</button>
                                        <button id="stop_b">&#11035;</button>
                                    </div>
                                    <div id="buttons_2">
                                        <button id="play_prev_b">&#10502;</button>
                                        <button id="play_next_b">&#10503;</button>
                                    </div>
                                </div>
                                <div id=sound_controls>
                                    <button id="decr_volume">-</button>
                                    <div id="sound_container">
                                        <div id="sound_bar"></div>
                                    </div>
                                    <button id="encr_volume">+</button>
                                </div>
                                <div id="progress_container">
                                    <div id="progress_bar"></div>
                                </div>
                                <div id="timeScope">

                                    <div id="player_time" class="noselect">
                                        <span id="currentTime">0:00:00</span>
                                        <span>&nbsp;|&nbsp;</span>
                                        <span id="totalTime">0:00:00</span>
                                    </div>
                                </div>

                                <div id="options">
                                    <div>
                                        <input type="checkbox" id="option_random">
                                        <label for="option_random" class="noselect"><ctg:loc property="music.labels.random"/></label>
                                    </div>
                                    <div>
                                        <input type="checkbox" id="option_repeat">
                                        <label for="option_repeat" class="noselect"><ctg:loc property="music.labels.repeat"/></label>
                                    </div>
                                </div>

                            </div>
                        </div>
                        <div id="track_props">
                            <div id="track_block">
                                <div class="area_header noselect"><ctg:loc property="general.labels.track"/></div>
                                <div id="track_name_block">
                                </div>
                            </div>
                            <div id="artist_block">
                                <div class="area_header noselect"><ctg:loc property="general.labels.artist"/></div>
                                <div class="image_container"></div>
                                <div class="prop_field"></div>
                            </div>
                            <div id="album_block">
                                <div class="area_header noselect"><ctg:loc property="general.labels.album"/></div>
                                <div class="image_container"></div>
                                <div class="prop_field"></div>
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

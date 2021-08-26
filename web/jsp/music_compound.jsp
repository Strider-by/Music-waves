<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page session="true" %>
<c:set var="artistsImgDir" scope="page" value="${contextPath}/static/images/artists/"/>
<c:set var="albumsImgDir" scope="page" value="${contextPath}/static/images/albums/"/>

<html>
    <head>
        <title id = "page_title">
            <ctg:loc property="main.app_title"/>: 
            <ctg:loc property="general.pages.music_compound"/></title>
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/music_compound_general.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/music_compound_tabs.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/access_denied_msg_box.css" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/messages.css" />
        <link rel="icon" type="image/png" href="${contextPath}/images/favicon-200x200.png" sizes="200x200">
        <script src="${contextPath}/js/long-press.js" charset="utf-8"></script>
        <script src="${contextPath}/js/messages.js" charset="utf-8"></script>
        <script src="${contextPath}/js/music_compound.js" charset="utf-8"></script>
        <script src="${contextPath}/js/music_compound_artists.js" charset="utf-8"></script>
        <script src="${contextPath}/js/music_compound_albums.js" charset="utf-8"></script>
        <script src="${contextPath}/js/music_compound_tracks.js" charset="utf-8"></script>
        <script>
            window.ctx = "${contextPath}";
            window.textbundle = {};
            window.artistsImgDir = "${artistsImgDir}";
            window.albumsImgDir = "${albumsImgDir}";
            window.labels = {};
            window.labels.name = "<ctg:loc property="general.labels.name"/>";

            window.textbundle.active = "<ctg:loc property="general.labels.activity_state.active"/>";
            window.textbundle.inactive = "<ctg:loc property="general.labels.activity_state.inactive"/>";
            window.textbundle.notLoggedIn = "<ctg:loc property="general.messages.not_logged"/>";
            window.textbundle.requestFailed = "<ctg:loc property="general.messages.request_failed"/>";
            window.textbundle.invalidData = "<ctg:loc property="general.messages.server_side_verification_failed"/>";
            window.textbundle.insufficientRights = "<ctg:loc property="general.messages.insufficient_rights"/>";
            window.textbundle.serverSideError = "<ctg:loc property="general.messages.server_side_error"/>";

            window.textbundle.nameAlreadyInUse = "<ctg:loc property="general.messages.name_already_in_use"/>";
            window.textbundle.invalidImageAttendantData = "<ctg:loc property="general.messages.invalid_image_attendant_data"/>";
            window.textbundle.invalidImage = "<ctg:loc property="general.messages.invalid_image"/>";
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
                <div class="upper_menu menu">
                    <a href="./" id="goto_index_page" title="<ctg:loc property="general.labels.site_map"/>">&#127968;</a>
                </div> 



                <div id="main" class="flex">
                    <div id="outer_container">
                        <div class="flex">
                            <div id="tabs_container">
                                <div id="artists_tab" class="tab noselect">
                                    <ctg:loc property="general.labels.artists"/>
                                </div>
                                <div id="albums_tab" class="tab noselect">
                                    <ctg:loc property="general.labels.albums"/>
                                </div>
                                <div id="tracks_tab" class="tab noselect">
                                    <ctg:loc property="general.labels.tracks"/>
                                </div>
                                <%-- <div id="track_properties" class="tab noselect">
                                    genre and tags
                                </div> --%>
                            </div>
                            <div id="tab_window">




                                <%-- ARTISTS TAB --%>
                                <%-- SEARCH AREA --%>
                                <div id="artists_tab_window" class="">
                                    <div class="flex">
                                        <div class="searchbox">
                                            <div class="search_controls">
                                                <%-- SEARCH CONTROLS --%>
                                                <div class="whitebordered_area maxheight">
                                                    <div class="area_inner_header"><ctg:loc property="general.labels.search"/></div>
                                                    <div class="indiv">
                                                        <div class="inside_search_controls">
                                                            <div class="">
                                                                <input class="search_field" id="artists_search_field">
                                                            </div> 
                                                            <div class="flex">
                                                                <button class="submit_button cleanse_search_field_button" id="cleanse_artist_search_field">&#10005;</button>
                                                            </div>
                                                        </div>
                                                        <div class="flex">
                                                            <button class="submit_button search_button button_100px" id="search_artists"><ctg:loc property="general.buttons.search"/></button>
                                                            <div class="exclamationmark invisible" id="artists_search_warning">!</div>
                                                        </div>
                                                        <div class="whiteboard" id="artist_search_type_area">
                                                            <label>
                                                                <input type="radio" name="artists_search_type" value="0" checked>
                                                                <span><ctg:loc property="general.labels.search_type_contains"/></span>
                                                            </label>
                                                            <label>
                                                                <input type="radio" name="artists_search_type" value="1">
                                                                <span><ctg:loc property="general.labels.search_type_strict"/></span>
                                                            </label>
                                                        </div> 

                                                        <div class="whiteboard" id="artist_activity_type_area">
                                                            <label>
                                                                <input type="radio" name="artists_search_active" value="0" checked>
                                                                <span><ctg:loc property="general.labels.search_activity_all"/></span>
                                                            </label>
                                                            <label>
                                                                <input type="radio" name="artists_search_active" value="1">
                                                                <span><ctg:loc property="general.labels.search_activity_active"/></span>
                                                            </label>
                                                            <label>
                                                                <input type="radio" name="artists_search_active" value="2">
                                                                <span><ctg:loc property="general.labels.search_activity_inactive"/></span>
                                                            </label>

                                                        </div> 
                                                    </div>

                                                </div>


                                                <div>
                                                    <%-- SEARCH NAVIGATION --%>
                                                    <div class="whitebordered_area maxheight">
                                                        <div class="area_inner_header"><ctg:loc property="general.labels.navigation"/></div>
                                                        <div class ="centered step_top_10px">
                                                            <div class="indiv">
                                                                <div class="page_number_area">
                                                                    <div class="whiten_background_label"><ctg:loc property="general.labels.page"/></div>
                                                                    <input type="number" min="1" value="1" class="page_number" id="artists_page_number">
                                                                </div>
                                                                <div class="navigation">
                                                                    <div class="flex navigation_buttons">
                                                                        <button class="navigate_button submit_button" id="1st_artist_page">&lt;&lt;</button>
                                                                        <button class="navigate_button submit_button" id="prev_artist_page">&lt;</button>
                                                                        <button class="navigate_button submit_button" id="next_artist_page">&gt;</button><br/>
                                                                    </div>
                                                                    <button class="submit_button search_results_reload_button" id="refresh_artists_list_button">&#x21ba;</button>
                                                                </div>

                                                            </div>

                                                        </div>
                                                    </div>
                                                </div>



                                            </div>
                                            <%-- SEARCH RESULTS --%>
                                            <div class="search_results">
                                                <div class="area_inner_header"><ctg:loc property="general.labels.search_results"/></div>
                                                <div  id="artists_search_results" class="indiv"></div>
                                            </div>
                                        </div>


                                        <%-- CREATE NEW ARTIST AREA --%>    
                                        <div class="databox">
                                            <div class="whitebordered_area new_instance_area">
                                                <div class="area_inner_header"><ctg:loc property="general.labels.create_new"/></div>

                                                <div class="new_instance_inner indiv">

                                                    <div class="new_instance_params">
                                                        <div class="instance_property_block"><div class="std_cell"><ctg:loc property="general.labels.name"/></div>
                                                            <input class="instance_data_field" maxlength="45" id="new_artist_name">
                                                            <div class="questionmark step_away_left"  title="<ctg:loc property="artists.messages.name_requirements"/>">?</div>
                                                        </div>
                                                        <div class="instance_property_block">
                                                            <div class="std_cell"><ctg:loc property="general.labels.activity_state"/></div>
                                                            <select size="1" class="instance_data_field" id="new_artist_state">
                                                                <option value="0"><ctg:loc property="general.labels.activity_state.active"/></option>
                                                                <option value="1"><ctg:loc property="general.labels.activity_state.inactive"/></option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <div class="new_instance_buttons">
                                                        <div class="flex button_row">
                                                            <div class="exclamationmark step_away_right step_away_bottom invisible" id="new_artist_block_create_warning">!</div>
                                                            <button id="create_new_artist_button" class="button_100px  submit_button">
                                                                <ctg:loc property="general.buttons.create"/>
                                                            </button>
                                                        </div>
                                                        <div class="keep_right button_row">
                                                            <button id="cleanse_new_artist_block_button" class="button_100px submit_button">
                                                                <ctg:loc property="general.buttons.cleanse"/>
                                                            </button>
                                                        </div>
                                                    </div>


                                                </div>
                                            </div> 




                                            <%-- EDIT ARTIST AREA --%>
                                            <div class="edit_instance_area undisplayable" id="edit_artist_area">
                                                <div class="area_inner_header"><ctg:loc property="general.labels.edit_existing"/></div>
                                                <div class="indiv">
                                                    <div class ="flex">
                                                        <div class ="image_border">
                                                            <img id = "artist_big_image">
                                                        </div>

                                                        <div class="keep_right button_row flex_1">
                                                            <div class="use_button_block">
                                                                <div class="flex">
                                                                    <div class="exclamationmark step_away_right step_away_bottom invisible" id="artist_edit_block_use_warning">!</div>
                                                                    <button id="use_artist_edit_button" class="button_100px submit_button">
                                                                        <ctg:loc property="general.buttons.use"/>
                                                                    </button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div class="set_image_block">
                                                        <button id ="upload_artist_image" class ="submit_button">
                                                            <ctg:loc property="general.buttons.upload_image"/></button> 
                                                        <div class="flex">
                                                            <div class = "questionmark bordered" title="<ctg:loc property="artists.messages.artist_image_hints"/>">?</div>
                                                            <div class = "exclamationmark bordered undisplayable" id ="artist_image_wrn">!</div>
                                                        </div>
                                                    </div>
                                                    <input type="file" id="artist_image_input" style = "display: none">    
                                                </div>
                                                <div class="edit_instance_inner indiv">
                                                    <div class="edit_instance_params">
                                                        <div class="instance_property_block"><div class="std_cell"><ctg:loc property="general.labels.id"/></div>
                                                            <input class="instance_data_field" id="edit_artist_id" disabled>
                                                        </div>
                                                        <div class="instance_property_block"><div class="std_cell"><ctg:loc property="general.labels.name"/></div>
                                                            <input class="instance_data_field" id="edit_artist_name"  maxlength="45">
                                                            <div class="questionmark step_away_left"  title="<ctg:loc property="artists.messages.name_requirements"/>">?</div>
                                                        </div>
                                                        <div class="instance_property_block">
                                                            <div class="std_cell"><ctg:loc property="general.labels.activity_state"/></div>
                                                            <select size="1" class="instance_data_field" id="edit_artist_state">
                                                                <option value="0"><ctg:loc property="general.labels.activity_state.active"/></option>
                                                                <option value="1"><ctg:loc property="general.labels.activity_state.inactive"/></option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <div class="edit_instance_buttons">
                                                        <div class="flex button_row">
                                                            <div class="exclamationmark step_away_right step_away_bottom invisible" id="artist_edit_block_save_warning">!</div>
                                                            <button id="save_artist_being_edited" class="button_100px  submit_button">
                                                                <ctg:loc property="general.buttons.save"/>
                                                            </button>
                                                        </div>
                                                        <div class="keep_right button_row">
                                                            <button id="cancel_artist_edit_button" class="button_100px submit_button">
                                                                <ctg:loc property="general.buttons.cancel_edit"/>
                                                            </button>
                                                        </div>
                                                        <div class="flex button_row">
                                                            <div class="questionmark step_away_right" title="<ctg:loc property="general.messages.instance_delete_hints"/>">?</div>
                                                            <button id="delete_artist_being_edited" class="button_100px  submit_button" data-long-press-delay="2000">
                                                                <ctg:loc property="general.buttons.delete"/>
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                    </div>        
                                </div>
                                <%-- ARTISTS TAB END --%>





                                <%-- ALBUMS TAB --%>
                                <%-- SEARCH AREA --%>
                                <div id="albums_tab_window" class="undisplayable">
                                    <div class="flex">
                                        <div class="searchbox">
                                            <div class="search_controls">
                                                <%-- SEARCH CONTROLS --%>
                                                <div class="whitebordered_area maxheight">
                                                    <div class="area_inner_header"><ctg:loc property="general.labels.search"/></div>
                                                    <div class="indiv">
                                                        <div class="inside_search_controls">
                                                            <div class="">
                                                                <div>
                                                                    <div class="left_upper_label"><ctg:loc property="general.labels.name"/></div>
                                                                    <input class="search_field" id="albums_search_field_name">
                                                                </div>
                                                                <div>
                                                                    <div class="left_upper_label"><ctg:loc property="general.labels.year"/></div>
                                                                </div>
                                                                <input class="search_field" id="albums_search_field_year">

                                                            </div>
                                                            <div class="flex">
                                                                <button class="submit_button cleanse_search_field_button" id="cleanse_album_search_field">&#10005;</button>
                                                            </div>
                                                        </div>
                                                        <div class="flex">
                                                            <button class="submit_button search_button button_100px" id="search_albums"><ctg:loc property="general.buttons.search"/></button>
                                                            <div class="exclamationmark invisible" id="albums_search_warning">!</div>
                                                        </div>

                                                    </div>

                                                </div>


                                                <div>
                                                    <%-- SEARCH NAVIGATION --%>
                                                    <div class="whitebordered_area maxheight">
                                                        <div class="area_inner_header"><ctg:loc property="general.labels.navigation"/></div>
                                                        <div class ="centered step_top_10px">
                                                            <div class="indiv">
                                                                <div class="page_number_area">
                                                                    <div class="whiten_background_label"><ctg:loc property="general.labels.page"/></div>
                                                                    <input type="number" min="1" value="1" class="page_number" id="albums_page_number">
                                                                </div>
                                                                <div class="navigation">
                                                                    <div class="flex navigation_buttons">
                                                                        <button class="navigate_button submit_button" id="1st_album_page">&lt;&lt;</button>
                                                                        <button class="navigate_button submit_button" id="prev_album_page">&lt;</button>
                                                                        <button class="navigate_button submit_button" id="next_album_page">&gt;</button><br/>
                                                                    </div>
                                                                    <button class="submit_button search_results_reload_button" id="refresh_albums_list_button">&#x21ba;</button>
                                                                </div>

                                                            </div>

                                                        </div>
                                                    </div>
                                                </div>



                                            </div>
                                            <%-- SEARCH RESULTS --%>
                                            <div class="search_results">
                                                <div class="area_inner_header"><ctg:loc property="general.labels.search_results"/></div>
                                                <div  id="albums_search_results" class="indiv"></div>
                                            </div>
                                        </div>


                                        <%-- CREATE NEW ALBUM AREA --%>    
                                        <div class="databox">
                                            <div class="whitebordered_area new_instance_area">
                                                <div class="area_inner_header"><ctg:loc property="general.labels.create_new"/></div>

                                                <div class="new_instance_inner indiv">

                                                    <div class="new_instance_params">
                                                        <div class="instance_property_block"><div class="std_cell"><ctg:loc property="general.labels.name"/></div>
                                                            <input class="instance_data_field" id="new_album_name"  maxlength="45">
                                                            <div class="questionmark step_away_left"  title="<ctg:loc property="general.messages.common_naming_rules"/>">?</div>
                                                        </div>
                                                        <div class="instance_property_block"><div class="std_cell"><ctg:loc property="general.labels.year"/></div>
                                                            <input class="instance_data_field" type="number" id="new_album_year" value="2000">
                                                        </div>
                                                        <div class="instance_property_block">
                                                            <div class="std_cell"><ctg:loc property="general.labels.activity_state"/></div>
                                                            <select size="1" class="instance_data_field" id="new_album_state">
                                                                <option value="0"><ctg:loc property="general.labels.activity_state.active"/></option>
                                                                <option value="1"><ctg:loc property="general.labels.activity_state.inactive"/></option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <div class="new_instance_buttons">
                                                        <div class="flex button_row">
                                                            <div class="exclamationmark step_away_right step_away_bottom invisible" id="new_album_block_create_warning">!</div>
                                                            <button id="create_new_album_button" class="button_100px  submit_button">
                                                                <ctg:loc property="general.buttons.create"/>
                                                            </button>
                                                        </div>
                                                        <div class="keep_right button_row">
                                                            <button id="cleanse_new_album_block_button" class="button_100px submit_button">
                                                                <ctg:loc property="general.buttons.cleanse"/>
                                                            </button>
                                                        </div>
                                                    </div>


                                                </div>
                                            </div> 




                                            <%-- EDIT ALBUM AREA --%>
                                            <div class="edit_instance_area undisplayable" id="edit_album_area">
                                                <div class="area_inner_header"><ctg:loc property="general.labels.edit_existing"/></div>
                                                <div class="indiv">
                                                    <div class ="flex">
                                                        <div class ="image_border">
                                                            <img id = "album_big_image">
                                                        </div>
                                                        <div class="keep_right button_row flex_1">
                                                            <div class="use_button_block">
                                                                <div class="flex">
                                                                    <div class="exclamationmark step_away_right step_away_bottom invisible" id="album_edit_block_use_warning">!</div>
                                                                    <button id="use_album_edit_button" class="button_100px submit_button">
                                                                        <ctg:loc property="general.buttons.use"/>
                                                                    </button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="set_image_block">
                                                        <button id ="upload_album_image" class ="submit_button">
                                                            <ctg:loc property="general.buttons.upload_image"/></button> 
                                                        <div class="flex">
                                                            <div class = "questionmark bordered" title="<ctg:loc property="general.messages.image_hints"/>">?</div>
                                                            <div class = "exclamationmark bordered undisplayable" id ="album_image_wrn">!</div>
                                                        </div>
                                                    </div>
                                                    <input type="file" id="album_image_input" style = "display: none">    
                                                </div>
                                                <div class="edit_instance_inner indiv">
                                                    <div class="edit_instance_params">
                                                        <div class="instance_property_block"><div class="std_cell"><ctg:loc property="general.labels.id"/></div>
                                                            <input class="instance_data_field" id="edit_album_id" disabled>
                                                        </div>
                                                        <div class="instance_property_block"><div class="std_cell"><ctg:loc property="general.labels.name"/></div>
                                                            <input class="instance_data_field" id="edit_album_name"  maxlength="45">
                                                            <div class="questionmark step_away_left"  title="<ctg:loc property="general.messages.common_naming_rules"/>">?</div>
                                                        </div>
                                                        <div class="instance_property_block"><div class="std_cell"><ctg:loc property="general.labels.year"/></div>
                                                            <input class="instance_data_field" type="number" id="edit_album_year">
                                                        </div>
                                                        <div class="instance_property_block">
                                                            <div class="std_cell"><ctg:loc property="general.labels.activity_state"/></div>
                                                            <select size="1" class="instance_data_field" id="edit_album_state">
                                                                <option value="0"><ctg:loc property="general.labels.activity_state.active"/></option>
                                                                <option value="1"><ctg:loc property="general.labels.activity_state.inactive"/></option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <div class="edit_instance_buttons">
                                                        <div class="flex button_row">
                                                            <div class="exclamationmark step_away_right step_away_bottom invisible" id="album_edit_block_save_warning">!</div>
                                                            <button id="save_album_being_edited" class="button_100px  submit_button">
                                                                <ctg:loc property="general.buttons.save"/>
                                                            </button>
                                                        </div>
                                                        <div class="keep_right button_row">
                                                            <button id="cancel_album_edit_button" class="button_100px submit_button">
                                                                <ctg:loc property="general.buttons.cancel_edit"/>
                                                            </button>
                                                        </div>
                                                        <div class="flex button_row">
                                                            <div class="questionmark step_away_right" title="<ctg:loc property="general.messages.instance_delete_hints"/>">?</div>
                                                            <button id="delete_album_being_edited" class="button_100px  submit_button" data-long-press-delay="2000">
                                                                <ctg:loc property="general.buttons.delete"/>
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                    </div>        
                                </div>
                                <%-- ALBUMS TAB END --%>

                                <%-- TRACKS TAB --%>
                                <%-- SEARCH AREA --%>
                                <div id="tracks_tab_window">
                                    <div class="flex">
                                        <div class="searchbox">

                                            <%-- SEARCH RESULTS --%>
                                            <div class="tracks_list">
                                                <div class="area_inner_header">tmp:: Tracks list</div>
                                                <div  id="tracks_search_results" class="indiv"></div>
                                            </div>
                                        </div>


                                        <%-- CREATE NEW TRACK AREA --%>    
                                        <div class="databox">
                                            <div class="whitebordered_area new_instance_area">
                                                <div class="area_inner_header"><ctg:loc property="general.labels.create_new"/></div>

                                                <div class="new_instance_inner indiv">

                                                    <div class="new_instance_params">
                                                        <div class="instance_property_block"><div class="std_cell"><ctg:loc property="general.labels.name"/></div>
                                                            <input class="instance_data_field" id="new_track_name"  maxlength="45">
                                                            <div class="questionmark step_away_left"  title="<ctg:loc property="general.messages.common_naming_rules"/>">?</div>
                                                        </div>
                                                        <div class="left_upper_label undisplayable"><ctg:loc property="general.labels.genre"/></div>
                                                        <div class="genre_selection undisplayable">
                                                            <div class="instance_property_block">
                                                                <input class="instance_data_field" id="new_track_genre_filter" value="">
                                                            </div>
                                                            <div class="instance_property_block">
                                                                <select size="1" class="instance_data_field" id="new_track_genre">
                                                                    <option value="0"><ctg:loc property="general.labels.activity_state.active"/></option>
                                                                    <option value="1"><ctg:loc property="general.labels.activity_state.inactive"/></option>
                                                                </select>
                                                            </div>
                                                        </div>
                                                        <div class="instance_property_block">
                                                            <div class="std_cell"><ctg:loc property="general.labels.activity_state"/></div>
                                                            <select size="1" class="instance_data_field" id="new_track_state">
                                                                <option value="0"><ctg:loc property="general.labels.activity_state.active"/></option>
                                                                <option value="1"><ctg:loc property="general.labels.activity_state.inactive"/></option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <div class="new_instance_buttons">
                                                        <div class="flex button_row">
                                                            <div class="exclamationmark step_away_right step_away_bottom invisible" id="new_track_block_create_warning">!</div>
                                                            <button id="create_new_track_button" class="button_100px  submit_button">
                                                                <ctg:loc property="general.buttons.create"/>
                                                            </button>
                                                        </div>
                                                        <div class="keep_right button_row">
                                                            <button id="cleanse_new_track_block_button" class="button_100px submit_button">
                                                                <ctg:loc property="general.buttons.cleanse"/>
                                                            </button>
                                                        </div>
                                                    </div>


                                                </div>
                                            </div> 




                                            <%-- EDIT TRACK AREA --%>
                                            <div class="edit_instance_area undisplayable" id="edit_track_area">
                                                <div class="area_inner_header"><ctg:loc property="general.labels.edit_existing"/></div>

                                                <div class="edit_instance_inner indiv">
                                                    <div id="track_editable_props_block">
                                                        <div class="flex">
                                                            <div class="edit_instance_params">
                                                                <div class="instance_property_block"><div class="std_cell"><ctg:loc property="general.labels.id"/></div>
                                                                    <input class="instance_data_field" id="edit_track_id" disabled>
                                                                </div>
                                                                <div class="instance_property_block"><div class="std_cell"><ctg:loc property="general.labels.name"/></div>
                                                                    <input class="instance_data_field" id="edit_track_name"  maxlength="45">
                                                                    <div class="questionmark step_away_left"  title="<ctg:loc property="general.messages.common_naming_rules"/>">?</div>
                                                                </div>
                                                                <div class="left_upper_label undisplayable"><ctg:loc property="general.labels.genre"/></div>
                                                                <div class="genre_selection undisplayable">
                                                                    <div class="instance_property_block">
                                                                        <input class="instance_data_field" id="edit_track_genre_filter" value="">
                                                                    </div>
                                                                    <div class="instance_property_block">
                                                                        <select size="1" class="instance_data_field" id="edit_track_genre">
                                                                        </select>
                                                                    </div>
                                                                </div>
                                                                <div class="instance_property_block">
                                                                    <div class="std_cell"><ctg:loc property="general.labels.activity_state"/></div>
                                                                    <select size="1" class="instance_data_field" id="edit_track_state">
                                                                        <option value="0"><ctg:loc property="general.labels.activity_state.active"/></option>
                                                                        <option value="1"><ctg:loc property="general.labels.activity_state.inactive"/></option>
                                                                    </select>
                                                                </div>
                                                            </div>

                                                            <div class="edit_instance_buttons">
                                                                <div class="flex button_row">
                                                                    <div class="exclamationmark step_away_right step_away_bottom invisible" id="track_edit_block_save_warning">!</div>
                                                                    <button id="save_track_being_edited" class="button_100px  submit_button">
                                                                        <ctg:loc property="general.buttons.save"/>
                                                                    </button>
                                                                </div>
                                                                <div class="keep_right button_row">
                                                                    <button id="cancel_track_edit_button" class="button_100px submit_button">
                                                                        <ctg:loc property="general.buttons.cancel_edit"/>
                                                                    </button>
                                                                </div>
                                                                <div class="flex button_row">
                                                                    <div class="questionmark step_away_right" title="<ctg:loc property="general.messages.instance_delete_hints"/>">?</div>
                                                                    <button id="delete_track_being_edited" class="button_100px  submit_button" data-long-press-delay="2000">
                                                                        <ctg:loc property="general.buttons.delete"/>
                                                                    </button>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="hr"><hr></div>

                                                        <div class="track_number_block">
                                                            <div class="instance_property_block">
                                                                <div class="std_cell">
                                                                    #
                                                                </div>
                                                                <input class="instance_data_field"  type="number" id="edit_track_number" disabled>
                                                                <div class = "exclamationmark bordered undisplayable step_away_left" id ="track_number_wrn">!</div>
                                                            </div>
                                                            <div class="flex">


                                                            </div>
                                                        </div>
                                                        <div id="change_track_number_buttons">
                                                            <button id="shift_track_number_up_button"><ctg:loc property="tracks.buttons.shift_up"/></button>
                                                            <button id="shift_track_number_down_button"><ctg:loc property="tracks.buttons.shift_down"/></button>
                                                        </div>

                                                        <div class="set_track_file_block">
                                                            <div class="instance_property_block">
                                                                <div class="std_cell">
                                                                    <ctg:loc property="general.labels.file"/>
                                                                </div>
                                                                <input class="instance_data_field" id="edit_track_filename" disabled>
                                                            </div>
                                                            <div class="flex">
                                                                <button id ="upload_track_file" class ="submit_button">
                                                                    <ctg:loc property="general.buttons.upload"/>
                                                                </button>
                                                                <div class = "questionmark bordered step_away_left" title="<ctg:loc property="tracks.messages.track_file_hints"/>">?</div>
                                                                <div class = "exclamationmark bordered undisplayable" id ="track_file_wrn">!</div>
                                                            </div>
                                                            <input type="file" id="track_file_input" style = "display: none"> 
                                                        </div>








                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                    </div>        
                                </div>

                                <%-- TRACKS TAB END --%>

                                <%-- TRACK PROPS TAB --%>
                                <%--<div id="track_properties_tab_window"></div> --%>
                                <%-- TRACK PROPS TAB END --%>
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

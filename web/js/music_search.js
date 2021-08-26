///////////////////////
/////   General   /////
///////////////////////

function run()
{
    setFormControlListeners();
    requestSearchResultsQuantity();
    requestUserPlaylists();
    activatePageControl();

    window.MessageType = Object.freeze({message: {}, warning: {}, error: {}});
}

function setFormControlListeners()
{
    document.getElementById("execute_search_button").onclick = requestSearchResultsQuantity;
    document.getElementById("artsits_found").onclick =
            function ()
            {
                setPageNumberAs1();
                activatePageControl("artists");
                requestSearchResultsQuantityAndArtistsList();
            };
    document.getElementById("albums_found").onclick =
            function ()
            {
                setPageNumberAs1();
                activatePageControl("albums");
                requestSearchResultsQuantityAndAlbumsList();
            };
    document.getElementById("tracks_found").onclick =
            function ()
            {
                setPageNumberAs1();
                activatePageControl("tracks");
                requestSearchResultsQuantityAndTracksList();
            };
    document.getElementById("cleanse_search_button").onclick =
            function ()
            {
                clearSearchResultsArea();
                clearSearchFillterFields();
                requestSearchResultsQuantity();
                window.pageControl.action = null;
                setPageNumberAs1();
            };
    document.getElementById("search_string").oninput =
            function ()
            {
                clearSearchResultsArea();
                requestSearchResultsQuantity();
            };
    document.getElementById("reload_playlists_list").addEventListener('click',
            function ()
            {
                requestUserPlaylists();
            });
    document.getElementById("playlist_name_filter").addEventListener('input',
            function ()
            {
                filterPlaylistsList();
            });
    document.getElementById("clean_playlists_filter").addEventListener('click',
            function ()
            {
                document.getElementById("playlist_name_filter").value = "";
                filterPlaylistsList();
            });
    document.getElementById("use_playlist").addEventListener('click',
            function ()
            {
                let selection = document.getElementById("availible_playlists");
                let playlistId = selection.value;
                if (playlistId !== undefined && playlistId != -1)
                {
                    let playlistName = selection.options[selection.selectedIndex].text;
                    usePlaylist(playlistId, playlistName);
                }
            });
    document.getElementById("reload_playlist").addEventListener('click',
            function ()
            {
                reloadPlaylist();
            });
    document.getElementById("cleanse_playlist").addEventListener('click',
            function ()
            {
                cleanPlaylistTracksArea();
            });
    document.getElementById("save_playlist").addEventListener('click',
            function ()
            {
                savePlaylist();
            });
    document.getElementById("create_new_playlist").addEventListener('click',
            function ()
            {
                createPlaylist();
            });

    document.getElementById("close_dialog_window").addEventListener("click",
            function ()
            {
                document.getElementById("dialog_window").close();
            });

    window.pageControl = {};

    //// music ////

    const audio = document.getElementById("player");
    const playButton = document.getElementById("play_b");
    const pauseButton = document.getElementById("pause_b");
    const stopButton = document.getElementById("stop_b");
    const currentTime = document.getElementById("currentTime");
    const totalTime = document.getElementById("totalTime");
    const progressContainer = document.getElementById("progress_container");
    const progressBar = document.getElementById("progress_bar");
    const soundContainer = document.getElementById("sound_container");
    const soundBar = document.getElementById("sound_bar");
    const plusVolumeButton = document.getElementById("encr_volume");
    const minusVolumeButton = document.getElementById("decr_volume");
    playButton.onclick = function () {
        showTotalTime();
        audio.play();
    };
    pauseButton.onclick = function () {
        audio.pause();
    };
    stopButton.addEventListener("click", function () {
        audio.pause();
        audio.currentTime = 0;
    });
    plusVolumeButton.addEventListener("click", function () {
        if (audio.volume < .9)
        {
            audio.volume += 0.1;
        } else
        {
            audio.volume = 1;
        }
    });
    minusVolumeButton.addEventListener("click", function () {
        if (audio.volume > 0.1)
        {
            audio.volume -= 0.1;
        } else
        {
            audio.volume = 0;
        }
    });
    audio.addEventListener("timeupdate", function () {
        currentTime.innerHTML = secondsToHMS(audio.currentTime);
        progressBar.style.width = (audio.currentTime / audio.duration) * 100 + "%";
    });
    audio.addEventListener("volumechange", function () {
        soundBar.style.width = (audio.volume) * 100 + "%";
        recordVolumeLevelChange();
    });
    audio.addEventListener("loadedmetadata", function () {
        showTotalTime();
    });


// seems fine but is buggy
// 
//    progressContainer.addEventListener("click", function (e) {
//        let target = e.target || e.srcElement;
//
//        let clickPosition = (e.pageX - target.getBoundingClientRect().left) / target.offsetWidth;
//        let clickTime = clickPosition * audio.duration;
//        if (isFinite(clickTime))
//            audio.currentTime = clickTime;
//    });
//
//    soundContainer.addEventListener("click", function (e) {
//        /*let volume = (e.pageX - this.offsetLeft) / this.offsetWidth;
//         audio.volume = volume;*/
//
//        let target = e.target || e.srcElement;
//        //alert(e.pageX + " | " + target.getBoundingClientRect().left + " | " + target.offsetWidth);
//        /*let volume = (target.pageX - target.offsetLeft) / target.getBoundingClientRect();
//         audio.volume = volume;*/
//        audio.volume = (e.pageX - target.getBoundingClientRect().left) / target.offsetWidth;
//    });


    function showTotalTime()
    {
        let duration = audio.duration;
        if (!isNaN(duration) && isFinite(duration))
        {
            totalTime.innerHTML = secondsToHMS(audio.duration);
        } else
        {
            setTimeout(showTotalTime, 100);
        }
    }

    function secondsToHMS(secondsTotal)
    {
        let hours = Math.floor(secondsTotal / (60 * 60));
        let minutes = Math.floor(secondsTotal / (60)) - hours * 60;
        let seconds = Math.floor(secondsTotal - hours * 60 * 60 - minutes * 60);
        let minutesStr = minutes > 9 ? minutes : "0" + minutes;
        let secondsStr = seconds > 9 ? seconds : "0" + seconds;
        let result = hours + ":" + minutesStr + ":" + secondsStr;
        return result;
    }

    function recordVolumeLevelChange()
    {
        setCookie("volume", audio.volume);
    }

    function loadVolumeLevelChange()
    {
        let cookieVolumeLvl = getCookie("volume");
        if (cookieVolumeLvl != undefined && cookieVolumeLvl != null)
        {
            audio.volume = cookieVolumeLvl;
        }
    }

    // init volume level from cookies
    loadVolumeLevelChange();
}

function clearSearchFillterFields()
{
    document.getElementById("search_string").value = "";
}



////////////////////////////////
/////        Search        /////
////////////////////////////////
////////////////////////////////
//// Request search results ////
////////////////////////////////

async function requestSearchResultsQuantity()
{
    clearSearchResultsQuantityArea();
    let searchString = document.getElementById("search_string").value;

    let formData = new FormData();
    formData.append("search_string", searchString);

    try
    {
        let response = await fetch(ctx + "/ajax?command=music_search_get_results_quantity", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let artists = respJson.results_quantity.artists;
                    let albums = respJson.results_quantity.albums;
                    let tracks = respJson.results_quantity.tracks;
                    document.getElementById("artists_found_val").innerHTML = artists;
                    document.getElementById("albums_found_val").innerHTML = albums;
                    document.getElementById("tracks_found_val").innerHTML = tracks;
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient rights
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }

    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}


async function requestSearchResultsQuantityAndArtistsList()
{
    clearSearchResultsQuantityArea();
    clearSearchResultsArea();
    let searchString = document.getElementById("search_string").value;
    let limit = 10;
    let page = document.getElementById("page_number").value;
    let offset = limit * (page - 1);
    let formData = new FormData();
    formData.append("search_string", searchString);
    formData.append("limit", limit);
    formData.append("offset", offset);

    try
    {
        let response = await fetch(ctx + "/ajax?command=music_search_get_artists_list", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let artists_q = respJson.results_quantity.artists;
                    let albums_q = respJson.results_quantity.albums;
                    let tracks_q = respJson.results_quantity.tracks;
                    document.getElementById("artists_found_val").innerHTML = artists_q;
                    document.getElementById("albums_found_val").innerHTML = albums_q;
                    document.getElementById("tracks_found_val").innerHTML = tracks_q;
                    // build rows
                    let artists = respJson.artists;
                    let html = "";
                    for (let i = 0; i < artists.length; i++)
                    {
                        html += buildArtistRow(artists[i]);
                    }
                    addDataRowToSearchArea(buildArtistsHeader());
                    addDataRowToSearchArea(html);
                    setArtistDataRowButtonsListeners();
                    setDataRowsHoverHighlight();
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient rights
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }

    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

async function requestSearchResultsQuantityAndAlbumsList()
{
    clearSearchResultsQuantityArea();
    clearSearchResultsArea();
    let searchString = document.getElementById("search_string").value;
    let limit = 10;
    let page = document.getElementById("page_number").value;
    let offset = limit * (page - 1);
    let formData = new FormData();
    formData.append("search_string", searchString);
    formData.append("limit", limit);
    formData.append("offset", offset);

    try
    {
        let response = await fetch(ctx + "/ajax?command=music_search_get_albums_list", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let artists_q = respJson.results_quantity.artists;
                    let albums_q = respJson.results_quantity.albums;
                    let tracks_q = respJson.results_quantity.tracks;
                    document.getElementById("artists_found_val").innerHTML = artists_q;
                    document.getElementById("albums_found_val").innerHTML = albums_q;
                    document.getElementById("tracks_found_val").innerHTML = tracks_q;
                    // build rows
                    let albums = respJson.albums;
                    let html = "";
                    for (let i = 0; i < albums.length; i++)
                    {
                        html += buildAlbumRow(albums[i]);
                    }
                    addDataRowToSearchArea(buildAlbumsHeader());
                    addDataRowToSearchArea(html);
                    setAlbumDataRowButtonsListeners();
                    setDataRowsHoverHighlight();
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient rights
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }

    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

async function requestSearchResultsQuantityAndTracksList()
{
    clearSearchResultsQuantityArea();
    clearSearchResultsArea();
    let searchString = document.getElementById("search_string").value;
    let limit = 10;
    let page = document.getElementById("page_number").value;
    let offset = limit * (page - 1);
    let formData = new FormData();
    formData.append("search_string", searchString);
    formData.append("limit", limit);
    formData.append("offset", offset);

    try
    {
        let response = await fetch(ctx + "/ajax?command=music_search_get_tracks_list", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let artists_q = respJson.results_quantity.artists;
                    let albums_q = respJson.results_quantity.albums;
                    let tracks_q = respJson.results_quantity.tracks;
                    document.getElementById("artists_found_val").innerHTML = artists_q;
                    document.getElementById("albums_found_val").innerHTML = albums_q;
                    document.getElementById("tracks_found_val").innerHTML = tracks_q;
                    // build rows
                    let tracks = respJson.tracks;
                    let html = "";
                    for (let i = 0; i < tracks.length; i++)
                    {
                        html += buildTrackRow(tracks[i]);
                    }
                    addDataRowToSearchArea(buildTracksHeader());
                    addDataRowToSearchArea(html);
                    setTrackDataRowButtonsListeners();
                    setDataRowsHoverHighlight();
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient right
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }

    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

function setPageNumberAs1()
{
    document.getElementById("page_number").value = 1;
}

function activatePageControl(searchType, searchParam)
{
    let pageNumberField = document.getElementById("page_number");
    window.pageControl.searchString = document.getElementById("search_string").value;
    switch (searchType)
    {
        case "artists":
        {
            window.pageControl.action = requestSearchResultsQuantityAndArtistsList;
            break;
        }
        case "albums":
        {
            window.pageControl.action = requestSearchResultsQuantityAndAlbumsList;
            break;
        }
        case "tracks":
        {
            window.pageControl.action = requestSearchResultsQuantityAndTracksList;
            break;
        }
        case "particular_artist":
        {
            window.pageControl.action = function () {
                showParticularArtistAlbums(searchParam);
            };
            break;
        }
        case "particular_album":
        {
            window.pageControl.action = function () {
                showParticularAlbumTracks(searchParam);
            };
            break;
        }
    }

    document.getElementById("goto_start_button").onclick =
            function ()
            {
                document.getElementById("page_number").value = 1;
                if (window.pageControl.action != null)
                {
                    window.pageControl.action();
                }
                ;
            };
    document.getElementById("goto_back_button").onclick =
            function ()
            {
                if (pageNumberField.value > 1)
                {
                    pageNumberField.value -= 1;
                }
                if (window.pageControl.action != null)
                {
                    window.pageControl.action();
                }
                ;
            };
    document.getElementById("goto_forward_button").onclick =
            function ()
            {
                pageNumberField.value = pageNumberField.value - 0 + 1;
                if (window.pageControl.action != null)
                {
                    window.pageControl.action();
                }
                ;
            };
    document.getElementById("apply_page_number").onclick =
            function ()
            {
                if (window.pageControl.action != null)
                {
                    window.pageControl.action();
                }
                ;
            };
}

//////////////////////////
////  build data rows ////
//////////////////////////

function buildArtistsHeader()
{
    let rowClazz = "artist_data_row header_row noselect";
    let favCellClazz = "data_cell fav_cell";
    let idCellClazz = "data_cell id_cell undisplayable";
    let imageCellClazz = "data_cell image_cell";
    let nameCellClazz = "data_cell name_cell";
    let artistAlbumsQuantityCellClazz = "data_cell artist_albums_quantity";
    let buttonClazz = "data_cell button_cell empty_cell";
    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + favCellClazz + "\">" + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">" + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + window.textbundle.artist + "</div>";
    html += "<div class=\"" + artistAlbumsQuantityCellClazz + "\">" + window.textbundle.albums + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "</div>";
    html += "<div class=\"" + buttonClazz + " undisplayable\">" + "</div>";
    html += "</div>";
    return html;
}

function buildArtistRow(artist)
{
    let rowClazz = "artist_data_row data_row noselect";
    let favCellClazz = "data_cell fav_cell";
    let favBoolCellClazz = "data_cell fav_bool_cell undisplayable";
    let idCellClazz = "data_cell id_cell undisplayable";
    let imageCellClazz = "data_cell image_cell";
    let nameCellClazz = "data_cell name_cell";
    let artistAlbumsQuantityCellClazz = "data_cell artist_albums_quantity";
    let buttonClazz = "data_cell button_cell";
    let star = artist.favourite === true ? "&starf;" : "&star;";
    //★ &starf;
    //☆ &star;

    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + favCellClazz + "\">"
            + star
            + "</div>";
    html += "<div class=\"" + favBoolCellClazz + "\">" + artist.favourite + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">"
            + buildArtistImage(artist.artist_image)
            + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + artist.artist_id + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + artist.artist_name + "</div>";
    html += "<div class=\"" + artistAlbumsQuantityCellClazz + "\">" + artist.albums_count_artist_has + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&equiv;" + "</div>";
    html += "<div class=\"" + buttonClazz + " undisplayable\">" + "&rsaquo;&rsaquo;&rsaquo;&rsaquo;" + "</div>";
    html += "</div>";
    return html;
}

function buildAlbumsHeader()
{
    let rowClazz = "album_data_row header_row noselect";
    let favCellClazz = "data_cell fav_cell";
    let idCellClazz = "data_cell id_cell undisplayable";
    let imageCellClazz = "data_cell image_cell";
    let nameCellClazz = "data_cell name_cell";
    let albumTracksQuantityCellClazz = "data_cell album_tracks_quantity";
    let buttonClazz = "data_cell button_cell empty_cell";
    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + favCellClazz + "\">" + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">" + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + window.textbundle.artist + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">" + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + window.textbundle.album + "</div>";
    html += "<div class=\"" + albumTracksQuantityCellClazz + "\">" + window.textbundle.tracks + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "</div>";
    html += "</div>";
    return html;
}

function buildChosenArtistAlbumsHeader(artist)
{
    let rowClazz = "album_data_row header_row noselect";
    let imageCellClazz = "data_cell image_cell";
    let nameCellClazz = "data_cell name_cell flexcolumn";
    let buttonClazz = "data_cell button_cell empty_cell";
    let favCellClazz = "data_cell fav_cell";
    let yearCellClazz = "data_cell year_cell";
    let albumTracksQuantityCellClazz = "data_cell album_tracks_quantity";
    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + favCellClazz + "\">" + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">" + buildArtistImage(artist.image) + "</div>";
    html += "<div class=\"" + yearCellClazz + "\">" + window.textbundle.year + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">"
            + "<div class=\"flex1\">" + window.textbundle.artist + " : <b>" + artist.name + "</b></div>"
            + "<div class=\"flex1\">" + window.textbundle.album + "</div>"
            + "</div>";
    html += "<div class=\"" + albumTracksQuantityCellClazz + "\">" + window.textbundle.tracks + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "</div>";
    html += "</div>";
    return html;
}

function buildChosenAlbumTracksHeader(artist, album)
{
    let rowClazz = "album_data_row header_row noselect";
    let imageCellClazz = "data_cell image_cell";
    let nameCellClazz = "data_cell name_cell flexcolumn";
    let buttonClazz = "data_cell button_cell empty_cell";
    let favCellClazz = "data_cell fav_cell";
    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + favCellClazz + "\">" + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">" + buildArtistImage(artist.image) + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">" + buildAlbumImage(album.image) + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">"
            + "<div class=\"flex1\">" + window.textbundle.artist + " : <b>" + artist.name + "</b></div>"
            + "<div class=\"flex1\">" + window.textbundle.album + " : <b>" + album.name
            + "</b>, " + window.textbundle.year + ": <b>" + album.year + "</b>" + "</div>"
            + "</div>";
    html += "</div>";
    return html;
}

function buildAlbumRow(album)
{
    let rowClazz = "album_data_row data_row noselect";
    let favCellClazz = "data_cell fav_cell";
    let idCellClazz = "data_cell id_cell undisplayable";
    let favBoolCellClazz = "data_cell fav_bool_cell undisplayable";
    let imageCellClazz = "data_cell image_cell";
    let nameCellClazz = "data_cell name_cell";
    let albumTracksQuantityCellClazz = "data_cell album_tracks_quantity";
    let buttonClazz = "data_cell button_cell";
    let star = album.favourite === true ? "&starf;" : "&star;";
    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + favCellClazz + "\">" + star + "</div>";
    html += "<div class=\"" + favBoolCellClazz + "\">" + album.favourite + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">"
            + buildArtistImage(album.artist_image)
            + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + album.album_id + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + album.artist_name + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">"
            + buildAlbumImage(album.album_image)
            + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + album.album_name + "</div>";
    html += "<div class=\"" + albumTracksQuantityCellClazz + "\">" + album.tracks_count_album_has + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&equiv;" + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&raquo;" + "</div>";
    html += "</div>";
    return html;
}

function buildParticularArtistAlbumRow(album)
{
    let rowClazz = "album_data_row data_row noselect";
    let favCellClazz = "data_cell fav_cell";
    let idCellClazz = "data_cell id_cell undisplayable";
    let favBoolCellClazz = "data_cell fav_bool_cell undisplayable";
    let imageCellClazz = "data_cell image_cell";
    let yearCellClazz = "data_cell year_cell";
    let nameCellClazz = "data_cell name_cell";
    let albumTracksQuantityCellClazz = "data_cell album_tracks_quantity";
    let buttonClazz = "data_cell button_cell";
    let star = album.favourite === true ? "&starf;" : "&star;";
    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + favCellClazz + "\">" + star + "</div>";
    html += "<div class=\"" + favBoolCellClazz + "\">" + album.favourite + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + album.album_id + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">"
            + buildAlbumImage(album.album_image)
            + "</div>";
    html += "<div class=\"" + yearCellClazz + "\">" + album.album_year + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + album.album_name + "</div>";
    html += "<div class=\"" + albumTracksQuantityCellClazz + "\">" + album.tracks_count_album_has + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&equiv;" + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&raquo;" + "</div>";
    html += "</div>";
    return html;
}


function buildParticularAlbumTrackRow(track)
{
    let rowClazz = "track_data_row data_row noselect";
    let favCellClazz = "data_cell fav_cell";
    let idCellClazz = "data_cell id_cell undisplayable";
    let favBoolCellClazz = "data_cell fav_bool_cell undisplayable";
    let nameCellClazz = "data_cell name_cell";
    let buttonClazz = "data_cell button_cell";
    let fileClazz = "data_cell file_cell undisplayable";
    let star = track.favourite === true ? "&starf;" : "&star;";
    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + favCellClazz + "\">" + star + "</div>";
    html += "<div class=\"" + favBoolCellClazz + "\">" + track.favourite + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + track.id + "</div>";
    html += "<div class=\"" + fileClazz + "\">" + track.file + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + track.name + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&#9658;" + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&raquo;" + "</div>";
    html += "</div>";
    return html;
}

function buildTracksHeader()
{
    let rowClazz = "track_data_row header_row noselect";
    let favCellClazz = "data_cell fav_cell";
    let idCellClazz = "data_cell id_cell undisplayable";
    let imageCellClazz = "data_cell image_cell";
    let nameCellClazz = "data_cell name_cell";
    let buttonClazz = "data_cell button_cell empty_cell";
    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + favCellClazz + "\">" + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">" + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + window.textbundle.artist + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">" + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + window.textbundle.album + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + window.textbundle.track + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "</div>";
    html += "</div>";
    return html;
}

function buildTrackRow(track)
{
    let rowClazz = "track_data_row data_row noselect";
    let favCellClazz = "data_cell fav_cell";
    let idCellClazz = "data_cell id_cell undisplayable";
    let favBoolCellClazz = "data_cell fav_bool_cell undisplayable";
    let imageCellClazz = "data_cell image_cell";
    let nameCellClazz = "data_cell name_cell";
    let buttonClazz = "data_cell button_cell";
    let fileClazz = "data_cell file_cell undisplayable";
    let star = track.favourite === true ? "&starf;" : "&star;";
    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + favCellClazz + "\">" + star + "</div>";
    html += "<div class=\"" + favBoolCellClazz + "\">" + track.favourite + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">"
            + buildArtistImage(track.artist_image)
            + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + track.track_id + "</div>";
    html += "<div class=\"" + fileClazz + "\">" + track.track_file + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + track.artist_name + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">"
            + buildAlbumImage(track.album_image)
            + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + track.album_name + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + track.track_name + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&#9658;" + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&raquo;" + "</div>";
    html += "</div>";
    return html;
}


function buildArtistImage(imageProperty)
{
    return "<img src=\"" + window.artistImagePath + (imageProperty !== null ? imageProperty : "default") + "\">";
}

function buildAlbumImage(imageProperty)
{
    return "<img src=\"" + window.albumImagePath + (imageProperty !== null ? imageProperty : "default") + "\">";
}
////////////////////////////////////////////////////////////////////////////////

/////////////////////////
//// Set favourites /////
/////////////////////////


//// artists ////
async function setArtistAsFavourite(artistId)
{
    let formData = new FormData();
    formData.append("artist_id", artistId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=set_favourite_artist", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    updateFavedEntityRow(artistId, true);
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient right
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

async function unsetArtistAsFavourite(artistId)
{
    let formData = new FormData();
    formData.append("artist_id", artistId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=unset_favourite_artist", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    updateFavedEntityRow(artistId, false);
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient right
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}


function updateFavedEntityRow(entityId, favourite)
{
    let rows = document.getElementById("search_results").getElementsByClassName("data_row");
    let star = favourite ? "&starf;" : "&star;";
    for (i = 0; i < rows.length; i++)
    {
        let row = rows[i];
        let theRowWeNeed = row.getElementsByClassName("id_cell")[0].innerHTML == entityId;
        if (theRowWeNeed)
        {
            row.getElementsByClassName("fav_cell")[0].innerHTML = star;
            row.getElementsByClassName("fav_bool_cell ")[0].innerHTML = favourite;
            break;
        }
    }
}

//// albums ////

async function setAlbumAsFavourite(albumId)
{
    let formData = new FormData();
    formData.append("album_id", albumId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=set_favourite_album", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    updateFavedEntityRow(albumId, true);
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient right
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

async function unsetAlbumAsFavourite(albumId)
{
    let formData = new FormData();
    formData.append("album_id", albumId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=unset_favourite_album", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    updateFavedEntityRow(albumId, false);
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient right
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}


//// tracks ////

async function setTrackAsFavourite(trackId)
{
    let formData = new FormData();
    formData.append("track_id", trackId);
    let response = await fetch(ctx + "/ajax?command=set_favourite_track", {method: "POST", body: formData});
    let respJson = await response.json();
    let appResponseStatus = respJson.appResponseCode;
    if (response.status >= 200 && response.status < 400)
    {
        // Successfully connected to server
        switch (appResponseStatus)
        {
            case 0: // success
                updateFavedEntityRow(trackId, true);
                break;
            case 1: // fail, not logged in
                showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                break;
            case 2: // invalid data passed
                showMessage(window.textbundle.invalidData, window.MessageType.error);
                break;
            case 3: // insufficient right
                showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                break;
            case 520: // server side error
                showMessage(window.textbundle.serverSideError, window.MessageType.error);
                break;
        }
    } else
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

async function unsetTrackAsFavourite(trackId)
{
    let formData = new FormData();
    formData.append("track_id", trackId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=unset_favourite_track", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    updateFavedEntityRow(trackId, false);
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient right
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}







////////////////////////////////////////////////////////////////////////////////

function clearSearchResults()
{
    document.getElementById("search_results").innerHTML = "";
}

function addToSearchResultsArea(value)
{
    document.getElementById("search_results").innerHTML += value;
}

function initSearchResultsElements()
{
    setArtistDataRowButtonsListeners();
    setDataRowsHoverHighlight();
}

function setArtistDataRowButtonsListeners()
{
    let rows = document.getElementById("search_results").getElementsByClassName("data_row");
    for (let i = 0; i < rows.length; i++)
    {
        rows[i].getElementsByClassName("button_cell")[0].onclick = e =>
        {
            showParticularArtistAlbums(e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML);
        };
        rows[i].getElementsByClassName("fav_cell")[0].onclick = e =>
        {
            let artistIsFaved = e.target.parentElement.getElementsByClassName("fav_bool_cell")[0].innerHTML;
            let artistId = e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML;
            if (artistIsFaved == "true")
            {
                unsetArtistAsFavourite(artistId);
            } else
            {
                setArtistAsFavourite(artistId);
            }
        };
    }
}

function setAlbumDataRowButtonsListeners()
{
    let rows = document.getElementById("search_results").getElementsByClassName("data_row");
    for (let i = 0; i < rows.length; i++)
    {
        rows[i].getElementsByClassName("button_cell")[0].onclick = e =>
        {
            setPageNumberAs1();
            showParticularAlbumTracks(e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML);
        };
        rows[i].getElementsByClassName("button_cell")[1].onclick = e =>
        {
            addAlbumToPlaylist(e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML);
        };
        rows[i].getElementsByClassName("fav_cell")[0].onclick = e =>
        {
            let albumIsFaved = e.target.parentElement.getElementsByClassName("fav_bool_cell")[0].innerHTML;
            let albumId = e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML;
            if (albumIsFaved == "true")
            {
                unsetAlbumAsFavourite(albumId);
            } else
            {
                setAlbumAsFavourite(albumId);
            }
        };
    }
}


function setTrackDataRowButtonsListeners()
{
    let rows = document.getElementById("search_results").getElementsByClassName("data_row");
    for (i = 0; i < rows.length; i++)
    {
        rows[i].getElementsByClassName("button_cell")[0].onclick = e =>
        {
            playTrack(e.target.parentElement.getElementsByClassName("file_cell")[0].innerHTML);
        };
        rows[i].getElementsByClassName("button_cell")[1].onclick = e =>
        {
            let trackId = e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML;
            let nameCells = e.target.parentElement.getElementsByClassName("name_cell");
            let trackName = nameCells[nameCells.length - 1].innerHTML;
            addTrackToPlaylist(trackId, trackName);
        };
        rows[i].getElementsByClassName("fav_cell")[0].onclick = e =>
        {
            let trackIsFaved = e.target.parentElement.getElementsByClassName("fav_bool_cell")[0].innerHTML;
            let trackId = e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML;
            if (trackIsFaved == "true")
            {
                unsetTrackAsFavourite(trackId);
            } else
            {
                setTrackAsFavourite(trackId);
            }
        };
    }
}

async function showParticularArtistAlbums(artistId)
{
    clearSearchResultsQuantityArea();
    clearSearchResultsArea();
    let limit = 10;
    let page = document.getElementById("page_number").value;
    let offset = limit * (page - 1);
    let formData = new FormData();
    formData.append("artist_id", artistId);
    formData.append("limit", limit);
    formData.append("offset", offset);

    try
    {
        let response = await fetch(ctx + "/ajax?command=music_search_get_chosen_artist_albums", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    // build rows
                    activatePageControl("particular_artist", artistId);
                    let albums = respJson.albums;
                    let artist = respJson.artist;
                    let html = "";
                    for (let i = 0; i < albums.length; i++)
                    {
                        html += buildParticularArtistAlbumRow(albums[i]);
                    }
                    addDataRowToSearchArea(buildChosenArtistAlbumsHeader(artist));
                    addDataRowToSearchArea(html);
                    setAlbumDataRowButtonsListeners();
                    setDataRowsHoverHighlight();
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient right
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }

    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

async function showParticularAlbumTracks(albumId)
{
    clearSearchResultsQuantityArea();
    clearSearchResultsArea();
    let limit = 10;
    let page = document.getElementById("page_number").value;
    let offset = limit * (page - 1);
    let formData = new FormData();
    formData.append("album_id", albumId);
    formData.append("limit", limit);
    formData.append("offset", offset);

    try
    {
        let response = await fetch(ctx + "/ajax?command=music_search_get_chosen_album_tracks", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    // build rows
                    activatePageControl("particular_album", albumId);
                    let album = respJson.album;
                    let artist = respJson.artist;
                    let tracks = respJson.tracks;
                    let html = "";
                    for (let i = 0; i < tracks.length; i++)
                    {
                        html += buildParticularAlbumTrackRow(tracks[i]);
                    }
                    addDataRowToSearchArea(buildChosenAlbumTracksHeader(artist, album));
                    addDataRowToSearchArea(html);
                    setTrackDataRowButtonsListeners();
                    setDataRowsHoverHighlight();
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient right
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

function setDataRowsHoverHighlight()
{
    let rows = document.getElementById("search_results")
            .getElementsByClassName("data_row");
    for (let i = 0; i < rows.length; i++)
    {
        let row = rows.item(i);
        row.onmouseout = function () {
            unhighlightRow(row);
        };
        row.onmouseover = function () {
            highlightRow(row);
        };
    }
}

//function unselectRows()
//{
//
//}

function highlightRow(row)
{
    row.classList.add("highlighted");
}

function unhighlightRow(row)
{
    row.classList.remove("highlighted");
}

function clearSearchResultsArea()
{
    document.getElementById("search_results").innerHTML = "";
}

function addDataRowToSearchArea(row)
{
    document.getElementById("search_results").innerHTML += row;
}

//function hideSearchWarning()
//{
//
//}
//
//function showSearchWarning()
//{
//
//}
//
//function setSearchWarning(text)
//{
//
//}

function clearSearchResultsQuantityArea()
{
    let cells = document.getElementById("items_found").getElementsByClassName("span");
    for (let i = 0; i < cells.length; i++)
    {
        cells[i].innerHTML = "";
    }
}


function playTrack(trackFileName)
{
    let audio = document.getElementById("player");
    audio.src = window.trackFilePath + trackFileName;
    audio.load();
    audio.play();
}



///////////////////////
//// playlist work ////
///////////////////////

async function requestUserPlaylists()
{

    let formData = new FormData();
    try
    {
        let response = await fetch(ctx + "/ajax?command=get_user_playlists", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let html = "";
                    let playlists = respJson.playlists;
                    for (let i = 0; i < playlists.length; i++)
                    {
                        let playlist = playlists[i];
                        html += buildPlaylistOption(playlist);
                    }
                    setPlaylistSelectOptions(html);
                    filterPlaylistsList();
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // insufficient rights
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}


async function usePlaylist(playlistId, playlistName)
{
    let formData = new FormData();
    formData.append("playlist_id", playlistId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_playlist_tracks", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let html = "";
                    let tracks = respJson.playlist_items;
                    for (let i = 0; i < tracks.length; i++)
                    {
                        let track = tracks[i];
                        html += createAudiotrackDataRow(track);
                    }
                    document.getElementById("track_items").innerHTML = html;
                    document.getElementById("used_playlist_id").innerHTML = playlistId;
                    document.getElementById("used_playlist_name").innerHTML = playlistName;
                    setPlaylistButtonsListeners();
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data
                    showMessage(window.textbundle.invalidData, window.MessageType.warning);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

function reloadPlaylist()
{
    let playlistId = document.getElementById("used_playlist_id").innerHTML;
    let playlistName = document.getElementById("used_playlist_name").innerHTML;
    if (playlistId != "")
    {
        usePlaylist(playlistId, playlistName);
    }
}

function buildPlaylistOption(playlist)
{
    let html = "<option value=\"" + playlist.id + "\">";
    html += playlist.name;
    html += "</option>";
    return html;
}

function setPlaylistSelectOptions(html)
{
    document.getElementById("availible_playlists").innerHTML = html;
}

function cleanPlaylistTracksArea()
{
    document.getElementById("track_items").innerHTML = "";
}

function filterPlaylistsList()
{
    let textToFind = document.getElementById("playlist_name_filter").value.toLowerCase();
    let options = document.getElementById("availible_playlists").getElementsByTagName("option");
    for (let i = 0; i < options.length; i++)
    {
        let playlistName = options[i].textContent.toLowerCase().replace(/\xa0/g, " ");
        if (playlistName.indexOf(textToFind) === -1)
        {
            options[i].classList.add("undisplayable");
        } else
        {
            options[i].classList.remove("undisplayable");
        }
    }

// setting first availible option value OR empty value if the isn't any
    let selection = document.getElementById("availible_playlists");
    for (let i = 0; i < options.length; i++)
    {
        if (!options[i].classList.contains("undisplayable"))
        {
            selection.value = options[i].value;
            return;
        }
    }
    document.getElementById("availible_playlists").value = -1;
}

function createAudiotrackDataRow(track)
{
    let activityState = track.active === true ? "active" : "inactive";
    let trackRowClazz = "audiotrack_row " + activityState;
    let idCellClazz = "audiotrack_id_cell undisplayable";
    let deleteTrackButtonCellClazz = "delete_track_button_cell";
    let deleteTrackButtonClazz = "delete_track_button";
    let nameCellClazz = "playlist_name_cell";
    let playButtonCellClazz = "playbutton_cell";
    let playButtonClazz = "play_button";
    let html = "<div class=\"" + trackRowClazz + "\">";
    html += "<div class=\"" + deleteTrackButtonCellClazz + "\">"
            + "<button class=\"" + deleteTrackButtonClazz + "\">"
            + "&#10005;"
            + "</button></div>";
    html += "<div class=\"" + idCellClazz + "\">" + track.track_id + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + track.track_name + "</div>";
    if (track.active)
    {
        html += "<div class=\"" + playButtonCellClazz + "\">";
        html += "<button class=\"" + playButtonClazz + "\">&#9658;</button>";
        html += "</div>";
    }

    html += "</div>";
    return html;
}

function setPlaylistButtonsListeners()
{
    let rows = document.getElementById("track_items").getElementsByClassName("audiotrack_row");
    for (let i = 0; i < rows.length; i++)
    {
        let deleteButton = rows[i].getElementsByClassName("delete_track_button")[0];
        deleteButton.onclick =
                function ()
                {
                    deletePlaylistTrack(rows[i]);
                };
        let trackId = rows[i].getElementsByClassName("audiotrack_id_cell")[0].innerHTML;
        let playButton = rows[i].getElementsByClassName("play_button")[0];
        if (playButton != undefined)
        {
            playButton.onclick =
                    function ()
                    {
                        playPlaylistTrack(trackId);
                    };
        }
    }
}

async function playPlaylistTrack(trackId)
{
    let formData = new FormData();
    formData.append("track_id", trackId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_active_track_data", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    playTrack(respJson.track.file);
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data
                    showMessage(window.textbundle.invalidData, window.MessageType.warning);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

function deletePlaylistTrack(trackRow)
{
    trackRow.parentNode.removeChild(trackRow);
    setPlaylistButtonsListeners();
}

function addPlaylistTrack(trackRowHtml)
{
    if (document.getElementById("used_playlist_id").textContent != "")
    {
        document.getElementById("track_items").innerHTML += trackRowHtml;
        setPlaylistButtonsListeners();
    }
}

function removeSingleTrack(trackRow)
{
    trackRow.parentNode.removeChild(trackRow);
}

function addTrackToPlaylist(trackId, trackName)
{
    let track = {};
    track.track_id = trackId;
    track.track_name = trackName;
    track.active = true;
    let html = createAudiotrackDataRow(track);
    addPlaylistTrack(html);
}

async function addAlbumToPlaylist(albumId)
{
    let formData = new FormData();
    formData.append("album_id", albumId);
    formData.append("limit", 100);
    formData.append("offset", 0);

    try
    {
        let response = await fetch(ctx + "/ajax?command=music_search_get_chosen_album_tracks", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let tracks = respJson.tracks;
                    for (let i = 0; i < tracks.length; i++)
                    {
                        addTrackToPlaylist(tracks[i].id, tracks[i].name);
                    }
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient right
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

async function savePlaylist()
{
    let playlistId = document.getElementById("used_playlist_id").innerHTML;
    if (playlistId == "" || playlistId == undefined)
    {
        return;
    }

    let formData = new FormData();
    formData.append("playlist_id", playlistId);
    let tracksIdCells = document.getElementById("track_items").getElementsByClassName("audiotrack_id_cell");
    for (let i = 0; i < tracksIdCells.length; i++)
    {
        formData.append("tracks_id[]", tracksIdCells[i].innerHTML);
    }

    try
    {
        let response = await fetch(ctx + "/ajax?command=record_playlist", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    // nothing?
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient right
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

async function createPlaylist()
{
    let playlistName = document.getElementById("playlist_name_filter").value;
    if (playlistName.length < 1)
    {
        return;
    }

    let formData = new FormData();
    formData.append("name", playlistName);
    try
    {
        let response = await fetch(ctx + "/ajax?command=create_new_playlist", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    requestUserPlaylists();
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data
                    showMessage(window.textbundle.invalidData, window.MessageType.warning);
                    break;
                case 520: // server side error
                    showMessage(window.textbundle.serverSideError, window.MessageType.error);
                    break;
            }
        } else
        {
            showMessage(window.textbundle.requestFailed, window.MessageType.error);
        }
    } catch (ex)
    {
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

/////////////////////////////////
//// Dialogue window control ////
/////////////////////////////////

function showMessage(text, messageType, headingMessage)
{
// setting window decoration depending on message type
    let dialogueWindow = document.getElementById("dialog_window");
    dialogueWindow.className = "";
    switch (messageType)
    {
        case window.MessageType.message:
            dialogueWindow.className = "message";
            break;
        case window.MessageType.warning:
            dialogueWindow.className = "warning";
            break;
        case window.MessageType.error:
            dialogueWindow.className = "error";
            break;
    }

    if (headingMessage !== undefined && headingMessage !== null)
    {
        dialogueWindow.getElementsByTagName("h3")[0].innerHTML = headingMessage;
    }

    dialogueWindow.getElementsByTagName("p")[0].innerHTML = text;
    dialogueWindow.showModal();
}

////////////////////////////////////////////////////////////////////////////////
// 3RD side
////////////////////////////////////////////////////////////////////////////////

function getCookie(name)
{
    var matches = document.cookie.match(new RegExp(
            "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
            ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}

function setCookie(name, value, options)
{
    options = options || {};

    var expires = options.expires;

    if (typeof expires === "number" && expires)
    {
        var d = new Date();
        d.setTime(d.getTime() + expires * 1000);
        expires = options.expires = d;
    }

    if (expires && expires.toUTCString) {
        options.expires = expires.toUTCString();
    }

    value = encodeURIComponent(value);

    var updatedCookie = name + "=" + value;

    for (var propName in options) {
        updatedCookie += "; " + propName;
        var propValue = options[propName];
        if (propValue !== true) {
            updatedCookie += "=" + propValue;
        }
    }

    document.cookie = updatedCookie;
}
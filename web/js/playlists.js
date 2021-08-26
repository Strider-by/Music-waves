function run()
{
    window.pageControl = {};
    initControls();
    initPlayerControls();
    requestUserPlaylists();

    window.MessageType = Object.freeze({message: {}, warning: {}, error: {}});
}

function initControls()
{
    document.getElementById("playlists_mode_button").addEventListener("click", setSelectPlaylistsMode);
    document.getElementById("favourites_mode_button").addEventListener("click", setSelectFavouritesMode);
    document.getElementById("rename_playlist_button").addEventListener("click", setRenamePlaylistMode);
    document.getElementById("new_playlist_button").addEventListener("click", setNewPlaylistMode);
    document.getElementById("delete_playlist_button").addEventListener("click", setDeletePlaylistMode);
    document.getElementById("reload_playlists_list_button").addEventListener("click",
            function ()
            {
                setDefaultPlaylistAreaMode();
                requestUserPlaylists();
            });
    document.getElementById("fav_artists").addEventListener("click", setFavouriteArtistsMode);
    document.getElementById("fav_albums").addEventListener("click", setFavouriteAlbumsMode);
    document.getElementById("fav_tracks").addEventListener("click", setFavouriteTracksMode);
    document.getElementById("apply_playlist_creation").addEventListener("click", createPlaylist);
    document.getElementById("cancel_playlist_creation").addEventListener("click",
            function ()
            {
                cleanNewPlaylistNameInput();
                setDefaultPlaylistAreaMode();
            });

    document.getElementById("clean_playlists_filter").addEventListener("click", cleanPlaylistsFilterInput);
    document.getElementById("select_all_playlists_button").addEventListener("click",
            function ()
            {
                setPlaylistSelectionToDelete(true);
            });
    document.getElementById("unselect_all_playlists_button").addEventListener("click",
            function ()
            {
                setPlaylistSelectionToDelete(false);
            });
    document.getElementById("exit_delete_playlists_mode_button").addEventListener("click",
            function ()
            {
                setDefaultPlaylistAreaMode();
                selectAllPlaylists(false);
            });
    document.getElementById("process_playlist_rename").addEventListener("click", renamePlaylist);
    document.getElementById("cancel_playlist_rename").addEventListener("click", cancelPlaylistRename);
    document.getElementById("playlists_filter_input").addEventListener("input", filterPlaylistsList);
    document.getElementById("delete_selected_playlists_button").addEventListener("click", deleteSelectedPlaylists);

    document.getElementById("search_string").addEventListener("input",
            function ()
            {
                if (window.pageControl.action != null)
                {
                    window.pageControl.action();
                }
            });

    document.getElementById("cleanse_search_button").addEventListener("click",
            function ()
            {
                document.getElementById("search_string").value = "";
                if (window.pageControl.action != null)
                {
                    window.pageControl.action();
                }
            });

    document.getElementById("select_all_tracks_pl1_button").addEventListener("click",
            function ()
            {
                selectAllTrack(1, true);
            });

    document.getElementById("select_all_tracks_pl2_button").addEventListener("click",
            function ()
            {
                selectAllTrack(2, true);
            });

    document.getElementById("unselect_all_tracks_pl1_button").addEventListener("click",
            function ()
            {
                selectAllTrack(1, false);
            });

    document.getElementById("unselect_all_tracks_pl2_button").addEventListener("click",
            function ()
            {
                selectAllTrack(2, false);
            });

    document.getElementById("p1_move_up").addEventListener("click",
            function ()
            {
                shiftSelectedTracksUp(1);
            });

    document.getElementById("p2_move_up").addEventListener("click",
            function ()
            {
                shiftSelectedTracksUp(2);
            });

    document.getElementById("p1_move_top").addEventListener("click",
            function ()
            {
                shiftSelectedTracksTop(1);
            });

    document.getElementById("p2_move_top").addEventListener("click",
            function ()
            {
                shiftSelectedTracksTop(2);
            });

    document.getElementById("p1_move_down").addEventListener("click",
            function ()
            {
                shiftSelectedTracksDown(1);
            });

    document.getElementById("p2_move_down").addEventListener("click",
            function ()
            {
                shiftSelectedTracksDown(2);
            });

    document.getElementById("p1_move_bottom").addEventListener("click",
            function ()
            {
                shiftSelectedTracksBottom(1);
            });

    document.getElementById("p2_move_bottom").addEventListener("click",
            function ()
            {
                shiftSelectedTracksBottom(2);
            });

    document.getElementById("delete_selected_tracks_p1_button").addEventListener("click",
            function ()
            {
                removeSelectedTracks(1);
            });

    document.getElementById("delete_selected_tracks_p2_button").addEventListener("click",
            function ()
            {
                removeSelectedTracks(2);
            });

    document.getElementById("reload_pl1_button").addEventListener("click",
            function ()
            {
                let id = document.getElementById("pl1_id").innerHTML;
                let name = document.getElementById("pl1_name").innerHTML;
                if (id != "")
                {
                    usePlaylist(id, 1, name);
                }
            });

    document.getElementById("reload_pl2_button").addEventListener("click",
            function ()
            {
                let id = document.getElementById("pl2_id").innerHTML;
                let name = document.getElementById("pl2_name").innerHTML;
                if (id != "")
                {
                    usePlaylist(id, 2, name);
                }
            });

    document.getElementById("copy_selected_to_pl2").addEventListener("click",
            function ()
            {
                copySelectedTracks(1, 2);
            });

    document.getElementById("copy_selected_to_pl1").addEventListener("click",
            function ()
            {
                copySelectedTracks(2, 1);
            });

    document.getElementById("move_selected_to_pl2").addEventListener("click",
            function ()
            {
                moveSelectedTracks(1, 2);
            });

    document.getElementById("move_selected_to_pl1").addEventListener("click",
            function ()
            {
                moveSelectedTracks(2, 1);
            });

    document.getElementById("save_pl1_button").addEventListener("click",
            function ()
            {
                savePlaylist(1);
            });

    document.getElementById("save_pl2_button").addEventListener("click",
            function ()
            {
                savePlaylist(2);
            });

    document.getElementById("close_dialog_window").addEventListener("click",
            function ()
            {
                document.getElementById("dialog_window").close();
            });
}

function initPlayerControls()
{
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
    const playPrevButton = document.getElementById("play_prev_b");
    const playNextButton = document.getElementById("play_next_b");

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
    audio.addEventListener("ended", function () {
        playNextTrack();
    });
    playPrevButton.addEventListener("click", function () {
        playPreviousTrack();
    });
    playNextButton.addEventListener("click", function () {
        playNextTrack();
    });

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

    ///////////////////////////////////////////////////////
    
    loadVolumeLevelChange();
}

function setSelectPlaylistsMode()
{
    document.getElementById("playlists_list_area").classList.remove("undisplayable");
    document.getElementById("favourites_area").classList.add("undisplayable");
}

function setSelectFavouritesMode()
{
    document.getElementById("playlists_list_area").classList.add("undisplayable");
    document.getElementById("favourites_area").classList.remove("undisplayable");
}


/////////////////////////////
//// playlist area modes ////
/////////////////////////////

function setNewPlaylistMode()
{
    document.getElementById("new_playlist_area").classList.remove("undisplayable");
    document.getElementById("rename_playlist_area").classList.add("undisplayable");
    document.getElementById("delete_playlist_area").classList.add("undisplayable");
    document.getElementById("new_playlist_name").focus();

    showDeleteCheckboxes(false);
    showRenameButtons(false);
}

function setRenamePlaylistMode()
{
    document.getElementById("new_playlist_area").classList.add("undisplayable");
    document.getElementById("rename_playlist_area").classList.remove("undisplayable");
    document.getElementById("delete_playlist_area").classList.add("undisplayable");

    showDeleteCheckboxes(false);
    showRenameButtons(true);
}

function setDeletePlaylistMode()
{
    document.getElementById("new_playlist_area").classList.add("undisplayable");
    document.getElementById("rename_playlist_area").classList.add("undisplayable");
    document.getElementById("delete_playlist_area").classList.remove("undisplayable");

    showDeleteCheckboxes(true);
    showRenameButtons(false);
}

function setDefaultPlaylistAreaMode()
{
    document.getElementById("new_playlist_area").classList.add("undisplayable");
    document.getElementById("rename_playlist_area").classList.add("undisplayable");
    document.getElementById("delete_playlist_area").classList.add("undisplayable");

    showDeleteCheckboxes(false);
    showRenameButtons(false);
}

function showRenameButtons(boolean)
{
    let elements = document.getElementById("playlists").getElementsByClassName("playlist_rename");
    switch (boolean)
    {
        case true:
            for (let i = 0; i < elements.length; i++)
            {
                elements[i].classList.remove("undisplayable");
            }
            break;

        case false:
            for (let i = 0; i < elements.length; i++)
            {
                elements[i].classList.add("undisplayable");
            }
            break;
    }
}

function showDeleteCheckboxes(boolean)
{
    let elements = document.getElementById("playlists").getElementsByClassName("playlist_delete_check");
    switch (boolean)
    {
        case true:
            for (let i = 0; i < elements.length; i++)
            {
                elements[i].classList.remove("undisplayable");
            }
            break;

        case false:
            for (let i = 0; i < elements.length; i++)
            {
                elements[i].classList.add("undisplayable");
            }
            break;
    }
}


///////////////////////////////
//// favourites area modes ////
///////////////////////////////

function setModeSymbol(symbol)
{
    document.getElementById("current_fav_mode").innerHTML = symbol;
}

function setFavouriteArtistsMode()
{
    document.getElementById("page_number").value = 1;
    setModeSymbol("&#128104;");
    getFavouriteArtists();
}

function setFavouriteAlbumsMode()
{
    document.getElementById("page_number").value = 1;
    setModeSymbol("&#128191;");
    getFavouriteAlbums();
}

function setFavouriteTracksMode()
{
    document.getElementById("page_number").value = 1;
    setModeSymbol("&#127925;");
    getFavouriteTracks();
}

function setArtistAlbumsMode()
{
    document.getElementById("page_number").value = 1;
    setModeSymbol("&#128104;&equiv;");
}

function setAlbumTracksMode()
{
    document.getElementById("page_number").value = 1;
    setModeSymbol("&#128191;&equiv;");
}

//////////////////////
//// xhr requests ////
//////////////////////

async function requestUserPlaylists()
{
    cleanPlaylistsArea();

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
                        html += createPlaylistDataRow(playlist);
                    }
                    setPlaylistsAreaData(html);
                    setPlaylistsDataRowsHoverHighlight();
                    setPlaylistsDataRowsRenameButtonListeners();
                    setPlaylistsDataRowsUsePlaylistButtonListeners();

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

async function createPlaylist()
{
    let playlistName = document.getElementById("new_playlist_name").value;
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
                    cleanNewPlaylistNameInput();
                    setDefaultPlaylistAreaMode();
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

async function renamePlaylist()
{
    let playlistName = document.getElementById("edit_playlist_name").value;
    let playlistId = document.getElementById("renamed_playlist_id").value;
    let formData = new FormData();
    formData.append("name", playlistName);
    formData.append("id", playlistId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=rename_playlist", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    requestUserPlaylists();
                    cleanRenamePlaylistInput();
                    setDefaultPlaylistAreaMode();
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

async function deleteSelectedPlaylists()
{
    let playlistRows = document.getElementById("playlists").getElementsByClassName("playlist_row");
    let playlistsToDelete = [];

    for (let i = 0; i < playlistRows.length; i++)
    {
        let checkbox = playlistRows[i].getElementsByTagName("input")[0];
        if (checkbox.checked)
        {
            let playlistId = playlistRows[i].getElementsByClassName("playlist_id_cell")[0].innerHTML;
            playlistsToDelete.push(playlistId);
        }
    }
    
    if(playlistsToDelete.length === 0)
    {
        return; // there is nothing to delete is chosen
    }


    let formData = new FormData();
    for (let i = 0; i < playlistsToDelete.length; i++)
    {
        formData.append("id[]", playlistsToDelete[i]);
    }

    try
    {
        let response = await fetch(ctx + "/ajax?command=delete_playlists", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    
                    
                    // in case deleted playlists were "in use"
                    let pl1InUseId = document.getElementById("pl1_id").innerHTML;
                    let pl2InUseId = document.getElementById("pl2_id").innerHTML;
                    if(playlistsToDelete.includes(pl1InUseId))
                    {
                        clearPlaylistArea(1);
                    }
                    if(playlistsToDelete.includes(pl2InUseId))
                    {
                        clearPlaylistArea(2);
                    }
                    requestUserPlaylists();
                    setDefaultPlaylistAreaMode();
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


async function usePlaylist(playlistId, columnNumber, playlistName)
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
                    document.getElementById("playlist_" + columnNumber + "_items").innerHTML = html;
                    document.getElementById("pl" + columnNumber + "_name").innerHTML = playlistName;
                    document.getElementById("pl" + columnNumber + "_id").innerHTML = playlistId;
                    setAudiotrackRowsListeners(columnNumber);
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

function setAudiotrackRowsListeners(columnNumber)
{
    let area = document.getElementById("playlist_" + columnNumber + "_items");
    let activeTrackRows = area.getElementsByClassName("audiotrack_row active");

    for (let i = 0; i < activeTrackRows.length; i++)
    {
        activeTrackRows[i].getElementsByTagName("button")[0].onclick =
                function ()
                {
                    let trackId = activeTrackRows[i].getElementsByClassName("audiotrack_id_cell")[0].innerHTML;
                    let trackPosition = i;
                    playTrack(trackId, columnNumber, trackPosition);
                };
    }
}


async function getFavouriteArtists()
{
    setCurrentFavAreaAction("artists");

    let formData = new FormData();
    let limit = 15; // fits good enough
    let pageNumber = document.getElementById("page_number").value;
    let offset = (pageNumber - 1) * limit;
    let searchValue = document.getElementById("search_string").value;

    formData.append("search_string", searchValue);
    formData.append("limit", limit);
    formData.append("offset", offset);

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_favourite_artists", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let artists = respJson.artists;
                    let html = "";

                    for (let i = 0; i < artists.length; i++)
                    {
                        html += createFavedArtistRow(artists[i]);
                    }
                    document.getElementById("favourited_items").innerHTML = html;
                    setFavedArtistDataRowButtonsListeners();
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

async function getFavouriteAlbums()
{
    setCurrentFavAreaAction("albums");

    let formData = new FormData();
    let limit = 14; // fits good enough
    let pageNumber = document.getElementById("page_number").value;
    let offset = (pageNumber - 1) * limit;
    let searchValue = document.getElementById("search_string").value;

    formData.append("search_string", searchValue);
    formData.append("limit", limit);
    formData.append("offset", offset);

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_favourite_albums", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let albums = respJson.albums;
                    let html = "";

                    for (let i = 0; i < albums.length; i++)
                    {
                        html += createAlbumRow(albums[i].artist, albums[i].album);
                    }
                    document.getElementById("favourited_items").innerHTML = html;
                    setAlbumDataRowButtonsListeners();
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

async function getArtistAlbums(artistId)
{
    setCurrentFavAreaAction("artist albums", artistId);

    let formData = new FormData();
    let limit = 14; // fits good enough
    let pageNumber = document.getElementById("page_number").value;
    let offset = (pageNumber - 1) * limit;
    let searchValue = document.getElementById("search_string").value;

    formData.append("search_string", searchValue);
    formData.append("artist_id", artistId);
    formData.append("limit", limit);
    formData.append("offset", offset);

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_artist_albums_data", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let albums = respJson.albums;
                    let html = "";

                    for (let i = 0; i < albums.length; i++)
                    {
                        html += createAlbumRow(albums[i].artist, albums[i].album, true);
                    }
                    document.getElementById("favourited_items").innerHTML = html;
                    setAlbumDataRowButtonsListeners();
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

async function getFavouriteTracks()
{
    setCurrentFavAreaAction("tracks");

    let formData = new FormData();
    let limit = 12; // fits good enough
    let pageNumber = document.getElementById("page_number").value;
    let offset = (pageNumber - 1) * limit;
    let searchValue = document.getElementById("search_string").value;

    formData.append("search_string", searchValue);
    formData.append("limit", limit);
    formData.append("offset", offset);

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_favourite_tracks", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let tracks = respJson.tracks;
                    let html = "";

                    for (let i = 0; i < tracks.length; i++)
                    {
                        html += createTrackRow(tracks[i].artist, tracks[i].album, tracks[i].track);
                    }
                    document.getElementById("favourited_items").innerHTML = html;
                    setTrackDataRowButtonsListeners();
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

async function getAlbumTracks(albumId)
{
    setCurrentFavAreaAction("album tracks", albumId);

    let formData = new FormData();
    let searchValue = document.getElementById("search_string").value;

    formData.append("search_string", searchValue);
    formData.append("album_id", albumId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=search_album_tracks", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let tracks = respJson.tracks;
                    let html = "";

                    for (let i = 0; i < tracks.length; i++)
                    {
                        html += createTrackRow(tracks[i].artist, tracks[i].album, tracks[i].track, true);
                    }
                    document.getElementById("favourited_items").innerHTML = html;
                    setTrackDataRowButtonsListeners();
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

/////////////////////////////
//// clean input methods ////
/////////////////////////////

function cleanNewPlaylistNameInput()
{
    document.getElementById("new_playlist_name").value = "";
}

function cleanRenamePlaylistInput()
{
    document.getElementById("edit_playlist_name").value = "";
}

function cleanPlaylistsFilterInput()
{
    document.getElementById("playlists_filter_input").value = "";
    filterPlaylistsList();
}

/////////////////////////////
//// serve lists methods ////
/////////////////////////////

function cleanPlaylistsArea()
{
    document.getElementById("playlists").innerHTML = "";
}

function setPlaylistsAreaData(html)
{
    document.getElementById("playlists").innerHTML = html;
}

function highlightHoveredRow(element)
{
    element.classList.add("highlighted");
}

function unhighlightHoveredRow(element)
{
    element.classList.remove("highlighted");
}

function setPlaylistsDataRowsHoverHighlight()
{
    let rows = document.getElementById("playlists")
            .getElementsByClassName("playlist_row");

    for (let i = 0; i < rows.length; i++)
    {
        let row = rows.item(i);
        row.onmouseout = function () {
            unhighlightHoveredRow(row);
        };
        row.onmouseover = function () {
            highlightHoveredRow(row);
        };
    }
}

function setPlaylistsDataRowsRenameButtonListeners()
{
    let buttons = document.getElementById("playlists")
            .getElementsByClassName("playlist_rename");

    for (let i = 0; i < buttons.length; i++)
    {
        buttons[i].addEventListener("click",
                (e) =>
        {
            let id = e.target.parentElement.parentElement.getElementsByClassName("playlist_id_cell")[0].textContent;
            let currentName = e.target.parentElement.parentElement.getElementsByClassName("playlist_name_cell")[0].textContent;

            setRenamedPlayListData(id, currentName);
        });
    }
}

function setPlaylistsDataRowsUsePlaylistButtonListeners()
{
    let buttons1 = document.getElementById("playlists")
            .getElementsByClassName("use_playlist_area_1");

    let buttons2 = document.getElementById("playlists")
            .getElementsByClassName("use_playlist_area_2");

    for (let i = 0; i < buttons1.length; i++)
    {
        buttons1[i].addEventListener("click",
                (e) =>
        {
            let id = e.target.parentElement.parentElement.getElementsByClassName("playlist_id_cell")[0].textContent;
            let playlistName = e.target.parentElement.parentElement.getElementsByClassName("playlist_name_cell")[0].textContent;
            usePlaylist(id, 1, playlistName);
        });
    }

    for (let i = 0; i < buttons2.length; i++)
    {
        buttons2[i].addEventListener("click",
                (e) =>
        {
            let id = e.target.parentElement.parentElement.getElementsByClassName("playlist_id_cell")[0].textContent;
            let playlistName = e.target.parentElement.parentElement.getElementsByClassName("playlist_name_cell")[0].textContent;
            usePlaylist(id, 2, playlistName);
        });
    }
}

function setFavedArtistDataRowButtonsListeners()
{
    let rows = document.getElementById("favourited_items").getElementsByClassName("data_row");
    for (let i = 0; i < rows.length; i++)
    {
        rows[i].getElementsByClassName("small_button_cell")[0].onclick = e =>
        {
            unsetArtistAsFavourite(e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML);
        };

        rows[i].getElementsByClassName("button_cell")[0].onclick = e =>
        {
            setArtistAlbumsMode();
            getArtistAlbums(e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML);
        };
    }
}

function setAlbumDataRowButtonsListeners()
{
    let rows = document.getElementById("favourited_items").getElementsByClassName("data_row");
    for (let i = 0; i < rows.length; i++)
    {
        rows[i].getElementsByClassName("small_button_cell")[0].onclick = e =>
        {
            unsetAlbumAsFavourite(e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML);
        };

        rows[i].getElementsByClassName("button_cell")[0].onclick = e =>
        {
            setAlbumTracksMode();
            getAlbumTracks(e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML);
        };
        rows[i].getElementsByClassName("button_cell")[1].onclick = e =>
        {
            addAlbumToPlaylist((e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML), 1);
        };
        rows[i].getElementsByClassName("button_cell")[2].onclick = e =>
        {
            addAlbumToPlaylist((e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML), 2);
        };
    }
}

function setTrackDataRowButtonsListeners()
{
    let rows = document.getElementById("favourited_items").getElementsByClassName("data_row");
    for (let i = 0; i < rows.length; i++)
    {
        rows[i].getElementsByClassName("small_button_cell")[0].onclick = e =>
        {
            unsetTrackAsFavourite(e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML);
        };

        rows[i].getElementsByClassName("button_cell")[0].onclick = e =>
        {
            addTrackToPlaylist((e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML), 1);
        };

        rows[i].getElementsByClassName("button_cell")[1].onclick = e =>
        {
            addTrackToPlaylist((e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML), 2);
        };
        rows[i].getElementsByClassName("button_cell")[2].onclick = e =>
        {
            playTrack((e.target.parentElement.getElementsByClassName("id_cell")[0].innerHTML), "", "");
        };
    }
}

function clearPlaylistArea(playlistAreaNumber)
{
    document.getElementById("pl" + playlistAreaNumber + "_id").innerHTML = "";
    document.getElementById("pl" + playlistAreaNumber + "_name").innerHTML = "";
    document.getElementById("playlist_" + playlistAreaNumber + "_items").innerHTML = "";
}



//////////////////////////////////
//// unset item as favourite /////
//////////////////////////////////
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
                    getFavouriteArtists();
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
                    getFavouriteAlbums();
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
                    getFavouriteTracks();
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


////////////////////////////////
//// build data row methods ////
////////////////////////////////

function createPlaylistDataRow(playlist)
{
    let playlistRowClazz = "playlist_row";
    let idCellClazz = "playlist_id_cell undisplayable";
    let nameCellClazz = "playlist_name_cell";
    let buttonCellClazz = "playlist_button_cell";
    let renameButtonCellClazz = "playlist_rename undisplayable";
    let deleteCheckerCellClazz = "playlist_delete_check undisplayable";

    let button1Clazz = "use_playlist_area_1";
    let button2Clazz = "use_playlist_area_2";

    let html = "<div class=\"" + playlistRowClazz + "\">";

    html += "<div class=\"" + renameButtonCellClazz + "\">"
            + "<button>&equiv;</button>"
            + "</div>";
    html += "<div class=\"" + deleteCheckerCellClazz + "\">"
            + "<input type=\"checkbox\">"
            + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + playlist.id + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + playlist.name + "</div>";
    html += "<div class=\"" + buttonCellClazz + "\">" + "<button class=\"" + button1Clazz + "\">&#9312;</button>" + "</div>";
    html += "<div class=\"" + buttonCellClazz + "\">" + "<button class=\"" + button2Clazz + "\">&#9313;</button>" + "</div>";
    html += "</div>";

    return html;
}

function createAudiotrackDataRow(track)
{
    let activityState = track.active === true ? "active" : "inactive";

    let trackRowClazz = "audiotrack_row " + activityState;
    let idCellClazz = "audiotrack_id_cell undisplayable";
    let selectCheckerCellClazz = "select_track_check";
    let nameCellClazz = "playlist_name_cell";
    let playButtonCellClazz = "playbutton_cell";
    let playButtonClazz = "play_button";


    let html = "<div class=\"" + trackRowClazz + "\">";
    html += "<div class=\"" + selectCheckerCellClazz + "\">"
            + "<input type=\"checkbox\">"
            + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + track.track_id + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + track.track_name + "</div>";
    html += "<div class=\"" + playButtonCellClazz + "\">";
    if (track.active) {
        html += "<button class=\"" + playButtonClazz + "\">&#9658;</button>";
    }
    html += "</div>";
    html += "</div>";

    return html;
}

function createFavedArtistRow(artist)
{
    let rowClazz = "artist_data_row data_row noselect";
    let idCellClazz = "data_cell id_cell undisplayable";
    let imageCellClazz = "data_cell image_cell";
    let nameCellClazz = "data_cell name_cell";
    let buttonClazz = "data_cell button_cell";
    let smallButtonClazz = "data_cell small_button_cell";

    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + smallButtonClazz + "\">" + "&#10005;" + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">"
            + buildArtistImage(artist.image)
            + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + artist.id + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">" + artist.name + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&equiv;" + "</div>";
    html += "</div>";

    return html;
}

function createAlbumRow(artist, album, skipUnfavouriteOption)
{
    let rowClazz = "album_data_row data_row noselect";
    let idCellClazz = "data_cell id_cell undisplayable";
    let imageCellClazz = "data_cell image_cell";
    let nameCellClazz = "data_cell name_cell";
    let buttonClazz = "data_cell button_cell";
    let smallButtonClazz = "data_cell small_button_cell";

    if (skipUnfavouriteOption == true)
    {
        smallButtonClazz += " undisplayable";
    }

    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + smallButtonClazz + "\">" + "&#10005;" + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + album.id + "</div>";
    html += "<div class=\"" + imageCellClazz + "\">"
            + buildAlbumImage(album.image)
            + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">"
            + "<span class=\"little_text\">" + artist.name + ":<br/><b>"
            + album.name + "</b>, " + album.year + "</span>"
            + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&equiv;" + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&#9312;<br/>&raquo;" + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&#9313;<br/>&raquo;" + "</div>";
    html += "</div>";

    return html;
}

function createTrackRow(artist, album, track, skipUnfavouriteOption)
{
    let rowClazz = "track_data_row data_row noselect";
    let idCellClazz = "data_cell id_cell undisplayable";
    let nameCellClazz = "data_cell name_cell";
    let buttonClazz = "data_cell button_cell";
    let smallButtonClazz = "data_cell small_button_cell";

    if (skipUnfavouriteOption == true)
    {
        smallButtonClazz += " undisplayable";
    }

    let html = "<div class =\"" + rowClazz + "\">";
    html += "<div class=\"" + smallButtonClazz + "\">" + "&#10005;" + "</div>";
    html += "<div class=\"" + idCellClazz + "\">" + track.id + "</div>";
    html += "<div class=\"" + nameCellClazz + "\">"
            + "<span class=\"little_text\">" + artist.name + ":<br/>"
            + album.name + ", " + album.year + "<br/><b>"
            + track.name + "</b></span>"
            + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&#9312;<br/>&raquo;" + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&#9313;<br/>&raquo;" + "</div>";
    html += "<div class=\"" + buttonClazz + "\">" + "&#9658;" + "</div>";
    html += "</div>";

    return html;
}

///////////////////////////////////
//// methods serving playlists ////
///////////////////////////////////

function setRenamedPlayListData(id, name)
{
    document.getElementById("renamed_playlist_id").value = id;
    document.getElementById("edit_playlist_name").value = name;
    document.getElementById("edit_playlist_name").focus();
}

function cancelPlaylistRename()
{
    document.getElementById("renamed_playlist_id").value = "";
    document.getElementById("edit_playlist_name").value = "";
    setDefaultPlaylistAreaMode();
}


function selectAllPlaylists(boolean) // all, even hidden ones
{
    let checkboxes = document.getElementById("playlists")
            .getElementsByTagName("input");

    for (let i = 0; i < checkboxes.length; i++)
    {
        checkboxes[i].checked = boolean;
    }
}

function setPlaylistSelectionToDelete(boolean)
{
    let rows = document.getElementsByClassName("playlist_row");

    for (let i = 0; i < rows.length; i++)
    {
        if (!rows[i].classList.contains("undisplayable"))
        {
            rows[i].getElementsByTagName("input")[0].checked = boolean;
        }
    }
}

function filterPlaylistsList()
{
    let tmpContainer = document.createElement("pre");
    tmpContainer.innerHTML = document.getElementById("playlists_filter_input").value.toLowerCase();
    let textToFind = tmpContainer.innerHTML;

    let playlistRows = document.getElementById("playlists").getElementsByClassName("playlist_row");

    for (let i = 0; i < playlistRows.length; i++)
    {
        let playlistName = playlistRows[i].getElementsByClassName("playlist_name_cell")[0].textContent.toLowerCase().replace(/\xa0/g, " ");

        if (playlistName.indexOf(textToFind) === -1)
        {
            playlistRows[i].classList.add("undisplayable");
        } else
        {
            playlistRows[i].classList.remove("undisplayable");
        }
    }
}

function addTrackToPlaylist(trackId, playlistNumber)
{
    getTracksData(trackId).then(
            function (tracks)
            {
                if (tracks == undefined)
                {
                    return;
                }
                let html = "";
                for (let i = 0; i < tracks.length; i++)
                {
                    let track = {};
                    track.track_id = tracks[i].track.id;
                    track.active = true; // this request returns active tracks only
                    track.track_name = tracks[i].track.name;
                    html += createAudiotrackDataRow(track);
                }
                writeTracksRowsToPlaylistArea(html, playlistNumber);
            });
}

function writeTracksRowsToPlaylistArea(rowHtml, playlistNumber)
{
    let playlistIsLoaded = document.getElementById("pl" + playlistNumber + "_name").innerHTML !== "";
    let playlistArea = document.getElementById("playlist_" + playlistNumber + "_items");
    if (playlistIsLoaded)
    {
        playlistArea.innerHTML += rowHtml;
    }
    playlistArea.scrollTop = playlistArea.scrollHeight; // scrolling to the last item
    setAudiotrackRowsListeners(playlistNumber);
}

async function getTracksData(tracks_id)
{
    let formData = new FormData();
    for (let i = 0; i < arguments.length; i++)
    {
        formData.append("track_id[]", arguments[i]);
    }

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_tracks_data_by_tracks_id", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let tracks = respJson.tracks_data;
                    return tracks;
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

async function addAlbumToPlaylist(albumId, playlistNumber)
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
                    let html = "";
                    for (let i = 0; i < tracks.length; i++)
                    {
                        let track = {};
                        track.track_id = tracks[i].id;
                        track.active = true; // this request returns active tracks only
                        track.track_name = tracks[i].name;
                        html += createAudiotrackDataRow(track);
                    }
                    writeTracksRowsToPlaylistArea(html, playlistNumber);
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

function selectAllTrack(areaNumber, boolean)
{
    let selectionBoxes = document.getElementById("playlist_" + areaNumber + "_items").getElementsByTagName("input");

    for (let i = 0; i < selectionBoxes.length; i++)
    {
        selectionBoxes[i].checked = boolean;
    }
}

function getSelectedTracksMap(playlistNumber)
{
    let response = {};
    response.shifted = [];
    response.resting = [];
    let rows = document.getElementById("playlist_" + playlistNumber + "_items").getElementsByClassName("audiotrack_row");
    for (let i = 0; i < rows.length; i++)
    {
        if (rows[i].getElementsByTagName("input")[0].checked == true)
        {
            response.shifted.push(i);
        } else
        {
            response.resting.push(i);
        }
    }
    response.data = rows;
    return response;
}

function removeSelectedTracks(playlistNumber)
{
    let tracksRowsMap = getSelectedTracksMap(playlistNumber);
    let deleteMe = [];

    for (let i = 0; i < tracksRowsMap.shifted.length; i++)
    {
        let rowPosition = tracksRowsMap.shifted[i];
        let row = tracksRowsMap.data[rowPosition];
        deleteMe.push(row); // we cannot delete "on fly", it works incorrectly
    }

    for (let i = 0; i < deleteMe.length; i++)
    {
        deleteMe[i].parentNode.removeChild(deleteMe[i]);
    }
}

function copySelectedTracks(playlistNumberFrom, playlistNumberTo)
{
    let tracksRowsMap = getSelectedTracksMap(playlistNumberFrom);
    let destinationContainer = document.getElementById("playlist_" + playlistNumberTo + "_items");
    let destinationPlaylistAvailible = document.getElementById("pl" + playlistNumberTo + "_id").innerHTML !== "";

    if (destinationPlaylistAvailible)
    {
        for (let i = 0; i < tracksRowsMap.shifted.length; i++)
        {
            let rowPosition = tracksRowsMap.shifted[i];
            let row = tracksRowsMap.data[rowPosition].cloneNode(true);
            row.getElementsByTagName("input")[0].checked = false;
            destinationContainer.appendChild(row);
        }
    }

    return destinationPlaylistAvailible;
}

function moveSelectedTracks(playlistNumberFrom, playlistNumberTo)
{
    let success = copySelectedTracks(playlistNumberFrom, playlistNumberTo);
    if (success)
    {
        removeSelectedTracks(playlistNumberFrom);
    }
}

async function savePlaylist(playlistAreaNumber)
{
    let playlistId = document.getElementById("pl" + playlistAreaNumber + "_id").innerHTML;
    let playlistArea = document.getElementById("playlist_" + playlistAreaNumber + "_items");

    if (playlistId == "" || playlistId == undefined)
    {
        return;
    }

    let formData = new FormData();
    formData.append("playlist_id", playlistId);

    let tracksIdCells = playlistArea.getElementsByClassName("audiotrack_id_cell");
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

function shiftSelectedTracksUp(playlistNumber)
{
    let tracksRowsMap = getSelectedTracksMap(playlistNumber);
    let newMap = new Array(tracksRowsMap.shifted.length + tracksRowsMap.resting.length);
    let container = document.getElementById("playlist_" + playlistNumber + "_items");
    let newContainer = container.cloneNode(true);
    newContainer.innerHTML = "";

    // writing elements [being shifted] positions
    for (let i = 0; i < tracksRowsMap.shifted.length; i++)
    {
        let oldPosition = tracksRowsMap.shifted[i];
        let newPosition = oldPosition > 0 && newMap[oldPosition - 1] === undefined ? oldPosition - 1 : oldPosition;
        newMap[newPosition] = oldPosition;
    }

    // writing elements [not being shifted] positions
    let pointer = 0;
    for (let i = 0; i < tracksRowsMap.resting.length; i++)
    {
        while (newMap[pointer] !== undefined)
        {
            pointer++;
        }
        newMap[pointer] = tracksRowsMap.resting[i];
    }

    for (let i = 0; i < newMap.length; i++)
    {
        newContainer.appendChild(tracksRowsMap.data[newMap[i]].cloneNode(true));
    }

    container.replaceWith(newContainer);
}

function shiftSelectedTracksDown(playlistNumber)
{
    let tracksRowsMap = getSelectedTracksMap(playlistNumber);
    let newMap = new Array(tracksRowsMap.shifted.length + tracksRowsMap.resting.length);

    let container = document.getElementById("playlist_" + playlistNumber + "_items");
    let newContainer = container.cloneNode(true);
    newContainer.innerHTML = "";

    // writing elements [being shifted] positions
    for (let i = tracksRowsMap.shifted.length - 1; i >= 0; i--)
    {
        let oldPosition = tracksRowsMap.shifted[i];
        let newPosition = oldPosition < newMap.length - 1
                && newMap[oldPosition + 1] === undefined ? oldPosition + 1 : oldPosition;
        newMap[newPosition] = oldPosition;
    }

    // writing elements [not being shifted] positions
    let pointer = 0;
    for (let i = 0; i < tracksRowsMap.resting.length; i++)
    {
        while (newMap[pointer] !== undefined)
        {
            pointer++;
        }
        newMap[pointer] = tracksRowsMap.resting[i];
    }

    for (let i = 0; i < newMap.length; i++)
    {
        newContainer.appendChild(tracksRowsMap.data[newMap[i]].cloneNode(true));
    }

    container.replaceWith(newContainer);
}

function shiftSelectedTracksTop(playlistNumber)
{
    let tracksRowsMap = getSelectedTracksMap(playlistNumber);

    let container = document.getElementById("playlist_" + playlistNumber + "_items");
    let newContainer = container.cloneNode(true);
    newContainer.innerHTML = "";

    // writing elements [being shifted] positions
    for (let i = 0; i < tracksRowsMap.shifted.length; i++)
    {
        newContainer.appendChild(tracksRowsMap.data[tracksRowsMap.shifted[i]].cloneNode(true));
    }

    // writing elements [not being shifted] positions
    for (let i = 0; i < tracksRowsMap.resting.length; i++)
    {
        newContainer.appendChild(tracksRowsMap.data[tracksRowsMap.resting[i]].cloneNode(true));
    }

    container.replaceWith(newContainer);
}

function shiftSelectedTracksBottom(playlistNumber)
{
    let tracksRowsMap = getSelectedTracksMap(playlistNumber);

    let container = document.getElementById("playlist_" + playlistNumber + "_items");
    let newContainer = container.cloneNode(true);
    newContainer.innerHTML = "";

    // writing elements [not being shifted] positions
    for (let i = 0; i < tracksRowsMap.resting.length; i++)
    {
        newContainer.appendChild(tracksRowsMap.data[tracksRowsMap.resting[i]].cloneNode(true));
    }

    // writing elements [being shifted] positions
    for (let i = 0; i < tracksRowsMap.shifted.length; i++)
    {
        newContainer.appendChild(tracksRowsMap.data[tracksRowsMap.shifted[i]].cloneNode(true));
    }

    container.replaceWith(newContainer);
}

//////////////////////////////////////
//////////////////////////////////////

function buildArtistImage(imageProperty)
{
    return "<img src=\"" + window.artistImagePath + (imageProperty !== null ? imageProperty : "default") + "\">";
}

function buildAlbumImage(imageProperty)
{
    return "<img src=\"" + window.albumImagePath + (imageProperty !== null ? imageProperty : "default") + "\">";
}

/////////////////////////////////////
/////////////////////////////////////

function setCurrentFavAreaAction(searchType, searchParam)
{
    let pageNumberField = document.getElementById("page_number");
    window.pageControl.searchString = document.getElementById("search_string").value;

    switch (searchType)
    {
        case "artists":
        {
            window.pageControl.action = getFavouriteArtists;
            break;
        }
        case "albums":
        {
            window.pageControl.action = getFavouriteAlbums;
            break;
        }
        case "tracks":
        {
            window.pageControl.action = getFavouriteTracks;
            break;
        }
        case "artist albums":
        {
            window.pageControl.action = function () {
                getArtistAlbums(searchParam);
            };
            break;
        }
        case "album tracks":
        {
            window.pageControl.action = function () {
                getAlbumTracks(searchParam);
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
            };
    document.getElementById("goto_forward_button").onclick =
            function ()
            {
                pageNumberField.value = pageNumberField.value - 0 + 1;
                if (window.pageControl.action != null)
                {
                    window.pageControl.action();
                }
            };
    document.getElementById("apply_page_number").onclick =
            function ()
            {
                if (window.pageControl.action != null)
                {
                    window.pageControl.action();
                }
            };
}

/////////////////////////////////
//// Dialogue window control ////
/////////////////////////////////

function showMessage(text, messageType, headingMessage)
{
    // setting window decoration depending on message type
    let dialogWindow = document.getElementById("dialog_window");
    dialogWindow.className = "";

    switch (messageType)
    {
        case window.MessageType.message:
            dialogWindow.className = "message";
            break;
        case window.MessageType.warning:
            dialogWindow.className = "warning";
            break;
        case window.MessageType.error:
            dialogWindow.className = "error";
            break;
    }

    if (headingMessage !== undefined && headingMessage !== null)
    {
        dialogWindow.getElementsByTagName("h3")[0].innerHTML = headingMessage;
    }

    dialogWindow.getElementsByTagName("p")[0].innerHTML = text;
    dialogWindow.showModal();
}


///////////////////////
//// playing music ////
///////////////////////

async function playTrack(trackId, playlistAreaNumber, trackNumber)
{
    getTracksData(trackId).then(
            function (tracksData)
            {
                if (tracksData == undefined || tracksData.length == 0)
                {
                    return;
                    // no data found for the given id OR an error occured
                }

                let audio = document.getElementById("player");
                let fileName = tracksData[0].track.file;
                if (fileName !== null)
                {
                    audio.src = window.trackFilePath + fileName;
                    audio.load();
                    audio.play();

                    // setting data for player so it will be able to navigate through the playlist items
                    document.getElementById("playlistAreaNumber").innerHTML = playlistAreaNumber;
                    document.getElementById("playlistItemNumber").innerHTML = trackNumber;
                }
            });
}

function playNextTrack()
{
    let playlistAreaNumber = document.getElementById("playlistAreaNumber").innerHTML;
    let container = document.getElementById("playlist_" + playlistAreaNumber + "_items");
    let currentTrackPosition = document.getElementById("playlistItemNumber").innerHTML;
    let nextTrackPosition = currentTrackPosition - 0 + 1;

    let activeTrackRows = container.getElementsByClassName("audiotrack_row active");

    if (activeTrackRows.length >= nextTrackPosition)
    {
        let trackId = activeTrackRows[nextTrackPosition].getElementsByClassName("audiotrack_id_cell")[0].innerHTML;
        playTrack(trackId, playlistAreaNumber, nextTrackPosition);
    }
}

function playPreviousTrack()
{
    let playlistAreaNumber = document.getElementById("playlistAreaNumber").innerHTML;
    let container = document.getElementById("playlist_" + playlistAreaNumber + "_items");
    let currentTrackPosition = document.getElementById("playlistItemNumber").innerHTML;
    let previousTrackPosition = currentTrackPosition - 1;

    let activeTrackRows = container.getElementsByClassName("audiotrack_row active");

    if (previousTrackPosition >= 0)
    {
        let trackId = activeTrackRows[previousTrackPosition].getElementsByClassName("audiotrack_id_cell")[0].innerHTML;
        playTrack(trackId, playlistAreaNumber, previousTrackPosition);
    }
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
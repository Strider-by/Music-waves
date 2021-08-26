function init()
{
    appendMessagePart();
    requestUserPlaylists();
    initPlayer();

    let playlistSelector = document.getElementById("playlists_list");
    playlistSelector.addEventListener("change",
            function ()
            {
                useSelectedPlaylist();
            });

    let randomOrderCheckBox = document.getElementById("option_random");
    randomOrderCheckBox.addEventListener("change",
            function ()
            {
                recordRandomOrderOption();
            });

    let repeatOnEndCheckBox = document.getElementById("option_repeat");
    repeatOnEndCheckBox.addEventListener("change",
            function ()
            {
                recordRepeatOnEndOption();
            });


}

function initPlayer()
{
    window.player = {};

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
        play();
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
        updateProgressBar();
    });
    audio.addEventListener("volumechange", function () {
        soundBar.style.width = (audio.volume) * 100 + "%";
        recordVolumeLevelChange();
    });
    audio.addEventListener("loadedmetadata", function () {
        showTotalTime();
    });
    audio.addEventListener("ended", function () {
        audio.src = audio.src; // jumpToTime works up only untill track ends without audio src change. Weird!
        playNextTrack();
    });
    playPrevButton.addEventListener("click", function () {
        playPreviousTrack();
    });
    playNextButton.addEventListener("click", function () {
        playNextTrack();
    });
    progressContainer.addEventListener("click", function (evt)
    {
        jumpToTime(evt);
    });
    soundContainer.addEventListener("click", function (evt)
    {
        jumpToSoundLevel(evt);
    });

    function recordVolumeLevelChange()
    {
        setCookie("volume", audio.volume);
    }

    function loadVolumeLevel()
    {
        let cookieVolumeLvl = getCookie("volume");
        if (cookieVolumeLvl != undefined && cookieVolumeLvl != null)
        {
            audio.volume = cookieVolumeLvl;
        }
    }


////////////////////////////////////////////////////////////////////////////////

    loadVolumeLevel();
    loadRandomOrderOption();
    loadRepeatOnEndOption();
}

////////////////////////////////////////////////////////////////////////////////

function recordRandomOrderOption()
{
    setCookie("random_order", isRandomOrder());
}

function loadRandomOrderOption()
{
    let cookieRandowOrderOption = getCookie("random_order");
    if (cookieRandowOrderOption != undefined && cookieRandowOrderOption != null)
    {
        document.getElementById("option_random").checked = (cookieRandowOrderOption == "true");
        console.log(cookieRandowOrderOption);
    }
}

function recordRepeatOnEndOption()
{
    setCookie("repeat_on_end", isRepeatOnEnd());
}

function loadRepeatOnEndOption()
{
    let cookieRepeatOnEndOption = getCookie("repeat_on_end");
    if (cookieRepeatOnEndOption != undefined && cookieRepeatOnEndOption != null)
    {
        document.getElementById("option_repeat").checked = (cookieRepeatOnEndOption == "true");
    }
}

function recordUsedPlaylistId(playlistId)
{
    setCookie("playlist_used", playlistId);
}

function loadLastUsedPlaylist()
{
    let usedPlaylistId = getCookie("playlist_used");
    if (usedPlaylistId != undefined && usedPlaylistId != null)
    {
        selectPlaylistInSelectBox(usedPlaylistId);
    }
}

function playlistPresentsAmongSelectOptions(playlistId)
{
    let playlistSelector = document.getElementById("playlists_list");
    for(let i = 0; i < playlistSelector.options.length; i++)
    {
        if(playlistSelector.options[i].value == playlistId)
        {
            return true;
        }
    }
    
    return false;
}

function selectPlaylistInSelectBox(playlistId)
{
    let playlistSelector = document.getElementById("playlists_list");
    for(let i = 0; i < playlistSelector.options.length; i++)
    {
        if(playlistSelector.options[i].value == playlistId)
        {
           playlistSelector.selectedIndex = i;
           useSelectedPlaylist();
           return;
        }
    }
}

////////////////////////////////////////////////////////////////////////////////

function isRandomOrder()
{
    return document.getElementById("option_random").checked;
}

function isRepeatOnEnd()
{
    return document.getElementById("option_repeat").checked;
}

function getCurrentRowNumber()
{
    return document.getElementById("playlistItemNumber").innerHTML - 0;
}

function getCurrentTrackPlaylistId()
{
    return document.getElementById("playlistId").innerHTML - 0;
}

function isTrackLastInPlaylist()
{
    let rowsNumber = document.getElementsByClassName("track_row").length;
    return getCurrentRowNumber() + 1 === rowsNumber;
}

function isTrackFirstInPlaylist()
{
    return getCurrentRowNumber() == 0;
}

function isSamePlaylist()
{
    let playedTrackPlayList = getCurrentTrackPlaylistId();
    let playlistSelector = document.getElementById("playlists_list");
    let pickedPlaylistId = playlistSelector.options[playlistSelector.selectedIndex].value;
    return playedTrackPlayList == pickedPlaylistId;
}

function play()
{
    let audio = document.getElementById("player");
    if(audio.src != "")
    {
        audio.load();
        audio.play();
    }
    else
    {
        playNextTrack();
    }
}

function playRandom()
{
    let rows = document.getElementsByClassName("track_row");
    if (rows.length === 0)
    {
        return; // empty playlist
    } else
    {
        let rowToPlay = Math.floor(Math.random() * rows.length);
        rows[rowToPlay].getElementsByClassName("play_track_button")[0].click();
    }
}

function playFirstTrack()
{
    let rows = document.getElementsByClassName("track_row");
    if (rows.length === 0)
    {
        return; // empty playlist
    } else
    {
        let rowToPlay = 0;
        rows[rowToPlay].getElementsByClassName("play_track_button")[0].click();
    }
}

function playNextTrack()
{
    if (isRandomOrder())
    {
        playRandom();
    } else
    {
        if (!isSamePlaylist())
        {
            playFirstTrack();
        } else
        {
            if (isTrackLastInPlaylist())
            {
                if (isRepeatOnEnd())
                {
                    playFirstTrack();
                }
            } else
            {
                let rows = document.getElementsByClassName("track_row");
                let rowToPlay = getCurrentRowNumber() + 1;
                rows[rowToPlay].getElementsByClassName("play_track_button")[0].click();
            }
        }
    }
}

function playPreviousTrack()
{
    if (isRandomOrder())
    {
        playRandom();
    } else
    {
        if (!isSamePlaylist())
        {
            playFirstTrack();
        } else
        {
            if (isTrackFirstInPlaylist())
            {
                playFirstTrack();
            } else
            {
                let rows = document.getElementsByClassName("track_row");
                let rowToPlay = getCurrentRowNumber() - 1;
                rows[rowToPlay].getElementsByClassName("play_track_button")[0].click();
            }
        }
    }
}

function updateProgressBar()
{
    if ((window.player.duration - 0) === 0 || !Number.isFinite((window.player.duration - 0)))
    {
        return;
    }

    let percent = document.getElementById("player").currentTime / window.player.duration;
    document.getElementById("progress_bar").style.width = percent * 100 + "%";
}

function jumpToTime(evt)
{
    if ((window.player.duration - 0) === 0 || !Number.isFinite((window.player.duration - 0)))
    {
        return;
    }
    let audio = document.getElementById("player");
    let percent = evt.offsetX / document.getElementById("progress_container").offsetWidth;
    audio.currentTime = percent * window.player.duration;
}

function jumpToSoundLevel(evt)
{
    let audio = document.getElementById("player");
    let percent = evt.offsetX / document.getElementById("sound_container").offsetWidth;
    audio.volume = percent;
}

function showTotalTime()
{
    let audio = document.getElementById("player");
    let totalTime = document.getElementById("totalTime");
    let duration = audio.duration;
    if (!isNaN(duration) && isFinite(duration))
    {
        window.player.duration = audio.duration;
        totalTime.innerHTML = secondsToHMS(audio.duration);
    } else
    {
        getDuration(audio.src, function (duration)
        {
            window.player.duration = duration;
            totalTime.innerHTML = secondsToHMS(duration);
        });
    }
}

function setZeroTotalTime()
{
    let totalTime = document.getElementById("totalTime");
    totalTime.innerHTML = secondsToHMS(0);
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

////////////////////////////////////////////////////////////////////////////////

function cleanPlaylistsList()
{
    let selector = document.getElementById("playlists_list");

    while (selector.firstChild)
    {
        selector.removeChild(selector.firstChild);
    }
}

function cleanPlaylistArea()
{
    let container = document.getElementById("tracks");

    while (container.firstChild)
    {
        container.removeChild(container.firstChild);
    }
}

function buildPlaylistOptionItem(playlist)
{
    let option = document.createElement("option");
    option.innerHTML = playlist.name;
    option.value = playlist.id;

    return option;
}

function buildTrackItem(track, rowNumber)
{
    let row = document.createElement("div");
    let idField = document.createElement("div");
    let rowNumberField = document.createElement("div");
    let nameField = document.createElement("div");
    let playButton = document.createElement("div");

    nameField.innerHTML = track.track_name;
    rowNumberField.innerHTML = rowNumber;
    idField.innerHTML = track.track_id;
    playButton.innerHTML = "&#9658;"; // "play" symbol

    row.appendChild(idField);
    row.appendChild(rowNumberField);
    row.appendChild(nameField);
    row.appendChild(playButton);

    row.classList.add("track_row");
    nameField.classList.add("track_name");
    playButton.classList.add("play_track_button");
    playButton.classList.add("noselect");
    idField.classList.add("track_id");
    idField.classList.add("undisplayable"); // track id is hidden
    rowNumberField.classList.add("row_number");
    rowNumberField.classList.add("undisplayable"); // row number is hidden

    playButton.addEventListener("click",
            function (obj)
            {
                playTrack(obj.target.parentNode);
                obj.target.parentNode.scrollIntoView();
            });
            
    nameField.addEventListener("dblclick",
            function (obj)
            {
                playTrack(obj.target.parentNode);
                obj.target.parentNode.scrollIntoView();
            });

    return row;
}

function unhighlightPlaylistItems()
{
    let rows = document.getElementById("tracks").getElementsByClassName("track_row");
    for (let i = 0; i < rows.length; i++)
    {
        rows[i].classList.remove("being_played_now");
    }
}

function highlightPlaylistItemBeingPlayed(trackRow)
{
    trackRow.classList.add("being_played_now");
}

function fillTrackPropertyFields(track)
{
    let artistName = track.artist.name;
    let artistImage = track.artist.image;
    let albumName = track.album.name;
    let albumImage = track.album.image;

    let artistImageContainer = document.getElementById("artist_block").getElementsByClassName("image_container")[0];

    artistImageContainer.innerHTML = "";
    if (artistImage !== null)
    {
        let image = document.createElement("img");
        image.src = window.artistImagePath + artistImage;
        artistImageContainer.appendChild(image);
    }

    let albumImageContainer = document.getElementById("album_block").getElementsByClassName("image_container")[0];

    albumImageContainer.innerHTML = "";
    if (albumImage !== null)
    {
        let image = document.createElement("img");
        image.src = window.albumImagePath + albumImage;
        albumImageContainer.appendChild(image);
    }

    let artistNameContainer = document.getElementById("artist_block").getElementsByClassName("prop_field")[0];
    artistNameContainer.innerHTML = artistName;

    let albumNameContainer = document.getElementById("album_block").getElementsByClassName("prop_field")[0];
    albumNameContainer.innerHTML = albumName;

    let trackNameContainer = document.getElementById("track_name_block");
    trackNameContainer.innerHTML = track.track.name;
}



//////////////////////
//// xhr requests ////
//////////////////////

async function requestUserPlaylists()
{
    cleanPlaylistsList();

    let formData = new FormData();
    formData.append("escape", true);

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_user_playlists", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;
        let selector = document.getElementById("playlists_list");

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let playlists = respJson.playlists;
                    // creating empty option
                    let emptyOption = document.createElement("option");
                    emptyOption.value = -1;
                    selector.appendChild(emptyOption);
                    // creating real options
                    for (let i = 0; i < playlists.length; i++)
                    {
                        let playlist = playlists[i];
                        let option = buildPlaylistOptionItem(playlist);
                        selector.appendChild(option);
                    }
                    loadLastUsedPlaylist();
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
        console.log("exeption catched");
        console.log(ex);
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}


async function useSelectedPlaylist()
{
    cleanPlaylistArea();

    let playlistSelector = document.getElementById("playlists_list");
    let playlistId = playlistSelector.options[playlistSelector.selectedIndex].value;
    recordUsedPlaylistId(playlistId);
    if (playlistId == -1) // "empty" option selected
    {
        return;
    }

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
                    let tracks = respJson.playlist_items;
                    let tracksContainer = document.getElementById("tracks");

                    // + highlight active track if playlist item from this playlist is active
                    let hightlightRequired = document.getElementById("playlistId").innerHTML == playlistId;
                    let activeRowNumber = document.getElementById("playlistItemNumber").innerHTML;

                    for (let i = 0; i < tracks.length; i++)
                    {
                        let track = tracks[i];
                        let trackElement = buildTrackItem(track, i);
                        tracksContainer.appendChild(trackElement);

                        if (hightlightRequired && activeRowNumber == i)
                        {
                            highlightPlaylistItemBeingPlayed(trackElement);
                            trackElement.focus();
                        }
                    }
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
        console.log("exception catched");
        console.log(ex);
        showMessage(window.textbundle.requestFailed, window.MessageType.error);
    }
}

async function playTrack(trackNodeCalledToBePlayed)
{
    let trackId = trackNodeCalledToBePlayed.getElementsByClassName("track_id")[0].innerHTML;
    let nodeNumber = trackNodeCalledToBePlayed.getElementsByClassName("row_number")[0].innerHTML;


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
                    setZeroTotalTime();
                    play();

                    // setting data for player so it will be able to navigate through the playlist items
                    // setting track row number
                    document.getElementById("playlistItemNumber").innerHTML = nodeNumber;
                    // setting playlist id
                    let playlistSelector = document.getElementById("playlists_list");
                    let playlistId = playlistSelector.options[playlistSelector.selectedIndex].value;
                    document.getElementById("playlistId").innerHTML = playlistId;

                    unhighlightPlaylistItems();
                    highlightPlaylistItemBeingPlayed(trackNodeCalledToBePlayed);
                    trackNodeCalledToBePlayed.focus();
                    fillTrackPropertyFields(tracksData[0]);
                }
            });
}

async function getTracksData(/*tracks_id_are_being_awaited*/)
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


/* "INFINITY AUDIO LENGTH" CHROME BUG WORKAROUND */
function getDuration(url, next)
{
    let _player = new Audio(url);
    _player.addEventListener("durationchange", function (e) {
        if (this.duration !== Infinity)
        {
            let duration = this.duration;
            _player.remove();
            next(duration);
        }
        ;
    }, false);
    _player.load();
    _player.currentTime = 24 * 60 * 60; // fake big time
    _player.volume = 0;
    _player.play();
    // waiting...
}

//// test - trying to swap players "on fly"
//function chromeBugWorkaround()
//{
//    let currentPlayer = document.getElementById("player");
//    let container = document.getElementById("music_player");
//    
//    let _player = new Audio();
//    _player.src = currentPlayer.src;
//    _player.classList = currentPlayer.classList;
//    _player.id = currentPlayer.id;
//    
//    _player.addEventListener("durationchange", function (e) {
//        if (this.duration !== Infinity)
//        {
//            /*_player.pause();
//            _player.volume = currentPlayer.volume;
//            _player.currentTime = currentPlayer.currentTime;
//            container.replaceChild(_player, currentPlayer);
//            _player.play();*/
//            _player.pause();
//            _player.src = _player.src;
//            _player.currentTime = 50;
//            _player.play();
//        }
//        ;
//    }, false);
//    
//    _player.load();
//    _player.currentTime = 24 * 60 * 60; // fake big time
//    _player.volume = 1;
//    _player.play();
//    // waiting...
//}




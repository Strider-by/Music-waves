function initTracksPart()
{
    let saveButton = document.getElementById("save_track_being_edited");
    saveButton.onclick = doTrackUpdate;


    let cancelButton = document.getElementById("cancel_track_edit_button");
    cancelButton.onclick = cleanseTrackEditFields;


    let createButton = document.getElementById("create_new_track_button");
    createButton.onclick = doCreateTrack;

    let cleanseCreateInstanceBlockButton = document.getElementById("cleanse_new_track_block_button");
    cleanseCreateInstanceBlockButton.onclick = cleanseNewTrackBlock;

    let newTrackGenresFilterInput = document.getElementById("new_track_genre_filter");
    newTrackGenresFilterInput.onkeyup = updateNewInstanceGenresList;

    let editTrackGenresFilterInput = document.getElementById("edit_track_genre_filter");
    editTrackGenresFilterInput.onkeyup = updateEditInstanceGenresList;

    let uploadTrackFileButton = document.getElementById("upload_track_file");
    uploadTrackFileButton.onclick = function () {
        document.getElementById("track_file_input").click();
    };

    let fileInput = document.getElementById("track_file_input");
    fileInput.onchange = function () {
        uploadTrackFile("track_file_input");
    };

    document.getElementById("edit_track_state").value = -1;
    document.getElementById("delete_track_being_edited").addEventListener("long-press",
            function ()
            {
                deleteTrack();
            });

    let shiftTrackUpButton = document.getElementById("shift_track_number_up_button");
    shiftTrackUpButton.onclick = shiftTrackNumberUp;

    let shiftTrackDownButton = document.getElementById("shift_track_number_down_button");
    shiftTrackDownButton.onclick = shiftTrackNumberDown;

    updateNewInstanceGenresList();
    updateEditInstanceGenresList();
}

/////////////////////////
//  SEARCH DATA BLOCK  //
/////////////////////////

function createTrackDataRow(track)
{
    let row;
    let idCellCls = "id_cell";
    let trackNumberCellCls = "track_number_cell";
    let nameCellCls = "name_cell";
    let rowCls = "data_row";

    let activeRowCls = "active_row";
    let inactiveRowCls = "inactive_row";
    let editElemCls = "button_cell";

    row = '<div class="' + rowCls + " "
            + (track.active === true ? activeRowCls : inactiveRowCls) + '">';
    row += '<div class="' + idCellCls + '">' + track.id + '</div>';
    row += '<div class="' + trackNumberCellCls + '">' + track.number + '</div>';
    row += '<div class="' + nameCellCls + '">' + track.name + '</div>';
    row += '<div class="' + editElemCls + '">' + "<button onclick='editTrackRecord("
            + track.id + ");'>&equiv;</button>" + '</div>';
    row += '</div>';

    return row;
}

function createTracksListTableHeader()
{
    let row;
    let idCellCls = "id_cell";
    let trackNumberCellCls = "track_number_cell";
    let nameCellCls = "name_cell";
    let rowCls = "data_row";
    let editElemCls = "button_cell";
    let headerRowCls = "table_header";

    row = '<div class="' + rowCls + " " + headerRowCls + '">';
    row += '<div class="' + idCellCls + '">' + "id" + '</div>';
    row += '<div class="' + trackNumberCellCls + '">' + "#" + '</div>';
    row += '<div class="' + nameCellCls + '">' + window.labels.name + '</div>';
    row += '<div class="' + editElemCls + '">' + '</div>';
    row += '</div>';

    return row;
}


function getTrackSearchTypeValue()
{
    let variants = document.getElementsByName("tracks_search_type");

    for (let i = 0; i < variants.length; i++)
    {
        if (variants[i].checked)
        {
            return variants[i].value;
        }
    }
}

function getTrackSearchActivityValue()
{
    let variants = document.getElementsByName("tracks_search_active");

    for (let i = 0; i < variants.length; i++)
    {
        if (variants[i].checked)
        {
            return variants[i].value;
        }
    }
}

async function requestTracksList()
{
    clearTracksSearchArea();

    let formData = new FormData();
    formData.append("album_id", document.getElementById("edit_album_id").value);

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_album_tracks", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let tableHeader = createTracksListTableHeader();
                    addRowToTracksSearchArea(tableHeader);
                    let tracks = respJson.tracks;
                    for (i = 0; i < tracks.length; i++)
                    {
                        let track = tracks[i];
                        let row = createTrackDataRow(track);
                        addRowToTracksSearchArea(row);
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

function addRowToTracksSearchArea(row)
{
    document.getElementById("tracks_search_results").innerHTML += row;
}

function clearTracksSearchArea()
{
    document.getElementById("tracks_search_results").innerHTML = "";
}


//////////////////////////
//   UPLOAD TRACK FILE  //
//////////////////////////

async function uploadTrackFile(input_id)
{

    let input = document.getElementById(input_id);

    let formData = new FormData();
    let image = input.files[0];

    formData.append("image", image);
    formData.append("id", document.getElementById("edit_track_id").value);

    try
    {
        let response = await fetch(ctx + "/ajax?command=upload_track_file", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let newFileName = respJson.trackFileName;
                    setTrackFileName(newFileName);
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // insufficient rights
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                    break;
                case 3: // something went wrong with the passed image
                    showMessage(window.textbundle.invalidImage, window.MessageType.error);
                    break;
                case 4: // something went wrong with the passed WITH IMAGE data
                    showMessage(window.textbundle.invalidImageAttendantData, window.MessageType.error);
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

function setTrackFileName(fileName)
{
    document.getElementById("edit_track_filename").value = fileName;
}

function unsetTrackFileName()
{
    document.getElementById("edit_track_filename").value = " --- ";
}


/////////////////////////
//   EDIT DATA BLOCK   //
/////////////////////////

async function editTrackRecord(id)
{
    let formData = new FormData();
    formData.append("id", id);

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_track_by_id", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let track = respJson.track;
                    showSelectedTrack(track);
                    unhighlightTrackRows();
                    setTimeout(function () {
                        highlightSelectedTrackRow(id);
                    }, 50);
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // insufficient right
                    showMessage(window.textbundle.insufficientRights, window.MessageType.error);

                case 4: // we failed to find that track
                    showMessage(window.textbundle.failedToFind, window.MessageType.error);
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

function showSelectedTrack(track)
{
    document.getElementById("edit_track_name").value = track.name;
    document.getElementById("edit_track_state").value = (track.active ? 0 : 1);
    document.getElementById("edit_track_id").value = track.id;
    document.getElementById("edit_track_number").value = track.number;
    document.getElementById("edit_track_genre").value = track.genre;
    document.getElementById("edit_track_filename").value =
            track.file != null ? track.file : " --- ";

    document.getElementById("edit_track_area").classList.remove("undisplayable");
}

function cleanseTrackEditFields()
{
    unhighlightTrackRows();
    document.getElementById("edit_track_name").value = "";
    document.getElementById("edit_track_state").value = -1;
    document.getElementById("edit_track_id").value = "";
    document.getElementById("edit_track_area").classList.add("undisplayable");

}

async function doTrackUpdate()
{
    let instanceId = document.getElementById("edit_track_id").value;
    let instanceName = document.getElementById("edit_track_name").value;
    let instanceActivityState = document.getElementById("edit_track_state").value;
    //let instanceGenre = document.getElementById("edit_track_genre").value;
    let instanceGenre = 1; // genres aren't in use now

    let formData = new FormData();
    formData.append("id", instanceId);
    formData.append("name", instanceName);
    formData.append("active", instanceActivityState == 0 ? true : false);
    formData.append("genre", instanceGenre);

    try
    {
        let response = await fetch(ctx + "/ajax?command=update_track", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    cleanseTrackEditFields();
                    unsetTrackFileName();
                    requestTracksList();
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // name already in use
                    showMessage(window.textbundle.nameAlreadyInUse, window.MessageType.error);
                    break;
                case 4: // insufficient right
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

async function shiftTrackNumberUp()
{
    let trackId = document.getElementById("edit_track_id").value;

    let formData = new FormData();
    formData.append("id", trackId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=shift_up_track_number", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    cleanseTrackEditFields();
                    requestTracksList();
                    editTrackRecord(trackId);
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);

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

async function shiftTrackNumberDown()
{
    let trackId = document.getElementById("edit_track_id").value;

    let formData = new FormData();
    formData.append("id", trackId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=shift_down_track_number", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    cleanseTrackEditFields();
                    requestTracksList();
                    editTrackRecord(trackId);
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);

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

function highlightSelectedTrackRow(rowId)
{
    unhighlightTrackRows();

    let searchArea = document.getElementById("tracks_search_results");
    let idCells = searchArea.getElementsByClassName("id_cell");
    let item; // the element we search for
    for (let cell of idCells)
    {
        if (cell.innerHTML == rowId)
        {
            item = cell;
            break;
        }
    }

    if (item != undefined) // if we find what we were searching for
    {
        item.parentElement.classList.add("highlighted");
    }
}

function unhighlightTrackRows()
{
    let searchArea = document.getElementById("tracks_search_results");
    let highlighted = searchArea.getElementsByClassName("highlighted");

    for (let item of highlighted)
    {
        item.classList.remove("highlighted");
    }
}

///////////////////////////////
// CREATE NEW INSTANCE BLOCK //
///////////////////////////////

function cleanseNewTrackBlock()
{
    document.getElementById("new_track_name").value = "";
    document.getElementById("new_track_state").value = 0;
}

async function doCreateTrack()
{
    let instanceName = document.getElementById("new_track_name").value;
    let instanceActivityState = document.getElementById("new_track_state").value;
    let instanceAlbumId = document.getElementById("edit_album_id").value;
    // let instanceGenreId = document.getElementById("new_track_genre").value;
    let instanceGenreId = 1; // temporary, genres now isn't in use

    let formData = new FormData();
    formData.append("name", instanceName);
    formData.append("active", instanceActivityState == 0 ? true : false);
    formData.append("album_id", instanceAlbumId);
    formData.append("genre_id", instanceGenreId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=create_track", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    cleanseNewTrackBlock();
                    requestTracksList();
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);
                    break;
                case 3: // name already in use
                    showMessage(window.textbundle.nameAlreadyInUse, window.MessageType.error);
                    break;
                case 4: // insufficient right
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

async function requestGenresList(input)
{
    cleanseNewTrackGenreSelectElement();
    let searchString = input.value;

    let formData = new FormData();
    formData.append("search_type", 0); // all
    formData.append("search_activity", 0); // all
    formData.append("search_string", searchString);
    formData.append("search_offset", 0);
    formData.append("search_limit", 1000); // ~= unlimited

    let response = await fetch(ctx + "/ajax?command=get_genres_list", {method: "POST", body: formData});
    let respJson = await response.json();
    let appResponseStatus = respJson.appResponseCode;

    if (response.status >= 200 && response.status < 400 && appResponseStatus == 0)
    {
        return respJson.genres;
    } else
    {
        return null;
    }
}

function updateNewInstanceGenresList()
{
    // genres feature is disabled for now
//    requestGenresList(document.getElementById("new_track_genre_filter"))
//            .then(
//                    function (genres)
//                    {
//                        cleanseNewTrackGenreSelectElement();
//                        for (let i = 0; i < genres.length; i++)
//                        {
//                            let option = createGenreOptionRow(genres[i]);
//                            addOptionToNewTrackGenreSelectElement(option);
//                        }
//                    }
//            );
}

function updateEditInstanceGenresList()
{
    // genres feature is disabled for now
//    requestGenresList(document.getElementById("edit_track_genre_filter"))
//            .then(
//                    function (genres)
//                    {
//                        cleanseEditTrackGenreSelectElement();
//                        for (let i = 0; i < genres.length; i++)
//                        {
//                            let option = createGenreOptionRow(genres[i]);
//                            addOptionToEditTrackGenreSelectElement(option);
//                        }
//                    }
//            );
}

function createGenreOptionRow(genre)
{
    return "<option value=\"" + genre.id + "\">" + genre.name + "</option>";
}

function addOptionToNewTrackGenreSelectElement(option)
{
    document.getElementById("new_track_genre").innerHTML += option;
}

function addOptionToEditTrackGenreSelectElement(option)
{
    document.getElementById("edit_track_genre").innerHTML += option;
}

function cleanseNewTrackGenreSelectElement()
{
    document.getElementById("new_track_genre").innerHTML = "";
}

function cleanseEditTrackGenreSelectElement()
{
    document.getElementById("edit_track_genre").innerHTML = "";
}

///////////////////////////////
//// DELETE INSTANCE BLOCK ////
///////////////////////////////

async function deleteTrack()
{
    let instanceId = document.getElementById("edit_track_id").value;

    let formData = new FormData();
    formData.append("id", instanceId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=delete_track", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    cleanseTrackEditFields();
                    requestTracksList();
                    break;
                case 1: // fail, not logged in
                    showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                    break;
                case 2: // invalid data passed
                    showMessage(window.textbundle.invalidData, window.MessageType.error);

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


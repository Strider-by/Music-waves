function initArtistsPart()
{

    let searchButon = document.getElementById("search_artists");
    searchButon.onclick = function ()
    {
        gotoFirstArtistsPage();
        requestArtistsList();
    };

    let firstPageButton = document.getElementById("1st_artist_page");
    firstPageButton.onclick = function ()
    {
        gotoFirstArtistsPage();
        requestArtistsList();
    };

    let prevPageButton = document.getElementById("prev_artist_page");
    prevPageButton.onclick = function ()
    {
        gotoPrevArtistPage();
        requestArtistsList();
    };

    let nextPageButton = document.getElementById("next_artist_page");
    nextPageButton.onclick = function ()
    {
        gotoNextArtistPage();
        requestArtistsList();
    };

    document.getElementById("artists_page_number").onkeyup = requestArtistsList;

    document.getElementById("artists_search_field").addEventListener("keyup", function (e)
    {
        if (e.keyCode === 13) // Enter key pressed
        {
            requestArtistsList();
        }
    });

    let saveButton = document.getElementById("save_artist_being_edited");
    saveButton.onclick = doArtistUpdate;


    let cancelButton = document.getElementById("cancel_artist_edit_button");
    cancelButton.onclick = cleanseArtistEditFields;


    let createButton = document.getElementById("create_new_artist_button");
    createButton.onclick = doCreateArtist;

    let cleanseCreateInstanceBlockButton = document.getElementById("cleanse_new_artist_block_button");
    cleanseCreateInstanceBlockButton.onclick = cleanseNewArtistBlock;

    let cleanseSearchFieldButton = document.getElementById("cleanse_artist_search_field");
    cleanseSearchFieldButton.onclick = cleanseArtistSearchField;

    let uploadImageButton = document.getElementById("upload_artist_image");
    uploadImageButton.onclick = function () {
        document.getElementById("artist_image_input").click();
    };

    let imageInput = document.getElementById("artist_image_input");
    imageInput.onchange = function () {
        uploadArtistImage("artist_image_input");
    };

    let reloadButton = document.getElementById("refresh_artists_list_button");
    reloadButton.onclick = requestArtistsList;

    document.getElementById("edit_artist_state").value = -1;
    document.getElementById("delete_artist_being_edited").addEventListener("long-press",
            function ()
            {
                deleteArtist();
            });

    let useArtistButton = document.getElementById("use_artist_edit_button");
    useArtistButton.onclick = useCurrentArtist;
}



/////////////////////////
//  SEARCH DATA BLOCK  //
/////////////////////////

function createArtistDataRow(artist)
{
    let row;
    let idCellCls = "id_cell";
    let nameCellCls = "name_cell";
    let rowCls = "data_row";

    let activeRowCls = "active_row";
    let inactiveRowCls = "inactive_row";
    let editElemCls = "button_cell";

    row = '<div class="' + rowCls + " "
            + (artist.active === true ? activeRowCls : inactiveRowCls) + '">';
    row += '<div class="' + idCellCls + '">' + artist.id + '</div>';
    row += '<div class="' + nameCellCls + '">' + artist.name + '</div>';
    row += '<div class="' + editElemCls + '">' + "<button onclick='editArtistRecord("
            + artist.id + ");'>&equiv;</button>" + '</div>';
    row += '</div>';

    return row;
}


function getArtistSearchTypeValue()
{
    let variants = document.getElementsByName("artists_search_type");

    for (let i = 0; i < variants.length; i++)
    {
        if (variants[i].checked)
        {
            return variants[i].value;
        }
    }
}

function getArtistSearchActivityValue()
{
    let variants = document.getElementsByName("artists_search_active");

    for (let i = 0; i < variants.length; i++)
    {
        if (variants[i].checked)
        {
            return variants[i].value;
        }
    }
}

async function requestArtistsList()
{
    clearArtistsSearchArea();
    let searchString = document.getElementById("artists_search_field").value;
    let offsetValue = (document.getElementById("artists_page_number").value - 1) * 12; // 12 is our default limit;

    let formData = new FormData();
    formData.append("search_type", getArtistSearchTypeValue());
    formData.append("search_activity", getArtistSearchActivityValue());
    formData.append("search_string", searchString);
    formData.append("search_offset", offsetValue);
    formData.append("search_limit", 12); // got to be enough

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_artists_list", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success

                    let artists = respJson.artists;
                    for (i = 0; i < artists.length; i++)
                    {
                        let artist = artists[i];
                        let row = createArtistDataRow(artist);
                        addRowToArtistsSearchArea(row);
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

function addRowToArtistsSearchArea(row)
{
    document.getElementById("artists_search_results").innerHTML += row;
}

function clearArtistsSearchArea()
{
    document.getElementById("artists_search_results").innerHTML = "";
}

function gotoFirstArtistsPage()
{
    document.getElementById("artists_page_number").value = 1;
}

function gotoPrevArtistPage()
{
    let elem = document.getElementById("artists_page_number");
    elem.value = elem.value == 1 ? elem.value : elem.value - 1;
}

function gotoNextArtistPage()
{
    let elem = document.getElementById("artists_page_number");
    elem.value = elem.value - 0 + 1;
}

function cleanseArtistSearchField()
{
    document.getElementById("artists_search_field").value = "";
    document.getElementById("artists_search_field").focus();
}


//////////////////////////
//  UPLOAD IMAGE BLOCK  //
//////////////////////////

async function uploadArtistImage(input_id)
{
    let input = document.getElementById(input_id);

    let formData = new FormData();
    let image = input.files[0];

    formData.append("image", image);
    formData.append("id", document.getElementById("edit_artist_id").value);

    try
    {
        let response = await fetch(ctx + "/ajax?command=upload_artist_image", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let newPicName = respJson.artistImage;
                    setArtistBigImage(newPicName);
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

function setArtistBigImage(image_name)
{
    if(image_name != null)
    {
        document.getElementById("artist_big_image").src = window.artistsImgDir + image_name;
    }
}

function unsetArtistBigImage()
{
    document.getElementById("artist_big_image").src = "";
}


/////////////////////////
//   EDIT DATA BLOCK   //
/////////////////////////
async function editArtistRecord(id)
{
    let formData = new FormData();
    formData.append("id", id);

    try {
        let response = await fetch(ctx + "/ajax?command=get_artist_by_id", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    let artist = respJson.artist;
                    showSelectedArtist(artist);
                    unhighlightArtistRows();
                    setTimeout(function () {
                        highlightSelectedArtistRow(id);
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

                case 4: // we failed to find that artist
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

function showSelectedArtist(artist)
{
    document.getElementById("edit_artist_name").value = artist.name;
    document.getElementById("artist_big_image").src =
             artist.image != null ? window.artistsImgDir + artist.image : "";
    document.getElementById("edit_artist_state").value = (artist.active ? 0 : 1);
    document.getElementById("edit_artist_id").value = artist.id;

    document.getElementById("edit_artist_area").classList.remove("undisplayable");
}

function cleanseArtistEditFields()
{
    unhighlightArtistRows();

    document.getElementById("edit_artist_name").value = "";
    document.getElementById("edit_artist_state").value = -1;
    document.getElementById("edit_artist_id").value = "";
    unsetArtistBigImage();

    document.getElementById("edit_artist_area").classList.add("undisplayable");
}

async function doArtistUpdate()
{
    let instanceId = document.getElementById("edit_artist_id").value;
    let instanceName = document.getElementById("edit_artist_name").value;
    let instanceActivityState = document.getElementById("edit_artist_state").value;

    let formData = new FormData();
    formData.append("id", instanceId);
    formData.append("name", instanceName);
    formData.append("active", instanceActivityState == 0 ? true : false);

    try
    {
        let response = await fetch(ctx + "/ajax?command=update_artist", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    cleanseArtistEditFields();
                    unsetArtistBigImage();
                    requestArtistsList();
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


function useCurrentArtist()
{
    if (document.getElementById("edit_artist_id").value != "")
    {
        activateAlbumsTab();
        requestAlbumsList();
    }
}

function highlightSelectedArtistRow(rowId)
{
    unhighlightArtistRows();

    let searchArea = document.getElementById("artists_search_results");
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

function unhighlightArtistRows()
{
    let searchArea = document.getElementById("artists_search_results");
    let highlighted = searchArea.getElementsByClassName("highlighted");

    for (let item of highlighted)
    {
        item.classList.remove("highlighted");
    }
}

///////////////////////////////
// CREATE NEW INSTANCE BLOCK //
///////////////////////////////

function cleanseNewArtistBlock()
{
    document.getElementById("new_artist_name").value = "";
    document.getElementById("new_artist_state").value = 0;
}

async function doCreateArtist()
{
    let instanceName = document.getElementById("new_artist_name").value;
    let instanceActivityState = document.getElementById("new_artist_state").value;

    let formData = new FormData();
    formData.append("name", instanceName);
    formData.append("active", instanceActivityState == 0 ? true : false);

    try
    {
        let response = await fetch(ctx + "/ajax?command=create_artist", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    cleanseNewArtistBlock();
                    requestArtistsList();
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



///////////////////////////////
//// DELETE INSTANCE BLOCK ////
///////////////////////////////

async function deleteArtist()
{
    let instanceId = document.getElementById("edit_artist_id").value;

    let formData = new FormData();
    formData.append("id", instanceId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=delete_artist", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    cleanseArtistEditFields();
                    unsetArtistBigImage();
                    requestArtistsList();
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
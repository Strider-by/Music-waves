function initAlbumsPart()
{

    let searchButon = document.getElementById("search_albums");
    searchButon.onclick = function ()
    {
        gotoFirstAlbumsPage();
        requestAlbumsList();
    };

    let firstPageButton = document.getElementById("1st_album_page");
    firstPageButton.onclick = function ()
    {
        gotoFirstAlbumsPage();
        requestAlbumsList();
    };

    let prevPageButton = document.getElementById("prev_album_page");
    prevPageButton.onclick = function ()
    {
        gotoPrevAlbumPage();
        requestAlbumsList();
    };

    let nextPageButton = document.getElementById("next_album_page");
    nextPageButton.onclick = function ()
    {
        gotoNextAlbumPage();
        requestAlbumsList();
    };

    document.getElementById("albums_page_number").onkeyup = requestAlbumsList;

    document.getElementById("albums_search_field_name").addEventListener("keyup", function (e)
    {
        if (e.keyCode === 13) // Enter key pressed
        {
            requestAlbumsList();
        }
    });

    document.getElementById("albums_search_field_year").addEventListener("keyup", function (e)
    {
        if (e.keyCode === 13) // Enter key pressed
        {
            requestAlbumsList();
        }
    });

    let saveButton = document.getElementById("save_album_being_edited");
    saveButton.onclick = doAlbumUpdate;


    let cancelButton = document.getElementById("cancel_album_edit_button");
    cancelButton.onclick = cleanseAlbumEditFields;


    let createButton = document.getElementById("create_new_album_button");
    createButton.onclick = doCreateAlbum;

    let cleanseCreateInstanceBlockButton = document.getElementById("cleanse_new_album_block_button");
    cleanseCreateInstanceBlockButton.onclick = cleanseNewAlbumBlock;

    let cleanseSearchFieldButton = document.getElementById("cleanse_album_search_field");
    cleanseSearchFieldButton.onclick = cleanseAlbumSearchField;

    let uploadImageButton = document.getElementById("upload_album_image");
    uploadImageButton.onclick = function () {
        document.getElementById("album_image_input").click();
    };

    let imageInput = document.getElementById("album_image_input");
    imageInput.onchange = function () {
        uploadAlbumImage("album_image_input");
    };

    let reloadButton = document.getElementById("refresh_albums_list_button");
    reloadButton.onclick = requestAlbumsList;

    document.getElementById("edit_album_state").value = -1;
    document.getElementById("delete_album_being_edited").addEventListener("long-press",
            function ()
            {
                deleteAlbum();
            });
    
    let useAlbumButton = document.getElementById("use_album_edit_button");
    useAlbumButton.onclick = useCurrentAlbum;
    
    document.getElementById("new_album_year").value = new Date().getFullYear();
}

/////////////////////////
//  SEARCH DATA BLOCK  //
/////////////////////////

function createAlbumDataRow(album)
{
    let row;
    let idCellCls = "id_cell";
    let nameCellCls = "name_cell";
    let yearCellCls = "year_cell";
    let rowCls = "data_row";

    let activeRowCls = "active_row";
    let inactiveRowCls = "inactive_row";
    let editElemCls = "button_cell";

    row = '<div class="' + rowCls + " "
            + (album.active === true ? activeRowCls : inactiveRowCls) + '">';
    row += '<div class="' + idCellCls + '">' + album.id + '</div>';
    row += '<div class="' + yearCellCls + '">' + album.year + '</div>';
    row += '<div class="' + nameCellCls + '">' + album.name + '</div>';
    row += '<div class="' + editElemCls + '">' + "<button onclick='editAlbumRecord("
            + album.id + ");'>&equiv;</button>" + '</div>';
    row += '</div>';

    return row;
}


async function requestAlbumsList()
{
    clearAlbumsSearchArea();

    let albumName = document.getElementById("albums_search_field_name").value;
    let albumYear = document.getElementById("albums_search_field_year").value;
    let artistId = document.getElementById("edit_artist_id").value;
    let offsetValue = (document.getElementById("albums_page_number").value - 1) * 12; // 12 is our default limit;

    let formData = new FormData();
    formData.append("search_type", 0); // "contains" search type
    formData.append("search_activity", 0); //  0 == all, both active and inactive
    formData.append("name", albumName);
    formData.append("year", albumYear);
    formData.append("artist_id", artistId);
    formData.append("search_limit", 12);
    formData.append("search_offset", offsetValue);


    let response = await fetch(ctx + "/ajax?command=get_albums_list", {method: "POST", body: formData});
    let respJson = await response.json();
    let appResponseStatus = respJson.appResponseCode;

    if (response.status >= 200 && response.status < 400)
    {
        // Successfully connected to server
        switch (appResponseStatus)
        {
            case 0: // success

                let albums = respJson.albums;
                for (i = 0; i < albums.length; i++)
                {
                    let album = albums[i];
                    let row = createAlbumDataRow(album);
                    addRowToAlbumsSearchArea(row);
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
}

function addRowToAlbumsSearchArea(row)
{
    document.getElementById("albums_search_results").innerHTML += row;
}

function clearAlbumsSearchArea()
{
    document.getElementById("albums_search_results").innerHTML = "";
}

function gotoFirstAlbumsPage()
{
    document.getElementById("albums_page_number").value = 1;
}

function gotoPrevAlbumPage()
{
    let elem = document.getElementById("albums_page_number");
    elem.value = elem.value == 1 ? elem.value : elem.value - 1;
}

function gotoNextAlbumPage()
{
    let elem = document.getElementById("albums_page_number");
    elem.value = elem.value - 0 + 1;
}

function cleanseAlbumSearchField()
{
    document.getElementById("albums_search_field_name").value = "";
    document.getElementById("albums_search_field_year").value = "";
    document.getElementById("albums_search_field_name").focus();
    requestAlbumsList();
}

//////////////////////////
//  UPLOAD IMAGE BLOCK  //
//////////////////////////

async function uploadAlbumImage(input_id)
{
    let input = document.getElementById(input_id);

    let formData = new FormData();
    let image = input.files[0];

    formData.append("image", image);
    formData.append("id", document.getElementById("edit_album_id").value);


    let response = await fetch(ctx + "/ajax?command=upload_album_image", {method: "POST", body: formData});
    let respJson = await response.json();
    let appResponseStatus = respJson.appResponseCode;

    if (response.status >= 200 && response.status < 400)
    {
        // Successfully connected to server
        switch (appResponseStatus)
        {
            case 0: // success
                let newPicName = respJson.albumImage;
                setAlbumBigImage(newPicName);
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

}

function setAlbumBigImage(image_name)
{
    if(image_name != null)
    {
        document.getElementById("album_big_image").src = window.albumsImgDir + image_name;
    }
}

function unsetAlbumBigImage()
{
    document.getElementById("album_big_image").src = "";
}


/////////////////////////
//   EDIT DATA BLOCK   //
/////////////////////////
async function editAlbumRecord(id)
{
    let formData = new FormData();
    formData.append("id", id);

    let response = await fetch(ctx + "/ajax?command=get_album_by_id", {method: "POST", body: formData});
    let respJson = await response.json();
    let appResponseStatus = respJson.appResponseCode;

    if (response.status >= 200 && response.status < 400)
    {
        // Successfully connected to server
        switch (appResponseStatus)
        {
            case 0: // success
                let album = respJson.album;
                showSelectedAlbum(album);
                unhighlightAlbumRows();
                setTimeout(function(){highlightSelectedAlbumRow(id);}, 50);
                break;
            case 1: // fail, not logged in
                showMessage(window.textbundle.notLoggedIn, window.MessageType.error);
                break;
            case 2: // invalid data passed
                showMessage(window.textbundle.invalidData, window.MessageType.error);
                break;
            case 3: // insufficient right
                showMessage(window.textbundle.insufficientRights, window.MessageType.error);
                
            case 4: // we failed to find that album
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
}

function showSelectedAlbum(album)
{
    //highlightSelectedAlbumRow(album.id);
    
    document.getElementById("edit_album_name").value = album.name;
    document.getElementById("album_big_image").src =
            album.image != null ? window.albumsImgDir + album.image : "";
    document.getElementById("edit_album_state").value = (album.active ? 0 : 1);
    document.getElementById("edit_album_id").value = album.id;
    document.getElementById("edit_album_year").value = album.year;
    
    document.getElementById("edit_album_area").classList.remove("undisplayable");
}

function cleanseAlbumEditFields()
{
    unhighlightAlbumRows();
    
    document.getElementById("edit_album_name").value = "";
    document.getElementById("edit_album_state").value = -1;
    document.getElementById("edit_album_id").value = "";
    unsetAlbumBigImage();
    
    
    document.getElementById("edit_album_area").classList.add("undisplayable");
}

async function doAlbumUpdate()
{
    
    let instanceId = document.getElementById("edit_album_id").value;
    let instanceName = document.getElementById("edit_album_name").value;
    let instanceYear = document.getElementById("edit_album_year").value;
    let instanceArtist = document.getElementById("edit_artist_id").value;
    let instanceActivityState = document.getElementById("edit_album_state").value;

    let formData = new FormData();
    formData.append("id", instanceId);
    formData.append("name", instanceName);
    formData.append("year", instanceYear);
    formData.append("artist", instanceArtist);
    formData.append("active", instanceActivityState == 0 ? true : false);

    let response = await fetch(ctx + "/ajax?command=update_album", {method: "POST", body: formData});
    let respJson = await response.json();
    let appResponseStatus = respJson.appResponseCode;

    if (response.status >= 200 && response.status < 400)
    {
        // Successfully connected to server
        switch (appResponseStatus)
        {
            case 0: // success
                cleanseAlbumEditFields();
                unsetAlbumBigImage();
                requestAlbumsList();
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
}

function useCurrentAlbum()
{
    if(document.getElementById("edit_album_id").value != "")
    {
        
        activateTracksTab();
        requestTracksList();
    }
}

function highlightSelectedAlbumRow(rowId)
{
    unhighlightAlbumRows();
    
    let searchArea = document.getElementById("albums_search_results");
    let idCells = searchArea.getElementsByClassName("id_cell");
    let item; // the element we search for
    for (let cell of idCells) 
    {
        if(cell.innerHTML == rowId)
        {
            item = cell;
            break;
        }
    }
    
    if(item != undefined) // if we find what we were searching for
    {
        item.parentElement.classList.add("highlighted");
    }
}

function unhighlightAlbumRows()
{
    let searchArea = document.getElementById("albums_search_results");
    let highlighted = searchArea.getElementsByClassName("highlighted");
    
    for (let item of highlighted) 
    {
        item.classList.remove("highlighted");
    }
}

///////////////////////////////
// CREATE NEW INSTANCE BLOCK //
///////////////////////////////

function cleanseNewAlbumBlock()
{
    document.getElementById("new_album_name").value = "";
    document.getElementById("new_album_year").value = new Date().getFullYear();
    document.getElementById("new_album_state").value = 0;
    
}

async function doCreateAlbum()
{
    
    let instanceName = document.getElementById("new_album_name").value;
    let instanceYear = document.getElementById("new_album_year").value;
    let instanceArtist = document.getElementById("edit_artist_id").value;
    let instanceActivityState = document.getElementById("new_album_state").value;

    let formData = new FormData();
    formData.append("name", instanceName);
    formData.append("year", instanceYear);
    formData.append("artist", instanceArtist);
    formData.append("active", instanceActivityState == 0 ? true : false);

    let response = await fetch(ctx + "/ajax?command=create_album", {method: "POST", body: formData});
    let respJson = await response.json();
    let appResponseStatus = respJson.appResponseCode;

    if (response.status >= 200 && response.status < 400)
    {
        // Successfully connected to server
        switch (appResponseStatus)
        {
            case 0: // success
                cleanseNewAlbumBlock();
                requestAlbumsList();
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


///////////////////////////////
//// DELETE INSTANCE BLOCK ////
///////////////////////////////

async function deleteAlbum()
{
    let instanceId = document.getElementById("edit_album_id").value;

    let formData = new FormData();
    formData.append("id", instanceId);

    try
    {
        let response = await fetch(ctx + "/ajax?command=delete_album", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    cleanseAlbumEditFields();
                    unsetAlbumBigImage();
                    requestAlbumsList();
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
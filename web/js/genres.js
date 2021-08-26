window.onload = init;

function init()
{
    let searchButon = document.getElementById("search");
    searchButon.onclick = function ()
    {
        gotoFirstPage();
        requestData();
    };

    let firstPageButton = document.getElementById("1st_page");
    firstPageButton.onclick = function ()
    {
        gotoFirstPage();
        requestData();
    };

    let prevPageButton = document.getElementById("prev_page");
    prevPageButton.onclick = function ()
    {
        gotoPrevPage();
        requestData();
    };

    let nextPageButton = document.getElementById("next_page");
    nextPageButton.onclick = function ()
    {
        gotoNextPage();
        requestData();
    };

    document.getElementById("page_number").onchange = requestData;
    document.getElementById("search_field").addEventListener("keyup", function (e)
    {
        if (e.keyCode === 13) // Enter key pressed
        {
            requestData();
        }
    });

    let saveButton = document.getElementById("save_record_being_edited");
    saveButton.onclick = doUpdate;

    let cancelButton = document.getElementById("cancel_edit_button");
    cancelButton.onclick = cleanseEditFields;


    let createButton = document.getElementById("create_new_instance");
    createButton.onclick = doCreate;

    let cleanseCreateInstanceBlockButton = document.getElementById("cleanse_new_instance_block");
    cleanseCreateInstanceBlockButton.onclick = cleanseNewInstanceBlock;

    let cleanseSearchFieldButton = document.getElementById("cleanse_search_field");
    cleanseSearchFieldButton.onclick = cleanseSearchField;

    readWarnings();
    requestData();
}

/////////////////////////
//  SEARCH DATA BLOCK  //
/////////////////////////

function createDataRow(id, name, active)
{
    let row;
    let idCellCls = "id_cell";
    let nameCellCls = "name_cell";
    let rowCls = "data_row";

    let activeRowCls = "active_row";
    let inactiveRowCls = "inactive_row";
    let editElemCls = "button_cell";
    let statusCellCls = "activity_status_cell";

    row = '<div class="' + rowCls + " "
            + (active === true ? activeRowCls : inactiveRowCls) + '">';
    row += '<div class="' + idCellCls + '">' + id + '</div>';
    row += '<div class="' + nameCellCls + '">' + name + '</div>';
    row += '<div class="' + statusCellCls + '">'
            + (active === true ? window.textbundle.active : window.textbundle.inactive) + '</div>';
    row += '<div class="' + editElemCls + '">' + "<button onclick='editRecord(" + id + ", \"" + name + "\", " + active + ");'>&equiv;</button>" + '</div>';
    row += '</div>';

    return row;
}


function getSearchTypeValue()
{
    let variants = document.getElementsByName("search_type");

    for (let i = 0; i < variants.length; i++)
    {
        if (variants[i].checked)
        {
            return variants[i].value;
        }
    }
}

function getSearchActivityValue()
{
    let variants = document.getElementsByName("search_active");

    for (let i = 0; i < variants.length; i++)
    {
        if (variants[i].checked)
        {
            return variants[i].value;
        }
    }
}

async function requestData()
{
    clearSearchArea();
    hideSearchWarning();
    let searchString = document.getElementById("search_field").value;
    let offsetValue = (document.getElementById("page_number").value - 1) * 10; // 10 is our default limit;

    let formData = new FormData();
    formData.append("search_type", getSearchTypeValue());
    formData.append("search_activity", getSearchActivityValue());
    formData.append("search_string", searchString);
    formData.append("search_offset", offsetValue);
    formData.append("search_limit", 10); // got to be enough

    try
    {
        let response = await fetch(ctx + "/ajax?command=get_genres_list", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success

                    let genres = respJson.genres;
                    for (i = 0; i < genres.length; i++)
                    {
                        let genre = genres[i];
                        let row = createDataRow(genre.id, genre.name, genre.active);
                        addRowToSearchArea(row);
                    }
                    break;
                case 1: // fail, not logged in
                    setSearchWarning(window.warnings.notLoggedIn);
                    showSearchWarning();
                    break;
                case 2: // invalid data passed
                    setSearchWarning(window.warnings.invalidData);
                    showSearchWarning();
                    break;
                case 3: // insufficient right
                    setSearchWarning(window.warnings.insufficientRights);
                    showSearchWarning();
                    break;
                case 520: // server side error
                    setSearchWarning(window.warnings.serverSideError);
                    showSearchWarning();
                    break;
            }
        } else
        {
            setSearchWarning(window.warnings.requestFailed);
            showSearchWarning();
        }
    } catch (ex)
    {
        setSearchWarning(window.warnings.requestFailed);
        showSearchWarning();
    }
}

function addRowToSearchArea(row)
{
    document.getElementById("search_result").innerHTML += row;
}

function clearSearchArea()
{
    document.getElementById("search_result").innerHTML = "";
}

function gotoFirstPage()
{
    document.getElementById("page_number").value = 1;
}

function gotoPrevPage()
{
    let elem = document.getElementById("page_number");
    elem.value = elem.value == 1 ? elem.value : elem.value - 1;
}

function gotoNextPage()
{
    let elem = document.getElementById("page_number");
    elem.value = elem.value - 0 + 1;
}

function cleanseSearchField()
{
    document.getElementById("search_field").value = "";
    document.getElementById("search_field").focus();
    gotoFirstPage();
    requestData();
}

function showSearchWarning()
{
    let wrn = document.getElementById("search_warning");
    wrn.classList.remove("invisible");
}

function hideSearchWarning()
{
    let wrn = document.getElementById("search_warning");
    wrn.classList.add("invisible");
}

function setSearchWarning(value)
{
    let wrn = document.getElementById("search_warning");
    wrn.title = value;
}

/////////////////////////
//   EDIT DATA BLOCK   //
/////////////////////////
function editRecord(id, name, activityState)
{
    document.getElementById("edit_id").value = id;
    document.getElementById("edit_name").value = name;
    document.getElementById("edit_state").value = (activityState == true ? 0 : 1);
}

function cleanseEditFields()
{
    document.getElementById("edit_name").value = "";
    document.getElementById("edit_id").value = "";
    document.getElementById("edit_state").value = -1;
}

function cancelDataEditing()
{
    closeDataForEditing();
    cleanseEditFields();
}

async function doUpdate()
{
    hideSaveWarning();
    let instanceId = document.getElementById("edit_id").value;
    let instanceName = document.getElementById("edit_name").value;
    let instanceActivityState = document.getElementById("edit_state").value;

    let formData = new FormData();
    formData.append("id", instanceId);
    formData.append("name", instanceName);
    formData.append("active", instanceActivityState == 0 ? true : false);

    try
    {
        let response = await fetch(ctx + "/ajax?command=update_genre", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    cleanseEditFields();
                    requestData();
                    break;
                case 1: // fail, not logged in
                    setSaveWarning(window.warnings.notLoggedIn);
                    showSaveWarning();
                    break;
                case 2: // invalid data passed
                    setSaveWarning(window.warnings.invalidData);
                    showSaveWarning();
                    break;
                case 3: // name already in use
                    setSaveWarning(window.warnings.nameAlreadyInUse);
                    showSaveWarning();
                    break;
                case 4: // insufficient right
                    setSaveWarning(window.warnings.insufficientRights);
                    showSaveWarning();
                    break;
                case 520: // server side error
                    setSaveWarning(window.warnings.serverSideError);
                    showSaveWarning();
                    break;
            }
        } else
        {
            setSearchWarning(window.warnings.requestFailed);
            showSearchWarning();
        }
    } catch (ex)
    {
        setSearchWarning(window.warnings.requestFailed);
        showSearchWarning();
    }
}

function showSaveWarning()
{
    let wrn = document.getElementById("edit_block_save_warning");
    wrn.classList.remove("invisible");
}

function hideSaveWarning()
{
    let wrn = document.getElementById("edit_block_save_warning");
    wrn.classList.add("invisible");
}

function setSaveWarning(value)
{
    let wrn = document.getElementById("edit_block_save_warning");
    wrn.title = value;
}

////////////////////////////
// CREATE NEW GENRE BLOCK //
////////////////////////////

function cleanseNewInstanceBlock()
{
    document.getElementById("new_instance_name").value = "";
    document.getElementById("new_instance_state").value = 0;
    hideCreateWarning();
}

async function doCreate()
{
    hideCreateWarning();
    let instanceName = document.getElementById("new_instance_name").value;
    let instanceActivityState = document.getElementById("new_instance_state").value;

    let formData = new FormData();
    formData.append("name", instanceName);
    formData.append("active", instanceActivityState == 0 ? true : false);

    try
    {
        let response = await fetch(ctx + "/ajax?command=create_genre", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    cleanseNewInstanceBlock();
                    requestData();
                    break;
                case 1: // fail, not logged in
                    setCreateWarning(window.warnings.notLoggedIn);
                    showCreateWarning();
                    break;
                case 2: // invalid data passed
                    setCreateWarning(window.warnings.invalidData);
                    showCreateWarning();
                    break;
                case 3: // name already in use
                    setCreateWarning(window.warnings.nameAlreadyInUse);
                    showCreateWarning();
                    break;
                case 4: // insufficient right
                    setCreateWarning(window.warnings.insufficientRights);
                    showCreateWarning();
                    break;
                case 520: // server side error
                    setCreateWarning(window.warnings.serverSideError);
                    showCreateWarning();
                    break;
            }
        } else
        {
            setSearchWarning(window.warnings.requestFailed);
            showSearchWarning();
        }

    } catch (ex)
    {
        setSearchWarning(window.warnings.requestFailed);
        showSearchWarning();
    }
}

function showCreateWarning()
{
    let wrn = document.getElementById("new_instance_block_create_warning");
    wrn.classList.remove("invisible");
}

function hideCreateWarning()
{
    let wrn = document.getElementById("new_instance_block_create_warning");
    wrn.classList.add("invisible");
}

function setCreateWarning(value)
{
    let wrn = document.getElementById("new_instance_block_create_warning");
    wrn.title = value;
}

////////////////////////////////////
// "INIT MESSAGE VARIABLES" BLOCK //
////////////////////////////////////

function readWarnings()
{
    let warnings = {};
    warnings.notLoggedIn = document.getElementById("not_logged").value;
    warnings.requestFailed = document.getElementById("request_failed").value;
    warnings.invalidData = document.getElementById("invalid_data").value;
    warnings.insufficientRights = document.getElementById("insufficient_rights").value;
    warnings.serverSideError = document.getElementById("server_side_error").value;
    warnings.nameAlreadyInUse = document.getElementById("genre_name_already_in_use").value;

    window.warnings = warnings;

    let warningsOrigin = document.getElementById("text_bundle");
    warningsOrigin.parentNode.removeChild(warningsOrigin);
}
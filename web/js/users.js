///////////////////////
/////   General   /////
///////////////////////

function run()
{
    appendMessagePart();
    setFormButtonsListeners();
    requestUsersList();
    setSearchFieldsListeners();
    cancelEdit();
}

function setFormButtonsListeners()
{
    document.getElementById("clean_filter_fields_button").onclick = cleanSearchFillterFields;
    document.getElementById("cancel_button").onclick = cancelEdit;
    document.getElementById("save_button").onclick = updateUserData;
    document.getElementById("first_page_button").onclick = gotoFirstPage;
    document.getElementById("previous_page_button").onclick = gotoPrevPage;
    document.getElementById("next_page_button").onclick = gotoNextPage;
    document.getElementById("load_list_button").onclick = requestUsersList;
}

function setSearchFieldsListeners()
{
    document.getElementById("id_filter").addEventListener("input", gotoFirstPage);
    document.getElementById("email_filter").addEventListener("input", gotoFirstPage);
    document.getElementById("nickname_filter").addEventListener("input", gotoFirstPage);
    document.getElementById("role_filter").addEventListener("input", gotoFirstPage);
    document.getElementById("date_filter").addEventListener("input", gotoFirstPage);
}

function cleanSearchFillterFields()
{
    document.getElementById("id_filter").value = "";
    document.getElementById("email_filter").value = "";
    document.getElementById("nickname_filter").value = "";
    document.getElementById("role_filter").value = -1;
    document.getElementById("date_filter").value = "";
    gotoFirstPage();
}

function activateProfileArea()
{
    document.getElementById("user_profile").classList.add("active");
    setProfileElementsEnabled(true);
}

function deactivateProfileArea()
{
    let area = document.getElementById("user_profile");
    area.classList.remove("active");
    let inputs = area.getElementsByTagName("input");

    for (let i = 0; i < inputs.length; i++)
    {
        inputs.item(i).value = "";
    }

    document.getElementById("profile_role").value = -1;
    document.getElementById("avatar").innerHTML = "";
    setProfileElementsEnabled(false);

}

function setProfileElementsEnabled(bool)
{
    let area = document.getElementById("user_profile");
    let inputs = area.getElementsByTagName("input");

    for (let i = 0; i < inputs.length; i++)
    {
        inputs.item(i).disabled = !bool;
    }

    document.getElementById("profile_role").disabled = !bool;
}

//////////////////////
/////   Search   /////
//////////////////////

function initSearchResultsElements()
{
    setDataRowButtonsListeners();
    setDataRowsHoverHighlight();
}

function setDataRowButtonsListeners()
{
    let buttons = document.getElementById("search_results")
            .getElementsByClassName("button_cell");

    for (let i = 0; i < buttons.length; i++)
    {
        let button = buttons.item(i);
        button.addEventListener('click', function ()
        {
            selectUser(button);
        }, false);
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

function selectUser(caller)
{
    unselectRows();
    let dataRow = caller.parentNode;
    dataRow.classList.add("selected_row");
    let userId = dataRow.getElementsByClassName("id_cell").item(0).innerHTML;
    requestUserData(userId);
}

function unselectRows()
{
    let selectedRows = document.getElementById("search_results")
            .getElementsByClassName("selected_row");

    for (let i = 0; i < selectedRows.length; i++)
    {
        let row = selectedRows.item(i);
        row.classList.remove("selected_row");
    }
}

function highlightRow(row)
{
    row.classList.add("highlighted");
}

function unhighlightRow(row)
{
    row.classList.remove("highlighted");
}

async function requestUsersList()
{
    clearSearchResultsArea();

    let id = document.getElementById("id_filter").value;
    let date = document.getElementById("date_filter").value;
    let email = document.getElementById("email_filter").value;
    let nickname = document.getElementById("nickname_filter").value;
    let role = document.getElementById("role_filter").value;

    let limit = 16; // fits perfectly in our form
    let pageNumber = document.getElementById("page_number").value;
    let offset = (pageNumber - 1) * limit;

    let formData = new FormData();
    formData.append("id", id);
    formData.append("date", date);
    formData.append("email", email);
    formData.append("nickname", nickname);
    formData.append("role", role);
    formData.append("search_limit", limit);
    formData.append("search_offset", offset);

    let response = await fetch(ctx + "/ajax?command=get_users_list", {method: "POST", body: formData});
    let respJson = await response.json();
    let appResponseStatus = respJson.appResponseCode;

    if (response.status >= 200 && response.status < 400)
    {
        // Successfully connected to server
        switch (appResponseStatus)
        {
            case 0: // success
                let users = respJson.users;
                for (let i = 0; i < users.length; i++)
                {
                    let user = users[i];
                    let row = createUserDataRow(user);
                    addDataRowToSearchArea(row);
                }
                initSearchResultsElements();
                highlightSelectedUser();
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
}

function clearSearchResultsArea()
{
    document.getElementById("search_results").innerHTML = "";
}

function createUserDataRow(user)
{
    let row;
    let rowClass = "data_row";
    let idCellClass = "id_cell";
    let emailCellClass = "email_cell";
    let nicknameCellClass = "nickname_cell";
    let roleCellClass = "role_cell";
    let dateCellClass = "date_cell";
    let buttonClass = "button_cell";

    row = '<div class="' + rowClass + '">';
    row += '<div class="' + idCellClass + '">' + user.id + '</div>';
    row += '<div class="' + emailCellClass + '">' + user.email + '</div>';
    row += '<div class="' + nicknameCellClass + '">' + user.nickname + '</div>';
    row += '<div class="' + roleCellClass + '">' + parseRoleDatabaseEquivalent(user.role) + '</div>';
    row += '<div class="' + dateCellClass + '">' + user.registerDate + '</div>';
    row += '<button class="' + buttonClass + '">' + "&equiv;" + '</button>';
    row += '</div>';

    return row;
}

function addDataRowToSearchArea(row)
{
    document.getElementById("search_results").innerHTML += row;
}

function gotoFirstPage()
{
    document.getElementById("page_number").value = 1;
    requestUsersList();
}

function gotoPrevPage()
{
    let pageNumElement = document.getElementById("page_number");
    let currPageValue = pageNumElement.value;
    pageNumElement.value = currPageValue > 1 ? currPageValue - 1 : currPageValue;
    requestUsersList();
}

function gotoNextPage()
{
    let currPageValue = document.getElementById("page_number").value;
    document.getElementById("page_number").value = currPageValue - 0 + 1;
    requestUsersList();
}


function highlightSelectedUser()
{
    let selectedUserId = document.getElementById("profile_id").value;
    if (selectedUserId != "")
    {
        let idCells = document.getElementById("search_results")
                .getElementsByClassName("id_cell");

        for (let i = 0; i < idCells.length; i++)
        {
            let idCell = idCells.item(i);
            if(idCell.innerHTML == selectedUserId)
            {
                idCell.parentElement.classList.add("selected_row");
                break;
            }
        }
    }
}


////////////////////
////    Edit    ////
////////////////////

function cancelEdit()
{
    deactivateProfileArea();
    unselectRows();
}

async function requestUserData(userId)
{
    let formData = new FormData();
    formData.append("id", userId);

    let response = await fetch(ctx + "/ajax?command=get_user_data", {method: "POST", body: formData});
    let respJson = await response.json();
    let appResponseStatus = respJson.appResponseCode;

    if (response.status >= 200 && response.status < 400)
    {
        // Successfully connected to server
        switch (appResponseStatus)
        {
            case 0: // success
                let user = respJson.user;
                parseUserObject(user);
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
            case 4: // user was not found
                showMessage(window.textbundle.userNotFound, window.MessageType.error);
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

function parseUserObject(user)
{
    document.getElementById("profile_id").value = user.id;
    document.getElementById("profile_email").value = user.email;
    document.getElementById("profile_nickname").value = user.nickname;

    let name = "";
    if (user.first_name != "" && user.last_name != "")
    {
        name += user.first_name + " " + user.last_name;
    } else if (user.first_name != "")
    {
        name += user.first_name;
    } else if (user.last_name != "")
    {
        name += user.last_name;
    }
    document.getElementById("profile_name").value = name;

    document.getElementById("profile_role").value = user.role;
    setUserAvatar(user.avatar);

    activateProfileArea();
}

function setUserAvatar(avatarFileName)
{
    let emptyImage = avatarFileName == "" || avatarFileName == null;
    let avatar = '<img src="' + window.avatarPath;
    avatar += emptyImage ? "default-avatar" : avatarFileName;
    avatar += '" class="avatar_pic">';

    document.getElementById("avatar").innerHTML = avatar;
}

async function updateUserData()
{
    let id = document.getElementById("profile_id").value;
    let email = document.getElementById("profile_email").value;
    let nickname = document.getElementById("profile_nickname").value;
    let role = document.getElementById("profile_role").value;

    let formData = new FormData();
    formData.append("id", id);
    formData.append("email", email);
    formData.append("nickname", nickname);
    formData.append("role", role);

    let response = await fetch(ctx + "/ajax?command=update_user_data_by_admin", {method: "POST", body: formData});
    let respJson = await response.json();
    let appResponseStatus = respJson.appResponseCode;

    if (response.status >= 200 && response.status < 400)
    {
        // Successfully connected to server
        switch (appResponseStatus)
        {
            case 0: // success
                cancelEdit();
                requestUsersList();
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
            case 4: // user wasn't found
                showMessage(window.textbundle.userNotFound, window.MessageType.error);
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
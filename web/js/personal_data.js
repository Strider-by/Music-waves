window.onload = init;

function init()
{
    var avatarInput = document.getElementById("avatar_input");
    avatarInput.onchange = function () {
        uploadAvatar("avatar_input");
    };


    var changeUserAvatarButton = document.getElementById("change_user_avatar");
    changeUserAvatarButton.onclick = function () {
        document.getElementById("avatar_input").click();
    };

    var updatePersonalDataButton = document.getElementById("sava_data");
    updatePersonalDataButton.onclick = updatePersonalData;

    readWarnings();

}

/////////////////////////
// UPLOAD AVATAR BLOCK //
/////////////////////////

async function uploadAvatar(input_id)
{
    hideAvatarWarnings();

    let input = document.getElementById(input_id);

    let formData = new FormData();
    let avatar = input.files[0];

    formData.append("avatar", avatar);

    try
    {
        let response = await fetch(ctx + "/ajax?command=change_avatar", {method: "POST", body: formData});
        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    var newPicName = respJson.avatarFile;
                    setAvatarPicture(newPicName);
                    break;
                case 1: // fail, not logged in
                    setAvatarWrn(window.warnings.notLoggedIn);
                    showAvatarWarnings();
                    break;
                case 2: // something went wrong with the passed image
                    setAvatarWrn(window.warnings.invalidImage);
                    showAvatarWarnings();
                    break;
                case 520: // server side error
                    setAvatarWrn(window.warnings.requestFailed);
                    showAvatarWarnings();
                    break;
            }
        } else
        {
            setAvatarWrn(window.warnings.requestFailed);
            showAvatarWarnings();

        }
    } catch (ex)
    {
        setAvatarWrn(window.warnings.requestFailed);
        showAvatarWarnings();
    }

}

function setAvatarPicture(avatar_name)
{
    document.getElementById("avatar_img").src = window.avatarDir + avatar_name;
}

////////////////////////////////
// UPDATE PERSONAL DATA BLOCK //
////////////////////////////////
function getSexCheckedValue()
{
    let variants = document.getElementsByName("sex");

    for (let i = 0; i < variants.length; i++)
    {
        if (variants[i].checked)
        {
            return variants[i].value;
        }
    }
}

function getCountrySelectedValue()
{
    let element = document.getElementById("country");
    return element.options[element.selectedIndex].value;
}

function getLanguageSelectedValue()
{
    let element = document.getElementById("language");
    return element.options[element.selectedIndex].value;
}

function updatePersonalData()
{
    cleanPDataWrn();
    hidePDataWarnings();

    let nicknameIsFine = validateNickname();
    let firstnameIsFine = validateFirstName();
    let lastnameIsFine = validateLastName();

    let succVerification = nicknameIsFine && firstnameIsFine && lastnameIsFine;

    if (succVerification)
    {
        // update data
        sendNewDataToServer();
    } else
    {
        if (!nicknameIsFine)
            appendPDataWrn(window.warnings.invalidNickname);
        if (!firstnameIsFine)
            appendPDataWrn(window.warnings.invalidFirstname);
        if (!lastnameIsFine)
            appendPDataWrn(window.warnings.invalidLastname);
        showPDataWarnings();
    }
}

async function sendNewDataToServer()
{
    // try-catch pair is the simpliest way to handle request error
    try
    {
        let formData = new FormData();
        formData.append("nickname", document.getElementById("nickname").value);
        formData.append("first_name", document.getElementById("first_name").value);
        formData.append("last_name", document.getElementById("last_name").value);
        formData.append("sex", getSexCheckedValue());
        formData.append("country", getCountrySelectedValue());
        formData.append("language", getLanguageSelectedValue());

        let response = await fetch(ctx + "/ajax?command=update_personal_data", {method: "POST", body: formData});

        let respJson = await response.json();
        let appResponseStatus = respJson.appResponseCode;

        if (response.status >= 200 && response.status < 400)
        {
            // Successfully connected to server
            switch (appResponseStatus)
            {
                case 0: // success
                    //document.getElementById("personal_data").submit();
                    document.location.href = ctx + "/personal-data";
                    break;
                case 1: // fail, not logged in
                    setPDataWrn(window.warnings.notLoggedIn);
                    showPDataWarnings();
                    break;
                case 2: // invalid data passed
                    setPDataWrn(window.warnings.invalidData);
                    showPDataWarnings();
                    break;
                case 520: // server side error
                    setPDataWrn(window.warnings.requestFailed);
                    showPDataWarnings();
                    break;
            }
        } else
        {
            setAvatarWrn(window.warnings.requestFailed);
            showAvatarWarnings();
        }
    } catch (ex)
    {
        setPDataWrn(window.warnings.requestFailed);
        showPDataWarnings();
    }

}



////////////////////////////////////
//  "VALIDATE ENTERED DATA" BLOCK //
////////////////////////////////////

function validateNickname()
{
    var value = document.getElementById("nickname").value;
    var pattern_view = /^[^`'\"#!@\\\/|&$?]{3,15}$/;
    var check_pattern_view = value.search(pattern_view);

    return check_pattern_view === 0;
}

function validateFirstName()
{
    var value = document.getElementById("first_name").value;
    var pattern_view = /^.{0,40}$/;
    var check_pattern_view = value.search(pattern_view);

    return check_pattern_view === 0;
}

function validateLastName()
{
    var value = document.getElementById("last_name").value;
    var pattern_view = /^.{0,40}$/;
    var check_pattern_view = value.search(pattern_view);

    return check_pattern_view === 0;
}


////////////////////////////
// WARNINGS CONTROL BLOCK //
////////////////////////////

// avatar warnings //

function switchAvatarWarnings()
{
    var wrn = document.getElementById("avatar_wrn");
    if (wrn.classList.contains("invisible"))
    {
        showAvatarWarnings();
    } else
    {
        hideAvatarWarnings();
    }
}

function cleanAvatarWrn()
{
    var wrn = document.getElementById("avatar_wrn");
    wrn.title = "";
}

function appendAvatarWrn(text)
{
    var wrn = document.getElementById("avatar_wrn");
    var tmp = wrn.title;
    tmp = tmp + text + "\n";
    wrn.title = tmp;
}

function setAvatarWrn(text)
{
    var wrn = document.getElementById("avatar_wrn");
    wrn.title = text;
}

function showAvatarWarnings()
{
    var wrn = document.getElementById("avatar_wrn");
    wrn.classList.remove("invisible");
}

function hideAvatarWarnings()
{
    var wrn = document.getElementById("avatar_wrn");
    wrn.classList.add("invisible");
}

// personal data warnings //

function switchPDataWarnings()
{
    var wrn = document.getElementById("p_data_wrn");
    if (wrn.classList.contains("invisible"))
    {
        showPDataWarnings();
    } else
    {
        hidePDataWarnings();
    }
}

function cleanPDataWrn()
{
    var wrn = document.getElementById("p_data_wrn");
    wrn.title = "";
}

function appendPDataWrn(text)
{
    var wrn = document.getElementById("p_data_wrn");
    var tmp = wrn.title;
    tmp = tmp + text + "\n";
    wrn.title = tmp;
}

function setPDataWrn(text)
{
    var wrn = document.getElementById("p_data_wrn");
    wrn.title = text;
}

function showPDataWarnings()
{
    var wrn = document.getElementById("p_data_wrn");
    wrn.classList.remove("invisible");
}

function hidePDataWarnings()
{
    var wrn = document.getElementById("p_data_wrn");
    wrn.classList.add("invisible");
}


////////////////////////////////////
// "INIT MESSAGE VARIABLES" BLOCK //
////////////////////////////////////

function readWarnings()
{
    var warnings = {};
    warnings.invalidImage = document.getElementById("avatar_warnings").value;
    warnings.notLoggedIn = document.getElementById("not_logged").value;
    warnings.invalidNickname = document.getElementById("nickname_body_warnings").value;
    warnings.invalidFirstname = document.getElementById("name_warnings").value;
    warnings.invalidLastname = document.getElementById("lastname_warnings").value;
    warnings.requestFailed = document.getElementById("request_failed").value;
    warnings.invalidData = document.getElementById("invalid_data").value;

    window.warnings = warnings;

    var warningsOrigin = document.getElementById("warnings");
    warningsOrigin.parentNode.removeChild(warningsOrigin);
}
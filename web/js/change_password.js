window.onload = init;

function init()
{
    document.getElementById("show_hidden__checkbox").onchange = switchPasswordFieldsType;
    document.getElementById("confirm__button").onclick = confirmPasswordChange;
    readMessages();
    
    var oldPasswordField = document.getElementById("password_old");
    oldPasswordField.oninput = function() { checkOldPassword("password_old", "password_old_star"); };
    
    var newPasswordField = document.getElementById("password");
    newPasswordField.oninput = function() 
    { 
        checkNewPassword("password", "password_new_star");
        checkRepeatedPassword("password", "password2", "password_repeat_star");
    };
    
    var confNewPasswordField = document.getElementById("password2");
    confNewPasswordField.oninput = function() { checkRepeatedPassword("password", "password2", "password_repeat_star"); };
}

function switchPasswordFieldsType()
{
    var showHidden = document.getElementById("show_hidden__checkbox").checked;
    
    var pswOld = document.getElementById("password_old");
    var pswNew = document.getElementById("password");
    var pswNewConf = document.getElementById("password2");

    pswOld.type = pswNew.type = pswNewConf.type = showHidden ? "text" : "password";
}

// "CONFIRM PASSWORD CHANGE" BLOCK //

function confirmPasswordChange()
{
    if (checkEnteredDataLocally())
    {
        var passwordOld = document.getElementById("password_old").value;
        var passwordNew = document.getElementById("password").value;
        var formData = new FormData();
        formData.append("old_password", passwordOld);
        formData.append("new_password", passwordNew);

        var request = new XMLHttpRequest();

        // ctx is a global variable defined inside jsp and represents context path to out application
        request.open("POST", ctx + "/ajax?command=change_password", true);
        request.send(formData);
        
        request.onload = function () {
            if (request.status >= 200 && request.status < 400)
            {
                // Successfully connected to server
                var requestResult = request.responseText;
                switch (requestResult)
                {
                    // success
                    case "0":
                        appendTip(window.messages.pswChanged);
                        break;
                    // fail: new passwords doesn't meet req.
                    case "1":
                        appendTip(window.messages.invalidNewPassword);
                        break;
                    // fail: sent orig. password does not match one in database
                    case "2":
                        appendTip(window.messages.invalidOrigPassword);
                        break;
                    // fail: user isn't logged in
                    case "3":
                        appendTip(window.messages.notLoggedIn);
                        break;
                    // fail: server side error
                    case "520":
                        appendTip(window.messages.failedToProcessRequest);
                        break;
                }
            } else
            {
                reportFailedToProcessRequest();
            }
        };
        
        request.onerror = function ()
        {
            reportFailedToProcessRequest();
        };

        cleanTipsBlock();
        showTips();
    }
}

function checkEnteredDataLocally()
{
    var pswOldState = document.getElementById("password_old").value.length > 0;
    var pswNewState = validatePassword("password");
    var pswNewConfState = 
            document.getElementById("password2").value === document.getElementById("password").value;
    
    var result = pswOldState && pswNewState && pswNewConfState;
    
    if(!result)
    {
        cleanTipsBlock();
        showTips();
    }
    
    if(!pswOldState) reportOrigPasswordsIsEmpty();
    if(!pswNewState) reportCheckNewPasword();
    if(!pswNewConfState) reportCheckConfPasword();
    
    return result;
}


/// /// /// /// /// /// ///


// "CHECK ENTERED DATA" BLOCK //

function validatePassword(element_id)
{
    var psw = document.getElementById(element_id).value;
    var pattern_view = /^.{1,45}$/;
    
    var check_pattern_view = psw.search(pattern_view);

    return check_pattern_view === 0;
}

function checkOldPassword(element_id, star_id)
{
    var result = document.getElementById(element_id).value.length > 0;

    var star = document.getElementById(star_id);
    
    switch(true)
    {
        case(!result):
            star.className = "checkstar_empty";
            break;
        case(result):
            star.className = "checkstar_fine";
            break;
    }
}

function checkNewPassword(element_id, star_id)
{
    var pswLength = document.getElementById(element_id).value.length;
    var result = validatePassword(element_id);

    var star = document.getElementById(star_id);
    
    switch(true)
    {
        case(!result && pswLength > 0):
            star.className = "checkstar_error";
            break;
        case(pswLength === 0):
            star.className = "checkstar_empty";
            break;
        case(result):
            star.className = "checkstar_fine";
            break;
    }
}

function checkRepeatedPassword(psw1_id, psw2_id, star_id)
{
    var psw1 = document.getElementById(psw1_id);
    var psw2 = document.getElementById(psw2_id);
    
    var equality = psw1.value === psw2.value;
    var star = document.getElementById(star_id);
    
    switch(true)
    {
        case(!equality && psw1.value.length > 0):
            star.className = "checkstar_error";
            break;
        case(equality && psw1.value.length === 0):
            star.className = "checkstar_empty";
            break;
        case(equality && psw1.value.length > 0):
            star.className = "checkstar_fine";
            break;
    }
}

/// /// /// /// /// /// ///

// TIPS CONTROL BLOCK //

function switchTips()
{
    var tips = document.getElementById("helper");
    if (tips.style.visibility === "hidden")
    {
        showTips();
    }
    else
    {
        hideTips();
    }
}

function cleanTipsBlock()
{
    var tips = document.getElementById("helper");
    tips.innerHTML = "";
}

function appendTip(tip)
{
    var tips = document.getElementById("helper");
    var tmp = tips.innerHTML;
    tmp = tmp + tip + "<br/>";
    tips.innerHTML = tmp;
}

function showTips()
{
    var tips = document.getElementById("helper");
    tips.className = "";
    tips.style.visibility = "visible";
    tips.style.height = "4em";
}

function hideTips()
{
    var tips = document.getElementById("helper");
    tips.style.visibility = "hidden";
    tips.className = "invisible";
    tips.style.height = "0em";
}

function reportCheckOrigPassword()
{
    appendTip(window.messages.invalidOrigPassword);
}

function reportOrigPasswordsIsEmpty()
{
    appendTip(window.messages.emptyOrigPassword);
}

function reportCheckNewPasword()
{
    appendTip(window.messages.invalidNewPassword);
}

function reportCheckConfPasword()
{
    appendTip(window.messages.invalidConfPassword);
}

function reportFailedToProcessRequest()
{
    appendTip(window.messages.failedToProcessRequest);
}

/// /// /// /// /// /// ///


// "INIT MESSAGE VARIABLES" BLOCK //

function readMessages()
{
    var messages = {};
    messages.emptyOrigPassword = document.getElementById("empty_orig_password").value;
    messages.invalidOrigPassword = document.getElementById("check_orig_password").value;
    messages.invalidNewPassword = document.getElementById("check_new_password").value;
    messages.invalidConfPassword = document.getElementById("check_conf_password").value;
    
    messages.notLoggedIn = document.getElementById("not_logged_in").value;
    messages.pswChanged = document.getElementById("psw_successfully_changed").value;
    messages.failedToProcessRequest = document.getElementById("request_failed").value;
    
    window.messages = messages;
    
    var messagesOrigin = document.getElementById("messages");
    messagesOrigin.parentNode.removeChild(messagesOrigin);
}
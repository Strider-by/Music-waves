window.onload = init;


function checkEmailPattern(input_id)
{
    var mail = document.getElementById(input_id).value;
    /* really simple pattern, we don't actually care here */
    var pattern_view = "^[^@]+@[^@]+[.][^@]{2,}$";
    var check_pattern_view = mail.search(pattern_view);

    return check_pattern_view === 0;
}

function checkEmailLength(input_id)
{
    var mail = document.getElementById(input_id).value;

    return mail.length <= 45;
}

function checkPasswordLength(input_id)
{
    var psw = document.getElementById(input_id).value;

    return psw.length >= 1 && psw.length <= 45;
}

function checkPasswordPattern(input_id)
{
    var psw = document.getElementById(input_id).value;
    var pattern_view = /^.+$/; // anything user wants. It's a password after all.
    
    var check_pattern_view = psw.search(pattern_view);

    return check_pattern_view === 0;
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

function cleanTipsBlock()
{
    var tips = document.getElementById("helper");
    tips.innerHTML = "";
}


function appendTip(tip)
{
    var tips = document.getElementById("helper");
    var tmp = tips.innerHTML;
    tmp = tmp + "&bull; " + tip + "<br/>";
    tips.innerHTML = tmp;
}

function validateEmail(input_id, req_star_id)
{
    var mailField = document.getElementById(input_id);
    var mailValue = mailField.value;

    var star = document.getElementById(req_star_id);


    var state = checkEmailPattern(input_id) && checkEmailLength(input_id);

    if (state)
    {
        star.style.color = "#4AEE12";
    } else if (!state && mailValue.length > 0)
    {
        star.style.color = "crimson";
    } else
    {
        star.style.color = "black";
    }
    
    return state;
}

function validatePassword(input_id, req_star_id)
{
    var pswField = document.getElementById(input_id);
    var pswValue = pswField.value;
    var star = document.getElementById(req_star_id);

    var state = checkPasswordPattern(input_id) && checkPasswordLength(input_id);

    if (state)
    {
        star.style.color = "#4AEE12";
    } else if (!state && pswValue.length > 0)
    {
        star.style.color = "crimson";
    } else
    {
        star.style.color = "black";
    }
    
    return state;
}

function validateRepeatedPassword(psw_id, repeated_psw_id, repeated_password_star_id)
{
    var pswField = document.getElementById(psw_id);
    var repeatedPswField = document.getElementById(repeated_psw_id);
    var star = document.getElementById(repeated_password_star_id);
    
    var state = pswField.value === repeatedPswField.value;
    
    if (state && pswField.value.length > 0)
    {
        star.style.color = "#4AEE12";
    } else if(state)
    {
        star.style.color = "black";
    } else
    {
        star.style.color = "crimson";
    }
    
    return state;
}

function checkRegFormLocally()
{
    var mailState = checkEmailLength("email_reg") && checkEmailPattern("email_reg");
    var psw1State = checkPasswordLength("password") && checkPasswordPattern("password");
    var psw2State = validateRepeatedPassword("password", "password2", "password_req_star2");

    if (mailState && psw1State && psw2State)
    {
        return true;
    } 
    else
    {
        cleanTipsBlock();
        if(!mailState) reportMailReqFail(); // set values directly
        if(!psw1State) reportPsw1ReqFail();
        if(!psw2State) reportPsw2ReqFail();
    }
}

function reportCheckEnteredPassword()
{
    showTips();
    appendTip(window.messages.checkEnteredPassword);
}

function reportCheckEnteredEmail()
{
    showTips();
    appendTip(window.messages.checkEnteredEmail);
}


function setLanguage(language)
{
    inactivateLangButtons();
    var lang = getLangBundle(language);
    window.messages = lang.message;

    document.getElementById("page_title").innerHTML = lang.pageTitle;
    document.getElementById("reg_block_button").innerHTML = lang.pickRegButton;
    document.getElementById("conf_block_button").innerHTML = lang.pickConfCodeButton;
    document.getElementById("conf_block_button").innerHTML = lang.pickConfCodeButton;
    document.getElementById("email_label").innerHTML = lang.mailLabel 
            + getEmail1LabelPostfix();
    document.getElementById("email_conf_label").innerHTML = lang.mailLabel 
            + getEmail2LabelPostfix();
    document.getElementById("password_label").innerHTML = lang.passwordLabel1
            + getPassword1LabelPostfix();
    document.getElementById("password_label2").innerHTML = lang.passwordLabel2
            + getPassword2LabelPostfix();
    document.getElementById("confirmation_code_label").innerHTML = lang.confCodeLabel
            + getConfCodeLabelPostfix();
    
    document.getElementById("email_reg").placeholder = lang.regMailPlaceholder;
    document.getElementById("password").placeholder = lang.regPsw1Placeholder;
    document.getElementById("password2").placeholder = lang.regPsw2Placeholder;
    
    document.getElementById("email_conf").placeholder = lang.confMailPlaceholder;
    document.getElementById("confirmation_code").placeholder = lang.confCodePlaceholder;
    
    document.getElementById("register_button").innerHTML = lang.regButton;
    document.getElementById("send_code_again_button").innerHTML = lang.sendCodeAgainButton;
    document.getElementById("check_code_button").innerHTML = lang.confCodeButton;

    hideTips();
    cleanTipsBlock();
    
    validateEmail("email_reg", "email_req_star");
    validateEmail("email_conf", "email_conf_star");
    validatePassword("password", "password_req_star");
    validateRepeatedPassword("password", "password2", "password_req_star2");

}

function getLangBundle(language)
{
    switch (language)
    {
        case "belarusian":
            return langBundle.belarusian;
        case "english":
            return langBundle.english;
        case "russian":
            return langBundle.russian;
    }
}

function getPassword1LabelPostfix()
{
    return " <span id = \"password_req_star\">*</span>";
}

function getPassword2LabelPostfix()
{
    return " <span id = \"password_req_star2\">*</span>";
}

function getEmail1LabelPostfix()
{
    return " <span id = \"email_req_star\">*</span>";
}

function getEmail2LabelPostfix()
{
    return " <span id = \"email_conf_star\">*</span>";
}

function getConfCodeLabelPostfix()
{
    return " <span id = \"password_conf_star\">*</span>";
}

function reportFailedToProcessRequest()
{
    showTips();
    appendTip(window.messages.failedToProcessRequest);
}

function inactivateLangButtons()
{
    var lang_buttons = document.getElementsByClassName("lang-button");
    for(var i = 0; i < lang_buttons.length; i++)
    {
        lang_buttons[i].classList.remove("active_lang_button");
        lang_buttons[i].classList.add("inactive_lang_button");
    }
}

function activateLangButton(langButton)
{
    langButton.classList.remove("inactive_lang_button");
    langButton.classList.add("active_lang_button");
}

function showRegisterPart()
{
    hideTips();
    
    var regPart = document.getElementById("reg_part");
    var regControls = document.getElementById("reg_button_container");
    
    var confirmPart = document.getElementById("confirm_part");
    var confirmControls = document.getElementById("conf_code_button_container");
    
    regPart.classList.remove("not_display");
    confirmPart.classList.add("not_display");
    
    regControls.classList.remove("not_display");
    confirmControls.classList.add("not_display");
    
    var selectRegButton = document.getElementById("reg_block_button");
    selectRegButton.classList.add("regPartBtnActive");
    
    var selectConfButton = document.getElementById("conf_block_button");
    selectConfButton.classList.remove("confirmPartBtnActive");
}

function showConfirmCodePart()
{
    hideTips();
    
    var regPart = document.getElementById("reg_part");
    var regControls = document.getElementById("reg_button_container");
    
    var confirmPart = document.getElementById("confirm_part");
    var confirmControls = document.getElementById("conf_code_button_container");
    
    regPart.classList.add("not_display");
    confirmPart.classList.remove("not_display");
    
    regControls.classList.add("not_display");
    confirmControls.classList.remove("not_display");
    
    var selectRegButton = document.getElementById("reg_block_button");
    selectRegButton.classList.remove("regPartBtnActive");
    
    var selectConfButton = document.getElementById("conf_block_button");
    selectConfButton.classList.add("confirmPartBtnActive");
}

//// REGISTRATION PART ////

function tryRegister()
{
    var localCheckResult = checkRegisterFormLocally();
    
    if(localCheckResult)
    {
        performRegistrationOnline();
    }
}

function checkRegisterFormLocally()
{
    var mailcheck = checkEmailPattern("email_reg");
    var pasw1Check = checkPasswordPattern("password");
    var pasw2Check = document.getElementById("password").value === document.getElementById("password2").value;
    
    var checkResult = mailcheck && pasw1Check && pasw2Check;
    
    if(!checkResult)
    {
        cleanTipsBlock();
        
        if(!mailcheck) appendTip(window.messages.checkEnteredEmail);
        if(!pasw1Check) appendTip(window.messages.checkEnteredPassword1);
        if(!pasw2Check) appendTip(window.messages.checkEnteredPassword2);
        
        showTips();
    }
    
    return checkResult;
}

function performRegistrationOnline()
{
    var email = document.getElementById("email_reg").value;
    var password = document.getElementById("password").value;
    var password2 = document.getElementById("password2").value;

    var request = new XMLHttpRequest();
    var formData = new FormData();
    formData.append("email", email);
    formData.append("password", password);
    formData.append("password2", password2);
    
    // ctx is a global variable defined inside jsp and represents context path to out application
    request.open("POST", ctx + "/ajax?command=register", true);
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
                    appendTip(window.messages.successInProcessingRegRequest);
                    break;
                // fail: invalid password
                case "1":
                    appendTip(window.messages.checkEnteredPassword1);
                    break;
                // fail: passwords do not match
                case "2":
                    appendTip(window.messages.checkEnteredPassword2);
                    break;
                // fail: email is already in use
                case "3":
                    appendTip(window.messages.regFailEmailAlreadyUsed);
                    break;
                // fail: invalid email
                case "4":
                    appendTip(window.messages.checkEnteredEmail);
                    break;
                // fail: couldn't send email
                case "5":
                    appendTip(window.messages.regFailCouldNotSendEmail);
                    break;
                // fail: server side error
                case "520":
                    appendTip(window.messages.failServerSideError);
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

//// CONFIRM REGISTRATION PART ////
function tryCheckCode()
{
    var localCheck = tryCheckCodeLocally();
    
    if(localCheck)
    {
        tryCheckCodeOnline();
    }
}


function tryCheckCodeLocally()
{
    var mailIsOk = checkEmailPattern("email_conf");
    if(!mailIsOk)
    {
        cleanTipsBlock();
        reportCheckEnteredEmail();
    }
    else
    {
        tryCheckCodeOnline();
    }
}


function tryCheckCodeOnline()
{
    
    var email = document.getElementById("email_conf").value;
    var code = document.getElementById("confirmation_code").value;

    var request = new XMLHttpRequest();
    var formData = new FormData();
    formData.append("email", email);
    formData.append("confirmation_code", code);
    
    // ctx is a global variable defined inside jsp and represents context path to out application
    request.open("POST", ctx + "/ajax?command=activate_account", true);
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
                    appendTip(window.messages.successInProcessingConfRequest);
                    break;
                // fail: wrong code
                case "1":
                    appendTip(window.messages.failConfirmationWrongCode);
                    break;
                // fail: e-mail can't be found
                case "2":
                    appendTip(window.messages.failConfirmationMailWasntFound);
                    break;
                // fail: account is already activated
                case "3":
                    appendTip(window.messages.failConfirmationAlreadyActivated);
                    break;
                // fail: server side error
                case "520":
                    appendTip(window.messages.failServerSideError);
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




//// SEND REGISTRATION CODE AGAIN PART ////
function sendConfCode()
{
    var mailIsOk = checkEmailPattern("email_conf");
    if(!mailIsOk)
    {
        cleanTipsBlock();
        reportCheckEnteredEmail();
    }
    else
    {
        requestSendConfCodeServerSide();
    }
}


function requestSendConfCodeServerSide()
{
    var email = document.getElementById("email_conf").value;
    var formData = new FormData();
    formData.append("email", email);
    var request = new XMLHttpRequest();

    // ctx is a global variable defined inside jsp and represents context path to out application
    request.open("POST", ctx + "/ajax?command=send_conf_code", true);
    request.send(formData);
    
    request.onload = function () {
        if (request.status >= 200 && request.status < 400)
        {
            cleanTipsBlock();
            // Successfully connected to server
            var requestResult = request.responseText;
            switch (requestResult)
            {
                // success
                case "0":
                    appendTip(window.messages.successConfCodeEmailSent);
                    break;
                // fail: e-mail can't be found
                case "1":
                    appendTip(window.messages.failConfirmationMailWasntFound);
                    break;
                // fail: account is already activated
                case "2":
                    appendTip(window.messages.failConfirmationAlreadyActivated);
                    break;
                // fail: somehow failed to send e-mail
                case "3":
                    appendTip(window.messages.failSendConfCodeEmailWasntSent);
                    break;
                // fail: server side error
                case "520":
                    appendTip(window.messages.failServerSideError);
                    break;
            }
            
            showTips();
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
}




////////////////////////////////////////////////////////////////////////////////
// 3RD side users
////////////////////////////////////////////////////////////////////////////////
function init()
{
    var register_button = document.getElementById("register_button");
    register_button.onclick = tryRegister;

    var check_code_button = document.getElementById("check_code_button");
    check_code_button.onclick = tryCheckCode;
    
    var send_code_again_button = document.getElementById("send_code_again_button");
    send_code_again_button.onclick = sendConfCode;
    
    var regMailField = document.getElementById("email_reg");
    regMailField.oninput = function() { validateEmail("email_reg", "email_req_star"); };
    
    var confMailField = document.getElementById("email_conf");
    confMailField.oninput = function() { validateEmail("email_conf", "email_conf_star"); };
    
    var pswField = document.getElementById("password");
    pswField.oninput = function() 
        { 
            validatePassword("password", "password_req_star"); 
            validateRepeatedPassword("password", "password2", "password_req_star2"); 
        };
    
    var pswField2 = document.getElementById("password2");
    pswField2.oninput = function() { validateRepeatedPassword("password", "password2", "password_req_star2"); };
    
    
    var langBel = document.getElementById("lang_belarusian");
    langBel.onclick = function () {
        setLanguage("belarusian");
        activateLangButton(langBel);
        setCookie("languageSet", "lang_belarusian");
    };

    var langEng = document.getElementById("lang_english");
    langEng.onclick = function () {
        setLanguage("english");
        activateLangButton(langEng);
        setCookie("languageSet", "lang_english");
    };

    var langRus = document.getElementById("lang_russian");
    langRus.onclick = function () {
        setLanguage("russian");
        activateLangButton(langRus);
        setCookie("languageSet", "lang_russian");
    };
    
    var regBlockButton = document.getElementById("reg_block_button");
    regBlockButton.onclick = showRegisterPart;
    
    var confBlockButton = document.getElementById("conf_block_button");
    confBlockButton.onclick = showConfirmCodePart;
    
    var checkCodeButton = document.getElementById("check_code_button");
    checkCodeButton.onclick = tryCheckCode;
    

    definePageLanguage();
    showRegisterPart();

}


function definePageLanguage()
{
    var cookieLangVar = getCookie("languageSet");
    var defaultLangButton = document.getElementById("lang_belarusian");
    
    if(cookieLangVar === undefined)
    {
        defaultLangButton.click();
    }
    else
    {
        var theButtonWeClick = document.getElementById(cookieLangVar);
        if(theButtonWeClick !== null)
        {
            theButtonWeClick.click();
        }
        else
        {
            defaultLangButton.click();
        }
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


window.onload = init;
var langBundle = initLangBundles();


function tryLogin()
{
    var tips = document.getElementById("helper");
    if (tips.className === "invisible")
    {
        showTips();
    } else
    {
        hideTips();
    }
}

function checkEmailPattern()
{
    var mail = document.getElementById("email").value;
    /* really simple pattern, we don't actually care here */
    var pattern_view = "^[^@]+@[^@]+[.][^@]{2,}$";
    var check_pattern_view = mail.search(pattern_view);

    return check_pattern_view === 0;
}

function checkEmailLength()
{
    var mail = document.getElementById("email").value;

    //return mail.length < 40;
    return true; // to think - we do not hold this address in database so user can use any length e-mail he would want to.
}

function checkPasswordLength()
{
    var psw = document.getElementById("password").value;

    return psw.length > 0;
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

function validateEmail()
{
    var mailField = document.getElementById("email");
    var mailValue = mailField.value;

    var star = document.getElementById("email_req_star");


    var state = checkEmailPattern() && checkEmailLength();

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

function validatePassword()
{
    var star = document.getElementById("password_req_star");

    var state = checkPasswordLength();

    if (state)
    {
        star.style.color = "#4AEE12";
    } else
    {
        star.style.color = "black";
    }

    return state;
}


function checkCredentials()
{
    cleanTipsBlock();
    if (checkCredentialsLocally())
    {
        var email = document.getElementById("email").value;
        var password = document.getElementById("password").value;
        var formData = new FormData();
        formData.append("email", email);
        formData.append("password", password);

        var request = new XMLHttpRequest();
        
        
        // ctx is a global variable defined inside jsp and represents context path to out application
        request.open("POST", ctx + "/ajax?command=check_login_credentials", true);
        request.send(formData);
        
        request.onload = function () {
            if (request.status >= 200 && request.status < 400)
            {
                // Successfully connected to server
                // now let's see what's inside responce part, 
                // 0 == everything is all right, 1 == user is inactive, 2 == user does not exist, 520 - error
                var result = request.responseText;

                if (result == 0)
                {
                    document.getElementById("login_form").submit();
                } else if (result == 1)
                {
                    reportUserIsNotActive();
                } else if (result == 2)
                {
                    reportNoSuchEmailPasswordPairFound();
                } else if (result == 520)
                {
                    reportServerSideError();
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

    }
}

function checkCredentialsLocally()
{
    var mailCheck;
    var passwCheck;

    if ((mailCheck = validateEmail()) !== true)
    {
        reportCheckEnteredEmail();
    }
    if ((passwCheck = validatePassword()) !== true)
    {
        reportCheckEnteredPassword();
    }

    return mailCheck & passwCheck;
}

function reportServerSideError()
{
    showTips();
}

function reportNoSuchEmailPasswordPairFound()
{
    showTips();
    appendTip(window.messages.noSuchEmailPasswordPairFound);
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

function reportUserIsNotActive()
{
    showTips();
    appendTip(window.messages.userIsNotActive);
}

function reportFailedToProcessRequest()
{
    showTips();
    appendTip(window.messages.failedToProcessRequest);
}

function setLanguage(language)
{
    inactivateLangButtons();
    var lang = getLangBundle(language);
    window.messages = lang.message;

    document.getElementById("login_button").innerHTML = lang.loginButton;
    document.getElementById("page_title").innerHTML = lang.pageTitle;
    document.getElementById("password_label").innerHTML = lang.passwordLabel
            + getPasswordLabelPostfix();
    document.getElementById("email_label").innerHTML = lang.mailLabel
            + getEmailLabelPostfix();

    hideTips();
    cleanTipsBlock();
    validateEmail();
    validatePassword();
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

function getPasswordLabelPostfix()
{
    return " <span id = \"password_req_star\">*</span>";
}

function getEmailLabelPostfix()
{
    return " <span id = \"email_req_star\">*</span>";
}

function inactivateLangButtons()
{
    var lang_buttons = document.getElementsByClassName("lang-button");
    for (i = 0; i < lang_buttons.length; i++)
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

////////////////////////////////////////////////////////////////////////////////
// 3RD side users
////////////////////////////////////////////////////////////////////////////////
function init()
{
    //alert( document.cookie );
    var login_button = document.getElementById("login_button");
    login_button.onclick = checkCredentials;


    var mailField = document.getElementById("email");
    mailField.oninput = validateEmail;

    var pswField = document.getElementById("password");
    pswField.oninput = validatePassword;


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

    langBundle = initLangBundles();
    definePageLanguage();

}


function definePageLanguage()
{
    var cookieLangVar = getCookie("languageSet");
    var defaultLangButton = document.getElementById("lang_belarusian");

    if (cookieLangVar === undefined)
    {
        defaultLangButton.click();
    } else
    {
        var theButtonWeClick = document.getElementById(cookieLangVar);
        if (theButtonWeClick !== null)
        {
            theButtonWeClick.click();
        } else
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


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <head>
        <title id = "page_title">Music waves: Login</title>
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/login.css" />
        <link rel="icon" type="image/png" href="${contextPath}/images/favicon-200x200.png" sizes="200x200">
        <script src="${contextPath}/js/login.js" charset="utf-8"></script>
        <script>var ctx = "${contextPath}";</script>
        <script>
            function initLangBundles()
            {
                var lang = {};
                lang.belarusian = {};
                lang.belarusian.pageTitle = "Music Waves: Уваход";
                lang.belarusian.mailLabel = "Адрас электроннай пошты";
                lang.belarusian.passwordLabel = "Пароль " + "";
                lang.belarusian.loginButton = "увайсьці";
                lang.belarusian.message = {};
                lang.belarusian.message.noSuchEmailPasswordPairFound = "падыходзячай пары [ пошта - пароль ] ня знойдзена";
                lang.belarusian.message.checkEnteredPassword = "нешта не так з вашым паролем";
                lang.belarusian.message.checkEnteredEmail = "нешта не так з вашым адрасам электроннай пошты";
                lang.belarusian.message.userIsNotActive = "акаунт існуе, але ен не актыўны";
                lang.belarusian.message.failedToProcessRequest = "нешта пайшло не так і наш запрос не здолеў зьдзейсніцца...";

                lang.english = {};
                lang.english.pageTitle = "Music Waves: Login";
                lang.english.mailLabel = "E-mail";
                lang.english.passwordLabel = "Password";
                lang.english.loginButton = "log in";
                lang.english.message = {};
                lang.english.message.noSuchEmailPasswordPairFound = "no such [ e-mail - password ] pair was found";
                lang.english.message.checkEnteredPassword = "something is wrong with your password";
                lang.english.message.checkEnteredEmail = "something is wrong with your e-mail address";
                lang.english.message.userIsNotActive = "account exists but is not activated";
                lang.english.message.failedToProcessRequest = "somehow login request is failed...";


                lang.russian = {};
                lang.russian.pageTitle = "Music Waves: Вход";
                lang.russian.mailLabel = "Адрес электронной почты";
                lang.russian.passwordLabel = "Пароль";
                lang.russian.loginButton = "войти";
                lang.russian.message = {};
                lang.russian.message.noSuchEmailPasswordPairFound = "подходящей пары [ e-mail - пароль ] не найдено";
                lang.russian.message.checkEnteredPassword = "что-то не так с вашим паролем";
                lang.russian.message.checkEnteredEmail = "что-то не так с вашим адресом электронной почты";
                lang.russian.message.userIsNotActive = "аккаунт существует, но он не был активирован";
                lang.russian.message.failedToProcessRequest = "запрос не удался...";


                return lang;
            }
        </script>
    </head>
    <body>


        <div class="upper_menu menu">
            <a href="register" id="goto_register_page" title=""><img src="${contextPath}/images/register_symbol.png" alt="login"></a>
            <a href="./" id="goto_index_page" title="">&#127968;</a>
        </div>

        <div id="main">

            <div id="form" class = "main_container">


                <div class = "dist_0"></div>

                <div class = "form_background_layer">
                    <form id = "login_form" method="POST" action = "controller">
                        <input type="hidden" name="command" value="login" />
                        <label for = "email" id = "email_label">E-mail <span id = "email_req_star">*</span></label><br/>
                        <input id="email" name="email" type="email" class="input_field" placeholder="mymail@moria.net">
                        <div class = "dist_1"></div>


                        <label for = "password"  id = "password_label">Password <span id = "password_req_star">*</span></label><br/>
                        <input id="password" name="password" type="password" class="input_field" placeholder="speak friend and enter">
                        <div class = "dist_2"></div>
                    </form>
                </div>

                <div id = "helper" class = "invisible">
                </div>
                <div class = "dist_3"></div>


                <div class = "control_container"><div class = "submit_neighbor"></div><button class = "login_button" id = "login_button">log in</button></div>


            </div>


        </div>
        <div class="bottom_menu menu">
            <input type="button" class = "lang-button" value="беларуская" id="lang_belarusian">
            <input type="button" class = "lang-button" value="english" id="lang_english">
            <input type="button" class = "lang-button" value="русский" id="lang_russian"></div>




    </body>
</html>

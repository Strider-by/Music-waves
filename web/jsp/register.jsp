<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="ctg" uri="customtags"%>
<html>
    <head>
        <title id = "page_title">Music waves: Register</title>
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/register.css" />
        <link rel="icon" type="image/png" href="${contextPath}/images/favicon-200x200.png" sizes="200x200">
        <script src="${contextPath}/js/register.js" charset="utf-8"></script>
        <script>var ctx = "${contextPath}";</script>
        <script>
            function initLangBundles()
            {
                var lang = {};
                lang.belarusian = {};
                lang.belarusian.pageTitle = "Music Waves: Рэгістрацыя";
                lang.belarusian.mailLabel = "Адрас электроннай пошты";
                lang.belarusian.confCodeLabel = "Праверачны код";
                lang.belarusian.passwordLabel1 = "Пароль";
                lang.belarusian.passwordLabel2 = "Пароль (паўтарыць)";
                lang.belarusian.regButton = "зарэгістравацца";
                lang.belarusian.confCodeButton = "пацвердзіць код";
                lang.belarusian.sendCodeAgainButton = "даслаць код зноў";

                lang.belarusian.pickRegButton = "Рэгістрацыя";
                lang.belarusian.pickConfCodeButton = "Увесьці праверачны код";

                lang.belarusian.regMailPlaceholder = "не больш за 45 сімвалаў";
                lang.belarusian.regPsw1Placeholder = "1 - 45 сімвалаў";
                lang.belarusian.regPsw2Placeholder = "паўтарыць уведзены пароль";

                lang.belarusian.confMailPlaceholder = "пошта, на якую быў зэрэгістраваны акаўнт";
                lang.belarusian.confCodePlaceholder = "код, які прыйшоў на пошту";


                lang.belarusian.message = {};
                lang.belarusian.message.checkEnteredPassword1 = "нешта не так з вашым паролем";
                lang.belarusian.message.checkEnteredPassword2 = "паролі павінны супадаць";
                lang.belarusian.message.checkEnteredEmail = "нешта не так з вашым адрасам электроннай пошты";
                lang.belarusian.message.failedToProcessRequest = "нешта пайшло не так і наш запрос не здолеў зьдзейсніцца...";

                lang.belarusian.message.successInProcessingRegRequest = "Рэгістрацыя паспяхова адбылася. Калі ласка, перайдзіце на форму "
                        + "ўвода кода пацьверджання і ўвядзіце код, дасланы вам на пошту.";
                lang.belarusian.message.regFailCouldNotSendEmail = "Акаўнт быў зарэгістраваны, але адправіць код пацьвярджэння не атрымалася. "
                        + "Рэгістрацыя скасавана. Калі ласка, паспрабуйце зноў пазьней альбо скарыстайцеся іншым адрасам электроннай пошты.";
                lang.belarusian.message.regFailEmailAlreadyUsed = "Акаўнт не можа быць створаны, гэты адрас электроннай пошты ўжо скарыстаны.";

                lang.belarusian.message.successInProcessingConfRequest = "Пацьвярджэнне паспяхова адбылося. Цяпер вы можаце увайсьці "
                        + "каб пачаць карыстацца сэрвісам.";
                lang.belarusian.message.failConfirmationMailWasntFound = "памылка: уведзены вамі адрас электроннай пошты адсутнічае ў базе дадзеных";
                lang.belarusian.message.failConfirmationWrongCode = "памылка: няправільны код пацьверджання";
                lang.belarusian.message.failConfirmationAlreadyActivated = "Гэты акаунт ўжо быў актываваны.";

                lang.belarusian.message.failServerSideError = "Памылка на баку сэрвіса. Калі ласка, паспрабуйце зноў пазьней";
                lang.belarusian.message.successConfCodeEmailSent = "Ліст з кодам актывацыі паспяхова дасланы";
                lang.belarusian.message.failSendConfCodeEmailWasntSent = "Памылка. Зараз мы не здольныя даслаць вам ліст з кодам актывацыі. "
                        + "Калі ласка, паспрабуйце зноў пазьней.";

                lang.english = {};
                lang.english.pageTitle = "Music Waves: Register";
                lang.english.mailLabel = "E-mail";
                lang.english.confCodeLabel = "Confirmation code";
                lang.english.passwordLabel1 = "Password";
                lang.english.passwordLabel2 = "Password (repeat)";
                lang.english.regButton = "register";
                lang.english.confCodeButton = "confirm code";
                lang.english.sendCodeAgainButton = "send code again";

                lang.english.pickRegButton = "Register";
                lang.english.pickConfCodeButton = "Enter confirmation code";

                lang.english.regMailPlaceholder = "45 symbols as maximum";
                lang.english.regPsw1Placeholder = "1 - 45 symbols";
                lang.english.regPsw2Placeholder = "just in case you mistyped it last time";


                lang.english.confMailPlaceholder = "e-mail you used to register your account";
                lang.english.confCodePlaceholder = "verification code you acquired via e-mail";

                lang.english.message = {};
                lang.english.message.checkEnteredPassword1 = "something is wrong with your password";
                lang.english.message.checkEnteredPassword2 = "passwords must match";
                lang.english.message.checkEnteredEmail = "something is wrong with your e-mail address";
                lang.english.message.failedToProcessRequest = "somehow our request is failed...";


                lang.english.message.checkEnteredPassword2 = "the passwords you entered must match";

                lang.english.message.successInProcessingRegRequest = "Registration is completed. Please, enter confirmation code "
                        + "to activate your account.";
                lang.english.message.regFailCouldNotSendEmail = "Account was registered but we failed in sending confirmation code. "
                        + "Registration process is aborted. Please, try again later. Using another e-mail may help as well.";
                lang.english.message.regFailEmailAlreadyUsed = "Account cannot be created: this e-mail has already been used.";
                lang.english.message.successInProcessingConfRequest = "Confirmation succeeded. Now you can log in to start using this service.";
                lang.english.message.failConfirmationMailWasntFound = "error: the e-mail you entered wasn't found in our database";
                lang.english.message.failConfirmationWrongCode = "error: wrong confirmation code";
                lang.english.message.failConfirmationAlreadyActivated = "Account linked to this e-mail has already been activated.";

                lang.english.message.failServerSideError = "Service side error. Please try again later.";
                lang.english.message.successConfCodeEmailSent = "E-mail with confirmation code has been successfully sent";
                lang.english.message.failSendConfCodeEmailWasntSent = "Error. We cannot send you confirmation code right now. "
                        + "Please, try again later.";

                lang.russian = {};
                lang.russian.pageTitle = "Music Waves: Регистрация";
                lang.russian.mailLabel = "Адрес электронной почты";
                lang.russian.confCodeLabel = "Проверочный код";
                lang.russian.passwordLabel1 = "Пароль";
                lang.russian.passwordLabel2 = "Пароль (повторить)";
                lang.russian.regButton = "регистрация";
                lang.russian.confCodeButton = "подтвердить код";
                lang.russian.sendCodeAgainButton = "выслать код снова";

                lang.russian.pickRegButton = "Регистрация";
                lang.russian.pickConfCodeButton = "Ввести проверочный код";

                lang.russian.regMailPlaceholder = "не более 45 символов";
                lang.russian.regPsw1Placeholder = "1 - 45 символов";
                lang.russian.regPsw2Placeholder = "повторить введённый пароль";

                lang.russian.confMailPlaceholder = "почта, которую вы использовали при регистрации";
                lang.russian.confCodePlaceholder = "проверочный код (должен придти на указанный адрес)";

                lang.russian.message = {};
                lang.russian.message.checkEnteredPassword1 = "что-то не так с вашим паролем";
                lang.russian.message.checkEnteredPassword2 = "пароли должны совпадать";
                lang.russian.message.checkEnteredEmail = "что-то не так с вашим адресом электронной почты";
                lang.russian.message.failedToProcessRequest = "запрос не удался...";

                lang.russian.message.checkEnteredPassword2 = "пароли должны совпадать";

                lang.russian.message.successInProcessingRegRequest = "Регистрация завершена. Пожалуйста, перейдите на форму ввода кода подтверждения, "
                        + "чтобы активировать аккаунт.";
                lang.russian.message.regFailCouldNotSendEmail = "Аккаунт был зарегистрирован, однако отправка кода подтверждения не удалась. "
                        + "Регистрация отменена. Попробуйте позже или воспользуйтесь другим адресом электронной почты.";
                lang.russian.message.regFailEmailAlreadyUsed = "Аккаунт не может быть создан: данный адрес электронной почты уже был использован.";
                lang.russian.message.successInProcessingConfRequest = "Код принят. Войдите в аккаунт чтобы начать пользоваться сервисом.";
                lang.russian.message.failConfirmationMailWasntFound = "ошибка: введённый вами адрес электронной почты отсутствует в базе данных";
                lang.russian.message.failConfirmationWrongCode = "ошибка: введённый код неверен";
                lang.russian.message.failConfirmationAlreadyActivated = "Аккаунт, привязанный к этому адресу электронной почты, уже активирован.";

                lang.russian.message.failServerSideError = "Ошибка на стороне сервиса. Пожалуйста, попробуйте снова позже.";
                lang.english.message.successConfCodeEmailSent = "Письмо с кодом подтверждения успешно отправлено.";
                lang.russian.message.failSendConfCodeEmailWasntSent = "Произошла ошибка. Прямо сейчас мы не можем выслать вам письмо к кодом подтверждения. "
                        + "Пожалуйста, попробуйте позже.";

                return lang;
            }

            window.langBundle = initLangBundles();
        </script>
    </head>
    <body>


        <div class="upper_menu menu">
            <a href="login" id="goto_login_page" title=""><img src="${contextPath}/images/login_symbol.png" alt="login"></a>
            <a href="./" id="goto_index_page" title="">&#127968;</a>
        </div>

        <div id="main">

            <div id="form" class = "main_container">


                <div class = "dist_0"></div>


                <div class="pick-header">
                    <button class = "partBtn" id = "reg_block_button">Register</button>
                    <button class = "partBtn" id = "conf_block_button">Enter confirmation code</button>
                </div>
                <p></p>

                <div class = "form reg_form_layer" id = "reg_part">
                    <form 
                        id = "register_form" method="POST" action = "controller">
                        <input type="hidden" name="command" value="register" />
                        <label for = "email_reg" id = "email_label" class = "label">E-mail <span id = "email_req_star">*</span></label><br/>
                        <input id="email_reg" name="email" type="email" class="input_field" placeholder="45 symbols as maximum" maxlength="45">
                        <div class = "dist_1"></div>

                        <label for = "password"  id = "password_label" class = "label">Password <span id = "password_req_star">*</span></label><br/>
                        <input id="password" name="password" type="password" class="input_field" placeholder="45 symbols as maximum" maxlength="45">
                        <div class = "dist_1"></div>

                        <label for = "password"  id = "password_label2" class = "label">Password (repeat) <span id = "password_req_star2">*</span></label><br/>
                        <input id="password2" name="password2" type="password" class="input_field" placeholder="just in case you mistyped it last time">
                        <div class = "dist_1"></div>

                    </form>
                </div>

                <div class = "form conf_form_layer not_display" id = "confirm_part"> 
                    <form 
                        id = "confirm_code_form" method="POST" action = "controller">
                        <input type="hidden" name="command" value="confirm_code" />
                        <label for = "email_conf" id = "email_conf_label" class = "label">E-mail <span id = "email_conf_star">*</span></label><br/>
                        <input id="email_conf" name="email" type="email" class="input_field" placeholder="mymail@moria.net">
                        <div class = "dist_1"></div>

                        <label for = "confirmation_code"  id = "confirmation_code_label" class = "label">Confirmation code <span id = "password_conf_star">*</span></label><br/>
                        <input id="confirmation_code" name="code" type="text" class="input_field" placeholder="bunch of symbols from the e-mail we sent">
                        <div class = "dist_1"></div>

                    </form>
                </div>    


                <div id = "helper" class = "invisible">
                </div>
                <div class = "dist_3"></div>


                <div class = "control_container" id = "reg_button_container"><div class = "submit_neighbor"></div><button class = "submit_button" id = "register_button">register</button></div>
                <div class = "control_container not_display" id = "conf_code_button_container"><div class = "submit_neighbor2"></div>
                    <button class = "submit_button" id = "send_code_again_button">send code again</button>
                    <button class = "submit_button" id = "check_code_button">confirm code</button>
                </div>


            </div>


        </div>
        <div class="bottom_menu menu">
            <input type="button" class = "lang-button" value="беларуская" id="lang_belarusian">
            <input type="button" class = "lang-button" value="english" id="lang_english">
            <input type="button" class = "lang-button" value="русский" id="lang_russian"></div>




    </body>
</html>

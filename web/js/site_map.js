function init()
{
    document.getElementById("music_search_link").onclick = function()
    {
        window.location.href = "music-search";
    };
    
    document.getElementById("my_playlists_link").onclick = function()
    {
        window.location.href = "playlists";
    };
    
    document.getElementById("listen_link").onclick = function()
    {
        window.location.href = "listen";
    };
    
    document.getElementById("music_compound_link").onclick = function()
    {
        window.location.href = "music-compound";
    };
    
    document.getElementById("users_link").onclick = function()
    {
        window.location.href = "users";
    };
    
    document.getElementById("personal_data_link").onclick = function()
    {
        window.location.href = "personal-data";
    };
    
    document.getElementById("change_password_link").onclick = function()
    {
        window.location.href = "change-password";
    };
    
    document.getElementById("logout_link").onclick = function()
    {
        let form = document.createElement("form");
        form.action = "controller";
        let commandInput = document.createElement("input");
        commandInput.name = "command";
        commandInput.value = "logout";
        form.appendChild(commandInput);
        form.classList.add("undisplayable");
        document.body.appendChild(form);
        form.submit();
    };
}
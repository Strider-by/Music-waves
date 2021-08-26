function init()
{
    document.getElementById("go_home").onclick = function ()
    {
        let form = document.createElement("form");
        form.action = "controller";
        let commandInput = document.createElement("input");
        commandInput.name = "command";
        commandInput.value = "";
        form.appendChild(commandInput);
        form.classList.add("undisplayable");
        document.body.appendChild(form);
        form.submit();
    };
}



window.MessageType = Object.freeze({message: {}, warning: {}, error: {}});

function showMessage(text, messageType, headingMessage)
{
// setting window decoration depending on message type
    let dialogueWindow = document.getElementById("dialog_window");
    dialogueWindow.className = "";
    switch (messageType)
    {
        case window.MessageType.message:
            dialogueWindow.className = "message";
            break;
        case window.MessageType.warning:
            dialogueWindow.className = "warning";
            break;
        case window.MessageType.error:
            dialogueWindow.className = "error";
            break;
    }

    if (headingMessage !== undefined && headingMessage !== null)
    {
        dialogueWindow.getElementsByTagName("h3")[0].innerHTML = headingMessage;
    }

    dialogueWindow.getElementsByTagName("p")[0].innerHTML = text;
    dialogueWindow.showModal();
}

function appendMessagePart()
{
    let msg = "<dialog id=\"dialog_window\" class=\"message\">";
    msg += "<div id=\"dialog_window_header\">";
    msg += "<span id=\"close_dialog_window\" class=\"noselect\">&#10005;</span>";
    msg += "</div>";
    msg += "<div id=\"dialog_window_content_container\">";
    msg += "<h3></h3>";
    msg += "<p></p>";
    msg += "</div>";
    msg += "</dialog>";
            
    document.getElementsByTagName("body")[0].innerHTML += msg;
    
    document.getElementById("close_dialog_window").addEventListener("click",
            function ()
            {
                document.getElementById("dialog_window").close();
            });
}



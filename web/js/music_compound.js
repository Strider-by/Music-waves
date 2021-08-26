window.onload = function ()
{
    appendMessagePart();
    initArtistsPart();
    initAlbumsPart();
    initTracksPart();
    initTabs();
    requestArtistsList();
};

function initTabs()
{
    //// tab labels init ////
    let tabLabels = {};
    
    tabLabels.artists = document.getElementById("artists_tab");
    tabLabels.artists.defaultMethod = activateArtistsTab;
    tabLabels.artists.order = 0;
    
    tabLabels.albums = document.getElementById("albums_tab");
    tabLabels.albums.defaultMethod = activateAlbumsTab;
    tabLabels.albums.order = 1;
    
    
    tabLabels.tracks = document.getElementById("tracks_tab");
    tabLabels.tracks.defaultMethod = activateTracksTab;
    tabLabels.tracks.order = 2;
    
    window.tabLabels = tabLabels;
    
    
    //// tab windows init ////
    let tablist = {};
    tablist.artists = document.getElementById("artists_tab_window");
    tablist.albums = document.getElementById("albums_tab_window");
    tablist.tracks = document.getElementById("tracks_tab_window");
    window.tablist = tablist;
    
    
    //// activate first tab ////
    activateArtistsTab();
}

function hideAllTabs()
{
    for (let tab in window.tablist)
    {
        window.tablist[tab].classList.add("undisplayable");
    }
}


function activateArtistsTab()
{
    hideAllTabs();
    window.tablist.artists.classList.remove("undisplayable");
    setTabLabelAsCurrent(window.tabLabels.artists);
    cleanseAlbumEditFields();
    
}

function activateAlbumsTab()
{
    hideAllTabs();
    window.tablist.albums.classList.remove("undisplayable");
    setTabLabelAsCurrent(window.tabLabels.albums);
    cleanseTrackEditFields();
}

function activateTracksTab()
{
    hideAllTabs();
    window.tablist.tracks.classList.remove("undisplayable");
    setTabLabelAsCurrent(window.tabLabels.tracks);
}

function setTabLabelsAsInactive()
{
    for (let i = 0; i < arguments.length; i++)
    {
        arguments[i].classList.remove("active_tab");
        arguments[i].classList.remove("current_tab");
        arguments[i].classList.add("inactive_tab");
        arguments[i].onclick = "";
    }
}

function setTabLabelsAsActive()
{
    for (let i = 0; i < arguments.length; i++)
    {
        arguments[i].classList.add("active_tab");
        arguments[i].classList.remove("current_tab");
        arguments[i].classList.remove("inactive_tab");
        arguments[i].onclick = arguments[i].defaultMethod;
    }
}

function setTabLabelAsCurrent(tabObject)
{
    tabObject.classList.add("active_tab");
    tabObject.classList.add("current_tab");
    tabObject.classList.remove("inactive_tab");
    tabObject.onclick = "";
    
    let tabNumber = tabObject.order;
    
    for (let objKey in window.tabLabels)
    {
        if(window.tabLabels[objKey].order < tabNumber)
        {
            setTabLabelsAsActive(window.tabLabels[objKey]);
        }
    }
    
    for (let objKey in window.tabLabels)
    {
        if(window.tabLabels[objKey].order > tabNumber)
        {
            setTabLabelsAsInactive(window.tabLabels[objKey]);
        }
    }
}

function restoreDefaultTabLabelMethod()
{
    for (let i = 0; i < arguments.length; i++)
    {
        arguments[i].onclick = arguments[i].defaultMethod;
    }
}
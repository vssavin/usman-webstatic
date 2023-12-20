getKey = function() {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", '/usman/security/key', false);
    xmlHttp.send(null);
    return xmlHttp.responseText;
};
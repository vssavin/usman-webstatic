getKey = function(apiVersion) {
    var xmlHttp = new XMLHttpRequest();
    if (typeof apiVersion === 'string') {
        xmlHttp.open("GET", '/usman/v' + apiVersion + 'security/key', false);
    } else {
        xmlHttp.open("GET", '/usman/v1/security/key', false);
    }
    xmlHttp.send(null);
    return xmlHttp.responseText;
};
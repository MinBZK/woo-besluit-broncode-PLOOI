(function () {

    function genereerID() {
        var toegestaan = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        var waarde = [new Date().getTime() + ''];
        for (var i = 0; i < 5; i++) {
            waarde.push(toegestaan.charAt(Math.floor(Math.random() * toegestaan.length)));
        }
        return waarde.join('');
    }

    function initSessionStorage() {
        if (typeof sessionStorage["id"] === 'undefined') {
            sessionStorage["id"] = genereerID();
        }
        saveSessionStorage();
    }

    function saveSessionStorage() {
        document.cookie = 'actieveSessieID=' + sessionStorage["id"] + '; path=/';
        if (typeof cookies !== 'undefined') {
          for (var i=0;i<cookies.length;i++) {
            document.cookie = sessionStorage["id"] + cookies[i] + '; path=/';
          }
        }
    }

    if (window.addEventListener) {
        window.addEventListener("load", initSessionStorage, false);
        window.addEventListener("beforeunload", saveSessionStorage, false);
    } else if (window.attachEvent) {
        window.attachEvent("onload", initSessionStorage);
        window.attachEvent("onbeforeunload", saveSessionStorage);
    } else {
        var savedOnload = window.onload;
        window.onload = function () {
            if (typeof savedOnload === 'function')
                savedOnload.apply(this, [].splice.call(arguments, 0));
            initSessionStorage();
        };
        var savedOnbeforeunload = window.onbeforeunload;
        window.onbeforeunload = function () {
            if (typeof savedOnbeforeunload === 'function')
                savedOnbeforeunload.apply(this, [].splice.call(arguments, 0));
            saveSessionStorage();
        };
    }
})();
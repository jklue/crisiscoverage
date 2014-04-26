
/**
 * Add a prototype method to String (careful to escape special chars used by regex)
 * @param find
 * @param replace
 * @returns {string}
 */
String.prototype.replaceAll = function (find, replace) {
    var str = this;
    return str.replace(new RegExp(find.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'), 'g'), replace);
};

/**
 * Be notified when a registered element has a class change.
 * This is useful to be notified when window.crisis_select has a value change.
 * @param elemId
 * @param callback
 */
function addClassNameListener(elemId, callback) {
    var elem = document.getElementById(elemId);
    var lastClassName = elem.className;
    window.setInterval( function() {
        var className = elem.className;
        if (className !== lastClassName) {
            callback();
            lastClassName = className;
        }
    },10);
}

/**
 * Useful for pages where crisis selector should be hidden.
 */
function hideCrisisSelector(){
    $("#crisis_selector").hide();
}

/**
 * Friendly print for numbers, considering decimals.
 * @param x
 * @returns {string}
 */
function numberWithCommas(x) {
    if (isNaN(x)) return x;
    var parts = x.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return parts.join(".");
}


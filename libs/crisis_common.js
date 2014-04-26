/**
 * Current Crisis Evaluator (runs on a timer from header.html).
 */
var firstTime = true;
function currentCrisisEvaluator(){
//            console.log("... running again");
    var changed = false;
    //HANDLE INITIAL CONDITIONS {{
    if (!localStorage.getItem('current_crisis')){
        localStorage.setItem('current_crisis', 'haiyan');
    }
    if (!window.crisis_select.value || firstTime){
        console.log("--> updating new window.crisis_select variable to reflect localStorage: "+localStorage.getItem('current_crisis'));
        window.crisis_select.value= localStorage.getItem('current_crisis');
        firstTime = false;
        changed = true;
    }
    //}}

    else  if (localStorage.getItem('current_crisis') !== window.crisis_select.value){
        console.log("--> updating localStorage 'current_crisis' variable to reflect new selection: "+window.crisis_select.value);
        localStorage.setItem('current_crisis', window.crisis_select.value);
        changed = true;
    }

    if (changed){
        /*NOTICE: Changing class to allow a class listener to take action */
        window.crisis_select.className = window.crisis_select.value;
    }
}

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


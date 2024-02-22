var hackTime = document.getElementById("hackTime");
var currentDateTime = new Date();
var hackDateTime;
if(hackTime != null) {
    hackDateTime = parseCountdownDateTimeString(hackTime.innerHTML);
}
var hackTimeLength = document.getElementById("hackTimeLength");
var hackTimeField = document.getElementById("hackTimeField");
var hackTimeRemaining;
if(hackTimeLength != null) {
    hackTimeRemaining = parseInt(hackTimeLength.innerHTML) - parseInt((currentDateTime.getTime() - hackDateTime.getTime()) / 1000);
}
if(hackTimeField != null) {
    hackTimeField.innerHTML = "[Time Left: " + parseTimeRemaining(hackTimeRemaining) + "]";
}

groupHackTime = document.getElementById("groupHackTime");
var groupHackDateTime;
if(groupHackTime != null) {
    groupHackDateTime = parseCountdownDateTimeString(groupHackTime.innerHTML);
}
var groupHackTimeLength = document.getElementById("groupHackTimeLength");
var groupHackTimeField = document.getElementById("groupHackTimeField");
var groupHackTimeRemaining;
if(groupHackTimeLength != null) {
    groupHackTimeRemaining = parseInt(groupHackTimeLength.innerHTML) - parseInt((currentDateTime.getTime() - groupHackDateTime.getTime()) / 1000);
}
if(groupHackTimeField != null) {
    groupHackTimeField.innerHTML = "[Time Left: " + parseTimeRemaining(groupHackTimeRemaining) + "]";
}

setInterval(updateCountdownTimers, 1000);

function updateCountdownTimers() {
    if(hackTimeRemaining != null) {
        if(hackTimeRemaining > 0) {
            hackTimeRemaining = hackTimeRemaining - 1;
            hackTimeField.innerHTML = "[Time Left: " + parseTimeRemaining(hackTimeRemaining) + "]";
        } else if(hackTimeRemaining == 0) {
            hackTimeField.innerHTML = "[Hack Complete!]";
        }
    }

    if(groupHackTimeRemaining != null) {
        if(groupHackTimeRemaining > 0) {
            groupHackTimeRemaining = groupHackTimeRemaining - 1;
            groupHackTimeField.innerHTML = "[Time Left: " + parseTimeRemaining(groupHackTimeRemaining) + "]";
        } else if(groupHackTimeRemaining == 0) {
            groupHackTimeField.innerHTML = "[Hack Complete!]";
        }
    }
}

function parseCountdownDateTimeString(hackTargetTime) {
    var year = hackTargetTime.substring(0, 4);
    hackTargetTime = hackTargetTime.substring(5);
    var month = parseInt(hackTargetTime.substring(0, 2)) - 1;
    hackTargetTime = hackTargetTime.substring(3);
    var day = hackTargetTime.substring(0, 2);
    hackTargetTime = hackTargetTime.substring(3);
    var hour = hackTargetTime.substring(0, 2);
    hackTargetTime = hackTargetTime.substring(3);
    var minutes = hackTargetTime.substring(0, 2);
    hackTargetTime = hackTargetTime.substring(3);
    var seconds = hackTargetTime.substring(0, 2);

    return new Date(year, month, day, hour, minutes, seconds);
}

function parseTimeRemaining(seconds) {
    var hours = Math.floor(seconds / 3600);
    var minutes = Math.floor((seconds % 3600) / 60);
    seconds = Math.floor((seconds % 3600) % 60);

    if(hours < 10) {
        hours = "0" + hours;
    }
    if(minutes < 10) {
        minutes = "0" + minutes;
    }
    if(seconds < 10) {
        seconds = "0" + seconds;
    }

    return hours + ":" + minutes + ":" + seconds;
}
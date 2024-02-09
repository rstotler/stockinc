function clickMessage(title, date, content) {
    const offscreenMenu = document.querySelector(".offscreen-menu");

    if(!offscreenMenu.classList.contains("active")) {
        const messageTitle = document.getElementById("messageTitle");
        const messageDate = document.getElementById("messageDate");
        const messageContent = document.getElementById("messageContent");

        messageTitle.innerHTML = title;
        messageDate.innerHTML = parseDateTimeString(date);
        messageContent.innerHTML = content;
    }

    offscreenMenu.classList.toggle('active');
}

function parseDateTimeString(dateTimeString) {
    var year = dateTimeString.substring(0, 4);
    dateTimeString = dateTimeString.substring(5);
    var month = dateTimeString.substring(0, 2);
    dateTimeString = dateTimeString.substring(3);
    var day = dateTimeString.substring(0, 2);
    dateTimeString = dateTimeString.substring(3);
    var hour = parseInt(dateTimeString.substring(0, 2));
    dateTimeString = dateTimeString.substring(3);
    var minutes = dateTimeString.substring(0, 2);
    dateTimeString = dateTimeString.substring(3);
    var seconds = dateTimeString.substring(0, 2);

    var amPmString = "AM";

    if(hour > 12) {
        hour = hour - 12;
        amPmString = "PM";
    }

    return month + "/" + day + "/" + year + " " + hour + ":" + minutes + ":" + seconds + " " + amPmString;
}
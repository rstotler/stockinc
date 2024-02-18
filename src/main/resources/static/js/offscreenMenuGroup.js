var selectedGroupSymbol = "None";
var playerGroupSymbol = "None";

var hackGroupForm = document.getElementById("hackGroupForm");

var hackTime = document.getElementById("hackTime");
var currentDateTime = new Date();
var hackDateTime;
if(hackTime != null) {
    hackDateTime = parseDateTimeString(hackTime.innerHTML);
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

setInterval(updateCountdownTimers, 1000);

function updateCountdownTimers() {
    if(hackTimeRemaining != null) {
        if(hackTimeRemaining > 0) {
            hackTimeRemaining = hackTimeRemaining - 1;
            hackTimeField.innerHTML = "[Time Left: " + parseTimeRemaining(hackTimeRemaining) + "]";
        } else if(hackTimeRemaining == 0) {
            hackTimeField.innerHTML = "[Hack Complete]";
        }
    }
}

function parseDateTimeString(hackTargetTime) {
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

function clickGroupListing(groupName, groupSymbol, groupStatus, requestedGroupList, playerGroup) {
    const offscreenMenuGroupListing = document.getElementById("groupListingMenu");
    const offscreenMenuCreateGroup = document.getElementById("createGroupMenu");

    if(offscreenMenuCreateGroup.classList.contains("active")) {
        offscreenMenuCreateGroup.classList.toggle("active");
    }

    if(!offscreenMenuGroupListing.classList.contains("active")) {
        const groupListingMenuHeaderName = document.getElementById("groupListingMenuHeaderName");
        groupListingMenuHeaderName.innerHTML = groupName;

        selectedGroupSymbol = groupSymbol;
        playerGroupSymbol = playerGroup;

        const offscreenMenuTabs = document.getElementsByName("offscreenMenuTab");
        for(let i = 0; i < offscreenMenuTabs.length; i++) {
            offscreenMenuTabs[i].style.display = "none";
        }
        const joinGroupTab = document.getElementById("joinGroupTab");
        joinGroupTab.style.display = "block";

        const hackerAmountInput = document.getElementById("hackerAmountInput");
        hackerAmountInput.value = "0";

        if(groupStatus == "None") {
            const groupJoinSymbolInput = document.getElementById("groupJoinSymbolInput");
            groupJoinSymbolInput.value = groupSymbol;

            const requestJoinGroupButton = document.getElementById("requestJoinGroupButton");
            if(requestedGroupList.includes(groupSymbol)) {
                if(!requestJoinGroupButton.classList.contains("button-inactive")) {
                    requestJoinGroupButton.classList.add("button-inactive");
                    requestJoinGroupButton.innerHTML = "Request Submitted";
                    requestJoinGroupButton.disabled = true;
                }
            } else {
                if(requestJoinGroupButton.classList.contains("button-inactive")) {
                    requestJoinGroupButton.classList.remove("button-inactive");
                    requestJoinGroupButton.innerHTML = "Request To Join";
                    requestJoinGroupButton.disabled = false;
                }
            }
        }
    }

    offscreenMenuGroupListing.classList.toggle("active");
}

function clickGroupSettingsTab(groupName, groupSymbol, groupStatus) {
    if(groupStatus == "None") {
        const createGroupForm = document.getElementById("createGroupForm");
        createGroupForm.style.display = "block";
    } else if(groupStatus == "Founder") {
        const textGroupName = document.getElementById("groupName");
        textGroupName.innerHTML = groupName;

        const inputGroupSymbol = document.getElementById("groupSymbolInput");
        inputGroupSymbol.value = groupSymbol;

        const groupSettingsForm = document.getElementById("groupSettingsForm");
        groupSettingsForm.style.display = "block";
    }

    const offscreenMenuGroupListing = document.getElementById("groupListingMenu");
    const offscreenMenuCreateGroup = document.getElementById("createGroupMenu");

    if(offscreenMenuGroupListing.classList.contains("active")) {
        offscreenMenuGroupListing.classList.toggle("active");
    }

    offscreenMenuCreateGroup.classList.toggle("active");
}

function clickRemoveFromGroup(clickIndex) {
    const removeButton = document.getElementsByName("removeFromGroupButton");
    removeButton[clickIndex - 1].innerHTML = "Removed"; // -1 To Account For Group Founder In List
    removeButton[clickIndex - 1].classList.add("button-inactive");
    removeButton[clickIndex - 1].disabled = true;
}

function clickAcceptToGroup(clickIndex) {
    const acceptToGroupButton = document.getElementsByName("acceptToGroupButton");
    acceptToGroupButton[clickIndex].innerHTML = "Accepted";
    acceptToGroupButton[clickIndex].classList.add("button-inactive");
    acceptToGroupButton[clickIndex].disabled = true;

    const denyFromGroupButton = document.getElementsByName("denyFromGroupButton");
    denyFromGroupButton[clickIndex].classList.add("button-inactive");
    denyFromGroupButton[clickIndex].disabled = true;
}

function clickDenyFromGroup(clickIndex) {
    const denyFromGroupButton = document.getElementsByName("denyFromGroupButton");
    denyFromGroupButton[clickIndex].innerHTML = "Denied";
    denyFromGroupButton[clickIndex].classList.add("button-inactive");
    denyFromGroupButton[clickIndex].disabled = true;

    const acceptToGroupButton = document.getElementsByName("acceptToGroupButton");
    acceptToGroupButton[clickIndex].classList.add("button-inactive");
    acceptToGroupButton[clickIndex].disabled = true;
}

function clickTab(tabTitle, availableHackerCount) {
    const offscreenMenuTabs = document.getElementsByName("offscreenMenuTab");
    for(let i = 0; i < offscreenMenuTabs.length; i++) {
        offscreenMenuTabs[i].style.display = "none";
    }

    if(tabTitle == "tabInfo") {
        const joinGroupTab = document.getElementById("joinGroupTab");
        joinGroupTab.style.display = "block";
    }
    else if(tabTitle == "tabHack" && (playerGroupSymbol == "None" || selectedGroupSymbol != playerGroupSymbol)) {
        const hackGroupTab = document.getElementById("hackGroupTab");
        hackGroupTab.style.display = "block";

        const availableHackerCountText = document.getElementById("availableHackerCount");
        availableHackerCountText.innerHTML = "Hackers Available: " + availableHackerCount;

        hackGroupForm.action = "/startHackGroup/" + selectedGroupSymbol;
    }
}
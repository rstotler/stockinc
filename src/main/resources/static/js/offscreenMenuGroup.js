var selectedGroupSymbol = "None";
var playerGroupSymbol = "None";

var hackGroupForm = document.getElementById("hackGroupForm");

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
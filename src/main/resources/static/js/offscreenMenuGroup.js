function clickGroupListing(groupName, groupSymbol) {
    const offscreenMenuGroupListing = document.getElementById("groupListingMenu");
    const offscreenMenuCreateGroup = document.getElementById("createGroupMenu");

    if(offscreenMenuCreateGroup.classList.contains("active")) {
        offscreenMenuCreateGroup.classList.toggle("active");
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
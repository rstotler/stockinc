const inputBuyCount = document.getElementById("inputBuyCount");
inputBuyCount.addEventListener('click', () => {updateBuyPrice();});
inputBuyCount.addEventListener('input', () => {updateBuyPrice();});

var selectedUnitPrice = null;
var unitTypeInput = document.getElementById("unitTypeInput");

var creatingUnitTime = document.getElementById("creatingUnitTime");
var tipsterCooldownTime = document.getElementById("tipsterCooldownTime");
var currentDateTime = new Date();
var unitCreationDateTime;
if(creatingUnitTime != null) {
    unitCreationDateTime = parseDateTimeString(creatingUnitTime.innerHTML);
}
var tipsterCooldownDateTime;
if(tipsterCooldownTime != null) {
    tipsterCooldownDateTime = parseDateTimeString(tipsterCooldownTime.innerHTML);
}

var creatingUnitTimeLength = document.getElementById("creatingUnitTimeLength");
var unitCreationTimeField = document.getElementById("unitCreationTimeField");
var unitCreationTimeRemaining;
if(creatingUnitTimeLength != null) {
    unitCreationTimeRemaining = parseInt(creatingUnitTimeLength.innerHTML) - parseInt((currentDateTime.getTime() - unitCreationDateTime.getTime()) / 1000);
}
if(unitCreationTimeField != null) {
    unitCreationTimeField.innerHTML = parseTimeRemaining(unitCreationTimeRemaining);
}

var tipsterCooldownTimeLength = document.getElementById("tipsterCooldownTimeLength");
var tipsterCooldownTimeField = document.getElementById("tipsterCooldownTimeField");
var tipsterCooldownTimeRemaining;
if(tipsterCooldownTimeLength != null) {
    tipsterCooldownTimeRemaining = parseInt(tipsterCooldownTimeLength.innerHTML) - parseInt((currentDateTime.getTime() - tipsterCooldownDateTime.getTime()) / 1000);
}
if(tipsterCooldownTimeField != null) {
    tipsterCooldownTimeField.innerHTML = parseTimeRemaining(tipsterCooldownTimeRemaining);
}

setInterval(updateCountdownTimers, 1000);

function formatNumber(num) {
    var roundedNum = (Math.round(num * 100) / 100).toFixed(2);
    return roundedNum.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function updateCountdownTimers() {
    if(tipsterCooldownTimeRemaining != null) {
        if(tipsterCooldownTimeRemaining > 0) {
            tipsterCooldownTimeRemaining = tipsterCooldownTimeRemaining - 1;
            tipsterCooldownTimeField.innerHTML = parseTimeRemaining(tipsterCooldownTimeRemaining);
        } else if(tipsterCooldownTimeRemaining == 0) {
            tipsterCooldownTimeField.innerHTML = "Done";
        }
    }
    
    if(unitCreationTimeRemaining != null) {
        if(unitCreationTimeRemaining > 0) {
            unitCreationTimeRemaining = unitCreationTimeRemaining - 1;
            unitCreationTimeField.innerHTML = parseTimeRemaining(unitCreationTimeRemaining);
        } else if(unitCreationTimeRemaining == 0) {
            unitCreationTimeField.innerHTML = "Done";
        }
    }
}

function parseDateTimeString(unitCreationTime) {
    var year = unitCreationTime.substring(0, 4);
    unitCreationTime = unitCreationTime.substring(5);
    var month = parseInt(unitCreationTime.substring(0, 2)) - 1;
    unitCreationTime = unitCreationTime.substring(3);
    var day = unitCreationTime.substring(0, 2);
    unitCreationTime = unitCreationTime.substring(3);
    var hour = unitCreationTime.substring(0, 2);
    unitCreationTime = unitCreationTime.substring(3);
    var minutes = unitCreationTime.substring(0, 2);
    unitCreationTime = unitCreationTime.substring(3);
    var seconds = unitCreationTime.substring(0, 2);

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

function updateBuyPrice() {
    const textBuyPrice = document.getElementById("buyPrice");
    const inputBuyCount = document.getElementById("inputBuyCount");

    textBuyPrice.innerHTML = formatNumber(parseFloat(selectedUnitPrice) * inputBuyCount.value);
}

function clickInfrastructureUnit(unitType, servicePrices, unitPrices, unitCounts) {
    const offscreenMenu = document.querySelector(".offscreen-menu");
    const offscreenMenuHeader = document.getElementById("offscreenMenuHeader");

    const buyTipsterScreen = document.getElementById("buyTipsterScreen");
    const buyUnitScreen = document.getElementById("buyUnitScreen");

    const upgradeInfrastructureScreen = document.getElementById("upgradeInfrastructureScreen");
    upgradeInfrastructureScreen.style.display = "none";

    if(!offscreenMenu.classList.contains("active")) {
        selectedUnitPrice = parseMap(unitType, unitPrices);
        
        if(unitType == "Tipster") {
            offscreenMenuHeader.innerHTML = "Hire Tipster";

            const tipsterPrice = document.getElementById("tipsterPrice");
            tipsterPrice.innerHTML = "Cost: $" + formatNumber(parseMap(unitType, servicePrices));
    
            buyTipsterScreen.style.display = "block";
            buyUnitScreen.style.display = "none";
        }
        
        else if(unitType == "Hacker" || unitType == "Analyst" || unitType == "Influencer") {
            const textUnitType = document.getElementById("unitType");
            offscreenMenuHeader.innerHTML = "Hire Units";
            textUnitType.innerHTML = unitType + " (Owned: " + parseMap(unitType, unitCounts) + ")";

            unitTypeInput.value = unitType;
    
            const textUnitPrice = document.getElementById("unitPrice");
            textUnitPrice.innerHTML = formatNumber(parseMap(unitType, unitPrices));
    
            buyTipsterScreen.style.display = "none";
            buyUnitScreen.style.display = "block";
        }
    }

    offscreenMenu.classList.toggle("active");
}

function clickInfrastructure(infrastructureType, infrastructurePrices) {
    const offscreenMenu = document.querySelector(".offscreen-menu");

    const buyTipsterScreen = document.getElementById("buyTipsterScreen");
    const buyUnitScreen = document.getElementById("buyUnitScreen");
    buyTipsterScreen.style.display = "none";
    buyUnitScreen.style.display = "none";
    
    if(!offscreenMenu.classList.contains("active")) {
        const upgradeInfrastructureScreen = document.getElementById("upgradeInfrastructureScreen");
        
        const offscreenMenuHeader = document.getElementById("offscreenMenuHeader");

        upgradeInfrastructureScreen.style.display = "block";
    }

    offscreenMenu.classList.toggle("active");
}

function parseMap(targetString, mapString) {
    if(mapString.includes(targetString)) {
        var startIndex = mapString.indexOf(targetString) + targetString.length + 1;
        var endIndex = null;

        if(mapString.substring(startIndex).includes(",")) {
            endIndex = startIndex + mapString.substring(startIndex).indexOf(",");
        } else {
            endIndex = startIndex + mapString.substring(startIndex).indexOf("}");
        }

        if(endIndex != null) {
            return mapString.substring(startIndex, endIndex);
        }
    }

    return 0.0;
}
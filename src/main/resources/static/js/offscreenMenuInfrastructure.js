const inputBuyCount = document.getElementById("inputBuyCount");
inputBuyCount.addEventListener('click', () => {updateBuyPrice();});
inputBuyCount.addEventListener('input', () => {updateBuyPrice();});

var unitTypeInput = document.getElementById("unitTypeInput");

var creatingUnitTime = document.getElementById("creatingUnitTime");
var currentDateTime = new Date();
var unitCreationDateTime = parseDateTimeString(creatingUnitTime.innerHTML);

var creatingUnitTimeLength = document.getElementById("creatingUnitTimeLength");
var unitCreationTimeField = document.getElementById("unitCreationTimeField");
var unitCreationTimeRemaining = parseInt(creatingUnitTimeLength.innerHTML) - parseInt((currentDateTime.getTime() - unitCreationDateTime.getTime()) / 1000);
unitCreationTimeField.innerHTML = parseTimeRemaining(unitCreationTimeRemaining);

setInterval(updateCountdownTimers, 1000);

function updateCountdownTimers() {
    if(unitCreationTimeRemaining > 0) {
        unitCreationTimeRemaining = unitCreationTimeRemaining - 1;
        unitCreationTimeField.innerHTML = parseTimeRemaining(unitCreationTimeRemaining);
    } else if(unitCreationTimeRemaining == 0) {
        unitCreationTimeField.innerHTML = "Done";
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
    const textUnitPrice = document.getElementById("unitPrice");
    const textBuyPrice = document.getElementById("buyPrice");
    const inputBuyCount = document.getElementById("inputBuyCount");

    textBuyPrice.innerHTML = parseFloat(textUnitPrice.innerHTML) * inputBuyCount.value;
}

function clickInfrastructureUnit(unitType, unitPrices) {
    const offscreenMenu = document.querySelector(".offscreen-menu");
    const offscreenMenuHeader = document.getElementById("offscreenMenuHeader");

    const buyTipsterScreen = document.getElementById("buyTipsterScreen");
    const buyUnitScreen = document.getElementById("buyUnitScreen");

    if(!offscreenMenu.classList.contains("active")) {
        if(unitType == "Tipster") {
            offscreenMenuHeader.innerHTML = "Hire Tipster";
    
            buyTipsterScreen.style.display = "block";
            buyUnitScreen.style.display = "none";
        }
        
        else if(unitType == "Hacker") {
            const textUnitType = document.getElementById("unitType");
            offscreenMenuHeader.innerHTML = "Hire Units";
            textUnitType.innerHTML = unitType;

            unitTypeInput.value = unitType;
    
            const textUnitPrice = document.getElementById("unitPrice");
            textUnitPrice.innerHTML = parseMap(unitType, unitPrices);
    
            buyTipsterScreen.style.display = "none";
            buyUnitScreen.style.display = "block";
        }
    }

    offscreenMenu.classList.toggle("active");
}

function parseMap(unitType, unitPrices) {
    if(unitPrices.includes(unitType)) {
        var startIndex = unitPrices.indexOf(unitType) + unitType.length + 1;
        var endIndex = null;

        if(unitPrices.substring(startIndex).includes(",")) {
            endIndex = startIndex + unitPrices.substring(startIndex).indexOf(",");
        } else {
            endIndex = startIndex + unitPrices.substring(startIndex).indexOf("}");
        }

        if(endIndex != null) {
            return unitPrices.substring(startIndex, endIndex);
        }
    }

    return 0.0;
}
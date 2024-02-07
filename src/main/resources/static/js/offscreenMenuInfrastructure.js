const inputBuyCount = document.getElementById("inputBuyCount");
inputBuyCount.addEventListener('click', () => {updateBuyPrice();});
inputBuyCount.addEventListener('input', () => {updateBuyPrice();});

var unitTypeInput = document.getElementById("unitTypeInput");

function updateBuyPrice() {
    const textUnitPrice = document.getElementById("unitPrice");
    const textBuyPrice = document.getElementById("buyPrice");
    const inputBuyCount = document.getElementById("inputBuyCount");

    textBuyPrice.innerHTML = parseFloat(textUnitPrice.innerHTML) * inputBuyCount.value;
}

function clickInfrastructureUnit(unitType, unitPrices) {
    const offscreenMenu = document.querySelector(".offscreen-menu");
    const offscreenMenuHeader = document.getElementById("offscreenMenuHeader");
    
    if(unitType == "Hacker") {
        offscreenMenuHeader.innerHTML = "Hire Units";

        const textUnitType = document.getElementById("unitType");
        textUnitType.innerHTML = "Hacker";
        unitTypeInput.value = "Hacker";

        const textUnitPrice = document.getElementById("unitPrice");
        textUnitPrice.innerHTML = parseMap(unitType, unitPrices);
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
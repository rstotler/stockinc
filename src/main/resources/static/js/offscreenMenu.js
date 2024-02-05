const inputStockQuantity = document.getElementById("inputStockQuantity");
inputStockQuantity.addEventListener('click', () => {updateBuySellPrice();});
inputStockQuantity.addEventListener('input', () => {updateBuySellPrice();});
var selectedStockSymbol = null;
var selectedStockQuantity = null;
var buySellPrice = 0.0;
var buySellQuantity = 0;
var targetStockName = null;
var targetAction = "Buy";

var buyStockForm = document.getElementById("buyStockForm");
var buyStockAmount = document.getElementById("buyStockAmount");
var sellStockForm = document.getElementById("sellStockForm");
var sellStockAmount = document.getElementById("sellStockAmount");

function updateBuySellPrice() {
    const textStockListingPrice = document.getElementById("stockListingPrice");
    const textBuySellPrice = document.getElementById("buySellPrice");
    const inputStockQuantity = document.getElementById("inputStockQuantity");

    textBuySellPrice.innerHTML = parseFloat(textStockListingPrice.innerHTML) * inputStockQuantity.value;

    buySellPrice = textBuySellPrice.innerHTML;
    buySellQuantity = inputStockQuantity.value;
}

function clickStockListing(stockListingName, stockListingSymbol, stockListingPrice, ownedStockString) {
    const offscreenMenu = document.querySelector(".offscreen-menu");

    if(!offscreenMenu.classList.contains("active")) {
        selectedStockSymbol = stockListingSymbol;
        selectedStockQuantity = getSelectedStockQuantity(ownedStockString);
        targetStockName = stockListingName;

        const textStockListingName = document.getElementById("stockListingName");
        const textStockListingSymbol = document.getElementById("stockListingSymbol");
        const textStockListingPrice = document.getElementById("stockListingPrice");
        const textBuySellPrice = document.getElementById("buySellPrice");
    
        textStockListingName.innerHTML = stockListingName;
        textStockListingSymbol.innerHTML = selectedStockSymbol + " (" + selectedStockQuantity + " Owned)";
        textStockListingPrice.innerHTML = stockListingPrice;
        textBuySellPrice.innerHTML = "0.00";

        const buySellScreen1 = document.getElementById("buySellScreen1");
        const buySellScreen2 = document.getElementById("buySellScreen2");

        buySellScreen1.style.display = "block";
        buySellScreen2.style.display = "none";

        const inputStockQuantity = document.getElementById("inputStockQuantity");
        inputStockQuantity.value = "0";

        toggleBuyButton();
    }

    offscreenMenu.classList.toggle('active');
}

function getSelectedStockQuantity(ownedStockString) {
    if(ownedStockString.includes(selectedStockSymbol)) {
        var startIndex = ownedStockString.indexOf(selectedStockSymbol) + selectedStockSymbol.length + 1;
        var endIndex = null;

        if(ownedStockString.substring(startIndex).includes(",")) {
            endIndex = startIndex + ownedStockString.substring(startIndex).indexOf(",");
        } else {
            endIndex = startIndex + ownedStockString.substring(startIndex).indexOf("}");
        }

        if(endIndex != null) {
            return ownedStockString.substring(startIndex, endIndex);
        }
    }

    return 0;
}

function toggleBuyButton() {
    const textOffscreenMenuHeader = document.getElementById("offscreenMenuHeader");
    textOffscreenMenuHeader.innerHTML = "Buy Stock";

    const toggleBuyButton = document.getElementById("toggleBuyButton");
    if(toggleBuyButton.classList.contains("button-green-inactive")) {
        toggleBuyButton.classList.remove("button-green-inactive");
        toggleBuyButton.classList.add("button-green");
    }
    
    const toggleSellButton = document.getElementById("toggleSellButton");
    if(toggleSellButton.classList.contains("button-red")) {
        toggleSellButton.classList.remove("button-red");
        toggleSellButton.classList.add("button-red-inactive");
    }

    const buySellPriceLine = document.getElementById("buySellPriceLine");
    if(buySellPriceLine.classList.contains("green")) {
        buySellPriceLine.classList.remove("green");
        buySellPriceLine.classList.add("red");
    }

    const buySellPriceSymbol = document.getElementById("buySellPriceSymbol");
    buySellPriceSymbol.innerHTML = "-$";

    const buySellPriceLine2 = document.getElementById("buySellPriceLine2");
    if(buySellPriceLine2.classList.contains("green")) {
        buySellPriceLine2.classList.remove("green");
        buySellPriceLine2.classList.add("red");
    }

    const buySellPriceSymbol2 = document.getElementById("buySellPriceSymbol2");
    buySellPriceSymbol2.innerHTML = "-$";

    const buyForm = document.getElementById("buyForm");
    const sellForm = document.getElementById("sellForm");
    buyForm.style.display = "block"
    sellForm.style.display = "none";

    targetAction = "Buy";
}

function toggleSellButton() {
    const textOffscreenMenuHeader = document.getElementById("offscreenMenuHeader");
    textOffscreenMenuHeader.innerHTML = "Sell Stock";

    const toggleBuyButton = document.getElementById("toggleBuyButton");
    if(toggleBuyButton.classList.contains("button-green")) {
        toggleBuyButton.classList.remove("button-green");
        toggleBuyButton.classList.add("button-green-inactive");
    }
    
    const toggleSellButton = document.getElementById("toggleSellButton");
    if(toggleSellButton.classList.contains("button-red-inactive")) {
        toggleSellButton.classList.remove("button-red-inactive");
        toggleSellButton.classList.add("button-red");
    }

    const buySellPriceLine = document.getElementById("buySellPriceLine");
    if(buySellPriceLine.classList.contains("red")) {
        buySellPriceLine.classList.remove("red");
        buySellPriceLine.classList.add("green");
    }

    const buySellPriceSymbol = document.getElementById("buySellPriceSymbol");
    buySellPriceSymbol.innerHTML = "+$";

    const buySellPriceLine2 = document.getElementById("buySellPriceLine2");
    if(buySellPriceLine2.classList.contains("red")) {
        buySellPriceLine2.classList.remove("red");
        buySellPriceLine2.classList.add("green");
    }

    const buySellPriceSymbol2 = document.getElementById("buySellPriceSymbol2");
    buySellPriceSymbol2.innerHTML = "+$";

    const buyForm = document.getElementById("buyForm");
    const sellForm = document.getElementById("sellForm");
    buyForm.style.display = "none"
    sellForm.style.display = "block";

    targetAction = "Sell";
}

function toggleConfirmScreen(screenNum) {
    const buySellScreen1 = document.getElementById("buySellScreen1");
    const buySellScreen2 = document.getElementById("buySellScreen2");

    if(screenNum == "1") {
        buySellScreen1.style.display = "block";
        buySellScreen2.style.display = "none";
    } else if(screenNum == "2") {
        buySellScreen1.style.display = "none";
        buySellScreen2.style.display = "block";

        const totalBuySellPrice = document.getElementById("totalBuySellPrice");
        const totalBuySellShares = document.getElementById("totalBuySellShares");

        if(targetAction == "Buy") {
            
        } else if(targetAction == "Sell") {
            if(parseInt(buySellQuantity) > parseInt(selectedStockQuantity)) {
                buySellQuantity = selectedStockQuantity;

                const textStockListingPrice = document.getElementById("stockListingPrice");
                buySellPrice = parseFloat(textStockListingPrice.innerHTML) * parseInt(buySellQuantity);

                const textBuySellPrice = document.getElementById("buySellPrice");
                textBuySellPrice.innerHTML = buySellPrice;
                inputStockQuantity.value = buySellQuantity;
            }
        }

        totalBuySellPrice.innerHTML = buySellPrice;
        totalBuySellShares.innerHTML = buySellQuantity;

        buyStockForm.action = "/buyStock/" + targetStockName;
        buyStockAmount.value = buySellQuantity;
        sellStockForm.action = "/sellStock/" + targetStockName;
        sellStockAmount.value = buySellQuantity;
    }
}
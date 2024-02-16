$(document).on("click", "#ajaxClickRemoveFromGroup", function(event) {
    var groupSymbol = $(this).data("group-symbol");
    var targetUser = $(this).data("target-user");
    $.ajax({
        type: "GET",
        url: "/removeFromGroup",
        data: {"groupSymbol": groupSymbol, "targetUser": targetUser},
        contentType: "application/json; charset=utf-8",
        dataType: "json"
    });
});

$(document).on("click", "#ajaxClickAcceptToGroup", function(event) {
    var groupSymbol = $(this).data("group-symbol");
    var targetUser = $(this).data("target-user");
    $.ajax({
        type: "GET",
        url: "/acceptDenyFromGroup",
        data: {"groupSymbol": groupSymbol, "targetUser": targetUser, "targetAction": "Accept"},
        contentType: "application/json; charset=utf-8",
        dataType: "json"
    });
});

$(document).on("click", "#ajaxClickDenyFromGroup", function(event) {
    var groupSymbol = $(this).data("group-symbol");
    var targetUser = $(this).data("target-user");
    $.ajax({
        type: "GET",
        url: "/acceptDenyFromGroup",
        data: {"groupSymbol": groupSymbol, "targetUser": targetUser, "targetAction": "Deny"},
        contentType: "application/json; charset=utf-8",
        dataType: "json"
    });
});
$(document).on("click", "#ajaxClickMessage", function(event) {
    var messageIndex = $(this).data("message-index");
    $.ajax({
        type: "GET",
        url: "/clickMessage",
        data: {"messageIndex": messageIndex},
        contentType: "application/json; charset=utf-8",
        dataType: "json"
    });
});
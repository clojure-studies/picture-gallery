function deleteImages() {
    var selectedInputs = $("input:checked");
    var selectedIds = [];

    selectedInputs
        .each(function() {
            selectedIds.push($(this).attr('id'));
        });
    if (selectedIds.length < 1) alert("no images selected");
    else {
        var params = {names: selectedIds};
        params[$('meta[name="csrf-param"]').attr('content')] = $('meta[name="csrf-token"]').attr('content')
        $.post(
            context + "/delete",
            params,
            function(response) {
                var errors = $('<ul>');
                $.each(response, function() {
                    if("ok" === this.status) {
                        var element = document.getElementById(this.name);
                        $(element).parent().parent().remove();
                    }
                    else
                    errors
                    .append($('<li>',
                            {html: "failed to remove " +
                                this.name +
                        ": " +
                        this.status}));
                });
                if (errors.length > 0)
                    $('#error').empty().append(errors);
            },
            "json");
    }
}

$(document).ready(function(){
    $("#delete").click(deleteImages);
});

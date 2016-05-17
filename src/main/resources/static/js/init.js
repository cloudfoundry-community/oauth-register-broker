(function ($) {
    $(function () {
        $('.button-collapse').sideNav();
        $(".dropdown-button").dropdown();
        $("a.showcred").click(function () {
            var parent = $(this).parent();
            $("table", parent).toggle("slow");
            if ($(this).text() == "Show credentials") {
                $(this).html("Hide credentials");
            } else {
                $(this).html("Show credentials");
            }
        })
    }); // end of document ready
})(jQuery); // end of jQuery name space
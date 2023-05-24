$(function() {

    $('#searchform, #searchresult-searchform1, #searchresult-searchform2').submit(function() {
        $(this)
            .find('input[name]')
            .filter(function() {
                return !this.value
            })
            .prop('name', '');
        $(this)
            .find('input[name=date-input--datumbeschikbaarvanaf], input[name=date-input--datumbeschikbaartot]')
            .prop('name', '');
    });

    $('#searchform input[name=datumrange]').on('change', function(event) {
        $(event.currentTarget.form)
            .find('input[name=datumBeschikbaarVanaf], input[name=datumBeschikbaarTot]')
            .each(function() {
                $(this).val('');
            });
    });

    $("#sort-select").change(function() {
        window.location.href = new URI(window.location.href).setSearch("sort", this.value);
    });

    $("a[id^='reset-']").click(function(event) {
        $('#input--' + $(this).attr('id').split('reset-')[1]).val('');
    });

    $(".list--subselection input[type='checkbox']").click(function(event) {
        $(this).closest("li").find("input[type='checkbox']").not(this).prop('checked', this.checked);
    });

    $(".nav-sub a[href^='#']").on('click', function(event) {
        event.preventDefault();

        var targetHref = $(this).attr('href');
        var target = $('.tabs .tabs__panels section[id]')
            .filter(targetHref);

        target
            .closest('[role=tabpanel]')
            .each(function() {
                if (this.id === 'panel-gegevens') {
                    openPanelGegevens();
                    $("body,html").animate({
                            scrollTop: $(target).offset().top - 150
                        },
                        200
                    );
                } else if (this.id === 'panel-tekst') {
                    openPanelTekst();
                    $("body,html").animate({
                            scrollTop: $(target).offset().top - 150
                        },
                        200
                    );
                }
            });
    });

    function openPanelGegevens() {
        $("a[id='tab-gegevens']").attr("aria-selected", "true");
        $("a[id='tab-tekst']").attr("aria-selected", "false");
        $("div[id='panel-gegevens']").attr("hidden", false);
        $("div[id='panel-tekst']").attr("hidden", true);
    };

    function openPanelTekst() {
        $("a[id='tab-gegevens']").attr("aria-selected", "false");
        $("a[id='tab-tekst']").attr("aria-selected", "true");
        $("div[id='panel-gegevens']").attr("hidden", true);
        $("div[id='panel-tekst']").attr("hidden", false);
    };

    $("button[id='btn-load-all-pages']").click(function(event) {
        var oldSrc = $("#pdfjs-iframe").attr("src");
        var newSrc = oldSrc.replaceAll(/max-pages%3D\d+(%26)?/gi, '');
        $("#pdfjs-iframe").attr("src", newSrc);
    });

});

var supportsES6 = function() {
    try {
        new Function("(a = 0) => a");
        return true;
    } catch (err) {
        return false;
    }
}();
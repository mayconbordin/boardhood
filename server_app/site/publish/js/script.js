/* Author:

*/

function next_slide() {
    var active = $('.android img.active');
    if (active.length == 0) active = $('.android img:last');
    var next = active.next().length ? active.next() : $('.android img:first');
    active.addClass('last-active');
    next.css({opacity: 0.0}).addClass('active').animate({opacity: 1.0}, 1500, function() {
        active.removeClass('active last-active');
    });
    setTimeout("next_slide()", 5000);
}

$(function() {
    setTimeout("next_slide()", 5000);
});


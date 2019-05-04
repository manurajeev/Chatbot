function filterText()
{  
var rex = new RegExp($('#filterText').val());
if(rex =="/ALL/"){clearFilter()}else{
if(rex == "/FAil/") {
rex = "/Fail/";
}
$('.content').hide();
$('.content').filter(function() {
return rex.test($(this).text());
}).show();
}
}

function clearFilter()
{
$('.filterText').val('');
$('.content').show();
}
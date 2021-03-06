var _cards;
var _filter = {};
var _order;
var _local = true;

$(document).ready(function () {
  if (_local) {
    $.getJSON("/local/carddata", function (d) {
      _cards = TAFFY(d);
      write_table();      
    })
  }
  
  $('body').on('mouseover','tr', function () {
    if (!_local) {
      warhammerCardTooltip.init({
        findCardLinks: () => $(this).find('.card-tooltip')
      });    
    }
  });
  
  $('#filter').on('input', function () {
    var flt = $(this).val();
    if (flt != "") {
      _filter = {"name":{"likenocase":flt}};
    } else {
      _filter = {};
    }
    write_table();
  });
  
  function write_table () {
    $('#tblbody').empty();
    _cards(_filter).each(function (c) {
      $('#tblbody').append(row (c));
    });
  }
  function row (src) {
    return "<tr>"
      + '<td>' + src.id + '</td>'
      + '<td>' + src.collectorInfo + '</td>'
      + '<td><span class="card-tooltip">' + src.name + '</span></td>'
      + '<td>' + src.category.en + '</td>'
      + '<td>' + src.alliance + '</td>'
      + '<td>' + (typeof src.class !== 'undefined' ? src.class.en : '') + '</td>'
      + '<td>' + (corners (src.category.en,src.corners)) + '</td>'
      + '<td>' + src.set[0].name + '</td>'
      + '</tr>';
  }
  function corners (cat, crn)  {
    var outp='';
    $.each(crn, function (id, c) {
      if (cat == "Champion" && !_local) {
        outp += '<span class="mr-1">'
          + '<img class="corner" src="https://assets.warhammerchampions.com/card-database/icons/quest_' 
            + c.value.toLowerCase() 
            + (typeof c.qualifier !== 'undefined' ? '_' + c.qualifier.toLowerCase() : '') 
            + '.png"></span>';
      } else {
        outp += '<span class="mr-1">' + c.value[0] + '</span>';
      }
    });
    return outp;
  }
});
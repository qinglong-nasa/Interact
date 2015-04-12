
var geocoder;
var map;
var p_lat;
var p_lng;
function initialize() {
  geocoder = new google.maps.Geocoder();
  var latlng = new google.maps.LatLng(-34.397, 150.644);
  var mapOptions = {
    zoom: 8,
    center: latlng
  }
  map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
}

function codeAddress() {
  var address = document.getElementById('address').value;
  geocoder.geocode( { 'address': address}, function(results, status) {
    if (status == google.maps.GeocoderStatus.OK) {
      map.setCenter(results[0].geometry.location);
      p_lat=document.getElementById('lattext').value = results[0].geometry.location.lat();
	        p_lng=document.getElementById('lngtext').value = results[0].geometry.location.lng();

      var marker = new google.maps.Marker({
          map: map,
          position: results[0].geometry.location

      });
    } else {
      alert('Geocode was not successful for the following reason: ' + status);
    }
  });
}

function f1()
{
    alert('fired: f1');
    po = 1;
    alert('po changed to 1');
}
google.maps.event.addDomListener(window, 'load', initialize);

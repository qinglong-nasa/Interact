var net = require('net');


var server = net.createServer(function(socket) { //'connection' listener
  
  console.log('client connected');
  socket.on('end', function() {
    console.log('client disconnected');
  });

  socket.on('data', function(data) {
    var response = data.toString().trim();
    buf = new Buffer(256);
    len = buf.write(data.toString());
    console.log(len + " bytes: " + buf.toString('utf8', 0, len));
  });

  socket.on('error', function() {
    console.log("Error is properly handled");
});
});


server.listen(3000, '10.2.179.138', function() { //'listening' listener
  console.log('server bound');
});
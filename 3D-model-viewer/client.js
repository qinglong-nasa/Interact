var net = require('net');
var command = 'hello';
var send_ = 0;


var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.get('/', function(req, res){
  res.sendFile(__dirname + '\\index.html');
});

http.listen(3001, function(){
  console.log('PC Server Started');
});

io.on('connection', function(socket){
  console.log('a user connected');
  socket.on('disconnect', function(){
    console.log('user disconnected');
  });
  socket.on('chat message', function(){
    if(send_ == 1){
    console.log('message: ' + command);
    io.emit('chat message', command);
    send_ = 0;
    }
  });
});

var phoneServer = net.createServer(function(socket) { //'connection' listener  
  console.log('client connected');
  socket.on('end', function() {
    console.log('client disconnected');
  });

  socket.on('data', function(data) {
    var response = data.toString().trim();
    buf = new Buffer(256);
    len = buf.write(data.toString());
    var old = buf.toString('utf8', 0, len);
    command = old;
    send_ = 1;
    console.log(len + " bytes: " + command);
  });

  socket.on('error', function() {
    console.log("Error is properly handled");
  });
});

phoneServer.listen(3000, '192.168.85.1', function() { //'listening' listener
  console.log('Phone Server Started');
});
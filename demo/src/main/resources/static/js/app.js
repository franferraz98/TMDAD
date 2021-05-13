var stompClient = null;
var stompClient2 = null;
var stompClient3 = null;
var stompClient4 = null;

function setConnected(connected) {

    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    // document.getElementById('subscribe').disabled = !connected;
    document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
    document.getElementById('createRoomDiv').style.visibility = connected ? 'visible' : 'hidden';
    document.getElementById('converRoomDiv').style.visibility = connected ? 'visible' : 'hidden';
    document.getElementById('response').innerHTML = '';
}

function connect() {

    // Subscribe to general chat
    var socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {

        setConnected(true);
        console.log('Connected 1: ' + frame);
    });

    // Subscribe to self queue
    var socket2 = new SockJS('/route');
    stompClient2 = Stomp.over(socket2);
    var username = document.getElementById('username').value;
    var topic = '/topic/';
    topic = topic.concat(username);

    stompClient2.connect({}, function(frame) {

      setConnected(true);
      console.log('Connected 2: ' + frame);
      stompClient2.subscribe(topic, function(messageOutput) {

          showMessageOutput(JSON.parse(messageOutput.body));
      });
    });

    // Subscribe to chat room creator
    var socket3 = new SockJS('/createRoom');
    stompClient3 = Stomp.over(socket3);

    stompClient3.connect({}, function(frame) {

        setConnected(true);
        console.log('Connected 3: ' + frame);
    });

    // Add user to chat room
    var socket4 = new SockJS('/addToRoom');
    stompClient4 = Stomp.over(socket4);

    stompClient4.connect({}, function(frame) {

        setConnected(true);
        console.log('Connected 4: ' + frame);
    });

    document.getElementById('subscribe').disabled = false;
}

function disconnect() {

    if(stompClient != null) {
        stompClient.disconnect();
    }

    setConnected(false);
    console.log("Disconnected");
}

function subscribe() {

    var username = document.getElementById('username').value;
    stompClient2.send("/app/route", {}, JSON.stringify({'from':username, 'text':username}));
    document.getElementById('subscribe').disabled = true;

}

function createRoom() {
    var from = document.getElementById('from').value;
    var text = document.getElementById('chatRoom').value;
    stompClient3.send("/app/createRoom", {}, JSON.stringify({'from':from, 'text':text}));
}

function addToRoom() {
    var from = document.getElementById('from').value;
    var text = document.getElementById('room').value;
    var user = document.getElementById('userToRoom').value;
    text = text.concat(':::');
    text = text.concat(user);
    stompClient3.send("/app/addToRoom", {}, JSON.stringify({'from':from, 'text':text}));
}

function sendToRoom() {

    var from = document.getElementById('from').value;
    var text = document.getElementById('textRoom').value;
    text = text.concat(':::');
    text = text.concat(document.getElementById('destRoom').value);
    stompClient.send("/app/chatRoom", {}, JSON.stringify({'from':from, 'text':text}));
}

function sendMessage() {

    var from = document.getElementById('from').value;
    var text = document.getElementById('text').value;
    text = text.concat(':::');
    text = text.concat(document.getElementById('destination').value);
    stompClient.send("/app/chat", {}, JSON.stringify({'from':from, 'text':text}));
}

function showMessageOutput(messageOutput) {

    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(messageOutput.from + ": " + messageOutput.text + " (" + messageOutput.time + ")"));
    response.appendChild(p);
}
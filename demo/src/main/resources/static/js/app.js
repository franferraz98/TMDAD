var stompClient = null;
var stompClient2 = null;

function setConnected(connected) {

    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('subscribe').disabled = !connected;
    document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
    document.getElementById('response').innerHTML = '';
}

function connect() {

    var socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {

        setConnected(true);
        console.log('Connected 1: ' + frame);
        stompClient.subscribe('/topic/pushmessages', function(messageOutput) {

            showMessageOutput(JSON.parse(messageOutput.body));
        });
    });

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
const HTTP = "http://localhost:8080";
const WS = "ws://localhost:8080";
const MESSAGE_JOIN = "join";
const MESSAGE_LEAVE = "leave";
const MESSAGE_OFFER = "offer";
const MESSAGE_ADD_ICE_CANDIDATE = "add-ice-candidate";
$(document).ready(function () {
    var user;
    var stompClient;
    var participants = {};
    $("#login").click(function () {
        $.ajax({
            type: "POST",
            dataType: "json",
            contentType: "application/json",
            url: HTTP + "/login",
            data: JSON.stringify({name: $("#username").val()}),
            success: function (result) {
                user = result;
                stompClient = Stomp.over(new WebSocket(WS + "/stomp"));
                stompClient.connect({"user-id": user.id}, function (data) {
                    stompClient.subscribe('/topic/' + user.id, function (message) {
                        var parsedMessage = JSON.parse(message.body);
                        switch (parsedMessage.id) {
                            case 'WEBRTC_ROOM_USERS':
                                onExistingParticipants(parsedMessage.data);
                                break;
                            case 'WEBRTC_ROOM_USER_ADDED':
                                onNewUserAdded(parsedMessage.data);
                                break;
                            case 'WEBRTC_ROOM_USER_LEFT':
                                onUserLeft(parsedMessage.data);
                                break;
                            case 'WEBRTC_ANSWER':
                                onAnswer(parsedMessage.data);
                                break;
                            case 'WEBRTC_ADD_ICE_CANDIDATE':
                                onIceCandidate(parsedMessage.data)
                                break;
                            default:
                                console.error('Unrecognized message', parsedMessage);
                        }
                    });
                });
            }
        });
    });
    $("#join").click(function () {
        sendMessage({userId: user.id}, MESSAGE_JOIN);
    });
    $("#leave").click(function () {
        sendMessage({}, MESSAGE_LEAVE);
        for (var key in participants) {
            participants[key].dispose();
        }
    });

    function onNewUserAdded(message) {
        receiveVideo(message.user);
    }

    function onAnswer(message) {
        participants[message.userId].rtcPeer.processAnswer(message.sdp, function (error) {
            if (error) return console.error(error);
        });
    }

    function onExistingParticipants(message) {
        var participant = new Participant(user);
        participants[user.id] = participant;

        var options = {
            localVideo: participant.getVideoElement(),
            mediaConstraints: {
                audio: true,
                video: {
                    mandatory: {
                        maxWidth: 320,
                        maxFrameRate: 15,
                        minFrameRate: 15
                    }
                }
            },
            onicecandidate: participant.onIceCandidate.bind(participant)
        }

        participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(options, function (error) {
            if (error) return console.error(error);
            this.generateOffer(participant.offerToReceiveVideo.bind(participant));
        });

        message.users.forEach(receiveVideo);
    }

    function onUserLeft(request) {
        console.log('Participant ' + request.user.id + ' left');
        var participant = participants[request.user.id];
        participant.dispose();
        delete participants[request.user.id];
    }

    function onIceCandidate(data) {
        var candidate = {
            sdpMLineIndex: data.sdpMLineIndex,
            sdpMid: data.sdpMLineIndex,
            candidate: data.sdp,
        }
        participants[data.userId].rtcPeer.addIceCandidate(candidate, function (error) {
            if (error) {
                console.error("Error adding candidate: " + error);
            }
        });
    }

    function sendMessage(message, path) {
        var jsonMessage = JSON.stringify(message);
        console.log('SEND message', path, jsonMessage);
        stompClient.send("/webrtc/" + path, {}, jsonMessage);
    }

    function receiveVideo(sender) {
        var participant = new Participant(sender);
        participants[sender.id] = participant;
        var options = {
            remoteVideo: participant.getVideoElement(),
            onicecandidate: participant.onIceCandidate.bind(participant)
        }
        participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(options,
            function (error) {
                if (error) return console.error(error);
                this.generateOffer(participant.offerToReceiveVideo.bind(participant));
            });
    }

    function Participant(user) {
        this.user = user;
        this.rtcPeer = null;

        var container = document.createElement('div');
        container.id = "participant-" + user.id;
        var span = document.createElement('span');
        var video = document.createElement('video');

        container.appendChild(video);
        container.appendChild(span);
        document.getElementById('participants').appendChild(container);

        span.appendChild(document.createTextNode(user.name));

        video.id = 'video-' + user.id;
        video.autoplay = true;
        video.controls = true;

        this.getVideoElement = function () {
            return video;
        }

        this.offerToReceiveVideo = function (error, offerSdp, wp) {
            if (error) return console.error("sdp offer error")
            console.log('Invoking SDP offer callback function');
            sendMessage({
                userId: user.id,
                sdp: offerSdp
            }, MESSAGE_OFFER);
        }
        this.onIceCandidate = function (candidate, wp) {
            console.log("Local candidate" + JSON.stringify(candidate));
            sendMessage({
                userId: user.id,
                sdpMLineIndex: candidate.sdpMLineIndex,
                sdpMid: candidate.sdpMid,
                sdp: candidate.candidate
            }, MESSAGE_ADD_ICE_CANDIDATE);
        }

        this.dispose = function () {
            console.log('Disposing participant ' + this.user.id);
            this.rtcPeer.dispose();
            container.parentNode.removeChild(container);
        };
    }
});

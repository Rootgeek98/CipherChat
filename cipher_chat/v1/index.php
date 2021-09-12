<?php

/**
 * This file contains code necessary for interaction of the
 * application with the database
 * 
 * @author Bill Glinton <tom.omuom@strathmore.edu>
 * @author Romuald Ashuza <romuald.ashuza@strathmore.edu>
 * @author Betty Kyalo <betty.kyalo@strathmore.edu>
 * @author Kelvin Kimutai<kelvinkpkrr.@gmail.com>
 */
 
error_reporting(-1);
ini_set('display_errors', 'On');
 
require_once '../include/DB_Functions.php';
require '.././libs/Slim/Slim.php';
 
\Slim\Slim::registerAutoloader();
 
$app = new \Slim\Slim();
 
/**
 * User Signup
 */
$app->post('/user/create_account', function() use ($app) {
    // check for required params
    verifyRequiredParams(array('firstname','lastname','username', 'phone_number', 'password'));
 
    // reading post params
    $firstname = $app->request->post('firstname');
    $lastname = $app->request->post('lastname');
    $username = $app->request->post('username');
    $phone_number = $app->request->post('phone_number');
    $password = $app->request->post('password');
 
    $db = new DB_Functions();
    $response = $db->createAccount($firstname, $lastname, $username, $phone_number, $password);
 
    // echo json response
    echoRespnse(200, $response);
});

/**
 * User Login
 */
$app->post('/user/login', function() use ($app) {
    // check for required params
    verifyRequiredParams(array('username', 'password'));
 
    // reading post params
    $username = $app->request->post('username');
    $password = $app->request->post('password');
 
    $db = new DB_Functions();
    $response = $db->authenticateUser($username, $password);
 
    // echo json response
    echoRespnse(200, $response);
});
 
/* * *
 * Updating user
 *  we use this url to update user's fcm registration id
 */
$app->put('/user/:id', function($phone_number) use ($app) {
    global $app;
 
    verifyRequiredParams(array('fcm_registration_id'));
 
    $fcm_registration_id = $app->request->put('fcm_registration_id');
 
    $db = new DB_Functions();
    $response = $db->updateGcmID($phone_number, $fcm_registration_id);
 
    echoRespnse(200, $response);
});

/**
 * Creating a Chat Room
 */
$app->post('/chat_rooms/create_chat_room', function() use ($app) {
    // check for required params
    verifyRequiredParams(array('room_name'));
 
    // reading post params
    $room_name = $app->request->post('room_name');
 
    $db = new DB_Functions();
    $response = $db->createChatRoom($room_name);
 
    // echo json response
    echoRespnse(200, $response);
});

 
/* * *
 * fetching all chat rooms
 */
$app->get('/chat_rooms', function() {

    $response = array();

    $db = new DB_Functions();
 
    // fetching all user tasks
    $result = $db->getAllChatrooms();

    $response["error"] = false;

    $response["chat_rooms"] = array();

    // pushing single chat room into array
    while($row = $result->fetch_assoc()) {

        $tmp = array();

        $tmp['urid'] = $row['urid'];

        $tmp['room_name'] = $row['room_name'];

        $tmp['created_at'] = $row['created_at'];

        array_push($response["chat_rooms"], $tmp);
    }
 
    echoRespnse(200, $response);
});
 
/**
 * Messaging in a chat room
 * Will send push notification using Topic Messaging
 *  */
$app->post('/chat_rooms/:id/message', function($room_id) {
    global $app;
    $db = new DB_Functions();
 
    verifyRequiredParams(array('phone_number', 'message'));
 
    $phone_number = $app->request->post('phone_number');
    $message = $app->request->post('message');
 
    $response = $db->addMessage($phone_number, $room_id, $message);
 
    if ($response['error'] == false) {
        require_once __DIR__ . '/../libs/fcm/Firebase.php';
        require_once __DIR__ . '/../libs/fcm/push.php';
        $fcm = new Firebase();
        $push = new Push();
 
        // get the user using userid
        $user = $db->getUser($phone_number);
 
        $data = array();
        $data['user'] = $user;
        $data['message'] = $response['message'];
        $data['room_id'] = $room_id;
 
        $push->setTitle("Firebase Cloud Messaging");
        $push->setIsBackground(FALSE);
        $push->setFlag(PUSH_FLAG_CHATROOM);
        $push->setData($data);
         
        // echo json_encode($push->getPush());exit;
 
        // sending push message to a topic
        $fcm->sendToTopic('topic_' . $room_id, $push->getPush());
 
        $response['user'] = $user;
        $response['error'] = false;
    }
 
    echoRespnse(200, $response);
});
 
 
/**
 * Sending push notification to a single user
 * We use user's fcm registration id to send the message
 * * */
$app->post('/users/:id/message', function($to_phone_number) {
    global $app;
    $db = new DB_Functions();
 
    verifyRequiredParams(array('message'));
 
    $from_phone_number = $app->request->post('phone_number');
    $message = $app->request->post('message');
 
    $response = $db->addMessage($from_phone_number, $to_phone_number, $message);
 
    if ($response['error'] == false) {
        require_once __DIR__ . '/../libs/fcm/Firebase.php';
        require_once __DIR__ . '/../libs/fcm/push.php';
        $fcm = new Firebase();
        $push = new Push();
 
        $user = $db->getUser($to_phone_number);
 
        $data = array();
        $data['user'] = $user;
        $data['message'] = $response['message'];
        $data['image'] = '';
 
        $push->setTitle("Firebase Cloud Messaging");
        $push->setIsBackground(FALSE);
        $push->setFlag(PUSH_FLAG_USER);
        $push->setData($data);
 
        // sending push message to single user
        $fcm->send($user['fcm_registration_id'], $push->getPush());
 
        $response['user'] = $user;
        $response['error'] = false;
    }
 
    echoRespnse(200, $response);
});
 
 
/**
 * Sending push notification to multiple users
 * We use fcm registration ids to send notification message
 * At max you can send message to 1000 recipients
 */
$app->post('/users/message', function() use ($app) {
 
    $response = array();
    verifyRequiredParams(array('phone_number', 'to', 'message'));
 
    require_once __DIR__ . '/../libs/fcm/Firebase.php';
    require_once __DIR__ . '/../libs/fcm/push.php';
 
    $db = new DB_Functions();
 
    $phone_number = $app->request->post('phone_number');
    $to_phone_numbers = array_filter(explode(',', $app->request->post('to')));
    $message = $app->request->post('message');
 
    $user = $db->getUser($phone_number);
    $users = $db->getUsers($to_phone_numbers);
 
    $registration_ids = array();
 
    // preparing fcm registration ids array
    foreach ($users as $u) {
        array_push($registration_ids, $u['fcm_registration_id']);
    }
 
    // insert messages in db
    // send push to multiple users
    $fcm = new Firebase();
    $push = new Push();
 
    // creating tmp message, skipping database insertion
    $msg = array();
    $msg['message'] = $message;
    $msg['message_id'] = '';
    $msg['room_id'] = '';
    $msg['created_at'] = date('Y-m-d G:i:s');
 
    $data = array();
    $data['user'] = $user;
    $data['message'] = $msg;
    $data['image'] = '';
 
    $push->setTitle("Firebase Cloud Messaging");
    $push->setIsBackground(FALSE);
    $push->setFlag(PUSH_FLAG_USER);
    $push->setData($data);
 
    // sending push message to multiple users
    $fcm->sendMultiple($registration_ids, $push->getPush());
 
    $response['error'] = false;
 
    echoRespnse(200, $response);
});
 
$app->post('/users/send_to_all', function() use ($app) {
 
    $response = array();
    verifyRequiredParams(array('phone_number', 'message'));
 
    require_once __DIR__ . '/../libs/fcm/Firebase.php';
    require_once __DIR__ . '/../libs/fcm/push.php';
 
    $db = new DB_Functions();
 
    $phone_number = $app->request->post('phone_number');
    $message = $app->request->post('message');
 
    require_once __DIR__ . '/../libs/fcm/Firebase.php';
    require_once __DIR__ . '/../libs/fcm/push.php';
    $fcm = new Firebase();
    $push = new Push();
 
    // get the user using userid
    $user = $db->getUser($phone_number);
     
    // creating tmp message, skipping database insertion
    $msg = array();
    $msg['message'] = $message;
    $msg['message_id'] = '';
    $msg['room_id'] = '';
    $msg['created_at'] = date('Y-m-d G:i:s');
 
    $data = array();
    $data['user'] = $user;
    $data['message'] = $msg;
    $data['image'] = '';
 
    $push->setTitle("Firebase Cloud Messaging");
    $push->setIsBackground(FALSE);
    $push->setFlag(PUSH_FLAG_USER);
    $push->setData($data);
 
    // sending message to topic `global`
    // On the device every user should subscribe to `global` topic
    $fcm->sendToTopic('global', $push->getPush());
 
    $response['user'] = $user;
    $response['error'] = false;
 
    echoRespnse(200, $response);
});
 
/**
 * Fetching single chat room including all the chat messages
 */
$app->get('/chat_rooms/:id', function($room_id) {
    global $app;
    $db = new DB_Functions();
 
    $result = $db->getChatRoom($room_id);
 
    $response["error"] = false;

    $response["messages"] = array();

    $response['chat_room'] = array();

    $i = 0;
    // looping through result and preparing tasks array
    while ($chat_room = $result->fetch_assoc()) {

        if ($i == 0) {
            // adding chat room node
            $tmp = array();

            $tmp["room_id"] = $chat_room["urid"];

            $tmp["room_name"] = $chat_room["room_name"];

            $tmp["created_at"] = $chat_room["created_at"];

            $response['chat_room'] = $tmp;

        }
        
        if ($chat_room['phone_number'] != NULL) {
            // message node
            $cmt = array();

            $cmt["message"] = $chat_room["message"];

            $cmt["message_id"] = $chat_room["ucid"];

            $cmt["sent_at"] = $chat_room["sent_at"];
 
            // user node
            $user = array();

            $user['phone_number'] = $chat_room['phone_number'];

            $user['username'] = $chat_room['username'];

            $cmt['user'] = $user;
 
            array_push($response["messages"], $cmt);
        }
    }
 
    echoRespnse(200, $response);
});
 
/**
 * Verifying required params posted or not
 */
function verifyRequiredParams($required_fields) {
    $error = false;
    $error_fields = "";
    $request_params = array();
    $request_params = $_REQUEST;
    // Handling PUT request params
    if ($_SERVER['REQUEST_METHOD'] == 'PUT') {
        $app = \Slim\Slim::getInstance();
        parse_str($app->request()->getBody(), $request_params);
    }
    foreach ($required_fields as $field) {
        if (!isset($request_params[$field]) || strlen(trim($request_params[$field])) <= 0) {
            $error = true;
            $error_fields .= $field . ', ';
        }
    }
 
    if ($error) {
        // Required field(s) are missing or empty
        // echo error json and stop the app
        $response = array();
        $app = \Slim\Slim::getInstance();
        $response["error"] = true;
        $response["message"] = 'Required field(s) ' . substr($error_fields, 0, -2) . ' is missing or empty';
        echoRespnse(400, $response);
        $app->stop();
    }
}
 
/**
 * Validating email address
 */
/*function validateEmail($email) {
    $app = \Slim\Slim::getInstance();
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $response["error"] = true;
        $response["message"] = 'Email address is not valid';
        echoRespnse(400, $response);
        $app->stop();
    }
}*/
 
function IsNullOrEmptyString($str) {
    return (!isset($str) || trim($str) === '');
}
 
/**
 * Echoing json response to client
 * @param String $status_code Http response code
 * @param Int $response Json response
 */
function echoRespnse($status_code, $response) {
    $app = \Slim\Slim::getInstance();
    // Http response code
    $app->status($status_code);
 
    // setting response content type to json
    $app->contentType('application/json');
 
    echo json_encode($response);
}
 
$app->run();
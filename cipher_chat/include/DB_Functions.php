<?php
/**
 * DB_Functions
 * Contains all the functions to manipulate the database
 * 
 * @author Bill Glinton <tom.omuom@strathmore.edu>
 * @author Romuald Ashuza <romuald.ashuza@strathmore.edu>
 * @author Betty Kyalo <betty.kyalo@strathmore.edu>
 * @author Kelvin Kimutai<kelvinkpkrr.@gmail.com>
 */
class DB_Functions {

    private $connection;

    function __construct()
    { // Constructor
        require_once "DB_Connect.php"; // Database connection File

        $database = new DB_Connect(); // Creating a new connection

        $this->connection = $database->connect(); // Retrieve the connection
    }

    function __destruct()
    { // Destructor
        
    }
    
    /**
     * createAccount
     * 
     * Creates a new user and returns user details
     *
     * @param  mixed $firstname
     * @param  mixed $lastname
     * @param  mixed $username
     * @param  mixed $phone_number
     * @param  mixed $password
     * @return void
     */
    public function createAccount(
        $firstname, $lastname, $username, $phone_number, $password) {

        $response = array();

        //$uuid = uniqid('', true); // Generates Unique User ID
        
        $encrypted_password = password_hash($password, PASSWORD_DEFAULT); // Hashes the Password
        
        $create_query = "INSERT INTO users (
            phone_number, firstname, lastname, username, encrypted_password, created_at) 
            VALUES (?, ?, ?, ?, ?,  now())";
        
        $prepared_statement = $this->connection->prepare($create_query);
        
        $prepared_statement->bind_param(
            "issss", 
            $phone_number, $firstname, $lastname, $username, $encrypted_password);
        
        $result = $prepared_statement->execute();

        if ($result) {

            $check_query = "SELECT * FROM users WHERE phone_number = ?";
        
            $prepared_statement = $this->connection->prepare($check_query);

            $prepared_statement->bind_param("i", $phone_number);

            $user = $prepared_statement->execute();

            if ($user) {

                $row = $prepared_statement->get_result()->fetch_assoc();

                $response["error"] = FALSE;

                $response["user"]["firstname"] = $row["firstname"];

                $response["user"]["lastname"] = $row["lastname"];

                $response["user"]["username"] = $row["username"];

                $response["user"]["phone_number"] = $row["phone_number"];

                $response["user"]["password"] = $row["encrypted_password"];

                $response["user"]["created_at"] = $row["created_at"];
                
                //return $user;
                
            }
            $prepared_statement->close();

        } else {

            $response['error'] = TRUE;

            $response['message'] = 'Failed Creating User with username ' . $username. ' and phone number '. $phone_number.  '. The user exists';
        }

        return $response;

        $prepared_statement->close();

    }
    
    /**
     * authenticateUser
     * 
     * Checks user credentials while logging in 
     *
     * @param  mixed $username
     * @param  mixed $password
     * @return void
     */
    public function authenticateUser($username, $password) {

        $response = array();
        
        $get_query = "SELECT * FROM users WHERE username = ?";

        $prepared_statement = $this->connection->prepare($get_query);

        $prepared_statement->bind_param("s", $username);

        $retrieve = $prepared_statement->execute();

        if ($retrieve) {

            $user = $prepared_statement->get_result()->fetch_assoc(); // Fetch Database Results

            $check_password = password_verify($password, $user['encrypted_password']);

            if ($check_password > 0) { // User Authentication Details are correct

                //return $user;

                $response["error"] = FALSE;

                $response["user"]["firstname"] = $user["firstname"];
    
                $response["user"]["lastname"] = $user["lastname"];
    
                $response["user"]["username"] = $user["username"];
    
                $response["user"]["phone_number"] = $user["phone_number"];
    
                $response["user"]["password"] = $user["encrypted_password"];
    
                $response["user"]["created_at"] = $user["created_at"];

            } else { //User Authentication Details are incorrect

                //return NULL;
                
                $response["error"] = TRUE;

                $response["error_message"] = "Wrong Credentials. Please try again!";

            }
        }

        return $response;
    }
    
    /**
     * checkUser
     * 
     * Checks if the username already exists
     *
     * @param  mixed $user_details
     * @return void
     */
    public function checkUser($username) {

        $check_query = "SELECT username FROM users WHERE username = ?";

        $prepared_statement = $this->connection->prepare($check_query);

        $prepared_statement->bind_param("s", $username);

        $prepared_statement->execute();

        $prepared_statement->store_result();

        if ($prepared_statement->num_rows() > 0) { 

            return true; // User Exists

            $prepared_statement->close();
            
        } else {

            return false; // User Does Not Exist

            $prepared_statement->close();

        }
    }
    
    /**
     * checkPhoneNumber
     * 
     * Checks if the phone number already exists 
     *
     * @param  mixed $phone_number
     * @return void
     */
    public function checkPhoneNumber($phone_number) {

        $check_query = "SELECT phone_number FROM users WHERE phone_number = ?";

        $prepared_statement = $this->connection->prepare($check_query);

        $prepared_statement->bind_param("i", $phone_number);

        $prepared_statement->execute();

        $prepared_statement->store_result();

        if ($prepared_statement->num_rows() > 0) {

            return true; // User Exists

            $prepared_statement->close();
        } else {

            return false; // User Does Not Exist

            $prepared_statement->close();
        }
    }
    
    /**
     * updateGcmID
     * 
     * Updates user GCM registration ID 
     *
     * @param  mixed $phone_number
     * @param  mixed $gcm_registration_id
     * @return void
     */
    public function updateGcmID($phone_number, $gcm_registration_id) {
        
        $response = array();

        $updateGcmID_query ="UPDATE users SET gcm_registration_id = ? WHERE phone_number = ?";

        $prepared_statement = $this->connection->prepare($updateGcmID_query);
        
        $prepared_statement->bind_param("si", $gcm_registration_id, $phone_number);
 
        if ($prepared_statement->execute()) { // User successfully updated
            
            $response["error"] = false;

            $response["message"] = 'GCM registration ID updated successfully';

        } else { // Failed to update user
            
            $response["error"] = true;

            $response["message"] = "Failed to update GCM registration ID";

            $prepared_statement->error;
        }

        $prepared_statement->close();
 
        return $response;
    }
   
    /**
     * getUser
     * 
     * Fetch single user by phone number 
     *
     * @param  mixed $phone_number
     * @return void
     */
    public function getUser($phone_number) {

        $getUser_query = "SELECT phone_number, firstname, lastname, username, gcm_registration_id, created_at 
        FROM users WHERE phone_number = ?";
    
        $stmt = $this->connection->prepare($getUser_query);

        $stmt->bind_param("i", $phone_number);

        if ($stmt->execute()) {

            // $user = $stmt->get_result()->fetch_assoc();

            $stmt->bind_result(
                $phone_number, 
                $firstname, 
                $lastname, 
                $username, 
                $gcm_registration_id, 
                $created_at);

            $stmt->fetch();

            $user = array();

            $user["firstname"] = $firstname;

            $user["lastname"] = $lastname;

            $user["username"] = $username;

            $user["phone_number"] = $phone_number;

            $user["gcm_registration_id"] = $gcm_registration_id;

            $user["created_at"] = $created_at;

            $stmt->close();

            return $user;

        } else {

            return NULL;
            
        }
    }
  
    /**
     * getUsers
     * 
     * fetch multiple users by phone numbers 
     *
     * @param  mixed $phone_numbers
     * @return void
     */
    public function getUsers($phone_numbers) {
 
        $users = array();
        if (sizeof($phone_numbers) > 0) {

            $query = "SELECT phone_number, firstname, lastname, username, gcm_registration_id, sent_at 
            FROM users WHERE phone_number IN (";
 
            foreach ($phone_numbers as $phone_number) {

                $query .= $phone_number . ',';

            }
 
            $query = substr($query, 0, strlen($query) - 1);

            $query .= ')';
 
            $prepared_statement = $this->connection->prepare($query);

            $prepared_statement->execute();
            
            $result = $prepared_statement->get_result();
 
            while ($user = $result->fetch_assoc()) {

                $tmp = array();
                
                $tmp["firstname"] = $user['firstname'];

                $tmp["lastname"] = $user['lastname'];

                $tmp["username"] = $user['username'];

                $tmp["phone_number"] = $user['phone_number'];

                $tmp["gcm_registration_id"] = $user['gcm_registration_id'];

                $tmp["sent_at"] = $user['sent_at'];

                array_push($users, $tmp);
            }
        }
 
        return $users;
    }
    
    /**
     * createChatRoom
     * 
     * Creates chat rooms
     *
     * @param  mixed $room_name
     * @param  mixed $password
     * @return void
     */
    public function createChatRoom($room_name) {

        $response = array();

        $urid = uniqid(''); // Generates Unique Room ID

        $create_query = "INSERT INTO rooms (
            urid, room_name, created_at) 
            VALUES (?, ?,  now())";

        $prepared_statement = $this->connection->prepare($create_query);

        $prepared_statement->bind_param(
            "ss", 
            $urid, $room_name);
        
        $result = $prepared_statement->execute();
    
        if ($result) {

            $response['error'] = false;

            $check_query = "SELECT * FROM rooms WHERE room_name = ?";
        
            $prepared_statement = $this->connection->prepare($check_query);

            $prepared_statement->bind_param("s", $room_name);

            $room = $prepared_statement->execute();

            if ($room) {

                $row = $prepared_statement->get_result()->fetch_assoc();

                $tmp = array();

                $tmp['urid'] = $row['urid'];

                $tmp['room_name'] = $row['room_name'];

                $tmp['created_at'] = $row['created_at'];

                $response['message'] = $tmp;

            }

            $prepared_statement->close();

        } else {

            $response['error'] = true;

            $response['message'] = 'Failed Creating Chat Room with name ' . $room_name. '. The chat room exists';
        }

        return $response;

        $prepared_statement->close();
        
    }
   
    /**
     * addMessage
     * 
     * Messaging in a chat room / direct message 
     *
     * @param  mixed $phone_number
     * @param  mixed $room_id
     * @param  mixed $message
     * @return void
     */
    public function addMessage($phone_number, $room_id, $message) {

        $response = array();

        $addMsg_query = "INSERT INTO chat (room_id, phone_number, message) VALUES (?, ?, ?)";
 
        $prepared_statement = $this->connection->prepare($addMsg_query);

        $prepared_statement->bind_param("sis", $room_id, $phone_number, $message);
 
        $result = $prepared_statement->execute();
 
        if ($result) {  // get the message
            
            $response['error'] = false;
           
            $ucid = $this->connection->insert_id;

            $getMsg_query = "SELECT * FROM chat WHERE ucid = ?";

            $prepared_statement = $this->connection->prepare($getMsg_query);

            $prepared_statement->bind_param("s", $ucid);

            $result = $prepared_statement->execute();

            if ($prepared_statement->execute()) {

                $row = $prepared_statement->get_result()->fetch_assoc();

                //$prepared_statement->bind_result(
                    //$chat_id, $room_id, $phone_number, $message, $sent_at);

                //$prepared_statement->fetch();

                $tmp = array();

                $tmp['ucid'] = $row['ucid'];

                $tmp['room_id'] = $row['room_id'];

                $tmp['phone_number'] = $row['phone_number'];

                $tmp['message'] = $row['message'];

                $tmp['sent_at'] = $row['sent_at'];

                $response['message'] = $tmp;
            }
        } else {

            $response['error'] = true;

            $response['message'] = 'Failed send message';

        }
 
        return $response;
    }
    
    /**
     * getAllChatrooms
     * 
     * Fetch all chat rooms
     *
     * @return void
     */
    public function getAllChatrooms() {

        $getRooms_query = "SELECT * FROM rooms";

        $prepared_statement = $this->connection->prepare($getRooms_query);

        $prepared_statement->execute();

        $tasks = $prepared_statement->get_result();

        $prepared_statement->close();

        return $tasks;


    }

    /**
     * getChatRoom
     * 
     * Fetch single chat room by id
     *
     * @param  mixed $room_id
     * @return void
     */
    function getChatRoom($room_id) {

        //$getRoom_query ="SELECT * FROM rooms WHERE urid = ?";

        $get_query = "SELECT cr.urid, cr.room_name, cr.created_at as created_at, 
        u.username as username, c.* FROM rooms cr LEFT JOIN chat c ON 
        c.room_id = cr.urid LEFT JOIN users u ON u.phone_number = c.phone_number 
        WHERE cr.urid = ?";

        //$prepared_statement = $this->connection->prepare($getRoom_query);

        $prepared_statement = $this->connection->prepare($get_query);

        $prepared_statement->bind_param("s", $room_id);

        $prepared_statement->execute();
        
        $tasks = $prepared_statement->get_result();

        $prepared_statement->close();
        
        return $tasks;
    }


}

?>
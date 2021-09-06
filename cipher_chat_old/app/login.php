<?php
/**
 * Login Endpoint
 * Handles user login requests 
 * @author Bill Glinton <tom.omuom@strathmore.edu>
 * @author Romuald Ashuza <romuald.ashuza@strathmore.edu>
 * @author Betty Kyalo <betty.kyalo@strathmore.edu>
 * @author Kelvin Kimutai <kelvinkpkrr@gmail.com>
 */
require_once '../include/DB_Functions.php'; // Functions file

$database = new DB_Functions(); // Create new Functions object

$response = array("error" => FALSE); // JSON response array

if (
    isset($_POST['username']) &&
    isset($_POST['password'])) { // Checks if the Login Form was sent using POST
    
    // Receive the POST parameters
    
    $username = $_POST['username']; // Username
    
    $password = $_POST['password']; // Password

    if (
        $username == NULL || 
        $password == NULL) {
        
        $response["error"] = TRUE;

        $response["message"] = ["One of your input fields is empty"];

        echo json_encode($response);

    } else {

        $user = $database->authenticateUser($username, $password); // Authenticate User

        if ($user){ // User is found with the credentials
            
            $response["error"] = FALSE;

            $response["user"]["firstname"] = $user["firstname"];

            $response["user"]["lastname"] = $user["lastname"];

            $response["user"]["username"] = $user["username"];

            $response["user"]["phone_number"] = $user["phone_number"];

            $response["user"]["password"] = $user["encrypted_password"];

            $response["user"]["created_at"] = $user["created_at"];

            echo json_encode($response);

        } else { // User not found with the credentials

            $response["error"] = TRUE;

            $response["error_message"] = "Wrong Credentials. Please try again!";

            echo json_encode($response);
        }
    }

} else {

    $response["error"] = TRUE;

    $response["error_msg"] = "Required parameters username or password is missing!";

    echo json_encode($response);
}


?>
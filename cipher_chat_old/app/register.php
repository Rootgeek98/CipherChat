<?php
/**
 * Registration Endpoint
 * Handles the user registration requests 
 * @author Bill Glinton <tom.omuom@strathmore.edu>
 * @author Romuald Ashuza <romuald.ashuza@strathmore.edu>
 * @author Betty Kyalo <betty.kyalo@strathmore.edu>
 * @author Kelvin Kimutai <kelvinkpkrr@gmail.com>
 */
require_once "../include/DB_Functions.php"; // Functions file

$database = new DB_Functions(); // Create a new Function Object

$response = array("error" => FALSE); // JSON response array

if (
    isset($_POST['firstname']) && 
    isset($_POST['lastname']) && 
    isset($_POST['username']) && 
    isset($_POST['phone_number']) && 
    isset($_POST['password'])) { // Checks if Registration form was sent using POST

    // Receive the POST parameters
    $firstname = $_POST['firstname']; // First name
    $lastname = $_POST['lastname']; // Last name
    $username = $_POST['username']; // Username
    $phone_number = $_POST['phone_number']; // Phone Number
    $password = $_POST['password']; // Password

    if (
        $firstname == NULL ||
        $lastname == NULL ||
        $username == NULL ||
        $phone_number == NULL ||
        $password == NULL ) {

        $response["error"] = TRUE;

        $response["message"] = ["One of your input fields is empty"];

        echo json_encode($response);

    } else {
        
        $check_user = $database->checkUser($username);

        if ($check_user) { // Username already exists

            $response["error"] = TRUE;

            $response["error_message"] = "User already exists with ". $username;

            echo json_encode($response);

        } else { // Username does not exist

            $check_phone = $database->checkPhoneNumber($phone_number);

            if ($check_phone) { // Phone number already exists 
                
                $response["error"] = TRUE;

                $response["error_message"] = "User already exists with ". $phone_number;

                echo json_encode($response);
            } else { // Phone number does not exist

                $user = $database->createAccount(
                    $firstname, $lastname, $username, $phone_number, $password);
                
                if ($user) { // Account creation successful
                    
                    $response["error"] = FALSE;

                    $response["user"]["firstname"] = $user["firstname"];

                    $response["user"]["lastname"] = $user["lastname"];

                    $response["user"]["username"] = $user["username"];

                    $response["user"]["phone_number"] = $user["phone_number"];

                    $response["user"]["password"] = $user["encrypted_password"];

                    $response["user"]["created_at"] = $user["created_at"];

                    echo json_encode($response);

                } else { // Account creation failed

                    $response["error"] = TRUE;

                    $response["error_message"] = [" Unknown error occurred in registration"];

                    echo json_encode($response);

                }
            }

        }
    }

} else {

    $response["error"] = TRUE;

    $response["error_msg"] = "Required parameters (firstname, lastname, username, phone_number or password) is missing!";

    echo json_encode($response);

}

?>
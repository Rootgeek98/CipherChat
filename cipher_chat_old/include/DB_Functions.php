<?php
/**
 * DB_Functions
 * Contains all the functions to manipulate the database
 * @author Bill Glinton <tom.omuom@strathmore.edu>
 * @author Romuald Ashuza <romuald.ashuza@strathmore.edu>
 * @author Betty Kyalo <betty.kyalo@strathmore.edu>
 * @author Kelvin Kimutai<kelvinkpkrr.@gmail.com>
 */
class DB_Functions {

    private $connection;

    function __construct()
    { // Constructor
        require_once "DB_Connect.php"; // Database Connection File

        $database = new DB_Connect(); // Creating a new Connection

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

        //$uuid = uniqid('', true); // Generates Unique User ID
        
        $encrypted_password = password_hash($password, PASSWORD_DEFAULT); // Hashes the Password
        
        $create_query = "INSERT INTO users (
            phone_number, firstname, lastname, username, encrypted_password, created_at) 
            VALUES (?, ?, ?, ?, ?,  now())";
        
        $prepared_statement = $this->connection->prepare($create_query);
        
        $prepared_statement->bind_param(
            "sssss", 
            $phone_number, $firstname, $lastname, $username, $encrypted_password);
        
        $result = $prepared_statement->execute();
        
        $prepared_statement->close();

        if ($result) {

            $check_query = "SELECT * FROM users WHERE phone_number = ?";
        
            $prepared_statement = $this->connection->prepare($check_query);

            $prepared_statement->bind_param("s", $phone_number);

            $prepared_statement->execute();

            $user = $prepared_statement->get_result()->fetch_assoc();

            $prepared_statement->close();
            
            return $user;
        }

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
        
        $get_query = "SELECT * FROM users WHERE username = ?";

        $prepared_statement = $this->connection->prepare($get_query);

        $prepared_statement->bind_param("s", $username);

        $retrieve = $prepared_statement->execute();

        if ($retrieve) {

            $user = $prepared_statement->get_result()->fetch_assoc(); // Fetch Database Results

            $check_password = password_verify($password, $user['encrypted_password']);

            if ($check_password > 0) {

                return $user; // User Authentication Details are correct

            } else {

                return NULL; //User Authentication Details are incorrect

            }
        }
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

        $prepared_statement->bind_param("s", $phone_number);

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
}

?>
<?php
/**
 * DB_Connect
 * Handles the Connection to the Database
 * @author Bill Glinton <tom.omuom@strathmore.edu>
 * @author Romuald Ashuza <romuald.ashuza@strathmore.edu>
 * @author Betty Kyalo <betty.kyalo@strathmore.edu>
 * @author Kevin Kimutai <kevinkpkrr.@gmail.com>
 */

class DB_Connect { // Connection Class

    private $connection; // Connection Attribute

    public function connect()
    { // Connection Function

        require_once "DB_Config.php"; // Database Configuration file 

        $this->connection = new mysqli ( 
            $hostname,
            $username,
            $password,
            $database
        ); // Connect using the params from the config file

        return $this->connection;
    }
}

?>
<?php
/**
 * Push
 * 
 * This class necessitates push notification of messages
 * 
 * @author Bill Glinton <tom.omuom@strathmore.edu>
 * @author Romuald Ashuza <romuald.ashuza@strathmore.edu>
 * @author Betty Kyalo <betty.kyalo@strathmore.edu>
 * @author Kelvin Kimutai<kelvinkpkrr.@gmail.com>
 */
 
class Push{
    // push message title
    private $title;
     
    // push message payload
    private $data;
     
    // flag indicating background task on push received
    private $is_background;
     
    // flag to indicate the type of notification
    private $flag;
     
    function __construct() {
         
    }
     
    public function setTitle($title){
        $this->title = $title;
    }
    
    public function setData($data){
        $this->data = $data;
    }
    
    public function setIsBackground($is_background){
        $this->is_background = $is_background;
    }
    
    public function setFlag($flag){
        $this->flag = $flag;
    }
    

    public function getPush(){
        $res = array();
        $res['title'] = $this->title;
        $res['is_background'] = $this->is_background;
        $res['flag'] = $this->flag;
        $res['data'] = $this->data;
        
        return $res;
    }
}
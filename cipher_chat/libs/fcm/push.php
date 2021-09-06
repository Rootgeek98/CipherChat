<?php
/**
 * @author Bill Glinton <tom.omuom@strathmore.edu>
 * @author Romuald Ashuza <romuald.ashuza@strathmore.edu>
 * @author Betty Kyalo <betty.kyalo@strathmore.edu>
 * @author Kelvin Kimutai<kelvinkpkrr.@gmail.com>
 */
 
class Push{
    // push message title
    //private $title;
     
    // push message payload
    //private $data;
     
    // flag indicating background task on push received
    //private $is_background;
     
    // flag to indicate the type of notification
    private $flag;

    // push message title
    private $title;
    private $message;
    private $image;
    // push message payload
    private $data;
    // flag indicating whether to show the push
    // notification or not
    // this flag will be useful when perform some operation
    // in background when push is received
    private $is_background;
     
    function __construct() {
         
    }
     
    public function setTitle($title){
        $this->title = $title;
    }

    public function setMessage($message) {
        $this->message = $message;
    }
 
    public function setImage($imageUrl) {
        $this->image = $imageUrl;
    }
 
    public function setPayload($data) {
        $this->data = $data;
    }
    
    public function setFlag($flag){
        $this->flag = $flag;
    }
 
    public function setIsBackground($is_background) {
        $this->is_background = $is_background;
    }

    public function getPush() {
        $res = array();
        $res['data']['title'] = $this->title;
        $res['data']['message'] = $this->message;
        $res['data']['is_background'] = $this->is_background;
        $res['data']['image'] = $this->image;
        $res['data']['payload'] = $this->data;
        $res['data']['flag'] = $this->flag;
        $res['data']['timestamp'] = date('Y-m-d G:i:s');
        return $res;
    }
     
    /**public function setData($data){
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
    }**/
}
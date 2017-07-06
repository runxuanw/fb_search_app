<?php
require_once __DIR__.'/php-graph-sdk-5.0.0/src/Facebook/autoload.php';

function fbQuery($fb, $q){
  try {
    return $fb->get($q);
  } catch(Facebook\Exceptions\FacebookResponseException $e) {
    // When Graph returns an error
    echo 'Graph returned an error: ' . $e->getMessage();
    exit;
  } catch(Facebook\Exceptions\FacebookSDKException $e) {
    // When validation fails or other local issues
    echo 'Facebook SDK returned an error: ' . $e->getMessage();
    exit;
  }
}
/*
echo "hi";
echo $_GET['type'];
echo $_GET['keyword'];
*/
function getTable($fb, $access_token){
  $query = "/search?";
  if(strcmp($_GET['type'], "user") == 0)
    $query .= "q='" . $_GET['keyword'] . "'&type=user&fields=id,name,picture.width(700).height(700)&access_token=" . $access_token;
  else if(strcmp($_GET['type'], "event") == 0)
	$query .= "q='" . $_GET['keyword'] . "'&type=event&fields=id,name,picture.width(700).height(700),place&access_token=" . $access_token;
  else if(strcmp($_GET['type'], "page") == 0)
	$query .= "q='" . $_GET['keyword'] . "'&type=page&fields=id,name,picture.width(700).height(700)&access_token=" . $access_token;
  else if(strcmp($_GET['type'], "group") == 0)
	$query .= "q='" . $_GET['keyword'] . "'&type=group&fields=id,name,picture.width(700).height(700)&access_token=" . $access_token;
  else if(strcmp($_GET['type'], "place") == 0){

  
	if(isset($_GET['lat']) && isset($_GET['lng'])){
	  $query .= "q='" . $_GET['keyword'] . "'&type=place&center=" . $_GET['lat'] . "," . $_GET['lng'] . "&fields=id,name,picture.width(700).height(700)&access_token=" . $access_token;
	}
	else
	  $query = "me";

  }

$response = fbQuery($fb, $query)->getBody();
  echo "{\"type\":\"".$_GET['type']."\",\"data\":[".$response."]}";

}

function getDetail($fb, $access_token){
  $query = "/" . $_GET['id'] . "?fields=id,name,picture.width(700).height(700),albums.limit(5){name,photos.limit(2){name,picture}},posts.limit(5)&access_token=" . $access_token;
  $response = fbQuery($fb, $query)->getDecodedBody();
  
  
  $res = "{";
  
  if(isset($response['name'])){
  $res .= "\"name\":\"" . $response['name'] . "\"";
  if(isset($response['albums']['data']) || isset($response['posts']['data'])) $res .= ",";
  }
  
    if(isset($response['albums']['data'])){
    $res .= "\"albums\":[";
	$cnt = 0;
	foreach($response['albums']['data'] as $albumData){
	  if(isset($albumData['photos']['data']) && sizeof($albumData['photos']['data']) > 0){
	  if($cnt != 0) $res .= ",";
	  $res .= "{\"name\":\"" . $albumData['name'] . "\",\"photos\":[";
	  $cntPic = 0;
	  foreach($albumData['photos']['data'] as $pictureData){
	    $query = "/" . $pictureData['id'] . "/picture?redirect=false&access_token=" . $access_token;
		$imgHD = fbQuery($fb, $query)->getDecodedBody();
		if(isset($pictureData['picture'])){
		  if($cntPic != 0) $res .= ",";
	      $res .= "{\"url\":\"" . $imgHD['data']['url'] . "\"";
		  $res .= ",\"picture\":\"" . $pictureData['picture'] . "\"}";
		  $cntPic++;
		  }
		}
	  $res .= "]}";
	  }
	  else{
	    if($cnt != 0) $res .= ",";
	    $res .= "{\"name\":\"" . $albumData['name'] . "\"}";
		}
	  $cnt++;
	}
	$res .= "]";
  }
  
  
  if(isset($response['posts']['data'])){
    if(isset($response['albums']['data'])) $res .= ",";

    $res .= "\"posts\":" . json_encode($response['posts']['data']) . "";
  }

  
  $res .= "}";
  echo "{\"id\":\"".$_GET['id']."\",\"data\":[".$res."]}";
  }


//need to switch access token, app_id, app_secret when upload
$app_id = '1341672949189346';
$app_secret = '082627b7cdef694078b018e48e019ce0';
$access_token = 'EAATEPpOKiuIBAMuf548Sm9GwZCmqv8qCvRCFkQ8qFitgHSS9nm6L3xrG8SX8EZBpzj4J0THmQvr5ynEowP8KlZC2CE0TtC1QC6CGnlQMnAy5ri40Dofu9vU6D4qWRQQU9auArzq3Q2EtZAt5hfHugrwEtvCOhg0ZD';


/*
$app_id = '1381228765275049';
$app_secret = 'e05f612f5f940d130cec9a87eff107e8';
$access_token = 'EAAToOGHoY6kBAHFkNuDuHnI0mcZAFs9WnAEmZA4xkkHaQgOuERsf9zpF1lE7btLX3xvYbknBc0JMtHJSp0SvwnaZCUnaB8oZCZCYx9fP7mdZA22cKALmX75ZCsU91esOW4DTYAz7TxRC3LrgVj9VxlHI6WMOEtZACBAZD';
*/
$googleKey = "AIzaSyAawJyx5H7Tra2InrLsslB7RUJiB79SYtA";
/*
$fb = new Facebook\Facebook([
  'app_id' => '1341672949189346',
  'app_secret' => '082627b7cdef694078b018e48e019ce0',
   'access_token' => EAATEPpOKiuIBAMuf548Sm9GwZCmqv8qCvRCFkQ8qFitgHSS9nm6L3xrG8SX8EZBpzj4J0THmQvr5ynEowP8KlZC2CE0TtC1QC6CGnlQMnAy5ri40Dofu9vU6D4qWRQQU9auArzq3Q2EtZAt5hfHugrwEtvCOhg0ZD
]);

*/
$fb = new Facebook\Facebook([
  'app_id' => $app_id,
  'app_secret' => $app_secret,
  'default_graph_version' => 'v2.5',
]);

$fb->setDefaultAccessToken($access_token);

if(isset($_GET['id'])){
    getDetail($fb, $access_token);
}
else if(isset($_GET['type'])&&isset($_GET['keyword'])){
    getTable($fb, $access_token);
}
else if(isset($_GET['param'])&&isset($_GET['type'])){
	
	
	$query = "";
	foreach($_GET as $key => $value){
	  if(strcmp($key, "param") != 0)
	    $query .= "&" . $key . "=" . rawurldecode($value);
	  else 
	    $query .= rawurldecode($value);
	}
	$response = fbQuery($fb, $query)->getBody();


	echo "{\"type\":\"".$_GET['type']."\",\"data\":[".$response."]}";
}


?>


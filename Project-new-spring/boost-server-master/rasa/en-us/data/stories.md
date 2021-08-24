## simple greet path
* greet
  - utter_greet
  - action_restart

## simple support path
* seek_support
  - utter_support
  - action_restart

## share code
* inform_own_referral_code{"referral_code": "a1b2c3d4"}
  - utter_own_code
* share_referral_code
  - utter_share_code
  - action_restart

## share code wrong
* inform_own_referral_code
  - utter_default
  - action_restart

## share code + support
* inform_own_referral_code{"referral_code": "a1b2c3d4"}
  - utter_own_code
* seek_support
  - utter_support
  - action_restart

## share code + greet
* inform_own_referral_code{"referral_code": "a1b2c3d4"}
  - utter_own_code
* greet
  - utter_greet
  - action_restart

## share code + own code
* inform_own_referral_code{"referral_code": "a1b2c3d4"}
  - utter_own_code
* inform_own_referral_code{"referral_code": "a2b3c4d5"}
  - utter_own_code

## share code + friend code 
* inform_own_referral_code{"referral_code": "a1b2c3d4"}
  - utter_own_code
* inform_referral_code{"referral_code": "a2b3c4d5"}
  - utter_friend_code 

## redeem code
* inform_referral_code{"referral_code": "a1b2c3d4"}
  - utter_friend_code
* redeem_referral_code
  - utter_redeem_code{"referral_code": "a1b2c3d4"}
  - action_restart

## redeem code wrong
* inform_referral_code
  - utter_default
  - action_restart

## redeem code + support
* inform_referral_code{"referral_code": "a1b2c3d4"}
  - utter_friend_code
* seek_support
  - utter_support
  - action_restart

## redeem code + greet
* inform_referral_code{"referral_code": "a1b2c3d4"}
  - utter_friend_code
* greet
  - utter_greet
  - action_restart

## redeem code + friend code 
* inform_referral_code{"referral_code": "a1b2c3d4"}
  - utter_friend_code
* inform_referral_code{"referral_code": "a2b3c4d5"} 
  - utter_friend_code 

## redeem code + own code
* inform_referral_code{"referral_code": "a1b2c3d4"}
  - utter_friend_code
* inform_own_referral_code{"referral_code": "a2b3c4d5"}
  - utter_own_code

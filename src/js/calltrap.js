import exec from 'cordova/exec'

const STATE = {
  RINGING: 'RINGING',
  OFFHOOK: 'OFFHOOK',
  IDLE: 'IDLE',
}

const CallTrap = {
  STATE,
  onCall(successCallback, errorCallback) {
    errorCallback = errorCallback || this.errorCallback
    exec(successCallback, errorCallback, 'CallTrap', 'onCall', [])
  },
  errorCallback() {
    console.log("WARNING: CallTrap errorCallback not implemented")
  },
}

export default CallTrap

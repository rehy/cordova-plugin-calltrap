const exec = require('cordova/exec')

const CallTrap = {
  onCall: function(successCallback, errorCallback) {
    errorCallback = errorCallback || this.errorCallback
    exec(successCallback, errorCallback, 'CallTrap', 'onCall', [])
  },

  errorCallback: function() {
    console.log("WARNING: CallTrap errorCallback not implemented")
  },
}

module.exports = CallTrap

var exec = require("cordova/exec");

var CLASS = "ThemeDetection";

exports.isAvailable = function(success, error) {
  exec(success, error, CLASS, "isAvailable", []);
};

exports.isDarkModeEnabled = function(success, error) {
  exec(success, error, CLASS, "isDarkModeEnabled", []);
};

exports.onThemeChanged = function(success, error) {
  exec(success, error, CLASS, "onThemeChanged", []);
};

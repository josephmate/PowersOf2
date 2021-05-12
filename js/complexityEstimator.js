
function parseInput() {
  var estimateInputSizePower2Input = document.getElementById("estimateInputSizePower2");
  var estimateInputSizePower10Input = document.getElementById("estimateInputSizePower10");
  // parsing the base and exponent from input
  var base;
  var exponent;
  if (estimateInputSizePower2Input.value != "") {
    base = 2;
    exponent = parseFloat(estimateInputSizePower2Input.value);
    if (isNaN(exponent)) {
      return {
        hasError: true,
        errorMsg: "In 2<sup>x</sup>, x must be an integer"
      };
      return;
    }
  } else if (estimateInputSizePower10Input.value != "") {
    base = 10;
    exponent = parseFloat(estimateInputSizePower10Input.value);
    if (isNaN(exponent)) {
      return {
        hasError: true,
        errorMsg: "In 10<sup>x</sup>, x must be an integer"
      };
    }
  } else {
    return {
      hasError: true,
      errorMsg: "Need to provide 2<sup>x</sup> or 10<sup>x</sup>"
    };
  }

  // parsing and converting the time to milliseconds
  var timeDetails = [
    {
      inputId: "estimateYears",
      errorMsg: "Years must be a positive number",
      multiplier: 365 * 24 * 60 * 60 * 1000
    },
    {
      inputId: "estimateDays",
      errorMsg: "Days must be a positive number",
      multiplier: 24 * 60 * 60 * 1000
    },
    {
      inputId: "estimateHours",
      errorMsg: "Hours must be a positive number",
      multiplier: 60 * 60 * 1000
    },
    {
      inputId: "estimateMinutes",
      errorMsg: "Minutes must be a positive number",
      multiplier: 60 * 1000
    },
    {
      inputId: "estimateSeconds",
      errorMsg: "Seconds must be a positive number",
      multiplier: 1000
    },
    {
      inputId: "estimateMilliseconds",
      errorMsg: "Milliseconds must be a positive number",
      multiplier: 1
    }
  ];
  var timeMsec = 0;
  for(var i = 0; i < timeDetails.length; i++) {
    var timeDetail = timeDetails[i];
    var inputTextbox = document.getElementById(timeDetail.inputId);
    if (inputTextbox.value != "") {
      var parsedDuration = parseFloat(inputTextbox.value);
      if (isNaN(parsedDuration) || parsedDuration < 0) {
        return {
          hasError: true,
          errorMsg: timeDetail.errorMsg
        };
      }
      timeMsec += parsedDuration * timeDetail.multiplier;
    }
  }
  if (timeMsec <= 0) {
    return {
      hasError: true,
      errorMsg: "Total time must be > 0 milliseconds"
    };
  }

  var rescaleEstimatePower;
  var rescaleEstimateTimeMsec;
  var rescaleEstimatePowerInput = document.getElementById("rescaleEstimatePower");
  var rescaleEstimateTimeMsecInput = document.getElementById("rescaleEstimateTimeMsec");
  if (rescaleEstimatePowerInput.value != "") {
    rescaleEstimatePower = parseFloat(rescaleEstimatePowerInput.value);
    if (isNaN(rescaleEstimatePower)) {
      return {
        hasError: true,
        errorMsg: "In rescale 2<sup>x</sup>, x must be an integer"
      };
      return;
    }
  } else {
    return {
      hasError: true,
      errorMsg: "Need to provide rescale 2<sup>x</sup>"
    };
  }
  if (rescaleEstimateTimeMsecInput.value != "") {
      rescaleEstimateTimeMsec = parseFloat(rescaleEstimateTimeMsecInput.value);
      if (isNaN(rescaleEstimateTimeMsec) || rescaleEstimateTimeMsec <= 0) {
        return {
          hasError: true,
          errorMsg: "Rescale time must be an integer"
        };
        return;
      }
    } else {
      return {
        hasError: true,
        errorMsg: "Need to provide rescale time in milliseconds"
      };
    }

  return {
    base: base,
    exponent: exponent,
    timeMsec: timeMsec,
    hasError: false,
    rescaleEstimatePower: rescaleEstimatePower,
    rescaleEstimateTimeMsec: rescaleEstimateTimeMsec
  };
}

function calcLinearBase2Power(
  timeMsec,
  rescaleEstimatePower,
  rescaleEstimateTimeMsec
) {
  var current = rescaleEstimateTimeMsec / Math.pow(2, rescaleEstimatePower);
  for(var i = 0; i <= 64; i++) {
    if (timeMsec <= current) {
      return {
        found: true,
        linearBase2Power: i
      }
    }
    current = current * 2;
  }
  return {
    found: false
  };
}

function estimate() {
  var estimateResultDiv = document.getElementById("estimateResult");
  var estimateInput = parseInput();
    estimateResultDiv.style.display = "block";
  if (estimateInput.hasError) {
    estimateResultDiv.innerHTML = estimateInput.errorMsg;
  }

  console.log("base=" + estimateInput.base);
  console.log("exponent=" + estimateInput.exponent);
  console.log("timeMsec=" + estimateInput.timeMsec);
  console.log("rescaleEstimatePower=" + estimateInput.rescaleEstimatePower);
  console.log("rescaleEstimateTimeMsec=" + estimateInput.rescaleEstimateTimeMsec);

  var linearBase2PowerResult = calcLinearBase2Power(
    estimateInput.timeMsec,
    estimateInput.rescaleEstimatePower,
    estimateInput.rescaleEstimateTimeMsec
  );
  console.log("linearBase2PowerResult.found=" + linearBase2PowerResult.found);
  if (!linearBase2PowerResult.found) {
    estimateResultDiv.innerHTML = "O(N<sup>N</sup>";
    return;
  }
  var linearBase2Power = linearBase2PowerResult.linearBase2Power;
  console.log("linearBase2Power=" + linearBase2Power);
  estimateResultDiv.innerHTML = "TODO";
}

window.addEventListener("load", function(){
  var warningDiv = document.getElementById("javascriptWarning");
  warningDiv.style.display = "none";

  var interactiveDiv = document.getElementById("interactive");
  interactiveDiv.style.display = "block";
});

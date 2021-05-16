
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

var EPSILON = 0.000001;
var FOUR_DECIMAL_PLACES = 0.0001;
function calcLinearBase2Power(
  timeMsec,
  rescaleEstimatePower,
  rescaleEstimateTimeMsec
) {
  var lowerBound = 0;
  var upperBound = 128;
  while (true) {
    // there an input size between 2^0 to 2^128 that achieved the target time
    if (upperBound < lowerBound) {
      return {
        found: false
      };
    }
    var midPoint = (lowerBound + upperBound) / 2;
    var current = rescaleEstimateTimeMsec * Math.pow(2, midPoint - rescaleEstimatePower);

    if (
      timeMsec - (FOUR_DECIMAL_PLACES*timeMsec) <= current
      && current <= timeMsec + (FOUR_DECIMAL_PLACES*timeMsec)
    ) {
      return {
        found: true,
        linearBase2Power: midPoint
      }
    } else if (current < timeMsec) {
      lowerBound = midPoint + EPSILON;
    } else {
      upperBound = midPoint - EPSILON;
    }
  }
}

var COMPLEXITIES = [
  {
    displayName: "O(lgN)",
    estimateRuntime: function(inputSize) {
      return Math.log(inputSize)/Math.log(2);
    }
  },
  {
    displayName: "O(&#8730N)",
    estimateRuntime: function(inputSize) {
      return Math.sqrt(inputSize);
    }
  },
  {
    displayName: "O(N)",
    estimateRuntime: function(inputSize) {
      return inputSize;
    }
  },
  {
    displayName: "O(NlgN)",
    estimateRuntime: function(inputSize) {
      return inputSize*Math.log(inputSize)/Math.log(2);
    }
  },
  {
    displayName: "O(N<sup>2</sup>)",
    estimateRuntime: function(inputSize) {
      return inputSize*inputSize;
    }
  },
  {
    displayName: "O(N<sup>3</sup>)",
    estimateRuntime: function(inputSize) {
      return inputSize*inputSize*inputSize;
    }
  },
  {
    displayName: "O(N<sup>4</sup>)",
    estimateRuntime: function(inputSize) {
      return inputSize*inputSize*inputSize*inputSize;
    }
  },
  {
    displayName: "O(2<sup>N</sup>)",
    estimateRuntime: function(inputSize) {
      return Math.pow(2, inputSize);
    }
  },
  {
    displayName: "O(N!)",
    estimateRuntime: function(inputSize) {
      return inputSize*inputSize*inputSize*inputSize;
    }
  },
  {
    displayName: "O(N<sup>N</sup>)",
    estimateRuntime: function(inputSize) {
      return Math.pow(inputSize, inputSize);
    }
  }
]

function findClosestPowerOf2(complexity, linearBase2Power) {
  if (complexity.displayName === "O(lgN)") {
    return Math.pow(2, linearBase2Power);
  } else if (complexity.displayName === "O(&#8730N)") {
    return linearBase2Power*2;
  }

  var expected = Math.pow(2, linearBase2Power);
  var lowerBound = 0;
  var upperBound = 128;
  while (true) {
    // there an input size between 2^0 to 2^128 that achieved the target time
    if (upperBound < lowerBound) {
      return {
        found: false
      };
    }
    var midPoint = (lowerBound + upperBound) / 2;
    var currentInputSize = Math.pow(2, midPoint);
    var currentRuntime = complexity.estimateRuntime(currentInputSize);

    if (
      expected - (FOUR_DECIMAL_PLACES*expected) <= currentRuntime
      && currentRuntime <= expected + (FOUR_DECIMAL_PLACES*expected)
    ) {
      return {
        found: true,
        power: midPoint
      }
    } else if (currentRuntime < expected) {
      lowerBound = midPoint + EPSILON;
    } else {
      upperBound = midPoint - EPSILON;
    }
  }
}

function estimateComplexity(
  linearBase2Power,
  inputSizeBase,
  inputSizeExponent
) {
  var reasons = [];
  var inputSize = Math.pow(inputSizeBase, inputSizeExponent);
  for(var i = COMPLEXITIES.length - 1; i >=0; i--) {
    var complexity = COMPLEXITIES[i];
    var result = findClosestPowerOf2(complexity, linearBase2Power);
    if (result.found) {
      var closestPowerOf2 = result.power;
      var closestN = Math.pow(2,closestPowerOf2);
      if (closestN >= inputSize) {
        reasons.push("when " + complexity.displayName + " is closest to "
          + " 2<sup>" + linearBase2Power + "</sup>,"
          + " N=2<sup>" + closestPowerOf2 + "</sup>"
          + " which is greater than or equal to the target input size "
          + inputSizeBase + "<sup>" + inputSizeExponent  + "</sup>"
          + " (" + closestN + " >= " + inputSize + ")"
        );
        return {
          complexity: complexity.displayName,
          reasons: reasons
        };
      }
      reasons.push("when " + complexity.displayName + " is closest to "
        + " 2<sup>" + linearBase2Power + "</sup>,"
        + " N=2<sup>" + closestPowerOf2 + "</sup>"
        + " which is less than the target input size "
        + inputSizeBase + "<sup>" + inputSizeExponent  + "</sup>"
        + " (" + closestN + " < " + inputSize + ")"
      );
    } else {
      reasons.push("Could not find an N when " + complexity.displayName + " is closest to "
        + " 2<sup>" + linearBase2Power + "</sup>."
      );
    }
  }

  reasons.push("the N such that made F(N) closest to"
    + " 2<sup>" + linearBase2Power + "</sup>"
    + " was always smaller than "
    + inputSizeBase + "<sup>" + inputSizeExponent  + "</sup>"
    + "for all hardcoded functions F"
  );
  return {
    complexity: "O(1)",
    reasons: reasons
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
    estimateResultDiv.innerHTML = "<div>O(&infin;)</div><ol>Because:<li>time provided was too large so I'm giving up.</li>";
    return;
  }
  var linearBase2Power = linearBase2PowerResult.linearBase2Power;
  console.log("linearBase2Power=" + linearBase2Power);
  var result = estimateComplexity(
    linearBase2Power,
    estimateInput.base,
    estimateInput.exponent
  );
  var resultHtml = "<div>" + result.complexity + "</div>"
    + "<ol>Because:";
    resultHtml += "<li>We assume a linear algorithm with input size 2<sup>" + estimateInput.rescaleEstimatePower + "</sup>"
    + " takes " + estimateInput.rescaleEstimateTimeMsec + " milliseconds";
    resultHtml += "<li>"
      + "So a linear algorithm that takes " + estimateInput.timeMsec + " milliseconds"
      + " needs an input size of about 2<sup>" + linearBase2Power + "</sup>"
      + "</li>";
  for(var i = 0; i < result.reasons.length; i++) {
    resultHtml += "<li>" + result.reasons[i] + "</li>";
  }
  resultHtml += "</ol>";
  estimateResultDiv.innerHTML = resultHtml;
}

window.addEventListener("load", function(){
  var warningDiv = document.getElementById("javascriptWarning");
  warningDiv.style.display = "none";

  var interactiveDiv = document.getElementById("interactive");
  interactiveDiv.style.display = "block";
});

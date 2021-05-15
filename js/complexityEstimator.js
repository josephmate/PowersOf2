
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

var COMPLEXITIES = [
  {
    displayName: "O(lgN)",
    displayNameNoO: "lgN",
    estimateRuntime: function(inputSize) {
      return Math.log(inputSize)/Math.log(2);
    }
  },
  {
    displayName: "O(&#8730N)",
    displayNameNoO: "&#8730N",
    estimateRuntime: function(inputSize) {
      return Math.sqrt(inputSize);
    }
  },
  {
    displayName: "O(N)",
    displayNameNoO: "N",
    estimateRuntime: function(inputSize) {
      return inputSize;
    }
  },
  {
    displayName: "O(NlgN)",
    displayNameNoO: "NlgN",
    estimateRuntime: function(inputSize) {
      return inputSize*Math.log(inputSize)/Math.log(2);
    }
  },
  {
    displayName: "O(N<sup>2</sup>)",
    displayNameNoO: "N<sup>2</sup>",
    estimateRuntime: function(inputSize) {
      return inputSize*inputSize;
    }
  },
  {
    displayName: "O(N<sup>3</sup>)",
    displayNameNoO: "N<sup>3</sup>",
    estimateRuntime: function(inputSize) {
      return inputSize*inputSize*inputSize;
    }
  },
  {
    displayName: "O(N<sup>4</sup>)",
    displayNameNoO: "N<sup>4</sup>",
    estimateRuntime: function(inputSize) {
      return inputSize*inputSize*inputSize*inputSize;
    }
  },
  {
    displayName: "O(2<sup>N</sup>)",
    displayNameNoO: "2<sup>N</sup>",
    estimateRuntime: function(inputSize) {
      return Math.pow(2, inputSize);
    }
  },
  {
    displayName: "O(N!)",
    displayNameNoO: "N!",
    estimateRuntime: function(inputSize) {
      return inputSize*inputSize*inputSize*inputSize;
    }
  },
  {
    displayName: "O(N<sup>N</sup>)",
    displayNameNoO: "N<sup>N</sup>",
    estimateRuntime: function(inputSize) {
      return Math.pow(inputSize, inputSize);
    }
  }
]

function findClosestPowerOf2(complexity, linearBase2Power) {
  var closest = 0;
  var closestDistance = Number.MAX_VALUE;
  var previousDistance = Number.MAX_VALUE;
  var expected = Math.pow(2, linearBase2Power);
  var i = 0;
  while (true) {
    var inputSize = Math.pow(2, i);
    var runtime = complexity.estimateRuntime(inputSize);
    var distance = Math.abs(runtime - expected);
    if(distance < closestDistance) {
      closestDistance = distance;
      closest = i;
    }
    if (distance > previousDistance) {
      break;
    }
    previousDistance = distance;
    i+=1;
  }
  return closest;
}

function estimateComplexity(
  linearBase2Power,
  inputSizeBase,
  inputSizeExponent
) {
  var reasons = [];
  var inputSize = Math.pow(inputSizeBase, inputSizeExponent);
  var minDistancePower = Number.MAX_VALUE;
  var minDistance = Number.MAX_VALUE;
  var minDistanceN = Number.MAX_VALUE;
  var minComplexity;
  for(var i = COMPLEXITIES.length - 1; i >=0; i--) {
    var complexity = COMPLEXITIES[i];
    var closestPowerOf2 = findClosestPowerOf2(complexity, linearBase2Power);
    var closestN = Math.pow(2,closestPowerOf2);
    var distance = Math.abs(closestN - inputSize);
    var weightedDistance = complexity.estimateRuntime(distance);

    reasons.push("when " + complexity.displayName + " is closest to "
      + " 2<sup>" + linearBase2Power + "</sup>,"
      + " N=2<sup>" + closestPowerOf2 + "</sup>=" + closestN
      + " has a weighted distance to "
      + inputSizeBase + "<sup>" + inputSizeExponent  + "</sup>=" + inputSize
      + " of " + weightedDistance
    );

    if (distance < minDistance) {
      minDistance = weightedDistance;
      minComplexity = complexity;
    }
  }

  return {
    complexity: minComplexity.displayName,
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
    estimateResultDiv.innerHTML = "time provided was too large so I'm giving up: O(&infin;)";
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

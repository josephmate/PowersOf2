
function pow2(exponent) {
  var result = 1;
  for(var i = 0; i < exponent; i++) {
    result *= 2;
  }
  return result;
}

function simulate(cellId, linearBase2Power) {
  var accumulator = 0;
  var iterations = pow2(linearBase2Power);

  var t1 = new Date().getTime();
  for(var i = 0; i < iterations; i++) {
    accumulator += 1;
  }
  var t2 = new Date().getTime();
  var timeMsec = t2 - t1;
  console.log("2^" + linearBase2Power + ": " + accumulator + ": " + timeMsec);

  var tableCell = document.getElementById(cellId);
  tableCell.innerHTML = "" + timeMsec + " ms";
}

function findJavaColumnIdx(tableHeaders) {
  for (var i = 0; i < tableHeaders.length; i++) {
    if (tableHeaders[i].innerHTML.startsWith("Java Time")) {
      return i;
    }
  }
  return tableHeaders.length;
}

function calculateRescale(rescalePower, millis, powerToScaleTo) {
  if (powerToScaleTo < rescalePower) {
    return millis / pow2(rescalePower-powerToScaleTo);
  } else {
    return millis * pow2(powerToScaleTo-rescalePower);
  }
}

function formatTime(durationNanos) {
    var nanoSecondsTrimmed = Math.floor(durationNanos % 1000);
    var microSeconds = Math.floor(durationNanos/1000);
    var microSecondsTrimmed = microSeconds % 1000;
    var milliSeconds = Math.floor(microSeconds / 1000);
    var millisecondsTrimmed = milliSeconds % 1000;
    var seconds = Math.floor(milliSeconds / 1000);
    var secondsTrimmed = seconds % 60;
    var minutes = Math.floor(seconds / 60);
    var minutesTrimmed = minutes % 60;
    var hours = Math.floor(minutes / 60);
    var hoursTrimmed = hours % 24;
    var days = Math.floor(hours / 24);
    var daysTrimmed = days % 36;
    var years = Math.floor(days / 365);

    if (years > 0) {
      return years + " years, " + daysTrimmed + " days";
    }
    if (days > 0) {
      return daysTrimmed + " days, " + hoursTrimmed + " hours";
    }
    if (hours > 0) {
      return hoursTrimmed + " hours, " + minutesTrimmed + " minutes";
    }
    if (minutes > 0) {
      return minutesTrimmed + " minutes, " + secondsTrimmed + " s";
    }
    if (seconds > 0) {
      return secondsTrimmed + " s, " + millisecondsTrimmed + " ms";
    }
    if (milliSeconds > 0) {
      return millisecondsTrimmed + " ms, " + microSecondsTrimmed + " &mu;s";
    }
    if (microSeconds > 0) {
      return microSecondsTrimmed + " &mu;s, " + nanoSecondsTrimmed + " ns";
    }

    return nanoSecondsTrimmed + " ns";
}

function rescale() {
  var inputRescalePower = document.getElementById("rescalePower");
  var inputRescaleTime = document.getElementById("rescaleTimeMsec");
  var rescalePower = parseInt(inputRescalePower.value);
  var millis = parseInt(inputRescaleTime.value);

  var runtimeTable = document.getElementById("runtimeTable");
  var javaRowColumnIdx = findJavaColumnIdx(runtimeTable.rows[0].cells);
  var newHeaderCell = runtimeTable.rows[0].insertCell(javaRowColumnIdx);
  newHeaderCell.innerHTML = "Your Time";

  for (var linearBase2Power = 0; linearBase2Power <= 64; linearBase2Power++) {
    var newCell = runtimeTable.rows[linearBase2Power+1].insertCell(javaRowColumnIdx);
    newCell.innerHTML = formatTime(calculateRescale(rescalePower, millis, linearBase2Power)*1000*1000);
  }
}

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

function estimate() {
  var estimateResultDiv = document.getElementById("estimateResult");
  var estimateInput = parseInput();
  if (estimateInput.hasError) {
    estimateResultDiv.style.display = "block";
    estimateResultDiv.innerHTML = estimateInput.errorMsg;
  }

  console.log("base=" + estimateInput.base);
  console.log("exponent=" + estimateInput.exponent);
  console.log("timeMsec=" + estimateInput.timeMsec);
  console.log("rescaleEstimatePower=" + estimateInput.rescaleEstimatePower);
  console.log("rescaleEstimateTimeMsec=" + estimateInput.rescaleEstimateTimeMsec);
}

window.addEventListener("load", function(){
  var warningDiv = document.getElementById("javascriptWarning");
  warningDiv.style.display = "none";

  var interactiveDiv = document.getElementById("interactive");
  interactiveDiv.style.display = "block";
});

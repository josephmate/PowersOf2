
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

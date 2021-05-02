
function pow(exponent) {
  var result = 1;
  for(var i = 0; i < exponent; i++) {
    result *= 2;
  }
  return result;
}

function simulate(cellId, linearBase2Power) {
  var accumulator = 0;
  var iterations = pow(linearBase2Power);

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
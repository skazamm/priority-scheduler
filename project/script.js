// append button to the display
function appendToDisplay(value) {
    document.getElementById("display").value += value;
}

// Calculate the result
function calculate() {
    let display = document.getElementById("display");
    try {
        display.value = eval(display.value); // Evaluate the expression
    } catch (e) {
        display.value = "Error"; // handle invalid input
    }
}

// Clear the display
function clearDisplay() {
    document.getElementById("display").value = ""; // Clear the display
}

// Prevent invalid input (only numbers, operators, and decimals)
function validateInput(input) {
    input.value = input.value.replace(/[^0-9+\-*/.]/g, '');
}

function handleKeyPress(event) {
   if (event.key === "Enter") {
    calculate(); // Calculate when press Enter
   } 
}


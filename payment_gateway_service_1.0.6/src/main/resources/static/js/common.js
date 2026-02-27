function showLoader() {
  document.getElementById("loadingOverlay").style.display = "flex";
}

function hideLoader() {
  document.getElementById("loadingOverlay").style.display = "none";
}

function dynamicMessageController(id, msg, status) {
  const element = document.getElementById(id);

  // Defensive check
  if (!element) {
    console.warn(`Element not found for id: ${id}`);
    return;
  }

  // Set message
  element.innerText = msg;

  // Reset styles first
  element.style.color = "";
  element.style.display = "block";

  // Status-based styling
  if (status === "success") {
    element.style.color = "green";
  } else if (status === "error" || status === "fail") {
    element.style.color = "red";
  } else if (status === "warning") {
    element.style.color = "orange";
  } else {
    element.style.color = "black"; // fallback
  }
}

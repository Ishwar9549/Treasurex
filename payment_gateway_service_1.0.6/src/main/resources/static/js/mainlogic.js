const emailEl = document.getElementById("email");
const subscriptionTypeEl = document.getElementById("subscriptionType");
const amountEl = document.getElementById("amount");

// Amount value change on subscription type change
subscriptionTypeEl.addEventListener("change", function () {
  switch (subscriptionTypeEl.value) {
    case "BASIC":
      amountEl.value = 50;
      break;
    case "SILVER":
      amountEl.value = 199;
      break;
    case "GOLD":
      amountEl.value = 499;
      break;
    case "PLATINUM":
      amountEl.value = 999;
      break;
    case "PRIME":
      amountEl.value = 1499;
      break;
    default:
      amountEl.value = "";
  }
});

// Validation before payment
function validationCheck() {
  dynamicMessageController("error_msg", "", "error");
  const emailValue = emailEl.value.trim();
  const subscriptionValue = subscriptionTypeEl.value;
  const amountValue = amountEl.value;

  if (!emailValue) {
    dynamicMessageController("error_msg", "Email is required", "error");
    return false;
  }

  // basic email format check
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(emailValue)) {
    dynamicMessageController("error_msg", "Invalid email format", "error");
    return false;
  }

  if (!subscriptionValue) {
    dynamicMessageController(
      "error_msg",
      "Please select a subscription plan",
      "error",
    );
    return false;
  }

  if (!amountValue || amountValue <= 0) {
    dynamicMessageController(
      "error_msg",
      "Invalid subscription amount",
      "error",
    );
    return false;
  }

  return true;
}

// ------------------create order-------------------
async function createOrder() {
  console.log("------- Order Creation started -----");

  try {
    showLoader();
    const response = await fetch("http://localhost:9090/create-order", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: document.getElementById("email").value,
        subscriptionType: document.getElementById("subscriptionType").value,
        amount: document.getElementById("amount").value,
      }),
    });

    hideLoader();

    const order = await response.json();

    console.log("---------order creation complited----" + order);

    return order;
  } catch (e) {
    hideLoader();
    dynamicMessageController("error_msg", "Server side error...", "error");
  } finally {
  }
}

// When user clicks Proceed To Pay button
async function proceedToPayBtn() {
  if (!validationCheck()) {
    return;
  }
  const order = await createOrder();

  var options = {
    key: "rzp_test_RWu7WwWwg3nhHn",
    amount: order.amount,
    currency: "INR",
    Name: "ISHWAR MUNNOLLI",
    description: "Subscription Amount",
    order_id: order.razorpayOrderId,
    receipt: order.email,
    prefill: {
      name: order.name,
      email: order.email,
    },
    theme: {
      color: "#3399cc",
    },
    callback_url: "http://localhost:9090/handle-payment-callback",
  };

  var rzp1 = new Razorpay(options);

  rzp1.open();
}

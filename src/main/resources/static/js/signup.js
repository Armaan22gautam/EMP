document.addEventListener("DOMContentLoaded", () => {
  const signupForm = document.getElementById("signupForm")
  const errorMessage = document.getElementById("error-message")
  const successMessage = document.getElementById("success-message")

  signupForm.addEventListener("submit", async (e) => {
    e.preventDefault()

    const formData = new FormData(signupForm)
    const employeeData = {
      name: formData.get("name"),
      email: formData.get("email"),
      password: formData.get("password"),
      salary: Number.parseFloat(formData.get("salary")),
      address: formData.get("address"),
    }

    try {
      const response = await fetch("/api/employees/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(employeeData),
      })

      const result = await response.json()

      if (result.success) {
        showSuccess("Registration successful! You can now login.")
        signupForm.reset()
        setTimeout(() => {
          window.location.href = "/login"
        }, 2000)
      } else {
        showError(result.message || "Registration failed")
      }
    } catch (error) {
      showError("Network error. Please try again.")
    }
  })

  function showError(message) {
    errorMessage.textContent = message
    errorMessage.style.display = "block"
    successMessage.style.display = "none"
    setTimeout(() => {
      errorMessage.style.display = "none"
    }, 5000)
  }

  function showSuccess(message) {
    successMessage.textContent = message
    successMessage.style.display = "block"
    errorMessage.style.display = "none"
  }
})

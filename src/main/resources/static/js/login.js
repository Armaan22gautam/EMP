document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm")
  const errorMessage = document.getElementById("error-message")

  loginForm.addEventListener("submit", async (e) => {
    e.preventDefault()

    const formData = new FormData(loginForm)
    const loginData = {
      username: formData.get("username"),
      password: formData.get("password"),
      userType: formData.get("userType"),
    }

    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(loginData),
      })

      const result = await response.json()

      if (result.success) {
        window.location.href = result.redirectUrl
      } else {
        showError(result.message || "Login failed")
      }
    } catch (error) {
      showError("Network error. Please try again.")
    }
  })

  function showError(message) {
    errorMessage.textContent = message
    errorMessage.style.display = "block"
    setTimeout(() => {
      errorMessage.style.display = "none"
    }, 5000)
  }
})

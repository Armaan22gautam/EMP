document.addEventListener("DOMContentLoaded", () => {
  const welcomeMessage = document.getElementById("welcomeMessage")
  const logoutBtn = document.getElementById("logoutBtn")
  const downloadPayrollBtn = document.getElementById("downloadPayrollBtn")
  const refreshBtn = document.getElementById("refreshBtn")
  const employeesTable = document.getElementById("employeesTable")
  const loading = document.getElementById("loading")

  let currentUser = null

  // Initialize dashboard
  init()

  async function init() {
    await checkSession()
    await loadEmployees()
  }

  async function checkSession() {
    try {
      const response = await fetch("/api/auth/session")
      const result = await response.json()

      if (result.authenticated && result.userType === "employee") {
        currentUser = result.user
        welcomeMessage.textContent = `Welcome, ${currentUser.name}!`
      } else {
        window.location.href = "/login"
      }
    } catch (error) {
      window.location.href = "/login"
    }
  }

  async function loadEmployees() {
    loading.style.display = "block"
    try {
      const response = await fetch("/api/employees")
      const employees = await response.json()
      displayEmployees(employees)
    } catch (error) {
      employeesTable.innerHTML = '<p class="error-message">Failed to load employees</p>'
    } finally {
      loading.style.display = "none"
    }
  }

  function displayEmployees(employees) {
    if (employees.length === 0) {
      employeesTable.innerHTML = "<p>No employees found.</p>"
      return
    }

    const table = `
            <div class="employees-table">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Salary</th>
                            <th>Address</th>
                            <th>Created Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${employees
                          .map(
                            (employee) => `
                            <tr>
                                <td>${employee.id}</td>
                                <td>${employee.name}</td>
                                <td>${employee.email}</td>
                                <td>$${employee.salary.toFixed(2)}</td>
                                <td>${employee.address}</td>
                                <td>${new Date(employee.createdAt).toLocaleDateString()}</td>
                            </tr>
                        `,
                          )
                          .join("")}
                    </tbody>
                </table>
            </div>
        `
    employeesTable.innerHTML = table
  }

  // Event listeners
  logoutBtn.addEventListener("click", async () => {
    try {
      await fetch("/api/auth/logout", { method: "POST" })
      window.location.href = "/login"
    } catch (error) {
      window.location.href = "/login"
    }
  })

  downloadPayrollBtn.addEventListener("click", () => {
    if (currentUser) {
      window.open(`/api/employees/${currentUser.id}/payroll`, "_blank")
    }
  })

  refreshBtn.addEventListener("click", loadEmployees)
})

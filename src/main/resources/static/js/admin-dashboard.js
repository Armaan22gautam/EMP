document.addEventListener("DOMContentLoaded", () => {
  const welcomeMessage = document.getElementById("welcomeMessage")
  const logoutBtn = document.getElementById("logoutBtn")
  const addEmployeeBtn = document.getElementById("addEmployeeBtn")
  const refreshBtn = document.getElementById("refreshBtn")
  const employeesTable = document.getElementById("employeesTable")
  const loading = document.getElementById("loading")

  // Modal elements
  const employeeModal = document.getElementById("employeeModal")
  const modalTitle = document.getElementById("modalTitle")
  const employeeForm = document.getElementById("employeeForm")
  const closeBtn = document.querySelector(".close")
  const cancelBtn = document.getElementById("cancelBtn")
  const modalErrorMessage = document.getElementById("modal-error-message")

  let currentUser = null
  let editingEmployeeId = null

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

      if (result.authenticated && result.userType === "admin") {
        currentUser = result.user
        welcomeMessage.textContent = `Welcome, ${currentUser.username}!`
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
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`)
      }
      const employees = await response.json()
      displayEmployees(employees)
    } catch (error) {
      console.error("Failed to load employees:", error)
      employeesTable.innerHTML = `<p class="error-message">Failed to load employees: ${error.message}</p>`
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
                            <th>Actions</th>
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
                                <td>
                                    <div class="action-buttons">
                                        <button class="btn btn-info" onclick="editEmployee(${employee.id})">Edit</button>
                                        <button class="btn btn-danger" onclick="deleteEmployee(${employee.id})">Delete</button>
                                        <button class="btn btn-success" onclick="downloadPayroll(${employee.id})">Payroll</button>
                                    </div>
                                </td>
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

  // Modal functions
  function openModal(title, employee = null) {
    modalTitle.textContent = title
    modalErrorMessage.style.display = "none"

    if (employee) {
      editingEmployeeId = employee.id
      document.getElementById("employeeId").value = employee.id
      document.getElementById("modalName").value = employee.name
      document.getElementById("modalEmail").value = employee.email
      document.getElementById("modalPassword").value = employee.password
      document.getElementById("modalSalary").value = employee.salary
      document.getElementById("modalAddress").value = employee.address
    } else {
      editingEmployeeId = null
      employeeForm.reset()
      document.getElementById("employeeId").value = ""
    }

    employeeModal.style.display = "flex"
  }

  function closeModal() {
    employeeModal.style.display = "none"
    employeeForm.reset()
    editingEmployeeId = null
  }

  // Global functions for button clicks
  window.editEmployee = async (id) => {
    try {
      const response = await fetch(`/api/employees/${id}`)
      if (response.ok) {
        const employee = await response.json()
        openModal("Edit Employee", employee)
      }
    } catch (error) {
      alert("Failed to load employee data")
    }
  }

  window.deleteEmployee = async (id) => {
    if (confirm("Are you sure you want to delete this employee?")) {
      try {
        const response = await fetch(`/api/employees/${id}`, {
          method: "DELETE",
        })
        const result = await response.json()

        if (result.success) {
          await loadEmployees()
        } else {
          alert(result.message || "Failed to delete employee")
        }
      } catch (error) {
        alert("Network error. Please try again.")
      }
    }
  }

  window.downloadPayroll = (id) => {
    window.open(`/api/employees/${id}/payroll`, "_blank")
  }

  // Event listeners
  addEmployeeBtn.addEventListener("click", () => {
    openModal("Add New Employee")
  })

  closeBtn.addEventListener("click", closeModal)
  cancelBtn.addEventListener("click", closeModal)

  employeeModal.addEventListener("click", (e) => {
    if (e.target === employeeModal) {
      closeModal()
    }
  })

  employeeForm.addEventListener("submit", async (e) => {
    e.preventDefault()

    const formData = new FormData(employeeForm)
    const employeeData = {
      name: formData.get("name"),
      email: formData.get("email"),
      password: formData.get("password"),
      salary: Number.parseFloat(formData.get("salary")),
      address: formData.get("address"),
    }

    try {
      let response
      if (editingEmployeeId) {
        response = await fetch(`/api/employees/${editingEmployeeId}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(employeeData),
        })
      } else {
        response = await fetch("/api/employees/register", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(employeeData),
        })
      }

      const result = await response.json()

      if (result.success) {
        closeModal()
        await loadEmployees()
      } else {
        modalErrorMessage.textContent = result.message || "Operation failed"
        modalErrorMessage.style.display = "block"
      }
    } catch (error) {
      modalErrorMessage.textContent = "Network error. Please try again."
      modalErrorMessage.style.display = "block"
    }
  })

  logoutBtn.addEventListener("click", async () => {
    try {
      await fetch("/api/auth/logout", { method: "POST" })
      window.location.href = "/login"
    } catch (error) {
      window.location.href = "/login"
    }
  })

  refreshBtn.addEventListener("click", loadEmployees)
})

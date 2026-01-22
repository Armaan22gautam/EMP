# Employee Management System

A comprehensive employee management system built with Spring Boot, HTML/CSS/JavaScript, and MySQL.

## Features

### For Employees:
- **Registration**: New employees can sign up with name, email, password, salary, and address
- **Login**: Existing employees can login with their name and password
- **Dashboard**: View all employees in the system (read-only access)
- **Payroll Export**: Download personal payroll information as a text file

### For Administrators:
- **Login**: Admin login with default credentials (admin/admin123)
- **Employee Management**: Full CRUD operations on employee records
- **Dashboard**: View, create, edit, and delete employee records
- **Payroll Access**: Download payroll for any employee

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Database**: MySQL 8.0
- **Build Tool**: Maven
- **Template Engine**: Thymeleaf

## Project Structure

\`\`\`
src/
├── main/
│   ├── java/com/employee/management/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── model/          # Entity classes
│   │   ├── repository/     # JPA repositories
│   │   └── service/        # Business logic services
│   └── resources/
│       ├── static/         # CSS, JS, images
│       └── templates/      # HTML templates
└── scripts/               # Database scripts
\`\`\`

## Setup Instructions

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Spring Tool Suite (STS) or any Java IDE

### Database Setup
1. Create a MySQL database named `employee_management_db`
2. Update database credentials in `application.properties`
3. Run the SQL script in `scripts/create_database.sql`

### Running the Application
1. Clone the repository
2. Open in Spring Tool Suite (STS)
3. Update database configuration in `src/main/resources/application.properties`
4. Run as Spring Boot Application
5. Access the application at `http://localhost:8080`

## Default Credentials

### Admin Login:
- Username: `admin`
- Password: `admin123`

### Sample Employee Login:
- Name: `John Doe`
- Password: `password123`

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/session` - Get current session

### Employee Management
- `GET /api/employees` - Get all employees
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees/register` - Register new employee
- `PUT /api/employees/{id}` - Update employee (Admin only)
- `DELETE /api/employees/{id}` - Delete employee (Admin only)

### Payroll
- `GET /api/employees/{id}/payroll` - Download payroll file
- `GET /api/payroll/download/{id}` - Alternative payroll download
- `GET /api/payroll/preview/{id}` - Preview payroll content

## Security Features

- Session-based authentication
- Role-based access control (Employee vs Admin)
- CSRF protection disabled for API endpoints
- Input validation on all forms
- Secure password handling

## Database Schema

### Employees Table
- `id` (Primary Key)
- `name` (Unique)
- `email` (Unique)
- `password`
- `salary`
- `address`
- `created_at`
- `updated_at`

### Admins Table
- `id` (Primary Key)
- `username` (Unique)
- `password`
- `role`

## Features in Detail

### Employee Dashboard
- View all employees in a responsive table
- Download personal payroll as text file
- Clean, professional interface
- Mobile-responsive design

### Admin Dashboard
- Full CRUD operations on employees
- Modal-based forms for adding/editing
- Confirmation dialogs for deletions
- Bulk payroll download capabilities

### Payroll System
- Detailed payroll information including:
  - Employee details
  - Salary breakdown (annual, monthly, weekly, daily)
  - Tax calculations (estimated)
  - Net salary calculations
- Professional document formatting
- Secure download with access controls

## Development Notes

- Built following Spring Boot best practices
- RESTful API design
- Responsive web design
- Clean separation of concerns
- Comprehensive error handling
- Session management for security

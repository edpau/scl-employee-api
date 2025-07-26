# Employee Creator Backend

Your task is to create an API that allows creating, listing, updating, and deleting employees. The project should follow MVC principles, with JSON responses acting as the "view" layer in this API-based application.

### Stack

-   Play framework for Scala
-   mySQL database

### Endpoints

Implement at least the following endpoints:

-   `POST` /employees - Create a new employee
-   `GET` /employees - List all employees
-   `DELETE` /employees/{id} - Delete an employee by id

#### Optional (recommended):

-   `PATCH` /employees/{id} - Update employee details
-   `GET` /employees/{id} - Retrieve specific employee

### Database schema

You should store the following information about each employee

-   first name
-   last name
-   email
-   mobile number
-   address
-   contract information:
    -   start date
    -   contract type (permanent or contract)
    -   full time/part time
    -   end date (optional, not required for permanent employees)
    -   hours per week

You will have to decide how you want to store the above information in the database:

**Simpler implementation** will be to create a single employee table with all the information stored there, keep in mind that this will not allow to store the history of employment for each employee.

**More challenging implementation** will be creating two tables with a relationship - employee and contract - this way you will be able to assign multiple contracts to one employee and store the entire history of their employment.
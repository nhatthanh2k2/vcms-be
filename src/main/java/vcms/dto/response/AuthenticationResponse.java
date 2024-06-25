package vcms.dto.response;

import vcms.model.Employee;

public class AuthenticationResponse {
    private Employee employee;

    private String token;

    public AuthenticationResponse(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

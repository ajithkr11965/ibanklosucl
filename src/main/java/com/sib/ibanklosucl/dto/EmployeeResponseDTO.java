package com.sib.ibanklosucl.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeResponseDTO {
    private Optional<Employee> employee;
    private List<Reportee> reportees;
    private List<String> roles;
    private List<String> clusterSols;
    private List<String> saleSols;
    private List<MenuList> menuList;

    // Add getters that handle null values
    public List<Reportee> getReportees() {
        return reportees != null ? reportees : new ArrayList<>();
    }

    public List<String> getRoles() {
        return roles != null ? roles : new ArrayList<>();
    }

    public List<String> getClusterSols() {
        return clusterSols != null ? clusterSols : new ArrayList<>();
    }

    public List<String> getSaleSols() {
        return saleSols != null ? saleSols : new ArrayList<>();
    }

    public List<MenuList> getMenuList() {
        return menuList != null ? menuList : new ArrayList<>();
    }
}


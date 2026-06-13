package com.cld.finding.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SecurityFinding {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String type;
        private String apiCall;
        private String username;
        private String sourceIp;
        private String region;
        private String severity;

        public SecurityFinding() {
        }

        public SecurityFinding(String type, String apiCall, String username, String sourceIp, String region, String severity) {
                this.type = type;
                this.apiCall = apiCall;
                this.username = username;
                this.sourceIp = sourceIp;
                this.region = region;
                this.severity = severity;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getType() {
                return type;
        }

        public void setType(String type) {
                this.type = type;
        }

        public String getApiCall() {
                return apiCall;
        }

        public void setApiCall(String apiCall) {
                this.apiCall = apiCall;
        }

        public String getUsername() {
                return username;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public String getSourceIp() {
                return sourceIp;
        }

        public void setSourceIp(String sourceIp) {
                this.sourceIp = sourceIp;
        }

        public String getRegion() {
                return region;
        }

        public void setRegion(String region) {
                this.region = region;
        }

        public String getSeverity() {
                return severity;
        }

        public void setSeverity(String severity) {
                this.severity = severity;
        }
}
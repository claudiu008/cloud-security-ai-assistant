package com.cld.finding.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SecurityFinding {

        @Id
        private Long id;

        private String type;
        private String apiCall;
        private String username;
        private String sourceIp;
        private String region;
        private String severity;

        public SecurityFinding() {
        }

        public SecurityFinding(Long id, String type, String apiCall, String username, String sourceIp, String region,
                        String severity) {
                this.id = id;
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

        public String getType() {
                return type;
        }

        public String getApiCall() {
                return apiCall;
        }

        public String getUsername() {
                return username;
        }

        public String getSourceIp() {
                return sourceIp;
        }

        public String getRegion() {
                return region;
        }

        public String getSeverity() {
                return severity;
        }
}
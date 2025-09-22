package com.example.rightbackend.member.domain.constant;

public enum Job {
    STUDENT(1, "학생"),
    OFFICE_WORKER(2, "회사원"),
    PROFESSIONAL(3, "전문직"),
    FREELANCER(4, "프리랜서"),
    BUSINESS_OWNER(5, "자영업"),
    PUBLIC_SERVANT(6, "공무원"),
    TEACHER(7, "교사"),
    HEALTHCARE(8, "의료계"),
    FINANCE(9, "금융업"),
    IT(10, "IT업"),
    SERVICE(11, "서비스업"),
    MANUFACTURING(12, "제조업"),
    ARTIST(13, "예술가"),
    ATHLETE(14, "운동선수"),
    OTHER(15, "기타");

    private final int id;
    private final String label;

    Job(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static Job fromId(int id) {
        for (Job job : values()) {
            if (job.id == id) {
                return job;
            }
        }
        throw new IllegalArgumentException("Invalid job id: " + id);
    }

    public static Job fromName(String name) {
        try {
            return Job.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid job name: " + name);
        }
    }
}
export default interface RAJob {
    id?: number;
    title: string;
    description: string;
    status: "OPEN" | "CLOSED";
    updateTime?: string; // format: "HH:MM:SS"
    department?: string;
    location?: string;
    startDate?: string; // format: "YYYY-MM-DD"
    endDate?: string;   // format: "YYYY-MM-DD"
    timeCommitment?: string;
    paid: boolean;
    stipendAmount?: number;
    preferredMajors?: string[]; // e.g., ["CS", "Math"]
    skillsRequired?: string[];  // e.g., ["Python", "Excel"]
}
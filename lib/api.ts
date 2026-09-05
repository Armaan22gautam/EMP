export const API_BASE_URL = "http://localhost:8080";

export const fetchApi = async (endpoint: string, options: RequestInit = {}) => {
    const defaultOptions: RequestInit = {
        headers: {
            "Content-Type": "application/json",
            ...options.headers,
        },
        credentials: "include", // Essential for maintaining Spring Boot Sessions
    };

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...defaultOptions,
        ...options,
    });

    if (!response.ok) {
        let errorMsg = "An error occurred";
        try {
            const errorData = await response.json();
            errorMsg = errorData.error || errorData.message || errorMsg;
            if (response.status === 401 || response.status === 403) {
                if (typeof window !== "undefined" && !endpoint.includes("/api/auth/login")) {
                    // Force redirect if not authenticated
                    window.location.href = "/login";
                }
            }
        } catch (e) {
            errorMsg = `HTTP Error ${response.status}`;
        }
        throw new Error(errorMsg);
    }

    // Return null if NO CONTENT, else parsed JSON
    if (response.status === 204 || response.headers.get("content-length") === "0") {
        return null;
    }

    try {
        return await response.json();
    } catch (e) {
        return null; // Might be text/plain or empty response
    }
};

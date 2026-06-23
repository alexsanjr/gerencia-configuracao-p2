export interface ApiError {
    message: string;
    error?: string;
    status?: number;
    path?: string;
    timestamp?: string;
}

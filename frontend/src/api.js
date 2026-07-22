const API_URL = process.env.API_URL || "http://localhost:8080/api/v1";

export async function api(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: { "Content-Type": "application/json", ...options.headers }
  });
  if (!response.ok) {
    const problem = await response.json().catch(() => ({}));
    throw new Error(problem.detail || "The request could not be completed.");
  }
  return response.json();
}
